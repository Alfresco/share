<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <@markup id="resizer">
   <script type="text/javascript">//<![CDATA[
      new Alfresco.widget.Resizer("Wiki");
   //]]></script>
   </@>
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
      <div class="yui-t1" id="alfresco-wiki">
         <div id="yui-main">
            <div class="yui-b" id="alf-content">
               <@region id="toolbar" scope="template" />
               <@region id="pagelist" scope="template" />
            </div>
         </div>
         <div class="yui-b" id="alf-filters">
            <@region id="filter" scope="template" />
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