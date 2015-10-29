<#if page.title??>
    <title>${(webSite.title!context.page.id)?html} - ${msg(page.title)!page.title?html}</title>
<#elseif asset?? && asset.name != 'index.html'>
    <title>${(webSite.title!context.page.id)?html} - ${asset.title!asset.name?html}</title>
    <#if asset.description??>
        <meta name="description" content="${asset.description?html}"/>
    </#if>
<#elseif section?? && section.id != webSite.id>
    <title>${(webSite.title!context.page.id)?html} - ${(section.title!section.name)?html}</title>
    <#if section.description??>
        <meta name="description" content="${section.description?html}"/>
    </#if>
<#else>
    <title>${(webSite.title!context.page.id)?html}</title>
    <#if webSite.description??>
        <meta name="description" content="${webSite.description?html}"/>
    </#if>
</#if>
