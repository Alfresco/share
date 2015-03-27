<#import "/org/alfresco/utils/feed.utils.ftl" as feedLib/>
<#assign rssItems>
   <#if items?exists>
      <#list items as item>
         <#if item_index &lt; limit?number>
            <@feedLib.renderItem item=item target=target/>
         <#else>
            <#break>
         </#if>
      </#list>
   </#if>
</#assign>
<#escape x as jsonUtils.encodeJSONString(x)>
{
   "title": "${title!''}",
   "feedURL": "${feedurl!''}", 
   "target": "${target!''}",
   "limit": "${limit!''}",
   "content": "${rssItems?replace('"', '\"')}"
}
</#escape>   	