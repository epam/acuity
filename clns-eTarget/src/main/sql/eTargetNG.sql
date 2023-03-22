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
/* SQL updates for new multi site developments*/
/* v2.0 Open Source */

create table CARE_SITE(
care_site_id int PRIMARY KEY,
care_site_name varchar(255)
)

ALTER TABLE PERSON ADD care_site_id int, FOREIGN KEY(care_site_id) REFERENCES CARE_SITE(care_site_id)

ALTER VIEW PATIENTS AS
SELECT PERSON.person_id AS person_id, 
PERSON.target_id AS target_id, 
PERSON.consent_date AS consent_date, 
PERSON.age_at_consent AS age_at_consent,
PERSON_CONDITIONS_V.condition_name AS condition_name, 
PERSON_CONDITIONS_V.subtype_name AS subtype_name,
CONCEPT_CONSULTANT.name AS consultant_name,
PERSON_CONDITIONS_V.condition_start_date AS condition_start_date,
CONCEPT_GENDER.gender_name AS gender_name,
CARE_SITE.care_site_name AS care_site_name
FROM dbo.PERSON, dbo.PERSON_CONDITIONS_V, dbo.CONCEPT_CONSULTANT, dbo.CONCEPT_GENDER, dbo.CARE_SITE
WHERE PERSON.person_id = PERSON_CONDITIONS_V.person_id 
AND PERSON.consultant_concept_id = CONCEPT_CONSULTANT.consultant_concept_id 
AND PERSON.gender_concept_id = CONCEPT_GENDER.gender_concept_id
AND PERSON.care_site_id = CARE_SITE.care_site_id
AND PERSON.person_id > 0;

INSERT INTO COMPONENTS (roleID, component) VALUES (1,'genericgenomictick')
INSERT INTO COMPONENTS (roleID, component) VALUES (2,'genericgenomictick')
INSERT INTO COMPONENTS (roleID, component) VALUES (3,'genericgenomictick')
INSERT INTO COMPONENTS (roleID, component) VALUES (6,'genericgenomictick')
INSERT INTO COMPONENTS (roleID, component) VALUES (2,'genericgenomiccheckbox')
INSERT INTO COMPONENTS (roleID, component) VALUES (6,'genericgenomiccheckbox')

ALTER TABLE SELECTED_GENE_VARIANT ALTER COLUMN type VARCHAR (100)
ALTER TABLE MEETING_OUTCOME_GENE_VARIANT ALTER COLUMN type VARCHAR (100)

ALTER TABLE CONCEPT_GENE_PANEL ADD short_code varchar(2)
UPDATE CONCEPT_GENE_PANEL set short_code='fm' where panel_name='foundationmedicine'

INSERT INTO CONCEPT_GENE_PANEL (panel_name, short_code) VALUES ('guardant', 'gh')




ALTER VIEW FMTUMOUR AS
SELECT 
specimen.person_id,
geneVar.measurement_gene_variant_id,
geneVar.measurement_gene_panel_id,
geneName.gene_name,
cdna_change, total_cfdna_reads, cfdna_frequency, sv_genome_position, amino_acid_change, ncbi_nucleotide, variant_concept_id, somatic_status,
specimen.baseline_number,
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
LEFT JOIN CONCEPT_GENE as cg1 on geneVar.rearr_gene_1=cg1.gene_concept_id
LEFT JOIN CONCEPT_GENE as cg2 on geneVar.rearr_gene_2=cg2.gene_concept_id 
WHERE (specimen.specimen_concept_id=2
OR specimen.specimen_concept_id=3) AND genePanel.ngs_run = 'FM'

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
specimen.baseline_number as baseline_number,
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
WHERE (specimen.specimen_concept_id=2
OR specimen.specimen_concept_id=3) AND genePanel.ngs_run != 'FM'

