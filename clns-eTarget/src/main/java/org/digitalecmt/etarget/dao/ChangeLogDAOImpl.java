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

import org.digitalecmt.etarget.dbentities.ChangeLog;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class ChangeLogDAOImpl extends JdbcDaoSupport implements ChangeLogDAO {

	private String insert="INSERT INTO CHANGE_LOG (updated_by, table_name, field_name, reference_id, old_value, new_value) VALUES (?, ?, ?, ?, ?, ?);";
	private String queryUser="SELECT * FROM CHANGE_LOG WHERE updated_by=?;";
	private String deleteId="DELETE FROM CHANGE_LOG where changelog_id=?;";
	
	public ChangeLogDAOImpl(DataSource dataSource) {
		setDataSource(dataSource);
	}
	
	@Override
	public void addChange(String user_id, String table_name, String field_name, Integer reference_id, String old_value,
			String new_value) {
		try {
			JdbcTemplate template = getJdbcTemplate();
			template.update(insert, user_id, table_name, field_name, reference_id, old_value, new_value);
		} catch (Exception e) {
			e.printStackTrace();
			//TODO: throw some exception on failure
		}
	}

	@Override
	public List<ChangeLog> getChangesByUser(String user_id) {
		List<ChangeLog> values=null;
		try {
			JdbcTemplate template = getJdbcTemplate();
			values=template.query(queryUser, new Object[] {user_id},
					new BeanPropertyRowMapper<ChangeLog>(ChangeLog.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return values;
	}

	@Override
	public void deleteChangeById(Integer changeLogId) {
		try {
			JdbcTemplate template = getJdbcTemplate();
			template.update(deleteId, changeLogId);
		} catch (Exception e) {
			e.printStackTrace();
			//TODO: throw some exception on failure
		}
	}

}
