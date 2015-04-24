<import resource="/org/springframework/extensions/surf/api.lib.js">

// one of these is mandatory
var type = url.templateArgs["type"];
var id = url.templateArgs["id"];

// optionals
var destId = args["dest"];
var assocType = args["assocType"];
var formatId = args["f"];

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

model.code = "OK";
model.message = "";
model.results = sitedata.findContentAssociations(sourceId, sourceType, destId, assocType, formatId);
