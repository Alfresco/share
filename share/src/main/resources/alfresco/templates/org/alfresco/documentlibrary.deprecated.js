/**
 * DEPRECATED!
 *
 * NOTE!
 *
 * This functionality was previously place in documentlibrary.js and was used by a number of templates to dynamically
 * alter the region-ids in the template. The preferred way of chaning components for a region is now to use evaluators.
 */


function toRepoType(contentType)
{
   var type = "";
   switch (String(contentType))
   {
      case "wcmqs":
         type = "ws:websiteContainer";
         break;
   }
   return type;
}

function fromRepoType(repoType)
{
   var type = "";
   switch (String(repoType))
   {
      case "ws:websiteContainer":
         type = "wcmqs";
         break;   
   }
   return type;
}

function getLocationType()
{
   // Need to know what type of node the container is
   var siteId = page.url.templateArgs.site,
      containerId = template.properties.container || "documentLibrary",
      containerType = "cm:folder",
      contentType = "";

   if (siteId !== null)
   {
      var p = sitedata.getPage("site/" + siteId + "/dashboard");
      if (p != null)
      {
         var pageMetadata = p.properties.pageMetadata;
         if (pageMetadata)
         {
            try
            {
               // Parse json using Java to a org.json.simple.JSONObject
               pageMetadata = jsonUtils.toObject(pageMetadata);

               // Print object as json and use eval so we get a Rhino javascript object to execute as usual
               pageMetadata = eval("(" + pageMetadata.toString() + ")");
            }
            catch(e){
               pageMetadata = {};
            }
         }
         else
         {
            pageMetadata = {};
         }

         var doclibMeta = pageMetadata[page.id] || {};
         if (doclibMeta.titleId != null)
         {
            // Save the overridden page title into the request context
            context.setValue("page-titleId", doclibMeta.titleId);
         }
         contentType = doclibMeta.type;
      }

      var connector = remote.connect("alfresco");
      var result = connector.get("/slingshot/doclib/container/" + siteId + "/" + containerId + "?type=" + toRepoType(contentType));
      if (result.status == 200)
      {
         var data = JSON.parse(result);
         containerType = data.container.type;
      }
   }
   
   return (
   {
      siteId: siteId,
      containerType: containerType
   });
}

var objLocation = getLocationType(),
   doclibType = fromRepoType(objLocation.containerType),
   scopeType = objLocation.siteId !== null ? "" : "repo-";

model.doclibType = doclibType == "" ? scopeType : doclibType + "-";
model.appType = "";

// Repository Library root node
var rootNode = "alfresco://company/home",
   repoConfig = config.scoped["RepositoryLibrary"]["root-node"];
if (repoConfig !== null)
{
   rootNode = repoConfig.value;
}

model.rootNode = rootNode;
