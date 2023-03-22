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

import java.time.Month;
import java.util.Calendar;
import java.util.List;

import org.digitalecmt.etarget.config.TargetConfiguration;
import org.digitalecmt.etarget.dbentities.NgsLib;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class NgsLibDOATest {
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
	public void testfindNgsLibByPersionID() {
		NgsLibDAO ngsLib = context.getBean(NgsLibDAO.class);
		List<NgsLib> result = ngsLib.findNgsLibByPersonID(12);
		System.out.println(result.size());
		assertNotNull(result);
	}
	@Test
	public void testfindLatest() {
		NgsLibDAO ngsLib = context.getBean(NgsLibDAO.class);
		NgsLib result = ngsLib.findLatestBySpecimenRun(3005, 1, 2);
		assertNotNull(result);
		assertTrue(result.getMeasurement_gene_panel_id()==2781);
	}
	
	@Test
	public void testfindTumourPanelName() {
		NgsLibDAO ngsLib = context.getBean(NgsLibDAO.class);
		Calendar c=Calendar.getInstance();
		c.set(2019, 4, 28);
		java.sql.Date blood_issue_date=new java.sql.Date(c.getTimeInMillis());
		String panel = ngsLib.findTumourPanelName(blood_issue_date);
		assertNotNull(panel);
		assertTrue(panel.equals("GDL Panel"));
		c.set(2020, 4, 28);
		blood_issue_date=new java.sql.Date(c.getTimeInMillis());
		panel = ngsLib.findTumourPanelName(blood_issue_date);
		assertNotNull(panel);
		assertTrue(panel.equals("even genes"));
	}
}
