<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/document-details/path.css" group="document-details"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/document-details/path.js" group="document-details"/>
</@>

<@markup id="widgets">
   <@createWidgets group="document-details"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#include "../../include/alfresco-macros.lib.ftl" />
      <#assign el=args.htmlid?html>
      <div class="path-nav theme-bg-color-2">
         <span class="heading">${msg("path.location")}:</span>
         <span id="${el}-defaultPath" class="path-link"><a href="${siteURL("documentlibrary")}">${msg("path.documents")}</a></span>
         <span id="${el}-path"></span>
      </div>
      <div id="${el}-status" class="status-banner hidden"></div>
   </@>
</@>
