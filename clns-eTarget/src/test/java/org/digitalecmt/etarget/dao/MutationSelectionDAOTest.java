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

import java.sql.SQLException;
import java.util.List;

import org.digitalecmt.etarget.config.TargetConfiguration;
import org.digitalecmt.etarget.dbentities.SelectedGeneVariant;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MutationSelectionDAOTest {
	
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
	public void test1() {
		MutationSelectionDAO mutationSelection = context.getBean(MutationSelectionDAO.class);
		try {
			mutationSelection.addMutation(2, 1, "CTDNA");
		}catch(SQLException ex) {
			fail("SQLException " + ex.getMessage());
		}
		assertTrue(true);
	}
	
	@Test
	public void test2() {
		MutationSelectionDAO mutationSelection = context.getBean(MutationSelectionDAO.class);
		List<SelectedGeneVariant> genes = mutationSelection.getSelectedMutationsByPersonID(2);
		assertTrue(genes.size()>0);
	}
	
	@Test
	public void test3() {
		MutationSelectionDAO mutationSelection = context.getBean(MutationSelectionDAO.class);
		try {
			mutationSelection.deleteMutation(2, 1, "CTDNA");
		} catch(SQLException ex) {
			fail("SQLException " + ex.getMessage());
		}
		assertTrue(true);
	}
	
	@Test
	public void testSelectedByGenePanelID() {
		MutationSelectionDAO mutationSelection = context.getBean(MutationSelectionDAO.class);
		try {
			List<Integer>values = mutationSelection.getSelectedByGenePanelID(2779);
			System.out.println(values);
			assertTrue(values.size()==2);
			assertTrue(values.contains(265));
			assertTrue(values.contains(266));
		}catch(Exception e) {
			System.err.println(e.getMessage());
			fail();
		}
	}

}
