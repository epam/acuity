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

import javax.sql.DataSource;

import org.digitalecmt.etarget.dbentities.TumourNgs;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class TumourNgsDAOImpl extends JdbcDaoSupport implements TumourNgsDAO {
	private String selection=" specimen_date,specimen.specimen_id, specimen.baseline_number, specimen.tumour_id, "
			+ "specimen.preclin_id, person.target_id, "
			+ "genePanel.run_number,genePanel.measurement_gene_panel_id,comments, "
			+ "unknown_significance, pathLab.path_lab_ref,coverage,filename,date_requested";
	private String queryPerson= "SELECT "+selection+" FROM dbo.SPECIMEN AS specimen " + 
			"FULL OUTER JOIN (SELECT * from MEASUREMENT_GENE_PANEL where ngs_run!='FM') as genePanel on genePanel.specimen_id=SPECIMEN.specimen_id " + 
			"FULL OUTER JOIN dbo.PATH_LAB_REF AS pathLab ON genePanel.measurement_gene_panel_id = pathLab.measurement_gene_panel_id " + 
			"FULL OUTER JOIN dbo.GDL_REQUEST AS gdlRequest ON specimen.specimen_id = gdlRequest.specimen_id "
			+ "FULL OUTER JOIN dbo.PERSON as person ON specimen.person_id = person.person_id " + 
			"FULL OUTER JOIN dbo.REPORT AS report ON genePanel.measurement_gene_panel_id = report.measurement_gene_panel_id  "
			+ "WHERE specimen.person_id = ? and (specimen.specimen_concept_id=2 OR specimen.specimen_concept_id=3) and (ngs_run='GDL' or ngs_run IS NULL) order by coalesce(date_issued, '9999-12-12') ASC";
	private String queryGeneVarient = "SELECT * FROM TUMOURNGS WHERE person_id = ? AND gene_varient_id = ?";
	
	public TumourNgsDAOImpl(DataSource dataSource) {
		setDataSource(dataSource);
	}

	@Override
	public List<TumourNgs> findTumourNgsByPersonID(int personID) {
		List<TumourNgs> values=null;
		try {
			JdbcTemplate template = getJdbcTemplate();
			values= template.query(queryPerson, new Object[] {personID}, 
					new BeanPropertyRowMapper<TumourNgs>(TumourNgs.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return values;
	}

	@Override
	public TumourNgs findTumourNgsByGeneVarientID(int personID, int geneVarientID) {
		List<TumourNgs> values=null;
		//JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);
		try {
			JdbcTemplate template = getJdbcTemplate();
			values= template.query(queryGeneVarient, new Object[] {personID, geneVarientID}, 
					new BeanPropertyRowMapper<TumourNgs>(TumourNgs.class));
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
	public List<TumourNgs> findFMTumourByPersonID(int personID) {
		return this.findTumourNgsByPersonID(personID);
	}

}
