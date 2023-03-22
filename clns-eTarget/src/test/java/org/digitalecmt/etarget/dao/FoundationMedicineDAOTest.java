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
import org.digitalecmt.etarget.dbentities.CopyNumberAlteration;
import org.digitalecmt.etarget.dbentities.FMSample;
import org.digitalecmt.etarget.dbentities.Rearrangement;
import org.digitalecmt.etarget.dbentities.ShortVariant;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class FoundationMedicineDAOTest {
	
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
	public void testShortVariantsPerson() {
		FoundationMedicineDAO fmDao = context.getBean(FoundationMedicineDAO.class);
		List<ShortVariant> svl = fmDao.getShortVariantsForPerson(19);
		assertTrue(svl!=null && svl.size()==16);
		assertTrue(svl.get(0).getGene_name()!=null);
	}
	
	@Test
	public void testShortVariants() {
		FoundationMedicineDAO fmDao = context.getBean(FoundationMedicineDAO.class);
		List<ShortVariant> svl = fmDao.getShortVariants(2785);
		assertTrue(svl.size()==8);
		assertTrue(svl.get(0).getGene_name()!=null);
	}
	
	@Test
	public void testCopyNumberAlterationsPerson() {
		FoundationMedicineDAO fmDao = context.getBean(FoundationMedicineDAO.class);
		List<CopyNumberAlteration> cnal = fmDao.getCopyNumberAlterationsForPerson(19);
		assertTrue(cnal!=null && cnal.size()==8);
		assertTrue(cnal.get(0).getGene_name()!=null);
	}
	
	@Test
	public void testCopyNumberAlterations() {
		FoundationMedicineDAO fmDao = context.getBean(FoundationMedicineDAO.class);
		List<CopyNumberAlteration> cnal = fmDao.getCopyNumberAlterations(2785);
		assertTrue(cnal.size()==4);
		assertTrue(cnal.get(0).getGene_name()!=null);
	}
	
	@Test
	public void testRearrangementsPerson() {
		FoundationMedicineDAO fmDao = context.getBean(FoundationMedicineDAO.class);
		List<Rearrangement> rl = fmDao.getRearrangementsForPerson(19);
		assertTrue(rl!= null && rl.size()==2);
		assertTrue(rl.get(0).getRearr_gene_1_name().equals("EP300"));
		assertTrue(rl.get(0).getRearr_gene_2_name().equals("EP300"));
		assertTrue(rl.get(0).getRearr_number_of_reads()==80);
	}
	
	@Test
	public void testRearrangements() {
		FoundationMedicineDAO fmDao = context.getBean(FoundationMedicineDAO.class);
		List<Rearrangement> rl = fmDao.getRearrangements(2785);
		assertTrue(rl.size()==1);
		assertTrue(rl.get(0).getRearr_gene_1_name().equals("EP300"));
		assertTrue(rl.get(0).getRearr_gene_2_name().equals("EP300"));
		assertTrue(rl.get(0).getRearr_number_of_reads()==80);
	}
	
	@Test
	public void testBloodSamples() {
		FoundationMedicineDAO fmDao = context.getBean(FoundationMedicineDAO.class);
		List<FMSample> sl = fmDao.getBloodSamplesForPerson(19);
		System.out.println(sl.size());
		assertTrue(sl.size()==2);
	}
	
	@Test
	public void testBloodSamples1() {
		FoundationMedicineDAO fmDao = context.getBean(FoundationMedicineDAO.class);
		List<FMSample> sl = fmDao.getBloodSamplesForPerson(5);
		System.out.println(sl.size());
		assertTrue(sl.size()==1);
	}
	
	@Test
	public void testTumourSamples() {
		FoundationMedicineDAO fmDao = context.getBean(FoundationMedicineDAO.class);
		List<FMSample> sl = fmDao.getTumourSamplesForPerson(19);
		System.out.println(sl.size());
		assertTrue(sl.size()==2);
		System.out.println(sl.get(0).getTmb_score());
		assertTrue(sl.get(0).getTmb_score()==1.75);
	}
	
	@Test
	public void testSelectByGene() {
		FoundationMedicineDAO fmDao = context.getBean(FoundationMedicineDAO.class);
		CopyNumberAlteration cna = fmDao.getCopyNumberAlterationByGeneVariantID(325);
		assertTrue(cna.getCna_ratio().equals(0.62f));
		Rearrangement r = fmDao.getRearrangementByGeneVariantID(326);
		assertTrue(r.getRearr_pos1().equals("chr22:41553244"));
		ShortVariant sv = fmDao.getShortVariantByGeneVariantID(317);
		assertTrue(sv.getCdna_change().equals("2687G>A"));
	}
	
	@Test
	public void testTumourShortVarient() {
		FoundationMedicineDAO fmDao = context.getBean(FoundationMedicineDAO.class);
		ShortVariant sv = fmDao.getTumourShortVariantByGeneVariantID(334);
		assertTrue(sv!=null);
		assertTrue(sv.getBaseline()==2);
	}
	
	@Test
	public void testTumourCopyNumberAlteration() {
		FoundationMedicineDAO fmDao = context.getBean(FoundationMedicineDAO.class);
		CopyNumberAlteration cna = fmDao.getTumourCopyNumberAlterationByGeneVariantID(337);
		assertTrue(cna!=null);
		assertTrue(cna.getBaseline()==2);
	}
	

	@Test
	public void testTumourRearrangement() {
		FoundationMedicineDAO fmDao = context.getBean(FoundationMedicineDAO.class);
		Rearrangement r = fmDao.getTumourRearrangementByGeneVariantID(339);
		assertTrue(r!=null);
		assertTrue(r.getBaseline()==2);
	}
//	
//	@Test
//	public void testgetTumourBaseline() {
//		FoundationMedicineDAO fmDao = context.getBean(FoundationMedicineDAO.class);
//		Integer i = fmDao.getTumourBaseline(2786);
//		assertNotNull(i);
//		assertTrue(i==2);
//	}
}
