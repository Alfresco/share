<import resource="/org/springframework/extensions/surf/api.lib.js">

var objectTypeId = url.templateArgs["type"];
var objectId = url.templateArgs["id"];

model.code = "ERROR";
model.message = "";

if (objectTypeId != null && objectId != null)
{
	var object = sitedata.getObject(objectTypeId, objectId);
	if (object != null)
	{
		model.data = object;
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


