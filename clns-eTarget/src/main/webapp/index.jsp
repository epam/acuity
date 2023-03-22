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

<%
	// Logout issue fix.
	// Check there is a username present. Otherwise reload the page.
	String loggedInUserID = request.getHeader("x-ms-client-principal-name");
	if (loggedInUserID==null || loggedInUserID.equals("")) {
		response.sendRedirect("/");
	}
%>
<%@ page language="java" import="java.util.*"%>
<%@ page import="java.util.ResourceBundle"%>
<%
	ResourceBundle resource = ResourceBundle.getBundle("application");
	String version = resource.getString("application.version");
	String appTitle = resource.getString("application.title");
	String  imageURL2 = resource.getString("application.imageURL");
	String support = resource.getString("application.support");
	String trialfinderURL = resource.getString("application.trailfinderURL");
	String[] names=resource.getString("page.name").split(",");
	String tabNames[] = new String[names.length];
	String tabBlood[] = new String[names.length];
	String tabTumour[] = new String[names.length];
	String tabId[] = new String[names.length];
	String ihc = resource.getString("page.ihc");
	String fmblood = resource.getString("page.fmblood");
	String fmtumour = resource.getString("page.fmtumour");
	String tumourngs = resource.getString("page.tumourngs");
	String ctdnangs = resource.getString("page.ctdnangs");
	String ctdnaexploratory = resource.getString("page.ctdnaexploratory");
	String pdxcdx=resource.getString("page.pdxcdx");
	for(int i=0;i<names.length;i++){
		tabId[i]=names[i].trim();
		tabNames[i]=resource.getString("page.name.".concat(names[i].trim()));
		tabBlood[i]=resource.getString("page.name.".concat(names[i].trim()).concat(".blood"));
		tabTumour[i]=resource.getString("page.name.".concat(names[i].trim()).concat(".tumour"));
	}
	
	request.setAttribute("tabName", tabNames);
	request.setAttribute("tabId", tabId);
	request.setAttribute("tabBlood", tabBlood);
	request.setAttribute("tabTumour", tabTumour);
	request.setAttribute("imageURL", resource.getString("application.imageURL").split(","));
	request.setAttribute("imageAlt", resource.getString("application.imageAlt").split(","));
	request.setAttribute("additionalreports", resource.getString("page.additional_reports"));
	request.setAttribute("ihc", resource.getString("page.ihc"));
	request.setAttribute("fmblood", resource.getString("page.fmblood"));
	request.setAttribute("fmtumour", resource.getString("page.fmtumour"));
	request.setAttribute("tumourngs", resource.getString("page.tumourngs"));
	request.setAttribute("ctdnangs", resource.getString("page.ctdnangs"));
	request.setAttribute("ctdnaexploratory", resource.getString("page.ctdnaexploratory"));
	request.setAttribute("pdxcdx", resource.getString("page.pdxcdx"));
	request.setAttribute("blood", resource.getString("data.blood"));
	request.setAttribute("tumour", resource.getString("data.tumour"));
%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html ng-app="targetapp" lang="en">
<head>
<!-- Metadata -->
<title><%=appTitle%> <%=version%></title>
<link rel="apple-touch-icon" sizes="57x57" href="./apple-icon-57x57.png">
<link rel="apple-touch-icon" sizes="60x60" href="./apple-icon-60x60.png">
<link rel="apple-touch-icon" sizes="72x72" href="./apple-icon-72x72.png">
<link rel="apple-touch-icon" sizes="76x76" href="./apple-icon-76x76.png">
<link rel="apple-touch-icon" sizes="114x114" href="./apple-icon-114x114.png">
<link rel="apple-touch-icon" sizes="120x120" href="./apple-icon-120x120.png">
<link rel="apple-touch-icon" sizes="144x144" href="./apple-icon-144x144.png">
<link rel="apple-touch-icon" sizes="152x152" href="./apple-icon-152x152.png">
<link rel="apple-touch-icon" sizes="180x180" href="./apple-icon-180x180.png">
<link rel="icon" type="image/png" sizes="192x192"  href="./android-icon-192x192.png">
<link rel="icon" type="image/png" sizes="32x32" href="./favicon-32x32.png">
<link rel="icon" type="image/png" sizes="96x96" href="./favicon-96x96.png">
<link rel="icon" type="image/png" sizes="16x16" href="./favicon-16x16.png">
<link rel="manifest" href="./manifest.json">
<meta name="msapplication-TileColor" content="#ffffff">
<meta name="msapplication-TileImage" content="./ms-icon-144x144.png">
<meta name="theme-color" content="#ffffff">
<meta http-equiv="Cache-Control"
	content="no-cache, no-store, must-revalidate" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="0" />
<meta charset="utf-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">

<!-- CSS -->
<link href="https://fonts.googleapis.com/css?family=Josefin+Sans:300"
	rel="stylesheet" />
<!-- <link rel="stylesheet" type="text/css" href="css/datatables.min.css" /> -->
<link rel="stylesheet" type="text/css"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" />
<link rel="stylesheet" type="text/css" href="css/pikaday.css" />
<link rel="stylesheet" type="text/css" href="css/style.css" />
<link rel="stylesheet" media="(max-width: 800px)" href="css/mobile.css" />
<link rel="stylesheet" type="text/css"
	href="css/ui-bootstrap-custom-2.5.0-csp.css" />
<!-- JS -->
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/es5-shim/4.4.1/es5-shim.js"></script>
<!-- Shim for IE -->
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/es6-shim/0.34.1/es6-shim.js"></script>
<!-- Shim for IE -->
<script src="https://use.typekit.net/cym5hby.js"></script>
<script>try {
		Typekit.load({
			async : true
		});
	} catch (e) {}
</script>
<script src="js/angular.min.js"></script>
<!--  <script src="js/datatables.min.js"></script>-->
<script src="js/app.js"></script>
<script defer src="js/moment.min.js"></script>
<script defer src="js/pikaday.js"></script>
<script defer src="js/notice.js"></script>
<script defer src="js/inactivity.js"></script>
<script defer src="js/ui-bootstrap-custom-tpls-2.5.0.min.js"></script>
<script>
	var picker;
	jQuery(document).ready(function() {
		picker = new Pikaday({
			field : jQuery('#datepicker')[0],
			format : 'DD/MM/YYYY',
			defaultDate : moment().toDate(),
			setDefaultDate : true
		});
	});
