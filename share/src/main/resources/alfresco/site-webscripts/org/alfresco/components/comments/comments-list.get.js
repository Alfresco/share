<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function getActivityParameters(nodeRef, defaultValue)
{
   var cm = "{http://www.alfresco.org/model/content/1.0}",
      metadata = AlfrescoUtil.getMetaData(nodeRef, {});
   if (metadata.properties)
   {
      if (model.activityType == "document")
      {
         return (
         {
            itemTitle: metadata.properties[cm + 'name'],
            page: 'document-details',
            pageParams:
            {
               nodeRef: metadata.nodeRef
            }
         });
      }
      else if (model.activityType == "folder")
      {
         return (
         {
            itemTitle: metadata.properties[cm + 'name'],
            page: 'folder-details',
            pageParams:
            {
               nodeRef: metadata.nodeRef
            }
         });
      }
      else if (model.activityType == "link")
      {
         var lm = "{http://www.alfresco.org/model/linksmodel/1.0}";
         return (
         {
            itemTitle: metadata.properties[lm + "title"],
            page: 'links-view',
            pageParams:
            {
               linkId: metadata.properties[cm + "name"]
            }
         });
      }
      else if (model.activityType == "blog")
      {
         return (
         {
            itemTitle: metadata.properties[cm + 'title'],
            page: 'blog-postview',
            pageParams:
            {
               postId: metadata.properties[cm + "name"]
            }
         });
      }
   }
   return defaultValue;
}

function main()
{
   AlfrescoUtil.param('nodeRef', null);
   AlfrescoUtil.param('site', null);
   AlfrescoUtil.param('maxItems', 10);
   AlfrescoUtil.param('activityType', null);

   if (!model.nodeRef)
   {
      // Handle urls that doesn't use nodeRef
      AlfrescoUtil.param('postId', null);
      if (model.postId)
      {
         // translate blog post "postId" to a nodeRef
         AlfrescoUtil.param('container', 'blog');
         model.nodeRef = AlfrescoUtil.getBlogPostDetailsByPostId(model.site, model.container, model.postId, {}).nodeRef;
      }
      else
      {
         AlfrescoUtil.param('linkId', null);
         if (model.linkId)
         {
            // translate link's "linkId" to a nodeRef
            AlfrescoUtil.param('container', 'links');
            model.nodeRef = AlfrescoUtil.getLinkDetailsByPostId(model.site, model.container, model.linkId, {}).nodeRef;
         }
      }
   }

   var documentDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);
   var suppressSocial = documentDetails && AlfrescoUtil.isComponentSuppressed(documentDetails.item.node, AlfrescoUtil.getSupressSocialfolderDetailsConfig());
   var activityParameters = null;
   if (documentDetails && !suppressSocial)
   {
      activityParameters = getActivityParameters(model.nodeRef, null);
   }
   else
   {
      // Signal to the template that the node doesn't exist and that comments therefore shouldn't be displayed.
      model.nodeRef = null;
   }
   
   // Widget instantiation metadata...
   var commentList = {
      id : "CommentsList",
      name : "Alfresco.CommentsList",
      options : {
         nodeRef : model.nodeRef,
         siteId : model.site,
         maxItems : parseInt(model.maxItems),
         activity :  activityParameters,
         editorConfig : {
            menu: {},
            toolbar: "bold italic underline | bullist numlist | forecolor backcolor | undo redo removeformat",
            language: locale,
            statusbar: false
         }
      }
   };
   model.widgets = [commentList];
}

main();
