<import resource="/org/springframework/extensions/surf/api.lib.js">

var objectTypeId = url.templateArgs["type"];
var objectId = url.templateArgs["id"];

var json = jsonUtils.toObject(requestbody.content);

model.code = "ERROR";
model.message = "";

var object = sitedata.getObject(objectTypeId, objectId);
if (object != null)
{
	// update the object
	Surf.ModelObject.readFromJson(object, json);
	
	// update the object
	object.save();
	
	model.code = "OK";
	model.data = object;
}
