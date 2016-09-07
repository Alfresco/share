<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

function main()
{
   var wikipage = args.wikipage;
   if (wikipage)
   {
      var wikiData = doGetCall("/slingshot/wiki/page/" + page.url.templateArgs.site + "/" + encodeURIComponent(wikipage) + "?minWikiData=true");
      if (wikiData)
      {
         var allowUnfilteredHTML = new XML(config.script).allowUnfilteredHTML;
         model.wikipage = allowUnfilteredHTML ? wikiData.pagetext : stringUtils.stripUnsafeHTML(wikiData.pagetext);
         model.pageList = wikiData.pageList;
         model.wikiLink = String(wikipage);
         model.pageTitle = String(wikipage).replace(/_/g, " ");
      }
   }
   model.pagePrefix = getPagePrefix();
   
   // Call the repository to see if the user is site manager or not
   var userIsSiteManager = false,
       json = remote.call("/api/sites/" + page.url.templateArgs.site + "/memberships/" + encodeURIComponent(user.name));
   
   if (json.status == 200)
   {
      var obj = JSON.parse(json);
      if (obj)
      {
         userIsSiteManager = (obj.role == "SiteManager");
      }
   }
   model.userIsSiteManager = userIsSiteManager;
   
   var wikiDashlet = {
      id : "WikiDashlet", 
      name : "Alfresco.dashlet.WikiDashlet",
      assignTo : "wiki_" + args.htmlid.replace(/-/g, "_"),
      options : {
         pages : (model.pageList != null) ? model.pageList : [],
         guid : instance.object.id,
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         pagePrefix : model.pagePrefix
      }
   };

   var dashletResizer = {
      id : "DashletResizer", 
      name : "Alfresco.widget.DashletResizer",
      initArgs : ["\"" + args.htmlid + "\"","\"" + instance.object.id + "\""],
      useMessages: false
   };

   var actions = [];
   if (model.userIsSiteManager)
   {
      actions.push(
      {
         cssClass: "edit",
         eventOnClick: { _alfValue : "editWikiDashletEvent_" + args.htmlid.replace(/-/g, "_"), _alfType: "REFERENCE"},
         tooltip: msg.get("dashlet.edit.tooltip")
      });
   }
   actions.push(
      {
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
   model.widgets = [wikiDashlet, dashletResizer, dashletTitleBarActions];
}

/**
 * Retrieve the wiki page prefix from Site Dashboard page meta-data. This additional meta-data is
 * set when a Site Manager customizes a dashboard and renames a components - such as renaming Wiki.
 * If we find that the wiki page component has been renamed, use that as the prefix instead.
 * 
 * @retrun page prefix - will default to "label.header-prefix" msg if no meta-data is set.
 */
function getPagePrefix()
{
   var siteId = page.url.templateArgs.site,
       pagePrefix = msg.get("label.header-prefix");
   if (siteId)
   {
      var dashboardPageData = sitedata.getPage("site/" + siteId + "/dashboard");
      if (dashboardPageData !== null)
      {
         var sitePages = dashboardPageData.properties.sitePages;
         if (sitePages)
         {
            try
            {
               // Print array as json and use JSON.parse so we get a Rhino javascript Array to execute as usual
               sitePages = JSON.parse('{"$":' + sitePages + '}').$;
               for (var i = 0; i < sitePages.length; i++)
               {
                  if (sitePages[i].pageId == "wiki-page")
                  {
                     pagePrefix = sitePages[i].sitePageTitle || pagePrefix;
                     break;
                  }
               }
            }
            catch (e)
            {
               // if the meta is not available or invalid then will default back to preset label
            }
         }
      }
   }
   return pagePrefix;
}

main();
