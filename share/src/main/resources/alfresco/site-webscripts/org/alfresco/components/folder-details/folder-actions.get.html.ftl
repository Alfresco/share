<@standalone>
   <@markup id="css" >
      <#-- CSS Dependencies -->
      <@link href="${url.context}/res/components/folder-details/folder-actions.css" group="folder-details"/>
   </@>
   
   <@markup id="js">
      <#-- JavaScript Dependencies -->
      <@script src="${url.context}/res/components/folder-details/folder-actions.js" group="folder-details"/>
   </@>
   
   <@markup id="widgets">
      <@createWidgets group="folder-details"/>
      <@inlineScript group="folder-details">
         YAHOO.util.Event.onContentReady("${args.htmlid?js_string}-heading", function() {
            Alfresco.util.createTwister("${args.htmlid?js_string}-heading", "FolderActions");
         });
      </@>
   </@>
   
   <@markup id="html">
      <@uniqueIdDiv>
         <#if folderDetails??>
            <#assign el=args.htmlid?html>
            <div id="${el}-body" class="folder-actions folder-details-panel">
               <h2 id="${el}-heading" class="thin dark">
                  ${msg("heading")}
               </h2>
               <div class="doclist">
                  <div id="${el}-actionSet" class="action-set"></div>
               </div>
            </div>
         </#if>
      </@>
   </@>
</@>