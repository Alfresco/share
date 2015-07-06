/**
 * Welcome panel preference component GET method
 */


function main()
{
   var welcomePanelEnabled = false;
   var dashboardPage = "user/" + encodeURIComponent(user.name) + "/dashboard";
   var oldComponents = sitedata.findComponents("page", null, dashboardPage, null);
   for (var oi = 0; oi < oldComponents.length; oi++)
   {
      var oldComponent = oldComponents[oi];
      var regionId = oldComponent.properties["region-id"];
      if (regionId == "full-width-dashlet")
      {
         welcomePanelEnabled = true;
         model.componentId = oldComponent.id;
         break;
      }
   }
   
   model.welcomePanelEnabled = welcomePanelEnabled;
   
   var welcomePreference = {
      id : "WelcomePreference",
      name : "Alfresco.WelcomePreference",
      options: {
         welcomePanelEnabled: welcomePanelEnabled
      }
   };
   model.widgets = [welcomePreference];
}

main();
