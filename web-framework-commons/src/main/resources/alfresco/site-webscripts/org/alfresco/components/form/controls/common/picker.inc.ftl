<#assign compactMode = field.control.params.compactMode!false>

<#-- Assing ["CommonComponentStyle"]["component-style"] configuration value from share-document-library-config.xml to customFolderStyleConfig variable. -->
<#-- This variable will be set as option for Alfresco.ObjectFinder. -->
<#assign commonComponentConfig = config.scoped["CommonComponentStyle"]["component-style"]!>
<#if commonComponentConfig.value??>
        <#assign tmp = commonComponentConfig.value>
        <#assign customFolderStyleConfig = tmp!"">
</#if>

<#macro renderPickerJS field picker="picker">
    <@renderPickerJS field "picker" false/>
</#macro>

<#macro renderPickerJS field picker="picker" cloud=false>
   <#if field.control.params.selectedValueContextProperty??>
      <#if context.properties[field.control.params.selectedValueContextProperty]??>
         <#local renderPickerJSSelectedValue = context.properties[field.control.params.selectedValueContextProperty]>
      <#elseif args[field.control.params.selectedValueContextProperty]??>
         <#local renderPickerJSSelectedValue = args[field.control.params.selectedValueContextProperty]>
      <#elseif context.properties[field.control.params.selectedValueContextProperty]??>
         <#local renderPickerJSSelectedValue = context.properties[field.control.params.selectedValueContextProperty]>
      </#if>
   </#if>

   <#if cloud>
      var ${picker} = new Alfresco.CloudObjectFinder("${controlId}", "${fieldHtmlId}").setOptions(
   <#else>
      var ${picker} = new Alfresco.ObjectFinder("${controlId}", "${fieldHtmlId}").setOptions(
   </#if>
   {
      <#if form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>disabled: true,</#if>
      field: "${field.name}",
      customFolderStyleConfig: <#if customFolderStyleConfig??>${(customFolderStyleConfig!"")}<#else>null</#if>,
      compactMode: ${compactMode?string},
   <#if field.mandatory??>
      mandatory: ${field.mandatory?string},
   <#elseif field.endpointMandatory??>
      mandatory: ${field.endpointMandatory?string},
   </#if>
   <#if field.control.params.startLocation??>
      startLocation: "${field.control.params.startLocation}",
      <#if form.mode == "edit" && args.itemId??>currentItem: "${args.itemId?js_string}",</#if>
      <#if form.mode == "create" && form.destination?? && form.destination?length &gt; 0>currentItem: "${form.destination?js_string}",</#if>
   </#if>
   <#if field.control.params.startLocationParams??>
      startLocationParams: "${field.control.params.startLocationParams?js_string}",
   </#if>
      currentValue: "${field.value?js_string}",
      <#if field.control.params.valueType??>valueType: "${field.control.params.valueType}",</#if>
      <#if renderPickerJSSelectedValue??>selectedValue: "${renderPickerJSSelectedValue}",</#if>
      <#if field.control.params.selectActionLabelId??>selectActionLabelId: "${field.control.params.selectActionLabelId}",</#if>
      selectActionLabel: "${field.control.params.selectActionLabel!msg("button.select")}",
      minSearchTermLength: ${field.control.params.minSearchTermLength!'1'},
      maxSearchResults: ${field.control.params.maxSearchResults!'100'}
   }).setMessages(
      ${messages}
   );
</#macro>

<#macro renderPickerHTML controlId>
   <#assign pickerId = controlId + "-picker">
<div id="${pickerId}" class="picker yui-panel">
   <div id="${pickerId}-head" class="hd">${msg("form.control.object-picker.header")}</div>

   <div id="${pickerId}-body" class="bd">
      <div class="picker-header">
         <div id="${pickerId}-folderUpContainer" class="folder-up"><button id="${pickerId}-folderUp"></button></div>
         <div id="${pickerId}-navigatorContainer" class="navigator">
            <button id="${pickerId}-navigator"></button>
            <div id="${pickerId}-navigatorMenu" class="yuimenu">
               <div class="bd">
                  <ul id="${pickerId}-navigatorItems" class="navigator-items-list">
                     <li>&nbsp;</li>
                  </ul>
               </div>
            </div>
         </div>
         <div id="${pickerId}-searchContainer" class="picker-search">
            <input type="text" class="picker-search-input" name="-" id="${pickerId}-searchText" value="" maxlength="256" />
            <span class="search-button"><button id="${pickerId}-searchButton">${msg("form.control.object-picker.search")}</button></span>
         </div>
      </div>
      <div class="yui-g">
         <div id="${pickerId}-left" class="yui-u first panel-left">
            <div id="${pickerId}-results" class="picker-items">
               <#nested>
            </div>
         </div>
         <div id="${pickerId}-right" class="yui-u panel-right">
            <div id="${pickerId}-selectedItems" class="picker-items"></div>
         </div>
      </div>
      <div class="bdft">
         <button id="${controlId}-ok" tabindex="0">${msg("button.ok")}</button>
         <button id="${controlId}-cancel" tabindex="0">${msg("button.cancel")}</button>
      </div>
   </div>

</div>
</#macro>
