if (url.args['sectionId'] != null)
{
	// If a search has already been performed then the search id will be available as a url parameter.
	// In this case we want to use this in preference to the section on the Surf RequestContext object.
	// This is because the search should be done within the scope of the original section and not the 
	// search section.
	model.sectionId = url.args['sectionId'];
}
else 
{
	// Use the current page's section as the scope for the search.
	model.sectionId = context.properties.section.id;	
}

if (url.args['phrase'] != null) 
{
	model.phrase = url.args['phrase'];
}
else
{
	model.phrase = null;
}