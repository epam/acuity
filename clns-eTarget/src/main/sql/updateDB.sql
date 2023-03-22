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
update USER_ROLES set allowedComponents='adminlink,patienttable,reportextraction,admin,ctdnangstick,ctdnaexptick,tumourtick' where roleID=1;
update USER_ROLES set allowedComponents='patienttable,reportextraction,ctdnangstick,ctdnaexptick,tumourtick' where roleID=3;

#TAR-322 release 0.7

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
specimen.baseline_number AS baseline_number
FROM dbo.SPECIMEN AS specimen 
FULL OUTER JOIN dbo.MEASUREMENT_GENE_PANEL AS genePanel ON specimen.specimen_id = genePanel.specimen_id 
FULL OUTER JOIN dbo.MEASUREMENT_GENE_VARIANT AS geneVar ON geneVar.measurement_gene_panel_id = genePanel.measurement_gene_panel_id 
FULL OUTER JOIN dbo.CONCEPT_GENE AS geneName ON geneVar.gene_concept_id = geneName.gene_concept_id 
FULL OUTER JOIN dbo.CONCEPT_GENE_PANEL AS genePanelConcept ON genePanelConcept.gene_panel_concept_id = genePanel.gene_panel_concept_id 
WHERE specimen.specimen_concept_id=1

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
genePanel.unknown_significance AS unknown_significance
FROM dbo.SPECIMEN AS specimen 
FULL OUTER JOIN dbo.MEASUREMENT_GENE_PANEL AS genePanel ON specimen.specimen_id = genePanel.specimen_id 
FULL OUTER JOIN dbo.PATH_LAB_REF AS pathLab ON specimen.specimen_id = pathLab.specimen_id 
FULL OUTER JOIN dbo.MEASUREMENT_GENE_VARIANT AS geneVar ON geneVar.measurement_gene_panel_id = genePanel.measurement_gene_panel_id 
FULL OUTER JOIN dbo.CONCEPT_GENE AS geneName ON geneVar.gene_concept_id = geneName.gene_concept_id 
FULL OUTER JOIN dbo.CONCEPT_GENE_PANEL AS genePanelConcept ON genePanelConcept.gene_panel_concept_id = genePanel.gene_panel_concept_id 
FULL OUTER JOIN dbo.GDL_REQUEST AS gdlRequest ON specimen.specimen_id = gdlRequest.specimen_id 
FULL OUTER JOIN dbo.REPORT AS report ON genePanel.measurement_gene_panel_id = report.measurement_gene_panel_id 
WHERE specimen.specimen_concept_id=2
OR specimen.specimen_concept_id=3

create table SELECTED_GENE_VARIANT(
person_id int not null,
measurement_gene_variant_id int  not null,
type varchar(20),
CONSTRAINT [fk_SELECTED_GENE_VARIANT_PERSON1] FOREIGN KEY(person_id)
REFERENCES [dbo].[PERSON] ([person_id]),
CONSTRAINT [fk_SELECTED_GENE_VARIANT_MEASUREMENT_GENE_VARIANT1] FOREIGN KEY(measurement_gene_variant_id)
REFERENCES [dbo].[MEASUREMENT_GENE_VARIANT] ([measurement_gene_variant_id]));

#TAR280 release 0.8
CREATE VIEW SEARCH AS
select DISTINCT cc.condition_name,c.gene_name,mv.cdna_change,mv.amino_acid_change,p.target_id,p.person_id FROM PERSON p 
LEFT OUTER JOIN CONDITION_OCCURRENCE co on p.person_id=co.person_id
LEFT OUTER JOIN CONCEPT_CONDITION cc on co.condition_concept_id=cc.condition_concept_id
LEFT OUTER JOIN SPECIMEN s on p.person_id=s.person_id 
LEFT OUTER JOIN MEASUREMENT_GENE_PANEL mp on s.specimen_id=mp.specimen_id 
JOIN MEASUREMENT_GENE_VARIANT mv on mp.measurement_gene_panel_id=mv.measurement_gene_panel_id
LEFT OUTER join CONCEPT_GENE c on mv.gene_concept_id=c.gene_concept_id

UPDATE USER_ROLES set allowedComponents='adminlink,patienttable,patienttable_search,reportextraction,admin,ctdnangstick,ctdnaexptick,tumourtick' where roleID=1
UPDATE USER_ROLES set allowedComponents='patienttable,patienttable_search,meetingoutcome,reportextraction,tumourcheckbox,ctdnangscheckbox,ctdnaexpcheckbox' where roleID=2
UPDATE USER_ROLES set allowedComponents='patienttable,patienttable_search,reportextraction,ctdnangstick,ctdnaexptick,tumourtick' where roleID=3

#TAR390 release 0.9
ALTER TABLE MEASUREMENT_GENE_PANEL ADD ingestion_date date;

