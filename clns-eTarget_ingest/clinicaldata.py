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
import utilities
import hashlib
from fileinput import filename
from azure.storage.file import FileService
import re
import csv
import traceback
import sys
from datetime import datetime

class ClinicalDataException(Exception):
    """Raised internally and is already sent to the log file/db"""
    pass

class ClinicalData:
    def __init__(self, filename, remotehostname, remoteusername, remotepassword, remotedbname, fileuser, filekey, datadir='data', logblob='log'):
        self.filename = filename
        self.datadir=datadir
        self.file_service = FileService(account_name=fileuser, account_key=filekey)
        try:
            self.clinicalDataString = self.file_service.get_file_to_text(self.datadir, None, filename).content
        except Exception as e:
            self.clinicalDataString = self.file_service.get_file_to_text(self.datadir, None, filename, encoding="cp1252").content
        self.remotehostname = remotehostname
        self.remoteusername = remoteusername
        self.remotepassword = remotepassword
        self.remotedbname = remotedbname
        print(self.remotehostname + "\n" + self.remoteusername+"\n"+self.remotepassword+"\n"+self.remotedbname)
        self.conn = pymssql.connect(self.remotehostname,self.remoteusername, self.remotepassword, self.remotedbname, autocommit=False)
        self.log = utilities.Util(remotehostname, remoteusername, remotepassword, remotedbname, fileuser, filekey, logblob)
        self.errorflag = False
        self.config = self.log.getConfig()
    
    def __del__(self):
        self.conn.close()
     
    def deleteFile(self):
        try:
            if self.file_service.exists(self.datadir, None, self.filename):
                self.file_service.delete_file(self.datadir, None, self.filename)
            if self.errorflag==False:
                self.log.systemStatusUpdate(filename, 'Clinical', self.log.timestamp(), 'Success')
        except:
            self.log.logMessage('There was a problem deleting '+self.filename)
    
    def rollback(self):
        self.conn.rollback()
        self.conn.close()
    
       

    def getPatientDetails(self, row):
        
    # Patient data from the file
        patientDetails = {'hospital_numberNo':row[0], 
            'patientNumber':row[1], 
            'ageOfConsent':row[2], 
            'gender':row[3], 
            'dateOfDiagnosis':row[4], 
            'primaryTumourType':row[5].replace("'", "''"), 
            'stage':row[6], 
            'dateOfConsent':row[7], 
            'dateOfBloodCollection':row[9], 
            'sampleType':row[10], 
            'sampleDate':row[11], 
            'biopsyLocation':row[12].replace("'", "''"), 
            'gdlRequestDate':row[13], 
            'treatmentDetails':row[14].replace("'", "''"), 
            'treatmentStartDate':row[15], 
            'treatmentEndDate':row[16], 
            'consultant':row[17].replace("'", "''"), 
            'updateStamp':row[18], 
            'preclinId':row[19].replace("'", "''"), 
            'tumourId':row[20].replace("'", "''"), 
            'addInfo':row[21].replace("'", "''")}
        
        if len(patientDetails['ageOfConsent'].strip())==0 :
            raise Exception('Age at consent cannot be empty')
        
        if not patientDetails['ageOfConsent'].isdigit() :
            raise Exception('Age at consent is not a number')
        
        if len(patientDetails['dateOfConsent'].strip())==0:
            raise Exception('Date of Consent cannot be empty')
        
        if len(patientDetails['gender'].strip())==0:
            raise Exception('Gender cannot be empty')
        
        if len(patientDetails['patientNumber'].strip())==0:
            raise Exception('Patient ID cannot be empty')
        
        if len(patientDetails['dateOfDiagnosis'].strip())==0:
            raise Exception('Date of diagnosis cannot be empty')
        
        if len(patientDetails['consultant'].strip())==0:
            raise Exception('Consultant cannot be empty')
        
        if len(patientDetails['primaryTumourType'].strip())==0:
            raise Exception('Diagnosis cannot be empty')
        
        if len(patientDetails['sampleType'].strip())==0 and (len(patientDetails['sampleDate'].strip())>0 or len(row[22].strip())>0 or len(patientDetails['biopsyLocation'].strip())>0):
            raise Exception('Sample type cannot be empty if other tumour data are present')
        
        if len(patientDetails['treatmentDetails'].strip())==0 and (len(patientDetails['treatmentStartDate'].strip())>0 or len(patientDetails['treatmentEndDate'].strip())>0):
            raise Exception('treatments require a name')
        
        if len(row[8].strip())>1:
            self.checkTimePoint(row[8], 'blood', patientDetails['patientNumber'])
            patientDetails['sampleTimePoint'] = row[8][1:]
            if len(patientDetails['dateOfBloodCollection'].strip())==0:
                raise Exception('Date of blood collection cannot be empty if there is a time point')
        elif len(patientDetails['dateOfBloodCollection'].strip())>0:
            raise Exception('Blood timepoint cannot be empty if other blood data are available')
        else:
            patientDetails['sampleTimePoint'] = ''
            
            
        if len(row[22].strip())>1:
            self.checkTimePoint(row[22].strip(), 'tumour', patientDetails['patientNumber'])
            patientDetails['tumourTimePoint'] = row[22][2:]
            if len(patientDetails['preclinId'].strip()) ==0:
                patientDetails['preclinId']=patientDetails['patientNumber']+row[22]
        else:
            if patientDetails['sampleType'].upper()!='CDX' and patientDetails['sampleType'].upper()!='PDX' and len(patientDetails['sampleDate'].strip()) > 0:
                raise Exception('Tumour timepoint cannot be empty if other tumour data are available')
            patientDetails['tumourTimePoint']=''
            
        f = "%d/%m/%Y"
        f2 = "%d/%m/%Y %H:%M"
        try:
            datetime.strptime(patientDetails['dateOfDiagnosis'], f)
            datetime.strptime(patientDetails['dateOfConsent'], f)
            if patientDetails['dateOfBloodCollection'].strip()!='':
                datetime.strptime(patientDetails['dateOfBloodCollection'], f)
            if patientDetails['sampleDate'].strip()!='':
                datetime.strptime(patientDetails['sampleDate'], f) 
            if patientDetails['treatmentStartDate'].strip()!='':
                datetime.strptime(patientDetails['treatmentStartDate'], f) 
            if patientDetails['treatmentEndDate'].strip()!='':
                datetime.strptime(patientDetails['treatmentEndDate'], f)
        except ValueError as e:   
            raise Exception('Date format is incorrect and does not match dd/mm/yyyy or is not a valid date')      
        try:  
            if patientDetails['updateStamp'].strip()!='':
                datetime.strptime(patientDetails['updateStamp'], f2)
        except ValueError as e:   
            try: 
                datetime.strptime(patientDetails['updateStamp'], f)
            except ValueError as e: 
                raise Exception('Date format is incorrect and does not match dd/mm/yyyy HH:MM or is not a valid date')
        
        return patientDetails
    
    def checkTimePoint(self, timepoint, type, patientID):
        if type=='blood':
            pattern = re.compile("^T\d{1,2}$")
            if not re.match(pattern, timepoint):
                self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: The blood time point ('+timepoint+') is incompatible with the naming convention ('+patientID+')')
                self.log.logMessage('Error creating person ' +patientID+"\n blood time point has the wrong structure")
                self.errorflag=True
                raise ClinicalDataException('Blood timepoint incorrect format')
        else:
            pattern = re.compile("^Bx\d{1,2}$")
            if not re.match(pattern, timepoint):
                self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: The tumour time point ('+timepoint+') is incompatible with the naming convention ('+patientID+')')
                self.log.logMessage('Error creating person ' +patientID+"\n tumour time point has the wrong structure")
                self.errorflag=True
                raise ClinicalDataException('Error checking timepoint')
        return
    
    def readLine(self, number):
        csvString = self.clinicalDataString.splitlines()
        readCSV = csv.reader(csvString, delimiter=',', dialect=csv.excel_tab)
        i=0
        for row in readCSV:
            i=i+1
            if i== number:
                return row
        return ''

    def ingest(self):        
        csvString = self.clinicalDataString.splitlines()
        try:
            readCSV = csv.reader(csvString, delimiter=',', dialect=csv.excel_tab)
        except Exception as e:
            self.log.logMessage(self.filename + ' problems with ingestion ' + str(e))
            print(e)
            self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Data import failed; check encoding of file')
        patientList = []
        
        rowCount = 0
        try:
            for row in readCSV:
                if rowCount==0:
                    rowCount = rowCount+1
                    continue
                if len(row) == 0:
                    continue
                if row[8] == 'Finished':
                    continue
                patientDetails = self.getPatientDetails(row)
                        
                # check if record exist and delete if present on appearance in file
                if patientDetails['patientNumber'] not in patientList:
                    patientList.append(patientDetails['patientNumber'])
                    self.checkAndDeletePatient(patientDetails)
                
                #process this patient 
                self.ingestPatient(patientDetails)
                rowCount = rowCount+1
                
            #self.conn.close()
            if self.errorflag==False:
                # no problems foundc
                self.conn.commit()
                self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Success')
                self.log.logMessage(self.filename + ' successfully ingested')
                return 0
            else:
                self.rollback()
                self.log.logMessage(self.filename + ' problems with ingestion')
                return -1
        except ClinicalDataException as e:
            self.rollback()
            self.log.logMessage(self.filename + ' problems with ingestion ' + str(e))
            traceback.print_exc(file=sys.stdout)
            return -1
        except Exception as e:
            self.rollback()
            self.log.logMessage(self.filename + ' problems with ingestion ' + str(e))
            print(e)
            traceback.print_exc(file=sys.stdout)
            self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Data import failed (' + str(e) +')')
            return -1
        
    def checkAndDeletePatient(self, patientDetails):
        #check if patient exist
        self.log.logMessage('checking if patient exists')
        
        selectPatient = self.conn.cursor()
        selectPatient.execute("SELECT * FROM PERSON WHERE target_id = '"+patientDetails['patientNumber']+"'")
        patients=selectPatient.fetchall()
        selectPatientRowCount = 0
        personID = 0
        for row in patients:
            personID = row[0]
            databaseLastUpdate = row[6]
            selectPatientRowCount = selectPatientRowCount+1
            
        if selectPatientRowCount == 0:
            return 
        
        #found patient need to clear old db entries
        deleteProcedureOccurance = self.conn.cursor()
        deleteProcedureOccurance.execute("DELETE from PROCEDURE_OCCURRENCE where person_id="+str(personID))
        
        deletePDXCDX = self.conn.cursor()
        deletePDXCDX.execute("DELETE from PDXCDX where person_id="+str(personID))
        
        #find  SPECIMEN
        selectSpecimen = self.conn.cursor()
        selectSpecimen.execute("SELECT specimen_id from SPECIMEN where person_id="+str(personID))
        specimenIDs=selectSpecimen.fetchall();
        for row in specimenIDs:
            specimen_id=row[0]
            selectGenePanel = self.conn.cursor()
            selectGenePanel.execute("SELECT measurement_gene_panel_id FROM MEASUREMENT_GENE_PANEL WHERE specimen_id="+str(specimen_id))
            row = selectGenePanel.fetchone()
            selectGenePanel.execute("select IHC_REPORT.ihc_report_id from IHC_REPORT where specimen_id="+str(specimen_id))
            row2 = selectGenePanel.fetchone()
            
            if row is None and row2 is None:
                deleteSpecimen = self.conn.cursor()
                deleteSpecimen.execute("DELETE FROM SPECIMEN WHERE specimen_id="+str(specimen_id))


    def ingestPatient(self,patientDetails):
        self.log.logMessage('Updating database with new Christie details...')
        personID = self.findPatient(patientDetails)
        if personID==0:
            personID = self.createPatient(patientDetails)
        else:
            self.updatePatient(personID, patientDetails)
        self.insertCondition(personID, patientDetails)
        self.insertConsultant(patientDetails)
        self.insertTreatment(personID, patientDetails)
        self.insertBlood(personID, patientDetails)
        self.insertTumour(personID, patientDetails)
        self.insertPDXCDX(personID, patientDetails)
        
    def findPatient(self, patientDetails):
        getPatient = self.conn.cursor()
        getPatient.execute("SELECT * FROM PERSON WHERE target_id = '"+patientDetails['patientNumber']+"'")
        patients=getPatient.fetchall();
        for row in patients:
            return(row[0])
        return 0
        
    def insertConsultant(self, patientDetails):
        getConsultant = self.conn.cursor()
        getConsultant.execute("SELECT * FROM CONCEPT_CONSULTANT WHERE name = '" + patientDetails['consultant'] + "'")
        getConsultantRowcount = 0
        consultantConceptID = 0
        consultants=getConsultant.fetchall()
        for row in consultants:
            getConsultantRowcount = getConsultantRowcount + 1
            consultantConceptID = row[0]
        
        if getConsultantRowcount == 0:
            try:
                insertConsultant = self.conn.cursor()
                insertConsultant.execute("INSERT INTO CONCEPT_CONSULTANT (name) VALUES('" + patientDetails['consultant'] + "')")
                consultantConceptID = insertConsultant.lastrowid
            except Exception as e:
                self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: Data import failed (insert consultant) -- ' +patientDetails['patientNumber'])
                self.log.logMessage('Error creating consultant ' +patientDetails['patientNumber']+"\n"+ str(e))
                self.errorflag=True
                print(str(e))
                print(12)
                raise ClinicalDataException('Error creating consultant')
        return consultantConceptID

    def createPatient(self, patientDetails):
        # Insert the patient details
        getGender = self.conn.cursor()
        genderConceptID = 0
        getGender.execute("SELECT * FROM CONCEPT_GENDER WHERE gender_name = '"+patientDetails['gender'].upper()+"'")
        
        gender=getGender.fetchall()
        for row in gender:
            genderConceptID = row[0]

        if genderConceptID == 0:
            self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: Data import failed (gender not recognised)')
            self.log.logMessage('Error creating person ' +patientDetails['patientNumber']+"\n gender is not registered in DB")
            self.errorflag=True
            raise ClinicalDataException('Gender is not recognised')

        #check person_id and find site
        targetid = patientDetails['patientNumber']
        pattern = re.compile("^[A-Z]{3}\d{7}$")
        if not re.match(pattern, targetid):
            self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: One or more patient IDs are incompatible with the naming convention')
            self.log.logMessage('Error creating person ' +patientDetails['patientNumber']+"\n patient ID has the wrong structure")
            self.errorflag=True
            raise ClinicalDataException('Error creating person')
        siteId = targetid[3:6]
        getSite = self.conn.cursor()
        getSite.execute("SELECT * FROM CARE_SITE WHERE care_site_id = "+siteId)   
        row=getSite.fetchone()
        if row is None:
            self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: One or more sites are not present in eTARGET')
            self.log.logMessage('Error creating person ' +patientDetails['patientNumber']+"\n site is not registered in DB")
            self.errorflag=True
            raise ClinicalDataException('Error creating person')
        
        consultantConceptID = self.insertConsultant(patientDetails)

        try:
            patientData = self.conn.cursor()
            consentDate = self.formatDate(patientDetails['dateOfConsent'])
            hash_object = hashlib.sha256(patientDetails['hospital_numberNo'].encode('utf-8'))
            hex_dig = hash_object.hexdigest()
            print("INSERT INTO PERSON (person_id, hospital_number, hospital_number_hash, age_at_consent, gender_concept_id, consent_date, consultant_concept_id, target_id, care_site_id) VALUES((SELECT 1+COALESCE(MAX(person_id),0) FROM PERSON person), ENCRYPTBYPASSPHRASE('"+self.config['patientkey']+"', '"+patientDetails['hospital_numberNo']+"'), '"+hex_dig+"', "+str(patientDetails['ageOfConsent'])+", "+str(genderConceptID)+", '"+consentDate+"', "+str(consultantConceptID)+", '"+patientDetails['patientNumber']+"', "+str(siteId)+")")
            patientData.execute("INSERT INTO PERSON (person_id, hospital_number, hospital_number_hash, age_at_consent, gender_concept_id, consent_date, consultant_concept_id, target_id, care_site_id) VALUES((SELECT 1+COALESCE(MAX(person_id),0) FROM PERSON person), ENCRYPTBYPASSPHRASE('"+self.config['patientkey']+"', '"+patientDetails['hospital_numberNo']+"'), '"+hex_dig+"', "+str(patientDetails['ageOfConsent'])+", "+str(genderConceptID)+", '"+consentDate+"', "+str(consultantConceptID)+", '"+patientDetails['patientNumber']+"', "+str(siteId)+")")
            personID = patientData.lastrowid
            self.log.logMessage('Patient ID '+str(personID)+' added.')
        except Exception as e:
            self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: A value is missing or of the wrong format -- ' +patientDetails['patientNumber'])
            self.log.logMessage('Error creating person ' +patientDetails['patientNumber']+"\n"+ str(e))
            self.errorflag=True
            print(str(e))
            print(13)
            raise ClinicalDataException('Error creating person')

        try:
            lastUpdateCursor = self.conn.cursor()
            if len(patientDetails['updateStamp'].strip())==0:
                lastUpdateCursor.execute("UPDATE PERSON SET last_update=NULL WHERE person_id=(SELECT MAX(person_id) FROM PERSON person)")
            else:
