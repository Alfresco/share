<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   AlfrescoUtil.param('container', 'documentLibrary');
   model.allowNewVersionUpload = false;
   model.isWorkingCopy = false;
   model.exist = false;
   var documentDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);
   if (documentDetails)
   {
      var userPermissions = documentDetails.item.node.permissions.user;
      model.allowNewVersionUpload = (!documentDetails.item.node.isLocked && documentDetails.item.node.permissions.user["Write"]) || false;
      model.isWorkingCopy = (documentDetails.item && documentDetails.item.workingCopy && documentDetails.item.workingCopy.isWorkingCopy) ? true : false;
      model.exist = true;
   }
   
   // Widget instantiation metadata...
   var documentVersions = {
      id : "DocumentVersions", 
      name : "Alfresco.DocumentVersions",
      options : {
         nodeRef : model.nodeRef,
         siteId : model.site,
         containerId : model.container,
         workingCopyVersion : model.workingCopyVersion,
         allowNewVersionUpload : model.allowNewVersionUpload
      }
   };
   
   model.widgets = [documentVersions];
}

main();