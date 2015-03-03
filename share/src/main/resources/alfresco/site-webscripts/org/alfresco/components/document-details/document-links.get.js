<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   var documentDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);
   if (documentDetails)
   {
      model.document = documentDetails.item;
      model.repositoryUrl = AlfrescoUtil.getRepositoryUrl();
   }
   
   // Widget instantiation metadata...
   var documentActions = {
      id: "DocumentLinks", 
      name: "Alfresco.DocumentLinks",
      options: {
         nodeRef: model.nodeRef,
         siteId: model.site
      }
   };
   model.widgets = [documentActions];
}

main();