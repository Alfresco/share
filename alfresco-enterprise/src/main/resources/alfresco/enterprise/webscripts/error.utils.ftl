<#-- MNT-20195 (LM-190130): common function that validates and retrieves error log number from given error message. -->
<#function getErrorId message>
   <#assign id = "">
   
   <#if message?? && message?is_string>
      <#assign instanceId = message?substring(0, 8)>
      <#assign seq = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9"]>
      <#assign isNumeric = false>
      
      <#list instanceId?split("", "r") as c>
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
         <#assign id = instanceId>
      </#if>
   </#if>
   
   <#return id>
</#function>