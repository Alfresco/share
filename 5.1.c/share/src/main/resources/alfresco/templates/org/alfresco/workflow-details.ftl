<#include "include/alfresco-template.ftl" />
<@templateHeader />

<@templateBody>
   <@markup id="alf-hd">
   <div id="alf-hd">
      <@region scope="global" id="share-header" chromeless="true"/>
      <@region id="title" scope="template"/>
      <#if page.url.args.nodeRef??>
         <@region id="path" scope="template"/>
      </#if>
   </div>
   </@>
   <@markup id="bd">
   <div id="bd">
      <div class="share-form">
         <@region id="data-header" scope="page" />
         <@region id="data-form" scope="page" />
         <@region id="data-actions" scope="page" />
      </div>
   </div>
   </@>
</@>

<@templateFooter>
   <@markup id="alf-ft">
   <div id="alf-ft">
      <@region id="footer" scope="global"/>
      <@region id="data-loader" scope="page" />
   </div>
   </@>
</@>
