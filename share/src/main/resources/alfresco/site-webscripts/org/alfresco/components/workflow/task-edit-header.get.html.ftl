<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/people-finder/people-finder.css" group="workflow"/>
   <@link href="${url.context}/res/components/workflow/task-edit-header.css" group="workflow"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/people-finder/people-finder.js" group="workflow"/>
   <@script src="${url.context}/res/components/workflow/task-edit-header.js" group="workflow"/>
</@>

<@markup id="widgets">
   <@createWidgets group="workflow"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#include "../../include/alfresco-macros.lib.ftl" />
      <#assign el=args.htmlid?html>
      <div id="${el}-body" class="form-manager task-edit-header">
         <div class="actions">
            <span class="claim hidden">
               <button id="${el}-claim">${msg("button.claim")}</button>
            </span>
            <span class="reassign hidden">
               <button id="${el}-reassign">${msg("button.reassign")}</button>
            </span>
            <span class="release hidden">      
               <button id="${el}-release">${msg("button.release")}</button>
            </span>
         </div>
         <h1>${msg("header")}: <span></span></h1>
         <div class="clear"></div>
         <div class="unassigned-message hidden theme-bg-color-2 theme-border-4"><span>${msg("message.unassigned")}</span></div>
         
         <!-- People Finder Dialog -->
         <div style="display: none;">
            <div id="${el}-reassignPanel" class="task-edit-header reassign-panel">
               <div class="hd">${msg("panel.reassign.header")}</div>
               <div class="bd">
                  <div style="margin: auto 10px;">
                     <div id="${el}-peopleFinder"></div>
                  </div>
               </div>
            </div>
         </div>
      </div>
   </@>
</@>