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

import org.digitalecmt.etarget.dbentities.MeetingOutcomeGeneVariant;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class MeetingOutcomeGeneVariantDAOImpl extends JdbcDaoSupport implements MeetingOutcomeGeneVariantDAO {
	
	private String select="SELECT * FROM MEETING_OUTCOME_GENE_VARIANT where mo_id=?;";
	private String add="INSERT INTO MEETING_OUTCOME_GENE_VARIANT (mo_id, measurement_gene_variant_id, type) VALUES(?,?,?);";
	private String remove="DELETE FROM MEETING_OUTCOME_GENE_VARIANT WHERE mo_id=? and measurement_gene_variant_id = ? and type=?;";
	
	public MeetingOutcomeGeneVariantDAOImpl(DataSource datasource) {
		setDataSource(datasource);
	}
	
	@Override
	public List<MeetingOutcomeGeneVariant> getGeneVariantsForMeetingOutcome(int mo_id) throws DataAccessException {
		List<MeetingOutcomeGeneVariant> values=null;
		JdbcTemplate template = getJdbcTemplate();
		values= template.query(select, new Object[] {mo_id},
				new BeanPropertyRowMapper<MeetingOutcomeGeneVariant>(MeetingOutcomeGeneVariant.class));
		return values;
	}

	@Override
	public void addGeneVariantToMeetingOutcome(int mo_id, int gene_variant_id, String type) throws DataAccessException {
		JdbcTemplate template = getJdbcTemplate();
		template.update(add, mo_id, gene_variant_id, type);
	}

	@Override
	public void removeGeneVariantFromMeetingOutcome(int mo_id, int gene_variant_id, String type) throws DataAccessException {
		JdbcTemplate template = getJdbcTemplate();
		template.update(remove, mo_id, gene_variant_id, type);
	}

}
