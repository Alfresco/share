<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/folder-details/folder-links.css" group="folder-details"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/document-details/document-links.js" group="folder-details"/>
   <@script src="${url.context}/res/components/folder-details/folder-links.js" group="folder-details"/>
</@>

<@markup id="widgets">
   <#if folder??>
      <@createWidgets group="folder-details"/>
      <@inlineScript group="folder-details">
         YAHOO.util.Event.onContentReady("${args.htmlid?js_string}-heading", function() {
            Alfresco.util.createTwister("${args.htmlid?js_string}-heading", "FolderLinks");
         });
      </@>
   </#if>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#if folder??>
         <#assign el=args.htmlid?html>
         <div id="${el}-body" class="folder-links folder-details-panel">
            <h2 id="${el}-heading" class="thin dark">${msg("header")}</h2>
            <div class="panel-body">
               <#-- Current page url - (javascript will prefix with the current browser location) -->
               <h3 class="thin dark">${msg("page.header")}</h3>
               <div class="link-info">
                  <input id="${el}-page" value=""/>
                  <a href="#" name=".onCopyLinkClick" class="${el} hidden">${msg("page.copy")}</a>
               </div>
               <#if webdavUrl??>
               <#-- webdav link -->
               <h3 class="thin dark">${msg("webdav.header")}</h3>
               <div class="link-info">
                  <input id="${el}-page-webdav" value="${webdavUrl}" />
                  <a href="#" name=".onCopyLinkClick" class="${el} hidden">${msg("webdav.copy")}</a>
               </div>
               </#if>
            </div>
         </div>
      </#if>
   </@>
</@>