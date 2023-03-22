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

class ClinicalDataException(Exception):
    """Raised internally and is already sent to the log file/db"""
    pass

class ClinicalDataJson:
    def __init__(self, filename, remotehostname, remoteusername, remotepassword, remotedbname, fileuser, filekey, datadir='data', logblob='log'):
        self.filename = filename
        self.datadir=datadir
        self.file_service = FileService(account_name=fileuser, account_key=filekey)
        self.clinicalDataString = self.file_service.get_file_to_text(self.datadir, None, filename).content
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
        
    def ingest(self):
        try:
            clinicalDataAll = json.loads(self.clinicalDataString)
            if not isinstance(clinicalDataAll, list):
                self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(),'Error: JSON format, first element must be an array')
                self.log.logMessage(self.filename + ' JSON does not start with an array')
                return -1
            for clinicalDataIndividual in clinicalDataAll:
                if clinicalDataIndividual['type'] == 'clinical':
                    patientDetails = self.getPatientDetails(clinicalDataIndividual)
                    self.checkAndDeletePatient(patientDetails)
                    self.ingestPatient(patientDetails)
                else:
                    self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: JSON contains data of wrong type.')
                    self.log.logMessage(self.filename + ' json contains wrong type of data')
                    raise ClinicalDataException("Type of data is not clinical")
            if self.errorflag==False:
                # no problems found
                self.conn.commit()
                self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Success')
                self.log.logMessage(self.filename + ' successfully ingested')
                return 0
            else:
                self.rollback()
                self.log.logMessage(self.filename + ' problems with ingestion')
                return -1
        except ClinicalDataException as e:
            self.log.logMessage("abort ingest because of previous errors")
            self.log.logMessage(traceback.format_exc())
            return -1    
        
        except Exception as e:
            self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: JSON format cannot be read; ' + str(e).replace("'", "''"))
            self.log.logMessage(self.filename + ' json format wrong' + str(e) )
            self.log.logMessage(traceback.format_exc())
            return -1
        
        
    def getPatientDetails(self, patientRecord):
        
        patientDetails = {'christieNo':"", 
            'patientNumber':patientRecord['patientId'], 
            'ageAtConsent':patientRecord['ageAtConsent'], 
            'gender':patientRecord['gender'], 
            'dateOfDiagnosis':patientRecord['dateOfDiagnosis'], 
            'dateOfConsent':patientRecord['dateOfConsent'], 
            'primaryTumourType':patientRecord['diagnosis'].replace("'", "''").strip(), 
            'consultant':patientRecord['consultant'].replace("'", "''").strip() 
        }
        if not 'dateTimeUpdate' in patientRecord or len(patientRecord['dateTimeUpdate'])==0:
            patientDetails['updateStamp']='NULL'
        else:
            patientDetails['updateStamp']=patientRecord['dateTimeUpdate']
        if not 'diagnosis' in patientRecord or len(patientRecord['diagnosis'])==0:
            self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: Data import failed (diagnosis not present)')
            self.log.logMessage('Error diagnosis not present')
            self.errorflag=True
            return
        if not 'dateOfDiagnosis' in patientRecord or len(patientDetails['dateOfDiagnosis'])==0:
            self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: Data import failed (dateOfDiagnosis not present)')
            self.log.logMessage('Error dateOfDiagnosis not present')
            self.errorflag=True
            return
        if not 'consultant' in patientRecord or len(patientDetails['consultant'])==0:
            self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: Data import failed (consultant data not present)')
            self.log.logMessage('Error consultant name not present')
            self.errorflag=True
            return
        if not 'ageAtConsent' in patientRecord or not isinstance(patientDetails['ageAtConsent'], int):
            self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: Data import failed (ageAtConsent data not present, or not a number)')
            self.log.logMessage('Error ageAtConsent name not present')
            self.errorflag=True
            return
        if 'additionalTumourInfo' in patientRecord:
            if isinstance(patientRecord['additionalTumourInfo'], str):
                patientDetails['addInfo']=patientRecord['additionalTumourInfo'].replace("'", "''")
            else:
                self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: Data import failed (additionalTumourInfo data not a string)')
                self.log.logMessage('Error additionalTumourInfo data not a string')
                self.errorflag=True
                return
        else:
            patientDetails['addInfo']=''
            
        if 'samples' in patientRecord:
            patientDetails['specimen']=patientRecord['samples']
        else:
            patientDetails['specimen']=[]
            
        if 'treatment' in patientRecord and len(patientRecord['treatment'])>0:
            patientDetails['treatment']=patientRecord['treatment']
        else:
            patientDetails['treatment']=[]
            
        print(patientDetails)
        return patientDetails
        
        
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
        self.insertTreatments(personID, patientDetails)
        self.insertSamples(personID, patientDetails)
