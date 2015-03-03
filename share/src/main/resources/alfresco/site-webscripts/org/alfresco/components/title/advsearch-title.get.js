/**
 * Advanced Search Title component GET method
 */

function main()
{
   if (page.url.templateArgs.site != null)
   {
      // Call the repository for the site profile
      var siteTitle = null;
      var json = remote.call("/api/sites/" + page.url.templateArgs.site);
      if (json.status == 200)
      {
         // Create javascript objects from the repo response
         var obj = JSON.parse(json);
         if (obj)
         {
            siteTitle = (obj.title.length != 0) ? obj.title : obj.shortName;
         }
      }
      
      // Prepare the model
      model.siteTitle = (siteTitle != null ? siteTitle : "");
   }
   
   // Build search results back link from supplied args
   var args = page.url.args;
   if (args["st"] != null || args["stag"] != null)
   {
      var query = "t=" + (args["st"] != null ? encodeURIComponent(args["st"]) : "") +
                  "&tag=" + (args["stag"] != null ? encodeURIComponent(args["stag"]) : "") +
                  "&s=" + (args["ss"] != null ? encodeURIComponent(args["ss"]) : "") +
                  "&a=" + (args["sa"] != null ? encodeURIComponent(args["sa"]) : "") +
                  "&r=" + (args["sr"] != null ? encodeURIComponent(args["sr"]) : "") +
                  "&q=" + (args["sq"] != null ? encodeURIComponent(args["sq"]) : "");
      model.backlink = query;
   }
}

main();