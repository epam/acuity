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

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class PersonDAOImpl extends JdbcDaoSupport implements PersonDAO {
	
	private static final Logger log = Logger.getLogger(PersonDAOImpl.class.getName());
	
	private String selectTarId="select top 1 target_id from PERSON where person_id=?";
	private String selectReports="select filename from REPORT \n" + 
			"left join MEASUREMENT_GENE_PANEL on REPORT.measurement_gene_panel_id=MEASUREMENT_GENE_PANEL.measurement_gene_panel_id\n" + 
			"left join SPECIMEN on SPECIMEN.specimen_id=MEASUREMENT_GENE_PANEL.specimen_id\n" + 
			"where SPECIMEN.person_id=? and filename is not null";
	
	
	
	public PersonDAOImpl(DataSource dataSource) {
		setDataSource(dataSource);
	}
	
	@Override
	public String getTarId(int person_id) {
		JdbcTemplate template = getJdbcTemplate();
		String tar_id = template.queryForObject(selectTarId, new Object[] {person_id}, String.class);
		return tar_id;
	}
	
	

	@Override
	public Boolean deltePersonByPersonId(int person_id) {
		JdbcTemplate template = getJdbcTemplate();
		try {
			template.update("exec DELETE_PERSON_PROC ?", new Object[] {person_id});
			return true;
		} catch (Exception e) {
			log.log(Level.SEVERE, "deletion of person " + person_id + " failed\n", e);
			return false;
		}
	}

	@Override
	public List<String> getReports(int person_id) {
		JdbcTemplate template = getJdbcTemplate();
		List<String> reports = template.queryForList(selectReports, new Object[] {person_id}, String.class);
		return reports;
	}

}
