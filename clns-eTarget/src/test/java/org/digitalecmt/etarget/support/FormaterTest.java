package org.digitalecmt.etarget.support;

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
import org.digitalecmt.etarget.dao.GeneNameDAO;
import org.digitalecmt.etarget.dao.GeneSubsetDAO;
import org.digitalecmt.etarget.dao.GenomicsDataDAO;
import org.digitalecmt.etarget.dao.NgsLibDAO;
import org.digitalecmt.etarget.dao.SearchDAO;
import org.digitalecmt.etarget.dbentities.CopyNumberAlteration;
import org.digitalecmt.etarget.dbentities.GeneName;
import org.digitalecmt.etarget.dbentities.GeneSubsetExt;
import org.digitalecmt.etarget.dbentities.NgsLib;
import org.digitalecmt.etarget.dbentities.NgsLibExt;
import org.digitalecmt.etarget.dbentities.PDXCDXSearch;
import org.digitalecmt.etarget.dbentities.ShortVariant;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class FormaterTest {
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
	public void testNgsLib() {
		NgsLibDAO ngsLib = context.getBean(NgsLibDAO.class);
		List<NgsLib> result = ngsLib.findNgsLibByPersonID(12);
		List<NgsLibExt> nlel = NgsLibExt.getNgsLibExtList(result);
		Map<String,Object> formatedMap=Formater.getNgsLibMap(nlel);
		System.out.println(formatedMap);
		assertTrue(formatedMap.isEmpty()==false);
	}
	
	@Test
	public void testGeneNames() {
		GeneNameDAO geneNameDao = context.getBean(GeneNameDAO.class);
		List<GeneName> result = geneNameDao.getGeneNamesByPersonID(12);
		List<String> geneStrings = Formater.getGeneNameList(result);
		System.out.println(geneStrings);
		assertTrue(result.size()==geneStrings.size());
	}
	
	@Test
	public void testFormatCTDNA() {
		GeneSubsetDAO geneSubsetDao = context.getBean(GeneSubsetDAO.class);
		GeneSubsetExt gene = GeneSubsetExt.getGeneSubsetExt(geneSubsetDao.findGeneSubsetByGeneVarientID(2, 14));
		Map<String,Object> res = Formater.formatCTDNA(gene);
		System.out.println(res);
		assertTrue(res.size()==11 && res.get("geneName")!=null && (Integer)res.get("geneVarientID")==14);
	}
	
	@Test
	public void testPDXCDXFormat() {
		SearchDAO search = context.getBean(SearchDAO.class);
		List<PDXCDXSearch> result = search.getPDXCDXPatients();
		List<Map<String,String>> res = Formater.getPDXCDXResults(result);
		System.out.println(res);
		assertTrue(res.size()==result.size());
	}

	@Test
	public void testCNAFromat() {
		GenomicsDataDAO fmDao = context.getBean(GenomicsDataDAO.class);
		ShortVariant sv = fmDao.getTumourShortVariantByGeneVariantID(334);
		Map<String, String> res = Formater.formatShortVariant(sv);
		System.out.println(res);
		assertTrue(res.get("subclonal").compareTo("True")==0);
	}
	
	@Test
	public void testCNAFormat() {
		GenomicsDataDAO fmDao = context.getBean(GenomicsDataDAO.class);
		CopyNumberAlteration cna = fmDao.getTumourCopyNumberAlterationByGeneVariantID(337);
		Map<String, String> res = Formater.formatCopyNumberAlteration(cna);
		System.out.println(res);
		assertTrue(res.get("equivocal").compareTo("True")==0);
	}
}
