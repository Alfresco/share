<#if articles??>
    <#if articles.title??>
        <div class="interior-header">
            <h2 class="full-h">${articles.title?html}</h2>
            <#if articles.description??><p class="intheader-paragraph">${articles.description?html}</p></#if>
        </div>
    </#if>
    
    <div class="interior-content">
        <#if articles.assets?size == 0>
            ${msg('list.none')}
        <#else>
            <ul class="publications-list">
                <#list articles.assets as article>
                    <li class="entry">
                        <a href="<@makeurl asset=article force='long'/>${(linkParam!'')?html}"><img src="<@makeurl asset=article rendition='mediumPublicationThumbnail'/>" alt="${article.title!''}" class="img-border"/></a>
                        <div class="publications-list-detail">
                            <h3><a href="<@makeurl asset=article force='long'/>${(linkParam!'')?html}">${(article.title!article.name)?html}</a></h3>
                            <span class="newslist-date">
                                <#if article.properties['cmis:lastModificationDate']??>${article.properties['cmis:lastModificationDate']?string(msg('date.format'))}</#if>
                                <#if article.properties['cm:author']??> by ${article.properties['cm:author']}</#if>
                            </span>
                            <p>${(article.description!'no preview')?html}</p>
                            <h3>${msg('tags')}</h3>
                            <#if !article.tags?? || article.tags?size == 0>
                                <p class="tag-list">${msg('tags.none')}</p>
                            <#else>
                                <ul class="tag-list">
                                    <#list article.tags as tag>
                                        <li><a href="${url.context}/search.html?sectionId=${section.id?url}&tag=${tag?url}">${tag}</a></li>
                                    </#list>
                                </ul>
                            </#if>
                        </div>
                    </li>
                </#list>
            </ul>
        </#if>
    </div>
</#if>