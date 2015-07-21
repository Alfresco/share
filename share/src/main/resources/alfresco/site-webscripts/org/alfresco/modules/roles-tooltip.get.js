/**
 * Roles Tooltip module/component GET method
 */

function main()
{
   if (args.noderef)
   {
      // This may be called from a context where there is a nodref such as Manage Permissions
      var nodeRefUri = args.noderef.replace("://", "/");
      var json = remote.call("/slingshot/doclib/permissions/" + nodeRefUri);
      if (json.status == status.STATUS_OK)
      {
         var data = JSON.parse(json);
         if (data)
         {
            var rolesTooltipData = [];
            for (var i = 0, j = data.settable.length; i < j; i++)
            {
               rolesTooltipData.push({
                  roleName: msg.get("role." + data.settable[i]),
                  roleDescription: msg.get("role." + data.settable[i] + ".description")
               });
            }
            model.rolesTooltipData = rolesTooltipData;
         }
      }
      else
      {
         status.setCode(json.status, json.status.message);
      }
   }
   else if (args.siteId)
   {
      // This may also be called from a context where there is only a site id such as Add Users
      var json = remote.call("/api/sites/" + args.siteId + "/roles");
      if (json.status == status.STATUS_OK)
      {
         var data = JSON.parse(json);
         if (data)
         {
            var rolesTooltipData = [];
            for (var i = 0, j = data.siteRoles.length; i < j; i++)
            {
               if (data.siteRoles[i] != "None")
               {
                  rolesTooltipData.push({
                     roleName: msg.get("role." + data.siteRoles[i]),
                     roleDescription: msg.get("role." + data.siteRoles[i] + ".description")
                  });
               }
            }
            model.rolesTooltipData = rolesTooltipData;
         }
      }
      else
      {
         status.setCode(json.status, json.status.message);
      }
   }
}

main();