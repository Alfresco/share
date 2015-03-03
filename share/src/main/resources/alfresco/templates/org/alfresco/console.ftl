<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <@markup id="resizer">
   <script type="text/javascript">//<![CDATA[
      new Alfresco.widget.Resizer("Console").setOptions(
      {
         initialWidth: 190
      });
   //]]></script>
   </@>
</@>

<@templateBody>
   <@markup id="alf-hd">
   <div id="alf-hd">
      <@region scope="global" id="share-header" chromeless="true"/>
   </div>
   </@>
   <@markup id="bd">
   <div id="bd">
      <div class="yui-t1" id="alfresco-console">
         <div id="yui-main">
            <div class="yui-b" id="alf-content">
               <@region id="ctool" scope="page" />
            </div>
         </div>
         <div class="yui-b" id="alf-filters">
            <@region id="tools" scope="page" />
         </div>
      </div>
      <@region id="html-upload" scope="template" />
      <@region id="flash-upload" scope="template" />
      <@region id="file-upload" scope="template" />
      <@region id="dnd-upload" scope="template"/>
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