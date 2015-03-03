<@markup id="css" >
   <#-- No CSS Dependencies -->
</@>

<@markup id="js">
   <#-- No JavaScript Dependencies -->
</@>

<@markup id="widgets">
   <@createWidgets group="navigation"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign activeSite = page.url.templateArgs.site!"">
      <#assign pageFamily = template.properties.pageFamily!"dashboard">
      <div class="site-navigation">
      <#if siteExists>
         <#if pageFamily == "dashboard" >
            <#assign linkClass>class="active-page theme-color-4"</#assign>
         <#else>
            <#assign linkClass>class="theme-color-4"</#assign>
         </#if>
         <span class="navigation-item"><a href="${url.context}/page/site/${activeSite}/dashboard" ${linkClass}>${msg("link.siteDashboard")}</a></span>
         <#if siteValid>
            <span class="navigation-separator">&nbsp;</span>
            <#list pages as p>
               <#assign linkPage=p.pageUrl!p.title/>
               <#if linkPage?index_of(pageFamily) != -1>
                  <#assign linkClass>class="active-page theme-color-4"</#assign>      
               <#else>
                  <#assign linkClass>class="theme-color-4"</#assign>
               </#if>
            <span class="navigation-item"><a href="${url.context}/page/site/${activeSite}/${linkPage}" ${linkClass}>${(p.sitePageTitle!p.title)?html}</a></span>
               <#if p_has_next>
            <span class="navigation-gap">&nbsp;</span>
               </#if>
            </#list>
         </#if>
         <span class="navigation-separator">&nbsp;</span>
         <#if pageFamily == "site-members">
            <#assign linkClass>class="active-page theme-color-4"</#assign>      
         <#else>
            <#assign linkClass>class="theme-color-4"</#assign>
         </#if>
      <span class="navigation-item"><a href="${url.context}/page/site/${activeSite}/site-members" ${linkClass}>${msg("link.members")}</a></span>
      </#if>
      </div>
   </@>
</@>

