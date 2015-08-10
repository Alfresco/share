/*
 * Custom Model Manager
 * Types and propertygroup imports
 * imports/cmm-types-propertygroups.lib.js
 * 
 * @author Kevin Roast
 * @author Richard Smith
 */

var typePropertyGroups = {};

/**
 * Form for creating a type
 */
typePropertyGroups.createTypeForm = [
   {
      name: "alfresco/forms/controls/TextBox",
      config: {
         fieldId: "NAME",
         /* TODO: namespace should be shown as part of label or simply implied from parent? e.g. cm:[blah] */
         label: "cmm.model.name",
         name: "name",
         description: "cmm.type.name.info",
         additionalCssClasses: "create-type-name",
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
      name: "alfresco/forms/controls/Select",
      config: {
         fieldId: "PARENT",
         label: "cmm.model.parent.type",
         name: "parent",
         description: "cmm.type.parent.info",
         additionalCssClasses: "create-type-parent",
         value: "cm:content", // default to cm:content
         optionsConfig: {
            publishTopic: "CMM_GET_PARENT_TYPES"
         }
      }
   },
   {
      name: "alfresco/forms/controls/TextBox",
      config: {
         fieldId: "TITLE",
         label: "cmm.model.title",
         name: "title",
         description: "cmm.type.title.info",
         additionalCssClasses: "create-type-title",
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
         additionalCssClasses: "create-type-description",
         validationConfig: [
            validation.maxlength1024
         ]
      }
   }
];

/**
 * Form for editing a type
 * Copy the create form and change the name field
 */
typePropertyGroups.editTypeForm = JSON.parse(JSON.stringify(typePropertyGroups.createTypeForm));
var nameFieldConfig = typePropertyGroups.editTypeForm[0].config;
nameFieldConfig._disabled = true;
nameFieldConfig.description = "cmm.type.name.no.edit";
nameFieldConfig.requirementConfig.initialValue = false;
typePropertyGroups.editTypeForm[4] = {
   name: "alfresco/forms/controls/HiddenValue",
   config: {
      fieldId: "MODEL_NAME",
      label: "cmm.model.name",
      name: "model_name",
      postWhenHiddenOrDisabled: true
   }
};

/**
 * Form for editing an active type
 * Copy the edit form and change the parent type field
 */
typePropertyGroups.editActiveTypeForm = JSON.parse(JSON.stringify(typePropertyGroups.editTypeForm));

var parentTypeFieldConfig = typePropertyGroups.editActiveTypeForm[1].config;
parentTypeFieldConfig._disabled = true;
parentTypeFieldConfig.description = "cmm.type.parent.no.edit";

/**
 * Form for creating a property group
 */
typePropertyGroups.createPropertyGroupForm = [
   {
      name: "alfresco/forms/controls/TextBox",
      config: {
         fieldId: "NAME",
         /* TODO: namespace should be shown as part of label or simply implied from parent? e.g. cm:[blah] */
         label: "cmm.model.name",
         name: "name",
         description: "cmm.propertygroup.name.info",
         additionalCssClasses: "create-propertygroup-name",
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
      name: "alfresco/forms/controls/Select",
      config: {
         fieldId: "PARENT",
         label: "cmm.model.parent.propertygroup",
         name: "parent",
         description: "cmm.propertygroup.parent.info",
         additionalCssClasses: "create-propertygroup-parent",
         value: "",
         optionsConfig: {
            publishTopic: "CMM_GET_PARENT_PROPERTYGROUPS"
         }
      }
   },
   {
      name: "alfresco/forms/controls/TextBox",
      config: {
         fieldId: "TITLE",
         label: "cmm.model.title",
         name: "title",
         description: "cmm.propertygroup.title.info",
         additionalCssClasses: "create-propertygroup-title",
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
         additionalCssClasses: "create-propertygroup-description",
         validationConfig: [
            validation.maxlength1024
         ]
      }
   }
];

/**
 * Form for editing a property group
 * Copy the create form and change the name field
 */
typePropertyGroups.editPropertyGroupForm = JSON.parse(JSON.stringify(typePropertyGroups.createPropertyGroupForm));
typePropertyGroups.editPropertyGroupForm[4] = {
   name: "alfresco/forms/controls/HiddenValue",
   config: {
      fieldId: "MODEL_NAME",
      label: "cmm.model.name",
      name: "model_name",
      postWhenHiddenOrDisabled: true
   }
};
var propertyGroupNameFieldConfig = typePropertyGroups.editPropertyGroupForm[0].config;
propertyGroupNameFieldConfig._disabled = true;
propertyGroupNameFieldConfig.description = "cmm.propertygroup.name.no.edit";
propertyGroupNameFieldConfig.requirementConfig.initialValue = false;

/**
 * Form for editing an active property group
 * Copy the edit form and change the parent property group field
 */
typePropertyGroups.editActivePropertyGroupForm = JSON.parse(JSON.stringify(typePropertyGroups.editPropertyGroupForm));

var propertyGroupParentPropertyGroupFieldConfig = typePropertyGroups.editActivePropertyGroupForm[1].config;
propertyGroupParentPropertyGroupFieldConfig._disabled = true;
propertyGroupParentPropertyGroupFieldConfig.description = "cmm.propertygroup.parent.no.edit";

/**
 * Menu for creating a type or property group
 */
typePropertyGroups.createMenu = {
   name: "alfresco/layout/LeftAndRight",
   config: {
      widgets: [
         {
            name: "alfresco/buttons/AlfButton",
            config: {
               additionalCssClasses: "backButton backToModels",
               label: "cmm.button.back-to-models",
               publishTopic: "CMM_DSP_MODELS"
            }
         },
         // Use AlfDynamicPayloadButtons so we can retrieve data from the hash string which is set when the 
         // current item is selected from the outer Custom Models list. We set into the formSubmissionPayloadMixin 
         // object as that mixes into the resulting form dialog payload.
         {
            name: "alfresco/buttons/AlfDynamicPayloadButton",
            config: {
               additionalCssClasses: "createTypeButton",
               label: "cmm.create-button.type",
               publishTopic: "ALF_CREATE_FORM_DIALOG_REQUEST",
               publishPayload: {
                  dialogId: "CMM_CREATE_TYPE_DIALOG",
                  dialogTitle: "cmm.model.create-type-title",
                  dialogConfirmationButtonTitle: "cmm.button.create",
                  dialogCancellationButtonTitle: "cmm.button.cancel",
                  dialogCloseTopic: "CMM_CREATE_CUSTOMTYPE_SUCCESS",
                  formSubmissionTopic: "CMM_CREATE_CUSTOMTYPE",
                  fixedWidth: true,
                  showValidationErrorsImmediately: false,
                  widgets: typePropertyGroups.createTypeForm
               },
               useHash: true,
               hashDataMapping: {
                  "model": "formSubmissionPayloadMixin.model_name"
               }
            }
         },
         {
            name: "alfresco/buttons/AlfDynamicPayloadButton",
            config: {
               additionalCssClasses: "createPropertyGroupButton",
               label: "cmm.create-button.propertygroup",
               publishTopic: "ALF_CREATE_FORM_DIALOG_REQUEST",
               publishPayload: {
                  dialogId: "CMM_CREATE_PROPERTYGROUP_DIALOG",
                  dialogTitle: "cmm.model.create-propertygroup-title",
                  dialogConfirmationButtonTitle: "cmm.button.create",
                  dialogCancellationButtonTitle: "cmm.button.cancel",
                  dialogCloseTopic: "CMM_CREATE_PROPERTYGROUP_SUCCESS",
                  formSubmissionTopic: "CMM_CREATE_PROPERTYGROUP",
                  fixedWidth: true,
                  showValidationErrorsImmediately: false,
                  widgets: typePropertyGroups.createPropertyGroupForm
               },
               useHash: true,
               hashDataMapping: {
                  "model": "formSubmissionPayloadMixin.model_name"
               }
            }
         }
      ]
   }
};

/**
 * Edit form values
 */
typePropertyGroups.typeFormValue = {
   model_name: {
      alfType: "item",
      alfProperty: "modelName"
   },
   name: {
      alfType: "item",
      alfProperty: "entry.name"
   },
   parent: {
      alfType: "item",
      alfProperty: "entry.parentName"
   },
   title: {
      alfType: "item",
      alfProperty: "entry.title"
   },
   description: {
      alfType: "item",
      alfProperty: "entry.description"
   }
};

typePropertyGroups.propertyGroupFormValue = {
   model_name: {
      alfType: "item",
      alfProperty: "modelName"
   },
   name: {
      alfType: "item",
      alfProperty: "entry.name"
   },
   parent: {
      alfType: "item",
      alfProperty: "entry.parentName"
   },
   title: {
      alfType: "item",
      alfProperty: "entry.title"
   },
   description: {
      alfType: "item",
      alfProperty: "entry.description"
   }
};

/**
 * Actions menu for the type listing
 */
typePropertyGroups.typeActionsMenu = {
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
                                 label: "cmm.button.form-editor",
                                 iconClass: "cmm-icon-edit-form",
                                 publishTopic: "CMM_DSP_FORM_EDITOR",
                                 publishGlobal: true,
                                 useCurrentItemAsPayload: false,
                                 publishPayloadType: "PROCESS",
                                 publishPayloadModifiers: ["processCurrentItemTokens"],
                                 // augmented data value see CMMService.getTypes()
                                 publishPayload: {
                                    modelName: "{modelName}",
                                    type: "{entry.name}"
                                 }
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
                                    dialogId: "CMM_EDIT_TYPE_DIALOG",
                                    dialogTitle: "cmm.model.edit-type-title",
                                    dialogConfirmationButtonTitle: "cmm.button.save-changes",
                                    dialogCancellationButtonTitle: "cmm.button.cancel",
                                    dialogCloseTopic: "CMM_EDIT_CUSTOMTYPE_SUCCESS",
                                    additionalCssClasses: "edit-form-dialog",
                                    formSubmissionTopic: "CMM_EDIT_CUSTOMTYPE",
                                    fixedWidth: true,
                                    showValidationErrorsImmediately: false,
                                    widgets: typePropertyGroups.editTypeForm,
                                    formValue: typePropertyGroups.typeFormValue
                                 },
                                 renderFilter: [
                                    {
                                       property: "status",
                                       values: ["DRAFT","DISABLED"],
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
                                    dialogId: "CMM_EDIT_TYPE_DIALOG",
                                    dialogTitle: "cmm.model.edit-type-title",
                                    dialogConfirmationButtonTitle: "cmm.button.save-changes",
                                    dialogCancellationButtonTitle: "cmm.button.cancel",
                                    dialogCloseTopic: "CMM_EDIT_CUSTOMTYPE_SUCCESS",
                                    additionalCssClasses: "edit-form-dialog",
                                    formSubmissionTopic: "CMM_EDIT_CUSTOMTYPE",
                                    fixedWidth: true,
                                    widgets: typePropertyGroups.editActiveTypeForm,
                                    formValue: typePropertyGroups.typeFormValue
                                 },
                                 renderFilter: [
                                    {
                                       property: "status",
                                       values: ["ACTIVE"],
                                       renderOnAbsentProperty: false
                                    }
                                 ]
                              }
                           },
                           {
                              name: "alfresco/menus/AlfMenuItem",
                              config: {
                                 label: "cmm.button.delete",
                                 iconClass: "cmm-icon-delete",
                                 publishTopic: "CMM_DELETE_TYPE",
                                 publishGlobal: true,
                                 publishPayloadType: "PROCESS",
                                 publishPayloadModifiers: ["processCurrentItemTokens"],
                                 publishPayload: {
                                    modelName: "{modelName}",
                                    name: "{entry.name}"
                                 }
                              }
                           },
                           {
                              name: "alfresco/menus/AlfMenuItem",
                              config: {
                                 label: "cmm.button.find-instances",
                                 iconClass: "cmm-icon-search",
                                 publishTopic: "CMM_FIND_TYPES",
                                 publishGlobal: true,
                                 publishPayloadType: "PROCESS",
                                 publishPayloadModifiers: ["processCurrentItemTokens"],
                                 publishPayload: {
                                    name: "{entry.prefixedName}"
                                 },
                                 renderFilter: [
                                    {
                                       property: "status",
                                       values: ["ACTIVE"],
                                       renderOnAbsentProperty: false
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
};

/**
 * List layout for the type listing
 */
typePropertyGroups.typeListLayout = {
   name: "alfresco/lists/views/AlfListView",
   config: {
      additionalCssClasses: "bordered",
      noItemsMessage: "cmm.message.no-types",
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
               label: "cmm.header.display-label",
               sortable: false,
               additionalCssClasses: "displayLabelColumn smallpad"
            }
         },
         {
            name: "alfresco/lists/views/layouts/HeaderCell",
            config: {
               label: "cmm.header.parent",
               sortable: false,
               additionalCssClasses: "parentColumn smallpad"
            }
         },
         {
            name: "alfresco/lists/views/layouts/HeaderCell",
            config: {
               label: "cmm.header.layout",
               sortable: false,
               additionalCssClasses: "layoutColumn smallpad"
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
                                 propertyToRender: "entry.prefixedName",
                                 publishTopic: "CMM_DSP_PROPERTIES",
                                 publishGlobal: true,
                                 useCurrentItemAsPayload: false,
                                 publishPayloadType: "PROCESS",
                                 publishPayloadModifiers: ["processCurrentItemTokens"],
                                 // augmented data value see CMMService.getTypes()
                                 publishPayload: {
                                    modelName: "{modelName}",
                                    type: "{entry.name}"
                                 }
                              }
                           }
                        ]
                     }
                  },
                  {
                     name: "alfresco/lists/views/layouts/Cell",
                     config: {
                        additionalCssClasses: "displayLabelColumn smallpad",
                        widgets: [
                           {
                              name: "alfresco/renderers/Property",
                              config: {
                                 renderedValueClass: "alfresco-renderers-Property",
                                 propertyToRender: "entry.title"
                              }
                           }
                        ]
                     }
                  },
                  {
                     name: "alfresco/lists/views/layouts/Cell",
                     config: {
                        additionalCssClasses: "parentColumn smallpad",
                        widgets: [
                           {
                              name: "alfresco/renderers/Property",
                              config: {
                                 renderedValueClass: "alfresco-renderers-Property",
                                 propertyToRender: "entry.parentName"
                              }
                           }
                        ]
                     }
                  },
                  {
                     name: "alfresco/lists/views/layouts/Cell",
                     config: {
                        additionalCssClasses: "layoutColumn smallpad",
                        widgets: [
                           {
                              name: "alfresco/renderers/Boolean",
                              config: {
                                 renderedValueClass: "alfresco-renderers-Property",
                                 propertyToRender: "layout",
                                 displayType: "YESNO"
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
                           typePropertyGroups.typeActionsMenu
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
 * The type listing
 */
typePropertyGroups.typesList = {
   id: "TYPES_LIST",
   name: "cmm/lists/CMMTPGList",
   config: {
      loadDataPublishTopic: "CMM_GET_TYPES",
      pubSubScope: "TYPES_LIST_",
      reloadDataTopic: "CMM_RELOAD_TYPES",
      itemsProperty: "list.entries",
      useHash: true,
      // Trigger a data update only when these hash variables change
      hashVarsForUpdate: [
         "model"
      ],
      // Only proceed with the data update when the hashVar 'view' is equal to 'types_property_groups'
      hashVarsForUpdateMustEqual: [
         {
            name: "view",
            value: "types_property_groups"
         }
      ],
      noDataMessage: msg.get("cmm.message.no-types-found"),
      dataFailureMessage: msg.get("cmm.message.error-loading-types"),
      widgets: [
         typePropertyGroups.typeListLayout
      ]
   }
};

/**
 * Actions menu for the property group listing
 */
typePropertyGroups.propertyGroupActionsMenu = {
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
                                 label: "cmm.button.form-editor",
                                 iconClass: "cmm-icon-edit-form",
                                 publishTopic: "CMM_DSP_FORM_EDITOR",
                                 publishGlobal: true,
                                 useCurrentItemAsPayload: false,
                                 publishPayloadType: "PROCESS",
                                 publishPayloadModifiers: ["processCurrentItemTokens"],
                                 // augmented data value see CMMService.getPropertyGroups()
                                 publishPayload: {
                                    modelName: "{modelName}",
                                    propertygroup: "{entry.name}"
                                 }
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
                                    dialogId: "CMM_EDIT_PROPERTYGROUP_DIALOG",
                                    dialogTitle: "cmm.model.edit-propertygroup-title",
                                    dialogConfirmationButtonTitle: "cmm.button.save-changes",
                                    dialogCancellationButtonTitle: "cmm.button.cancel",
                                    dialogCloseTopic: "CMM_EDIT_PROPERTYGROUP_SUCCESS",
                                    additionalCssClasses: "edit-form-dialog",
                                    formSubmissionTopic: "CMM_EDIT_PROPERTYGROUP",
                                    fixedWidth: true,
                                    showValidationErrorsImmediately: false,
                                    widgets: typePropertyGroups.editPropertyGroupForm,
                                    formValue: typePropertyGroups.propertyGroupFormValue
                                 },
                                 renderFilter: [
                                    {
                                       property: "status",
                                       values: ["DRAFT","DISABLED"],
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
                                    dialogId: "CMM_EDIT_PROPERTYGROUP_DIALOG",
                                    dialogTitle: "cmm.model.edit-propertygroup-title",
                                    dialogConfirmationButtonTitle: "cmm.button.save-changes",
                                    dialogCancellationButtonTitle: "cmm.button.cancel",
                                    dialogCloseTopic: "CMM_EDIT_PROPERTYGROUP_SUCCESS",
                                    additionalCssClasses: "edit-form-dialog",
                                    formSubmissionTopic: "CMM_EDIT_PROPERTYGROUP",
                                    fixedWidth: true,
                                    widgets: typePropertyGroups.editActivePropertyGroupForm,
                                    formValue: typePropertyGroups.propertyGroupFormValue
                                 },
                                 renderFilter: [
                                    {
                                       property: "status",
                                       values: ["ACTIVE"],
                                       renderOnAbsentProperty: false
                                    }
                                 ]
                              }
                           },
                           {
                              name: "alfresco/menus/AlfMenuItem",
                              config: {
                                 label: "cmm.button.delete",
                                 iconClass: "cmm-icon-delete",
                                 publishTopic: "CMM_DELETE_PROPERTYGROUP",
                                 publishGlobal: true,
                                 publishPayloadType: "PROCESS",
                                 publishPayloadModifiers: ["processCurrentItemTokens"],
                                 publishPayload: {
                                    modelName: "{modelName}",
                                    name: "{entry.name}"
                                 }
                              }
                           },
                           {
                              name: "alfresco/menus/AlfMenuItem",
                              config: {
                                 label: "cmm.button.find-instances",
                                 iconClass: "cmm-icon-search",
                                 publishTopic: "CMM_FIND_PROPERTYGROUPS",
                                 publishGlobal: true,
                                 publishPayloadType: "PROCESS",
                                 publishPayloadModifiers: ["processCurrentItemTokens"],
                                 publishPayload: {
                                    name: "{entry.prefixedName}"
                                 },
                                 renderFilter: [
                                    {
                                       property: "status",
                                       values: ["ACTIVE"],
                                       renderOnAbsentProperty: false
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
};

/**
 * List layout for the property group listing
 */
typePropertyGroups.propertyGroupListLayout = {
   name: "alfresco/lists/views/AlfListView",
   config: {
      additionalCssClasses: "bordered",
      noItemsMessage: "cmm.message.no-propertygroups",
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
               label: "cmm.header.display-label",
               sortable: false,
               additionalCssClasses: "displayLabelColumn smallpad"
            }
         },
         {
            name: "alfresco/lists/views/layouts/HeaderCell",
            config: {
               label: "cmm.header.parent",
               sortable: false,
               additionalCssClasses: "parentColumn smallpad"
            }
         },
         {
            name: "alfresco/lists/views/layouts/HeaderCell",
            config: {
               label: "cmm.header.layout",
               sortable: false,
               additionalCssClasses: "layoutColumn smallpad"
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
                                 propertyToRender: "entry.prefixedName",
                                 publishTopic: "CMM_DSP_PROPERTIES",
                                 publishGlobal: true,
                                 useCurrentItemAsPayload: false,
                                 publishPayloadType: "PROCESS",
                                 publishPayloadModifiers: ["processCurrentItemTokens"],
                                 // augmented data value see CMMService.getPropertyGroups()
                                 publishPayload: {
                                    modelName: "{modelName}",
                                    propertygroup: "{entry.name}"
                                 }
                              }
                           }
                        ]
                     }
                  },
                  {
                     name: "alfresco/lists/views/layouts/Cell",
                     config: {
                        additionalCssClasses: "displayLabelColumn smallpad",
                        widgets: [
                           {
                              name: "alfresco/renderers/Property",
                              config: {
                                 renderedValueClass: "alfresco-renderers-Property",
                                 propertyToRender: "entry.title"
                              }
                           }
                        ]
                     }
                  },
                  {
                     name: "alfresco/lists/views/layouts/Cell",
                     config: {
                        additionalCssClasses: "parentColumn smallpad",
                        widgets: [
                           {
                              name: "alfresco/renderers/Property",
                              config: {
                                 renderedValueClass: "alfresco-renderers-Property",
                                 propertyToRender: "entry.parentName"
                              }
                           }
                        ]
                     }
                  },
                  {
                     name: "alfresco/lists/views/layouts/Cell",
                     config: {
                        additionalCssClasses: "layoutColumn smallpad",
                        widgets: [
                           {
                              name: "alfresco/renderers/Boolean",
                              config: {
                                 renderedValueClass: "alfresco-renderers-Property",
                                 propertyToRender: "layout",
                                 displayType: "YESNO"
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
                           typePropertyGroups.propertyGroupActionsMenu
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
 * The property group listing
 */
typePropertyGroups.propertyGroupsList = {
   id: "PROPERTY_GROUPS_LIST",
   name: "cmm/lists/CMMTPGList",
   config: {
      loadDataPublishTopic: "CMM_GET_PROPERTYGROUPS",
      pubSubScope: "PROPERTYGROUPS_LIST_",
      reloadDataTopic: "CMM_RELOAD_PROPERTYGROUPS",
      itemsProperty: "list.entries",
      useHash: true,
      // Trigger a data update only when these hash variables change
      hashVarsForUpdate: [
         "model"
      ],
      // Only proceed with the data update when the hashVar 'view' is equal to 'types_property_groups'
      hashVarsForUpdateMustEqual: [
         {
            name: "view",
            value: "types_property_groups"
         }
      ],
      noDataMessage: msg.get("cmm.message.no-property-groups-found"),
      dataFailureMessage: msg.get("cmm.message.error-loading-property-groups"),
      widgets: [
         typePropertyGroups.propertyGroupListLayout
      ]
   }
};
