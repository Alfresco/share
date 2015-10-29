/**
 * Site Members Groups component GET method
 */
function main()
{
   var siteId, theUrl, json, membership, data;
   
   siteId = page.url.templateArgs.site;
   
   // get the membership info for the current user in the current site
   theUrl = "/api/sites/" + siteId + "/memberships/" + encodeURIComponent(user.name);
   json = remote.call(theUrl);
   membership = JSON.parse(json);
   
   // add the role to the model
   model.currentUserRole = membership.role ? membership.role : "";
   
   // get the roles available in the current site
   theUrl = "/api/sites/" + siteId + "/roles";
   json = remote.call(theUrl);
   data = JSON.parse(json);
   
   // add all roles except "None"
   model.siteRoles = [];
   
   if (json.status == 200 && data.siteRoles)
   {
      for (var i = 0, j = data.siteRoles.length; i < j; i++)
      {
         if (data.siteRoles[i] != "None")
         {
            model.siteRoles.push(data.siteRoles[i]);
         }
      }
   }
   else
   {
      model.error = membership.message;
   }
   
   // Widget instantiation metadata...
   var searchConfig = config.scoped['Search']['search'],
       defaultMinSearchTermLength = searchConfig.getChildValue('min-search-term-length'),
       defaultMaxSearchResults = searchConfig.getChildValue('max-search-results');

   var siteGroups = {
      id : "SiteGroups", 
      name : "Alfresco.SiteGroups",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         currentUser : user.name,
         currentUserRole : model.currentUserRole,
         roles : model.siteRoles,
         minSearchTermLength : parseInt((args.minSearchTermLength != null) ? args.minSearchTermLength : defaultMinSearchTermLength),
         maxSearchResults : parseInt((args.maxSearchResults != null) ? args.maxSearchResults : defaultMaxSearchResults),
         setFocus : (args.setFocus == "true")
      }
   };

   if (model.error)
   {
      siteGroups.options.error = model.error;
   }

   model.widgets = [siteGroups];
}

main();


