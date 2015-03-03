<#assign el=args.htmlid?html>
<div id="${el}-dialog" class="create-site">
   <div class="hd">
      <@markup id="title">${msg("header.createSite")}</@markup>
   </div>
   <div class="bd">
      <form id="${el}-form" method="POST" action="">

         <#-- FIELDS -->
         <@markup id="fields">

            <input type="hidden" id="${el}-visibility" name="visibility" value="PUBLIC"/>

            <#-- TITLE -->
            <div class="yui-gd">
               <div class="yui-u first"><label for="${el}-title">${msg("label.name")}:</label></div>
               <div class="yui-u"><input id="${el}-title" type="text" name="title" tabindex="0" maxlength="255" />&nbsp;*</div>
            </div>

            <#-- SHORTNAME -->
            <div class="yui-gd">
               <div class="yui-u first"><label for="${el}-shortName">${msg("label.shortName")}:</label></div>
               <div class="yui-u">
                  <input id="${el}-shortName" type="text" name="shortName" tabindex="0" maxlength="255" />&nbsp;*<br>
                  <span class="help">${msg("label.shortNameHelp")}</span>
               </div>
            </div>

            <#-- DESCRIPTION -->
            <div class="yui-gd">
               <div class="yui-u first"><label for="${el}-description">${msg("label.description")}:</label></div>
            <div class="yui-u"><textarea id="${el}-description" name="description" rows="6" tabindex="0"></textarea></div>
            </div>

            <#-- SITEPRESET -->
            <div class="yui-gd">
               <div class="yui-u first"><label for="${el}-sitePreset">${msg("label.type")}:</label></div>
               <div class="yui-u">
                  <select id="${el}-sitePreset" name="sitePreset" tabindex="0">
                     <#list sitePresets as sitePreset>
                        <option value="${sitePreset.id}">${sitePreset.name}</option>
                     </#list>
                  </select>
               </div>
            </div>

            <#-- ACCESS -->
            <div class="yui-gd">
               <div class="yui-u first"><label for="${el}-isPublic">${msg("label.access")}:</label></div>
               <div class="yui-u">
                  <input id="${el}-isPublic" type="radio" <#if defaultVisibility == "PUBLIC">checked="checked" </#if>tabindex="0" name="-" /> <label for="${el}-isPublic">${msg("label.isPublic")}</label><br />
                  <div class="moderated">
                     <input id="${el}-isModerated" type="checkbox" tabindex="0" name="-" <#if defaultVisibility == "PRIVATE">disabled </#if> /> <label for="${el}-isModerated">${msg("label.isModerated")}</label><br />
                     <span class="help">${msg("label.moderatedHelp")}</span>
                  </div>
               </div>
            </div>
            <div class="yui-gd">
               <div class="yui-u first">&nbsp;</div>
               <div class="yui-u">
                  <input id="${el}-isPrivate" type="radio" <#if defaultVisibility == "PRIVATE">checked="checked" </#if>tabindex="0" name="-" /> <label for="${el}-isPrivate">${msg("label.isPrivate")}</label>
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
Alfresco.util.addMessages(${messages}, "Alfresco.module.CreateSite");
//]]></script>
