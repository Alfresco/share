<#include "include/alfresco-template.ftl" />
<@templateHeader />

<@templateBody>
   <@markup id="alf-hd">
   <div id="alf-hd">
      <@region scope="global" id="share-header" chromeless="true"/>
   </div>
   </@>
   <@markup id="bd">
   <div id="bd">
      <div class="yui-gb">
         <div class="yui-u first">
         <@region id="left-column" scope="page"/>
         </div>
         <div class="yui-u">
         <@region id="middle-column" scope="page"/>
         </div>
         <div class="yui-u">
         <@region id="right-column" scope="page"/>
         </div>
      </div>
   </div>
   </@>
</@>

<@templateFooter>
   <@markup id="alf-ft">
   <div id="alf-ft">
      <@region id="footer" scope="global"/>
   </div>
   </@>
</@>