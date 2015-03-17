<@standalone>
<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/document-details/document-tags.css" group="document-details"/>
</@>

<@markup id="js">
      <#-- JavaScript Dependencies -->
      <@script src="${url.context}/res/components/document-details/document-tags.js" group="document-details"/>
</@>

<@markup id="widgets">
   <#if allowMetaDataUpdate??>
      <@createWidgets group="document-details"/>
      <@inlineScript group="document-details">
         YAHOO.util.Event.onContentReady("${args.htmlid?js_string}-heading", function() {
            Alfresco.util.createTwister("${args.htmlid?js_string}-heading", "DocumentTags");
         });
      </@>
   </#if>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid?html>
      <#if allowMetaDataUpdate??>
         <#include "../../include/alfresco-macros.lib.ftl" />
         <div id="${el}-body" class="document-tags document-details-panel">
            <h2 id="${el}-heading" class="thin dark">
               ${msg("label.tags")}
               <#if allowMetaDataUpdate>
                  <span class="alfresco-twister-actions">
                     <a href="${siteURL("edit-metadata?nodeRef="+nodeRef?html)}" class="edit" title="${msg("label.edit")}">&nbsp;</a>
                  </span>
               </#if>
            </h2>
            <div class="panel-body">
               <#if tags?size == 0>
                  ${msg("label.none")}
               <#else>
                  <#list tags as tag>
                     <span class="tag">${tag?html}</span>
                  </#list>
               </#if>
            </div>
         </div>
      </#if>
   </@>
</@>
</@>