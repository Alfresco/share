/**
 * Main entrypoint for component webscript logic
 *
 * @method main
 */
function main()
{
   // Repository Library root node
   model.rootNode = "alfresco://user/home";
   
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
      }
   }

   var repoDocListTree = {
      id : "RepositoryDocListTree", 
      name : "Alfresco.RepositoryDocListTree",
      options : {
         rootNode : model.rootNode != null ? model.rootNode : "null",
         evaluateChildFolders : (evaluateChildFolders == "true"),
         maximumFolderCount : parseInt(maximumFolderCount),
         setDropTargets : true
      }
   };
   model.widgets = [repoDocListTree];
}

main();
