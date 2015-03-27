function main()
{
   // Widget instantiation metadata...
   var createTopic = {
      id : "CreateTopic", 
      name : "Alfresco.CreateTopic",
      options : {
         topicId : (page.url.args.topicId != null) ? page.url.args.topicId : "",
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         containerId : (page.url.args.containerId != null) ? page.url.args.containerId : "discussions",
         editMode : (page.url.args.topicId != null),
         editorConfig : {
            height: 300,
            language: this.locale
         }
      }
   };
   
   model.widgets = [createTopic];
}

main();
