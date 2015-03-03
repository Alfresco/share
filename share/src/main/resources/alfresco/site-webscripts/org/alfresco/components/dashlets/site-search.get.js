function main()
{
   // Widget instantiation metadata...
   var siteSearch = {
      id : "SiteSearch", 
      name : "Alfresco.dashlet.SiteSearch",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         searchRootNode : (config.scoped['RepositoryLibrary']['root-node']).value,
         regionId : args['region-id']
      }
   };

   var dashletResizer = {
      id : "DashletResizer", 
      name : "Alfresco.widget.DashletResizer",
      initArgs : ["\"" + args.htmlid + "\"", "\"" + instance.object.id + "\""],
      useMessages: false
   };

   var dashletTitleBarActions = {
      id : "DashletTitleBarActions", 
      name : "Alfresco.widget.DashletTitleBarActions",
      useMessages : false,
      options : {
         actions: [
            {
               cssClass: "help",
               bubbleOnClick:
               {
                  message: msg.get("dashlet.help")
               },
               tooltip:  msg.get("dashlet.help.tooltip")
            }
         ]
      }
   };
   model.widgets = [siteSearch, dashletResizer, dashletTitleBarActions];
}

main();
