function main()
{
   // Call the repo to collect server meta-data
   var conn = remote.connect("alfresco"),
      res = conn.get("/api/server"),
      json = JSON.parse(res);


   // Create model and defaults
   model.shareVersion = "Unknown";
   model.shareLibs = {};
   model.serverEdition = "Unknown";
   model.serverVersion = "Unknown (Unknown)";
   model.serverSchema = "Unknown";
   model.licenseHolder = "UNKNOWN";
   
   // Check if we got a positive result
   if (json.data)
   {
      model.serverEdition = json.data.edition;
      model.serverVersion = json.data.version;
      model.serverSchema = json.data.schema;
      model.licenseHolder = context.properties["editionInfo"].holder;
   }
   model.shareVersion = shareManifest.getImplementationVersion();
   model.shareLibs = shareManifest.attributesMap("Share Libraries");
   model.shareBuild = "r" + shareManifest.mainAttributeValue("Build-Revision") + 
      "-b" + shareManifest.mainAttributeValue("Build-Number");
}

main();