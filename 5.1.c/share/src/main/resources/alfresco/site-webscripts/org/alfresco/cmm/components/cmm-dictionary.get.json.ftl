<#escape x as jsonUtils.encodeJSONString(x)>
{
   "entities": [
   <#list entities as e>
      {
         "name": "${e.name}",
         "title": "${e.title!""}",
         "description": "${e.description!""}"
      }<#if e_has_next>,</#if>
   </#list>
   ]
}
</#escape>