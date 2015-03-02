<#macro renderItem item target="_self">
<div class="headline">
<#if item.image??>
   <img align="left" src="${item.image}" alt="" style="padding-right:10px"/>
</#if>
   <h4>
      <#if (item.link?exists)>
      <a href="${item.link}" target="${target}" class="theme-color-1">${item.title}</a>
      <#else>
      ${item.title}
      </#if>
   </h4>
   <p>${item.description}</p>
<#if item.attachment??>
   <div><img src="${url.context}/res/images/filetypes32/${item.attachment.type}.gif"/><a href="${item.attachment.url}">${item.attachment.name}</a></div>
</#if>
   <br />
</div>
</#macro>
