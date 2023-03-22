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

<%-- API imports --%>
<%@ page import="org.digitalecmt.etarget.API" %>

<%-- Set the content type header with the JSP directive --%>
<%@ page contentType="text/html" %>

<%
// Get the current logged in user's data from Azure headers
String loggedInUserID = request.getHeader("x-ms-client-principal-name");
String component = request.getParameter("component");
String personID = request.getParameter("personID");

API api = new API();
String requestedData = api.fetchHTMLComponent(component, loggedInUserID, personID);

// Send the data
response.setContentType("text/html");
response.setHeader("Content-Disposition", "inline");
response.setCharacterEncoding("UTF-8");
response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
response.setHeader("Expires", "0"); // Proxies.
response.getWriter().write(requestedData);
%>
