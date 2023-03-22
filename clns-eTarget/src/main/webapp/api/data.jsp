<%--
  #%L
  eTarget Maven Webapp
  %%
  Copyright (C) 2017 - 2021 digital ECMT
  %%
  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:
  
  The above copyright notice and this permission notice shall be included in
  all copies or substantial portions of the Software.
  
  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  THE SOFTWARE.
  #L%
  --%>
<%-- General imports --%>
<%@ page import="java.util.*" %>
<%@ page import="java.net.*" %>
<%@ page import="java.io.*" %>
<%@ page import="com.google.gson.*" %>
<%@ page import="java.util.logging.Logger" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%-- API imports --%>
<%@ page import="org.digitalecmt.etarget.API" %>
<%@ page import="org.digitalecmt.etarget.GetAllPatients" %>
<%@ page import="org.digitalecmt.etarget.GetSinglePatient" %>
<%@ page import="org.digitalecmt.etarget.CTDNAExploratory" %>
<%@ page import="org.digitalecmt.etarget.CTDNANGSSubset" %>
<%@ page import="org.digitalecmt.etarget.GeneralInfo" %>
<%@ page import="org.digitalecmt.etarget.GetMeetingOutcome" %>
<%@ page import="org.digitalecmt.etarget.PostMeetingOutcome" %>
<%@ page import="org.digitalecmt.etarget.GetMutationSelection" %>
<%@ page import="org.digitalecmt.etarget.PDXCDX" %>
<%@ page import="org.digitalecmt.etarget.TumourNGS" %>
<%@ page import="org.digitalecmt.etarget.SystemStatus" %>
<%@ page import="org.digitalecmt.etarget.Admin" %>
<%@ page import="org.digitalecmt.etarget.MutationSelection" %>
<%@ page import="org.digitalecmt.etarget.UnlockEdit" %>
<%@ page import="org.digitalecmt.etarget.IsLocked" %>
<%@ page import="org.digitalecmt.etarget.GetAllGenes" %>
<%@ page import="org.digitalecmt.etarget.PostGenePanel" %>

<%-- Set the content type header with the JSP directive --%>
<%@ page contentType="application/json" %>

<%
Logger log = Logger.getLogger("data.jsp");
// Get the current logged in user's data from Azure headers
String loggedInUserID = request.getHeader("x-ms-client-principal-name");
// Get any vars sent
String endpoint = request.getParameter("endpoint");
String data = request.getParameter("data");
String personID = request.getParameter("personID");
// General vars
String requestedData = "";

