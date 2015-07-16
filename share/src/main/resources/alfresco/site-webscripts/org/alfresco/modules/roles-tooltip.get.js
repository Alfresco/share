/**
 * Roles Tooltip module/component GET method
 */

function main()
{
   if (args.noderef)
   {
      // This may be called from a context where there is a nodref such as Manage Permissions
      var nodeRefUri = args.noderef.replace("://", "/");
      var connector = remote.connect("alfresco");
      var remoteUrl = "slingshot/doclib/permissions/" + nodeRefUri;
      var result = connector.get(remoteUrl);
      if (result.status == status.STATUS_OK)
      {
         var response = result.response;
         var data = JSON.parse(response);
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
         var jresponse = result.response;
         var jstatus = result.status;
         var jmessage = result.message;
         status.setCode(json.status, json.status.message);
      }
   }
   else if (args.siteId)
   {
      // This may also be called from a context where there is only a site id such as Add Users
      var json = remote.call("/api/sites/" + args.siteId + "/roles");
      if (json.status == 200)
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