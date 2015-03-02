<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/workflow/workflow.lib.js">
model.hiddenTaskTypes = getHiddenTaskTypes();

var myConfig = new XML(config.script),
   filters = [],
   filterMap = {};

for each(var xmlFilter in myConfig..filter)
{
   filters.push(
   {
      type: xmlFilter.@type.toString(),
      parameters: xmlFilter.@parameters.toString()
   });
   filterMap[xmlFilter.@type.toString()] = xmlFilter.@parameters.toString();
}
model.filters = filters;

model.maxItems = getMaxItems();


function main()
{
   // Widget instantiation metadata...
   var myTasks = {
      id : "MyTasks",
      name : "Alfresco.dashlet.MyTasks",
      options : {
         hiddenTaskTypes : model.hiddenTaskTypes,
         maxItems : parseInt(model.maxItems),
         filters : filterMap,
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
   model.widgets = [myTasks, dashletResizer, dashletTitleBarActions];
}

main();
