<@standalone>
   <@markup id="css" >
      <#-- CSS Dependencies -->
      <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/document-details/document-permissions.css" group="document-details"/>
   </@>
   
   <@markup id="js">
      <#-- JavaScript Dependencies -->
      <@script type="text/javascript" src="${url.context}/res/components/document-details/document-permissions.js" group="document-details"/>
   </@>
   
   <@markup id="widgets">
      <#if displayName??>
         <@createWidgets group="document-details"/>
         <@inlineScript group="document-details">
            YAHOO.util.Event.onContentReady("${args.htmlid?js_string}-heading", function() {
               Alfresco.util.createTwister("${args.htmlid?js_string}-heading", "DocumentPermissions");
            });
         </@>
      </#if>
   </@>
   
   <@markup id="html">
      <@uniqueIdDiv>
         <#if displayName??>
            <#include "../../include/alfresco-macros.lib.ftl" />
            <#assign el=args.htmlid?html>
            <div id="${el}-body" class="document-permissions document-details-panel">
               <h2 id="${el}-heading" class="thin dark">
                  ${msg("document-info.permissions")}
                  <#if allowPermissionsUpdate>
                     <span class="alfresco-twister-actions">
                        <a href="${siteURL("manage-permissions?nodeRef="+nodeRef?html)}" class="edit" title="${msg("label.edit")}">&nbsp;</a>
                     </span>
                  </#if>
               </h2>
               <div class="form-container">
                  <div class="form-fields">
                  <#if readPermission!false>
                     <div class="viewmode-field">
                        <span class="viewmode-label">${msg("document-info.managers")}:</span>
                        <span class="viewmode-value">${msg("document-info.role." + managers)}</span>
                     </div>
                     <div class="viewmode-field">
                        <span class="viewmode-label">${msg("document-info.collaborators")}:</span>
                        <span class="viewmode-value">${msg("document-info.role." + collaborators)}</span>
                     </div>
                     <div class="viewmode-field">
                        <span class="viewmode-label">${msg("document-info.contributors")}:</span>
                        <span class="viewmode-value">${msg("document-info.role." + contributors)}</span>
                     </div>
                     <div class="viewmode-field">
                        <span class="viewmode-label">${msg("document-info.consumers")}:</span>
                        <span class="viewmode-value">${msg("document-info.role." + consumers)}</span>
                     </div>
                     <div class="viewmode-field">
                        <span class="viewmode-label">${msg("document-info.everyone")}:</span>
                        <span class="viewmode-value">${msg("document-info.role." + everyone)}</span>
                     </div>
                  <#else>
                     <div class="viewmode-field">${msg("document-info.no-read-permission")}</div>
                  </#if>
                  </div>
               </div>
            </div>
         </#if>
      </@>
   </@>
</@>
