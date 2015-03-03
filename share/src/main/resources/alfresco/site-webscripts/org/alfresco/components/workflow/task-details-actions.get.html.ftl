<@markup id="css" >
   <#-- No CSS Dependencies -->
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/workflow/task-details-actions.js" group="workflow"/>
</@>

<@markup id="widgets">
   <@createWidgets group="workflow"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#include "../../include/alfresco-macros.lib.ftl" />
      <#assign el=args.htmlid?html>
      <div id="${el}-body" class="form-manager task-details-actions">
         <div class="actions hidden">
            <button id="${el}-edit">${msg("button.edit")}</button>
         </div>
      </div>
   </@>
</@>