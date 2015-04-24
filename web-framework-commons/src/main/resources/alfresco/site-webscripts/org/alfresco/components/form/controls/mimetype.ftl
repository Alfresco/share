<#if field.control.params.property??>
   <#-- use the supplied property to retrieve the mimetype value -->
   <#assign mimetype="">
   <#assign contentUrl=form.data["prop_" + field.control.params.property?replace(":", "_")]!"">
   <#if contentUrl?? && contentUrl != "">
      <#assign mtBegIdx=contentUrl?index_of("mimetype=")+9>
      <#assign mtEndIdx=contentUrl?index_of("|", mtBegIdx)>
      <#assign mimetype=contentUrl?substring(mtBegIdx, mtEndIdx)>
   </#if>
<#else>
   <#assign mimetype=field.value>
</#if>

<div class="form-field">
   <#if form.mode == "view">
      <div class="viewmode-field">
         <span class="viewmode-label">${msg("form.control.mimetype.label")}:</span>
         <span class="viewmode-value">${getMimetypeLabel("${mimetype}")}</span>
      </div>
   <#else>
      <label for="${fieldHtmlId}">${msg("form.control.mimetype.label")}:</label>
      <#-- TODO: Make this control make an AJAX callback to get list of mimetypes OR use dataTypeParamters structure -->
      <select id="${fieldHtmlId}" name="${field.name}" tabindex="0"
              <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
              <#if field.control.params.style??>style="${field.control.params.style}"</#if>>
         <option value="">${msg("form.control.mimetype.unknown")}</option>

         <#assign types = mimetypes.mimetypesByDisplay>
         <#list types?keys as description>
            <@mimetypeOption mt="${types[description]}" mtdesc="${description}" />
         </#list>
      </select>
   </#if>
</div>

<#function getMimetypeLabel mt>
   <#-- Share specific overrides to the repository list -->
   <#local localMimetypes = {
      "text/javascript": "JavaScript",
      "image/jpeg2000", "JPEG 2000",
      "audio/x-mpeg": "MPEG Audio (x-mpeg)"
     }>

   <#-- Combine with the Repository list (local takes precidence) -->
   <#local mimetypes = mimetypes.displaysByMimetype + localMimetypes>

   <#-- Look up the description for this type, falling back on the unknown -->
   <#local label=mimetypes[mt]!msg("form.control.mimetype.unknown")>
   <#return label>
</#function>

<#macro mimetypeOption mt mtdesc="">
   <#if mtdesc?length == 0><#local mtdesc = getMimetypeLabel(mt)></#if>
   <option value="${mt}"<#if mimetype==mt> selected="selected"</#if>>${mtdesc}</option>
</#macro>
