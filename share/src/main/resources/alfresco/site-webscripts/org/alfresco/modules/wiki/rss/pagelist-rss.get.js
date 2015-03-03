function convertPageJSONData(page)
{
   // created
   var created = new Date(page["createdOn"])
   page["createdOn"] = created;

   // modified
   var modified = new Date(page["modifiedOn"]);
   page["modifiedOn"] = modified;
}

function convertPagesJSONData(data)
{
   for (var x=0; x < data.pages.length; x++)
   {
       convertPageJSONData(data.pages[x]);
   }
}

function main()
{
   var site, theUrl, cname, connector, result, data;

   // gather all required data
   site = args["site"];

   theUrl = '/slingshot/wiki/pages/' + site + '?format=json';

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
   convertPagesJSONData(data);
   model.pages = data.pages;
   // set additional properties
   // PENDING: where to get this information?
   //model.lang = "en-us";
   model.site = site;
   model.lang = "en-us";
}

main();