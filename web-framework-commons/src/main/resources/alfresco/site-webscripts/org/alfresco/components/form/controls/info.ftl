<#if field.value?is_number>
   <#assign displayValue=field.value?c />
<#elseif field.value?is_boolean>
   <#if field.value>
      <#assign displayValue=msg("form.control.checkbox.yes") />
   <#else>
      <#assign displayValue=msg("form.control.checkbox.no") />
   </#if>
<#else>
   <#if field.value == "">
      <#assign displayValue=msg("form.control.novalue") />
   <#else>   
      <#assign displayValue=field.value?html />
   </#if>
</#if>

<div class="form-field">
   <div class="viewmode-field">
      <span class="viewmode-label">${field.label?html}:</span>
      <span class="viewmode-value" data-dataType="${field.dataType}">${displayValue}</span>
   </div>
</div>