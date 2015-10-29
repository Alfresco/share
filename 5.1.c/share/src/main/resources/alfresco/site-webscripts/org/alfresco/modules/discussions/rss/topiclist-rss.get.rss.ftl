<?xml version="1.0" encoding="UTF-8"?>
<rss version="2.0" xmlns:atom="http://www.w3.org/2005/Atom">
   <channel>
      <title>${msg("topiclistrss.title")}</title>
      <link>${absurl(url.context)}/service/components/discussions/rss?site=${site}</link>
      <description>${msg("topiclistrss.description")}</description>
      <language>${lang}</language>

<#if (items?size > 0)>
   <#list items as topic>
      <item>
         <title>${topic.title?html}</title>
         <link>${absurl(url.context)}/page/site/${site}/discussions-topicview?topicId=${topic.name}</link>
         <description>${topic.content?html}</description>
         <pubDate>${xmldate(topic.createdOn)?string(msg("date-format.rfc822"))}</pubDate>
      </item>
   </#list>
<#else>
      <item>${msg("topiclistrss.nocontent")}</item>
</#if>
   </channel>
</rss>