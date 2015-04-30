<import resource="/org/springframework/extensions/surf/api.lib.js">

var objectTypeId = url.templateArgs["type"];
var objectId = args["id"];

var json = jsonUtils.toObject(requestbody.content);

model.code = "ERROR";
model.message = "";

var object = null;
if (objectId != null)
{
	object = sitedata.newObject(objectTypeId, objectId);
}
else
{
	object = sitedata.newObject(objectTypeId);
}

if (object != null)
{
	// update the object
	Surf.ModelObject.readFromJson(object, json);
	
	// save
	object.save();
	
	model.code = "OK";
	model.data = object;
}
