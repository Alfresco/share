function convertPostJSONData(post)
{
   // created
   var created = new Date(post["createdOn"])
   post["createdOn"] = created;
   
   // modified
   if (post["modifiedOn"] != undefined)
   {
      var modified = new Date(post["modifiedOn"]);
      post["modifiedOn"] = created;
   }
   // released
   if (post["releasedOn"] != undefined)
   {
      post["releasedOn"] = new Date(post["releasedOn"]);
   }
   // updated
   if (post["updatedOn"] != undefined)
   {
       post["updatedOn"] = new Date(post["updatedOn"]);
   }
   // last comment
   if (post["lastCommentOn"] != undefined)
   {
      post["lastCommentOn"] = new Date(post["lastCommentOn"])
   }
}

/**
 * Converts the data object from strings to the proper types
 * (currently this only handles strings
 */
function convertPostsJSONData(data)
{
   for (var x=0; x < data.items.length; x++)
   {
      convertPostJSONData(data.items[x]);
   }
}

function main()
{
   var site, container, theUrl, cname, connector, result, data;
   
   // gather all required data
   site = args["site"];
   container = (args["container"] != undefined) ? args["container"] : "blog";
   
   theUrl = '/api/blog/site/' + site + '/' + container + "/posts?contentLength=512";
   
   cname = (args.loopback != null && args.loopback == "1") ? "alfresco" : "alfresco-feed";
   connector = remote.connect(cname);
   result = connector.get(theUrl);
   if (result.status != status.STATUS_OK)
   {
      status.setCode(status.STATUS_INTERNAL_SERVER_ERROR, "Unable to do backend call. " +
                     "status: " + result.status + ", response: " + result.response);
      return null;
   }
   data = JSON.parse(result.response);
   convertPostsJSONData(data);
   model.items = data.items;

   // set additional properties
   model.lang = "en-us";
   model.site = site;
   model.container = container;
}

main();