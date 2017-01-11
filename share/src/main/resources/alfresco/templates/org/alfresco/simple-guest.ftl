<#include "include/alfresco-template.ftl" />
<@templateHeader/>

<@templateBody type="alfresco-guest">
   <#if outcome??>
      <@region id=outcome scope="page"/>
   <#else>
      <@region id="components" scope="page"/>
   </#if>
</@>

<@templateFooter/>