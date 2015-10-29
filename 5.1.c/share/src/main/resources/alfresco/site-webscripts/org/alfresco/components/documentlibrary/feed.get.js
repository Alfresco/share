function main()
{
   // gather all required data
   var cname = (args.loopback != null && args.loopback == "1") ? "alfresco" : "alfresco-feed",
      feedArgs = url.templateArgs["feedArgs"],
      connector = remote.connect(cname);

   var remoteUrl = "/slingshot/doclib/doclist/" + encodeURI(feedArgs),
      remoteArgs = "";
   for (var arg in args)
   {
      if (arg != "format")
      {
         remoteArgs += "&" + arg + "=" + encodeURIComponent(args[arg]);
         if (arg == "filter")
         {
            model.filter = args[arg];
         }
         if (arg == "filterData")
         {
            model.filterData = args[arg];
         }
   }
   }
   if (remoteArgs != "")
   {
      remoteArgs = "?" + remoteArgs.substring(1);
      remoteUrl += remoteArgs;
   }

   var result = connector.get(remoteUrl);
   if (result.status != status.STATUS_OK)
   {
      status.setCode(status.STATUS_INTERNAL_SERVER_ERROR, "Unable to call doclist data webscript. " +
                     "Status: " + result.status + ", response: " + result.response);
      return null;
   }
   
   var data = JSON.parse(result.response);
   model.items = data.items;
}

main();