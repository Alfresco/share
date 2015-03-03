<import resource="classpath:alfresco/site-webscripts/org/alfresco/utils/feed.utils.js">

/**
 * Main entry point for the webscript
 */
function main()
{
   var uri = args.feedurl;
   if (!uri)
   {
      // Use the default
      var conf = new XML(config.script);
      uri = getValidRSSUri(conf.feed[0].toString());
   }
   model.uri = uri;
   model.limit = args.limit || 100;
   model.target = args.target || "_self";

   var userIsSiteManager = true;
   if (page.url.templateArgs.site)
   {
      // We are in the context of a site, so call the repository to see if the user is site manager or not
      userIsSiteManager = false;
      var json = remote.call("/api/sites/" + page.url.templateArgs.site + "/memberships/" + encodeURIComponent(user.name));

      if (json.status == 200)
      {
         var obj = JSON.parse(json);
         if (obj)
         {
            userIsSiteManager = (obj.role == "SiteManager");
         }
      }
   }
   model.userIsSiteManager = userIsSiteManager;
   
   //Widget instantiation metadata...
   var rssFeed = {
      id : "RssFeed", 
      name : "Alfresco.dashlet.RssFeed",
      assignTo : "rssFeed",
      options : {
         componentId : instance.object.id,
         feedURL : model.uri,
         target: model.target,
         limit : (!isNaN(model.limit) && model.limit != 100) ? model.limit : "all",
         titleElSuffix : "-title",
         targetElSuffix : "-scrollableList"
      }
   };

   var dashletResizer = {
      id : "DashletResizer", 
      name : "Alfresco.widget.DashletResizer",
      initArgs : ["\"" + args.htmlid + "\"", "\"" + instance.object.id + "\""],
      useMessages: false
   };
   
   var actions = [];
   if (model.userIsSiteManager)
   {
      actions.push({
         cssClass: "edit",
         eventOnClick: {
            _alfValue : "rssFeedDashletEvent" + args.htmlid.replace(/-/g, "_"),
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
   var dashletTitleBarActions = {
      id : "DashletTitleBarActions", 
      name : "Alfresco.widget.DashletTitleBarActions",
      useMessages : false,
      options : {
         actions: actions
      }
   };
   model.widgets = [rssFeed, dashletResizer, dashletTitleBarActions];
}

main();