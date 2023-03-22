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
import xml.etree.ElementTree as ET
import utilities
from fileinput import filename
from azure.storage.file import FileService
import re

class FoundationMedicine:
    def __init__(self, filename, remotehostname, remoteusername, remotepassword, remotedbname, fileuser, filekey, datadir='data', logblob='log'):
        self.filename = filename
        self.datadir=datadir
        self.file_service = FileService(account_name=fileuser, account_key=filekey)
        self.fmFileString = self.file_service.get_file_to_text(self.datadir, None, filename).content
        self.remotehostname = remotehostname
        self.remoteusername = remoteusername
        self.remotepassword = remotepassword
        self.remotedbname = remotedbname
        print(self.remotehostname + "\n" + self.remoteusername+"\n"+self.remotepassword+"\n"+self.remotedbname)
        self.conn = pymssql.connect(self.remotehostname,self.remoteusername, self.remotepassword, self.remotedbname, autocommit=False)
        self.root = ET.fromstring(self.fmFileString)
        self.log = utilities.Util(remotehostname, remoteusername, remotepassword, remotedbname, fileuser, filekey, logblob)
        
        
    def ingest(self):
        print(self.root.tag)
        print(self.root.attrib)
        specimen_id, baseline, tar_id, source_txt = self.getSpecimen()
        
        if(tar_id is None):
            siteOK=self.checkSite()
            if not siteOK:
                self.log.logMessage(self.filename + ' could not find site in DB')
                self.log.systemStatusUpdate(self.filename, 'FM', self.log.timestamp(), 'Error: the site is not present in eTARGET')
                raise Exception('Ingest of '+self.filename+' failed -- site not found')
                
            self.log.logMessage(self.filename + ' could not find patient')
            self.log.systemStatusUpdate(self.filename, 'FM', self.log.timestamp(), 'Error: Patient not found')
            raise Exception('Ingest of '+self.filename+' failed -- patient not found')
        if(specimen_id is None):
            self.log.logMessage(self.filename + ' could not find specimen_id')
            self.log.systemStatusUpdate(self.filename, 'FM', self.log.timestamp(), 'Error: Specimen not found')
            raise Exception('Ingest of '+self.filename+' failed -- specimen not found')
        if(source_txt is None):
            self.log.logMessage(self.filename + ' could not find source chars')
            self.log.systemStatusUpdate(self.filename, 'FM', self.log.timestamp(), 'Error: Source chars not found')
            raise Exception('Ingest of '+self.filename+' failed -- source not found')
        else:
            sourceOK=self.checkSource(source_txt)
            if not sourceOK:
                self.log.logMessage(self.filename + ' could not find source in DB')
                self.log.systemStatusUpdate(self.filename, 'FM', self.log.timestamp(), 'Error: the source is not present in eTARGET')
                raise Exception('Ingest of '+self.filename+' failed -- source not found')
        try:
            resubmission,measurement_gene_panel_id=self.checkResubmission(specimen_id, baseline, tar_id, source_txt)
            print(resubmission, measurement_gene_panel_id)
            # resubmission=0: no previous data -- new ingest
            # resubmission=1: it is resubmission and old data can be deleted
            # resubmission=2: it is resubmission and data needs to be new version
            if(resubmission==1):
                self.deleteFMdata(measurement_gene_panel_id)
                #check again in case there is an even older version
                resubmission,measurement_gene_panel_id=self.checkResubmission(specimen_id, baseline, tar_id, source_txt)
                print('after delete the previous version', resubmission, measurement_gene_panel_id)
            updateSelection=False
            if(resubmission==2):
                updateSelection=True
            measrumentgenepanelid = self.insertMeasurementGenePanel(specimen_id, baseline, source_txt)
            self.parseShortVariants(measrumentgenepanelid, tar_id, updateSelection, measurement_gene_panel_id)
            self.parseCopyNumberAlterations(measrumentgenepanelid, tar_id, updateSelection, measurement_gene_panel_id)
            self.parseRearrangements(measrumentgenepanelid, tar_id, updateSelection, measurement_gene_panel_id)
            # came to this place all OK commit all changes
            self.conn.commit()
            self.log.systemStatusUpdate(self.filename, 'FM', self.log.timestamp(), 'Success')
        except Exception as e:
            print(e)
            self.log.logMessage(self.filename + ' error writing to DB')
            self.log.systemStatusUpdate(self.filename, 'FM', self.log.timestamp(), 'error writing to DB')
            self.rollback()
            raise Exception('Ingest of '+self.filename+' failed')
        
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
        
    def deleteFMdata(self, measurement_gene_panel_id):  
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
        
    def checkSource(self, source_txt):
        select_gp_concept_id = "SELECT * from CONCEPT_DATA_SOURCES where short_code = '"+source_txt+"'"
        cursor = self.conn.cursor()
        cursor.execute(select_gp_concept_id)
        row = cursor.fetchone()
        cursor.close()
        if row is not None:
            return True
        else:
            return False
    
    def checkSite(self):
        payload = self.root.find('{http://integration.foundationmedicine.com/reporting}ResultsPayload')
        self.variant_report = payload.find('{http://foundationmedicine.com/compbio/variant-report-external}variant-report')
        MRN = payload.find('.//MRN')
        
        specimentext=MRN.text;
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
        
    def getSpecimen(self):

        payload = self.root.find('{http://integration.foundationmedicine.com/reporting}ResultsPayload')
        self.variant_report = payload.find('{http://foundationmedicine.com/compbio/variant-report-external}variant-report')
        MRN = payload.find('.//MRN')
        
        specimentext=MRN.text;
        #specimentext = self.variant_report.get('specimen')
        test_type = self.variant_report.get('test-type')
        if specimentext is None or len(specimentext)==0:
            self.log.systemStatusUpdate(self.filename, 'FM', self.log.timestamp(), 'Error: specimen ID is required in field MRN')
            self.log.logMessage('Error ingesting FM data - MRN is empty')
            raise Exception('Error MRN field required')
        specimentext = specimentext.strip()
        tarid=specimentext[0:10]
        pattern = re.compile("^[A-Z]{3}\d{7}\D")
        if not re.match(pattern, specimentext):
            self.log.systemStatusUpdate(self.filename, 'FM', self.log.timestamp(), 'Error: specimen ID is incompatible with the naming convention')
            self.log.logMessage('Error ingesting FM data - patient ID has the wrong structure')
            raise Exception('Error person id does not match pattern')
        
        timepoint=specimentext[10:]
        pattern = re.compile("^T\d{1,2}(\D|$)")
        pattern2 = re.compile("^Bx\d{1,2}(\D|$)")
        if not re.match(pattern, timepoint) and not re.match(pattern2, timepoint):
            self.log.systemStatusUpdate(self.filename, 'FM', self.log.timestamp(), 'Error: The time point is incompatible with the naming convention')
            self.log.logMessage('Error ingesting FM data - time point has the wrong structure')
            raise Exception('Error timepoint does not match pattern')
        
        pattern = re.compile("^T\d{1,2}\D{2}")
        pattern2 = re.compile("^Bx\d{1,2}\D{2}")
        if not re.match(pattern, timepoint) and not re.match(pattern2, timepoint):
            self.log.systemStatusUpdate(self.filename, 'FM', self.log.timestamp(), 'Error: Specimen ID is incompatible with the naming convention')
            self.log.logMessage('Error ingesting FM data - 2 char source not found')
            raise Exception('Error source chars does not match pattern')
        
        p2 = re.compile('\D{2}')
        source_txt=p2.findall(specimentext[12:])[0]
        
        patientselect = "SELECT person_id from PERSON where target_id= '"+tarid+"'"
        print('tarid ', tarid)
        cursor = self.conn.cursor()
        cursor.execute(patientselect)
        row = cursor.fetchone()
        if row is None:
            cursor.close()
            return specimentext, None, None, source_txt
        if not 'FoundationOneLiquidDx' in test_type:
            #Tumour
