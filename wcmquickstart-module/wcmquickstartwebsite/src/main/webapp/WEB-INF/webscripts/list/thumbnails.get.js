// Get the collection of articles indicated in the component properties
model.articles = collectionService.getCollection(context.properties.section.id, args.collection);

// If a link page is specified in the component properties then pass this to the view
if (args.linkPage != null) 
{
	model.linkParam = '?view='+args.linkPage;
}

//If a sub title is specified in the component properties then pass this to the view
if (args.subTitle != null) 
{
	model.subTitle = args.subTitle;
}
