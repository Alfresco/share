<div class="form-field">
   <#if form.mode == "view">
      <div class="viewmode-field">
         <span class="viewmode-label">${field.label?html}:</span>
         <#if field.value?is_number>
            <#assign fieldValue=field.value?c>
         <#else>
            <#assign fieldValue=field.value?html>
         </#if>
         <span class="viewmode-value"><#if fieldValue == "">${msg("form.control.novalue")}<#else>${fieldValue}</#if></span>
      </div>
   <#else>
      <#assign fieldValue = "">
      <#if field.control.params.contextProperty??>
         <#if context.properties[field.control.params.contextProperty]??>
            <#assign fieldValue = context.properties[field.control.params.contextProperty]>
         <#elseif args[field.control.params.contextProperty]??>
            <#assign fieldValue = args[field.control.params.contextProperty]>
         </#if>
      <#elseif context.properties[field.name]??>
         <#assign fieldValue = context.properties[field.name]>
      <#else>
         <#assign fieldValue = field.value>
      </#if>

      <label for="${fieldHtmlId}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
      <input id="${fieldHtmlId}" name="${field.name}" tabindex="0"
             <#if field.control.params.password??>type="password"<#else>type="text"</#if>
             <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
             <#if field.control.params.style??>style="${field.control.params.style}"</#if>
             <#if fieldValue?is_number>value="${fieldValue?c}"<#else>value="${fieldValue?html}"</#if>
             <#if field.description??>title="${field.description}"</#if>
             <#if field.control.params.maxLength??>maxlength="${field.control.params.maxLength}"</#if>
             <#if field.control.params.size??>size="${field.control.params.size}"</#if> 
             <#if field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if> />
      <@formLib.renderFieldHelp field=field />
   </#if>
</div>
