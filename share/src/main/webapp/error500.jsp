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
<%@ page import="org.alfresco.web.site.*" %>
<%@ page import="org.springframework.extensions.webscripts.ui.common.StringUtils" %>
<%@ page import="org.apache.commons.logging.LogFactory" %>
<%
   String dashboardPath = "";
   // try retrieving user name from the session
   if (session != null)
   {
       String userid = (String)session.getAttribute(SlingshotUserFactory.SESSION_ATTRIBUTE_KEY_USER_ID);
       if (userid != null)
       {
           dashboardPath = "/page/user/" + userid + "/dashboard";
       }
   }
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
   <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
   <style type="text/css">
body
{
   font: 13px/1.231 Open Sans,arial,helvetica,clean,sans-serif;
   color: #333333;
}

body,div,p
{
   margin: 0;
   padding: 0;
}

.alf-error-wrapper
{
    width: 600px;
    margin: 0 auto;
    margin-top: 10%;
    position: relative;
    z-index: 2;
}

.alf-error-logo
{
    margin: 5px 20px;
}

.alf-error-bg
{
    float: right;
    width: 501px;
}
.alf-error-bg img
{
    position: absolute;
    top: 0;
    clip: rect(0px,501px,250px,250px);
}

.alf-error-header
{
    font-size: 1.8em;
    color: #4F4F57;
    clear: both;
}

div.alf-error-detail
{
   display: inline-block;
   margin-top: 2em;
}

div.alf-error-detail p
{
   padding: 0.7em 0;
   font-size: 1.1em;
}

.alf-error-nav
{
    margin: 3em 0;
    text-align: center;
}

.alf-primary-button
{
    color: white;
    background-color: #0082c8;
    padding: 0.4em 10px;
    min-height: 2em;
    cursor: pointer;
    border-radius: 3px;
    border-width: 1px 0;
    border-style: solid;
    border-color: #0082c8;
    text-decoration: none;
}
.alf-primary-button:hover
{
    background-color: #006ca6;
}
.alf-primary-button:active
{
    background-color: #005684;
}

.alf-error-footer
{
    margin-top: 10%;
    text-align: center;
    color: #A6A6A6;
}
   </style>
   <title>Alfresco Share &raquo; System Error</title>
</head>
<body>
   <div class="alf-error-bg">
       <img src="${pageContext.request.contextPath}/res/modules/images/about-bg-vanilla.png" />
   </div>
   <div class="alf-error-logo">
       <img src="${pageContext.request.contextPath}/res/themes/default/images/app-logo.png" />
    </div>
   <div class="alf-error-wrapper">
      <div class="alf-error-header">Something's wrong with this page...</div>
      <div class="alf-error-detail">
	        <p>We may have hit an error or something might have been removed or deleted, so check that the URL is correct.</p>
            <p>Alternatively you might not have permission to view the page (it could be on a private site) 
            or there could have been an internal error. Try checking with your Alfresco administrator.</p>
            <p>If you're trying to get to your home page and it's no longer available you 
            should change it by clicking your name on the Alfresco toolbar.</p>
            <div class="alf-error-nav">
                <a class="alf-primary-button" href="${pageContext.request.contextPath}<%=dashboardPath%>">Back to My Dashboard</a>
            </div>
      </div>
      <div class="alf-error-footer">
        <a href="http://www.alfresco.com">Alfresco Software</a> Inc. &copy; 2005-2015 All rights reserved.
      </div>
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
</body>
</html>