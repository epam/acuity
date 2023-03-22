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

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;

import org.digitalecmt.etarget.config.TargetConfiguration;
import org.digitalecmt.etarget.dbentities.IHCReport;
import org.digitalecmt.etarget.dbentities.TumourNgs;
import org.digitalecmt.etarget.support.Formater;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import de.jollyday.HolidayCalendar;
import de.jollyday.HolidayManager;
import de.jollyday.HolidayType;
import de.jollyday.ManagerParameters;

public class IHCReportDAOTest {
	
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
	public void testFound() {
		IHCReportDAO ihc = context.getBean(IHCReportDAO.class);
		List<IHCReport> report = ihc.getIHCReport(6);
		assertTrue(report.size()==2);
		assertTrue(report.get(0).getReport_date_formatted().equals("02/10/2019"));
		assertTrue(report.get(0).getCd8_intrastromal()==null);
		assertTrue(report.get(0).getCd3_intrastromal().intValue()==368);
		assertTrue(report.get(0).getSpecimen_date_formated().equals("21/01/2016"));
	}
	
	@Test
	public void testNotFound() {
		IHCReportDAO ihc = context.getBean(IHCReportDAO.class);
		List<IHCReport> report = ihc.getIHCReport(16);
		assertTrue(report.size()==1);
		assertTrue(report.get(0).getIhc_report_id()==null);
	}
	
	@Test
	public void testFormat() {
		IHCReportDAO ihc = context.getBean(IHCReportDAO.class);
		TumourNgsDAO tumourDAO = context.getBean(TumourNgsDAO.class);
		List<IHCReport> report = ihc.getIHCReport(6);
		System.out.println(report.size());
		List<TumourNgs> sample = tumourDAO.findFMTumourByPersonID(6);
		Map<String,Map<String,Map<String,String>>> formatted = Formater.formatIHCReport(report, sample);
		System.out.println(formatted);
		assertTrue(formatted.get("1")!=null);
		Map<String,Map<String,String>> act1 = formatted.get("1");
		Map<String,String> ver1 = act1.get("1");
		assertTrue(ver1.get("personID").equals("6"));
	}
	
	@Test
	public void testHolidays() {
		HolidayManager m = HolidayManager.getInstance(ManagerParameters.create(HolidayCalendar.UNITED_KINGDOM));
		LocalDate ld = LocalDate.of(2018, Month.APRIL, 2);
		boolean isHoliday = m.isHoliday(ld, (HolidayType) null, "en");
		System.out.println(isHoliday);
		assertTrue(isHoliday);
		ld = LocalDate.of(2019, Month.APRIL, 19);
		isHoliday = m.isHoliday(ld, (HolidayType) null, "en");
		System.out.println(isHoliday);
		assertTrue(isHoliday);
		ld = LocalDate.of(2019, Month.APRIL, 22);
		isHoliday = m.isHoliday(ld, (HolidayType) null, "en");
		System.out.println(isHoliday);
		assertTrue(isHoliday);
	}

}
