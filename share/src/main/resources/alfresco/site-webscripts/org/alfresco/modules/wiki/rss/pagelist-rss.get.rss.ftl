<?xml version="1.0" encoding="UTF-8"?>
<rss version="2.0" xmlns:atom="http://www.w3.org/2005/Atom">
   <channel>
      <title>${msg("pagelistrss.title")}</title>
      <link>${absurl(url.context)}/service/components/wiki/rss?site=${site}</link>
      <description>${msg("pagelistrss.description")}</description>
      <language>${lang}</language>

      <#list pages?sort_by(['modifiedOn'])?reverse as p>
         <item>
            <title>${(p.title!"")?html}</title>
            <link>${absurl(url.context)}/page/site/${site}/wiki-page?title=${p.title?url('UTF-8')}</link>
            <pubDate><#assign locale_original=.locale><#setting locale="en_US">${p.modifiedOn?string("EEE, dd MMM yyyy HH:mm:ss Z")}<#setting locale=locale_original></pubDate>
         </item>
      </#list>
   </channel>
</rss>
