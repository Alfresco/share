<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/document-details/document-workflows.css" group="document-details"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/document-details/document-workflows.js" group="document-details"/>
</@>

<@markup id="widgets">
   <#if workflows??>
      <@createWidgets group="document-details"/>
      <@inlineScript group="document-details">
         YAHOO.util.Event.onContentReady("${args.htmlid?js_string}-heading", function() {
            Alfresco.util.createTwister("${args.htmlid?js_string}-heading", "DocumentWorkflows");
         });
      </@>
   </#if>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#if workflows??>
         <#include "../../include/alfresco-macros.lib.ftl" />
         <#assign el=args.htmlid?html>
         <div id="${el}-body" class="document-workflows document-details-panel">
            <h2 id="${el}-heading" class="thin dark">
               ${msg("header.workflows")}
               <span class="alfresco-twister-actions">
                  <a href="#" name=".onAssignWorkflowClick" class="${el} edit" title="${msg("label.assignWorkflow")}">&nbsp;</a>
               </span>
            </h2>
            <div class="panel-body">
               <div class="info">
               <#if workflows?size == 0>
                  ${msg("label.partOfNoWorkflows")}
               <#else>
                  ${msg("label.partOfWorkflows")}
               </#if>
               </div>
               <#if workflows?size &gt; 0>
               <hr/>
               <div class="workflows">
                  <#list workflows as workflow>
                     <div class="workflow <#if !workflow_has_next>workflow-last</#if>">
                        <#if workflow.initiator?? && workflow.initiator.avatarUrl??>
                        <img src="${url.context}/proxy/alfresco/${workflow.initiator.avatarUrl}" alt="${msg("label.avatar")}"/>
                        <#else>
                        <img src="${url.context}/res/components/images/no-user-photo-64.png" alt="${msg("label.avatar")}"/>
                        </#if>
                        <a href="${siteURL("workflow-details?workflowId=" + workflow.id?js_string + "&nodeRef=" + (args.nodeRef!"")?js_string)}"><#if workflow.message?? && workflow.message?length &gt; 0>${workflow.message?html}<#else>${msg("workflow.no_message")?html}</#if></a>
                        <div class="title">${workflow.title?html}</div>
                        <div class="clear"></div>
                     </div>
                  </#list>
               </div>
               </#if>
            </div>
         </div>
      </#if>
   </@>
</@>