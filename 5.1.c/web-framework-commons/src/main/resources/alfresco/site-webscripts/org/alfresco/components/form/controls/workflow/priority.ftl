<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />
<div class="form-field">
   <#if form.mode == "view" || (form.mode == "edit" && field.disabled)>
      <div class="viewmode-field">
         <span class="viewmode-label">${field.label?html}:</span>
         <span class="viewmode-value">
         <#if field.value?string == "1">${msg("priority.high")}
         <#elseif field.value?string == "2">${msg("priority.medium")}
         <#elseif field.value?string == "3">${msg("priority.low")}
         <#else>${field.value?html}</#if>
         </span>
      </div>
   <#else>
      <label for="${fieldHtmlId}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
      <select id="${fieldHtmlId}" name="${field.name}" tabindex="0" size="1"
            <#if field.description??>title="${field.description}"</#if>
            <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
            <#if field.control.params.style??>style="${field.control.params.style}"</#if>
            <#if field.disabled>disabled="true"</#if>>
            <option value="1"<#if field.value?string == "1"> selected="selected"</#if>>${msg("priority.high")}</option>
            <option value="2"<#if field.value?string == "2"> selected="selected"</#if>>${msg("priority.medium")}</option>
            <option value="3"<#if field.value?string == "3"> selected="selected"</#if>>${msg("priority.low")}</option>
      </select>
   </#if>
</div>