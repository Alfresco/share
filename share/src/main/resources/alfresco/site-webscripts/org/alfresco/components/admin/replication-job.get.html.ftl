<#include "../form/form.dependencies.inc">
<#assign jobName = page.url.args.jobName!"">
<#assign mode = (jobName = "")?string("create", "edit")>
<#assign action = url.context + "/proxy/alfresco/api/replication-definition" + (jobName = "")?string("s", "/" + jobName?url)>
<#assign intervalPeriods =
{
   "Second": msg("option.seconds"),
   "Minute": msg("option.minutes"),
   "Hour": msg("option.hours"),
   "Day": msg("option.days"),
   "Week": msg("option.weeks"),
   "Month": msg("option.months")
}>

<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/admin/replication-job.css" group="admin"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/modules/form/control-wrapper.js" group="admin"/>
   <@script type="text/javascript" src="${url.context}/res/components/admin/replication-job.js" group="admin"/>
</@>


<@markup id="widgets">
   <#if mode == "edit" && !(jobDetail.name??)>
      <div class="error">${msg("message.no-job-details", jobName)}</div>
   <#else>
      <@createWidgets group="admin"/>
   </#if>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      
      <#if mode == "edit" && !(jobDetail.name??)>
         <div class="error">${msg("message.no-job-details", jobName)}</div>
      <#else>
         <#assign id = args.htmlid?html>
         <div id="${id}-body" class="form-manager replication-job">
            <h1>${msg("header." + mode, jobName)}</h1>
         </div>
         <div class="share-form">
            <div class="form-container">
               <div class="caption"><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span>${msg("form.required.fields")}</div>
               <form id="${id}-form" method="post" action="${action}">
                  <div id="${id}-form-fields" class="form-fields">
                     <div class="set">
                        <div class="set-title">${msg("label.set.general")}</div>
                        <div class="form-field">
                           <label for="${id}-prop_name">${msg("label.name")}:<span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></label>
                           <input type="text" id="${id}-prop_name" name="name" tabindex="0" title="${msg("label.name")}" value="${(jobDetail.name!"")?html}" />
                        </div>
                        <div class="form-field">
                           <label for="${id}-prop_description">${msg("label.description")}:</label>
                           <textarea id="${id}-prop_description" name="description" rows="2" cols="60" tabindex="0" title="${msg("label.description")}">${(jobDetail.description!"")?html}</textarea>
                        </div>
                     </div>
                     <div class="set">
                        <div class="set-title">${msg("label.set.payload")}</div>
                        <div id="${id}-payloadContainer"></div>
                     </div>
                     <div class="set">
                        <div class="set-title">${msg("label.set.transfer-target")}</div>
                        <div id="${id}-transferTargetContainer"></div>
                     </div>
                    
                     <div class="set">
                        <div class="set-title">${msg("label.set.schedule")}</div>
                        <div class="form-field">
                           <input id="${id}-scheduleEnabled" class="formsCheckBox" type="checkbox" tabindex="0" name="-" title="${msg("label.schedule-job")}" <#if jobDetail.schedule??>checked="checked"</#if>>
                           <label for="${id}-scheduleEnabled" class="checkbox">${msg("label.schedule-job")}</label>
                        </div>
                        <div id="${id}-scheduleContainer" class="hidden">
                           <div id="${id}-scheduleStartContainer"></div>
                           <div class="form-field">
                              <label for="${id}-prop_intervalCount">${msg("label.repeat")}:<span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></label>
                              <input type="text" id="${id}-prop_intervalCount" name="schedule.intervalCount" tabindex="0" class="number" value="${((jobDetail.schedule.intervalCount)!"")?html}" />
                              <select id="${id}-prop_intervalPeriod" name="schedule.intervalPeriod" tabindex="0">
                                 <option value="-">${msg("option.none")}</option>
                                 <#list intervalPeriods?keys as ip>
                                    <option value="${ip?html}"<#if ip == (jobDetail.schedule.intervalPeriod)!""> selected="selected"</#if>>${(intervalPeriods[ip])?html}</option>
                                 </#list>
                              </select>
                           </div>
                        </div>
                     </div>
                     <div class="set">
                        <div class="set-title">${msg("label.set.other")}</div>
                        <div class="form-field">
                           <input id="${id}-prop_enabled" type="hidden" name="enabled" value="${(jobDetail.enabled!false)?string}">
                           <input id="${id}-prop_enabled-entry" class="formsCheckBox" type="checkbox" tabindex="0" name="-" title="${msg("label.enabled")}" <#if jobDetail.enabled!false>checked="checked"</#if> onchange="javascript:YUIDom.get('${id}-prop_enabled').value=this.checked;">
                           <label for="${id}-prop_enabled-entry" class="checkbox">${msg("label.enabled")}</label>
                        </div>
                     </div>
                  </div>
                  <div id="${id}-form-buttons" class="form-buttons">
                     <span id="${id}-form-submit" class="yui-button yui-submit-button">
                        <span class="first-child">
                           <button type="button" tabindex="0" name="-">${msg("button." + mode + "-job")}</button>
                        </span>
                     </span>
                     &nbsp;
                     <span id="${id}-form-cancel" class="yui-button yui-push-button">
                        <span class="first-child">
                           <button type="button" tabindex="0" name="-">${msg("button.cancel")}</button>
                        </span>
                     </span>
                  </div>
               </form>
            </div>
         </div>
      </#if>
   </@>
</@>

