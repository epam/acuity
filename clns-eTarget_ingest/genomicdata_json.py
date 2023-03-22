 # 
 # This file is part of the eTarget ingest distribution (https://github.com/digital-ECMT/eTarget_ingest).
 # Copyright (C) 2017 - 2021 digital ECMT
 # 
 # This program is free software: you can redistribute it and/or modify  
 # it under the terms of the GNU General Public License as published by  
 # the Free Software Foundation, version 3.
 #
 # This program is distributed in the hope that it will be useful, but 
 # WITHOUT ANY WARRANTY; without even the implied warranty of 
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 # General Public License for more details.
 #
 # You should have received a copy of the GNU General Public License 
 # along with this program. If not, see <http://www.gnu.org/licenses/>.
 #

import pymssql
import json
from azure.storage.file import FileService
import utilities
import re
import datetime
import traceback
from jsonschema import Draft7Validator
from jsonschema import ValidationError 


class GenomicDataException(Exception):
    """Raised internally and is already sent to the log file/db"""
    pass

class GenomicDataJson:
    def __init__(self, filename, remotehostname, remoteusername, remotepassword, remotedbname, fileuser, filekey, datadir='data', logblob='log'):
        self.filename = filename
        self.datadir=datadir
        self.file_service = FileService(account_name=fileuser, account_key=filekey)
        self.genomicDataString = self.file_service.get_file_to_text(self.datadir, None, filename).content
        self.remotehostname = remotehostname
        self.remoteusername = remoteusername
        self.remotepassword = remotepassword
        self.remotedbname = remotedbname
        self.conn = pymssql.connect(self.remotehostname,self.remoteusername, self.remotepassword, self.remotedbname, autocommit=False)
        self.log = utilities.Util(remotehostname, remoteusername, remotepassword, remotedbname, fileuser, filekey, logblob)
        self.errorflag = False
        self.config = self.log.getConfig()
        with open('genomicdata_schema.json', 'r') as f:
            schema_data = f.read()
        self.schema=Draft7Validator(json.loads(schema_data))
        
    def __del__(self):
        self.conn.close()
        
    def deleteFile(self):
        try:
            if self.file_service.exists(self.datadir, None, self.filename):
                self.file_service.delete_file(self.datadir, None, self.filename)
            if self.errorflag==False:
                self.log.systemStatusUpdate(filename, 'Genomic', self.log.timestamp(), 'Success')
        except:
            self.log.logMessage('There was a problem deleting '+self.filename)
    
    def rollback(self):
        self.conn.rollback()
        self.conn.close()
        
    def ingest(self):
        try:
            self.genomicalDataAll = json.loads(self.genomicDataString)
            self.schema.validate(self.genomicalDataAll)
            if not 'type' in self.genomicalDataAll:
                self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: JSON file type not recognised')
                self.log.logMessage('Error: JSON file type not recognised')
                self.errorflag=True
                return -1
            if self.genomicalDataAll['type'].lower().strip()!='genomic':
                self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: JSON file type must be genomic')
                self.log.logMessage('Error: JSON file type must be genomic')
                self.errorflag=True
                return -1
                
            specimen_id, baseline, tar_id, source_txt = self.getSpecimen()
            companyOK=self.checkCompany()
            if not companyOK:
                self.log.logMessage(self.filename + ' could not find source in DB')
                self.log.systemStatusUpdate(self.filename, 'FM', self.log.timestamp(), 'Error: the source is not present in eTARGET')
                raise Exception('Ingest of '+self.filename+' failed -- source not found')
            if(tar_id is None):
                siteOK=self.checkSite()
                if not siteOK:
                    self.log.logMessage(self.filename + ' could not find site in DB')
                    self.log.systemStatusUpdate(self.filename, 'FM', self.log.timestamp(), 'Error: the site is not present in eTARGET')
                    raise Exception('Ingest of '+self.filename+' failed -- site not found')
                self.log.logMessage(self.filename + ' could not find patient')
                self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: Patient not found')
                raise Exception('Ingest of '+self.filename+' failed -- patient not found')
            if(specimen_id is None):
                self.log.logMessage(self.filename + ' could not find specimen_id')
                self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: Specimen not found')
                raise Exception('Ingest of '+self.filename+' failed -- specimen not found')
            if(source_txt is None):
                self.log.logMessage(self.filename + ' could not find source chars')
                self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: Source chars not found')
                raise Exception('Ingest of '+self.filename+' failed -- source not found')
            resubmission,measurement_gene_panel_id=self.checkResubmission(specimen_id, baseline, tar_id, source_txt)
            # resubmission=0: no previous data -- new ingest
            # resubmission=1: it is resubmission and old data can be deleted
            # resubmission=2: it is resubmission and data needs to be new version
            updateSelection=False
            if(resubmission==2):
                updateSelection=True
            if(resubmission==1):
                self.deleteGenomicData(measurement_gene_panel_id)
            measrumentgenepanelid = self.insertMeasurementGenePanel(specimen_id, baseline, source_txt)
            self.parseAlterations(measrumentgenepanelid, tar_id, updateSelection, measurement_gene_panel_id)
            
            if self.errorflag==False:
                # no problems found
                self.conn.commit()
                self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Success')
                self.log.logMessage(self.filename + ' successfully ingested')
                return 0
            else:
                self.rollback()
                self.log.logMessage(self.filename + ' problems with ingestion')
                return -1

        except ValidationError as e:
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: JSON format invalid; check: ' + ' -> '.join(str(x) for x in e.path).replace("'", "''") +"; "+ e.message.replace("'", "''"))
            self.log.logMessage(self.filename + ' json format wrong' + str(e) )
            self.log.logMessage(traceback.format_exc())
            return -1
        except GenomicDataException as e:
            self.log.logMessage("abort ingest because of previous errors")
            self.log.logMessage(traceback.format_exc())
            return -1
        except Exception as e:
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: JSON format cannot be read; ' + str(e).replace("'", "''"))
            self.log.logMessage(self.filename + ' json format wrong' + str(e) )
            self.log.logMessage(traceback.format_exc())
            return -1
        
    def checkSite(self):
        specimentext = self.genomicalDataAll['specimenId'].strip()
        site=specimentext[3:6]
        siteselect = "SELECT * from CARE_SITE where care_site_id= "+site
        cursor = self.conn.cursor()
        cursor.execute(siteselect)
        row = cursor.fetchone()
        cursor.close()
        if row is not None:
            return True
        else:
            return False
        
    def checkCompany(self):
        specimentext = self.genomicalDataAll['specimenId'].strip()
        p2 = re.compile('\D{2}')
        source_txt=p2.findall(specimentext[12:])[0]
        select_source_code = "SELECT data_source_concept_id from CONCEPT_DATA_SOURCES where short_code = '"+source_txt+"'"
        cursor = self.conn.cursor()
        cursor.execute(select_source_code)
        row = cursor.fetchone()
        cursor.close()
        if row is not None:
            return True
        else:
            return False
        
        
    def checkResubmission(self,specimen_id, baseline, tar_id, source_txt):
        checkResubmissionSQL="select measurement_gene_panel_id,ingestion_date from MEASUREMENT_GENE_PANEL as mgp LEFT JOIN SPECIMEN s on mgp.specimen_id=s.specimen_id LEFT JOIN CONCEPT_DATA_SOURCES gp on gp.data_source_concept_id = mgp.data_source_concept_id where s.specimen_id="+str(specimen_id)+" and mgp.baseline_number="+str(baseline)+" and gp.short_code='"+source_txt+"' order by measurement_gene_panel_id desc"
        cursor = self.conn.cursor()
        cursor.execute(checkResubmissionSQL)
        row=cursor.fetchone()
        if row is None:
            return 0,0
        
        measurement_gene_panel_id=row[0]
        ingestion_date=row[1]
        
        lastReportSQL = "SELECT created_on FROM MEETING_OUTCOME as mo LEFT JOIN PERSON as p on p.person_id=mo.person_id where p.target_id='"+tar_id+"' ORDER BY created_on DESC;"
        cursor = self.conn.cursor()
        cursor.execute(lastReportSQL)
        row=cursor.fetchone()
        if row is not None:
            lastDiscussedDate = row[0]
            if lastDiscussedDate>=ingestion_date:
                # new version required; discussed after previous ingestion
                return 2,measurement_gene_panel_id
            else: 
                # discussed but not this report
                return 1,measurement_gene_panel_id
        #never discussed
        return 1, measurement_gene_panel_id
        
    def deleteGenomicData(self, measurement_gene_panel_id):  
        deleteSelectionSQL ="DELETE from SELECTED_GENE_VARIANT where SELECTED_GENE_VARIANT.measurement_gene_variant_id in \
            (select SELECTED_GENE_VARIANT.measurement_gene_variant_id from SELECTED_GENE_VARIANT \
            LEFT JOIN MEASUREMENT_GENE_VARIANT on MEASUREMENT_GENE_VARIANT.measurement_gene_variant_id=SELECTED_GENE_VARIANT.measurement_gene_variant_id \
            where measurement_gene_panel_id="+str(measurement_gene_panel_id)+")" 
        deleteVariantsSQL="DELETE from MEASUREMENT_GENE_VARIANT where measurement_gene_panel_id="+str(measurement_gene_panel_id)
        deletePanelSQL="DELETE from MEASUREMENT_GENE_PANEL where measurement_gene_panel_id="+str(measurement_gene_panel_id)
        cursor = self.conn.cursor()
        cursor.execute(deleteSelectionSQL)
        cursor.execute(deleteVariantsSQL)
        cursor.execute(deletePanelSQL)

        
    def getSpecimen(self):
        if not 'specimenId' in self.genomicalDataAll or len(self.genomicalDataAll['specimenId'])==0:
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: Data import failed (specimenId not present)')
            self.log.logMessage('Error specimenId not present')
            self.errorflag=True
            raise GenomicDataException('Error specimenId not present')
        specimenId=self.genomicalDataAll['specimenId'].strip()
        if not 'sampleType' in self.genomicalDataAll or len(self.genomicalDataAll['sampleType'])==0:
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: Data import failed (sampleType not present)')
            self.log.logMessage('Error sampleType not present')
            self.errorflag=True
            raise GenomicDataException('Error sampleType not present')
        test_type=self.genomicalDataAll['sampleType']
        tarid=specimenId[0:10]
        pattern = re.compile("^[A-Z]{3}\d{7}\D")
        if not re.match(pattern, specimenId):
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: specimen ID is incompatible with the naming convention')
            self.log.logMessage('Error ingesting Genomic data - patient ID has the wrong structure')
            self.errorflag=True
            raise GenomicDataException('Error person id does not match pattern')
        
        timepoint=specimenId[10:]
        pattern = re.compile("^T\d{1,2}(\D|$)")
        pattern2 = re.compile("^Bx\d{1,2}(\D|$)")
        if not re.match(pattern, timepoint) and not re.match(pattern2, timepoint):
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: The time point is incompatible with the naming convention')
            self.log.logMessage('Error ingesting Genomic data - time point has the wrong structure')
            raise GenomicDataException('Error timepoint does not match pattern')
        
        pattern = re.compile("^T\d{1,2}\D{2}")
        pattern2 = re.compile("^Bx\d{1,2}\D{2}")
        if not re.match(pattern, timepoint) and not re.match(pattern2, timepoint):
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: Specimen ID is incompatible with the naming convention')
            self.log.logMessage('Error ingesting Genomic data - 2 char source not found')
            raise GenomicDataException('Error source chars does not match pattern')
        
        p2 = re.compile('\D{2}')
        source_txt=p2.findall(specimenId[12:])[0]
        
        patientselect = "SELECT person_id from PERSON where target_id= '"+tarid+"'"
        print('tarid ', tarid)
        cursor = self.conn.cursor()
        cursor.execute(patientselect)
        row = cursor.fetchone()
        if row is None:
            cursor.close()
            return None, None, None, source_txt
        
        if 'blood' == test_type.lower():
            p = re.compile('T\d+')
            baseline_v=p.findall(specimenId[10:])
            if len(baseline_v)==0:
                self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: specimenId and sampleType are not compatible')
                self.log.logMessage('Error ingesting Genomic data - speicmenID is not of correct structure for blood')
                raise GenomicDataException('Error specimenID is not of correct structure for blood')
            baseline_id=baseline_v[0][1:]
            print('baseline id =' + str(baseline_id));
            
            specimenselect = "SELECT SPECIMEN.specimen_id, SPECIMEN.baseline_number from PERSON LEFT JOIN SPECIMEN on PERSON.person_id=SPECIMEN.person_id where PERSON.target_id='"+tarid+"' and SPECIMEN.specimen_concept_id=1 and SPECIMEN.baseline_number="+str(baseline_id)+""
            print(specimenselect)
            cursor.execute(specimenselect)
            row = cursor.fetchone()
            if row is not None:
                cursor.close()
                return row[0], row[1], tarid, source_txt
            else:
                cursor.close()
                return None, None, tarid, source_txt
        elif 'tissue' == test_type.lower():
            p = re.compile('Bx\d+')
            baseline_v=p.findall(specimenId[10:])
            if len(baseline_v)==0:
                self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: specimenId and sampleType are not compatible')
                self.log.logMessage('Error ingesting Genomic data - speicmenID is not of correct structure for tissue')
                raise GenomicDataException('Error specimenID is not of correct structure for tissue')
            baseline_id=baseline_v[0][2:]
            print('baseline id =' + str(baseline_id));
            
            specimenselect = "SELECT SPECIMEN.specimen_id, SPECIMEN.baseline_number from PERSON LEFT JOIN SPECIMEN on PERSON.person_id=SPECIMEN.person_id where PERSON.target_id='"+tarid+"' and SPECIMEN.specimen_concept_id!=1 and SPECIMEN.baseline_number='"+str(baseline_id)+"'"
            cursor.execute(specimenselect)
            row = cursor.fetchone()
            if row is not None:
                cursor.close()
                return row[0], row[1], tarid, source_txt
            else:
                cursor.close()
                return None, None, tarid, source_txt
        else:
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: sampleType must be either "blood" or "tissue"')
            self.log.logMessage('Error ingesting Genomic data - sampleType is not a recognised string')
            raise GenomicDataException('Error sampleType is not a recognised string')
            
            
    def insertMeasurementGenePanel(self, specimen_id, baseline, source_txt):
        # insert new measuremnt gene panel with empty set of data and gene panel name of 'foundationmedicine'
        try:
            tumourFractionScore= None    
            tumourFractionUnit = None
            mean_exon_depth = None
            percentExons100x = None
            if 'sample' in self.genomicalDataAll:
                sample = self.genomicalDataAll['sample']
                if 'tumourFractionScore' in sample:
                    if 'value' in sample['tumourFractionScore']:
                        tumourFractionScore=sample['tumourFractionScore']['value']
                        if not isinstance(tumourFractionScore, (float, int)):
                            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: tumourFractionScore requires a value field')
                            self.log.logMessage(self.filename + ' value in tumourFractionScore must be number')
                            self.errorflag=True
                            raise GenomicDataException("value in tumourFractionScore must be a number")
                        tumourFractionScore=str(tumourFractionScore)
                    else:
                        self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: tumourFractionScore requires a value')
                        self.log.logMessage(self.filename + ' tumourFractionScore requires a value')
                        self.errorflag=True
                        raise GenomicDataException("missing value in tumourFractionScore")
                    if 'unit' in sample['tumourFractionScore']:
                        tumourFractionUnit=sample['tumourFractionScore']['unit']
            
                    if tumourFractionUnit is not None:
                        tumourFractionUnit=tumourFractionUnit.strip()    
            
                if 'meanExonDepth' in sample:
                    mean_exon_depth = str(sample['meanExonDepth'])
                    print('median exon depth ' + str(mean_exon_depth) )
                
                if 'percentExons100x' in sample:
                    percentExons100x = str(sample['percentExons100x'])
                    print('percentExons100x ' + percentExons100x)
                 
                 
            mistatus=None
            minscore=None
            tmbscore=None
            tmbstatus=None
            tmbunit=None
            
            if 'bioMarkers' in self.genomicalDataAll:   
                biomarkers = self.genomicalDataAll['bioMarkers']
                if not isinstance(biomarkers, list):
                    self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: "bioMarkers" must be a list')
                    self.log.logMessage(self.filename + ' biomarkers must be of type list')
                    self.errorflag=True
                    raise GenomicDataException("value in bioMarker must be a list")
                for biomarker in biomarkers:
                    if not 'type' in biomarker:
                        self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: bioMarkers requires a type field')
                        self.log.logMessage(self.filename + ' bioMarkers requires a type field')
                        self.errorflag=True
                        raise GenomicDataException("bioMarkers requires a type field")
                    if biomarker['type'].lower()=='microsatellite-instability':
                        if not 'status' in biomarker:
                            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: microsatellite-instability requires a status field')
                            self.log.logMessage(self.filename + ' microsatellite-instability requires a status field')
                            self.errorflag=True
                            raise GenomicDataException("microsatellite-instability requires a status field")
                        mistatus=biomarker['status']
                        if len(mistatus.strip())==0:
                            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: status field needs a value in microsatellite-instability')
                            self.log.logMessage(self.filename + ' status field needs a value in microsatellite-instability')
                            self.errorflag=True
                            raise GenomicDataException("status field needs a value in microsatellite-instability")
                        if 'score' in biomarker:
                            minscore=biomarker['score']
                        print(mistatus)
                    elif biomarker['type'].lower()=='tumour mutation burden':
                        if not 'status' in biomarker:
                            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: tumour mutation burden requires a status field')
                            self.log.logMessage(self.filename + ' tumour mutation burden requires a status field')
                            self.errorflag=True
                            raise GenomicDataException("tumour mutation burden requires a status field")
                        tmbstatus=biomarker['status']
                        if len(tmbstatus.strip())==0:
                            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: status field needs a value in tumour mutation burden')
                            self.log.logMessage(self.filename + ' status field needs a value in tumour mutation burden')
                            self.errorflag=True
                            raise GenomicDataException("status field needs a value in tumour mutation burden")
                        if 'score' in biomarker:
                            if not 'value' in biomarker['score']:
                                self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: tumour mutation burden requires a score value')
                                self.log.logMessage(self.filename + ' tumour mutation burden requires a score value')
                                self.errorflag=True
                                raise GenomicDataException("tumour mutation burden requires a score value")
                            tmbscore=str(biomarker['score']['value'])
                            if 'unit' in biomarker['score']:
                                tmbunit=biomarker['score']['unit']
                    else:
                        self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: bioMarker type not supported')
                        self.log.logMessage(self.filename + ' bioMarker type not supported')
                        self.errorflag=True
                        raise GenomicDataException("bioMarker type not supported")
        
            select_gp_concept_id = "SELECT data_source_concept_id from CONCEPT_DATA_SOURCES where short_code = '"+source_txt+"'"
            cursor = self.conn.cursor()
            cursor.execute(select_gp_concept_id)
            row = cursor.fetchone()
            cursor.close()
            if row is not None:
                data_source_concept_id=row[0]
                
            else:
                self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: data source not registered')
                self.log.logMessage(self.filename + ' data source not registered ' + source_txt)
                self.errorflag=True
                raise GenomicDataException("data source not found")

            addFMsql = "INSERT INTO MEASUREMENT_GENE_PANEL (specimen_id, data_source_concept_id,average_read_depth, ngs_run, baseline_number, microsatellite_instability_status, tmb_score, tmb_status, tmb_unit, mean_exon_depth, percent_exons_100x, tumour_fraction_score, tumour_fraction_unit) VALUES(%s, %s, 'n/a', 'FM', %s, %s, %s, %s, %s, %s, %s, %s, %s)"
            cursor = self.conn.cursor()
            cursor.execute(addFMsql, (specimen_id, data_source_concept_id, baseline, mistatus, tmbscore, tmbstatus, tmbunit, mean_exon_depth, percentExons100x, tumourFractionScore, tumourFractionUnit))
            measurement_gene_panel_id = cursor.lastrowid
            print('measurement gene panel id ' + str(measurement_gene_panel_id))
            return measurement_gene_panel_id
        except Exception as e:
            self.log.logMessage('cannot get all biomarker values')
            self.log.logMessage(str(e))
            self.errorflag=True
            print(e)
            raise e
            
    def parseAlterations(self, measrumentgenepanelid, tar_id, updateSelection, prev_gene_panel_id):
        print('parseAlterations')
        if 'alterations' in self.genomicalDataAll:
            for alteration in self.genomicalDataAll['alterations']:
                if not 'type' in alteration:
                    self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: all alterations require a type field')
                    self.log.logMessage(self.filename + ' all alterations require a type field')
                    self.errorflag=True
                    raise GenomicDataException("all alterations require a type field")
                if not isinstance(alteration['type'], str):
                    self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: alterations type field must be string')
                    self.log.logMessage(self.filename + ' alterations type field must be string')
                    self.errorflag=True
                    raise GenomicDataException("alterations type field must be string")
            
                if alteration['type'].lower()=='short variant':
                    self.parseShortVariant(alteration,  measrumentgenepanelid, tar_id, updateSelection, prev_gene_panel_id)
                elif alteration['type'].lower()=='copy number alteration':
                    self.parseCNA(alteration,  measrumentgenepanelid, tar_id, updateSelection, prev_gene_panel_id)
                elif alteration['type'].lower()=='rearrangement':
                    self.parseRearrangement(alteration,  measrumentgenepanelid, tar_id, updateSelection, prev_gene_panel_id)
                else:
                    self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: alterations type not supported')
                    self.log.logMessage(self.filename + ' alterations type not supported')
                    self.errorflag=True
                    raise GenomicDataException("alterations type not supported")
                
    def parseShortVariant(self, alteration,  measrumentgenepanelid, tar_id, updateSelection, prev_gene_panel_id):            
        #parse the sections with short variants and add to db
        print('parseShortVariant')
        allelefraction = None
        cdseffect = None
        depth = None
        gene = None
        position = None
        proteineffect = None
        status = None
        transcript = None
        classification = None
        subclonal = None
        
        if not 'gene' in alteration:
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: gene field is required in short variant alterations')
            self.log.logMessage(self.filename + ' gene field is required in short variant alterations')
            self.errorflag=True
            raise GenomicDataException("gene field is required in short variant alterations")
        gene=alteration['gene']
        if len(gene.strip())==0:
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: gene field needs a value in short variant alterations')
            self.log.logMessage(self.filename + ' gene field needs a value in short variant alterations')
            self.errorflag=True
            raise GenomicDataException("gene field needs a value in short variant alterations")
        if not 'proteinChange' in alteration:
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: proteinChange field is required in short variant alterations')
            self.log.logMessage(self.filename + ' proteinChange field is required in short variant alterations')
            self.errorflag=True
            raise GenomicDataException("proteinChange field is required in short variant alterations")
        proteineffect=alteration['proteinChange']
        if len(proteineffect.strip())==0:
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: proteinChange field needs a value in short variant alterations')
            self.log.logMessage(self.filename + ' proteinChange field needs a value in short variant alterations')
            self.errorflag=True
            raise GenomicDataException("proteinChange field needs a value in short variant alterations")
        if not 'cdsChange' in alteration:
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: cdsChange field is required in short variant alterations')
            self.log.logMessage(self.filename + ' cdsChange field is required in short variant alterations')
            self.errorflag=True
            raise GenomicDataException("cdsChange field is required in short variant alterations")
        cdseffect=alteration['cdsChange']
        if len(cdseffect.strip())==0:
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: cdsChange field needs a value in short variant alterations')
            self.log.logMessage(self.filename + ' cdsChange field needs a value in short variant alterations')
            self.errorflag=True
            raise GenomicDataException("cdsChange field needs a value in short variant alterations")
        if not 'readDepth' in alteration:
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: readDepth field is required in short variant alterations')
            self.log.logMessage(self.filename + ' readDepth field is required in short variant alterations')
            self.errorflag=True
            raise GenomicDataException("readDepth field is required in short variant alterations")
        depth=alteration['readDepth']
        if not 'position' in alteration:
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: position field is required in short variant alterations')
            self.log.logMessage(self.filename + ' position field is required in short variant alterations')
            self.errorflag=True
            raise GenomicDataException("position field is required in short variant alterations")
        position = alteration['position']
        if len(position.strip())==0:
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: position field needs a value in short variant alterations')
            self.log.logMessage(self.filename + ' position field needs a value in short variant alterations')
            self.errorflag=True
            raise GenomicDataException("position field needs a value in short variant alterations")
        
        if 'status' in alteration:
            status=alteration['status']
        if 'transcript' in alteration:
            transcript=alteration['transcript']
        
        if not 'variantAlleleFrequency%' in alteration:
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: variantAlleleFrequency% field is required in short variant alterations')
            self.log.logMessage(self.filename + ' variantAlleleFrequency% field is required in short variant alterations')
            self.errorflag=True
            raise GenomicDataException("variantAlleleFrequency% field is required in short variant alterations")  
        allelefraction=alteration['variantAlleleFrequency%']
        
        
        if not 'functionalEffect' in alteration:
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: functionalEffect field is required in short variant alterations')
            self.log.logMessage(self.filename + ' functionalEffect field is required in short variant alterations')
            self.errorflag=True
            raise GenomicDataException("functionalEffect field is required in short variant alterations")
        classification=alteration['functionalEffect']
        if len(classification.strip())==0:
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: functionalEffect field needs a value in short variant alterations')
            self.log.logMessage(self.filename + ' functionalEffect field needs a value in short variant alterations')
            self.errorflag=True
            raise GenomicDataException("functionalEffect field needs a value in short variant alterations")
        
        if 'subclonal' in alteration:
            subclonal=alteration['subclonal']
            if not isinstance(subclonal, bool):
                self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: subclonal field needs to be boolean in short variant alterations')
                self.log.logMessage(self.filename + ' subclonal field needs to be boolean in short variant alterations')
                self.errorflag=True
                raise GenomicDataException("subclonal field needs to be boolean in short variant alterations")
            subclonal=str(subclonal)
        print(gene + ' ' + position + ' ' + cdseffect)
        gene_id = self.getGene(gene)
        
        #insert into measurement_gene_variant
        addShortVariant = "INSERT INTO MEASUREMENT_GENE_VARIANT (measurement_gene_panel_id, gene_concept_id, cdna_change, read_depth, variant_allele_frequency, position, amino_acid_change, transcript, status, variant_type, functional_effect, subclonal) VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)"
        cursor = self.conn.cursor()
        cursor.execute(addShortVariant,(str(measrumentgenepanelid), str(gene_id), cdseffect, depth, allelefraction, position, proteineffect, transcript, status, 'short_variant', classification, subclonal))
        rowid=cursor.lastrowid
        if updateSelection:
            #check whether was ticked before
            findPreviouseSelectionQuery="select type from SELECTED_GENE_VARIANT FULL OUTER JOIN MEASUREMENT_GENE_VARIANT on \
            MEASUREMENT_GENE_VARIANT.measurement_gene_variant_id=SELECTED_GENE_VARIANT.measurement_gene_variant_id \
            FULL OUTER JOIN MEASUREMENT_GENE_PANEL on MEASUREMENT_GENE_PANEL.measurement_gene_panel_id=MEASUREMENT_GENE_VARIANT.measurement_gene_panel_id \
            where person_id=(SELECT person_id FROM PERSON WHERE person.target_id = '"+tar_id+"') and \
            MEASUREMENT_GENE_PANEL.measurement_gene_panel_id=" +str(prev_gene_panel_id)+ " and gene_concept_id=(SELECT TOP 1 gene_concept_id FROM CONCEPT_GENE \
            WHERE gene_name = '"+gene+"') and amino_acid_change = '"+str(proteineffect)+"' and cdna_change ='"+str(cdseffect)+"';"
            findPreviousSelectionCursor = self.conn.cursor()
            findPreviousSelectionCursor.execute(findPreviouseSelectionQuery)
            row=findPreviousSelectionCursor.fetchone()
            findPreviousSelectionCursor.close()
            if row is not None:
                #was previously selected
                insertSelectionQuery="insert into SELECTED_GENE_VARIANT (person_id, measurement_gene_variant_id, type) values((SELECT person_id FROM PERSON WHERE person.target_id = '"+tar_id+"'),"+str(rowid)+",'"+row[0]+"');"
                insertSelectionCursor = self.conn.cursor()
                insertSelectionCursor.execute(insertSelectionQuery)
                insertSelectionCursor.close()
        return 
         
         
    def parseCNA(self, alteration,  measrumentgenepanelid, tar_id, updateSelection, prev_gene_panel_id):            
        #parse the sections with copy number alteration and add to db
        print('parseCNA')
        copynumber = None
        gene = None
        numberofexons=None
        position = None
        ratio = None
        status = None
        type = None
        equivocal = None
        
        if not 'copyNumber' in alteration:
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: copyNumber field is required in CNA')
            self.log.logMessage(self.filename + ' copyNumber field is required in CNA')
            self.errorflag=True
            raise GenomicDataException("copyNumber field is required in CNA")  
        copynumber=alteration['copyNumber']
        
        if not 'gene' in alteration:
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: gene field is required in CNA')
            self.log.logMessage(self.filename + ' gene field is required in CNA')
            self.errorflag=True
            raise GenomicDataException("gene field is required in CNA")  
        gene=alteration['gene']
        if len(gene.strip())==0:
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: gene field needs a value in CNA')
            self.log.logMessage(self.filename + ' gene field needs a value in CNA')
            self.errorflag=True
            raise GenomicDataException("gene field needs a value in CNA")
        gene_id = self.getGene(gene)
        
        if not 'description' in alteration:
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: description field is required in CNA')
            self.log.logMessage(self.filename + ' description field is required in CNA')
            self.errorflag=True
            raise GenomicDataException("description field is required in CNA")  
        type=alteration['description']
        if len(type.strip())==0:
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: description field needs a value in CNA')
            self.log.logMessage(self.filename + ' description field needs a value in CNA')
            self.errorflag=True
            raise GenomicDataException("description field needs a value in CNA")
        
        if 'exons' in alteration:
            numberofexons=alteration['exons']
        
        if 'position' in alteration:
            position=alteration['position']
            
        if 'ratio' in alteration:
            ratio=alteration['ratio']
                
        if 'status' in alteration:
            status=alteration['status']
            
        if 'equivocal' in alteration:
            equivocal=alteration['equivocal']
            if not isinstance(equivocal, bool):
                self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: equivocal field needs to be boolean in CNA')
                self.log.logMessage(self.filename + ' equivocal field needs to be boolean in CNA')
                self.errorflag=True
                raise GenomicDataException("equivocal field needs to be boolean in CNA")
            equivocal=str(equivocal)
            
        addCopyNumberAlteration = "INSERT INTO MEASUREMENT_GENE_VARIANT (measurement_gene_panel_id, gene_concept_id, copy_number, exons, position, cna_ratio, status, cna_type, variant_type, equivocal) VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)"
        cursor = self.conn.cursor()
        cursor.execute(addCopyNumberAlteration,(str(measrumentgenepanelid), str(gene_id), copynumber, numberofexons, position, ratio, status, type, 'copy_number_alteration', equivocal))
        rowid=cursor.lastrowid
        if updateSelection:
            #check whether was ticked before
            findPreviouseSelectionQuery="select type from SELECTED_GENE_VARIANT FULL OUTER JOIN MEASUREMENT_GENE_VARIANT on \
            MEASUREMENT_GENE_VARIANT.measurement_gene_variant_id=SELECTED_GENE_VARIANT.measurement_gene_variant_id \
            FULL OUTER JOIN MEASUREMENT_GENE_PANEL on MEASUREMENT_GENE_PANEL.measurement_gene_panel_id=MEASUREMENT_GENE_VARIANT.measurement_gene_panel_id \
            where person_id=(SELECT person_id FROM PERSON WHERE person.target_id = '"+tar_id+"') and \
            MEASUREMENT_GENE_PANEL.measurement_gene_panel_id=" +str(prev_gene_panel_id)+ " and gene_concept_id=(SELECT TOP 1 gene_concept_id FROM CONCEPT_GENE \
            WHERE gene_name = '"+gene+"') and copy_number = "+str(copynumber)+" and exons ='"+str(numberofexons)+"' and position='"+str(position)+"' and cna_ratio="+str(ratio)+" and  status='"+str(status)+"' and cna_type='"+str(type)+"';"
