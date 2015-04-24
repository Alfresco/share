<#include "common/picker.inc.ftl" />

<#assign controlId = fieldHtmlId + "-cntrl">

<script type="text/javascript">//<![CDATA[
(function()
{
   <@renderPickerJS field "picker" true />
   picker.setOptions(
   {
      itemType: "cm:person",
      multipleSelectMode: ${field.endpointMany?string},
      itemFamily: "authority",
      maintainAddedRemovedItems: false,
      finderAPI: Alfresco.constants.PROXY_URI + "cloud/forms/picker/{itemFamily}",
      itemsAPI: Alfresco.constants.PROXY_URI + "cloud/forms/picker/items",
      singleItemLabel: 'hybridworkflow.authority.single',
      multipleItemsLabel: 'hybridworkflow.authority.multiple'
   });
})();
//]]></script>

<div class="form-field">
   <#if form.mode == "view">
      <div id="${controlId}" class="viewmode-field">
         <#if field.endpointMandatory && field.value == "">
            <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
         </#if>
         <span class="viewmode-label">${field.label?html}:</span>
         <span id="${controlId}-currentValueDisplay" class="viewmode-value current-values"></span>
         <input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
      </div>
   <#else>
      <label for="${controlId}">${field.label?html}:<#if field.endpointMandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
      
      <div id="${controlId}" class="object-finder">
         
         <div id="${controlId}-currentValueDisplay" class="current-values"></div>
         
         <#if field.disabled == false>
            <input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
            <div id="${controlId}-itemGroupActions" class="show-picker"></div>
            <@renderPickerHTML controlId />
         </#if>
      </div>
   </#if>
</div>
