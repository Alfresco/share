<#if field.control.params.property??>
   <#-- use the supplied property to retrieve the encoding value -->
   <#assign encoding="">
   <#assign contentUrl=form.data["prop_" + field.control.params.property?replace(":", "_")]!"">
   <#if contentUrl?? && contentUrl != "">
      <#assign mtBegIdx=contentUrl?index_of("encoding=")+9>
      <#assign mtEndIdx=contentUrl?index_of("|", mtBegIdx)>
      <#assign encoding=contentUrl?substring(mtBegIdx, mtEndIdx)>
   </#if>
<#else>
   <#assign encoding=field.value>
</#if>

<div class="form-field">
   <#if form.mode == "view">
      <div class="viewmode-field">
         <span class="viewmode-label">${msg("form.control.encoding.label")}:</span>
         <span class="viewmode-value">${getEncodingLabel("${encoding}")}</span>
      </div>
   <#else>
      <label for="${fieldHtmlId}">${msg("form.control.encoding.label")}:</label>
      <#-- TODO: Make this control make an AJAX callback to get list of encodings OR use dataTypeParamters structure -->
      <select id="${fieldHtmlId}" name="${field.name}" tabindex="0"
              <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
              <#if field.control.params.style??>style="${field.control.params.style}"</#if>>
         <option value="">${msg("form.control.encoding.unknown")}</option>
         <@encodingOption enc="ISO-8859-1" />
         <@encodingOption enc="MacRoman" />
         <@encodingOption enc="Shift_JIS" />
         <@encodingOption enc="US-ASCII" />
         <@encodingOption enc="UTF-8" />
         <@encodingOption enc="UTF-16" />
         <@encodingOption enc="UTF-32" />
      </select>
   </#if>
</div>

<#function getEncodingLabel enc>
   <#if enc=="UTF-8">
      <#return "UTF-8">
   <#elseif enc=="UTF-16">
      <#return "UTF-16">
   <#elseif enc=="UTF-32">
      <#return "UTF-32">
   <#elseif enc=="ISO-8859-1">
      <#return "ISO-8859-1">
   <#elseif enc=="US-ASCII">
      <#return "US-ASCII">
   <#elseif enc=="MacRoman">
      <#return "MacRoman">
   <#elseif enc=="Shift_JIS">
      <#return "Shift_JIS">
   <#else>
      <#return msg("form.control.encoding.unknown")>
   </#if>
</#function>

<#macro encodingOption enc>
   <option value="${enc}"<#if encoding==enc> selected="selected"</#if>>${getEncodingLabel("${enc}")}</option>
</#macro>
              