<#assign controlId = fieldHtmlId + "-cntrl">

<script type="text/javascript">//<![CDATA[
(function()
{
   new Alfresco.Period("${controlId}", "${fieldHtmlId}").setOptions(
   {
      <#if form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>disabled: true,</#if>
      currentValue: "${field.value?js_string}",
      mandatory: ${field.mandatory?string},
      data : ${field.control.params.dataTypeParameters}
   }).setMessages(
      ${messages}
   );
})();
//]]></script>

<div class="form-field">
   <#if form.mode == "view">
      <div class="viewmode-field">
         <span class="viewmode-label">${field.label?html}:</span>
         <span class="viewmode-value" id="${controlId}"></span>
      </div>
   <#else>
      <label for="${fieldHtmlId}-when">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
      <input id="${fieldHtmlId}" name="${field.name}" type="hidden" value="${field.value?html}"/>
      <div id="${controlId}" class="period">
         <span>${msg("form.control.period.type")}</span><select id="${controlId}-type" name="-" tabindex="0"></select>
         <span>${msg("form.control.period.expression")}</span><input id="${controlId}-expression" name="-" type="text" tabindex="0" />
         <@formLib.renderFieldHelp field=field />
      </div>
   </#if>
</div>
     