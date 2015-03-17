function main() {
   // Widget instantiation metadata...
   var widget = {
      id : "CreateContentMgr", 
      name : "Alfresco.CreateContentMgr",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         isContainer: "true" == ((page.url.args.isContainer != null) ? page.url.args.isContainer : "false")
      }
   };
   model.widgets = [widget];
}
main();