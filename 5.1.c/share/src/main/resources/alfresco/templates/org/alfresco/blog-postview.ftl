<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <!-- General Blog Assets -->
   <@script type="text/javascript" src="${url.context}/res/components/blog/blogdiscussions-common.js"></@script>
   <@script type="text/javascript" src="${url.context}/res/components/blog/blog-common.js"></@script>
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
      <@region id="postview" scope="template" />
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