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
<%@ page import="java.util.*" %>

<h3 style="text-align:center">eTarget logging out...</h3>

<%
Cookie cookie = null;
Cookie[] cookies = null;
cookies = request.getCookies();
if(cookies != null) {
  for(int i = 0; i<cookies.length; i++) {
    cookie = cookies[i];
    cookie.setMaxAge(0);
    response.addCookie(cookie);

    //out.println("Deleted cookie: " + cookie.getName());
    //out.println("Name : " + cookie.getName());
    //out.println("Value: " + cookie.getValue());
  }
}
String url = request.getRequestURL().toString();
String baseURL = url.substring(0, url.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
baseURL = baseURL.replace("http", "https");
response.sendRedirect("https://login.windows.net/common/oauth2/logout?post_logout_redirect_uri="+baseURL);
%>
