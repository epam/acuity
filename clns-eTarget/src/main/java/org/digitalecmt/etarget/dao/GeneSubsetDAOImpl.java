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

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.digitalecmt.etarget.dbentities.GeneSubset;
import org.digitalecmt.etarget.dbentities.GeneSubsetTumour;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class GeneSubsetDAOImpl implements GeneSubsetDAO {
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	private String selection="gene_name, specimen_date,gene_varient_id, cdna_change,amino_acid_change,variant_allele_frequency,read_depth, "
			+ "germline_frequency, functional_effect, high_confidence," + 
			"is_specific_mutation_in_panel, baseline_number, run_number, measurement_gene_panel_id, position, cosmic_url ";
	
	private String tumourSelection="gene_name, specimen_date, gene_varient_id, cdna_change, amino_acid_change, variant_allele_frequency,"
			+ "baseline_number, run_number, measurement_gene_panel_id, amino_acid_change";
	
	private String gene_exploratory ="SELECT "+selection+" FROM CTDNA_SUBSETS WHERE "
			+ "(person_id = :personID AND ngs_gene_present = 'N' AND high_confidence = 'Y' and (run_number is not null or baseline_number<2)) OR "
			+ "(person_id = :personID AND ngs_gene_present IS NULL and (run_number is not null or baseline_number<2)) ORDER BY gene_name, specimen_date ASC";
	
	private String gene_subset= "SELECT "+selection+" FROM CTDNA_SUBSETS WHERE "
			+ "(person_id = :personID AND ngs_gene_present = 'Y' AND is_specific_mutation_in_panel = 'Y' and (run_number is not null or baseline_number<2)) OR \n" + 
			"(person_id = :personID AND ngs_gene_present = 'Y' AND is_specific_mutation_in_panel = 'N' and (run_number is not null or baseline_number<2)) OR \n" + 
			"(person_id = :personID AND ngs_gene_present IS NULL and (run_number is not null or baseline_number<2)) ORDER BY high_confidence DESC, gene_name ASC, specimen_date ASC";

	private String tumour_subset="SELECT " +tumourSelection+" FROM TUMOURNGS WHERE "
			+ "(person_id = :personID and (run_number is not null or baseline_number<2)) order by baseline_number, run_number, measurement_gene_panel_id";
	
	private String gene_variant = "SELECT "+selection+" FROM CTDNA_SUBSETS WHERE person_id = :personID AND gene_varient_id = :geneVarientID";
	
//	private String latestMeasurementGenePanels = "select measurement_gene_panel_id, gene_panel_concept_id,baseline_number, run_number from \n" + 
//			"(SELECT measurement_gene_panel_id, gene_panel_concept_id, MEASUREMENT_GENE_PANEL.baseline_number, MEASUREMENT_GENE_PANEL.run_number, ROW_NUMBER() OVER \n" + 
//			"(PARTITION by gene_panel_concept_id, MEASUREMENT_GENE_PANEL.baseline_number, MEASUREMENT_GENE_PANEL.run_number \n" + 
//			"ORDER by MEASUREMENT_GENE_PANEL.baseline_number desc, MEASUREMENT_GENE_PANEL.run_number desc, MEASUREMENT_GENE_PANEL.measurement_gene_panel_id desc) as rn \n" + 
//			"from MEASUREMENT_GENE_PANEL join Specimen on SPECIMEN.specimen_id=MEASUREMENT_GENE_PANEL.specimen_id where SPECIMEN.person_id=4) dd \n" + 
//			"where rn=1 order by baseline_number DESC, run_number DESC, measurement_gene_panel_id desc";
	
	private String latestMeasurementGenePanelsBlood = "with gp as (select distinct measurement_gene_panel_id, baseline_number, run_number\n" + 
			"from CTDNA_SUBSETS where person_id=:personID),\n" + 
			"tmp as (\n" + 
			"select distinct measurement_gene_panel_id, baseline_number, run_number , ROW_NUMBER() OVER\n" + 
			"(PARTITION by run_number order by measurement_gene_panel_id desc) rn from gp\n" + 
			")\n" + 
			"select measurement_gene_panel_id from tmp where rn=1 order by run_number desc";
	
//	private String latestMeasurementGenePanels = "select measurement_gene_panel_id from\n" + 
//			"(select gene_panel_concept_id,measurement_gene_panel_id,baseline_number,run_number,rn, ROW_NUMBER() over\n" + 
//			"(PARTITION by gene_panel_concept_id, baseline_number, run_number order by gene_panel_concept_id desc, baseline_number desc, run_number desc) as row from\n" + 
//			"(SELECT gene_panel_concept_id,measurement_gene_panel_id, MEASUREMENT_GENE_PANEL.baseline_number, MEASUREMENT_GENE_PANEL.run_number, ROW_NUMBER() OVER \n" + 
//			"(PARTITION by gene_panel_concept_id \n" + 
//			"ORDER by MEASUREMENT_GENE_PANEL.baseline_number desc, MEASUREMENT_GENE_PANEL.run_number desc, MEASUREMENT_GENE_PANEL.measurement_gene_panel_id desc) as rn \n" + 
//			"from MEASUREMENT_GENE_PANEL join Specimen on SPECIMEN.specimen_id=MEASUREMENT_GENE_PANEL.specimen_id where SPECIMEN.person_id=:personID) dd\n" + 
//			") ddd where row=1 order by baseline_number DESC, run_number DESC, measurement_gene_panel_id desc\n" + 
//			"";
	
	private String geneVarientsbyGenePanelId = "select MEASUREMENT_GENE_VARIANT.measurement_gene_variant_id \n" + 
			"from MEASUREMENT_GENE_VARIANT \n" + 
			"join MEASUREMENT_GENE_PANEL on MEASUREMENT_GENE_VARIANT.measurement_gene_panel_id=MEASUREMENT_GENE_PANEL.measurement_gene_panel_id where " +
			"MEASUREMENT_GENE_PANEL.measurement_gene_panel_id=:genePanelID";
	private String geneVarientsByGeneVarientIds = "select "+selection+" FROM CTDNA_SUBSETS where gene_varient_id in (:ids)";
	

	public void setDataSource(DataSource dataSource) {
	    this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	public GeneSubsetDAOImpl(DataSource dataSource) {
		setDataSource(dataSource);
	}
	
	@Override
	public List<GeneSubset> findGeneSubsetByPersonID(int personID) {
		List<GeneSubset> values=null;
		//JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);
		try {
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("personID", personID);
			values= namedParameterJdbcTemplate.query(gene_subset, parameters, 
					new BeanPropertyRowMapper<GeneSubset>(GeneSubset.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return values;
	}

	@Override
	public List<GeneSubset> findGeneExploratoryByPersonID(int personID) {
		List<GeneSubset> values=null;
		//JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);
		try {
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("personID", personID);
			values= namedParameterJdbcTemplate.query(gene_exploratory, parameters, 
					new BeanPropertyRowMapper<GeneSubset>(GeneSubset.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return values;
	}

	@Override
	public GeneSubset findGeneSubsetByGeneVarientID(int personID, int geneVarientID) {
		List<GeneSubset> values=null;
		//JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);
		try {
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("personID", personID);
			parameters.addValue("geneVarientID", geneVarientID);
			values= namedParameterJdbcTemplate.query(gene_variant, parameters, 
					new BeanPropertyRowMapper<GeneSubset>(GeneSubset.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//should never be more than one!
		if(values.size()>0) 
			return values.get(0);
		//nothing found
		else 
			return null;
	}

	@Override
	public List<Integer> findLatestGenePanelIDsBlood(int personID) {
		List<Integer> values = null;
		try {
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("personID", personID);
			values= namedParameterJdbcTemplate.queryForList(latestMeasurementGenePanelsBlood, parameters, 
					Integer.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return values;
	}

	@Override
	public List<Integer> findGeneVarientsByGenePanelID(int genePanelID) {
		List<Integer> values = null;
		try {
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("genePanelID", genePanelID);
			values= namedParameterJdbcTemplate.queryForList(geneVarientsbyGenePanelId, parameters, 
					Integer.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return values;
	}

	@Override
	public List<GeneSubset> findGeneSubsetByGeneVarientsIDs(List<Integer> geneVarientIDs) {
		List<GeneSubset> values = new ArrayList<GeneSubset>();
		if(geneVarientIDs.size()==0) {
			return values;
		}
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("ids", geneVarientIDs);
		try {
			values= namedParameterJdbcTemplate.query(geneVarientsByGeneVarientIds, parameters, 
					new BeanPropertyRowMapper<GeneSubset>(GeneSubset.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return values;
	}
	
	@Override
	public List<GeneSubsetTumour> findTumourGeneVariantsByPersonID(int personID) {
		List<GeneSubsetTumour> values = null;
		try {
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("personID", personID);
			values= namedParameterJdbcTemplate.query(tumour_subset, parameters, 
					new BeanPropertyRowMapper<GeneSubsetTumour>(GeneSubsetTumour.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return values;
	}

}
