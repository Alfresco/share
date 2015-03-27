function main()
{
   var height = (args.editorHeight != null) ? args.editorHeight : 250,
         width = (args.editorWidth != null) ? args.editorWidth : 538;

   var createComment = {
      id: "CreateComment",
      name : "Alfresco.CreateComment",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         containerId : template.properties.container != null ? template.properties.container : "documentLibrary",
         height : height,
         width : width,
         editorConfig : {
            height: height,
            width: width,
            menu: {},
            toolbar: "bold italic underline | bullist numlist | forecolor backcolor | undo redo removeformat",
            language: locale,
            statusbar: false
         }
      }
   };
   model.widgets = [createComment];
   model.widgets.push();
}

main();
