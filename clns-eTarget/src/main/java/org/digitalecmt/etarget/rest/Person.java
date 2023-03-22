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

import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
//import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.ws.rs.DELETE;
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
import org.digitalecmt.etarget.dao.PersonDAO;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

@Path("/person")
public class Person extends Application {
	private static final Logger log = Logger.getLogger(Person.class.getName());
	private ApplicationContext appContext;
	static ResourceBundle resource;
	static String storageConnection;
	static String containerName;
	static String trialContainerName;
	
	public Person() {
		appContext= new AnnotationConfigApplicationContext(TargetConfiguration.class);
		resource = ResourceBundle.getBundle("config");
		storageConnection = resource.getString("storageURL");
		containerName = resource.getString("storageContainerName");
		trialContainerName = resource.getString("trialContainerName");
	}
	
	@Path("{person_id}")
	@Produces({MediaType.TEXT_PLAIN})
	@DELETE
	public Response deletePerson(@HeaderParam("x-ms-client-principal-name") String loggedInUserID , @PathParam("person_id") String person_id) {
		log.info("delete called " + person_id );
		if (!new API().isUserPermittedEndpoint(loggedInUserID, "DeletePatient")) {
			// Stop the request if user doesn't have permission for this API or web
			// component
			return Response.status(403).entity("User not permitted to delete patients.").build();
		}
		
		
		try {
			Integer person_id_int = Integer.parseInt(person_id);
			
			
			PersonDAO personDAO=appContext.getBean(PersonDAO.class);
			String tar_id=personDAO.getTarId(person_id_int);
			List<String> reports = personDAO.getReports(person_id_int);
			deleteFiles(reports,tar_id);
			Boolean result=personDAO.deltePersonByPersonId(person_id_int);
			if(result) {
				ChangeLogDAO changeDAO = appContext.getBean(ChangeLogDAO.class);
				changeDAO.addChange(loggedInUserID, "Person", tar_id, -1, "", "deleted");
				return Response.status(200).entity("OK").build(); 	
			}
			
		} catch(NumberFormatException ex) {
			return Response.status(404).entity("person_id must be a number.").build();
		}
		
		return Response.status(410).entity("delete did not succeed").build(); 	
	}
	
	private void deleteFiles(List<String> reports, String tar_id) {
		
		try {
			CloudStorageAccount account = CloudStorageAccount.parse(storageConnection);
			CloudBlobClient serviceClient = account.createCloudBlobClient();
			CloudBlobContainer container = serviceClient.getContainerReference(containerName);
			CloudBlobContainer trails = serviceClient.getContainerReference(trialContainerName);
			for(String report : reports) {
				CloudBlockBlob r = container.getBlockBlobReference(report);
				log.info("file to delete : " +report);
				r.deleteIfExists();
			}
			if(tar_id!=null && tar_id.trim().length()>0) {
				Iterator<ListBlobItem> itBlob=container.listBlobs().iterator();
				while(itBlob.hasNext()) {
					ListBlobItem item = itBlob.next();
					URI uri= item.getUri();
					String filename=uri.toString().substring(uri.toString().lastIndexOf('/')+1);
					if(filename.contains(tar_id)) {
						CloudBlockBlob r = container.getBlockBlobReference(filename);
						log.info("delete file " + filename);
						r.delete();
					}
				}
				CloudBlockBlob r = trails.getBlockBlobReference("trial_report_"+tar_id+".html");
				r.deleteIfExists();
			}
		}catch (Exception ex) {
			log.log(Level.SEVERE, "storage exception ", ex);
		}
	}
}
