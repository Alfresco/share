<@markup id="css" >
   <#-- CSS Dependencies -->
   <#include "../form/form.css.ftl"/>
   <@link href="${url.context}/res/components/workflow/start-workflow.css" group="workflow"/>
   
   <#-- Global Folder Picker (req'd by Copy/Move To) -->
   <@link rel="stylesheet" type="text/css" href="${page.url.context}/res/modules/documentlibrary/global-folder.css" />
   
   <#-- Cloud Authentication -->
   <@link rel="stylesheet" type="text/css" href="${page.url.context}/res/modules/cloud/cloud-auth-form.css" />
   <#-- Cloud Folder Picker -->
   <@link rel="stylesheet" type="text/css" href="${page.url.context}/res/modules/cloud/cloud-folder-picker.css" />
   <#-- Cloud Sync Status -->
   <@link rel="stylesheet" type="text/css" href="${page.url.context}/res/modules/cloud/cloud-sync-status.css" />
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <#include "../form/form.js.ftl"/>
   <@script src="${url.context}/res/components/workflow/start-workflow.js" group="workflow"/>
   <@script src="${url.context}/res/modules/documentlibrary/doclib-actions.js" group="workflow" />
   <@script src="${url.context}/res/modules/simple-dialog.js" group="workflow"/>
   <@script src="${url.context}/res/modules/documentlibrary/global-folder.js" group="workflow" />
   <@script src="${url.context}/res/modules/documentlibrary/cloud-folder.js" group="workflow" />
   <@script src="${url.context}/res/modules/documentlibrary/cloud-folder.js" group="workflow" />
   <@script src="${url.context}/res/modules/cloud-auth.js" group="workflow" />
   <@script src="${url.context}/res/components/common/common-component-style-filter-chain.js" group="workflow"/>
</@>

<@markup id="widgets">
   <@createWidgets group="workflow"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#include "../../include/alfresco-macros.lib.ftl" />
      <#assign el=args.htmlid?html>
      <div id="${el}-body" class="form-manager start-workflow">
         <div>
            <label for="${el}-workflowDefinitions" class="workflow-definition">${msg("label.workflow")}:</label>
            <#-- Workflow type menu button  -->
            <span class="selected-form-button">
               <span id="${el}-workflow-definition-button" class="yui-button yui-menu-button">
                  <span class="first-child">
                     <button type="button" tabindex="0"></button>
                  </span>
               </span>
            </span>
            <#-- Workflow type menu -->
            <div id="${el}-workflow-definition-menu" class="yuimenu" style="visibility:hidden">
               <div class="bd">
                  <ul>
                     <#list workflowDefinitions as workflowDefinition>
                     <li>
                        <span class="title" tabindex="0">${workflowDefinition.title!workflowDefinition.id?html}</span>
                        <span class="description">${(workflowDefinition.description!"")?html}</span>
                     </li>
                     </#list>
                  </ul>
               </div>
            </div>
         </div>
      </div>
      <div id="${el}-workflowFormContainer"></div>
   </@>
</@>