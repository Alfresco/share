function main()
{
   model.lists = ['listA', 'listB'];

   // Widget instantiation metadata...
   var list = {
      id : "DataListList", 
      name : "Alfresco.DataListList",
      options : {
         siteId : (page.url.templateArgs["site"] != null) ? page.url.templateArgs["site"] : ""
      }
   };
   model.widgets = [list];
}

main();
