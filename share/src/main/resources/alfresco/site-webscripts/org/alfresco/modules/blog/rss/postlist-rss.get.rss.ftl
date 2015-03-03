<?xml version="1.0" encoding="UTF-8"?>
<rss version="2.0" xmlns:atom="http://www.w3.org/2005/Atom">
   <channel>
      <title>${msg("postlistrss.title")}</title>
      <link>${absurl(url.context)}/service/components/blog/rss?site=${site}</link>
      <description>${msg("postlistrss.description")}</description>
      <language>${lang}</language>

<#if (items?size > 0)>
   <#list items as post>
      <item>
         <title><#if post.isDraft>${msg("postlistrss.draft")}: </#if>${post.title?html}</title>
         <link>${absurl(url.context)}/page/site/${site}/blog-postview?postId=${post.name}</link>
         <description>${post.content?html}</description>
      <#if (!post.isDraft)>
         <pubDate><#assign locale_original=.locale><#setting locale="en_US">${post.releasedOn?string("EEE, dd MMM yyyy HH:mm:ss Z")}<#setting locale=locale_original></pubDate>
      </#if>
      </item>
   </#list>
<#else>
   <item>${msg("postlistrss.noposts")}</item>
</#if>
   </channel>
</rss>