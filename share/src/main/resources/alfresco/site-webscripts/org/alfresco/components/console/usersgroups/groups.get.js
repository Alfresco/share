function main() {
   // Widget instantiation metadata...
   var search = null,
       searchMain = config.scoped["Search"];
   if (searchMain != null)
   {
     search = searchMain["search"];
   }
   var groups = null,
       console = config.scoped["Console"];
   if (console != null)
   {
      groups = console["groups"];
   }
   
   var minSearchTermLength = (args.minSearchTermLength != null) ? args.minSearchTermLength : search.getChildValue("min-search-term-length"),
       maxSearchResults  = (args.maxSearchResults != null) ? args.maxSearchResults : search.getChildValue("max-search-results"),
       maxPageSize = (args.maxPageSize != null) ? args.maxPageSize : groups.getChildValue('max-page-size');

   var widget = {
      id : "ConsoleGroups", 
      name : "Alfresco.ConsoleGroups",
      options : {
         minSearchTermLength: parseInt(minSearchTermLength),
         maxSearchResults: parseInt(maxSearchResults),
         maxPageSize: parseInt(maxPageSize)
      }
   };
   model.widgets = [widget];
}
main();