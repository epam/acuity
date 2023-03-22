---
-- #%L
-- eTarget Maven Webapp
-- %%
-- Copyright (C) 2017 - 2021 digital ECMT
-- %%
-- Permission is hereby granted, free of charge, to any person obtaining a copy
-- of this software and associated documentation files (the "Software"), to deal
-- in the Software without restriction, including without limitation the rights
-- to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
-- copies of the Software, and to permit persons to whom the Software is
-- furnished to do so, subject to the following conditions:
-- 
-- The above copyright notice and this permission notice shall be included in
-- all copies or substantial portions of the Software.
-- 
-- THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
-- IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
-- FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
-- AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
-- LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
-- OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
-- THE SOFTWARE.
-- #L%
---
#version 1.1

ALTER TABLE SPECIMEN
ALTER COLUMN specimen_date date NULL

ALTER TABLE SPECIMEN ADD tumour_id varchar(100) DEFAULT (NULL)
ALTER TABLE SPECIMEN ADD preclin_id varchar(100) DEFAULT (NULL)

Create Function [dbo].[RemoveNonAlphaNumChars](@Temp VarChar(1000))
Returns VarChar(1000)
AS
Begin
    Declare @KeepValues as varchar(50)
    Set @KeepValues = '%[^a-z^0-9]%'
    While PatIndex(@KeepValues, @Temp) > 0
        Set @Temp = Stuff(@Temp, PatIndex(@KeepValues, @Temp), 1, '')
    Return @Temp
End


ALTER VIEW TUMOURNGS AS
with bl (baseline_number, specimen_id) as (select ROW_NUMBER() over(PARTITION BY person_id ORDER BY specimen_id) as baseline_number, specimen_id from SPECIMEN where specimen_concept_id=2 or specimen_concept_id=3)
SELECT 
specimen.person_id AS person_id,
specimen.specimen_id AS specimen_id,
specimen.specimen_date AS specimen_date,
geneName.gene_name AS gene_name,
geneVar.measurement_gene_variant_id as gene_varient_id,
geneVar.cdna_change AS cdna_change,
geneVar.amino_acid_change AS amino_acid_change,
geneVar.approximate_mutatnt_allele_frequency AS approximate_mutatnt_allele_frequency,
genePanel.average_read_depth AS average_read_depth,
genePanel.coverage AS coverage,
pathLab.path_lab_ref AS path_lab_ref,
gdlRequest.date_requested AS date_requested,
report.filename AS filename,
genePanel.comments AS comments,
genePanel.unknown_significance AS unknown_significance,
bl.baseline_number as baseline_number,
genePanel.run_number AS run_number,
genePanel.measurement_gene_panel_id AS measurement_gene_panel_id
FROM dbo.SPECIMEN AS specimen 
FULL OUTER JOIN dbo.MEASUREMENT_GENE_PANEL AS genePanel ON specimen.specimen_id = genePanel.specimen_id 
FULL OUTER JOIN dbo.PATH_LAB_REF AS pathLab ON genePanel.measurement_gene_panel_id = pathLab.measurement_gene_panel_id 
FULL OUTER JOIN dbo.MEASUREMENT_GENE_VARIANT AS geneVar ON geneVar.measurement_gene_panel_id = genePanel.measurement_gene_panel_id 
FULL OUTER JOIN dbo.CONCEPT_GENE AS geneName ON geneVar.gene_concept_id = geneName.gene_concept_id 
FULL OUTER JOIN dbo.CONCEPT_GENE_PANEL AS genePanelConcept ON genePanelConcept.gene_panel_concept_id = genePanel.gene_panel_concept_id 
FULL OUTER JOIN dbo.GDL_REQUEST AS gdlRequest ON specimen.specimen_id = gdlRequest.specimen_id 
FULL OUTER JOIN dbo.REPORT AS report ON genePanel.measurement_gene_panel_id = report.measurement_gene_panel_id 
FULL OUTER JOIN bl as bl ON bl.specimen_id=specimen.specimen_id
WHERE specimen.specimen_concept_id=2
OR specimen.specimen_concept_id=3


ALTER TABLE MEASUREMENT_GENE_VARIANT ADD 
cna_ratio decimal(5,2) NULL,
rearr_in_frame varchar(50) NULL,
rearr_gene_2 varchar(100) NULL,
rearr_pos1 varchar(100) NULL,
rearr_pos2 varchar(100) NULL

ALTER TABLE MEASUREMENT_GENE_PANEL ADD
tmb_score decimal(5,2) NULL,
tmb_status varchar(50) NULL,
tmb_unit varchar(50) NULL,
microsatellite_instability_score decimal(5,2) NULL,
microsatellite_instability_status varchar(50) NULL

INSERT into CONCEPT_GENE_PANEL (panel_name) VALUES ('foundationmedicine')

ALTER TABLE MEASUREMENT_GENE_PANEL ALTER COLUMN ingestion_date datetime 

ALTER TABLE MEASUREMENT_GENE_PANEL
ADD CONSTRAINT df_ingestion_date
DEFAULT (getdate()) FOR ingestion_date

ALTER TABLE MEASUREMENT_GENE_VARIANT ADD
number_of_exons varchar(50) NULL,
rearr_number_of_reads int NULL,
ncbi_nucleotide varchar(50) NULL

#filter out fm rearrangement data
ALTER VIEW SEARCH AS
select DISTINCT cc.condition_name,c.gene_name,mv.cdna_change,mv.amino_acid_change,p.target_id,p.person_id FROM PERSON p 
LEFT OUTER JOIN CONDITION_OCCURRENCE co on p.person_id=co.person_id
LEFT OUTER JOIN CONCEPT_CONDITION cc on co.condition_concept_id=cc.condition_concept_id
LEFT OUTER JOIN SPECIMEN s on p.person_id=s.person_id 
LEFT OUTER JOIN MEASUREMENT_GENE_PANEL mp on s.specimen_id=mp.specimen_id 
JOIN MEASUREMENT_GENE_VARIANT mv on mp.measurement_gene_panel_id=mv.measurement_gene_panel_id
LEFT OUTER join CONCEPT_GENE c on mv.gene_concept_id=c.gene_concept_id
where mv.gene_concept_id is not null

