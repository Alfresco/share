/**
 * Customise Site Dashlets component GET method
 */

function getNoOfColumns(template)
{
   var noOfColumns = 0;
   while (template.properties["gridColumn" + (noOfColumns + 1)] !== null)
   {
      noOfColumns++;
   }
   return noOfColumns;
}

function getUserGroups()
{
   var result = remote.connect("alfresco").get("/api/people/" + encodeURIComponent(user.name) + "?groups=true");
   if (result.status == status.STATUS_OK)
   {
      return JSON.parse(result.response).groups.map(function(group){ return group.itemName; });
   }
   return [];
}

function main()
{
   // Get available components of family/type dashlet
   var webscripts;
   if (args.dashboardType == "user")
   {
      webscripts = sitedata.findWebScripts("user-dashlet");
   }
   else if (args.dashboardType == "site")
   {
      webscripts = sitedata.findWebScripts("site-dashlet");   
   }
   if (!webscripts)
   {
      webscripts = [];
   }
   var tmp = sitedata.findWebScripts("dashlet");
   if (tmp || tmp.length != 0)
   {
      webscripts = webscripts.concat(tmp);
   }

   // Transform the webscripts to easy-to-access dashlet items for the template
   var availableDashlets = [];
   var usergroups;
   for (var i = 0; i < webscripts.length; i++)
   {
      var webscript = webscripts[i];
      var uris = webscript.getURIs();
      var scriptId, scriptName, shortNameId, descriptionId;
      if (uris !== null && uris.length > 0 && webscript.shortName !== null)
      {
         // Check if the webscript is limited to certain groups
         var allowed = true;
         for (var fi = 0; fi < webscript.familys.length; fi++)
         {
            if (webscript.familys[fi].indexOf("group:") == 0)
            {
               allowed = false;
               var group = webscript.familys[fi].substring("group:".length).trim();
               group += ""; // make it of type "string" rather than "object" so that indexOf works
               if (!usergroups)
               {
                  usergroups = getUserGroups();
               }
               if (usergroups.indexOf(group) != -1)
               {
                  allowed = true;
                  break;
               }
            }
         }

         if (allowed)
         {
            // Use the webscript ID to generate a message bundle ID
            //
            // This should really be retrieved from an explicit value but the web scripts framework does not provide
            // a means for storing message bundle IDs, and the web framework is not being used here.
            scriptId = webscript.id;
            scriptName = scriptId.substring(scriptId.lastIndexOf("/") + 1, scriptId.lastIndexOf("."));
            shortNameId = "dashlet." + scriptName + ".shortName";
            descriptionId = "dashlet." + scriptName + ".description";
            availableDashlets.push(
               {
                  url: uris[0],
                  // msg.get(key) returns key if no matching value
                  shortName: (msg.get(shortNameId) != shortNameId ? msg.get(shortNameId) : webscript.shortName),
                  description: (msg.get(descriptionId) != descriptionId ? msg.get(descriptionId) : webscript.description)
               });
         }
      }
      // else skip this webscript since it lacks uri or shortName
   }
   
   var dashboardId, dashboardId;
   if (args.dashboardType == "user")
   {
      dashboardId = "user/" + user.name + "/dashboard";
      dashboardUrl = "user/" + encodeURIComponent(user.name) + "/dashboard";
   }
   else if (args.dashboardType == "site")
   {
      dashboardId = "site/" + page.url.templateArgs.site + "/dashboard";
      dashboardUrl = dashboardId;
   }
   
   var components = sitedata.findComponents("page", null, dashboardId, null);
   if (components === undefined || components.length === 0)
   {
      components = [];
   }
   
   var welcomePanelEnabled = false;
   
   // Transform the webscripts to easy-to-access dashlet items for the template
   var columns = [[], [], [], []];
   for (i = 0; i < components.length; i++)
   {
      var comp = components[i];
   
      var regionId = comp.properties["region-id"],
         theUrl = comp.properties.url;
      if (regionId !== null && theUrl !== null)
      {
         // Create dashlet
         var shortName = null, description;
         for (var j = 0, d; j < availableDashlets.length; j++)
         {
            d = availableDashlets[j];
            if (d.url == theUrl)
            {
               shortName = d.shortName;
               description = d.description;
               break;
            }
         }
         if (shortName != null)
         {
            var dashlet =
            {
               url: theUrl,
               shortName: shortName,
               description: description,
               originalRegionId: regionId
            };
      
            // Use Java regex matching as it performs better.
            var javaStr = new java.lang.String(regionId);
            if (javaStr.matches(new java.lang.String("^component-\\d+-\\d+$")))
            {
               // Place it in correct column and in a temporary row literal
               var column = parseInt(regionId.substring(regionId.indexOf("-") + 1, regionId.lastIndexOf("-"))),
                  row = parseInt(regionId.substring(regionId.lastIndexOf("-") + 1));
               columns[column-1][row-1] = dashlet;
            }
         }
         
         if (regionId == "full-width-dashlet")
         {
            welcomePanelEnabled = true;
         }
      }
      // else skip this component since it lacks regionId or shortName
   }
   
   // clean undefined elements from columns
   var cleanArray = function (actual)
   {
      var newArray = new Array();
      for(var i = 0; i < actual.length; i++)
      {
         if (actual[i])
         {
            newArray.push(actual[i]);
         }
      }
      return newArray;
   }

   for (var i = 0; i < columns.length; i++)
   {
      columns[i] = cleanArray(columns[i]);
   }
   
   // Get current template
   var currentTemplate = sitedata.findTemplate(dashboardId),
      currentNoOfColumns = getNoOfColumns(currentTemplate),
      currentLayout = 
      {
         templateId: currentTemplate.id,
         noOfColumns: currentNoOfColumns,
         description: currentTemplate.description
      };
   
   // Define the model for the template
   model.availableDashlets = availableDashlets;
   model.dashboardUrl = dashboardUrl;
   model.dashboardId = dashboardId;
   model.columns = columns;
   model.currentLayout = currentLayout;
   model.welcomePanelEnabled = welcomePanelEnabled;
   model.showWelcomePanelOptions = args.dashboardType == "user";
   
   // Widget instantiation metadata...
   var customizeDashlets = {
      id : "CustomiseDashlets",
      name : "Alfresco.CustomiseDashlets",
      options : {
         currentLayout : {
            templateId : currentTemplate.id,
            noOfColumns : parseInt("" + currentNoOfColumns),
            description : currentTemplate.description,
            icon : url.context + "/res/components/dashboard/images/" + currentTemplate.id + ".png"
         },
         dashboardUrl : model.dashboardUrl,
         dashboardId : model.dashboardId,
         welcomePanelEnabled: model.welcomePanelEnabled
      }
   };
   model.widgets = [customizeDashlets];
}

main();