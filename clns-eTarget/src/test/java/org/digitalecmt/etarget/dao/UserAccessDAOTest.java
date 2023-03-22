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

import static org.junit.Assert.*;

import org.digitalecmt.etarget.config.TargetConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class UserAccessDAOTest {
	
	private static ApplicationContext context;
	
	@BeforeClass
	public static void setUpClass() {
		try {
			context = new AnnotationConfigApplicationContext(TargetConfiguration.class);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testAdmin() {
		UserAccessDAO userAccess = context.getBean(UserAccessDAO.class);
		assertTrue(userAccess.isAdmin("anja@bindrich.de"));
		assertFalse(userAccess.isAdmin("rob.dunne@apps.idecide.science"));
		assertFalse(userAccess.isAdmin("nobody@somewhere.co.uk"));	
	}
	
	@Test
	public void testDisabled() {
		UserAccessDAO userAccess = context.getBean(UserAccessDAO.class);
		assertFalse(userAccess.isUserDeactivated("anja@bindrich.de"));
		assertTrue(userAccess.isUserDeactivated("rob.dunne@apps.idecide.science"));
		assertFalse(userAccess.isUserDeactivated("nobody@somewhere.co.uk"));	
	}
	
	@Test 
	public void testChiefInvestigator() {
		UserAccessDAO userAccess = context.getBean(UserAccessDAO.class);
		assertTrue(userAccess.isChiefInvestigator("julie.stevenson@apps.idecide.science"));
		assertFalse(userAccess.isChiefInvestigator("anja@bindrich.de"));
		assertFalse(userAccess.isChiefInvestigator("rob.dunne@apps.idecide.science"));
		assertFalse(userAccess.isChiefInvestigator("nobody@somewhere.co.uk"));	
	}
	
	@Test
	public void testAllowEndpoint() {
		UserAccessDAO userAccess = context.getBean(UserAccessDAO.class);
		assertTrue(userAccess.isUserAllowedEndpoint("anja@bindrich.de", "Admin"));
		assertTrue(userAccess.isUserAllowedEndpoint("anja@bindrich.de", "PostMutationSelection"));
		assertFalse(userAccess.isUserAllowedEndpoint("anja@bindrich.de", "adminlink"));
		assertFalse(userAccess.isUserAllowedEndpoint("rob.dunne@apps.idecide.science", "Admin"));
		assertFalse(userAccess.isUserAllowedEndpoint("rob.dunne@apps.idecide.science", "PostMutationSelection"));
		assertTrue(userAccess.isUserAllowedEndpoint("richard.hoskins@apps.idecide.science", "GetAllPatients"));
		assertFalse(userAccess.isUserAllowedEndpoint("richard.hoskins@apps.idecide.science", "PostMutationSelection"));
		assertFalse(userAccess.isUserAllowedEndpoint("nobody@somewhere.co.uk", "GetAllPatients"));
		
	}
	
	@Test
	public void testAllowComponent() {
		UserAccessDAO userAccess = context.getBean(UserAccessDAO.class);
		assertTrue(userAccess.isUserAllowedComponent("anja@bindrich.de", "adminlink"));
		assertFalse(userAccess.isUserAllowedComponent("anja@bindrich.de", "PostMutationSelection"));
		assertFalse(userAccess.isUserAllowedComponent("rob.dunne@apps.idecide.science", "patienttable"));
		assertFalse(userAccess.isUserAllowedComponent("rob.dunne@apps.idecide.science", "admin"));
		assertTrue(userAccess.isUserAllowedComponent("richard.hoskins@apps.idecide.science", "patienttable"));
		assertFalse(userAccess.isUserAllowedComponent("richard.hoskins@apps.idecide.science", "admin"));
		assertFalse(userAccess.isUserAllowedComponent("nobody@somewhere.co.uk", "patienttable"));
	}
	
	@Test
	public void testhasUserAccount() {
		UserAccessDAO userAccess = context.getBean(UserAccessDAO.class);
		assertTrue(userAccess.hasUserAccount("anja@bindrich.de"));
		assertFalse(userAccess.hasUserAccount("someane@else.com"));
		assertTrue(userAccess.hasUserAccount("rob.dunne@apps.idecide.science"));
	}

}
