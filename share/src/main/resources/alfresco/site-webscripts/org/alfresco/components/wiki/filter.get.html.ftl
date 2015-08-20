<@markup id="css" >
   <#-- No CSS Dependencies -->
</@>

<@markup id="js">
   <#-- No JavaScript Dependencies -->
</@>

<@markup id="widgets">
   <@createWidgets group="wiki"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div id="${args.htmlid?html}-body" class="filter wiki-filter">
         <h2>${msg("header.pages")}</h2>
         <ul class="filterLink">
            <li><span class="recentlyModified"><a href="${url.context}/page/site/${(page.url.templateArgs.site!"")?url}/wiki?filter=recentlyModified" class="filter-link">${msg("link.recentlyModified")}</a></span></li>
            <li><span class="all"><a href="${url.context}/page/site/${(page.url.templateArgs.site!"")?url}/wiki?filter=all" class="filter-link">${msg("link.all")}</a></span></li>
            <li><span class="recentlyAdded"><a href="${url.context}/page/site/${(page.url.templateArgs.site!"")?url}/wiki?filter=recentlyAdded" class="filter-link">${msg("link.recentlyAdded")}</a></span></li>
            <li><span class="myPages"><a href="${url.context}/page/site/${(page.url.templateArgs.site!"")?url}/wiki?filter=myPages" class="filter-link">${msg("link.myPages")}</a></span></li>
         </ul>
      </div>
   </@>
</@>
