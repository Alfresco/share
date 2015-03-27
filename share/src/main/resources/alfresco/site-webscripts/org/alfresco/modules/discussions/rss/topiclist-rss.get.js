
function convertTopicJSONData(topic)
{
    // created
    var created = new Date(topic["createdOn"]);
    if (isNaN(created) == false)
    {
        topic["createdOn"] = created;
    }
    
    // last reply
    if (topic["lastReplyOn"] != undefined)
    {
        var last = new Date(topic["lastReplyOn"]);
        if (isNaN(last) == false)
        {
	    topic["lastReplyOn"] = last;
        }
    }
}

/**
 * Converts the data object from strings to the proper types
 * (currently this only handles strings
 */
function convertTopicsJSONData(data)
{
    for (var x=0; x < data.items.length; x++)
    {
        convertTopicJSONData(data.items[x]);
    }
}

function main()
{
   var site, container, theUrl, cname, connector, result, data;
   
   // gather all required data
   site = args["site"];
   container = (args["container"] != undefined) ? args["container"] : "discussions";
   
   theUrl = '/api/forum/site/' + site + '/' + container + "/posts?contentLength=512";
   
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
   convertTopicsJSONData(data);
   model.items = data.items;

   // set additional properties
   // PENDING: where to get this information?
   model.lang = "en-us";
   model.site = site;
   model.container = container;
}

main();