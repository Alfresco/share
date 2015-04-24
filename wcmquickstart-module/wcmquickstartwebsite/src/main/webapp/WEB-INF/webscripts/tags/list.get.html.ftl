<div class="blog-categ">
    <h3>${msg('section.tags')}</h3>
    <#if !section.tags?? || section.tags?size == 0>
        <p>${msg('tags.none')}</p>
    <#else>
        <ul class="tag-list">
            <#list section.tags as tag>
                <li><a href="${url.context}/search.html?sectionId=${section.id?url}&tag=${tag.name?url}">${tag.name?html} (${tag.count})</a></li>
            </#list>
        </ul>
    </#if>
</div>