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

'''
  Parse data from CSV files into the TARGET database.
  @author rob.dunne@manchester.ac.uk
  July 2017

  0. Start: cronjob runs this
  1. Check for files in the Samba network share: getNewFiles()
  2. Loop over files: getNewFiles()
  3. Process the file contents: processFile()
  4. Update the database: updateDatabase()
  5. Delete the file: processFile()
  6. Repeat until no more files
  7. End.
'''

import csv
import pymssql
import os
import sys
import datetime
from azure.storage.file import FileService
from azure.storage.blob import AppendBlobService
from azure.storage.blob import BlockBlobService
from azure.storage.blob import ContentSettings
import io
import base64
from Crypto import Random
from Crypto.Cipher import AES
from ftpretty import ftpretty
import xml.etree.ElementTree as ET
import hashlib
import foundationmedicine
import clinicaldata
import clinical_json
import genomicdata_json
import ihc_report
import re
import utilities
from pycel.lib.lookup import row
from fileinput import filename
from openpyxl.compat.strings import file
from azure.storage.file.models import File
from builtins import str

class TargetData():
    # Constructor
    def __init__(self):
        self.filepath = os.path.dirname(os.path.abspath(__file__))
        self.config = self.getConfig()
        self.AKEY = self.config['patientkey']
        self.iv = Random.new().read(AES.block_size)
        self.file_service = FileService(account_name=self.config['fileuser'], account_key=self.config['filekey'])
        self.append_blob_service = AppendBlobService(account_name=self.config['fileuser'], account_key=self.config['filekey'])
        self.block_blob_service = BlockBlobService(account_name=self.config['fileuser'], account_key=self.config['filekey'])
        self.pdf_blob_service = BlockBlobService(connection_string=self.config['storageurl'])
        self.container_name = self.config['containername']
        self.log = utilities.Util(self.config['remotehostname'], self.config['remoteusername'], \
                    self.config['remotepassword'], self.config['remotedbname'],self.config['fileuser'],self.config['filekey'],self.config['logblob'])
        self.conn = pymssql.connect(self.config['remotehostname'], self.config['remoteusername'], self.config['remotepassword'], self.config['remotedbname'], autocommit=False)
        try:
            assert 'datadir' in self.config
            self.data=self.config['datadir']
        except Exception as e:
            self.data='data'
            
        self.getNewFiles()

    def getNewFiles(self):
        # Get new files
        print('')
        processedFiles = 0
        #files = os.listdir(self.filepath)
        files = self.file_service.list_directories_and_files(self.data)
        for file in files:
            try:
                # Filter the files
                if file.name.lower().endswith('.csv') and not file.name.startswith('.'):
                    try:
                        fileContent = self.file_service.get_file_to_text(self.data, None, file.name)
                    except Exception as e:
                        fileContent = self.file_service.get_file_to_text(self.data, None, file.name, encoding="cp1252")
                    fileContentString = fileContent.content
                    firstCell = fileContentString.split(',')
    
                    if 'Patient_ID' in firstCell[0]:
                        # Blood sample report
                        self.log.logMessage('Processing blood report: '+file.name)
                        self.processBloodFile(file.name)
                        processedFiles = processedFiles+1
    
                    elif 'hospitalnumber' in firstCell[0].lower():
                        # Christie patient data
                        self.log.logMessage('Processing Christie data: '+file.name)
                        cd = clinicaldata.ClinicalData(file.name,self.config['remotehostname'], self.config['remoteusername'], \
                                                       self.config['remotepassword'], self.config['remotedbname'],self.config['fileuser'],self.config['filekey'],self.data, self.config['logblob'])
                        if cd.ingest()==0:
                            cd.deleteFile()
                        #self.processChristieFile(file.name)
                        processedFiles = processedFiles+1
    
                    else:
                        self.log.logMessage('Unknown CSV file: '+file.name)
                        self.log.logMessage('Skipping file: '+file.name)
    
                if file.name.lower().endswith('.pdf') and not file.name.startswith('.'):
                    pdf = self.file_service.get_file_to_bytes(self.data, None, file.name)
                    with open(self.filepath+'/tmp/'+file.name, 'wb') as f:
                        f.write(pdf.content)
                        self.processPDFReport(file.name)
                        
                if file.name.lower().endswith('.jpg') and not file.name.startswith('.'):
                    self.processIHCPictures(file.name)
    
                if file.name.lower().endswith('.xml') and not file.name.startswith('.'):
                    fileContent = self.file_service.get_file_to_text(self.data, None, file.name)
                    fileContentString = fileContent.content
                    if('foundationmedicine.com' in fileContentString):
                        try:
                            fm = foundationmedicine.FoundationMedicine(file.name,self.config['remotehostname'], self.config['remoteusername'], \
                                                                   self.config['remotepassword'], self.config['remotedbname'],self.config['fileuser'],self.config['filekey'], self.data, self.config['logblob'])
                            fm.ingest()
                            fm.deleteFile()
                        except Exception as e:
                            self.log.logMessage('Problems ingesting FM file ' + file.name + ' ' +str(e))
                    else:
                        self.processGDLData(file.name, fileContentString)
                if file.name.lower().endswith('.xlsx') and not file.name.startswith('.'):
                    #do something
                    ihc = ihc_report.IHC_Report(file.name,self.config['remotehostname'], self.config['remoteusername'], \
                                                self.config['remotepassword'], self.config['remotedbname'],self.config['fileuser'],self.config['filekey'], self.data, self.config['logblob'])
                    data=ihc.ingest()
                    if data is not None:
                        ihc.deleteFile()
                if file.name.lower().endswith('.json') and not file.name.startswith('.'):
                    fileContent = self.file_service.get_file_to_text(self.data, None, file.name)
                    fileContentString = fileContent.content
                    if 'clinical' in fileContentString:
                        cd = clinical_json.ClinicalDataJson(file.name,self.config['remotehostname'], self.config['remoteusername'], \
                                                           self.config['remotepassword'], self.config['remotedbname'],self.config['fileuser'],self.config['filekey'],self.data, self.config['logblob'])
                        if cd.ingest()==0:
                            cd.deleteFile()
                            #self.processChristieFile(file.name)
                            processedFiles = processedFiles+1
                    elif 'genomic' in fileContentString:
                        genomic = genomicdata_json.GenomicDataJson(file.name,self.config['remotehostname'], self.config['remoteusername'], \
                                                           self.config['remotepassword'], self.config['remotedbname'],self.config['fileuser'],self.config['filekey'],self.data, self.config['logblob'])
                        if genomic.ingest()==0:
                            genomic.deleteFile()
                            processedFiles = processedFiles+1
                    else:
                        self.log.logMessage('json file type not recognised ' + str(file))
                        self.log.systemStatusUpdate(file.name, 'unknown', self.timestamp(), 'Error: JSON file type not recognised')
                            
            except Exception as e:
                self.log.logMessage('exception occurred ' + str(file) + " " + str(e))
                
        if processedFiles == 0:
            self.log.logMessage('No files to process.')
            self.log.logMessage('File list:'+",".join(map(str, files)))


    def processBloodFile(self, filename):
        self.log.logMessage('Fetching file: '+filename)
        #with open(self.filepath+filename) as csvfile:
            #readCSV = csv.reader(csvfile, delimiter=',')

        csvfile = self.file_service.get_file_to_text(self.data, None, filename)
        csvString = csvfile.content
        csvString = csvString.splitlines()
        readCSV = csv.reader(csvString, delimiter=',', dialect=csv.excel_tab)

        # Get the row count so we know where to trim for the genes
        rowCount = sum(1 for row in csv.reader(csvString, delimiter=',', dialect=csv.excel_tab))
        #csvfile.seek(0)
        self.log.logMessage('Row count is: '+str(rowCount))

        # Patient data from the file
        patientDetails = {
            'patientID': '',
            'baseline': '',
            'runNumber': '',
            'sampleType': '',
            'reportIssued': '',
            'ngsRun': '',
            'ngsSampleType': '',
            'pipelineVersion': '',
            'ngsLibraryCFDNAInput': '',
            'averageReadDepth': '',
            'colourGreenCFDNA': '',
            'colourYellowCFDNA': '',
            'colourRedCFDNA': '',
            'colourGreenReadDepth': '',
            'colourYellowReadDepth': '',
            'colourRedReadDepth': '',
            'detectionLevel': '',
            'ngsComment': '',
            'exploratoryComment': '',
            'geneData': {}
        }

        # Loop over the rows and update the patientDetails
        baselineNumberKey = {
            'Baseline1': 1,
            'B': 1,
            'Baseline2': 2,
            'B2': 2,
            'DT1': 3,
            'DT2': 4,
            'DT3': 5,
            'DT4': 6,
            'DT5': 7,
            'DT6': 8,
            'DT7': 9,
            'DT8': 10,
            'DT9': 11,
            'DT10': 12,
            'DT11': 13,
            'DT12': 14,
            'DT13': 15,
            'DP1': 16,
            'DP2': 17,
            'DP3': 18,
            'EoT': 19,
            'EoT2':20,
            'EoT3':21
        }

        geneRowsStart = rowCount-19
        currentRow = 1
        currentGeneRow = 1
        for row in readCSV:
            # Get the patient ID
            if 'Patient_ID' in row[0]:
                patientDetails['patientID'] = row[1]

            # Get the baseline number
            if 'Visit' in row[0]:
                try:
                    if row[1].startswith('NT'):
                        row[1]=row[1][3:]

                    if (not 'baseline' in patientDetails or len(str(patientDetails['baseline'])) ==0): 
                        patientDetails['baseline'] = baselineNumberKey[row[1]]

                    print('Baseline number is: '+str(patientDetails['baseline']))
                except Exception as e:
                    self.log.systemStatusUpdate(filename, 'CEP', self.timestamp(), 'Error: Baseline missing or incorrect')
                    print(str(e))
                    return 0

            # Get the run number
            if 'Run' == row[0]:
                patientDetails['runNumber'] = row[1]

            # Get the sample type
            if 'Sample Type' in row[0]:
                patientDetails['sampleType'] = row[1]

            # Get the date report issued
            if 'Date Report Issued' in row[0]:
                patientDetails['reportIssued'] = row[1]

            # Get the NGS run
            if 'NGS Run' in row[0]:
                patientDetails['ngsRun'] = row[1]

            # Get the NGS sample type
            if 'NGS Sample Type' in row[0]:
                patientDetails['ngsSampleType'] = row[1]

            # Get the Bioinformatics Pipeline
            if 'Bioinformatics Pipeline' in row[0]:
                patientDetails['pipelineVersion'] = row[1]

            # Get the NGS Library cfDNA input
            if 'NGS Library cfDNA Input' in row[0]:
                patientDetails['ngsLibraryCFDNAInput'] = row[1]

            # Get the Average Read Depth
            if 'Average Read Depth' in row[0]:
                patientDetails['averageReadDepth'] = row[1]

            # Get the Colour Green cfDNA
            if 'Colour Green cfDNA' in row[0]:
                patientDetails['colourGreenCFDNA'] = row[1]

            # Get the Colour Yellow cfDNA
            if 'Colour Yellow cfDNA' in row[0]:
                patientDetails['colourYellowCFDNA'] = row[1]

            # Get the Colour Red cfDNA
            if 'Colour Red cfDNA' in row[0]:
                patientDetails['colourRedCFDNA'] = row[1]

            # Get the Colour Green Read Depth
            if 'Colour Green Read Depth' in row[0]:
                patientDetails['colourGreenReadDepth'] = row[1]

            # Get the Colour Yellow Read Depth
            if 'Colour Yellow Read Depth' in row[0]:
                patientDetails['colourYellowReadDepth'] = row[1]

            # Get the Colour Red Read Depth
            if 'Colour Red Read Depth' in row[0]:
                patientDetails['colourRedReadDepth'] = row[1]

            # Get the Detection Level
            if 'Detection Level' in row[0]:
                patientDetails['detectionLevel'] = row[1]

            # Get the NGS comment
            if 'Comments for NGS Subset' in row[0]:
                patientDetails['ngsComment'] = row[1].replace("'","''")

            # Get the Exploratory comment
            if 'Comment for Exploratory Subset' in row[0]:
                patientDetails['exploratoryComment'] = row[1].replace("'","''")

            # Get the gene rows as lists
            if currentRow > 20:
                # Add the gene data
                patientDetails['geneData'][currentGeneRow] = row

                # Increment the tracking value
                currentGeneRow = currentGeneRow+1

            # Increment the tracking value
            currentRow = currentRow+1

        #for i in patientDetails:
            #print(str(patientDetails[i]))
            #self.log.logMessage(i +' '+ patientDetails[i])

        #print('--------->'+str(patientDetails['runNumber']))
        #print(len(patientDetails['runNumber']))

        if len(patientDetails['runNumber']) == 0:
            self.log.systemStatusUpdate(filename, 'CEP', self.timestamp(), 'Error: Run number missing')
            return 0
        else:
            #check if re-submit and delete old data if patient was not discussed since first submission
            resubmission,prev_gene_panel_id=self.checkResubmission(patientDetails)
            print('Resubmission: ' + str(resubmission))
            #if resubmission == 2:
            #    self.log.systemStatusUpdate(filename, 'CEP', self.timestamp(), 'Error: Resubmission after patient being discussed')
            #    self.log.logMessage(filename + ' Error: Resubmission after patient being discussed')
            #    return 0
            if resubmission == 1:
                self.deleteOldBloodReport(patientDetails)
                self.log.systemStatusUpdate(filename, 'CEP', self.timestamp(), "Resubmit of file -- delete old content")
                self.log.logMessage(filename + ' Resubmit of file -- delete old content')
            self.updateDatabaseBloodReport(patientDetails, filename, (resubmission==2), prev_gene_panel_id)

    # checkResubmission returns 0 if no resubmission; 1 if resubmission can overwrite; 2 if resubmission requires new version
    def checkResubmission(self, patientDetails):
        checkBaselineRunSQL = "select measurement_gene_panel_id,ingestion_date from MEASUREMENT_GENE_PANEL as mgp LEFT JOIN SPECIMEN s on mgp.specimen_id=s.specimen_id LEFT JOIN PERSON p on s.person_id=p.person_id LEFT JOIN CONCEPT_DATA_SOURCES gp on gp.data_source_concept_id = mgp.data_source_concept_id where p.target_id='"+patientDetails['patientID']+"' and mgp.baseline_number="+str(patientDetails['baseline'])+" and mgp.run_number="+patientDetails['runNumber']+" and mgp.ngs_run!='GDL' and gp.panel_name!='foundationmedicine' order by measurement_gene_panel_id desc;"
        cursorRun = self.conn.cursor()
        cursorRun.execute(checkBaselineRunSQL)
        row = cursorRun.fetchone()
        cursorRun.close()
        if row is None:
            return 0,0
        
        measurement_gene_panel_id=row[0];
        ingestion_date=None
        if row[1] is not None:
            ingestion_date= row[1]#datetime.datetime.strptime(row[1],'%Y-%m-%d H:%M:%S.%fZ')
        old_date = "select date_issued from REPORT where measurement_gene_panel_id = " +str(measurement_gene_panel_id)+";"
        cursorReport = self.conn.cursor()
        cursorReport.execute(old_date)
        row = cursorReport.fetchone()
        if row is not None:
            bloodReportDate = row[0]#datetime.datetime.strptime(row[0],'%Y-%m-%d')
        
        lastReportSQL = "SELECT created_on FROM MEETING_OUTCOME as mo LEFT JOIN PERSON as p on p.person_id=mo.person_id where p.target_id='"+patientDetails['patientID']+"' ORDER BY created_on DESC;"
        cursor = self.conn.cursor()
        cursor.execute(lastReportSQL)
        row = cursor.fetchone();
        cursor.close()
        if row is not None:
            lastDiscussedDate = row[0]
            if ingestion_date is not None:
                if lastDiscussedDate>=ingestion_date:
                    return 2,measurement_gene_panel_id
                else: 
                    return 1,measurement_gene_panel_id
            
            if bloodReportDate is None:
                bloodReportDate = datetime.datetime.strptime(patientDetails['reportIssued'], '%d/%m/%Y')
            if lastDiscussedDate>=bloodReportDate:
                return 2,measurement_gene_panel_id
        return 1,measurement_gene_panel_id

        

    # checkResubmission returns 0 if no resubmission; 1 if resubmission can overwrite; 2 if resubmission requires new version
    def checkResubmissionTumour(self, specimen_id, baseline, run):
        checkBaselineRunSQL = "select measurement_gene_panel_id, s.person_id, ingestion_date from MEASUREMENT_GENE_PANEL as mgp LEFT JOIN SPECIMEN s on mgp.specimen_id=s.specimen_id LEFT JOIN PERSON p on s.person_id=p.person_id where mgp.specimen_id="+str(specimen_id)+" and mgp.baseline_number="+str(baseline)+" and mgp.run_number="+str(run)+" and ngs_run='GDL' order by measurement_gene_panel_id desc;"
        cursorRun = self.conn.cursor()
        cursorRun.execute(checkBaselineRunSQL)
        row = cursorRun.fetchone()
        cursorRun.close()
        if row is None:
            return 0,0
        
        measurement_gene_panel_id=row[0];
        ingestion_date=None
        person_id=None
        if row[2] is not None:
            ingestion_date=row[2]
        if row[1] is not None:
            person_id=row[1]   
        lastReportSQL = "SELECT created_on FROM MEETING_OUTCOME as mo where mo.person_id="+str(person_id)+" ORDER BY created_on DESC;"
        cursor = self.conn.cursor()
        cursor.execute(lastReportSQL)
        row = cursor.fetchone();
        cursor.close()
        if row is not None:
            lastDiscussedDate = row[0]
            if ingestion_date is not None:
                if lastDiscussedDate>=ingestion_date:
                    return 2,measurement_gene_panel_id
                else: 
                    return 1,measurement_gene_panel_id
            
        return 1,measurement_gene_panel_id

       
    
    def deleteOldBloodReport(self, patientDetails):
        checkBaselineRunSQL = "select measurement_gene_panel_id from MEASUREMENT_GENE_PANEL as mgp LEFT JOIN SPECIMEN s on mgp.specimen_id=s.specimen_id LEFT JOIN PERSON p on s.person_id=p.person_id LEFT JOIN CONCEPT_DATA_SOURCES gp on gp.data_source_concept_id = mgp.data_source_concept_id where p.target_id='"+patientDetails['patientID']+"' and mgp.baseline_number="+str(patientDetails['baseline'])+" and mgp.run_number="+patientDetails['runNumber']+" and mgp.ngs_run!='GDL' and gp.panel_name!='foundationmedicine' ORDER BY measurement_gene_panel_id DESC;"     
        cursorRun = self.conn.cursor()
        cursorRun.execute(checkBaselineRunSQL)
        row= cursorRun.fetchone()
        cursorRun.close()
        if row is not None:
            measurement_gene_panel_id=row[0];
            selectGeneVariantSQL = "select measurement_gene_variant_id from MEASUREMENT_GENE_VARIANT where measurement_gene_panel_id="+str(measurement_gene_panel_id)+";"
            cursorSelectGeneVariant = self.conn.cursor()
            cursorSelectGeneVariant.execute(selectGeneVariantSQL);
            gene_variant_ids=cursorSelectGeneVariant.fetchall();
            for row2 in gene_variant_ids:
                gene_variant_id=row2[0]
                deletedSelectedGeneVariant = "DELETE FROM SELECTED_GENE_VARIANT WHERE measurement_gene_variant_id="+str(gene_variant_id)+";"
                cursorDeletedSelectedGeneVariant = self.conn.cursor()
                cursorDeletedSelectedGeneVariant.execute(deletedSelectedGeneVariant)
            # remove data from measurement_gene_variant for this measurement_gene_panel_id
            deleteGeneVariantSQL = "DELETE FROM MEASUREMENT_GENE_VARIANT where measurement_gene_panel_id="+str(measurement_gene_panel_id)+";"
            cursorDeleteVariant = self.conn.cursor()
            cursorDeleteVariant.execute(deleteGeneVariantSQL)
            deleteReportSQL = "DELETE FROM REPORT where measurement_gene_panel_id="+str(measurement_gene_panel_id)+";"
            cursorDeleteReport = self.conn.cursor()
            cursorDeleteReport.execute(deleteReportSQL)
            deleteGenePanelSQL = "DELETE FROM MEASUREMENT_GENE_PANEL where measurement_gene_panel_id="+str(measurement_gene_panel_id)+";"
            cursorDeleteGenePanel = self.conn.cursor()
            cursorDeleteGenePanel.execute(deleteGenePanelSQL)

    def deleteOldTumourReport(self, measurement_gene_panel_id):
        selectGeneVariantSQL = "select measurement_gene_variant_id from MEASUREMENT_GENE_VARIANT where measurement_gene_panel_id="+str(measurement_gene_panel_id)+";"
        cursorSelectGeneVariant = self.conn.cursor()
        cursorSelectGeneVariant.execute(selectGeneVariantSQL);
        gene_variant_ids=cursorSelectGeneVariant.fetchall();
        for row2 in gene_variant_ids:
            gene_variant_id=row2[0]
            deletedSelectedGeneVariant = "DELETE FROM SELECTED_GENE_VARIANT WHERE measurement_gene_variant_id="+str(gene_variant_id)+";"
            cursorDeletedSelectedGeneVariant = self.conn.cursor()
            cursorDeletedSelectedGeneVariant.execute(deletedSelectedGeneVariant)
        # remove data from measurement_gene_variant for this measurement_gene_panel_id
        deleteGeneVariantSQL = "DELETE FROM MEASUREMENT_GENE_VARIANT where measurement_gene_panel_id="+str(measurement_gene_panel_id)+";"
        cursorDeleteVariant = self.conn.cursor()
        cursorDeleteVariant.execute(deleteGeneVariantSQL)
        deleteReportSQL = "DELETE FROM REPORT where measurement_gene_panel_id="+str(measurement_gene_panel_id)+";"
        cursorDeleteReport = self.conn.cursor()
        cursorDeleteReport.execute(deleteReportSQL)
        deletePathLabRefSQL = "DELETE FROM PATH_LAB_REF where measurement_gene_panel_id="+str(measurement_gene_panel_id)+";"
        cursorDeletePathLabRef = self.conn.cursor()
        cursorDeletePathLabRef.execute(deletePathLabRefSQL)
        deleteGenePanelSQL = "DELETE FROM MEASUREMENT_GENE_PANEL where measurement_gene_panel_id="+str(measurement_gene_panel_id)+";"
        cursorDeleteGenePanel = self.conn.cursor()
        cursorDeleteGenePanel.execute(deleteGenePanelSQL)


    def updateDatabaseBloodReport(self, patientDetails, filename, updateSelection, prev_gene_panel_id):
        # Check if patient exists
        checkSQL = "SELECT * FROM dbo.PERSON WHERE target_id='"+patientDetails['patientID']+"'"
        cursor = self.conn.cursor()
        cursor.execute(checkSQL)

        foundPerson = 'No'
        personID = 0
        for row in cursor:
            foundPerson = 'Yes'
            personID = row[0]

        self.log.logMessage('Is patient '+patientDetails['patientID']+' in the database? '+foundPerson)

        if foundPerson == 'Yes':
            findGeneConceptID = "SELECT data_source_concept_id from CONCEPT_DATA_SOURCES WHERE panel_name='"+patientDetails['ngsSampleType']+"'"
            cursor = self.conn.cursor()
            cursor.execute(findGeneConceptID)
            row=cursor.fetchone()
            geneConceptID=0
            if row is not None:
                geneConceptID=row[0]
            else:
                insertGeneConcept = "INSERT INTO CONCEPT_DATA_SOURCES (panel_name) VALUES('"+patientDetails['ngsSampleType']+"')"
                cursor = self.conn.cursor()
                cursor.execute(insertGeneConcept)
                geneConceptID=cursor.lastrowid

            # Get the lastest blood specimen to attach the report to
            #specimenSQL = "SELECT TOP 1 * FROM SPECIMEN WHERE person_id = "+str(personID)+" AND specimen_concept_id=1 ORDER BY specimen_date DESC"
            specimenSQL = "SELECT * FROM SPECIMEN WHERE person_id = "+str(personID)+" AND specimen_concept_id=1 AND baseline_number = "+str(patientDetails['baseline'])+" ORDER BY specimen_date DESC"
            cursor = self.conn.cursor()
            cursor.execute(specimenSQL)
            row = cursor.fetchone()

            #print('row count for '+patientDetails['patientID']+' is: '+str(row.rowcount))

            # Check a result has been returned (if not there is no blood sample in the database for this person)
            if row is not None:
                specimenID = 0
                specimenID = row[0]

                self.log.logMessage('Adding report for person ID '+str(personID)+' ('+patientDetails['patientID']+') against specimen_id '+str(specimenID))

                # Update the existing patient data from the file data
                cfdnaColourKey = patientDetails['colourGreenCFDNA']+';'+patientDetails['colourYellowCFDNA']+';'+patientDetails['colourRedCFDNA']
                averageColourKey = patientDetails['colourGreenReadDepth']+';'+patientDetails['colourYellowReadDepth']+';'+patientDetails['colourRedReadDepth']

                # Record whether analysis failed
                analysisFailed = 'false'
                if patientDetails['geneData'][1][0] == 'Failed':
                    # Analysis failed
                    analysisFailed = 'true'

                # Record if there were no mutations found
                noMutations = 'false'
                if patientDetails['geneData'][1][0] == 'None':
                    # No mutations found
                    noMutations = 'true'

                # Update MEASUREMENT_GENE_PANEL table
                update1Cursor = self.conn.cursor()
                update1SQL = "INSERT INTO MEASUREMENT_GENE_PANEL (specimen_id, data_source_concept_id, average_read_depth, bioinformatics_pipeline, ngs_library_cfdna_input, ngs_run, level_of_detection, cfdna_input_colour_key, average_read_depth_colour_key, analysis_failed, no_mutations_found, ngs_comment, exploratory_comment, run_number, baseline_number) VALUES('"+str(specimenID)+"', '"+str(geneConceptID)+"', '"+patientDetails['averageReadDepth']+"', '"+patientDetails['pipelineVersion']+"', '"+patientDetails['ngsLibraryCFDNAInput']+"', '"+patientDetails['ngsRun']+"', '"+patientDetails['detectionLevel']+"', '"+cfdnaColourKey+"', '"+averageColourKey+"', '"+analysisFailed+"', '"+noMutations+"', '"+patientDetails['ngsComment']+"', '"+patientDetails['exploratoryComment']+"', '"+patientDetails['runNumber']+"', "+str(patientDetails['baseline'])+")"

                update1SQLID = 0
                try:
                    update1Cursor.execute(update1SQL)
                    #self.log.logMessage('Updated MEASUREMENT_GENE_PANEL with '+str(update1Cursor.rowcount)+' row(s)')

                    update1SQLID = update1Cursor.lastrowid
                    self.log.logMessage('Created MEASUREMENT_GENE_PANEL record ID: '+str(update1SQLID))
                except Exception as e:
                    self.log.logMessage('Database MEASUREMENT_GENE_PANEL update failed, exiting...')
                    self.log.logMessage('Error message: ')
                    self.log.logMessage(str(e))

                # Update MEASUREMENT_GENE_VARIANT table
                if patientDetails['geneData'][1][0] != 'Failed' and patientDetails['geneData'][1][0] != 'None':
                    for i in patientDetails['geneData']:
                        if "sep-" in patientDetails['geneData'][i][0].lower():
                            patientDetails['geneData'][i][0]=patientDetails['geneData'][i][0].lower().replace('sep-0','SEPT');
                            patientDetails['geneData'][i][0]=patientDetails['geneData'][i][0].replace('sep-','SEPT');
                        # Mutations found, add the gene data for the patient
                        selectGeneCursor = self.conn.cursor()
                        selectGeneCursor.execute("SELECT * FROM CONCEPT_GENE WHERE gene_name = '"+patientDetails['geneData'][i][0]+"'")
                        gene_concept_id = ''

                        # If the gene exists in the table, get the ID, else add it to the table and use the ID
                        selectGeneCursorRowcount = 0
                        gene_concept_id = 0
                        for row in selectGeneCursor:
                            selectGeneCursorRowcount = selectGeneCursorRowcount+1
                            gene_concept_id = row[0]

                        if selectGeneCursorRowcount == 0:
                            selectGeneCursor = self.conn.cursor()
                            selectGeneCursor.execute("INSERT INTO CONCEPT_GENE (gene_name) VALUES('"+patientDetails['geneData'][i][0]+"')")
                            gene_concept_id = selectGeneCursor.lastrowid

                        update2Cursor = self.conn.cursor()
                        update2SQL = "INSERT INTO MEASUREMENT_GENE_VARIANT (measurement_gene_panel_id, gene_concept_id, cosmic_url, variant_allele_frequency, read_depth, germline_frequency, functional_effect, cdna_change, amino_acid_change, chromosome, high_confidence, ngs_gene_present, is_specific_mutation_in_panel, position) VALUES("+str(update1SQLID)+", '"+str(gene_concept_id)+"', '"+patientDetails['geneData'][i][11].replace("'",'"')+"', '"+patientDetails['geneData'][i][4]+"', '"+patientDetails['geneData'][i][5]+"', '"+patientDetails['geneData'][i][6]+"', '"+patientDetails['geneData'][i][7]+"', '"+patientDetails['geneData'][i][2]+"', '"+patientDetails['geneData'][i][3]+"', '"+patientDetails['geneData'][i][1]+"', '"+patientDetails['geneData'][i][8]+"', '"+patientDetails['geneData'][i][9]+"', '"+patientDetails['geneData'][i][10]+"', '"+patientDetails['geneData'][i][12]+"')"
                        #print(patientDetails['geneData'][i][12])

                        try:
                            update2Cursor.execute(update2SQL)
                            #self.log.logMessage('Updated MEASUREMENT_GENE_VARIANT with '+str(update2Cursor.rowcount)+' row(s)')

                            measurement_gene_variant_id = update2Cursor.lastrowid
                            self.log.logMessage('Created MEASUREMENT_GENE_VARIANT record ID: '+str(measurement_gene_variant_id))
                        except Exception as e:
                            self.log.logMessage('Database MEASUREMENT_GENE_VARIANT update failed, exiting...')
                            self.log.logMessage('Error message: ')
                            self.log.logMessage(str(e))
                            
                        # new version of old blood report keep selections
                        if updateSelection == True:
                            findPreviouseSelectionQuery="select person_id from SELECTED_GENE_VARIANT FULL OUTER JOIN MEASUREMENT_GENE_VARIANT on MEASUREMENT_GENE_VARIANT.measurement_gene_variant_id=SELECTED_GENE_VARIANT.measurement_gene_variant_id FULL OUTER JOIN MEASUREMENT_GENE_PANEL on MEASUREMENT_GENE_PANEL.measurement_gene_panel_id=MEASUREMENT_GENE_VARIANT.measurement_gene_panel_id where person_id="+str(personID)+" and type='CTDNA' and baseline_number="+str(patientDetails['baseline'])+" and run_number="+str(patientDetails['runNumber'])+" and MEASUREMENT_GENE_PANEL.measurement_gene_panel_id=" +str(prev_gene_panel_id)+ " and position='"+patientDetails['geneData'][i][12] +"';"
                            findPreviousSelectionCursor = self.conn.cursor()
                            findPreviousSelectionCursor.execute(findPreviouseSelectionQuery)
                            row=findPreviousSelectionCursor.fetchone()
                            findPreviousSelectionCursor.close()
                            if row is not None:
                                #was previously selected
                                insertSelectionQuery="insert into SELECTED_GENE_VARIANT (person_id, measurement_gene_variant_id, type) values("+str(personID)+","+str(measurement_gene_variant_id)+",'CTDNA');"
                                insertSelectionCursor = self.conn.cursor()
                                insertSelectionCursor.execute(insertSelectionQuery)
                                insertSelectionCursor.close()


                # Insert report data
                reportIssued = self.formatDate(patientDetails['reportIssued'])
                update3Cursor = self.conn.cursor()
                update3SQL = "INSERT INTO REPORT (date_issued, measurement_gene_panel_id) VALUES('"+reportIssued+"','"+str(update1SQLID)+"')"

                try:
                    update3Cursor.execute(update3SQL)
                    #self.log.logMessage('Updated REPORT with '+str(update3Cursor.rowcount)+' row(s)')

                    update3SQLID = update3Cursor.lastrowid
                    self.log.logMessage('Created REPORT record ID: '+str(update3SQLID))
                except Exception as e:
                    self.log.logMessage('Database REPORT update failed.')
                    self.log.logMessage('Error message: ')
                    self.log.logMessage(str(e))

                # Delete the file once processed
                if update1Cursor.rowcount > 0 and update3Cursor.rowcount > 0:
                    # All should be OK, we can commit changes.
                    self.conn.commit()
                    # Both updates were successful...
                    # Delete the file
                    self.log.logMessage('Deleting '+filename)
                    try:
                        #os.remove(self.filepath+filename)
                        self.file_service.delete_file(self.data, None, filename)

                        self.log.systemStatusUpdate(filename, 'CEP', self.timestamp(), 'Success')
                        self.conn.commit()
                    except:
                        self.log.logMessage('There was a problem deleting '+filename)
                else:
                    self.log.logMessage('Error: The cfDNA blood report did not update properly in the database for '+patientDetails['patientID'])
                    self.log.systemStatusUpdate(filename, 'CEP', self.timestamp(), 'Error: Data import failed')
                    self.conn.rollback()
            else:
                self.log.logMessage(' ######## ERROR: There is no blood sample for patient '+patientDetails['patientID']+' ########')
                self.log.logMessage(' ######## Skipping import for this patient '+patientDetails['patientID']+' ########')
                self.log.systemStatusUpdate(filename, 'CEP', self.timestamp(), 'Error: No specimen data')
                self.conn.rollback()

        else:
            self.log.logMessage(" ######## ERROR: Patient "+patientDetails['patientID']+" doesn't exist. ########")
            self.log.systemStatusUpdate(filename, 'CEP', self.timestamp(), 'Error: Patient is not present in the database')
            self.conn.rollback()


    def processIHCPictures(self, filename):
        self.log.logMessage('Sending IHC image '+filename+' to the web app reports folder.')
        pict = self.file_service.get_file_to_bytes(self.data, None, filename)
        upload_name=filename[0:-4]+filename[-4:].lower()
        self.pdf_blob_service.create_container(self.container_name) 
        version=0
        prev_versions=self.pdf_blob_service.list_blobs(self.container_name, filename[0:-4])
        props=None
        #find the next version number
        for v in prev_versions:
            v_version=v.name[len(filename[0:-4]):-4]
            if v_version is not None and len(v_version)>0: 
                d=re.search("\d", v_version)
                if d is not None and version<=int(d.group(0)):
                    version=int(d.group(0))
                    props=v.properties
            else:
                props=v.properties
        print(props)
        if props is not None:
            lastModified=props.last_modified.replace(tzinfo=datetime.timezone.utc)
            lastMeetingSQL="SELECT created_on FROM MEETING_OUTCOME as mo LEFT JOIN SPECIMEN on mo.person_id=SPECIMEN.person_id where SPECIMEN.preclin_id='"+filename[0:-8]+"' order by created_on desc"
            cursorRun = self.conn.cursor()
            cursorRun.execute(lastMeetingSQL)
            row = cursorRun.fetchone()
            cursorRun.close()
            if row is not None:
                lastMeetingDate=row[0].replace(tzinfo=datetime.timezone.utc)
                if lastMeetingDate>lastModified:
                    #amend filename with version number
                    upload_name=filename[:-4]+'_v'+str(version+1)+filename[-4:]
                    self.log.logMessage('New version ' + upload_name)
                #delete previous blob
                else:
                    if version!=0:
                        upload_name=filename[:-4]+'_v'+str(version)+filename[-4:]
                    self.pdf_blob_service.delete_blob(self.container_name, upload_name)
        print(upload_name)
        self.pdf_blob_service.create_blob_from_bytes(self.container_name,upload_name, pict.content)
        self.file_service.delete_file(self.data, None, filename)
        self.log.logMessage('Success ' + filename)
        self.log.systemStatusUpdate(filename, 'IHC', self.log.timestamp(), "Success")

    # Move the pdf file to the web app
    def processPDFReport(self, filename):
        self.log.logMessage('Sending '+filename+' to the web app reports folder.')
        f=filename
        filename=filename[0:-4]+filename[-4:].lower()
        #move to azure storage
        self.pdf_blob_service.create_container(self.container_name) 
        self.pdf_blob_service.create_blob_from_path(self.config['containername'],filename,self.filepath+'/tmp/'+f);

        # Delete the copies
        self.log.logMessage('Deleting PDF report file '+filename)

        # This throws an error, even though it works correctly.
        try:
            if self.file_service.exists(self.data, None, filename):
                self.file_service.delete_file(self.data, None, filename)
            if os.path.exists(self.filepath+'/tmp/'+f):
                os.remove(self.filepath+'/tmp/'+f)
            if re.search("FM.PDF|FMV[1-9].PDF", filename.upper()):
                if self.checkFMPDF(filename):
                    self.log.systemStatusUpdate(filename, 'FM', self.timestamp(), 'Success')
            elif re.search("QCI.PDF|QCIV[1-9].PDF", filename.upper()):
                if self.checkQCIPDF(filename):
                    self.log.systemStatusUpdate(filename, 'QCI', self.timestamp(), 'Success')
