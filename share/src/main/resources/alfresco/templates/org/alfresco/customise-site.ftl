<#include "include/alfresco-template.ftl" />
<@templateHeader/>

<@templateBody>
   <@markup id="alf-hd">
   <div id="alf-hd">
      <@region scope="global" id="share-header" chromeless="true"/>
      <h1 class="sub-title"><#if page.titleId??>${msg(page.titleId)!page.title}<#else>${page.title}</#if></h1>
   </div>
   </@>
   <#if access>
      <@markup id="bd">
      <div id="bd">
         <@region id="customise-pages" scope="template" />
      </div>
      </@>
   </#if>
</@>

<@templateFooter>
   <@markup id="alf-ft">
   <div id="alf-ft">
      <@region id="footer" scope="global" />
   </div>
   </@>
</@>
