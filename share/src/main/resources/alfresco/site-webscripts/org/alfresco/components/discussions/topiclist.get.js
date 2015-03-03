function main()
{
   // Widget instantiation metadata...
   var initialFilter = 
   {
      filterId: (page.url.args.filterId != null) ? page.url.args.filterId : "new",
      filterOwner: (page.url.args.filterOwner != null) ? page.url.args.filterOwner : "Alfresco.TopicListFilter",
      filterData: (page.url.args.filterData != null) ? page.url.args.filterData : null
   }
   
   var discussionsTopicList = {
      id : "DiscussionsTopicList", 
      name : "Alfresco.DiscussionsTopicList",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         containerId : (page.url.args.containerId != null) ? page.url.args.containerId : "discussions",
         initialFilter : initialFilter
      }
   };
   model.widgets = [discussionsTopicList];
}

main();

