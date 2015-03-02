<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<#import "import/alfresco-common.ftl" as common />
<#assign DEBUG=(common.globalConfig("client-debug", "false") = "true")>
<#assign AUTOLOGGING=(common.globalConfig("client-debug-autologging", "false") = "true")>
<#assign theme = (page.url.args.theme!theme)?html />

<head>
      <title><@region id="head-title" scope="global" chromeless="true"/></title>
      <meta http-equiv="X-UA-Compatible" content="IE=Edge" />

      <#--
         This is where the JavaScript and CSS dependencies will initially be added through the use of the
         <@script> and <@link> directives. The JavaScript can be moved through the use
         of the <@relocateJavaScript> directive (i.e. to move it to the end of the page). These directives
         must be placed before directives that add dependencies to them otherwise those resources will
         be placed in the output of the ${head} variable (i.e. this applied to all usage of those directives
         in *.head.ftl files)
      -->
      <@outputJavaScript/>
      <@outputCSS/>

      <!-- Template Resources' stylesheets gathered to workaround IEBug KB262161 -->
      <#if (templateStylesheets?? && templateStylesheets?size > 0)>
         <style type="text/css" media="screen">
               <#list templateStylesheets as href>
               @import "${href}";
               </#list>
         </style>
      </#if>

   <!-- Component Resources (from .get.head.ftl files) -->
   ${head}

   </head>
   <body id="Share" class="alfresco-redirect">

      <#if outcome??>
         <@region id=outcome scope="page"/>
      <#else>
         <@region id="components" scope="page"/>
      </#if>

   </body>
</html>


