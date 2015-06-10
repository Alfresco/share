// YUI Widget instantiation metadata...
var addedUsersListComponent = {
   id : "AddedUsersList", 
   name : "Alfresco.AddedUsersList",
   options : {
      siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : ""
   }
};
model.widgets = [addedUsersListComponent];

// Aikau Widget Configuration
model.jsonModel = {
   rootNodeId: args.htmlid + "-added-users-list-content",
   services: [
      "share-components/invite/AddedUsersService",
      "alfresco/services/LoggingService"
   ]
};

var views = [
   {
      name: "alfresco/lists/views/AlfListView",
      config: {
         subscribeToDocRequests: true,
         documentSubscriptionTopic: "ADDED_USERS_LIST_TOPIC",
         itemsProperty: "items",
         widgets: [
            {
               name: "alfresco/lists/views/layouts/Row",
               config: {
                  widgets: [
                     {
                        name: "alfresco/lists/views/layouts/Cell",
                        config: {
                           additionalCssClasses: "yui-dt-liner",
                           widgets: [
                              {
                                 name: "alfresco/renderers/Property",
                                 config: {
                                    propertyToRender: "displayName",
                                    renderOnNewLine: true
                                 }
                              },
                              {
                                 name: "alfresco/renderers/Property",
                                 config: {
                                    propertyToRender: "roleName",
                                    renderOnNewLine: true
                                 }
                              }
                           ]
                        }
                     },
                     {
                        name: "alfresco/lists/views/layouts/Cell",
                        config: {
                           additionalCssClasses: "greenCheck yui-dt-liner"
                        }
                     }
                  ]
               }
            }
         ]
      }
   }
];

model.jsonModel.widgets = views;