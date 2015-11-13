<@markup id="css" >
   <#-- CSS Dependencies -->
   <#include "../form/form.css.ftl"/>
   <@link href="${url.context}/res/components/workflow/workflow-form.css" group="workflow"/>
   
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
   <@script src="${url.context}/res/components/workflow/workflow-form.js" group="workflow"/>
   
   <@script src="${url.context}/res/modules/documentlibrary/doclib-actions.js" group="workflow" />
   <@script src="${url.context}/res/modules/simple-dialog.js" group="workflow"/>
   <@script src="${url.context}/res/modules/documentlibrary/global-folder.js" group="workflow" />
   <@script src="${url.context}/res/modules/documentlibrary/cloud-folder.js" group="workflow" />
   <@script src="${url.context}/res/modules/cloud-auth.js" group="workflow" />
   <@script src="${url.context}/res/components/common/common-component-style-filter-chain.js" group="workflow"/>
</@>

<@markup id="widgets">
   <@createWidgets group="workflow"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid?html>
      <div id="${el}-body" class="workflow-form"></div>
      <div class="hidden">
      <#--
       The workflow details page form is actually a form display of the workflow's start task AND data from the workflow itself.
       The approach taken to mix all this information is described in the Alfresco.WorkflowForm javascript class.
      -->
         <#-- Will be inserted in the top of the form after its been loaded through ajax -->
         <div id="${el}-summary-form-section">
            <h3>
               ${msg("header.workflowSummary")}
            </h3>
            <div class="workflow-summary-buttons">
               <button id="${el}-viewWorkflowDiagram" class="hidden">${msg("button.viewWorkflowDiagram")}</button>
            </div>
            <div class="form-element-background-color form-element-border summary">
               <div class="summary-icons">
                  <h3>${msg("label.general")}</h3>
                  <div id="${el}-statusSummary" class="status"></div>
                  <div id="${el}-dueSummary" class="due"></div>
                  <div id="${el}-prioritySummary" class="priority"></div>
               </div>
               <div class="recent-task form-element-border">
                  <div class="yui-gc">
                     <div class="yui-u first">
                        <h3>${msg("label.mostRecentlyCompletedTask")}</h3>
                     </div>
                     <div class="yui-u current-tasks">
                        <a href="#current-tasks">${msg("link.viewCurrentTasks")}</a>
                     </div>
                  </div>
      
                  <div>
                     <a id="${el}-recentTaskTitle" href=""></a>
                  </div>
      
                  <div class="yui-gb">
                     <div class="yui-u first">
                        <span class="viewmode-label">${msg("label.completedOn")}:</span>
                        <span class="viewmode-value" id="${el}-recentTaskCompletedOn"></span>
                     </div>
                     <div class="yui-u">
                        <span class="viewmode-label">${msg("label.completedBy")}:</span>
                        <span class="viewmode-value" id="${el}-recentTaskCompletedBy"></span>
                     </div>
                     <div class="yui-u">
                        <span class="viewmode-label">${msg("label.outcome")}:</span>
                        <span class="viewmode-value" id="${el}-recentTaskOutcome"></span>
                     </div>
                  </div>
      
                  <div class="yui-gf">
                     <div class="yui-u first avatar">
                        <img id="${el}-recentTaskOwnersAvatar" src="" alt="${msg("label.avatar")}">
                     </div>
                     <div class="yui-u">
                        <div id="${el}-recentTaskOwnersCommentLink"></div>
                        <div id="${el}-recentTaskOwnersComment" class="task-comment form-element-border"></div>
                     </div>
                  </div>
               </div>
               <div class="clear"></div>
            </div>
      
         </div>
      
         <#-- Will be inserted above "More Info" in the form after its been loaded through ajax -->
         <div id="${el}-general-form-section">
            <div class="set">
               <div class="set-title">${msg("header.generalInfo")}</div>
               <div class="form-field">
                  <div class="viewmode-field">
                     <span class="viewmode-label">${msg("label.title")}:</span>
                     <span class="viewmode-value" id="${el}-title"></span>
                  </div>
               </div>
               <div class="form-field">
                  <div class="viewmode-field">
                     <span class="viewmode-label">${msg("label.description")}:</span>
                     <span class="viewmode-value" id="${el}-description"></span>
                  </div>
               </div>
               <div class="yui-gb">
                  <div class="yui-u first">
                     <div class="form-field">
                        <div class="viewmode-field">
                           <span class="viewmode-label">${msg("label.startedBy")}:</span>
                           <span class="viewmode-value" id="${el}-startedBy"></span>
                        </div>
                     </div>
                  </div>
                  <div class="yui-u">
                     <div class="form-field">
                        <div class="viewmode-field">
                           <span class="viewmode-label">${msg("label.due")}:</span>
                           <span class="viewmode-value" id="${el}-due"></span>
                        </div>
                     </div>
                  </div>
                  <div class="yui-u">
                     <div class="form-field">
                        <div class="viewmode-field">
                           <span class="viewmode-label">${msg("label.completed")}:</span>
                           <span class="viewmode-value" id="${el}-completed"></span>
                        </div>
                     </div>
                  </div>
               </div>
               <div class="yui-gb">
                  <div class="yui-u first">
                     <div class="form-field">
                        <div class="viewmode-field">
                           <span class="viewmode-label">${msg("label.started")}:</span>
                           <span class="viewmode-value" id="${el}-started"></span>
                        </div>
                     </div>
                  </div>
                  <div class="yui-u">
                     <div class="form-field">
                        <div class="viewmode-field">
                           <span class="viewmode-label">${msg("label.priority")}:</span>
                           <span class="viewmode-value" id="${el}-priority"></span>
                        </div>
                     </div>
                  </div>
                  <div class="yui-u">
                     <div class="form-field">
                        <div class="viewmode-field">
                           <span class="viewmode-label">${msg("label.status")}:</span>
                           <span class="viewmode-value" id="${el}-status"></span>
                        </div>
                     </div>
                  </div>
               </div>
               <div class="form-field">
                  <div class="viewmode-field">
                     <span class="viewmode-label">${msg("label.message")}:</span>
                     <span class="viewmode-value" id="${el}-message"></span>
                  </div>
               </div>
            </div>
         </div>
      
         <#-- Will be inserted below "Items" in the form after its been loaded through ajax -->
         <div id="${el}-currentTasks-form-section" class="current-tasks">
            <a name="current-tasks"></a>
            <h3>${msg("header.currentTasks")}</h3>
            <div class="form-element-background-color"></div>
         </div>
      
         <#-- Will be inserted in the bottom of the form after its been loaded through ajax -->
         <div id="${el}-workflowHistory-form-section" class="workflow-history">
            <h3>${msg("header.history")}</h3>
            <div class="form-element-background-color"></div>
         </div>
      </div>
   </@>
</@>
