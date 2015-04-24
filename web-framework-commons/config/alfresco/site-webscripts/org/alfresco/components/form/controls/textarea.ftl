<#if field.control.params.rows??><#assign rows=field.control.params.rows><#else><#assign rows=3></#if>
<#if field.control.params.columns??><#assign columns=field.control.params.columns><#else><#assign columns=60></#if>

<div class="form-field alf-textarea">
   <#if form.mode == "view">
   <div class="viewmode-field">
      <#if field.mandatory && field.value == "">
      <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
      </#if>
      <span class="viewmode-label">${field.label?html}:</span>
         <#assign tmpFieldValue=field.value?html?replace("\n", "<br>")>
         <#if field.control.params.saveLineBreaks?? && field.control.params.saveLineBreaks == "false">
            <#assign tmpFieldValue=field.value?html>
         </#if>
      <#if field.control.params.activateLinks?? && field.control.params.activateLinks == "true">
            <#assign fieldValue=tmpFieldValue?replace("((http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?\\^=%&:\\/~\\+#]*[\\w\\-\\@?\\^=%&\\/~\\+#])?)", "<a href=\"$1\" target=\"_blank\">$1</a>", "r")>
      <#else>
            <#assign fieldValue=tmpFieldValue>
      </#if>
      <span class="viewmode-value"><#if fieldValue == "">${msg("form.control.novalue")}<#else>${fieldValue}</#if></span>
   </div>
   <#else>
   <label for="${fieldHtmlId}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
   <@formLib.renderFieldHelp field=field />
   <textarea id="${fieldHtmlId}" name="${field.name}" rows="${rows}" cols="${columns}" tabindex="0"
      <#if field.description??>title="${field.description}"</#if>
      <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
      <#if field.control.params.style??>style="${field.control.params.style}"</#if>
      <#if field.control.params.maxLength??>maxlength="${field.control.params.maxLength}"<#else>maxlength="1024"</#if>
      <#if field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if>>${field.value?html}</textarea>
   </#if>
</div>