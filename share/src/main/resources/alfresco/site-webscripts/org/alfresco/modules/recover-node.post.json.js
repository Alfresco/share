function main()
{
   // parse the input json into an object - to retrieve parameters
   var reqJSON = JSON.parse(json.toString());
   
   // Call the repo to recover the node from the archive store
   var conn = remote.connect("alfresco"),
       res = conn.put("/api/archive/" + reqJSON.nodeRef, "", "application/json"),
       response = JSON.parse(res);
   
   if (res.status.code == 200)
   {
      if (reqJSON.nodeType == "st:site")
      {
         // collect up objects needed
         var dashboardURL = "site/" + reqJSON.name + "/dashboard",
             dashboardPage = sitedata.getPage(dashboardURL);
         
         // remove dashboard page instance from view cache
         if (dashboardPage != null)
         {
            viewResolverUtils.removeFromCache(dashboardURL);
         }
      }
      
      model.success = true;
   }
   else
   {
      // Error occured - report back to client with the status and message
      status.setCode(response.status.code, response.message);
      model.success = false;
   }
}

main();