<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <@script type="text/javascript" src="${url.context}/res/modules/documentlibrary/doclib-actions.js"></@script>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/folder-details/folder-details-panel.css" />
   <@templateHtmlEditorAssets />
</@>

<@templateBody>
   <@markup id="alf-hd">
   <div id="alf-hd">
      <@region scope="global" id="share-header" chromeless="true"/>
   </div>
   </@>
   <@markup id="bd">
   <div id="bd">
      <@region id="actions-common" scope="template"/>
      <@region id="actions" scope="template"/>
      <@region id="node-header" scope="template"/>
      <div class="yui-gc">
         <div class="yui-u first">
            <@region id="comments" scope="template"/>
         </div>
         <div class="yui-u">
            <@region id="folder-actions" scope="template"/>
            <@region id="folder-tags" scope="template"/>
            <@region id="folder-links" scope="template"/>
            <@region id="folder-metadata" scope="template"/>
            <@region id="folder-sync" scope="template"/>
            <@region id="folder-permissions" scope="template"/>
         </div>
      </div>
      <@region id="archive-and-download" scope="template"/>
   </div>
   <@region id="doclib-custom" scope="template"/>
   </@>
</@>

<@templateFooter>
   <@markup id="alf-ft">
   <div id="alf-ft">
      <@region id="footer" scope="global"/>
   </div>
   </@>
</@>
