// TODO: This currently only contains the model for the main content (i.e. it will work with the hybrid-template)
//       but ideally we should sort out all the lib files so that it runs in the full template...
//       /share/page/dp/ws/manage-sites as opposed to /share/page/hdp/ws/manage-sites



model.jsonModel = {
   services: [{
      name: "alfresco/services/LoggingService",
      config: {
         loggingPreferences: {
            enabled: true,
            all: true,
            warn: true,
            error: true
         }
      }
   },
   "alfresco/services/SiteService"],
   widgets: [
      {
         id: "SET_PAGE_TITLE",
         name: "alfresco/header/SetTitle",
         config: {
            title: msg.get("manage-site-members.page.title")
         }
      },
      {
         id: "SHARE_VERTICAL_LAYOUT",
         name: "alfresco/layout/VerticalWidgets",
         config: 
         {
            widgets: [
               {
                  name: "alfresco/layout/LeftAndRight",
                  config: {
                     widgets: [
                        {
                           name: "alfresco/forms/controls/DojoValidationTextBox",
                           align: "left",
                           config: {
                              label: ""
                           }
                        },
                        {
                           name: "alfresco/buttons/AlfButton",
                           align: "left",
                           config: {
                              label: "Filter"
                           }
                        },
                        {
                           name: "alfresco/buttons/AlfButton",
                           align: "right",
                           config: {
                              label: "Export"
                           }
                        },
                        {
                           name: "alfresco/buttons/AlfButton",
                           align: "right",
                           config: {
                              label: "Add User"
                           }
                        }
                     ]
                  }
               },
               {
                  id: "MANAGE_SITES_TOOLBAR",
                  name: "alfresco/documentlibrary/AlfToolbar",
                  config: {
                     id: "MANAGE_SITES_TOOLBAR",
                     widgets: [
                        {
                           id: "MANAGE_SITES_PAGINATION_MENU",
                           name: "alfresco/documentlibrary/AlfDocumentListPaginator",
                           align: "left"
                        }
                     ]
                  }
               },
               {
                  id: "DOCLIB_DOCUMENT_LIST",
                  name: "alfresco/documentlibrary/AlfSitesList",
                  config: {
                     useHash: false,
                     sortAscending: true,
                     sortField: "title",
                     usePagination: true,
                     dataRequestTopic: "ALF_GET_SITE_MEMBERSHIPS",
                     site: page.url.args["site"],
                     widgets: [
                        {
                           name: "alfresco/documentlibrary/views/AlfDocumentListView",
                           config: {
                              itemKey: "shortName",
                              widgetsForHeader: [
                                 {
                                    name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                    config: {
                                       label: "First Name",
                                       sortable: false,
                                       sortValue: ""
                                    }
                                 },
                                 {
                                    name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                    config: {
                                       label: "Last Name",
                                       sortable: false,
                                       sortValue: "title"
                                    }
                                 },
                                 {
                                    name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                    config: {
                                       label: "Site Role",
                                       sortable: false
                                    }
                                 },
                                 {
                                    name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                    config: {
                                       label: "Actions",
                                       sortable: false,
                                       sortValue: ""
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
                                                widgets: [
                                                   {
                                                      name: "alfresco/renderers/Property",
                                                      config: {
                                                         propertyToRender: "authority.firstName",
                                                         renderAsLink: false
                                                      }
                                                   }
                                                ]
                                             }
                                          },
                                          {
                                             name: "alfresco/documentlibrary/views/layouts/Cell",
                                             config: {
                                                widgets: [
                                                   {
                                                      name: "alfresco/renderers/Property",
                                                      config: {
                                                         propertyToRender: "authority.lastName",
                                                         renderAsLink: false
                                                      }
                                                   }
                                                ]
                                             }
                                          },
                                          {
                                             name: "alfresco/documentlibrary/views/layouts/Cell",
                                             config: {
                                                widgets: [
                                                   {
                                                      name: "alfresco/renderers/PublishingDropDownMenu",
                                                      config: {
                                                         publishTopic: "ALF_UPDATE_SITE_MEMBERSHIP",
                                                         propertyToRender: "role",
                                                         optionsConfig: {
                                                            fixed: [
                                                               {label: "Manager", value: "SiteManager"},
                                                               {label: "Collaborator", value: "SiteCollaborator"},
                                                               {label: "Contributor", value: "SiteContributor"},
                                                               {label: "Consumer", value: "SiteConsumer"}
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
                                                widgets: [
                                                   {
                                                      name: "alfresco/renderers/Actions",
                                                      config: {
                                                         customActions: [
                                                            {
                                                               label: "Remove",
                                                               icon : "document-delete",
                                                               index: "10",
                                                               publishTopic : "",
                                                               type: "javascript"
                                                            },
                                                            {
                                                               label: "Manage User's Sites",
                                                               icon : "document-delete",
                                                               index: "20",
                                                               publishTopic : "",
                                                               type: "javascript"
                                                            },
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
};
