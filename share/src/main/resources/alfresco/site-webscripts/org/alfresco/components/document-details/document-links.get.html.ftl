<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/document-details/document-links.css" group="document-details"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/document-details/document-links.js" group="document-details"/>
</@>

<@markup id="widgets">
   <#if document??>
      <#if document.workingCopy??>
          <!-- Don't display links since this nodeRef points to one of a working copy pair -->
      <#else>
         <@createWidgets group="document-details"/>
         <@inlineScript group="document-details">
            YAHOO.util.Event.onContentReady("${args.htmlid?js_string}-heading", function() {
               Alfresco.util.createTwister("${args.htmlid?js_string}-heading", "DocumentLinks");
            });
         </@>
      </#if>
   </#if>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#if document??>
         <#assign el=args.htmlid?html>
         <#if document.workingCopy??>
            <!-- Don't display links since this nodeRef points to one of a working copy pair -->
         <#else>
         <div id="${el}-body" class="document-links document-details-panel">
            <h2 id="${el}-heading" class="thin dark">${msg("header")}</h2>
            <div class="panel-body">
               <!-- Current page url - (javascript will prefix with the current browser location) -->
               <h3 class="thin dark">${msg("page.header")}</h3>
               <div class="link-info">
                  <input id="${el}-page" value=""/>
                  <a href="#" name=".onCopyLinkClick" class="${el} hidden">${msg("page.copy")}</a>
               </div>
            </div>
         </div>
         </#if>
      </#if>
   </@>
</@>