#             specimenid=specimentext[0:specimentext.find('FM')]
#             print('TAR : ' + tarid + ' tumour ' + specimenid)
#             #query for sample_id
#             specimenselect = "SELECT SPECIMEN.specimen_id, SPECIMEN.baseline_number from PERSON LEFT JOIN SPECIMEN on PERSON.person_id=SPECIMEN.person_id where PERSON.target_id='"+tarid+"' and SPECIMEN.preclin_id='"+specimenid+"'"
            p = re.compile('Bx\d+')
            matches=p.findall(specimentext[10:])
            if len(matches)==0:
                self.log.systemStatusUpdate(self.filename, 'FM', self.log.timestamp(), 'Error: specimenId is not compatible with the profiling test')
                self.log.logMessage('Error ingesting FM data - specimenId is not compatible with the profiling test')
                raise Exception('Error specimenId is not compatible with the profiling test')
            baseline_txt=matches[0]
            baseline_id=baseline_txt[2:]
            print('baseline id =' + str(baseline_id));
            
            specimenselect = "SELECT SPECIMEN.specimen_id, SPECIMEN.baseline_number from PERSON LEFT JOIN SPECIMEN on PERSON.person_id=SPECIMEN.person_id where PERSON.target_id='"+tarid+"' and SPECIMEN.specimen_concept_id!=1 and SPECIMEN.baseline_number="+str(baseline_id)+""
            print(specimenselect)
            cursor.execute(specimenselect)
            row = cursor.fetchone()
            if row is not None:
                cursor.close()
                return row[0], row[1], tarid, source_txt
            else:
                cursor.close()
                return None, None, tarid, source_txt
        else:
            p = re.compile('T\d+')
            matches=p.findall(specimentext[10:])
            if len(matches)==0:
                self.log.systemStatusUpdate(self.filename, 'FM', self.log.timestamp(), 'Error: specimenId is not compatible with the profiling test')
                self.log.logMessage('Error ingesting FM data - specimenId is not compatible with the profiling test')
                raise Exception('Error specimenId is not compatible with the profiling test')
            baseline_txt=matches[0]
            baseline_id=baseline_txt[1:]
            print('baseline id =' + str(baseline_id));
            
            specimenselect = "SELECT SPECIMEN.specimen_id, SPECIMEN.baseline_number from PERSON LEFT JOIN SPECIMEN on PERSON.person_id=SPECIMEN.person_id where PERSON.target_id='"+tarid+"' and SPECIMEN.specimen_concept_id=1 and SPECIMEN.baseline_number='"+str(baseline_id)+"'"
            cursor.execute(specimenselect)
            row = cursor.fetchone()
            if row is not None:
                cursor.close()
                return row[0], row[1], tarid, source_txt
            else:
                cursor.close()
                return None, None, tarid, source_txt
            
            
        
    def insertMeasurementGenePanel(self, specimen_id, baseline, source_txt):
        # insert new measuremnt gene panel with empty set of data and gene panel name of 'foundationmedicine'
        try:
