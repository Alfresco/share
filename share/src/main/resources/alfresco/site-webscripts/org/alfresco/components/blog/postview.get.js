function main()
{
   var result = remote.call("/isreadonly");
   var isReadOnly = (result.status == 200 && result == "true");
   var blogPostView = {
      id: "BlogPostView",
      name : "Alfresco.BlogPostView",
      options : {
         siteId : page.url.templateArgs.site,
         containerId : "blog",
         postId : page.url.args.postId,
         isReadOnly: isReadOnly
      }
   };
   model.widgets = [blogPostView];
}

main();
