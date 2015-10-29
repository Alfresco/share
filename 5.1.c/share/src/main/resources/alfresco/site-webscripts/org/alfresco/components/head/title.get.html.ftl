<#-- Look up page title from message bundles where possible -->
<#if metaPage??>
   <#-- Title had been renamed by site manager in ui (value from site's dashboard page properties) -->
   <#assign pageTitle = metaPage.sitePageTitle!metaPage.title!""/>
<#else>
   <#assign pageTitle = "">
   <#if page??>
      <#-- Title defined in page's xml definition's <title> element -->
      <#assign pageTitle = page.title!""/>
      <#if page.titleId?? && msg(page.titleId) != page.titleId>
         <#-- Page's xml definition had defined an i18n key -->
         <#assign pageTitle = ((msg(page.titleId))!pageTitle)>
      </#if>
   </#if>
   <#if context.properties["page-titleId"]??>
      <#assign pageTitle = msg(context.properties["page-titleId"])>
   </#if>
</#if>
${msg("page.title", pageTitle?html)}