<%--
  #%L
  Alfresco Web Editor Samples
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
<%@ taglib uri="http://www.alfresco.org/tags/awe" prefix="awe" %>
<%@ taglib uri="http://www.alfresco.org/tags/customer" prefix="customer" %>

<%@ page buffer="16kb" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<%@ include file="includes/noderefs.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">

   <head>
      <meta http-equiv="content-type" content="text/html;charset=utf-8" />
      <title>Alfresco Web Editor Demo</title>
      <awe:startTemplate />
      <link rel="stylesheet" type="text/css" href="customer.css" />
   </head>
   
   <%@ include file="includes/body.jsp" %>

</html>
