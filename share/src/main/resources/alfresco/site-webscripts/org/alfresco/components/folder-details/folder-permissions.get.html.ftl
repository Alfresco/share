<@standalone>
   <@markup id="css" >
      <#-- CSS Dependencies -->
      <@link href="${url.context}/res/components/folder-details/folder-permissions.css" group="folder-details"/>
   </@>
   
   <@markup id="js">
      <#-- JavaScript Dependencies -->
      <@script type="text/javascript" src="${url.context}/res/components/folder-details/folder-permissions.js" group="folder-details"/>
   </@>
   
   <@markup id="widgets">
      <#if displayName??>
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
         <#if displayName??>
            <#include "../../include/alfresco-macros.lib.ftl" />
            <#assign el=args.htmlid?html>
            <div id="${el}-body" class="folder-permissions folder-details-panel">
               <h2 id="${el}-heading" class="thin dark">
                  ${msg("folder-info.permissions")}
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
                        <span class="viewmode-label">${msg("folder-info.managers")}:</span>
                        <span class="viewmode-value">${msg("folder-info.role." + managers)}</span>
                     </div>
                     <div class="viewmode-field">
                        <span class="viewmode-label">${msg("folder-info.collaborators")}:</span>
                        <span class="viewmode-value">${msg("folder-info.role." + collaborators)}</span>
                     </div>
                     <div class="viewmode-field">
                        <span class="viewmode-label">${msg("folder-info.contributors")}:</span>
                        <span class="viewmode-value">${msg("folder-info.role." + contributors)}</span>
                     </div>
                     <div class="viewmode-field">
                        <span class="viewmode-label">${msg("folder-info.consumers")}:</span>
                        <span class="viewmode-value">${msg("folder-info.role." + consumers)}</span>
                     </div>
                     <div class="viewmode-field">
                        <span class="viewmode-label">${msg("folder-info.everyone")}:</span>
                        <span class="viewmode-value">${msg("folder-info.role." + everyone)}</span>
                     </div>
                  <#else>
                     <div class="viewmode-field">${msg("folder-info.no-read-permission")}</div>
                  </#if>
                  </div>
               </div>
            </div>
         </#if>
      </@>
   </@>
</@>
