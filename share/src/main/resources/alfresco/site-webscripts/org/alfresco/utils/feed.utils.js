/**
 * Controls max items on display at any one time in the RSS dashlet.
 */
const DISPLAY_ITEMS = 100;

/**
 * Function to return a URI with a valid http protocol prefix if it does not already have one
 */
function getValidRSSUri(uri)
{
   var re = /^(http|https):\/\//;
   if (!re.test(uri))
   {
      uri = "http://" + uri;
   }
   return uri;
}

/**
 * Takes a URL of an RSS feed and returns an rss object
 * with title and an array of items in the feed.
 *
 * @param uri {String} the uri of the RSS feed (previously passed through getValidRSSUri())
 */
function getRSSFeed(uri, limit)
{
   limit = limit || null;
   
   // We only handle "http" connections for the time being
   var connector = remote.connect("http");
   var result = connector.call(uri);

   if (result !== null && result.status == 200)
   {
      return rssFromString(new String(result), limit);
   }
   else
   {
      return {
         error: "unavailable"
      };
   }
}

/**
 * Parse feed items from an XML string
 *
 * @param rssXml {String} XML markup to be parsed
 * @param limit {int} Maximum number of feed entries to return, if not specified defaults to DISPLAY_ITEMS global
 * @return {object} Object containing the feed entries or exception details in the event of an error
 */
function rssFromString(rssXml, limit)
{
   limit = limit || DISPLAY_ITEMS;
   
   var rss;

   // Prepare string for E4X
   rssXml = prepareForE4X(rssXml);

   // Find out what type of feed
   try
   {
      rss = new XML(rssXml);
      if (rss.name().localName.toLowerCase() == "rss")
      {
          return parseRssFeed(rss, rssXml, limit);
      }
      else if(rss.name().localName.toLowerCase() == "feed")
      {
          return parseAtomFeed(rss, rssXml, limit);
      }
      else
      {
         return {
            error: "unsupported"
         };
      }
   }
   catch (e)
   {
      logger.log(e);
      return {
         error: "bad_data"
      };
   }
}

/**
 * Takes am xml string and prepares it for E4X.
 *
 * Removes all comments and instructions and trims the string
 *
 * @param xmlStr {string} An string representing an xml document
 * @return {string} An E4X compatible string
 */
function prepareForE4X(xmlStr)
{
   // NOTE: we use the Java regex features here - as the Rhino impl of regex is x100's slower!!
   // NOTE: In Java RegExps the . (dot) does NOT include \r or \n characters by default,
   // to turn that on we use the (?s) instruction at the beginning of the regexp.
   var str = new java.lang.String(xmlStr);
   return new String(str.replaceAll("(?s)(<\\?.*?\\?>)|(<!--.*?-->)", "").replaceAll("^[^<]*", "").replaceAll("[^>]*$", ""));
}

/**
 * Takes a rss feed string and returns feed object
 *
 * @param rss {XML} represents an Rss feed
 * @param rssStr {String} represents an Rss feed
 * @param limit {int} The maximum number of items to display
 * @return {object} A feed object with title and items with malicious html code removed
 */
