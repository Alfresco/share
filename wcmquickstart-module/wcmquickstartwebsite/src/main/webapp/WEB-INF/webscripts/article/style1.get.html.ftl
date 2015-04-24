<div class="interior-content">
    <h2>${title?html}</h2>
    <span class="ih-date">From <a href="<@makeurl section=asset.containingSection/>">${asset.containingSection.title}</a><#if asset.properties['ws:publishedTime']??> - ${asset.properties['ws:publishedTime']?string(msg('date.format'))}</#if></span>
    
    <@markContent id=asset.id  nestedMarker="true" />    
    
    <div class="article-body">
        <#if asset.relatedAssets['ws:primaryImage']??>             
            <#assign image=asset.relatedAssets['ws:primaryImage'][0]>             
            <a rel="lightbox" title="detail image" href="<@makeurl asset=image rendition='largeNewsThumbnail'/>">
                <img src="<@makeurl asset=image rendition='mediumNewsThumbnail'/>" alt="${image.title!image.name}" class="img-border left-img" />
            </a>
        </#if>
               
        <@streamasset asset=asset/>
    </div>
        
    <h3 class="tag-list">${msg('tags')}</h3>
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
