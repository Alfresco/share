function main()
{
   var height = (args.editorHeight != null) ? args.editorHeight : 180,
       width = (args.editorWidth != null) ? args.editorWidth : 700;

   var commentList = {
      id: "CommentList",
      name : "Alfresco.CommentList",
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
   
   model.widgets = [commentList];
}

main();
