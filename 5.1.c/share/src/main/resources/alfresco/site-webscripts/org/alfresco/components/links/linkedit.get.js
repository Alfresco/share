function main()
{
   // Widget instantiation metadata...
   
   var editMode = false;
   var linkId = "";
   if (page.url.args.linkId != null)
   {
      editMode = true;
      linkId = page.url.args.linkId;
   }
   
   var linkEdit = {
      id: "LinkEdit", 
      name: "Alfresco.LinkEdit",
      options: {
         siteId: (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         containerId: "links",
         editMode: editMode,
         linkId: linkId
      }
   };
   model.widgets = [linkEdit];
}

main();
