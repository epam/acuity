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

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
//import java.net.URI;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.digitalecmt.etarget.API;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;
//import com.microsoft.azure.storage.blob.ListBlobItem;

@Path("/{a:pdf|html|image}")
public class FileAccess {

	private static final Logger log = Logger.getLogger(FileAccess.class.getName());

	static ResourceBundle resource;
	static String storageConnection;
	static String containerName;
	static String trialContainerName;

	public FileAccess() {
		resource = ResourceBundle.getBundle("config");
		storageConnection = resource.getString("storageURL");
		containerName = resource.getString("storageContainerName");
		trialContainerName = resource.getString("trialContainerName");
		log.info("connection url " + storageConnection);
		log.info(trialContainerName);
	}

	@GET
	@Path("/info")
	@Produces(MediaType.TEXT_PLAIN)
	public Response info() {
		return Response.ok("Info called").build();
	}

	@GET
	@Path("/trials/{filename}")
	@Produces("text/html")
	public Response getHTML(@HeaderParam("x-ms-client-principal-name") String loggedInUserID,
			@PathParam("filename") String filename) {
		log.info("access file: (html) " + filename + " by " + loggedInUserID);
		if (!new API().isUserPermittedEndpoint(loggedInUserID, "TumourNGS")) {
			// Stop the request if user doesn't have permission for this API or web
			// component
			return Response.status(403).entity("User not permitted to access files").build();
		}

		// Azure login
		// String storageConnection =
		// "DefaultEndpointsProtocol=https;AccountName=rgtargetdevdiag313;AccountKey=JEJ7vnoPApDB1ma+/AV3nX/LHtvWdKjiCNUg1d4oFg4EFb24+CvCBEeGu5z0+sp39SCBpxP1Doz1/fLr1NA6uw==;EndpointSuffix=core.windows.net";

		try {
			CloudStorageAccount account = CloudStorageAccount.parse(storageConnection);
			CloudBlobClient serviceClient = account.createCloudBlobClient();

			// Create a BlockBlobURL to run operations on Block Blobs. Alternatively create
			// a ServiceURL, or ContainerURL for operations on Blob service, and Blob
			// containers
			// SharedKeyCredentials creds = new SharedKeyCredentials(accountName,
			// accountKey);

			CloudBlobContainer container = serviceClient.getContainerReference(trialContainerName);
			log.info("check if container exists : " + container.exists());
			container.createIfNotExists();

//			for (ListBlobItem blobItem : container.listBlobs()) {
//
//				// Getting URI
//
//				URI blobUri = blobItem.getUri();
//				log.info("content : " + blobUri.getPath());
//			}

			CloudBlockBlob myhtml = container.getBlockBlobReference(filename);

			StreamingOutput outStream = new StreamingOutput() {
				@Override
				public void write(final OutputStream output) throws IOException, WebApplicationException {
					try {
						myhtml.download(output);
					} catch (StorageException e) {
						log.log(Level.SEVERE, "StorageException", e);
					}
				}
			};

			return Response.ok(outStream).type("text/html")
					.header("Content-Disposition", "filename=\"" + myhtml.getName() + "\"") // optional
					.build();

		} catch (Exception ex) {
			log.log(Level.SEVERE, "storage exception ", ex);
		}
		return Response.serverError().build();
	}

