<#escape x as jsonUtils.encodeJSONString(x)>
{
   "data": {
      "locales" : [
         <#list locales as locale>
            { 
              "id":"${locale}",
              "name":"${message('content_filter_lang.'+locale)}" 
            }<#if locale_has_next>,</#if>
         </#list>
      ],

      "name": "${nodeRef.name}",
      "type": "${nodeRef.typeShort}",
      "isContainer": ${nodeRef.isContainer?string},
      "locale": <#if nodeLocale??>"${nodeLocale}"<#else>null</#if>,
      "localeName": <#if nodeLocale??>"${message('content_filter_lang.'+nodeLocale)}"<#else>null</#if>,
      "parentNodeRef": "${nodeRef.parent.nodeRef}",
      "translationEnabled": ${translationEnabled?string},

      "translations": {
         <#list translations?keys as locale>
            "${locale}": {
               "nodeRef": "${translations[locale].nodeRef}",
               "name": "${translations[locale].name}"
            }
            <#if locale_has_next>,</#if>
         </#list>
      },
      "parents": {
         <#list translationParents?keys as locale>
            "${locale}": {
               "nodeRef": "${translationParents[locale].first}",
               "allPresent": ${translationParents[locale].second?string}
            }
            <#if locale_has_next>,</#if>
         </#list>
      }
   }
}
</#escape>
