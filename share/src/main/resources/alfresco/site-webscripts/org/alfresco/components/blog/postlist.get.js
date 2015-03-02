function main()
{
   // Set the initialFilter defaults if not supplied.
   var initialFilter = {
      filterId : (page.url.args.filterId != null) ? page.url.args.filterId : "new",
      filterOwner : (page.url.args.filterOwner != null) ? page.url.args.filterOwner : "Alfresco.BlogPostListFilter",
      filterData : page.url.args.filterData
   };

   // Allow a tag specific URL shortcut, as per MNT-12058
   if (page.url.args.tag)
   {
      initialFilter = {
         filterId: "tag",
         filterOwner: "Alfresco.TagFilter",
         filterData: page.url.args.tag
      }
   }

   var blogPostList = {
      id : "BlogPostList",
      name : "Alfresco.BlogPostList",
      options : {
         siteId :(page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         containerId : (template.properties.container != null) ? template.properties.container : "blog",
         initialFilter : initialFilter
      }
   };
   model.widgets = [blogPostList];
}

main();
