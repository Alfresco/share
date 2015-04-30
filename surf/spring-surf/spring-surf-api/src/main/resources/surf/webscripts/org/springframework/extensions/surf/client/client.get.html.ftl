<#import "/org/springframework/extensions/surf/api.lib.ftl" as apiLib />
//
// Surf Namespace
//
function Surf() {} 


//
// RequestContext object
//
Surf.RequestContext = function()
{
	return {
	
		id : <#if context.id?exists>"${context.id}"<#else>null</#if>
		,
		websiteTitle : <#if websiteTitle?exists>"${websiteTitle}"<#else>null</#if>
		,
		pageTitle : <#if pageTitle?exists>"${pageTitle}"<#else>null</#if>
		,
		uri : <#if uri?exists>"${uri}"<#else>null</#if>
		,
		pageId : <#if pageId?exists>"${pageId}"<#else>null</#if>
		,
		templateId : <#if templateId?exists>"${templateId}"<#else>null</#if>
		,
		objectId : <#if contentId?exists>"${contentId}"<#else>null</#if>
		,
		formatId : <#if formatId?exists>"${formatId}"<#else>null</#if>
		,
		themeId : <#if themeId?exists>"${themeId}"<#else>null</#if>
		,
		getRootPageId : <#if rootPageId?exists>"${rootPageId}"<#else>null</#if>
		,
		getRootPageTitle : <#if rootPageTitle?exists>"${rootPageTitle}"<#else>null</#if>
		,
		getStoreId : <#if previewStoreId?exists>"${previewStoreId}"<#else>null</#if>
		,
		getWebappId : <#if previewWebappId?exists>"${previewWebappId}"<#else>null</#if>
		,
		getCurrentPageId : function() {
			return this.pageId;
		}
		,
		getCurrentTemplateId : function() {
			return this.templateId;
		}
	};	
};



//
// Helper Functions
//
Surf.wait = function(msecs)
{
	var start = new Date().getTime();
	var cur = start
	while(cur - start < msecs)
	{
		cur = new Date().getTime();
	}
}



//
// Additional empowerments
//
String.prototype.startsWith = function(s) { return this.indexOf(s)==0; }
String.prototype.endsWith = function(str) { return (this.match(str+"$")==str); }



//
// Instances
//
Surf.context = new Surf.RequestContext();
