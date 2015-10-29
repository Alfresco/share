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
            <div class="yui-gd<#if sitePresets?size == 1> hidden</#if>">
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
                  <input id="${el}-isPublic" type="radio" <#if defaultVisibility == "PUBLIC">checked="checked" </#if>tabindex="0" name="-" />
                  <label for="${el}-isPublic">${msg("site.visibility.label.PUBLIC")}<br />
                     <span id="${el}-public-help-text" class="help">${msg("site.visibility.description.PUBLIC")}</span>
                  </label>
               </div>
            </div>
            <div class="yui-gd">
               <div class="yui-u first">&nbsp;</div>
               <div class="yui-u">
                  <input id="${el}-isModerated" type="radio" <#if defaultVisibility == "MODERATED">checked="checked" </#if>tabindex="0" name="-" />
                  <label for="${el}-isModerated">${msg("site.visibility.label.MODERATED")}<br />
                     <span id="${el}-moderated-help-text" class="help">${msg("site.visibility.description.MODERATED")}</span>
                  </label>
               </div>
            </div>
            <div class="yui-gd">
               <div class="yui-u first">&nbsp;</div>
               <div class="yui-u">
                  <input id="${el}-isPrivate" type="radio" <#if defaultVisibility == "PRIVATE">checked="checked" </#if>tabindex="0" name="-" />
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
Alfresco.util.addMessages(${messages}, "Alfresco.module.CreateSite");
//]]></script>
