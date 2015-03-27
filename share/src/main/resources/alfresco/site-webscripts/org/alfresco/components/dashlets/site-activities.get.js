function getFilters(filterType)
{
   var myConfig = new XML(config.script),
       filters = [];

   for each (var xmlFilter in myConfig[filterType].filter)
   {
      filters.push(
      {
         type: xmlFilter.@type.toString(),
         label: xmlFilter.@label.toString()
      });
   }

   return filters;
}

model.filterRanges = getFilters("filter-range");
model.filterTypes = getFilters("filter-type");
model.filterActivities = getFilters("filter-activities");

function main()
{
   // Widget instantiation metadata...
   var myActivities = {
      id : "Activities", 
      name : "Alfresco.dashlet.Activities",
      assignTo : "activities",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         mode : "site",
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
               cssClass: "rss",
               eventOnClick: { _alfValue : "activitiesFeedDashletEvent" + args.htmlid.replace(/-/g, "_"), _alfType: "REFERENCE"},
               tooltip: msg.get("dashlet.rss.tooltip")
            },
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
   model.widgets = [myActivities, dashletResizer, dashletTitleBarActions];
}

main();
