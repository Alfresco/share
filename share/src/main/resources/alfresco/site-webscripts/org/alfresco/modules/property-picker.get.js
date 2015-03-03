<import resource="classpath:alfresco/site-webscripts/org/alfresco/config.lib.js">

function main()
{
   // Load transient property names, display labels and types
   var connector = remote.connect("alfresco");
   var result = connector.get("/api/actionConstraints/ac-content-properties");
   var templates = [];
   if (result.status == 200)
   {
      model.transientContentProperties = jsonUtils.toJSONString(JSON.parse(result).data.values);
   }

   // Load filter for aspects, types and property evalurators that shall be visible
   var classFilter = {};
   classFilter["aspects"] = getDocumentLibraryAspects();
   classFilter["types"] = getDocumentLibraryTypes();
   model.classFilter = jsonUtils.toJSONString(classFilter);
   
}

main();
