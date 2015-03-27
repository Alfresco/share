function main()
{
   // Widget instantiation metadata...
   var searchConfig = config.scoped['Search']['search'],
       defaultMinSearchTermLength = searchConfig.getChildValue('min-search-term-length'),
       defaultMaxSearchResults = searchConfig.getChildValue('max-search-results');
   
   var sentInvites = {
      id : "SentInvites", 
      name : "Alfresco.SentInvites",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         minSearchTermLength : parseInt((args.minSearchTermLength != null) ? args.minSearchTermLength : defaultMinSearchTermLength),
         maxSearchResults : parseInt((args.maxSearchResults != null) ? args.maxSearchResults : defaultMaxSearchResults),
         setFocus: (args.setFocus == "true")
      }
   };
   model.widgets = [sentInvites];
}

main();
