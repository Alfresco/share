<#escape x as jsonUtils.encodeJSONString(x)>
{
    "success": ${success?string}    
<#if success == true && preview == true>
	,"importids" : 
	{
	<#list importids as importid>
            "${importid}" : "${msg("wcmqs.importFileLocation." + importid)}"<#if importid_has_next>,</#if>
    </#list>  			
	}    
</#if>
}
</#escape>
