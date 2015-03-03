<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/document-details/path.css" group="folder-details"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/components/document-details/path.js" group="folder-details"/>
</@>

<@markup id="widgets">
   <@createWidgets group="folder-details"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#include "../../include/alfresco-macros.lib.ftl" />
      <#assign el=args.htmlid?html>
      <div class="path-nav">
         <span class="heading">${msg("path.location")}:</span>
         <#assign href>${url.context}/page/repository</#assign>
         <span id="${el}-defaultPath" class="path-link"><a href="${siteURL("repository")}">${msg("path.repository")}</a></span>
         <span id="${el}-path"></span>
      </div>
      <#if (args.showIconType!"true") == "true">
      <div id="${el}-iconType" class="icon-type"></div>
      </#if>
   </@>
</@>

