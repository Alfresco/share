/**
 * User Profile Component - User Sites list GET method
 */

function main()
{
   // read config - use default values if not found
   var maxItems = 100,
       conf = new XML(config.script);
   if (conf["max-items"] != null)
   {
      maxItems = parseInt(conf["max-items"]);
   }

   // Call the repo for sites the user is a member of
   var userId = page.url.templateArgs["userid"];
   if (userId == null)
   {
      userId = user.name;
   }
   model.userid = userId;
   var result = remote.call("/api/people/" + encodeURIComponent(userId) + "/sites?size=" + maxItems);
   model.sites = [];
   model.feedControls = [];
   if (result.status == 200)
   {
      // Create javascript objects from the server response
      model.sites = JSON.parse(result);
      
      result = remote.call("/api/activities/feed/controls");
      if (result.status == 200)
      {
         var feedControls = JSON.parse(result);
         for(var i = 0; i < feedControls.length; i++)
         {
            model.feedControls.push(feedControls[i].siteId);
         }
      }
   }
   
   model.numSites = model.sites.length;
   // Widget instantiation metadata...
   
   var userSite = {
      id : "UserSites", 
      name : "Alfresco.UserSites"
   };
   model.widgets = [userSite];
}

main();