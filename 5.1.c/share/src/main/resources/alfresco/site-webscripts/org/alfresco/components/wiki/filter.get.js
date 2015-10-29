function main()
{
   // Widget instantiation metadata...
   var baseFilter = {
      id : "BaseFilter", 
      name : "Alfresco.component.BaseFilter",
      initArgs : [ "'Alfresco.WikiFilter'", "\"" + args.htmlid + "\""],
      useMessages : false
   };
   model.widgets = [baseFilter];
}

main();

