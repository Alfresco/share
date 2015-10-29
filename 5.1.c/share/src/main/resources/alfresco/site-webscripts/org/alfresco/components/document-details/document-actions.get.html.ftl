<@standalone>
   <@markup id="css" >
      <#-- CSS Dependencies -->
      <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/document-details/document-actions.css" group="document-details" />
   </@>
   
   <@markup id="js">
      <#-- JavaScript Dependencies -->
      <@script type="text/javascript" src="${url.context}/res/components/document-details/document-actions.js" group="document-details" />
   </@>
   
   <@markup id="widgets">
      <#if documentDetails??>
         <@createWidgets group="document-details"/>
         <@inlineScript group="document-details">
            YAHOO.util.Event.onContentReady("${args.htmlid?js_string}-heading", function() {
               Alfresco.util.createTwister("${args.htmlid?js_string}-heading", "DocumentActions");
            });
         </@>
      </#if>
   </@>
   
   <@markup id="html">
      <@uniqueIdDiv>
         <#assign el=args.htmlid?html>
         <#if documentDetails??>
            <div id="${el}-body" class="document-actions document-details-panel">
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