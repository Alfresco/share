<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader/>

<#if outcome == "error">
   <@templateBody type="brand-bg-1">
      <@region id="error" scope="page"/>
   </@>
<#else>
   <@templateBody>
      <@region id="components" scope="page"/>
   </@>
</#if>

<@templateFooter>
   <@markup id="alf-ft">
   <div id="alf-ft">
      <@region id="footer" scope="global"/>
   </div>
   </@>
</@templateFooter>