/* TNE-62 */
exec sp_rename 'CONCEPT_GENE_PANEL', 'CONCEPT_DATA_SOURCES';
EXEC sp_rename 'CONCEPT_DATA_SOURCES.gene_panel_concept_id', 'data_source_concept_id', 'COLUMN';  
EXEC sp_rename 'MEASUREMENT_GENE_PANEL.gene_panel_concept_id', 'data_source_concept_id', 'COLUMN'; 
EXEC sp_rename 'PERSON.christie', 'hospital_number', 'COLUMN'; 
EXEC sp_rename 'PERSON.christie_hash', 'hospital_number_hash', 'COLUMN'; 

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
specimen.baseline_number as baseline_number,
genePanel.run_number AS run_number,
genePanel.measurement_gene_panel_id AS measurement_gene_panel_id
FROM dbo.SPECIMEN AS specimen 
FULL OUTER JOIN dbo.MEASUREMENT_GENE_PANEL AS genePanel ON specimen.specimen_id = genePanel.specimen_id 
FULL OUTER JOIN dbo.PATH_LAB_REF AS pathLab ON genePanel.measurement_gene_panel_id = pathLab.measurement_gene_panel_id 
FULL OUTER JOIN dbo.MEASUREMENT_GENE_VARIANT AS geneVar ON geneVar.measurement_gene_panel_id = genePanel.measurement_gene_panel_id 
FULL OUTER JOIN dbo.CONCEPT_GENE AS geneName ON geneVar.gene_concept_id = geneName.gene_concept_id 
FULL OUTER JOIN dbo.CONCEPT_DATA_SOURCES AS genePanelConcept ON genePanelConcept.data_source_concept_id = genePanel.data_source_concept_id 
FULL OUTER JOIN dbo.GDL_REQUEST AS gdlRequest ON specimen.specimen_id = gdlRequest.specimen_id 
FULL OUTER JOIN dbo.REPORT AS report ON genePanel.measurement_gene_panel_id = report.measurement_gene_panel_id 
WHERE (specimen.specimen_concept_id=2
OR specimen.specimen_concept_id=3) AND genePanel.ngs_run != 'FM'

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
FULL OUTER JOIN dbo.CONCEPT_DATA_SOURCES AS genePanelConcept ON genePanelConcept.data_source_concept_id = genePanel.data_source_concept_id
LEFT JOIN dbo.CONCEPT_DATA_SOURCES on genePanel.data_source_concept_id=CONCEPT_DATA_SOURCES.data_source_concept_id 
WHERE specimen.specimen_concept_id=1 and CONCEPT_DATA_SOURCES.panel_name!='foundationmedicine'

ALTER VIEW FMTUMOUR AS
SELECT 
specimen.person_id,
geneVar.measurement_gene_variant_id,
geneVar.measurement_gene_panel_id,
geneName.gene_name,
cdna_change, total_cfdna_reads, cfdna_frequency, sv_genome_position, amino_acid_change, ncbi_nucleotide, variant_concept_id, somatic_status,
specimen.baseline_number,
copy_number, number_of_exons, cna_ratio, cna_type,
rearr_description, rearr_in_frame,rearr_pos1, rearr_pos2, rearr_number_of_reads,cg2.gene_name as rearr_gene_2_name,cg1.gene_name as rearr_gene_1_name
FROM dbo.SPECIMEN AS specimen 
FULL OUTER JOIN dbo.MEASUREMENT_GENE_PANEL AS genePanel ON specimen.specimen_id = genePanel.specimen_id 
FULL OUTER JOIN dbo.PATH_LAB_REF AS pathLab ON genePanel.measurement_gene_panel_id = pathLab.measurement_gene_panel_id 
FULL OUTER JOIN dbo.MEASUREMENT_GENE_VARIANT AS geneVar ON geneVar.measurement_gene_panel_id = genePanel.measurement_gene_panel_id 
FULL OUTER JOIN dbo.CONCEPT_GENE AS geneName ON geneVar.gene_concept_id = geneName.gene_concept_id 
FULL OUTER JOIN dbo.CONCEPT_DATA_SOURCES AS genePanelConcept ON genePanelConcept.data_source_concept_id = genePanel.data_source_concept_id 
FULL OUTER JOIN dbo.GDL_REQUEST AS gdlRequest ON specimen.specimen_id = gdlRequest.specimen_id 
FULL OUTER JOIN dbo.REPORT AS report ON genePanel.measurement_gene_panel_id = report.measurement_gene_panel_id 
LEFT JOIN CONCEPT_GENE as cg1 on geneVar.rearr_gene_1=cg1.gene_concept_id
LEFT JOIN CONCEPT_GENE as cg2 on geneVar.rearr_gene_2=cg2.gene_concept_id 
WHERE (specimen.specimen_concept_id=2
OR specimen.specimen_concept_id=3) AND genePanel.ngs_run = 'FM'

