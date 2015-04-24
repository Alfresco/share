<#if articles??>
    <#if articles.title??>
        <div class="interior-header">
            <h2>${articles.title?html}</h2>
            <#if articles.description??><p class="intheader-paragraph">${articles.description?html}</p></#if>
            <span class="ih-rss"> <a href="<@makeurl section=section/>rss.xml"><img src="${url.context}/img/rss_16.png" width="16" height="16" alt="" /></a><a href="<@makeurl section=section/>rss.xml">${msg('list.wide2.rss')}</a></span> 
        </div>
    </#if>
    
    <div class="interior-content">
        <#if articles.assets?size == 0>
            ${msg('list.none')}
        <#else>
    		<ul class="newslist-wrapper">
    		  	<#list articles.assets as article>
    		        <li>
                        <#if article.relatedAssets['ws:primaryImage']??>
                            <#assign image=article.relatedAssets['ws:primaryImage'][0]>
                            <a href="<@makeurl asset=article/>"><img src="<@makeurl asset=image rendition='smallThumbnail'/>" alt="${image.title!article.title!article.name}" class="news-img" /></a>
                        </#if>
    		            <h4><a href="<@makeurl asset=article/>">${(article.title!article.name)?html}</a></h4>
    		            <span class="newslist-date"><#if article.properties['ws:publishedTime']??>${article.properties['ws:publishedTime']?string(msg('date.format'))}</#if></span>
    		            <p>${(article.description!'')?html}</p>
    		        </li>
    		    </#list>
    		</ul>
    	</#if>
    </div>
</#if>	

