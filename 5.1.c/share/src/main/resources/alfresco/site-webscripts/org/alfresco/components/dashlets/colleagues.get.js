<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function sortByName(membership1, membership2)
{
   var name1 = membership1.authority ? membership1.authority.firstName + membership1.authority.lastName : "";
   var name2 = membership2.authority ? membership2.authority.firstName + membership2.authority.lastName : "";
   return (name1 > name2) ? 1 : (name1 < name2) ? -1 : 0;
}

/**
 * Get Max Items from local configuration
 */
function getMaxItems()
{
   var myConfig = new XML(config.script),
      maxItems = myConfig["max-items"];

   if (maxItems)
   {
      maxItems = myConfig["max-items"].toString();
   }
   return parseInt(maxItems && maxItems.length > 0 ? maxItems : 100, 10);
}

/**
 * Site Colleagues component GET method
 */
function main()
{
   // Call the repo for the site memberships
   var maxItems = getMaxItems(),
      size = maxItems + 1,
      json = remote.call("/api/sites/" + page.url.templateArgs.site + "/memberships?size=" + size + "&authorityType=USER");
   
   var memberships = [],
      totalResults = 0;
   
   if (json.status == 200)
   {
      // Create javascript objects from the repo response
      var obj = JSON.parse(json);
      if (obj)
      {
         totalResults = obj.length;
         memberships = obj.slice(0, maxItems);
         memberships.sort(sortByName);
      }
   }
   
   // Prepare the model
   model.memberships = memberships;
   model.totalResults = totalResults;
   model.maxResults = maxItems;
   model.userMembership = AlfrescoUtil.getSiteMembership(page.url.templateArgs.site);
   
   // Widget instantiation metadata...
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
         actions : [
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
   model.widgets = [dashletResizer, dashletTitleBarActions];
   model.addUsersPage = config.scoped["SitePages"]["additional-pages"].getChildValue("add-users");
}

main();