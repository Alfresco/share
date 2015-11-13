/**
 * Main entrypoint for component webscript logic
 *
 * @method main
 */
function main()
{
   // Repository Library root node
   model.rootNode = "alfresco://company/shared";
   
   var evaluateChildFolders = "true",
       maximumFolderCount = "-1",
       customFolderStyleConfig = null;
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
   var commonComponentStyleConfig = config.scoped["CommonComponentStyle"];
   if (commonComponentStyleConfig != null)
   {
      var commonComponentStyle = commonComponentStyleConfig["component-style"];
      if (commonComponentStyle != null)
      {
         customFolderStyleConfig = commonComponentStyle != null ? JSON.parse(commonComponentStyle.value) : null;
      }
   }

   var repoDocListTree = {
      id : "RepositoryDocListTree", 
      name : "Alfresco.RepositoryDocListTree",
      options : {
         rootNode : model.rootNode != null ? model.rootNode : "null",
         evaluateChildFolders : (evaluateChildFolders == "true"),
         maximumFolderCount : parseInt(maximumFolderCount),
         setDropTargets : true,
         customFolderStyleConfig : customFolderStyleConfig
      }
   };
   model.widgets = [repoDocListTree];
}

main();
