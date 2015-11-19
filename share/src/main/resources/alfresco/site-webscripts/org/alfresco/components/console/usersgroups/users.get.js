function main() {
   
   var search = null,
       searchMain = config.scoped["Search"];
   if (searchMain != null)
   {
      search = searchMain["search"];
   }
   var users = null,
       usersMain = config.scoped["Users"];
   if (usersMain != null)
   {
      users = usersMain["users"];
   }
   
   var minSearchTermLength = (args.minSearchTermLength != null) ? args.minSearchTermLength : search.getChildValue("min-search-term-length"),
       maxSearchResults  = (args.maxSearchResults != null) ? args.maxSearchResults : search.getChildValue("max-users-search-results"),
       minUsernameLength = users.getChildValue('username-min-length'),
       minPasswordLength = users.getChildValue('password-min-length'),
       showAuthorizationStatus = users.getChildValue('show-authorization-status');
   
   // Widget instantiation metadata...
   var widget = {
      id : "ConsoleUsers", 
      name : "Alfresco.ConsoleUsers",
      options : {
         minSearchTermLength: parseInt(minSearchTermLength),
         maxSearchResults: parseInt(maxSearchResults),
         minUsernameLength: parseInt(minUsernameLength),
         minPasswordLength: parseInt(minPasswordLength),
         showAuthorizationStatus: showAuthorizationStatus == 'true' ? true : false,
         docsEdition: context.properties["docsEdition"].getValue()
      }
   };
   model.widgets = [widget];
}
main();