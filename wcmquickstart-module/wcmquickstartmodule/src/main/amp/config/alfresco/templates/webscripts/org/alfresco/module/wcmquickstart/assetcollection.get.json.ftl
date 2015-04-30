<#escape x as jsonUtils.encodeJSONString(x)>
{
    data:
    {
        "id" : "${collection.id}",
        "name" : "${collection.name}",
        "title" : "${collection.title}",
        "description" : "${collection.description}",
        "lastUpdated" : "${collection.lastUpdateTime?string("yyyyMMdd-HH:mm:ss.SSSZ")}",
        "secondsBetweenUpdates" : "${collection.secondsBetweenUpdates}",
        "assets" :
        [
        <#list collection.assetIds as assetId>
            "${assetId}"<#if assetId_has_next>,</#if>
        </#list>    
        ]
    }
}
</#escape>
