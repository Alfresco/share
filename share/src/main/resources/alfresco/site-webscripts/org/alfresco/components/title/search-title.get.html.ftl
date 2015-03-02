<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/title/search-title.css" group="title"/>
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
         <h1 class="theme-color-3"><span><#if !reposearch>${msg("header.searchresults")}<#else>${msg("header.advsearchresults")}</#if></span></h1>
         <div>
            <span class="navigation-item forwardLink">
               <a href="advsearch<#if advsearchlink??>?${advsearchlink?html}</#if>">${msg("header.advanced")}</a>
            </span>
         </div>
         <#if page.url.templateArgs.site??>
         <div>
            <span class="navigation-item backLink">
               <a href="${url.context}/page/site/${page.url.templateArgs.site}/dashboard">${msg("header.backlink", siteTitle?html)}</a>
            </span>
         </div>
         </#if>
         <div class="clear"></div>
      </div>
   </@>
</@>
