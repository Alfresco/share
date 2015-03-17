<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{

   // Widget instantiation metadata...
   model.widgets = [];
   model.filters = getFilters();
   model.userMembership = AlfrescoUtil.getSiteMembership(page.url.templateArgs.site);
   
   var forumSummary = {
      id: "ForumSummary",
      name: "Alfresco.dashlet.ForumSummary",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         searchRootNode : (config.scoped['RepositoryLibrary']['root-node']).value,
         filters : model.filters,
         regionId : args['region-id']
      }
   };
   
   var dashletResizer = {
      id: "DashletResizer",
      name: "Alfresco.widget.DashletResizer",
      initArgs: ["\"" + args.htmlid + "\"", "\"" + instance.object.id + "\""],
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

   model.widgets = [forumSummary, dashletResizer, dashletTitleBarActions];
}

function getFilters()
{
   var myConfig = new XML(config.script);
   var filters = [];

   for each(var xmlFilter in myConfig..filter)
   {
      var filter = new XML(xmlFilter);
      var name = filter.@name.toString();
      var options = [];
      for each(var option in filter..option)
      {
         options.push(
         {
            value: option.@value.toString(),
            label: option.@label.toString()
         });
      }
      
      filters.push(
      {
         name: xmlFilter.@name.toString(),
         scope: xmlFilter.@scope.toString(),
         options: options
      });
   }

   return filters;
}

main();
