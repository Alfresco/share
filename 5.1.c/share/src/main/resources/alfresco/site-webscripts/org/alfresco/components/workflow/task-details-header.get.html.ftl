<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/workflow/task-details-header.css" group="workflow"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/workflow/task-details-header.js" group="workflow"/>
</@>

<@markup id="widgets">
   <@createWidgets group="workflow"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid?html>
      <div id="${el}-body" class="form-manager task-details-header">
         <div class="links hidden">
            <span class="theme-color-2">${msg("label.taskDetails")}</span>
            <span class="separator">|</span>
            <a href="">${msg("label.workflowDetails")}</a>
         </div>
         <h1>${msg("header")}: <span></span></h1>
         <div class="clear"></div>
      </div>
   </@>
</@>