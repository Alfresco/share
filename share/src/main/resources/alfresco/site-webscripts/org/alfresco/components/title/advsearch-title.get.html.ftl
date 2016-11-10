<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/title/search-title.css" group="title"/>
</@>

<@markup id="js">
   <#-- No JavaScript Dependencies -->
</@>

<@markup id="widgets">
   <@createWidgets group="title"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div class="page-title search-title theme-bg-color-1">
         <h1 class="theme-color-3"><span>${msg("header.advancedsearch")}</span></h1>
         <#if page.url.templateArgs.site??>
         <div>
            <span class="navigation-item backLink">
               <a href="${url.context}/page/site/${page.url.templateArgs.site}">${msg("header.backlink", siteTitle?html)}</a>
            </span>
         </div>
         </#if>
         <#if backlink??>
         <div>
            <span class="navigation-item backLink">
               <a href="search?${backlink?html}">${msg("header.results")}</a>
            </span>
         </div>
         </#if>
         <div class="clear"></div>
      </div>
   </@>
</@>
