function filterSites(p_arrSites, p_pageType)
{
   var arrFiltered = [],
      pageType = '"' + p_pageType + '"';

   for (var i = 0, ii = p_arrSites.length; i < ii; i++)
   {
      try
      {
         if (sitedata.getPage("site/" + p_arrSites[i].shortName + "/dashboard").properties.sitePages.indexOf(pageType) !== -1)
         {
            arrFiltered.push(p_arrSites[i]);
         }
      }
      catch (e)
      {
      }
   }
   return arrFiltered;
}

function main()
{
   var result, mySites = [], objMySites = {}, publicSites = [], otherSites = [], i, ii;

   // Call the repo for sites the user is a member of
   result = remote.call("/api/people/" + encodeURIComponent(user.name) + "/sites?size=100");
   if (result.status == 200)
   {
      // Create javascript objects from the server response
      mySites = JSON.parse(result);
      for (i = 0, ii = mySites.length; i < ii; i++)
      {
         objMySites[mySites[i].shortName] = true;
      }
      // Filter out sites without a documentlibrary page
      mySites = filterSites(mySites, "documentlibrary");
   }
   
   // Call the repo for all sites the user has access to
   result = remote.call("/api/sites?size=100");
   if (result.status == 200)
   {
      // Create javascript objects from the server response
      publicSites = JSON.parse(result);
      for (i = 0, ii = publicSites.length; i < ii; i++)
      {
         if (!objMySites.hasOwnProperty(publicSites[i].shortName))
         {
            otherSites.push(publicSites[i]);
         }
      }
      // Filter out sites without a documentlibrary page
      otherSites = filterSites(otherSites, "documentlibrary");
   }

   model.mySites = mySites;
   model.otherSites = otherSites;
}

main();