</script>
</head>
<body ng-controller="targetController">
	<!-- Top navigation -->
	<nav id="target-top-navigation">
		<span id="list-link" ng-click="showPage('list')">View All
			Patients</span> <span id="search-link" ng-click="showPage('search')">Search</span>
		<span id="list-link" ng-click="showPage('status')">Report
			Extraction Status</span> <span
			ng-include="'rest/component/adminlink'"></span>
		<!--<span id="loggedin-user">Logged in as {{currentUser.userID}} (<a href="/.auth/logout">Logout</a>)</span>-->
		<!--<span id="loggedin-user">Logged in as {{currentUser.userID}} (<a href="https://login.windows.net/common/oauth2/logout?post_logout_redirect_uri=https://decmttarget.azurewebsites.net">Logout</a>)</span>-->
		<span id="loggedin-user">Logged in as {{currentUser.userID}} 
		(<a	href="./logout.jsp" ng-click="unlockEdit()">Logout</a>)
		</span> <span id="header-title">eTARGET</span>
	</nav>

	<!-- Patients table -->
	<div class="page" ng-show="page.list" id="patient-list">
		<h1>All Patients</h1>
		<div ng-include="'rest/component/patienttable'"
			onload="setConfig('<%=ihc%>','<%=tumourngs%>','<%=fmblood%>','<%=fmtumour%>','<%=ctdnangs%>','<%=ctdnaexploratory%>','<%=pdxcdx%>'); getAllPatients()"></div>
	</div>

	<!-- Search page -->
	<div class="page" ng-show="page.search" id="search">
		<h1>Search</h1>
		<c:if test = "${pdxcdx =='true'}">
		<div ng-include="'rest/component/patienttable_search'"
			onload="getAllGenesSearch(); getAllConditionsSearch(); getAllPatientsSearch(); getPDXCDXPatients();">
		</div>
		</c:if>
		<c:if test = "${!pdxcdx =='true'}">
		<div ng-include="'rest/component/patienttable_search'"
			onload="getAllGenesSearch(); getAllConditionsSearch(); getAllPatientsSearch(); getPDXCDXPatients(); removePDX()">
		</div>
		</c:if>
	</div>

	<!-- Patient details -->
	<div class="page" ng-show="page.detail">
		<!-- Top section -->
		<div id="patient-details">
			<span id="patient-id">Patient ID: {{currentPatient.id}}</span> | <span
				id="patient-disease">Disease Type: {{currentPatient.disease}}</span>
			| <span id="patient-age">Age at Consent:
				{{currentPatient.age}}</span> | <span id="patient-gender">Gender:
				{{currentPatient.gender}}</span> | <span id="care-site">Site: {{currentPatient.site}}</span>
			<span id="patient-navigation">
				<i ng-show="!first()" ng-click="navigatePrev()" class="fa fa-chevron-circle-left"></i>
				<i ng-show="!last()" ng-click="navigateNext()" class="fa fa-chevron-circle-right"></i>
			</span>
		</div>

		<!-- Sidebar -->
		<div id="detail-sidebar">
			<ul>
				<li ng-click="showDetailSection('general')"
					id="detail-sidebar-general" class="detail-sidebar-selected">General
					Information</li>
					
				<c:forEach var="tag" items="${tabName}" varStatus="loop">
					<c:if test = "${tabBlood[loop.index] =='true'}">
						<li ng-click="showDetailSection('${tabId[loop.index]}_blood')"
							id="detail-sidebar-${tabId[loop.index]}_blood" class="generic-genomics-data blood">${tag} Blood</li>
					</c:if>
					<c:if test = "${tabTumour[loop.index] =='true'}">
						<li ng-click="showDetailSection('${tabId[loop.index]}_tumour')"
							id="detail-sidebar-${tabId[loop.index]}_tumour" class="generic-genomics-data tumour">${tag} Tumour</li>
					</c:if>
				</c:forEach>
					
				<c:if test = "${tumourngs =='true'}">	
					<li ng-click="showDetailSection('tumourNGS')"
						id="detail-sidebar-tumourNGS">Tumour NGS</li>
				</c:if>
				<c:if test = "${fmtumour =='true'}">
					<li ng-click="showDetailSection('foundationTumour')"
						id="detail-sidebar-foundationTumour">Foundation Tumour</li>
				</c:if>
				<c:if test = "${ctdnangs =='true'}">
					<li ng-click="showDetailSection('ctdnaNGS')"
						id="detail-sidebar-ctdnaNGS">ctDNA NGS Subset</li>
				</c:if>
				<c:if test = "${ctdnaexploratory =='true'}">
					<li ng-click="showDetailSection('ctdnaExploratory')"
						id="detail-sidebar-ctdnaExploratory">ctDNA Exploratory Subset</li>
				</c:if>
				<c:if test = "${fmblood =='true'}">
				    <li ng-click="showDetailSection('foundationBlood')"
						id="detail-sidebar-foundationBlood">Foundation Blood</li>
				</c:if>
				<c:if test = "${pdxcdx =='true'}">
					<li ng-click="showDetailSection('pdxcdx')"
						id="detail-sidebar-pdxcdx">PDX/CDX</li>
				</c:if>
				<c:if test = "${ihc =='true'}">
					<li ng-click="showDetailSection('ihc')"
						id="detail-sidebar-ihc">IHC</li>
				</c:if>
				<c:if test = "${additionalreports =='true'}">
  				<li ng-click="showDetailSection('addReports')"
					id="detail-sidebar-addReports">Additional Reports</li>
				</c:if>
				
				<li ng-click="showDetailSection('meeting')"
					id="detail-sidebar-meeting">Meeting Outcome</li>
			</ul>
		</div>

		<!-- Content section -->
		<div id="detail-content">
			<!-- General Patient Info start -->
			<div ng-show="detailSection.general">
				<h1>General Information</h1>

				<div id="general-info">Date of Diagnosis:
					{{currentPatient.dateDiagnosis}}. Date of Consent:
					{{currentPatient.dateConsent}}.</div>

				<h3>Treatment History</h3>
				<table >
					<tr>
						<th>Treatment</th>
						<th>Treatment Start Date</th>
						<th>Treatment End Date</th>
					</tr>

					<tr ng-repeat="treatment in currentPatient.treatments.history">
						<td width="30%">{{treatment.procedureName}}</td>
						<td width="15%">{{treatment.procedureStartDate}}</td>
						<td width="55%">{{treatment.procedureEndDate}}</td>
					</tr>
				</table>
				<div ng-if="currentPatient.treatments.history.length == 0">
					No treatment has been recorded.</div>

				<h3>Sample Details</h3>

				<c:if test = "${blood =='true'}">
				<h4>ctDNA</h4>
				<table >
					<tr>
						<th width="15%">Timepoint</th>
						<th width="85%">Date of Sample</th>
					</tr>

					<tr ng-repeat="sample in currentPatient.treatments.bloodSamples">
						<td width="15%">T{{sample.baseline_number}}</td>
						<td width="85%">{{sample.dateBlood}}</td>
					</tr>
				</table>

				<div ng-if="currentPatient.treatments.bloodSamples.length == 0">
					No blood samples found.</div>
				</c:if>
				<c:if test = "${tumour =='true'}">
				<h4>Tumour Biopsy</h4>
				<table >
					<tr>
						<th>Timepoint</th>
						<th>Date of Sample</th>
						<th>Type of Specimen</th>
						<th>Nature of Specimen</th>
					</tr>

					<tr ng-repeat="sample in currentPatient.treatments.tumourSamples">
						<td>{{sample.timepoint}}</td>
						<td>{{sample.dateTumour}}</td>
						<td>{{sample.specimenName}}</td>
						<td>{{sample.specimenNature}}</td>
					</tr>
				</table>

				<div ng-if="currentPatient.treatments.tumourSamples.length == 0">
					No tumour samples found.</div>
				</c:if>
			<c:if test = "${ctdnangs =='true' || ctdnaexploratory=='true'}">
				<h3>Report Details</h3>
				<c:if test = "${ctdnangs =='true' || ctdnaexploratory=='true'}">
				<h4>CEP</h4>
				<table >
					<tr>
						<th>Timepoint</th>
						<th>Run</th>
						<th>Date Report Issued</th>
						<th class="tooltip">Days to complete <i
							class="fa fa-info-circle" aria-hidden="true"></i> <span
							class="tooltiptext">Number of working days between date of
								sample and date report issued inclusive</span></th>
						<th></th>
					</tr>
					<tr ng-repeat="activity in currentPatient.treatments.reportCEP">
						<td>T{{activity.baseline_number}}</td>
						<td>Run {{activity.run_number}}</td>
						<td>{{activity.reportIssued}}</td>
						<td>{{activity.daysTaken}}</td>
						<td ng-click="deleteReport(activity.specimen_id,activity.run_number)"
						    ng-show="activity.deletable==true"
							class="pointer tooltip">
							<div class="relative">
							<i class="fa fa-trash-o"></i>
							<span class="tooltiptext tooltiptext2">Deletes this report</span></div></td>
					</tr>
				</table>

				<div ng-if="currentPatient.treatments.reportCEP.length == 0">
					No CEP reports found.</div>
				</c:if>
				<c:if test = "${ctdnangs =='true'}">
				<h4>GDL</h4>
				<table >
					<tr>
						<th>Timepoint</th>
						<th>Run</th>
						<th>Date Report Issued</th>
						<th class="tooltip">Days to complete <i
							class="fa fa-info-circle" aria-hidden="true"></i> <span
							class="tooltiptext">Number of working days between date of
								GDL request and date report issued inclusive</span></th>
						<th></th>
					</tr>
					<tr ng-repeat="activity in currentPatient.treatments.reportGDL">
						<td>Bx{{activity.baseline_number}}</td>
						<td>Run {{activity.run_number}}</td>
						<td>{{activity.reportIssued}}</td>
						<td>{{activity.daysTaken}}</td>
						<td ng-click="deleteReport(activity.specimen_id,activity.run_number)"
						    ng-show="activity.deletable==true"
							class="pointer tooltip">
							<div class="relative">
							<i class="fa fa-trash-o"></i>
							<span class="tooltiptext tooltiptext2">Deletes this report</span></div></td>
					</tr>
				</table>

				<div ng-if="currentPatient.treatments.reportGDL.length == 0">
					No GDL reports found.</div>
				</c:if>
				<c:if test = "${ihc =='true'}">
				<h4>IHC</h4>
				<table>
					<tr>
						<th>Timepoint</th>
						<th>Date Report Issued</th>
						<th class="tooltip">Days to complete <i
							class="fa fa-info-circle" aria-hidden="true"></i> <span
							class="tooltiptext">Number of working days between date of
								sample received and date report issued inclusive</span></th>
					</tr>
					<tr ng-repeat="ihc in currentPatient.treatments.reportIHC">
						<td>Bx{{ihc.timepoint}}</td>
						<td>{{ihc.reportIssued}}</td>
						<td>{{ihc.daysTaken}}</td>
					</tr>
				</table>
				<div ng-if="currentPatient.treatments.reportIHC.length == 0">
					No IHC reports found.</div>
				</c:if>
			  </c:if>
			</div>

			<!-- Tumour NGS start -->
			<c:if test = "${tumourngs =='true'}">
			<div ng-show="detailSection.tumourNGS">
				<p id="editLockMessage" ng-show="currentPatient.editLockMessage!= ''">
					{{currentPatient.editLockMessage}}&emsp;<i class="fa fa-refresh pointer" aria-hidden="true" ng-click="checkLockStatus()"></i>
				</p>
				<h1>Tumour NGS</h1>
				<nav class="detail-section">
					<a href="#"
						ng-repeat="(key,value) in currentPatient.tumourNGS.baseline"
						ng-class="(detailSection.tumourSubs[key]) ? 'baseline-active': 'none'"
						ng-click="showTumourBaseline(key)">Bx{{key}}</a>
				</nav>
				<!-- Baselines -->
				<div ng-show="!currentPatient.tumourNGS.baseline.hasOwnProperty('1')" >
					<p>No tumour samples found.</p>
				</div>
				<div ng-repeat="(keyBase, valueBase) in currentPatient.tumourNGS.baseline"	
				     ng-show="detailSection.tumourSubs[keyBase]"
				     ng-init="baselineCurr = $index">
					<p>Date of Sample: {{valueBase.specimen.specimenDate}}</p>
					<p ng-show="valueBase.runs['1']['1'].gdlRequest != ''">Date of
							GDL Request: {{valueBase.runs['1']['1'].gdlRequest}}</p>
					<p ng-show="valueBase.runs['1']['1'].noResults!=''">{{valueBase.runs['1']['1'].noResults}}</p>
					<p ng-if="valueBase.runs['1']['1'].noResultsReport">
						<i class="fa fa-file-pdf-o" aria-hidden="true"></i> Original
						report: <a target="_blank"
						ng-href="rest/pdf/{{valueBase.runs['1']['1'].pdfReport}}">{{valueBase.runs['1']['1'].pdfReport}}</a>
					</p>
					<!-- Runs -->
					<div ng-repeat="(key, versions) in valueBase.runs" ng-show="valueBase.runs['1']['1'].geneSubset!=null">
					  	<h2>Run {{key}}</h2>
						<uib-tabset active="active_tumour[keyBase][key]">
						<div ng-repeat="(version, value) in versions">
						<uib-tab index="version" heading="Version {{version}}" class="tab_header{{versions | objLength}}">
						<div ng-class="{'greybackground' : (versions | objLength)>1 && ''+(versions | objLength)!=version}">
						<div>
							<!-- TODO how is base1results set? -->
							<div ng-show="detailSection.tumourSubs[1]">
								<table 
								ng-if="!value.analysisFailed && !value.noMutationsFound">
									<tr>
										<th>Significant Mutations</th>
										<th>Gene Name</th>
										<th id="result_column">Result</th>
										<th>Mutant Allele Frequency (%)</th>
									</tr>
	
									<tr
										ng-repeat="gene in value.geneSubset">
										<td>
											<div ng-include="loadComponent('tumourcheckbox')"></div>
											<div ng-include="loadComponent('tumourtick')"></div>
										</td>
										<td><em>{{gene.geneName}}</em></td>
										<td
											ng-bind-html="trustAsHtml(gene.result)"></td>
										<td>{{gene.mutantAllele}}</td>
									</tr>
								</table>

								<table 
									ng-if="value.analysisFailed || value.noMutationsFound">
									<tr>
										<th>Significant Mutations</th>
										<th>Gene Name</th>
										<th id="result_column">Result</th>
										<th>Mutant Allele Frequency (%)</th>
									</tr>
		
									<tr>
										<td ng-if="value.analysisFailed">Analysis failed</td>
										<td ng-if="value.noMutationsFound">Nil</td>
										<td></td>
										<td></td>
										<td></td>
									</tr>
								</table>

								<table >
									<tr>
										<th class="tumour_overview_c1">&nbsp;</th>
										<th>&nbsp;</th>
									</tr>
									<tr>
										<td>Coverage</td>
										<td>{{value.coverage}}</td>
									</tr>
									<tr>
										<td>Path Lab Ref No</td>
										<td>{{value.pathLabRef}}</td>
									</tr>
								</table>

								<p ng-show="value.unknownSignificance!='n/a' && value.unknownSignificance!=''">Variants of unknown significance: {{value.unknownSignificance}}.</p>

								<p ng-show="value.pdfReport != ''">
									<i class="fa fa-file-pdf-o" aria-hidden="true"></i> Original
									report: <a target="_blank"
										ng-href="rest/pdf/{{value.pdfReport}}">{{value.pdfReport}}</a>
								</p>
								<p ng-show="value.hasOwnProperty('pdfReportOther')">
									<i class="fa fa-file-pdf-o" aria-hidden="true"></i> New core panel report: 
									<a target="_blank"	ng-href="rest/pdf/{{value.pdfReportOther}}">{{value.pdfReportOther}}</a>
								</p>
							</div>
						</div>
					</div>
					</uib-tab>
				</div>
				</uib-tabset>
			</div>
			</div>
			</div>
			</c:if>
			
			<!--  Generic Genomics start -->
			<c:forEach var="tag" items="${tabName}" varStatus="loop">
			 	<c:if test = "${tabBlood[loop.index] =='true'}">
					<div ng-show="detailSection.${tabId[loop.index]}_blood">
					<h1>${tag} Blood</h1>
					<nav class="detail-section">
					<a href="#"
						ng-repeat="(key,value) in currentPatient.${tabId[loop.index]}_blood.baselineList | orderBy:'date'"
						ng-show="value.baseline<2 || currentPatient.${tabId[loop.index]}_blood.baseline[value.baseline][1].hasOwnProperty('panel_id') || currentPatient.${tabId[loop.index]}_blood.baseline[value.baseline]['1'].hasOwnProperty('report') 
						|| currentPatient.${tabId[loop.index]}_blood.baseline[value.baseline]['1'].hasOwnProperty('versions')"
						ng-class="(detailSection.${tabId[loop.index]}_bloodSubs[value.baseline]) ? 'baseline-active': 'none'"
						ng-click="showBaseline('${tabId[loop.index]}_blood', value.baseline)">T{{value.baseline}}</a>
					</nav>
					<!-- Baselines -->
					<div ng-show="!currentPatient.${tabId[loop.index]}_blood.baseline.hasOwnProperty('1')" >
						<p>No blood samples found.</p>
					</div>
					<div ng-repeat="(keyBase, valueBase) in currentPatient.${tabId[loop.index]}_blood.baseline"	
					     ng-show="detailSection.${tabId[loop.index]}_bloodSubs[keyBase]"
					     ng-init="baselineCurr = $index">
						<p>Date of Sample: {{valueBase.specimen.specimenDate}}</p>
						<p ng-show="!valueBase['1'].hasOwnProperty('panel_id')">Results not available.</p>
						<uib-tabset active="active_${tabId[loop.index]}_blood[1][keyBase]" ng-show="valueBase['1'].hasOwnProperty('panel_id')">
						<!-- <div ng-repeat="(version, value) in valueBase" 
						     ng-show="version!='specimen' && value.hasOwnProperty('tmb_status')">-->
							<uib-tab data-ng-repeat="(version, value) in valueBase" 
							         ng-show="version!='specimen' && value.hasOwnProperty('panel_id')" 
							         index="version" heading="Version {{version}}" class="tab_header{{(valueBase | objLength)-1}}">
							<div ng-class="{'greybackground' : (valueBase | objLength)>2 && ''+((valueBase | objLength)-1)!=version}">
						
						<!-- <div ng-repeat="(key, run) in valueBase" 
						     ng-show="key!='specimen' && run.hasOwnProperty('tmb_status')">
							<h2 ng-show="valueBase.hasOwnProperty('2')">Run {{key}}</h2> -->
							<p>TMB: <span>{{value.tmb_status}}<span ng-show="value.tmb_score!=''">; </span>{{value.tmb_score}} {{value.tmb_unit}}</span></p>
							<p>Microsatellite Status: <span ng-if="value.microsatellite_status!='MSS'">{{value.microsatellite_status}}</span><span ng-if="value.microsatellite_status=='MSS'">Cannot Be Determined</span></p>
							<p>Tumour Fraction Score: <span>{{value.tumour_fraction_score}} {{value.tumour_fraction_unit}}</span>
							<!-- <p>Percentage with 100x read depth: <span>{{value.percent_exons_100x}}</span></p>-->
							<div class="fm_headline">
								<h3>Genomic Alterations Identified</h3>
								<h4>Short Variants</h4>
								<table>
										<tr>
											<th>Significant Mutations</th>
											<th>Gene Name</th>
											<th id="result_column">Result</th>
											<th>Variant Allele Frequency (%)</th>
											<th>Read Depth</th>
											<th>Functional Effect</th>
											<th>Position</th>
											<th>Transcript</th>
											<th>Status</th>
										</tr>
				
										<tr
											ng-repeat="gene in value.short_variants | filterterm:'is_significant':'true' | orderBy: 'geneName'">
											<td>
												<div ng-include="loadComponent('genericgenomiccheckbox','${tabId[loop.index]}_blood')"></div>
												<div ng-include="loadComponent('genericgenomictick','${tabId[loop.index]}_blood')"></div>
											</td>
											<td><em>{{gene.geneName}}</em></td>
											<td
												ng-bind-html="trustAsHtml(gene.result)"></td>
											<td>{{gene.variant_allele_frequency}}</td>
											<td>{{gene.read_depth}}</td>
											<td>{{gene.functional_effect}}</td>
											<td>{{gene.position}}
											<td ng-if="(gene.transcript).startsWith('NM')==true"><a target="_blank" ng-href="https://www.ncbi.nlm.nih.gov/nuccore/{{gene.transcript}}">
											{{gene.transcript}}</a></td>
											<td ng-if="(gene.transcript).startsWith('NM')==false">{{gene.transcript}}</td>
											<td>{{gene.status}}</td>
										</tr>
										<tr	ng-if="(value.short_variants | filterterm:'is_significant':'true').length == 0">
											<td>Nil</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
										</tr>
								</table>
								<h4>Copy Number Alterations</h4>
								<table>
										<tr>
											<th>Significant</th>
											<th>Gene Name</th>
											<th>Copy Number</th>
											<th>Exons</th>
											<th>Ratio</th>
											<th>Type</th>
											<th>Equivocal</th>
											<th>Status</th>
										</tr>
				
										<tr
											ng-repeat="gene in value.copy_number_alterations | filterterm:'is_significant':'true' | orderBy: 'geneName'">
											<td>
												<div ng-include="loadComponent('genericgenomiccheckbox','${tabId[loop.index]}_blood')"></div>
												<div ng-include="loadComponent('genericgenomictick','${tabId[loop.index]}_blood')"></div>
											</td>
											<td><em>{{gene.geneName}}</em></td>
											<td>{{gene.copy_number}}</td>
											<td>{{gene.exons}}</td>
											<td>{{gene.ratio}}</td>
											<td>{{gene.type}}</td>
											<td>{{gene.equivocal}}</td>
											<td>{{gene.status}}</td>
										</tr>
										<tr ng-if="(value.copy_number_alterations | filterterm:'is_significant':'true').length == 0">
											<td>Nil</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
										</tr>
								</table>
								<h4>Rearrangements</h4>
								<table>
										<tr>
											<th>Significant</th>
											<th>Gene 1</th>
											<th>Gene 2</th>
											<th>Type</th>
											<th>Inframe</th>
											<th>Pos1</th>
											<th>Pos2</th>
											<th>Number of Reads</th>
											<th>Variant Allele Frequency (%)</th>
											<th>Description</th>
											<th>Status</th>
										</tr>
				
										<tr
											ng-repeat="gene in value.rearrangements | filterterm:'is_significant':'true' | orderBy: 'gene1'">
											<td>
												<div ng-include="loadComponent('genericgenomiccheckbox','${tabId[loop.index]}_blood')"></div>
												<div ng-include="loadComponent('genericgenomictick','${tabId[loop.index]}_blood')"></div>
											</td>
											<td><em>{{gene.gene1}}</em></td>
											<td><em>{{gene.gene2}}</em></td>
											<td>{{gene.rearrangement_type}}</td>
											<td>{{gene.inframe}}</td>
											<td>{{gene.pos1}}</td>
											<td>{{gene.pos2}}</td>
											<td>{{gene.number_of_reads}}</td>
											<td>{{gene.variant_allele_frequency}}</td>
											<td>{{gene.description}}</td>
											<td>{{gene.status}}</td>
										</tr>
										<tr ng-if="(value.rearrangements | filterterm:'is_significant':'true').length == 0">
											<td>Nil</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
										</tr>
								</table>
							</div>
							<div ng-show="(value.short_variants | filterterm:'is_significant':'false').length > 0||
							              (value.copy_number_alterations | filterterm:'is_significant':'false').length >0 || 
							              (value.rearrangements | filterterm:'is_significant':'false').length">
								<h3 class="pointer fm_headline" ng-click="showTumourVUS = !showTumourVUS">Variants of Unknown Significance 
								<span ng-show="!showTumourVUS">(Show)</span><span ng-show="showTumourVUS">(Hide)</span></h3>
								<div ng-show="showTumourVUS">
								<p>Note: One or more variants of unknown significance (VUS) were detected in this patient's tumour. 
								These variants may not have been adequately characterized in the scientific literature at the time this report was issued,
								and/or the genomic context of these alterations makes their significance unclear. 
								We choose to include them here in the event that they become clinically meaningful in the future.</p>
								<div ng-show="(value.short_variants | filterterm:'is_significant':'false').length">
								<h4>Short Variants</h4>
								<table>
										<tr>
											<th>Gene Name</th>
											<th id="result_column">Result</th>
											<th>Variant Allele Frequency (%)</th>
											<th>Read Depth</th>
											<th>Functional Effect</th>
											<th>Position</th>
											<th>Transcript</th>
											<th>Status</th>
										</tr>
				
										<tr
											ng-repeat="gene in value.short_variants | filterterm:'is_significant':'false' | orderBy: 'geneName'">
											<td><em>{{gene.geneName}}</em></td>
											<td
												ng-bind-html="trustAsHtml(gene.result)"></td>
											<td>{{gene.variant_allele_frequency}}</td>
											<td>{{gene.read_depth}}</td>
											<td>{{gene.functional_effect}}</td>
											<td>{{gene.position}}</td>
											<td ng-if="(gene.transcript).startsWith('NM')==true"><a target="_blank" ng-href="https://www.ncbi.nlm.nih.gov/nuccore/{{gene.transcript}}">
											{{gene.transcript}}</a></td>
											<td ng-if="(gene.transcript).startsWith('NM')==false">{{gene.transcript}}</td>
											<td>{{gene.status}}</td>
										</tr>
								</table>
								</div>
								<div ng-show="(value.copy_number_alterations | filterterm:'is_significant':'false').length">
								<h4>Copy Number Alterations</h4>
								<table>
										<tr>
											<th>Gene Name</th>
											<th>Copy Number</th>
											<th>Exons</th>
											<th>Ratio</th>
											<th>Type</th>
											<th>Equivocal</th>
											<th>Status</th>
										</tr>
				
										<tr
											ng-repeat="gene in value.copy_number_alterations | filterterm:'is_significant':'false' | orderBy:'geneName'">
											<td><em>{{gene.geneName}}</em></td>
											<td>{{gene.copy_number}}</td>
											<td>{{gene.exons}}</td>
											<td>{{gene.ratio}}</td>
											<td>{{gene.type}}</td>
											<td>{{gene.equivocal}}</td>
											<td>{{gene.status}}</td>
										</tr>
								</table>
								</div>
								<div ng-show="(value.rearrangements | filterterm:'is_significant':'false').length">
								<h4>Rearrangements</h4>
								<table>
										<tr>
											<th>Gene 1</th>
											<th>Gene 2</th>
											<th>Type</th>
											<th>Inframe</th>
											<th>Pos1</th>
											<th>Pos2</th>
											<th>Number of Reads</th>
											<th>Variant allele frequency (%)</th>
											<th>Description</th>
											<th>Status</th>
										</tr>
				
										<tr
											ng-repeat="gene in value.rearrangements | filterterm:'is_significant':'false' | orderBy:'gene1'">
											<td><em>{{gene.gene1}}</em></td>
											<td><em>{{gene.gene2}}</em></td>
											<td>{{gene.rearrangemnet_type}}</td>
											<td>{{gene.inframe}}</td>
											<td>{{gene.pos1}}</td>
											<td>{{gene.pos2}}</td>
											<td>{{gene.number_of_reads}}</td>
											<td>{{gene.variant_allele_frequency}}</td>
											<td>{{gene.description}}
											<td>{{gene.status}}</td>
										</tr>
										
								</table>
								</div>
							</div>
							</div>
						</div>
						
					</uib-tab>
					<!-- </div> -->
				    </uib-tabset>
					    <p class="fm_headline" ng-show="valueBase['1'].hasOwnProperty('report') && !valueBase['1'].hasOwnProperty('versions')">
					        <i class="fa fa-file-pdf-o" aria-hidden="true"></i> Report: 
							<a target="_blank" ng-href="rest/pdf/{{valueBase['1'].report}}">{{valueBase['1'].report}}</a>
					    </p>
					    <p class="fm_headline" ng-show="valueBase['1'].hasOwnProperty('versions')" ng-repeat="report in valueBase['1'].versions">
					        <i class="fa fa-file-pdf-o" aria-hidden="true"></i> Report: 
							<a target="_blank" ng-href="rest/pdf/{{report}}">{{report}}</a>
					    </p>
					</div>
		
				</div>
				
					
				</c:if>
				<c:if test = "${tabTumour[loop.index] =='true'}">
					<div ng-show="detailSection.${tabId[loop.index]}_tumour">
					<h1>${tag} Tumour</h1>
					<nav class="detail-section">
					<a href="#"
						ng-repeat="(key,value) in currentPatient.${tabId[loop.index]}_tumour.baselineList | orderBy:'date'"
						ng-show="value.baseline<2 || currentPatient.${tabId[loop.index]}_tumour.baseline[value.baseline][1].hasOwnProperty('panel_id') || currentPatient.${tabId[loop.index]}_tumour.baseline[value.baseline]['1'].hasOwnProperty('report') 
						|| currentPatient.${tabId[loop.index]}_tumour.baseline[value.baseline]['1'].hasOwnProperty('versions')"
						ng-class="(detailSection.${tabId[loop.index]}_tumourSubs[value.baseline]) ? 'baseline-active': 'none'"
						ng-click="showBaseline('${tabId[loop.index]}_tumour', value.baseline)">Bx{{value.baseline}}</a>
					</nav>
					<!-- Baselines -->
					<div ng-show="!currentPatient.${tabId[loop.index]}_tumour.baseline.hasOwnProperty('1')" >
						<p>No tumour samples found.</p>
					</div>
					<div ng-repeat="(keyBase, valueBase) in currentPatient.${tabId[loop.index]}_tumour.baseline"	
					     ng-show="detailSection.${tabId[loop.index]}_tumourSubs[keyBase]"
					     ng-init="baselineCurr = $index">
						<p>Date of Sample: {{valueBase.specimen.specimenDate}}</p>
						<p ng-show="!valueBase['1'].hasOwnProperty('panel_id')">Results not available.</p>
						<uib-tabset active="active_${tabId[loop.index]}_tumour[1][keyBase]" ng-show="valueBase['1'].hasOwnProperty('panel_id')">
						<!-- <div ng-repeat="(version, value) in valueBase" 
						     ng-show="version!='specimen' && value.hasOwnProperty('tmb_status')">-->
						     
							<uib-tab data-ng-repeat="(version, value) in valueBase" 
							         ng-show="version!='specimen' && value.hasOwnProperty('panel_id')" 
							         index="version" heading="Version {{version}}" class="tab_header{{(valueBase | objLength)-1}}">
							<div ng-class="{'greybackground' : (valueBase | objLength)>2 && ''+((valueBase | objLength)-1)!=version}">
						
						<!-- <div ng-repeat="(key, run) in valueBase" 
						     ng-show="key!='specimen' && run.hasOwnProperty('tmb_status')">
							<h2 ng-show="valueBase.hasOwnProperty('2')">Run {{key}}</h2> -->
							<p>TMB: <span>{{value.tmb_status}}<span ng-show="value.tmb_score!=''">; </span>{{value.tmb_score}} {{value.tmb_unit}}</span></p>
							<p>Microsatellite Status: <span ng-if="value.microsatellite_status!='MSS'">{{value.microsatellite_status}}</span><span ng-if="value.microsatellite_status=='MSS'">MS-Stable</span></p>
							<p>Mean exon depth: <span>{{value.mean_exon_depth}}</span>
							<!-- <p>Percentage with 100x read depth: <span>{{value.percent_exons_100x}}</span></p>-->
							<div class="fm_headline">
								<h3>Genomic Alterations Identified</h3>
								<h4>Short Variants</h4>
								<table>
										<tr>
											<th>Significant Mutations</th>
											<th>Gene Name</th>
											<th id="result_column">Result</th>
											<th>Variant Allele Frequency (%)</th>
											<th>Read Depth</th>
											<th>Functional Effect</th>
											<th>Position</th>
											<th>Transcript</th>
											<th>Subclonal</th>
											<th>Status</th>
										</tr>
				
										<tr
											ng-repeat="gene in value.short_variants | filterterm:'is_significant':'true' | orderBy: 'geneName'">
											<td>
												<div ng-include="loadComponent('genericgenomiccheckbox','${tabId[loop.index]}_tumour')"></div>
												<div ng-include="loadComponent('genericgenomictick', '${tabId[loop.index]}_tumour')"></div>
											</td>
											<td><em>{{gene.geneName}}</em></td>
											<td
												ng-bind-html="trustAsHtml(gene.result)"></td>
											<td>{{gene.variant_allele_frequency}}</td>
											<td>{{gene.read_depth}}</td>
											<td>{{gene.functional_effect}}</td>
											<td>{{gene.position}}</td>
											<td ng-if="(gene.transcript).startsWith('NM')==true"><a target="_blank" ng-href="https://www.ncbi.nlm.nih.gov/nuccore/{{gene.transcript}}">
											{{gene.transcript}}</a></td>
											<td ng-if="(gene.transcript).startsWith('NM')==false">{{gene.transcript}}</td>
											<td>{{gene.subclonal}}</td>
											<td>{{gene.status}}</td>
										</tr>
										<tr	ng-if="(value.short_variants | filterterm:'is_significant':'true').length == 0">
											<td>Nil</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
										</tr>
								</table>
								<h4>Copy Number Alterations</h4>
								<table>
										<tr>
											<th>Significant</th>
											<th>Gene Name</th>
											<th>Copy Number</th>
											<th>Exons</th>
											<th>Ratio</th>
											<th>Type</th>
											<th>Equivocal</th>
											<th>Status</th>
										</tr>
				
										<tr
											ng-repeat="gene in value.copy_number_alterations | filterterm:'is_significant':'true' | orderBy: 'geneName'">
											<td>
												<div ng-include="loadComponent('genericgenomiccheckbox','${tabId[loop.index]}_tumour')"></div>
												<div ng-include="loadComponent('genericgenomictick', '${tabId[loop.index]}_tumour')"></div>
											</td>
											<td><em>{{gene.geneName}}</em></td>
											<td>{{gene.copy_number}}</td>
											<td>{{gene.exons}}</td>
											<td>{{gene.ratio}}</td>
											<td>{{gene.type}}</td>
											<td>{{gene.equivocal}}</td>
											<td>{{gene.status}}</td>
										</tr>
										<tr ng-if="(value.copy_number_alterations | filterterm:'is_significant':'true').length == 0">
											<td>Nil</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
										</tr>
								</table>
								<h4>Rearrangements</h4>
								<table>
										<tr>
											<th>Significant</th>
											<th>Gene 1</th>
											<th>Gene 2</th>
											<th>Type</th>
											<th>Inframe</th>
											<th>Pos1</th>
											<th>Pos2</th>
											<th>Number of Reads</th>
											<th>Variant Allele Frequency (%)</th>
											<th>Description</th>
											<th>Status</th>
										</tr>
				
										<tr
											ng-repeat="gene in value.rearrangements | filterterm:'is_significant':'true' | orderBy: 'gene1'">
											<td>
												<div ng-include="loadComponent('genericgenomiccheckbox','${tabId[loop.index]}_tumour')"></div>
												<div ng-include="loadComponent('genericgenomictick', '${tabId[loop.index]}_tumour')"></div>
											</td>
											<td><em>{{gene.gene1}}</em></td>
											<td><em>{{gene.gene2}}</em></td>
											<td>{{gene.rearrangement_type}}</td>
											<td>{{gene.inframe}}</td>
											<td>{{gene.pos1}}</td>
											<td>{{gene.pos2}}</td>
											<td>{{gene.number_of_reads}}</td>
											<td>{{gene.variant_allele_frequency}}</td>
											<td>{{gene.description}}</td>
											<td>{{gene.status}}</td>
										</tr>
										<tr ng-if="(value.rearrangements | filterterm:'is_significant':'true').length == 0">
											<td>Nil</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
										</tr>
								</table>
							</div>
							<div ng-show="(value.short_variants | filterterm:'is_significant':'false').length > 0||
							              (value.copy_number_alterations | filterterm:'is_significant':'false').length >0 || 
							              (value.rearrangements | filterterm:'is_significant':'false').length">
								<h3 class="pointer fm_headline" ng-click="showTumourVUS = !showTumourVUS">Variants of Unknown Significance 
								<span ng-show="!showTumourVUS">(Show)</span><span ng-show="showTumourVUS">(Hide)</span></h3>
								<div ng-show="showTumourVUS">
								<p>Note: One or more variants of unknown significance (VUS) were detected in this patient's tumour. 
								These variants may not have been adequately characterized in the scientific literature at the time this report was issued,
								and/or the genomic context of these alterations makes their significance unclear. 
								We choose to include them here in the event that they become clinically meaningful in the future.</p>
								<div ng-show="(value.short_variants | filterterm:'is_significant':'false').length">
								<h4>Short Variants</h4>
								<table>
										<tr>
											<th>Gene Name</th>
											<th id="result_column">Result</th>
											<th>Variant Allele Frequency (%)</th>
											<th>Read Depth</th>
											<th>Functional Effect</th>
											<th>Position</th>
											<th>Transcript</th>
											<th>Subclonal</th>
											<th>Status</th>
										</tr>
				
										<tr
											ng-repeat="gene in value.short_variants | filterterm:'is_significant':'false' | orderBy: 'geneName'">
											<td><em>{{gene.geneName}}</em></td>
											<td
												ng-bind-html="trustAsHtml(gene.result)"></td>
											<td>{{gene.variant_allele_frequency}}</td>
											<td>{{gene.read_depth}}</td>
											<td>{{gene.functional_effect}}</td>
											<td>{{gene.position}}</td>
											<td ng-if="(gene.transcript).startsWith('NM')==true"><a target="_blank" ng-href="https://www.ncbi.nlm.nih.gov/nuccore/{{gene.transcript}}">
											{{gene.transcript}}</a></td>
											<td ng-if="(gene.transcript).startsWith('NM')==false">{{gene.transcript}}</td>
											<td>{{gene.subclonal}}</td>
											<td>{{gene.status}}</td>
										</tr>
								</table>
								</div>
								<div ng-show="(value.copy_number_alterations | filterterm:'is_significant':'false').length">
								<h4>Copy Number Alterations</h4>
								<table>
										<tr>
											<th>Gene Name</th>
											<th>Copy Number</th>
											<th>Exons</th>
											<th>Ratio</th>
											<th>Type</th>
											<th>Equivocal</th>
											<th>Status</th>
										</tr>
				
										<tr
											ng-repeat="gene in value.copy_number_alterations | filterterm:'is_significant':'false' | orderBy:'geneName'">
											<td><em>{{gene.geneName}}</em></td>
											<td>{{gene.copy_number}}</td>
											<td>{{gene.exons}}</td>
											<td>{{gene.ratio}}</td>
											<td>{{gene.type}}</td>
											<td>{{gene.equivocal}}</td>
											<td>{{gene.status}}</td>
										</tr>
								</table>
								</div>
								<div ng-show="(value.rearrangements | filterterm:'is_significant':'false').length">
								<h4>Rearrangements</h4>
								<table>
										<tr>
											<th>Gene 1</th>
											<th>Gene 2</th>
											<th>Type</th>
											<th>Inframe</th>
											<th>Pos1</th>
											<th>Pos2</th>
											<th>Number of Reads</th>
											<th>Variant Allele Frequency (%)</th>
											<th>Description</th>
											<th>Status</th>
										</tr>
				
										<tr
											ng-repeat="gene in value.rearrangements | filterterm:'is_significant':'false' | orderBy:'gene1'">
											<td><em>{{gene.gene1}}</em></td>
											<td><em>{{gene.gene2}}</em></td>
											<td>{{gene.rearrangement_type}}</td>
											<td>{{gene.inframe}}</td>
											<td>{{gene.pos1}}</td>
											<td>{{gene.pos2}}</td>
											<td>{{gene.number_of_reads}}</td>
											<td>{{gene.variant_allele_frequency}}</td>
											<td>{{gene.description}}</td>
											<td>{{gene.status}}</td>
										</tr>
										
								</table>
								</div>
							</div>
							</div>
						</div>
						
					</uib-tab>
					<!-- </div> -->
				    </uib-tabset>
					    <p class="fm_headline" ng-show="valueBase['1'].hasOwnProperty('report') && !valueBase['1'].hasOwnProperty('versions')">
					        <i class="fa fa-file-pdf-o" aria-hidden="true"></i> Report: 
							<a target="_blank" ng-href="rest/pdf/{{valueBase['1'].report}}">{{valueBase['1'].report}}</a>
					    </p>
					    <p class="fm_headline" ng-show="valueBase['1'].hasOwnProperty('versions')" ng-repeat="report in valueBase['1'].versions">
					        <i class="fa fa-file-pdf-o" aria-hidden="true"></i> Report: 
							<a target="_blank" ng-href="rest/pdf/{{report}}">{{report}}</a>
					    </p>
					</div>
		
				</div>

				</c:if>
			</c:forEach>
			
			<!-- Foundation Tumour start -->
			<c:if test = "${fmtumour =='true'}">
			<div ng-show="detailSection.foundationTumour">
				<h1>Foundation Tumour</h1>
				<nav class="detail-section">
					<a href="#"
						ng-repeat="(key,value) in currentPatient.fmTumour"
						ng-show="key<2 || currentPatient.fmTumour[key][1].hasOwnProperty('panel_id') || currentPatient.fmTumour[key]['1'].hasOwnProperty('report') 
						|| currentPatient.fmTumour[key]['1'].hasOwnProperty('versions')"
						ng-class="(detailSection.fmTumourSubs[key]) ? 'baseline-active': 'none'"
						ng-click="showFMTumourBaseline(key)">Bx{{key}}</a>
				</nav>
				<!-- Baselines -->
				<div ng-show="!currentPatient.fmTumour.hasOwnProperty('1')" >
					<p>No tumour samples found.</p>
				</div>
				<div ng-repeat="(keyBase, valueBase) in currentPatient.fmTumour"	
				     ng-show="detailSection.fmTumourSubs[keyBase]"
				     ng-init="baselineCurr = $index">
					<p>Date of Sample: {{valueBase.specimen.specimenDate}}</p>
					<p ng-show="!valueBase['1'].hasOwnProperty('panel_id')">Results not available.</p>
					<uib-tabset active="active_FMTumour[1][keyBase]" ng-show="valueBase['1'].hasOwnProperty('panel_id')">
					<!-- <div ng-repeat="(version, value) in valueBase" 
					     ng-show="version!='specimen' && value.hasOwnProperty('tmb_status')">-->
						<uib-tab data-ng-repeat="(version, value) in valueBase" 
						         ng-show="version!='specimen' && value.hasOwnProperty('panel_id')" 
						         index="version" heading="Version {{version}}" class="tab_header{{(valueBase | objLength)-1}}">
						<div ng-class="{'greybackground' : (valueBase | objLength)>2 && ''+((valueBase | objLength)-1)!=version}">
					
					<!-- <div ng-repeat="(key, run) in valueBase" 
					     ng-show="key!='specimen' && run.hasOwnProperty('tmb_status')">
						<h2 ng-show="valueBase.hasOwnProperty('2')">Run {{key}}</h2> -->
						<p>TMB: <span>{{value.tmb_status}}<span ng-show="value.tmb_score!=''">; </span>{{value.tmb_score}} {{value.tmb_unit}}</span></p>
						<p>Microsatellite Status: <span ng-if="value.microsatellite_status!='MSS'">{{value.microsatellite_status}}</span><span ng-if="value.microsatellite_status=='MSS'">MS-Stable</span></p>
						<p>Mean exon depth: <span>{{value.mean_exon_depth}}</span>
						<!--<p>Percentage with 100x read depth: <span>{{value.percent_exons_100x}}</span></p>-->
						<div class="fm_headline">
							<h3>Genomic Alterations Identified</h3>
							<h4>Short Variants</h4>
							<table>
									<tr>
										<th>Significant Mutations</th>
										<th>Gene Name</th>
										<th id="result_column">Result</th>
										<th>Variant Allele Frequency (%)</th>
										<th>Read Depth</th>
										<th>Functional Effect</th>
										<th>Position</th>
										<th>Transcript</th>
										<th>Subclonal</th>
										<th>Status</th>
									</tr>
			
									<tr
										ng-repeat="gene in value.short_variants | filterterm:'is_significant':'true' | orderBy: 'geneName'">
										<td>
											<div ng-include="loadComponent('fmtumourcheckbox')"></div>
											<div ng-include="loadComponent('fmtumourtick')"></div>
										</td>
										<td><em>{{gene.geneName}}</em></td>
										<td
											ng-bind-html="trustAsHtml(gene.result)"></td>
										<td>{{gene.variant_allele_frequency}}</td>
										<td>{{gene.read_depth}}</td>
										<td>{{gene.functional_effect}}</td>
										<td>{{gene.position}}</td>
										<td ng-if="(gene.transcript).startsWith('NM')==true"><a target="_blank" ng-href="https://www.ncbi.nlm.nih.gov/nuccore/{{gene.transcript}}">
										{{gene.transcript}}</a></td>
										<td ng-if="(gene.transcript).startsWith('NM')==false">{{gene.transcript}}</td>
										<td>{{gene.subclonal}}</td>
										<td>{{gene.status}}</td>
									</tr>
									<tr	ng-if="(value.short_variants | filterterm:'is_significant':'true').length == 0">
										<td>Nil</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
									</tr>
							</table>
							<h4>Copy Number Alterations</h4>
							<table>
									<tr>
										<th>Significant</th>
										<th>Gene Name</th>
										<th>Copy Number</th>
										<th>Exons</th>
										<th>Ratio</th>
										<th>Type</th>
										<th>Equivocal</th>
										<th>Status</th>
									</tr>
			
									<tr
										ng-repeat="gene in value.copy_number_alterations | filterterm:'is_significant':'true' | orderBy: 'geneName'">
										<td>
											<div ng-include="loadComponent('fmtumourcheckbox')"></div>
											<div ng-include="loadComponent('fmtumourtick')"></div>
										</td>
										<td><em>{{gene.geneName}}</em></td>
										<td>{{gene.copy_number}}</td>
										<td>{{gene.exons}}</td>
										<td>{{gene.ratio}}</td>
										<td>{{gene.type}}</td>
										<td>{{gene.equivocal}}</td>
										<td>{{gene.status}}</td>
									</tr>
									<tr ng-if="(value.copy_number_alterations | filterterm:'is_significant':'true').length == 0">
										<td>Nil</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
									</tr>
							</table>
							<h4>Rearrangements</h4>
							<table>
									<tr>
										<th>Significant</th>
										<th>Gene 1</th>
										<th>Gene 2</th>
										<th>Type</th>
										<th>Inframe</th>
										<th>Pos1</th>
										<th>Pos2</th>
										<th>Number of Reads</th>
										<th>Variant Allele Frequency (%)</th>
										<th>Description</th>
										<th>Status</th>
									</tr>
			
									<tr
										ng-repeat="gene in value.rearrangements | filterterm:'is_significant':'true' | orderBy: 'gene1'">
										<td>
											<div ng-include="loadComponent('fmtumourcheckbox')"></div>
											<div ng-include="loadComponent('fmtumourtick')"></div>
										</td>
										<td><em>{{gene.gene1}}</em></td>
										<td><em>{{gene.gene2}}</em></td>
										<td>{{gene.rearrangement_type}}</td>
										<td>{{gene.inframe}}</td>
										<td>{{gene.pos1}}</td>
										<td>{{gene.pos2}}</td>
										<td>{{gene.number_of_reads}}</td>
										<td>{{gene.variant_allele_frequency}}</td>
										<td>{{gene.description}}</td>
                                        <td>{{gene.status}}</td>
									</tr>
									<tr ng-if="(value.rearrangements | filterterm:'is_significant':'true').length == 0">
										<td>Nil</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
				                        <td>&nbsp;</td>
				                        <td>&nbsp;</td>
				                        <td>&nbsp;</td>
									</tr>
							</table>
						</div>
						<div ng-show="(value.short_variants | filterterm:'is_significant':'false').length > 0||
						              (value.copy_number_alterations | filterterm:'is_significant':'false').length >0 || 
						              (value.rearrangements | filterterm:'is_significant':'false').length">
							<h3 class="pointer fm_headline" ng-click="showTumourVUS = !showTumourVUS">Variants of Unknown Significance 
							<span ng-show="!showTumourVUS">(Show)</span><span ng-show="showTumourVUS">(Hide)</span></h3>
							<div ng-show="showTumourVUS">
							<p>Note: One or more variants of unknown significance (VUS) were detected in this patient's tumour. 
							These variants may not have been adequately characterized in the scientific literature at the time this report was issued,
							and/or the genomic context of these alterations makes their significance unclear. 
							We choose to include them here in the event that they become clinically meaningful in the future.</p>
							<div ng-show="(value.short_variants | filterterm:'is_significant':'false').length">
							<h4>Short Variants</h4>
							<table>
									<tr>
										<th>Gene Name</th>
										<th id="result_column">Result</th>
										<th>Variant Allele Frequency (%)</th>
										<th>Read Depth</th>
										<th>Functional Effect</th>
										<th>Transcript</th>
										<th>Position</th>
										<th>Subclonal</th>
										<th>Status</th>
									</tr>
			
									<tr
										ng-repeat="gene in value.short_variants | filterterm:'is_significant':'false' | orderBy: 'geneName'">
										<td><em>{{gene.geneName}}</em></td>
										<td
											ng-bind-html="trustAsHtml(gene.result)"></td>
										<td>{{gene.variant_allele_frequency}}</td>
										<td>{{gene.read_depth}}</td>
										<td>{{gene.functional_effect}}</td>
										<td>{{gene.position}}</td>
										<td ng-if="(gene.transcript).startsWith('NM')==true"><a target="_blank" ng-href="https://www.ncbi.nlm.nih.gov/nuccore/{{gene.transcript}}">
										{{gene.transcript}}</a></td>
										<td ng-if="(gene.transcript).startsWith('NM')==false">{{gene.transcript}}</td>
										<td>{{gene.subclonal}}</td>
                                        <td>{{gene.status}}</td>
									</tr>
							</table>
							</div>
							<div ng-show="(value.copy_number_alterations | filterterm:'is_significant':'false').length">
							<h4>Copy Number Alterations</h4>
							<table>
									<tr>
										<th>Gene Name</th>
										<th>Copy Number</th>
										<th>Exons</th>
										<th>Ratio</th>
										<th>Type</th>
										<th>Equivocal</th>
			                            <th>Status</th>
									</tr>
			
									<tr
										ng-repeat="gene in value.copy_number_alterations | filterterm:'is_significant':'false' | orderBy:'geneName'">
										<td><em>{{gene.geneName}}</em></td>
										<td>{{gene.copy_number}}</td>
										<td>{{gene.exons}}</td>
										<td>{{gene.ratio}}</td>
										<td>{{gene.type}}</td>
										<td>{{gene.equivocal}}</td>
                                        <td>{{gene.status}}</td>
									</tr>
							</table>
							</div>
							<div ng-show="(value.rearrangements | filterterm:'is_significant':'false').length">
							<h4>Rearrangements</h4>
							<table>
									<tr>
										<th>Gene 1</th>
										<th>Gene 2</th>
										<th>Type</th>
										<th>Inframe</th>
										<th>Pos1</th>
										<th>Pos2</th>
										<th>Number of Reads</th>
										<th>Variant Allele Frequency (%)</th>
										<th>Description</th>
                                        <th>Status</th>
									</tr>
			
									<tr
										ng-repeat="gene in value.rearrangements | filterterm:'is_significant':'false' | orderBy:'gene1'">
										<td><em>{{gene.gene1}}</em></td>
										<td><em>{{gene.gene2}}</em></td>
										<td>{{gene.rearrangement_type}}</td>
										<td>{{gene.inframe}}</td>
										<td>{{gene.pos1}}</td>
										<td>{{gene.pos2}}</td>
										<td>{{gene.number_of_reads}}</td>
										<td>{{gene.variant_allele_frequency}}</td>
										<td>{{gene.description}}</td>
                                        <td>{{gene.status}}</td>
									</tr>
									
							</table>
							</div>
						</div>
						</div>
					</div>
					
				</uib-tab>
				<!-- </div> -->
			    </uib-tabset>
				    <p class="fm_headline" ng-show="valueBase['1'].hasOwnProperty('report') && !valueBase['1'].hasOwnProperty('versions')">
				        <i class="fa fa-file-pdf-o" aria-hidden="true"></i> Report: 
						<a target="_blank" ng-href="rest/pdf/{{valueBase['1'].report}}">{{valueBase['1'].report}}</a>
				    </p>
				    <p class="fm_headline" ng-show="valueBase['1'].hasOwnProperty('versions')" ng-repeat="report in valueBase['1'].versions">
				        <i class="fa fa-file-pdf-o" aria-hidden="true"></i> Report: 
						<a target="_blank" ng-href="rest/pdf/{{report}}">{{report}}</a>
				    </p>
				</div>
	
			</div>
			</c:if>
			<!-- Foundation Blood start -->
			<c:if test = "${fmblood =='true'}">
			<div ng-show="detailSection.foundationBlood">
				<h1>Foundation Blood</h1>
				<nav id="fmblood_nav" class="detail-section"> <!-- ng-show="document.querySelectorAll('#fmblood_nav a:not(.ng-hide)').length>1 || currentPatient.fmBlood.baseline[1][1].hasOwnProperty('panel_id') -->
					<a href="#"
						ng-repeat="(key,value) in currentPatient.fmBlood.baselineList | orderBy:'date'"
						ng-show="value.baseline<2 || currentPatient.fmBlood.baseline[value.baseline][1].hasOwnProperty('panel_id') || currentPatient.fmBlood.baseline[value.baseline]['1'].hasOwnProperty('report') 
						|| currentPatient.fmBlood.baseline[value.baseline]['1'].hasOwnProperty('versions')"
						ng-class="(detailSection.fmBloodSubs[value.baseline]) ? 'baseline-active': 'none'"
						ng-click="showFMBloodBaseline(value.baseline)">T{{value.baseline}}</a>
				</nav>
				<!-- Baselines -->
				<div ng-show="!currentPatient.fmBlood.baseline.hasOwnProperty('1')" >
					<p>No blood samples found.</p>
				</div>
				<div ng-repeat="(keyBase, valueBase) in currentPatient.fmBlood.baseline"	
				     ng-show="detailSection.fmBloodSubs[keyBase]"
				     ng-init="baselineCurr = $index">
					<p>Date of Sample: {{valueBase.specimen.specimenDate}}</p>
					<p ng-show="!valueBase['1'].hasOwnProperty('panel_id')">Results not available.</p>
					<uib-tabset active="active_FMBlood[1][keyBase]" ng-show="valueBase['1'].hasOwnProperty('panel_id')">
					<div ng-repeat="(version, value) in valueBase" 
					     ng-show="version!='specimen' && value.hasOwnProperty('panel_id')">
						<uib-tab index="version" heading="Version {{version}}" class="tab_header{{(valueBase | objLength)-1}}">
						<div ng-class="{'greybackground' : (valueBase | objLength)>2 && ''+((valueBase | objLength)-1)!=version}">
					<!-- <div ng-repeat="(key, run) in valueBase" 
					     ng-show="key!='specimen' && run.hasOwnProperty('tmb_status')">
						<h2 ng-show="valueBase.hasOwnProperty('2')">Run {{key}}</h2> -->
						<p>TMB: <span>{{value.tmb_status}}<span ng-show="value.tmb_score!=''">; </span>{{value.tmb_score}} {{value.tmb_unit}}</span></p>
						<p>Microsatellite Status: <span ng-if="value.microsatellite_status!='MSS'">{{value.microsatellite_status}}</span><span ng-if="value.microsatellite_status=='MSS'">Cannot Be Determined</span></p>
						<p>Tumour Fraction Score: <span>{{value.tumour_fraction_score}} {{value.tumour_fraction_unit}}</span>
						<!-- <p>Percentage with 100x read depth: <span>{{value.percent_exons_100x}}</span></p>-->
						<div class="fm_headline">
							<h3>Genomic Alterations Identified</h3>
							<h4>Short Variants</h4>
							<table>
									<tr>
										<th>Significant Mutations</th>
										<th>Gene Name</th>
										<th id="result_column">Result</th>
										<th>Variant Allele Frequency (%)</th>
										<th>Read Depth</th>
										<th>Functional Effect</th>
										<th>Position</th>
										<th>Transcript</th>
                                        <th>Status</th>
									</tr>
			
									<tr
										ng-repeat="gene in value.short_variants | filterterm:'is_significant':'true' | orderBy:'geneName'">
										<td>
											<div ng-include="loadComponent('fmbloodcheckbox')"></div>
											<div ng-include="loadComponent('fmbloodtick')"></div>
										</td>
										<td><em>{{gene.geneName}}</em></td>
										<td
											ng-bind-html="trustAsHtml(gene.result)"></td>
										<td>{{gene.variant_allele_frequency}}</td>
										<td>{{gene.read_depth}}</td>
										<td>{{gene.functional_effect}}</td>
										<td>{{gene.position}}</td>
										<td ng-if="(gene.transcript).startsWith('NM')==true"><a target="_blank" ng-href="https://www.ncbi.nlm.nih.gov/nuccore/{{gene.transcript}}">
										{{gene.transcript}}</a></td>
										<td ng-if="(gene.transcript).startsWith('NM')==false">{{gene.transcript}}</td>
                                        <td>{{gene.status}}</td>
									</tr>
									<tr	ng-if="(value.short_variants | filterterm:'is_significant':'true').length == 0">
										<td>Nil</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
				                        <td>&nbsp;</td>
				                        <td>&nbsp;</td>
									</tr>
							</table>
							<h4>Copy Number Alterations</h4>
							<table>
									<tr>
										<th>Significant</th>
										<th>Gene Name</th>
										<th>Copy Number</th>
										<th>Exons</th>
										<th>Ratio</th>
										<th>Type</th>
										<th>Equivocal</th>
			                            <th>Status</th>
									</tr>
			
									<tr
										ng-repeat="gene in value.copy_number_alterations | filterterm:'is_significant':'true' | orderBy: 'geneName'">
										<td>
											<div ng-include="loadComponent('fmbloodcheckbox')"></div>
											<div ng-include="loadComponent('fmbloodtick')"></div>
										</td>
										<td><em>{{gene.geneName}}</em></td>
										<td>{{gene.copy_number}}</td>
										<td>{{gene.exons}}</td>
										<td>{{gene.ratio}}</td>
										<td>{{gene.type}}</td>
										<td>{{gene.equivocal}}</td>
                                        <td>{{gene.status}}</td>
									</tr>
									<tr ng-if="(value.copy_number_alterations | filterterm:'is_significant':'true').length == 0">
										<td>Nil</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
				                        <td>&nbsp;</td>
				                        <td>&nbsp;</td>
									</tr>
							</table>
							<h4>Rearrangements</h4>
							<table>
									<tr>
										<th>Significant</th>
										<th>Gene 1</th>
										<th>Gene 2</th>
										<th>Type</th>
										<th>Inframe</th>
										<th>Pos1</th>
										<th>Pos2</th>
										<th>Number of Reads</th>
										<th>Variant Allele Frequency (%)</th>
										<th>Description</th>
                                        <th>Status</th>
									</tr>
			
									<tr
										ng-repeat="gene in value.rearrangements | filterterm:'is_significant':'true' | orderBy:'gene1'">
										<td>
											<div ng-include="loadComponent('fmbloodcheckbox')"></div>
											<div ng-include="loadComponent('fmbloodtick')"></div>
										</td>
										<td><em>{{gene.gene1}}</em></td>
										<td><em>{{gene.gene2}}</em></td>
										<td>{{gene.rearrangement_type}}</td>
										<td>{{gene.inframe}}</td>
										<td>{{gene.pos1}}</td>
										<td>{{gene.pos2}}</td>
										<td>{{gene.number_of_reads}}</td>
										<td>{{gene.variant_allele_frequency}}</td>
										<td>{{gene.description}}</td>
                                        <td>{{gene.status}}</td>
									</tr>
									<tr ng-if="(value.rearrangements | filterterm:'is_significant':'true').length == 0">
										<td>Nil</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
				                        <td>&nbsp;</td>
				                        <td>&nbsp;</td>
									</tr>
							</table>
						</div>
						<div ng-show="(value.short_variants | filterterm:'is_significant':'false').length > 0||
						              (value.copy_number_alterations | filterterm:'is_significant':'false').length >0 || 
						              (value.rearrangements | filterterm:'is_significant':'false').length">
							<h3 class="pointer fm_headline" ng-click="showBloodVUS = !showBloodVUS">Variants of Unknown Significance  
							<span ng-show="!showBloodVUS">(Show)</span><span ng-show="showBloodVUS">(Hide)</span></h3>
							<div ng-show="showBloodVUS">
							<p>Note: One or more variants of unknown significance (VUS) were detected in this patient's tumour. 
							These variants may not have been adequately characterized in the scientific literature at the time this report was issued,
							and/or the genomic context of these alterations makes their significance unclear. 
							We choose to include them here in the event that they become clinically meaningful in the future.</p>
							<div ng-show="(value.short_variants | filterterm:'is_significant':'false').length">
							<h4>Short Variants</h4>
							<table>
									<tr>
										<th>Gene Name</th>
										<th id="result_column">Result</th>
										<th>Variant Allele Frequency (%)</th>
										<th>Read Depth</th>
										<th>Functional Effect</th>
										<th>Position</th>
										<th>Transcript</th>
                                        <th>Status</th>
									</tr>
			
									<tr
										ng-repeat="gene in value.short_variants | filterterm:'is_significant':'false' | orderBy:'geneName'">
										<td><em>{{gene.geneName}}</em></td>
										<td
											ng-bind-html="trustAsHtml(gene.result)"></td>
										<td>{{gene.variant_allele_frequency}}</td>
										<td>{{gene.read_depth}}</td>
										<td>{{gene.functional_effect}}</td>
										<td>{{gene.position}}</td>
										<td ng-if="(gene.transcript).startsWith('NM')==true"><a target="_blank" ng-href="https://www.ncbi.nlm.nih.gov/nuccore/{{gene.transcript}}">
										{{gene.transcript}}</a></td>
										<td ng-if="(gene.transcript).startsWith('NM')==false">{{gene.transcript}}</td>
                                        <td>{{gene.status}}</td>
									</tr>
							</table>
							</div>
							<div ng-show="(value.copy_number_alterations | filterterm:'is_significant':'false').length">
							<h4>Copy Number Alterations</h4>
							<table>
									<tr>
										<th>Gene Name</th>
										<th>Copy Number</th>
										<th>Exons</th>
										<th>Ratio</th>
										<th>Type</th>
										<th>Equivocal</th>
			                            <th>Status</th>
									</tr>
			
									<tr
										ng-repeat="gene in value.copy_number_alterations | filterterm:'is_significant':'false' | orderBy:'geneName'">
										<td><em>{{gene.geneName}}</em></td>
										<td>{{gene.copy_number}}</td>
										<td>{{gene.exons}}</td>
										<td>{{gene.ratio}}</td>
										<td>{{gene.type}}</td>
										<td>{{gene.equivocal}}</td>
                                        <td>{{gene.status}}</td>
									</tr>
							</table>
							</div>
							<div ng-show="(value.rearrangements | filterterm:'is_significant':'false').length">
							<h4>Rearrangements</h4>
							<table>
									<tr>
										<th>Gene 1</th>
										<th>Gene 2</th>
										<th>Type</th>
										<th>Inframe</th>
										<th>Pos1</th>
										<th>Pos2</th>
										<th>Number of Reads</th>
										<th>Variant Allele Frequency (%)</th>
										<th>Description</th>
                                        <th>Status</th>
									</tr>
			
									<tr
										ng-repeat="gene in value.rearrangements | filterterm:'is_significant':'false' | orderBy:'gene1'">
										<td><em>{{gene.gene1}}</em></td>
										<td><em>{{gene.gene2}}</em></td>
										<td>{{gene.rearrangement_type}}</td>
										<td>{{gene.inframe}}</td>
										<td>{{gene.pos1}}</td>
										<td>{{gene.pos2}}</td>
										<td>{{gene.number_of_reads}}</td>
										<td>{{gene.variant_allele_frequency}}</td>
										<td>{{gene.description}}
                                        <td>{{gene.status}}</td>
									</tr>
							</table>
							</div>
							</div>
						</div>
					</div>
					</uib-tab>
				</div>
				</uib-tabset>
					<p class="fm_headline" ng-show="valueBase['1'].hasOwnProperty('report') && !valueBase['1'].hasOwnProperty('versions')"><i class="fa fa-file-pdf-o" aria-hidden="true"></i> Report: 
						<a target="_blank" ng-href="rest/pdf/{{valueBase['1'].report}}">{{valueBase['1'].report}}</a>
					</p>
					<p class="fm_headline" ng-show="valueBase['1'].hasOwnProperty('versions')" ng-repeat="report in valueBase['1'].versions">
					<i class="fa fa-file-pdf-o" aria-hidden="true"></i> Report: 
						<a target="_blank" ng-href="rest/pdf/{{report}}">{{report}}</a>
					</p>
				</div>
	
				
			</div>
			</c:if>
			
			<!-- ctDNA NGS start -->
			<c:if test = "${ctdnangs =='true'}">
			<div ng-show="detailSection.ctdnaNGS">
				<p id="editLockMessage" ng-show="currentPatient.editLockMessage!= ''">
					{{currentPatient.editLockMessage}}&emsp;<i class="fa fa-refresh pointer" aria-hidden="true" ng-click="checkLockStatus()"></i>
				</p>
				<h1>ctDNA NGS Subset</h1>
				<nav class="detail-section">
					<a href="#"
						ng-repeat="key in currentPatient.ngsSubset.baselineList | orderBy:'date'"
						ng-class="(detailSection.ctdnaNGSSubs[key.baseline]) ? 'baseline-active': 'none'"
						ng-click="showBaseline('ctdnaNGS',key.baseline)">T{{key.baseline}}</a>
				</nav>

				<div ng-if="detailSection.ngsSubset.noResults.message != ''">
					<p ng-if="detailSection.ngsSubset.noResults.specimenDate != ''">Date of Sample:
						{{detailSection.ngsSubset.noResults.specimenDate}}</p>
					<p>{{detailSection.ngsSubset.noResults.message}}</p>
				</div>

				<div
					ng-repeat="(keyBase, valueBase) in currentPatient.ngsSubset.baseline"
					ng-show="detailSection.ctdnaNGSSubs[keyBase]"
					ng-init="baselineCurr = $index">
					<!-- Each BASELINE -->
					<h1>T{{keyBase}}</h1>
					<p>Date of Sample: {{valueBase.specimen.specimenDate}}</p>
					<p ng-show="valueBase.message != ''">{{valueBase.message}}</p>


					<!-- Each baseline RUN -->
					<div ng-repeat="(key, versions) in valueBase.runs" ng-show="valueBase.runs['1']['1'].geneSubset!=null">
						<h2>Run	{{key}}</h2>
						<uib-tabset active="active_ctdna[keyBase][key]">
						<div ng-repeat="(version, value) in versions">
						<uib-tab index="version" heading="Version {{version}}" class="tab_header{{versions | objLength}}">
						<div ng-class="{'greybackground' : (versions | objLength)>1 && ''+(versions | objLength)!=version}">
						<div
							ng-if="value.showNilResult == 'true' || value.noMutationsFound == 'true'">
							<table >
								<thead>
									<tr>
										<th>Gene Name</th>
										<th id="result_column">Result</th>
										<th>cfDNA Frequency (%)</th>
										<th>Total cfDNA Reads</th>
										<th>Germline Frequency (%)</th>
										<th>Mutation Type</th>
										<th>High Confidence Mutation</th>
										<th>Cosmic ID</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td>Nil</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
									</tr>
								</tbody>
							</table>
						</div>

						<div ng-if="value.analysisFailed == 'true'">
							<table >
								<thead>
									<tr>
										<th>Gene Name</th>
										<th id="result_column">Result</th>
										<th>cfDNA Frequency (%)</th>
										<th>Total cfDNA Reads</th>
										<th>Germline Frequency (%)</th>
										<th>Mutation Type</th>
										<th>High Confidence Mutation</th>
										<th>Cosmic ID</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td>Failed</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
									</tr>
								</tbody>
							</table>
						</div>

						<table 
							ng-if="value.analysisFailed == 'false' && value.showNilResult == 'false' && value.noMutationsFound == 'false'">
							<thead>
								<tr>
									<th>Significant Mutations</th>
									<th>Gene Name</th>
									<th id="result_column">Result</th>
									<th>cfDNA Frequency (%)</th>
									<th>Total cfDNA Reads</th>
									<th>Germline Frequency (%)</th>
									<th>Mutation Type</th>
									<th>High Confidence Mutation</th>
									<th>Cosmic ID</th>
								</tr>
							</thead>
							<tbody>
								<tr ng-repeat="gene in value.geneSubset"
									ng-class="{'blue': gene.isSpecificMutationInPanel == 'N'}">
									<td>
										<div
											ng-include="loadComponent('ctdnangscheckbox')"></div>
										<div ng-include="loadComponent('ctdnangstick')"></div>
									</td>
									<td><em>{{gene.geneName}}</em></td>
									<td ng-bind-html="trustAsHtml(gene.result)"></td>
									<td>{{toDecimal(gene.cfdnaFrequency, 1)}}</td>
									<td>{{gene.cfdnaReads}}</td>
									<td>{{toDecimal(gene.germlineFrequency ,1)}}</td>
									<td>{{gene.mutationType}}</td>
									<td>{{gene.highConfidence}}</td>
									<td ng-bind-html="trustAsHtml(gene.cosmicURL)"></td>
								</tr>
							</tbody>
						</table>

						<div id="ctdna-legend"
							ng-if="value.analysisFailed == 'false' && value.showNilResult == 'false' && value.noMutationsFound == 'false'">
							<p>
								<span class="not-specific-mutation-key blue"></span> Gene
								present in GDL NGS panel but not to the specific mutations in
								the panel.
							</p>

							<p>All mutations displayed are high confidence. High confidence variants are those called in both bioinformatics pipelines. 
							   The other variants displayed are low confidence, i.e., called in at least one pipeline.</p>
						</div>

						<div>
							<h3>Report comments</h3>
							<p>{{value.ngsLib.ngsComment}}</p>
						</div>

						<h2>NGS Library QC</h2>

						<!--<table  ng-if="value.analysisFailed == 'true' || value.showNilResult == 'true' || value.noMutationsFound == 'true'">-->
						<table >
							<thead>
								<tr>
									<th width="20%">&nbsp;</th>
									<th width="20%">Sample value</th>
									<th width="30%">Colour key</th>
									<th width="30%">&nbsp;</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td width="20%">NGS library cfDNA input</td>
									<td width="20%" ng-class="value.ngsLib.cfDNAInputColour">{{toDecimal(value.ngsLib.cfDNASample,2)}}
										ng</td>
									<td width="30%">
										<table>
											<tr>
												<td width="33%" class="green">{{value.ngsLib.cfdnaInputColourKeyGreen}}</td>
												<td width="33%" class="yellow">{{value.ngsLib.cfdnaInputColourKeyYellow}}</td>
												<td width="33%" class="red">{{value.ngsLib.cfdnaInputColourKeyRed}}</td>
											</tr>
										</table>
									</td>
									<td width="30%">&nbsp;</td>
								</tr>
								<tr>
									<td width="20%">Average read depth</td>
									<td width="20%" ng-class="value.ngsLib.averageReadDepthColour">{{value.ngsLib.averageReadDepth}}</td>
									<td width="30%">
										<table>
											<tr>
												<td width="33%" class="green">{{value.ngsLib.averageReadDepthColourKeyGreen}}</td>
												<td width="33%" class="yellow">{{value.ngsLib.averageReadDepthColourKeyYellow}}</td>
												<td width="33%" class="red">{{value.ngsLib.averageReadDepthColourKeyRed}}</td>
											</tr>
										</table>
									</td>
									<td width="30%">&nbsp;</td>
								</tr>
								<tr>
									<td width="20%">Level of detection</td>
									<td width="20%">{{value.ngsLib.levelOfDetection}}</td>
									<td width="30%">&nbsp;</td>
									<td width="20%">&nbsp;</td>
								</tr>
							</tbody>
						</table>

						<h3>
							<span style="margin: 10px 0; cursor: pointer;"
								ng-click="detailSection.showhide.toggle = !detailSection.showhide.toggle"
								class="showhide"> <i
								ng-show="detailSection.showhide.toggle"
								class="fa fa-minus-square-o" aria-hidden="true"></i> <i
								ng-show="!detailSection.showhide.toggle"
								class="fa fa-plus-square-o" aria-hidden="true"></i>
							</span> NGS Library Details
						</h3>

						<!--<div ng-if="value.analysisFailed == 'true' || value.showNilResult == 'true' || value.noMutationsFound == 'true'">-->
						<div ng-show="detailSection.showhide.toggle">
							<table >
								<tbody>
									<tr>
										<td width="20%">NGS run</td>
										<td width="80%">{{value.ngsLib.ngsRun}}</td>
									</tr>
									<tr>
										<td width="20%">NGS Sample Type</td>
										<td width="80%">{{value.ngsLib.ngsSampleType}}</td>
									</tr>
									<tr>
										<td width="20%">Bioinformatics Pipeline</td>
										<td width="80%">{{value.ngsLib.bioinfPipeline}}</td>
									</tr>
								</tbody>
							</table>
						</div>
						
						<div>
						<h3>NGS Subset at Time of Report</h3>
						<div class="outline">
							<em><span>{{genePanels[value.tumourPanelName].join(', ')}}</span></em>
						</div>
						<br>
					</div>
						
						</div>
						</uib-tab> 
						</div>
						</uib-tabset>
					</div>
				
				</div>
			</div>
			</c:if>

			<!-- ctDNA Exploratory start -->
			<c:if test = "${ctdnaexploratory =='true'}">
			<div ng-show="detailSection.ctdnaExploratory">
			    <p id="editLockMessage" ng-show="currentPatient.editLockMessage!= ''">
					{{currentPatient.editLockMessage}}&emsp;<i class="fa fa-refresh pointer" aria-hidden="true" ng-click="checkLockStatus()"></i>
				</p>
				<h1>ctDNA Exploratory Subset</h1>
				<nav class="detail-section">
					<a href="#"
						ng-repeat="key in currentPatient.exploratory.baselineList | orderBy:'date'"
						ng-class="(detailSection.ctdnaNGSSubs[key.baseline]) ? 'baseline-active': 'none'"
						ng-click="showBaseline('ctdnaNGS',key.baseline)">T{{key.baseline}}</a>
				</nav>

				<div ng-if="detailSection.exploratory.noResults.message != ''">
					<p ng-if="detailSection.exploratory.noResults.specimenDate != ''">Date of Sample:
						{{detailSection.exploratory.noResults.specimenDate}}</p>
					<p>{{detailSection.exploratory.noResults.message}}</p>
				</div>

				<div
					ng-repeat="(keyBase, valueBase) in currentPatient.exploratory.baseline"
					ng-show="detailSection.ctdnaNGSSubs[keyBase]"
					ng-init="baselineCurr = $index">
					<!-- Each BASELINE -->
					<h1>T{{keyBase}}</h1>
					<p>Date of Sample: {{valueBase.specimen.specimenDate}}</p>
					<p ng-show="valueBase.message != ''">{{valueBase.message}}</p>

					<!-- Each baseline RUN -->
					<div ng-repeat="(key, versions) in valueBase.runs" ng-show="valueBase.runs['1']['1'].geneSubset!=null">
						<h2>Run {{key}}</h2>
						<uib-tabset active="active_exploratory[keyBase][key]">
						  <div ng-repeat="(version, value) in versions">

							<uib-tab index=version heading="Version {{version}}" class="tab_header{{versions | objLength}}">
							<div ng-class="{'greybackground' : (versions | objLength)>1 && ''+(versions | objLength)!=version}">
							<div
								ng-if="value.showNilResult == 'true' || value.noMutationsFound == 'true'">
								<table >
									<thead>
										<tr>
											<th>Gene Name</th>
											<th id="result_column">Result</th>
											<th>cfDNA Frequency (%)</th>
											<th>Total cfDNA Reads</th>
											<th>Germline Frequency (%)</th>
											<th>Mutation Type</th>
											<th>High Confidence Mutation</th>
											<th>Cosmic ID</th>
										</tr>
									</thead>
									<tbody>
										<tr>
											<td>Nil</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
										</tr>
									</tbody>
								</table>
							</div>

							<div ng-if="value.analysisFailed == 'true'">
								<table >
									<thead>
										<tr>
											<th>Gene Name</th>
											<th id="result_column">Result</th>
											<th>cfDNA Frequency (%)</th>
											<th>Total cfDNA Reads</th>
											<th>Germline Frequency (%)</th>
											<th>Mutation Type</th>
											<th>High Confidence Mutation</th>
											<th>Cosmic ID URL</th>
										</tr>
									</thead>
									<tbody>
										<tr>
											<td>Failed</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
										</tr>
									</tbody>
								</table>
							</div>

							<table 
								ng-if="value.analysisFailed == 'false' && value.showNilResult == 'false' && value.noMutationsFound == 'false'">
								<thead>
									<tr>
										<th>Significant Mutations</th>
										<th>Gene Name</th>
										<th id="result_column">Result</th>
										<th>cfDNA Frequency (%)</th>
										<th>Total cfDNA Reads</th>
										<th>Germline Frequency (%)</th>
										<th>Mutation Type</th>
										<th>High Confidence Mutation</th>
										<th>Cosmic ID</th>
									</tr>
								</thead>
								<tbody>
									<tr ng-repeat="gene in value.geneSubset">
										<td>
											<div
												ng-include="loadComponent('ctdnaexpcheckbox')"></div>
											<div ng-include="loadComponent('ctdnaexptick')"></div>
										</td>
										<td><em>{{gene.geneName}}</em></td>
										<td ng-bind-html="trustAsHtml(gene.result)"></td>
										<td>{{toDecimal(gene.cfdnaFrequency, 1)}}</td>
										<td>{{gene.cfdnaReads}}</td>
										<td>{{toDecimal(gene.germlineFrequency, 1)}}</td>
										<td>{{gene.mutationType}}</td>
										<td>{{gene.highConfidence}}</td>
										<td ng-bind-html="trustAsHtml(gene.cosmicURL)"></td>
									</tr>
								</tbody>
							</table>

							<div id="ctdna-legend"
								ng-if="value.analysisFailed == 'false' && value.showNilResult == 'false' && value.noMutationsFound == 'false'">
								<p>
									<strong>All mutations displayed are high confidence.</strong>
									High confidence variants are those called in both bioinformatics pipelines. 
							   The other variants displayed are low confidence, i.e., called in at least one pipeline.
								</p>
							</div>

							<div>
								<h3>Report comments</h3>
								<p>{{value.ngsLib.exploratoryComment}}</p>
							</div>

							<h2>NGS Library QC</h2>

							<!--<table  ng-if="value.analysisFailed == 'true' || value.showNilResult == 'true' || value.noMutationsFound == 'true'">-->
							<table >
								<thead>
									<tr>
										<th width="20%">&nbsp;</th>
										<th width="20%">Sample value</th>
										<th width="30%">Colour key</th>
										<th width="30%">&nbsp;</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td width="20%">NGS library cfDNA input</td>
										<td width="20%" ng-class="value.ngsLib.cfDNAInputColour">{{toDecimal(value.ngsLib.cfDNASample,2)}}
											ng</td>
										<td width="30%">
											<table>
												<tr>
													<td width="33%" class="green">{{value.ngsLib.cfdnaInputColourKeyGreen}}</td>
													<td width="33%" class="yellow">{{value.ngsLib.cfdnaInputColourKeyYellow}}</td>
													<td width="33%" class="red">{{value.ngsLib.cfdnaInputColourKeyRed}}</td>
												</tr>
											</table>
										</td>
										<td width="30%">&nbsp;</td>
									</tr>
									<tr>
										<td width="20%">Average read depth</td>
										<td width="20%" ng-class="value.ngsLib.averageReadDepthColour">{{value.ngsLib.averageReadDepth}}</td>
										<td width="30%">
											<table>
												<tr>
													<td width="33%" class="green">{{value.ngsLib.averageReadDepthColourKeyGreen}}</td>
													<td width="33%" class="yellow">{{value.ngsLib.averageReadDepthColourKeyYellow}}</td>
													<td width="33%" class="red">{{value.ngsLib.averageReadDepthColourKeyRed}}</td>
												</tr>
											</table>
										</td>
										<td width="30%">&nbsp;</td>
									</tr>
									<tr>
										<td width="20%">Level of detection</td>
										<td width="20%">{{value.ngsLib.levelOfDetection}}</td>
										<td width="30%">&nbsp;</td>
										<td width="20%">&nbsp;</td>
									</tr>
								</tbody>
							</table>

							<h3>
								<span style="margin: 10px 0; cursor: pointer;"
									ng-click="detailSection.showhide.toggleExp = !detailSection.showhide.toggleExp"
									class="showhide"> <i
									ng-show="detailSection.showhide.toggleExp"
									class="fa fa-minus-square-o" aria-hidden="true"></i> <i
									ng-show="!detailSection.showhide.toggleExp"
									class="fa fa-plus-square-o" aria-hidden="true"></i>
								</span> NGS Library Details
							</h3>

							<!--<div ng-show="detailSection.ctdnaExploratorySubs.ngsLib" ng-if="value.analysisFailed == 'true' || value.showNilResult == 'true' || value.noMutationsFound == 'true'">-->
							<div ng-show="detailSection.showhide.toggleExp">
								<table >
									<tbody>
										<tr>
											<td width="20%">NGS run</td>
											<td width="80%">{{value.ngsLib.ngsRun}}</td>
										</tr>
										<tr>
											<td width="20%">NGS Sample Type</td>
											<td width="80%">{{value.ngsLib.ngsSampleType}}</td>
										</tr>
										<tr>
											<td width="20%">Bioinformatics Pipeline</td>
											<td width="80%">{{value.ngsLib.bioinfPipeline}}</td>
										</tr>
									</tbody>
								</table>
							</div>
							</div>
							</uib-tab>
						</div>
						</uib-tabset>
					</div>
				</div>
			</div>
			</c:if>

			<!-- PDX/CDX start -->
			<div ng-show="detailSection.pdxcdx">
				<h1>PDX/CDX</h1>

				<table >
					<tr>
						<th>PDX Created</th>
					</tr>
					<tr>
						<td ng-if="pdxcdx.pdx.length == 0">No data.</td>
					</tr>
					<tr ng-repeat="dates in pdxcdx.pdx track by $index">
						<td>{{dates}}</td>
					</tr>
				</table>

				<table >
					<tr>
						<th>CDX Created</th>
					</tr>
					<tr>
						<td ng-if="pdxcdx.cdx.length == 0">No data.</td>
					</tr>
					<tr ng-repeat="dates in pdxcdx.cdx track by $index">
						<td>{{dates}}</td>
					</tr>
				</table>
			</div>

			<!-- Additional Reports start -->
			<c:if test = "${additionalreports =='true'}">
			<div ng-show="detailSection.addReports">
				<h1>Additional Reports</h1>
				<h2>Potential Matches to Clinical Trials</h2>
				<div ng-show="'<%=trialfinderURL%>'.length>0">
					<p><a target="_blank" rel="noreferrer" class="trailfinder_highlight"
						ng-href="<%=trialfinderURL%>?{{getTrialFinderArguments()}}">Find potential matches to trials</a> </p>
					<p>(The patient&apos;s disease type, selected genes plus alteration type will be used to automatically populate the cancer trial matching tool)</p>
					<br>
				</div>
				<p ng-show="currentPatient.trialFile!=''">Potential Trials Report</p>
				<p ng-show="currentPatient.trialFile!=''"><a target="_blank"
						ng-href="rest/html/trials/{{currentPatient.trialFile}}">{{currentPatient.trialFile}}</a></p>
				<p ng-show="currentPatient.trialFile==''">A trial report has not been generated for this patient.</p> 
				<h2>QCI reports</h2>
				<div>
					<nav class="detail-section">
						<a href="#"
						ng-repeat="(timepoint, value) in currentPatient.qciFiles"
						ng-class="(detailSection.qciSubs[timepoint]) ? 'baseline-active': 'none'"
						ng-click="showQCIBaseline(timepoint)">{{timepoint}}</a>
					</nav>
					<div ng-repeat="(timepoint, value) in currentPatient.qciFiles"
					     ng-show="detailSection.qciSubs[timepoint]"
				         ng-init="baselineCurr = $index">
						<p>QCI report: <a target="_blank"
						ng-href="rest/pdf/{{value}}">{{value}}</a></p>
					</div>
					<p ng-show="(currentPatient.qciFiles | objLength)==0">No QCI report available.</p>
				</div>
				<h2>RNA Sequencing Report</h2>
				<div>
					<div ng-repeat="rnaFile in currentPatient.rnaFiles">
						<p>RNA report: <a target="_blank"
						ng-href="rest/pdf/{{rnaFile}}">{{rnaFile}}</a></p>
					</div>
					<p ng-show="(currentPatient.rnaFiles | objLength)==0">No report available.</p>
				</div>
			</div>
			</c:if>
			
			<!-- IHC section start -->
			<c:if test = "${ihc =='true'}">
			<div ng-show="detailSection.ihc">
				<h1>IHC</h1>
				<div ng-show="(currentPatient.ihc | objLength) == 0">
					<p>No tumour samples found.</p>
				</div>
				<div ng-show="(currentPatient.ihc | objLength) > 0">
				<nav class="detail-section">
					<a href="#"
						ng-repeat="(key,value) in currentPatient.ihc"
						ng-class="(detailSection.ihcSubs[key]) ? 'baseline-active': 'none'"
						ng-click="showIHCBaseline(key)">Bx{{key}}</a>
				</nav>
				<div ng-repeat="(keyBase, valueBase) in currentPatient.ihc"	
				     ng-show="detailSection.ihcSubs[keyBase]"
				     ng-init="baselineCurr = $index">
					
					<uib-tabset active="active_ihc[1][keyBase]">
						<div ng-repeat="(version, value) in valueBase">
						<uib-tab index="version" heading="Version {{version}}" class="tab_header{{valueBase | objLength}}">
						<div ng-class="{'greybackground' : (versions | objLength)>1 && ''+(versions | objLength)!=version}">
					
					<p>Date of Sample: {{value.sampleDate}}</p>
					<div ng_show="value.hasOwnProperty('message')">
						<p>{{value.message}}</p>
					</div>
					
					<div ng_show="value.hasOwnProperty('reportDate')">
						<br/>
						<h3>CD3 and CD8</h3>
						<table id="ihc_cd_table">
							<thead>
								<tr><td></td><th colspan="3" id="ihc_table_header">Number of positive cells/mm&sup2;</th></tr>
								<tr><td id="ihc_type"></td><th id="ihc_area">Central tumour</th><th id="ihc_intratumoural">Intratumoural</th><th id="ihc_intrastromal">Intrastromal</th></tr>
							</thead>
							<tbody>
								<tr>
									<th>CD3</th>
									<td>{{value.cd3_total_tissue_area}}</td>
									<td>{{value.cd3_instratumoural}}</td>
									<td>{{value.cd3_instrastromal}}</td>
								</tr>
								<tr>
									<th>CD8</th>
									<td>{{value.cd8_total_tissue_area}}</td>
									<td>{{value.cd8_instratumoural}}</td>
									<td>{{value.cd8_instrastromal}}</td>
								</tr>
							</tbody>
						</table>
						
						<table>
							<thead>
								<tr><td><h3>CD3 Expression</h3></td><td><h3>CD8 Expression</h3></td></tr>
								<tr>
									<td><img ng-show="value.hasOwnProperty('cd3_expression')" alt="CD3 Expression" src="rest/image/ihc/{{value.cd3_expression}}">
									<span ng-show="!value.hasOwnProperty('cd3_expression')">No image available</span></td>
									<td><img ng-show="value.hasOwnProperty('cd8_expression')" alt="CD3 Expression" src="rest/image/ihc/{{value.cd8_expression}}">
									<span ng-show="!value.hasOwnProperty('cd8_expression')">No image available</span></td>
								</tr>
							</thead>
						</table>
						<br/>
						<h3>PD-L1</h3>
						<table id=ihc_pdl_table>
							<thead>
								<tr><th colspan="4" id="ihc_pld_header">Tumour Proportion Score (TPS)</th></tr>
							</thead>
							<tbody>
								<tr>
									<td class="ihc_pld_column">&lt; 1 &#37;</td>
									<td class="ihc_pld_column">&#8805; 1&#37; to &lt; 50&#37;</td>
									<td class="ihc_pld_column">&#8805; 50&#37;</td>
									<td class="ihc_pld_column">No result</td> 
								</tr>
								<tr>
									<td><i ng-show="value.pdl1_tps=='0-1'" class="fa fa-check"></i></td>
									<td><i ng-show="value.pdl1_tps=='1-50'" class="fa fa-check"></i></td>
									<td><i ng-show="value.pdl1_tps=='50-100'" class="fa fa-check"></i></td>
									<td><i ng-show="value.pdl1_tps=='No result'" class="fa fa-check"></i></td>
								</tr>
							</tbody>
						</table>
						<br/>
						<p>Estimated result: {{value.estimated_results}}</p>
						<br/>
						<div>
							<img ng-show="value.hasOwnProperty('pdl1_expression')" alt="PD-L1 Expression" src="rest/image/ihc/{{value.pdl1_expression}}">
							<p ng-show="!value.hasOwnProperty('pdl1_expression')">No PD-L1 image available</p>
						</div>
						<br/>
						<h3>Comments</h3>
						<p class="linebreaks">{{value.comments}}</p>
					</div>
					</div>
					</uib-tab>
					</div>
					</uib-tabset>
				</div>
				</div>
			
			</div>
			</c:if>

			<!-- Meeting Outcome start -->
			<div ng-show="detailSection.meeting">
				<p id="editLockMessage" ng-show="currentPatient.editLockMessage!= ''">
					{{currentPatient.editLockMessage}}&emsp;<i class="fa fa-refresh pointer" aria-hidden="true" ng-click="checkLockStatus()"></i>
				</p>
				<h1>Meeting Outcome</h1>

				<p id="pii-warning">Please do not enter any personally
					identifiable information in the meeting outcome.</p>

				<p ng-show="meeting.message.length > 0" id="meeting-outcome-updated">
					<i class="fa fa-check" aria-hidden="true"></i>{{meeting.message}}
				</p>

				<h2>Genomic Alterations</h2>
				<table>
					<thead>
						<tr>
							<th ng-click="defineMOMutationsOrder('gene')">Gene <span ng-show="MOView.orderByField == 'gene'"> <i
					ng-show="MOView.order==1" class="fa fa-sort-asc"
					aria-hidden="true"></i> <i ng-show="MOView.order==2"
					class="fa fa-sort-desc" aria-hidden="true"></i>
			</span><i ng-show="MOView.order==0" class="fa fa-sort" aria-hidden="true"></i></th>
							<th ng-click="defineMOMutationsOrder('description')">Description <span ng-show="MOView.orderByField == 'description'"> <i
					ng-show="MOView.order==1" class="fa fa-sort-asc"
					aria-hidden="true"></i> <i ng-show="MOView.order==2"
					class="fa fa-sort-desc" aria-hidden="true"></i>
			</span><i ng-show="MOView.order==0" class="fa fa-sort" aria-hidden="true"></i></th>
							<th ng-click="defineMOMutationsOrder('type')">Type <span ng-show="MOView.orderByField == 'type'"> <i
					ng-show="MOView.order==1" class="fa fa-sort-asc"
					aria-hidden="true"></i> <i ng-show="MOView.order==2"
					class="fa fa-sort-desc" aria-hidden="true"></i>
			</span><i ng-show="MOView.order==0" class="fa fa-sort" aria-hidden="true"></i></th>
							<th ng-click="defineMOMutationsOrder('source')">Source <span ng-show="MOView.orderByField == 'source'"> <i
					ng-show="MOView.order==1" class="fa fa-sort-asc"
					aria-hidden="true"></i> <i ng-show="MOView.order==2"
					class="fa fa-sort-desc" aria-hidden="true"></i>
			</span><i ng-show="MOView.order==0" class="fa fa-sort" aria-hidden="true"></i></th>
							<th ng-click="defineMOMutationsOrder('timepoint')">Timepoint <span ng-show="MOView.orderByField[0]=='timepoint'"> <i
					ng-show="MOView.order==1" class="fa fa-sort-asc"
					aria-hidden="true"></i> <i ng-show="MOView.order==2"
					class="fa fa-sort-desc" aria-hidden="true"></i>
			</span><i ng-show="MOView.order==0" class="fa fa-sort" aria-hidden="true"></i></th>
						</tr>
					</thead>
					<tbody>
						<tr ng-repeat="mutation in currentPatient.significantMutations.summery | unique3 : 'gene':'description':'source' | orderBy:MOView.orderByField:MOView.order>1" 
							ng-class="{'grey':mutation.newlySelected=='true'}">
							<td>{{mutation.gene}}</td>
							<td ng-bind-html="trustAsHtml(mutation.description)"></td>
							<td>{{mutation.type}}</td>
							<td>{{mutation.source.replace("_"," ");}}</td>
							<td><span ng-repeat="genes in currentPatient.significantMutations.summery | unique4 : 'gene':'description':'source':'timepoint' | orderBy:'timepoint'"
							          ng-show="genes.gene==mutation.gene && genes.description==mutation.description && genes.source==mutation.source">{{genes.timepoint}} </span><td>
						</tr>
						<tr ng-show="currentPatient.significantMutations.summery.length==0">
							<td>Nil</td>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
						</tr>
	
					</tbody>
				
				
				</table>
				<div ng-show="currentPatient.significantMutations.summery.some(checkNewlySelected)" id="mo-ctdna-legend">
					<p>
						<span class="newly-selected-key grey"></span> Variant was not selected in the previous run
					</p>
				</div>
				
				<br />
				<h3>Meeting Outcome</h3>
				<div id="meeting-outcome-buttons"
					ng-include="loadComponent('meetingoutcome')"></div>

				<table >
					<tr>
						<th style="min-width: 100px">Date discussed</th>
						<th width="24%">Outcome</th>
						<th width="30%">Notes</th>
						<th width="16%">Short Variants</th>
						<th width="18%">Rearrangements</th>
						<th width="12%">CNAs</th>
						<th style="min-width: 100px">Last Updated</th>
						<th style="min-width: 100px">Sent to Printer by Gatekeeper</th>
						<th width="5%">&nbsp;</th>
					</tr>

					<tr ng-show="meeting.new" id="meeting-add">
						<td valign="top"><input ng-model="meeting.add.date"
							type="text" id="datepicker"></td>
						<td valign="top"><select ng-model="meeting.add.outcome"
							multiple>
								<option value="Further Standard of Care Treatment">Further Standard of Care Treatment</option>
								<option value="Clinical Trial Recommended Locally">Clinical
									Trial Recommended Locally</option>
								<option value="Clinical Trial Recommended Elsewhere">Clinical
									Trial Recommended Elsewhere</option>
								<option value="Actionable - Explore Clinical Trial Options">Actionable
									- Explore Clinical Trial Options</option>
								<option value="Actionable But No Clinical Trial Available">Actionable
									But No Clinical Trial Available</option>
								<option value="Further Analysis / Information Required">Further
									Analysis / Information Required</option>
								<option value="Further Translational Research">Further
									Translational Research</option>
								<option value="Patient Deteriorated - No Longer Suitable">Patient
									Deteriorated - No Longer Suitable</option>
								<option value="Nil Actionable">Nil Actionable</option>
								<option value="Relisted For Next MTB">Relisted For Next
									MTB</option>
								<option value="Not Referred to MTB">Not Referred to MTB</option>
						</select></td>
						<td valign="top">
							<textarea ng-model="meeting.add.notes"></textarea>
						</td>
						<td id="significant-mutations" valign="top">
							<div
								ng-repeat="mutation in currentPatient.significantMutations.meetingOutcomeSummarySV  | unique_list track by $index">
								{{mutation}}</div>
						</td>
						<td>
							<div
								ng-repeat="mutation in currentPatient.significantMutations.meetingOutcomeSummaryR  | unique_list track by $index">
								{{mutation}}</div>
						</td>
						<td>
							<div
								ng-repeat="mutation in currentPatient.significantMutations.meetingOutcomeSummaryCNA  | unique_list track by $index">
								{{mutation}}</div>
						</td>
						<td valign="top">-</td>
						<td valign="top">&nbsp;</td>
						<td valign="top">&nbsp;</td>
					</tr>

					<tr ng-repeat="meeting in meeting.outcome track by $index"
						id="meeting-list">
						<td>{{meeting.meetingDate}}</td>
						<td id="outcome{{$index}}" width="15%" min-width="200px">
						  <span id="editoutcome{{$index}}" ng-show="!showedit(outcome{{$index}}edit)">{{meeting.outcome}}<span class="icon-right" ng-include="loadComponent('editmeetingoutcome')"></span></span>
						  <span id="selectoutcome{{$index}}" ng-show="showedit(outcome{{$index}}edit)">
						    <select ng-model="meeting.edit.outcome" multiple>
						    	<option value="Further Standard of Care Treatment">Further Standard of Care Treatment</option>
								<option value="Clinical Trial Recommended Locally">Clinical
									Trial Recommended Locally</option>
								<option value="Clinical Trial Recommended Elsewhere">Clinical
									Trial Recommended Elsewhere</option>
								<option value="Actionable - Explore Clinical Trial Options">Actionable
									- Explore Clinical Trial Options</option>
								<option value="Actionable But No Clinical Trial Available">Actionable
									But No Clinical Trial Available</option>
								<option value="Further Analysis / Information Required">Further
									Analysis / Information Required</option>
								<option value="Further Translational Research">Further
									Translational Research</option>
								<option value="Patient Deteriorated - No Longer Suitable">Patient
									Deteriorated - No Longer Suitable</option>
								<option value="Nil Actionable">Nil Actionable</option>
								<option value="Relisted For Next MTB">Relisted For Next
									MTB</option>
								<option value="Not Referred to MTB">Not Referred to MTB</option>
							</select>
							<i class="fa fa-times icon-right" aria-hidden="true" ng-click="$parent.cancelEditMeetingOutcome($event,$index)"></i><i class="fa fa-check icon-right" aria-hidden="true" ng-click="$parent.saveEditMeetingOutcome($event,$index)"></i>
						  </span>
						</td>
						<td id="notes{{$index}}">
						  <span id="editnotes{{$index}}" ng-show="!showedit(notes{{$index}}edit)">{{meeting.notes}}<span class="icon-right" ng-include="loadComponent('editmeetingoutcome')"></span></span>
						  <span id="textnotes{{$index}}" ng-show="showedit(notes{{$index}}edit)">
						    <textarea ng-model="meeting.edit.notes"></textarea>
						    <i class="fa fa-times icon-right" aria-hidden="true" ng-click="$parent.cancelEditMeetingOutcome($event,$index)"></i><i class="fa fa-check icon-right" aria-hidden="true" ng-click="$parent.saveEditMeetingOutcome($event,$index)"></i>
						  </span>
						</td>
						<td>
						    <!-- <div class="tooltip"> -->
							<div ng-repeat="mutation in [].concat(meeting.ctDNA).concat(meeting.tumourNGS).concat((meeting.fmBlood.hasOwnProperty('FMBloodSV'))?meeting.fmBlood.FMBloodSV:[])
							 .concat((meeting.fmTumour.hasOwnProperty('FMTumourSV'))?meeting.fmTumour.FMTumourSV:[]).concat(getGenericGenomicMOPart(meeting, 'SV')) | unique : 'geneName':'result' track by $index">
								<div>{{mutation.geneName}} {{mutation.result}}</div>
							</div>
							<!-- <span class="tooltiptext">
							<span   ng-repeat="baseline in [].concat(meeting.ctDNA).concat(meeting.tumourNGS) | unique : 'baseline' track by $index"
									ng-show="(currentPatient.exploratory.baseline | objLength)>1 ||
									(currentPatient.ngsSubset.baseline | objLength)>1 ||
									(currentPatient.tumourNGS.baseline | objLength)>1" >{{baseline.baseline}}</span></span>
							</div>-->
							</td>
							<td>
							<!-- 
							MY_SCOPE.meeting.outcome[0].genericGenomic[Object.keys(MY_SCOPE.meeting.outcome[0].genericGenomic)][Object.keys(MY_SCOPE.meeting.outcome[0].genericGenomic[Object.keys(MY_SCOPE.meeting.outcome[0].genericGenomic)]).filter(function(word) { return word.endsWith('R'); })]
