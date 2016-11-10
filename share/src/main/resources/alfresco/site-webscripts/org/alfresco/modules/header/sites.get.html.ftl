<#assign id = args.htmlid?html>
<#assign id_js = id?js_string>
<script type="text/javascript">//<![CDATA[
   Alfresco.util.ComponentManager.get("${id_js}").setOptions(
   {
      siteTitle: "${siteTitle?js_string}",
      favouriteSites: {<#list favouriteSites as site>'${site.shortName}': '${site.title?js_string}'<#if site_has_next>,</#if></#list>}
   }).setMessages(${messages});
//]]></script>
<div id="${id}-sites-menu" class="yuimenu menu-with-icons">
   <div class="bd">

      <#if showFavourites>
      <h6 id="${id}-favouritesContainer" class="favourite-sites">${msg("label.favourite-sites")}</h6>
      <ul id="${id}-favouriteSites" class="favourite-sites-list separator">
      <#list favouriteSites as site>
         <li>
            <a href="${url.context}/page/site/${site.shortName}">${site.title?html}</a>
         </li>
      </#list>
      </ul>
      </#if>

      <#if showAddFavourites>
      <ul id="${id}-addFavourite" class="add-favourite-menuitem separator">
         <li>
            <a href="#" onclick="Alfresco.util.ComponentManager.get('${id_js}').addAsFavourite(); return false;">${msg("label.add-favourite", siteTitle?html)}</a>
         </li>
      </ul>
      </#if>

      <#if showFindSites>
      <ul class="site-finder-menuitem">
         <li>
            <a href="${url.context}/page/site-finder">${msg("label.find-sites")}</a>
         </li>
      </ul>
      </#if>

      <#if showCreateSite>
      <ul class="create-site-menuitem">
         <li>
            <a href="#" onclick="Alfresco.util.ComponentManager.get('${id_js}').showCreateSite(); return false;">${msg("label.create-site")}</a>
         </li>
      </ul>
      </#if>

   </div>
</div>