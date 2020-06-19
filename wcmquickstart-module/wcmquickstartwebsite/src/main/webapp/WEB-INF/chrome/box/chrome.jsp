<%--

    Copyright 2005 - 2020 Alfresco Software Limited.

    This file is part of the Alfresco software.
    If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
    Otherwise, the software is provided under the following open source license terms:

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

--%>
<%@ page import="org.springframework.extensions.surf.*" %>
<%@ page import="org.springframework.extensions.surf.types.*"%>
<%@ page import="org.springframework.extensions.surf.render.*" %>
<%@ page buffer="0kb" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="surf" uri="http://www.springframework.org/tags/surf" %>
<%
	RenderContext context = (RenderContext) request.getAttribute("renderContext");
	
	// get the component	
	String componentId = (String) context.getValue(WebFrameworkConstants.RENDER_DATA_COMPONENT_ID);
	Component component = context.getObjectService().getComponent(componentId);
	
	String servletPath = request.getContextPath();

	String htmlBindingId = (String) context.getValue(WebFrameworkConstants.RENDER_DATA_HTMLID);
	
	String title = null;
	if (component != null && component.getTitle() != null)
	{
		title = component.getTitle();
	}
	else
	{
		title = "Untitled Component";
	}
%>
<style type="text/css">
<!--
#chrome-content-<%=htmlBindingId%>
{
	background-color: #ffffff;
	border: solid 1px #cccccc;
}

#chrome-header-<%=htmlBindingId%>
{
	background-color: #ffffff;
	border: solid 1px #cccccc;
	color: #014a67;
	font-weight: bold;
	padding: 3px;
	padding-left: 5px;
	background-image: url(<%=servletPath%>/images/chrome/box/box_chrome_header_bg.gif);
	background-repeat: repeat-x;
	background-position: bottom;
	border-bottom: 0px;
}
-->
</style>

<table width="100%" cellpadding="0" cellspacing="0"> 
	<tr>
		<td id="chrome-header-<%=htmlBindingId%>" align="left" valign="top">
			<%=title%>
		</td>
	</tr>
	<tr>
		<td id="chrome-content-<%=htmlBindingId%>" style="padding: 5px;" align="left" valign="top">
			
			<surf:componentInclude/>
			
		</td>
	</tr>
</table>
