<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <@markup id="resizer">
   <script type="text/javascript">//<![CDATA[
      new Alfresco.widget.Resizer("${page.id?js_string}").setOptions(
      {
         divLeft: "divLeft",
         divRight: "divRight",
         initialWidth: 215
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
      <div class="yui-t1">
         <div id="yui-main">
            <div id="divRight">
               <@region id="right-column" scope="page"/>
            </div>
         </div>
         <div id="divLeft">
            <@region id="left-column" scope="page"/>
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

