function main()
{
   // A default blog description
   var defaultBlog = 
   {
      permissions:
      {
         create: false,
         edit: false
      }
   };

   // Call the repo to get the permissions for the user for this blog
   var result = remote.call("/api/blog/site/" + page.url.templateArgs.site + "/" + (template.properties.container ? template.properties.container : "blog"));
   var obj = JSON.parse(result);
   if (result.status == 200)
   {
      // Prepare the model for the template
      model.blog = obj.item;
   }
   else
   {
      model.blog = defaultBlog;
   }
   
   // Widget instantiation metadata...
   
   var blogToolbar = {
      id : "BlogToolbar",
      name : "Alfresco.BlogToolbar",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         containerId : (template.properties.container != null) ? template.properties.continer : "blog",
         allowCreate : model.blog.permissions.create,
         allowConfigure : model.blog.permissions.edit
      }
   };
   model.widgets = [blogToolbar];
}

main();