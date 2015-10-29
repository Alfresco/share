/*
 * Custom Model Manager
 * Models imports
 * imports/cmm-models.lib.js
 * 
 * @author Kevin Roast
 * @author Richard Smith
 */

var models = {};

/**
 * Form for creating a model
 */
models.createForm = [
   {
      name: "alfresco/forms/controls/TextBox",
      config: {
         fieldId: "NAMESPACE",
         label: "cmm.model.namespace",
         name: "namespace",
         description: "cmm.model.namespace.info",
         additionalCssClasses: "create-form-namespace",
         requirementConfig: {
            initialValue: true
         },
         validationConfig: [
            validation.alphanumericURIMandatory,
            validation.maxlength255
         ]
      }
   },
   {
      name: "alfresco/forms/controls/TextBox",
      config: {
         fieldId: "PREFIX",
         label: "cmm.model.prefix",
         name: "prefix",
         description: "cmm.model.prefix.info",
         additionalCssClasses: "create-form-prefix",
         requirementConfig: {
            initialValue: true
         },
         validationConfig: [
            validation.alphanumericMandatory,
            validation.maxlength255
         ]
      }
   },
   {
      name: "alfresco/forms/controls/TextBox",
      config: {
         fieldId: "NAME",
         label: "cmm.model.name",
         name: "name",
         description: "cmm.model.name.info",
         additionalCssClasses: "create-form-name",
         requirementConfig: {
            initialValue: true
         },
         validationConfig: [
            validation.alphanumericMandatory,
            validation.maxlength255
         ]
      }
   },
   {
      name: "alfresco/forms/controls/TextBox",
      config: {
         fieldId: "AUTHOR",
         label: "cmm.model.author",
         name: "author",
         description: "cmm.model.author.info",
         additionalCssClasses: "create-form-author",
         validationConfig: [
            validation.maxlength255
         ]
      }
   },
   {
      name: "alfresco/forms/controls/TextArea",
      config: {
         fieldId: "DESCRIPTION",
         label: "cmm.model.description",
         name: "description",
         additionalCssClasses: "create-form-description",
         validationConfig: [
            validation.maxlength1024
         ]
      }
   }
];

models.importForm = [
   {
      name: "alfresco/forms/controls/FileSelect",
      config: {
         fieldId: "FILE",
         label: "cmm.model.select",
         name: "file",
         description: "cmm.model.import.info",
         additionalCssClasses: "import-form-select"
      }
   }
];

/**
 * Form for editing a model
 * Copy the create form and change the name field
 */
models.editForm = JSON.parse(JSON.stringify(models.createForm));

var nameFieldConfig = models.editForm[2].config;
nameFieldConfig._disabled = true;
nameFieldConfig.description = "cmm.model.name.no.edit";
nameFieldConfig.requirementConfig.initialValue = false;

/**
 * Menu for creating a model
 */
models.createMenu = {
   name: "alfresco/layout/LeftAndRight",
   config: {
      widgets: [
         {
            name: "alfresco/buttons/AlfButton",
            widthCalc: 220,
            config: {
               additionalCssClasses: "createButton",
               label: "cmm.create-button.model",
               publishTopic: "ALF_CREATE_FORM_DIALOG_REQUEST",
               publishPayload: {
                  dialogId: "CMM_CREATE_MODEL_DIALOG",
                  dialogTitle: "cmm.model.create-title",
                  dialogConfirmationButtonTitle: "cmm.button.create",
                  dialogCancellationButtonTitle: "cmm.button.cancel",
                  dialogCloseTopic: "CMM_CREATE_MODEL_SUCCESS",
                  additionalCssClasses: "create-form-dialog",
                  formSubmissionTopic: "CMM_CREATE_MODEL",
                  fixedWidth: true,
                  showValidationErrorsImmediately: false,
                  widgets: models.createForm
               }
            }
         },
         {
            name: "alfresco/buttons/AlfButton",
            widthCalc: 220,
            config: {
               additionalCssClasses: "importButton",
               label: "cmm.import-button.model",
               publishTopic: "ALF_CREATE_FORM_DIALOG_REQUEST",
               publishPayload: {
                  dialogId: "CMM_IMPORT_DIALOG",
                  dialogTitle: "cmm.model.import-title",
                  dialogConfirmationButtonTitle: "cmm.button.import",
                  dialogCancellationButtonTitle: "cmm.button.cancel",
                  dialogCloseTopic: "CMM_IMPORT_MODEL_SUCCESS",
                  additionalCssClasses: "import-form-dialog",
                  formSubmissionTopic: "CMM_IMPORT_MODEL",
                  fixedWidth: true,
                  widgets: models.importForm
               }
            }
         }
      ]
   }
};

