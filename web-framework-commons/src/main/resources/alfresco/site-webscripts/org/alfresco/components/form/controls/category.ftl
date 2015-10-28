<#include "common/picker.inc.ftl" />

<#assign controlId = fieldHtmlId + "-cntrl">

<script type="text/javascript">//<![CDATA[
(function()
{
   <@renderPickerJS field "picker" />
   picker.setOptions(
   {
      itemType: "cm:category",
      multipleSelectMode: ${(field.control.params.multipleSelectMode!true)?string},
      parentNodeRef: "${field.control.params.parentNodeRef!"alfresco://category/root"}",
      itemFamily: "category",
      maintainAddedRemovedItems: false,
      params: "${field.control.params.params!""}",
      createNewItemUri: "${field.control.params.createNewItemUri!}",
      createNewItemIcon: "${field.control.params.createNewItemIcon!}"
   });
})();
//]]></script>

<div class="form-field inlineable">
   <#if form.mode == "view">
      <div id="${controlId}" class="viewmode-field inlineable">
         <#if (field.mandatory!false) && (field.value == "")>
            <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /></span>
         </#if>
         <#if field.label != ""><span class="viewmode-label">${field.label?html}:</span></#if>
         <span id="${controlId}-currentValueDisplay" class="viewmode-value current-values"></span>
      </div>
   <#else>
      <#if field.label != "">
      <label for="${controlId}">${field.label?html}:<#if field.mandatory!false><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
      </#if>
      
      <div id="${controlId}" class="object-finder inlineable">
         
         <div id="${controlId}-currentValueDisplay" class="current-values inlineable"></div>
         
         <#if field.disabled == false>
            <input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
            <input type="hidden" id="${fieldHtmlId}_isCategory" name="${field.name}_isCategory" value="true" />
            <div id="${controlId}-itemGroupActions" class="show-picker inlineable"></div>
            
            <#if field.control.params.showSubCategoriesOption?? && field.control.params.showSubCategoriesOption == "true">
               <div class="subcats-option">
                  <input type="checkbox" name="${field.name}_usesubcats" value="true" checked="true" />&nbsp;${msg("form.control.category.include.subcats")}
               </div>
            </#if>
            
            <#if field.control.params.mode?? && isValidMode(field.control.params.mode?upper_case)>
               <input id="${fieldHtmlId}-mode" type="hidden" name="${field.name}-mode" value="${field.control.params.mode?upper_case}" />
            </#if>
            
            <@renderPickerHTML controlId />
         </#if>
      </div>
   </#if>
</div>

<#function isValidMode modeValue>
   <#return modeValue == "OR" || modeValue == "AND">
</#function>