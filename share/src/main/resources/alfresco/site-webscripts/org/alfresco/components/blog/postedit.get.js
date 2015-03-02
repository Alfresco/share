function main()
{
   var blogPostEdit = {
      id : "BlogPostEdit",
      name : "Alfresco.BlogPostEdit",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         containerId : "blog",
         editMode : (page.url.args.postId != null),
         postId : (page.url.args.postId != null) ? page.url.args.postId : "",
         editorConfig : {
            height: 300,
            language: locale
         }
      }
   };
   model.widgets = [blogPostEdit];
}

main();
