<#import "/org/springframework/extensions/surf/api.lib.ftl" as apiLib />
{

<#list data?keys as formatId>

	<#assign title = "">
	<#assign description = "">
	
	<#if data[formatId].title?exists>
		<#assign title = data[formatId].title>
	</#if>
	<#if data[formatId].description?exists>
		<#assign description = data[formatId].description>
	</#if>

	"${formatId}": {
		"title": "${title}",
		"description": "${description}"
	}
	
	<#if formatId_has_next>,</#if>
</#list>
}
