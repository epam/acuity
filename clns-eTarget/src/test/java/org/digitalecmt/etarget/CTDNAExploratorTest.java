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

import org.digitalecmt.etarget.CTDNAExploratory;
import org.junit.Test;

public class CTDNAExploratorTest {
	
	@Test
	public void testProcessRequest_12() {
		CTDNAExploratory exploartor = new CTDNAExploratory("anja.leblanc@apps.idecide.science", "12");
		String result = exploartor.processRequest();
		System.out.println(result);
		assertTrue(result!=null && result.length()>0);
	}

//	@Test
//	public void testProcessRequest2_12() {
//		CTDNAExploratory exploartor = new CTDNAExploratory("anja.leblanc@apps.idecide.science", "12");
//		String result = exploartor.processRequest2();
//		System.out.println(result);
//		assertTrue(result!=null && result.length()>0);
//	}
	
	@Test
	public void testProcessRequest_18() {
		CTDNAExploratory exploartor = new CTDNAExploratory("anja.leblanc@apps.idecide.science", "18");
		String result = exploartor.processRequest();
		System.out.println(result);
		assertTrue(result!=null && result.length()>0);
	}

//	@Test
//	public void testProcessRequest2_18() {
//		CTDNAExploratory exploartor = new CTDNAExploratory("anja.leblanc@apps.idecide.science", "18");
//		String result = exploartor.processRequest2();
//		System.out.println(result);
//		assertTrue(result!=null && result.length()>0);
//	}

}
