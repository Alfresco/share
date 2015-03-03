
function main()
{
   // retrieve comments
   var params = [];
   for (var name in args)
   {
      params.push(name + "=" + args[name]);
   }
   var uri = "/api/node/" + url.templateArgs.store_type + "/" + url.templateArgs.store_id +"/" + url.templateArgs.id + "/comments?" + params.join("&");
   var connector = remote.connect("alfresco");
   var result = connector.get(encodeURI(uri));
   if (result.status.code == status.STATUS_OK)
   {
      // Strip out possible malicious code
      var comments = JSON.parse(result.response);
      if (comments && comments.items) {
         for (var i = 0, il = comments.items.length; i < il; i++)
         {
            comments.items[i].content = stringUtils.stripUnsafeHTML(comments.items[i].content);
         }
      }
      return jsonUtils.toJSONString(comments);
   }
   else
   {
      status.code = result.status.code;
      status.message = msg.get("message.failure");
      status.redirect = true;
   }
}

model.comments = main();