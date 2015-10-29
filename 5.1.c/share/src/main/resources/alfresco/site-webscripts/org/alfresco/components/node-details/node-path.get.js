<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

/**
 * Cloud Sync Status Information
 *
 */

function main()
{
   AlfrescoUtil.param("nodeRef");
   AlfrescoUtil.param("site", "defaultSite");
   AlfrescoUtil.param("rootPage", "documentlibrary");
   AlfrescoUtil.param("rootLabelId", "path.documents");
   var nodeDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);
   if (nodeDetails)
   {
      model.item = nodeDetails.item;
      model.node = nodeDetails.item.node;
      model.paths = AlfrescoUtil.getPaths(nodeDetails, model.rootPage, model.rootLabelId);
   }
}

main();