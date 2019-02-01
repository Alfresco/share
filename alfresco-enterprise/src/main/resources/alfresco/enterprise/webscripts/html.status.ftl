<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
   <title>Alfresco &raquo; System Error</title>
   <script type="text/javascript">
var _errorhidden = true;
function _toggleErrorDetails()
{
   if (_errorhidden)
   {
      document.getElementById('_errorDetails').style.display = 'block';
      _errorhidden = false;
   }
   else
   {
      document.getElementById('_errorDetails').style.display = 'none';
      _errorhidden = true;
   }
}
   </script>
</head>
<body>

<#-- MNT-20195 (LM-190130): import new utility file. -->
<#import "error.utils.ftl" as errorLib />

<div style="padding: 8px; margin: 8px; border: 1px dashed #D7D7D7;">
   <div style="font-weight: bold; font-size: 116%">
      <div style="padding: 2px">An error has occured in the API: ${url.service?html}.</div>
      <div style="padding: 2px">It responded with a status of ${status.code} - ${status.codeName}.</div>
   </div>
   <div style="padding-top:8px;">
      <div style="padding: 2px"><b>Error Code Information:</b> ${status.code} - ${status.codeDescription}</div>
      <#-- 
         MNT-20195: hide "Server", "Time" and "Stack trace", and check if should show error log number or error message.
         LM-190130: code changes from line 38-43.
      -->
      <#assign errorId = errorLib.getErrorId(status.message)>
      <#if errorId?has_content>
      <div style="padding: 2px"><b>Error Log Number:</b> ${errorId}</div>
      <#else>
      <div style="padding: 2px"><b>Error Message:</b> <#if status.message??>${status.message?html}<#else><i>&lt;Not specified&gt;</i></#if></div>
      </#if>
   </div>
</div>

</body>
</html>

<#macro recursestack exception>
   <#if exception.cause?exists>
      <@recursestack exception=exception.cause/>
   </#if>
   <#if exception.message?? && exception.message?is_string>
      <div style="padding: 2px"><b>Exception:</b> ${exception.class.name} - ${exception.message?html}</div>
      <#if exception.cause?exists == false>
         <#list exception.stackTrace as element>
            <div>${element?html}</div>
         </#list>
      <#else>
         <div>${exception.stackTrace[0]?html}</div>
      </#if>
   </#if>
</#macro>