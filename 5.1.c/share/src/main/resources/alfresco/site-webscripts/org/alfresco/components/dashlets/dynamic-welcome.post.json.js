var clientRequest = json.toString();
var clientJSON = JSON.parse(clientRequest);

// The dashboard we are modifiying of the form "user/" + user.name + "/dashboard"
var dashboardPage = decodeURIComponent(clientJSON.dashboardUrl);
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