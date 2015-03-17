function main()
{
   var shareId = args.shareId,
      result = remote.connect("alfresco-noauth").get("/api/internal/shared/node/" + encodeURIComponent(shareId) + "/metadata"),
      title;

   if (result.status == 200)
   {
      var nodeMetadata = JSON.parse(result);

      // Display name
      model.title = nodeMetadata.name;
   }
   else
   {
      model.title = msg.get("message.not-found");
   }
}

main();
