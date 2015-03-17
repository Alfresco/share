function main()
{
   var blogPostListArchive = {
      id: "BlogPostListArchive",
      name : "Alfresco.BlogPostListArchive",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         containerId : template.properties.container != null ? template.properties.container : "documentLibrary"
      }
   };
   model.widgets = [blogPostListArchive];
}

main();
