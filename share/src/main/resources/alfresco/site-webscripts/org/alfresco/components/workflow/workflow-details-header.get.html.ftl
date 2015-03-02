<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/workflow/workflow-details-header.css" group="workflow"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/workflow/workflow-details-header.js" group="workflow"/>
</@>

<@markup id="widgets">
   <@createWidgets group="workflow"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#include "../../include/alfresco-macros.lib.ftl" />
      <#assign el=args.htmlid?html>
      <div id="${el}-body" class="form-manager workflow-details-header">
         <#if page.url.args.taskId??>
         <div class="links">
            <#assign referrer><#if page.url.args.referrer??>&referrer=${page.url.args.referrer?url}</#if></#assign>
            <#assign nodeRef><#if page.url.args.nodeRef??>&nodeRef=${page.url.args.nodeRef?url}</#if></#assign>
            <a href="${siteURL("task-details?taskId=" + page.url.args.taskId?url + referrer + nodeRef)}">${msg("label.taskDetails")}</a>
            <span class="separator">|</span>
            <span class="theme-color-2">${msg("label.workflowDetails")}</span>
         </div>
         </#if>
         <h1>${msg("header")}: <span></span></h1>
         <div class="clear"></div>
      </div>
   </@>
</@>