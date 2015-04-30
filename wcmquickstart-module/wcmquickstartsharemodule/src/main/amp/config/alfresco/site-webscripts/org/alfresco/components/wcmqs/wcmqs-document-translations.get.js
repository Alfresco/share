function main()
{
	var nodeRef = args.nodeRef;

	/* Fetch the details from the repo */
	var result = remote.connect("alfresco").get("/api/webassettranslations?nodeRef=" + nodeRef);
	if (result.status == 200)
	{
		model.nodeRef = nodeRef;

		var detailsObj = JSON.parse(result);
		if (detailsObj.data)
		{
			model.translationData = detailsObj.data;
		}


	//	model.name = detailsObj.data.name;
	//	model.type = detailsObj.data.type;
	//	model.parentNodeRef = detailsObj.data.parentNodeRef;

		/* Languages in the site */
	//	model.locales = detailsObj.data.locales;
		/* Language of the node */
	//	model.currentLocale = detailsObj.data.locale;
	//	model.currentLocaleName = detailsObj.data.localeName;

		/* Translations of the node */
	//	model.translations = detailsObj.data.translations;
		/* Translated parents */
	//	model.parents = detailsObj.data.parents;
		/* Is translation enabled for this node? */
	//	model.translationEnabled = detailsObj.data.translationEnabled;
	}
}

main();