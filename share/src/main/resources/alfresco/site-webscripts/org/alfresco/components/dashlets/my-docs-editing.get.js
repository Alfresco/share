function main()
{
   // Component definition
   var myDocsEditing = {
      id: "MyDocsEditing",
      name: "Alfresco.dashlet.MyDocsEditing"
   };
   
   var dashletResizer = {
      id: "DashletResizer",
      name: "Alfresco.widget.DashletResizer",
      initArgs: ["\"" + args.htmlid + "\"", "\"" + instance.object.id + "\""],
      useMessages: false
   };

   var dashletTitleBarActions = {
      id: "DashletTitleBarActions",
      name: "Alfresco.widget.DashletTitleBarActions",
      useMessages: false,
      options: {
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
   model.widgets = [myDocsEditing, dashletResizer, dashletTitleBarActions];
}

main();