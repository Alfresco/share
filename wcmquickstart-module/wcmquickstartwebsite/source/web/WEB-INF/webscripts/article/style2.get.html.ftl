<div class="interior-content">
    <h2>${title?html}</h2>
    <div class="blog-misc">
        <#if asset.properties['ws:publishedTime']??><span>${asset.properties['ws:publishedTime']?string(msg('date.format'))}</span>&nbsp;&nbsp;&nbsp;&bull;&nbsp;&nbsp;</#if>
        <#if asset.properties['cm:author']??><span>${asset.properties['cm:author']}</span>&nbsp;&nbsp;&nbsp;&bull;&nbsp;&nbsp;</#if>
        <span><a href="#read-comments">${asset.properties['ws:derivedCommentCount']!0}
                                       <#if (asset.properties['ws:derivedCommentCount']!0) == 1>
                                         comment
                                       <#else>
                                         comments
                                       </#if>
        </a></span>
    </div>
        
    <@markContent id=asset.id nestedMarker="true" />    
    
    <div class="article-body">
        <@streamasset asset=asset/>
    </div>
    
    <h3  class="tag-list">${msg('tags')}</h3>
    <#if !asset.tags?? || asset.tags?size == 0>
        <p>${msg('tags.none')}</p>
    <#else>
        <ul class="tag-list">
            <#list asset.tags as tag>
                <li><a href="${url.context}/search.html?sectionId=${section.id?url}&tag=${tag?url}">${tag}</a></li>
            </#list>
        </ul>
    </#if>
</div>
