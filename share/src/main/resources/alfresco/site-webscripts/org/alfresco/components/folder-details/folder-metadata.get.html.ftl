<@standalone>
<@markup id="css" >
   <#-- CSS Dependencies -->
   <#include "../form/form.css.ftl"/>
   <@link href="${url.context}/res/components/folder-details/folder-metadata.css" group="folder-details"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <#include "../form/form.js.ftl"/>
   <@script src="${url.context}/res/components/folder-details/folder-metadata.js" group="folder-details" />
</@>

<@markup id="widgets">
   <#if allowMetaDataUpdate??>
      <@createWidgets group="folder-details"/>
      <@inlineScript group="folder-details">
         YAHOO.util.Event.onContentReady("${args.htmlid?js_string}-heading", function() {
            Alfresco.util.createTwister("${args.htmlid?js_string}-heading", "FolderMetadata");
         });
      </@>
   </#if>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#if allowMetaDataUpdate??>
          <!-- Parameters and libs -->
          <#include "../../include/alfresco-macros.lib.ftl" />
          <#assign el=args.htmlid>
          <!-- Markup -->
          <div class="folder-metadata-header folder-details-panel">
             <h2 id="${el}-heading" class="thin dark">
                ${msg("heading")}
                <#if allowMetaDataUpdate>
                <span class="alfresco-twister-actions">
                   <a href="${siteURL("edit-metadata?nodeRef=" + nodeRef?url)}" class="edit" title="${msg("label.edit")}">&nbsp;</a>
                </span>
                </#if>
             </h2>
             <div id="${el}-formContainer"></div>
          </div>
      </#if>
   </@>
</@>
</@>