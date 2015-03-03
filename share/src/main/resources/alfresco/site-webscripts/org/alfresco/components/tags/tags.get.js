<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

function getTags(site, container)
{
    var theUrl = "/api/tagscopes/site/" + site + "/" + container + "/tags";
    var data = doGetCall(theUrl, true);
    return data;
}

function main()
{
   var site = page.url.templateArgs.site,
   container = template.properties.container,
   tags = [];

   var data = getTags(site, container);
   if (data && data.tags)
   {
      tags = data.tags;
   }
   
   model.tags = tags;
   
   // Widget instantiation metadata...
   var tagComponent = {
      id : "TagComponent", 
      name : "Alfresco.TagComponent",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         containerId : (template.properties.container != null) ? template.properties.container : ""
      }
   };
   
   model.widgets = [tagComponent];
}

main();

