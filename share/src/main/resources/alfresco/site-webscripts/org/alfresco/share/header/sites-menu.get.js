function getSitesMenuData() {

   // Work out the mode to operate in...
   // 'all' - get both recent and favourite sites information
   // 'recent' - just get the recent sites menu item configuration
   // 'favourites' - just get the favourite sites menu item configuration
   var mode = url.templateArgs.mode;
   var getFavouriteSites = (mode == "all" || mode == "favourites"),
       getRecentSites = (mode == "all" || mode == "recent");
   
   // Get the preferences for the current user...
   var prefs = JSON.parse(preferences.value);
   var recentSites, favourites;
   if (prefs && prefs.org && prefs.org.alfresco && prefs.org.alfresco.share && prefs.org.alfresco.share.sites)
   {
      recentSites = prefs.org.alfresco.share.sites.recent;
      favourites = prefs.org.alfresco.share.sites.favourites;
   }

   // Check that recentSites and favourites have been initialised by the successful
   // response of requesting preferences. If not then just make them a new object and
   // this will be reflected in the UI as there being no recent sites or favourites.
   if (typeof recentSites != "object")
   {
      recentSites = {};
   }
   if (typeof favourites != "object")
   {
      favourites = {};
   }

   // Check to see if a site has been provided for determining whether or not a 
   // favourite can be added or removed...
   var siteId = null;
   if (url.templateArgs.site)
   {
      siteId = url.templateArgs.site;
   }
   
   var recentSitePrefsUpdate = {}; 
   if (getRecentSites)
   {
      // Make an array of the recent sites...
      var orderedRecentSites = [],
          currentSiteIndex = null;
      for (var index in recentSites)
      {
         // Add the site to the array if its mapped to a valid index...
         // The only way to use a number as a JSON object key and have it encoded successfully
         // was to add a "_" prefix. Therefore it is necessary to check that we have at least
         // 2 characters and then discard the first character (the underscore)...
         if (index.length > 1)
         {
            var numericalIndex = index.substr(1);
            if (!isNaN(numericalIndex))
            {
               orderedRecentSites[numericalIndex] = recentSites[index];
            }
         }
      }
      
      // Clean up the array...
      for (var i=0; i < orderedRecentSites.length; i++)
      {
         // Remove any gaps...
         if (orderedRecentSites[i] == undefined)
         {
            orderedRecentSites.splice(i,1);
         }
         else if (orderedRecentSites[i] == siteId)
         {
            // If the current recent site is the site being visited then it needs to go to the top of the list
            currentSiteIndex = i;
         }
      }
      
      // Trim the number of recent sites to the configured preferences...
      // 5 recent sites is the default if no configuration is available...
      var maxRecentSites = 5;
      if (config.global.header && config.global.header.maxRecentSites)
      {
         maxRecentSites = config.global.header.maxRecentSites;
      }
      while (orderedRecentSites.length > maxRecentSites)
      {
         orderedRecentSites.pop(); // Remove the last entry
         updateRequired = true;
      }
      
      // Build a map of the latest recent sites information to save as the latest preferences.
      // Even if a save isn't required this object is useful for building the Recent Site menu
      // item widget configuration...
      for (var i=0; i < orderedRecentSites.length; i++)
      {
         recentSitePrefsUpdate[orderedRecentSites[i]] = i;
      }
   }
   
   
   // Call the repository to return a list of sites with their full metadata. Both those in the recent sites
   // and favourites list are included. This information is required in order to get the display name of the
   // site (and in the case of favourites to ensure that the user is a member of that site - if not they will
   // be presented with the option to add the site as a favourite)
   var query = {
      shortName:
      {
         match: "shortname",
         values: []
      }
   };
   
   if (getRecentSites && !getFavouriteSites)
   {
      // Getting ONLY recent sites...
      query.shortName.values = orderedRecentSites;
   }
   else if (getFavouriteSites)
   {
      // Getting favourites (and possibly recent sites)...
      for (var shortName in favourites)
      {
         if (favourites[shortName] && recentSitePrefsUpdate)
         {
            query.shortName.values.push(shortName);
         }
      }
   }
   
   if (getRecentSites && getFavouriteSites) 
   {
      // Getting recent sites (having already got favourites)...
      for (var i=0; i < orderedRecentSites.length; i++)
      {
         if (favourites[orderedRecentSites[i]])
         {
            // Ignore recent a recent site if it is already a favourite
         }
         else
         {
            // Add a recent site to the query if it's not a favourite
            query.shortName.values.push(orderedRecentSites[i]);
         }
      }
   }
   
   // Create new lists to hold the favourite site and recent site menu item widget definitions...
   // These lists will be populated from the full site metadata that is requested from the 
   // repository.
   var favouriteSiteMenuItems = [],
       recentSiteMenuItems = [],
       potentialFavourite = null,
       offerSiteAsFavourite = false,
       offerUnfavourite = false;
   
   // Make the request to get the site metadata...
   var connector = remote.connect("alfresco");
   var result = connector.post("/api/sites/query", jsonUtils.toJSONString(query), "application/json");
   if (result.status == 200)
   {
      // Create JavaScript objects from the server response
      var sites = JSON.parse(result), site;
      if (sites.length != 0)
      {
         // Iterate over the list and construct the favourites list...
         for (var i=0; i<sites.length; i++)
         {
            // Store the current shortName in a local variable as its used repeatedly...
            var currShortName = sites[i].shortName;
            
            // If the current site is a favourite site AND the current user is still a member
            // of that site, then construct a menu item for it (there is a precedent set that
            // you can't have a favourite site that you're not a member of)...
            if (favourites[currShortName])
            {
               if (getFavouriteSites)
               {
                  favouriteSiteMenuItems.push({
                     id: "ALF_FAVOURITE_SITE___" + currShortName,
                     name: "alfresco/header/AlfMenuItem",
                     config:
                     {
                        id: "HEADER_SITES_MENU_FAVOURITE_" + currShortName,
                        label: sites[i].title,
                        iconClass: "alf-favourite-site-icon",
                        targetUrl: "site/" + currShortName,
                        siteShortName: currShortName,
                        siteRole: sites[i].siteRole
                     }
                  });
               }
               
               // If the current site is a favourite then offer the option to remove it from the favourites list...
               if (currShortName == siteId)
               {
                  offerUnfavourite = true;
               }
            }
            else if (currShortName == siteId)
            {
               // If the current site is NOT a favourite, but the user is a member then we should offer
               // the opporunity for the user to make the current site a favourite.
               offerSiteAsFavourite = true;
            }
            
            // If the current site is a recent site, construct a menu item for it...
            if (getRecentSites && recentSitePrefsUpdate[currShortName] != null)
            {
               recentSiteMenuItems[recentSitePrefsUpdate[currShortName]] = {
                  id: "ALF_RECENT_SITE___" + currShortName,
                  name: "alfresco/header/AlfMenuItem",
                  config: {
                     id: "HEADER_SITES_MENU_RECENT_" + currShortName,
                     label: sites[i].title,
                     iconClass: "alf-recent-site-icon",
                     targetUrl: "site/" + currShortName,
                     siteShortName: currShortName,
                     siteRole: sites[i].siteRole
                  }
               };
            }
         }
      }
   }
   
   // Build the full sites menu that includes the Recent and Favourite site menu items...
   var sitesMenu = {
      showAddFavourite: offerSiteAsFavourite,
      showRemoveFavourite: offerUnfavourite
   };
   
   if (getRecentSites)
   {
      // clean up the recent sites menu by removing gaps
      for (var i=0; i<recentSiteMenuItems.length; i++)
      {
         if (recentSiteMenuItems[i] === undefined)
         {
            recentSiteMenuItems.splice(i--,1);
         }
      }
      sitesMenu.widgetsRecent = recentSiteMenuItems;
   }  
   if (getFavouriteSites)
   {
      sitesMenu.widgetsFavourites = favouriteSiteMenuItems;
   }
   return sitesMenu;
}

model.sitesMenu = getSitesMenuData();
