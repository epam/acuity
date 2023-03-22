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

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.digitalecmt.etarget.config.TargetConfiguration;
import org.digitalecmt.etarget.dbentities.MeetingOutcome;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MeetingOutcomeDAOTest {

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
	public void testGetByPersionID() {
		MeetingOutcomeDAO meetingOutcome = context.getBean(MeetingOutcomeDAO.class); 
		List<MeetingOutcome> meetingOutcomes = meetingOutcome.getMeetingOutcomesByPersonID(6);
		System.out.println(meetingOutcomes.size());
		assertTrue(meetingOutcomes.size()==2);
		assertTrue(meetingOutcomes.get(1).getSummary().size()==14);
		assertTrue(meetingOutcomes.get(1).getSummary().get(0).get("gene").equals("ERBB2"));
		assertTrue(meetingOutcomes.get(1).getSummary().get(13).get("gene").equals("TP53"));
	}
	
	@Test
	public void testGetByPersionIDHistoric() {
		MeetingOutcomeDAO meetingOutcome = context.getBean(MeetingOutcomeDAO.class); 
		List<MeetingOutcome> meetingOutcomes = meetingOutcome.getMeetingOutcomesByPersonID(12);
		System.out.println(meetingOutcomes.size());
		assertTrue(meetingOutcomes.size()==1);
		assertTrue(meetingOutcomes.get(0).getOutcome().compareTo("Historical data")==0);
	}
	
	@Test
	public void testGetByMeetingObjectID() {
		MeetingOutcomeDAO meetingOutcome = context.getBean(MeetingOutcomeDAO.class); 
		MeetingOutcome mo = meetingOutcome.getMeetingOutcomeByMeetingID(20);
		assertTrue(mo.getNotes().compareTo("updated notes")==0);
		assertTrue(mo.getPerson_id()==6);
		Calendar c = Calendar.getInstance();
		c.setTime(mo.getMeeting_date());
		assertTrue(c.get(Calendar.YEAR)==2018);
		System.out.println(mo.getCtDNA());
		assertTrue(mo.getCtDNA()!=null);
		System.out.println(mo.getTumourNGS());
		assertTrue(mo.getTumourNGS()!=null);
	}

	@Test
	public void testUpdateNote() {
		MeetingOutcomeDAO meetingOutcome = context.getBean(MeetingOutcomeDAO.class); 
		MeetingOutcome mo = meetingOutcome.getMeetingOutcomeByMeetingID(20);
		meetingOutcome.editMeetingOutcome("anja@bindrich.de", "20", "notes", "changed note");
		MeetingOutcome mo2 = meetingOutcome.getMeetingOutcomeByMeetingID(20);
		assertTrue(mo2.getNotes().compareTo("changed note")==0);
		assertTrue(mo2.getUpdated_by().compareTo("anja@bindrich.de")==0);
		System.out.println(mo2.getLast_updated());
		meetingOutcome.editMeetingOutcome(mo.getUpdated_by(), "20", "notes", mo.getNotes());
		
	}
	
	@Test
	public void testUpdateOutcome() {
		MeetingOutcomeDAO meetingOutcome = context.getBean(MeetingOutcomeDAO.class); 
		MeetingOutcome mo = meetingOutcome.getMeetingOutcomeByMeetingID(20);
		meetingOutcome.editMeetingOutcome("anja@bindrich.de", "20", "outcome", "Actionable - Explore Clinical Trial Options\n" + 
				"Actionable But No Clinical Trial Available");
		MeetingOutcome mo2 = meetingOutcome.getMeetingOutcomeByMeetingID(20);
		assertTrue(mo2.getOutcome().compareTo("Actionable - Explore Clinical Trial Options\n" + 
				"Actionable But No Clinical Trial Available")==0);
		assertTrue(mo2.getUpdated_by().compareTo("anja@bindrich.de")==0);
		System.out.println(mo2.getLast_updated());
		meetingOutcome.editMeetingOutcome(mo.getUpdated_by(), "20", "outcome", mo.getOutcome());
		
	}
	
	@Test
	public void addMeetingOutcome() {
		MeetingOutcomeDAO meetingOutcome = context.getBean(MeetingOutcomeDAO.class); 
		boolean success=meetingOutcome.addMeetingOutcome("anja@bindrich.de", "2", "2018-08-21", "Clinical Trial Recommended Locally\n" + 
				"Clinical Trial Recommended Elsewhere\n" + 
				"Actionable - Explore Clinical Trial Options\n" + 
				"Actionable But No Clinical Trial Available\n" + 
				"Further Analysis / Information Required\n" + 
				"Further Translational Research\n" + 
				"Patient Deteriorated - No Longer Suitable\n" + 
				"Nil Actionable\n" + 
				"Relisted For Next MTB", "selected everything", 
				"[10,12,18,39]",
				"[78,79]",
				"{}",
				"{}",
				"{}");
		
		assertTrue(success);
		List<MeetingOutcome> mo=meetingOutcome.getMeetingOutcomesByPersonID(2);
		MeetingOutcome last=mo.get(0);
		assertTrue(last.getUpdated_by().equals("anja@bindrich.de"));
		System.out.println(last.getCtDNA());
		System.out.println(last.getTumourNGS());
		assertTrue(last.getCtDNA()!=null);
		assertTrue(last.getCtDNA().size()==4);
		assertTrue(last.getTumourNGS()!=null);
		assertTrue(last.getTumourNGS().size()==2);
		if(success) {
			meetingOutcome.deleteMeetingOutcome(last.getMo_id());
		}
	}
	
	@Test
	public void printMeetingOutcome() {
		MeetingOutcomeDAO meetingOutcome = context.getBean(MeetingOutcomeDAO.class);
		MeetingOutcome mo = meetingOutcome.getMeetingOutcomeByMeetingID(20);
		boolean success=meetingOutcome.printMeetingOutcome(20);
		assertTrue(success);
		MeetingOutcome mo2 = meetingOutcome.getMeetingOutcomeByMeetingID(20);
		assertTrue(mo2.getLast_printed()!=null);
		if(mo.getLast_printed()!=null) {
			assertTrue(mo.getLast_printed().getTime()<mo2.getLast_printed().getTime());
		}
	}
	
	@Test
	public void getPrinting() {
		MeetingOutcomeDAO meetingOutcome = context.getBean(MeetingOutcomeDAO.class);
		List<Integer> person_ids = meetingOutcome.getPersonIdsForPrinting();
		System.out.println(person_ids);
		assertTrue(person_ids!=null && person_ids.size()>0);
		assertTrue(person_ids.contains(11));
	}
	
	@Test
	public void getPrintingPerson() {
		MeetingOutcomeDAO meetingOutcome = context.getBean(MeetingOutcomeDAO.class);
		List<Integer> mo_ids = meetingOutcome.getMeetingOutcomesForPrinting(6);
		System.out.println(mo_ids);
		assertTrue(mo_ids!=null && mo_ids.size()>0);
		assertTrue(mo_ids.get(0)==20);
	}
	
	@Test
	public void getLastMeetingOutcomeDate() {
		MeetingOutcomeDAO meetingOutcome = context.getBean(MeetingOutcomeDAO.class);
		Calendar date = meetingOutcome.getLastMeetingOutcomeDate(6);
		assertNotNull(date);
		assertTrue(date.get(Calendar.MINUTE)==22);
	}

	@Test
	public void getSpecimenDateBlood() {
		MeetingOutcomeDAO meetingOutcome = context.getBean(MeetingOutcomeDAO.class);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2017);
		cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
		cal.set(Calendar.DAY_OF_MONTH, 14);
		java.sql.Date meeting= new java.sql.Date(cal.getTimeInMillis());
		Calendar date = meetingOutcome.getSampleDateBlood(meeting, 4);
		assertNotNull(date);
		assertTrue(date.get(Calendar.DAY_OF_MONTH)==12);
		cal.set(Calendar.YEAR, 2018);
		meeting= new java.sql.Date(cal.getTimeInMillis());
		date = meetingOutcome.getSampleDateBlood(meeting, 4);
		assertNotNull(date);
		assertTrue(date.get(Calendar.DAY_OF_MONTH)==19);
	}
	
	@Test
	public void getSpecimenDateTumour() {
		MeetingOutcomeDAO meetingOutcome = context.getBean(MeetingOutcomeDAO.class);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2017);
		cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
		cal.set(Calendar.DAY_OF_MONTH, 14);
		java.sql.Date meeting= new java.sql.Date(cal.getTimeInMillis());
		Calendar date = meetingOutcome.getSampleDateTumour(meeting, 4);
		assertNotNull(date);
		assertTrue(date.get(Calendar.DAY_OF_MONTH)==15);
	}
	
}
