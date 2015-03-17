/**
 * Cloud Folder Picker Server side work.
 *
 */

function main()
{
   var connector = remote.connect("alfresco");
   var networkResult = connector.get("/cloud/tenant/information");
   // Check and parse the response
   if (networkResult.status.code === 200)
   {
      var response = JSON.parse(networkResult);
      var networks = [];

      if (response.multiTenancyEnabled)
      {
         networks.push(response.defaultTenant);
         var secondaryTenants = response.secondaryTenants;
         for (var i in secondaryTenants)
         {
            networks.push(secondaryTenants[i]);
         }
      }

      var activeNetworks = [];
      var inactiveNetworks = [];

      if (networks.length == 0)
      {
         status.code = 204;
      }
      else
      {
         for (var i in networks)
         {
            if (networks[i].isSyncEnabled)
            {
               activeNetworks.push(networks[i]);
            }
            else
            {
               inactiveNetworks.push(networks[i]);
            }
         }
         if (activeNetworks.length === 0)
         {
            status.code = 204;
         }
      }
      model.networks = activeNetworks.concat(inactiveNetworks);
   }
   else
   {
      status.code=500;
      status.message="unable to load network list";
      status.redirect=true;
   }
}

main();