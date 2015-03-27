function main()
{
   // A default blog description
   var defaultForum =
   {
      forumPermissions:
      {
         create: false
      }
   };

   // Call the repo to get the permissions for the user for this blog
   var result = remote.call("/api/forum/site/" + page.url.templateArgs.site + "/" + (template.properties.container ? template.properties.container : "discussions") + "/posts?startIndex=0&pageSize=0");
   // Create javascript objects from the server response
   var obj = JSON.parse(result);
   if (result.status == 200)
   {
      forum = obj;
   }
   else
   {
      forum = defaultForum;
   }

   // Prepare the model for the template
   model.forum = forum;
   
   // Widget instantiation metadata...
   var toolbar = {
      id : "DiscussionsToolbar", 
      name : "Alfresco.DiscussionsToolbar",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         containerId : (page.url.args.containerId != null) ? page.url.args.containerId : "discussions",
         allowCreate : model.forum.forumPermissions.create
      }
   };
   model.widgets = [toolbar];
}

main();

