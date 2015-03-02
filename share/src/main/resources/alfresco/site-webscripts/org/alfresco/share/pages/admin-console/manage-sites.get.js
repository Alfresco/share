// Ideally we would want the SiteService to subscribe to global topics, but because it is used within
// the AdminConsole we need to render the page as a "hybrid" of multiple Components. The header component
// has it's own SiteService so we need to scope this one in order to prevent duplicate HTTP requests from
// occurring. It is not possible to simply omit this SiteService and rely on the one provided by the
// share-header.get WebScript as race conditions come into play...

// Get the user data (this replicates a function in share-header.lib.js which ideally we wouldn't do,
// however, this is required due to the limitation of this being part of the Admin Console and the lib
// not being imported)...
var userData = {};
var groups = user.properties["alfUserGroups"];
if (groups != null)
{
   groups = groups.split(",");
   var processedGroups = {};
   for (var i=0; i<groups.length; i++)
   {
      processedGroups[groups[i]] = true;
   }
   userData.groups = processedGroups;
}
userData.isNetworkAdmin = user.properties["isNetworkAdmin"];
userData.isAdmin = user.capabilities["isAdmin"];

var siteServiceScope = "MANAGE_SITES_SITE_SERVICE_";

model.jsonModel = {
   services: [
      {
         name: "alfresco/services/SiteService",
         config: {
            pubSubScope: siteServiceScope
         }
      },
      {
         name: "alfresco/dialogs/AlfDialogService",
         config: {
            pubSubScope: siteServiceScope
         }
      }
   ],
   widgets: [
      {
         id: "SET_PAGE_TITLE",
         name: "alfresco/header/SetTitle",
         config: {
            title: msg.get("manage-sites.page.title")
         }
      },
      {
         id: "SHARE_VERTICAL_LAYOUT",
         name: "alfresco/layout/VerticalWidgets",
         config:
         {
            pubSubScope: siteServiceScope,
            currentItem: {
               user: userData
            },
            widgets: [
               {
                  id: "DOCLIB_DOCUMENT_LIST",
                  name: "alfresco/documentlibrary/AlfSitesList",
                  config: {
                     useHash: false,
                     sortAscending: true,
                     sortField: "title",
                     usePagination: true,
                     dataRequestTopic: "ALF_GET_SITES_ADMIN",
                     renderFilterMethod: "ANY",
                     renderFilter: [
                        {
                           property: "user.groups.GROUP_ALFRESCO_ADMINISTRATORS",
                           values: [true]
                        },
                        {
                           property: "user.groups.GROUP_SITE_ADMINISTRATORS",
                           values: [true]
                        },
                        {
                           property: "user.isAdmin",
                           values: [true]
                        },
                        {
                           property: "user.isNetworkAdmin",
                           values: [true]
                        }
                     ],
                     widgets: [
                        {
                           name: "alfresco/documentlibrary/views/AlfDocumentListView",
                           config: {
                              additionalCssClasses: "bordered",
                              noItemsMessage: msg.get("message.no-sites"),
                              itemKey: "shortName",
                              widgetsForHeader: [
                                 {
                                    name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                    config: {
                                       id: "titleTableHeader",
                                       label: msg.get("message.site-name-header-label"),
                                       sortable: false
                                    }
                                 },
                                 {
                                    name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                    config: {
                                       id: "descriptionTableHeader",
                                       label: msg.get("message.site-description-header-label"),
                                       sortable: false
                                    }
                                 },
                                 {
                                    name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                    config: {
                                       label: msg.get("message.visibility-header-label")
                                    }
                                 },
                                 {
                                    name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                    config: {
                                       label: msg.get("message.manager-header-label")
                                    }
                                 },
                                 {
                                    name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                    config: {
                                       label: msg.get("message.actions-header-label")
                                    }
                                 }
                              ],
                              widgets: [
                                 {
                                    name: "alfresco/documentlibrary/views/layouts/Row",
                                    config: {
                                       widgets: [
                                          {
                                             name: "alfresco/documentlibrary/views/layouts/Cell",
                                             config: {
                                                additionalCssClasses: "siteName mediumpad",
                                                widgets: [
                                                   {
                                                      name: "alfresco/renderers/PropertyLink",
                                                      config: {
                                                         renderedValueClass: "alfresco-renderers-Property pointer",
                                                         publishGlobal: true,
                                                         propertyToRender: "title",
                                                         publishTopic: "ALF_NAVIGATE_TO_PAGE",
                                                         useCurrentItemAsPayload: false,
                                                         publishPayloadType: "PROCESS",
                                                         publishPayloadModifiers: ["processCurrentItemTokens"],
                                                         publishPayload: {
                                                            url: "site/{shortName}/site-members",
                                                            type: "SHARE_PAGE_RELATIVE"
                                                         }
                                                      }
                                                   }
                                                ]
                                             }
                                          },
                                          {
                                             name: "alfresco/documentlibrary/views/layouts/Cell",
                                             config: {
                                                additionalCssClasses: "siteDescription mediumpad",
                                                widgets: [
                                                   {
                                                      name: "alfresco/renderers/Property",
                                                      config: {
                                                         propertyToRender: "description",
                                                         renderAsLink: false
                                                      }
                                                   }
                                                ]
                                             }
                                          },
                                          {
                                             name: "alfresco/documentlibrary/views/layouts/Cell",
                                             config: {
                                                additionalCssClasses: "visibility smallpad",
                                                widgets: [
                                                   {
                                                      name: "alfresco/renderers/PublishingDropDownMenu",
                                                      config: {
                                                         additionalCssClasses: "unmargined no-title",
                                                         publishTopic: "ALF_UPDATE_SITE_DETAILS",
                                                         publishPayloadType: "BUILD",
                                                         publishPayload: {
                                                            shortName: {
                                                               alfType: "item",
                                                               alfProperty: "shortName"
                                                            },
                                                            visibility: {
                                                               alfType: "payload",
                                                               alfProperty: "value"
                                                            }
                                                         },
                                                         propertyToRender: "visibility",
                                                         optionsConfig: {
                                                            fixed: [
                                                               {label: msg.get("message.site-visibility-dropdown-public-label"), value: "PUBLIC"},
                                                               {label: msg.get("message.site-visibility-dropdown-moderated-label"), value: "MODERATED"},
                                                               {label: msg.get("message.site-visibility-dropdown-private-label"), value: "PRIVATE"}
                                                            ]
                                                         }
                                                      }
                                                   }
                                                ]
                                             }
                                          },
                                          {
                                             name: "alfresco/documentlibrary/views/layouts/Cell",
                                             config: {
                                                additionalCssClasses: "siteManager mediumpad",
                                                widgets: [
                                                   {
                                                      name: "alfresco/renderers/Boolean",
                                                      config: {
                                                         propertyToRender: "userIsSiteManager",
                                                         renderAsLink: false
                                                      }
                                                   }
                                                ]
                                             }
                                          },
                                          {
                                             name: "alfresco/documentlibrary/views/layouts/Cell",
                                             config: {
                                                additionalCssClasses: "actions smallpad",
                                                widgets: [
                                                   {
                                                      name: "alfresco/menus/AlfMenuBar",
                                                      align: "left",
                                                      config: {
                                                         widgets: [
                                                            {
                                                               name: "alfresco/menus/AlfMenuBarPopup",
                                                               config: {
                                                                  label: msg.get("message.actions-header-label"),
                                                                  widgets: [
                                                                     {
                                                                        name: "alfresco/menus/AlfMenuGroup",
                                                                        config: {
                                                                           additionalCssClasses: "unmargined",
                                                                           widgets: [
                                                                              {
                                                                                 name: "alfresco/menus/AlfMenuItem",
                                                                                 config: {
                                                                                    label: msg.get("button.site-delete.label"),
                                                                                    iconClass: "alf-delete-icon",
                                                                                    publishTopic: "ALF_DELETE_SITE"
                                                                                 }
                                                                              },
                                                                              {
                                                                                 name: "alfresco/menus/AlfMenuItem",
                                                                                 config: {
                                                                                    label: msg.get("button.site-manage.label"),
                                                                                    iconClass: "alf-password-icon",
                                                                                    publishTopic: "ALF_BECOME_SITE_MANAGER",
                                                                                    publishPayloadType: "BUILD",
                                                                                    publishPayload: {
                                                                                       site: {
                                                                                          alfType: "item",
                                                                                          alfProperty: "shortName"
                                                                                       }
                                                                                    },
                                                                                    renderFilter: [
                                                                                       {
                                                                                          property: "userIsSiteManager",
                                                                                          values: ["false"],
                                                                                          renderOnAbsentProperty: true
                                                                                       }
                                                                                    ]
                                                                                 }
                                                                              }
                                                                           ]
                                                                        }
                                                                     }
                                                                  ]
                                                               }
                                                            }
                                                         ]
                                                      }
                                                   }
                                                ]
                                             }
                                          }
                                       ]
                                    }
                                 }
                              ]
                           }
                        }
                     ]
                  }
               },
               {
                  name: "alfresco/layout/CenteredWidgets",
                  config: {
                     pubSubScope: siteServiceScope,
                     widgets: [
                        {
                           id: "DOCLIB_PAGINATION_MENU",
                           name: "alfresco/documentlibrary/AlfDocumentListPaginator",
                           widthCalc: 430
                        }
                     ]
                  }
               }
            ]
         }
      }
   ]
};