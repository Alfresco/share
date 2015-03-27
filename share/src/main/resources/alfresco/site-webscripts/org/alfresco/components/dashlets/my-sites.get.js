function main()
{
   // Prepare the model for the template
   model.imapServerEnabled = imapServerStatus.enabled;
   
   // Widget instantiation metadata...
   var dashboardconfig = config.scoped['Dashboard']['dashboard'];
   var listSize = dashboardconfig.getChildValue('summary-list-size');
   if (listSize == null)
   {
      listSize = 100;
   }
   
   var mySites = {
      id : "MySites", 
      name : "Alfresco.dashlet.MySites",
      options : {
         imapEnabled : imapServerStatus.enabled,
         listSize : parseInt(listSize),
         regionId : args['region-id']
      }
   };
   
   var dashletResizer = {
      id : "DashletResizer", 
      name : "Alfresco.widget.DashletResizer",
      initArgs : ["\"" + args.htmlid + "\"", "\"" + instance.object.id + "\""],
      useMessages : false
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
               tooltip: msg.get("dashlet.help.tooltip")
            }
         ]
      }
   };
   model.widgets = [mySites, dashletResizer, dashletTitleBarActions];
   model.showCreateSite = true;
}

main();