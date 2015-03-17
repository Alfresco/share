<#assign mode = args.mode!"">
<#if mode = "user">
   <#assign title=msg("atom.title.user", user.fullName?xml)>
<#else>
   <#assign title=msg("atom.title.site", args["site"]?xml)>
</#if>
<#assign genericTitle=msg("title.generic")>
<?xml version="1.0" encoding="UTF-8"?>
<feed xmlns="http://www.w3.org/2005/Atom">
   <generator version="1.0">Alfresco (1.0)</generator>
   <link rel="self" href="${absurl(url.full)?xml}" />
   <id>${absurl(url.full)?xml}</id>
   <title>${title?xml}</title>
<#if activities?exists && activities?size &gt; 0>
   <updated>${activities[0].date.isoDate}</updated>
   <#list activities as activity>
      <#assign userLink="<a href=\"${absurl(activity.userProfile)}\">${activity.fullName?html}</a>">
      <#assign itemLink="<a href=\"${absurl(activity.itemPage)}\">${activity.title?html}</a>">
      <#assign siteTitle=siteTitles[activity.siteId]!activity.siteId>
      <#assign siteLink="<a href=\"${absurl(activity.sitePage)}\">${siteTitle?html}</a>">
      <#if activity.secondUserProfile??>
        <#assign secondUserLink="<a href=\"${absurl(activity.secondUserProfile)}\">${(activity.secondFullName!\"\")?html}</a>">
      <#else>
        <#assign secondUserLink="">
      </#if>
   <entry xmlns='http://www.w3.org/2005/Atom'>
      <#assign detail = msg(activity.type, activity.title?xml, activity.fullName?xml, activity.custom0, activity.custom1, siteTitle?xml, (activity.secondFullName!"")?xml)>
      <#if mode="user" && !activity.suppressSite><#assign detail=msg("in.site", detail, siteTitle?xml)></#if>
      <title type="html"><![CDATA[${detail}]]></title>
      <link rel="alternate" type="text/html" href="${absurl(activity.itemPage)}" />
      <id>${activity.id}</id>
      <updated>${activity.date.isoDate}</updated>
      <#assign detailHTML = msg(activity.type, itemLink, userLink, activity.custom0, activity.custom1, siteLink, secondUserLink)>
      <#if mode = "user" && !activity.suppressSite><#assign detailHTML = msg("in.site", detailHTML, siteLink)></#if>
      <summary type="html">
         <![CDATA[${msg(detailHTML)}]]>
      </summary>
      <author>
         <name>${activity.fullName?xml}</name>
         <uri>${absurl(activity.userProfile)?xml}</uri>
      </author>
   </entry>
   </#list>
</#if>
</feed>