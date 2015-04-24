<#macro serializeResponse code message>
{
	"code" : "${code}"
	
	<#if message?exists>
	,
	"message" : "${message}"	
	</#if>
}
</#macro>

<#macro serializeObject code message data>
{
	"code" : "${code}"
	
	<#if message?exists>
	,
	"message" : "${message}"	
	</#if>
	
	<#if data?exists>	
	,
	"data" : <@serializeModelObject object=data />
	
	</#if>
}
</#macro>

<#macro serializeObjects code message results>
{
	"code" : "${code}"
	
	<#if message?exists>
	,
	"message" : "${message}"
	</#if>
	
	<#if results?exists>
	,
	"results" :
	
	{
		<#list results as data>
		
			"${data.id}" : <@serializeModelObject object=data />
			
			<#if data_has_next>,</#if>
			
		</#list>
	}
	</#if>
}
</#macro>

<#macro serializeModelObject object>
{
	"id" : "${object.id}",
	"typeId" : "${object.typeId}"

	<#if object.properties?exists>

		,
		"properties":
		{
		
		<#assign first = true >

		<#list object.properties?keys as key>
		
			<#if key != "resources">

				<#if first == false>,</#if>		
				
				<#assign value = object.properties[key]>

				<#assign value = value?replace("\"", "'")>
				<#assign value = value?replace("\n", "")>
				<#assign value = value?replace("\r", "")>

				"${key}" : "${value}"
				
				<#assign first = false >
			</#if>

		</#list>

		}
	</#if>

	<#if object.resources?exists>

		,
		"resources":
		{

		<#list object.resources.properties?keys as key>
		
			"${key}" :
			{
				<#assign resource = object.resources.properties[key]>
				
				<#list resource.properties?keys as attributeKey>
					<#assign value = resource.properties[attributeKey]>
					
					<#assign value = value?replace("\"", "'")>
					<#assign value = value?replace("\n", "")>
					<#assign value = value?replace("\r", "")>					
					
					"${attributeKey}" : "${value}"
					
					<#if attributeKey_has_next>,</#if>
				</#list>
			}
			
			<#if key_has_next>,</#if>
			
		</#list>
		
		}
	</#if>


}
</#macro>