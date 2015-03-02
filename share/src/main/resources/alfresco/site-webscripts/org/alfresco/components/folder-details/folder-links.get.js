<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   var folderDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);
   if (folderDetails)
   {
      model.folder = folderDetails.item;
      var repositoryUrl = AlfrescoUtil.getRepositoryUrl(),
         webdavUrl = folderDetails.item.webdavUrl;

      if (repositoryUrl && webdavUrl)
      {
         model.webdavUrl = AlfrescoUtil.combinePaths(repositoryUrl, webdavUrl);
      }
   }

   // Widget instantiation metadata...
   var folderLinks = {
      id : "FolderLinks", 
      name : "Alfresco.FolderLinks",
      options : {
         nodeRef : model.nodeRef,
         siteId : (model.site != null) ? model.site : null
      }
   };
   model.widgets = [folderLinks];
}

main();

