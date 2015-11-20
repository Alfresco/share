<%--
 * Copyright (C) 2005-2016 Alfresco Software Limited.
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
<%@ page isErrorPage="true" pageEncoding="UTF-8"%>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
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
   ResourceBundle messages = ResourceBundle.getBundle("alfresco/messages/slingshot", request.getLocale());
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
    background-color: #0C79BF;
    padding: 0.4em 10px;
    min-height: 2em;
    cursor: pointer;
    border-radius: 3px;
    border-width: 1px 0;
    border-style: solid;
    border-color: #0C79BF;
    text-decoration: none;
}
.alf-primary-button:hover
{
    background-color: #135FA3;
}
.alf-primary-button:active
{
    background-color: #125380;
}

.alf-error-footer
{
    margin-top: 10%;
    text-align: center;
    color: #A6A6A6;
}
   </style>
   <title><%= messages.getString("page.error.500.title")%></title>
</head>
<body>
   <div class="alf-error-bg">
       <img src="${pageContext.request.contextPath}/res/modules/images/about-bg-vanilla.png" />
   </div>
   <div class="alf-error-logo">
       <img src="${pageContext.request.contextPath}/res/themes/default/images/app-logo.png" />
    </div>
   <div class="alf-error-wrapper">
      <div class="alf-error-header"><%= messages.getString("page.error.500.header")%></div>
      <div class="alf-error-detail">
	        <%= messages.getString("page.error.500.detail")%>
            <div class="alf-error-nav">
                <a class="alf-primary-button" href="${pageContext.request.contextPath}<%=dashboardPath%>"><%= messages.getString("page.error.500.nav.dashboard")%></a>
            </div>
      </div>
      <div class="alf-error-footer">
        <%= messages.getString("page.error.500.footer")%>
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