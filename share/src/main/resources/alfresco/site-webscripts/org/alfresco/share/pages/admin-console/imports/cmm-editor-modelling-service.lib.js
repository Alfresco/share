/*
 * Custom Model Manager
 * Form editor modelling service imports
 * imports/cmm-editor-modelling-service.lib.js
 * 
 * @author Kevin Roast
 * @author Richard Smith
 */

var modellingService = {};

/**
 * Build the modelling service for the editor
 */
modellingService.attributes = {};
modellingService.attributes.fixedName = {
   name: "alfresco/forms/controls/TextBox",
   config: {
      name: "elementconfig.infoname",
      label: "cmm.form-editor.property.fixed-name",
      _disabled: true,
      postWhenHiddenOrDisabled: false,
      additionalCssClasses: "edit-properties-info first"
   }
};
modellingService.attributes.fixedLabel = {
   name: "alfresco/forms/controls/TextBox",
   config: {
      name: "elementconfig.infolabel",
      label: "cmm.form-editor.property.fixed-label",
      _disabled: true,
      postWhenHiddenOrDisabled: false,
      additionalCssClasses: "edit-properties-info"
   }
};
modellingService.attributes.fixedType = {
   name: "alfresco/forms/controls/TextBox",
   config: {
      name: "elementconfig.infotype",
      label: "cmm.form-editor.property.fixed-type",
      _disabled: true,
      postWhenHiddenOrDisabled: false,
      additionalCssClasses: "edit-properties-info last"
   }
};
modellingService.attributes.simpleStyle = {
   name: "cmm/forms/controls/SimpleStyle",
   config: {
      name: "elementconfig.style",
      label: "cmm.form-editor.property.style",
      description: "cmm.form-editor.property.style.desc"
   }
};
modellingService.attributes.simpleClass = {
   name: "alfresco/forms/controls/TextBox",
   config: {
      name: "elementconfig.styleclass",
      label: "cmm.form-editor.property.styleclass",
      description: "cmm.form-editor.property.styleclass.desc"
   }
};
modellingService.attributes.readOnly = {
   name: "alfresco/forms/controls/CheckBox",
   config: {
      name: "elementconfig.read-only",
      label: "cmm.form-editor.property.read-only",
      description: "cmm.form-editor.property.read-only.desc"
   }
};
modellingService.attributes.force = {
   name: "alfresco/forms/controls/CheckBox",
   config: {
      name: "elementconfig.force",
      label: "cmm.form-editor.property.force",
      description: "cmm.form-editor.property.force.desc"
   }
};
modellingService.attributes.hidden = {
   name: "alfresco/forms/controls/CheckBox",
   config: {
      name: "elementconfig.hidden",
      label: "cmm.form-editor.property.hidden",
      description: "cmm.form-editor.property.hidden.desc"
   }
};
modellingService.attributes.selectMode = {
   name: "alfresco/forms/controls/Select",
   config: {
      name: "elementconfig.for-mode",
      label: "cmm.form-editor.property.for-mode",
      description: "cmm.form-editor.property.for-mode.desc",
      value: "any",
      optionsConfig: {
         fixed: [
            {
               value: "any",
               label: "cmm.form-editor.property.for-mode.any"
            },
            {
               value: "view",
               label: "cmm.form-editor.property.for-mode.view"
            },
            {
               value: "edit",
               label: "cmm.form-editor.property.for-mode.edit"
            }
         ]
      }
   }
};
modellingService.attributes.selectAppearance = {
   name: "alfresco/forms/controls/Select",
   config: {
      name: "elementconfig.appearance",
      label: "cmm.form-editor.property.appearance",
      description: "cmm.form-editor.property.appearance.desc",
      value: "bordered-panel",
      optionsConfig: {
         fixed: [
            {
               value: "bordered-panel",
               label: "cmm.form-editor.property.appearance.options.bordered-panel"
            },
            {
               value: "fieldset",
               label: "cmm.form-editor.property.appearance.options.fieldset"
            },
            {
               value: "panel",
               label: "cmm.form-editor.property.appearance.options.panel"
            },
            {
               value: "title",
               label: "cmm.form-editor.property.appearance.options.title"
            },
            {
               value: "whitespace",
               label: "cmm.form-editor.property.appearance.options.whitespace"
            }
         ]
      }
   }
};
modellingService.attributes.textLabel = {
   name: "alfresco/forms/controls/TextBox",
   config: {
      name: "elementconfig.label",
      label: "cmm.form-editor.property.panel-label",
      description: "cmm.form-editor.property.panel-label.desc"
   }
};
modellingService.attributes.panelTypeOne = {
   name: "alfresco/forms/controls/Select",
   config: {
      name: "wrapperSettings.type",
      label: "cmm.form-editor.property.panel-cols",
      description: "cmm.form-editor.property.panel-cols.desc",
      additionalCssClasses: "underborder",
      value: "one",
      optionsConfig: {
         fixed: [
            {
               value: "one",
               label: "cmm.form-editor.palette.one-column"
            },
            {
               value: "two",
               label: "cmm.form-editor.palette.two-column"
            },
            {
               value: "twowideleft",
               label: "cmm.form-editor.palette.two-column-wide-left"
            },
            {
               value: "three",
               label: "cmm.form-editor.palette.three-column"
            }
         ]
      }
   }
};
modellingService.attributes.panelTypeTwo = JSON.parse(JSON.stringify(modellingService.attributes.panelTypeOne));
modellingService.attributes.panelTypeTwo.config.value = "two";
modellingService.attributes.panelTypeTwoWideLeft = JSON.parse(JSON.stringify(modellingService.attributes.panelTypeOne));
modellingService.attributes.panelTypeTwoWideLeft.config.value = "twowideleft";
modellingService.attributes.panelTypeThree = JSON.parse(JSON.stringify(modellingService.attributes.panelTypeOne));
modellingService.attributes.panelTypeThree.config.value = "three";