#                 luP1 = patientDetails['updateStamp'].split(' ')
                lastUpdateInsert = self.formatDateTime(patientDetails['updateStamp'])
                lastUpdateCursor.execute("UPDATE PERSON SET last_update='"+lastUpdateInsert+"' WHERE person_id=(SELECT MAX(person_id) FROM PERSON person)")
        except Exception as e:
            self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: Data import failed (set last updated) -- ' +patientDetails['patientNumber'])
            self.log.logMessage('Error updating person '+patientDetails['patientNumber']+"\n" + str(e))
            self.errorflag=True
            print(str(e))
            print(14)
            raise ClinicalDataException('Error updating person')
            
        try:
            selectPersonIDCursor = self.conn.cursor()
            selectPersonIDCursor.execute("select top 1 person_id from PERSON WHERE target_id='"+patientDetails['patientNumber']+"'")
            row=selectPersonIDCursor.fetchone()
            personID= row[0]
            return personID
        except Exception as e:
            self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: Data import failed (select person) -- ' +patientDetails['patientNumber'])
            self.log.logMessage('Error selecting person ' +patientDetails['patientNumber']+"\n"+ str(e))
            self.errorflag=True
            print(str(e))
            print(15)
            raise ClinicalDataException('Error selecting person')


    def insertCondition(self, personID, patientDetails):
        # Insert the condition data
        getCondition = self.conn.cursor()
        getCondition.execute("SELECT * FROM CONCEPT_CONDITION_SUBTYPE WHERE subtype_name = '"+patientDetails['primaryTumourType']+"'")
