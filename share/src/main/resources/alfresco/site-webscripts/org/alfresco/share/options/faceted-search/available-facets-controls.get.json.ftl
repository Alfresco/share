<#macro renderItem item>
<#escape x as jsonUtils.encodeJSONString(x)>
{
   "label": "${item.label!''}",
   "value": "${item.value!''}"
}
</#escape>
</#macro>
<#escape x as jsonUtils.encodeJSONString(x)>
{
   "options": 
      [
      <#list items as item>
         <@renderItem item /><#if item_has_next>,</#if>
      </#list>
      ]
}
</#escape>