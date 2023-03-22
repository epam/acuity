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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.digitalecmt.etarget.dbentities.GeneName;
import org.digitalecmt.etarget.dbentities.GenePanelGene;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class GeneNameDAOImpl extends JdbcDaoSupport implements GeneNameDAO {
	
	private String query="SELECT DISTINCT geneName.gene_name FROM dbo.SPECIMEN AS specimen, dbo.MEASUREMENT_GENE_PANEL AS mesGenePanel, dbo.CONCEPT_GENE AS geneName,dbo.GENE_PANEL AS genePanel WHERE specimen.person_id = ?" + 
			" AND specimen.specimen_id = mesGenePanel.specimen_id AND mesGenePanel.data_source_concept_id = genePanel.data_source_concept_id AND genePanel.concept_gene_id = geneName.gene_concept_id ORDER BY geneName.gene_name ASC";
	
	private String allPanels="select panel_name, gene_name FROM GENE_PANEL \n" + 
			"LEFT join CONCEPT_DATA_SOURCES on GENE_PANEL.data_source_concept_id=CONCEPT_DATA_SOURCES.data_source_concept_id\n" + 
			"LEFT join CONCEPT_GENE on GENE_PANEL.concept_gene_id=CONCEPT_GENE.gene_concept_id\n" + 
			"ORDER BY panel_name, gene_name";

	

	@Override
	public List<GeneName> getGeneNamesByPersonID(int personID) {
		List<GeneName> values=null;
		try {
			JdbcTemplate template = getJdbcTemplate();
			values= template.query(query, new Object[] {personID}, 
					new BeanPropertyRowMapper<GeneName>(GeneName.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return values;
	}
	
	public GeneNameDAOImpl(DataSource dataSource) {
		setDataSource(dataSource);
	}

	@Override
	public Map<String, List<String>> getGenePanels() {
		Map<String, List<String>> genePanels = new HashMap<>();
		try {
			JdbcTemplate template = getJdbcTemplate();
			List<GenePanelGene> genelist = template.query(allPanels, new BeanPropertyRowMapper<GenePanelGene>(GenePanelGene.class));
			genelist.stream().forEach(gene -> {
				if(genePanels.get(gene.getPanel_name())==null) {
					genePanels.put(gene.getPanel_name(), new ArrayList<String>());
				}
				genePanels.get(gene.getPanel_name()).add(gene.getGene_name());
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return genePanels;
	}

}
