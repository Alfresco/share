<#if field.control.params.submitTime?? && field.control.params.submitTime == "false"><#assign submitTime=false><#else><#assign submitTime=true></#if>
<#if field.control.params.showTime?? && field.control.params.showTime == "true"><#assign showTime=true><#else><#assign showTime=false></#if>

<#if showTime><#assign viewFormat>${msg("form.control.date-picker.view.time.format")}</#assign><#else><#assign viewFormat>${msg("form.control.date-picker.view.date.format")}</#assign></#if>

<#assign disabled=field.disabled>
<#if field.control.params.forceEditable?? && field.control.params.forceEditable == "true">
   <#assign disabled=false>
</#if>

<#assign multiValued=false>
<#if field.value != "" && field.value?index_of(",") != -1>
   <#assign multiValued=true>
</#if>

<#if form.capabilities?? && form.capabilities.javascript?? && form.capabilities.javascript == false><#assign jsDisabled=true><#else><#assign jsDisabled=false></#if>

<div class="form-field">
   <#if form.mode == "view">
      <div class="viewmode-field">
         <#if field.mandatory && field.value == "">
            <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
         </#if>
         <span class="viewmode-label">${field.label?html}:</span>
         <#if field.value == "">
            <span class="viewmode-value">
            ${msg("form.control.novalue")}
         <#elseif !multiValued>
            <span class="viewmode-value viewmode-value-date" data-date-iso8601="${field.value}" data-show-time="${showTime?string}">
            ${xmldate(field.value)?string(viewFormat)}
         <#else>
            <span class="viewmode-value">
            <#list field.value?split(",") as dateEl>
               ${xmldate(dateEl)?string(viewFormat)}<#if dateEl_has_next>,</#if>
            </#list>
         </#if>
         </span>
      </div>
   <#elseif !multiValued>
      <#if jsDisabled>
         <label for="${fieldHtmlId}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
         <input id="${fieldHtmlId}" name="${field.name?html}" type="text" class="date-entry" value="${field.value?html}" <#if field.description??>title="${field.description}"</#if> <#if disabled>disabled="true"<#else>tabindex="0"</#if> />
         <div class="format-info">
            <span class="date-format">${msg("form.control.date-picker.entry.datetime.format.nojs")}</span>
         </div>
      <#else>
         <#assign controlId = fieldHtmlId + "-cntrl">
         
         <script type="text/javascript">//<![CDATA[
         (function()
         {
            new Alfresco.DatePicker("${controlId}", "${fieldHtmlId}").setOptions(
            {
               <#if form.mode == "view" || disabled>disabled: true,</#if>
               currentValue: "${field.value?js_string}",
               showTime: ${showTime?string},
               submitTime: ${submitTime?string},
               mandatory: ${field.mandatory?string}
            }).setMessages(
               ${messages}
            );
         })();
         //]]></script>
      
         <input id="${fieldHtmlId}" type="hidden" name="${field.name?html}" value="${field.value?html}" />
      
         <label for="${controlId}-date">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
         <input id="${controlId}-date" name="-" type="text" class="date-entry" <#if field.description??>title="${field.description}"</#if> <#if disabled>disabled="true"<#else>tabindex="0"</#if> />
      
         <#if disabled == false>
            <a id="${controlId}-icon" tabindex="0" href="#"><img src="${url.context}/res/components/form/images/calendar.png" class="datepicker-icon"/></a>
         </#if>
      
         <div id="${controlId}" class="datepicker"></div>
      
         <#if showTime>
            <input id="${controlId}-time" name="-" type="text" class="time-entry" <#if field.description??>title="${field.description}"</#if> <#if disabled>disabled="true"<#else>tabindex="0"</#if> />
         </#if>
         
         <@formLib.renderFieldHelp field=field />
      
         <div class="format-info">
            <span class="date-format">${msg("form.control.date-picker.display.date.format")}</span>
            <#if showTime><span class="time-format<#if disabled>-disabled</#if>">${msg("form.control.date-picker.display.time.format")}</span></#if>
         </div>
      </#if>
   </#if>
</div>