<#import "/org/alfresco/utils/feed.utils.ftl" as feedLib/>
<#assign html>
   <#if items?? && items?size &gt; 0>
      <#list items as item>
         <#if limit == "all" || item_index &lt; limit?number><@feedLib.renderItem item=item target=target/><#else><#break></#if>
      </#list>
   <#elseif !error?exists>
      <h3>${msg("label.noItems")}</h3>
   </#if>
</#assign>
<#escape x as jsonUtils.encodeJSONString(x)>
{
  "title": "${title!msg("label.header")}",
  "html": "${html?replace('"', '\"')}"
}
</#escape>