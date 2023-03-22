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
import org.digitalecmt.etarget.dbentities.Search;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.digitalecmt.etarget.dbentities.GeneSearch;
import org.digitalecmt.etarget.dbentities.PDXCDXSearch;

public class SearchDAOTest {
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
		SearchDAO search = context.getBean(SearchDAO.class);
		List<Search> result = search.getAllPatientsSearch();
		System.out.println(result.size());
		assertTrue(result.size()>0);
		System.out.println(result.get(0).getCondition_name());
		System.out.println(result.get(0).getCdna_change());
		System.out.println(result.get(0).getAmino_acid_change());
		assertTrue(result.get(0).getCondition_name().compareTo("Colorectal")==0);
	}
	
	@Test
	public void genes() {
		SearchDAO search = context.getBean(SearchDAO.class);
		List<GeneSearch> result = search.getAllGenesSearch();
		System.out.println(result.size());
		System.out.println(result);
		assertTrue(result.size()>0);
		assertTrue(result.get(0).getGeneName().equals("AKAP9"));
	}
	
	@Test
	public void conditions() {
		SearchDAO search = context.getBean(SearchDAO.class);
		List<String> result = search.getAllConditionsSearch();
		System.out.println(result.size());
		System.out.println(result);
		assertTrue(result.size()>0);
		assertTrue(result.get(0).equals("Breast"));	
	}
	
	@Test
	public void cdxpdx() {
		SearchDAO search = context.getBean(SearchDAO.class);
		List<PDXCDXSearch> result = search.getPDXCDXPatients();
		System.out.println(result.size());
		assertTrue(result.size()>0);
		assertTrue(result.get(0).getPDXCDX().compareTo("CDX")==0);
	}

}
