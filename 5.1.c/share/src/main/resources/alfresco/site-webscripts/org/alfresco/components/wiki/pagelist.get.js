function main()
{
   // retrieve the wiki pages for the current site
   var uri = "/slingshot/wiki/pages/" + page.url.templateArgs.site;
   var filter = page.url.args.filter;
   if (filter)
   {
      uri += "?filter=" + filter;
   }

   var connector = remote.connect("alfresco");
   var result = connector.get(uri);
   if (result.status.code == status.STATUS_OK)
   {
      model.pageList = JSON.parse(result.response);
   }
   else
   {
      model.error = "Error during remote call. Server code " + result.status + ".";
   }

   // Widget instantiation metadata...
   var pages = [];
   if (model.pageList != null && model.pageList.pageTitles != null && model.pageList.pageTitles.length > 0)
   {
      for (var i=0; i<model.pageList.pageTitles.length; i++)
      {
         pages.push(model.pageList.pageTitles[i]);
      }
   }
   else if (model.pageList != null && model.pageList.pages != null && model.pageList.pages.length > 0)
   {
      for (var i=0; i<model.pageList.pages.length; i++)
      {
         pages.push(model.pageList.pages[i].title);
      }
   }

   
   var wikiList = {
      id : "WikiList", 
      name : "Alfresco.WikiList",
      useMessages: false,
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         pages: pages,
         permissions : {
           create : (model.pageList != null && model.pageList.permissions != null && model.pageList.permissions.create != null) ? model.pageList.permissions.create : "false"
         },
         filterId : (page.url.args.filter != null) ? page.url.args.filter : "recentlyModified"
      }
   };
   model.widgets = [wikiList];
}

main();

