<#if articles??>
    <#if articles.title??>
        <div class="interior-header">
            <h2>${articles.title?html}</h2>
            <#if articles.description??><p class="intheader-paragraph">${articles.description?html}</p></#if>
        </div>
    </#if>

    <div class="interior-content">
        <#if articles.assets?size == 0>
            ${msg('list.none')}
        <#else>
    	  	<#list articles.assets as article>
                <div class="blog-entry">
    	            <h2><a href="<@makeurl asset=article/>">${(article.title!article.name)?html}</a></h2>
                    <div class="blog-list-misc">
        	            <#if article.properties['ws:publishedTime']??><span>${article.properties['ws:publishedTime']?string(msg('date.format'))}</span>&nbsp;&nbsp;&nbsp;&bull;&nbsp;&nbsp;</#if>
                        <#if article.properties['cm:author']??><span>${article.properties['cm:author']}</span>&nbsp;&nbsp;&nbsp;&bull;&nbsp;&nbsp;</#if>
                        <span><a href="<@makeurl asset=article/>#read-comments">${article.properties['ws:derivedCommentCount']!0}
                                                                                <#if (article.properties['ws:derivedCommentCount']!0) == 1>
                                                                                  comment
                                                                                <#else>
                                                                                  comments
                                                                                </#if>
                        </a></span>
                    </div>
    	            <p>${(article.description!'')?html}</p>
                    <div class="body-rm"><a href="<@makeurl asset=article/>">${msg('read.more')}</a></div>
        		</div>
            </#list>
            <div class="pagination">
                <#if (pageNumber > 1)>
                    <div class="reverse-arrow"><a href="${uri}?resultsToSkip=${prevSkip}">${msg("pagination.prev")}</a></div>
                </#if>
                <#if (pageNumber < totalPages)>
                    <div class="body-rm"><a href="${uri}?resultsToSkip=${nextSkip}">${msg("pagination.next")}</a></div>
                </#if>   
                <span class="page-number">${msg('pagination.page', pageNumber, totalPages)}</span>
            </div>  
    	</#if>
    </div>
</#if>
	

