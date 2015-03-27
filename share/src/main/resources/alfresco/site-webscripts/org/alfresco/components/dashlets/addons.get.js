<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/dashlets/rssfeed.get.js">

main();

function defineWidgets()
{
   // Widget instantiation metadata...
   model.target = args.target || "_self";
   var rssFeed = {
      id : "RssFeed",
      name : "Alfresco.dashlet.RssFeed",
      assignTo : "addOnsRssFeed",
      options : {
         componentId : instance.object.id,
         feedURL : model.uri,
         target: model.target,
         limit : (!isNaN(model.limit) && model.limit != 100) ? model.limit : "all",
         titleElSuffix : "-title",
         targetElSuffix : "-scrollableList"
      }
   };

   var actions = [];
   if (model.userIsSiteManager)
   {
      actions.push({
         cssClass: "edit",
         eventOnClick: {
            _alfValue : "addOnsRssFeedDashletEvent" + args.htmlid.replace(/-/g, "_"),
            _alfType: "REFERENCE"
         },
         tooltip: msg.get("dashlet.edit.tooltip")
      });
   }
   actions.push({
      cssClass: "help",
      bubbleOnClick:
      {
         message: msg.get("dashlet.help")
      },
      tooltip: msg.get("dashlet.help.tooltip")
   });

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
         actions : actions
      }
   };
   model.widgets = [rssFeed, dashletResizer, dashletTitleBarActions];
}

defineWidgets();