function parseRssFeed(rss, rssStr, limit)
{
   /**
    * We do this (dynamically) as some feeds, e.g. the BBC, leave the trailing slash
    * off the end of the Yahoo Media namespace! Technically this is wrong but what to do.
    */
   var mediaRe = /xmlns\:media="([^"]+)"/;
   var hasMediaExtension = mediaRe.test(rssStr);
   
   if (hasMediaExtension)
   {
      var result = mediaRe.exec(rssStr);
      // The default (correct) namespace should be 'http://search.yahoo.com/mrss/'
      var media = new Namespace( result[1] );
      var fileext = /([^\/]+)$/;
   }
   
   var items = [],
      item,
      obj,
      count = 0;
   for each (item in rss.channel..item)
   {
      if (count >= limit)
      {
         break;
      }
      
      obj =
      {
         "title": stringUtils.stripUnsafeHTML(item.title.toString()),
         "description": stringUtils.stripUnsafeHTML(item.description.toString() || ""),
         "link": stringUtils.stripUnsafeHTML(item.link.toString())
      };
      
      if (hasMediaExtension)
      {
         // We only look for the thumbnail as a direct child in RSS
         var thumbnail = item.media::thumbnail;
         if (thumbnail && thumbnail.@url.length() > 0)
         {
            obj["image"] = stringUtils.stripUnsafeHTML(thumbnail.@url[0].toString());
         }

         var attachment = item.media::content;
         if (attachment)
         {
            var contenturl = attachment.@url.toString();
            if (contenturl.length > 0)
            {
               var filename = fileext.exec(contenturl)[0];
               // Use the file extension to figure out what type it is for now
               var ext = filename.split(".");

               obj["attachment"] =
               {
                  "url": stringUtils.stripUnsafeHTML(contenturl),
                  "name": stringUtils.stripUnsafeHTML(filename),
                  "type": stringUtils.stripUnsafeHTML((ext[1] ? ext[1] : "_default"))
               };
            }
         }
      }
           
      items.push(obj);
      ++count;
   }

   return {
      title: stringUtils.stripUnsafeHTML(rss.channel.title.toString()),
      items: items
   };
}

/**
 * Takes an atom feed and returns an array of entries.
 *
 * @param atom {XML} represents an Atom feed
 * @param atomStr {String} represents an Atom feed
 * @param limit {int} The maximum number of items to display
 * @return {object} A feed object with title and items with malicious html code removed
 */
function parseAtomFeed(atom, atomStr, limit)
{
   // Recreate the xml with default namespace
   default xml namespace = new Namespace("http://www.w3.org/2005/Atom");
   atom = new XML(atomStr);

   // Do we have the media extensions such as thumbnails?
   var mediaRe = /xmlns\:media="([^"]+)"/;
   var hasMediaExtension = mediaRe.test(atomStr);
   if(hasMediaExtension)
   {
      var media = new Namespace("http://search.yahoo.com/mrss/");
   }

   var items = [],
      entry,
      link,
      obj,
      count = 0;
   for each (entry in atom.entry)
   {
      if (count >= limit)
      {
         break;
      }

      obj = {
         "title": stringUtils.stripUnsafeHTML(entry.title.toString()),
         "description": stringUtils.stripUnsafeHTML(entry.summary.toString().replace(/(target=)/g, "rel=")),
         "link": entry.link[0] ? stringUtils.stripUnsafeHTML(entry.link[0].@href.toString()) : null
      };
     
      if (hasMediaExtension)
      {
         // In Atom, it could be a direct child
         var thumbnail = entry.media::thumbnail;
         if (thumbnail && thumbnail.@url.length() > 0)
         {
            obj["image"] = stringUtils.stripUnsafeHTML(thumbnail.@url[0].toString());
         }
         else 
         {
            // If not, it could be attached to one of the link tags,
            //  typically a <link rel="alternate">
            var found = false;
            for each (link in entry.link)
            {
               var rel = link.@rel.toString();
               if(! found && (!rel || rel == "alternate"))
               {
                  // Thumbnail can be on the link, or inside a media:content
                  thumbnail = link.media::thumbnail;
                  if (!thumbnail || !thumbnail.@url.toString())
                  {
                     thumbnail = link.media::content.media::thumbnail[0];
                  }

                  if (thumbnail && thumbnail.@url.toString())
                  {
                     found = true;
                     obj["image"] = stringUtils.stripUnsafeHTML(thumbnail.@url.toString());
                  }
               }
            }
         }
      }

      items.push(obj);

      ++count;
   }
   
   return {
      title: stringUtils.stripUnsafeHTML(atom.title.toString()),
      items: items
   };
}
