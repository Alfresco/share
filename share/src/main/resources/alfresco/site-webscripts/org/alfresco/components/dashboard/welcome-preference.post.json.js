/**
 * Welcome preference POST method
 */

function main()
{

   var clientRequest = json.toString();
   var clientJSON = JSON.parse(clientRequest);
   
   var welcomePanelEnabled = decodeURIComponent(clientJSON.welcomePanelEnabled);
   var dashboardPage = "user/" + user.name + "/dashboard";
   var welcomePanelRegionId = "full-width-dashlet";
   
   if (welcomePanelEnabled == "true")
   {
      var comp = sitedata.newComponent("page", welcomePanelRegionId, dashboardPage);
      comp.properties.url = "/components/dashlets/dynamic-welcome";
      comp.properties.dashboardType = "user";
      comp.save();
   }
   else
   {
      var oldComponents = sitedata.findComponents("page", null, dashboardPage, null);
      // Unbind the full-width-dashlet (which will be the welcome for the dashboard
      for (var oi = 0; oi < oldComponents.length; oi++)
      {
         var oldComponent = oldComponents[oi];
         var regionId = oldComponent.properties["region-id"];
         if (regionId == "full-width-dashlet")
         {
            sitedata.unbindComponent("page", regionId, dashboardPage);
            break;
         }
      }
   }
   
   model.welcomePanelEnabled = welcomePanelEnabled;
}

main();