<a id="read-comments"></a>

<ul class="comments-wrapper">    
    <h3>${msg('comments.display.title')}</h3>
    <#if feedbackPage.size == 0>
        <p>${msg('comments.display.none')}</p>
    <#else>	
	  	<#list feedbackPage.feedback as comment>  	
	        <li>
                <#if comment.visitorWebsite??>
                    <h4><a href="${comment.visitorWebsite}">${(comment.visitorName!'Anonymous')?html}</a></h4>
                <#else>
                    <h4>${(comment.visitorName!'Anonymous')?html}</h4>
                </#if>
                <span class="newslist-date"><#if comment.postTime??>${comment.postTime?string(msg('date.format'))}</#if></span>
                <#if comment.commentFlagged || (context.properties['report']!'') = comment.id>
                    <span class="comments-text">${msg('comments.display.censored')}</span>
                <#else>
                    <span class="comments-text">${(comment.comment!'nothing to say')?html}</span>
                    <span class="comments-report"><a href="?report=${comment.id}">report this post</a></span>
                </#if>
                <div class="clearfix"></div>
	        </li>
	    </#list>
	</#if>
</ul>
