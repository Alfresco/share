<#macro authorityJSON authority>
   <#assign metadata = authority.metadata>
   <#escape x as jsonUtils.encodeJSONString(x)>
{
      "authorityType": "${authority.authorityType}",
      "shortName": "${authority.shortName}",
      "fullName": "${authority.fullName}",
      "displayName": "${authority.displayName}",
      "metadata":
      {
      <#if authority.authorityType = "USER">
         "avatar": "${metadata.avatar!""}",
         "jobTitle": "${metadata.jobTitle!""}",
         "organization": "${metadata.organization!""}"
      </#if>
      }
   }
   </#escape>
</#macro>
{
   "authorities":
   [
   <#list authorities as authority>
      <@authorityJSON authority=authority/><#if authority_has_next>,</#if>
   </#list>
   ]
}