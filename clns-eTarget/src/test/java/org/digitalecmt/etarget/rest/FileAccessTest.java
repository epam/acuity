package org.digitalecmt.etarget.rest;

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
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Application;

import org.junit.Test;

public class FileAccessTest extends JerseyTest{
	
	@Override
    protected Application configure() {
        return new ResourceConfig(FileAccess.class);
    }

	@Test
	public void test() {
		Boolean ret=FileAccess.exists("0000001.pdf");
		System.out.println(ret);
		assertTrue(ret);
		
		ret=FileAccess.exists("ABC0000001.pdf");
		System.out.println(ret);
		assertFalse(ret);
		
		ret=FileAccess.exists("Trial_finder_prototype_Sept2019.html");
		System.out.println(ret);
		assertTrue(ret);
		
		ret=FileAccess.exists("trial_report_TAR00001.html");
		System.out.println(ret);
		assertTrue(ret);
	}
	
	@Test
	public void getFile() {
		String ret = FileAccess.getFile("TAR00328", "DT3WaFM.pdf");
		assertTrue(ret.equals("TAR00328NT2DT3WaFM.pdf"));
	}
	
	@Test
	public void getFiles() {
		List<String> files = FileAccess.getAllFiles("00000", ".pdf");
		assertTrue(files.size()==16);
		assertTrue(files.contains("0000001.pdf"));
		assertFalse(files.contains("0000009.pdf"));
	}
	
	@Test
    public void getHtmlTest() {
        String response = target("html/trials/Trial_finder_prototype_Sept2019.html").request().header("x-ms-client-principal-name", "anja.leblanc@apps.idecide.science").get(String.class);
        assertTrue(response.length()>0);
        assertTrue(response.contains("<!DOCTYPE html>"));
    }
	
	@Test
    public void getPdfTest() {
        String response = target("pdf/0000001.pdf").request().header("x-ms-client-principal-name", "anja.leblanc@apps.idecide.science").get(String.class);
        assertTrue(response.length()>0);
        assertTrue(response.contains("%PDF"));
    }
	
	@Test
	public void getFilesDate() {
		Map<String, Date> files = FileAccess.getAllFilesDate("00000", ".pdf");
		assertTrue(files.size()==16);
		assertTrue(files.containsKey("0000001.pdf"));
		System.out.println(files.get("0000001.pdf").toString());
	}
	
	@Test
	public void getImage() {
		String response = target("image/ihc/TAR00008PTc_CD3.jpg").request().header("x-ms-client-principal-name", "anja.leblanc@apps.idecide.science").get(String.class);
		assertTrue(response.length()>0);
		assertTrue(response.contains("sRGB"));
        
	}

}
