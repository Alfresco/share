<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
   <head>
      <title>Web Script Status ${status.code} - ${status.codeName}</title>
      <link rel="stylesheet" href="${url.context}/css/webscripts.css" type="text/css" />
   </head>
   <body>
      <div>
         <table>
            <tr>
               <td><img src="${url.context}/res/images/logo/SurfLogo200.png" alt="SURF" /></td>
               <td><span class="title">Web Script Status ${status.code} - ${status.codeName}</span></td>
            </tr>
         </table>
         <br/>
         <table>
            <tr><td>The Web Script <a href="${url.full?url}">${url.service?html}</a> has responded with a status of ${status.code} - ${status.codeName}.</td></tr>
         </table>
         <br/>
         <table>
            <tr><td><b>${status.code} Description:</b></td><td> ${status.codeDescription}</td></tr>
            <tr><td>&nbsp;</td></tr>
            <tr><td><b>Message:</b></td><td><#if status.message??>${status.message?html}<#else><i>&lt;Not specified&gt;</i></#if></td></tr>
            <#if status.exception?exists>
            <tr><td></td><td>&nbsp;</td></tr>
            <@recursestack status.exception/>
            </#if>
            <tr><td><b>Server</b>:</td><td>${server.edition?html} v${server.version?html} schema ${server.schema?html}</td></tr>
            <tr><td><b>Time</b>:</td><td>${date?datetime}</td></tr>
            <tr><td></td><td>&nbsp;</td></tr>
            <#if webscript?exists>
            <tr><td><b>Diagnostics</b>:</td><td><a href="${url.serviceContext}/script/${webscript.id}">Inspect Web Script (${webscript.id})</a></td></tr>
            </#if>
         </table>
      </div>
   </body>
</html>

<#macro recursestack exception>
   <#if exception.cause?exists>
      <@recursestack exception=exception.cause/>
   </#if>
   <#if exception.message?? && exception.message?is_string>
   <tr><td><b>Exception:</b></td><td>${exception.class.name} - ${exception.message?html}</td></tr>
   <tr><td></td><td>&nbsp;</td></tr>
   <#if exception.cause?exists == false>
      <#list exception.stackTrace as element>
         <tr><td></td><td>${element?html}</td></tr>
      </#list>
   <#else>
      <tr><td></td><td>${exception.stackTrace[0]?html}</td></tr>
   </#if>
   <tr><td></td><td>&nbsp;</td></tr>
   </#if>
</#macro>