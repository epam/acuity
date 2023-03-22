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

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.digitalecmt.etarget.dao.EditLockDAO;
import org.digitalecmt.etarget.rest.FileAccess;
import org.digitalecmt.etarget.support.Formater;
import org.digitalecmt.etarget.support.TimepointComparator;

import java.sql.*;
import java.text.*;
import com.google.gson.*;

public class GetSinglePatient extends API {
	private static final Logger log = Logger.getLogger(GetSinglePatient.class.getName());
  
  // Vars
  String personID;
  String targetID;
  int personAge;
  String conditionName;
  String subtypeName;
  String genderName;
  String site;
  String consultant;
  java.sql.Date consentDate;
  java.sql.Date conditionStartDate;
  java.sql.Date specimenDate1;
  Map<String, Object> patient = new HashMap<String, Object>();
  String userID;

  // Constructor
  public GetSinglePatient(String loggedInUserID, String dbPersonID) {
    userID = loggedInUserID;
    personID = dbPersonID;
  }

  public String processRequest() {
    if(!super.isUserPermittedEndpoint(userID, "GetSinglePatient")) {
      // Stop the request if user doesn't have permission for this API or web component
    	Map<String,String> response = new HashMap<>();
    	response.put("success", "false");
    	response.put("error", "User not permitted to access: GetSinglePatient");
    	return new Gson().toJson(response);
    } else {
      // Process the request
      try {
        String sql = "SELECT * FROM dbo.PATIENTS WHERE person_id = "+personID;
        ResultSet rs = super.getData(sql);
        log.info("after ResutlSet " + Boolean.toString(rs==null));
        while(rs.next()) {
          // Get the data
          targetID = rs.getString("target_id");
          personAge = rs.getInt("age_at_consent");
          conditionName = rs.getString("condition_name");
          subtypeName = rs.getString("subtype_name");
          genderName = rs.getString("gender_name");
          site = rs.getString("care_site_name");
          consentDate = rs.getDate("consent_date");
          conditionStartDate = rs.getDate("condition_start_date");
          consultant = rs.getString("consultant_name");
          // Reformat the date
          DateFormat dfconsentDate = new SimpleDateFormat("dd/MM/yyyy");
          
          String consentDateFormatted ="n/a";
          if(consentDate!=null)
        	  consentDateFormatted = dfconsentDate.format(consentDate);

          DateFormat dfconditionStartDate = new SimpleDateFormat("dd/MM/yyyy");
          String conditionStartDateFormatted="n/a";
          if(conditionStartDate!=null) {
        	  conditionStartDateFormatted = dfconditionStartDate.format(conditionStartDate);
          }

          // Add to a list
          patient.put("personID", personID);
          patient.put("targetID", targetID);
          patient.put("personAge", Integer.toString(personAge));
          patient.put("conditionName", (subtypeName==null)?conditionName:subtypeName);
          patient.put("genderName", genderName);
          patient.put("site", site);
          patient.put("consentDate", consentDateFormatted);
          patient.put("conditionStartDate", conditionStartDateFormatted);
          patient.put("consultant", consultant);
          String trialreport="trial_report_"+targetID+".html";
          log.info("name " + trialreport);
          if(FileAccess.exists(trialreport)) {
        	  patient.put("trialFile",trialreport);
          } else {
        	  patient.put("trialFile","");
          }
          List<String> qciFiles=FileAccess.getAllFiles(targetID+"T", "QCI.pdf");
          Map<String,String> qciMap=new TreeMap<String,String>(new TimepointComparator());
          for(String qciFile : qciFiles) {
        	  String timepoint= qciFile.substring(targetID.length(), qciFile.indexOf("QCI.pdf"));
        	  if(Pattern.matches(targetID+"T\\d*QCI.pdf", qciFile)){
	        	  qciMap.put(timepoint, qciFile);
	        	  log.info(qciFile+" "+ timepoint);
        	  }
          }
          patient.put("qciFiles", qciMap);
          
          String rnareport_prefix=targetID+"T1SM";
          List<String> rnaFiles=FileAccess.getAllFiles(rnareport_prefix, ".pdf");
          patient.put("rnaFiles", rnaFiles);
        }
        
        //check edit permission
        EditLockDAO editLockDao = this.getContext().getBean(EditLockDAO.class);
        if(editLockDao.isEditor(userID)) {
        	try {
	        	if(editLockDao.isLocked(userID, Integer.parseInt(personID))) {
	        		patient.put("editLockMessage", "This patient is currently being edited by " + editLockDao.getLocker(Integer.parseInt(personID)));
	        	} else {
	        		patient.put("editLockMessage", "");
	        	}
        	} catch (NumberFormatException e) {
        		patient.put("editLockMessage", "");
        	}
        	
        } else {
        	patient.put("editLockMessage", "");
        }

        // Return patient data as JSON string
        String json = new Gson().toJson(patient);
        return json;

      } catch(Exception e) {
        e.printStackTrace();
        log.log(Level.SEVERE,e.getMessage(), e);
        Map<String,String> response = new HashMap<>();
    	response.put("success", "false");
    	response.put("error", "Failed to get patient data.");
    	return new Gson().toJson(response);
      }
    }
  }
}
