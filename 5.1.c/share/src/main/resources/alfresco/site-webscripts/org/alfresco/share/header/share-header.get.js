<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-header.lib.js">

model.jsonModel = {
   rootNodeId: "share-header",
   services: getHeaderServices(),
   widgets: [
      {
         id: "SHARE_VERTICAL_LAYOUT",
         name: "alfresco/layout/VerticalWidgets",
         config: 
         {
            widgets: getHeaderModel()
         }
      }
   ]
};
