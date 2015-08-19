<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/wiki/toolbar.css" group="wiki"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/wiki/toolbar.js" group="wiki"/>
</@>

<@markup id="widgets">
   <@createWidgets group="wiki"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div id="${args.htmlid}-body" class="share-toolbar wiki-toolbar flat-button theme-bg-2">
         <div class="navigation-bar theme-bg-1">
            <div>
               <#if args.showBackLink == "true">
               <span class="<#if (page.url.args.listViewLinkBack! == "true")>backLink<#else>forwardLink</#if>">
                  <a href="${url.context}/page/site/${page.url.templateArgs.site?url}/wiki">${msg("link.listView")}</a>
               </span>
               </#if>
               <#if page.url.args.title! != "Main_Page">
               <span class="forwardLink">
                  <a href="${url.context}/page/site/${(page.url.templateArgs.site!"")?url}/wiki-page?filter=main&amp;title=Main_Page<#if args.showBackLink != "true">&amp;listViewLinkBack=true</#if>">${msg("link.mainPage")}</a>
               </span>
               </#if>
            </div>
         </div>
         <div class="action-bar">
            <#assign hide><#if (page.url.args.title! == "")>style="display: none;"</#if></#assign>
            <div class="new-page"><button id="${args.htmlid}-create-button">${msg("button.create")}</button></div>
            <div class="separator" ${hide}>&nbsp;</div>
            <div class="delete-page" ${hide}><button id="${args.htmlid}-delete-button">${msg("button.delete")}</button></div>
            <div class="separator" ${hide}>&nbsp;</div>
            <div class="rename-page" ${hide}><button id="${args.htmlid}-rename-button">${msg("button.rename")}</button></div>
         </div>
         <@markup id="rssAction">
         <div class="rss-feed">
            <div>
               <a id="${args.htmlid}-rssFeed-button" href="#">${msg("message.rssfeed")}</a>
            </div>
         </div>
         </@>
         <div id="${args.htmlid}-renamepanel" class="rename-panel">
            <div class="hd"><label for="${args.htmlid}-renameTo">${msg("panel.rename.title")}</label></div>
            <div class="bd">
               <form id="${args.htmlid}-renamePageForm" method="post" action="${url.context}/proxy/alfresco/slingshot/wiki/page/${page.url.templateArgs["site"]?url}/${(page.url.args.title!"")?url}">
                  <div class="yui-ge">
                     <input type="hidden" id="${args.htmlid}-page" name="page" value="wiki-page" />
                     <div class="yui-u first">
                        <input type="text" id="${args.htmlid}-renameTo" name="name" value="" size="30" tabindex="0" />
                     </div>
                     <div class="yui-u">
                        <input type="submit" id="${args.htmlid}-rename-save-button" value="${msg("button.save")}" tabindex="0" />
                     </div>
                  </div>
               </form>
               <div class="bdft">${msg("panel.rename.footer")}</div>
            </div>
         </div>
      </div>
      <div class="clear"></div>
   </@>
</@>