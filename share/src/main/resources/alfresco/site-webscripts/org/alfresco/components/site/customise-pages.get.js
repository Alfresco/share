<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

/**
 * Customise Site Pages component GET method
 */

function main()
{
   // site ID and available pages
   model.siteId = page.url.templateArgs.site;
   model.pages = AlfrescoUtil.getPages(true);
   
   // available and currently selected site theme
   var currentTheme = null;
   var dashboardPage = sitedata.getPage("site/" + page.url.templateArgs.site + "/dashboard");
   if (dashboardPage.properties.theme != null)
   {
      currentTheme = dashboardPage.properties.theme;
   }
   model.themes = [];
   model.themes.push(
   {
      id: "",
      title: msg.get("label.applicationTheme"),
      selected: (currentTheme == null || currentTheme.length == 0)
   });
   var themes = sitedata.getObjects("theme");
   for (var i = 0, t; i < themes.length; i++)
   {
      t = themes[i];
      model.themes.push(
      {
         id: t.id,
         title: (t.titleId != null && msg.get(t.titleId) != t.titleId ? msg.get(t.titleId) : t.title),
         selected: (t.id == currentTheme)
      });
   }
   
   // Widget instantiation metadata...
   var customisePages = {
      id : "CustomisePages", 
      name : "Alfresco.CustomisePages",
      options : {
         siteId : model.siteId
      }
      
   };
   model.widgets = [customisePages];
}

main();


