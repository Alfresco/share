<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{
   AlfrescoUtil.param("nodeRef");
   AlfrescoUtil.param("site", null);
   AlfrescoUtil.param("rootPage", "documentlibrary");
   AlfrescoUtil.param("rootLabelId", "path.documents");
   AlfrescoUtil.param("showOnlyLocation", "false");
   AlfrescoUtil.param("showFavourite", "true");
   AlfrescoUtil.param("showLikes", "true");
   AlfrescoUtil.param("showComments", "true");
   AlfrescoUtil.param("showQuickShare", "true");
   AlfrescoUtil.param("showDownload", "true");
   AlfrescoUtil.param("showPath", "true");
   AlfrescoUtil.param("libraryRoot", null);
   AlfrescoUtil.param("pagecontext", null);
   var nodeDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site, null, model.libraryRoot);
   if (nodeDetails)
   {
      model.item = nodeDetails.item;
      model.node = nodeDetails.item.node;
      model.isContainer = nodeDetails.item.node.isContainer;
      model.paths = AlfrescoUtil.getPaths(nodeDetails, model.rootPage, model.rootLabelId);
      model.showQuickShare = (!model.isContainer && model.showQuickShare && (config.scoped["Social"]["quickshare"].getChildValue("url") != null) && quickShareStatus.enabled).toString();
      model.isWorkingCopy = (model.item && model.item.workingCopy && model.item.workingCopy.isWorkingCopy) ? true : false;
      model.showFavourite = (model.isWorkingCopy ? false : model.showFavourite).toString();
      model.showLikes = (model.isWorkingCopy ? false : model.showLikes).toString();
      model.showComments = (model.isWorkingCopy ? false : ((nodeDetails.item.node.permissions.user["CreateChildren"] || false) && model.showComments)).toString();
      model.showDownload = (!model.isContainer && model.showDownload).toString();
      model.showOnlyLocation = model.showOnlyLocation.toString();
      var count = nodeDetails.item.node.properties["fm:commentCount"];
      model.commentCount = (count != undefined ? count : null);

      // Widget instantiation metadata...
      var likes = {};
      if (model.item.likes != null)
      {
         likes.isLiked = model.item.likes.isLiked || false;
         likes.totalLikes = model.item.likes.totalLikes || 0;
      }

      var nodeHeader = {
         id : "NodeHeader",
         name : "Alfresco.component.NodeHeader",
         options : {
            nodeRef : model.nodeRef,
            siteId : model.site,
            actualSiteId: model.item.location.site != null ? model.item.location.site.name : null,
            rootPage : model.rootPage,
            rootLabelId : model.rootLabelId,
            showOnlyLocation: (model.showOnlyLocation == "true"),
            showQuickShare: (model.showQuickShare == "true"),
            showFavourite : (model.showFavourite == "true"),
            showLikes : (model.showLikes == "true"),
            showComments : (model.showComments == "true"),
            showDownload : (model.showDownload == "true"),
            showPath : (model.showPath == "true"),
            displayName : (model.item.displayName != null) ? model.item.displayName : model.item.fileName,
            likes : likes,
            isFavourite : (model.item.isFavourite || false),
            isContainer : model.isContainer,
            sharedId: model.item.node.properties["qshare:sharedId"] || null,
            sharedBy: model.item.node.properties["qshare:sharedBy"] || null,
            pagecontext: model.pagecontext,
            libraryRoot: model.libraryRoot
         }
      };
      
      if(nodeDetails.item.workingCopy != null && nodeDetails.item.workingCopy.isWorkingCopy)
      {
         nodeHeader.options.showFavourite = false;
         nodeHeader.options.showLikes = false;
         model.showQuickShare = "false";
         model.showComments = "false";
      }
      
      model.widgets = [nodeHeader];
   }
}

main();
