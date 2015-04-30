<import resource="/org/springframework/extensions/surf/api.lib.js">

model.websiteTitle = context.websiteTitle;

model.pageTitle = null;
if (context.page != null)
{
	model.pageTitle = context.page.title;
}

model.uri = null;
if (context.uri != null)
{
	model.uri = context.uri;
}

model.pageId = null;
if (context.pageId != null)
{
	model.pageId = context.pageId;
}

model.templateId = context.templateId;

model.contentId = context.contentId;

model.formatId = context.formatId;

model.rootPageId = null;
if (context.rootPage != null)
{
	model.rootPageId = context.rootPage.id;
}

model.rootPageTitle = null;
if (context.rootPage != null)
{
	model.rootPageTitle = context.rootPage.title;
}

model.previewWebappId = context.previewWebappId;

model.previewStoreId = context.previewStoreId;