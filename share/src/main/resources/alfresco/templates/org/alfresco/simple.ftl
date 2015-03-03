<#include "include/alfresco-template.ftl" />
<@templateHeader />

<@templateBody>
   <div id="alf-hd">
      <@region id="share-header" scope="global" chromeless="true"/>
      <@region id="title" scope="page"/>
   </div>
   <div id="bd">
      <#if outcome??>
         <@region id=outcome scope="page"/>
      <#else>
         <@region id="components" scope="page"/>
      </#if>
   </div>
</@>

<@templateFooter>
<div id="alf-ft">
   <@region id="footer" scope="global"/>
</div>
</@>