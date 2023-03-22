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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.digitalecmt.etarget.dao.ChangeLogDAO;
import org.digitalecmt.etarget.dao.EditLockDAO;
import org.digitalecmt.etarget.dao.MeetingOutcomeDAO;
import org.digitalecmt.etarget.dbentities.MeetingOutcome;
import org.springframework.dao.DataAccessException;

import com.google.gson.*;

public class PostMeetingOutcome extends API {
  // Vars
  String personID;
  String meetingDate;
  String outcome;
  String notes;
  String gene_tables;
  String userID;
  String userName;
  String ctDNA;
  String tumourNGS;
  String fmTumour;
  String fmBlood;
  String genericGenomic;
  
  private static final Logger log = Logger.getLogger(PostMeetingOutcome.class.getName());


  
  public PostMeetingOutcome(String loggedInUserID, String loggedInUserName, String dbPersonID, String postedMeetingDate, 
		  String postedOutcome, String postedNotes, String ctDNA_, String tumourNGS_, String fmBlood_, String fmTumour_, String genericGenomic_) {
	    userID = loggedInUserID;
	    userName = loggedInUserName;
	    personID = dbPersonID;
	    meetingDate = postedMeetingDate;
	    outcome = postedOutcome;
	    notes = postedNotes;
	    ctDNA=ctDNA_;
	    tumourNGS=tumourNGS_;
	    fmTumour=fmTumour_;
	    fmBlood = fmBlood_;
	    genericGenomic = genericGenomic_;
	  }

  public PostMeetingOutcome() {
  }
  
  
  public String processRequest() {
	if(userID==null) {
		Map<String,String> response = new HashMap<>();
    	response.put("success", "false");
    	response.put("error", "Required information missing");
    	return new Gson().toJson(response);
	}
	  
    if(!super.isUserPermittedEndpoint(userID, "PostMeetingOutcome")) {
      // Stop the request if user doesn't have permission for this API or web component
    	Map<String,String> response = new HashMap<>();
    	response.put("success", "false");
    	response.put("error", "User not permitted to access: PostMeetingOutcome");
    	return new Gson().toJson(response);
    } else {
    	Map<String, String> responseData = new HashMap<String, String>();
    	//lock patient
    	EditLockDAO editLockDao = this.getContext().getBean(EditLockDAO.class);
    	Boolean success=editLockDao.lock(userID, Integer.parseInt(personID));
    	if(success==Boolean.FALSE) {
    		responseData.put("success", "false");
    		String locker= editLockDao.getLocker(Integer.parseInt(personID));
    		responseData.put("error", "You cannot edit this patient as it is locked by: "+locker+". This change has not been saved.");
    		return new Gson().toJson(responseData);
    	}
      // Process the request
      try {
        String[] mdF = meetingDate.split("/");
        String meetingDateFormatted = mdF[2]+"-"+mdF[1]+"-"+mdF[0];
        
        MeetingOutcomeDAO meetingOutcomeDao = this.getContext().getBean(MeetingOutcomeDAO.class);
        meetingOutcomeDao.addMeetingOutcome(userID, personID, meetingDateFormatted, outcome, notes, ctDNA, tumourNGS, fmBlood, fmTumour, genericGenomic);

       

        meetingOutcomeDao= this.getContext().getBean(MeetingOutcomeDAO.class);
            List<MeetingOutcome> mol = meetingOutcomeDao.getMeetingOutcomesByPersonID(Integer.parseInt(personID));
            if(mol!=null && mol.size()>0) {
            	responseData.put("mo_id", mol.get(0).getMo_id().toString());
            }	
        	
        	
          responseData.put("success", "true");


        String json = new Gson().toJson(responseData);
        return json;

      } catch(Exception e) {
    	 log.log(Level.SEVERE, "Exception:", e);
        e.printStackTrace();
        responseData.put("success", "false");
        responseData.put("error", "Failed to post meeting outcome.");
        
        return new Gson().toJson(responseData);
      }
    }
  }
  