#         self.insertBlood(personID, patientDetails)
#         self.insertTumour(personID, patientDetails)
#         self.insertPDXCDX(personID, patientDetails)
        
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
            self.log.logMessage('Error creating person ' +patientDetails['patientNumber']+"\n site is not registerd in DB")
            self.errorflag=True
            raise ClinicalDataException('Error creating person')
        
        consultantConceptID = self.insertConsultant(patientDetails)

        try:
            patientData = self.conn.cursor()
            print(patientDetails)
            consentDate = self.formatDate(patientDetails['dateOfConsent'])
            #hash_object = hashlib.sha256(patientDetails['christieNo'].encode('utf-8'))
            #hex_dig = hash_object.hexdigest()
            print("INSERT INTO PERSON (person_id, age_at_consent, gender_concept_id, consent_date, consultant_concept_id, target_id, care_site_id) VALUES((SELECT 1+COALESCE(MAX(person_id),0) FROM PERSON person), "+str(patientDetails['ageAtConsent'])+", "+str(genderConceptID)+", '"+consentDate+"', "+str(consultantConceptID)+", '"+patientDetails['patientNumber']+"', "+str(siteId)+")")
            patientData.execute("INSERT INTO PERSON (person_id, age_at_consent, gender_concept_id, consent_date, consultant_concept_id, target_id, care_site_id) VALUES((SELECT 1+COALESCE(MAX(person_id),0) FROM PERSON person), "+str(patientDetails['ageAtConsent'])+", "+str(genderConceptID)+", '"+consentDate+"', "+str(consultantConceptID)+", '"+patientDetails['patientNumber']+"', "+str(siteId)+")")
            personID = patientData.lastrowid
            self.log.logMessage('Patient ID '+str(personID)+' added.')
        except Exception as e:
            self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: Data import failed (create patient) -- ' +patientDetails['patientNumber'])
            self.log.logMessage('Error creating person ' +patientDetails['patientNumber']+"\n"+ str(e))
            self.errorflag=True
            print(str(e))
            print(13)
            raise ClinicalDataException('Error creating person')

        try:
            lastUpdateCursor = self.conn.cursor()
            if patientDetails['updateStamp']!='NULL':
                lastUpdateInsert = "'"+self.formatDateTime(patientDetails['updateStamp'])+"'"
            else:
                lastUpdateInsert = 'NULL'
            lastUpdateCursor.execute("UPDATE PERSON SET last_update="+lastUpdateInsert+" WHERE person_id=(SELECT MAX(person_id) FROM PERSON person)")
        except Exception as e:
            self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: Data import failed (set last updated) -- ' +patientDetails['patientNumber'])
            self.log.logMessage('Error updating person '+patientDetails['patientNumber']+"\n" + str(e))
            self.errorflag=True
            print(str(e))
            print(14)
            raise ClinicalDataException(str(e))
            
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
                self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: Data import failed (update condition_occurance) -- ' +patientDetails['patientNumber'])
                self.log.logMessage('Error inserting/updateing codition_occurance ' +patientDetails['patientNumber']+"\n" + str(e))
                self.errorflag=True
                print(str(e))
                print(16)
                raise ClinicalDataException('Error inserting/updateing codition_occurance')
        return personID
    
    def insertSamples(self,personID, patientDetails):
        for sample in patientDetails['specimen']:
            if 'type' in sample and isinstance(sample['type'], str):
                type=sample['type']
                if type.lower()=='blood':
                    self.insertBlood(personID, patientDetails['patientNumber'], sample)
                elif type.lower()=='tumour':
                    self.insertTumour(personID,patientDetails['patientNumber'], sample, patientDetails['primaryTumourType'])
                else:
                   self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: Data import failed (unknown type of sample. Must be blood or tumour)')
                   self.log.logMessage('Error unknown type of sample')
                   self.errorflag=True 
            else:
                self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: Data import failed (type of sample must be present and a string)')
                self.log.logMessage('Error sample type missing or not a string')
                self.errorflag=True

    def insertTreatments(self, personID, patientDetails):
        for treatment in patientDetails['treatment']:
            if 'name' not in treatment or len(treatment['name'])==0:
                self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(),'Error: Data import failed (treatments require a name)')
                self.log.logMessage('Treatment name required')
                self.errorflag=True
                return
            procedure={
                'name':treatment['name'].replace("'", "''").strip(),
                'start':'',
                'end':''
                }
            if 'dateStart' in treatment:
                procedure['start']=treatment['dateStart']
            if 'dateEnd' in treatment:
                procedure['end']=treatment['dateEnd']
            print(procedure)
            self.insertTreatment(personID,patientDetails,procedure)

    def insertTreatment(self, personID, patientDetails, treatment):
        # Update the procedure / treatment details
        getProcedure = self.conn.cursor()
        getProcedure.execute("SELECT * FROM CONCEPT_PROCEDURE WHERE procedure_name = '"+treatment['name']+"'")
        procedureRows = 0
        
        procedure=getProcedure.fetchall()
        
        for row in procedure:
            procedureConceptID = row[0]
            procedureRows = procedureRows+1

        if procedureRows == 0:
            try:
                insertProcedure = self.conn.cursor()
                insertProcedure.execute("INSERT INTO CONCEPT_PROCEDURE (procedure_name) VALUES('"+treatment['name']+"')")
                procedureConceptID = insertProcedure.lastrowid
            except Exception as e:
                self.log.systemStatusUpdate(filename, 'Clinical', self.log.timestamp(), 'Error: Data import failed (procedure)')
                self.log.logMessage('Error inserting procedure_name ' +patientDetails['patientNumber']+"\n" + str(e))
                self.errorflag=True
                print(str(e))
                print(7)
                raise ClinicalDataException('Error inserting procedure_name')

        procedureData = self.conn.cursor()

        if treatment['start'] != '':
            treatmentStartDate = self.formatDate(treatment['start'])
        else:
            treatmentStartDate = ''

        if treatment['end'] != '':
            treatmentEndDate = self.formatDate(treatment['end'])
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

        # Update the patient details
        consultantID = self.insertConsultant(patientDetails)
        try:
            patientData = self.conn.cursor()
            consentDate = self.formatDate(patientDetails['dateOfConsent'])
            if patientDetails['updateStamp']!='NULL':
                lastUpdate = "'"+self.formatDateTime(patientDetails['updateStamp'])+"'"
            else:
                lastUpdate = 'NULL'
            #christieNo = self.encode(patientDetails['christieNo'])
            patientData.execute("UPDATE PERSON SET age_at_consent="+str(patientDetails['ageAtConsent'])+", gender_concept_id=(SELECT gender_concept_id FROM CONCEPT_GENDER WHERE gender_name ='"+patientDetails['gender'].upper()+"'), consent_date='"+consentDate+"', last_update="+lastUpdate+" WHERE target_id = '"+patientDetails['patientNumber']+"'")
            patientData.execute("UPDATE PERSON SET consultant_concept_id=(SELECT TOP 1 consultant_concept_id FROM CONCEPT_CONSULTANT WHERE name = '" + patientDetails['consultant'] + "') WHERE target_id = '"+patientDetails['patientNumber']+"'")
            self.log.logMessage('Patient ID '+str(personID)+' updated.')
        except Exception as e:
            self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: Data import failed (update person) -- ' +patientDetails['patientNumber'] + ' ' + str(e))
            self.log.logMessage('Error updateing person '+patientDetails['patientNumber']+"\n" + (str(e)))
            self.errorflag=True
            print(str(e))
            print(1)
            raise ClinicalDataException(str(e))

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
                self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: Data import failed for -- ' +patientDetails['patientNumber'] + ': ' +str(e).replace("'", "''"))
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

    def insertBlood(self, personID, patientNumber, sample):
        # Blood specimen
        if 'cdx' in sample and (sample['cdx']==True or (isinstance(sample['cdx'], str) and sample['cdx'].lower()=='true')):
            self.insertPDXCDX(personID, patientNumber, sample)
        if 'cdx' in sample and not isinstance(sample['cdx'], bool) and ((isinstance(sample['cdx'], str) and sample['cdx'].lower()!='true' and sample['cdx'].lower()!='false') or 
                                (isinstance(sample['cdx'], (int, float, complex)))):
            raise Exception('CDX value must be either true or false')
        try:
            specimenData = self.conn.cursor()
            bloodDate = self.formatDate(sample['date'])
            pattern = re.compile("^T\d{1,2}$")
            if not re.match(pattern, sample['timePoint']):
                raise Exception('timepoint does not conform to pattern T[number]')
            print(sample['timePoint'])
            timepoint = sample['timePoint'][1:]
            print(timepoint)
            specimenData.execute("IF NOT EXISTS (SELECT * FROM SPECIMEN WHERE person_id="+str(personID)+" AND specimen_concept_id=1 AND baseline_number="+timepoint+") "+
                "INSERT INTO SPECIMEN (person_id, specimen_date, specimen_concept_id, baseline_number) VALUES("+str(personID)+", '"+bloodDate+"', 1, "+timepoint+") "+
                "ELSE UPDATE SPECIMEN SET specimen_date = '"+bloodDate+"' WHERE person_id="+str(personID)+" AND specimen_concept_id=1 AND baseline_number="+timepoint)
            specimenDataID = specimenData.lastrowid
            self.log.logMessage('Specimen blood ID for '+sample['timePoint']+' updated.')
                
        except Exception as e:
            self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: Data import failed (Blood specimen) -- ' +patientNumber)
            self.log.logMessage('Error inserting/updating blood specimen ' +patientNumber+"\n" + str(e))
            self.errorflag=True
            print(str(e))
            print(4)
            raise ClinicalDataException('Error inserting/updating blood specimen')
            
    def insertTumour(self, personID, patientNumber, sample, diagnosis):
        if 'pdx' in sample and (sample['pdx']==True or (isinstance(sample['pdx'], str) and sample['pdx'].lower()=='true')):
            self.insertPDXCDX(personID, patientNumber, sample)
        if 'pdx' in sample and not isinstance(sample['pdx'], bool) and ((isinstance(sample['pdx'], str) and sample['pdx'].lower()!='true' and sample['pdx'].lower()!='false') or 
                                (isinstance(sample['pdx'], (int, float, complex)))):
            raise Exception('PDX value must be either true or false')
        
        pattern = re.compile("^Bx\d{1,2}$")
        if not re.match(pattern, sample['timePoint']):
            raise Exception('timepoint does not conform to pattern T[number]')
        print(sample['timePoint'])
        timepoint = sample['timePoint'][2:]
        print(timepoint)
        if sample['sampleType'].lower() == 'ffpe' or sample['sampleType'].lower() == 'snap' or sample['sampleType'].lower() ==  'fresh':
            specimenConceptID = '3'
        elif sample['sampleType'].lower() == 'archival':
            specimenConceptID = '2'
        else:
            self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: Data import failed (tumour sampleType not present, or value not supported)')
            self.log.logMessage('Error sampleType not present')
            self.errorflag=True
            return
        if 'sampleId' in sample and sample['sampleId']!='':
            sampleId = sample['sampleId']
        else:
            sampleId = patientNumber+sample['timePoint']
        try:
            specimenData = self.conn.cursor()
            if 'date' in sample and sample['date'] !='':
                tumourDate = self.formatDate(sample['date'])
                #specimenData.execute("UPDATE SPECIMEN SET specimen_date = '"+tumourDate+"', anatomic_site_id=(SELECT anatomy_concept_id FROM CONCEPT_ANATOMY WHERE anatomy_name = '"+patientDetails['primaryTumourType']+"') WHERE person_id="+str(personID)+" AND specimen_concept_id="+specimenConceptID)
            else :
                tumourDate =''
            specimenData.execute("SELECT * FROM SPECIMEN WHERE person_id="+str(personID)+" AND (preclin_id like '"+sampleId+"')");
            specimenDataRows = 0
            specimen=specimenData.fetchall()
            for row in specimen:
                specimenID=row[0]
                specimenDataRows = specimenDataRows+1
            # The record didn't exist to update, so let's add it now
            if specimenDataRows == 0:
                specimenData.execute("INSERT INTO SPECIMEN (person_id, specimen_date, specimen_concept_id, anatomic_site_id, preclin_id, baseline_number) VALUES('"+str(personID)+"', '"+tumourDate+"', "+str(specimenConceptID)+", (SELECT anatomy_concept_id FROM CONCEPT_ANATOMY WHERE anatomy_name = '"+diagnosis+"'), '"+sampleId+"',"+timepoint+")")
                specimenID = specimenData.lastrowid
            else:
                specimenData.execute("UPDATE SPECIMEN set specimen_concept_id="+str(specimenConceptID)+" ,specimen_date='"+tumourDate+"', preclin_id='"+sampleId+"', baseline_number="+timepoint+", anatomic_site_id= (SELECT TOP 1 anatomy_concept_id FROM CONCEPT_ANATOMY WHERE anatomy_name = '"+diagnosis+"') where specimen_id="+str(specimenID))

            #print(specimenDataID)
            self.log.logMessage('Specimen tumour ID '+str(specimenID)+' updated.')
            if 'siteOfBiobsy' in sample and sample['siteOfBiobsy']!= '':
                sample['siteOfBiobsy']=sample['siteOfBiobsy'].replace("'", "''")
                gdlData = self.conn.cursor()
                gdlSQL = "UPDATE GDL_REQUEST SET sample_type='"+sample['siteOfBiobsy']+"' WHERE specimen_id='"+str(specimenID)+"'"
                gdlData.execute(gdlSQL)
    
                if gdlData.rowcount == 0:
                    gdlSQL = "INSERT INTO GDL_REQUEST (path_lab_ref, specimen_id, sample_type) VALUES('', '"+str(specimenID)+"', '"+sample['siteOfBiobsy']+"')"
                    gdlData.execute(gdlSQL)

        except Exception as e:
            self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: Data import failed (tumour specimen) -- ' +patientNumber)
            self.log.logMessage('Error inserting/updating tumour specimen ' +patientNumber+"\n" + str(e))
            self.errorflag=True
            print(str(e))
            print(5)
            raise ClinicalDataException('Error inserting/updating tumour specimen')

