<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/wiki/wiki.css" group="wiki"/>
   <@link href="${url.context}/res/modules/document-picker/document-picker.css" group="wiki"/>
   <@link href="${url.context}/res/components/object-finder/object-finder.css" group="wiki"/>
   <@link href="${url.context}/res/modules/simple-editor.css" group="wiki"/>
   <@link href="${url.context}/res/modules/taglibrary/taglibrary.css" group="wiki"/>
   <@link href="${url.context}/res/modules/wiki/revert-wiki-version.css" group="wiki"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/wiki/parser.js" group="wiki"/>
   <@script src="${url.context}/res/components/wiki/page.js" group="wiki"/>
   <@script src="${url.context}/res/modules/document-picker/document-picker.js" group="wiki"/>
   <@script src="${url.context}/res/components/object-finder/object-finder.js" group="wiki"/>
   <@script src="${url.context}/res/modules/simple-editor.js" group="wiki"/>
   <@script src="${url.context}/res/modules/taglibrary/taglibrary.js" group="wiki"/>
   <@script src="${url.context}/res/modules/wiki/revert-wiki-version.js" group="wiki"/>
   <@script src="${url.context}/res/components/common/common-component-style-filter-chain.js" group="wiki"/>
</@>

<@markup id="widgets">
   <@createWidgets group="wiki"/>
</@>

