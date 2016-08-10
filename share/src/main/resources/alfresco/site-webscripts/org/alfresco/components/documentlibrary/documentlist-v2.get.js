<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/include/toolbar.lib.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/upload/uploadable.lib.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/include/documentlist.lib.js">

doclibCommon();

function widgets()
{
   var useTitle = "true";
   var docLibConfig = config.scoped["DocumentLibrary"];
   if (docLibConfig != null)
   {
      var tmp = docLibConfig["use-title"];
      useTitle = tmp != null ? tmp : "true";
   }

   var docListToolbar = {
      id: "DocListToolbar", 
      name: "Alfresco.DocListToolbar",
      assignTo: "docListToolbar",
      options: {
         siteId: (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         rootNode: toolbar.rootNode != null ? toolbar.rootNode : "",
         repositoryRoot: toolbar.rootNode,
         hideNavBar: Boolean(toolbar.preferences.hideNavBar),
         repositoryBrowsing: toolbar.rootNode != null,
         useTitle: (useTitle == "true"),
         syncMode: toolbar.syncMode != null ? toolbar.syncMode : "",
         createContentByTemplateEnabled: model.createContentByTemplateEnabled,
         createContentActions: model.createContent
      }
   };
   
   var documentList = {
      id : "DocumentList", 
      name : "Alfresco.DocumentList",
      options : {
         syncMode : model.syncMode != null ? model.syncMode : "",         
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         containerId : template.properties.container != null ? template.properties.container : "documentLibrary",
         rootNode : model.rootNode != null ? model.rootNode : "null",
         repositoryRoot : model.rootNode,
         usePagination : (args.pagination == "true"),
         sortAscending : (model.preferences.sortAscending != null ? model.preferences.sortAscending : true),
         sortField : model.preferences.sortField != null ? model.preferences.sortField : "cm:name",
         showFolders : (model.preferences.showFolders != null ? model.preferences.showFolders : true),
         hideNavBar: (model.preferences.hideNavBar != null ? model.preferences.hideNavBar : false),
         simpleView : model.preferences.simpleView != null ? model.preferences.simpleView : "null",
         viewRenderers: model.viewRenderers,
         viewRendererName : model.preferences.viewRendererName != null ? model.preferences.viewRendererName : "detailed",
         viewRendererNames : model.viewRendererNames != null ? model.viewRendererNames : ["simple", "detailed", "gallery", "filmstrip"],
         highlightFile : page.url.args["file"] != null ? page.url.args["file"] : "",
         replicationUrlMapping : model.replicationUrlMapping != null ? model.replicationUrlMapping : "{}",
         repositoryBrowsing : model.rootNode != null, 
         useTitle : (model.useTitle != null ? model.useTitle == "true" : true),
         userIsSiteManager : model.userIsSiteManager,
         associatedToolbar: { _alfValue: "docListToolbar", _alfType: "REFERENCE" },
         commonComponentStyle : model.commonComponentStyle,
         suppressComponent : model.suppressComponent,
         filmstripImageLazyLoading: model.filmstripImageLazyLoading == "true"
      }
   };
   if (model.repositoryUrl != null)
   {
      documentList.options.repositoryUrl = model.repositoryUrl;
   }
   
   model.widgets = [docListToolbar, documentList];
}

widgets();
