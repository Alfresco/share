<#import "../generic-form-tool.lib.ftl" as gft>

<@gft.renderPanel config.script.config "workflow">
   <div class="workflow-tools">
      <h1 class="thin dark">${msg("tool.workflow.activiti.tools")}</h1>
      <a target="_blank" href=<#if activitiAdminUrl??>"${activitiAdminUrl?string}"<#else>"${url.context}/proxy/alfresco/activiti-admin"</#if>>${msg("tool.workflow.activiti.admin.link")}</a>
   </div>
</@>

<@link href="${page.url.context}/res/components/console/workflow.css" group="console"/>
