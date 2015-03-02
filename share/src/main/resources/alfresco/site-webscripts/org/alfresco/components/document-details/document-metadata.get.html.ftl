<@standalone>
<@markup id="css" >
   <#-- CSS Dependencies -->
   <#include "../form/form.css.ftl"/>
   <@link href="${url.context}/res/components/document-details/document-metadata.css" group="document-details"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <#include "../form/form.js.ftl"/>
   <@script src="${url.context}/res/components/document-details/document-metadata.js" group="document-details"/>
</@>

<@markup id="widgets">
   <#if document??>
      <@createWidgets group="document-details"/>
      <@inlineScript group="document-details">
         YAHOO.util.Event.onContentReady("${args.htmlid?js_string}-heading", function() {
            Alfresco.util.createTwister("${args.htmlid?js_string}-heading", "DocumentMetadata");
         });
      </@>
   </#if>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#if document??>
         <!-- Parameters and libs -->
         <#include "../../include/alfresco-macros.lib.ftl" />
         <#assign el=args.htmlid?html>
         <!-- Markup -->
         <div class="document-metadata-header document-details-panel">
            <h2 id="${el}-heading" class="thin dark">
               ${msg("heading")}
               <#if allowMetaDataUpdate!false>
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