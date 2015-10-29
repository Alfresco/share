<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   AlfrescoUtil.param('formId', null);
   var folderDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);
   if (folderDetails)
   {
      model.allowMetaDataUpdate = folderDetails.item.node.permissions.user["Write"] || false;
   }
   
   // Widget instantiation metadata...
   var folderMetadata = {
      id : "FolderMetadata", 
      name : "Alfresco.FolderMetadata",
      options : {
         nodeRef : model.nodeRef,
         siteId : model.site,
         formId : model.formId
      }
   };
   model.widgets = [folderMetadata];
}

main();