	@GET
	@Path("/ihc/{filename}")
	@Produces("image/jpeg")
	public Response getImage(@HeaderParam("x-ms-client-principal-name") String loggedInUserID,
			@PathParam("filename") String filename) {
		log.info("access file image: " + filename + " by " + loggedInUserID);
		if (!new API().isUserPermittedEndpoint(loggedInUserID, "IHCReport")) {
			// Stop the request if user doesn't have permission for this API or web
			// component
			return Response.status(403).entity("User not permitted to access files").build();
		}

		// Azure login
		// String storageConnection =
		// "DefaultEndpointsProtocol=https;AccountName=rgtargetdevdiag313;AccountKey=JEJ7vnoPApDB1ma+/AV3nX/LHtvWdKjiCNUg1d4oFg4EFb24+CvCBEeGu5z0+sp39SCBpxP1Doz1/fLr1NA6uw==;EndpointSuffix=core.windows.net";

		try {
			CloudStorageAccount account = CloudStorageAccount.parse(storageConnection);
			CloudBlobClient serviceClient = account.createCloudBlobClient();

			// Create a BlockBlobURL to run operations on Block Blobs. Alternatively create
			// a ServiceURL, or ContainerURL for operations on Blob service, and Blob
			// containers
			// SharedKeyCredentials creds = new SharedKeyCredentials(accountName,
			// accountKey);

			CloudBlobContainer container = serviceClient.getContainerReference(containerName);
			log.info("check if container exists : " + container.exists());
			container.createIfNotExists();

			CloudBlockBlob image = container.getBlockBlobReference(filename);

			StreamingOutput outStream = new StreamingOutput() {
				@Override
				public void write(final OutputStream output) throws IOException, WebApplicationException {
					try {
						image.download(output);
					} catch (StorageException e) {
						//log.log(Level.SEVERE, "StorageException", e);
					}
				}
			};

			return Response.ok(outStream).type("image/jpeg")
					.header("Content-Disposition", "filename=\"" + image.getName() + "\"") // optional
					.build();

		} catch (Exception ex) {
			log.log(Level.SEVERE, "storage exception ", ex);
		}
		return Response.serverError().build();
	}

	
	@GET
	@Path("{filename}")
	@Produces("application/pdf")
	public Response getPDF(@HeaderParam("x-ms-client-principal-name") String loggedInUserID,
			@PathParam("filename") String filename) {
		log.info("access file pdf: " + filename + " by " + loggedInUserID);
		if (!new API().isUserPermittedEndpoint(loggedInUserID, "TumourNGS")) {
			// Stop the request if user doesn't have permission for this API or web
			// component
			return Response.status(403).entity("User not permitted to access files").build();
		}

		// Azure login
		// String storageConnection =
		// "DefaultEndpointsProtocol=https;AccountName=rgtargetdevdiag313;AccountKey=JEJ7vnoPApDB1ma+/AV3nX/LHtvWdKjiCNUg1d4oFg4EFb24+CvCBEeGu5z0+sp39SCBpxP1Doz1/fLr1NA6uw==;EndpointSuffix=core.windows.net";

		try {
			CloudStorageAccount account = CloudStorageAccount.parse(storageConnection);
			CloudBlobClient serviceClient = account.createCloudBlobClient();

			// Create a BlockBlobURL to run operations on Block Blobs. Alternatively create
			// a ServiceURL, or ContainerURL for operations on Blob service, and Blob
			// containers
			// SharedKeyCredentials creds = new SharedKeyCredentials(accountName,
			// accountKey);

			CloudBlobContainer container = serviceClient.getContainerReference(containerName);
			log.info("check if container exists : " + container.exists());
			container.createIfNotExists();

//			for (ListBlobItem blobItem : container.listBlobs()) {
//
//				// Getting URI
//
//				URI blobUri = blobItem.getUri();
//				log.info("content : " + blobUri.getPath());
//			}

			CloudBlockBlob mypdf = container.getBlockBlobReference(filename);

			StreamingOutput outStream = new StreamingOutput() {
				@Override
				public void write(final OutputStream output) throws IOException, WebApplicationException {
					try {
						mypdf.download(output);
					} catch (StorageException e) {
						log.log(Level.SEVERE, "StorageException", e);
					}
				}
			};

			return Response.ok(outStream).type("application/pdf")
					.header("Content-Disposition", "filename=\"" + mypdf.getName() + "\"") // optional
					.build();

		} catch (Exception ex) {
			log.log(Level.SEVERE, "storage exception ", ex);
		}
		return Response.serverError().build();
	}
	
