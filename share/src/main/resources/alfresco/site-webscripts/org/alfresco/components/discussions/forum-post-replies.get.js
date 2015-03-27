
function main()
{
   // retrieve forum posts using a filter
   var params = [];
   for (var name in args)
   {
      params.push(name + "=" + args[name]);
   }
   var uri = "/api/forum/post/site/" + url.templateArgs.site + "/" + url.templateArgs.container + "/" + url.templateArgs.path +"/replies?" + params.join("&");
   var connector = remote.connect("alfresco");
   var result = connector.get(uri);
   if (result.status.code == status.STATUS_OK)
   {
      // Strip out possible malicious code
      var replies = JSON.parse(result.response);
      if (replies && replies.items) {
         for (var i = 0, il = replies.items.length; i < il; i++)
         {
            replies.items[i].content = stringUtils.stripUnsafeHTML(replies.items[i].content);
         }
      }
      return jsonUtils.toJSONString(replies);
   }
   else
   {
      status.code = result.status.code;
      status.message = msg.get("message.failure");
      status.redirect = true;
   }
}

model.replies = main();