#TAR393 add measurement_gene_panel_id to get order
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
WHERE specimen.specimen_concept_id=1

#!!!!!to be done for every new gene_panel!!!! Adjust concept_gene_panel_id
insert into GENE_PANEL (concept_gene_panel_id, concept_gene_id)
select 6, concept_gene_id from  GENE_PANEL where concept_gene_panel_id=2

#v0.10 new componenet for editor
UPDATE USER_ROLES set allowedComponents='patienttable,patienttable_search,meetingoutcome,editmeetingoutcome,reportextraction,tumourcheckbox,ctdnangscheckbox,ctdnaexpcheckbox' where roleID=2

CREATE TABLE "dbo"."CHANGE_LOG"
(
   changelog_id int IDENTITY(1,1) PRIMARY KEY,
   updated_by varchar(100),
   updated_on datetime DEFAULT (getdate()) NOT NULL,
   table_name varchar(50) NOT NULL,
   field_name varchar(50) NOT NULL,
   reference_id int,
   old_value text,
   new_value text
)
GO

#v0.11 multiple editors 
create table EDIT_LOCK
(
	user_id varchar(512) NOT NULL,
	person_id int NOT NULL,
	locked_on datetime DEFAULT (getdate()) NOT NULL,
	CONSTRAINT [fk_EDIT_LOCK_PERSON1] FOREIGN KEY(person_id)
	REFERENCES [dbo].[PERSON] ([person_id])
)

UPDATE USER_ROLES set allowedComponents='patienttable,patienttable_search,meetingoutcome,editmeetingoutcome,reportextraction,tumourcheckbox,ctdnangscheckbox,ctdnaexpcheckbox,ctdnangstick,ctdnaexptick,tumourtick' where roleID=2

# meeting outcome  gene variants

ALTER TABLE MEETING_OUTCOME 
ADD CONSTRAINT PK_mo_id PRIMARY KEY (mo_id)

create table MEETING_OUTCOME_GENE_VARIANT(
mo_id int not null,
measurement_gene_variant_id int  not null,
type varchar(20),
CONSTRAINT [fk_MEETING_OUTCOME_GENE_VARIANT_MEETING_OUTCOME1] FOREIGN KEY(mo_id)
REFERENCES [dbo].[MEETING_OUTCOME] ([mo_id]),
CONSTRAINT [fk_MEETING_OUTCOME_GENE_VARIANT_MEASUREMENT_GENE_VARIANT1] FOREIGN KEY(measurement_gene_variant_id)
REFERENCES [dbo].[MEASUREMENT_GENE_VARIANT] ([measurement_gene_variant_id]));

# allow for 100% of frequency collumns
alter table MEASUREMENT_GENE_VARIANT ALTER COLUMN cfdna_frequency decimal(5,2)
alter table MEASUREMENT_GENE_VARIANT ALTER COLUMN germline_frequency decimal(5,2)


#v0.12
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
WHERE specimen.specimen_concept_id=1

#v0.13
# add measurement_gene_panel_id column
ALTER TABLE PATH_LAB_REF ADD measurement_gene_panel_id int, FOREIGN KEY(measurement_gene_panel_id) REFERENCES MEASUREMENT_GENE_PANEL(measurement_gene_panel_id)

UPDATE PATH_LAB_REF set PATH_LAB_REF.measurement_gene_panel_id = t2.measurement_gene_panel_id
FROM (SELECT measurement_gene_panel_id, specimen_id from MEASUREMENT_GENE_PANEL) as t2
where PATH_LAB_REF.specimen_id=t2.specimen_id

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
genePanel.baseline_number AS baseline_number,
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
WHERE specimen.specimen_concept_id=2
OR specimen.specimen_concept_id=3

#clean out concept_gene table by removing not used genes but keep all unique ones
delete from CONCEPT_GENE where gene_concept_id 
not in (select min(gene_concept_id) as gene_concept_id from CONCEPT_GENE GROUP by gene_name) 
and gene_concept_id not in (
select distinct concept_gene_id from GENE_PANEL, MEASUREMENT_GENE_VARIANT
) and gene_concept_id not in (
select distinct gene_concept_id from MEASUREMENT_GENE_VARIANT)


#TAR-508 new gene panel ui for admin
update USER_ROLES set allowedComponents='adminlink,patienttable,reportextraction,admin,ctdnangstick,ctdnaexptick,tumourtick,genepanel' where roleID=1

#TAR-524 allow gatekeeper to search
UPDATE USER_ROLES set allowedComponents='adminlink,patienttable,patienttable_search,reportextraction,admin,ctdnangstick,ctdnaexptick,tumourtick,genepanel'  WHERE roleID=1


