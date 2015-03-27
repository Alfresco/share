<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

/**
 * Share URL wrapper for the cloud
 *
 */

function main()
{
   model.result = null;

   // Get nodeRef
   AlfrescoUtil.param('nodeRef', null);

   // Convert nodeRef to remote nodeRef
   var remoteNodeInfo = AlfrescoUtil.getRemoteNodeRef(model.nodeRef);

   // Get cloud URL
   if (remoteNodeInfo && remoteNodeInfo.remoteNodeRef)
   {
      var connector = remote.connect("alfresco"),
         result = connector.get("/cloud/sites/shareUrl?nodeRef=" + encodeURIComponent(remoteNodeInfo.remoteNodeRef) + "&network=" + remoteNodeInfo.remoteNetworkId);
      if (result.status == 200)
      {
         var resultObj = JSON.parse(result);
         if (resultObj.url)
         {
            status.code = 303;
            status.location = resultObj.url;
         }
      }
   }

}

main();