# keep foundation medicine data out of view
ALTER VIEW TUMOURNGS AS
with bl (baseline_number, specimen_id) as (select ROW_NUMBER() over(PARTITION BY person_id ORDER BY specimen_id) as baseline_number, specimen_id from SPECIMEN where specimen_concept_id=2 or specimen_concept_id=3)
SELECT 
specimen.person_id AS person_id,
specimen.specimen_id AS specimen_id,
specimen.specimen_date AS specimen_date,
geneName.gene_name AS gene_name,
geneVar.measurement_gene_variant_id as gene_varient_id,
geneVar.cdna_change AS cdna_change,
geneVar.amino_acid_change AS amino_acid_change,
geneVar.approximate_mutatnt_allele_frequency AS approximate_mutatnt_allele_frequency,
genePanel.average_read_depth AS average_read_depth,
genePanel.coverage AS coverage,
pathLab.path_lab_ref AS path_lab_ref,
gdlRequest.date_requested AS date_requested,
report.filename AS filename,
genePanel.comments AS comments,
genePanel.unknown_significance AS unknown_significance,
bl.baseline_number as baseline_number,
genePanel.run_number AS run_number,
genePanel.measurement_gene_panel_id AS measurement_gene_panel_id
FROM dbo.SPECIMEN AS specimen 
FULL OUTER JOIN dbo.MEASUREMENT_GENE_PANEL AS genePanel ON specimen.specimen_id = genePanel.specimen_id 
FULL OUTER JOIN dbo.PATH_LAB_REF AS pathLab ON genePanel.measurement_gene_panel_id = pathLab.measurement_gene_panel_id 
FULL OUTER JOIN dbo.MEASUREMENT_GENE_VARIANT AS geneVar ON geneVar.measurement_gene_panel_id = genePanel.measurement_gene_panel_id 
FULL OUTER JOIN dbo.CONCEPT_GENE AS geneName ON geneVar.gene_concept_id = geneName.gene_concept_id 
FULL OUTER JOIN dbo.CONCEPT_GENE_PANEL AS genePanelConcept ON genePanelConcept.gene_panel_concept_id = genePanel.gene_panel_concept_id 
FULL OUTER JOIN dbo.GDL_REQUEST AS gdlRequest ON specimen.specimen_id = gdlRequest.specimen_id 
FULL OUTER JOIN dbo.REPORT AS report ON genePanel.measurement_gene_panel_id = report.measurement_gene_panel_id 
FULL OUTER JOIN bl as bl ON bl.specimen_id=specimen.specimen_id
WHERE (specimen.specimen_concept_id=2
OR specimen.specimen_concept_id=3) AND genePanel.ngs_run != 'FM'

insert into COMPONENTS (roleID, component) VALUES (1, 'fmtumourtick')
insert into COMPONENTS (roleID, component) VALUES (2, 'fmtumourtick')
insert into COMPONENTS (roleID, component) VALUES (3, 'fmtumourtick')
insert into COMPONENTS (roleID, component) VALUES (1, 'fmbloodtick')
insert into COMPONENTS (roleID, component) VALUES (2, 'fmbloodtick')
insert into COMPONENTS (roleID, component) VALUES (3, 'fmbloodtick')
insert into COMPONENTS (roleID, component) VALUES (2, 'fmtumourcheckbox')
insert into COMPONENTS (roleID, component) VALUES (2, 'fmbloodcheckbox')

ALTER TABLE MEASUREMENT_GENE_PANEL ADD
median_exon_depth int NULL,
percent_exons_100x decimal(5,2) NULL

ALTER TABLE MEASUREMENT_GENE_VARIANT ALTER COLUMN variant_concept_id varchar(50)

ALTER VIEW CTDNA_SUBSETS AS
SELECT 
specimen.person_id AS person_id,
specimen.specimen_id AS specimen_id,
specimen.specimen_concept_id AS specimen_concept_id,
specimen.specimen_date AS specimen_date,
geneVar.measurement_gene_variant_id as gene_varient_id,
geneVar.ngs_gene_present  AS ngs_gene_present,
geneVar.cdna_change  AS cdna_change,
geneName.gene_name  AS gene_name,
geneVar.amino_acid_change  AS amino_acid_change,
geneVar.cfdna_frequency AS cfdna_frequency,
geneVar.total_cfdna_reads AS total_cfdna_reads,
geneVar.germline_frequency AS germline_frequency,
geneVar.mutation_type AS mutation_type,
geneVar.high_confidence AS high_confidence,
geneVar.is_specific_mutation_in_panel AS is_specific_mutation_in_panel,
geneVar.sv_genome_position as sv_genome_position,
genePanel.ngs_library_cfdna_input AS ngs_library_cfdna_input,
genePanel.cfdna_input_colour_key AS cfdna_input_colour_key,
genePanel.average_read_depth AS average_read_depth,
genePanel.average_read_depth_colour_key AS average_read_depth_colour_key,
genePanel.ngs_run AS ngs_run,
genePanel.ngs_comment AS ngs_comment,
genePanel.exploratory_comment AS exploratory_comment,
genePanelConcept.panel_name AS panel_name,
genePanel.bioinformatics_pipeline AS bioinformatics_pipeline,
genePanel.level_of_detection AS level_of_detection,
genePanel.analysis_failed AS analysis_failed,
genePanel.no_mutations_found AS no_mutations_found,
genePanel.run_number AS run_number,
specimen.baseline_number AS baseline_number,
genePanel.measurement_gene_panel_id AS measurement_gene_panel_id
FROM dbo.SPECIMEN AS specimen 
FULL OUTER JOIN dbo.MEASUREMENT_GENE_PANEL AS genePanel ON specimen.specimen_id = genePanel.specimen_id 
FULL OUTER JOIN dbo.MEASUREMENT_GENE_VARIANT AS geneVar ON geneVar.measurement_gene_panel_id = genePanel.measurement_gene_panel_id 
FULL OUTER JOIN dbo.CONCEPT_GENE AS geneName ON geneVar.gene_concept_id = geneName.gene_concept_id 
FULL OUTER JOIN dbo.CONCEPT_GENE_PANEL AS genePanelConcept ON genePanelConcept.gene_panel_concept_id = genePanel.gene_panel_concept_id
LEFT JOIN dbo.CONCEPT_GENE_PANEL on genePanel.gene_panel_concept_id=CONCEPT_GENE_PANEL.gene_panel_concept_id 
WHERE specimen.specimen_concept_id=1 and CONCEPT_GENE_PANEL.panel_name!='foundationmedicine'

