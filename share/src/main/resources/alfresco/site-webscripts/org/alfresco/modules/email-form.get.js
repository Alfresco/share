function main()
{
   var connector = remote.connect("alfresco");
   var result = connector.get("/api/actionConstraints?name=ac-email-templates");
   var templates = [];
   if (result.status == 200)
   {
      templates = JSON.parse(result).data[0].values;
   }
   model.templates = templates;
}

main();
