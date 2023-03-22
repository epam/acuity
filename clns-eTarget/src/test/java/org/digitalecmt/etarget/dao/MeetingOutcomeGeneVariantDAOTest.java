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
import org.digitalecmt.etarget.dbentities.MeetingOutcomeGeneVariant;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MeetingOutcomeGeneVariantDAOTest {

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
		MeetingOutcomeGeneVariantDAO mogv = context.getBean(MeetingOutcomeGeneVariantDAO.class);
		mogv.addGeneVariantToMeetingOutcome(1, 100, "CTDNA");
		mogv.addGeneVariantToMeetingOutcome(1, 101, "NGS");
		mogv.addGeneVariantToMeetingOutcome(2, 22, "CTDNA");
		mogv.addGeneVariantToMeetingOutcome(2, 33, "CTDNA");
		List<MeetingOutcomeGeneVariant> result=mogv.getGeneVariantsForMeetingOutcome(2);
		assertTrue(result.size()==2);
		assertTrue(result.get(0).getMeasurement_gene_variant_id()==22);
		assertTrue(result.get(0).getType().equals("CTDNA"));
		assertTrue(result.get(0).getMo_id()==2);
		mogv.removeGeneVariantFromMeetingOutcome(1, 100, "CTDNA");
		mogv.removeGeneVariantFromMeetingOutcome(1, 101, "NGS");
		mogv.removeGeneVariantFromMeetingOutcome(2, 22, "CTDNA");
		mogv.removeGeneVariantFromMeetingOutcome(2, 33, "CTDNA");
		result=mogv.getGeneVariantsForMeetingOutcome(2);
		assertTrue(result.size()==0);
	}

}
