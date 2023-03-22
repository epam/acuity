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
import ihc_report


class IHC_Reoprt_test(unittest.TestCase):
#     def test_getPersonID(self):
#         ihc = ihc_report.IHC_Report("IHC.xlsx", 
#                                     "uomdevsqlserveret.database.windows.net", 
#                                     "et_python_process@uomdevsqlserveret.database.windows.net", 
#                                     "Funvax41!", 
#                                     "uom_dev_etarget_db", 
#                                     "uomdevstorageet", 
#                                     "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 
#                                     "test")
#         person_id =ihc.queryPerson("TAR00001")
#         self.assertTrue(person_id==6)
        
    def test_getSpecimenID(self):
        ihc = ihc_report.IHC_Report("CUP0014567IHC.xlsx", 
                                    "uomdevsqlserveret.database.windows.net", 
                                    "et_python_process@uomdevsqlserveret.database.windows.net", 
                                    "Funvax41!", 
                                    "uom_dev_etarget_db", 
                                    "uomdevstorageet", 
                                    "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 
                                    "test")
        specimen_id =ihc.querySpecimen("CUP0014567Bx1ICabc",89)
        self.assertTrue(specimen_id==4609)
        with self.assertRaises(Exception): ihc.querySpecimen("CUP0014567Bx9ICabc",89)
        with self.assertRaises(Exception): ihc.querySpecimen("CUP0014500Bx1ICabc",89)
        
#     def test_parseDate(self):
#         ihc = ihc_report.IHC_Report("IHC.xlsx", 
#                                     "uomdevsqlserveret.database.windows.net", 
#                                     "et_python_process@uomdevsqlserveret.database.windows.net", 
#                                     "Funvax41!", 
#                                     "uom_dev_etarget_db", 
#                                     "uomdevstorageet", 
#                                     "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 
#                                     "test")
#         date =ihc.parseDate("01OCT2019")
#         self.assertTrue(date=="2019-10-01")
#         self.assertTrue(ihc.parseDate("")=="NULL")
#         self.assertTrue(ihc.parseDate(None)=="NULL")
        
#     def test_parseFloat(self):
#         ihc = ihc_report.IHC_Report("IHC.xlsx", 
#                                     "uomdevsqlserveret.database.windows.net", 
#                                     "et_python_process@uomdevsqlserveret.database.windows.net", 
#                                     "Funvax41!", 
#                                     "uom_dev_etarget_db", 
#                                     "uomdevstorageet", 
#                                     "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 
#                                     "test")   
#         ret_float = ihc.parseFloat(1.11)
#         self.assertTrue(ret_float==1.11)
#         ret_float = ihc.parseFloat(233.1765089919)
#         self.assertTrue(ret_float==233.1765089919)
#         self.assertTrue(ihc.parseFloat('No result')=='NULL')
#         self.assertTrue(ihc.parseFloat(None)=='NULL')
#         
#     def test_checkRecordExists(self):
#         ihc = ihc_report.IHC_Report("IHC.xlsx", 
#                                     "uomdevsqlserveret.database.windows.net", 
#                                     "et_python_process@uomdevsqlserveret.database.windows.net", 
#                                     "Funvax41!", 
#                                     "uom_dev_etarget_db", 
#                                     "uomdevstorageet", 
#                                     "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 
#                                     "test")   
#         self.assertTrue(ihc.checkRecordExists(3260))
#         self.assertFalse(ihc.checkRecordExists(3261))
#         
#     def test_checkResubmission(self):
#         ihc = ihc_report.IHC_Report("IHC.xlsx", 
#                                     "uomdevsqlserveret.database.windows.net", 
#                                     "et_python_process@uomdevsqlserveret.database.windows.net", 
#                                     "Funvax41!", 
#                                     "uom_dev_etarget_db", 
#                                     "uomdevstorageet", 
#                                     "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 
#                                     "test")   
#         result= ihc.checkResubmission(3260)
#         self.assertTrue(result[0]==2)
#         self.assertTrue(result[1]==1)
#         result= ihc.checkResubmission(3261)
#         self.assertTrue(result[0]==0)
#         self.assertTrue(result[1]==0)
#         
#     def test_ingets(self):
#         ihc = ihc_report.IHC_Report("IHC.xlsx", 
#                                     "uomdevsqlserveret.database.windows.net", 
#                                     "et_python_process@uomdevsqlserveret.database.windows.net", 
#                                     "Funvax41!", 
#                                     "uom_dev_etarget_db", 
#                                     "uomdevstorageet", 
#                                     "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 
#                                     "test")   
#         data = ihc.ingest()
#         print(str(data))
#         self.assertTrue(data['person_id']==6)
        
#     def test_insertIHCRecord(self):
#         ihc = ihc_report.IHC_Report("IHC.xlsx", 
#                                     "uomdevsqlserveret.database.windows.net", 
#                                     "et_python_process@uomdevsqlserveret.database.windows.net", 
#                                     "Funvax41!", 
#                                     "uom_dev_etarget_db", 
#                                     "uomdevstorageet", 
#                                     "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 
#                                     "test")  
#         data = {'person_id': 6, 'specimen_id': 3260, 'date_received': '2019-09-10', 'date_report': '2019-09-21', 'cd3_area': 'NULL', 'cd3_tumoural': 7389.65636061774, 'cd3_stromal': 504.645569023251, 'cd8_area': 1.23, 'cd8_tumoural': 4.56, 'cd8_stromal': 'NULL', 'pdl1_tps': '≥1 to <50', 'estimated_result': 'No result', 'comments': 'Hello'}
#         id=ihc.insertIHCRecord(data)
#         print(str(id))
#         self.assertTrue(id>0)
#         ihc.commit()
        