switch(endpoint) {
  case "currentuser":
    // Send the login for the current user
    if(loggedInUserID==null || loggedInUserID.length()==0){
    	loggedInUserID="did not get user id";
    }
    Map<String,String> currentUser = new HashMap<String,String>();
    currentUser.put("userID", loggedInUserID);
    requestedData = new Gson().toJson(currentUser);
    break;

  case "patients":
    // Get the patients list
    GetAllPatients patients = new GetAllPatients(loggedInUserID);
    requestedData = patients.processRequest();
    break;

  case "patient":
    // Get a single patient's data
    GetSinglePatient patient = new GetSinglePatient(loggedInUserID, personID);
    requestedData = patient.processRequest();
    break;

  case "ctdnaexploratory":
    // Get a single patient's data
    CTDNAExploratory ctdnaexploratory = new CTDNAExploratory(loggedInUserID, personID);
    requestedData = ctdnaexploratory.processRequest();
    break;

  case "ctdnangssubset":
    // Get a single patient's data
    CTDNANGSSubset ctdnangssubset = new CTDNANGSSubset(loggedInUserID, personID);
    requestedData = ctdnangssubset.processRequest();
    break;

  case "generalinfo":
    // Get a patient's general treatment data
    GeneralInfo generalinfo = new GeneralInfo(loggedInUserID, personID);
    requestedData = generalinfo.processRequest();
    break;

  case "getmeetingoutcome":
    // Get a patient's list of MTB meeting outcomes
    GetMeetingOutcome getmeetingoutcome = new GetMeetingOutcome(loggedInUserID, personID);
    requestedData = getmeetingoutcome.processRequest();
    break;

  case "postmeetingoutcome":
    // Post a patient's MTB meeting outcome update
    String meetingDate = request.getParameter("meetingDate");
    String outcome = request.getParameter("outcome");
    String notes = request.getParameter("notes");
    String ctDNA = request.getParameter("ctDNA");
    String tumourNGS = request.getParameter("tumourNGS");
    String fmTumour = request.getParameter("fmTumour");
    String fmBlood = request.getParameter("fmBlood");
    String genericGenomic = request.getParameter("genericGenomic");
    if(fmTumour==null) fmTumour="{}";
    if(fmBlood==null) fmBlood="{}";
    PostMeetingOutcome postmeetingoutcome = new PostMeetingOutcome(loggedInUserID,loggedInUserID, personID, meetingDate,outcome,notes,ctDNA,tumourNGS,fmBlood,fmTumour, genericGenomic);
    		//new PostMeetingOutcome(loggedInUserID, loggedInUserID, personID, meetingDate, outcome, notes, ctDNA, tumourNGS, fmBlood, fmTumour);
    requestedData = postmeetingoutcome.processRequest();
    if(requestedData.contains("\"success\":\"false\"")){
    	response.setStatus(423);
    }
    break;
    
  case "postmeetingoutcomeedit":
	    // Post a patient's MTB meeting outcome update
	    String meetingId = request.getParameter("meetingId");
	    String typeMO = request.getParameter("type");
	    String value = request.getParameter("value");
	    PostMeetingOutcome postmeetingoutcomeedit = new PostMeetingOutcome();
	    requestedData = postmeetingoutcomeedit.editRequest(loggedInUserID, meetingId, typeMO, value);
	    if(requestedData.contains("\"success\":\"false\"")){
	    	response.setStatus(423);
	    }
	    break;

  case "getmutationselection":
    // Get a patient's unsaved mutation selections
    GetMutationSelection getmutationselection = new GetMutationSelection(loggedInUserID, personID);
    requestedData = getmutationselection.processRequest();
    break;
    
  case "getmutationselectionlatest":
	    // Get a patient's unsaved mutation selections
	    GetMutationSelection getmutationselectionlatest = new GetMutationSelection(loggedInUserID, personID);
	    requestedData = getmutationselectionlatest.getLatest();
	    break;
    
  case "addmutationselection":
	String geneVarientID = request.getParameter("geneVarientId"); 
	String type = request.getParameter("ngsOrctdna");
	MutationSelection mutationselection = new MutationSelection(loggedInUserID, personID, geneVarientID, type);
	requestedData = mutationselection.addData();
    if(requestedData.contains("\"success\":\"false\"")){
    	response.setStatus(423);
    }
	break;
	
  case "deletemutationselection":
	String geneVarient = request.getParameter("geneVarientId"); 
	String ngsOrctdna = request.getParameter("ngsOrctdna");
	MutationSelection mutationsel = new MutationSelection(loggedInUserID, personID, geneVarient, ngsOrctdna);
	requestedData = mutationsel.removeData();  
    if(requestedData.contains("\"success\":\"false\"")){
    	response.setStatus(423);
    }
	break;

  case "pdxcdx":
    // Get a patient's PDXCDX data
    PDXCDX pdxcdx = new PDXCDX(loggedInUserID, personID);
    requestedData = pdxcdx.processRequest();
    break;

  case "tumourngs":
    // Get the patient's tumour results
    TumourNGS tumourngs = new TumourNGS(loggedInUserID, personID);
    requestedData = tumourngs.processRequest();
    break;

  case "systemstatus":
    // Get the system status
    SystemStatus systemstatus = new SystemStatus(loggedInUserID);
    requestedData = systemstatus.processRequest();
    break;

  case "listadminusers":
    // Get a list of admin users
    Admin adminList = new Admin(loggedInUserID);
    requestedData = adminList.listUsers();
    break;

  case "adduser":
    // Add a new user
    String email = request.getParameter("email");
    String roleID = request.getParameter("roleID");
    Admin adminAdd = new Admin(loggedInUserID);
    requestedData = adminAdd.addUser(email, roleID);
    break;

  case "updateuser":
    // Update a user
    String whitelistID2 = request.getParameter("whitelistID");
    String roleID2 = request.getParameter("roleID");
    Admin adminUpdate = new Admin(loggedInUserID);
    requestedData = adminUpdate.updateUser(whitelistID2, roleID2);
    break;
    
  case "unlockedit":
	  new UnlockEdit(loggedInUserID);
	  break;
	  
  case "islocked":
	  IsLocked isLocked = new IsLocked(loggedInUserID, personID);
	  requestedData = isLocked.processRequest();
	  break;
	 
  case "printmeetingoutcome":
	  String meetId = request.getParameter("meetingId");
	  PostMeetingOutcome printMeetingOutcome = new PostMeetingOutcome();
	  requestedData = printMeetingOutcome.printMeetingOutcome(loggedInUserID, meetId);
	  if(requestedData.contains("note")){
	  	response.setStatus(204);
	  }
	  break;
	  
  case "deletereport":
	  String specimen_id = request.getParameter("specimen_id");
	  String run_number = request.getParameter("run");
	  log.info("deletereport called " + specimen_id + " " + run_number);
	  requestedData = "dont do anything yet";
	  break;
	  
  case "allgenes":
	  GetAllGenes allGenes = new GetAllGenes(loggedInUserID);
	  requestedData = allGenes.getAllGenes();
	  break;
	  
  case "allgenespanel":
	  GetAllGenes allGenes2 = new GetAllGenes(loggedInUserID);
	  requestedData = allGenes2.getAllPanelGenes();
	  break;

  case "postgenepanel":
	  String name = request.getParameter("genePanelName");
	  String genes = request.getParameter("genePanelGenes");
	  String date = request.getParameter("date");
	  SimpleDateFormat sdf_in = new SimpleDateFormat("dd/MM/yyyy");
	  SimpleDateFormat sdf_out = new SimpleDateFormat("yyyy-MM-dd");
	  String out_date = sdf_out.format(sdf_in.parse(date));
	  PostGenePanel postGenePanel =  new PostGenePanel(loggedInUserID, name, genes, out_date);
	  requestedData = postGenePanel.processRequest();
	  break;
	  
  default:
    requestedData = "Invalid endpoint";
}

// Send the data
if(requestedData.contains("\"success\":\"false\"") && (response.getStatus()!=423 && response.getStatus()!=204)){
	response.setStatus(500);
}
response.setContentType("application/json");
response.setHeader("Content-Disposition", "inline");
response.setCharacterEncoding("UTF-8");
response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
response.setHeader("Expires", "0"); // Proxies.
log.info("before writing of data");
response.getWriter().write(requestedData);
log.info("after writing of data");
%>
