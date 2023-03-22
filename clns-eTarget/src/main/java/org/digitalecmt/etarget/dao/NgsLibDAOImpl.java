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

import java.sql.Date;
import java.util.List;

import javax.sql.DataSource;

import org.digitalecmt.etarget.dbentities.NgsLib;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class NgsLibDAOImpl extends JdbcDaoSupport implements NgsLibDAO {
	private String subset = "specimen.baseline_number,run_number,genePanel.measurement_gene_panel_id,ngs_library_cfdna_input,cfdna_input_colour_key,"
			+ "average_read_depth,average_read_depth_colour_key,bioinformatics_pipeline,level_of_detection,"
			+ "exploratory_comment,ngs_comment,specimen_date,specimen.specimen_id,analysis_failed,no_mutations_found,ngs_run,panel_name, "
			+ "ingestion_date,person.target_id, date_issued";
	private String ngsLibQuery="SELECT "+subset+"  FROM dbo.SPECIMEN AS specimen\n" + 
			"LEFT JOIN (SELECT * from MEASUREMENT_GENE_PANEL where ngs_run!='FM') as genePanel  ON specimen.specimen_id = genePanel.specimen_id "
			+ "FULL OUTER JOIN dbo.CONCEPT_DATA_SOURCES ON genePanel.data_source_concept_id = CONCEPT_DATA_SOURCES.data_source_concept_id "
			+ "FULL OUTER JOIN dbo.PERSON as person on specimen.person_id=person.person_id LEFT JOIN REPORT on genePanel.measurement_gene_panel_id=REPORT.measurement_gene_panel_id "
			+ "WHERE specimen.person_id=? and specimen_concept_id=1 and (run_number is not null or specimen.baseline_number<2) and (ngs_run is null or ngs_run!='FM')"
			+ "order by genePanel.measurement_gene_panel_id";
	private String latest="SELECT top 1 "+subset+" \n" + 
			"from MEASUREMENT_GENE_PANEL AS genePanel \n" + 
			"join SPECIMEN as specimen on specimen.specimen_id=genePanel.specimen_id\n" + 
			"FULL OUTER JOIN dbo.PERSON as person on specimen.person_id=person.person_id " +
			"FULL OUTER JOIN dbo.CONCEPT_DATA_SOURCES ON genePanel.data_source_concept_id = CONCEPT_DATA_SOURCES.data_source_concept_id LEFT JOIN REPORT on genePanel.measurement_gene_panel_id=REPORT.measurement_gene_panel_id\n" + 
			"where genePanel.specimen_id=? and genePanel.baseline_number =? and \n" + 
			"run_number=? and (ngs_run is null or ngs_run!='FM') order by measurement_gene_panel_id desc, run_number desc";
	
	private String tumourPanel="select top 1 panel_name from CONCEPT_DATA_SOURCES where valid_start_date<=? order by valid_start_date desc";
	
	public NgsLibDAOImpl(DataSource dataSource) {
		setDataSource(dataSource);
	}
	@Override
	public List<NgsLib> findNgsLibByPersonID(int personID) {
		List<NgsLib> values = null;
		try {
			JdbcTemplate template = getJdbcTemplate();
			values= template.query(ngsLibQuery, new Object[] {personID}, 
					new BeanPropertyRowMapper<NgsLib>(NgsLib.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return values;
	}
	@Override
	public NgsLib findLatestBySpecimenRun(int specimen_id, int baseline, int run) {
		NgsLib values = null;
		try {
			JdbcTemplate template = getJdbcTemplate();
			try {
				values= template.queryForObject (latest, new Object[] {specimen_id, baseline, run}, new BeanPropertyRowMapper<NgsLib>(NgsLib.class));
			}catch (IncorrectResultSizeDataAccessException err) {
				return null;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return values;
	}
	
	@Override
	public List<NgsLib> findFMBloodByPersonID(int personID) {
		//TODO
		return this.findNgsLibByPersonID(personID);
	}
	
	@Override
	public List<NgsLib> findFMTumourByPersonID(int personID) {
		//TODO
		return this.findNgsLibByPersonID(personID);
	}
	
	@Override
	public String findTumourPanelName(Date blood_issue_date) {
		if(blood_issue_date==null) {
			return null;
		}
		String value = null;
		try {
			JdbcTemplate template = getJdbcTemplate();
			value= template.queryForObject(tumourPanel, new Object[] {blood_issue_date}, 
					String.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
}
