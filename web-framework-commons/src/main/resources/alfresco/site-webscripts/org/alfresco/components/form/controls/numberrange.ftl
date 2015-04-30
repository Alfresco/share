<#assign viewFormat>${msg("form.control.date-picker.view.date.format")}</#assign>

<div class="form-field">
   <#assign controlId = fieldHtmlId + "-cntrl">
   
   <script type="text/javascript">//<![CDATA[
   (function()
   {
      new Alfresco.NumberRange("${controlId}", "${fieldHtmlId}").setMessages(
         ${messages}
      );
   })();
   //]]></script>
   
   <label for="${controlId}">${field.label?html}:</label>
   
   <input id="${fieldHtmlId}" type="hidden" name="${field.name}-range" value="" />
   
   <div id="${controlId}" style="width:24em">
      <div class="yui-g">
         <div class="yui-u first">
            <span>${msg("form.control.range.min")}:</span>
            <#-- min value -->
            <input id="${controlId}-min" name="-" type="text" class="number" <#if field.description??>title="${field.description}"</#if> tabindex="0" />
         </div>
         <div class="yui-u">
            <span>${msg("form.control.range.max")}:</span>
            <#-- max value -->
            <input id="${controlId}-max" name="-" type="text" class="number" <#if field.description??>title="${field.description}"</#if> tabindex="1" />
         </div>
      </div>
   </div>
   
</div>