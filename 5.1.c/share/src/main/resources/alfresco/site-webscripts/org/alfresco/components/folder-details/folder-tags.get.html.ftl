<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/folder-details/folder-tags.css" group="folder-details"/>
</@>

<@markup id="js">
   <#-- No JavaScript Dependencies -->
</@>

<@markup id="widgets">
   <#if allowMetaDataUpdate??>
      <@createWidgets group="folder-details"/>
      <@inlineScript group="folder-details">
         YAHOO.util.Event.onContentReady("${args.htmlid?js_string}-heading", function() {
            Alfresco.util.createTwister("${args.htmlid?js_string}-heading", "FolderPermissions");
         });
      </@>
   </#if>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#if allowMetaDataUpdate??>
         <#include "../../include/alfresco-macros.lib.ftl" />
         <#assign el=args.htmlid?html>
         <div id="${el}-body" class="folder-tags folder-details-panel">
            <h2 id="${el}-heading" class="thin dark">
               ${msg("label.tags")}
               <#if allowMetaDataUpdate>
                  <span class="alfresco-twister-actions">
                     <a href="${siteURL("edit-metadata?nodeRef=" + nodeRef?html)}" class="edit" title="${msg("label.edit")}">&nbsp;</a>
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
