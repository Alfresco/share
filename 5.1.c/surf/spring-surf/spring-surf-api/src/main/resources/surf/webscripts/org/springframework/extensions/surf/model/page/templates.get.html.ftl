<#import "/org/springframework/extensions/surf/api.lib.ftl" as apiLib />
{
	"associations": [

<#assign first = true>
<#list associations?keys as formatId>

	<#assign association = associations[formatId]>

	<#if !first>,</#if>
	
	{
		'format-id' : '${formatId}'
		,
		'template-id' : '${association.id}'
		
		<#if association.title?exists>
		,
		'template-title': '${association.title}'
		</#if>

		<#if association.description?exists>
		,
		'template-description': '${association.description}'
		</#if>

	}	
	
	<#assign first = false>

</#list>

	]
}