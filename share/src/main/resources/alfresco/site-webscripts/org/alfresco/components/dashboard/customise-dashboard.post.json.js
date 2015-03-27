/**
 * Customise Dashboard component POST method
 */

function main()
{
   // Get clients json request as a js object
   var clientRequest = json.toString();
   var clientJSON = JSON.parse(clientRequest);
   
   // The dashboard we are modifiying
   var dashboardPage = clientJSON.dashboardPage;
   var newDashlets = clientJSON.dashlets;
   
   // Change the dashbpards template
   var templateId = clientJSON.templateId;
   sitedata.associateTemplate(templateId, dashboardPage);
   
   // Get existing dashlets/component bindings for the page
   var oldComponents = sitedata.findComponents("page", null, dashboardPage, null);
   if (oldComponents === undefined || oldComponents.length === 0)
   {
      oldComponents = [];
   }
   
   // Unbind all components, old properties will be stored in the oldComponents list.
   for (var oi = 0; oi < oldComponents.length; oi++)
   {
      var oldComponent = oldComponents[oi];
      var regionId = oldComponent.properties["region-id"];
      if (regionId.match("^component-\\d+-\\d+$"))
      {
         // Unbind the component if it has been delete or moved
         var existingDashlet = null;
         for (var ni = 0; ni < newDashlets.length; ni++)
         {
            var newDashlet = newDashlets[ni];
            if (newDashlet.originalRegionId == regionId)
            {
               existingDashlet = newDashlet;
               break;
            }
         }
         if (existingDashlet == null || existingDashlet.regionId != existingDashlet.originalRegionId)
         {
            // Delete dashlet if it has been removed or moved         
            sitedata.unbindComponent("page", regionId, dashboardPage);
         }
      }
   }
   
   // Create bindings for new and moved dashlets.
   var newDashlets = clientJSON.dashlets;
   for (var ni = 0; ni < newDashlets.length; ni++)
   {
      var newDashlet = newDashlets[ni];
      if (newDashlet.originalRegionId)
      {
         if (newDashlet.originalRegionId != newDashlet.regionId)
         {
            // An existing/moved dashlet
            var existingDashlet = null;
            for (var oi = 0; oi < oldComponents.length; oi++)
            {
               var oldDashlet = oldComponents[oi];
               if (oldDashlet.properties["region-id"] == newDashlet.originalRegionId)
               {
                  existingDashlet = oldDashlet;
                  break;
               }
            }
            if (existingDashlet != null)
            {
               // Its an old component that has been moved, use object from the list so we don't loose the properties            
               var comp = sitedata.newComponent("page", newDashlet.regionId, dashboardPage);
               for (var propertyKey in existingDashlet.properties)
               {
                  comp.properties[propertyKey] = existingDashlet.properties[propertyKey];
               }
               comp.properties["region-id"] = newDashlet.regionId;
               comp.properties.url = newDashlet.url;
               comp.save();
            }
         }
      }
      else
      {
         // An new/added dashlet
         var comp = sitedata.newComponent("page", newDashlet.regionId, dashboardPage);
         comp.properties.url = newDashlet.url;
         comp.save();
      }
   }
}

main();