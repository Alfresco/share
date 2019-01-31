"'></a></script>
<!-- above dodgy HTML is required to end any open tags, script etc. before further script injection -->

<div class="theme-color-2" style="padding: 8px; margin: 8px; border: 1px dashed #D7D7D7;">
   <div style="font-weight: bold; font-size: 116%">
      <div style="padding: 2px">An error has occured in the Share component: ${url.service?html}.</div>
      <div style="padding: 2px">It responded with a status of ${status.code} - ${status.codeName}.</div>
   </div>
   <div class="theme-color-4" style="padding-top:8px;">
      <div style="padding: 2px"><b>Error Code Information:</b> ${status.code} - ${status.codeDescription}</div>
      <#-- MNT-20195 (LM-190130): hide server and time info, and display error log number/error message. -->
      <@messageOrId status />
      <div style="padding: 2px"><b>Your request could not be processed at this time. Please contact your system administrator for further information.</b></div>
   </div>
</div>

<#-- MNT-20195 (LM-190130): macro to show error message if available -->
<#macro errorMessage status>
   <div style="padding: 2px"><b>Error Message:</b> <#if status.message??>${status.message?html}<#else><i>&lt;Not specified&gt;</i></#if></div>
</#macro>

<#-- MNT-20195 (LM-190130): macro to determine if should display error log number only or full error message. -->
<#macro messageOrId status>
   <#if status.message?? && status.message?is_string>
      <#assign id = status.message?substring(0, 8)>
      <#assign isNumeric = false>
      <#assign seq = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9"]>
      
      <#list id?split("", "r") as c>
         <#list seq as x>
            <#if c == x>
               <#assign isNumeric = true>
               <#break>
            <#else>
           	   <#assign isNumeric = false>
            </#if>
         </#list>
         
         <#if !isNumeric>
         	<#break>
         </#if>
      </#list>
      
      <#if isNumeric>
         <div style="padding: 2px"><b>Error Log Number: </b>${id}</div>
      <#else>
         <@errorMessage status />
      </#if>
      
   <#else>
      <@errorMessage status />
   </#if>
</#macro>