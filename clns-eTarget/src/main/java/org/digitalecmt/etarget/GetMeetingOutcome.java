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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.digitalecmt.etarget.config.ConfigDataSources;
import org.digitalecmt.etarget.dao.MeetingOutcomeDAO;
import org.digitalecmt.etarget.dbentities.MeetingOutcome;

import com.google.gson.Gson;

public class GetMeetingOutcome extends API {

	private static final Logger log = Logger.getLogger(GetMeetingOutcome.class.getName());
	// Vars
	String personID;
	String updated_by;
	String outcome;
	String notes;
	java.sql.Date meetingDate;
	String meetingDateFormatted;
	String gene_tables;
	Integer meeting_id;
	// java.sql.Date lastUpdated;
	// java.sql.Date lastPrinted;
//	java.sql.Timestamp lastUpdated;
	String lastUpdatedFormated = "";
	String userID;
	int personIDint = 0;
	List<ConfigDataSources> addSources;

	// Constructor
	public GetMeetingOutcome(String loggedInUserID, String dbPersonID) {
		userID = loggedInUserID;
		personID = dbPersonID;
		try {
			personIDint = Integer.parseInt(personID);
		} catch (NumberFormatException e) {
			log.severe("no personID " + personID);
			personIDint = 0;
		}
		addSources = (List<ConfigDataSources>)this.getContext().getBean("configuredDataSources");
	}

	public String processRequest() {
		if (!super.isUserPermittedEndpoint(userID, "GetMeetingOutcome")) {
			// Stop the request if user doesn't have permission for this API or web
			// component
			Map<String,String> response = new HashMap<>();
	    	response.put("success", "false");
	    	response.put("error", "User not permitted to access: GetMeetingOutcome");
	    	return new Gson().toJson(response);
		} else {
			boolean isAdmin=super.isAdmin(userID); 
			List<Integer> needPrinting=null;
			
			MeetingOutcomeDAO mo = this.getContext().getBean(MeetingOutcomeDAO.class);
			mo.setAdditionalSources(addSources);
			try {
				List<MeetingOutcome> lmos = mo.getMeetingOutcomesByPersonID(personIDint);
				if(isAdmin) {
					needPrinting=mo.getMeetingOutcomesForPrinting(personIDint);
				}
				List<Map<String, Object>> meetings = new ArrayList<>();
				for (MeetingOutcome meetingoutcome : lmos) {
					Map<String, Object> meeting = new HashMap<>();
					// Reformat the date
					DateFormat dfmeetingDate = new SimpleDateFormat("dd/MM/yyyy");
					Date meetingDate = meetingoutcome.getMeeting_date();
					if(meetingDate!=null) {
						meetingDateFormatted = dfmeetingDate.format(meetingDate);
					} else {
						meetingDateFormatted = "";
					}

					DateFormat timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					timestamp.setTimeZone(TimeZone.getTimeZone("Europe/London"));
					if (meetingoutcome.getLast_updated() != null) {
						lastUpdatedFormated = timestamp.format(meetingoutcome.getLast_updated());
					}

					meeting.put("meeting_id", meetingoutcome.getMo_id());
					meeting.put("meetingDate", meetingDateFormatted);
					meeting.put("updatedBy", meetingoutcome.getUpdated_by());
					meeting.put("lastUpdated", lastUpdatedFormated);
					if(meetingoutcome.getLast_printed()!=null) {
						meeting.put("lastPrinted", timestamp.format(meetingoutcome.getLast_printed()));
					} else {
						meeting.put("lastPrinted", "");
					}
					meeting.put("outcome", meetingoutcome.getOutcome());
					meeting.put("notes", meetingoutcome.getNotes());
					meeting.put("ctDNA", meetingoutcome.getCtDNA());
					meeting.put("tumourNGS", meetingoutcome.getTumourNGS());
					meeting.put("fmBlood", meetingoutcome.getFMBlood());
					meeting.put("fmTumour", meetingoutcome.getFMTumour());
					meeting.put("genericGenomic", meetingoutcome.getGenericGenomic());
					meeting.put("summary", meetingoutcome.getSummary());
					if(needPrinting!=null) {
						meeting.put("needPrinting", needPrinting.contains(meetingoutcome.getMo_id()));
					}
					if(meetingoutcome.getCtDNA().size()>0) {
						Map<String,Object> firstblood=meetingoutcome.getCtDNA().get(0);
						if(firstblood.get("specimenDate")!=null) {
							meeting.put("ctDNASampleDate",firstblood.get("specimenDate")); 
						} else {
							meeting.put("ctDNASampleDate",firstblood.get("")); 
						}
					} else {
						Calendar sampleDate=mo.getSampleDateBlood(meetingoutcome.getMeeting_date(), meetingoutcome.getPerson_id());
						if(sampleDate!=null) {
							meeting.put("ctDNASampleDate", dfmeetingDate.format(sampleDate.getTime()));
						} else {
							meeting.put("ctDNASampleDate","");
						}
							
					}
					if(meetingoutcome.getTumourNGS().size()>0) {
						Map<String,Object> firsttumour=meetingoutcome.getTumourNGS().get(0);
						if(firsttumour.get("specimenDate")!=null) {
							meeting.put("tumourSampleDate",firsttumour.get("specimenDate"));
						} else if(firsttumour.get("SpecimentDate")!=null){ 
							//old spelling mistake lives on in the database :-(
							meeting.put("tumourSampleDate",firsttumour.get("SpecimentDate"));
						} else {
							meeting.put("tumourSampeleDate","");
						}
					} else {
						Calendar sampleDate=mo.getSampleDateTumour(meetingoutcome.getMeeting_date(), meetingoutcome.getPerson_id());
						if(sampleDate!=null) {
							meeting.put("tumourSampleDate",dfmeetingDate.format(sampleDate.getTime()));
						} else {
							meeting.put("tumourSampleDate","");
						}
					}
					meetings.add(meeting);
				}
				String json = new Gson().toJson(meetings);
				return json;

			} catch (Exception e) {
				log.log(Level.SEVERE, e.getMessage(), e);
				e.printStackTrace();
				Map<String,String> response = new HashMap<>();
		    	response.put("success", "false");
		    	response.put("error", "Failed to get meeting outcome.");
		    	return new Gson().toJson(response);
			}
		}

	}
}
