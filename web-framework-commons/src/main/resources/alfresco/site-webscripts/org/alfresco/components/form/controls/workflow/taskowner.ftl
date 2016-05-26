<div class="form-field">
   <div class="viewmode-field">
      <span class="viewmode-label">${field.label?html}:</span>
      <span id="${fieldHtmlId}" class="viewmode-value"><#if (!field.value?? || field.value?length == 0)>${msg("form.control.novalue")}</#if></span>
   </div>
</div>

<#if field.value?? && field.value?length &gt; 0>
<#if field.value?string?index_of("|") == -1>
   <#assign userName=field.value>
   <#assign fullName="">
   <#assign disableLink=true>
<#else>
   <#assign ownerParts=field.value?split("|") />
   <#assign userName=ownerParts[0]>
   <#assign fullName="${ownerParts[1]} ${ownerParts[2]}">
   <#assign disableLink=false>
</#if>
<script type="text/javascript">//<![CDATA[
YAHOO.util.Event.onContentReady("${fieldHtmlId}", function ()
{
   YAHOO.util.Dom.get("${fieldHtmlId}").innerHTML = Alfresco.util.userProfileLink("${userName}", "${fullName?js_string}", "", ${disableLink?string});
}, this);
//]]></script>
</#if>