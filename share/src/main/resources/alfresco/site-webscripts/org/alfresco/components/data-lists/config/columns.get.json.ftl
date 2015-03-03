<#escape x as jsonUtils.encodeJSONString(x)>
{
<#if error??>
   "error": "${error}"
<#else>
   "columns":
   [
   <#list columns as col>
      {
         "type": "${col.type}",
         "name": "${col.name}",
         "formsName": "<#if col.type == "association">assoc<#else>prop</#if>_${col.name?replace(":", "_")}",
         "label": "${col.label!""}",
      <#if col.dataType??>
         "dataType": "${col.dataType}"
      <#else>
         "dataType": "${col.endpointType}"
      </#if>
      }<#if col_has_next>,</#if>
   </#list>
   ]
</#if>
}
</#escape>