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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import org.digitalecmt.etarget.config.TargetConfiguration;
import org.digitalecmt.etarget.dbentities.GeneSubset;
import org.digitalecmt.etarget.dbentities.GeneSubsetTumour;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class GeneSubsetDAOTest {

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
	public void testFindGeneSubsetByPersonID() {
		
		GeneSubsetDAO genesubset = context.getBean(GeneSubsetDAO.class);
		List<GeneSubset> results = genesubset.findGeneSubsetByPersonID(12);
		System.out.println(results.size());
		assertNotNull(results);
	}
	
	@Test
	public void testFindGeneSubsetByGeneVariantID() {
		GeneSubsetDAO genesubset = context.getBean(GeneSubsetDAO.class);
		GeneSubset result = genesubset.findGeneSubsetByGeneVarientID(2, 14);
		System.out.println(result.getGene_name() + " " + result.getCdna_change());
		assertNotNull(result);
	}
	
	@Test
	public void testFindLatestGenePanelIDs() {
		GeneSubsetDAO genesubset = context.getBean(GeneSubsetDAO.class);
		List<Integer> result = genesubset.findLatestGenePanelIDsBlood(8);
		System.out.print(result);
		assertTrue(result.size()>0);
		assertTrue(result.contains(2773));
		result = genesubset.findLatestGenePanelIDsBlood(4);
		System.out.print(result);
		assertTrue(result.size()>0);
		assertTrue(result.contains(2754));
	}
	
	@Test
	public void testFindGeneVariantIDsByGenePanelID() {
		GeneSubsetDAO genesubset = context.getBean(GeneSubsetDAO.class);
		List<Integer> result = genesubset.findGeneVarientsByGenePanelID(2779);
		System.out.println(result);
		assertTrue(result.size()>0);
		assertTrue(result.contains(266));
		assertTrue(result.contains(202));
	}
	
	@Test
	public void testFindGeneVariantsByGeneVariantIDs() {
		GeneSubsetDAO genesubset = context.getBean(GeneSubsetDAO.class);
		List<Integer> geneIDs = genesubset.findGeneVarientsByGenePanelID(2779);
		System.out.println(geneIDs);
		List<GeneSubset> result = genesubset.findGeneSubsetByGeneVarientsIDs(geneIDs);
		assertTrue(result.size()==geneIDs.size());
		assertTrue(geneIDs.contains(result.get(0).getGene_varient_id()));
	}
	
	@Test
	public void testFindTumourGeneVariantsByPersonID( ) {
		GeneSubsetDAO genesubset = context.getBean(GeneSubsetDAO.class);
		List<GeneSubsetTumour> tumourGenes = genesubset.findTumourGeneVariantsByPersonID(2);
		System.out.println(tumourGenes.size());
		assertTrue(tumourGenes.size()==3);
	}
}
