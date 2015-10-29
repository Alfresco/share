<div class="form-field">
   <#assign controlId = fieldHtmlId + "-cntrl">
   <script type="text/javascript">//<![CDATA[
   (function()
   {
      new Alfresco.PercentageApprove("${fieldHtmlId}").setOptions(
      {
         currentValue: <#if field.value?exists>${field.value}<#else>50</#if>,
         minValue: <#if field.control.params.minValue?exists>${field.control.params.minValue}<#else>1</#if>,
         maxValue: <#if field.control.params.maxValue?exists>${field.control.params.maxValue}<#else>100</#if>
      }).setMessages(
         ${messages}
      );
   })();
   //]]></script>
   <div id="${controlId}">
      <label for="${controlId}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
      <input id="${fieldHtmlId}-value" type="hidden" name="${field.name}" value="" />
      <input id="${fieldHtmlId}" name="-" type="text" <#if field.description??>title="${field.description}"</#if> tabindex="0" />
      <@formLib.renderFieldHelp field=field />
   </div>
</div>