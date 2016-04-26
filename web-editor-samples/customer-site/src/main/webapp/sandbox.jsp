<%@ taglib uri="http://www.alfresco.org/tags/awe" prefix="awe" %>
<%@ taglib uri="http://www.alfresco.org/tags/customer" prefix="customer" %>

<%@ page buffer="16kb" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ include file="includes/noderefs.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">

   <head>
      <meta http-equiv="content-type" content="text/html;charset=utf-8" />
      <title>Alfresco Web Editor Demo (sandbox mode)</title>
      
      <%
         //check if yui version is specified in querystring and load that version instead
         String yuiVersion = "2.7.0";
         String yuiloaderModuleName = "yuiloader";

         if (request.getParameter("yuiVersion")!=null)
         {
            yuiVersion = request.getParameter("yuiVersion");
         }
         Float yuiv = Float.parseFloat(yuiVersion.substring(0, yuiVersion.indexOf(".")+1) + yuiVersion.substring(yuiVersion.indexOf(".")).replace(".",""));
         if (yuiv <= 2.51)
         {
            yuiloaderModuleName = "yuiloader-beta";
         }
      %>
      <!-- Add ydn served yui files for testing sandbox loading -->
      <link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/combo?<%=yuiVersion%>/build/button/assets/skins/sam/button.css"> 
      <script type="text/javascript" src="http://yui.yahooapis.com/combo?<%=yuiVersion%>/build/yuiloader/<%=yuiloaderModuleName%>-debug.js"></script> 

      <awe:startTemplate />
      
      <script type="text/javascript">
         /* Force sandbox mode */
         var c = WEF.get('loaderConfig');
         if (c.useSandboxLoader!==true)
         {
            c.useSandboxLoader=true;
            WEF.set('loaderConfig', c);
         }
      </script>
            
      <link rel="stylesheet" type="text/css" href="customer.css" />
   </head>
   
   <%@ include file="includes/body.jsp" %>

</html>
