var userData = {};
var groups = user.properties["alfUserGroups"];
if (groups != null)
{
    groups = groups.split(",");
    var processedGroups = {};
    for (var i=0; i<groups.length; i++)
    {
        processedGroups[groups[i]] = true;
    }
    userData.groups = processedGroups;
}
userData.isNetworkAdmin = user.properties["isNetworkAdmin"];
userData.isAdmin = user.capabilities["isAdmin"];
userData.name = user.name;

function main() {
   // Widget instantiation metadata...
   var widget = {
      id : "ConsoleHybridSyncManagement", 
      name : "Alfresco.ConsoleHybridSyncManagement",
      options : {
         pageSize: parseInt((args.pageSize != null) ? args.pageSize : "15"),
         username: user.name
      }
   };
   model.widgets = [widget];
}
main();