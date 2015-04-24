<#if field.value == "">
   <#assign displayValue=msg("form.control.novalue") />
<#else>
   <#assign parts=field.value?split("|") />
   <#assign displayValue=parts[0] />
</#if>
   
<div class="form-field">
   <div class="viewmode-field">
      <span class="viewmode-label">${field.label?html}:</span>
      <span class="viewmode-value">${displayValue}</span>
   </div>
</div>