#                 conditionConceptID = 0
        getConditionRowcount = 0

        conditions=getCondition.fetchall()

        for row in conditions:
            getConditionRowcount = getConditionRowcount+1
#                     conditionConceptID = row[0]

        if getConditionRowcount == 0:
            insertCondition = self.conn.cursor()
            insertCondition.execute("INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES ('"+patientDetails['primaryTumourType']+"', (select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Other'))")
            print('new condition ' + str(insertCondition.lastrowid))
#                     conditionConceptID= insertCondition.lastrowid

        if len(patientDetails['dateOfDiagnosis']) > 0:
            try:
                conditionData = self.conn.cursor()
                diagnosisDate = self.formatDate(patientDetails['dateOfDiagnosis'])
                conditionData.execute("IF NOT EXISTS (SELECT * FROM CONDITION_OCCURRENCE WHERE person_id="+str(personID)+") "+
                            "INSERT INTO CONDITION_OCCURRENCE (person_id, condition_concept_id, condition_start_date, condition_subtype_concept_id, additional_details) VALUES("+str(personID)+", (SELECT TOP 1 condition_concept_id FROM CONCEPT_CONDITION_SUBTYPE WHERE subtype_name = '"+patientDetails['primaryTumourType']+"'), '"+diagnosisDate+"', (SELECT TOP 1 subtype_concept_id FROM CONCEPT_CONDITION_SUBTYPE WHERE subtype_name = '"+patientDetails['primaryTumourType']+"'), '"+patientDetails['addInfo']+"')"+
                            "ELSE UPDATE CONDITION_OCCURRENCE SET condition_start_date='"+diagnosisDate+"', condition_concept_id=(SELECT TOP 1 condition_concept_id FROM CONCEPT_CONDITION_SUBTYPE WHERE subtype_name = '"+patientDetails['primaryTumourType']+"'), condition_subtype_concept_id=(SELECT TOP 1 subtype_concept_id FROM CONCEPT_CONDITION_SUBTYPE WHERE subtype_name = '"+patientDetails['primaryTumourType']+"'), additional_details='"+patientDetails['addInfo']+"'WHERE person_id="+str(personID))
                conditionDataID = conditionData.lastrowid
                self.log.logMessage('conditionData ID '+str(conditionDataID)+' added/updated.')
                print('conditionData ID '+str(conditionDataID)+' added/updated.')
            except Exception as e:
                self.log.systemStatusUpdate(filename, 'Clinical', self.log.timestamp(), 'Error: Data import failed (update condition_occurance) -- ' +patientDetails['patientNumber'])
                self.log.logMessage('Error inserting/updateing codition_occurance ' +patientDetails['patientNumber']+"\n" + str(e))
                self.errorflag=True
                print(str(e))
                print(16)
                raise ClinicalDataException('Error inserting/updateing codition_occurance')
        return personID

    def insertTreatment(self, personID, patientDetails):
        # Update the procedure / treatment details
        getProcedure = self.conn.cursor()
        getProcedure.execute("SELECT * FROM CONCEPT_PROCEDURE WHERE procedure_name = '"+patientDetails['treatmentDetails']+"'")
        procedureRows = 0
        
        procedure=getProcedure.fetchall()
        
        for row in procedure:
            procedureConceptID = row[0]
            procedureRows = procedureRows+1

        if procedureRows == 0:
            try:
                insertProcedure = self.conn.cursor()
                insertProcedure.execute("INSERT INTO CONCEPT_PROCEDURE (procedure_name) VALUES('"+patientDetails['treatmentDetails']+"')")
                procedureConceptID = insertProcedure.lastrowid
            except Exception as e:
                self.log.systemStatusUpdate(filename, 'Clinical', self.log.timestamp(), 'Error: Data import failed (procedure)')
                self.log.logMessage('Error inserting procedure_name ' +patientDetails['patientNumber']+"\n" + str(e))
                self.errorflag=True
                print(str(e))
                print(7)
                raise ClinicalDataException('Error inserting procedure_name')

        procedureData = self.conn.cursor()

        if patientDetails['treatmentStartDate'] != '':
            treatmentStartDate = self.formatDate(patientDetails['treatmentStartDate'])
        else:
            treatmentStartDate = ''

        if patientDetails['treatmentEndDate'] != '':
            treatmentEndDate = self.formatDate(patientDetails['treatmentEndDate'])
        else:
            treatmentEndDate = ''

        try:
            procedureData.execute("IF NOT EXISTS (SELECT * FROM PROCEDURE_OCCURRENCE WHERE person_id ='"+str(personID)+"' AND procedure_concept_id = '"+str(procedureConceptID)+"' AND procedure_start_date='"+treatmentStartDate+"') INSERT INTO PROCEDURE_OCCURRENCE (person_id, procedure_concept_id, procedure_start_date, procedure_end_date) VALUES('"+str(personID)+"', '"+str(procedureConceptID)+"', '"+treatmentStartDate+"', '"+treatmentEndDate+"')")
            procedureDataID = procedureData.lastrowid
            self.log.logMessage('procedureData ID '+str(procedureDataID)+' updated.')
        except Exception as e:
            self.log.systemStatusUpdate(filename, 'Clinical', self.log.timestamp(), 'Error: Data import failed (procedure_occurance) -- ' +patientDetails['patientNumber'])
            self.log.logMessage('Error inserting procedure_occurance ' +patientDetails['patientNumber']+"\n" + str(err)) 
            self.errorflag=True
            print(str(e))
            print(8)
            raise ClinicalDataException('Error inserting procedure_occurance')


    def updatePatient(self, personID, patientDetails):
        if len(patientDetails['updateStamp'].strip())==0:
            lastUpdate = 'NULL'
        else:
