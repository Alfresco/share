<import resource="/org/springframework/extensions/surf/api.lib.js">

model.code = "ERROR";
model.message = "";

var object = sitedata.getSiteConfiguration();
if (object != null)
{
	model.data = object;
	model.properties = object.properties;
	
	model.code = "OK";
}
else
{
	model.code = "ERROR";
	model.message = "Unable to locate site configuration";
}
