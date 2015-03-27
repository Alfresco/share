<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <!-- General Links Assets -->
   <@script type="text/javascript" src="${url.context}/res/components/links/linksdiscuss-common.js"></@script>
   <@script type="text/javascript" src="${url.context}/res/components/links/links-common.js"></@script>
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
      <@region id="linksview" scope="template" />
      <@region id="comments" scope="template" />
   </div>
   </@>
</@>

<@templateFooter>
   <@markup id="alf-ft">
   <div id="alf-ft">
      <@region id="footer" scope="global" />
   </div>
   </@>
</@>