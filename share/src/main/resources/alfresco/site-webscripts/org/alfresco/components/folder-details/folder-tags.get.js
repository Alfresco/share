<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

var FolderTags =
{
   PROP_TAGGABLE: "cm:taggable",

   getTags: function FolderTags_getTags(record)
   {
      var tagsArray = [],
         node = record.node,
         prop_taggable = node.properties[FolderTags.PROP_TAGGABLE] || [];

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
   var folderDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);
   if (folderDetails)
   {
      model.nodeRef = folderDetails.item.nodeRef;
      model.tags = FolderTags.getTags(folderDetails.item);
      model.allowMetaDataUpdate = folderDetails.item.node.permissions.user["Write"] || false;
   }
}

main();
