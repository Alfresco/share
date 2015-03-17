<import resource="classpath:alfresco/site-webscripts/org/alfresco/utils/feed.utils.js">

/**
 * Main entry point for the webscript
 */
function main()
{
   var uri = args["feed-url"];
   if (!uri || uri == "default")
   {
      // Use the default
      var conf = new XML(config.script);
      uri = getValidRSSUri(conf.feed[0].toString());
   }
   else
   {
      var protocol = url.templateArgs.protocol
      uri = protocol + "://" + uri;
   }
   
   var feed = rssFromString(requestbody.content);
   if (logger.isLoggingEnabled())
   {
      logger.log("Received RSS content: " + requestbody.content);
   }
   
   model.uri = uri;
   model.limit = url.templateArgs.limit;
   model.target = url.templateArgs.target;
   if (feed.error)
   {
      model.title = msg.get("title.error." + feed.error);
      model.error = true;
      model.items = [];
   }
   else
   {
      model.title = feed.title;
      model.items = feed.items;
   }
}

main();
