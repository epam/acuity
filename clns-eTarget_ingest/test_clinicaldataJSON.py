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

import unittest
import clinical_json

class Clinicaldata(unittest.TestCase):
#     
#     def test_insertConsultant(self):
#         cd = clinicaldata.ClinicalData("TAR00100.csv", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')
#         pd = cd.getPatientDetails(cd.readLine(2))
#         print(pd['consultant'])
#         consultant_id=cd.insertConsultant(pd)
#         print(str(consultant_id))
#         self.assertTrue(consultant_id>0)
#         print('end test_createConsultant\n')
#         
#         
    def test_createPatient(self):
        cd = clinical_json.ClinicalDataJson("NonFhirClinical.json", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')
        
        print(cd.formatDateTime('2020-01-01T23:55:00'))
        
        print(cd.formatDateTime('2020-02-01'))
        
        print(cd.formatDateTime('2020-03-01T23:55'))
        
        print(cd.formatDateTime('2020-01-01T23:55:00.123'))
        
        cd.ingest()
#       
#       
#     def test_updatePatient(self):
#         cd = clinicaldata.ClinicalData("TAR00100.csv", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')
#         pd = cd.getPatientDetails(cd.readLine(20))
#         print(pd['patientNumber'])
#         patient_id=cd.createPatient(pd)
#         print(str(patient_id))
#         self.assertTrue(patient_id>0) 
#         print('end test_updatePatient\n')
#         
#         
#     def test_checkAndDeletePatient(self):
#         cd = clinicaldata.ClinicalData("TAR00100.csv", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')
#         pd = cd.getPatientDetails(cd.readLine(20))
#         print(pd['patientNumber'])
#         cd.checkAndDeletePatient(pd)
#         print('end test_checkAndDeletePatient\n')
#         
#     def test_insertCondition(self):
#         cd = clinicaldata.ClinicalData("TAR00100.csv", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')
#         pd = cd.getPatientDetails(cd.readLine(2))
#         person_id=cd.insertCondition(21,pd)
#         self.assertTrue(person_id==21)
#         print('end test_insertCondition\n')
#        
#        
#     def test_insertCondition2(self):
#         cd = clinicaldata.ClinicalData("TAR00100.csv", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')
#         pd = cd.getPatientDetails(cd.readLine(20))
#         person_id=cd.insertCondition(21,pd)
#         self.assertTrue(person_id==21) 
#         print('end test_insertCondition2\n')
#         
#         
#     def test_insertTreatment(self):
#         cd = clinicaldata.ClinicalData("TAR00100.csv", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')
#         pd = cd.getPatientDetails(cd.readLine(2))
#         cd.insertTreatment(21,pd)
#         print('end test_insertTreatment\n')
#         
#         
#     def test_insertTreatment2(self):
#         cd = clinicaldata.ClinicalData("TAR00100.csv", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')
#         pd = cd.getPatientDetails(cd.readLine(20))
#         cd.insertTreatment(21,pd)
#         print('end test_insertTreatment2\n')     
#         
#     def test_insertBlood(self):
#         cd = clinicaldata.ClinicalData("TAR00100.csv", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')
#         pd = cd.getPatientDetails(cd.readLine(20))
#         cd.insertBlood(21,pd)
#         print('end test_insertBlood\n')
#         
#     def test_insertTumour(self):
#         cd = clinicaldata.ClinicalData("TAR00100.csv", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')
#         pd = cd.getPatientDetails(cd.readLine(20))
#         cd.insertTumour(21,pd)
#         print('end test_insertTumour\n') 
    
#     def test_insertTumour2(self):
#         cd = clinicaldata.ClinicalData("TAR00100.csv", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')
#         pd = cd.getPatientDetails(cd.readLine(19))
#         cd.insertTumour(21,pd)
#         print('end test_insertTumour\n') 
    
#     
#     def test_insertPDXCDX(self):
#         cd = clinicaldata.ClinicalData("TAR00100.csv", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')
#         pd = cd.getPatientDetails(cd.readLine(2))
#         cd.insertPDXCDX(21,pd)
#         print('end test_insertPDXCDX\n')        
#         
#     def test_formatDate(self):
#         cd = clinicaldata.ClinicalData("TAR00100.csv", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')
#         date_str=cd.formatDate('15/05/2016')
#         self.assertTrue(date_str=='2016-05-15')
#         print('end test_formatDate\n')
#         
#         
#     def test_ingestPatient(self):
#         cd = clinicaldata.ClinicalData("TAR00100.csv", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')
#         pd = cd.getPatientDetails(cd.readLine(3))
#         cd.ingestPatient(pd)
#         print('end test_ingestPatient\n')
#         
#     def test_ingest(self):
#         cd = clinicaldata.ClinicalData("TAR00100.csv", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')
#         cd.ingest()
#         print('end test_ingest\n')