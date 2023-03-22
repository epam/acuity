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

import org.digitalecmt.etarget.dao.MeetingOutcomeDAO;
import org.digitalecmt.etarget.dao.NgsLibDAO;
import org.digitalecmt.etarget.dao.TumourNgsDAO;
import org.digitalecmt.etarget.dbentities.NgsLib;
import org.digitalecmt.etarget.dbentities.TumourNgs;
import org.digitalecmt.etarget.dbentities.TumourNgsExt;

import java.sql.*;
import java.sql.Date;
import java.text.*;
import com.google.gson.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

import de.jollyday.*;

public class GeneralInfo extends API {
	
	private static final Logger log = Logger.getLogger(GeneralInfo.class.getName());
	// Vars
	String personID;
	java.sql.Date procedureStartDate;
	java.sql.Date procedureEndDate;
	String procedureName;
	java.sql.Date bloodSampleDate;
	java.sql.Date tumourSampleDate;
	String specimenName;
	java.sql.Date gdlRequest;
	String specimenNature;
	Integer timepoint;
	String tumourNature;
	java.sql.Date reportDate;
	java.sql.Date specimenDate;
	int baselineNumber;
	int runNumber;
	Map<String,Object> treatmentHistory = new HashMap<>();
	List<Map<String,Object>> treatments = new ArrayList<>();
	List<Map<String,Object>> bloodSamples = new ArrayList<>();
	List<Map<String,Object>> tumourSamples = new ArrayList<>();
	List<Map<String,Object>> reportCEP = new ArrayList<>();
	List<Map<String,Object>> reportGDL = new ArrayList<>();
	List<Map<String,Object>> reportIHC = new ArrayList<>();
	// HolidayManager m =
	// HolidayManager.getInstance(HolidayCalendar.UNITED_KINGDOM);
	HolidayManager m = HolidayManager.getInstance(ManagerParameters.create(HolidayCalendar.UNITED_KINGDOM));
	String userID;

	// Constructor
	public GeneralInfo(String loggedInUserID, String dbPersonID) {
		userID = loggedInUserID;
		personID = dbPersonID;
	}

