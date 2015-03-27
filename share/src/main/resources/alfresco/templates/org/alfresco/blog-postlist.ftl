<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <@markup id="resizer">
   <script type="text/javascript">//<![CDATA[
      new Alfresco.widget.Resizer("Blog-PostList");
   //]]></script>
   </@>
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
      <div class="yui-t1" id="alfresco-blog-postlist">
         <div id="yui-main">
            <div class="yui-b" id="alf-content">
               <@region id="toolbar" scope="template" />
               <@region id="postlist" scope="template" />
            </div>
         </div>
         <div class="yui-b" id="alf-filters">
            <@region id="filters" scope="template" />
            <@region id="archives" scope="template" />
            <@region id="tags" scope="template" />
         </div>
      </div>
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