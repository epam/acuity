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


import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;

public class IHCReportTest extends JerseyTest{
	
	@Override
    protected Application configure() {
        return new ResourceConfig(IHCReport.class);
    }
	@Test
	public void getIHCReportTest() {
		String response = target("IHCReport/6").request().header("x-ms-client-principal-name", "anja.leblanc@apps.idecide.science").get(String.class);
        System.out.println(response);
        Assert.assertTrue(response.length()>0);
        Assert.assertTrue(response.contains("1"));
        Assert.assertTrue(response.contains("No result"));
	}
	
	@Test
	public void getIHCReportTest2() {
		String response = target("IHCReport/1").request().header("x-ms-client-principal-name", "anja.leblanc@apps.idecide.science").get(String.class);
        System.out.println(response);
        Assert.assertTrue(response.length()>0);
        Assert.assertTrue(response.contains("\"message\":\"Results not available.\""));
	}
	
	@Test
	public void getIHCReportNotAllowedTest() {
		Response response = target("IHCReport/6").request().header("x-ms-client-principal-name", "anjaleblanc@apps.idecide.science").get();
        System.out.println(response);
        Assert.assertTrue(response.getStatus()==403);
	}
}
