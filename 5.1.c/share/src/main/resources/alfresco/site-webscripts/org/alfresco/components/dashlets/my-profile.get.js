<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/dashlets/my-workspaces.inc.js">

main("meeting-workspace");

function widgets()
{
   // Widget instantiation metdata...
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
   model.widgets = [dashletTitleBarActions];
}

widgets();