#             elif re.search("[A-Z]{3}\d{7}T1SM(.)*\.PDF", filename.upper()):
            elif re.search("^[A-Z]{3}\d{7}(.)*SM(.)*\.PDF", filename.upper()):
                print('match rna')
                if self.checkRNAPDF(filename):
                    self.log.systemStatusUpdate(filename, 'RNA', self.timestamp(), 'Success')
            elif self.checkGDLPDF(filename):
                self.log.systemStatusUpdate(filename, 'GDL', self.timestamp(), 'Success')
            elif self.checkOtherSourcePDF(filename):
                self.log.systemStatusUpdate(filename, 'PDF', self.timestamp(), 'Success')

        except Exception as inst:
            self.log.logMessage(str(type(inst)))
            self.log.logMessage(str(inst))
            self.log.logMessage('There was a problem deleting '+filename)
            if filename.upper().endswith('FM.PDF'):
                self.log.systemStatusUpdate(filename, 'FM', self.timestamp(), 'Error: Moving the PDF reports failed.')
            elif filename.upper().endswith('QCI.PDF'):
                self.log.systemStatusUpdate(filename, 'QCI', self.timestamp(), 'Error: Moving the PDF reports failed.')
            else:
                self.log.systemStatusUpdate(filename, 'GDL', self.timestamp(), 'Error: Moving the PDF reports failed.')

    def checkFMPDF(self, filename):
                
        try:
            print(filename)
            #find numeric patient id
            patientid = int(re.search(r'\d+', filename).group())
            try:
                patternBlood = re.compile("^[A-Z]{3}\d{7}T\d{1,2}FM")
                patternTumour = re.compile("^[A-Z]{3}\d{7}Bx\d{1,2}FM")
                if not re.match(patternBlood, filename) and not re.match(patternTumour,filename):
                    self.log.systemStatusUpdate(filename, 'FM', self.timestamp(), 'Warning: the file has been uploaded but filename does not conform to naming convention')
                    return False
                
                timepointInt = re.findall(r'\d+',filename[10:])
                print(timepointInt)
                if re.match(patternBlood, filename):
                    SQL = "SELECT specimen_id FROM SPECIMEN left join PERSON on PERSON.person_id=SPECIMEN.person_id WHERE specimen_concept_id=1 and baseline_number = "+str(timepointInt[0])+" and target_id='"+filename[:10]+"'"
                else:
                    SQL = "SELECT specimen_id FROM SPECIMEN left join PERSON on PERSON.person_id=SPECIMEN.person_id WHERE specimen_concept_id!=1 and baseline_number = "+str(timepointInt[0])+" and target_id='"+filename[:10]+"'"
                    
            except (ValueError, KeyError, AssertionError) as e:  #no blood structure try tumour
                print(filename[:filename.upper().index('FM')])
                assert len(filename[:filename.upper().index('FM')]) >0, "Sample missing in filename"
                SQL = "SELECT specimen_id FROM SPECIMEN WHERE preclin_id = '"+str(filename[:filename.upper().index('FM')])+"'"
            print(SQL)
            cursor = self.conn.cursor()
            cursor.execute(SQL)
            row = cursor.fetchone()
            print(row)
            if row is not None:
                return True
            self.log.systemStatusUpdate(filename, 'FM', self.timestamp(), 'Warning: the file has been uploaded but unable to match file to sample/timepoint')
            return False
        except Exception as e:
            self.log.logMessage(str(type(e)))
            self.log.logMessage(str(e))
            print(e)
            self.log.systemStatusUpdate(filename, 'FM', self.timestamp(), 'Warning: ' + str(e))
            return False
    
    def checkRNAPDF(self, filename):
        try:
            rna_re="^[A-Z]{3}\d{7}T1SM"
            if not re.match(rna_re, filename):
                self.log.systemStatusUpdate(filename, 'RNA', self.timestamp(), 'Warning: the file has been uploaded but unable to match to a timepoint as it can only be T1 at present')
                return False
            siteId=filename[3:6]
            getSite = self.conn.cursor()
            getSite.execute("SELECT * FROM CARE_SITE WHERE care_site_id = "+siteId)   
            row=getSite.fetchone()
            if row is None:
                self.log.systemStatusUpdate(filename, 'RNA', self.timestamp(), 'Warning: the file has been uploaded but unable to match file to a site')
                return False
            SQL = "SELECT specimen_id FROM SPECIMEN left join PERSON on PERSON.person_id=SPECIMEN.person_id WHERE specimen_concept_id=1 and baseline_number = 1 and target_id='"+filename[:10]+"'"
            cursor = self.conn.cursor()
            cursor.execute(SQL)
            row = cursor.fetchone()
            print(row)
            cursor.close()
            if row is None:
                self.log.systemStatusUpdate(filename, 'RNA', self.timestamp(), 'Warning: the file has been uploaded but unable to match file to patient')
                return False
            else:
                return True
        except Exception as e:
            self.log.logMessage(str(type(e)))
            self.log.logMessage(str(e))
            self.log.systemStatusUpdate(filename, 'RNA', self.timestamp(), 'Warning: the file has been uploaded but exception occurred '+ str(e))
            print(e)
            return False
        
    def checkOtherSourcePDF(self, filename):
        print('start checkOtherSourcePDF '+ filename)
        try:
            print(filename)
            patternBlood = re.compile("^[A-Z]{3}\d{7}T\d{1,2}[a-zA-Z]{2}")
            patternTumour = re.compile("^[A-Z]{3}\d{7}Bx\d{1,2}[a-zA-Z]{2}")
            assert re.match(patternBlood, filename) or re.match(patternTumour, filename), "Filename has wrong structure"
            siteId=filename[3:6]
            getSite = self.conn.cursor()
            getSite.execute("SELECT * FROM CARE_SITE WHERE care_site_id = "+siteId)   
            row=getSite.fetchone()
            if row is None:
                self.log.systemStatusUpdate(filename, 'PDF', self.timestamp(), 'Warning: the file has been uploaded but unable to match file to a site')
                return False
            timepointInt = re.findall(r'\d+',filename[10:])
            print(timepointInt)
            if re.match(patternBlood, filename):
                SQL = "SELECT specimen_id FROM SPECIMEN left join PERSON on PERSON.person_id=SPECIMEN.person_id WHERE specimen_concept_id=1 and baseline_number = "+str(timepointInt[0])+" and target_id='"+filename[:10]+"'"
            else:
                SQL = "SELECT specimen_id FROM SPECIMEN left join PERSON on PERSON.person_id=SPECIMEN.person_id WHERE specimen_concept_id!=1 and baseline_number = "+str(timepointInt[0])+" and target_id='"+filename[:10]+"'"
            print(SQL)
            cursor = self.conn.cursor()
            cursor.execute(SQL)
            row = cursor.fetchone()
            print(row)
            if row is None:
                self.log.systemStatusUpdate(filename, 'PDF', self.timestamp(), 'Warning: the file has been uploaded but unable to match file to sample')
                return False
            
            #check two letter code
            shortCode=re.findall(r'[a-zA-Z]{2}', filename[12:])
            assert len(shortCode)>0, "No two char source code found"
            SQL = "select short_code from CONCEPT_DATA_SOURCES where short_code='"+str(shortCode[0])+"'"
            print(SQL)
            cursor.execute(SQL)
            row = cursor.fetchone()
            print(row)
            if row is not None:
                return True
            self.log.systemStatusUpdate(filename, 'PDF', self.timestamp(), 'Warning: the file has been uploaded but unable to match file to source')
            return False
        except Exception as e:
            self.log.logMessage(str(type(e)))
            self.log.logMessage(str(e))
            self.log.systemStatusUpdate(filename, 'PDF', self.timestamp(), 'Warning: the file has been uploaded but filename does not conform to naming convention')
            print(e)
            return False
    
    def checkQCIPDF(self, filename):
        try:
            print('filename ', filename)
            siteId=filename[3:6]
            getSite = self.conn.cursor()
            getSite.execute("SELECT * FROM CARE_SITE WHERE care_site_id = "+siteId)   
            row=getSite.fetchone()
            if row is None:
                self.log.systemStatusUpdate(filename, 'QCI', self.timestamp(), 'Warning: the file has been uploaded but unable to match file to a site')
                return False
            SQL = "SELECT person_id FROM PERSON WHERE target_id='"+filename[:10]+"'"
            cursor = self.conn.cursor()
            cursor.execute(SQL)
            row = cursor.fetchone()
            print(row)
            cursor.close()
            if row is None:
                self.log.systemStatusUpdate(filename, 'QCI', self.timestamp(), 'Warning: the file has been uploaded but unable to match file to patient')
                return False
            
            pattern = re.compile("^[A-Z]{3}\d{7}T\d{1,2}QCI.PDF")
            assert re.match(pattern, filename.upper()), "Filename has wrong structure"
            timepoint = filename[11:filename.upper().index("QCI")] #throghs exception ValueError if not found
            print('Timepoint ', timepoint)
            assert timepoint is not None, "Timepoint missing in name"
            SQL = "SELECT specimen_id FROM SPECIMEN left join PERSON on PERSON.person_id=SPECIMEN.person_id WHERE specimen_concept_id=1 and baseline_number = "+timepoint+" and target_id='"+filename[:10]+"'"
            print(SQL)
            cursor = self.conn.cursor()
            cursor.execute(SQL)
            row = cursor.fetchone()
            print(row)
            if row is not None:
                return True
            self.log.systemStatusUpdate(filename, 'QCI', self.timestamp(), 'Warning: the file has been uploaded but sample was not found')
            return False
        except Exception as e:
            self.log.logMessage(str(type(e)))
            self.log.logMessage(str(e))
            print(e)
            self.log.systemStatusUpdate(filename, 'QCI', self.timestamp(), 'Warning: the file has been uploaded but filename does not conform to naming convention')
            return False
    
    def checkGDLPDF(self, filename):
        SQL = "SELECT report_id FROM REPORT WHERE filename = '"+str(filename)+"'"
        cursor = self.conn.cursor()
        cursor.execute(SQL)
        row = cursor.fetchone()
        if row is not None:
            return True
        return False

    def processGDLData(self, filename, xmlString):
        # Parse the xml string into a usable format
        try:
            # Get the patient data
            root = ET.fromstring(xmlString)
            assert root.find('.//identifier[@type="Your ref"]') is not None, 'identifier "Your ref" missing'
            christieNo = root.find('.//identifier[@type="Your ref"]').text
            assert root.find('.//identifier[@type="Our ref"]') is not None, 'identifier "Our ref" missing'
            reportRef = root.find('.//identifier[@type="Our ref"]').text
            
            assert root.find('.//submitted') is not None, 'report submitted date missing'
            reportDate = root.find('.//submitted').text
            assert root.find('.//activated') is not None, 'report activated date missing'
            gdlDate = root.find('.//activated').text
            
            reportDateFormatted = datetime.datetime.strptime(reportDate, '%d-%b-%Y').strftime('%Y-%m-%d')

            assert root.find('.//identifier') is not None, "Path lab can't be missing or empty"
            pathLabRef = root.find('.//identifier[@type="Path lab"]').text

            coverage = 0
            if root.find('.//coverage') is not None:
                coverage = root.find('.//coverage').text
                coverage = coverage.replace("%", "")

            comments = 'n/a'
            if root.find('.//comments') is not None:
                comments = root.find('.//comments').text

            comment = 'n/a'
            if root.find('.//comment') is not None:
                comment = root.find('.//comment').text

            # Update the database, measurement_gene_panel table
            cursor = self.conn.cursor()

            # Get the specimen ID for this patient
            hash_object = hashlib.sha256(christieNo.encode('utf-8'))
            hex_dig = hash_object.hexdigest()
            
            print("hex_dig " + str(hex_dig));
            #gdlSQL = "SELECT specimen.specimen_id FROM SPECIMEN AS specimen, PERSON AS person WHERE person.hospital_number_hash = '"+hex_dig+"'"
            gdlSQL = """SELECT TOP 1 specimen.specimen_id FROM SPECIMEN AS specimen left join PERSON AS person on specimen.person_id= person.person_id 
            WHERE specimen.person_id = (SELECT person_id FROM PERSON WHERE person.hospital_number_hash = %s) AND (specimen.specimen_concept_id = 2 OR specimen.specimen_concept_id = 3) 
            AND dbo.RemoveNonAlphaNumChars(tumour_id) like dbo.RemoveNonAlphaNumChars(%s) order by specimen.specimen_id asc"""
            cursor.execute(gdlSQL, (hex_dig, pathLabRef))
            #print(gdlSQL)

            specimenID = 0
            for row in cursor:
                specimenID = row[0]

            print('SPECIMEN ID IS: '+str(specimenID))

            if specimenID == 0:
                # No specimen found
                self.log.logMessage('No specimen ID found for '+filename)
                raise Exception('No specimen ID found.')

            else:
                #check resubmission tumour
                resubmission,prev_gene_panel_id=self.checkResubmissionTumour(specimenID, 1, 1)
                print("resubmission: " + str(resubmission) + "prev_gene_panel: " + str(prev_gene_panel_id))
                updateSelection=False
                if resubmission==1:
                    self.deleteOldTumourReport(prev_gene_panel_id);
                    self.log.systemStatusUpdate(filename, 'GDL', self.timestamp(), "Resubmit of file -- delete old content")
                    self.log.logMessage(filename + ' Resubmit of file -- delete old content')
                if resubmission==2:
                    updateSelection=True
                
                #find the latest GDL panel id
                cursor = self.conn.cursor()
                genePanelSQL= "select top 1 data_source_concept_id from CONCEPT_DATA_SOURCES where valid_start_date<%s order by valid_start_date desc"
                cursor.execute(genePanelSQL, (reportDateFormatted))
                row=cursor.fetchone()
                if row is not None:
                    data_source_concept_id=row[0]
                else:
                    data_source_concept_id=2
                
                # Update MEASUREMENT_GENE_PANEL
                cursor = self.conn.cursor()
                gdlSQL = """INSERT INTO MEASUREMENT_GENE_PANEL (specimen_id, data_source_concept_id, coverage, average_read_depth, ngs_run, analysis_failed, no_mutations_found, run_number, baseline_number, comments, unknown_significance) 
                VALUES(%s, %s, %s, 'n/a', 'GDL', 'false', 'false', 1, 1, %s, %s)"""
                #print(gdlSQL)
                cursor.execute(gdlSQL, (specimenID, data_source_concept_id, coverage, comments, comment))
                measurement_gene_panel_id = cursor.lastrowid

                #print(gdlSQL)

                # Update REPORT
                cursor = self.conn.cursor()
                reportFilename = reportRef+'.pdf'
                reportSQL = "INSERT INTO REPORT (date_issued, measurement_gene_panel_id, filename) VALUES(%s, %d, %s)"
                cursor.execute(reportSQL, (reportDateFormatted, measurement_gene_panel_id, reportFilename))

                #print(reportSQL)

                # Update PATH_LAB_REF
                cursor = self.conn.cursor()
                pathSQL = "INSERT INTO PATH_LAB_REF (path_lab_ref, specimen_id, measurement_gene_panel_id) VALUES(%s, %d, %d)"
                #print(pathSQL)
                cursor.execute(pathSQL, (pathLabRef, specimenID, measurement_gene_panel_id))

                # Update GDL_REQUEST
                cursor = self.conn.cursor()
                gdlDateFormatted = datetime.datetime.strptime(gdlDate, '%d-%b-%Y').strftime('%Y-%m-%d')

                #print(gdlDate)
                #print(gdlDateFormatted)

                #reportSQL = "INSERT INTO GDL_REQUEST (date_requested, path_lab_ref, specimen_id, sample_type) VALUES('"+gdlDateFormatted+"', '"+pathLabRef+"', '"+str(specimenID)+"', '')"
                #reportSQL = "UPDATE GDL_REQUEST SET date_requested='"+gdlDateFormatted+"', path_lab_ref='"+pathLabRef+"' WHERE specimen_id='"+str(specimenID)+"'"
                #print(reportSQL)
                #cursor.execute(reportSQL)

                # Get the gene results data and update MEASUREMENT_GENE_VARIANT
                geneCount = 0
                try:
                    insertSGVarguments=[]
                    for gene in root.iter(tag = 'gene'):
                        geneName = gene.attrib['name']

                        for variant in gene[0]:
                            cdna = variant[0].text
                            if variant.find('protein') is not None:
                                amino = variant.find('protein').text
                            else:
                                amino = ""
                            if variant.find('reads') is not None:
                                reads = variant.find('reads').text
                                reads = reads.replace("%", "")
                            else:
                                reads = ""

                            cursor = self.conn.cursor()
                            gdlSQL = "INSERT INTO MEASUREMENT_GENE_VARIANT (measurement_gene_panel_id, gene_concept_id, cdna_change, amino_acid_change, variant_allele_frequency) VALUES(%d, (SELECT TOP 1 gene_concept_id FROM CONCEPT_GENE WHERE gene_name = %s), %s, %s, %s)"
                            cursor.execute(gdlSQL,(measurement_gene_panel_id, geneName, cdna, amino, reads))
                            measurement_gene_variant_id = cursor.lastrowid
                                                
                            # new version of old tumour report keep selections
                            if updateSelection == True:
                                findPreviouseSelectionQuery="select person_id from SELECTED_GENE_VARIANT FULL OUTER JOIN MEASUREMENT_GENE_VARIANT on MEASUREMENT_GENE_VARIANT.measurement_gene_variant_id=SELECTED_GENE_VARIANT.measurement_gene_variant_id FULL OUTER JOIN MEASUREMENT_GENE_PANEL on MEASUREMENT_GENE_PANEL.measurement_gene_panel_id=MEASUREMENT_GENE_VARIANT.measurement_gene_panel_id where person_id=(SELECT person_id FROM PERSON WHERE person.hospital_number_hash = '"+hex_dig+"') and type='NGS' and baseline_number=1 and run_number=1 and MEASUREMENT_GENE_PANEL.measurement_gene_panel_id=" +str(prev_gene_panel_id)+ " and gene_concept_id=(SELECT TOP 1 gene_concept_id FROM CONCEPT_GENE WHERE gene_name = '"+str(geneName)+"') and amino_acid_change = '"+str(amino)+"' and cdna_change ='"+str(cdna)+"';"
                                findPreviousSelectionCursor = self.conn.cursor()
                                findPreviousSelectionCursor.execute(findPreviouseSelectionQuery)
                                row=findPreviousSelectionCursor.fetchone()
                                findPreviousSelectionCursor.close()
                                if row is not None:
                                    #was previously selected
                                    insertSGVarguments.append((hex_dig, measurement_gene_variant_id))
                    insertSelectionQuery="insert into SELECTED_GENE_VARIANT (person_id, measurement_gene_variant_id, type) values((SELECT person_id FROM PERSON WHERE person.hospital_number_hash = %s),%d,'NGS');"
                    if len(insertSGVarguments)>0:
                        insertSelectionCursor = self.conn.cursor()
                        insertSelectionCursor.executemany(insertSelectionQuery, insertSGVarguments)
                        insertSelectionCursor.close()
                except Exception as e:
                    print(str(e))
                    print('Error importing genes.')
                    self.log.logMessage('GDL -- Error importing genes.' +str(e))
                    raise e

                # Delete the copies
                self.log.logMessage('Deleting GDL xml data file '+filename)
                try:
                    self.conn.commit()
                    if self.file_service.exists(self.data, None, filename):
                        self.file_service.delete_file(self.data, None, filename)
                    self.log.systemStatusUpdate(filename, 'GDL', self.timestamp(), 'Success')
                except:
                    self.log.logMessage('There was a problem deleting '+filename)
        except Exception as e:
            self.log.systemStatusUpdate(filename, 'GDL', self.timestamp(), 'Error: XML file could not be imported.')
            self.conn.rollback()
            print(str(e))
            return 0

    def getConfig(self):
        # Get the config file details (database login)
        d = {}
        with open(self.filepath+"/.config") as f:
            for line in f:
                (key, val) = line.split(';',1)
                d[key] = val.replace('\n', '').strip()

            return d

    def timestamp(self):
        #return datetime.datetime.now().isoformat()
        return datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')


    def patientLookUp(self, christieNo, targetID):
        # Check if record already exists
        with open(self.filepath+"/.users", "a+") as f:
            patientExists = False;
            for line in f:
                # Split the line
                userData = line.split(':')
                print(self.decode(userData[1]))

                # Check the record
                if targetID == userData[0]:
                    patientExists = True;

            # If not append it
            if patientExists == False:
                f.write(targetID+':'+str(self.encode(christieNo))+'\n')
                #print('Writing patient data...')
                #print(targetID+':'+str(self.encode(christieNo)))

    def encode(self, message):
        obj = AES.new(self.AKEY, AES.MODE_CFB, self.iv)
        return base64.urlsafe_b64encode(obj.encrypt(message))

    def decode(self, cipher):
        obj2 = AES.new(self.AKEY, AES.MODE_CFB, self.iv)
        return obj2.decrypt(base64.urlsafe_b64decode(cipher))

    def formatDate(self, dateString):
        #print(dateString)
        dateList = dateString.split('/')
        formattedDate = dateList[2]+'-'+dateList[1]+'-'+dateList[0]

        if len(dateList[2]) < 3:
            formattedDate = '20'+formattedDate

        return formattedDate

TargetData()
