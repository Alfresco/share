<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-header.lib.js">

function getSiteDefaultPageUrl()
{
   var pages = getSitePages();
   var siteUrl = page.url.templateArgs.site + "/";
   // Check if the current URL has a trailing slash
   if (page.url.url.indexOf("/", page.url.url.length() - 1) !== -1)
   {
      siteUrl = "";
   }
   if (pages != null && pages.length > 0)
   {
      // The first configured page is the 'default' page
      return siteUrl + pages[0].pageUrl;
   }
   else
   {
      if (model.siteData && model.siteData.profile && model.siteData.profile.shortName == "")
      {
         // redirect to missing site's dashboard which will give us the proper error
         return siteUrl + "dashboard";
      }
   }
   return null;
}

function main()
{
    model.redirectUrl = getSiteDefaultPageUrl();
}

main();
