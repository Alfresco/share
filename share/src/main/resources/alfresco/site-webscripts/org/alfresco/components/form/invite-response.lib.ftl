<#macro renderInviteResponse outcome formUI formId>
   <#if form.mode =="edit" && formUI == "true">
      <@formLib.renderFormsRuntime formId=formId />
   </#if>
      
   <div class="form-container">
      
      <#if form.mode =="edit">
         <form id="${formId}" method="${form.method}" accept-charset="utf-8" enctype="${form.enctype}" action="${form.submissionUrl}">
      </#if>
      
      <div id="${formId}-fields" class="form-fields">
         <div class="yui-gc">
            <div class="yui-u first">
               <div class="invite-task-title">
                  <img src="${url.context}/res/components/images/site-24.png" />
                  <span>${msg("workflow.task.invite." + outcome, form.data["prop_inwf_inviteeFirstName"], form.data["prop_inwf_inviteeLastName"], form.data["prop_inwf_resourceTitle"])?html}</span>
               </div>
            </div>
            <div class="yui-u">
               <div class="invite-task-priority">
                  <img src="${url.context}/res/components/images/${getPriorityIcon()}" />
               </div>
            </div>
         </div>
         
         <#if form.mode =="edit">
            <div class="invite-task-subtitle">
               <@formLib.renderField field=form.fields["prop_transitions"] />
            </div>
         </#if>
      </div>
      
      <#if form.mode =="edit">
         <@formLib.renderFormButtons formId=formId />
         </form>
      </#if>
   </div>
</#macro>

<#macro hideSaveCloseButton formId>
<#if form.mode =="edit">
   <script type="text/javascript">//<![CDATA[
   (function()
   {
      YAHOO.util.Event.onContentReady("${formId}-submit-button", function()
      {
         YAHOO.util.Dom.setStyle("${formId}-submit", "display", "none");
      });
   })();
   //]]></script>
</#if>
</#macro>

<#function getPriorityIcon>
   <#assign priority=form.data["prop_bpm_priority"]?string />
   <#assign priorityMap={"1": "high", "2": "medium", "3": "low"} />
   <#return "priority-" + priorityMap[priority] + "-16.png">
</#function>
