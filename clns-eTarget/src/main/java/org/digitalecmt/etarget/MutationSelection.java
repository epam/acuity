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

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.digitalecmt.etarget.dao.EditLockDAO;
import org.digitalecmt.etarget.dao.MutationSelectionDAO;

import com.google.gson.Gson;

public class MutationSelection extends API {
	private static final Logger log = Logger.getLogger(MutationSelection.class.getName());
	
	String personID;
	String userID;
	String geneVarientID;
	String ngsORctdna;
	
	public MutationSelection(String loggedInUserID, String dbPersonID, String geneVarientID, String ngsORctdna) {
		log.info("MutationSelection " + dbPersonID +" "+geneVarientID+" " + ngsORctdna);
	    this.userID = loggedInUserID;
	    this.personID = dbPersonID;
	    this.geneVarientID = geneVarientID;
	    this.ngsORctdna = ngsORctdna;
	}
	
	public String addData() {
		Map<String, String> responseData = new HashMap<String, String>();
		if(!super.isUserPermittedEndpoint(userID, "GetMutationSelection")) {
		      // Stop the request if user doesn't have permission for this API or web component
			Map<String,String> response = new HashMap<>();
	    	response.put("success", "false");
	    	response.put("error", "User not permitted to access: GetMutationSelection");
	    	return new Gson().toJson(response);
		} else {
			//lock patient
	    	EditLockDAO editLockDao = this.getContext().getBean(EditLockDAO.class);
	    	Boolean success=editLockDao.lock(userID, Integer.parseInt(personID));
	    	if(success==Boolean.FALSE) {
	    		String locker= editLockDao.getLocker(Integer.parseInt(personID));
	    		responseData.put("success", "false");
	    		responseData.put("error", "You cannot edit this patient as it is locked by: "+locker+". This change has not been saved.");
	    	} else {
				MutationSelectionDAO mutationSelectionDao = this.getContext().getBean(MutationSelectionDAO.class);
				try {
					mutationSelectionDao.addMutation(Integer.parseInt(this.personID), Integer.parseInt(this.geneVarientID), this.ngsORctdna);
					responseData.put("success", "true");
				} catch(SQLException ex) {
					log.severe("addData " + ex.getMessage());
					responseData.put("success", "false");
					responseData.put("error", ex.getMessage());
				}
	    	}
		
			return new Gson().toJson(responseData);
		}
	}
	
	public String removeData() {
		Map<String, String> responseData = new HashMap<String, String>();
		if(!super.isUserPermittedEndpoint(userID, "GetMutationSelection")) {
		      // Stop the request if user doesn't have permission for this API or web component
			Map<String,String> response = new HashMap<>();
	    	response.put("success", "false");
	    	response.put("error", "User not permitted to access: GetMutationSelection");
	    	return new Gson().toJson(response);
		} else {
			EditLockDAO editLockDao = this.getContext().getBean(EditLockDAO.class);
			Boolean success=editLockDao.lock(userID, Integer.parseInt(personID));
			if(success==Boolean.FALSE) {
				String locker= editLockDao.getLocker(Integer.parseInt(personID));
	    		responseData.put("success", "false");
	    		responseData.put("error", "You cannot edit this patient as it is locked by: "+locker+". This change has not been saved.");
	    	} else {
				log.info("removeData");
				MutationSelectionDAO mutationSelectionDao = this.getContext().getBean(MutationSelectionDAO.class);
				try {
					mutationSelectionDao.deleteMutation(Integer.parseInt(this.personID), Integer.parseInt(this.geneVarientID), this.ngsORctdna);
					responseData.put("success", "true");
				} catch(SQLException ex) {
					log.severe("removeData " + ex.getMessage());
					responseData.put("success", "false");
					responseData.put("error", ex.getMessage());
				}
	    	}
			return new Gson().toJson(responseData);
		}
	}

}
