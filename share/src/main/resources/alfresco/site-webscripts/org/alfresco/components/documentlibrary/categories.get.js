function main()
{
   var evaluateChildFolders = "true";
   var docLibConfig = config.scoped["DocumentLibrary"];
   if (docLibConfig != null)
   {
      var categories = docLibConfig["categories"];
      if (categories != null)
      {
         var tmp = categories.getChildValue("evaluate-child-folders");
         evaluateChildFolders = tmp != null ? tmp : "true";
      }
   }

   var categories = {
      id : "DocListCategories", 
      name : "Alfresco.DocListCategories",
      options : {
         nodeRef : "alfresco://category/root", 
         evaluateChildFolders : (evaluateChildFolders == "true")
      }
   };
   model.widgets = [categories];
}

main();
