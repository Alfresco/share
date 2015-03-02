<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <@markup id="resizer">
   <script type="text/javascript">//<![CDATA[
      new Alfresco.widget.Resizer("Calendar").setOptions(
      {
         divLeft: "divCalendarFilters",
         divRight: "divCalendarContent",
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
      <div class="yui-t1" id="alfresco-calendar">
         <div id="yui-main">
            <div id="divCalendarContent">
               <@region id="toolbar" scope="template" class="toolbar" />
               <@region id="view" scope="template" class="view" />
            </div>
         </div>
         <div id="divCalendarFilters">
            <@region id="calendar" scope="template" />
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

