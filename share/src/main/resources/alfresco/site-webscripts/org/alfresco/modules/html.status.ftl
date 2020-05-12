"'></a></script>
<!-- above dodgy HTML is required to end any open tags, script etc. before further script injection -->

<#import "../errorcode.lib.ftl" as codeLib />

<div class="theme-color-2" style="padding: 8px; margin: 8px; border: 1px dashed #D7D7D7;">
   <div style="font-weight: bold; font-size: 116%">
      <div style="padding: 2px">An error has occured in the Share component: ${url.service?html}.</div>
      <div style="padding: 2px">It responded with a status of ${status.code} - ${status.codeName}.</div>
   </div>
   <div class="theme-color-4" style="padding-top:8px;">
      <div style="padding: 2px"><b>Error Code Information:</b> ${status.code} - ${status.codeDescription}</div>
      <#assign errorCode = codeLib.getErrorCode(status.message)>
      <#if errorCode?has_content>
      <div style="padding: 2px"><b>Error Log Number: </b>${errorCode}</div>
      <#else>
      <div style="padding: 2px"><b>Error Message:</b> <#if status.message??>${status.message?html}<#else><i>&lt;Not specified&gt;</i></#if></div>
      </#if>
      <div style="padding: 2px"><b>Your request could not be processed at this time. Please contact your system administrator for further information.</b></div>
   </div>
</div>
