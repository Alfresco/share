function main()
{
   var siteId;
   if (typeof page != "undefined")
   {
      if (page.url.templateArgs.site != null)
      {
         siteId = page.url.templateArgs.site;
      }
      else
      {
         siteId = "";
      }
   }
   else if (args.site != null)
   {
      siteId = args.site;
   }
   else
   {
      siteId = "";
   }
   
   // Widget instantiation metadata...
   var authorityFinder = {
      id : "AuthorityFinder", 
      name : "Alfresco.AuthorityFinder",
      options : {
         siteId : siteId,
         minSearchTermLength : parseInt((args.minSearchTermLength != null) ? args.minSearchTermLength : "3"),
         maxSearchResults : parseInt((args.maxSearchResults != null) ? args.maxSearchResults : "100"),
         setFocus : (args.setFocus == "true"),
         addButtonSuffix : (args.addButtonSuffix != null) ? args.addButtonSuffix : "",
         dataWebScript : { _alfValue : "dataWebScript", _alfType: "REFERENCE"},
         viewMode : { _alfValue : "Alfresco.AuthorityFinder.VIEW_MODE_DEFAULT", _alfType: "REFERENCE"},
         authorityType : { _alfValue : "Alfresco.AuthorityFinder.AUTHORITY_TYPE_ALL", _alfType: "REFERENCE"}
      }
   };
   model.widgets = [authorityFinder];
}

main();