<@markup id="html">
   <link rel="alternate" type="application/wiki" href="${page.url.servletContext}/site/${page.url.templateArgs.site?url}/wiki-page?title=${(page.url.args.title!"")?url}&amp;action=edit" />
   <@uniqueIdDiv>
      <#-- Version History -->
      <#if (result.versionhistory?? && result.versionhistory?size > 0)>
         <#assign currentVersion = result.versionhistory[0].version>
      <#else>
         <#assign currentVersion = "">
      </#if>
      <#-- Permissions -->
      <#if result.permissions??>
         <#assign permissions = result.permissions>
      <#else>
         <#assign permissions = {}>
      </#if>
      <#-- Error State? -->
      <#assign errorState = (!result.pagetext?? && result.message??)>

      <#-- Note, since result.pagetext has already been stripped by the page.get.js script -->
      <div class="yui-g wikipage-bar">

         <div class="title-bar">
      <div id="${args.htmlid}-viewButtons" class="yui-u first pageTitle">${page.url.args["title"]?replace("_", " ")?html}</div>
            <div class="yui-u align-right">
      <#assign action = page.url.args.action!"view">
      <#assign tabs =
      [
         {
            "label": msg("tab.view"),
            "action": "view",
            "permitted": !errorState
         },
         {
            "label": msg("tab.edit"),
            "action": "edit",
            "permitted": permissions["edit"]!false
         },
         {
            "label": msg("tab.details"),
            "action": "details",
            "permitted": !errorState
         }
      ]>
      <#list tabs as tab>
         <#if tab.action == action>
               <span class="theme-color-2">${tab.label}</span>
         <#elseif tab.permitted == false>
               <span class="tabLabelDisabled">${tab.label}</span>
         <#else>
               <a href="?title=${(page.url.args.title!"")?url}&amp;action=${tab.action?url}" class="tabLabel">${tab.label}</a>
         </#if>
         <#if tab_has_next>
               <span class="separator">|</span>
         </#if>
      </#list>
            </div>
         </div>
      </div>
      <div id="${args.htmlid}-wikipage" class="wiki-page">
         <div class="yui-content" style="background: #FFFFFF;">
      <#if action == "view">
            <div id="${args.htmlid}-page" class="rich-content"><#if result.pagetext??>${result.pagetext}<#elseif result.message??><span class="error-alt">${result.message}</span></#if></div>
      <#elseif action == "edit">
            <div class="page-form-body">
               <form id="${args.htmlid}-form" action="${url.context}/proxy/alfresco/slingshot/wiki/page/${page.url.templateArgs.site?url}/${page.url.args["title"]?url}" method="post">
                  <fieldset>
                  <#assign pageContext = page.url.context + "/page/site/" + page.url.templateArgs.site?url + "/wiki-page?title=" + page.url.args.title?url>
                     <input type="hidden" name="context" value="${pageContext?html}" />
                     <input type="hidden" name="page" value="wiki-page" />
                     <input type="hidden" name="currentVersion" value="${currentVersion}" />
                     <div class="yui-gd">
                        <div class="yui-u first">
                           <label for="${htmlid}-content">${msg("label.text")}:</label>
                        </div>
                        <div class="yui-u">
                           <textarea name="pagecontent" id="${args.htmlid}-content" cols="50" rows="10"><#if result.pagetext??>${result.pagetext?html}</#if></textarea>
                        </div>
                     </div>
                     <div class="yui-gd">
                        <div class="yui-u first">
                           <label for="${htmlid}-tag-input-field">${msg("label.tags")}:</label>
                        </div>
                        <div class="yui-u">
                           <#import "/org/alfresco/modules/taglibrary/taglibrary.lib.ftl" as taglibraryLib/>
                           <@taglibraryLib.renderTagLibraryHTML htmlid=args.htmlid />
                        </div>
                     </div>
                     <div class="yui-gd">
                        <div class="yui-u first">&nbsp;</div>
                        <div class="yui-u">
                           <div class="buttons">
                              <input type="submit" id="${args.htmlid}-save-button" value="${msg("button.save")}" />
                              <input type="submit" id="${args.htmlid}-cancel-button" value="${msg("button.cancel")}" />
                           </div>
                        </div>
                     </div>
                  </fieldset>
               </form>
            </div>
      <#elseif action == "details">
            <div>
               <div class="details-wrapper">
               <div class="yui-g">
                  <div class="yui-u first">
                     <h2>
                        ${(result.title!"")?html}
                        <#if result.versionhistory??><#list result.versionhistory as version><#if version_index == 0><span id="${args.htmlid}-version-header" class="light">${msg("label.shortVersion")}${version.version}</span></#if></#list></#if>
                     </h2>
                  </div>
                  <div class="yui-u">
                  <#if result.versionhistory??>
                     <div class="version-quick-change">
                     <#list result.versionhistory as version>
                        <#if version_index == 0>
                        <input type="button" id="${args.htmlid}-selectVersion-button" name="selectButton" value="${version.version} (${msg("label.latest")})" />
                        <select id="${args.htmlid}-selectVersion-menu" name="selectVersion">
                        </#if>
                           <option value="${version.versionId}">${version.version} <#if version_index = 0>(${msg("label.latest")})</#if></option>
                     </#list>
                        </select>
                     </div>
                     <div class="version-quick-change">${msg("label.viewVersion")}</div>
                  </#if>
                  </div>
               </div>
               <div id="${args.htmlid}-page" class="details-page-content">
                  <#-- PAGE CONTENT GOES HERE -->
                  <#if result.pagetext??>${result.pagetext}</#if>
               </div>
               </div>
               <div class="yui-gb">
                  <div class="yui-u first">
                     <div class="columnHeader">${msg("label.versionHistory")}</div>
                     <#if result.versionhistory??>
                     <#assign canRevert = permissions["edit"]!false>
                     <#list result.versionhistory as version>
                        <#if version_index == 0>
                        <div class="info-sub-section">
                           <span class="meta-heading">${msg("section.thisVersion")}</span>
                        </div>
                        </#if>
                        <#if version_index == 1>
                        <div class="info-sub-section">
                           <span class="meta-heading">${msg("section.olderVersion")}</span>
                        </div>
                        </#if>
                        <div id="${args.htmlid}-expand-div-${version_index}" class="info more <#if version_index != 0>collapsed<#else>expanded</#if>">
                           <span class="meta-section-label theme-color-1">${msg("label.version")} ${version.version}</span>
                           <span id="${args.htmlid}-createdDate-span-${version_index}" class="meta-value">&nbsp;</span>
                        </div>
                        <div id="${args.htmlid}-moreVersionInfo-div-${version_index}" class="moreInfo" <#if version_index != 0>style="display: none;"</#if>>
                           <div class="info">
                              <span class="meta-label">${msg("label.title")}</span>
                              <span class="meta-value">${version.title?html}</span>
                           </div>
                           <div class="info">
                              <span class="meta-label">${msg("label.creator")}</span>
                              <span class="meta-value">${version.author?html}</span>
                           </div>
                           <#if version_index != 0>
                           <div class="actions">
                              <#if canRevert>
                              <span id="${args.htmlid}-revert-span-${version_index}" class="revert"><a>${msg("link.revert")}</a></span>
                              <#else>
                              <span class="revertDisabled">${msg("link.revert")}</span>
                              </#if>
                           </div>
                           </#if>
                        </div>
                     </#list>
                     </#if>
                  </div>
                  <div class="yui-u">
                     <div class="columnHeader">${msg("label.tags")}</div>
                     <div class="tags">
                     <#if result.tags?? && result.tags?size &gt; 0>
                        <#list result.tags as tag>
                        <div class="tag"><img src="${url.context}/res/components/images/tag-16.png" /> ${tag}</img></div>
                        </#list>
                     <#else>
                        ${msg("label.none")}
                     </#if>
                     </div>
                  </div>
                  <div class="yui-u">
                     <div class="columnHeader">${msg("label.linkedPages")}</div>
                     <div class="links">
                     <#if result.links??>
                        <#list result.links as link>
                        <div><span>[[${link?replace("</?[^>]+>", " ", "ir")}]]</div>
                        </#list>
                     </#if>
                     </div>
                     <script type="text/javascript">//<![CDATA[
var links = YUIDom.getElementsByClassName("links", "div");
this.parser = new Alfresco.WikiParser();
this.parser.URL = "${url.context}/page/site/${page.url.templateArgs.site?url}/wiki-page?title=";
for (i=0; i<links.length; i++)
{
   if (links[i].className === "links")
   {
      links[i].innerHTML = this.parser.parse(links[i].innerHTML);
   }
}
                     //]]></script>
                  </div>
               </div>
            </div>
      </#if>
         </div>
      </div>
   </@>
</@>
