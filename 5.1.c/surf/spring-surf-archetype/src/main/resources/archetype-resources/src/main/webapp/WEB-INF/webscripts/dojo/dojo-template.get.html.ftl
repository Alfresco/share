<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
   <head>
      <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
      <title>Demo</title>
      <@outputCSS forceAggregation="true"/>
      
      <#-- Load up the core messages. This makes all global messages
           available in the JavaScript object "Surf.messages.global" -->
      <@markup id="messages">
         <#-- Common i18n msg properties -->
         <@generateMessages type="text/javascript" src="${url.context}/service/messages.js" locale="${locale}"/>
      </@markup>
      
      <#-- Bootstrap Dojo -->
      <@createComponent scope="global" regionId="bootstrap" sourceId="global" uri="/surf/dojo/bootstrap"/>
      <@region scope="global" id="bootstrap" chromeless="true"/>
      <#-- This is a markup section for any global JavaScript constants that might be required -->
      <@markup id="constants">
         <script type="text/javascript">
            var RESOURCE_URI = window.location.protocol + "//" + window.location.host + "${url.context}/res/"
            var PROXY_URI = window.location.protocol + "//" + window.location.host + "${url.context}/proxy/alfresco/";
            var SERVICE_URI = window.location.protocol + "//" + window.location.host + "${url.context}/service/";
         </script>
      </@>
      <@outputJavaScript forceAggregation="true"/>
   </head>
   <body class="claro concept-cloud-body">
      <div id="content">
         <#-- Here we create the a component purely to serve the WebScript requested. If the Component already exists then 
              it won't get recreated. This allows us to never need to create components -->
         <#assign regionId = page.url.templateArgs.webscript?replace("/", "-")/>
         <@createComponent scope="global" regionId="${regionId}" sourceId="global" uri="/${page.url.templateArgs.webscript}"/>
         <@region scope="global" id="${regionId}" chromeless="true"/>
      </div>
   </body>
</html>