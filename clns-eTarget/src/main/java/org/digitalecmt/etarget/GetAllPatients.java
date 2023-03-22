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

import org.digitalecmt.etarget.dao.EditLockDAO;
import org.digitalecmt.etarget.dao.MeetingOutcomeDAO;

import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.google.gson.Gson;

public class GetAllPatients extends API {
  // Vars
  String targetID;
  Integer personID;
  java.sql.Date consentDate;
  String conditionName;
  String subtypeName;
  String consultantName;
  String status;
  java.sql.Date mtbDate;
  String mtbDateFormatted;
  List<Map<String,String>> patients = new ArrayList<>();
  String userID;
  Integer mo_id;
  String care_site;

  private static Logger log = Logger.getLogger(GetAllPatients.class.getName());
  // Constructor
  public GetAllPatients(String loggedInUserID) {
    userID = loggedInUserID;
  }

  public String processRequest() {
    if(!super.isUserPermittedEndpoint(userID, "GetAllPatients")) {
      // Stop the request if user doesn't have permission for this API or web component
    	Map<String,String> response = new HashMap<>();
    	response.put("success", "false");
    	response.put("error", "User not permitted to access: GetAllPatients");
    	return new Gson().toJson(response);
    } else {
    	//unlock edit
    	EditLockDAO editLockDao = this.getContext().getBean(EditLockDAO.class);
    	editLockDao.unlockAll(userID);
    	MeetingOutcomeDAO meetingOutcomeDao = this.getContext().getBean(MeetingOutcomeDAO.class);
    	List<Integer> person_ids = meetingOutcomeDao.getPersonIdsForPrinting();
    	
      // Process the request
      try {
    	boolean isAdmin=super.isAdmin(userID);  
    	boolean isChiefInvestigator=super.isChiefInvestigator(userID);
    	  
        String sql = "SELECT * FROM dbo.PATIENTS p OUTER APPLY (SELECT  TOP 1 * FROM dbo.MEETING_OUTCOME mo WHERE mo.person_id = p.person_id ORDER BY mo.mo_id DESC) mo ORDER BY consent_date DESC, target_id DESC";
        ResultSet rs = super.getData(sql);

        while(rs.next()) {
          // Get the data
          personID = rs.getInt("person_id");
          targetID = rs.getString("target_id");
          consentDate = rs.getDate("consent_date");
          conditionName = rs.getString("condition_name");
          subtypeName = rs.getString("subtype_name");
          consultantName = rs.getString("consultant_name");
          status = (rs.getString("outcome") == null) ? "Awaiting Discussion": rs.getString("outcome");
          mtbDate = rs.getDate("meeting_date");
          mo_id = rs.getInt("mo_id");
          care_site = rs.getString("care_site_name");


          // Reformat the date
          DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
          String consentDateFormatted = df.format(consentDate);

          if(mtbDate != null) {
            DateFormat dfmtbDate = new SimpleDateFormat("dd/MM/yyyy");
            mtbDateFormatted = dfmtbDate.format(mtbDate);
          } else {
            mtbDateFormatted = "-";
          }

          // Add to a list
          Map<String, String> patient = new HashMap<String, String>();
          patient.put("personID", personID.toString());
          patient.put("targetID", targetID);
          patient.put("consentDate", consentDateFormatted);
          patient.put("conditionName", (subtypeName==null)?conditionName:subtypeName);
          patient.put("consultantName", consultantName);
          patient.put("careSite", care_site);
          patient.put("status", status);
          patient.put("mtbDate", mtbDateFormatted);
          if(isAdmin) {
        	  patient.put("requirePrinting", person_ids.contains(personID)?"true":"false");
          }
          if(isChiefInvestigator) {
        	  patient.put("delete", "true");
          }

          // Add to the parent list
          patients.add(patient);
        }

        // Return patient data as JSON string
        String json = new Gson().toJson(patients);
        return json;

      } catch(Exception e) {
        e.printStackTrace();
        Map<String,String> response = new HashMap<>();
        log.log(Level.SEVERE, "failed get patients", e);
    	response.put("success", "false");
    	response.put("error", "Failed to construct patient list.");
    	return new Gson().toJson(response);
      }
    }
  }
}
