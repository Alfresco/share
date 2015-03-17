function main() {
   // Widget instantiation metadata...
   var widget = {
      id : "CategoryManager", 
      name : "Alfresco.CategoryManager",
      options : {
         nodeRef: "alfresco://category/root"
      }
   };
   model.widgets = [widget];
}
main();