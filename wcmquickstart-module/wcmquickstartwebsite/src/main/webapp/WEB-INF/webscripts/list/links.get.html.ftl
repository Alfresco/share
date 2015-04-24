<#if articles??>
    <div class="services-box">
        <#if articles.title??>
            <h3>${articles.title?html}</h3>
            <#if articles.description??><p>${articles.description?html}</p></#if>
        </#if>
        
        <#if articles.assets?size == 0>
            ${msg('list.none')}
        <#else>
            <ul class="services-box-list">
                <#list articles.assets as article>
                    <li>
                        <a href="<@makeurl asset=article/>"><#if article.title?? && article.title?length gt 0>${article.title?html}<#else>${article.name?html}</#if></a>
                    </li>
                </#list>
            </ul>
        </#if>
    </div>
</#if>