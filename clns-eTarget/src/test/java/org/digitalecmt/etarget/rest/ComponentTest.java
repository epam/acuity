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

import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

public class ComponentTest extends JerseyTest {

	@Override
    protected Application configure() {
        return new ResourceConfig(Component.class);
    }
	
	@Test
    public void getComponentTest() {
        String response = target("component/fmbloodtick/3").request().header("x-ms-client-principal-name", "anja@bindrich.de").get(String.class);
        assertTrue(response.length()>0);
        System.out.println(response);
        assertTrue(response.contains("<div"));
    }
	
	@Test
    public void getComponentTest2() {
        String response = target("component/genericgenomictick/foundationmedice_blood/3").request().header("x-ms-client-principal-name", "anja@bindrich.de").get(String.class);
        assertTrue(response.length()>0);
        System.out.println(response);
        assertTrue(response.contains("<div"));
    }
	
	@Test
    public void getComponentTest3() {
        String response = target("component/genericgenomiccheckbox/foundationmedice_blood/3").request().header("x-ms-client-principal-name", "anja.leblanc@apps.idecide.science").get(String.class);
        assertTrue(response.length()>0);
        System.out.println(response);
        assertTrue(response.contains("<input"));
    }
	
	@Test
    public void getComponentTest4() {
        String response = target("component/patienttable_search").request().header("x-ms-client-principal-name", "someone@else.com").get(String.class);
        assertTrue(response.length()>0);
        System.out.println(response);
        assertTrue(response.contains("To view patients please contact the administrator to create an account in eTARGET itself."));
    }
	
	@Test
    public void getComponentTest5() {
        String response = target("component/patienttable").request().header("x-ms-client-principal-name", "rob.dunne@apps.idecide.science").get(String.class);
        assertTrue(response.length()>0);
        System.out.println(response);
        assertTrue(response.contains("Your account has been deactivated."));
    }

}
