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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.digitalecmt.etarget.config.TargetConfiguration;
import org.digitalecmt.etarget.dbentities.ConceptGene;
import org.digitalecmt.etarget.dbentities.ConceptDataSource;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.google.gson.Gson;

public class GenePanelDAOTest {
	
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
	public void testGetGenePanles() {
		GenePanelDAO genePanelDao=context.getBean(GenePanelDAO.class);
		List<ConceptDataSource> result = genePanelDao.getGenePanels();
		System.out.println(result);
		assertTrue(result.size()>0);
		assertTrue(result.get(0).getPanel_name().equals("GDL Panel"));
	}
	
	@Test
	public void testAddDeleteGenePanel() {
		List<Integer> genes= Arrays.asList(new Integer[] {2,4,6,8,10});
		GenePanelDAO genePanelDao=context.getBean(GenePanelDAO.class);
		int gene_panel_id=genePanelDao.addGenePanel("even genes", "2019-10-10", genes);
		assertTrue(gene_panel_id>0);
		System.out.println(gene_panel_id);
		boolean success = genePanelDao.deleteGenePanel(gene_panel_id);
		assertTrue(success);
	}

	@Test
	public void testGetAllGenes() {
		GenePanelDAO genePanelDao=context.getBean(GenePanelDAO.class);
		List<ConceptGene> genes = genePanelDao.getAllGenes();
		assertTrue(genes.size()>0);
		assertTrue(genes.get(0).getGene_name().equals("AKT1"));
	}
	
	@Test
	public void testAddGene() {
		GenePanelDAO genePanelDao=context.getBean(GenePanelDAO.class);
		int geneID = genePanelDao.addGene("newGene");
		assertTrue(geneID!=-1);
		System.out.println(geneID);
		boolean success=genePanelDao.deleteGene(geneID);
		assertTrue(success);
	}
	
	@Test
	public void testgetGenePanelMap() {
		GenePanelDAO genePanelDao=context.getBean(GenePanelDAO.class);
		Map<String, Map<String,Object>> genePanels= genePanelDao.getGenePanelMap();
		assertNotNull(genePanels);
		assertTrue(genePanels.size()==2);
		System.out.println(new Gson().toJson(genePanels));
		Iterator<String> it= genePanels.keySet().iterator();
		while(it.hasNext()) {
			String p= it.next();
			Map<String,Object> g = genePanels.get(p);
			assertNotNull(g);
			if(g.size()>0) {
				assertTrue(((List<ConceptGene>)g.get("genes")).get(0).getGene_name().length()>0);
			}
		}
		
	}
	
}
