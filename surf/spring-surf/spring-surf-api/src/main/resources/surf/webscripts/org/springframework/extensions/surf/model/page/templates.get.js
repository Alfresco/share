<import resource="/org/springframework/extensions/surf/api.lib.js">

var pageId = url.templateArgs["id"];

var associations = { };

var templatesMap = sitedata.findTemplatesMap(pageId);
for(var formatId in templatesMap)
{
	var template = templatesMap[formatId];
	
	associations[formatId] = { };
	associations[formatId]["id"] = template.id;
	
	if(template.title != null)
	{
		associations[formatId]["title"] = template.title;
	}
	if(template.description != null)
	{
		associations[formatId]["description"] = template.description;
	}
}

model.associations = associations;
