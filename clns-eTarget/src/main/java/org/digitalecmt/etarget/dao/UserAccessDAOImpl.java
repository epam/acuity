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

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class UserAccessDAOImpl extends JdbcDaoSupport implements UserAccessDAO {

	private static final Logger log = Logger.getLogger(UserAccessDAOImpl.class.getName());
	
	private String componentQuery="select count(*) from COMPONENTS\n" + 
			"LEFT JOIN USER_ROLES on COMPONENTS.roleID=USER_ROLES.roleID\n" + 
			"LEFT JOIN WHITELIST on COMPONENTS.roleID= WHITELIST.roleID\n" + 
			"where userID=? and COMPONENTS.component=?;";
	
	private String endpointQuery="select count(*) from ENDPOINTS\n" + 
			"LEFT JOIN USER_ROLES on ENDPOINTS.roleID=USER_ROLES.roleID\n" + 
			"LEFT JOIN WHITELIST on ENDPOINTS.roleID= WHITELIST.roleID\n" + 
			"where userID=? and ENDPOINTS.endpoint=?;";
	
	private String queryRole="select count(*) from USER_ROLES\n" + 
			"LEFT JOIN WHITELIST on WHITELIST.roleID= USER_ROLES.roleID\n" + 
			"where userID=? and roleName like ?;";
	
	public UserAccessDAOImpl(DataSource dataSource) {
		setDataSource(dataSource);
	}
	
	@Override
	public Boolean isUserAllowedComponent(String userID, String component) {
		JdbcTemplate template = getJdbcTemplate();
		Integer count=template.queryForObject(componentQuery,new Object[] {userID, component}, Integer.class);
		if(count>0) return Boolean.TRUE;
		return Boolean.FALSE;
	}

	@Override
	public Boolean isUserAllowedEndpoint(String userID, String endpoint) {
		JdbcTemplate template = getJdbcTemplate();
		Integer count=template.queryForObject(endpointQuery,new Object[] {userID, endpoint}, Integer.class);
		if(count>0) return Boolean.TRUE;
		return Boolean.FALSE;
	}

	@Override
	public Boolean isUserDeactivated(String userID) {
		return isUserOfType(userID, "DeactivatedUser");
	}

	@Override
	public Boolean isAdmin(String userID) {
		return isUserOfType(userID,"Gatekeeper");
	}
	
	public Boolean isUserOfType(String userID, String role) {
		JdbcTemplate template = getJdbcTemplate();
		Integer count=template.queryForObject(queryRole, new Object[] {userID, role}, Integer.class);
		if(count>0) return Boolean.TRUE;
		return Boolean.FALSE;
	}

	@Override
	public Boolean isChiefInvestigator(String userID) {
		return isUserOfType(userID,"Chief Investigator");
	}
	
	@Override
	public Boolean hasUserAccount(String userID) {
		return isUserOfType(userID,"%");
	}

}