#             lU1 = patientDetails['updateStamp'].split(' ')
            lastUpdate = "'"+self.formatDateTime(patientDetails['updateStamp'])+"'"
        # Update the patient details
        consultantID = self.insertConsultant(patientDetails)
        
        getGender = self.conn.cursor()
        genderConceptID = 0
        getGender.execute("SELECT * FROM CONCEPT_GENDER WHERE gender_name = '"+patientDetails['gender'].upper()+"'")
        
        gender=getGender.fetchall()
        for row in gender:
            genderConceptID = row[0]

        if genderConceptID == 0:
            self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: Data import failed (gender not recognised)')
            self.log.logMessage('Error creating person ' +patientDetails['patientNumber']+"\n gender is not registered in DB")
            self.errorflag=True
            raise ClinicalDataException('Gender is not recognised')
        
        try:
            patientData = self.conn.cursor()
            consentDate = self.formatDate(patientDetails['dateOfConsent'])
#             uD1 = patientDetails['updateStamp'].split(' ')
            hash_object = hashlib.sha256(patientDetails['hospital_numberNo'].encode('utf-8'))
            hex_dig = hash_object.hexdigest()
            #hospital_numberNo = self.encode(patientDetails['hospital_numberNo'])
            patientData.execute("UPDATE PERSON SET age_at_consent="+str(patientDetails['ageOfConsent'])+", hospital_number=ENCRYPTBYPASSPHRASE('"+self.config['patientkey']+"', '"+patientDetails['hospital_numberNo']+"'), hospital_number_hash='"+hex_dig+"', gender_concept_id="+str(genderConceptID)+", consent_date='"+consentDate+"', last_update="+lastUpdate+" WHERE target_id = '"+patientDetails['patientNumber']+"'")
            patientData.execute("UPDATE PERSON SET consultant_concept_id=(SELECT TOP 1 consultant_concept_id FROM CONCEPT_CONSULTANT WHERE name = '" + patientDetails['consultant'] + "') WHERE target_id = '"+patientDetails['patientNumber']+"'")
            self.log.logMessage('Patient ID '+str(personID)+' updated.')
        except Exception as e:
            self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: Data import failed (update person) -- ' +patientDetails['patientNumber'])
            self.log.logMessage('Error updateing person '+patientDetails['patientNumber']+"\n" + (str(e)))
            self.errorflag=True
            print(str(e))
            print(1)
            raise ClinicalDataException('Error updating person')

        # Update the condition data
        getCondition = self.conn.cursor()
        getCondition.execute("SELECT * FROM CONCEPT_CONDITION_SUBTYPE WHERE subtype_name = '"+patientDetails['primaryTumourType']+"'")
        
        row = getCondition.fetchone()
        if row is None:
            insertCondition = self.conn.cursor()
            insertCondition.execute("INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES ('"+patientDetails['primaryTumourType']+"', (select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Other'))")            
            
        if len(patientDetails['dateOfDiagnosis']) > 0:
            try:
                conditionData = self.conn.cursor()
                diagnosisDate = self.formatDate(patientDetails['dateOfDiagnosis'])
                #check if exists
                conditionData.execute("IF NOT EXISTS (SELECT * FROM CONDITION_OCCURRENCE WHERE person_id="+str(personID)+") "+
                    "INSERT INTO CONDITION_OCCURRENCE (person_id, condition_concept_id, condition_start_date, condition_subtype_concept_id, additional_details) VALUES("+str(personID)+", (SELECT TOP 1 condition_concept_id FROM CONCEPT_CONDITION_SUBTYPE WHERE subtype_name = '"+patientDetails['primaryTumourType']+"'), '"+diagnosisDate+"', (SELECT TOP 1 subtype_concept_id FROM CONCEPT_CONDITION_SUBTYPE WHERE subtype_name = '"+patientDetails['primaryTumourType']+"'), '"+patientDetails['addInfo']+"')"+
                    "ELSE UPDATE CONDITION_OCCURRENCE SET condition_start_date='"+diagnosisDate+"', condition_concept_id=(SELECT TOP 1 condition_concept_id FROM CONCEPT_CONDITION_SUBTYPE WHERE subtype_name = '"+patientDetails['primaryTumourType']+"'), condition_subtype_concept_id=(SELECT TOP 1 subtype_concept_id FROM CONCEPT_CONDITION_SUBTYPE WHERE subtype_name = '"+patientDetails['primaryTumourType']+"'), additional_details='"+patientDetails['addInfo']+"'WHERE person_id="+str(personID))
                conditionDataID = conditionData.lastrowid
                self.log.logMessage('conditionData ID '+str(conditionDataID)+' updated.')
            except Exception as e:
                self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: Data import failed (condition data) -- ' +patientDetails['patientNumber'])
                self.log.logMessage('Error updateing person '+patientDetails['patientNumber']+ ' Condition_Occurance \n' +str(e))
                self.errorflag=True
                print(str(e))
                print(3)
                raise ClinicalDataException('Error Condition_Occurance')
        
        try:
            consultantData = self.conn.cursor()
            consultantData.execute("UPDATE PERSON SET consultant_concept_id='"+str(consultantID)+"' WHERE person_id = '"+str(personID)+"'")
            consultantDataID = consultantData.lastrowid
            self.log.logMessage('consultantDataID ID '+str(consultantDataID)+' updated.')
        except Exception as e:
            self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: Data import failed (update consultant) -- ' +patientDetails['patientNumber'])
            self.log.logMessage('Error updating consultant ' +patientDetails['patientNumber']+"\n" + str(e))
            self.errorflag=True
            print(str(e))
            print(11)
            raise ClinicalDataException('Error updating consultant')

    def insertBlood(self, personID, patientDetails):
        # Blood specimen
        if len(patientDetails['dateOfBloodCollection']) > 0:
            try:
                specimenData = self.conn.cursor()
                bloodDate = self.formatDate(patientDetails['dateOfBloodCollection'])
                specimenData.execute("IF NOT EXISTS (SELECT * FROM SPECIMEN WHERE person_id="+str(personID)+" AND specimen_concept_id=1 AND baseline_number="+str(patientDetails['sampleTimePoint'])+") "+
                    "INSERT INTO SPECIMEN (person_id, specimen_date, specimen_concept_id, baseline_number) VALUES("+str(personID)+", '"+bloodDate+"', 1, "+str(patientDetails['sampleTimePoint'])+") "+
                    "ELSE UPDATE SPECIMEN SET specimen_date = '"+bloodDate+"' WHERE person_id="+str(personID)+" AND specimen_concept_id=1 AND baseline_number="+str(patientDetails['sampleTimePoint']))
                specimenDataID = specimenData.lastrowid
                self.log.logMessage('Specimen blood ID '+str(specimenDataID)+' updated.')
            except Exception as e:
                self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: Data import failed (Blood specimen) -- ' +patientDetails['patientNumber'])
                self.log.logMessage('Error inserting/updating blood specimen ' +patientDetails['patientNumber']+"\n" + str(e))
                self.errorflag=True
                print(str(e))
                print(4)
                raise ClinicalDataException('Error inserting/updating blood specimen')
            
    def insertTumour(self, personID, patientDetails):
        if patientDetails['sampleType'].lower() == 'ffpe' or patientDetails['sampleType'].lower() == 'snap' or patientDetails['sampleType'].lower() == 'fresh':
            specimenConceptID = '3'
        elif patientDetails['sampleType'].lower() == 'archival':
            specimenConceptID = '2'
        else:
            specimenConceptID = '0'
            if patientDetails['sampleDate']!='' and patientDetails['sampleType'].lower() != 'cdx' and patientDetails['sampleType'].lower() != 'pdx':
                raise Exception('unknown sample type')
        if specimenConceptID == '2' or specimenConceptID == '3':
            try:
                specimenData = self.conn.cursor()
                if patientDetails['sampleDate'] !='':
                    tumourDate = self.formatDate(patientDetails['sampleDate'])
                    #specimenData.execute("UPDATE SPECIMEN SET specimen_date = '"+tumourDate+"', anatomic_site_id=(SELECT anatomy_concept_id FROM CONCEPT_ANATOMY WHERE anatomy_name = '"+patientDetails['primaryTumourType']+"') WHERE person_id="+str(personID)+" AND specimen_concept_id="+specimenConceptID)
                else :
                    tumourDate =''
                specimenData.execute("SELECT * FROM SPECIMEN WHERE person_id="+str(personID)+" AND baseline_number="+str(patientDetails['tumourTimePoint'])+ " and specimen_concept_id="+str(specimenConceptID))
                specimenDataRows = 0
                specimen=specimenData.fetchall()
                for row in specimen:
                    specimenID=row[0]
                    specimenDataRows = specimenDataRows+1
                # The record didn't exist to update, so let's add it now
                if specimenDataRows == 0:
                    specimenData.execute("INSERT INTO SPECIMEN (person_id, specimen_date, specimen_concept_id, anatomic_site_id, tumour_id, preclin_id, baseline_number) VALUES('"+str(personID)+"', '"+tumourDate+"', "+str(specimenConceptID)+", (SELECT anatomy_concept_id FROM CONCEPT_ANATOMY WHERE anatomy_name = '"+patientDetails['primaryTumourType']+"'), '"+str(patientDetails['tumourId']) + "', '"+str(patientDetails['preclinId'])+"', "+str(patientDetails['tumourTimePoint'])+")")
                    specimenID = specimenData.lastrowid
                else:
                    specimenData.execute("UPDATE SPECIMEN set specimen_concept_id="+str(specimenConceptID)+" ,specimen_date='"+tumourDate+"', tumour_id='"+str(patientDetails['tumourId']) +"', baseline_number="+str(patientDetails['tumourTimePoint'])+", preclin_id='"+str(patientDetails['preclinId'])+"', anatomic_site_id= (SELECT TOP 1 anatomy_concept_id FROM CONCEPT_ANATOMY WHERE anatomy_name = '"+patientDetails['primaryTumourType']+"') where specimen_id="+str(specimenID))

                #print(specimenDataID)
                self.log.logMessage('Specimen tumour ID '+str(specimenID)+' updated.')
            except Exception as e:
                self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: Data import failed (tumour specimen) -- ' +patientDetails['patientNumber'])
                self.log.logMessage('Error inserting/updating tumour specimen ' +patientDetails['patientNumber']+"\n" + str(e))
                self.errorflag=True
                print(str(e))
                print(5)
                raise ClinicalDataException('Error inserting/updating tumour specimen')

            try:
                gdlSQL = ""
                gdlData = self.conn.cursor()
                if len(patientDetails['gdlRequestDate']) > 0:
                    gdlDate = self.formatDate(patientDetails['gdlRequestDate'])
                    gdlSQL = "UPDATE GDL_REQUEST SET date_requested='"+gdlDate+"', sample_type='"+patientDetails['biopsyLocation']+"' WHERE specimen_id=(SELECT TOP 1 specimen_id FROM SPECIMEN WHERE person_id="+str(personID)+" AND ((specimen_concept_id="+str(specimenConceptID)+" and specimen_date='"+tumourDate+"' and (preclin_id is NULL)) OR dbo.RemoveNonAlphaNumChars(preclin_id) like dbo.RemoveNonAlphaNumChars('"+str(patientDetails['preclinId'])+"')))"
                    gdlData.execute(gdlSQL)

                    if gdlData.rowcount == 0:
                        gdlSQL = "INSERT INTO GDL_REQUEST (date_requested, path_lab_ref, specimen_id, sample_type) VALUES('"+gdlDate+"', '', '"+str(specimenID)+"', '"+patientDetails['biopsyLocation']+"')"
                        gdlData.execute(gdlSQL)
                else:
                    gdlSQL = "UPDATE GDL_REQUEST SET sample_type='"+patientDetails['biopsyLocation']+"' WHERE specimen_id=(SELECT TOP 1 specimen_id FROM SPECIMEN WHERE person_id="+str(personID)+" AND specimen_concept_id="+str(specimenConceptID)+" AND ((specimen_date='"+tumourDate+"' and (preclin_id is NULL)) OR dbo.RemoveNonAlphaNumChars(preclin_id) like dbo.RemoveNonAlphaNumChars('"+str(patientDetails['preclinId'])+"')))"
                    gdlData.execute(gdlSQL)

                    if gdlData.rowcount == 0:
                        gdlSQL = "INSERT INTO GDL_REQUEST (path_lab_ref, specimen_id, sample_type) VALUES('', '"+str(specimenID)+"', '"+patientDetails['biopsyLocation']+"')"
                        gdlData.execute(gdlSQL)

                specimenID = specimenData.lastrowid
                print(specimenID)
                self.log.logMessage('GDL ID '+str(specimenID)+' updated.')
            except Exception as e:
                self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: Data import failed (GDL request) -- ' +patientDetails['patientNumber'])
                self.log.logMessage('Error inserting/updating GDL data ' +patientDetails['patientNumber']+"\n" + str(e))
                self.errorflag=True
                print(str(e))
                print(6)
                raise ClinicalDataException('Error inserting/updating GDL data')

            
    def insertPDXCDX(self, personID, patientDetails):
        if patientDetails['sampleType'] == 'PDX' or patientDetails['sampleType'] == 'CDX':
            # Insert the PDX/CDX data
            try:
                insertPDXCDX = self.conn.cursor()
                if patientDetails['sampleDate']!='':
                    pdxDate = self.formatDate(patientDetails['sampleDate'])
                    insertPDXCDX.execute("IF NOT EXISTS (SELECT * FROM PDXCDX WHERE person_id ='"+str(personID)+"' AND date_created='"+str(pdxDate)+"' AND pdx_or_cdx='"+str(patientDetails['sampleType'])+"') INSERT INTO PDXCDX (date_created, pdx_or_cdx, timepoint, person_id) VALUES('"+str(pdxDate)+"', '"+str(patientDetails['sampleType'])+"', '"+str(patientDetails['sampleTimePoint'])+"', '"+str(personID)+"')")
                else :
                    insertPDXCDX.execute("IF NOT EXISTS (SELECT * FROM PDXCDX WHERE person_id ='"+str(personID)+"' AND date_created is null AND pdx_or_cdx='"+str(patientDetails['sampleType'])+"') INSERT INTO PDXCDX (pdx_or_cdx, timepoint, person_id) VALUES('"+str(patientDetails['sampleType'])+"', '"+str(patientDetails['sampleTimePoint'])+"', '"+str(personID)+"')")
                insertPDXCDXID = insertPDXCDX.lastrowid
            except Exception as e:
                self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: Data import failed (PDX/CDX) -- ' +patientDetails['patientNumber'])
                self.log.logMessage('Error insert PDXCDX ' +patientDetails['patientNumber']+"\n" + str(e))
                self.errorflag=True
                print(str(e))
                print(9)
                raise ClinicalDataException('Error insert PDXCDX')

    def formatDateTime(self, dateString):
#         print(dateString)
        if len(dateString)<16:
            return self.formatDate(dateString)
        try:
            dateString= formattedDate = dateString[0:16]
            datetimeO=datetime.strptime(dateString, '%d/%m/%Y %H:%M')
            return datetimeO.strftime('%Y-%m-%d %H:%M:%S')
        except ValueError:
            raise Exception('Date string not correct '+ dateString)

    def formatDate(self, dateString):
        #print(dateString)
        dateList = dateString.split('/')
        formattedDate = dateList[2]+'-'+dateList[1]+'-'+dateList[0]

        if len(dateList[2]) < 3:
            formattedDate = '20'+formattedDate

        return formattedDate
    