#             reportProperty = self.root.find('.//')
            payload = self.root.find('./{http://integration.foundationmedicine.com/reporting}ResultsPayload')
            for child in payload:
                print(child.tag, child.attrib)
            
            tumourFractionScore= None    
            tumourFractionUnit = None
            tumourFraction = self.root.find(".//reportProperty[@key='TumorFractionScore']")
            if tumourFraction is not None:
                tumourFractionText=tumourFraction.find('./value').text
                try:
                    x,tumourFractionScore,tumourFractionUnit=re.split(r'[-+]?([0-9]*\.[0-9]+|[0-9]+)',tumourFractionText, 1)
                except Exception as e:
                    self.log.logMessage('TumourFractionScore present but does not contain number ' + tumourFractionText)
                    self.log.logMessage(str(e))
            if tumourFractionUnit is not None:
                tumourFractionUnit=tumourFractionUnit.strip()    
            
                
            sample = self.variant_report.find('.//{http://foundationmedicine.com/compbio/variant-report-external}sample')
            print(str(sample))
            mean_exon_depth = sample.get('mean-exon-depth')
            print('median exon depth ' + str(mean_exon_depth) )
            biomarkers = self.variant_report.find('{http://foundationmedicine.com/compbio/variant-report-external}biomarkers')
            micorsatelliteinst = biomarkers.find('{http://foundationmedicine.com/compbio/variant-report-external}microsatellite-instability')
