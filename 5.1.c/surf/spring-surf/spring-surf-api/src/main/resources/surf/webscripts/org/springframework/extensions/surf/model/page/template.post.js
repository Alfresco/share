<import resource="/org/springframework/extensions/surf/api.lib.js">

var pageId = url.templateArgs["id"];
var templateId = url.templateArgs["templateId"];
var formatId = args["f"];
if (formatId == null)
{
	formatId = "default";
}

model.code = "ERROR";
model.message = "";

if (pageId != null)
{
	var object = sitedata.getObject("page", pageId);
	if (object != null)
	{
		sitedata.associateTemplate(templateId, pageId, formatId);
		model.code = "OK";
		model.data = object;
	}
	else
	{
		model.code = "ERROR";
		model.message = "Unable to find object";
	}
}
else
{
	model.code = "ERROR";
	model.message = "Bad Arguments";
}


