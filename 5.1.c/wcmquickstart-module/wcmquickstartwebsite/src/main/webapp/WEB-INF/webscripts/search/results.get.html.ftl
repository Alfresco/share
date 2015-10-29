<div class="interior-header">
<h2>${msg("search.results.title")}</h2>

<#if results.query.phrase??>
    <#assign text=results.query.phrase/>
    <#assign link="${url.context}${rootSection.path}search.html?sectionId=${results.query.sectionId?url}&phrase=${results.query.phrase?url}"/>
<#else>
    <#assign text=results.query.tag/>
    <#assign link="${url.context}${rootSection.path}search.html?sectionId=${results.query.sectionId?url}&tag=${results.query.tag?url}"/>
</#if>

<#if results.size == 0>
    <p class="intheader-paragraph"> ${msg('search.results.none', text, sectionTitle!'the website')}</p>
    </div>
<#else>
    <p class="intheader-paragraph"> ${msg('search.results.found', results.size, results.totalSize, text, sectionTitle!'the website')}</p>
    </div>

    <div class="interior-content">
        <ul class="newslist-wrapper">
            <#list results.results as result>
                <li> 
                  <h4><a href="<@makeurl asset=result force='short'/>">${(result.title!result.name)?html}</a></h4>
                  <span class="newslist-date"><#if result.properties['ws:publishedTime']??>${result.properties['ws:publishedTime']?string(msg('date.format'))}</#if></span>
                  <p>${(result.description!'')?html}</p>
                </li>
            </#list>
        </ul>
        <div class="pagination">
            <#if (pageNumber > 1)>
                <div class="reverse-arrow"><a href="${link}&resultsToSkip=${prevSkip}">${msg("pagination.prev")}</a></div>
            </#if>
            <#if (pageNumber < totalPages)>
                <div class="body-rm"><a href="${link}&resultsToSkip=${nextSkip}">${msg("pagination.next")}</a></div>
            </#if>   
            <span class="page-number">${msg('pagination.page', pageNumber, totalPages)}</span>
        </div>  
    </div>
</#if>

