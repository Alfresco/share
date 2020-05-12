<#-- MNT-20195 (LM-190214): strip out and return error code from given error message. -->
<#function getErrorCode message>
   <#assign code = "">

   <#if message?? && message?is_string>
      <!-- substring first 8 characters from message that usually be the error code id. -->
      <#assign instanceId = message?substring(0, 8)>
      <!-- check if these codes are 'numeric'. -->
      <#if instanceId?matches("^[0-9]*$")>
         <#assign code = instanceId>
      </#if>
   </#if>

   <#return code>
</#function>