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

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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
import org.digitalecmt.etarget.dao.SearchDAO;
import org.digitalecmt.etarget.dbentities.GeneSearch;
import org.digitalecmt.etarget.dbentities.PDXCDXSearch;
import org.digitalecmt.etarget.support.Formater;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.google.gson.Gson;

@Path("/search")
public class Search extends Application {
	private static final Logger log = Logger.getLogger(Search.class.getName());
	private ApplicationContext appContext;
	
	public Search() {
		appContext= new AnnotationConfigApplicationContext(TargetConfiguration.class);
	}
	
	@GET
	@Path("/genes")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getGenes(@HeaderParam("x-ms-client-principal-name") String loggedInUserID) {
		if (!new API().isUserPermittedEndpoint(loggedInUserID, "GetAllPatients")) {
			// Stop the request if user doesn't have permission for this API or web
			// component
			return Response.status(403).entity("User not permitted to access: GetAllPatients").build();
		}
		SearchDAO search = appContext.getBean(SearchDAO.class);
		List<GeneSearch> results = search.getAllGenesSearch();
		return Response.status(200).entity(new Gson().toJson(results)).build();
	}
	
	@GET
	@Path("/conditions")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getConditions(@HeaderParam("x-ms-client-principal-name") String loggedInUserID) {
		if (!new API().isUserPermittedEndpoint(loggedInUserID, "GetAllPatients")) {
			// Stop the request if user doesn't have permission for this API or web
			// component
			return Response.status(403).entity("User not permitted to access: GetAllPatients").build();
		}
		long startTime = System.nanoTime();
		SearchDAO search = appContext.getBean(SearchDAO.class);
		long t1 = System.nanoTime();
		log.info("init time " + (t1-startTime));
		List<String> results = search.getAllConditionsSearch();
		long t2 = System.nanoTime();
		log.info("search time " + (t2-t1));
		log.info("search Genes size: " +results.size());
		log.info("search time " + ((t2 - startTime)/1000000));
		return Response.status(200).entity(new Gson().toJson(results)).build();
	}
	
	@GET
	@Path("/patients")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getPatients(@HeaderParam("x-ms-client-principal-name") String loggedInUserID) {
		if (!new API().isUserPermittedEndpoint(loggedInUserID, "GetAllPatients")) {
			// Stop the request if user doesn't have permission for this API or web
			// component
			return Response.status(403).entity("User not permitted to access: GetAllPatients").build();
		}
		long startTime = System.nanoTime();
		SearchDAO search = appContext.getBean(SearchDAO.class);
		long t1 = System.nanoTime();
		log.info("init time " + (t1-startTime));
		List<org.digitalecmt.etarget.dbentities.Search> results = search.getAllPatientsSearch();
		long t2 = System.nanoTime();
		log.info("search time " + (t2-t1));
		log.info("search Genes size: " +results.size());
		List<Map<String,String>> searchResults = Formater.getSearchResults(results);
		long endTime = System.nanoTime();
		log.info("formating time " + (endTime -t2));
		log.info("search time " + ((endTime - startTime)/1000000));
		return Response.status(200).entity(new Gson().toJson(searchResults)).build();
	}
	
	@GET
	@Path("/conditions/{condition}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getCondition(@HeaderParam("x-ms-client-principal-name") String loggedInUserID, 
			@PathParam("condition") String condition) {
		if (!new API().isUserPermittedEndpoint(loggedInUserID, "GetAllPatients")) {
			// Stop the request if user doesn't have permission for this API or web
			// component
			return Response.status(403).entity("User not permitted to access: GetAllPatients").build();
		}
		long startTime = System.nanoTime();
		SearchDAO search = appContext.getBean(SearchDAO.class);
		long t1 = System.nanoTime();
		log.info("init duration " + (t1-startTime));
		List<org.digitalecmt.etarget.dbentities.Search> results = search.getCondition(condition);
		long t2 = System.nanoTime();
		log.info("search duration " + (t2-t1));
		List<Map<String,String>> searchResults = Formater.getSearchResults(results);
		long t3 = System.nanoTime();
		log.info("formatign time " + (t3-t2));
		log.info("search time " + ((t3 - startTime)/1000000));
		return Response.status(200).entity(new Gson().toJson(searchResults)).build();
	}
	
	@GET
	@Path("/genes/{gene}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getGene(@HeaderParam("x-ms-client-principal-name") String loggedInUserID, 
			@PathParam("gene") String gene) {
		if (!new API().isUserPermittedEndpoint(loggedInUserID, "GetAllPatients")) {
			// Stop the request if user doesn't have permission for this API or web
			// component
			return Response.status(403).entity("User not permitted to access: GetAllPatients").build();
		}
		log.info("gene " + gene);
		long startTime = System.nanoTime();
		SearchDAO search = appContext.getBean(SearchDAO.class);
		long t1 = System.nanoTime();
		log.info("init duration " + (t1-startTime));
		List<org.digitalecmt.etarget.dbentities.Search> results = search.getGene(gene);
		long t2 = System.nanoTime();
		log.info("search duration " + (t2-t1));
		List<Map<String,String>> searchResults = Formater.getSearchResults(results);
		long t3 = System.nanoTime();
		log.info("formatign time " + (t3-t2));
		log.info("search time " + ((t3 - startTime)/1000000));
		return Response.status(200).entity(new Gson().toJson(searchResults)).build();
	}
	
	@GET
	@Path("/pdxcdxPatients")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getPDXCDX(@HeaderParam("x-ms-client-principal-name") String loggedInUserID) {
		if (!new API().isUserPermittedEndpoint(loggedInUserID, "GetAllPatients")) {
			// Stop the request if user doesn't have permission for this API or web
			// component
			return Response.status(403).entity("User not permitted to access: GetAllPatients").build();
		}
		SearchDAO search = appContext.getBean(SearchDAO.class);
		List<PDXCDXSearch> results = search.getPDXCDXPatients();
		List<Map<String,String>> searchResults = Formater.getPDXCDXResults(results);
		return Response.status(200).entity(new Gson().toJson(searchResults)).build();
	}
}
