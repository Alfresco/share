function main()
{
   // Widget instantiation metadata...
   var wikiCreateForm = {
      id : "WikiCreateForm", 
      name : "Alfresco.WikiCreateForm",
      options : {
         siteId :(page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         locale : this.locale
      }
   };
   model.widgets = [wikiCreateForm];
}

main();
