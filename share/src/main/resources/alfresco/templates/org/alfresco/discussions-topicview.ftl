<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <!-- General Discussion Assets -->
   <@script type="text/javascript" src="${url.context}/res/components/blog/blogdiscussions-common.js"></@script>
   <@script type="text/javascript" src="${url.context}/res/components/discussions/discussions-common.js"></@script>
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
      <@region id="toolbar" scope="template" />
      <@region id="topic" scope="template" />
      <@region id="replies" scope="template" />
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
