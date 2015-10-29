/**
 * Share Header component GET method
 */

const PREF_COLLAPSED_TWISTERS = "org.alfresco.share.twisters.collapsed";

/**
 * Twister Preferences
 */
function getTwisterPrefs()
{
   var collapsedTwisters = "",
      result,
      prefs;

   prefs = jsonUtils.toObject(preferences.value);
   collapsedTwisters = eval('try{(prefs.' + PREF_COLLAPSED_TWISTERS + ')}catch(e){}');
   if (typeof collapsedTwisters != "string")
   {
      collapsedTwisters = "";
   }
   model.collapsedTwisters = collapsedTwisters;
}

/**
 * Site Title
 */
function getSiteTitle()
{
   var siteTitle = "",
      result,
      response;

   var siteId = page.url.templateArgs.site || "";
   if (siteId !== "")
   {
      result = remote.call("/api/sites/" + encodeURIComponent(siteId));
      if (result.status == 200 && result != "{}")
      {
         response = JSON.parse(result);
         siteTitle = response.title;
         if (typeof siteTitle != "string")
         {
            siteTitle = "";
         }
      }
   }
   model.siteTitle = siteTitle;
}

/**
 * Customizable Header
 */
function getHeader()
{
   // Array of tokenised values for use in i18n messages
   model.labelTokens = [ user.name || "", user.firstName || "", user.lastName || "", user.fullName || ""];
   model.permissions =
   {
      guest: user.isGuest,
      admin: user.isAdmin
   };
}

/**
 * User Status
 */
function getUserStatus()
{
   var userStatus = msg.get("status.default"),
      userStatusTime = "";
   
   if (user.properties["userStatus"] != null)
   {
      userStatus = user.properties["userStatus"];
   }
   if (user.properties["userStatusTime"] != null)
   {
      userStatusTime = user.properties["userStatusTime"];
   }
   
   model.userStatus = userStatus;
   model.userStatusTime = userStatusTime;
}

/**
 * Application logo override
 */
function getLogo()
{
   model.logo = context.getSiteConfiguration().getProperty("logo");
}

/**
 * License usage warnings and errors
 */
function getLicenseInfo()
{
   // Only retrieve license usage information for the first few seconds after login
   // this ensures the usage information is not continually queried.
   // This could be improved by having a central usage service on the web-tier that
   // is reponsible for retrieving the usage in a more sensible schedule.
   if (context.properties["editionInfo"].edition != "UNKNOWN" &&
       user.properties["alfUserLoaded"] > new Date().getTime() - 5000)
   {
      // retrieve license usage information
      var result = remote.call("/api/admin/usage");
      if (result.status.code == status.STATUS_OK)
      {
         usage = JSON.parse(result);
         // if warnings or errors are present, display them to the admin or user
         // admin sees messages if WARN_ADMIN, WARN_ALL, LOCKED_DOWN
         // users see messages if WARN_ALL, LOCKED_DOWN
         if ( (user.isAdmin && (usage.warnings.length != 0 || usage.errors.length != 0)) ||
              (usage.level >= 2 && (usage.warnings.length != 0 || usage.errors.length != 0)) )
         {
            model.usage = usage;
         }
      }
   }
}

function main()
{
   getTwisterPrefs();
   getSiteTitle();
   getHeader();
   getUserStatus();
   getLogo();
   getLicenseInfo();
   
   // Widget instantiation metadata...
   var header = {
      id : "Header", 
      name : "Alfresco.component.Header",
      assignTo : args.htmlid.replace("-", "_"),
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         siteTitle : model.siteTitle,
         minSearchTermLength : parseInt((args.minSearchTermLength != null) ? args.minSearchTermLength : config.scoped["Search"]["search"].getChildValue("min-search-term-length")),
         tokens : {
            site: (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
            pageid: (page.url.templateArgs.pageid != null) ? page.url.templateArgs.pageid : "",
            userid: encodeURIComponent(user.name)
         }
      }
   };   
   model.widgets = [header];
}

if (!user.isGuest)
{
   main();
}
else
{
   getTwisterPrefs();
}
