function getDocumentDetailsUrl(shareId)
{
   var result = remote.connect("alfresco").get("/api/internal/shared/share/" + encodeURIComponent(page.url.args.id));
   if (result.status == 200)
   {
      var info = JSON.parse(result);
      return url.context + "/page" + (info.siteId ? "/site/" + encodeURIComponent(info.siteId) : "") + '/document-details?nodeRef=' + encodeURIComponent(info.nodeRef)
   }
   else
   {
      // In the unlikely case it was not found just go to the dashboard
      return url.context;
   }
}

function main()
{
   model.redirectUrl = getDocumentDetailsUrl(page.url.args.id);
}

main();
