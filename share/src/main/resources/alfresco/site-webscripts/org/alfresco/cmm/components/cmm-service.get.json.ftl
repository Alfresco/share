{
<#if form??>
   "form": ${form}
</#if>
<#escape x as jsonUtils.encodeJSONString(x)>
<#if moduleId??>
   "moduleId": "${moduleId}"
</#if>
<#if forms??>
   "forms": {
   <#list forms as f>
      "${f}": true<#if f_has_next>,</#if>
   </#list>
   }
</#if>
</#escape>
}