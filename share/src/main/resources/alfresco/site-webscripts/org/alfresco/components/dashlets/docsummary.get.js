<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function runEvaluator(evaluator)
{
   return eval(evaluator);
}

/* Get filters */
function getFilters()
{
   var myConfig = new XML(config.script),
      filters = [];

   for each (var xmlFilter in myConfig..filter)
   {
      // add support for evaluators on the filter. They should either be missing or eval to true
      if (xmlFilter.@evaluator.toString() === "" || runEvaluator(xmlFilter.@evaluator.toString()))
      {
         filters.push(
         {
            type: xmlFilter.@type.toString(),
            parameters: xmlFilter.@parameters.toString()
         });
      }
   }
   return filters
}

/* Max Items */
function getMaxItems()
{
   var myConfig = new XML(config.script),
      maxItems = myConfig["max-items"];

   if (maxItems)
   {
      maxItems = myConfig["max-items"].toString();
   }
   return parseInt(maxItems && maxItems.length > 0 ? maxItems : 50, 10);
}

var regionId = args['region-id'];
model.preferences = AlfrescoUtil.getPreferences("org.alfresco.share.docsummary.dashlet." + regionId);
model.filters = getFilters();
model.maxItems = getMaxItems();

function main()
{
   // Widget instantiation metadata...
   var docSummary = {
      id : "DocSummary",
      name : "Alfresco.dashlet.DocSummary",
      options : {
         filter : model.preferences.filter != null ? model.preferences.filter : "recentlyModifiedByMe",
         validFilters : model.filters,
         simpleView : (model.preferences.simpleView == true), 
         maxItems : parseInt(model.maxItems),
         regionId : regionId
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
   model.widgets = [docSummary, dashletResizer, dashletTitleBarActions];
}

main();
