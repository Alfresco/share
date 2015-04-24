<#if form.mode == "edit">
<script type="text/javascript">//<![CDATA[
(function()
{
   new Alfresco.Operations("${fieldHtmlId}").setOptions(
   {
      currentValue: "${field.value?js_string}"
   }).setMessages(
      ${messages}
   );
})();
//]]></script>

<div class="form-field" id="${fieldHtmlId}">
   <div id="${fieldHtmlId}-buttons">
   </div>
</div>
</#if>