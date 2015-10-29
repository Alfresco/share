<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/discussions/toolbar.css" group="discussions"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/discussions/toolbar.js" group="discussions"/>
</@>

<@markup id="widgets">
   <@createWidgets group="discussions"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div id="${args.htmlid}-body" class="share-toolbar discussions-toolbar flat-button theme-bg-2">
         <div class="navigation-bar <#if (args.showNavigationBar == "false")>hide</#if>">
            <div>
               <span class="<#if (page.url.args.listViewLinkBack! == "true")>backLink<#else>forwardLink</#if>">
                  <a href="${url.context}/page/site/${page.url.templateArgs.site?url}/discussions-topiclist">${msg("link.listView")}</a>
               </span>
            </div>
         </div>
         <div class="action-bar theme-bg-1">
            <div class="new-topic"><button id="${args.htmlid}-create-button">${msg("button.create")}</button></div>
         </div>
         <@markup id="rssAction">
         <div class="rss-feed">
            <div>
               <a id="${args.htmlid}-rssFeed-button" href="${url.context}/proxy/alfresco-feed/slingshot/wiki/pages/${page.url.templateArgs["site"]?url}?format=rss">${msg("button.rssfeed")}</a>
            </div>
         </div>
         </@>
      </div>
      <div class="clear"></div>
   </@>
</@>