#                 print(findPreviouseSelectionQuery)
            findPreviousSelectionCursor = self.conn.cursor()
            findPreviousSelectionCursor.execute(findPreviouseSelectionQuery)
            row=findPreviousSelectionCursor.fetchone()
#                 print(row)
            findPreviousSelectionCursor.close()
            if row is not None:
                #was previously selected
                insertSelectionQuery="insert into SELECTED_GENE_VARIANT (person_id, measurement_gene_variant_id, type) values((SELECT person_id FROM PERSON WHERE person.target_id = '"+tar_id+"'),"+str(rowid)+",'"+row[0]+"');"
#                     print("---------\n",insertSelectionQuery)
                insertSelectionCursor = self.conn.cursor()
                insertSelectionCursor.execute(insertSelectionQuery)
            
        
    def parseRearrangement(self, alteration,  measrumentgenepanelid, tar_id, updateSelection, prev_gene_panel_id):            
        #parse the sections with rearrangement and add to db
        print('parseRearrangement')       
        description = None
        inframe = None
        pos1 = None
        pos2 = None
        status = None
        supportingreadpairs = None
        type = None
        allelefraction = None
        rtype=None
        
        if not 'gene1' in alteration:
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: gene1 field is required in rearrangements')
            self.log.logMessage(self.filename + ' gene1 field is required in rearrangements')
            self.errorflag=True
            raise GenomicDataException("gene1 field is required in rearrangements")  
        if len(alteration['gene1'].strip())==0:
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: gene1 field needs a value in rearrangements')
            self.log.logMessage(self.filename + ' gene1 field needs a value in rearrangements')
            self.errorflag=True
            raise GenomicDataException("gene1 field needs a value in rearrangements")
        gene1_id = self.getGene(alteration['gene1'])
        
        if not 'gene2' in alteration:
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: gene2 field is required in rearrangements')
            self.log.logMessage(self.filename + ' gene2 field is required in rearrangements')
            self.errorflag=True
            raise GenomicDataException("gene2 field is required in rearrangements")  
        if len(alteration['gene2'].strip())==0:
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: gene2 field needs a value in rearrangements')
            self.log.logMessage(self.filename + ' gene2 field needs a value in rearrangements')
            self.errorflag=True
            raise GenomicDataException("gene2 field needs a value in rearrangements")
        gene2_id = self.getGene(alteration['gene2'])    
        
        if not 'rearrangementType' in alteration:
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: rearrangementType field is required in rearrangements')
            self.log.logMessage(self.filename + ' rearrangementType field is required in rearrangements')
            self.errorflag=True
            raise GenomicDataException("rearrangementType field is required in rearrangements")  
        rtype = alteration['rearrangementType'] 
        if len(rtype.strip())==0:
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: rearrangementType field needs a value in rearrangements')
            self.log.logMessage(self.filename + ' rearrangementType field needs a value in rearrangements')
            self.errorflag=True
            raise GenomicDataException("rearrangementType field needs a value in rearrangements")
        
        if not 'position1' in alteration:
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: position1 field is required in rearrangements')
            self.log.logMessage(self.filename + ' position1 field is required in rearrangements')
            self.errorflag=True
            raise GenomicDataException("position1 field is required in rearrangements")  
        pos1 = alteration['position1'] 
        if len(pos1.strip())==0:
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: position1 field needs a value in rearrangements')
            self.log.logMessage(self.filename + ' position1 field needs a value in rearrangements')
            self.errorflag=True
            raise GenomicDataException("position1 field needs a value in rearrangements")
        if not 'position2' in alteration:
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: position2 field is required in rearrangements')
            self.log.logMessage(self.filename + ' position2 field is required in rearrangements')
            self.errorflag=True
            raise GenomicDataException("position2 field is required in rearrangements")  
        pos2 = alteration['position2']
        if len(pos2.strip())==0:
            self.log.systemStatusUpdate(self.filename, 'Genomic', self.log.timestamp(), 'Error: position2 field needs a value in rearrangements')
            self.log.logMessage(self.filename + ' position2 field needs a value in rearrangements')
            self.errorflag=True
            raise GenomicDataException("position2 field needs a value in rearrangements")
        
        if 'status' in alteration:
            status=alteration['status']
            
        if 'variantAlleleFrequency' in alteration:
            allelefraction=alteration['variantAlleleFrequency']
            
        if 'inFrame' in alteration:
            inframe = alteration['inFrame']
            
        if 'supportingReadPairs' in alteration:
            supportingreadpairs = alteration['supportingReadPairs']
        
        if 'description' in alteration:
            description =  alteration['description']
            
        #insert into measurement_gene_variant
        addRearrangement = "INSERT INTO MEASUREMENT_GENE_VARIANT (measurement_gene_panel_id, rearr_description, rearr_in_frame, rearr_gene_2, \
        rearr_pos1, rearr_pos2, status, rearr_number_of_reads, rearr_gene_1, variant_type, variant_allele_frequency, rearr_type) VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)"
        cursor = self.conn.cursor()
        cursor.execute(addRearrangement, (str(measrumentgenepanelid), description, inframe, str(gene2_id), pos1, pos2, status, supportingreadpairs, str(gene1_id),'rearrangement', allelefraction, rtype))
        rowid=cursor.lastrowid
        if updateSelection:
            #check whether was ticked before
            findPreviouseSelectionQuery="select type from SELECTED_GENE_VARIANT FULL OUTER JOIN MEASUREMENT_GENE_VARIANT on \
            MEASUREMENT_GENE_VARIANT.measurement_gene_variant_id=SELECTED_GENE_VARIANT.measurement_gene_variant_id \
            FULL OUTER JOIN MEASUREMENT_GENE_PANEL on MEASUREMENT_GENE_PANEL.measurement_gene_panel_id=MEASUREMENT_GENE_VARIANT.measurement_gene_panel_id \
            where person_id=(SELECT person_id FROM PERSON WHERE person.target_id = '"+tar_id+"') and \
            MEASUREMENT_GENE_PANEL.measurement_gene_panel_id=" +str(prev_gene_panel_id)+ " and  \
            rearr_gene_1="+str(gene1_id)+" and rearr_description = '"+str(description)+"' and rearr_in_frame ='"+str(inframe)+"' and  rearr_gene_2="+str(gene2_id)+" \
            and rearr_pos1='"+str(pos1)+"' and rearr_pos2='"+str(pos2)+"' and status='"+str(status)+"' and rearr_number_of_reads="+str(supportingreadpairs)+";"
