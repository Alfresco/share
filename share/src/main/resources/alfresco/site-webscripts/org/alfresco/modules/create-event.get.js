<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

if (args['uri']) 
{
	var uri = unescape(args['uri']);
	// Call the repo for the event information
	model.event = doGetCall(uri);
	model.edit = true;
}
else
{
	model.event = {};
	model.event.what = model.event.location = model.event.description = model.event.docfolder = '';
	model.edit =false;
}

