<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/wiki/list.css" group="wiki"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/wiki/parser.js" group="wiki"/>
   <@script src="${url.context}/res/components/wiki/list.js" group="wiki"/>
</@>

<@markup id="widgets">
   <@createWidgets group="wiki"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div id="${args.htmlid}-pagelist" class="yui-navset pagelist"> 
      <#if pageList?? && pageList.pages?size &gt; 0>
      <#list pageList.pages as p>
         <div class="wikipage <#if p.tags??><#list p.tags as t>wp-${t}<#if t_has_next> </#if></#list></#if>">
         <div class="actionPanel">
            <#if p.permissions.edit><div class="editPage"><a href="${url.context}/page/site/${page.url.templateArgs.site?url}/wiki-page?title=${p.title?url}&amp;action=edit&amp;listViewLinkBack=true">${msg("link.edit")}</a></div></#if>
            <div class="detailsPage"><a href="${url.context}/page/site/${page.url.templateArgs.site?url}/wiki-page?title=${p.title?url}&amp;action=details&amp;listViewLinkBack=true">${msg("link.details")}</a></div>
            <#if p.permissions.delete><div class="deletePage"><a href="#" class="delete-link" title="${p.title?html}">${msg("link.delete")}</a></div></#if>
         </div>
   <div class="pageTitle"><a class="pageTitle theme-color-1" href="${url.context}/page/site/${page.url.templateArgs.site?url}/wiki-page?title=${p.title?url}&amp;listViewLinkBack=true">${p.title?html?replace("_", " ")}</a></div>
         <div class="publishedDetails">
            <span class="attrLabel">${msg("label.creator")}</span> <span class="attrValue"><a href="${url.context}/page/user/${p.createdByUser?url}/profile" class="theme-color-1" >${p.createdBy?html}</a></span>
            <span class="spacer">&nbsp;</span>
            <span class="attrLabel">${msg("label.createDate")}</span> <span class="attrValue parseTime">${p.createdOn}</span>
            <span class="spacer">&nbsp;</span>
            <span class="attrLabel">${msg("label.modifier")}</span> <span class="attrValue"><a href="${url.context}/page/user/${p.modifiedByUser?url}/profile" class="theme-color-1">${p.modifiedBy?html}</a></span>
            <span class="spacer">&nbsp;</span>
            <span class="attrLabel">${msg("label.modifiedDate")}</span> <span class="attrValue parseTime">${p.modifiedOn}</span>
         </div>
         <#assign pageCopy>${(p.text!"")?replace("</?[^>]+>", " ", "ir")}</#assign>
         <div class="pageCopy rich-content"><#if pageCopy?length &lt; 1000>${pageCopy}<#else>${pageCopy?substring(0, 1000)}...</#if></div>
         <#-- Display tags, if any -->
         <div class="pageTags">
            <span class="tagDetails">${msg("label.tags")}</span>
            <#if p.tags?? && p.tags?size &gt; 0><#list p.tags as tag><a href="#"  class="wiki-tag-link">${tag}</a><#if tag_has_next>,&nbsp;</#if></#list><#else>${msg("label.none")}</#if>
         </div>
         </div><#-- End of wikipage -->
      </#list>
      <#elseif error??>
         <div class="error-alt">${error}</div>
      <#else>
         <div class="noWikiPages">${msg("label.noPages")}</div>
      </#if>
      </div>
   </@>
</@>