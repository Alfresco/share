{
   "nonAmdDeps": [
      <#list nonAmdDeps as dep>
         "${dep}"<#if dep_has_next>,</#if>
      </#list>
   ],
   "javaScript": "${jsResource}",
   "cssMap": {
      <#list cssMap?keys as mediaType>
      "${mediaType}" : "${cssMap[mediaType]}"<#if mediaType_has_next>,</#if>
      </#list>
   },
   "i18nGlobalObject" : "${i18nGlobalObject}",
   "i18nMap": {
      <#list i18nMap?keys as scope>
      "${scope}" : {
         <#list i18nMap[scope]?keys as key>
         <#-- If we upgrade to FreeMarker 2.3.19 then we'd have access to the ?json_string builtin and wouldn't need the two replace calls!-->
         "${key}" : "${i18nMap[scope][key]?js_string?replace("\\'", "'")?replace("\\>", ">")}"<#if key_has_next>,</#if>
         </#list>
      }<#if scope_has_next>,</#if>
      </#list>
   }
}