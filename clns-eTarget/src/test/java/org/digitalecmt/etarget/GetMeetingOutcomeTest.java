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

import org.digitalecmt.etarget.GetMeetingOutcome;
import org.junit.Test;

public class GetMeetingOutcomeTest {

	@Test
	public void test() {
		GetMeetingOutcome mo = new GetMeetingOutcome("anja.leblanc@aps.idecide.science", "6");
		String result = mo.processRequest();
		System.out.println(result);
		assertTrue(result.length()>0);
		//spelling of username false
		assertTrue(result.equals("{\"success\":\"false\",\"error\":\"User not permitted to access: GetMeetingOutcome\"}"));
	}

	
	@Test
	public void test1() {
		GetMeetingOutcome mo = new GetMeetingOutcome("anja.leblanc@apps.idecide.science", "6");
		String result = mo.processRequest();
		System.out.println(result);
		assertTrue(result.length()>0);
		assertTrue(result.contains("sdasdasdasda"));
	}
	
	@Test
	public void test2() {
		GetMeetingOutcome mo = new GetMeetingOutcome("anja.leblanc@apps.idecide.science", null);
		String result = mo.processRequest();
		System.out.println(result);
		assertTrue(result.length()>0);
		assertTrue(result.contains("[]"));
	}
	
	@Test
	public void test3() {
		GetMeetingOutcome mo = new GetMeetingOutcome("anja.leblanc@apps.idecide.science", "1000");
		String result = mo.processRequest();
		System.out.println(result);
		assertTrue(result.length()>0);
		assertTrue(result.contains("[]"));
	}
	
	@Test
	public void test4() {
		GetMeetingOutcome mo = new GetMeetingOutcome("anja.leblanc@apps.idecide.science", "12");
		String result = mo.processRequest();
		System.out.println(result);
		assertTrue(result.length()>0);
		assertTrue(result.contains("Historical data"));
	}
}
