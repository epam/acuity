package org.digitalecmt.etarget.dao;

/*-
 * #%L
 * eTarget Maven Webapp
 * %%
 * Copyright (C) 2017 - 2021 digital ECMT
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.digitalecmt.etarget.dbentities.CopyNumberAlteration;
import org.digitalecmt.etarget.dbentities.FMSample;
import org.digitalecmt.etarget.dbentities.Rearrangement;
import org.digitalecmt.etarget.dbentities.ShortVariant;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public final class GenomicsDataDAOImpl implements GenomicsDataDAO {
	private static GenomicsDataDAO INSTANCE;
	
	private static Logger log = Logger.getLogger(GenomicsDataDAOImpl.class.getName());
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	private String shortVariantSelectionTumour = "measurement_gene_variant_id, measurement_gene_panel_id, functional_effect,"
			+ "gene_name, cdna_change, read_depth, variant_allele_frequency, position, amino_acid_change, transcript, variant_type, status, baseline_number as baseline, subclonal";
	private String copyNumberalterationTumour = "measurement_gene_variant_id, measurement_gene_panel_id, "
			+ "gene_name, copy_number, exons, position, cna_ratio, status, cna_type, variant_type, baseline_number as baseline, equivocal";
	private String rearrangementSelectionTumour = "measurement_gene_variant_id, measurement_gene_panel_id, "
			+ "rearr_description, rearr_in_frame, rearr_gene_2_name, rearr_pos1, rearr_pos2, status, rearr_number_of_reads, "
			+ "rearr_gene_1_name, variant_type, baseline_number as baseline, rearr_type, variant_allele_frequency";
	private String sample ="measurement_gene_panel_id, specimen_id, MEASUREMENT_GENE_PANEL.data_source_concept_id, percent_exons_100x, mean_exon_depth, microsatellite_instability_status, microsatellite_instability_score, tmb_unit, "
			+ "tmb_status, tmb_score, run_number, tumour_fraction_score, tumour_fraction_unit";

	private String shortVariantSelection = "MEASUREMENT_GENE_VARIANT.measurement_gene_variant_id, MEASUREMENT_GENE_VARIANT.measurement_gene_panel_id, functional_effect,"
			+ "gene_name, cdna_change, read_depth, variant_allele_frequency, position, amino_acid_change, transcript, variant_type, status, MEASUREMENT_GENE_PANEL.baseline_number as baseline, subclonal";
	private String copyNumberalteration = "MEASUREMENT_GENE_VARIANT.measurement_gene_variant_id, MEASUREMENT_GENE_VARIANT.measurement_gene_panel_id, "
			+ "gene_name, copy_number, exons, position, cna_ratio, status, cna_type, variant_type, MEASUREMENT_GENE_PANEL.baseline_number as baseline, equivocal";
	private String rearrangementSelection = "MEASUREMENT_GENE_VARIANT.measurement_gene_variant_id, MEASUREMENT_GENE_VARIANT.measurement_gene_panel_id, "
			+ "rearr_description, rearr_in_frame, cg2.gene_name as rearr_gene_2_name, rearr_pos1, rearr_pos2, status, rearr_number_of_reads, "
			+ "cg1.gene_name as rearr_gene_1_name, variant_type, MEASUREMENT_GENE_PANEL.baseline_number as baseline, rearr_type, variant_allele_frequency";
	private String getShortVariantsSQL ="SELECT " + shortVariantSelection + " FROM MEASUREMENT_GENE_VARIANT\n" + 
			"LEFT JOIN CONCEPT_GENE on MEASUREMENT_GENE_VARIANT.gene_concept_id=CONCEPT_GENE.gene_concept_id\n" + 
			"LEFT JOIN MEASUREMENT_GENE_PANEL on MEASUREMENT_GENE_VARIANT.measurement_gene_panel_id=MEASUREMENT_GENE_PANEL.measurement_gene_panel_id\n" + 
			"where MEASUREMENT_GENE_VARIANT.read_depth IS NOT NULL and  MEASUREMENT_GENE_VARIANT.measurement_gene_panel_id=:measurement_gene_panel_id ";
	private String getShortVariantsByPersonId = "SELECT " + shortVariantSelection + " FROM MEASUREMENT_GENE_VARIANT \n" + 
			"LEFT JOIN CONCEPT_GENE on MEASUREMENT_GENE_VARIANT.gene_concept_id=CONCEPT_GENE.gene_concept_id\n" + 
			"LEFT JOIN MEASUREMENT_GENE_PANEL on MEASUREMENT_GENE_VARIANT.measurement_gene_panel_id=MEASUREMENT_GENE_PANEL.measurement_gene_panel_id\n" + 
			"LEFT JOIN SPECIMEN on MEASUREMENT_GENE_PANEL.specimen_id=SPECIMEN.specimen_id\n" + 
			"LEFT JOIN CONCEPT_DATA_SOURCES on MEASUREMENT_GENE_PANEL.data_source_concept_id=CONCEPT_DATA_SOURCES.data_source_concept_id\n" + 
			"LEFT JOIN PERSON on SPECIMEN.person_id=PERSON.person_id\n" + 
			"where MEASUREMENT_GENE_VARIANT.read_depth IS NOT NULL and CONCEPT_DATA_SOURCES.panel_name=:panel_name and SPECIMEN.person_id=:person_id";
	private String getShortVariantByGeneId = "SELECT " + shortVariantSelection + " FROM MEASUREMENT_GENE_VARIANT\n" +
			"LEFT JOIN CONCEPT_GENE on MEASUREMENT_GENE_VARIANT.gene_concept_id=CONCEPT_GENE.gene_concept_id\n" + 
			"LEFT JOIN MEASUREMENT_GENE_PANEL on MEASUREMENT_GENE_VARIANT.measurement_gene_panel_id=MEASUREMENT_GENE_PANEL.measurement_gene_panel_id\n" +
			"where measurement_gene_variant_id=:gene_variant_id";
	private String getShortVariantByGeneIdTumour = "SELECT " + shortVariantSelectionTumour + " FROM FMTUMOUR\n" +
			"where measurement_gene_variant_id=:gene_variant_id";
	private String getCopyNumberAlterationSQL ="SELECT " + copyNumberalteration + " FROM MEASUREMENT_GENE_VARIANT\n" + 
			"LEFT JOIN CONCEPT_GENE on MEASUREMENT_GENE_VARIANT.gene_concept_id=CONCEPT_GENE.gene_concept_id\n" + 
			"LEFT JOIN MEASUREMENT_GENE_PANEL on MEASUREMENT_GENE_VARIANT.measurement_gene_panel_id=MEASUREMENT_GENE_PANEL.measurement_gene_panel_id\n" + 
			"where MEASUREMENT_GENE_VARIANT.variant_type='copy_number_alteration' and  MEASUREMENT_GENE_VARIANT.measurement_gene_panel_id=:measurement_gene_panel_id ";
	private String getCopyNumberAlterationByPersonId = "SELECT " + copyNumberalteration + " FROM MEASUREMENT_GENE_VARIANT \n" + 
			"LEFT JOIN CONCEPT_GENE on MEASUREMENT_GENE_VARIANT.gene_concept_id=CONCEPT_GENE.gene_concept_id\n" + 
			"LEFT JOIN MEASUREMENT_GENE_PANEL on MEASUREMENT_GENE_VARIANT.measurement_gene_panel_id=MEASUREMENT_GENE_PANEL.measurement_gene_panel_id\n" + 
			"LEFT JOIN SPECIMEN on MEASUREMENT_GENE_PANEL.specimen_id=SPECIMEN.specimen_id\n" + 
			"LEFT JOIN CONCEPT_DATA_SOURCES on MEASUREMENT_GENE_PANEL.data_source_concept_id=CONCEPT_DATA_SOURCES.data_source_concept_id\n" + 
			"LEFT JOIN PERSON on SPECIMEN.person_id=PERSON.person_id\n" + 
			"where MEASUREMENT_GENE_VARIANT.variant_type='copy_number_alteration' and CONCEPT_DATA_SOURCES.panel_name=:panel_name and SPECIMEN.person_id=:person_id";
	private String getCopyNumberAlterationByGeneId = "SELECT " + copyNumberalteration + " FROM MEASUREMENT_GENE_VARIANT\n" + 
			"LEFT JOIN CONCEPT_GENE on MEASUREMENT_GENE_VARIANT.gene_concept_id=CONCEPT_GENE.gene_concept_id\n" + 
			"LEFT JOIN MEASUREMENT_GENE_PANEL on MEASUREMENT_GENE_VARIANT.measurement_gene_panel_id=MEASUREMENT_GENE_PANEL.measurement_gene_panel_id\n" +
			"where measurement_gene_variant_id=:gene_variant_id";
	private String getCopyNumberAlterationByGeneIdTumour = "SELECT " + copyNumberalterationTumour + " FROM FMTUMOUR\n" + 
			"where measurement_gene_variant_id=:gene_variant_id";
	private String getRearrangementsSQL ="SELECT " + rearrangementSelection + " FROM MEASUREMENT_GENE_VARIANT\n" + 
			"LEFT JOIN CONCEPT_GENE as cg1 on MEASUREMENT_GENE_VARIANT.rearr_gene_1=cg1.gene_concept_id\n" + 
			"LEFT JOIN CONCEPT_GENE as cg2 on MEASUREMENT_GENE_VARIANT.rearr_gene_2=cg2.gene_concept_id\n" + 
			"LEFT JOIN MEASUREMENT_GENE_PANEL on MEASUREMENT_GENE_VARIANT.measurement_gene_panel_id=MEASUREMENT_GENE_PANEL.measurement_gene_panel_id\n" + 
			"where MEASUREMENT_GENE_VARIANT.rearr_pos1 IS NOT NULL and  MEASUREMENT_GENE_VARIANT.measurement_gene_panel_id=:measurement_gene_panel_id ";
	private String getRearrangementsByPersonId = "SELECT " + rearrangementSelection + " FROM MEASUREMENT_GENE_VARIANT \n" + 
			"LEFT JOIN CONCEPT_GENE as cg1 on MEASUREMENT_GENE_VARIANT.rearr_gene_1=cg1.gene_concept_id\n" + 
			"LEFT JOIN CONCEPT_GENE as cg2 on MEASUREMENT_GENE_VARIANT.rearr_gene_2=cg2.gene_concept_id\n" + 
			"LEFT JOIN MEASUREMENT_GENE_PANEL on MEASUREMENT_GENE_VARIANT.measurement_gene_panel_id=MEASUREMENT_GENE_PANEL.measurement_gene_panel_id\n" + 
			"LEFT JOIN SPECIMEN on MEASUREMENT_GENE_PANEL.specimen_id=SPECIMEN.specimen_id\n" + 
			"LEFT JOIN CONCEPT_DATA_SOURCES on MEASUREMENT_GENE_PANEL.data_source_concept_id=CONCEPT_DATA_SOURCES.data_source_concept_id\n" + 
			"LEFT JOIN PERSON on SPECIMEN.person_id=PERSON.person_id\n" + 
			"where MEASUREMENT_GENE_VARIANT.rearr_pos1 IS NOT NULL and CONCEPT_DATA_SOURCES.panel_name=:panel_name and SPECIMEN.person_id=:person_id";
	private String getRearrangementByGeneId = "SELECT " + rearrangementSelection + " FROM MEASUREMENT_GENE_VARIANT\n" + 
			"LEFT JOIN CONCEPT_GENE as cg1 on MEASUREMENT_GENE_VARIANT.rearr_gene_1=cg1.gene_concept_id\n" + 
			"LEFT JOIN CONCEPT_GENE as cg2 on MEASUREMENT_GENE_VARIANT.rearr_gene_2=cg2.gene_concept_id\n" + 
			"LEFT JOIN MEASUREMENT_GENE_PANEL on MEASUREMENT_GENE_VARIANT.measurement_gene_panel_id=MEASUREMENT_GENE_PANEL.measurement_gene_panel_id\n" +
			"where measurement_gene_variant_id=:gene_variant_id";
	private String getRearrangementByGeneIdTumour = "SELECT " + rearrangementSelectionTumour + " FROM FMTUMOUR\n" + 
			"where measurement_gene_variant_id=:gene_variant_id";
	private String samples="PERSON.target_id,SPECIMEN.specimen_id, t1.measurement_gene_panel_id, SPECIMEN.specimen_date, \n" + 
			"SPECIMEN.specimen_concept_id, SPECIMEN.baseline_number, t1.run_number, SPECIMEN.tumour_id, t1.tmb_score, \n" + 
			"t1.tmb_status, t1.tmb_unit, t1.microsatellite_instability_score, \n" + 
			"t1.microsatellite_instability_status, t1.mean_exon_depth, \n" + 
			"t1.percent_exons_100x, t1.tumour_fraction_score, t1.tumour_fraction_unit, SPECIMEN.preclin_id";
	private String getSpecimenSamplesBlood = "SELECT "+samples+" from SPECIMEN\n" + 
			"LEFT JOIN (SELECT "+sample+" from MEASUREMENT_GENE_PANEL left join CONCEPT_DATA_SOURCES on MEASUREMENT_GENE_PANEL.data_source_concept_id="
			+ "CONCEPT_DATA_SOURCES.data_source_concept_id where panel_name=:panel_name) as t1 on t1.specimen_id=SPECIMEN.specimen_id\n" + 
			"LEFT JOIN PERSON on SPECIMEN.person_id=PERSON.person_id\n"+
			"where SPECIMEN.specimen_concept_id=1 and SPECIMEN.person_id=:person_id order by SPECIMEN.baseline_number, SPECIMEN.specimen_date";
	private String getSpecimenSamplesTumour = "SELECT "+samples+" from SPECIMEN\n" + 
			"LEFT JOIN (SELECT "+sample+" from MEASUREMENT_GENE_PANEL left join CONCEPT_DATA_SOURCES on MEASUREMENT_GENE_PANEL.data_source_concept_id=" + 
			"CONCEPT_DATA_SOURCES.data_source_concept_id where panel_name=:panel_name) as t1 on t1.specimen_id=SPECIMEN.specimen_id\n" + 
			"LEFT JOIN PERSON on SPECIMEN.person_id=PERSON.person_id\n"+
			"where (SPECIMEN.specimen_concept_id=2 or SPECIMEN.specimen_concept_id=3) and SPECIMEN.person_id=:person_id";
//	private String getTumourSpecimenBaseline ="select TUMOUR_SPECIMEN_BASELINE.baseline_number from MEASUREMENT_GENE_PANEL\n" + 
//			"left JOIN TUMOUR_SPECIMEN_BASELINE on MEASUREMENT_GENE_PANEL.specimen_id=TUMOUR_SPECIMEN_BASELINE.specimen_id\n" + 
//			"where MEASUREMENT_GENE_PANEL.measurement_gene_panel_id=:genePanelId";
	
	
	public void setDataSource(DataSource dataSource) {
	    this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	public static GenomicsDataDAO getInstance(DataSource datasource) {
        if(INSTANCE == null) {
            INSTANCE = new GenomicsDataDAOImpl(datasource);
        }
        return INSTANCE;
    }
	
	private GenomicsDataDAOImpl(DataSource datasource) {
		setDataSource(datasource);
	}

	@Override
	public List<ShortVariant> getShortVariants(Integer measurement_gene_panel_id) {
		List<ShortVariant> values = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("measurement_gene_panel_id", measurement_gene_panel_id);
		try {
			values=namedParameterJdbcTemplate.query(getShortVariantsSQL, parameters, new BeanPropertyRowMapper<ShortVariant>(ShortVariant.class));
		} catch (Exception e) {
			log.log(Level.SEVERE, "getShortVariantsSQL", e);
		}
		return values;
	}

	@Override
	public List<CopyNumberAlteration> getCopyNumberAlterations(Integer measurement_gene_panel_id) {
		List<CopyNumberAlteration> values = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("measurement_gene_panel_id", measurement_gene_panel_id);
		try {
			values=namedParameterJdbcTemplate.query(getCopyNumberAlterationSQL, parameters, new BeanPropertyRowMapper<CopyNumberAlteration>(CopyNumberAlteration.class));
		} catch (Exception e) {
			log.log(Level.SEVERE, "getShortVariantsSQL", e);
		}
		return values;
	}

	@Override
	public List<Rearrangement> getRearrangements(Integer measurement_gene_panel_id) {
		List<Rearrangement> values = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("measurement_gene_panel_id", measurement_gene_panel_id);
		try {
			values=namedParameterJdbcTemplate.query(getRearrangementsSQL, parameters, new BeanPropertyRowMapper<Rearrangement>(Rearrangement.class));
		} catch (Exception e) {
			log.log(Level.SEVERE, "getShortVariantsSQL", e);
		}
		return values;
	}

	@Override
	public List<ShortVariant> getShortVariantsForPerson(String source_id, Integer person_id) {
		List<ShortVariant> values = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("person_id", person_id);
		parameters.addValue("panel_name", source_id);
		try {
			values=namedParameterJdbcTemplate.query(getShortVariantsByPersonId, parameters, new BeanPropertyRowMapper<ShortVariant>(ShortVariant.class));
		} catch (Exception e) {
			log.log(Level.SEVERE, "getShortVariantsSQL", e);
		}
		return values;
	}

	@Override
	public List<CopyNumberAlteration> getCopyNumberAlterationsForPerson(String source_id, Integer person_id) {
		List<CopyNumberAlteration> values = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("person_id", person_id);
		parameters.addValue("panel_name", source_id);
		try {
			values=namedParameterJdbcTemplate.query(getCopyNumberAlterationByPersonId, parameters, new BeanPropertyRowMapper<CopyNumberAlteration>(CopyNumberAlteration.class));
		} catch (Exception e) {
			log.log(Level.SEVERE, "getShortVariantsSQL", e);
		}
		return values;
	}

	@Override
	public List<Rearrangement> getRearrangementsForPerson(String source_id, Integer person_id) {
		List<Rearrangement> values = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("person_id", person_id);
		parameters.addValue("panel_name", source_id);
		try {
			values=namedParameterJdbcTemplate.query(getRearrangementsByPersonId, parameters, new BeanPropertyRowMapper<Rearrangement>(Rearrangement.class));
		} catch (Exception e) {
			log.log(Level.SEVERE, "getShortVariantsSQL", e);
		}
		return values;
	}

	@Override
	public List<FMSample> getBloodSamplesForPerson(String source_id, Integer person_id) {
		List<FMSample> values = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("person_id", person_id);
		parameters.addValue("panel_name", source_id);
		try {
			values=namedParameterJdbcTemplate.query(getSpecimenSamplesBlood, parameters, new BeanPropertyRowMapper<FMSample>(FMSample.class));
		} catch (Exception e) {
			log.log(Level.SEVERE, "getShortVariantsSQL", e);
		}
		return values;
	}
	
	@Override
	public List<FMSample> getTumourSamplesForPerson(String source_id, Integer person_id) {
		List<FMSample> values = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("person_id", person_id);
		parameters.addValue("panel_name", source_id);
		try {
			values=namedParameterJdbcTemplate.query(getSpecimenSamplesTumour, parameters, new BeanPropertyRowMapper<FMSample>(FMSample.class));
		} catch (Exception e) {
			log.log(Level.SEVERE, "getShortVariantsSQL", e);
		}
		return values;
	}

	@Override
	public ShortVariant getShortVariantByGeneVariantID(Integer gene_variant_id) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("gene_variant_id", gene_variant_id);
		ShortVariant sv = namedParameterJdbcTemplate.queryForObject(getShortVariantByGeneId,parameters, new BeanPropertyRowMapper<ShortVariant>(ShortVariant.class));
		return sv;
	}

	@Override
	public CopyNumberAlteration getCopyNumberAlterationByGeneVariantID(Integer gene_variant_id) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("gene_variant_id", gene_variant_id);
		CopyNumberAlteration cna = namedParameterJdbcTemplate.queryForObject(getCopyNumberAlterationByGeneId,parameters, new BeanPropertyRowMapper<CopyNumberAlteration>(CopyNumberAlteration.class));
		return cna;
	}

	@Override
	public Rearrangement getRearrangementByGeneVariantID(Integer gene_variant_id) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("gene_variant_id", gene_variant_id);
		Rearrangement r = namedParameterJdbcTemplate.queryForObject(getRearrangementByGeneId, parameters, new BeanPropertyRowMapper<Rearrangement>(Rearrangement.class));
		return r;
	}

	@Override
	public ShortVariant getTumourShortVariantByGeneVariantID(Integer gene_variant_id) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("gene_variant_id", gene_variant_id);
		ShortVariant sv = namedParameterJdbcTemplate.queryForObject(getShortVariantByGeneIdTumour,parameters, new BeanPropertyRowMapper<ShortVariant>(ShortVariant.class));
		return sv;
	}

	@Override
	public CopyNumberAlteration getTumourCopyNumberAlterationByGeneVariantID(Integer gene_variant_id) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("gene_variant_id", gene_variant_id);
		CopyNumberAlteration cna = namedParameterJdbcTemplate.queryForObject(getCopyNumberAlterationByGeneIdTumour,parameters, new BeanPropertyRowMapper<CopyNumberAlteration>(CopyNumberAlteration.class));
		return cna;
	}

	@Override
	public Rearrangement getTumourRearrangementByGeneVariantID(Integer gene_variant_id) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("gene_variant_id", gene_variant_id);
		Rearrangement r = namedParameterJdbcTemplate.queryForObject(getRearrangementByGeneIdTumour, parameters, new BeanPropertyRowMapper<Rearrangement>(Rearrangement.class));
		return r;
	}

//	@Override
//	public Integer getTumourBaseline(Integer measurement_gene_panel_id) {
//		MapSqlParameterSource parameters = new MapSqlParameterSource();
//		parameters.addValue("genePanelId", measurement_gene_panel_id);
//		return namedParameterJdbcTemplate.queryForObject(getTumourSpecimenBaseline, parameters, Integer.class);
//	}
}