	public static Boolean exists(String filename) {
		try {
			resource = ResourceBundle.getBundle("config");
			storageConnection = resource.getString("storageURL");
			containerName = resource.getString("storageContainerName");
			trialContainerName = resource.getString("trialContainerName");
			CloudStorageAccount account = CloudStorageAccount.parse(storageConnection);
			CloudBlobClient serviceClient = account.createCloudBlobClient();
			if(filename.toLowerCase().endsWith(".html")) {
				CloudBlobContainer container = serviceClient.getContainerReference(trialContainerName);
				return container.getBlockBlobReference(URLEncoder.encode(filename, "UTF-8")).exists();
			} else {
				CloudBlobContainer container = serviceClient.getContainerReference(containerName);
				return container.getBlockBlobReference(URLEncoder.encode(filename, "UTF-8")).exists();
			}
			
		} catch (Exception e) {
			log.log(Level.SEVERE, "exception", e);
			return Boolean.FALSE;
		}
		
	}
	
	public static String getFile(String prefix, String suffix) {
		try {
			resource = ResourceBundle.getBundle("config");
			storageConnection = resource.getString("storageURL");
			containerName = resource.getString("storageContainerName");
			CloudStorageAccount account = CloudStorageAccount.parse(storageConnection);
			CloudBlobClient serviceClient = account.createCloudBlobClient();
			CloudBlobContainer container = serviceClient.getContainerReference(containerName);
			Iterator<ListBlobItem> itBlob=container.listBlobs(prefix).iterator();
			while(itBlob.hasNext()) {
				ListBlobItem item = itBlob.next();
				URI uri= item.getUri();
				String filename=uri.toString().substring(uri.toString().lastIndexOf('/')+1);
				if(filename.endsWith(suffix)) {
					return filename;
				}
			}
		} catch (Exception e) {
			return null;
		}
		return null;
		
	}
	
	public static List<String> getAllFiles(String prefix, String suffix) {
		List<String> files = new ArrayList<String>();
		try {
			resource = ResourceBundle.getBundle("config");
			storageConnection = resource.getString("storageURL");
			containerName = resource.getString("storageContainerName");
			CloudStorageAccount account = CloudStorageAccount.parse(storageConnection);
			CloudBlobClient serviceClient = account.createCloudBlobClient();
			CloudBlobContainer container = serviceClient.getContainerReference(containerName);
			Iterator<ListBlobItem> itBlob=container.listBlobs(prefix).iterator();
			while(itBlob.hasNext()) {
				ListBlobItem item = itBlob.next();
				URI uri= item.getUri();
				String filename=uri.toString().substring(uri.toString().lastIndexOf('/')+1);
				if(filename.endsWith(suffix)) {
					files.add(filename);
				}
			}
			return files;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static Map<String, Date> getAllFilesDate(String prefix, String suffix) {
		HashMap<String, Date> files = new HashMap<>();
		try {
			resource = ResourceBundle.getBundle("config");
			storageConnection = resource.getString("storageURL");
			containerName = resource.getString("storageContainerName");
			CloudStorageAccount account = CloudStorageAccount.parse(storageConnection);
			CloudBlobClient serviceClient = account.createCloudBlobClient();
			CloudBlobContainer container = serviceClient.getContainerReference(containerName);
			Iterator<ListBlobItem> itBlob=container.listBlobs(prefix).iterator();
			while(itBlob.hasNext()) {
				ListBlobItem item = itBlob.next();
				URI uri= item.getUri();
				String filename=uri.toString().substring(uri.toString().lastIndexOf('/')+1);
				if(filename.endsWith(suffix)) {
					CloudBlob blob = container.getBlobReferenceFromServer(filename);
					files.put(filename, blob.getProperties().getLastModified());
				}
			}
			return files;
		} catch (Exception e) {
			return null;
		}
	}

}
