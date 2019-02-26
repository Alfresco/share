<%--
  #%L
  Alfresco Share WAR
  %%
  Copyright (C) 2005 - 2016 Alfresco Software Limited
  %%
  This file is part of the Alfresco software. 
  If the software was purchased under a paid Alfresco license, the terms of 
  the paid license agreement will prevail.  Otherwise, the software is 
  provided under the following open source license terms:
  
  Alfresco is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  Alfresco is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.
  
  You should have received a copy of the GNU Lesser General Public License
  along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
  #L%
  --%>
<%@ page import="org.alfresco.web.site.*" %>
<%@ page import="org.springframework.extensions.surf.*" %>
<%@ page import="org.springframework.extensions.surf.site.*" %>
<%@ page import="org.springframework.extensions.surf.util.*" %>
<%@ page import="java.util.*" %>

<%@ page import="org.owasp.esapi.ESAPI" %>
<%
   // retrieve user name from the session
   String userid = (String)session.getAttribute(SlingshotUserFactory.SESSION_ATTRIBUTE_KEY_USER_ID);

   // MNT-20202: neutralize redirect url that contains user input
   // LM_2019-01-30
   userid = UserInputValidator.validateRedirectUrl(userid);
   
   // test user dashboard page exists?
   RequestContext context = (RequestContext)request.getAttribute(RequestContext.ATTR_REQUEST_CONTEXT);
   if (!context.getObjectService().hasPage("user/" + userid + "/dashboard"))
   {
      // no user dashboard page found! create initial dashboard for this user...
      Map<String, String> tokens = new HashMap<String, String>();
      tokens.put("userid", userid);
      FrameworkUtil.getServiceRegistry().getPresetsManager().constructPreset("user-dashboard", tokens);
   }
   
   // redirect to site or user dashboard as appropriate
   String siteName = request.getParameter("site");
   if (siteName == null || siteName.length() == 0)
   {
      // Get and forward to user's home page
      SlingshotUserFactory slingshotUserFactory = 
              (SlingshotUserFactory) FrameworkUtil.getServiceRegistry().getUserFactory();
      String userHomePage = slingshotUserFactory.getUserHomePage(context, userid);
      response.sendRedirect(request.getContextPath() + userHomePage);
   }
   else
   {
      // MNT-20202: neutralize redirect url that contains user input
      // LM_2019-01-30
      siteName = UserInputValidator.validateRedirectUrl(siteName);
      // forward to site specific dashboard page
      response.sendRedirect(request.getContextPath() + "/page/site/" + URLEncoder.encode(siteName));
   }
%>