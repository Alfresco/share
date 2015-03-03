<#assign id=args.htmlid?html>
<#if formUI == "true">
   <@formLib.renderFormsRuntime formId=formId />
</#if>

<div id="${id}-dialog">
   <div id="${id}-dialogTitle" class="hd">${msg("title")}</div>
   <div class="bd">

      <div id="${formId}-container" class="form-container">

<#-- No full-page edit view for v3.3-
   <#if form.mode == "edit">
         <div class="yui-u first edit-dataitem flat-button">
            <button id="${id}-editDataItem" tabindex="0"></button>
         </div>
   </#if>
-->
         <#if form.showCaption?exists && form.showCaption>
            <div id="${formId}-caption" class="caption"><span class="mandatory-indicator">*</span>${msg("form.required.fields")}</div>
         </#if>
      
         <form id="${formId}" method="${form.method}" accept-charset="utf-8" enctype="${form.enctype}" action="${form.submissionUrl}">
   
         <#if form.destination??>
            <input id="${formId}-destination" name="alf_destination" type="hidden" value="${form.destination?html}" />
         </#if>
   
            <div id="${formId}-fields" class="form-fields">

               <@formLib.renderField field=form.fields["prop_cm_title"] />
               <@formLib.renderField field=form.fields["prop_cm_description"] />
               
               <div class="yui-g">
                  <div class="yui-u first" style="padding:0; text-align:left;">
                     <@formLib.renderField field=form.fields["prop_dl_ganttStartDate"] />
                  </div>
                  <div class="yui-u" style="padding:0;">
                     <@formLib.renderField field=form.fields["prop_dl_ganttEndDate"] />
                  </div>
               </div>

               <@formLib.renderField field=form.fields["assoc_dl_taskAssignee"] />

               <div class="yui-g">
                  <div class="yui-u first" style="padding:0; text-align:left;">
                     <@formLib.renderField field=form.fields["prop_dl_taskPriority"] />
                  </div>
                  <div class="yui-u" style="padding:0;">
                     <@formLib.renderField field=form.fields["prop_dl_taskStatus"] />
                  </div>
               </div>

               <@formLib.renderField field=form.fields["prop_dl_ganttPercentComplete"] />
               <@formLib.renderField field=form.fields["prop_dl_taskComments"] />
               <@formLib.renderField field=form.fields["assoc_cm_attachments"] />

            </div>

            <div class="bdft">
               <input id="${formId}-submit" type="submit" value="${msg("form.button.submit.label")}" />
               &nbsp;<input id="${formId}-cancel" type="button" value="${msg("form.button.cancel.label")}" />
            </div>
      
         </form>

      </div>
   </div>
</div>