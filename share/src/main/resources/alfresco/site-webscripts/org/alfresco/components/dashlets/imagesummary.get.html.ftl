<#assign el=args.htmlid?html>
<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/modules/documentlibrary/global-folder.css" group="dashlets"/>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/dashlets/imagesummary.css" group="dashlets"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/modules/documentlibrary/global-folder.js"/>
   <@script type="text/javascript" src="${url.context}/res/components/dashlets/imagesummary.js" group="dashlets"/>
</@>

<@markup id="widgets">
   <#assign id=el?replace("-", "_")>
   <@inlineScript group="dashlets">
      var imageFolderDashletEvent${id} = new YAHOO.util.CustomEvent("onDashletConfigure");
   </@>
   <@createWidgets group="dashlets"/>
   <@inlineScript group="dashlets">
      imageFolderDashletEvent${id}.subscribe(imageSummary.onConfigImageFolderClick, imageSummary, true);
   </@>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid?html>
      <div class="hidden">
         <#-- HTML template for an image item -->
         <div id="${el}-item-template" class="item">
            <div class="thumbnail">
               <div class="action-overlay">
                  <a href="${url.context}/page/site/${page.url.templateArgs.site}/document-details?nodeRef={nodeRef}"><img title="${msg("label.viewdetails")}" src="${url.context}/res/components/documentlibrary/actions/document-view-details-16.png" width="16" height="16" /></a>
                  <a href="${url.context}/proxy/alfresco/slingshot/node/content/{nodeRefUrl}/{name}?a=true"><img title="${msg("label.download")}" src="${url.context}/res/components/documentlibrary/actions/document-download-16.png" width="16" height="16"/></a>
               </div>
               <a href="${url.context}/proxy/alfresco/api/node/content/{nodeRefUrl}/{name}" onclick="Alfresco.Lightbox.show(this);return false;" title="{title} - {modifier} {modified}"><img src="${url.context}/proxy/alfresco/api/node/{nodeRefUrl}/content/thumbnails/doclib?c=force"/></a>
            </div>
         </div>
      </div>
      <div class="dashlet">
         <div class="title">${msg("header.title")}</div>
         <div id="${el}-list" class="body scrollableList" <#if args.height??>style="height: ${args.height?html}px;"</#if>>
            <div class="dashlet-padding">
               <div id="${el}-wait" class="images-wait hidden"></div>
               <div id="${el}-message" class="images-message hidden"></div>
               <div id="${el}-images" class="images hidden"></div>
            </div>
         </div>
      </div>
   </@>
</@>