ALTER VIEW LATEST_MEASUREMENT_GENE_PANEL_IDS as (
select specimen_id, measurement_gene_panel_id from (SELECT TUMOUR_SPECIMEN_BASELINE.specimen_id, MEASUREMENT_GENE_PANEL.measurement_gene_panel_id,
ROW_NUMBER() over(PARTITION BY TUMOUR_SPECIMEN_BASELINE.person_id, TUMOUR_SPECIMEN_BASELINE.specimen_id, MEASUREMENT_GENE_PANEL.data_source_concept_id  ORDER BY MEASUREMENT_GENE_PANEL.ingestion_date desc, MEASUREMENT_GENE_PANEL.measurement_gene_panel_id desc) as version
from TUMOUR_SPECIMEN_BASELINE
LEFT JOIN MEASUREMENT_GENE_PANEL on TUMOUR_SPECIMEN_BASELINE.specimen_id=MEASUREMENT_GENE_PANEL.specimen_id
where measurement_gene_panel_id is not null) as t1 where version=1
union
select specimen_id, measurement_gene_panel_id from (SELECT SPECIMEN.specimen_id, MEASUREMENT_GENE_PANEL.measurement_gene_panel_id,
ROW_NUMBER() over(PARTITION BY SPECIMEN.person_id, SPECIMEN.specimen_id, SPECIMEN.baseline_number, MEASUREMENT_GENE_PANEL.run_number, MEASUREMENT_GENE_PANEL.data_source_concept_id ORDER BY MEASUREMENT_GENE_PANEL.ingestion_date desc, MEASUREMENT_GENE_PANEL.measurement_gene_panel_id desc) as version
from SPECIMEN
LEFT JOIN MEASUREMENT_GENE_PANEL on SPECIMEN.specimen_id=MEASUREMENT_GENE_PANEL.specimen_id
where SPECIMEN.specimen_concept_id=1 and MEASUREMENT_GENE_PANEL.measurement_gene_panel_id is not null) as t2
where version=1)

/* TNE-75 */

UPDATE CONCEPT_CONDITION set condition_name='Skin' where condition_name='Melanoma'

insert into CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES ('Squamous cell', (select condition_concept_id from CONCEPT_CONDITION where condition_name='Skin'))

insert into CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES ('Basal cell', (select condition_concept_id from CONCEPT_CONDITION where condition_name='Skin'))

delete from CONCEPT_CONDITION_SUBTYPE where condition_concept_id=(select condition_concept_id from CONCEPT_CONDITION where condition_name='Neuroendocrine')

