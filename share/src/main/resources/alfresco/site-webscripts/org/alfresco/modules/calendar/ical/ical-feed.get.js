function main()
{
   var site, cname, theUrl;

   // gather all required data
   site = args["site"];


   theUrl = '/calendar/eventList-' + site + '.ics?site=' + site + '&format=calendar';

   cname = (args.loopback != null && args.loopback == "1") ? "alfresco" : "alfresco-feed";
   connector = remote.connect(cname);
   result = connector.get(theUrl);

   if (result.status != status.STATUS_OK)
   {
       status.setCode(status.STATUS_INTERNAL_SERVER_ERROR, "Unable to do backend call. " +
           "status: " + result.status + ", response: " + result.response);
       return null;
   }
   data = result.response;
   model.data = data;
}

main();