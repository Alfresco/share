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
	
	String title = null;
	if (component != null && component.getTitle() != null)
	{
		title = component.getTitle();
	}
	
	if (title != null)
	{
%>	
<h2><%=title%></h2>
<%
	}
%>	
<surf:componentInclude/>
