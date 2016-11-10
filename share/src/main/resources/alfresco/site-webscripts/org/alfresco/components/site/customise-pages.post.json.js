/**
 * Customise Site Pages component POST method
 */

function main()
{
   // Get clients json request as a "normal" js object literal
   var clientRequest = json.toString();
   var clientJSON = JSON.parse(clientRequest);
   
   // The site and pages we are modifiying
   var siteId = clientJSON.siteId;
   var newPages = clientJSON.pages;
   
   /**
    * The dashboard page always exists and can be used to save the pages.
    * Create a property named "sitePages" in the page's properties object
    * and store a json string representing the pages list.
    */
   
   // Cast from object string
   var newPagesString = jsonUtils.toJSONString(newPages) + "";
   // Apply the list of pages to the site object property
   var p = sitedata.getPage("site/" + siteId + "/dashboard");
   p.properties.sitePages = newPagesString;
   p.properties.dashboardSitePage = "true";
   
   // Theme override for the site
   p.properties.theme = clientJSON.themeId;
   
   // Save site object properties
   p.save();
   
   model.success = true;
}

main();