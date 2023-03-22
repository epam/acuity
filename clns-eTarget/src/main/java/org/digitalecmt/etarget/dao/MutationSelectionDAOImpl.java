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

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.digitalecmt.etarget.dbentities.SelectedGeneVariant;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class MutationSelectionDAOImpl extends JdbcDaoSupport implements MutationSelectionDAO {
	
	private static final Logger log = Logger.getLogger(MutationSelectionDAOImpl.class.getName());
	
	private String insertNew="INSERT INTO \"dbo\".\"SELECTED_GENE_VARIANT\" (person_id, measurement_gene_variant_id, type) VALUES (?,?,?)";
	private String delete="DELETE FROM \"dbo\".\"SELECTED_GENE_VARIANT\" WHERE person_id=? AND measurement_gene_variant_id=? AND type=?";
	private String query="SELECT * FROM \"dbo\".\"SELECTED_GENE_VARIANT\" WHERE person_id=? ";
//	private String queryLatest="WITH CTE AS(\n" + 
//			"   SELECT MEASUREMENT_GENE_VARIANT.measurement_gene_panel_id,\n" + 
//			"       RN = ROW_NUMBER()OVER(PARTITION BY type, baseline_number, run_number ORDER BY type, baseline_number, run_number, MEASUREMENT_GENE_PANEL.measurement_gene_panel_id desc)\n" + 
//			"   FROM SELECTED_GENE_VARIANT\n" + 
//			"join MEASUREMENT_GENE_VARIANT on MEASUREMENT_GENE_VARIANT.measurement_gene_variant_id=SELECTED_GENE_VARIANT.measurement_gene_variant_id\n" + 
//			"join MEASUREMENT_GENE_PANEL on MEASUREMENT_GENE_PANEL.measurement_gene_panel_id=MEASUREMENT_GENE_VARIANT.measurement_gene_panel_id WHERE person_id=?\n" + 
//			"and baseline_number=\n" + 
//			"(select max(SPECIMEN.baseline_number) from SPECIMEN JOIN MEASUREMENT_GENE_PANEL on SPECIMEN.specimen_id=MEASUREMENT_GENE_PANEL.specimen_id WHERE person_id=?))\n" + 
//			"select SELECTED_GENE_VARIANT.person_id, SELECTED_GENE_VARIANT.measurement_gene_variant_id, SELECTED_GENE_VARIANT.type from SELECTED_GENE_VARIANT \n" + 
//			"join MEASUREMENT_GENE_VARIANT on MEASUREMENT_GENE_VARIANT.measurement_gene_variant_id=SELECTED_GENE_VARIANT.measurement_gene_variant_id\n" + 
//			"join CTE on CTE.measurement_gene_panel_id=MEASUREMENT_GENE_VARIANT.measurement_gene_panel_id where RN=1";
//	private String queryLatest="select * from (SELECT MEASUREMENT_GENE_PANEL.measurement_gene_panel_id, ROW_NUMBER() OVER (PARTITION by data_source_concept_id, MEASUREMENT_GENE_PANEL.baseline_number, MEASUREMENT_GENE_PANEL.run_number ORDER by MEASUREMENT_GENE_PANEL.baseline_number desc, MEASUREMENT_GENE_PANEL.run_number desc, MEASUREMENT_GENE_PANEL.measurement_gene_panel_id desc) as rn from MEASUREMENT_GENE_PANEL join Specimen on SPECIMEN.specimen_id=MEASUREMENT_GENE_PANEL.specimen_id where SPECIMEN.person_id=?) t1 \n" + 
//			"join MEASUREMENT_GENE_VARIANT on t1.measurement_gene_panel_id=MEASUREMENT_GENE_VARIANT.measurement_gene_panel_id\n" + 
//			"join SELECTED_GENE_VARIANT on SELECTED_GENE_VARIANT.measurement_gene_variant_id=MEASUREMENT_GENE_VARIANT.measurement_gene_variant_id\n" + 
//			"where t1.rn=1 and SELECTED_GENE_VARIANT.person_id=?";
	private String queryLatest="select * from (select *,ROW_NUMBER() over\n" + 
			"(PARTITION by t3.data_source_concept_id, t3.baseline_number, t3.run_number, t3.specimen_concept_id ORDER by t3.baseline_number desc, t3.run_number desc, t3.measurement_gene_panel_id desc) as rn\n" + 
			"from (select t1.measurement_gene_panel_id, t1.baseline_number, t1.run_number,t1.data_source_concept_id, t1.specimen_concept_id from \n" + 
			"(SELECT MEASUREMENT_GENE_PANEL.measurement_gene_panel_id, SPECIMEN.baseline_number, \n" + 
			"MEASUREMENT_GENE_PANEL.run_number,data_source_concept_id, SPECIMEN.specimen_concept_id \n" + 
			"from MEASUREMENT_GENE_PANEL \n" + 
			"join Specimen on SPECIMEN.specimen_id=MEASUREMENT_GENE_PANEL.specimen_id \n" + 
			"where SPECIMEN.person_id=?) as t1  \n" + 
			"left outer join (SELECT MEASUREMENT_GENE_PANEL.measurement_gene_panel_id, SPECIMEN.baseline_number, MEASUREMENT_GENE_PANEL.run_number,data_source_concept_id, SPECIMEN.specimen_concept_id\n" + 
			"from MEASUREMENT_GENE_PANEL \n" + 
			"join Specimen on SPECIMEN.specimen_id=MEASUREMENT_GENE_PANEL.specimen_id \n" + 
			"where SPECIMEN.person_id=?) as t2 on t1.data_source_concept_id=t2.data_source_concept_id and t2.baseline_number>t1.baseline_number\n" + 
			") as t3) as t4 \n" + 
			"join MEASUREMENT_GENE_VARIANT on t4.measurement_gene_panel_id=MEASUREMENT_GENE_VARIANT.measurement_gene_panel_id  \n" + 
			"join SELECTED_GENE_VARIANT on SELECTED_GENE_VARIANT.measurement_gene_variant_id=MEASUREMENT_GENE_VARIANT.measurement_gene_variant_id\n" + 
			"where rn=1";
	
	private String querySelectedByGenePanelId="select MEASUREMENT_GENE_VARIANT.measurement_gene_variant_id from SELECTED_GENE_VARIANT \n" + 
			"join MEASUREMENT_GENE_VARIANT on SELECTED_GENE_VARIANT.measurement_gene_variant_id=MEASUREMENT_GENE_VARIANT.measurement_gene_variant_id\n" + 
			"join MEASUREMENT_GENE_PANEL on MEASUREMENT_GENE_VARIANT.measurement_gene_panel_id=MEASUREMENT_GENE_PANEL.measurement_gene_panel_id " +
			"where MEASUREMENT_GENE_PANEL.measurement_gene_panel_id=?";
	
	public MutationSelectionDAOImpl(DataSource dataSource) {
		setDataSource(dataSource);
	}

	@Override
	public List<SelectedGeneVariant> getSelectedMutationsByPersonID(int personID) {
		List<SelectedGeneVariant> values=null;
		try {
			JdbcTemplate template =  getJdbcTemplate();
			values=template.query(query, new Object[] {personID},
				new BeanPropertyRowMapper<SelectedGeneVariant>(SelectedGeneVariant.class));
		}catch (Exception e) {
			e.printStackTrace();
		}
		log.info("select SELECTED_GENE_VARIANT " + personID + " " + values.size());
		return values;
	}
	
	@Override
	public List<SelectedGeneVariant> getLatestSelectedMutationsByPersonID(int personID) {
		List<SelectedGeneVariant> values=null;
		try {
			JdbcTemplate template =  getJdbcTemplate();
			values=template.query(queryLatest, new Object[] {personID,personID},
				new BeanPropertyRowMapper<SelectedGeneVariant>(SelectedGeneVariant.class));
		}catch (Exception e) {
			e.printStackTrace();
		}
		log.info("select SELECTED_GENE_VARIANT " + personID + " " + values.size());
		return values;
	}

	@Override
	public void addMutation(Integer personID, Integer geneVariantID, String type) throws SQLException {
		if(personID==null || geneVariantID==null || type==null) {
			return;
		}
		JdbcTemplate template =  getJdbcTemplate();
		int rows=template.update(insertNew, new Object[] {personID,geneVariantID,type});
		log.info("Insert SELECTED_GENE_VARIANT " + personID +" " + geneVariantID+" result " + rows);
	}

	@Override
	public void deleteMutation(Integer personID, Integer geneVariantID, String type) throws SQLException {
		if(personID==null || geneVariantID==null || type==null) {
			return;
		}
		JdbcTemplate template =  getJdbcTemplate();
		int rows=template.update(delete, new Object[] {personID,geneVariantID,type});
		log.info("Delete SELECTED_GENE_VARIANT " + personID +" " + geneVariantID+" result " + rows);
	}

	@Override
	public List<Integer> getSelectedByGenePanelID(int genePanelId) throws SQLException {
		JdbcTemplate template =  getJdbcTemplate();
		List<Integer> values= template.queryForList(querySelectedByGenePanelId, Integer.class, genePanelId);
		return values;
	}

}