#                 print(findPreviouseSelectionQuery)
            findPreviousSelectionCursor = self.conn.cursor()
            findPreviousSelectionCursor.execute(findPreviouseSelectionQuery)
            row=findPreviousSelectionCursor.fetchone()
#                 print(row)
            findPreviousSelectionCursor.close()
            if row is not None:
                #was previously selected
                insertSelectionQuery="insert into SELECTED_GENE_VARIANT (person_id, measurement_gene_variant_id, type) values((SELECT person_id FROM PERSON WHERE person.target_id = '"+tar_id+"'),"+str(rowid)+",'"+row[0]+"');"
#                     print("---------\n",insertSelectionQuery)
                insertSelectionCursor = self.conn.cursor()
                insertSelectionCursor.execute(insertSelectionQuery)
                insertSelectionCursor.close()
         
                
    def getGene(self, gene):
        genesql = "SELECT gene_concept_id FROM CONCEPT_GENE WHERE gene_name = '"+str(gene)+"'"
        cursor = self.conn.cursor()
        cursor.execute(genesql)
        row = cursor.fetchone()
        if row is not None:
            print(row[0])
            return row[0]
        insertgene = "INSERT INTO CONCEPT_GENE (gene_name) VALUES(%s)"
        cursor = self.conn.cursor()
        cursor.execute(insertgene,(str(gene)))
        gene_id = cursor.lastrowid
        print(str(gene_id))
        return gene_id            
