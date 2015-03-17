<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/console/replication-jobs.css" group="console"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/console/consoletool.js" group="console"/>
   <@script src="${url.context}/res/components/console/replication-jobs.js" group="console"/>
</@>

<@markup id="widgets">
   <@createWidgets group="console"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <!--[if IE]>
      <iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe> 
      <![endif]-->
      <input id="yui-history-field" type="hidden" />
      
      <#assign id = args.htmlid?html>
      <div id="${id}-body" class="replication">
      
         <!-- Main panel -->
         <div id="${id}-replication" class="hidden">
      
            <#-- Summary -->
            <div id="${id}-summary" class="summary-panel">
               <h2>${msg("header.summary")}</h2>
               <div class="container-panel">
                  <div id="${id}-jobSummary" class="job-summary"></div>
               </div>
            </div>
            
            <#-- Jobs -->
            <div id="${id}-jobs">
               <h2>${msg("header.jobs")}</h2>
               <div class="yui-gd">
                  
                  <div class="yui-u first jobs-list-container">
                     <div class="container-panel">
                        <div class="bottom-border">
                           <span id="${id}-create" class="yui-button yui-push-button">
                              <span class="first-child">
                                 <a tabindex="0" href="${url.context}/page/console/replication-job">${msg("button.create-job")}</a>
                              </span>
                           </span>
                           <input type="button" id="${id}-sortBy" value="${msg("button.sort-by", msg("label.sort-by.status"))}" />
                           <select id="${id}-sortBy-menu">
                               <option value="status">${msg("label.sort-by.status")}</option>
                               <option value="name">${msg("label.sort-by.name")}</option>
                               <option value="lastRun">${msg("label.sort-by.last-run-date")}</option>
                           </select>                  
                        </div>
                        <div id="${id}-jobsList" class="jobs-list"></div>
                     </div>
                  </div>
                  
                  <div class="yui-u job-detail-container">
                     <div id="${id}-jobDetailContainer" class="container-panel">
                        <div class="job-buttons" style="float: right;">
                           <span id="${id}-run" class="yui-button yui-push-button">
                              <span class="first-child">
                                 <button type="button" tabindex="0">${msg("button.run-job")}</button>
                              </span>
                           </span>
                           <span id="${id}-cancel" class="yui-button yui-push-button">
                              <span class="first-child">
                                 <button type="button" tabindex="0">${msg("button.cancel-job")}</button>
                              </span>
                           </span>
                           <span id="${id}-edit" class="yui-button yui-push-button">
                              <span class="first-child">
                                 <button type="button" tabindex="0">${msg("button.edit-job")}</button>
                              </span>
                           </span>
                           <span id="${id}-delete" class="yui-button yui-push-button">
                              <span class="first-child">
                                 <button type="button" tabindex="0">${msg("button.delete-job")}</button>
                              </span>
                           </span>
                        </div>
                        <div id="${id}-jobDetail" class="job-detail">
                           <div class="message">${msg("label.no-job-selected")}</div>
                        </div>
                     </div>
                  </div>
               </div>
            </div>
      
         </div>
         
         <div id="${id}-jobTemplate" style="display:none;">
            <h2>{name}</h2>
            <div>{description}</div>
            <div class="{enabledClass}">{enabledText}</div>
            <hr />
            <div style="float: right;">
               <span id="${id}-refresh" class="yui-button yui-push-button">
                  <span class="first-child">
                     <button type="button" tabindex="0">${msg("button.refresh")}</button>
                  </span>
               </span>
               <span id="${id}-viewReportLocal" class="yui-button yui-button-disabled yui-link-button">
                  <span class="first-child">
                     <a tabindex="0" href="{viewReportLocalLink}">${msg("button.view-report.local")}</a>
                  </span>
               </span>
               <span id="${id}-viewReportRemote" class="yui-button yui-button-disabled yui-link-button">
                  <span class="first-child">
                     <a tabindex="0" href="{viewReportRemoteLink}">${msg("button.view-report.remote")}</a>
                  </span>
               </span>
            </div>
            <div>
               <h3>${msg("label.status")}</h3>
               <div id="${id}-jobStatus" class="job-status">{statusText}</div>
            </div>
            <hr />
            <h3>${msg("label.schedule")}</h3>
            <div class="schedule">{scheduleHTML}</div>
            <hr />
            <h3>${msg("label.transfer-target")}</h3>
            <div class="transfer-target">
               <div class="{targetNameClass}">{targetHTML}</div>
            </div>
            <hr />
            <h3>${msg("label.payload")}</h3>
            <div class="payload">{payloadHTML}</div>
         </div>
      
      </div>
   </@>
</@>

