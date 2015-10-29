/**
 * Create Site POST Component
 * 
 * Reponsible for call the /api/sites to generate the st:site folder structure then
 * creating the Surf config structure on the web and repo tier. The config creation
 * will retry if a timeout occurs - if total failure occurs to create to the config
 * then the st:site node will be deleted and error reported.
 */

function main()
{
   model.success = false;
   
   var clientRequest = json.toString();
   
   // Convert client json request to a usable js object to retrieve site preset name
   var clientJSON = JSON.parse(clientRequest);
   
   // Call the repo to create the st:site folder structure
   var conn = remote.connect("alfresco");
   var repoResponse = conn.post("/api/sites", clientRequest, "application/json");
   if (repoResponse.status == 401)
   {
      status.setCode(repoResponse.status, "error.loggedOut");
   }
   else
   {
      var repoJSON = JSON.parse(repoResponse);
      // Check if we got a positive result from create site
      if (repoJSON.shortName)
      {
         // Yes we did, now create the Surf objects in the web-tier and the associated configuration elements
         // Retry a number of times until success - remove the site on total failure
         for (var r=0; r<3 && !model.success; r++)
         {
            var tokens = [];
            tokens["siteid"] = repoJSON.shortName;
            model.success = sitedata.newPreset(clientJSON.sitePreset, tokens);
         }
         // if we get here - it was a total failure to create the site config - even after retries
         if (!model.success)
         {
            // Delete the st:site folder structure and set error handler
            conn.del("/api/sites/" + encodeURIComponent(repoJSON.shortName));
            status.setCode(status.STATUS_INTERNAL_SERVER_ERROR, "error.create");
         }
      }
      else if (repoJSON.status.code)
      {
         // Default error handler to report failure to create st:site folder
         status.setCode(repoJSON.status.code, repoJSON.message);
      }
   }
}

main();