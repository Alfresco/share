<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/include/documentlist.lib.js">

model.rootNode = DocumentList.getConfigValue("RepositoryLibrary", "root-node", "alfresco://company/home");

function main()
{
   // Widget instantiation metadata...
   var rulesLinked = {
      id : "RulesLinked", 
      name : "Alfresco.RulesLinked",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         nodeRef : (page.url.args.nodeRef != null) ? page.url.args.nodeRef : "",
         repositoryBrowsing : (model.rootNode != null)
      }
   };
   model.widgets = [rulesLinked];
}

main();