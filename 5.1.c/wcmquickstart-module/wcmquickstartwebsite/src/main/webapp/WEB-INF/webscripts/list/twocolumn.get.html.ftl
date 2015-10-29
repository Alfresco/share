<#if articles??>
    <div class="interior-content">
        <#if articles.assets?size == 0>
            ${msg('list.none')}
        <#else>
    	  	<#list articles.assets as article>  
    	  	    <#if article_index == 0>
    	  	        <div class="featured-news">
                        <h2><a href="<@makeurl asset=article/>">${(article.title!article.name)?html}</a></h2>
                        <span class="newslist-date">From <a href="<@makeurl section=article.containingSection/>">${article.containingSection.title?html}</a><#if article.properties['ws:publishedTime']??> - ${article.properties['ws:publishedTime']?string(msg('date.format'))}</#if></span>
                        <div class="featured-news-body">
                            <#if article.relatedAssets['ws:primaryImage']??>
                                <#assign image=article.relatedAssets['ws:primaryImage'][0]>
                                <a title="Link to news article" href="<@makeurl asset=article/>">
                                    <img src="<@makeurl asset=image rendition='mediumNewsThumbnail'/>" alt="${image.title!article.title!article.name}" class="left-img" />
                                </a>
                            </#if>
                            <p>${(article.description!'')?html}</p>
                        </div>
                    </div>
                    <ul class="services-wrapper">
    	  	    <#else>	
    	  	        <#if article_index%2 = 1>
    	  	            <#assign liClass='odd'/>
    	  	        <#else>
                        <#assign liClass='even'/>
                    </#if>
    		        <li class="${liClass}">
                        <div class="service-txt">
                            <span class="newslist-date">From <a href="<@makeurl section=article.containingSection/>">${article.containingSection.title?html}</a><#if article.properties['ws:publishedTime']??> - ${article.properties['ws:publishedTime']?string(msg('date.format'))}</#if></span>
        		            <h3><a href="<@makeurl asset=article/>">${(article.title!article.name)?html}</a></h3>
        		            <p>${(article.description!'no preview')?html}</p>
        		        </div>
    		        </li>
    		    </#if>
    	    </#list>
    		</ul>
    	</#if>
    </div>
</#if>	

