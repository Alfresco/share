
function main()
{
   // Load folder info
   var connector = remote.connect("alfresco");
   model.folder = loadDisplayInfo(connector, page.url.args.nodeRef);

   // Load rules
   result = connector.get("/api/node/" + page.url.args.nodeRef.replace("://", "/") + "/ruleset");
   if (result.status == 200)
   {
      var ruleset = JSON.parse(result).data;
      if (!ruleset)
      {
         ruleset = {};
      }
      model.ruleset = ruleset;

      var linkedToNodeRef = ruleset.linkedToRuleSet;
      if (linkedToNodeRef)
      {
         linkedToNodeRef = linkedToNodeRef.substring("/api/node/".length);
         linkedToNodeRef = linkedToNodeRef.substring(0, linkedToNodeRef.indexOf("/ruleset"));
         var tokens = linkedToNodeRef.split("/");
         linkedToNodeRef = tokens[0] + "://" + tokens[1] + "/" + tokens[2];
         model.linkedToFolder = loadDisplayInfo(connector, linkedToNodeRef);         
      }
   }
}

function loadDisplayInfo(connector, nodeRef)
{
   var siteId = page.url.templateArgs.site,
      url = "/slingshot/doclib/node/" + nodeRef.replace("://", "/");

   if (siteId == null)
   {
      // Repository Library root node
      var rootNode = "alfresco://company/home",
         repoConfig = config.scoped["RepositoryLibrary"]["root-node"];
      if (repoConfig !== null)
      {
         rootNode = repoConfig.value;
      }
      url += "?libraryRoot=" + encodeURIComponent(rootNode);
   }
   var result = connector.get(url);
   if (result.status == 200)
   {
      var location = JSON.parse(result).item.location;
      return (
      {
         nodeRef: nodeRef,
         site: location.site, 
         name: location.file,
         path: location.path
      });
   }
   return null;
}

main();
