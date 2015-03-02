<#--
   Renders the tag input fields for the form

   @param htmlid (String) the id to use
   @param tags (array) an array of tags
   @param tagInputName the name to use for the input field
-->
<#macro renderTagInputs htmlid tags tagInputName>
   <#list tags as tag>
      <input type="hidden" name="${tagInputName}[]" value="${tag?html}" />
   </#list>
</#macro>

<#--
   Outputs the passed JavaScript array *of strings* as a JSON array
   
   @param arr the javascript array to render
-->
<#macro toJSONArray arr>
[
   <#list arr as x>"${x?html?j_string}"<#if x_has_next>, </#if></#list>
]
</#macro>

<#-- 
   Renders the tag library component
   
   @param htmlid the html id to use for the component
   @param tags the current tags to display
-->
<#macro renderTagLibraryHTML htmlid>
<div class="taglibrary">
   <div class="top_taglist tags_box">
      <ul id="${htmlid}-current-tags">
         <li>&nbsp;</li>
      </ul>
   </div>
   <div class="clear"></div>
   <input type="text" size="30" class="rel_left suppress-validation" id="${htmlid}-tag-input-field" name="-" title="${msg("taglibrary.tip.help")}" />
   <input type="button" id="${htmlid}-add-tag-button" value="${msg("button.add")}" />
   <div class="clear"></div>
   <div class="bottom_taglist tags_box">
      <a href="#" id="${htmlid}-load-popular-tags-link">${msg("taglibrary.populartagslink")}</a>
      <ul id="${htmlid}-popular-tags">
         <li>&nbsp;</li>
      </ul>
   </div>
   <div class="clear"></div>
</div>

<script type="text/javascript">//<![CDATA[
Alfresco.util.addMessages(${messages}, "Alfresco.module.TagLibrary");
//]]></script>
</#macro>