# Not clear yet whether we want this, if yes it needs to be specific to where it went
#             try:
#                 gdlSQL = ""
#                 gdlData = self.conn.cursor()
#                 if len(patientDetails['gdlRequestDate']) > 0:
#                     gdlDate = self.formatDate(patientDetails['gdlRequestDate'])
#                     gdlSQL = "UPDATE GDL_REQUEST SET date_requested='"+gdlDate+"', sample_type='"+patientDetails['biopsyLocation']+"' WHERE specimen_id=(SELECT TOP 1 specimen_id FROM SPECIMEN WHERE person_id="+str(personID)+" AND ((specimen_date='"+tumourDate+"' and (preclin_id is NULL)) OR dbo.RemoveNonAlphaNumChars(preclin_id) like dbo.RemoveNonAlphaNumChars('"+str(patientDetails['preclinId'])+"')))"
#                     gdlData.execute(gdlSQL)
# 
#                     if gdlData.rowcount == 0:
#                         gdlSQL = "INSERT INTO GDL_REQUEST (date_requested, path_lab_ref, specimen_id, sample_type) VALUES('"+gdlDate+"', '', '"+str(specimenID)+"', '"+patientDetails['biopsyLocation']+"')"
#                         gdlData.execute(gdlSQL)
#                 else:
#                     gdlSQL = "UPDATE GDL_REQUEST SET sample_type='"+patientDetails['biopsyLocation']+"' WHERE specimen_id=(SELECT TOP 1 specimen_id FROM SPECIMEN WHERE person_id="+str(personID)+" AND specimen_concept_id="+str(specimenConceptID)+" AND ((specimen_date='"+tumourDate+"' and (preclin_id is NULL)) OR dbo.RemoveNonAlphaNumChars(preclin_id) like dbo.RemoveNonAlphaNumChars('"+str(patientDetails['preclinId'])+"')))"
#                     gdlData.execute(gdlSQL)
# 
#                     if gdlData.rowcount == 0:
#                         gdlSQL = "INSERT INTO GDL_REQUEST (path_lab_ref, specimen_id, sample_type) VALUES('', '"+str(specimenID)+"', '"+patientDetails['biopsyLocation']+"')"
#                         gdlData.execute(gdlSQL)
# 
#                 specimenID = specimenData.lastrowid
#                 print(specimenID)
#                 self.log.logMessage('GDL ID '+str(specimenID)+' updated.')
#             except Exception as e:
#                 self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: Data import failed (GDL request) -- ' +patientDetails['patientNumber'])
#                 self.log.logMessage('Error inserting/updating GDL data ' +patientDetails['patientNumber']+"\n" + str(e))
#                 self.errorflag=True
#                 print(str(e))
#                 print(6)
#                 raise Exception('Error inserting/updating GDL data')

            
    def insertPDXCDX(self, personID, patientNumber, sample):
        # Insert the PDX/CDX data
        try:
            if sample['type']=='blood':
                pdxcdx='CDX'
                pattern = re.compile("^T\d{1,2}$")
                if not re.match(pattern, sample['timePoint']):
                    raise Exception('timepoint does not conform to pattern T[number]')
                timepoint = sample['timePoint'][1:]
            else:
                pdxcdx='PDX'
                pattern = re.compile("^Bx\d{1,2}$")
                if not re.match(pattern, sample['timePoint']):
                    raise Exception('timepoint does not conform to pattern T[number]')
                timepoint = sample['timePoint'][2:]
            insertPDXCDX = self.conn.cursor()
            if 'date' in sample and sample['date']!='':
                pdxDate = self.formatDate(sample['date'])
                insertPDXCDX.execute("IF NOT EXISTS (SELECT * FROM PDXCDX WHERE person_id ='"+str(personID)+"' AND date_created='"+str(pdxDate)+"' AND pdx_or_cdx='"+pdxcdx+"') INSERT INTO PDXCDX (date_created, pdx_or_cdx, timepoint, person_id) VALUES('"+str(pdxDate)+"', '"+pdxcdx+"', '"+str(timepoint)+"', '"+str(personID)+"')")
            else :
                insertPDXCDX.execute("IF NOT EXISTS (SELECT * FROM PDXCDX WHERE person_id ='"+str(personID)+"' AND date_created is null AND pdx_or_cdx='"+pdxcdx+"') INSERT INTO PDXCDX (pdx_or_cdx, timepoint, person_id) VALUES('"+pdxcdx+"', '"+str(timepoint)+"', '"+str(personID)+"')")
            insertPDXCDXID = insertPDXCDX.lastrowid
        except Exception as e:
            self.log.systemStatusUpdate(self.filename, 'Clinical', self.log.timestamp(), 'Error: Data import failed (PDX/CDX) -- ' +patientNumber)
            self.log.logMessage('Error insert PDXCDX ' +patientNumber+"\n" + str(e))
            self.errorflag=True
            print(str(e))
            print(9)
            raise ClinicalDataException('Error insert PDXCDX')
        
    def formatDateTime(self, dateString):
#         print(dateString)
        if len(dateString)<19:
            return self.formatDate(dateString)
        try:
            dateString= formattedDate = dateString[0:19]
            datetime.datetime.strptime(dateString, '%Y-%m-%dT%H:%M:%S')
        except ValueError:
            raise Exception('Date string not correct '+ dateString)
        return dateString

    def formatDate(self, dateString):
#         print(dateString)
        formattedDate = dateString[0:10]
        if not self.valid_date(formattedDate):
            raise Exception('Date string not correct '+ dateString)
#         print(formattedDate)

        return formattedDate
    
    def valid_date(self, datestring):
        try:
            datetime.datetime.strptime(datestring, '%Y-%m-%d')
            return True
        except ValueError:
            return False
    