  public String editRequest(String loggedInuserId, String meetingID, String type, String value) {
	  MeetingOutcomeDAO moDAO= this.getContext().getBean(MeetingOutcomeDAO.class);
	  ChangeLogDAO changeDAO = this.getContext().getBean(ChangeLogDAO.class);
	  EditLockDAO editLockDao = this.getContext().getBean(EditLockDAO.class);
	  Map<String, String> responseData = new HashMap<String, String>();
	  Boolean success=editLockDao.lock(loggedInuserId,moDAO.getMeetingOutcomeByMeetingID(Integer.parseInt(meetingID)).getPerson_id());
  	  if(success==Boolean.FALSE) {
  		  responseData.put("success", "false");
  		  String locker= editLockDao.getLocker(moDAO.getMeetingOutcomeByMeetingID(Integer.parseInt(meetingID)).getPerson_id());
  		  responseData.put("error", "You cannot edit this patient as it is locked by: "+locker+". Any unsaved changes will be lost.");
  		  return new Gson().toJson(responseData);
  	  }
	  
	  
	  DateFormat timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	  timestamp.setTimeZone(TimeZone.getTimeZone("Europe/London"));
	  String lastUpdatedFormated="";
	  
	  try {
		  MeetingOutcome mo = moDAO.getMeetingOutcomeByMeetingID(Integer.parseInt(meetingID));
		  moDAO.editMeetingOutcome(loggedInuserId, meetingID, type, value);
		  if(type.compareTo("notes")==0) {
			  changeDAO.addChange(loggedInuserId, "MEETING_OUTCOME", type, Integer.parseInt(meetingID), mo.getNotes(), value);
		  } else if (type.compareTo("outcome")==0) {
			  changeDAO.addChange(loggedInuserId, "MEETING_OUTCOME", type, Integer.parseInt(meetingID), mo.getOutcome(), value);
		  }
		  mo = moDAO.getMeetingOutcomeByMeetingID(Integer.parseInt(meetingID));
	      if(mo.getLast_updated()!=null) {
	    	  lastUpdatedFormated = timestamp.format(mo.getLast_updated());
	      }
		  responseData.put("success", "true");
		  responseData.put("lastUpdated", lastUpdatedFormated);
	  } catch (DataAccessException ex) {
		  ex.printStackTrace();
		  responseData.put("success", "false");
		  responseData.put("error", ex.getMessage());
	  }
	  String json = new Gson().toJson(responseData);
      return json;
  }
  
  public String printMeetingOutcome(String loggedInuserId, String meetingID) {
	  Map<String, String> responseData = new HashMap<String, String>();
	  if(this.isAdmin(loggedInuserId)) {
		  MeetingOutcomeDAO moDAO= this.getContext().getBean(MeetingOutcomeDAO.class);
		  DateFormat timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		  timestamp.setTimeZone(TimeZone.getTimeZone("Europe/London"));
		  String lastPrintedFormated="";
		  try {
			  moDAO.printMeetingOutcome(Integer.parseInt(meetingID));
		
			  MeetingOutcome mo = moDAO.getMeetingOutcomeByMeetingID(Integer.parseInt(meetingID));
		      if(mo.getLast_printed()!=null) {
		    	  lastPrintedFormated = timestamp.format(mo.getLast_printed());
		      }
			  responseData.put("success", "true");
			  responseData.put("lastPrinted", lastPrintedFormated);
		  } catch (DataAccessException ex) {
			  ex.printStackTrace();
			  responseData.put("success", "false");
			  responseData.put("error", ex.getMessage());
		  }
	  } else {
		  responseData.put("success", "false");
		  responseData.put("note", "Not a gatekeeper; won't use the timestamp");
	  }
	  String json = new Gson().toJson(responseData);
      return json;
  }
}
