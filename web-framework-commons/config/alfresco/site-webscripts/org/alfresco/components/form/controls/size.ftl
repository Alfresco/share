<#if field.control.params.property??>
   <#-- use the supplied property to retrieve the size value -->
   <#assign size="0">
   <#assign contentUrl=form.data["prop_" + field.control.params.property?replace(":", "_")]!"">
   <#if contentUrl?? && contentUrl != "">
      <#assign mtBegIdx=contentUrl?index_of("size=")+5>
      <#assign mtEndIdx=contentUrl?index_of("|", mtBegIdx)>
      <#assign size=contentUrl?substring(mtBegIdx, mtEndIdx)>
   </#if>
<#else>
   <#assign size=field.value>
</#if>

<div class="form-field">
   <div class="viewmode-field">
      <span class="viewmode-label">${msg("form.control.size.label")}:</span>
      <span id="${fieldHtmlId}" class="viewmode-value"></span>
   </div>
</div>

<script type="text/javascript">//<![CDATA[
YAHOO.util.Event.onContentReady("${fieldHtmlId}", function ()
{
   YAHOO.util.Dom.get("${fieldHtmlId}").innerHTML = <#if size?is_number>Alfresco.util.formatFileSize(${size?c})<#else>"${msg("form.control.novalue")}"</#if>;
}, this);
//]]></script>