#TAR-515
UPDATE MEASUREMENT_GENE_VARIANT set sv_genome_position=cdna_change where SUBSTRING(cdna_change,1,3)='chr' and sv_genome_position is null
#71 Row(s) Updated


# New endpoint for admin DeleteReport
UPDATE USER_ROLES set allowedEndpoints='GetAllPatients,SystemStatus,GetSinglePatient,GeneralInfo,TumourNGS,CTDNANGSSubset,CTDNAExploratory,PDXCDX,GetMeetingOutcome,PostMutationSelection,PostMeetingOutcome,GetMutationSelection,Admin,DeleteReport' where roleID=1;


# Clean up db
DROP TABLE CONCEPT;
DROP TABLE CONCEPT_VARIANT;
DROP TABLE DRUG_EXPOSURE;
DROP TABLE CONCEPT_DRUG;
DROP TABLE NOTE;

ALTER TABLE SYSTEM_STATUS
ADD CONSTRAINT PK_SYSTEM_STATUS PRIMARY KEY (ss_id)

ALTER TABLE USER_ROLES ALTER COLUMN roleID int NOT NULL;

ALTER TABLE USER_ROLES ADD CONSTRAINT PK_USER_ROLES PRIMARY KEY (roleID)

ALTER TABLE WHITELIST
ADD CONSTRAINT PK_WHITELIST PRIMARY KEY (whitelistID);

ALTER TABLE WHITELIST
ADD CONSTRAINT FK_WHITELIST_USER_ROLES
FOREIGN KEY (roleID) REFERENCES USER_ROLES (roleID);

ALTER TABLE PDXCDX
ADD CONSTRAINT FK_PDXCDX_PERSON
FOREIGN KEY (person_id) REFERENCES PERSON (person_id)

ALTER TABLE MEETING_OUTCOME
ADD CONSTRAINT FK_MEETING_OUTCOME_PERSON
FOREIGN KEY (person_id) REFERENCES PERSON (person_id)

ALTER TABLE CHANGE_LOG ALTER COLUMN updated_by VARCHAR (512)

#Required in_ test
ALTER TABLE WHITELIST
ALTER COLUMN userID varchar(512) NOT NULL;

ALTER  TABLE  WHITELIST WITH CHECK 
   ADD CONSTRAINT UQ_WHITELIST_userID UNIQUE (userID)
#end_ required in_ test

ALTER TABLE EDIT_LOCK
ADD CONSTRAINT FK_EDIT_LOCK_WHITELIST 
FOREIGN KEY (user_id) REFERENCES WHITELIST (userID)

ALTER TABLE CHANGE_LOG
ADD CONSTRAINT FK_CHANGE_LOCK_WHITELIST
FOREIGN KEY (updated_by) REFERENCES WHITELIST (userID)

DROP TABLE MEETING_OUTCOME_LATEST

ALTER TABLE MEETING_OUTCOME
DROP COLUMN gene_summary;

create table ENDPOINTS(
roleID int not null,
endpoint varchar(50) not null,
CONSTRAINT [fk_ENDPOINTS_USER_ROLES] FOREIGN KEY(roleID)
REFERENCES [dbo].[USER_ROLES] ([roleID]))

create table COMPONENTS(
roleID int not null,
component varchar(50) not null,
CONSTRAINT [fk_COMPONENTS_USER_ROLES] FOREIGN KEY(roleID)
REFERENCES [dbo].[USER_ROLES] ([roleID]))

insert into COMPONENTS (roleID, component) VALUES (1, 'adminlink')
insert into COMPONENTS (roleID, component) VALUES (1, 'reportextraction')
insert into COMPONENTS (roleID, component) VALUES (1, 'patienttable')
insert into COMPONENTS (roleID, component) VALUES (1, 'patienttable_search')
insert into COMPONENTS (roleID, component) VALUES (1, 'admin')
insert into COMPONENTS (roleID, component) VALUES (1, 'ctdnangstick')
insert into COMPONENTS (roleID, component) VALUES (1, 'ctdnaexptick')
insert into COMPONENTS (roleID, component) VALUES (1, 'tumourtick')
insert into COMPONENTS (roleID, component) VALUES (1, 'genepanel')
insert into COMPONENTS (roleID, component) VALUES (2, 'patienttable')
insert into COMPONENTS (roleID, component) VALUES (2, 'patienttable_search')
insert into COMPONENTS (roleID, component) VALUES (2, 'meetingoutcome')
insert into COMPONENTS (roleID, component) VALUES (2, 'editmeetingoutcome')
insert into COMPONENTS (roleID, component) VALUES (2, 'reportextraction')
insert into COMPONENTS (roleID, component) VALUES (2, 'tumourcheckbox')
insert into COMPONENTS (roleID, component) VALUES (2, 'ctdnangscheckbox')
insert into COMPONENTS (roleID, component) VALUES (2, 'ctdnaexpcheckbox')
insert into COMPONENTS (roleID, component) VALUES (2, 'ctdnangstick')
insert into COMPONENTS (roleID, component) VALUES (2, 'ctdnaexptick')
insert into COMPONENTS (roleID, component) VALUES (2, 'tumourtick')
insert into COMPONENTS (roleID, component) VALUES (3, 'patienttable')
insert into COMPONENTS (roleID, component) VALUES (3, 'patienttable_search')
insert into COMPONENTS (roleID, component) VALUES (3, 'reportextraction')
insert into COMPONENTS (roleID, component) VALUES (3, 'ctdnangstick')
insert into COMPONENTS (roleID, component) VALUES (3, 'ctdnaexptick')
insert into COMPONENTS (roleID, component) VALUES (3, 'tumourtick')
insert into COMPONENTS (roleID, component) VALUES (4, 'reportextraction')

