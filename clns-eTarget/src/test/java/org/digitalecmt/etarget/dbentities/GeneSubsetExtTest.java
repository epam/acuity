package org.digitalecmt.etarget.dbentities;

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
import org.digitalecmt.etarget.dao.GeneSubsetDAO;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class GeneSubsetExtTest {
	
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
	public void testQuery() {
		GeneSubsetDAO genesubset = context.getBean(GeneSubsetDAO.class);
		List<GeneSubset> results = genesubset.findGeneSubsetByPersonID(12);
		List<GeneSubsetExt> gensubsets=GeneSubsetExt.getGeneSubsetExtList(results);
		assertTrue(gensubsets.size()==results.size());
	}

	@Test
	public void testQueryGeneResult() {
		GeneSubsetDAO genesubset = context.getBean(GeneSubsetDAO.class);
		List<GeneSubset> results = genesubset.findGeneSubsetByPersonID(12);
		GeneSubsetExt gs=GeneSubsetExt.getGeneSubsetExt(results.get(0));
		String geneResult=gs.getGeneResult();
		System.out.println(geneResult);
		assertTrue(geneResult.length()>0);
	}
	
	
	@Test
	public void testQueryRunNumber() {
		GeneSubsetDAO genesubset = context.getBean(GeneSubsetDAO.class);
		List<GeneSubset> results = genesubset.findGeneSubsetByPersonID(12);
		GeneSubsetExt gs=GeneSubsetExt.getGeneSubsetExt(results.get(0));
		int runnumber = gs.getRun_number();
		System.out.println(runnumber);
		assertTrue(runnumber>0);
	}
	
	@Test
	public void testGeneSubsetMap() {
		GeneSubsetDAO genesubset = context.getBean(GeneSubsetDAO.class);
		List<GeneSubset> results = genesubset.findGeneSubsetByPersonID(12);
		GeneSubsetExt gs=GeneSubsetExt.getGeneSubsetExt(results.get(0));
		Map<String,Object> geneMap = gs.getGeneSubsetMap();
		System.out.println(geneMap.toString());
		assertTrue(!geneMap.isEmpty());
	}
	
	@Test
	public void testCompareEquals() {
		GeneSubsetDAO genesubset = context.getBean(GeneSubsetDAO.class);
		List<GeneSubset> results = genesubset.findGeneSubsetByPersonID(12);
		GeneSubsetExt gs=GeneSubsetExt.getGeneSubsetExt(results.get(0));
		assertTrue(gs.compareTo(gs)==0);
	}
	
	@Test
	public void testCompareUnequals1() {
		GeneSubsetDAO genesubset = context.getBean(GeneSubsetDAO.class);
		List<GeneSubset> results = genesubset.findGeneSubsetByPersonID(12);
		GeneSubsetExt gs=GeneSubsetExt.getGeneSubsetExt(results.get(0));
		gs.setGene_name("NEW NAME");
		assertTrue(gs.compareTo(GeneSubsetExt.getGeneSubsetExt(results.get(0)))!=0);
	}
	
	@Test
	public void testCompareUnequals2() {
		GeneSubsetDAO genesubset = context.getBean(GeneSubsetDAO.class);
		List<GeneSubset> results = genesubset.findGeneSubsetByPersonID(12);
		GeneSubsetExt gs=GeneSubsetExt.getGeneSubsetExt(results.get(0));
		gs.setGermline_frequency((gs.getGermline_frequency()+0.1f));
		assertTrue(gs.compareTo(GeneSubsetExt.getGeneSubsetExt(results.get(0)))!=0);
	}
	
	@Test
	public void testEquals() {
		GeneSubsetDAO genesubset = context.getBean(GeneSubsetDAO.class);
		List<GeneSubset> results = genesubset.findGeneSubsetByPersonID(12);
		GeneSubsetExt gs=GeneSubsetExt.getGeneSubsetExt(results.get(0));
		assertTrue(gs.numberOfChanges(gs)==0);
	}
	
	@Test
	public void testOneChange() {
		GeneSubsetDAO genesubset = context.getBean(GeneSubsetDAO.class);
		List<GeneSubset> results = genesubset.findGeneSubsetByPersonID(12);
		GeneSubsetExt gs=GeneSubsetExt.getGeneSubsetExt(results.get(0));
		gs.setGene_name("NEW NAME");
		assertTrue(gs.numberOfChanges(GeneSubsetExt.getGeneSubsetExt(results.get(0)))==1);
	}
	
	@Test
	public void testTwoChanges() {
		GeneSubsetDAO genesubset = context.getBean(GeneSubsetDAO.class);
		List<GeneSubset> results = genesubset.findGeneSubsetByPersonID(12);
		GeneSubsetExt gs=GeneSubsetExt.getGeneSubsetExt(results.get(0));
		gs.setGene_name("NEW NAME");
		gs.setGermline_frequency((gs.getGermline_frequency()+0.1f));
		assertTrue(gs.numberOfChanges(GeneSubsetExt.getGeneSubsetExt(results.get(0)))==2);
	}
	
	@Test
	public void testUnequal() {
		GeneSubsetDAO genesubset = context.getBean(GeneSubsetDAO.class);
		List<GeneSubset> results = genesubset.findGeneSubsetByPersonID(12);
		GeneSubsetExt gs=GeneSubsetExt.getGeneSubsetExt(results.get(1));
		GeneSubsetExt gs2=GeneSubsetExt.getGeneSubsetExt(results.get(2));
		System.out.println(gs.getGeneSubsetMap());
		System.out.println(gs2.getGeneSubsetMap());
		assertTrue(gs.numberOfChanges(gs2)>=4);
	}
	
	@Test
	public void testSameAs() {
		GeneSubsetDAO genesubset = context.getBean(GeneSubsetDAO.class);
		List<GeneSubset> results = genesubset.findGeneSubsetByPersonID(12);
		assertTrue(results.get(0).sameAs(results.get(0)));
		assertTrue(results.get(0).sameAs(results.get(1)));
		assertFalse(results.get(0).sameAs(results.get(2)));
	}
}
