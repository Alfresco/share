<import resource="classpath:alfresco/site-webscripts/org/alfresco/config.lib.js">
<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{
   AlfrescoUtil.param("nodeRef"); // pass in localNodeRef.
   AlfrescoUtil.param("rootPage", "documentlibrary");
   AlfrescoUtil.param("rootLabelId", msg.get("location.path.documents"));
   model.synced = true;

   model.syncMode = syncMode.value;

   // Get the nodeRef for the remote node.
   var remoteNodeInfo = AlfrescoUtil.getRemoteNodeRef(model.nodeRef),
      nodeDetails = null;

   // If we've got the remote nodeRef, let's fetch the details for it, otherwise lookup parent details (it might be indirectly synced).
   if (remoteNodeInfo && remoteNodeInfo.remoteNodeRef)
   {
      nodeDetails = AlfrescoUtil.getRemoteNodeDetails(remoteNodeInfo.remoteNodeRef, remoteNodeInfo.remoteNetworkId);
   }
   else if (remoteNodeInfo && remoteNodeInfo.remoteParentNodeRef)
   {
      model.isParentPath = true;
      nodeDetails = AlfrescoUtil.getRemoteNodeDetails(remoteNodeInfo.remoteParentNodeRef, remoteNodeInfo.remoteNetworkId);
   }

   if (nodeDetails)
   {
      if (nodeDetails.error)
      {
         // Pass through the error by default:
         model.error = {
            code: nodeDetails.error.status.code,
            message: nodeDetails.error.message
         };
         // Handle special errors
         switch (nodeDetails.error.status.code) {
            case 403:
               // On premise permissions error.
               // 403 returned when user doesn't have permission request node details (e.g. no cloud credentials)
               model.error.message = msg.get("sync.status.unknown-location.unauthorized");
               break;
            case 410:
               // Remote user permissions error.
               // 410 returned if the user is authenticated to remote repo, but remote user lacks node permissions to get details.
               model.error.message = msg.get("sync.status.unknown-location.no-permissions");
               break;
         }
      }
      // There's no error:
      else
      {
         // Even though there's no error, lets check we've got the essential location.
         // TODO: I'm not sure when this would occur.
         if (!nodeDetails.item.location.container)
         {
            model.error = {
               code: 410,
               message: msg.get("sync.status.unknown-location.no-permissions")
            };
         }
         // Everything went well:
         else
         {
            model.nodeFound = true;
            model.item = nodeDetails.item;
            model.node = nodeDetails.item.node;
            model.paths = AlfrescoUtil.getPaths(nodeDetails, model.rootPage, model.rootLabelId);
            model.site = nodeDetails.item.location.site;
            model.remoteNetworkId = remoteNodeInfo.remoteNetworkId;
            model.nodeTitle = nodeDetails.item.node.properties["cm:name"];
            model.shareURL = nodeDetails.metadata.shareURL;
            model.isContainer = nodeDetails.item.node.isContainer;
            model.isDirectSync = nodeDetails.item.node.properties["sync:directSync"];
            model.rootNodeRef = remoteNodeInfo.localRootNodeRef;
            model.rootNodeName = remoteNodeInfo.localRootNodeName;
            model.syncOwnerFullName = (remoteNodeInfo.syncSetOwnerFirstName + " " + remoteNodeInfo.syncSetOwnerLastName).replace(/^\s+|\s+$/g, "");
         }
      }
   }
}

main();