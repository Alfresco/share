function main()
{

   var nodeRef = (page.url.args.nodeRef != null) ? page.url.args.nodeRef : "";


   var connector = remote.connect("alfresco"),
      remoteUrl = "/api/node/" + nodeRef.replace("://", "/") + "/ruleset/inheritrules/state",
      result = connector.get(remoteUrl);

   if (result.status == status.STATUS_OK)
   {
      var json = JSON.parse(result.response);
      model.inheritRules = !!(json.data.inheritRules == "true");
   }

   // Widget instantiation metadata...
   var rulesHeader = {
      id : "RulesHeader", 
      name : "Alfresco.RulesHeader",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         nodeRef : nodeRef,
         inheritRules: model.inheritRules || ""
      }
   };
   model.widgets = [rulesHeader];

}

main();

