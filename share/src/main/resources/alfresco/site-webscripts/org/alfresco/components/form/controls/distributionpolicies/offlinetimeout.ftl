<div class="form-field">
   <#if form.mode == "view">
      <div class="viewmode-field">
         <span class="viewmode-label">${field.label?html}:</span>
         <span class="viewmode-value"><#if field.value?is_number>${field.value/3600000}<#elseif field.value == "">${msg("form.control.novalue")}<#else>${field.value?html}</#if></span>
      </div>
   <#else>
      <label for="${fieldHtmlId}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
      <input id="${fieldHtmlId}" type="hidden" name="${field.name}" value="${field.value?c}" />
      <input id="${fieldHtmlId}-entry" type="text" name="-" tabindex="0"
             class="number<#if field.control.params.styleClass??> ${field.control.params.styleClass}</#if>"
             <#if field.control.params.style??>style="${field.control.params.style}"</#if>
             <#if field.value?is_number>value="${field.value/3600000}"<#else>value="${field.value?html}"</#if>
             <#if field.description??>title="${field.description}"</#if>
             <#if field.control.params.maxLength??>maxlength="${field.control.params.maxLength}"</#if> 
             <#if field.control.params.size??>size="${field.control.params.size}"</#if> 
             <#if field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if>
             onchange='javascript:if(!isNaN(YAHOO.util.Dom.get("${fieldHtmlId}-entry").value)){YAHOO.util.Dom.get("${fieldHtmlId}").value=YAHOO.util.Dom.get("${fieldHtmlId}-entry").value*3600000;}' />
      <@formLib.renderFieldHelp field=field />
   </#if>
</div>