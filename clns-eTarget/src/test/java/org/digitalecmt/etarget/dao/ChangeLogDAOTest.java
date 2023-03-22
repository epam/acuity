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

import java.util.List;

import org.digitalecmt.etarget.config.TargetConfiguration;
import org.digitalecmt.etarget.dbentities.ChangeLog;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ChangeLogDAOTest {

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
	public void test() {
		ChangeLogDAO changeLog = context.getBean(ChangeLogDAO.class);
		changeLog.addChange("anja@bindrich.de", "MeetingOutcome", "notes", 21, "sdasdasdasda", "abc");
		List<ChangeLog> mychanges = changeLog.getChangesByUser("anja@bindrich.de");
		assertTrue(mychanges.size()>0);
		int size = mychanges.size();
		assertTrue(mychanges.get(mychanges.size()-1).getNew_value().compareTo("abc")==0);
		assertTrue(mychanges.get(mychanges.size()-1).getOld_value().compareTo("sdasdasdasda")==0);
		assertTrue(mychanges.get(mychanges.size()-1).getField_name().compareTo("notes")==0);
		assertTrue(mychanges.get(mychanges.size()-1).getTable_name().compareTo("MeetingOutcome")==0);
		assertTrue(mychanges.get(mychanges.size()-1).getReference_id().compareTo(21)==0);
		assertTrue(mychanges.get(mychanges.size()-1).getUpdated_by().compareTo("anja@bindrich.de")==0);
		assertTrue(mychanges.get(mychanges.size()-1).getUpdated_on()!=null);
		changeLog.deleteChangeById(mychanges.get(mychanges.size()-1).getChangelog_id());
		List<ChangeLog> after = changeLog.getChangesByUser("anja@bindrich.de");
		assertTrue(after.size()==size-1);
	}

}
