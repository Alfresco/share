<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/include/documentlist.lib.js">

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   AlfrescoUtil.param('container', 'documentLibrary');

   var folderDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site,
   {
      actions: true
   });
   if (folderDetails)
   {
      model.folderDetails = true;
      doclibCommon();
   }
   
   model.syncMode = syncMode.getValue();

   // Widget instantiation metadata...
   var folderActions = {
      id : "FolderActions", 
      name : "Alfresco.FolderActions",
      options : {
         nodeRef : model.nodeRef,
         siteId : (model.site != null) ? model.site : null,
         containerId : model.container,
         rootNode : model.rootNode,
         repositoryRoot : AlfrescoUtil.getRootNode(),
         replicationUrlMapping : (model.replicationUrlMapping != null) ? model.replicationUrlMapping : "{}",
         repositoryBrowsing : (model.rootNode != null),
         folderDetails : folderDetails,
         syncMode : model.syncMode != null ? model.syncMode : ""
      }
   };
   if (model.repositoryUrl != null)
   {
      folderActions.options.repositoryUrl = model.repositoryUrl;
   }
   
   model.widgets = [folderActions];
}

main();
