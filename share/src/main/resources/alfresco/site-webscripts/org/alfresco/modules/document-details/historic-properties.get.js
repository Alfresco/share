<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

var documentDetails = AlfrescoUtil.getNodeDetails(args.nodeRef, null);
if (documentDetails)
{
   model.document = documentDetails.item;
}