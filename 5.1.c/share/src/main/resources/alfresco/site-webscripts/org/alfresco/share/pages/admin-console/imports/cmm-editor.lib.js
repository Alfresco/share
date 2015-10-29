/*
 * Custom Model Manager
 * Form editor imports
 * imports/cmm-editor.lib.js
 * 
 * @author Kevin Roast
 * @author Richard Smith
 */

var editor = {};

/**
 * Menu for back to types properties
 */
editor.backMenu = {
   name: "alfresco/layout/LeftAndRight",
   config: {
      widgets: [
         // Use AlfDynamicPayloadButtons so we can retrieve data from the hash string which is set when the 
         // current item is selected from the outer Custom Models list.
         {
            name: "alfresco/buttons/AlfDynamicPayloadButton",
            config: {
               additionalCssClasses: "backButton backToTypesPropertyGroups",
               label: "cmm.button.back-to-types-propertygroups",
               publishTopic: "CMM_DSP_TYPES_AND_PROPERTYGROUPS",
               useHash: true,
               hashDataMapping: {
                  "model": "modelName"
               }
            }
         }
      ]
   }
};

/**
 * Build the form editor with its canvas drop zone
 */
editor.form = {
   id: "FORM_EDITOR",
   name: "alfresco/forms/Form",
   config: {
      scopeFormControls: false,
      okButtonLabel: "cmm.button.editor-save-layout",
      okButtonPublishTopic: "CMM_FORM_EDITOR_SAVE",
      okButtonPublishGlobal: true,
      showCancelButton: false,
      useHash: true,
      setValueTopic: "FORM_EDITOR_GET_VALUE_SUCCESS",
      // Trigger a data update only when these hash variables change
      hashVarsForUpdate: [
         "view",
         "model",
         "type",
         "propertygroup"
      ],
      widgetsAdditionalButtons: [
         {
            name: "alfresco/buttons/AlfDynamicPayloadButton",
            config: {
               additionalCssClasses: "editorDefaultLayoutButton",
               label: "cmm.button.editor-default-layout",
               publishTopic: "CMM_EDITOR_DEFAULT_LAYOUT_ACTION",
               useHash: true,
               hashDataMapping: {
                  "view": "view",
                  "model": "modelName",
                  "type": "type",
                  "propertygroup": "propertygroup"
               }
            }
         },
         {
            name: "alfresco/buttons/AlfButton",
            config: {
               additionalCssClasses: "editorClearLayoutButton",
               label: "cmm.button.editor-clear-layout",
               publishTopic: "CMM_EDITOR_CLEAR_TO_PALETTE_ACTION"
            }
         }
      ],
      widgets: [
         {
            name: "cmm/help/CMMHelpBubble",
            config: {
               additionalCssClasses: "top layout-help-top",
               label: "cmm.form-editor.message.help1"
            }
         },
         {
            name: "cmm/help/CMMHelpBubble",
            config: {
               additionalCssClasses: "left layout-help-left",
               label: "cmm.form-editor.message.help2"
            }
         },
         {
            name: "cmm/help/CMMHelpBubble",
            config: {
               additionalCssClasses: "bottom layout-help-bottom",
               label: "cmm.form-editor.message.help3"
            }
         },
         {
            id: "FORM_CANVAS",
            name: "cmm/forms/controls/DragAndDropTargetControl",
            config: {
               fieldId: "FORM_CANVAS",
               name: "formData",
               value: null,
               acceptTypes: ["layout"],
               withHandles: false,
               useModellingService: true,
               clearTopic: "CMM_EDITOR_CLEAR",
               clearDroppedItemsTopic: "CMM_EDITOR_CLEAR_TO_PALETTE",
               syncNodesTopic: "CMM_EDITOR_PROPERTIES_SYNC",
               syncNodesWhiteListProp: "data.value.pseudonym",
               syncNodesWhiteList: [
                  "cmm/editor/layout/1cols",
                  "cmm/editor/layout/2cols",
                  "cmm/editor/layout/2colswideleft",
                  "cmm/editor/layout/3cols"
               ],
               selectListenTopic: "FORM_EDITOR_ITEM_SELECTED",
               focusListenTopic: "FORM_EDITOR_ITEM_FOCUSED"
            }
         },
         {
            id: "MODEL_NAME",
            name: "alfresco/forms/controls/HiddenValue",
            config: {
               name: "model",
               value: ""
            }
         },
         {
            id: "TYPE_NAME",
            name: "alfresco/forms/controls/HiddenValue",
            config: {
               name: "type",
               value: ""
            }
         },
         {
            id: "PROPERTYGROUP_NAME",
            name: "alfresco/forms/controls/HiddenValue",
            config: {
               name: "propertygroup",
               value: ""
            }
         }
      ]
   }
};

/**
 * Build the nonPropertyPalette manually from fixed layout elements
 */