insert into ENDPOINTS (roleID, endpoint) VALUES (1, 'GetAllPatients')
insert into ENDPOINTS (roleID, endpoint) VALUES (1, 'SystemStatus')
insert into ENDPOINTS (roleID, endpoint) VALUES (1, 'GetSinglePatient')
insert into ENDPOINTS (roleID, endpoint) VALUES (1, 'GeneralInfo')
insert into ENDPOINTS (roleID, endpoint) VALUES (1, 'TumourNGS')
insert into ENDPOINTS (roleID, endpoint) VALUES (1, 'CTDNANGSSubset')
insert into ENDPOINTS (roleID, endpoint) VALUES (1, 'CTDNAExploratory')
insert into ENDPOINTS (roleID, endpoint) VALUES (1, 'PDXCDX')
insert into ENDPOINTS (roleID, endpoint) VALUES (1, 'GetMeetingOutcome')
insert into ENDPOINTS (roleID, endpoint) VALUES (1, 'PostMutationSelection')
insert into ENDPOINTS (roleID, endpoint) VALUES (1, 'PostMeetingOutcome')
insert into ENDPOINTS (roleID, endpoint) VALUES (1, 'GetMutationSelection')
insert into ENDPOINTS (roleID, endpoint) VALUES (1, 'Admin')
insert into ENDPOINTS (roleID, endpoint) VALUES (1, 'DeleteReport')
insert into ENDPOINTS (roleID, endpoint) VALUES (1, 'PostGenePanel')
insert into ENDPOINTS (roleID, endpoint) VALUES (2, 'GetAllPatients')
insert into ENDPOINTS (roleID, endpoint) VALUES (2, 'SystemStatus')
insert into ENDPOINTS (roleID, endpoint) VALUES (2, 'GetSinglePatient')
insert into ENDPOINTS (roleID, endpoint) VALUES (2, 'GeneralInfo')
insert into ENDPOINTS (roleID, endpoint) VALUES (2, 'TumourNGS')
insert into ENDPOINTS (roleID, endpoint) VALUES (2, 'CTDNANGSSubset')
insert into ENDPOINTS (roleID, endpoint) VALUES (2, 'CTDNAExploratory')
insert into ENDPOINTS (roleID, endpoint) VALUES (2, 'PDXCDX')
insert into ENDPOINTS (roleID, endpoint) VALUES (2, 'GetMeetingOutcome')
insert into ENDPOINTS (roleID, endpoint) VALUES (2, 'PostMutationSelection')
insert into ENDPOINTS (roleID, endpoint) VALUES (2, 'GetMutationSelection')
insert into ENDPOINTS (roleID, endpoint) VALUES (2, 'PostMeetingOutcome')
insert into ENDPOINTS (roleID, endpoint) VALUES (3, 'GetAllPatients')
insert into ENDPOINTS (roleID, endpoint) VALUES (3, 'SystemStatus')
insert into ENDPOINTS (roleID, endpoint) VALUES (3, 'GetSinglePatient')
insert into ENDPOINTS (roleID, endpoint) VALUES (3, 'GeneralInfo')
insert into ENDPOINTS (roleID, endpoint) VALUES (3, 'TumourNGS')
insert into ENDPOINTS (roleID, endpoint) VALUES (3, 'CTDNANGSSubset')
insert into ENDPOINTS (roleID, endpoint) VALUES (3, 'CTDNAExploratory')
insert into ENDPOINTS (roleID, endpoint) VALUES (3, 'PDXCDX')
insert into ENDPOINTS (roleID, endpoint) VALUES (3, 'GetMeetingOutcome')
insert into ENDPOINTS (roleID, endpoint) VALUES (3, 'GetMutationSelection')
insert into ENDPOINTS (roleID, endpoint) VALUES (4, 'SystemStatus')

ALTER TABLE USER_ROLES
DROP COLUMN allowedComponents;

ALTER TABLE USER_ROLES
DROP COLUMN allowedEndpoints;
