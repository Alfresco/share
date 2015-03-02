<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/dashlets/my-workspaces.inc.js">
main("meeting-workspace");

function widgets()
{
   // Widget instantiation metadata...
   var myMeetingWorkspaces = {
      id : "MyMeetingWorkspaces",
      name : "Alfresco.dashlet.MyMeetingWorkspaces",
      options : {
         imapEnabled : model.imapServerEnabled,
         sites : model.sites
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
               tooltip: msg.get("dashlet.help.tooltip")
            }
         ]
      }
   };
   model.widgets = [myMeetingWorkspaces, dashletResizer, dashletTitleBarActions];
}

widgets();
