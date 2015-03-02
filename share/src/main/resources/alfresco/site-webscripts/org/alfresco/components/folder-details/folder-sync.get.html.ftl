<@standalone>
   <@markup id="css" >
      <#-- CSS Dependencies -->
      <@link href="${url.context}/res/components/document-details/document-sync.css" group="document-details"/>
   </@>
   
   <@markup id="js">
      <#-- JavaScript Dependencies -->
      <@script src="${url.context}/res/components/document-details/document-sync.js" group="document-details"/>
   </@>
   
   <@markup id="widgets">
      <#if folderDetails && syncEnabled>
         <@createWidgets group="document-details"/>
         <@inlineScript group="document-details">
            YAHOO.util.Event.onContentReady("${args.htmlid?js_string}-heading", function() {
               Alfresco.util.createTwister("${args.htmlid?js_string}-heading", "FolderSync");
            });
         </@>
      </#if>
   </@>
   
   <@markup id="html">
      <@uniqueIdDiv>
         <#if folderDetails && syncEnabled>
         <#assign el=args.htmlid?html>
         <div class="document-sync folder-details-panel">
            <h2 id="${el}-heading" class="thin dark">
               ${msg("heading")}
               <span id="${el}-document-sync-twister-actions" class="alfresco-twister-actions hidden">
                  {syncActionButtons}
               </span>
            </h2>
            <div id="${el}-formContainer" class="document-sync-formContainer"></div>
         </div>
         </#if>
      </@>
   </@>
</@>