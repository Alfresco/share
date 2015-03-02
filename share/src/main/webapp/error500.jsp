<%--
 * Copyright (C) 2005-2015 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page isErrorPage="true" %>
<%@ page import="java.io.*" %>
<%@ page import="org.springframework.extensions.webscripts.ui.common.StringUtils" %>
<%@ page import="org.apache.commons.logging.LogFactory" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
   <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
   <style type="text/css">
body
{
   font: 13px/1.231 arial,helvetica,clean,sans-serif;
   color: #000000;
}

body,div,p
{
   margin: 0;
   padding: 0;
}

div
{
	text-align: center;
}

ul
{
   text-align: left;
}

li
{
   padding: 0.2em;
}

div.panel
{
   display: inline-block;
}
   </style>
   <title>Alfresco Share &raquo; System Error</title>
</head>
<body>
   <div>
      <br/>
      <img src="${pageContext.request.contextPath}/res/themes/default/images/app-logo.png">
      <br/>
      <br/>
      <p style="font-size:150%">A server error has occurred.</p>
      <br/>
      <p>There are a number of reasons why this could have happened:</p>
      <div class="panel">
         <ul>
            <li>You have attempted to access a page that does not exist - check the URL in the address bar.</li>
            <li>You have attempted to access a page that is not accessible to you, such as a private Site dashboard.</li>
            <li>A valid page has been requested but the server was unable to render it due to an internal error - contact your administrator.</li>
         <ul>
      </div>
      <br/>
      <a href="${pageContext.request.contextPath}">Return to your dashboard page</a>
      <br/>
      <br/>
      <br/>
      <a href="http://www.alfresco.com">Alfresco Software</a> Inc. &copy; 2005-2015 All rights reserved.
   </div>
   <div>
<%
out.println("<!--");
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
if (exception != null)
{
	exception.printStackTrace(pw);
	out.print(StringUtils.encode(sw.toString()));
	sw.close();
	pw.close();
	out.println("-->");
	LogFactory.getLog("org.alfresco.web.site").error(exception, exception.getCause());
}
%>
   </div>
</div>
</body>
</html>