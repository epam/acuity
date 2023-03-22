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
import java.util.Map;

import org.digitalecmt.etarget.config.TargetConfiguration;
import org.digitalecmt.etarget.dbentities.TumourNgs;
import org.digitalecmt.etarget.dbentities.TumourNgsExt;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TumourNgsDAOTest {
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
	public void testfindTumourNgsbyPersonID() {
		TumourNgsDAO tumourNgs = context.getBean(TumourNgsDAO.class);
		List<TumourNgs> result = tumourNgs.findTumourNgsByPersonID(2);
		System.out.println(result.size());
		assertTrue(result.size()==3);
		List<TumourNgsExt> tumourExt = TumourNgsExt.getTumourNgsExtList(result);
		Map<String,Object> map=tumourExt.get(0).getTumourMap();
		System.out.println(map);
		assertTrue(map.containsKey("analysisFailed"));
	}
	
	@Test
	public void testfindTumourNgsbyPersonID6() {
		TumourNgsDAO tumourNgs = context.getBean(TumourNgsDAO.class);
		List<TumourNgs> result = tumourNgs.findTumourNgsByPersonID(6);
		System.out.println(result.size());
		assertTrue(result.size()==1);
		List<TumourNgsExt> tumourExt = TumourNgsExt.getTumourNgsExtList(result);
		Map<String,Object> map=tumourExt.get(0).getTumourMap();
		System.out.println(map);
		//assertTrue(map.containsKey("analysisFailed"));
	}
	
	@Test
	public void testfindTumourNgsbyPersonID19() {
		TumourNgsDAO tumourNgs = context.getBean(TumourNgsDAO.class);
		List<TumourNgs> result = tumourNgs.findTumourNgsByPersonID(19);
		System.out.println(result.size());
		assertTrue(result.size()==2);
		List<TumourNgsExt> tumourExt = TumourNgsExt.getTumourNgsExtList(result);
		System.out.println(tumourExt.get(0).getPreclin_id());
		assertTrue(tumourExt.get(0).getPreclin_id()!=null);
		Map<String,Object> map=tumourExt.get(0).getTumourMap();
		System.out.println(map);
		assertTrue(map.containsValue("S24/00222-1"));
		Map<String,Object> map2=tumourExt.get(1).getTumourMap();
		System.out.println(map2);
		assertTrue(map2.containsValue("S25/00223-1"));
		//assertTrue(map.containsKey("analysisFailed"));
	}
	
	@Test
	public void testfindTumourNgsbyGeneVarientID() {
		TumourNgsDAO tumourNgs = context.getBean(TumourNgsDAO.class);
		TumourNgs result = tumourNgs.findTumourNgsByGeneVarientID(2, 79);
		assertTrue(result.getFilename().compareTo("17027126.n17.pdf")==0);
	}
}
