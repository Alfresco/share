function main()
{
   var title = page.url.args.title;
   model.exists = false;
   if (title)
   {
      var context = page.url.context + "/page/site/" + page.url.templateArgs.site + "/wiki-page?title=" + page.url.args.title,
      uri = "/slingshot/wiki/page/" + encodeURIComponent(page.url.templateArgs.site) + "/" + encodeURIComponent(page.url.args.title) + "?context=" + encodeURI(context),
      connector = remote.connect("alfresco"),
      result = connector.get(uri);

      // we allow 200 and 404 as valid responses - any other error then cannot show page
      // the 404 response means we can create a new page for the title
      if (result.status.code == status.STATUS_OK || result.status.code == status.STATUS_NOT_FOUND)
      {
         model.exists = (result.status.code == status.STATUS_OK);
      }
   }
   
   // Widget instantiation metadata...
   var wikiToolbar = {
      id: "WikiToolbar", 
      name: "Alfresco.WikiToolbar",
      options: {
         siteId: (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         title: title ? title : "",
         showBackLink: (args.showBackLink == "true"),
         exists: model.exists
      }
   };
   model.widgets = [wikiToolbar];
}

main();
