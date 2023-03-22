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

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;
import org.digitalecmt.etarget.API;

@Path("/component")
public class Component extends Application {
	private static final Logger log = Logger.getLogger(Component.class.getName());
	private ResourceBundle resource,application;
	private String path="";
	private Boolean fm_data=false;
	
	public Component() {
		resource = ResourceBundle.getBundle("config");
		path = resource.getString("webapp.include.path");
		application = ResourceBundle.getBundle("application");
		log.info("fmtumour " + application.getString("page.fmtumour"));
		log.info("fmblood " + application.getString("page.fmblood"));
		if(application.getString("page.fmblood").compareTo("true")==0) {
			fm_data=true;
		}
		if(application.getString("page.fmtumour").compareTo("true")==0) {
			fm_data=true;
		}
		
	}
	
	@Path("{component}")
	@Produces({MediaType.TEXT_HTML})
	@GET
	public Response getComponent(@HeaderParam("x-ms-client-principal-name") String loggedInUserID , 
			@PathParam("component") String component) {
		API api = new API();
		
		log.info("Rest Componant called for (1) " + component);
		
		if (!api.isUserPermittedComponent(loggedInUserID, component)) {
			// Stop the request if user doesn't have permission for this API or web
			// component
			String message = "";
			if(component.equals("patienttable") || component.equals("patienttable_search") || component.equals("reportextraction") || component.equals("admin") || component.equals("genepanel")) {
		        if(api.isUserDeactivated(loggedInUserID)) {
		          message = "Your account has been deactivated. Please contact the MTB Chair if you require access.";
		        }
		        if(!api.hasUserAccount(loggedInUserID)) {
			      message = "To view patients please contact the administrator to create an account in eTARGET itself.";
			    }
		        
		      }
			
			return Response.status(200).entity(message).build();
		}
		
		StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(OutputStream os) throws IOException, WebApplicationException {
                Writer writer = new BufferedWriter(new OutputStreamWriter(os));
                log.info("component " + component + " " + fm_data);
            	if(component.equals("patienttable_search") && fm_data) {
            		IOUtils.copy(new InputStreamReader(new FileInputStream(path+component+"_fm.html")), writer);
            	} else {
            		IOUtils.copy(new InputStreamReader(new FileInputStream(path+component+".html")), writer);
            	}
                writer.flush();
            }
        };

		return Response.ok(stream).build();
	}
	
	@Path("{component}/{personID}")
	@Produces({MediaType.TEXT_HTML})
	@GET
	public Response getComponent(@HeaderParam("x-ms-client-principal-name") String loggedInUserID , 
			@PathParam("component") String component, @PathParam("personID") String personID) {
		API api = new API();
		
		log.info("Rest Componant called for (2) " + component);
		
		if (!api.isUserPermittedComponent(loggedInUserID, component)  || !api.checkAccessPermission(component, loggedInUserID, personID)) {
			// Stop the request if user doesn't have permission for this API or web
			// component
			String message = "";
			if(component.equals("patienttable") || component.equals("patienttable_search") || component.equals("reportextraction") || component.equals("admin") || component.equals("genepanel")) {
		        if(api.isUserDeactivated(loggedInUserID)) {
		          message = "Your account has been deactivated. Please contact the MTB Chair if you require access.";
		        } 
		        if(!api.hasUserAccount(loggedInUserID)) {
			      message = "To view patients please contact the administrator to create an account in eTARGET itself.";
			    }
		      }
			return Response.status(200).entity(message).build();
		}
		
		StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(OutputStream os) throws IOException, WebApplicationException {
                Writer writer = new BufferedWriter(new OutputStreamWriter(os));
                try {
                	IOUtils.copy(new InputStreamReader(new FileInputStream(path+component+".html")), writer);
                } catch(FileNotFoundException ex) {
                	writer.write("An error occurred fetching the HTML component.");
                } catch(IOException ex) {
                	writer.write("An error occured accessing the HTML component.");
                }
                writer.flush();
            }
        };

		return Response.ok(stream).build();
	}
	
	@Path("{component}/{type}/{personID}")
	@Produces({MediaType.TEXT_HTML})
	@GET
	public Response getComponent(@HeaderParam("x-ms-client-principal-name") String loggedInUserID , 
			@PathParam("component") String component, @PathParam("personID") String personID, @PathParam("type") String type) {
		API api = new API();
		
		log.info("Rest Componant called for (3) " + component + " " + type);
		
		if (!api.isUserPermittedComponent(loggedInUserID, component)  || !api.checkAccessPermission(component, loggedInUserID, personID)) {
			// Stop the request if user doesn't have permission for this API or web
			// component
			String message = "";
			return Response.status(200).entity(message).build();
		}
		
		StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(OutputStream os) throws IOException, WebApplicationException {
                Writer writer = new BufferedWriter(new OutputStreamWriter(os));
                try {
                	List<String> lines=IOUtils.readLines(new InputStreamReader(new FileInputStream(path+component+".html")));
                	lines.replaceAll(s -> s.replaceAll("XXXX", type));
                	IOUtils.writeLines(lines, " ", writer);
                } catch(FileNotFoundException ex) {
                	writer.write("An error occurred fetching the HTML component.");
                } catch(IOException ex) {
                	writer.write("An error occured accessing the HTML component.");
                }
                writer.flush();
            }
        };

		return Response.ok(stream).build();
	}

}