/*ADD csmic url*/

ALTER VIEW CTDNA_SUBSETS AS
SELECT 
specimen.person_id AS person_id,
specimen.specimen_id AS specimen_id,
specimen.specimen_concept_id AS specimen_concept_id,
specimen.specimen_date AS specimen_date,
geneVar.measurement_gene_variant_id as gene_varient_id,
geneVar.ngs_gene_present  AS ngs_gene_present,
geneVar.cdna_change  AS cdna_change,
geneName.gene_name  AS gene_name,
geneVar.amino_acid_change  AS amino_acid_change,
geneVar.cfdna_frequency AS cfdna_frequency,
geneVar.total_cfdna_reads AS total_cfdna_reads,
geneVar.germline_frequency AS germline_frequency,
geneVar.mutation_type AS mutation_type,
geneVar.high_confidence AS high_confidence,
geneVar.is_specific_mutation_in_panel AS is_specific_mutation_in_panel,
geneVar.sv_genome_position as sv_genome_position,
geneVar.cosmic_url as cosmic_url,
genePanel.ngs_library_cfdna_input AS ngs_library_cfdna_input,
genePanel.cfdna_input_colour_key AS cfdna_input_colour_key,
genePanel.average_read_depth AS average_read_depth,
genePanel.average_read_depth_colour_key AS average_read_depth_colour_key,
genePanel.ngs_run AS ngs_run,
genePanel.ngs_comment AS ngs_comment,
genePanel.exploratory_comment AS exploratory_comment,
genePanelConcept.panel_name AS panel_name,
genePanel.bioinformatics_pipeline AS bioinformatics_pipeline,
genePanel.level_of_detection AS level_of_detection,
genePanel.analysis_failed AS analysis_failed,
genePanel.no_mutations_found AS no_mutations_found,
genePanel.run_number AS run_number,
specimen.baseline_number AS baseline_number,
genePanel.measurement_gene_panel_id AS measurement_gene_panel_id
FROM dbo.SPECIMEN AS specimen 
FULL OUTER JOIN dbo.MEASUREMENT_GENE_PANEL AS genePanel ON specimen.specimen_id = genePanel.specimen_id 
FULL OUTER JOIN dbo.MEASUREMENT_GENE_VARIANT AS geneVar ON geneVar.measurement_gene_panel_id = genePanel.measurement_gene_panel_id 
FULL OUTER JOIN dbo.CONCEPT_GENE AS geneName ON geneVar.gene_concept_id = geneName.gene_concept_id 
FULL OUTER JOIN dbo.CONCEPT_GENE_PANEL AS genePanelConcept ON genePanelConcept.gene_panel_concept_id = genePanel.gene_panel_concept_id
LEFT JOIN dbo.CONCEPT_GENE_PANEL on genePanel.gene_panel_concept_id=CONCEPT_GENE_PANEL.gene_panel_concept_id 
WHERE specimen.specimen_concept_id=1 and CONCEPT_GENE_PANEL.panel_name!='foundationmedicine'

/*eTarget Version 1.3*/
/*clean up CONCEPT_CONDITION table*/

delete from CONCEPT_CONDITION where condition_concept_id
not in (select min(condition_concept_id) as condition_concept_id from CONCEPT_CONDITION GROUP by condition_name) 
and condition_concept_id not in (
select distinct condition_concept_id from CONDITION_OCCURRENCE
)