insert into CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES ('Lung', (select condition_concept_id from CONCEPT_CONDITION where condition_name='Neuroendocrine'))
insert into CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES ('Upper GI', (select condition_concept_id from CONCEPT_CONDITION where condition_name='Neuroendocrine'))
insert into CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES ('Lower GI', (select condition_concept_id from CONCEPT_CONDITION where condition_name='Neuroendocrine'))
insert into CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES ('Skin', (select condition_concept_id from CONCEPT_CONDITION where condition_name='Neuroendocrine'))
insert into CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES ('Pancreas', (select condition_concept_id from CONCEPT_CONDITION where condition_name='Neuroendocrine'))
insert into CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES ('Liver', (select condition_concept_id from CONCEPT_CONDITION where condition_name='Neuroendocrine'))
insert into CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES ('Prostate', (select condition_concept_id from CONCEPT_CONDITION where condition_name='Neuroendocrine'))
insert into CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES ('Ovary', (select condition_concept_id from CONCEPT_CONDITION where condition_name='Neuroendocrine'))
insert into CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES ('Testes', (select condition_concept_id from CONCEPT_CONDITION where condition_name='Neuroendocrine'))
insert into CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES ('Renal', (select condition_concept_id from CONCEPT_CONDITION where condition_name='Neuroendocrine'))
insert into CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES ('Appendix', (select condition_concept_id from CONCEPT_CONDITION where condition_name='Neuroendocrine'))
insert into CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES ('Thymus', (select condition_concept_id from CONCEPT_CONDITION where condition_name='Neuroendocrine'))
insert into CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES ('Brain', (select condition_concept_id from CONCEPT_CONDITION where condition_name='Neuroendocrine'))
insert into CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES ('Pheochromocytoma', (select condition_concept_id from CONCEPT_CONDITION where condition_name='Neuroendocrine'))
insert into CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES ('Pituitary', (select condition_concept_id from CONCEPT_CONDITION where condition_name='Neuroendocrine'))

ALTER TABLE SYSTEM_STATUS ALTER COLUMN message varchar(500)

EXEC sp_RENAME 'MEASUREMENT_GENE_PANEL.median_exon_depth', 'mean_exon_depth', 'COLUMN'
ALTER TABLE dbo.MEASUREMENT_GENE_PANEL ALTER COLUMN mean_exon_depth DECIMAL(7,2)
ALTER TABLE MEASUREMENT_GENE_PANEL ADD tumour_fraction_score decimal(5,2)
ALTER TABLE MEASUREMENT_GENE_PANEL ADD tumour_fraction_unit varchar(50)
ALTER TABLE MEASUREMENT_GENE_VARIANT ADD subclonal varchar(50) 
ALTER TABLE MEASUREMENT_GENE_VARIANT ADD equivocal varchar(50)
ALTER TABLE MEASUREMENT_GENE_VARIANT ADD rearrangementType varchar(100)

ALTER VIEW FMTUMOUR AS
SELECT 
specimen.person_id,
geneVar.measurement_gene_variant_id,
geneVar.measurement_gene_panel_id,
geneName.gene_name,
cdna_change, total_cfdna_reads, cfdna_frequency, sv_genome_position, amino_acid_change, ncbi_nucleotide, variant_concept_id, somatic_status,
specimen.baseline_number,
copy_number, number_of_exons, cna_ratio, cna_type, mutation_type,
rearr_description, rearr_in_frame,rearr_pos1, rearr_pos2, rearr_number_of_reads,cg2.gene_name as rearr_gene_2_name,cg1.gene_name as rearr_gene_1_name, 
rearrangementType, equivocal, subclonal
FROM dbo.SPECIMEN AS specimen 
FULL OUTER JOIN dbo.MEASUREMENT_GENE_PANEL AS genePanel ON specimen.specimen_id = genePanel.specimen_id 
FULL OUTER JOIN dbo.PATH_LAB_REF AS pathLab ON genePanel.measurement_gene_panel_id = pathLab.measurement_gene_panel_id 
FULL OUTER JOIN dbo.MEASUREMENT_GENE_VARIANT AS geneVar ON geneVar.measurement_gene_panel_id = genePanel.measurement_gene_panel_id 
FULL OUTER JOIN dbo.CONCEPT_GENE AS geneName ON geneVar.gene_concept_id = geneName.gene_concept_id 
FULL OUTER JOIN dbo.CONCEPT_DATA_SOURCES AS genePanelConcept ON genePanelConcept.data_source_concept_id = genePanel.data_source_concept_id 
FULL OUTER JOIN dbo.GDL_REQUEST AS gdlRequest ON specimen.specimen_id = gdlRequest.specimen_id 
FULL OUTER JOIN dbo.REPORT AS report ON genePanel.measurement_gene_panel_id = report.measurement_gene_panel_id 
LEFT JOIN CONCEPT_GENE as cg1 on geneVar.rearr_gene_1=cg1.gene_concept_id
LEFT JOIN CONCEPT_GENE as cg2 on geneVar.rearr_gene_2=cg2.gene_concept_id 
WHERE (specimen.specimen_concept_id=2
OR specimen.specimen_concept_id=3) AND genePanel.ngs_run = 'FM'


