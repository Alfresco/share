<import resource="/org/springframework/extensions/surf/api.lib.js">

var json = jsonUtils.toObject(requestbody.content);

model.code = "ERROR";
model.message = "";

var object = sitedata.getSiteConfiguration();
if (object != null)
{
	// update the object
	Surf.ModelObject.readFromJson(object, json);
	
	object.save();
	
	model.data = object;	
	model.code = "OK";
}
else
{
	model.code = "ERROR";
	model.message = "Unable to locate site configuration";
}


