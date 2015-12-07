<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />
<#if field.control.params.size??><#assign size=field.control.params.size><#else><#assign size=5></#if>

<#if field.control.params.optionSeparator??>
   <#assign optionSeparator=field.control.params.optionSeparator>
<#else>
   <#assign optionSeparator=",">
</#if>
<#if field.control.params.labelSeparator??>
   <#assign labelSeparator=field.control.params.labelSeparator>
<#else>
   <#assign labelSeparator="|">
</#if>

<#assign fieldValue=field.value>

<#if fieldValue?string == "" && field.control.params.defaultValueContextProperty??>
   <#if context.properties[field.control.params.defaultValueContextProperty]??>
      <#assign fieldValue = context.properties[field.control.params.defaultValueContextProperty]>
   <#elseif args[field.control.params.defaultValueContextProperty]??>
      <#assign fieldValue = args[field.control.params.defaultValueContextProperty]>
   </#if>
</#if>

<#if fieldValue?string != "">
   <#assign values=fieldValue?split(",")>
<#else>
   <#assign values=[]>
</#if>

<div class="form-field">
   <#if form.mode == "view">
      <div class="viewmode-field">
         <#if field.mandatory && !(fieldValue?is_number) && fieldValue?string == "">
            <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
         </#if>
         <span class="viewmode-label">${field.label?html}:</span>
         <#if fieldValue?string == "">
            <#assign valueToShow=msg("form.control.novalue")>
         <#else>
            <#if field.control.params.options?? && field.control.params.options != "" &&
                 field.control.params.options?index_of(labelSeparator) != -1>
                 <#assign valueToShow="">
                 <#assign firstLabel=true>
                 <#list field.control.params.options?split(optionSeparator) as nameValue>
                    <#assign choice=nameValue?split(labelSeparator)>
                    <#if isSelected(choice[0])>
                       <#if !firstLabel>
                          <#assign valueToShow=valueToShow+",">
                       <#else>
                          <#assign firstLabel=false>
                       </#if>
                       <#assign valueToShow=valueToShow+choice[1]>
                    </#if>
                 </#list>
            <#else>
               <#assign valueToShow=fieldValue>
            </#if>
         </#if>
         <span class="viewmode-value">${valueToShow?html}</span>
      </div>
   <#else>
      <label for="${fieldHtmlId}-entry">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
      <input id="${fieldHtmlId}" type="hidden" name="${field.name}" value="${fieldValue?string}" />
      <input type="hidden" id="${fieldHtmlId}_isListProperty" name="${field.name}_isListProperty" value="true" />
      <#if field.control.params.options?? && field.control.params.options != "">
         <select id="${fieldHtmlId}-entry" name="-" multiple="multiple" size="${size}" tabindex="0"
               onchange="javascript:Alfresco.util.updateMultiSelectListValue('${fieldHtmlId}-entry', '${fieldHtmlId}', <#if field.mandatory>true<#else>false</#if>);"
               <#if field.description??>title="${field.description}"</#if> 
               <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
               <#if field.control.params.style??>style="${field.control.params.style}"</#if>
               <#if field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if>>
               <#list field.control.params.options?split(optionSeparator) as nameValue>
                  <#if nameValue?index_of(labelSeparator) == -1>
                     <option value="${nameValue?html}"<#if isSelected(nameValue)> selected="selected"</#if>>${nameValue?html}</option>
                  <#else>
                     <#assign choice=nameValue?split(labelSeparator)>
                     <option value="${choice[0]?html}"<#if isSelected(choice[0])> selected="selected"</#if>>${msgValue(choice[1])?html}</option>
                  </#if>
               </#list>
         </select>
         <@formLib.renderFieldHelp field=field />
         <#if field.control.params.mode?? && isValidMode(field.control.params.mode?upper_case)>
            <input id="${fieldHtmlId}-mode" type="hidden" name="${field.name}-mode" value="${field.control.params.mode?upper_case}" />
         </#if>
      <#else>
         <div id="${fieldHtmlId}" class="missing-options">${msg("form.control.selectone.missing-options")}</div>
      </#if>
   </#if>
</div>

<#function isSelected optionValue>
   <#list values as value>
      <#if optionValue == value?string || (value?is_number && value?c == optionValue)>
         <#return true>
      </#if>
   </#list>
   <#return false>
</#function>

<#function isValidMode modeValue>
   <#return modeValue == "OR" || modeValue == "AND">
</#function>