editor.nonPropertyPalette = {
   id: "NON_PROPERTY_PALETTE",
   name: "alfresco/dnd/DragAndDropItems",
   config: {
      items: [
         {
            type: [ "layout" ],
            label: "cmm.form-editor.palette.one-column",
            iconClass: "editor-1cols",
            value: {
               pseudonym: "cmm/editor/layout/1cols",
               elementconfig: {
                  label: "",
                  appearance: "bordered-panel"
               }
            }
         },
         {
            type: [ "layout" ],
            label: "cmm.form-editor.palette.two-column",
            iconClass: "editor-2cols",
            value: {
               pseudonym: "cmm/editor/layout/2cols",
               elementconfig: {
                  label: "",
                  appearance: "bordered-panel"
               }
            }
         },
         {
            type: [ "layout" ],
            label: "cmm.form-editor.palette.two-column-wide-left",
            iconClass: "editor-2cols-wide-left",
            value: {
               pseudonym: "cmm/editor/layout/2colswideleft",
               elementconfig: {
                  label: "",
                  appearance: "bordered-panel"
               }
            }
         },
         {
            type: [ "layout" ],
            label: "cmm.form-editor.palette.three-column",
            iconClass: "editor-3cols",
            value: {
               pseudonym: "cmm/editor/layout/3cols",
               elementconfig: {
                  label: "",
                  appearance: "bordered-panel"
               }
            }
         }
      ]
   }
};

/**
 * Build the propertyPalette from type and aspect properties
 */
editor.propertyPalette = {
   id: "PROPERTY_PALETTE",
   name: "cmm/lists/CMMTPGPropertiesList",
   config: {
      loadDataPublishTopic: "CMM_GET_EDITOR_PROPERTIES",
      pubSubScope: "FORM_EDITOR_",
      reloadDataTopic: "CMM_RELOAD_FORM_EDITOR_PROPERTIES",
      useHash: true,
      // Trigger a data update only when these hash variables change
      hashVarsForUpdate: [
         "model",
         "type",
         "propertygroup"
      ],
      // Only proceed with the data update when the hashVar 'view' is equal to 'editor'
      hashVarsForUpdateMustEqual: [
         {
            name: "view",
            value: "editor"
         }
      ],
      noDataMessage: msg.get("cmm.message.no-properties-found"),
      dataFailureMessage: msg.get("cmm.message.error-loading-properties"),
      widgets: [
         {
            name: "alfresco/dnd/DragAndDropItemsListView",
            config: {
               useItemsOnce: true,
               useItemsOnceComparisonKey: "id"
            }
         }
      ],
      syncNodesTopic: "CMM_EDITOR_PROPERTIES_SYNC"
   }
};

editor.inlinePropertyButton = {
   name: "cmm/buttons/CMMHashPayloadButton",
   config: {
      additionalCssClasses: "inlinePropertyButton",
      iconClass: "inlinePropertyButtonIcon",
      label: "cmm.create-button.property",
      publishTopic: "ALF_CREATE_FORM_DIALOG_REQUEST",
      publishPayload: {
         dialogId: "CMM_CREATE_PROPERTY_DIALOG",
         dialogTitle: "cmm.property.create-title",
         dialogConfirmationButtonTitle: "cmm.button.create",
         dialogConfirmationButtonId: "CMM_CREATE_PROPERTY_DIALOG_CREATE",
         dialogCancellationButtonTitle: "cmm.button.cancel",
         dialogCancellationButtonId: "CMM_CREATE_PROPERTY_DIALOG_CANCEL",
         dialogConfirmAndRepeatButtonTitle: "cmm.button.create-and-another",
         dialogConfirmAndRepeatButtonId: "CMM_CREATE_PROPERTY_DIALOG_CREATE_AND_ANOTHER",
         dialogRepeats: true,
         dialogCloseTopic: "CMM_CREATE_PROPERTY_SUCCESS",
         additionalCssClasses: "create-form-dialog",
         formSubmissionTopic: "CMM_CREATE_PROPERTY",
         fixedWidth: true,
         handleOverflow: true,
         widgets: properties.createForm
      },
      useHash: true,
      hashDataMapping: {
         "model": "formSubmissionPayloadMixin.model_name",
         "type": "formSubmissionPayloadMixin.type_name",
         "propertygroup": "formSubmissionPayloadMixin.propertygroup_name"
      }
   }
};

/**
 * Build the overall editor console for display
 */
editor.console = {
   id: "FORM_EDITOR_CONSOLE",
   name: "alfresco/layout/LeftAndRight",
   config: {
      widgets: [
         {
            name: "alfresco/layout/VerticalWidgets",
            config: {
               widgets: [
                  {
                     name: "alfresco/html/Label",
                     config: {
                        label: "cmm.heading.properties",
                        additionalCssClasses: "paletteLabel"
                     }
                  },
                  editor.propertyPalette,
                  editor.inlinePropertyButton
               ]
            }
         },
         {
            name: "alfresco/layout/VerticalWidgets",
            config: {
               widgets: [
                  {
                     name: "alfresco/html/Label",
                     config: {
                        label: "cmm.header.form-layout",
                        additionalCssClasses: "layoutLabel"
                     }
                  },
                  editor.nonPropertyPalette,
                  editor.form
               ]
            }
         },
         {
            id: "PROPERTIES_PANEL",
            name: "alfresco/layout/VerticalWidgets"
         }
      ]
   }
};