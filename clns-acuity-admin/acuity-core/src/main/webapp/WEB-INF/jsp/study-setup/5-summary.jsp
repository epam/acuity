<%@page import="com.acuity.visualisations.web.controller.HelpController" %>
<%--
  ~ Copyright 2021 The University of Manchester
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<script type="text/javascript" src="js/study-setup/5-summary.js"></script>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<table border="0" id="summaryStudyTable" class="summaryTable">
    <tr>
        <td colspan="2" valign="top">
            <div style="margin-bottom:5px;text-align: center;">
                <label class="sum-header">Dataset Summary</label> <a href="#" class="help"
                                                                     title='<%= HelpController.getHelpText("5-study-setup-summary", "studySummary") %>'>?</a>
            </div>
        </td>
    </tr>
    <tr>
        <td style="width:35%;" valign="top">
            <table border="0" class="summaryInnerTable">
                <tr>
                    <td class="tdf">Dataset name:</td>
                    <td id="stdName"></td>
                </tr>
                <tr>
                    <td class="tdf">Dataset ID:</td>
                    <td id="stdId"></td>
                </tr>
                <tr>
                    <td class="tdf">Clinical study name:</td>
                    <td id="csName"></td>
                </tr>
                <tr>
                    <td class="tdf">Clinical study ID:</td>
                    <td id="csId"></td>
                </tr>
                <tr>
                    <td class="tdf">Blinded:</td>
                    <td id="blinding"></td>
                </tr>
                <tr>
                    <td class="tdf">Randomisation:</td>
                    <td id="randomisation"></td>
                </tr>
                <tr>
                    <td class="tdf">Regularity:</td>
                    <td id="regularity"></td>
                </tr>
            </table>
        </td>
        <td style="width:65%;" valign="top">
            <table border="0" class="summaryInnerTable" style="width:100%;">
                <tr>
                    <td class="tdf" valign="top" style="white-space:nowrap;width:1%;">Study comment:</td>
                    <td id="summaryStudyComment" style="word-break:break-word;"></td>
                </tr>
            </table>
        </td>
    </tr>

    <tr>
        <td colspan="2" style="vertical-align:top;">
            <div class="contentTable">
                <div style="margin-bottom:5px;">
                    <label class="sum-header">Data Mappings</label>
                    <a href="javascript:void(0)" class="editLink" id="studyLinkToMappings">view/edit</a>
                    <a href="#" class="help"
                       title='<%= HelpController.getHelpText("5-study-setup-summary", "dataMappings") %>'>?</a>
                </div>
                <table id="dataMappingsStudyTable" style="width:100%"></table>
            </div>
        </td>
    </tr>
    <tr>
        <td colspan="2">
            <div style="width:100%;" class="contentTable">
                <div style="margin-bottom:5px;">
                    <label class="sum-header">Alternative Subject Groupings</label> <a href="#" class="help"
                                                                                       title='<%= HelpController.getHelpText("5-study-setup-summary", "altSubjectGroupings") %>'>?</a>
                    <a href="javascript:void(0)" class="editLink" id="studyLinkToGroupings">view/edit</a>
                </div>
                <table id="subjectGroupingsTable"></table>
            </div>
        </td>
    </tr>
</table>

<div class="bottomSummaryBlock">
    <table border="0" class="dataTable">
        <tr>
            <td>
                <div class="contentTable">
                    <div style="margin-bottom:5px;">
                        <label class="sum-header">History</label> <a href="#" class="help"
                                                                     title='<%= HelpController.getHelpText("5-study-setup-summary", "history") %>'>?</a>
                    </div>
                    <table id="sumHistoryTable"></table>
                </div>
            </td>
        </tr>
    </table>
</div>
