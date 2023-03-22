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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.digitalecmt.etarget.dbentities.ConceptGene;
import org.digitalecmt.etarget.dbentities.ConceptDataSource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

public class GenePanelDAOImpl implements GenePanelDAO {
	private static Logger log = Logger.getLogger(GenePanelDAOImpl.class.getName());
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	private String getGenePanelsSQL="select * from CONCEPT_DATA_SOURCES where valid_start_date is not null order by valid_start_date";
	private String addGenePanelSQL="insert into CONCEPT_DATA_SOURCES (panel_name, valid_start_date) values (:panel_name, :date)";
	private String deleteGenesOfPanelSQL="delete from GENE_PANEL where data_source_concept_id=:panel_id";
	private String deleteGenePanelSQL="delete from CONCEPT_DATA_SOURCES where data_source_concept_id=:panel_id";
	private String insertGenePanel="insert into GENE_PANEL (data_source_concept_id, concept_gene_id) values(:panel_id, :gene_id)";
	
	private String getGenesSQL="select * from CONCEPT_GENE order by gene_concept_id";
	private String getGeneConcpetID="select top 1 gene_concept_id from CONCEPT_GENE where gene_name=:gene_name order by gene_concept_id desc";
	private String addGeneSQL="IF NOT EXISTS (SELECT * from CONCEPT_GENE where gene_name=:gene_name) "
			+ " INSERT INTO CONCEPT_GENE (gene_name) values (:gene_name)";
	private String deleteGeneSQL="delete from CONCEPT_GENE where gene_concept_id=:gene_id";
	private String getGenesForPanel = "select gene_concept_id, gene_name, chromosome_location, vocabulary_id from CONCEPT_GENE\n" + 
			"join GENE_PANEL on CONCEPT_GENE.gene_concept_id=GENE_PANEL.concept_gene_id\n" + 
			"where GENE_PANEL.data_source_concept_id=(:gene_panel_id)";
	
	
	
	public void setDataSource(DataSource dataSource) {
	    this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	public GenePanelDAOImpl(DataSource dataSource) {
		setDataSource(dataSource);
	}

	@Override
	public List<ConceptDataSource> getGenePanels() {
		List<ConceptDataSource> values=null;
		try {
			values=namedParameterJdbcTemplate.query(getGenePanelsSQL, new BeanPropertyRowMapper<ConceptDataSource>(ConceptDataSource.class));
		} catch (Exception e) {
			log.log(Level.SEVERE, "getGenePanels database problem", e);
		}
		return values;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int addGenePanel(String genePanelName, String date, List<Integer> genes) {
		try {
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			KeyHolder keyHolder = new GeneratedKeyHolder();
			parameters.addValue("panel_name", genePanelName);
			parameters.addValue("date", date);
			int rows=namedParameterJdbcTemplate.update(addGenePanelSQL, parameters, keyHolder, new String[]{"data_source_concept_id"});
			System.out.println(parameters.getValue("panel_name")+ " " + rows);
			Number gene_panel_id=keyHolder.getKey();
			List<Map<String, Object>> batchValues = new ArrayList<>(genes.size());
			for (Integer gene_id : genes) {
			    batchValues.add(
			            new MapSqlParameterSource("panel_id", gene_panel_id)
			                    .addValue("gene_id", gene_id)
			                    .getValues());
			}
			System.out.println(batchValues.size());
			namedParameterJdbcTemplate.batchUpdate(insertGenePanel,
                    batchValues.toArray(new Map[genes.size()]));
			return gene_panel_id.intValue();
		} catch(Exception e) {
			log.log(Level.SEVERE, "addGenePanel database problem", e);
			return -1;
		}
	}

	@Override
	public boolean deleteGenePanel(int genePanel) {
		try {
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("panel_id", genePanel);
			namedParameterJdbcTemplate.update(deleteGenesOfPanelSQL, parameters);
			int success=namedParameterJdbcTemplate.update(deleteGenePanelSQL, parameters);
			if(success==1) {
				return true;
			}
		} catch(Exception e) {
			log.log(Level.SEVERE, "deleteGenePanel database problem", e);
			return false;
		}
		return false;
	}

	@Override
	public List<ConceptGene> getAllGenes() {
		List<ConceptGene> values=null;
		try {
			values=namedParameterJdbcTemplate.query(getGenesSQL, new BeanPropertyRowMapper<ConceptGene>(ConceptGene.class));
		} catch (Exception e) {
			log.log(Level.SEVERE, "getGenes database problem", e);
		}
		return values;
	}

	@Override
	public int addGene(String geneName) {
		try {
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("gene_name", geneName.toUpperCase());
			int rows=namedParameterJdbcTemplate.update(addGeneSQL, parameters);
			System.out.println("rows affected: " + rows);
			int geneConceptId= namedParameterJdbcTemplate.queryForObject(getGeneConcpetID, parameters, Integer.class);
			return geneConceptId;
		} catch(Exception e) {
			log.log(Level.SEVERE, "addGene database problem", e);
			return -1;
		}
	}

	@Override
	public boolean deleteGene(int gene_id) {
		try {
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("gene_id", gene_id);
			int success=namedParameterJdbcTemplate.update(deleteGeneSQL, parameters);
			if(success==1) {
				return true;
			}
		} catch(Exception e) {
			log.log(Level.SEVERE, "deleteGenePanel database problem", e);
			return false;
		}
		return false;
	}

	@Override
	public Map<String, Map<String,Object>> getGenePanelMap() {
//		Map<String, List<ConceptGene>> genePanels= new HashMap<>();
		Map<String, Map<String,Object>> genePanels= new HashMap<>();
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		List<ConceptDataSource> names=this.getGenePanels();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		for(ConceptDataSource cgp : names) {
			Map<String,Object> properties = new HashMap<>();
			parameters.addValue("gene_panel_id", cgp.getData_source_concept_id());
			List<ConceptGene> values=null;
			values=namedParameterJdbcTemplate.query(getGenesForPanel, parameters, new BeanPropertyRowMapper<ConceptGene>(ConceptGene.class));
			properties.put("genes", values);
			properties.put("startDate", cgp.getValid_start_date()==null?"":sdf.format(cgp.getValid_start_date()));
			properties.put("genepanel", cgp.getPanel_name());
			genePanels.put(cgp.getPanel_name(), properties);
		}
		return genePanels;
	}

}
