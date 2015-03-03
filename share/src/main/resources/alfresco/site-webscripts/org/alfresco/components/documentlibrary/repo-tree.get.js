/**
 * Main entrypoint for component webscript logic
 *
 * @method main
 */
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
   
   var evaluateChildFolders = "true",
       maximumFolderCount = "-1";
   var docLibConfig = config.scoped["RepositoryLibrary"];
   if (docLibConfig != null)
   {
      var tree = docLibConfig["tree"];
      if (tree != null)
      {
         var tmp = tree.getChildValue("evaluate-child-folders");
         evaluateChildFolders = tmp != null ? tmp : "true";
         tmp = tree.getChildValue("maximum-folder-count");
         maximumFolderCount = tmp != null ? tmp : "-1";
         tmp = tree.getChildValue("apply-drop-targets");
         applyDropTargets = tmp != null ? tmp : "true";
      }
   }

   var repoDocListTree = {
      id : "RepositoryDocListTree", 
      name : "Alfresco.RepositoryDocListTree",
      options : {
         rootNode : model.rootNode != null ? model.rootNode : "null",
         evaluateChildFolders : (evaluateChildFolders == "true"),
         maximumFolderCount : parseInt(maximumFolderCount),
         setDropTargets : (applyDropTargets == "true")
      }
   };
   model.widgets = [repoDocListTree];
}

main();
