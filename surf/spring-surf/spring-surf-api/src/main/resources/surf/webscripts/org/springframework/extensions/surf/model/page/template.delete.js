<import resource="/org/springframework/extensions/surf/api.lib.js">

var pageId = url.templateArgs["id"];
var formatId = url.args["formatId"];
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
		sitedata.unassociateTemplate(pageId, formatId);
		model.code = "OK";
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


