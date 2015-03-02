<#assign el=args.htmlid?html>
<div id="${el}-dialog" class="edit-site">
   <div class="hd">
      <#-- TITLE -->
      <@markup id="title">${msg("header.editSite")}</@markup>
   </div>
   <div class="bd">
      <form id="${el}-form" method="PUT"  action="">

      <#-- FIELDS -->
      <@markup id="fields">

         <#-- HIDDEN -->
         <input type="hidden" id="${el}-visibility" name="visibility" value="${profile.visibility}"/>
         <input id="${el}-shortName" type="hidden" name="shortName" value="${profile.shortName}"/>

         <#-- TITLE -->
         <div class="yui-gd">
            <div class="yui-u first"><label for="${el}-title">${msg("label.name")}:</label></div>
            <div class="yui-u"><input id="${el}-title" type="text" name="title" value="${profile.title?html}" tabindex="0"/>&nbsp;*</div>
         </div>

         <#-- DESCRIPTION -->
         <div class="yui-gd">
            <div class="yui-u first"><label for="${el}-description">${msg("label.description")}:</label></div>
            <div class="yui-u"><textarea id="${el}-description" name="description" rows="6" tabindex="0">${profile.description?html}</textarea></div>
         </div>

         <#-- ACCESS -->
         <div class="yui-gd">
            <div class="yui-u first"><label for="${el}-isPublic">${msg("label.access")}:</label></div>
            <div class="yui-u">
               <input id="${el}-isPublic" type="radio" <#if (profile.visibility == "PUBLIC" || profile.visibility == "MODERATED")>checked="checked"</#if> tabindex="0" name="-" /> ${msg("label.isPublic")}<br />
               <div class="moderated">
                  <input id="${el}-isModerated" type="checkbox" tabindex="0" name="-" <#if (profile.visibility == "MODERATED")>checked="checked"</#if> <#if (profile.visibility == "PRIVATE")>disabled="true"</#if>/> ${msg("label.isModerated")}<br />
                  <span class="help"><label for="${el}-isModerated">${msg("label.moderatedHelp")}</label></span>
               </div>
            </div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first">&nbsp;</div>
            <div class="yui-u">
               <input id="${el}-isPrivate" type="radio" tabindex="0" name="-" <#if (profile.visibility == "PRIVATE")>checked="checked"</#if>/> ${msg("label.isPrivate")}
            </div>
         </div>

      </@markup>

      <div class="bdft">
         <#-- BUTTONS -->
         <input type="submit" id="${el}-ok-button" value="${msg("button.ok")}" tabindex="0"/>
         <input type="button" id="${el}-cancel-button" value="${msg("button.cancel")}" tabindex="0"/>
      </div>
   </form>
   </div>
</div>

<script type="text/javascript">//<![CDATA[
Alfresco.util.addMessages(${messages}, "Alfresco.module.EditSite");
//]]></script>