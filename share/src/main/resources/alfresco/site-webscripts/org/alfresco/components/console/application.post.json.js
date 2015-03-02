/**
 * Admin Console Application Tool POST method
 */

function main()
{
   if (user.isAdmin)
   {
      var sc = context.getSiteConfiguration();
      
      // theme option
      var themeId = json.get("console-options-theme-menu");
      context.setThemeId(new String(themeId));
      
      // persist theme across application
      // the theme is applied by the SlingshotPageView class on view render
      if (sc.getProperty("theme") != themeId)
      {
         sc.setProperty("theme", themeId);
         sc.save();
      }
      
      // logo option
      var logoId = json.get("console-options-logo");
      if (logoId != null && (logoId = new String(logoId)).length != 0)
      {
         // "reset" is special case to reset application logo to theme default
         sc.setProperty("logo", (logoId != "reset" ? logoId : ""));
         sc.save();
      }
   }
   model.success = true;
}

main();