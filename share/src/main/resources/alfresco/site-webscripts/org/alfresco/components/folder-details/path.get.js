function main()
{
   // Widget instantiation metadata...
   var showIconType = (args.showIconType == "true");
   var path = {
      id: "Path", 
      name: "Alfresco.component.Path",
      options: {
         showIconType: showIconType
      }
   };
   model.showIconType = showIconType;
   model.widgets = [path];
}

main();