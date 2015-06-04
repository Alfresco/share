model.jsonModel = {
   services: [
      "share/services/AddedUsersService",
      "alfresco/services/LoggingService"
   ]
};
//
//var list = {
//   name: "alfresco/lists/AlfList",
//   config: {
//      currentData: {
//            items: [
//                 {
//                    userName: "aowian",
//                    roleName: "role.SiteCollaborator",
//                    displayName: "Ahmed Owian"
//                 },
//                 {
//                    userName: "aowian",
//                    roleName: "role.SiteCollaborator",
//                    displayName: "Ahmed Owian"
//                 }
//            ]
//         },
//      loadDataPublishTopic: "ADDED_USERS_LIST_TOPIC"
////      loadDataPublishPayload: {
////         url: "slingshot/datalists/lists/site/datalistexample/dataLists"
////      },
////      itemsProperty: "datalists"
//   }
//};

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
//                     {
//                        name: "alfresco/lists/views/layouts/Cell",
//                        config: {
//                           widgets: [
//                              {
//                                 name: "alfresco/renderers/AvatarThumbnail",
//                                 config: {
//                                    userNameProperty: "userName",
//                                    imageTitleProperty: "displayName"
//                                 }
//                              }
//                           ]
//                        }
//                     },
                     {
                        name: "alfresco/lists/views/layouts/Cell",
                        config: {
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
                           widgets: [
                              
                           ]
                        }
                     }
                  ]
               }
            }
         ]
      }
   }
];
//list.config.widgets = views;
model.jsonModel.widgets = views;