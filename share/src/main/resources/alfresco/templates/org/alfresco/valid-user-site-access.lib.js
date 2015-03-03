/**
 * Access test to ensure page is appropriate for the current user.
 * 
 * - User Dashboard - user must be same user id as specified in the page view id
 * - Site Dashboard - cannot view or modify a non-public private site
 * 
 * @param siteManager   True if the Site Dashboard test must be for SiteManager status
 */
function isValidUserOrSite(siteManager)
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
         if (json.status == 200)
         {
            // Any 200 return from the call means the site was not Private or
            // we are a valid member of a Private site.
            var site = JSON.parse(json);
            if (site.visibility != "MODERATED")
            {
               // Do we want to test for SiteManager role status?
               valid = (!siteManager || isSiteManager(site));
            }
            else
            {
               // If this site is Moderated - we need to see if we are a member to view dashboards etc.
               json = remote.call("/api/sites/" + page.url.templateArgs.site + "/memberships/" + encodeURIComponent(user.name));
               if (json.status == 200)
               {
                  // Any 200 return from the call means we are a member - else 404 is returned
                  valid = (!siteManager || isSiteManager(site));
               }
            }
         }
      }
   }
   return valid;
}

function isSiteManager(site)
{
   var managers = site.siteManagers;
   for (var i = 0; i < managers.length; i++)
   {
      if (managers[i] == user.name)
      {
         return true;
      }
   }
   return false;
}