modellingService.attributes.colsConfig = {
   allStyles: {
      wrappers: [
         "singleColumnWrapper",
         "doubleColumnWrapper",
         "doubleColumnWideLeftWrapper",
         "tripleColumnWrapper"
      ],
      targets: [
         "singleColumn",
         "doubleColumn",
         "doubleColumnWideLeft",
         "tripleColumn"
      ]
   },
   one: {
      pseudonym: "cmm/editor/layout/1cols",
      label: "cmm.form-editor.palette.one-column",
      wrapperClass: "singleColumnWrapper",
      targetClass: "singleColumn"
   },
   two: {
      pseudonym: "cmm/editor/layout/2cols",
      label: "cmm.form-editor.palette.two-column",
      wrapperClass: "doubleColumnWrapper",
      targetClass: "doubleColumn"
   },
   twowideleft: {
      pseudonym: "cmm/editor/layout/2colswideleft",
      label: "cmm.form-editor.palette.two-column-wide-left",
      wrapperClass: "doubleColumnWideLeftWrapper",
      targetClass: "doubleColumnWideLeft"
   },
   three: {
      pseudonym: "cmm/editor/layout/3cols",
      label: "cmm.form-editor.palette.three-column",
      wrapperClass: "tripleColumnWrapper",
      targetClass: "tripleColumn"
   }
};

