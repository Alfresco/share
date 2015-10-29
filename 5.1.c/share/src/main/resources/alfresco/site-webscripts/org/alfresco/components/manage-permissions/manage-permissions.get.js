function main()
{
   // Repository Library root node
   var rootNode = "alfresco://company/home",
      repoConfig = config.scoped["RepositoryLibrary"]["root-node"];
   if (repoConfig !== null)
   {
      rootNode = repoConfig.value;
   }
   
   model.rootNode = rootNode;
   
   // Widget instantiation metadata...
   var managePermissions = {
      id : "ManagePermissions", 
      name : "Alfresco.component.ManagePermissions",
      options : {
         nodeRef : args.nodeRef,
         site : (this.page != null) ? page.url.templateArgs.site : ""
      }
      
   };
   model.widgets = [managePermissions];
}

main();