/**
 * Actions menu for the model listing
 */
models.actionsMenu = {
   name: "alfresco/menus/AlfMenuBar",
   align: "left",
   config: {
      widgets: [
         {
            name: "alfresco/menus/AlfMenuBarPopup",
            config: {
               label: "cmm.header.actions",
               widgets: [
                  {
                     name: "alfresco/menus/AlfMenuGroup",
                     config: {
                        additionalCssClasses: "unmargined",
                        widgets: [
                           {
                              name: "alfresco/menus/AlfMenuItem",
                              config: {
                                 label: "cmm.button.activate",
                                 iconClass: "cmm-icon-activate",
                                 publishTopic: "CMM_ACTIVATE_MODEL",
                                 publishGlobal: true,
                                 publishPayloadType: "PROCESS",
                                 publishPayloadModifiers: ["processCurrentItemTokens"],
                                 publishPayload: {
                                    name: "{entry.name}"
                                 },
                                 renderFilter: [
                                    {
                                       property: "entry.status",
                                       values: ["DRAFT","DISABLED"],
                                       renderOnAbsentProperty: true
                                    }
                                 ]
                              }
                           },
                           {
                              name: "alfresco/menus/AlfMenuItem",
                              config: {
                                 label: "cmm.button.deactivate",
                                 iconClass: "cmm-icon-deactivate",
                                 publishTopic: "CMM_DEACTIVATE_MODEL",
                                 publishGlobal: true,
                                 publishPayloadType: "PROCESS",
                                 publishPayloadModifiers: ["processCurrentItemTokens"],
                                 publishPayload: {
                                    name: "{entry.name}"
                                 },
                                 renderFilter: [
                                    {
                                       property: "entry.status",
                                       values: ["ACTIVE"],
                                       renderOnAbsentProperty: true
                                    }
                                 ]
                              }
                           },
                           {
                              name: "alfresco/menus/AlfMenuItem",
                              config: {
                                 label: "cmm.button.edit",
                                 iconClass: "cmm-icon-edit",
                                 publishTopic: "ALF_CREATE_FORM_DIALOG_REQUEST",
                                 publishGlobal: true,
                                 publishPayloadType: "BUILD",
                                 publishPayload: {
                                    dialogId: "CMM_EDIT_MODEL_DIALOG",
                                    dialogTitle: "cmm.model.edit-title",
                                    dialogConfirmationButtonTitle: "cmm.button.save-changes",
                                    dialogCancellationButtonTitle: "cmm.button.cancel",
                                    dialogCloseTopic: "CMM_EDIT_MODEL_SUCCESS",
                                    additionalCssClasses: "edit-form-dialog",
                                    formSubmissionTopic: "CMM_EDIT_MODEL",
                                    fixedWidth: true,
                                    showValidationErrorsImmediately: false,
                                    widgets: models.editForm,
                                    formValue: {
                                       currentname: {
                                          alfType: "item",
                                          alfProperty: "entry.name"
                                       },
                                       namespace: {
                                          alfType: "item",
                                          alfProperty: "entry.namespaceUri"
                                       },
                                       prefix: {
                                          alfType: "item",
                                          alfProperty: "entry.namespacePrefix"
                                       },
                                       name: {
                                          alfType: "item",
                                          alfProperty: "entry.name"
                                       },
                                       author: {
                                          alfType: "item",
                                          alfProperty: "entry.author"
                                       },
                                       description: {
                                          alfType: "item",
                                          alfProperty: "entry.description"
                                       }
                                    }
                                 },
                                 renderFilter: [
                                    {
                                       property: "entry.status",
                                       values: ["DRAFT","DISABLED"],
                                       renderOnAbsentProperty: true
                                    }
                                 ]
                              }
                           },
                           {
                              name: "alfresco/menus/AlfMenuItem",
                              config: {
                                 label: "cmm.button.delete",
                                 iconClass: "cmm-icon-delete",
                                 publishTopic: "CMM_DELETE_MODEL",
                                 publishGlobal: true,
                                 publishPayloadType: "PROCESS",
                                 publishPayloadModifiers: ["processCurrentItemTokens"],
                                 publishPayload: {
                                    name: "{entry.name}"
                                 },
                                 renderFilter: [
                                    {
                                       property: "entry.status",
                                       values: ["DRAFT","DISABLED"],
                                       renderOnAbsentProperty: true
                                    }
                                 ]
                              }
                           },
                           {
                              name: "alfresco/menus/AlfMenuItem",
                              config: {
                                 label: "cmm.button.export",
                                 iconClass: "cmm-icon-export",
                                 publishTopic: "CMM_EXPORT_MODEL",
                                 publishGlobal: true,
                                 publishPayloadType: "PROCESS",
                                 publishPayloadModifiers: ["processCurrentItemTokens"],
                                 publishPayload: {
                                    name: "{entry.name}"
                                 }
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
};

/**
 * List layout for the model listing
 */
models.listLayout = {
   name: "alfresco/lists/views/AlfListView",
   config: {
      noItemsMessage: "cmm.message.no-models",
      sortField: "entry.name",
      additionalCssClasses: "bordered",
      widgetsForHeader: [
         {
            name: "alfresco/lists/views/layouts/HeaderCell",
            config: {
               label: "cmm.header.name",
               sortable: false,
               additionalCssClasses: "nameColumn smallpad"
            }
         },
         {
            name: "alfresco/lists/views/layouts/HeaderCell",
            config: {
               label: "cmm.header.namespace",
               sortable: false,
               additionalCssClasses: "namespaceColumn smallpad"
            }
         },
         {
            name: "alfresco/lists/views/layouts/HeaderCell",
            config: {
               label: "cmm.header.status",
               sortable: false,
               additionalCssClasses: "statusColumn smallpad"
            }
         },
         {
            name: "alfresco/lists/views/layouts/HeaderCell",
            config: {
               label: "cmm.header.actions",
               sortable: false,
               additionalCssClasses: "actionsColumn smallpad"
            }
         }
      ],
      widgets: [
         {
            name: "alfresco/lists/views/layouts/Row",
            config: {
               widgets: [
                  {
                     name: "alfresco/lists/views/layouts/Cell",
                     config: {
                        additionalCssClasses: "nameColumn smallpad",
                        widgets: [
                           {
                              name: "alfresco/renderers/PropertyLink",
                              config: {
                                 renderedValueClass: "alfresco-renderers-Property",
                                 propertyToRender: "entry.name",
                                 publishTopic: "CMM_DSP_TYPES_AND_PROPERTYGROUPS",
                                 publishGlobal: true,
                                 useCurrentItemAsPayload: false,
                                 publishPayloadType: "PROCESS",
                                 publishPayloadModifiers: ["processCurrentItemTokens"],
                                 publishPayload: {
                                    modelName: "{entry.name}"
                                 }
                              }
                           }
                        ]
                     }
                  },
                  {
                     name: "alfresco/lists/views/layouts/Cell",
                     config: {
                        additionalCssClasses: "namespaceColumn smallpad",
                        widgets: [
                           {
                              name: "alfresco/renderers/Property",
                              config: {
                                 renderedValueClass: "alfresco-renderers-Property",
                                 propertyToRender: "entry.namespaceUri"
                              }
                           }
                        ]
                     }
                  },
                  {
                     name: "alfresco/lists/views/layouts/Cell",
                     config: {
                        additionalCssClasses: "statusColumn smallpad",
                        widgets: [
                           {
                              name: "alfresco/renderers/Property",
                              config: {
                                 renderedValueClass: "status",
                                 propertyToRender: "entry.statusLabel",
                                 renderFilter: [
                                    {
                                       property: "entry.status",
                                       values: ["DRAFT","DISABLED"],
                                       renderOnAbsentProperty: false
                                    }
                                 ]
                              }
                           },
                           {
                              name: "alfresco/renderers/Property",
                              config: {
                                 renderedValueClass: "status active",
                                 propertyToRender: "entry.statusLabel",
                                 renderFilter: [
                                    {
                                       property: "entry.status",
                                       values: ["ACTIVE"],
                                       renderOnAbsentProperty: false
                                    }
                                 ]
                              }
                           }
                        ]
                     }
                  },
                  {
                     name: "alfresco/lists/views/layouts/Cell",
                     config: {
                        additionalCssClasses: "actionsColumn smallpad",
                        widgets: [
                           models.actionsMenu
                        ]
                     }
                  }
               ]
            }
         }
      ]
   }
};

/**
 * The model listing
 */
models.list = {
   id: "MODELS_LIST",
   name: "alfresco/lists/AlfList",
   config: {
      loadDataPublishTopic: "CMM_GET_MODELS",
      pubSubScope: "MODELS_LIST_",
      reloadDataTopic: "CMM_RELOAD_MODELS",
      itemsProperty: "list.entries",
      noDataMessage: msg.get("cmm.message.no-models-found"),
      dataFailureMessage: msg.get("cmm.message.error-loading-models"),
      widgets: [
         models.listLayout
      ]
   }
};

