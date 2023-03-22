package org.digitalecmt.etarget;

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

import java.util.HashMap;

import org.digitalecmt.etarget.config.TargetConfiguration;
import org.digitalecmt.etarget.dao.MeetingOutcomeDAO;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PostMeetingOutcomeTest {
	
	private static ApplicationContext context;
	
	@BeforeClass
	public static void setUpClass() {
		try {
			context = new AnnotationConfigApplicationContext(TargetConfiguration.class);
			new UnlockEdit("anja.leblanc@apps.idecide.science");
			new UnlockEdit("anja.leblanc@manchester.ac.uk");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testNew() {
		PostMeetingOutcome mo = new PostMeetingOutcome("anja.leblanc@manchester.ac.uk", "anja.leblanc@manchester.ac.uk", "2", "26/10/2018", 
				"Patient Deteriorated - No Longer Suitable", "some note goes here",
				"[10,12,18,39]", "[78,79]","{}","{}", "{}") ;
		String result=mo.processRequest();
		System.out.println(result);
		assertTrue(result.contains("true"));
		HashMap<String,String> rs=new Gson().fromJson(result, new TypeToken<HashMap<String,String>>() {}.getType());
		assertTrue(rs.containsKey("mo_id"));
		
		MeetingOutcomeDAO meetingOutcome = context.getBean(MeetingOutcomeDAO.class); 
		meetingOutcome.deleteMeetingOutcome(Integer.parseInt(rs.get("mo_id")));
	}
	
	@Test
	public void testNewLocked() {
		PostMeetingOutcome mo_first = new PostMeetingOutcome();
		String result_update=mo_first.editRequest("anja.leblanc@apps.idecide.science", "20", "notes", "updated notes");
		assertTrue(result_update.contains("true"));
		
		PostMeetingOutcome mo = new PostMeetingOutcome("anja.leblanc@manchester.ac.uk", "anja.leblanc@manchester.ac.uk", "6", "26/10/2018", 
				"Patient Deteriorated - No Longer Suitable", "some note goes here",
				"[10,12,18,39]", "[78,79]","","", "{}");
		String result=mo.processRequest();
		System.out.println(result);
		assertTrue(result.contains("false"));
		HashMap<String,String> rs=new Gson().fromJson(result, new TypeToken<HashMap<String,String>>() {}.getType());
		assertTrue(rs.containsKey("error"));
		
		result_update=mo_first.editRequest("anja.leblanc@apps.idecide.science", "20", "outcome", "Actionable - Explore Clinical Trial Options");
		assertTrue(result_update.contains("true"));
		
		result_update=mo_first.editRequest("anja.leblanc@manchester.ac.uk", "20", "outcome", "Actionable - Explore Clinical Trial Options");
		assertTrue(result_update.contains("false"));
	}
	
	@Test
	public void testPrint() {
		PostMeetingOutcome mo = new PostMeetingOutcome();
		String result=mo.printMeetingOutcome("anja@bindrich.de","20");
		assertTrue(result.contains("true"));
		assertTrue(result.contains("lastPrinted"));
		result=mo.printMeetingOutcome("anja.leblanc@apps.idecide.science","20");
		assertTrue(result.contains("note"));
	}
	
	@AfterClass
	public static void unlock() {
		new UnlockEdit("anja.leblanc@apps.idecide.science");
		new UnlockEdit("anja.leblanc@manchester.ac.uk");
	}

}
