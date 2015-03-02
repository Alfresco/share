<#macro jsonHiddenTaskTypes hiddenTaskTypes>
   [<#list hiddenTaskTypes as type>"${type?js_string}"<#if type_has_next>, </#if></#list>]
</#macro>

<#macro jsonWorkflowDefinitions workflowDefinitions>
   [<#list workflowDefinitions as definition>
      {
         "id" : "${definition.id?js_string}",
         "name": "${definition.name!""?js_string}",
         "title": "${definition.title!""?js_string}",
         "description": "${definition.description!""?js_string}"
      }<#if definition_has_next>, </#if>
   </#list>]
</#macro>