/* TNE-88 */
ALTER TABLE Person ALTER COLUMN last_update datetime


/* TNE-100*/
UPDATE a  set a.variant_type=b.variant_concept_id FROM MEASUREMENT_GENE_VARIANT a INNER JOIN MEASUREMENT_GENE_VARIANT b on a.measurement_gene_variant_id = b.measurement_gene_variant_id where b.variant_concept_id is not null

DROP INDEX fk_MEASUREMENT_GENE_VARIANT_CONCEPT_VARIANT1_idx on MEASUREMENT_GENE_VARIANT

ALTER TABLE MEASUREMENT_GENE_VARIANT DROP CONSTRAINT DF__MEASUREME__varia__690797E6, COLUMN variant_concept_id

ALTER TABLE MEASUREMENT_GENE_VARIANT DROP COLUMN variant_concept_id 

EXEC sp_RENAME 'MEASUREMENT_GENE_VARIANT.total_cfdna_reads', 'read_depth', 'COLUMN'
EXEC sp_RENAME 'MEASUREMENT_GENE_VARIANT.rearrangementType', 'rearr_type', 'COLUMN'

EXEC sp_RENAME 'MEASUREMENT_GENE_VARIANT.sv_genome_position', 'position', 'COLUMN'

EXEC sp_RENAME 'MEASUREMENT_GENE_VARIANT.ncbi_nucleotide', 'transcript', 'COLUMN'

ALTER TABLE MEASUREMENT_GENE_VARIANT DROP COLUMN tumour_mutational_burden

EXEC sp_RENAME 'MEASUREMENT_GENE_VARIANT.mutation_type', 'functional_effect', 'COLUMN'

EXEC sp_RENAME 'MEASUREMENT_GENE_VARIANT.number_of_exons', 'exons', 'COLUMN'

EXEC sp_RENAME 'MEASUREMENT_GENE_VARIANT.somatic_status', 'status', 'COLUMN'


EXEC sp_RENAME 'MEASUREMENT_GENE_VARIANT.cfdna_frequency', 'variant_allele_frequency', 'COLUMN'

UPDATE a  set a.variant_allele_frequency=b.approximate_mutatnt_allele_frequency FROM MEASUREMENT_GENE_VARIANT a INNER JOIN MEASUREMENT_GENE_VARIANT b on a.measurement_gene_variant_id = b.measurement_gene_variant_id where b.approximate_mutatnt_allele_frequency is not null

ALTER TABLE MEASUREMENT_GENE_VARIANT  DROP CONSTRAINT DF__MEASUREME__appro__6EC0713C, COLUMN approximate_mutatnt_allele_frequency

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
geneVar.variant_allele_frequency AS variant_allele_frequency,
geneVar.read_depth AS read_depth,
geneVar.germline_frequency AS germline_frequency,
geneVar.functional_effect AS functional_effect,
geneVar.high_confidence AS high_confidence,
geneVar.is_specific_mutation_in_panel AS is_specific_mutation_in_panel,
geneVar.position as position,
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
FULL OUTER JOIN dbo.CONCEPT_DATA_SOURCES AS genePanelConcept ON genePanelConcept.data_source_concept_id = genePanel.data_source_concept_id
LEFT JOIN dbo.CONCEPT_DATA_SOURCES on genePanel.data_source_concept_id=CONCEPT_DATA_SOURCES.data_source_concept_id 
WHERE specimen.specimen_concept_id=1 and CONCEPT_DATA_SOURCES.panel_name!='foundationmedicine'


