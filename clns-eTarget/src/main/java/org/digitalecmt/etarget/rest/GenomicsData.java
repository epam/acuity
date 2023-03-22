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
import java.time.ZonedDateTime;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.digitalecmt.etarget.API;
import org.digitalecmt.etarget.config.ConfigDataSources;
import org.digitalecmt.etarget.config.TargetConfiguration;
import org.digitalecmt.etarget.dao.GenomicsDataDAO;
import org.digitalecmt.etarget.dbentities.CopyNumberAlteration;
import org.digitalecmt.etarget.dbentities.FMSample;
import org.digitalecmt.etarget.dbentities.Rearrangement;
import org.digitalecmt.etarget.dbentities.ShortVariant;
import org.digitalecmt.etarget.support.Formater;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.google.gson.Gson;

@Path("/GenomicsData")
public class GenomicsData extends Application {
	
	private static final Logger log = Logger.getLogger(GenomicsData.class.getName());
	private ApplicationContext appContext;
	
	public GenomicsData() {
		appContext= new AnnotationConfigApplicationContext(TargetConfiguration.class);
	}
	
	
	@Path("{source_id}/{person_id}/{type}")
	@Produces({MediaType.APPLICATION_JSON})
	@GET
	public Response getFMTumour(@HeaderParam("x-ms-client-principal-name") String loggedInUserID , @PathParam("person_id") String personID, 
			@PathParam("source_id") String sourceID, @PathParam("type") String type) {
		if (!new API().isUserPermittedEndpoint(loggedInUserID, "TumourNGS")) {
			// Stop the request if user doesn't have permission for this API or web
			// component
			return Response.status(403).entity("User not permitted to view data.").build();
		}
		log.info("Genomic 0 "+ZonedDateTime.now().toInstant().toEpochMilli());
		log.info("GenomicData called " + personID+ " " + sourceID +" " + type);
		try {
			Integer person_id = Integer.parseInt(personID);
			//TumourNgsDAO tumour=appContext.getBean(TumourNgsDAO.class);
			GenomicsDataDAO tumour = appContext.getBean(GenomicsDataDAO.class);
			List<FMSample> samples;
			if(type.trim().toLowerCase().compareTo("tumour")==0) {
				log.info("type tumour");
				samples = tumour.getTumourSamplesForPerson(sourceID, person_id);
				log.info("samples " + samples.size());
			} else {
				log.info("type blood");
				samples = tumour.getBloodSamplesForPerson(sourceID, person_id);
				log.info("samples " + samples.size());
			}
			
			List<ConfigDataSources> addSources=(List<ConfigDataSources>)appContext.getBean("configuredDataSources");
			String code="";
			for(ConfigDataSources cds : addSources) {
				if(cds.getDb_panel_name().equals(sourceID)) {
					code=cds.getCode();
					break;
				}
			}
			log.info("Genomic 1 "+ZonedDateTime.now().toInstant().toEpochMilli());
			List<ShortVariant> shortvariants = tumour.getShortVariantsForPerson(sourceID, person_id);
			log.info("Genomic 2 "+ZonedDateTime.now().toInstant().toEpochMilli());
			List<CopyNumberAlteration> copynumberalteration = tumour.getCopyNumberAlterationsForPerson(sourceID, person_id);
			log.info("Genomic 3 "+ZonedDateTime.now().toInstant().toEpochMilli());
			List<Rearrangement> rearrangement = tumour.getRearrangementsForPerson(sourceID, person_id);
			log.info("Genomic 4 "+ZonedDateTime.now().toInstant().toEpochMilli());
			Map<String,Map<String,Object>> tumourMap=Formater.formatFM(samples, shortvariants, copynumberalteration, rearrangement, type, code);
			log.info("Genomic 5 "+ZonedDateTime.now().toInstant().toEpochMilli());
			Formater.cleanFMTumourMap(tumourMap);
			log.info("Genomic 6 "+ZonedDateTime.now().toInstant().toEpochMilli());
			log.info(new Gson().toJson(tumourMap));
			log.info("Genomic 7 "+ZonedDateTime.now().toInstant().toEpochMilli());
			return Response.status(200).entity(new Gson().toJson(tumourMap)).build();
			
			
		} catch(NumberFormatException ex) {
			return Response.status(404).entity("person_id must be a number").build();
		}
	}
	
}
