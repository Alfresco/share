function main()
{
   // Widget instantiation metadata...
   var linksView = {
      id : "LinksView", 
      name : "Alfresco.LinksView",
      options : {
         siteId : page.url.templateArgs.site,
         containerId : "links",
         linkId : page.url.args.linkId
      }
   };
   model.widgets = [linksView];
}

main();