meeting.genericGenomic[Object.keys(meeting.genericGenomic)][Object.keys(meeting.genericGenomic[Object.keys(meeting.genericGenomic)]).filter(function(word) { return word?word.endsWith('R'):''})]
							 -->
							<div ng-repeat="r in [].concat((meeting.fmBlood.hasOwnProperty('FMBloodR'))?meeting.fmBlood.FMBloodR:[])
								.concat((meeting.fmTumour.hasOwnProperty('FMTumourR'))?meeting.fmTumour.FMTumourR:[])
								.concat(getGenericGenomicMOPart(meeting, 'R')) | unique : 'gene1' : 'gene2' track by $index">
								<div>{{r.gene1}} {{r.gene2}} {{r.description}}</div>
							</div>
							</td>
							<td>
							<div ng-repeat="cna in [].concat((meeting.fmBlood.hasOwnProperty('FMBloodCNA'))?meeting.fmBlood.FMBloodCNA:[])
								.concat((meeting.fmTumour.hasOwnProperty('FMTumourCNA'))?meeting.fmTumour.FMTumourCNA:[]).concat(getGenericGenomicMOPart(meeting, 'CNA')) | unique : 'geneName' : 'type' track by $index">
								<div>{{cna.geneName}} {{cna.type}}</div>
							</div>
							
							<!--  
							angular.extend({},meeting.ctDNA,meeting.tumourNGS)
							<div ng-repeat="mutation in meeting.geneSummary | unique_list track by $index ">
								{{mutation}}</div>-->
						</td>
						<td>{{meeting.lastUpdated}}</td>
						<td>{{meeting.lastPrinted}}</td>
						<td ng-click="printMeetingOutcome(meeting, $index)"
							class="pointer"><i class="fa fa-print" aria-hidden="true" ng-style="meeting.needPrinting==true ? {'background-color':'red'}:{}"></i></td>
					</tr>

					<tr ng-if="meeting.outcome.length == 0">
						<td>No data.</td>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
					</tr>
				</table>
			</div>
		</div>
	</div>

	<!-- Report Extraction table -->
	<div class="page" ng-show="page.status" id="system-status">
		<h1>Report Extraction Status</h1>
		<div ng-include="'rest/component/reportextraction'"></div>
	</div>

	<!-- Add Gene Panel page 
	<div class="page" ng-show="page.genepanel" id="genePanel">
		<h1>Add NGS Panel</h1>
		<div ng-include="'rest/component/genepanel'"></div>
	</div>-->
	
	<!-- Admin page -->
	<div class="page" ng-show="page.admin" id="admin">
		<h1>Admin</h1>
		<div ng-include="'rest/component/admin'"></div>
	</div>

	<div id="footer">
		<p id="support-email">
			For application support please contact: <a
				href="mailto:<%=support%>?subject=eTarget Support Request"><%=support%></a>
		</p>
		
		<nav>
				
			<c:forEach var="imageAltTag" items="${imageAlt}" varStatus="loop">
				<a href="#"> <img src="${imageURL[loop.index]}"	alt="${imageAltTag}" /> </a>
			</c:forEach>
			
		</nav>
		<!-- <p>
			
			<c:forEach var="tag" items="${tabName}" varStatus="loop">
				${tag}<br>
			</c:forEach>
		</p> -->
		
	</div>

	<div id="section-to-print">
		<!-- Meeting outcome print section -->
		<h1>MTB Meeting Outcome</h1>
		<table>
			<tr>
				<td>Patient ID</td>
				<td>{{print.targetID}}</td>
			</tr>
			<tr>
				<td>Consultant</td>
				<td>{{print.consultant}}</td>
			</tr>
			<tr>
				<td>ctDNA Date of Sample</td>
				<td>{{print.ctDNASampleDate}}</td>
			</tr>
			<tr>
				<td>Tumour Date of Sample</td>
				<td>{{print.tumourSampleDate}}</td>
			</tr>
			<tr>
				<td>Date of MTB</td>
				<td>{{print.mtbDate}}</td>
			</tr>
			<tr ng-show="print.lastUpdated!=''">
				<td>Last Updated</td>
				<td>{{print.lastUpdated}} by {{print.updatedBy}}</td>
			</tr>
			<tr>
				<td>Date Printed</td>
				<td>{{print.datePrinted}}</td>
			</tr>
		</table>

		<h2>Genomic Alterations</h2>
		<table border="1">
			<thead>
				<tr>
					<th>Gene</th>
					<th>Description</th>
					<th>Type</th>
					<th>Source</th>
					<th>Timepoint</th>
				</tr>
			</thead>
			<tbody>
				<tr ng-repeat="mutation in print.summary | unique3 : 'gene':'description':'source' | orderBy:['source','gene']">
					<td>{{mutation.gene}}</td>
					<td ng-bind-html="trustAsHtml(mutation.description)"></td>
					<td>{{mutation.type}}</td>
					<td>{{mutation.source}}</td>
					<td><span ng-repeat="genes in print.summary | unique4 : 'gene':'description':'source':'timepoint' | orderBy:'timepoint'" 
					          ng-show="genes.gene==mutation.gene && genes.description==mutation.description && genes.source==mutation.source">{{genes.timepoint}} </span></td>
				</tr>
				<tr ng-show="print.summary.length==0">
					<td>Nil</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
				</tr>
	
			</tbody>
		</table>
		
		<br />
		
		<h2>Meeting Outcome</h2>

		<table>
			<tr>
				<td>Meeting Outcome</td>
				<td style="white-space: pre-line">{{print.outcome}}</td>
			</tr>
			<tr>
				<td>Notes</td>
				<td>{{print.notes}}</td>
			</tr>
		</table>
		
		<div ng-show="meeting.outcome.length>1 && (print.index+1)!=meeting.outcome.length">
		<br />
		<h2>Previous Meeting Outcomes</h2>
		
		<table>
			<tr>
				<th>Meeting Date</th>
				<th>Meeting Outcome</th>
				<th>Notes</th>
			</tr>
			<tr ng-repeat="ameeting in meeting.outcome | limitTo:meeting.outcome.length:print.index+1 track by $index ">
				<td width="15%">{{ameeting.meetingDate}}</td>
				<td width="40%" style="white-space: pre-line">{{ameeting.outcome}}</td>
				<td width="40%">{{ameeting.notes}}</td>
			</tr>
		</table>
		</div>
		<hr>

		<p>
			<strong>Disclaimer: This report has been generated using the
				beta version of eTARGET. These data are not validated. Please check
				against the hard-copy results.</strong>
		</p>

		<hr>

		<div>
			<h4>Signed by:</h4>
			<h4>Date:</h4>
		</div>
	</div>

	<div id="notice-overlay"></div>
	<div id="notice">
		<h1>Governance Statement</h1>
		<p>This is a beta version of eTARGET. The data presented must be
			validated against the genomic paper reports until further notice.</p>
		<p>The data presented by eTARGET should not be solely used to make
			clinical decisions.</p>
		<button onclick="closeNotice()">I agree</button>
	</div>
</body>
</html>
