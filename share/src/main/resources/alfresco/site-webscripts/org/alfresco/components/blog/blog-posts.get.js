
function main()
{
   // retrieve blog posts using a filter
   var filter = "",
         params = [];
   if (url.templateArgs.filter)
   {
      filter = "/" + url.templateArgs.filter;
   }
   for (var name in args)
   {
      params.push(name + "=" + args[name]);
   }
   var uri = "/api/blog/site/" + url.templateArgs.site +"/" + url.templateArgs.container +"/posts" + filter + "?" + params.join("&");
   var connector = remote.connect("alfresco");
   var result = connector.get(encodeURI(uri));
   if (result.status.code == status.STATUS_OK)
   {
      // Strip out possible malicious code
      var posts = JSON.parse(result.response);
      if (posts && posts.items) {
         for (var i = 0, il = posts.items.length; i < il; i++)
         {
            posts.items[i].content = stringUtils.stripUnsafeHTML(posts.items[i].content);
         }
      }
      return jsonUtils.toJSONString(posts);
   }
   else
   {
      status.code = result.status.code;
      status.message = msg.get("message.failure");
      status.redirect = true;
   }
}

model.posts = main();