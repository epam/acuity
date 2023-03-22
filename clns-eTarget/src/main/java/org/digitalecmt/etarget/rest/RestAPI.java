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

//import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.digitalecmt.etarget.API;
import org.digitalecmt.etarget.config.TargetConfiguration;
import org.digitalecmt.etarget.dao.ChangeLogDAO;
import org.digitalecmt.etarget.dao.ReportDAO;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Path("/report")
public class RestAPI extends Application {
	private static final Logger log = Logger.getLogger(RestAPI.class.getName());
	private ApplicationContext appContext;
//	private ResourceBundle resource;
	
	public RestAPI() {
//		resource = ResourceBundle.getBundle("config");
		appContext= new AnnotationConfigApplicationContext(TargetConfiguration.class);
	}
	
	@Path("{specimen_id}/{run}")
	@Produces({MediaType.TEXT_PLAIN})
	@DELETE
	public Response deleteReport(@HeaderParam("x-ms-client-principal-name") String loggedInUserID , @PathParam("specimen_id") String specimen, @PathParam("run") String run) {
		log.info("delete called " + specimen + " " + run);
		if (!new API().isUserPermittedEndpoint(loggedInUserID, "DeleteReport")) {
			// Stop the request if user doesn't have permission for this API or web
			// component
			return Response.status(403).entity("User not permitted to delete reports.").build();
		}
		
		
		try {
			Integer specimen_id = Integer.parseInt(specimen);
			Integer run_number = Integer.parseInt(run);
			
			
			ReportDAO report=appContext.getBean(ReportDAO.class);
			Boolean result=report.deleteReportBySpecimenRun(specimen_id, run_number);
			if(result) {
				ChangeLogDAO changeDAO = appContext.getBean(ChangeLogDAO.class);
				changeDAO.addChange(loggedInUserID, "Report", specimen+"/"+run, -1, "", "deleted");
				return Response.status(200).entity("OK").build(); 	
			}
			
		} catch(NumberFormatException ex) {
			return Response.status(404).entity("Specimen_id and run number must be numbers.").build();
		}
		
		return Response.status(410).entity("delete did not succeed").build(); 	
	}
	
	@GET
	@Path("/info")
	@Produces(MediaType.TEXT_PLAIN)
	public Response info() {
		return Response.ok("Info called").build();
	}

}
