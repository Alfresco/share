<import resource="classpath:alfresco/site-webscripts/org/alfresco/utils/feed.utils.js">

/**
 * RSS Feed configuration component POST method
 */

function main()
{
   var c = sitedata.getComponent(url.templateArgs.componentId),
       uri = getValidRSSUri(String(json.get("url")));
   
   c.properties["feedurl"] = uri;
   model.feedurl = uri;
   
   var feed = getRSSFeed(uri);
   if (feed.error)
   {
      model.title = msg.get("title.error." + feed.error);
      model.items = [];
   }
   else
   {
      model.title = feed.title;
      model.items = feed.items;
   }
   
   var target;
   if (json.isNull("new_window"))
   {
      // Doesn't seem to like setting properties as boolean so we use a string instead
      target = "_self";
   }
   else
   {
      target = "_blank";
   }
   model.target = target;
   c.properties["target"] = target;
   
   var limit = String(json.get("limit"));
   if (limit === "all")
   {
      c.properties["limit"] = null; // reset
      model.limit = 100;
   }
   else
   {
      c.properties["limit"] = limit;
      model.limit = limit;
   }
   
   c.save();
}

main();