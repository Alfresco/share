/**
 * Sites module GET method
 */

const PREF_FAVOURITE_SITES = "org.alfresco.share.sites.favourites";

function sortByTitle(site1, site2)
{
   return (site1.title > site2.title) ? 1 : (site1.title < site2.title) ? -1 : 0;
}

function main()
{
   // Model variables - must have defaults
   var favouriteSites = [],
       currentSiteIsFav = false,
       siteTitle = "";

   var prefs,
       favourites;

   // can optionally pass JSON list of fav sites if they are already known to the caller
   if (args.favsites)
   {
      try
      {
         // Parse JSON using Java to a native JavaScript object
         favourites = jsonUtils.toObject(args.favsites);
      }
      catch(e)
      {
         favourites = {};
      }
   }
   else
   {
      // Process the user's favourite sites
      // TODO: Clean-up old favourites here?
      prefs = JSON.parse(preferences.value);

      // Populate the favourites object literal for easy look-up later
      favourites = eval('try{(prefs.' + PREF_FAVOURITE_SITES + ')}catch(e){}');

      if (typeof favourites != "object")
      {
         favourites = {};
      }
   }

   // Call the repo to return a specific list of site metadata i.e. those in the fav list
   // and ensure the current user is a member of each before adding to fav list
   var query =
   {
      shortName:
      {
         match: "exact-membership",
         values: []
      }
   },
   currentSite = args.siteId || "",
   ignoreCurrentSite = false,
   shortName;
   
   for (shortName in favourites)
   {
      if (favourites[shortName])
      {
         query.shortName.values.push(shortName);
      }
   }
   
   // Also tack the current site onto the query, so we can pass the Site Title to header.js
   if (currentSite !== "" && !favourites[currentSite])
   {
      query.shortName.values.push(currentSite);
      ignoreCurrentSite = true;
   }
   
   var connector = remote.connect("alfresco");
   result = connector.post("/api/sites/query", jsonUtils.toJSONString(query), "application/json");
   
   if (result.status == 200)
   {
      var i, ii;
      
      // Create javascript objects from the server response
      // Each item is a favourite site that the user is a member of
      var sites = JSON.parse(result), site;
      
      if (sites.length != 0)
      {
         // Sort the sites by title
         sites.sort(sortByTitle);
         
         for (i = 0, ii = sites.length; i < ii; i++)
         {
            site = sites[i];
            if (site.shortName == currentSite)
            {
               siteTitle = site.title;
               if (ignoreCurrentSite)
               {
                  // The current site was piggy-backing the query call; it's not a favourite
                  continue;
               }
               currentSiteIsFav = true;
            }
            favouriteSites.push(site);
         }
      }
   }

   // Prepare models
   model.siteTitle = siteTitle;

   // Helpers and legacy model values
   model.siteActive = siteTitle.length > 0;
   model.currentSiteIsFav = currentSiteIsFav;
   model.favouriteSites = favouriteSites;

   // Menu items
   model.showFavourites = favouriteSites.length > 0;
   model.showAddFavourites = siteTitle.length > 0 && !currentSiteIsFav;
   model.showFindSites = true;
   model.showCreateSite = true;
}

main();