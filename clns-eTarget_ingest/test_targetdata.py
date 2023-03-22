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
import targetdata
import pymssql

class TargetdataTest(unittest.TestCase):
    
    
    
#     def testRNAPDF(self):
#         td = targetdata.TargetData()
#         self.assertTrue(td.checkRNAPDF('CUP0010002T1SMQER.pdf'))
#         self.assertFalse(td.checkRNAPDF('CUP0010010T1SMQER.pdf'))
#         self.assertFalse(td.checkRNAPDF('CUP0110002T1SMQER.pdf'))
#     
#     def test_checkOtherPDF(self):
#         print('test')
#         td = targetdata.TargetData()
#         print('object')
#         self.assertTrue(td.checkOtherSourcePDF('CUP0010002T1fm.pdf'))
#         self.assertTrue(td.checkOtherSourcePDF('CUP0010003T1gh.pdf')) 
#         self.assertTrue(td.checkOtherSourcePDF('CUP0010005Bx1gh.pdf')) 
#         self.assertFalse(td.checkOtherSourcePDF('CUP0110002T1SMQER.pdf'))
#         print('end')
    
#     def test_checkFMPDF(self):
#         td = targetdata.TargetData()
#         self.assertTrue(td.checkFMPDF('TAR00002BWaFM.pdf'))
#         self.assertTrue(td.checkFMPDF('TAR00002DP2WaFM.pdf'))
#         self.assertFalse(td.checkFMPDF('TAR00002DP3WaFM.pdf'))
#         self.assertTrue(td.checkFMPDF('TAR00002PTaFM.pdf'))
#         self.assertFalse(td.checkFMPDF('TAR00005PTbFM.pdf'))
#         self.assertFalse(td.checkFMPDF('TAR11001BWaFM.pdf'))
#         self.assertFalse(td.checkFMPDF('TAR00001DT7WaFM.pdf'))#     def test_checkQCIPDF(self):
#         td = targetdata.TargetData()
#         self.assertTrue(td.checkQCIPDF('TAR00002BWaQCI.pdf'))
#         self.assertTrue(td.checkQCIPDF('TAR00002DP3WaQCI.pdf'))
#         self.assertTrue(td.checkQCIPDF('TAR00002PTaQCI.pdf'))
#         self.assertFalse(td.checkQCIPDF('TAR00005PTbQCI.pdf'))
#         self.assertFalse(td.checkQCIPDF('TAR11001BWaQCI.pdf'))
#         self.assertFalse(td.checkQCIPDF('TAR00001DT7WaQCI.pdf'))
#         self.assertFalse(td.checkFMPDF('TAR00001DT33WaFM.pdf'))
#         self.assertTrue(td.checkFMPDF('TAR00002PTaFM.pdf'))
#         
    def test_checkQCIPDF(self):
        td = targetdata.TargetData()
        self.assertTrue(td.checkQCIPDF('CUP0010001T1QCI.pdf'))
        self.assertTrue(td.checkQCIPDF('CUP0010002T10QCI.pdf'))
        self.assertFalse(td.checkQCIPDF('TAR00005PT1QCI.pdf'))
        self.assertFalse(td.checkQCIPDF('ABC0010001T1QCI.pdf'))
        self.assertFalse(td.checkQCIPDF('CUP0990001T1QCI.pdf'))
        self.assertFalse(td.checkQCIPDF('CUP0010001T1QCIabc.pdf'))
        self.assertFalse(td.checkQCIPDF('CUP0010002T12QCI.pdf'))
#         
#     def test_checkGDLPDF(self):
#         td = targetdata.TargetData()
#         self.assertTrue(td.checkGDLPDF('17004931.pdf'))
#         self.assertFalse(td.checkGDLPDF('999asfjls.pdf'))
#         
#     def test_processBloodFile(self):
#         td = targetdata.TargetData()
#         config = td.getConfig()
#         conn = pymssql.connect(config['remotehostname'], config['remoteusername'], config['remotepassword'], config['remotedbname'], autocommit=False)
#         cursor=conn.cursor()
#         cursor.execute("select specimen_id from SPECIMEN where person_id=11 and specimen_concept_id=1 and baseline_number=1")
#         row=cursor.fetchone()
#         if row is not None:
#             print(row[0])
#         