#             miscore = micorsatelliteinst.attrib['score']
            mistatus = micorsatelliteinst.attrib['status']
            print(mistatus)
            tmb = biomarkers.find('{http://foundationmedicine.com/compbio/variant-report-external}tumor-mutation-burden')
            tmbscore = tmb.attrib['score']
            tmbstatus = tmb.attrib['status']
            tmbunit = tmb.attrib['unit']
            print(tmbstatus)
        
            select_gp_concept_id = "SELECT data_source_concept_id from CONCEPT_DATA_SOURCES where short_code = '"+source_txt+"'"
            cursor = self.conn.cursor()
            cursor.execute(select_gp_concept_id)
            row = cursor.fetchone()
            cursor.close()
            if row is not None:
                data_source_concept_id=row[0]
                
            else:
                self.log.logMessage('cannot find '+source_txt+' gene_panel')
                raise Exception('cannot find '+source_txt+' gene_panel')
         
            addFMsql = "INSERT INTO MEASUREMENT_GENE_PANEL (specimen_id, data_source_concept_id,average_read_depth, ngs_run, baseline_number, microsatellite_instability_status, tmb_score, tmb_status, tmb_unit, mean_exon_depth, tumour_fraction_score, tumour_fraction_unit) VALUES(%s, %s, 'n/a', 'FM', %s, %s, %s, %s, %s, %s, %s, %s)"
            cursor = self.conn.cursor()
            cursor.execute(addFMsql, (str(specimen_id), str(data_source_concept_id), str(baseline), str(mistatus), str(tmbscore), str(tmbstatus), str(tmbunit), mean_exon_depth, tumourFractionScore, tumourFractionUnit))
            measurement_gene_panel_id = cursor.lastrowid
            print('measurement gene panel id ' + str(measurement_gene_panel_id))
            return measurement_gene_panel_id
        except Exception as e:
            self.log.logMessage('cannot get all biomarker values')
            self.log.logMessage(str(e))
            print(e)

    def parseShortVariants(self, measrumentgenepanelid, tar_id, updateSelection, prev_gene_panel_id):
        #parse the sections with short variants and add to db
        print('measurement gene panel id ' + str(measrumentgenepanelid))
        for sv in self.variant_report.iter(tag = '{http://foundationmedicine.com/compbio/variant-report-external}short-variant'):
            cdseffect = sv.attrib['cds-effect']
            cdseffect = cdseffect.replace("&gt;", ">")
            depth = sv.attrib['depth']
            gene = sv.attrib['gene']
            percentread = sv.attrib['percent-reads']
            position = sv.attrib['position']
            proteineffect = sv.attrib['protein-effect']
            stat = sv.attrib['status']
            transcript = sv.attrib['transcript']
            functional_effect = sv.attrib['functional-effect']
            try:
                subclonal = sv.attrib['subclonal']
            except KeyError:
                subclonal = None
            print(gene + ' ' + position + ' ' + cdseffect)
            gene_id = self.getGene(gene)
            
            #insert into measurement_gene_variant
            addShortVariant = "INSERT INTO MEASUREMENT_GENE_VARIANT (measurement_gene_panel_id, gene_concept_id, cdna_change, read_depth, variant_allele_frequency, position, amino_acid_change, transcript, status, variant_type, functional_effect, subclonal) VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)"
            cursor = self.conn.cursor()
            cursor.execute(addShortVariant,(str(measrumentgenepanelid), str(gene_id), str(cdseffect), str(depth), str(percentread), str(position), str(proteineffect), str(transcript), str(stat), 'short_variant', functional_effect, subclonal))
            rowid=cursor.lastrowid
            if updateSelection:
                #check whether was ticked before
                findPreviouseSelectionQuery="select type from SELECTED_GENE_VARIANT FULL OUTER JOIN MEASUREMENT_GENE_VARIANT on \
                MEASUREMENT_GENE_VARIANT.measurement_gene_variant_id=SELECTED_GENE_VARIANT.measurement_gene_variant_id \
                FULL OUTER JOIN MEASUREMENT_GENE_PANEL on MEASUREMENT_GENE_PANEL.measurement_gene_panel_id=MEASUREMENT_GENE_VARIANT.measurement_gene_panel_id \
                where person_id=(SELECT person_id FROM PERSON WHERE person.target_id = '"+tar_id+"') and \
                MEASUREMENT_GENE_PANEL.measurement_gene_panel_id=" +str(prev_gene_panel_id)+ " and gene_concept_id=(SELECT TOP 1 gene_concept_id FROM CONCEPT_GENE \
                WHERE gene_name = '"+gene+"') and amino_acid_change = '"+str(proteineffect)+"' and cdna_change ='"+str(cdseffect)+"';"
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
        return
        
    def parseCopyNumberAlterations(self, measrumentgenepanelid, tar_id, updateSelection, prev_gene_panel_id):
        #parse the sections with copy number alterations and add to db
        for cna in self.variant_report.iter(tag= '{http://foundationmedicine.com/compbio/variant-report-external}copy-number-alteration'):
            copynumber = cna.attrib['copy-number']
            gene = cna.attrib['gene']
            numberofexons=cna.attrib['number-of-exons']
            position = cna.attrib['position']
            ratio = cna.attrib['ratio']
            stat = cna.attrib['status']
            type = cna.attrib['type']
            try:
                equivocal= cna.attrib['equivocal']
            except KeyError:
                equivocal = None
            gene_id = self.getGene(gene)
            
            #insert into measurement_gene_variant
            addCopyNumberAlteration = "INSERT INTO MEASUREMENT_GENE_VARIANT (measurement_gene_panel_id, gene_concept_id, copy_number, exons, position, cna_ratio, status, cna_type, variant_type, equivocal) VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)"
            cursor = self.conn.cursor()
            cursor.execute(addCopyNumberAlteration,(str(measrumentgenepanelid), str(gene_id), str(copynumber), str(numberofexons), str(position), str(ratio), str(stat), str(type), 'copy_number_alteration', equivocal))
            rowid=cursor.lastrowid
            if updateSelection:
                #check whether was ticked before
                findPreviouseSelectionQuery="select type from SELECTED_GENE_VARIANT FULL OUTER JOIN MEASUREMENT_GENE_VARIANT on \
                MEASUREMENT_GENE_VARIANT.measurement_gene_variant_id=SELECTED_GENE_VARIANT.measurement_gene_variant_id \
                FULL OUTER JOIN MEASUREMENT_GENE_PANEL on MEASUREMENT_GENE_PANEL.measurement_gene_panel_id=MEASUREMENT_GENE_VARIANT.measurement_gene_panel_id \
                where person_id=(SELECT person_id FROM PERSON WHERE person.target_id = '"+tar_id+"') and \
                MEASUREMENT_GENE_PANEL.measurement_gene_panel_id=" +str(prev_gene_panel_id)+ " and gene_concept_id=(SELECT TOP 1 gene_concept_id FROM CONCEPT_GENE \
                WHERE gene_name = '"+gene+"') and copy_number = "+str(copynumber)+" and exons ='"+str(numberofexons)+"' and position='"+str(position)+"' and cna_ratio="+str(ratio)+" and  status='"+str(stat)+"' and cna_type='"+str(type)+"';"
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
        return
        
    def parseRearrangements(self, measrumentgenepanelid, tar_id, updateSelection, prev_gene_panel_id):
        for ra in self.variant_report.iter(tag='{http://foundationmedicine.com/compbio/variant-report-external}rearrangement'):
            description = ra.attrib['description']
            inframe = ra.attrib['in-frame']
            othergene = ra.attrib['other-gene']
            pos1 = ra.attrib['pos1']
            pos2 = ra.attrib['pos2']
            stat = ra.attrib['status']
            supportingreadpairs = ra.attrib['supporting-read-pairs']
            targetgene = ra.attrib['targeted-gene']
            type = ra.attrib['type']
            allelefraction = ra.attrib['percent-reads']
            
            gene1_id = self.getGene(targetgene)
            gene2_id = self.getGene(othergene)
            
            #insert into measurement_gene_variant
            addRearrangement = "INSERT INTO MEASUREMENT_GENE_VARIANT (measurement_gene_panel_id, rearr_description, rearr_in_frame, rearr_gene_2, \
            rearr_pos1, rearr_pos2, status, rearr_number_of_reads, rearr_gene_1, variant_type,variant_allele_frequency,rearr_type) VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)"
            cursor = self.conn.cursor()
            cursor.execute(addRearrangement, (str(measrumentgenepanelid), description, inframe, str(gene2_id), pos1, pos2, stat, supportingreadpairs, str(gene1_id),'rearrangement', allelefraction,type))
            rowid=cursor.lastrowid
            if updateSelection:
                #check whether was ticked before
                findPreviouseSelectionQuery="select type from SELECTED_GENE_VARIANT FULL OUTER JOIN MEASUREMENT_GENE_VARIANT on \
                MEASUREMENT_GENE_VARIANT.measurement_gene_variant_id=SELECTED_GENE_VARIANT.measurement_gene_variant_id \
                FULL OUTER JOIN MEASUREMENT_GENE_PANEL on MEASUREMENT_GENE_PANEL.measurement_gene_panel_id=MEASUREMENT_GENE_VARIANT.measurement_gene_panel_id \
                where person_id=(SELECT person_id FROM PERSON WHERE person.target_id = '"+tar_id+"') and \
                MEASUREMENT_GENE_PANEL.measurement_gene_panel_id=" +str(prev_gene_panel_id)+ " and  \
                rearr_gene_1="+str(gene1_id)+" and rearr_description = '"+str(description)+"' and rearr_in_frame ='"+str(inframe)+"' and  rearr_gene_2="+str(gene2_id)+" \
                and rearr_pos1='"+str(pos1)+"' and rearr_pos2='"+str(pos2)+"' and status='"+stat+"' and rearr_number_of_reads="+str(supportingreadpairs)+";"
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
        return
    
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
      
    def deleteFile(self):
        try:
            if self.file_service.exists(self.datadir, None, self.filename):
                self.file_service.delete_file(self.datadir, None, self.filename)
            self.log.systemStatusUpdate(self.filename, 'FM', self.log.timestamp(), 'Success')
        except:
            self.log.logMessage('There was a problem deleting '+self.filename)
    
    def rollback(self):
        self.conn.rollback()
        self.conn.close()
        
