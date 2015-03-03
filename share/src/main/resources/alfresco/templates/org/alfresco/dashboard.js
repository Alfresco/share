<import resource="classpath:alfresco/templates/org/alfresco/valid-user-site-access.lib.js">

/**
 * Dashboard template controller script
 */

function main()
{
   // 1 - ""
   // 2 - "yui-g" "yui-gc" "yui-gd" "yui-ge" "yui-gf"
   // 3 - "yui-gb"
   // 4 - "yui-g"
   // 5 - "yui-gb" // note, will leave an empty column to the right
   // 6 - "yui-gb"
   
   var columns = [];
   model.gridClass = template.properties.gridClass;
   if (isValidUserOrSite())
   {
      for (var i = 0; true; i++)
      {
         var noOfComponents = template.properties["gridColumn" + (i + 1)];
         if (noOfComponents)
         {
            columns[i] =
            {
               components: parseInt(noOfComponents)
            };
         }
         else
         {
            break;
         }
      }
   }
   else
   {
      var valid = true;
      if (!user.isAdmin)
      {
         if (page.url.templateArgs.userid != null)
         {
            // User Dashboard - user must be same user as per page view id
            valid = (user.name.toLowerCase() == page.url.templateArgs.userid.toLowerCase());
         }
         else if (page.url.templateArgs.site != null)
         {
            valid = false;
            
            // Site Dashboard - cannot view/enter private site pages
            var json = remote.call("/api/sites/" + page.url.templateArgs.site);
            if (json.status != 200)
            {
               // If the user does not have access to the site or it is not a valid site then we're going to intentionally
               // throw and error that will force Surf to render the standard error page. This will result in an exception
               // logged in the server logs.
               throw new Error("A user attempted to access a private site that they do not have access to");
            }
         }
      }
   }
   model.gridColumns = columns;
}

main();