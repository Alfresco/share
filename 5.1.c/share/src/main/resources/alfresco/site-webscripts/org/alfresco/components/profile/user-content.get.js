/**
 * User Profile Component - User Content list GET method
 */

function main()
{
   model.addedContent = [];
   model.modifiedContent = [];
   
   // read config - use default values if not found
   var maxItems = 3,
       conf = new XML(config.script);
   if (conf["max-items"] != null)
   {
      maxItems = parseInt(conf["max-items"]);
   }
   
   var userId = page.url.templateArgs["userid"];
   if (userId == null)
   {
      userId = user.name;
   }
   var result = remote.call("/slingshot/profile/usercontents?user=" + encodeURIComponent(userId) + "&maxResults=" + maxItems);
   if (result.status == 200)
   {
      // Create javascript objects from the server response
      var data = JSON.parse(result);
      
      ['created','modified'].forEach(function(type)
      {
         var store = (type === 'created') ? model.addedContent : model.modifiedContent,
            contents = data[type].items,
            dateType = type + 'On',
            item;
         
         for (var i = 0, len = contents.length; i < len; i++)
         {
            item = contents[i];
            if (store.length < maxItems)
            {
               if (!item.browseUrl)
               {
                  switch (item.type)
                  {
                     case "document":
                        item.browseUrl = "document-details?nodeRef=" + item.nodeRef;
                        break;
                     case "blogpost":
                        item.browseUrl = "blog-postview?postId=" + item.name;
                        break;
                     case "wikipage":
                        item.browseUrl = "wiki-page?title=" + item.name;
                        break;
                     case "forumpost":
                        item.browseUrl = "discussions-topicview?topicId=" + item.name;
                        break;
                  }
               }
               store.push(item);
            }
         }
         
         model[type === 'created' ? "addedContent" : "modifiedContent"] = store;
      });
   }
   
   model.numAddedContent = model.addedContent.length;
   model.numModifiedContent = model.modifiedContent.length;
}

main();