	public String processRequest() {
		if (!super.isUserPermittedEndpoint(userID, "GeneralInfo")) {
			// Stop the request if user doesn't have permission for this API or web
			// component
			Map<String,String> response = new HashMap<>();
	    	response.put("success", "false");
	    	response.put("error", "User not permitted to access: GeneralInfo");
	    	return new Gson().toJson(response);
		} else {
			// Process the request
			try {
				Boolean isAdmin = isAdmin(userID);
				String treatmentHistorySQL = "SELECT distinct procedure_start_date,procedure_end_date, procedure_name  FROM dbo.PROCEDURE_OCCURRENCE, dbo.CONCEPT_PROCEDURE WHERE CONCEPT_PROCEDURE.procedure_concept_id = PROCEDURE_OCCURRENCE.procedure_concept_id AND PROCEDURE_OCCURRENCE.person_id = "
						+ personID + " ORDER BY PROCEDURE_OCCURRENCE.procedure_start_date ASC";
				ResultSet rs1 = super.getData(treatmentHistorySQL);

				while (rs1.next()) {
					// Get the data
					procedureStartDate = rs1.getDate("procedure_start_date");
					procedureEndDate = rs1.getDate("procedure_end_date");
					procedureName = rs1.getString("procedure_name");

					// Reformat the date
					DateFormat dfprocedureStartDate = new SimpleDateFormat("dd/MM/yyyy");
					String procedureStartDateFormatted = dfprocedureStartDate.format(procedureStartDate);

					DateFormat dfprocedureEndDate = new SimpleDateFormat("dd/MM/yyyy");
					String procedureEndDateFormatted = dfprocedureEndDate.format(procedureEndDate);

					if (procedureName.trim().length() == 0) {
						procedureName = "No treatment has been recorded.";
						procedureEndDateFormatted = "";
						procedureStartDateFormatted = "";
					} else {
						if (procedureEndDateFormatted.contains("/1900")) {
							procedureEndDateFormatted = "No end date recorded";
						}

						if (procedureStartDateFormatted.contains("/1900")) {
							procedureStartDateFormatted = "No start date recorded";
						}
					}

					// Add to the treatment history data
					Map<String,Object> procedure = new HashMap<>();
					procedure.put("personID", personID);
					procedure.put("procedureName", procedureName);
					procedure.put("procedureStartDate", procedureStartDateFormatted);
					procedure.put("procedureEndDate", procedureEndDateFormatted);

					treatments.add(procedure);
				}

				String sampleBloodSQL = "SELECT distinct specimen_date, specimen.baseline_number FROM dbo.SPECIMEN AS specimen " + 
						"Full outer join dbo.CONCEPT_SPECIMEN AS conceptSpec on specimen.specimen_concept_id=conceptSpec.specimen_concept_id " + 
						"FULL OUTER JOIN dbo.MEASUREMENT_GENE_PANEL AS genePanel ON specimen.specimen_id = genePanel.specimen_id WHERE specimen.person_id = " + personID + 
						" AND conceptSpec.specimen_name LIKE '%Plasma%' ORDER BY specimen.specimen_date ASC";

				ResultSet rs2 = super.getData(sampleBloodSQL);

				while (rs2.next()) {
					// Get the data
					bloodSampleDate = rs2.getDate("specimen_date");

					// Date formatting
					DateFormat dfbloodSampleDate = new SimpleDateFormat("dd/MM/yyyy");
					String bloodSampleDateFormatted = dfbloodSampleDate.format(bloodSampleDate);
					int baseline = rs2.getInt("baseline_number");

					// Add to the sample history data
					Map<String,Object> sample = new HashMap<>();
					sample.put("dateBlood", bloodSampleDateFormatted);
					sample.put("baseline_number", baseline);

					bloodSamples.add(sample);
				}

				String sampleTumourSQL = "with issue_dates as\n" + 
						"(SELECT distinct min(date_issued) as date_issued, specimen.specimen_id \n" + 
						"FROM dbo.SPECIMEN AS specimen FULL OUTER JOIN dbo.MEASUREMENT_GENE_PANEL AS genePanel ON specimen.specimen_id = genePanel.specimen_id \n" + 
						"FULL OUTER JOIN dbo.REPORT AS report ON genePanel.measurement_gene_panel_id = report.measurement_gene_panel_id FULL OUTER JOIN dbo.CONCEPT_SPECIMEN AS conceptSpec ON specimen.specimen_concept_id = conceptSpec.specimen_concept_id FULL OUTER JOIN dbo.GDL_REQUEST AS gdlRequest ON specimen.specimen_id = gdlRequest.specimen_id \n" + 
						"WHERE specimen.person_id = "+personID+" AND conceptSpec.specimen_name LIKE '%Tumour%' AND ngs_run!='FM' group by specimen.specimen_id)\n" + 
						"SELECT distinct specimen_date, specimen.specimen_id, specimen.baseline_number, specimen_name,date_requested,anatomy_name,sample_type, coalesce(issue_dates.date_issued, '9999-12-12')\n" + 
						"FROM dbo.SPECIMEN AS specimen FULL OUTER JOIN dbo.CONCEPT_SPECIMEN AS conceptSpec ON specimen.specimen_concept_id = conceptSpec.specimen_concept_id \n" + 
						"FULL OUTER JOIN dbo.GDL_REQUEST AS gdlRequest ON gdlRequest.specimen_id = specimen.specimen_id FULL OUTER JOIN dbo.CONCEPT_ANATOMY AS conceptAnatomy ON specimen.anatomic_site_id = conceptAnatomy.anatomy_concept_id \n" + 
						"FULL OUTER JOIN issue_dates on issue_dates.specimen_id=SPECIMEN.specimen_id\n" + 
						"WHERE specimen.person_id = "+personID + 
						"AND conceptSpec.specimen_name LIKE '%Tumour%' ORDER BY baseline_number, coalesce(issue_dates.date_issued, '9999-12-12') ASC, gdlRequest.date_requested ASC";
				ResultSet rs3 = super.getData(sampleTumourSQL);

				while (rs3.next()) {
					// Get the data
					tumourSampleDate = rs3.getDate("specimen_date");
					specimenName = rs3.getString("specimen_name");
					gdlRequest = rs3.getDate("date_requested");
					tumourNature = rs3.getString("anatomy_name");
					specimenNature = rs3.getString("sample_type");
					timepoint = rs3.getInt("baseline_number");
					if(specimenNature==null || specimenNature.trim().length()==0) {
						specimenNature="Not recorded";
					}

					// Add to the sample history data
					Map<String,Object> sample = new HashMap<>();
					sample.put("specimenName", specimenName);
					sample.put("tumourNature", tumourNature);
					sample.put("specimenNature", specimenNature);
					sample.put("timepoint", "Bx"+timepoint);

					// Date formatting
					if (tumourSampleDate != null) {
						DateFormat dftumourSampleDate = new SimpleDateFormat("dd/MM/yyyy");
						String tumourSampleDateFormatted = dftumourSampleDate.format(tumourSampleDate);
						sample.put("dateTumour", tumourSampleDateFormatted);
					} else {
						sample.put("dateTumour", "Not recorded");
					}

					if (gdlRequest != null) {
						DateFormat dfgdlRequest = new SimpleDateFormat("dd/MM/yyyy");
						String gdlRequestFormatted = dfgdlRequest.format(gdlRequest);
						sample.put("gdlRequest", gdlRequestFormatted);
					} else {
						sample.put("gdlRequest", "");
					}

					tumourSamples.add(sample);
				}

				NgsLibDAO ngs = this.getContext().getBean(NgsLibDAO.class);
				MeetingOutcomeDAO meeting = this.getContext().getBean(MeetingOutcomeDAO.class);
				
				
				String reportGDLSQL = "SELECT distinct date_requested, date_issued, specimen.baseline_number, genePanel.run_number, specimen.specimen_id FROM dbo.SPECIMEN AS specimen LEFT JOIN LATEST_MEASUREMENT_GENE_PANEL_IDS on specimen.specimen_id=LATEST_MEASUREMENT_GENE_PANEL_IDS.specimen_id\n" + 
						"LEFT JOIN dbo.MEASUREMENT_GENE_PANEL AS genePanel ON LATEST_MEASUREMENT_GENE_PANEL_IDS.measurement_gene_panel_id = genePanel.measurement_gene_panel_id FULL OUTER JOIN dbo.REPORT AS report ON genePanel.measurement_gene_panel_id = report.measurement_gene_panel_id FULL OUTER JOIN dbo.CONCEPT_SPECIMEN AS conceptSpec ON specimen.specimen_concept_id = conceptSpec.specimen_concept_id FULL OUTER JOIN dbo.GDL_REQUEST AS gdlRequest ON specimen.specimen_id = gdlRequest.specimen_id WHERE specimen.person_id = "
						+ personID + " AND conceptSpec.specimen_name LIKE '%Tumour%' AND ngs_run!='FM'  ORDER BY report.date_issued ASC";
				ResultSet rs4 = super.getData(reportGDLSQL);
				int baseline=0;
				while (rs4.next()) {
					// Get the data
					specimenDate = rs4.getDate("date_requested");
					reportDate = rs4.getDate("date_issued");
					//ignore baseline number from the db as this is always 1
					baseline = rs4.getInt("baseline_number");
					int specimen_id = rs4.getInt("specimen_id");
					int run = rs4.getInt("run_number");

					// Add to the report data
					// List runs = new ArrayList<>();
					Map<String,Object> report = new HashMap<>();
					report.put("baseline_number", baseline);
					report.put("run_number", run == 0 ? 1 : run);
					
					if(isAdmin) {
						if(run==0) run=1;
						NgsLib latest = ngs.findLatestBySpecimenRun(specimen_id, baseline, run);
						Calendar lastMO = meeting.getLastMeetingOutcomeDate(Integer.parseInt(personID));
						if(latest!=null && lastMO!=null && latest.getIngestion_calendar()!=null  && latest.getIngestion_calendar().compareTo(lastMO)>0){
							report.put("deletable", Boolean.TRUE);
						} else if(latest!=null && lastMO==null){
							report.put("deletable", Boolean.TRUE);
						} else {
							report.put("deletable", Boolean.FALSE);
						}
						report.put("specimen_id", specimen_id);
					}
					if (specimenDate != null) {
						DateFormat dfspecimenDate = new SimpleDateFormat("dd/MM/yyyy");
						String specimenDateFormatted = dfspecimenDate.format(specimenDate);
						if (reportDate == null) {
							// Calculate days taken: Days minus weekends
							DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
							java.util.Date date1 = df.parse(specimenDateFormatted);
							java.util.Date date2 = new java.util.Date();
							Calendar cal1 = Calendar.getInstance();
							Calendar cal2 = Calendar.getInstance();
							cal1.setTime(date1);
							cal2.setTime(date2);

							report.put("reportIssued", "Report not issued");
							// report.put("daysTaken", numberOfDays+" ("+specimenDateFormatted+" to
							// present)");
							report.put("daysTaken", "");
						} else {
							// Date formatting
							DateFormat dfreportDate = new SimpleDateFormat("dd/MM/yyyy");
							String reportDateFormatted = dfreportDate.format(reportDate);
							report.put("reportIssued", reportDateFormatted);

							// Calculate days taken: Days minus weekends
							DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
							java.util.Date date1 = df.parse(specimenDateFormatted);
							java.util.Date date2 = df.parse(reportDateFormatted);
							Calendar cal1 = Calendar.getInstance();
							Calendar cal2 = Calendar.getInstance();
							cal1.setTime(date1);
							cal2.setTime(date2);

							int numberOfDays = 0;
							while (!cal1.after(cal2)) {
								LocalDate todaysDate = LocalDate
										.parse(new SimpleDateFormat("yyyy-MM-dd").format(cal1.getTime()));
								boolean isHoliday = m.isHoliday((LocalDate) todaysDate, (HolidayType) null, "en");

								// System.out.println("-------> "+String.valueOf(isHoliday)+"
								// "+String.valueOf((LocalDate)todaysDate));

								if ((Calendar.SATURDAY != cal1.get(Calendar.DAY_OF_WEEK))
										&& (Calendar.SUNDAY != cal1.get(Calendar.DAY_OF_WEEK)) && !isHoliday) {
									numberOfDays++;
								}
								cal1.add(Calendar.DATE, 1);
							}
							// report.put("daysTaken", numberOfDays+" ("+specimenDateFormatted+" to
							// "+reportDateFormatted+")");
							report.put("daysTaken", numberOfDays);
						}
					} else {
						report.put("reportIssued", "Report not issued");
						report.put("daysTaken", "Not requested yet");
					}
					
					// runs.add(report);
					reportGDL.add(report);
				}

				String reportCEPSQL = "WITH CTE AS(\n" + 
						"SELECT specimen.specimen_id, specimen_date, date_issued, specimen.baseline_number, genePanel.run_number, RN = ROW_NUMBER() OVER (PARTITION  by specimen.baseline_number,genePanel.run_number\n" + 
						"ORDER BY specimen.baseline_number ASC,genePanel.run_number ASC, date_issued DESC, genePanel.measurement_gene_panel_id DESC)  \n" + 
						"FROM dbo.SPECIMEN AS specimen \n" + 
						"FULL OUTER JOIN dbo.MEASUREMENT_GENE_PANEL AS genePanel ON specimen.specimen_id = genePanel.specimen_id \n" + 
						"FULL OUTER JOIN dbo.REPORT AS report ON genePanel.measurement_gene_panel_id = report.measurement_gene_panel_id \n" + 
						"FULL OUTER JOIN dbo.CONCEPT_SPECIMEN AS conceptSpec ON specimen.specimen_concept_id = conceptSpec.specimen_concept_id \n" + 
						"WHERE specimen.person_id = "+personID+" AND conceptSpec.specimen_name LIKE '%Plasma%' and run_number is not null \n" + 
						")\n" + 
						"select * from CTE where RN=1 order by specimen_date";
				ResultSet rs5 = super.getData(reportCEPSQL);

				while (rs5.next()) {
					// Get the data
					specimenDate = rs5.getDate("specimen_date");
					reportDate = rs5.getDate("date_issued");
					baseline = rs5.getInt("baseline_number");
					int specimen_id = rs5.getInt("specimen_id");
					int run = rs5.getInt("run_number");
					log.info("run number " + run);

					DateFormat dfspecimenDate = new SimpleDateFormat("dd/MM/yyyy");
					String specimenDateFormatted = dfspecimenDate.format(specimenDate);

					// Add to the report data
					// List runs = new ArrayList<>();
					Map<String,Object> report = new HashMap<>();
					
					if(isAdmin) {
						NgsLib latest = ngs.findLatestBySpecimenRun(specimen_id, baseline, run);
						Calendar lastMO = meeting.getLastMeetingOutcomeDate(Integer.parseInt(personID));
						if(latest!=null && lastMO!=null && latest.getIngestion_calendar()!=null  && latest.getIngestion_calendar().compareTo(lastMO)>0){
							report.put("deletable", Boolean.TRUE);
						} else if(latest!=null && lastMO==null){
							report.put("deletable", Boolean.TRUE);
						} else {
							report.put("deletable", Boolean.FALSE);
						}
						report.put("specimen_id", rs5.getInt("specimen_id"));
							
					}

					if (specimenDate != null) {
						report.put("baseline_number",
								baseline == 0 ? 1 : baseline);
						//do not show any data about time series
						if(baseline>=3) continue;
						report.put("run_number", run == 0 ? 1 : run);
						if (reportDate == null) {
							report.put("daysTaken", "");
						} else if(baseline>=3) {
							report.put("daysTaken", "");
							DateFormat dfreportDate = new SimpleDateFormat("dd/MM/yyyy");
							String reportDateFormatted = dfreportDate.format(reportDate);
							report.put("reportIssued", reportDateFormatted);
						} 
						else{
							// Date formatting
							DateFormat dfreportDate = new SimpleDateFormat("dd/MM/yyyy");
							String reportDateFormatted = dfreportDate.format(reportDate);
							report.put("reportIssued", reportDateFormatted);

							// Calculate days taken: Days minus weekends
							DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
							java.util.Date date1 = df.parse(specimenDateFormatted);
							java.util.Date date2 = df.parse(reportDateFormatted);
							Calendar cal1 = Calendar.getInstance();
							Calendar cal2 = Calendar.getInstance();
							cal1.setTime(date1);
							cal2.setTime(date2);

							int numberOfDays = 0;
							while (!cal1.after(cal2)) {
								LocalDate todaysDate = LocalDate
										.parse(new SimpleDateFormat("yyyy-MM-dd").format(cal1.getTime()));
								boolean isHoliday = m.isHoliday((LocalDate) todaysDate, (HolidayType) null, "en");

								// System.out.println("-------> "+String.valueOf(isHoliday)+"
								// "+String.valueOf((LocalDate)todaysDate));

								if ((Calendar.SATURDAY != cal1.get(Calendar.DAY_OF_WEEK))
										&& (Calendar.SUNDAY != cal1.get(Calendar.DAY_OF_WEEK)) && !isHoliday) {
									numberOfDays++;
								}
								cal1.add(Calendar.DATE, 1);
							}
							// report.put("daysTaken", numberOfDays+" ("+specimenDateFormatted+" to
							// "+reportDateFormatted+")");
							report.put("daysTaken", numberOfDays);
						}
					} else {
						//do not show any data about time series
						if(baseline>=3) continue;
						report.put("reportIssued", "Report not issued");
						report.put("daysTaken", "Not requested yet");
					}

					// runs.add(report);
					reportCEP.add(report);
				}
					
				//IHC report
				Integer personIDint = Integer.parseInt(personID);
				TumourNgsDAO tumourDAO = this.getContext().getBean(TumourNgsDAO.class);
				List<TumourNgs> specimen = tumourDAO.findFMTumourByPersonID(personIDint);
				TumourNgsExt.adjustBaselines(specimen);
				String reportIHCSQL="select distinct sample_received_date,report_date, specimen_id from IHC_REPORT where person_id="+personID;
				ResultSet rs6 = super.getData(reportIHCSQL);
				while (rs6.next()) {
					Map<String,Object> ihcReport = new HashMap<>();
					Date receivedSQL = rs6.getDate("sample_received_date");
					LocalDate receivedLD = receivedSQL!=null?receivedSQL.toLocalDate():null;
					Date reportSQL = rs6.getDate("report_date");
					LocalDate reportLD = reportSQL!=null?reportSQL.toLocalDate():null;
					int days=0;
					if(receivedLD!=null && reportLD!=null) {
						//even if on the same day count 1
						days++;
					}
					while(receivedLD!=null && reportLD!=null && receivedLD.isBefore(reportLD)) {
						boolean isHoliday = m.isHoliday(receivedLD, (HolidayType) null, "en");
						if(receivedLD.getDayOfWeek()!=DayOfWeek.SATURDAY &&
								receivedLD.getDayOfWeek()!=DayOfWeek.SUNDAY &&
								!m.isHoliday((LocalDate) receivedLD, (HolidayType) null, "en")) {
							days++;
						}
						receivedLD=receivedLD.plusDays(1);
					}
					
					int specimen_id = rs6.getInt("specimen_id");
					for(TumourNgs sp:specimen) {
						if(sp.getSpecimen_id()==specimen_id) {
							ihcReport.put("timepoint", sp.getBaseline_number());
							break;
						}
					}
					
					ihcReport.put("daysTaken", days==0?"":days);
					ihcReport.put("reportIssued", reportLD.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
					
					reportIHC.add(ihcReport);
				}

				// Add them all together
				treatmentHistory.put("history", treatments);
				treatmentHistory.put("bloodSamples", bloodSamples);
				treatmentHistory.put("tumourSamples", tumourSamples);
				treatmentHistory.put("reportGDL", reportGDL);
				treatmentHistory.put("reportCEP", reportCEP);
				treatmentHistory.put("reportIHC", reportIHC);

				// Return JSON for patient treatment
				String json = new Gson().toJson(treatmentHistory);
				return json;

			} catch (Exception e) {
				e.printStackTrace();
				log.log(Level.SEVERE, "generalInfo error: ", e);
				return "Failed to get general info.";
			}
		}
	}
}