ALTER VIEW FMTUMOUR AS
SELECT 
specimen.person_id,
geneVar.measurement_gene_variant_id,
geneVar.measurement_gene_panel_id,
geneName.gene_name,
cdna_change, read_depth, variant_allele_frequency, position, amino_acid_change, transcript, variant_type, status,
specimen.baseline_number,
copy_number, exons, cna_ratio, cna_type, functional_effect,
rearr_description, rearr_in_frame,rearr_pos1, rearr_pos2, rearr_number_of_reads,cg2.gene_name as rearr_gene_2_name,cg1.gene_name as rearr_gene_1_name, 
rearr_type, equivocal, subclonal
FROM dbo.SPECIMEN AS specimen 
FULL OUTER JOIN dbo.MEASUREMENT_GENE_PANEL AS genePanel ON specimen.specimen_id = genePanel.specimen_id 
FULL OUTER JOIN dbo.PATH_LAB_REF AS pathLab ON genePanel.measurement_gene_panel_id = pathLab.measurement_gene_panel_id 
FULL OUTER JOIN dbo.MEASUREMENT_GENE_VARIANT AS geneVar ON geneVar.measurement_gene_panel_id = genePanel.measurement_gene_panel_id 
FULL OUTER JOIN dbo.CONCEPT_GENE AS geneName ON geneVar.gene_concept_id = geneName.gene_concept_id 
FULL OUTER JOIN dbo.CONCEPT_DATA_SOURCES AS genePanelConcept ON genePanelConcept.data_source_concept_id = genePanel.data_source_concept_id 
FULL OUTER JOIN dbo.GDL_REQUEST AS gdlRequest ON specimen.specimen_id = gdlRequest.specimen_id 
FULL OUTER JOIN dbo.REPORT AS report ON genePanel.measurement_gene_panel_id = report.measurement_gene_panel_id 
LEFT JOIN CONCEPT_GENE as cg1 on geneVar.rearr_gene_1=cg1.gene_concept_id
LEFT JOIN CONCEPT_GENE as cg2 on geneVar.rearr_gene_2=cg2.gene_concept_id 
WHERE (specimen.specimen_concept_id=2
OR specimen.specimen_concept_id=3) AND genePanel.ngs_run = 'FM'

ALTER VIEW SEARCH as
select DISTINCT cc.condition_name, ccs.subtype_name, c.gene_name,mv.variant_type,mv.cdna_change,mv.amino_acid_change,g1.gene_name as rearr_gene1, g2.gene_name as rearr_gene2, mv.rearr_description, mv.cna_type, p.target_id,p.person_id, 
(CASE mv.status WHEN 'unknown' THEN 1 ELSE 0 END) as 'unknown_significant'
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

