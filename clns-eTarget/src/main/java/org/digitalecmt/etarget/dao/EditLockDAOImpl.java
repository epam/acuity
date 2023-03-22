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

import java.util.logging.Logger;

import javax.sql.DataSource;

import org.digitalecmt.etarget.dbentities.EditLock;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;


public class EditLockDAOImpl extends JdbcDaoSupport implements EditLockDAO {
	private static final Logger log = Logger.getLogger(EditLockDAOImpl.class.getName());
	private int lockMin=10;
	
	private String lock = "UPDATE EDIT_LOCK SET locked_on=GETDATE() WHERE person_id=? and user_id=? and NOT EXISTS (SELECT 1  FROM EDIT_LOCK WHERE EDIT_LOCK.person_id = ? and user_id!=? and DATEDIFF(MINUTE, locked_on , GETDATE())<?) " + 
			"IF @@ROWCOUNT = 0 and NOT EXISTS (SELECT 1  FROM EDIT_LOCK WHERE EDIT_LOCK.person_id = ? and user_id!=? and DATEDIFF(MINUTE, locked_on , GETDATE())<?)" + 
			"   INSERT INTO EDIT_LOCK (user_id,person_id) VALUES (?,?)";
	private String isLocked = "SELECT TOP 1 * FROM EDIT_LOCK WHERE person_id =? and not user_id=? and DATEDIFF(MINUTE, locked_on , GETDATE())<?;";
	private String getLocker= "SELECT TOP 1 * from EDIT_LOCK where person_id=? and DATEDIFF(MINUTE, locked_on , GETDATE())<?;";
	private String unlock = "DELETE FROM EDIT_LOCK WHERE person_id=? and user_id=?";
	private String unlockAll = "DELETE FROM EDIT_LOCK WHERE user_id=?";
	private String getRole = "SELECT roleID from WHITELIST where userID=?";

	public EditLockDAOImpl(DataSource dataSource, int timeoutMin) {
		setDataSource(dataSource);
		lockMin=timeoutMin;
	}
	
	@Override
	public Boolean lock(String user_id, int person_id) throws DataAccessException {
		log.info("lock " + user_id + " " + person_id);
		JdbcTemplate template = getJdbcTemplate();
		try {
			Integer role = template.queryForObject(getRole, new Object[] {user_id}, Integer.class);
			if(role!=2 && role!=6)  return Boolean.FALSE;
		} catch (EmptyResultDataAccessException e) {
			return Boolean.FALSE;
		}
		int rows = template.update(lock, person_id, user_id, person_id, user_id, lockMin, person_id, user_id, lockMin, user_id, person_id);
		if(rows==0) return Boolean.FALSE;
		else return Boolean.TRUE;
	}

	@Override
	public Boolean isLocked(String user_id, int person_id) throws DataAccessException {
		JdbcTemplate template = getJdbcTemplate();
		try {
			template.queryForObject(isLocked, new Object[] {person_id, user_id, lockMin}, new BeanPropertyRowMapper<EditLock>(EditLock.class));
			return Boolean.TRUE;
		} catch(EmptyResultDataAccessException e) {
			return Boolean.FALSE;
		}
	}

	@Override
	public void unlock(String user_id, int person_id) throws DataAccessException {
		JdbcTemplate template = getJdbcTemplate();
		template.update(unlock, person_id, user_id);
	}

	@Override
	public String getLocker(int person_id) throws DataAccessException {
		JdbcTemplate template = getJdbcTemplate();
		EditLock el = template.queryForObject(getLocker, new Object[] {person_id, lockMin}, new BeanPropertyRowMapper<EditLock>(EditLock.class));
		return el.getUser_id();
	}

	@Override
	public void unlockAll(String user_id) throws DataAccessException {
		JdbcTemplate template = getJdbcTemplate();
		int changeCount=template.update(unlockAll, user_id);
		log.info("unlock all for " + user_id + " changed " + changeCount);
	}

	@Override
	public Boolean isEditor(String user_id) throws DataAccessException {
		JdbcTemplate template = getJdbcTemplate();
		try {
			Integer role = template.queryForObject(getRole, new Object[] {user_id}, Integer.class);
			if(role!=2 && role!=6) return Boolean.FALSE;
			else return Boolean.TRUE;
		} catch (EmptyResultDataAccessException e) {
			return Boolean.FALSE;
		}
	}

}
