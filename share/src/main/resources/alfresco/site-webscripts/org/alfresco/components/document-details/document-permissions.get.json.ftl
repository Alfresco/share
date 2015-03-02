{
   "nodeRef": "${nodeRef?js_string}",
   "siteId": <#if site??>"${site?js_string}"<#else>null</#if>,
   "displayName": "${displayName?js_string}",
   "roles": [<#list roles as r>"${r}"<#if r_has_next>, </#if></#list>]
}