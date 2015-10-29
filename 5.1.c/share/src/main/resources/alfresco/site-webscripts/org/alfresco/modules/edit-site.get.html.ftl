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
               <input id="${el}-isPublic" type="radio" <#if profile.visibility == "PUBLIC">checked="checked"</#if> tabindex="0" name="-" />
               <label for="${el}-isPublic">${msg("site.visibility.label.PUBLIC")}<br />
                 <span id="${el}-public-help-text" class="help">${msg("site.visibility.description.PUBLIC")}</span>
              </label>
            </div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first">&nbsp;</div>
            <div class="yui-u">
               <input id="${el}-isModerated" type="radio" <#if profile.visibility == "MODERATED">checked="checked" </#if>tabindex="0" name="-" />
               <label for="${el}-isModerated">${msg("site.visibility.label.MODERATED")}<br />
                  <span id="${el}-moderated-help-text" class="help">${msg("site.visibility.description.MODERATED")}</span>
               </label>
            </div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first">&nbsp;</div>
            <div class="yui-u">
               <input id="${el}-isPrivate" type="radio" <#if profile.visibility == "PRIVATE">checked="checked" </#if>tabindex="0" name="-" />
               <label for="${el}-isPrivate">${msg("site.visibility.label.PRIVATE")}<br />
                  <span id="${el}-private-help-text" class="help">${msg("site.visibility.description.PRIVATE")}</span>
               </label>
            </div>
         </div>

      </@markup>

      <div class="bdft">
         <#-- BUTTONS -->
         <input type="submit" id="${el}-ok-button" value="${msg("button.save")}" tabindex="0"/>
         <input type="button" id="${el}-cancel-button" value="${msg("button.cancel")}" tabindex="0"/>
      </div>
   </form>
   </div>
</div>

<script type="text/javascript">//<![CDATA[
Alfresco.util.addMessages(${messages}, "Alfresco.module.EditSite");
//]]></script>