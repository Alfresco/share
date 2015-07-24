<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
function main()
{
   var shareId = args.shareId,
      result = remote.connect("alfresco-noauth").get("/api/internal/shared/node/" + encodeURIComponent(shareId) + "/metadata");

   if (result.status == 200)
   {
      var nodeMetadata = JSON.parse(result);

      // File extension
      model.fileExtension = "generic";
      var i = nodeMetadata.name.lastIndexOf(".");
      if (i > -1)
      {
         model.fileExtension = nodeMetadata.name.substring(i + 1);
      }

      // Display name
      model.displayName = nodeMetadata.name;

      // Modify
      model.modifierFirstName = nodeMetadata.modifier.firstName || "";
      model.modifierLastName = nodeMetadata.modifier.lastName || "";
      model.modifyDate = nodeMetadata.modifiedOn;

      // Download button for images
      model.showDownload = "false";

      var isImage = (nodeMetadata.mimetype && nodeMetadata.mimetype.match("^image/"));
      var  nodeRef= nodeMetadata.nodeRef;

      if (isImage)
      {
         var documentDetails = AlfrescoUtil.getNodeDetails(nodeRef, null);
         if (documentDetails)
         {
            model.contentURL = documentDetails.item.node.contentURL;
            model.showDownload = "true";
         }
         else
         {
            model.contentURL="";
         }
      }
   }

}

main();
