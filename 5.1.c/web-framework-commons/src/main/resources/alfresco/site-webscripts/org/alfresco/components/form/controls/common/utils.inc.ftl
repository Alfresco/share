<#function msgValue valueOrId >
   <#if (valueOrId?string?starts_with("{") && valueOrId?string?ends_with("}"))>
      <#return msg(valueOrId?substring(1, valueOrId?length - 1))>
   </#if>
   <#return valueOrId>
</#function>