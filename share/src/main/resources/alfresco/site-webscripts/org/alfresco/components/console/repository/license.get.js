/**
 * Admin Console License info component GET method
 */

function main()
{
   var usage = {};
   
   // retrieve license usage information
   var result = remote.call("/api/admin/usage");
   if (result.status.code == status.STATUS_OK)
   {
      usage = JSON.parse(result);
   }
   
   model.usage = usage;
}

main();