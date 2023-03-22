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

import org.digitalecmt.etarget.GeneralInfo;
import org.junit.Test;

public class GenearlInfoTest {

	@Test
	public void test1() {
		GeneralInfo gi= new GeneralInfo("anja.leblanc@apps.idecide.science", "12");
		String result = gi.processRequest();
		System.out.println(result);
		assertTrue(result.length()>0);
		assertTrue(result.contains("dateBlood"));
	}
	
	@Test
	public void testNullValue() {
		GeneralInfo gi= new GeneralInfo("anja.leblanc@apps.idecide.science", "3");
		String result = gi.processRequest();
		System.out.println(result);
		assertTrue(result.length()>0);
		assertTrue(result.contains("\"baseline_number\":1"));
	}
	
	//API tests
	@Test
	public void testAdminUser() {
		GeneralInfo gi= new GeneralInfo("anja.leblanc@apps.idecide.science", "3");
		assertFalse(gi.isAdmin("anja.leblanc@apps.idecide.science"));
		assertTrue(gi.isAdmin("anja@bindrich.de"));
	}
	
	@Test
	public void testDisabledUser() {
		GeneralInfo gi= new GeneralInfo("anja.leblanc@apps.idecide.science", "3");
		assertFalse(gi.isUserDeactivated("anja.leblanc@apps.idecide.science"));
		assertTrue(gi.isUserDeactivated("rob.dunne@apps.idecide.science"));
	}

	@Test
	public void testIsDeletable() {
		GeneralInfo gi= new GeneralInfo("anja@bindrich.de", "6");
		String result = gi.processRequest();
		System.out.println(result);
		assertTrue(result.length()>0);
		assertTrue(result.contains("deletable"));
	}
	
	@Test
	public void testIsDeletable2() {
		GeneralInfo gi= new GeneralInfo("anja@bindrich.de", "13");
		String result = gi.processRequest();
		System.out.println(result);
		assertTrue(result.length()>0);
		assertTrue(result.contains("\"deletable\":true"));
		assertTrue(result.contains("\"deletable\":false"));
	}
}
