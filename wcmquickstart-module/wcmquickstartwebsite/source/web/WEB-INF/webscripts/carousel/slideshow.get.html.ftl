<#if (articles.assets?size > 0)>
    <script type="text/javascript" src="${url.context}/js/slideswitch.js"></script>
    <div id="slideshow-wrapper">
        <ul id="slideshow">
            <#list articles.assets as article>
                <li <#if article_index == 1>class="active"</#if>>                     
                    <#if article.relatedAssets['ws:primaryImage']??>             
                        <#assign image=article.relatedAssets['ws:primaryImage'][0]>             
                        <a href="<@makeurl asset=article/>"><img src="<@makeurl asset=image rendition='featuredNewsThumbnail'/>" alt="${image.title!image.name}" class="slide-1-img" /></a>
                    </#if>
                    <div class="slide-1-desc">
                      <h1>${(article.title!article.name)?html}</h1>
                      <p>${(article.description!'')?html}</p>
                      <div class="slideshow-rm"><a href="<@makeurl asset=article/>">${msg('read.more')}</a></div>
                    </div>
                </li>
            </#list>
        </ul>
    </div>
</#if> 
