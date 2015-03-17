<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/workflow/task-list-toolbar.css" group="workflow"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/workflow/task-list-toolbar.js" group="workflow"/>
</@>

<@markup id="widgets">
   <@createWidgets group="workflow"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid?html>
      <div id="${el}-body" class="task-list-toolbar toolbar">
         <div id="${el}-headerBar" class="header-bar flat-button theme-bg-2">
            <div class="left">
               <div class="hideable hidden">
                  <div class="start-workflow"><button id="${el}-startWorkflow-button" name="startWorkflow">${msg("button.startWorkflow")}</button></div>
               </div>
            </div>
            <div class="right">
            </div>
         </div>
      </div>
   </@>
</@>