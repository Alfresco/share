function main()
{
   // Widget instantiation metadata...
   var inlineEditMgr = {
      id : "InlineEditMgr", 
      name : "Alfresco.InlineEditMgr",
      options : {
         nodeRef : page.url.args.nodeRef,
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : ""
      }
   };
   model.widgets = [inlineEditMgr];
}

main();