ALTER VIEW TUMOURNGS AS
SELECT 
specimen.person_id AS person_id,
specimen.specimen_id AS specimen_id,
specimen.specimen_date AS specimen_date,
geneName.gene_name AS gene_name,
geneVar.measurement_gene_variant_id as gene_varient_id,
geneVar.cdna_change AS cdna_change,
geneVar.amino_acid_change AS amino_acid_change,
geneVar.variant_allele_frequency AS variant_allele_frequency,
genePanel.average_read_depth AS average_read_depth,
genePanel.coverage AS coverage,
pathLab.path_lab_ref AS path_lab_ref,
gdlRequest.date_requested AS date_requested,
report.filename AS filename,
genePanel.comments AS comments,
genePanel.unknown_significance AS unknown_significance,
specimen.baseline_number as baseline_number,
genePanel.run_number AS run_number,
genePanel.measurement_gene_panel_id AS measurement_gene_panel_id
FROM dbo.SPECIMEN AS specimen 
FULL OUTER JOIN dbo.MEASUREMENT_GENE_PANEL AS genePanel ON specimen.specimen_id = genePanel.specimen_id 
FULL OUTER JOIN dbo.PATH_LAB_REF AS pathLab ON genePanel.measurement_gene_panel_id = pathLab.measurement_gene_panel_id 
FULL OUTER JOIN dbo.MEASUREMENT_GENE_VARIANT AS geneVar ON geneVar.measurement_gene_panel_id = genePanel.measurement_gene_panel_id 
FULL OUTER JOIN dbo.CONCEPT_GENE AS geneName ON geneVar.gene_concept_id = geneName.gene_concept_id 
FULL OUTER JOIN dbo.CONCEPT_DATA_SOURCES AS genePanelConcept ON genePanelConcept.data_source_concept_id = genePanel.data_source_concept_id 
FULL OUTER JOIN dbo.GDL_REQUEST AS gdlRequest ON specimen.specimen_id = gdlRequest.specimen_id 
FULL OUTER JOIN dbo.REPORT AS report ON genePanel.measurement_gene_panel_id = report.measurement_gene_panel_id 
WHERE (specimen.specimen_concept_id=2
OR specimen.specimen_concept_id=3) AND genePanel.ngs_run != 'FM'

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
geneVar.variant_allele_frequency AS variant_allele_frequency,
geneVar.read_depth AS read_depth,
geneVar.germline_frequency AS germline_frequency,
geneVar.functional_effect AS functional_effect,
geneVar.high_confidence AS high_confidence,
geneVar.is_specific_mutation_in_panel AS is_specific_mutation_in_panel,
geneVar.position as position,
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
FULL OUTER JOIN dbo.CONCEPT_DATA_SOURCES AS genePanelConcept ON genePanelConcept.data_source_concept_id = genePanel.data_source_concept_id
LEFT JOIN dbo.CONCEPT_DATA_SOURCES on genePanel.data_source_concept_id=CONCEPT_DATA_SOURCES.data_source_concept_id 
WHERE specimen.specimen_concept_id=1 and genePanel.ngs_run!='FM'  

/*TNE-135 Target National only*/
INSERT INTO CARE_SITE (care_site_id, care_site_name) VALUES (151, 'University Hospitals of Leicester NHS Trust');
INSERT INTO CARE_SITE (care_site_id, care_site_name) VALUES (161, 'London - Imperial College London');
INSERT INTO CARE_SITE (care_site_id, care_site_name) VALUES (171, 'London - ICR & The Royal Marsden');
INSERT INTO CARE_SITE (care_site_id, care_site_name) VALUES (181, 'London - King''s Health Partners');
INSERT INTO CARE_SITE (care_site_id, care_site_name) VALUES (191, 'Oxford ECMC');
INSERT INTO CARE_SITE (care_site_id, care_site_name) VALUES (211, 'University Hospitals Southampton NHS Foundation Trust');
INSERT INTO CARE_SITE (care_site_id, care_site_name) VALUES (221, 'University Hospitals Birmingham NHS Foundation Trust');
INSERT INTO CARE_SITE (care_site_id, care_site_name) VALUES (231, 'Barts Cancer Institute');

/*TNE-149 update subtypes*/
update CONCEPT_CONDITION_SUBTYPE set subtype_name = CONCAT('Neuroendocrine: ', subtype_name) WHERE condition_concept_id=(SELECT condition_concept_id from CONCEPT_CONDITION where condition_name='Neuroendocrine')

/*TNE-152 new subtype*/
INSERT INTO CONCEPT_CONDITION_SUBTYPE (subtype_name, condition_concept_id) VALUES ('Vaginal', (SELECT condition_concept_id from CONCEPT_CONDITION where condition_name='Female reproductive organs'))

/*TNE-153 remove subtype*/
delete From CONCEPT_CONDITION_SUBTYPE where subtype_name='Vaginal' and condition_concept_id=(select condition_concept_id from CONCEPT_CONDITION where condition_name='Anogenital')

/*TNE-201 ambiguous as VUS*/
ALTER VIEW SEARCH as
select DISTINCT cc.condition_name, ccs.subtype_name, c.gene_name,mv.variant_type,mv.cdna_change,mv.amino_acid_change,g1.gene_name as rearr_gene1, g2.gene_name as rearr_gene2, mv.rearr_description, mv.cna_type, p.target_id,p.person_id, 
(CASE mv.status WHEN 'unknown' THEN 1 WHEN 'ambiguous' THEN 1 ELSE 0 END) as 'unknown_significant'
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
