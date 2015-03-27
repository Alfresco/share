function main()
{
   var shareId = args.shareId;

   model.linkButtons = [];

   // Login link
   if (user && user.isGuest)
   {
      model.linkButtons.push({
         id: "login",
         href: url.context,
         label: msg.get("button.login"),
         cssClass: "brand-bgcolor-2"
      });
   }
   if (args.loginLink == "document-details")
   {
      var result = remote.connect("alfresco").get("/api/internal/shared/node/" + encodeURIComponent(shareId) + "/read");
      if (result.status == 200)
      {
         var nodeMetadata = JSON.parse(result);
         if (nodeMetadata.canRead == true)
         {
            model.linkButtons.push({
               id: "document-details",
               href: url.context + "/page/quickshare-redirect?id=" + args.shareId,
               label: (user && user.isGuest) ? msg.get("button.login") : msg.get("button.document-details"),
               cssClass: "brand-bgcolor-2"
            });
         }
      }
   }
}

main();
