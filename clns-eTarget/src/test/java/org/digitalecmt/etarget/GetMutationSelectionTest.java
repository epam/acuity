package org.digitalecmt.etarget;

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

import org.digitalecmt.etarget.config.TargetConfiguration;
import org.digitalecmt.etarget.dao.MutationSelectionDAO;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class GetMutationSelectionTest {

	private static ApplicationContext context;
	
	@BeforeClass
	public static void setUpClass() throws SQLException{
		context = new AnnotationConfigApplicationContext(TargetConfiguration.class);
		MutationSelectionDAO mutationSelection = context.getBean(MutationSelectionDAO.class);
		mutationSelection.addMutation(12, 129, "CTDNA");
		mutationSelection.addMutation(12, 133, "CTDNA");
		mutationSelection.addMutation(12, 153, "CTDNA");
		
		mutationSelection.addMutation(2, 79	, "NGS");
		mutationSelection.addMutation(2, 7, "CTDNA");
	}
	
	@Test
	public void test() {
		GetMutationSelection ms = new GetMutationSelection("anja.leblanc@apps.idecide.science", "12");
		String result=ms.processRequest();
		System.out.println(result);
		assertTrue(result.contains("129") && result.contains("133") && result.contains("153"));
		assertTrue(result.contains("fmTumour"));
		assertTrue(result.contains("fmBlood"));
		assertTrue(result.contains("rearrangement"));
		assertTrue(result.contains("shortVariant"));
		assertTrue(result.contains("copyNumberAlteration"));
	}
	
	@Test
	public void testTumour() {
		GetMutationSelection ms = new GetMutationSelection("anja.leblanc@apps.idecide.science", "2");
		String result=ms.processRequest();
		System.out.println(result);
		assertTrue(result.contains("79") && result.contains("7"));
	}

	@Test
	public void testLatest() {
		GetMutationSelection ms = new GetMutationSelection("anja.leblanc@apps.idecide.science", "3");
		String result=ms.getLatest();
		System.out.println(result);
		assertTrue(result.contains("ALK") && !result.contains("SEPT3"));
		assertTrue(result.contains("latestFmTumour"));
		assertTrue(result.contains("latestFmBlood"));
		assertTrue(result.contains("rearrangement"));
		assertTrue(result.contains("shortVariant"));
		assertTrue(result.contains("copyNumberAlteration"));
	}

	@Test
	public void testAll() {
		GetMutationSelection ms = new GetMutationSelection("anja.leblanc@apps.idecide.science", "3");
		String result=ms.processRequest();
		System.out.println(result);
		assertTrue(result.contains("ALK") && result.contains("SEPT3"));
	}
	
	@AfterClass
	public static void tearDownClass() throws SQLException{
		MutationSelectionDAO mutationSelection = context.getBean(MutationSelectionDAO.class);
		mutationSelection.deleteMutation(12, 129, "CTDNA");
		mutationSelection.deleteMutation(12, 133, "CTDNA");
		mutationSelection.deleteMutation(12, 153, "CTDNA");
		
		mutationSelection.deleteMutation(2, 79	, "NGS");
		mutationSelection.deleteMutation(2, 7, "CTDNA");
	}
}
