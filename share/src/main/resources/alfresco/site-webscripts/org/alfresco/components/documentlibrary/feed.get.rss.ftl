<#assign conditionvalueEdition = (context.properties["editionInfo"].edition)!"UNKNOWN">
<#assign conditionEditionEnterprise = conditionvalueEdition == "ENTERPRISE">
<#function formatDate date><#return xmldate(date)?string(msg("date-format.rfc822"))></#function>
<#function siteLoc loc><#if (loc.site?length > 0)><#return "/page/site/" + loc.site + "/documentlibrary"><#else><#return "/page/repository"></#if></#function>
<#function location loc><#return absurl(url.context) + siteLoc(loc) + "?file=" + loc.file?url + "&amp;path=" + loc.path?url></#function>
<#function displayLocation loc><#return absurl(url.context) + siteLoc(loc) + "?file=" + loc.file + "&amp;path=" + loc.path></#function>
<?xml version="1.0" encoding="UTF-8"?>
<rss version="2.0" xmlns:atom="http://www.w3.org/2005/Atom">
<channel>
   <title>Alfresco Share - ${msg("feed.filter." + (filter!"path")?html, (filterData!"")?html)}</title>
   <link>${absurl(url.context)}/</link>
   <description>Alfresco Document List</description>
   <generator>Alfresco Share DocumentLibrary</generator>
<#assign proxyLink=absurl(url.context) + "/proxy/alfresco-feed/">
   <image>
      <title>Alfresco Share - ${msg("feed.filter." + (filter!"path")?html)}</title>
      <url>${absurl(url.context)}/themes/default/images/logo<#if conditionEditionEnterprise>-enterprise</#if>.png</url>
      <link>${absurl(url.context)}/</link>
   </image>
<#list items as item>
   <#if item.type == "document">
      <#assign isImage=(item.mimetype="image/gif" || item.mimetype="image/jpeg" || item.mimetype="image/png")>
      <#assign isMP3=(item.mimetype="audio/x-mpeg" || item.mimetype="audio/mpeg")>
   <item>
      <title>${item.displayName?html}</title>
      <description>
         &lt;img src=&quot;${proxyLink + "api/node/" + item.nodeRef?replace("://", "/") + "/content/thumbnails/doclib?c=queue&amp;ph=true"}&quot;&gt;${(item.description!"")?html}&lt;br /&gt;
         ${msg("feed.created", formatDate(item.createdOn), item.createdBy)}&lt;br /&gt;
         ${msg("feed.modified", formatDate(item.modifiedOn), item.modifiedBy)}&lt;br /&gt;
         ${msg("feed.location")}:&#160;&lt;a href="${location(item.location)?xml}"&gt;${displayLocation(item.location)?xml}&lt;/a&gt;
      </description>
      <link>${proxyLink + item.contentUrl}</link>
      <guid isPermaLink="false">${item.nodeRef}</guid>
      <pubDate><#assign locale_original=.locale><#setting locale="en_US">${xmldate(item.modifiedOn)?string(msg("date-format.rfc822"))}<#setting locale=locale_original></pubDate>
      <#if isMP3><enclosure url="${proxyLink + item.contentUrl}" length="${item.size}" type="audio/mpeg" /></#if>
   </item>
   </#if>
</#list>
</channel>
</rss>