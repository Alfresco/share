<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

var DocumentTags =
{
   PROP_TAGGABLE: "cm:taggable",

   getTags: function DocumentTags_getTags(record)
   {
      var tagsArray = [],
         node = record.node,
         prop_taggable = node.properties[DocumentTags.PROP_TAGGABLE] || [];

      for (var i = 0, ii = prop_taggable.length; i < ii; i++)
      {
         tagsArray.push(prop_taggable[i].name);
      }
      return tagsArray;
   }
};

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   var documentDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);
   if (documentDetails)
   {
      model.nodeRef = documentDetails.item.nodeRef;
      model.tags = DocumentTags.getTags(documentDetails.item);
      model.allowMetaDataUpdate = (!documentDetails.item.node.isLocked && documentDetails.item.node.permissions.user["Write"]) || false;
   }

   // Widget instantiation metadata...
   var documentTags = {
      id : "DocumentTags", 
      name : "Alfresco.DocumentTags",
      options : {
         nodeRef : model.nodeRef,
         siteId : model.site
      }
   };
   model.widgets = [documentTags];
}

main();
