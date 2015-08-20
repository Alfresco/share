<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/blog/toolbar.css" group="blog"/>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/modules/blog/config-blog.css" group="blog"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/components/blog/toolbar.js" group="blog"/>
   <@script type="text/javascript" src="${url.context}/res/modules/blog/config-blog.js" group="blog"/>
</@>

<@markup id="widgets">
   <@createWidgets group="blog"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div id="${args.htmlid}-body" class="share-toolbar blog-toolbar flat-button theme-bg-2">
         <div class="navigation-bar <#if ((args.showNavigationBar!"false") == "false")>hide</#if>">
            <div>
               <span class="<#if (page.url.args.listViewLinkBack! == "true")>backLink<#else>forwardLink</#if>">
                  <a href="${url.context}/page/site/${page.url.templateArgs.site?url}/blog-postlist">${msg("link.listView")}</a>
               </span>
            </div>
         </div>
         <div class="action-bar theme-bg-1">
            <div class="new-blog"><button id="${args.htmlid}-create-button">${msg("button.create")}</button></div>
         </div>
         <@markup id="rssAction">
         <div class="rss-feed">
            <div>
               <a id="${args.htmlid}-rssFeed-button" href="#">${msg("button.rssfeed")}</a>
            </div>
         </div>
         </@>
      </div>
      <div class="clear"></div>
   </@>
</@>