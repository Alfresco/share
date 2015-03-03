function main()
{
   // Repository Library root node
   var rootNode = "alfresco://company/home",
       repoConfig = config.scoped["RepositoryLibrary"]["root-node"];
   
   if (repoConfig !== null && repoConfig.value !== null)
   {
      rootNode = repoConfig.value;
   }
   model.rootNode = rootNode;
   
   // Widget instantiation metadata...
   var filters = config.scoped['DocumentLibrary']['filters'],
       maxTagCount = filters.getChildValue('maximum-tag-count');
   
   if (maxTagCount == null)
   {
      maxTagCount = "100";
   }
   
   var tagFilter = {
      id : "TagFilter", 
      name : "Alfresco.TagFilter",
      assignTo : "tagFilter",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         containerId : (template.properties.container != null) ? template.properties.container : "",
         rootNode : model.rootNode,
         numTags : parseInt(maxTagCount)
      }
   };
   model.widgets = [tagFilter];
}

main();
