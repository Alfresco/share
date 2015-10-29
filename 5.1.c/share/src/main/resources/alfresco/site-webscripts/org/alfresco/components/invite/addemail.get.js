function main()
{
   // get details of the authentication chain
   var res = remote.call("/api/authentication");
   var json = JSON.parse(res);

   model.allowEmailInvite = json.data.creationAllowed
      && config.scoped["Users"]["enable-external-users-panel"].getValue() == "true";

   // Widget instantiation metadata...
   var addEmailInvite = {
      id : "AddEmailInvite", 
      name : "Alfresco.AddEmailInvite",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : ""
      }
   };
   model.widgets = [addEmailInvite];
}

main();

