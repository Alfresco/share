function main()
{
   // parse the input json into an object - to retrieve shortName
   var reqJSON = JSON.parse(json.toString());
   
   // collect up objects to delete later - as the repo removes component
   // definitions and we cannot retrieve them after
   var dashboardURL = "site/" + reqJSON.shortName + "/dashboard",
       dashboardPage = sitedata.getPage(dashboardURL);
   
   // Call the repo to delete the site
   var conn = remote.connect("alfresco"),
       res = conn.del("/api/sites/" + reqJSON.shortName),
       resJSON = JSON.parse(res);
   
   // Check if we got a positive result
   if (resJSON.success)
   {
      // Yes we did - now remove sitestore model artifacts...
      
      // remove dashboard page instance and update view cache
      if (dashboardPage != null)
      {
         viewResolverUtils.removeFromCache(dashboardURL);
      }
      
      // the client will refresh on success
      model.success = true;
   }
   else
   {
      // Error occured - report back to client with the status and message
      status.setCode(resJSON.status.code, resJSON.message);
      model.success = false;
   }
}

main();