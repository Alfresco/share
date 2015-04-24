<import resource="/org/springframework/extensions/surf/api.lib.js">

var data = {};

var formatIds = sitedata.getFormatIds();
for (var i = 0; i < formatIds.length; i++)
{
	var formatId = formatIds[i];
	
	var title = sitedata.getFormatTitle(formatId);
	var description = sitedata.getFormatDescription(formatId);
	
	data[formatId] = { };
	data[formatId]["title"] = title;
	data[formatId]["description"] = description;
}

model.data = data;
