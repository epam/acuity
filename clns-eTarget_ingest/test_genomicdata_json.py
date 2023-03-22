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
import genomicdata_json

class GenomicData_specimen(unittest.TestCase):
    """test getting the correct specimen name"""
    
    def test_is_correct_specimen_tumour(self):
#         fm = foundationmedicine.FoundationMedicine("PRF500068_02.xml", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')
        genomic = genomicdata_json.GenomicDataJson("CUP0014573T1Blood.json", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')
        genomic.ingest()
#         specimen_id, baseline, tar_id, source_txt=genomic.getSpecimen()
#         print(specimen_id)
#         self.assertTrue(specimen_id != None)
#         print(source_txt)
#         self.assertTrue(source_txt!=None)
#         resubmission,measurement_gene_panel_id=fm.checkResubmission(specimen_id, baseline, tar_id, source_txt)
#         print(resubmission,measurement_gene_panel_id)
#         self.assertTrue(resubmission==0)
#         measrumentgenepanelid = fm.insertMeasurementGenePanel(specimen_id, baseline, source_txt)
#         self.assertTrue(measrumentgenepanelid!=None)
#         updateSelection=False
#         fm.parseShortVariants(measrumentgenepanelid, tar_id, updateSelection, measurement_gene_panel_id)
#         fm.parseCopyNumberAlterations(measrumentgenepanelid, tar_id, updateSelection, measurement_gene_panel_id)
#         fm.parseRearrangements(measrumentgenepanelid, tar_id, updateSelection, measurement_gene_panel_id)
#     
#     def test_is_correct_specimen(self):
#         fm = foundationmedicine.FoundationMedicine("PRF500068_01.xml", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')
# #         fm.ingest()
#         self.specimen=fm.getSpecimen()
#         print(self.specimen)
#         self.assertTrue(self.specimen != None)
#         
#     def test_is_correct_specimen_blood(self):
#         fm = foundationmedicine.FoundationMedicine("FMTAR00051Blood.xml", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')
# #         fm.ingest()
#         self.specimen=fm.getSpecimen()
#         print(self.specimen)
#         self.assertTrue(self.specimen != None)
#         
#     def test_resubmission(self):
#         fm = foundationmedicine.FoundationMedicine("FMTAR00051Blood.xml", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')
#         self.code, self.gene_panel_id = fm.checkResubmission(3337,1,'TAR00052')
#         print(self.code)
#         print(self.gene_panel_id)
#         self.assertTrue(self.code==2)
#         self.assertTrue(self.gene_panel_id==2993)
#         
#     def test_delete(self):
#         fm = foundationmedicine.FoundationMedicine("FMTAR00051Blood.xml", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')
#         try :
#             fm.deleteFMdata(2993)
#             fm.rollback()
#             self.assertTrue(True)
#         except Exception as e:
#             fm.rollback()
#             self.assertTrue(False)
#        
#     def test_get_gene_id(self):
#         fm = foundationmedicine.FoundationMedicine("PRF500068_01.xml", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')
#         gene_id=fm.getGene('TP53')
#         print(gene_id)
#         self.assertTrue(gene_id==25)
#         gene_id=fm.getGene('GeneC')
#         print(gene_id)
#         self.assertTrue(gene_id>0)
#        
# #     def test_short_variants(self):
# #         fm = foundationmedicine.FoundationMedicine("PRF500068_01.xml", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')
# #         try:
# #             fm.parseShortVariants(2952)
# #             self.assertTrue(True)
# #         except Exception as e:
# #             self.assertTrue(False)
# #             
#     def test_short_variants_tick(self):
#         fm = foundationmedicine.FoundationMedicine("FMTAR00051Blood.xml", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')
#         try:
#             fm.parseShortVariants(2952,'TAR00051', True, 2991)
#             self.assertTrue(True)
#         except Exception as e:
#             print(e)
#             self.assertTrue(False)
#         try:
#             fm.parseRearrangements(2952,'TAR00051', True, 2981)
#             self.assertTrue(True)
#         except Exception as e:
#             print(e)
#             self.assertTrue(False)
#         try:
#             fm.parseCopyNumberAlterations(2952,'TAR00051', True, 2981)
#             self.assertTrue(True)
#         except Exception as e:
#             print(e)
#             self.assertTrue(False)
#             
# #     def test_copy_number_alteration(self):
# #         fm = foundationmedicine.FoundationMedicine("PRF500068_01.xml", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')
# #         try:
# #             fm.parseCopyNumberAlterations(2952)
# #             self.assertTrue(True)
# #         except Exception as e:
# #             self.assertTrue(False)def test_is_correct_specimen(self):
#         fm = foundationmedicine.FoundationMedicine("PRF500068_01.xml", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')
# #         fm.ingest()
#         self.specimen=fm.getSpecimen()
#         print(self.specimen)
#         self.assertTrue(self.specimen != None)
#         
#     def test_is_correct_specimen_blood(self):
#         fm = foundationmedicine.FoundationMedicine("FMTAR00051Blood.xml", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')
# #         fm.ingest()
#         self.specimen=fm.getSpecimen()
#         print(self.specimen)
#         self.assertTrue(self.specimen != None)
#         
#     def test_resubmission(self):
#         fm = foundationmedicine.FoundationMedicine("FMTAR00051Blood.xml", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')
#         self.code, self.gene_panel_id = fm.checkResubmission(3337,1,'TAR00052')
#         print(self.code)
#         print(self.gene_panel_id)
#         self.assertTrue(self.code==2)
#         self.assertTrue(self.gene_panel_id==2993)
#         
#     def test_delete(self):
#         fm = foundationmedicine.FoundationMedicine("FMTAR00051Blood.xml", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')
#         try :
#             fm.deleteFMdata(2993)
#             fm.rollback()
#             self.assertTrue(True)
#         except Exception as e:
#             fm.rollback()
#             self.assertTrue(False)
#        
#     def test_get_gene_id(self):
#         fm = foundationmedicine.FoundationMedicine("PRF500068_01.xml", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')
#         gene_id=fm.getGene('TP53')
#         print(gene_id)
#         self.assertTrue(gene_id==25)
#         gene_id=fm.getGene('GeneC')
#         print(gene_id)
#         self.assertTrue(gene_id>0)
#        
#     def test_short_variants(self):
#         fm = foundationmedicine.FoundationMedicine("PRF500068_01.xml", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')
#         try:
#             fm.parseShortVariants(2952)
#             self.assertTrue(True)
#         except Exception as e:
#             self.assertTrue(False)
#             
#     def test_short_variants_tick(self):
#         fm = foundationmedicine.FoundationMedicine("FMTAR00051Blood.xml", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')
#         try:
#             fm.parseShortVariants(2952,'TAR00051', True, 2991)
#             self.assertTrue(True)
#         except Exception as e:
#             print(e)
#             self.assertTrue(False)
#         try:
#             fm.parseRearrangements(2952,'TAR00051', True, 2981)
#             self.assertTrue(True)
#         except Exception as e:
#             print(e)
#             self.assertTrue(False)
#         try:
#             fm.parseCopyNumberAlterations(2952,'TAR00051', True, 2981)
#             self.assertTrue(True)
#         except Exception as e:
#             print(e)
#             self.assertTrue(False)
            
#     def test_copy_number_alteration(self):
#         fm = foundationmedicine.FoundationMedicine("PRF500068_01.xml", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')
#         try:
#             fm.parseCopyNumberAlterations(2952)
#             self.assertTrue(True)
#         except Exception as e:
#             self.assertTrue(False)
#             
#     def test_rearrangement(self):
#         fm = foundationmedicine.FoundationMedicine("PRF500068_01.xml", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')        
#         try:
#             fm.parseRearrangements(2952)
#             self.assertTrue(True)
#         except Exception as e:
#             self.assertTrue(False)
    
#     def test_ingest(self):
#         fm = foundationmedicine.FoundationMedicine("PRF500068_01.xml", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')        
#         try:
#             fm.ingest()
#             self.assertTrue(True)
#         except Exception as e:
#             self.assertTrue(False)
#             
#     def test_ingest_blood(self):
#         fm = foundationmedicine.FoundationMedicine("FMTAR00051Blood.xml", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')        
#         try:
#             fm.ingest()
#             self.assertTrue(True)
#         except Exception as e:
#             
#     def test_rearrangement(self):
#         fm = foundationmedicine.FoundationMedicine("PRF500068_01.xml", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')        
#         try:
#             fm.parseRearrangements(2952)
#             self.assertTrue(True)
#         except Exception as e:
#             self.assertTrue(False)
    
#     def test_ingest(self):
#         fm = foundationmedicine.FoundationMedicine("PRF500068_01.xml", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')        
#         try:
#             fm.ingest()
#             self.assertTrue(True)
#         except Exception as e:
#             self.assertTrue(False)
#             
#     def test_ingest_blood(self):
#         fm = foundationmedicine.FoundationMedicine("FMTAR00051Blood.xml", "uomdevsqlserveret.database.windows.net", "et_python_process@uomdevsqlserveret.database.windows.net", "Funvax41!", "uom_dev_etarget_db", "uomdevstorageet", "GIGjJSDgeMf3bS8EJVclOHEPOz8/Dxan6wWYjJa73MSLT5IIkpo81ZHPy5cncvMWrVuuIbiKqEMvwYO7KvpKDw==", 'test')        
#         try:
#             fm.ingest()
#             self.assertTrue(True)
#         except Exception as e:
#             self.assertTrue(False) 
              
if __name__ == '__main__':
    unittest.main()