modellingService.service = {
   name: "alfresco/services/DragAndDropModellingService",
   config: {
      models: [

         // 1 column layout
         {
            property: "pseudonym",
            targetValues: ["cmm/editor/layout/1cols"],
            widgetsForConfig: [
               modellingService.attributes.panelTypeOne,
               modellingService.attributes.textLabel,
               modellingService.attributes.selectAppearance
            ],
            widgetsForNestedConfig: [],
            widgetsForDisplay: [
               {
                  name: "cmm/dnd/DroppedNestingItemValueWrapper",
                  config: {
                     label: "{label}",
                     type: "{type}",
                     value: "{value}",
                     displayValueProperty: "elementconfig.label",
                     editPublishTopic: "ALF_CREATE_FORM_TITLE_PANE_REQUEST",
                     editPublishPayload: {
                        titlePaneContainer: "PROPERTIES_PANEL",
                        titlePanePositionContainer: "CMM",
                        titlePaneId: "ALF_DROPPED_ITEM_CONFIGURATION_TITLE_PANE",
                        titlePaneTitle: msg.get("cmm.form-editor.properties.title"),
                        formSubmissionTopic: "{subscriptionTopic}",
                        formSubmissionPayloadMixin: "{payloadMixin}",
                        formValue: "{item}",
                        widgets: "{widgets}"
                     },
                     selectPublishTopic: "FORM_EDITOR_ITEM_SELECTED",
                     focusPublishTopic: "FORM_EDITOR_ITEM_FOCUSED",
                     colsConfig: modellingService.attributes.colsConfig,
                     additionalCssClasses: "singleColumnWrapper",
                     widgets: [
                        {
                           name: "alfresco/dnd/DragAndDropNestedTarget",
                           config: {
                              label: "",
                              additionalCssClasses: "singleColumn",
                              targetProperty: "column",
                              useModellingService: true,
                              acceptTypes: ["property"],
                              withHandles: false
                           }
                        }
                     ]
                  }
               }
            ]
         },

         // 2 column wide left layout
         {
            property: "pseudonym",
            targetValues: ["cmm/editor/layout/2colswideleft"],
            widgetsForConfig: [
               modellingService.attributes.panelTypeTwoWideLeft,
               modellingService.attributes.textLabel,
               modellingService.attributes.selectAppearance
            ],
            widgetsForNestedConfig: [],
            widgetsForDisplay: [
               {
                  name: "cmm/dnd/DroppedNestingItemValueWrapper",
                  config: {
                     label: "{label}",
                     type: "{type}",
                     value: "{value}",
                     displayValueProperty: "elementconfig.label",
                     editPublishTopic: "ALF_CREATE_FORM_TITLE_PANE_REQUEST",
                     editPublishPayload: {
                        titlePaneContainer: "PROPERTIES_PANEL",
                        titlePanePositionContainer: "CMM",
                        titlePaneId: "ALF_DROPPED_ITEM_CONFIGURATION_TITLE_PANE",
                        titlePaneTitle: msg.get("cmm.form-editor.properties.title"),
                        formSubmissionTopic: "{subscriptionTopic}",
                        formSubmissionPayloadMixin: "{payloadMixin}",
                        formValue: "{item}",
                        widgets: "{widgets}"
                     },
                     selectPublishTopic: "FORM_EDITOR_ITEM_SELECTED",
                     focusPublishTopic: "FORM_EDITOR_ITEM_FOCUSED",
                     colsConfig: modellingService.attributes.colsConfig,
                     additionalCssClasses: "doubleColumnWideLeftWrapper",
                     widgets: [
                        {
                           name: "alfresco/dnd/DragAndDropNestedTarget",
                           config: {
                              label: "",
                              additionalCssClasses: "doubleColumnWideLeft",
                              targetProperty: "column",
                              useModellingService: true,
                              acceptTypes: ["property"],
                              withHandles: false
                           }
                        }
                     ]
                  }
               }
            ]
         },

         // 2 column layout
         {
            property: "pseudonym",
            targetValues: ["cmm/editor/layout/2cols"],
            widgetsForConfig: [
               modellingService.attributes.panelTypeTwo,
               modellingService.attributes.textLabel,
               modellingService.attributes.selectAppearance
            ],
            widgetsForNestedConfig: [],
            widgetsForDisplay: [
               {
                  name: "cmm/dnd/DroppedNestingItemValueWrapper",
                  config: {
                     label: "{label}",
                     type: "{type}",
                     value: "{value}",
                     displayValueProperty: "elementconfig.label",
                     editPublishTopic: "ALF_CREATE_FORM_TITLE_PANE_REQUEST",
                     editPublishPayload: {
                        titlePaneContainer: "PROPERTIES_PANEL",
                        titlePanePositionContainer: "CMM",
                        titlePaneId: "ALF_DROPPED_ITEM_CONFIGURATION_TITLE_PANE",
                        titlePaneTitle: msg.get("cmm.form-editor.properties.title"),
                        formSubmissionTopic: "{subscriptionTopic}",
                        formSubmissionPayloadMixin: "{payloadMixin}",
                        formValue: "{item}",
                        widgets: "{widgets}"
                     },
                     selectPublishTopic: "FORM_EDITOR_ITEM_SELECTED",
                     focusPublishTopic: "FORM_EDITOR_ITEM_FOCUSED",
                     colsConfig: modellingService.attributes.colsConfig,
                     additionalCssClasses: "doubleColumnWrapper",
                     widgets: [
                        {
                           name: "alfresco/dnd/DragAndDropNestedTarget",
                           config: {
                              label: "",
                              additionalCssClasses: "doubleColumn",
                              targetProperty: "column",
                              useModellingService: true,
                              acceptTypes: ["property"],
                              withHandles: false
                           }
                        }
                     ]
                  }
               }
            ]
         },

         // 3 column layout
         {
            property: "pseudonym",
            targetValues: ["cmm/editor/layout/3cols"],
            widgetsForConfig: [
               modellingService.attributes.panelTypeThree,
               modellingService.attributes.textLabel,
               modellingService.attributes.selectAppearance
            ],
            widgetsForNestedConfig: [],
            widgetsForDisplay: [
               {
                  name: "cmm/dnd/DroppedNestingItemValueWrapper",
                  config: {
                     label: "{label}",
                     type: "{type}",
                     value: "{value}",
                     displayValueProperty: "elementconfig.label",
                     editPublishTopic: "ALF_CREATE_FORM_TITLE_PANE_REQUEST",
                     editPublishPayload: {
                        titlePaneContainer: "PROPERTIES_PANEL",
                        titlePanePositionContainer: "CMM",
                        titlePaneId: "ALF_DROPPED_ITEM_CONFIGURATION_TITLE_PANE",
                        titlePaneTitle: msg.get("cmm.form-editor.properties.title"),
                        formSubmissionTopic: "{subscriptionTopic}",
                        formSubmissionPayloadMixin: "{payloadMixin}",
                        formValue: "{item}",
                        widgets: "{widgets}"
                     },
                     selectPublishTopic: "FORM_EDITOR_ITEM_SELECTED",
                     focusPublishTopic: "FORM_EDITOR_ITEM_FOCUSED",
                     colsConfig: modellingService.attributes.colsConfig,
                     additionalCssClasses: "tripleColumnWrapper",
                     widgets: [
                        {
                           name: "alfresco/dnd/DragAndDropNestedTarget",
                           config: {
                              label: "",
                              targetProperty: "column",
                              additionalCssClasses: "tripleColumn",
                              useModellingService: true,
                              acceptTypes: ["property"],
                              withHandles: false
                           }
                        }
                     ]
                  }
               }
            ]
         },

         // Property - text, mltext
         {
            property: "pseudonym",
            targetValues: [
               "cmm/editor/property/text",
               "cmm/editor/property/mltext"
            ],
            widgetsForConfig: [
               modellingService.attributes.fixedName,
               modellingService.attributes.fixedLabel,
               modellingService.attributes.fixedType,
               {
                  name: "alfresco/forms/controls/Select",
                  config: {
                     fieldId: "CONTROLTYPE",
                     name: "elementconfig.controltype",
                     label: "cmm.form-editor.property.form-control",
                     description: "cmm.form-editor.property.form-control.desc",
                     value: "textfield",
                     optionsConfig: {
                        fixed: [
                           {
                              value: "default",
                              label: "cmm.form-editor.property.form-control.default"
                           },
                           {
                              value: "textfield",
                              label: "cmm.form-editor.property.form-control.textfield"
                           },
                           {
                              value: "textarea",
                              label: "cmm.form-editor.property.form-control.textarea"
                           },
                           {
                              value: "richtext",
                              label: "cmm.form-editor.property.form-control.richtext"
                           },
                           {
                              value: "password",
                              label: "cmm.form-editor.property.form-control.password"
                           }
                        ]
                     }
                  }
               },
               modellingService.attributes.selectMode,
               modellingService.attributes.simpleStyle,
               modellingService.attributes.simpleClass,
               modellingService.attributes.readOnly,
               modellingService.attributes.force,
               modellingService.attributes.hidden
            ],
            widgetsForNestedConfig: [],
            widgetsForDisplay: [
               {
                  name: "cmm/dnd/DroppedNestingItemWrapper",
                  config: {
                     label: "{label}",
                     type: "{type}",
                     value: "{value}",
                     displayValueProperty: "elementconfig.heading",
                     editPublishTopic: "ALF_CREATE_FORM_TITLE_PANE_REQUEST",
                     editPublishPayload: {
                        titlePaneContainer: "PROPERTIES_PANEL",
                        titlePanePositionContainer: "CMM",
                        titlePaneId: "ALF_DROPPED_ITEM_CONFIGURATION_TITLE_PANE",
                        titlePaneTitle: msg.get("cmm.form-editor.properties.title"),
                        formSubmissionTopic: "{subscriptionTopic}",
                        formSubmissionPayloadMixin: "{payloadMixin}",
                        formValue: "{item}",
                        widgets: "{widgets}"
                     },
                     selectPublishTopic: "FORM_EDITOR_ITEM_SELECTED",
                     focusPublishTopic: "FORM_EDITOR_ITEM_FOCUSED",
                     additionalCssClasses: "textWidget",
                     widgets: [
                        {
                           name: "alfresco/dnd/DroppedItem"
                        }
                     ]
                  }
               }
            ]
         },

         // Property - content
         {
            property: "pseudonym",
            targetValues: ["cmm/editor/property/content"],
            widgetsForConfig: [
               modellingService.attributes.fixedName,
               modellingService.attributes.fixedLabel,
               modellingService.attributes.fixedType,
               {
                  name: "alfresco/forms/controls/Select",
                  config: {
                     name: "elementconfig.controltype",
                     label: "cmm.form-editor.property.form-control",
                     description: "cmm.form-editor.property.form-control.desc",
                     value: "content",
                     optionsConfig: {
                        fixed: [
                           /* Currently unsupported by the Forms Runtime expect in 'Create' form mode which is only
                              used by the Doclib Create New Plain Text content action...*/
                           {
                              value: "content",
                              label: "cmm.form-editor.property.form-control.textarea"
                           },
                           {
                              value: "richtext",
                              label: "cmm.form-editor.property.form-control.richtext"
                           }
                        ]
                     }
                  }
               },
               modellingService.attributes.selectMode,
               modellingService.attributes.simpleStyle,
               modellingService.attributes.simpleClass,
               modellingService.attributes.readOnly,
               modellingService.attributes.force
            ],
            widgetsForNestedConfig: [],
            widgetsForDisplay: [
               {
                  name: "cmm/dnd/DroppedNestingItemWrapper",
                  config: {
                     label: "{label}",
                     type: "{type}",
                     value: "{value}",
                     displayValueProperty: "elementconfig.heading",
                     editPublishTopic: "ALF_CREATE_FORM_TITLE_PANE_REQUEST",
                     editPublishPayload: {
                        titlePaneContainer: "PROPERTIES_PANEL",
                        titlePanePositionContainer: "CMM",
                        titlePaneId: "ALF_DROPPED_ITEM_CONFIGURATION_TITLE_PANE",
                        titlePaneTitle: msg.get("cmm.form-editor.properties.title"),
                        formSubmissionTopic: "{subscriptionTopic}",
                        formSubmissionPayloadMixin: "{payloadMixin}",
                        formValue: "{item}",
                        widgets: "{widgets}"
                     },
                     selectPublishTopic: "FORM_EDITOR_ITEM_SELECTED",
                     focusPublishTopic: "FORM_EDITOR_ITEM_FOCUSED",
                     additionalCssClasses: "contentWidget",
                     widgets: [
                        {
                           name: "alfresco/dnd/DroppedItem"
                        }
                     ]
                  }
               }
            ]
         },

         // Property - int, long, double, float
         {
            property: "pseudonym",
            targetValues: [
               "cmm/editor/property/int",
               "cmm/editor/property/long",
               "cmm/editor/property/double",
               "cmm/editor/property/float"
            ],
            widgetsForConfig: [
               modellingService.attributes.fixedName,
               modellingService.attributes.fixedLabel,
               modellingService.attributes.fixedType,
               {
                  name: "alfresco/forms/controls/Select",
                  config: {
                     name: "elementconfig.controltype",
                     label: "cmm.form-editor.property.form-control",
                     description: "cmm.form-editor.property.form-control.desc",
                     value: "number",
                     optionsConfig: {
                        fixed: [
                           {
                              value: "default",
                              label: "cmm.form-editor.property.form-control.default"
                           },
                           {
                              value: "number",
                              label: "cmm.form-editor.property.form-control.number"
                           }
                        ]
                     }
                  }
               },
               modellingService.attributes.selectMode,
               modellingService.attributes.simpleStyle,
               modellingService.attributes.simpleClass,
               modellingService.attributes.readOnly,
               modellingService.attributes.force
            ],
            widgetsForNestedConfig: [],
            widgetsForDisplay: [
               {
                  name: "cmm/dnd/DroppedNestingItemWrapper",
                  config: {
                     label: "{label}",
                     type: "{type}",
                     value: "{value}",
                     displayValueProperty: "elementconfig.heading",
                     editPublishTopic: "ALF_CREATE_FORM_TITLE_PANE_REQUEST",
                     editPublishPayload: {
                        titlePaneContainer: "PROPERTIES_PANEL",
                        titlePanePositionContainer: "CMM",
                        titlePaneId: "ALF_DROPPED_ITEM_CONFIGURATION_TITLE_PANE",
                        titlePaneTitle: msg.get("cmm.form-editor.properties.title"),
                        formSubmissionTopic: "{subscriptionTopic}",
                        formSubmissionPayloadMixin: "{payloadMixin}",
                        formValue: "{item}",
                        widgets: "{widgets}"
                     },
                     selectPublishTopic: "FORM_EDITOR_ITEM_SELECTED",
                     focusPublishTopic: "FORM_EDITOR_ITEM_FOCUSED",
                     additionalCssClasses: "numericWidget",
                     widgets: [
                        {
                           name: "alfresco/dnd/DroppedItem"
                        }
                     ]
                  }
               }
            ]
         },

         // Property - datetime
         {
            property: "pseudonym",
            targetValues: ["cmm/editor/property/datetime"],
            widgetsForConfig: [
               modellingService.attributes.fixedName,
               modellingService.attributes.fixedLabel,
               modellingService.attributes.fixedType,
               {
                  name: "alfresco/forms/controls/Select",
                  config: {
                     name: "elementconfig.controltype",
                     label: "cmm.form-editor.property.form-control",
                     description: "cmm.form-editor.property.form-control.desc",
                     value: "datetime",
                     optionsConfig: {
                        fixed: [
                           {
                              value: "datetime",
                              label: "cmm.form-editor.property.form-control.datetime"
                           }
                        ]
                     }
                  }
               },
               modellingService.attributes.selectMode,
               modellingService.attributes.simpleStyle,
               modellingService.attributes.simpleClass,
               modellingService.attributes.readOnly,
               modellingService.attributes.force
            ],
            widgetsForNestedConfig: [],
            widgetsForDisplay: [
               {
                  name: "cmm/dnd/DroppedNestingItemWrapper",
                  config: {
                     label: "{label}",
                     type: "{type}",
                     value: "{value}",
                     displayValueProperty: "elementconfig.heading",
                     editPublishTopic: "ALF_CREATE_FORM_TITLE_PANE_REQUEST",
                     editPublishPayload: {
                        titlePaneContainer: "PROPERTIES_PANEL",
                        titlePanePositionContainer: "CMM",
                        titlePaneId: "ALF_DROPPED_ITEM_CONFIGURATION_TITLE_PANE",
                        titlePaneTitle: msg.get("cmm.form-editor.properties.title"),
                        formSubmissionTopic: "{subscriptionTopic}",
                        formSubmissionPayloadMixin: "{payloadMixin}",
                        formValue: "{item}",
                        widgets: "{widgets}"
                     },
                     selectPublishTopic: "FORM_EDITOR_ITEM_SELECTED",
                     focusPublishTopic: "FORM_EDITOR_ITEM_FOCUSED",
                     additionalCssClasses: "dateWidget",
                     widgets: [
                        {
                           name: "alfresco/dnd/DroppedItem"
                        }
                     ]
                  }
               }
            ]
         },
         
         // Property - date
         {
            property: "pseudonym",
            targetValues: ["cmm/editor/property/date"],
            widgetsForConfig: [
               modellingService.attributes.fixedName,
               modellingService.attributes.fixedLabel,
               modellingService.attributes.fixedType,
               {
                  name: "alfresco/forms/controls/Select",
                  config: {
                     name: "elementconfig.controltype",
                     label: "cmm.form-editor.property.form-control",
                     description: "cmm.form-editor.property.form-control.desc",
                     value: "date",
                     optionsConfig: {
                        fixed: [
                           {
                              value: "date",
                              label: "cmm.form-editor.property.form-control.date"
                           }
                        ]
                     }
                  }
               },
               modellingService.attributes.selectMode,
               modellingService.attributes.readOnly,
               modellingService.attributes.force
            ],
            widgetsForNestedConfig: [],
            widgetsForDisplay: [
               {
                  name: "cmm/dnd/DroppedNestingItemWrapper",
                  config: {
                     label: "{label}",
                     type: "{type}",
                     value: "{value}",
                     displayValueProperty: "elementconfig.heading",
                     editPublishTopic: "ALF_CREATE_FORM_TITLE_PANE_REQUEST",
                     editPublishPayload: {
                        titlePaneContainer: "PROPERTIES_PANEL",
                        titlePanePositionContainer: "CMM",
                        titlePaneId: "ALF_DROPPED_ITEM_CONFIGURATION_TITLE_PANE",
                        titlePaneTitle: msg.get("cmm.form-editor.properties.title"),
                        formSubmissionTopic: "{subscriptionTopic}",
                        formSubmissionPayloadMixin: "{payloadMixin}",
                        formValue: "{item}",
                        widgets: "{widgets}"
                     },
                     selectPublishTopic: "FORM_EDITOR_ITEM_SELECTED",
                     focusPublishTopic: "FORM_EDITOR_ITEM_FOCUSED",
                     additionalCssClasses: "dateWidget",
                     widgets: [
                        {
                           name: "alfresco/dnd/DroppedItem"
                        }
                     ]
                  }
               }
            ]
         },

         // Property - boolean
         {
            property: "pseudonym",
            targetValues: ["cmm/editor/property/boolean"],
            widgetsForConfig: [
               modellingService.attributes.fixedName,
               modellingService.attributes.fixedLabel,
               modellingService.attributes.fixedType,
               {
                  name: "alfresco/forms/controls/Select",
                  config: {
                     name: "elementconfig.controltype",
                     label: "cmm.form-editor.property.form-control",
                     description: "cmm.form-editor.property.form-control.desc",
                     value: "checkbox",
                     optionsConfig: {
                        fixed: [
                           {
                              value: "checkbox",
                              label: "cmm.form-editor.property.form-control.checkbox"
                           }
                        ]
                     }
                  }
               },
               modellingService.attributes.selectMode,
               modellingService.attributes.simpleStyle,
               modellingService.attributes.simpleClass,
               modellingService.attributes.readOnly,
               modellingService.attributes.force
            ],
            widgetsForNestedConfig: [],
            widgetsForDisplay: [
               {
                  name: "cmm/dnd/DroppedNestingItemWrapper",
                  config: {
                     label: "{label}",
                     type: "{type}",
                     value: "{value}",
                     displayValueProperty: "elementconfig.heading",
                     editPublishTopic: "ALF_CREATE_FORM_TITLE_PANE_REQUEST",
                     editPublishPayload: {
                        titlePaneContainer: "PROPERTIES_PANEL",
                        titlePanePositionContainer: "CMM",
                        titlePaneId: "ALF_DROPPED_ITEM_CONFIGURATION_TITLE_PANE",
                        titlePaneTitle: msg.get("cmm.form-editor.properties.title"),
                        formSubmissionTopic: "{subscriptionTopic}",
                        formSubmissionPayloadMixin: "{payloadMixin}",
                        formValue: "{item}",
                        widgets: "{widgets}"
                     },
                     selectPublishTopic: "FORM_EDITOR_ITEM_SELECTED",
                     focusPublishTopic: "FORM_EDITOR_ITEM_FOCUSED",
                     additionalCssClasses: "booleanWidget",
                     widgets: [
                        {
                           name: "alfresco/dnd/DroppedItem"
                        }
                     ]
                  }
               }
            ]
         },
         
         // Property - size (special pseudo content URL property)
         {
            property: "pseudonym",
            targetValues: [
               "cmm/editor/property/size"
            ],
            widgetsForConfig: [
               modellingService.attributes.fixedName,
               modellingService.attributes.fixedLabel,
               modellingService.attributes.fixedType,
               modellingService.attributes.simpleStyle,
               modellingService.attributes.simpleClass
            ],
            widgetsForNestedConfig: [],
            widgetsForDisplay: [
               {
                  name: "cmm/dnd/DroppedNestingItemWrapper",
                  config: {
                     label: "{label}",
                     type: "{type}",
                     value: "{value}",
                     displayValueProperty: "elementconfig.heading",
                     editPublishTopic: "ALF_CREATE_FORM_TITLE_PANE_REQUEST",
                     editPublishPayload: {
                        titlePaneContainer: "PROPERTIES_PANEL",
                        titlePanePositionContainer: "CMM",
                        titlePaneId: "ALF_DROPPED_ITEM_CONFIGURATION_TITLE_PANE",
                        titlePaneTitle: msg.get("cmm.form-editor.properties.title"),
                        formSubmissionTopic: "{subscriptionTopic}",
                        formSubmissionPayloadMixin: "{payloadMixin}",
                        formValue: "{item}",
                        widgets: "{widgets}"
                     },
                     selectPublishTopic: "FORM_EDITOR_ITEM_SELECTED",
                     focusPublishTopic: "FORM_EDITOR_ITEM_FOCUSED",
                     additionalCssClasses: "numericWidget",
                     widgets: [
                        {
                           name: "alfresco/dnd/DroppedItem"
                        }
                     ]
                  }
               }
            ]
         },
         
         // Property - mimetype (special pseudo content URL property)
         {
            property: "pseudonym",
            targetValues: [
               "cmm/editor/property/mimetype"
            ],
            widgetsForConfig: [
               modellingService.attributes.fixedName,
               modellingService.attributes.fixedLabel,
               modellingService.attributes.fixedType,
               modellingService.attributes.selectMode,
               modellingService.attributes.simpleStyle,
               modellingService.attributes.simpleClass,
               modellingService.attributes.readOnly,
               modellingService.attributes.force
            ],
            widgetsForNestedConfig: [],
            widgetsForDisplay: [
               {
                  name: "cmm/dnd/DroppedNestingItemWrapper",
                  config: {
                     label: "{label}",
                     type: "{type}",
                     value: "{value}",
                     displayValueProperty: "elementconfig.heading",
                     editPublishTopic: "ALF_CREATE_FORM_TITLE_PANE_REQUEST",
                     editPublishPayload: {
                        titlePaneContainer: "PROPERTIES_PANEL",
                        titlePanePositionContainer: "CMM",
                        titlePaneId: "ALF_DROPPED_ITEM_CONFIGURATION_TITLE_PANE",
                        titlePaneTitle: msg.get("cmm.form-editor.properties.title"),
                        formSubmissionTopic: "{subscriptionTopic}",
                        formSubmissionPayloadMixin: "{payloadMixin}",
                        formValue: "{item}",
                        widgets: "{widgets}"
                     },
                     selectPublishTopic: "FORM_EDITOR_ITEM_SELECTED",
                     focusPublishTopic: "FORM_EDITOR_ITEM_FOCUSED",
                     additionalCssClasses: "contentWidget",
                     widgets: [
                        {
                           name: "alfresco/dnd/DroppedItem"
                        }
                     ]
                  }
               }
            ]
         },
         
         // Property - taggable
         {
            property: "pseudonym",
            targetValues: [
               "cmm/editor/property/taggable"
            ],
            widgetsForConfig: [
               modellingService.attributes.fixedName,
               modellingService.attributes.fixedLabel,
               modellingService.attributes.fixedType,
               modellingService.attributes.simpleStyle,
               modellingService.attributes.simpleClass
            ],
            widgetsForNestedConfig: [],
            widgetsForDisplay: [
               {
                  name: "cmm/dnd/DroppedNestingItemWrapper",
                  config: {
                     label: "{label}",
                     type: "{type}",
                     value: "{value}",
                     displayValueProperty: "elementconfig.heading",
                     editPublishTopic: "ALF_CREATE_FORM_TITLE_PANE_REQUEST",
                     editPublishPayload: {
                        titlePaneContainer: "PROPERTIES_PANEL",
                        titlePanePositionContainer: "CMM",
                        titlePaneId: "ALF_DROPPED_ITEM_CONFIGURATION_TITLE_PANE",
                        titlePaneTitle: msg.get("cmm.form-editor.properties.title"),
                        formSubmissionTopic: "{subscriptionTopic}",
                        formSubmissionPayloadMixin: "{payloadMixin}",
                        formValue: "{item}",
                        widgets: "{widgets}"
                     },
                     selectPublishTopic: "FORM_EDITOR_ITEM_SELECTED",
                     focusPublishTopic: "FORM_EDITOR_ITEM_FOCUSED",
                     additionalCssClasses: "contentWidget",
                     widgets: [
                        {
                           name: "alfresco/dnd/DroppedItem"
                        }
                     ]
                  }
               }
            ]
         },
         
         // Property - categories
         {
            property: "pseudonym",
            targetValues: [
               "cmm/editor/property/categories"
            ],
            widgetsForConfig: [
               modellingService.attributes.fixedName,
               modellingService.attributes.fixedLabel,
               modellingService.attributes.fixedType,
               modellingService.attributes.selectMode,
               modellingService.attributes.simpleStyle,
               modellingService.attributes.simpleClass,
               modellingService.attributes.readOnly,
               modellingService.attributes.force
            ],
            widgetsForNestedConfig: [],
            widgetsForDisplay: [
               {
                  name: "cmm/dnd/DroppedNestingItemWrapper",
                  config: {
                     label: "{label}",
                     type: "{type}",
                     value: "{value}",
                     displayValueProperty: "elementconfig.heading",
                     editPublishTopic: "ALF_CREATE_FORM_TITLE_PANE_REQUEST",
                     editPublishPayload: {
                        titlePaneContainer: "PROPERTIES_PANEL",
                        titlePanePositionContainer: "CMM",
                        titlePaneId: "ALF_DROPPED_ITEM_CONFIGURATION_TITLE_PANE",
                        titlePaneTitle: msg.get("cmm.form-editor.properties.title"),
                        formSubmissionTopic: "{subscriptionTopic}",
                        formSubmissionPayloadMixin: "{payloadMixin}",
                        formValue: "{item}",
                        widgets: "{widgets}"
                     },
                     selectPublishTopic: "FORM_EDITOR_ITEM_SELECTED",
                     focusPublishTopic: "FORM_EDITOR_ITEM_FOCUSED",
                     additionalCssClasses: "contentWidget",
                     widgets: [
                        {
                           name: "alfresco/dnd/DroppedItem"
                        }
                     ]
                  }
               }
            ]
         }
      ]
   }
};