<import resource="/org/springframework/extensions/surf/api.lib.js">

// one of these is mandatory
var type = url.templateArgs["type"];
var id = url.templateArgs["id"];
var destId = args["dest"];

// optionals
var assocType = args["assocType"];
var formatId = args["f"];

// defaults
if (assocType == null)
{
	assocType = "template";
}

// determine bindings
var sourceId = null;
var sourceType = null;

if (type != null)
{
	sourceId = type;
	sourceType = "type";	
}
else if (id != null)
{
	sourceId = id;
	sourceType = "instance";	
}

model.code = "ERROR";
model.message = "";

if (sourceType == "type")
{
	sitedata.associateContentType(sourceId, destId, assocType, formatId);
	model.code = "OK";
	model.message = "";
}
if (sourceType == "instance")
{
	sitedata.associateContent(sourceId, destId, assocType, formatId);
	model.code = "OK";
	model.message = "";
}