INSERT INTO CONCEPT_CONDITION (condition_concept_id, condition_name) VALUES((SELECT ISNULL(MAX(condition_concept_id),0)+1 FROM CONCEPT_CONDITION ccon), 'Adrenocortical')
INSERT INTO CONCEPT_CONDITION (condition_concept_id, condition_name) VALUES((SELECT ISNULL(MAX(condition_concept_id),0)+1 FROM CONCEPT_CONDITION ccon), 'Breast')
INSERT INTO CONCEPT_CONDITION (condition_concept_id, condition_name) VALUES((SELECT ISNULL(MAX(condition_concept_id),0)+1 FROM CONCEPT_CONDITION ccon), 'Cancer of Unknown Primary')
INSERT INTO CONCEPT_CONDITION (condition_concept_id, condition_name) VALUES((SELECT ISNULL(MAX(condition_concept_id),0)+1 FROM CONCEPT_CONDITION ccon), 'Upper GI')
INSERT INTO CONCEPT_CONDITION (condition_concept_id, condition_name) VALUES((SELECT ISNULL(MAX(condition_concept_id),0)+1 FROM CONCEPT_CONDITION ccon), 'Hepatobiliary')
INSERT INTO CONCEPT_CONDITION (condition_concept_id, condition_name) VALUES((SELECT ISNULL(MAX(condition_concept_id),0)+1 FROM CONCEPT_CONDITION ccon), 'Colorectal')
INSERT INTO CONCEPT_CONDITION (condition_concept_id, condition_name) VALUES((SELECT ISNULL(MAX(condition_concept_id),0)+1 FROM CONCEPT_CONDITION ccon), 'GI - Other')
INSERT INTO CONCEPT_CONDITION (condition_concept_id, condition_name) VALUES((SELECT ISNULL(MAX(condition_concept_id),0)+1 FROM CONCEPT_CONDITION ccon), 'Anogenital')
INSERT INTO CONCEPT_CONDITION (condition_concept_id, condition_name) VALUES((SELECT ISNULL(MAX(condition_concept_id),0)+1 FROM CONCEPT_CONDITION ccon), 'Urological')
INSERT INTO CONCEPT_CONDITION (condition_concept_id, condition_name) VALUES((SELECT ISNULL(MAX(condition_concept_id),0)+1 FROM CONCEPT_CONDITION ccon), 'Female reproductive organs')
INSERT INTO CONCEPT_CONDITION (condition_concept_id, condition_name) VALUES((SELECT ISNULL(MAX(condition_concept_id),0)+1 FROM CONCEPT_CONDITION ccon), 'Head and Neck')
INSERT INTO CONCEPT_CONDITION (condition_concept_id, condition_name) VALUES((SELECT ISNULL(MAX(condition_concept_id),0)+1 FROM CONCEPT_CONDITION ccon), 'Melanoma')
INSERT INTO CONCEPT_CONDITION (condition_concept_id, condition_name) VALUES((SELECT ISNULL(MAX(condition_concept_id),0)+1 FROM CONCEPT_CONDITION ccon), 'Neuroendocrine')
INSERT INTO CONCEPT_CONDITION (condition_concept_id, condition_name) VALUES((SELECT ISNULL(MAX(condition_concept_id),0)+1 FROM CONCEPT_CONDITION ccon), 'Lung')
INSERT INTO CONCEPT_CONDITION (condition_concept_id, condition_name) VALUES((SELECT ISNULL(MAX(condition_concept_id),0)+1 FROM CONCEPT_CONDITION ccon), 'Sarcoma')
INSERT INTO CONCEPT_CONDITION (condition_concept_id, condition_name) VALUES((SELECT ISNULL(MAX(condition_concept_id),0)+1 FROM CONCEPT_CONDITION ccon), 'Thymic')
INSERT INTO CONCEPT_CONDITION (condition_concept_id, condition_name) VALUES((SELECT ISNULL(MAX(condition_concept_id),0)+1 FROM CONCEPT_CONDITION ccon), 'Thyroid')
INSERT INTO CONCEPT_CONDITION (condition_concept_id, condition_name) VALUES((SELECT ISNULL(MAX(condition_concept_id),0)+1 FROM CONCEPT_CONDITION ccon), 'Other')

create table CONCEPT_CONDITION_SUBTYPE(
subtype_concept_id int IDENTITY(1,1) PRIMARY KEY,
subtype_name varchar(255) not null,
condition_concept_id int not null,
vocabulary_id varchar(20)
CONSTRAINT [fk_CONCEPT_CONDITION_SUBTYPE_CONCEPT_CONDITION] FOREIGN KEY(condition_concept_id)
REFERENCES [dbo].[CONCEPT_CONDITION] ([condition_concept_id]))

INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Adrenocortical',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Adrenocortical'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Breast',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Breast'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('CUP',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Cancer of Unknown Primary'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Oesophageal',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Upper GI'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Gastroesophageal junction',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Upper GI'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Stomach',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Upper GI'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Duodenal',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Upper GI'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Liver',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Hepatobiliary'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Pancreatic',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Hepatobiliary'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Cholangiocarcinoma',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Hepatobiliary'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Gall bladder',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Hepatobiliary'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Ampullary',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Hepatobiliary'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Colorectal',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Colorectal'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Small bowel',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='GI - Other'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Appendiceal',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='GI - Other'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Pseudomyxoma peritonei',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='GI - Other'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Anal',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Anogenital'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Penile',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Anogenital'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Vaginal',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Anogenital'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Vulval',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Anogenital'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Bladder',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Urological'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Renal',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Urological'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Prostate',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Urological'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Testis',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Urological'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Ovarian',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Female reproductive organs'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Endometrial',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Female reproductive organs'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Cervical',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Female reproductive organs'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Adenoid cystic',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Head and Neck'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('SCCHN',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Head and Neck'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Salivary gland',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Head and Neck'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Melanoma',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Melanoma'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Neuroendocrine',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Neuroendocrine'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('NSCLC',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Lung'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('SCLC',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Lung'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Mesothelioma',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Lung'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Sarcoma: Soft tissue',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Sarcoma'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Sarcoma: Bone',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Sarcoma'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Thymoma',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Thymic'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Thymic carcinoma',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Thymic'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Papillary',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Thyroid'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Medullary',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Thyroid'))
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES('Anaplastic',(select top 1 condition_concept_id from CONCEPT_CONDITION where condition_name='Thyroid'))

ALTER TABLE CONDITION_OCCURRENCE ADD condition_subtype_concept_id int NULL

ALTER TABLE CONDITION_OCCURRENCE ADD additional_details varchar(512) NULL

ALTER TABLE CONDITION_OCCURRENCE
ADD CONSTRAINT FK_CONDITION_OCCURRENCE_CONCEPT_CONDITION_SUBTYPE
FOREIGN KEY (condition_subtype_concept_id) REFERENCES CONCEPT_CONDITION_SUBTYPE (subtype_concept_id)

/* new view to populate baseline for FM tumour data*/

CREATE VIEW FMTUMOUR AS
with bl (baseline_number, specimen_id) as (select ROW_NUMBER() over(PARTITION BY person_id ORDER BY specimen_id) as baseline_number, specimen_id from SPECIMEN where specimen_concept_id=2 or specimen_concept_id=3)
SELECT 
specimen.person_id,
geneVar.measurement_gene_variant_id,
geneVar.measurement_gene_panel_id,
geneName.gene_name,
cdna_change, total_cfdna_reads, cfdna_frequency, sv_genome_position, amino_acid_change, ncbi_nucleotide, variant_concept_id, somatic_status,
bl.baseline_number,
copy_number, number_of_exons, cna_ratio, cna_type,
rearr_description, rearr_in_frame,rearr_pos1, rearr_pos2, rearr_number_of_reads,cg2.gene_name as rearr_gene_2_name,cg1.gene_name as rearr_gene_1_name
FROM dbo.SPECIMEN AS specimen 
FULL OUTER JOIN dbo.MEASUREMENT_GENE_PANEL AS genePanel ON specimen.specimen_id = genePanel.specimen_id 
FULL OUTER JOIN dbo.PATH_LAB_REF AS pathLab ON genePanel.measurement_gene_panel_id = pathLab.measurement_gene_panel_id 
FULL OUTER JOIN dbo.MEASUREMENT_GENE_VARIANT AS geneVar ON geneVar.measurement_gene_panel_id = genePanel.measurement_gene_panel_id 
FULL OUTER JOIN dbo.CONCEPT_GENE AS geneName ON geneVar.gene_concept_id = geneName.gene_concept_id 
FULL OUTER JOIN dbo.CONCEPT_GENE_PANEL AS genePanelConcept ON genePanelConcept.gene_panel_concept_id = genePanel.gene_panel_concept_id 
FULL OUTER JOIN dbo.GDL_REQUEST AS gdlRequest ON specimen.specimen_id = gdlRequest.specimen_id 
FULL OUTER JOIN dbo.REPORT AS report ON genePanel.measurement_gene_panel_id = report.measurement_gene_panel_id 
FULL OUTER JOIN bl ON bl.specimen_id=specimen.specimen_id
LEFT JOIN CONCEPT_GENE as cg1 on geneVar.rearr_gene_1=cg1.gene_concept_id
LEFT JOIN CONCEPT_GENE as cg2 on geneVar.rearr_gene_2=cg2.gene_concept_id 
WHERE (specimen.specimen_concept_id=2
OR specimen.specimen_concept_id=3) AND genePanel.ngs_run = 'FM'

/* add subtype to views TAR-628*/

ALTER view PERSON_CONDITIONS_V as 
select p.person_id, o.condition_start_date, c.condition_name, s.subtype_name
  from person p 
  left outer join condition_occurrence o on p.person_id=o.person_id
  left join concept_condition c on o.condition_concept_id=c.condition_concept_id
  left join CONCEPT_CONDITION_SUBTYPE s on o.condition_subtype_concept_id=s.subtype_concept_id


ALTER VIEW PATIENTS AS
SELECT PERSON.person_id AS person_id, 
PERSON.target_id AS target_id, 
PERSON.consent_date AS consent_date, 
PERSON.age_at_consent AS age_at_consent,
PERSON_CONDITIONS_V.condition_name AS condition_name, 
PERSON_CONDITIONS_V.subtype_name AS subtype_name,
CONCEPT_CONSULTANT.name AS consultant_name,
PERSON_CONDITIONS_V.condition_start_date AS condition_start_date,
CONCEPT_GENDER.gender_name AS gender_name
FROM dbo.PERSON, dbo.PERSON_CONDITIONS_V, dbo.CONCEPT_CONSULTANT, dbo.CONCEPT_GENDER
WHERE PERSON.person_id = PERSON_CONDITIONS_V.person_id 
AND PERSON.consultant_concept_id = CONCEPT_CONSULTANT.consultant_concept_id 
AND PERSON.gender_concept_id = CONCEPT_GENDER.gender_concept_id
AND PERSON.person_id > 0 
AND PERSON.target_id LIKE '%TAR%';

/* eTarget Version 1.3 */
/* update search for FM data */

ALTER VIEW SEARCH AS
select DISTINCT cc.condition_name,c.gene_name,mv.variant_concept_id,mv.cdna_change,mv.amino_acid_change,g1.gene_name as rearr_gene1, g2.gene_name as rearr_gene2, mv.rearr_description, mv.cna_type, p.target_id,p.person_id 
FROM PERSON p 
LEFT OUTER JOIN CONDITION_OCCURRENCE co on p.person_id=co.person_id
LEFT OUTER JOIN CONCEPT_CONDITION cc on co.condition_concept_id=cc.condition_concept_id
LEFT OUTER JOIN SPECIMEN s on p.person_id=s.person_id 
LEFT OUTER JOIN MEASUREMENT_GENE_PANEL mp on s.specimen_id=mp.specimen_id 
JOIN MEASUREMENT_GENE_VARIANT mv on mp.measurement_gene_panel_id=mv.measurement_gene_panel_id
LEFT OUTER join CONCEPT_GENE c on mv.gene_concept_id=c.gene_concept_id
LEFT OUTER join CONCEPT_GENE g1 on mv.rearr_gene_1=g1.gene_concept_id
LEFT OUTER join CONCEPT_GENE g2 on mv.rearr_gene_2=g2.gene_concept_id
/*step two*/
ALTER VIEW SEARCH AS
select DISTINCT cc.condition_name, ccs.subtype_name, c.gene_name,mv.variant_concept_id,mv.cdna_change,mv.amino_acid_change,g1.gene_name as rearr_gene1, g2.gene_name as rearr_gene2, mv.rearr_description, mv.cna_type, p.target_id,p.person_id 
FROM PERSON p 
LEFT OUTER JOIN CONDITION_OCCURRENCE co on p.person_id=co.person_id
LEFT OUTER JOIN CONCEPT_CONDITION cc on co.condition_concept_id=cc.condition_concept_id
LEFT OUTER JOIN CONCEPT_CONDITION_SUBTYPE ccs on co.condition_subtype_concept_id=ccs.subtype_concept_id
LEFT OUTER JOIN SPECIMEN s on p.person_id=s.person_id 
LEFT OUTER JOIN MEASUREMENT_GENE_PANEL mp on s.specimen_id=mp.specimen_id 
JOIN MEASUREMENT_GENE_VARIANT mv on mp.measurement_gene_panel_id=mv.measurement_gene_panel_id
LEFT OUTER join CONCEPT_GENE c on mv.gene_concept_id=c.gene_concept_id
LEFT OUTER join CONCEPT_GENE g1 on mv.rearr_gene_1=g1.gene_concept_id
LEFT OUTER join CONCEPT_GENE g2 on mv.rearr_gene_2=g2.gene_concept_id


/* eTarget Version 1.5 */

CREATE TABLE IHC_REPORT (
	ihc_report_id int IDENTITY(1,1) PRIMARY KEY,
	person_id int NOT NULL,
	specimen_id int NOT NULL,
	sample_received_date date,
	report_date date,
	cd3_total_tissue_area decimal(16,10),
	cd3_intratumoural  decimal(16,10),
	cd3_intrastromal  decimal(16,10),
	cd8_total_tissue_area  decimal(16,10),
	cd8_intratumoural  decimal(16,10),
	cd8_intrastromal  decimal(16,10),
	pdl1_tps varchar(25),
	estimated_results varchar(25),
	comments text,
	ingestion_date datetime default CURRENT_TIMESTAMP, 
	CONSTRAINT FK_IHC_REPORT_PERSON FOREIGN KEY (person_id) REFERENCES PERSON(person_id),
	CONSTRAINT FK_IHC_REPORT_SPECIMEN FOREIGN KEY (specimen_id) REFERENCES SPECIMEN(specimen_id)
)

INSERT INTO ENDPOINTS (roleID, endpoint) VALUES (1,'IHCReport')
INSERT INTO ENDPOINTS (roleID, endpoint) VALUES (2,'IHCReport')
INSERT INTO ENDPOINTS (roleID, endpoint) VALUES (3,'IHCReport')

/* eTarget Version 1.6 */

alter table CONCEPT_GENE_PANEL add  valid_start_date date

UPDATE CONCEPT_GENE_PANEL set valid_start_date='1970-01-01' where gene_panel_concept_id=2

CREATE VIEW TUMOUR_SPECIMEN_BASELINE AS
with issue_dates as
(SELECT distinct min(date_issued) as date_issued, specimen.specimen_id
FROM dbo.SPECIMEN AS specimen FULL OUTER JOIN dbo.MEASUREMENT_GENE_PANEL AS genePanel ON specimen.specimen_id = genePanel.specimen_id 
FULL OUTER JOIN dbo.REPORT AS report ON genePanel.measurement_gene_panel_id = report.measurement_gene_panel_id FULL OUTER JOIN dbo.CONCEPT_SPECIMEN AS conceptSpec ON specimen.specimen_concept_id = conceptSpec.specimen_concept_id FULL 
OUTER JOIN dbo.GDL_REQUEST AS gdlRequest ON specimen.specimen_id = gdlRequest.specimen_id 
WHERE conceptSpec.specimen_name LIKE '%Tumour%' AND ngs_run!='FM' group by specimen.specimen_id)
select ROW_NUMBER() over(PARTITION BY person_id ORDER BY coalesce(issue_dates.date_issued, '9999-12-12') ASC, gdlRequest.date_requested ASC) as baseline_number, SPECIMEN.specimen_id, person_id
FROM dbo.SPECIMEN AS specimen
FULL OUTER JOIN dbo.GDL_REQUEST AS gdlRequest ON gdlRequest.specimen_id = specimen.specimen_id
FULL OUTER JOIN issue_dates on issue_dates.specimen_id=SPECIMEN.specimen_id
WHERE (specimen_concept_id=2 or specimen_concept_id=3)

ALTER VIEW TUMOURNGS AS
SELECT 
specimen.person_id AS person_id,
specimen.specimen_id AS specimen_id,
specimen.specimen_date AS specimen_date,
geneName.gene_name AS gene_name,
geneVar.measurement_gene_variant_id as gene_varient_id,
geneVar.cdna_change AS cdna_change,
geneVar.amino_acid_change AS amino_acid_change,
geneVar.approximate_mutatnt_allele_frequency AS approximate_mutatnt_allele_frequency,
genePanel.average_read_depth AS average_read_depth,
genePanel.coverage AS coverage,
pathLab.path_lab_ref AS path_lab_ref,
gdlRequest.date_requested AS date_requested,
report.filename AS filename,
genePanel.comments AS comments,
genePanel.unknown_significance AS unknown_significance,
bl.baseline_number as baseline_number,
genePanel.run_number AS run_number,
genePanel.measurement_gene_panel_id AS measurement_gene_panel_id
FROM dbo.SPECIMEN AS specimen 
FULL OUTER JOIN dbo.MEASUREMENT_GENE_PANEL AS genePanel ON specimen.specimen_id = genePanel.specimen_id 
FULL OUTER JOIN dbo.PATH_LAB_REF AS pathLab ON genePanel.measurement_gene_panel_id = pathLab.measurement_gene_panel_id 
FULL OUTER JOIN dbo.MEASUREMENT_GENE_VARIANT AS geneVar ON geneVar.measurement_gene_panel_id = genePanel.measurement_gene_panel_id 
FULL OUTER JOIN dbo.CONCEPT_GENE AS geneName ON geneVar.gene_concept_id = geneName.gene_concept_id 
FULL OUTER JOIN dbo.CONCEPT_GENE_PANEL AS genePanelConcept ON genePanelConcept.gene_panel_concept_id = genePanel.gene_panel_concept_id 
FULL OUTER JOIN dbo.GDL_REQUEST AS gdlRequest ON specimen.specimen_id = gdlRequest.specimen_id 
FULL OUTER JOIN dbo.REPORT AS report ON genePanel.measurement_gene_panel_id = report.measurement_gene_panel_id 
FULL OUTER JOIN TUMOUR_SPECIMEN_BASELINE as bl ON bl.specimen_id=specimen.specimen_id
WHERE (specimen.specimen_concept_id=2
OR specimen.specimen_concept_id=3) AND genePanel.ngs_run != 'FM'

/*drop VIEW PERSON_DRUGS_V
drop view PERSON_notes_V
drop view person_latest_drug_v
drop view person_latest_note_v*/

Alter VIEW SEARCH as
select DISTINCT cc.condition_name, ccs.subtype_name, c.gene_name,mv.variant_concept_id,mv.cdna_change,mv.amino_acid_change,g1.gene_name as rearr_gene1, g2.gene_name as rearr_gene2, mv.rearr_description, mv.cna_type, p.target_id,p.person_id, 
(CASE mv.somatic_status WHEN 'unknown' THEN 1 ELSE 0 END) as 'unknown_significant'
FROM PERSON p 
LEFT OUTER JOIN CONDITION_OCCURRENCE co on p.person_id=co.person_id
LEFT OUTER JOIN CONCEPT_CONDITION cc on co.condition_concept_id=cc.condition_concept_id
LEFT OUTER JOIN CONCEPT_CONDITION_SUBTYPE ccs on co.condition_subtype_concept_id=ccs.subtype_concept_id
LEFT OUTER JOIN SPECIMEN s on p.person_id=s.person_id 
LEFT OUTER JOIN MEASUREMENT_GENE_PANEL mp on s.specimen_id=mp.specimen_id 
JOIN MEASUREMENT_GENE_VARIANT mv on mp.measurement_gene_panel_id=mv.measurement_gene_panel_id
LEFT OUTER join CONCEPT_GENE c on mv.gene_concept_id=c.gene_concept_id
LEFT OUTER join CONCEPT_GENE g1 on mv.rearr_gene_1=g1.gene_concept_id
LEFT OUTER join CONCEPT_GENE g2 on mv.rearr_gene_2=g2.gene_concept_id

create view GENE_SEARCH as
with t1 as
(select gene_name, min(unknown_significant) as unknown_significant from SEARCH where gene_name is not null GROUP by gene_name 
UNION
select rearr_gene1 as gene_name,  min(unknown_significant) as unknown_significant from SEARCH where rearr_gene1 is not null GROUP by rearr_gene1
UNION
SELECT rearr_gene2 as gene_name, min(unknown_significant) as unknown_significant from SEARCH where rearr_gene2 is not null GROUP by rearr_gene2 )
select gene_name, min(unknown_significant) as unknown_significant from t1 GROUP by gene_name


INSERT INTO USER_ROLES (roleID, roleName) VALUES (6, 'Chief Investigator')

insert into ENDPOINTS (roleID, endpoint) VALUES (6, 'GetAllPatients')
insert into ENDPOINTS (roleID, endpoint) VALUES (6, 'SystemStatus')
insert into ENDPOINTS (roleID, endpoint) VALUES (6, 'GetSinglePatient')
insert into ENDPOINTS (roleID, endpoint) VALUES (6, 'GeneralInfo')
insert into ENDPOINTS (roleID, endpoint) VALUES (6, 'TumourNGS')
insert into ENDPOINTS (roleID, endpoint) VALUES (6, 'CTDNANGSSubset')
insert into ENDPOINTS (roleID, endpoint) VALUES (6, 'CTDNAExploratory')
insert into ENDPOINTS (roleID, endpoint) VALUES (6, 'PDXCDX')
insert into ENDPOINTS (roleID, endpoint) VALUES (6, 'GetMeetingOutcome')
insert into ENDPOINTS (roleID, endpoint) VALUES (6, 'PostMutationSelection')
insert into ENDPOINTS (roleID, endpoint) VALUES (6, 'GetMutationSelection')
insert into ENDPOINTS (roleID, endpoint) VALUES (6, 'PostMeetingOutcome')
insert into ENDPOINTS (roleID, endpoint) VALUES (6, 'DeletePatient')
insert into ENDPOINTS (roleID, endpoint) VALUES (6, 'IHCReport')

insert into COMPONENTS (roleID, component) VALUES (6, 'patienttable')
insert into COMPONENTS (roleID, component) VALUES (6, 'patienttable_search')
insert into COMPONENTS (roleID, component) VALUES (6, 'meetingoutcome')
insert into COMPONENTS (roleID, component) VALUES (6, 'editmeetingoutcome')
insert into COMPONENTS (roleID, component) VALUES (6, 'reportextraction')
insert into COMPONENTS (roleID, component) VALUES (6, 'tumourcheckbox')
insert into COMPONENTS (roleID, component) VALUES (6, 'ctdnangscheckbox')
insert into COMPONENTS (roleID, component) VALUES (6, 'ctdnaexpcheckbox')
insert into COMPONENTS (roleID, component) VALUES (6, 'ctdnangstick')
insert into COMPONENTS (roleID, component) VALUES (6, 'ctdnaexptick')
insert into COMPONENTS (roleID, component) VALUES (6, 'tumourtick')
insert into COMPONENTS (roleID, component) VALUES (6, 'fmbloodtick')
insert into COMPONENTS (roleID, component) VALUES (6, 'fmtumourtick')
insert into COMPONENTS (roleID, component) VALUES (6, 'fmtumourcheckbox')
insert into COMPONENTS (roleID, component) VALUES (6, 'fmbloodcheckbox')

CREATE PROC DELETE_PERSON_PROC @person_id int
AS
BEGIN
	BEGIN TRANSACTION;
	BEGIN TRY
		declare @ErrorMessage nvarchar(max)
		declare @ErrorSeverity int
		declare @ErrorState int
		DELETE from SELECTED_GENE_VARIANT WHERE person_id=@person_id;
		DELETE FROM MEETING_OUTCOME_GENE_VARIANT where mo_id in (select mo_id from MEETING_OUTCOME WHERE person_id=@person_id);
		delete FROM MEASUREMENT_GENE_VARIANT where measurement_gene_variant_id in (select measurement_gene_variant_id from SPECIMEN LEFT JOIN MEASUREMENT_GENE_PANEL on SPECIMEN.specimen_id=MEASUREMENT_GENE_PANEL.specimen_id 
		LEFT JOIN MEASUREMENT_GENE_VARIANT on MEASUREMENT_GENE_PANEL.measurement_gene_panel_id=MEASUREMENT_GENE_VARIANT.measurement_gene_panel_id
		where SPECIMEN.person_id=@person_id and MEASUREMENT_GENE_VARIANT.measurement_gene_variant_id is not null);
		DELETE from REPORT where measurement_gene_panel_id in (select measurement_gene_panel_id from SPECIMEN LEFT JOIN MEASUREMENT_GENE_PANEL on SPECIMEN.specimen_id=MEASUREMENT_GENE_PANEL.specimen_id
		where SPECIMEN.person_id=@person_id and measurement_gene_panel_id is not null);
		DELETE FROM PATH_LAB_REF where specimen_id in (select specimen_id FROM SPECIMEN where SPECIMEN.person_id=@person_id);
		DELETE FROM MEASUREMENT_GENE_PANEL where specimen_id in (select specimen_id FROM SPECIMEN where SPECIMEN.person_id=@person_id);
		DELETE FROM GDL_REQUEST where specimen_id in (select specimen_id FROM SPECIMEN where SPECIMEN.person_id=@person_id);
		DELETE FROM IHC_REPORT where person_id=@person_id;
		DELETE FROM CHANGE_LOG where table_name='MEETING_OUTCOME' and reference_id in (select mo_id from MEETING_OUTCOME WHERE person_id=@person_id);
		DELETE FROM MEETING_OUTCOME where person_id=@person_id;
		DELETE from PDXCDX WHERE person_id=@person_id;
		DELETE FROM PROCEDURE_OCCURRENCE where person_id=@person_id;
		DELETE FROM SPECIMEN WHERE person_id=@person_id;
		DELETE from CONDITION_OCCURRENCE where person_id=@person_id;
		DELETE from EDIT_LOCK where person_id=@person_id;
		DELETE FROM PERSON where person_id=@person_id;
	END TRY
	BEGIN CATCH
		IF @@TRANCOUNT > 0
		BEGIN
			ROLLBACK TRANSACTION;
			SET @ErrorMessage  = ERROR_MESSAGE()
		     SET @ErrorSeverity = ERROR_SEVERITY()
		     SET @ErrorState    = ERROR_STATE()
		     RAISERROR(@ErrorMessage, @ErrorSeverity, @ErrorState)
		END
	END CATCH
	COMMIT TRANSACTION
END
GO

CREATE VIEW LATEST_MEASUREMENT_GENE_PANEL_IDS as (
select specimen_id, measurement_gene_panel_id from (SELECT TUMOUR_SPECIMEN_BASELINE.specimen_id, MEASUREMENT_GENE_PANEL.measurement_gene_panel_id,
ROW_NUMBER() over(PARTITION BY TUMOUR_SPECIMEN_BASELINE.person_id, TUMOUR_SPECIMEN_BASELINE.specimen_id, MEASUREMENT_GENE_PANEL.gene_panel_concept_id  ORDER BY MEASUREMENT_GENE_PANEL.ingestion_date desc, MEASUREMENT_GENE_PANEL.measurement_gene_panel_id desc) as version
from TUMOUR_SPECIMEN_BASELINE
LEFT JOIN MEASUREMENT_GENE_PANEL on TUMOUR_SPECIMEN_BASELINE.specimen_id=MEASUREMENT_GENE_PANEL.specimen_id
where measurement_gene_panel_id is not null) as t1 where version=1
union
select specimen_id, measurement_gene_panel_id from (SELECT SPECIMEN.specimen_id, MEASUREMENT_GENE_PANEL.measurement_gene_panel_id,
ROW_NUMBER() over(PARTITION BY SPECIMEN.person_id, SPECIMEN.specimen_id, SPECIMEN.baseline_number, MEASUREMENT_GENE_PANEL.run_number, MEASUREMENT_GENE_PANEL.gene_panel_concept_id ORDER BY MEASUREMENT_GENE_PANEL.ingestion_date desc, MEASUREMENT_GENE_PANEL.measurement_gene_panel_id desc) as version
from SPECIMEN
LEFT JOIN MEASUREMENT_GENE_PANEL on SPECIMEN.specimen_id=MEASUREMENT_GENE_PANEL.specimen_id
where SPECIMEN.specimen_concept_id=1 and MEASUREMENT_GENE_PANEL.measurement_gene_panel_id is not null) as t2
where version=1)


ALTER VIEW SEARCH as
select DISTINCT cc.condition_name, ccs.subtype_name, c.gene_name,mv.variant_concept_id,mv.cdna_change,mv.amino_acid_change,g1.gene_name as rearr_gene1, g2.gene_name as rearr_gene2, mv.rearr_description, mv.cna_type, p.target_id,p.person_id, 
(CASE mv.somatic_status WHEN 'unknown' THEN 1 ELSE 0 END) as 'unknown_significant'
FROM PERSON p 
LEFT OUTER JOIN CONDITION_OCCURRENCE co on p.person_id=co.person_id
LEFT OUTER JOIN CONCEPT_CONDITION cc on co.condition_concept_id=cc.condition_concept_id
LEFT OUTER JOIN CONCEPT_CONDITION_SUBTYPE ccs on co.condition_subtype_concept_id=ccs.subtype_concept_id
LEFT OUTER JOIN SPECIMEN s on p.person_id=s.person_id 
LEFT JOIN LATEST_MEASUREMENT_GENE_PANEL_IDS on s.specimen_id=LATEST_MEASUREMENT_GENE_PANEL_IDS.specimen_id
LEFT JOIN MEASUREMENT_GENE_PANEL mp on LATEST_MEASUREMENT_GENE_PANEL_IDS.measurement_gene_panel_id=mp.measurement_gene_panel_id 
JOIN MEASUREMENT_GENE_VARIANT mv on mp.measurement_gene_panel_id=mv.measurement_gene_panel_id
LEFT OUTER join CONCEPT_GENE c on mv.gene_concept_id=c.gene_concept_id
LEFT OUTER join CONCEPT_GENE g1 on mv.rearr_gene_1=g1.gene_concept_id
LEFT OUTER join CONCEPT_GENE g2 on mv.rearr_gene_2=g2.gene_concept_id
