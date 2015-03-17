function main()
{
   // Make the shareid available in token resolution when the components are bound into the page
   // I.e. <param>{shareid}</param>
   var shareId = page.url.args.id;
   context.attributes.shareid = shareId;

   // Check of the shareId exists
   var result = remote.connect("alfresco-noauth").get("/api/internal/shared/node/" + encodeURIComponent(shareId) + "/metadata");
   model.outcome = result.status == 200 ? "" : "error";
}

main();
