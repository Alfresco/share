<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/widget-palette.lib.js">

// Make an attempt to load a previously saved page if provided on the args...
var publishOnReady = null,
    services = null,
    widgets = null,
    pageName = "";

try
{
   if (url.args["page"] != null)
   {
      var response = remote.call("/remote-share/pages/name/" + url.args["page"]);
      if (response.status == 200)
      {
         var json = JSON.parse(response);
         var page = json.items[0].content;
         var pageData = JSON.parse(page);
         publishOnReady = pageData.publishOnReadyEditorConfig;
         services = pageData.servicesEditorConfig;
         widgets = pageData.widgetsEditorConfig;
         pageName = url.args["page"];
      }
   }
}
catch(e)
{
   // Could not get page data for editing. Proceed with page creation as a fallback.
}

model.jsonModel = {
   services: [
      "alfresco/services/PageService",
      "alfresco/services/OptionsService",
      {
         name: "alfresco/services/NavigationService",
         config: {
            subscriptions: [
               {
                  topic: "ALF_CREATE_PAGE_DEFINITION_SUCCESS",
                  type: "SHARE_PAGE_RELATIVE",
                  url: "user/{{userid}}/dashboard",
                  target: "CURRENT"
               }
            ]
         }
      }
   ],
   widgets: [
      {
         name: "alfresco/header/SetTitle",
         config: {
            title: "Page Creator"
         }
      },
      {
         name: "alfresco/layout/VerticalWidgets",
         config: {
            renderFilter: [
               {
                  target: "groupMemberships",
                  property: "GROUP_ALFRESCO_ADMINISTRATORS",
                  renderOnAbsentProperty: true,
                  values: [false]
               }
            ],
            widgets: [
               {
                  name: "alfresco/header/Warning",
                  config: {
                     warnings: [
                        {
                           message: "Admin permissions required",
                           level: 3
                        }
                     ]
                  }
               }
            ]
         }
      },
      {
         name: "alfresco/layout/VerticalWidgets",
         config: {
            renderFilter: [
               {
                  target: "groupMemberships",
                  property: "GROUP_ALFRESCO_ADMINISTRATORS",
                  values: [true]
               }
            ],
            generatePubSubScope: true,
            widgets: [
               {
                  name: "alfresco/layout/ClassicWindow",
                  config: {
                     title: "Preview",
                     widgets: [
                        {
                           name: "alfresco/prototyping/Preview"
                        }
                     ]
                  }
               },
               {
                  name: "alfresco/layout/SlideOverlay",
                  config: {
                     showTopics: ["ALF_CONFIGURE_WIDGET"],
                     hideTopics: ["ALF_UPDATE_RENDERED_WIDGET","ALF_CLEAR_CONFIGURE_WIDGET"],
                     adjustHeightTopics: ["ALF_CONFIGURE_WIDGET"],
                     widgets: [
                        {
                           name: "alfresco/layout/HorizontalWidgets",
                           align: "underlay",
                           assignTo: "layoutWidget",
                           config: {
                              widgets: [
                                 {
                                    name: "alfresco/layout/ClassicWindow",
                                    config: {
                                       title: "Page Details",
                                       widgets: [
                                          {
                                             name: "alfresco/forms/Form",
                                             config: {
                                                okButtonLabel: "Save",
                                                okButtonPublishTopic: (pageName == "") ? "ALF_CREATE_PAGE_DEFINITION": "ALF_UPDATE_PAGE_DEFINITION",
                                                okButtonPublishGlobal: true,
                                                widgets: [
                                                   {
                                                      name: "alfresco/forms/controls/DojoValidationTextBox",
                                                      config: {
                                                         name: "pageName",
                                                         label: "Page Name",
                                                         description: "The name to use when referencing the page",
                                                         value: pageName,
                                                         requirementConfig: {
                                                            initialValue: true
                                                         }
                                                      }
                                                   },
                                                   {
                                                      name: "alfresco/forms/controls/DropZoneControl",
                                                      assignTo: "previewWidget",
                                                      config: {
                                                         label: "Page Load Publications",
                                                         name: "publishOnReady",
                                                         description: "Drag and drop items from the 'Publications' palette here. These are 'data payloads' that are published when the page first loads. They can be used to trigger initial events such as requesting data.",
                                                         acceptTypes: ["publication"],
                                                         value: publishOnReady
                                                      }
                                                   },
                                                   {
                                                      name: "alfresco/forms/controls/DropZoneControl",
                                                      assignTo: "previewWidget",
                                                      config: {
                                                         label: "Services",
                                                         name: "services",
                                                         description: "Drag and drop items from the 'Services' palette here. These can either act as an interface between the client-side widgets and the server-side services (e.g. for retrieving or saving data) or to provide client-side services such as logging or notifications",
                                                         acceptTypes: ["service"],
                                                         value: services
                                                      }
                                                   },
                                                   {
                                                      name: "alfresco/forms/controls/DropZoneControl",
                                                      assignTo: "previewWidget",
                                                      config: {
                                                         label: "Widgets",
                                                         name: "widgets",
                                                         description: "Drag and drop widget items here to build the user-interface for the page.",
                                                         value: widgets,
                                                         acceptTypes: ["widget"]
                                                      }
                                                   }
                                                ],
                                                widgetsAdditionalButtons: [
                                                   {
                                                      name: "alfresco/buttons/AlfButton",
                                                      config: {
                                                         label: "Preview",
                                                         publishTopic: "ALF_GENERATE_PAGE_PREVIEW"
                                                      }
                                                   },
                                                   {
                                                      name: "alfresco/buttons/AlfButton",
                                                      config: {
                                                         label: "WebScript Controller Export",
                                                         publishTopic: "ALF_EXPORT_PAGE_DEFINITION",
                                                         publishGlobal: true
                                                      }
                                                   }
                                                ]
                                             }
                                          },
                                       ]
                                    }
                                 },
                                 {
                                    name: "alfresco/layout/ClassicWindow",
                                    config: {
                                       title: "Widget Palette",
                                       widgets: [
                                          {
                                             name: "alfresco/layout/AlfAccordionContainer",
                                             config: {
                                                widgets: [
                                                   {
                                                      title: "Publications",
                                                      name: "alfresco/creation/DragPalette",
                                                      config: {
                                                         widgetsForPalette: getAllPublications()
                                                      }
                                                   },
                                                   {
                                                      title: "Services",
                                                      name: "alfresco/creation/DragPalette",
                                                      config: {
                                                         widgetsForPalette: getAllServices()
                                                      }
                                                   },
                                                   {
                                                      title: "General Widgets",
                                                      name: "alfresco/creation/DragPalette",
                                                      config: {
                                                         widgetsForPalette: getGeneralWidgets()
                                                      }
                                                   },
                                                   {
                                                      title: "Document List Widgets",
                                                      name: "alfresco/creation/DragPalette",
                                                      config: {
                                                         widgetsForPalette: getAllDocListWidgets()
                                                      }
                                                   },
                                                   {
                                                      title: "Menu Widgets",
                                                      name: "alfresco/creation/DragPalette",
                                                      config: {
                                                         widgetsForPalette: getAllMenuWidgets()
                                                      }
                                                   },
                                                   {
                                                      title: "Layout Widgets",
                                                      name: "alfresco/creation/DragPalette",
                                                      config: {
                                                         widgetsForPalette: getAllLayoutWidgets()
                                                      }
                                                   },
                                                   {
                                                      title: "Form Widgets",
                                                      name: "alfresco/creation/DragPalette",
                                                      config: {
                                                         widgetsForPalette: getAllFormWidgets()
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
                           name: "alfresco/creation/WidgetConfig",
                           align: "overlay",
                           assignTo: "configWidget",
                           config: {
                              width: "50%"
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
model.jsonModel.groupMemberships = user.properties["alfUserGroups"];