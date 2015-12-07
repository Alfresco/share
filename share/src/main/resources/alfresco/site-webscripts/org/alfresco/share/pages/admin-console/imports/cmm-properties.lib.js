/*
 * Custom Model Manager
 * Properties imports
 * imports/cmm-properties.lib.js
 * 
 * @author Kevin Roast
 * @author Richard Smith
 */

var properties = {};

// Converts the Java.lang.String to a Javascript string for serialization
var inlineHelpVal = "" + msg.get("cmm.property.indexing.txthelp", [context.properties["docsEdition"].getValue()]);

/**
 * Form for creating a property
 */
properties.createForm = [
   {
      name: "alfresco/forms/controls/TextBox",
      config: {
         fieldId: "NAME",
         label: "cmm.model.name",
         name: "name",
         description: "cmm.property.name.info",
         additionalCssClasses: "create-property-name",
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
         fieldId: "TITLE",
         label: "cmm.model.title",
         name: "title",
         description: "cmm.property.title.info",
         additionalCssClasses: "create-property-title",
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
         additionalCssClasses: "create-property-description",
         validationConfig: [
            validation.maxlength1024
         ]
      }
   },
   // d:datatype drop down
   {
      name: "alfresco/forms/controls/Select",
      config: {
         fieldId: "DATATYPE",
         label: "cmm.property.datatype",
         name: "datatype",
         description: "cmm.property.datatype.info",
         additionalCssClasses: "create-property-datatype",
         value: "d:text", // default to d:text
         optionsConfig: {
            fixed: [
               {
                  value: "d:text",
                  label: "d:text"
               },
               {
                  value: "d:mltext",
                  label: "d:mltext"
               },
/* NOTE: disabled as Forms Runtime does not support content fields except in 'Create' Form mode 
   which is currently only accessible when using the Create plain/html content pages
               {
                  value: "d:content",
                  label: "d:content"
               },
*/
               {
                  value: "d:int",
                  label: "d:int"
               },
               {
                  value: "d:long",
                  label: "d:long"
               },
               {
                  value: "d:float",
                  label: "d:float"
               },
               {
                  value: "d:double",
                  label: "d:double"
               },
               {
                  value: "d:date",
                  label: "d:date"
               },
               {
                  value: "d:datetime",
                  label: "d:datetime"
               },
               {
                  value: "d:boolean",
                  label: "d:boolean"
               }
            ]
         }
      }
   },
   // mandatory def drop down ( mandatory = false, mandatory = true (not enforced) )
   {
      name: "alfresco/forms/controls/Select",
      config: {
         fieldId: "MANDATORY",
         label: "cmm.property.mandatory",
         name: "mandatory",
         description: "cmm.property.mandatory.info",
         additionalCssClasses: "create-property-mandatory",
         optionsConfig: {
            fixed: [
               {
                  value: "optional",
                  label: "cmm.property.mandatory.optional"
               },
               {
                  value: "mandatory",
                  label: "cmm.property.mandatory.mandatory-not-enforced"
               }
            ]
         }
      }
   },
   {
      name: "alfresco/forms/controls/CheckBox",
      config: {
         fieldId: "MULTIPLE",
         label: "cmm.property.multiple",
         name: "multiple",
         description: "cmm.property.multiple.info",
         additionalCssClasses: "create-property-multiple"
      }
   },
   
   // Various default controls - toggled depending on the chosen data type
   {
      name: "alfresco/forms/controls/TextBox",
      config: {
         fieldId: "DEFAULT_TEXT",
         label: "cmm.property.default",
         name: "defaultValue",
         description: "cmm.property.default.info",
         additionalCssClasses: "create-property-default text",
         value: "",
         validationConfig: [
            validation.maxlength1024
         ],
         visibilityConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "DATATYPE",
                  is: ["d:text", "d:mltext", "d:content"]
               }
            ]
         },
         postWhenHiddenOrDisabled: false
      }
   },
   {
      name: "alfresco/forms/controls/TextBox",
      config: {
         fieldId: "DEFAULT_NUMERIC",
         label: "cmm.property.default",
         name: "defaultValue",
         description: "cmm.property.default.info",
         additionalCssClasses: "create-property-default number",
         value: "",
         postWhenHiddenOrDisabled: false,
         visibilityConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "DATATYPE",
                  is: ["d:int", "d:long", "d:float", "d:double"]
               }
            ]
         },
         validationConfig: [
            {
               validation: "regex",
               regex: "^$|^[-]?[0-9]{1,20}(?:\.[0-9]{1,10})?$",
               errorMessage: "cmm.property.default.number.error"
            }
         ]
      }
   },
   {
      name: "alfresco/forms/controls/DateTextBox",
      config: {
         fieldId: "DEFAULT_DATE",
         label: "cmm.property.default",
         name: "defaultValue",
         description: "cmm.property.default.info",
         additionalCssClasses: "create-property-default date",
         value: "",
         visibilityConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "DATATYPE",
                  is: ["d:date", "d:datetime"]
               }
            ]
         },
         postWhenHiddenOrDisabled: false
      }
   },
   {
      name: "alfresco/forms/controls/Select",
      config: {
         fieldId: "DEFAULT_BOOLEAN",
         label: "cmm.property.default",
         name: "defaultValue",
         description: "cmm.property.default.info",
         additionalCssClasses: "create-property-default boolean",
         value: "false",
         optionsConfig: {
            fixed: [
               {
                  value: "true",
                  label: "cmm.property.default.bool.true"
               },
               {
                  value: "false",
                  label: "cmm.property.default.bool.false"
               }
            ]
         },
         visibilityConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "DATATYPE",
                  is: ["d:boolean"]
               }
            ]
         },
         postWhenHiddenOrDisabled: false
      }
   },

   // Constraint dropdown
   {
      name: "alfresco/forms/controls/Select",
      config: {
         fieldId: "CONSTRAINT",
         label: "cmm.property.constraint",
         name: "constraint",
         description: "cmm.property.constraint.info",
         additionalCssClasses: "create-property-constraint",
         optionsConfig: {
            fixed: [
               {
                  value: "NONE",
                  label: "cmm.property.constraint.options.none"
               },
               {
                  value: "REGEX",
                  label: "cmm.property.constraint.options.regex"
               },
               {
                  value: "LENGTH",
                  label: "cmm.property.constraint.options.length"
               },
               {
                  value: "MINMAX",
                  label: "cmm.property.constraint.options.minmax"
               },
               {
                  value: "LIST",
                  label: "cmm.property.constraint.options.list"
               },
               {
                  value: "CLASS",
                  label: "cmm.property.constraint.options.class"
               }
            ]
         }
      }
   },

   // Constraint options for REGEX
   {
      name: "alfresco/forms/controls/TextBox",
      config: {
         fieldId: "CONSTRAINT_EXPRESSION",
         label: "cmm.property.constraint.expression",
         name: "constraintExpression",
         description: "cmm.property.constraint.expression.info",
         additionalCssClasses: "create-property-constraint-expression",
         validationConfig: [
            validation.maxlength1024
         ],
         requirementConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "CONSTRAINT",
                  is: ["REGEX"]
               }
            ]
         },
         visibilityConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "CONSTRAINT",
                  is: ["REGEX"]
               }
            ]
         },
         postWhenHiddenOrDisabled: false
      }
   },

   // Constraint options for LENGTH
   {
      name: "alfresco/forms/controls/NumberSpinner",
      config: {
         fieldId: "CONSTRAINT_MIN_LENGTH",
         label: "cmm.property.constraint.min-length",
         name: "constraintMinLength",
         description: "cmm.property.constraint.min-length.info",
         additionalCssClasses: "create-property-constraint-min-length",
         min: 0,
         value: 1,
         requirementConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "CONSTRAINT",
                  is: ["LENGTH"]
               }
            ]
         },
         visibilityConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "CONSTRAINT",
                  is: ["LENGTH"]
               }
            ]
         },
         postWhenHiddenOrDisabled: false
      }
   },
   {
      name: "alfresco/forms/controls/NumberSpinner",
      config: {
         fieldId: "CONSTRAINT_MAX_LENGTH",
         label: "cmm.property.constraint.max-length",
         name: "constraintMaxLength",
         description: "cmm.property.constraint.max-length.info",
         additionalCssClasses: "create-property-constraint-max-length",
         min: 1,
         value: 256,
         requirementConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "CONSTRAINT",
                  is: ["LENGTH"]
               }
            ]
         },
         visibilityConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "CONSTRAINT",
                  is: ["LENGTH"]
               }
            ]
         },
         postWhenHiddenOrDisabled: false
      }
   },

   // Constraint options for MINMAX
   {
      name: "alfresco/forms/controls/NumberSpinner",
      config: {
         fieldId: "CONSTRAINT_MIN_VALUE",
         label: "cmm.property.constraint.min-value",
         name: "constraintMinValue",
         description: "cmm.property.constraint.min-value.info",
         additionalCssClasses: "create-property-constraint-min-value",
         value: 0,
         requirementConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "CONSTRAINT",
                  is: ["MINMAX"]
               }
            ]
         },
         visibilityConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "CONSTRAINT",
                  is: ["MINMAX"]
               }
            ]
         },
         postWhenHiddenOrDisabled: false
      }
   },
   {
      name: "alfresco/forms/controls/NumberSpinner",
      config: {
         fieldId: "CONSTRAINT_MAX_VALUE",
         label: "cmm.property.constraint.max-value",
         name: "constraintMaxValue",
         description: "cmm.property.constraint.max-value.info",
         additionalCssClasses: "create-property-constraint-max-value",
         value: 10,
         requirementConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "CONSTRAINT",
                  is: ["MINMAX"]
               }
            ]
         },
         visibilityConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "CONSTRAINT",
                  is: ["MINMAX"]
               }
            ]
         },
         postWhenHiddenOrDisabled: false
      }
   },
   
   // Constraint options for LIST
   {
      name: "alfresco/forms/controls/TextArea",
      config: {
         fieldId: "CONSTRAINT_ALLOWED_VALUES",
         label: "cmm.property.constraint.allowed-values",
         name: "constraintAllowedValues",
         description: "cmm.property.constraint.allowed-values.info",
         additionalCssClasses: "create-property-constraint-allowed-values",
         requirementConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "CONSTRAINT",
                  is: ["LIST"]
               }
            ]
         },
         visibilityConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "CONSTRAINT",
                  is: ["LIST"]
               }
            ]
         },
         postWhenHiddenOrDisabled: false
      }
   },
   {
      name: "alfresco/forms/controls/CheckBox",
      config: {
         fieldId: "CONSTRAINT_SORTED",
         label: "cmm.property.constraint.sorted",
         name: "constraintSorted",
         description: "cmm.property.constraint.sorted.info",
         additionalCssClasses: "create-property-constraint-sorted",
         visibilityConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "CONSTRAINT",
                  is: ["LIST"]
               }
            ]
         },
         postWhenHiddenOrDisabled: false,
         value: false
      }
   },
   
   // Constraint options for CLASS
   {
      name: "alfresco/forms/controls/TextBox",
      config: {
         fieldId: "CONSTRAINT_CLASS",
         label: "cmm.property.constraint.class",
         name: "constraintClass",
         description: "cmm.property.constraint.class.info",
         additionalCssClasses: "create-property-constraint-class",
         validationConfig: [
            validation.maxlength1024
         ],
         requirementConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "CONSTRAINT",
                  is: ["CLASS"]
               }
            ]
         },
         visibilityConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "CONSTRAINT",
                  is: ["CLASS"]
               }
            ]
         },
         postWhenHiddenOrDisabled: false
      }
   },
   
   // Indexing
   {
      name: "alfresco/forms/controls/Select",
      config: {
         fieldId: "INDEXING_NON_TEXT",
         label: "cmm.property.indexing",
         name: "indexing_nontxt",
         description: "cmm.property.indexing.info",
         additionalCssClasses: "create-property-indexing nontext",
         value: "nontxt-standard",
         optionsConfig: {
            fixed: [
               {
                  value: "nontxt-none",
                  label: "cmm.property.indexing.nontext.none"
               },
               {
                  value: "nontxt-standard",
                  label: "cmm.property.indexing.nontext.basic"
               },
               {
                  value: "nontxt-enhanced",
                  label: "cmm.property.indexing.nontext.enhanced"
               }
            ]
         },
         visibilityConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "DATATYPE",
                  isNot: ["d:text", "d:mltext", "d:content", "d:boolean"]
               }
            ]
         },
         inlineHelp: inlineHelpVal
      }
   },
   {
      name: "alfresco/forms/controls/Select",
      config: {
         fieldId: "INDEXING_BOOLEAN",
         label: "cmm.property.indexing",
         name: "indexing_boolean",
         description: "cmm.property.indexing.info",
         additionalCssClasses: "create-property-indexing boolean",
         value: "boolean-standard",
         optionsConfig: {
            fixed: [
               {
                  value: "boolean-none",
                  label: "cmm.property.indexing.nontext.none"
               },
               {
                  value: "boolean-standard",
                  label: "cmm.property.indexing.nontext.basic"
               }
            ]
         },
         visibilityConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "DATATYPE",
                  is: ["d:boolean"]
               }
            ]
         },
         inlineHelp: inlineHelpVal
      }
   },
   {
      name: "alfresco/forms/controls/Select",
      config: {
         fieldId: "INDEXING_TEXT",
         label: "cmm.property.indexing",
         name: "indexing_txt",
         description: "cmm.property.indexing.info",
         additionalCssClasses: "create-property-indexing text",
         value: "txt-free",
         optionsConfig: {
            fixed: [
               {
                  value: "txt-none",
                  label: "cmm.property.indexing.text.none"
               },
               {
                  value: "txt-free",
                  label: "cmm.property.indexing.text.free"
               },
               {
                  value: "txt-list-whole",
                  label: "cmm.property.indexing.text.list-whole"
               },
               {
                  value: "txt-list-partial",
                  label: "cmm.property.indexing.text.list-partial"
               },
               {
                  value: "txt-pattern-unique",
                  label: "cmm.property.indexing.text.pattern-unique"
               },
               {
                  value: "txt-pattern-many",
                  label: "cmm.property.indexing.text.pattern-many"
               }
            ]
         },
         visibilityConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "DATATYPE",
                  is: ["d:text", "d:mltext", "d:content"]
               }
            ]
         },
         inlineHelp: inlineHelpVal
      }
   }
];

/**
 * Form for editing a property
 * Copy the create form and change the name field
 */
properties.editForm = JSON.parse(JSON.stringify(properties.createForm));
var nameFieldConfig = properties.editForm[0].config;
nameFieldConfig._disabled = true;
nameFieldConfig.description = "cmm.property.name.no.edit";
nameFieldConfig.requirementConfig.initialValue = false;
properties.editForm[22] = {
   name: "alfresco/forms/controls/HiddenValue",
   config: {
      fieldId: "MODEL_NAME",
      label: "cmm.model.name",
      name: "model_name",
      postWhenHiddenOrDisabled: true
   }
};
properties.editForm[23] = {
   name: "alfresco/forms/controls/HiddenValue",
   config: {
      fieldId: "TYPE_NAME",
      label: "cmm.type.name",
      name: "type_name",
      postWhenHiddenOrDisabled: true
   }
};
properties.editForm[24] = {
   name: "alfresco/forms/controls/HiddenValue",
   config: {
      fieldId: "PROPERTYGROUP_NAME",
      label: "cmm.propertygroup.name",
      name: "propertygroup_name",
      postWhenHiddenOrDisabled: true
   }
};

/**
 * Form for editing an active property
 * Copy the edit form and change the fields that are not editable
 */
properties.editActiveForm = JSON.parse(JSON.stringify(properties.editForm));

var dataTypeFieldConfig = properties.editActiveForm[3].config;
dataTypeFieldConfig._disabled = true;
dataTypeFieldConfig.description = "cmm.property.datatype.no.edit";
var mandatoryFieldConfig = properties.editActiveForm[4].config;
mandatoryFieldConfig._disabled = true;
mandatoryFieldConfig.description = "cmm.property.mandatory.no.edit";
var multipleFieldConfig = properties.editActiveForm[5].config;
multipleFieldConfig._disabled = true;
multipleFieldConfig.description = "cmm.property.multiple.no.edit";
var nontxtIndexingFieldConfig = properties.editActiveForm[19].config;
nontxtIndexingFieldConfig._disabled = true;
nontxtIndexingFieldConfig.description = "cmm.property.indexing.no.edit";
var booleanIndexingFieldConfig = properties.editActiveForm[20].config;
booleanIndexingFieldConfig._disabled = true;
booleanIndexingFieldConfig.description = "cmm.property.indexing.no.edit";
var txtIndexingFieldConfig = properties.editActiveForm[21].config;
txtIndexingFieldConfig._disabled = true;
txtIndexingFieldConfig.description = "cmm.property.indexing.no.edit";

/**
 * Menu for creating a property
 */
properties.createMenu = {
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
         },
         // Current item is selected from the outer PropertyGroups or Types list. We set into the formSubmissionPayloadMixin 
         // object as that mixes into the resulting form dialog payload.
         // We use cmm/buttons/CMMHashPayloadButton as this will null any missing values which is required to maintain the
         // mutually exclusive state of type|propertygroup set value.
         {
            name: "cmm/buttons/CMMHashPayloadButton",
            config: {
               additionalCssClasses: "createPropertyButton",
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
                  showValidationErrorsImmediately: false,
                  widgets: properties.createForm
               },
               useHash: true,
               hashDataMapping: {
                  "model": "formSubmissionPayloadMixin.model_name",
                  "type": "formSubmissionPayloadMixin.type_name",
                  "propertygroup": "formSubmissionPayloadMixin.propertygroup_name"
               }
            }
         }
      ]
   }
};

/**
 * Edit form value
 */
properties.propertyFormValue = {
   model_name: {
      alfType: "item",
      alfProperty: "modelName"
   },
   type_name: {
      alfType: "item",
      alfProperty: "type_name"
   },
   propertygroup_name: {
      alfType: "item",
      alfProperty: "propertygroup_name"
   },
   name: {
      alfType: "item",
      alfProperty: "name"
   },
   title: {
      alfType: "item",
      alfProperty: "title"
   },
   description: {
      alfType: "item",
      alfProperty: "description"
   },
   datatype: {
      alfType: "item",
      alfProperty: "dataType"
   },
   mandatory: {
      alfType: "item",
      alfProperty: "mandatoryEdit"
   },
   multiple: {
      alfType: "item",
      alfProperty: "multiValued"
   },
   defaultValue: {
      alfType: "item",
      alfProperty: "defaultValue"
   },
   indexing_txt: {
      alfType: "item",
      alfProperty: "indexing_txt"
   },
   indexing_boolean: {
      alfType: "item",
      alfProperty: "indexing_boolean"
   },
   indexing_nontxt: {
      alfType: "item",
      alfProperty: "indexing_nontxt"
   },
   constraint: {
      alfType: "item",
      alfProperty: "constraint"
   },
   constraintExpression: {
      alfType: "item",
      alfProperty: "constraintExpression"
   },
   constraintMinLength: {
      alfType: "item",
      alfProperty: "constraintMinLength"
   },
   constraintMaxLength: {
      alfType: "item",
      alfProperty: "constraintMaxLength"
   },
   constraintMinValue: {
      alfType: "item",
      alfProperty: "constraintMinValue"
   },
   constraintMaxValue: {
      alfType: "item",
      alfProperty: "constraintMaxValue"
   },
   constraintAllowedValues: {
      alfType: "item",
      alfProperty: "constraintAllowedValues"
   },
   constraintSorted: {
      alfType: "item",
      alfProperty: "constraintSorted"
   },
   constraintClass: {
      alfType: "item",
      alfProperty: "constraintClass"
   }
};

/**
 * Actions menu for the properties listing
 */
properties.actionsMenu = {
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
                                 label: "cmm.button.edit",
                                 iconClass: "cmm-icon-edit",
                                 publishTopic: "ALF_CREATE_FORM_DIALOG_REQUEST",
                                 publishGlobal: true,
                                 publishPayloadType: "BUILD",
                                 publishPayload: {
                                    dialogId: "CMM_EDIT_PROPERTY_DIALOG",
                                    dialogTitle: "cmm.property.edit-title",
                                    dialogConfirmationButtonTitle: "cmm.button.save-changes",
                                    dialogConfirmationButtonId: "CMM_EDIT_PROPERTY_DIALOG_SAVE",
                                    dialogCancellationButtonTitle: "cmm.button.cancel",
                                    dialogCancellationButtonId: "CMM_EDIT_PROPERTY_DIALOG_CANCEL",
                                    dialogCloseTopic: "CMM_EDIT_PROPERTY_SUCCESS",
                                    additionalCssClasses: "edit-form-dialog",
                                    formSubmissionTopic: "CMM_EDIT_PROPERTY",
                                    fixedWidth: true,
                                    showValidationErrorsImmediately: false,
                                    widgets: properties.editForm,
                                    formValue: properties.propertyFormValue
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
                                    dialogId: "CMM_EDIT_PROPERTY_DIALOG",
                                    dialogTitle: "cmm.property.edit-title",
                                    dialogConfirmationButtonTitle: "cmm.button.save-changes",
                                    dialogConfirmationButtonId: "CMM_EDIT_PROPERTY_DIALOG_SAVE",
                                    dialogCancellationButtonTitle: "cmm.button.cancel",
                                    dialogCancellationButtonId: "CMM_EDIT_PROPERTY_DIALOG_CANCEL",
                                    dialogCloseTopic: "CMM_EDIT_PROPERTY_SUCCESS",
                                    additionalCssClasses: "edit-form-dialog",
                                    formSubmissionTopic: "CMM_EDIT_PROPERTY",
                                    fixedWidth: true,
                                    showValidationErrorsImmediately: false,
                                    widgets: properties.editActiveForm,
                                    formValue: properties.propertyFormValue
                                 },
                                 renderFilter: [
                                    {
                                       property: "status",
                                       values: ["ACTIVE"],
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
                                 publishTopic: "CMM_DELETE_PROPERTY",
                                 publishGlobal: true,
                                 publishPayloadType: "PROCESS",
                                 publishPayloadModifiers: ["processCurrentItemTokens"],
                                 publishPayload: {
                                    name: "{name}",
                                    modelName: "{modelName}",
                                    type_name: "{type_name}",
                                    propertygroup_name: "{propertygroup_name}"
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
 * List layout for the properties listing
 */
properties.listLayout = {
   name: "alfresco/lists/views/AlfListView",
   config: {
      additionalCssClasses: "bordered",
      noItemsMessage: "cmm.message.no-properties",
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
               label: "cmm.header.type",
               sortable: false,
               additionalCssClasses: "datatypeColumn smallpad"
            }
         },
         {
            name: "alfresco/lists/views/layouts/HeaderCell",
            config: {
               label: "cmm.header.mandatory",
               sortable: false,
               additionalCssClasses: "mandatoryColumn smallpad"
            }
         },
         {
            name: "alfresco/lists/views/layouts/HeaderCell",
            config: {
               label: "cmm.header.defaultvalue",
               sortable: false,
               additionalCssClasses: "defaultvalueColumn smallpad"
            }
         },
         {
            name: "alfresco/lists/views/layouts/HeaderCell",
            config: {
               label: "cmm.header.multivalue",
               sortable: false,
               additionalCssClasses: "multivalueColumn smallpad"
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
                                 propertyToRender: "prefixedName",
                                 publishTopic: "ALF_CREATE_FORM_DIALOG_REQUEST",
                                 publishGlobal: true,
                                 useCurrentItemAsPayload: false,
                                 publishPayloadType: "BUILD",
                                 publishPayload: {
                                    dialogId: "CMM_EDIT_PROPERTY_DIALOG",
                                    dialogTitle: "cmm.property.edit-title",
                                    dialogConfirmationButtonTitle: "cmm.button.save-changes",
                                    dialogConfirmationButtonId: "CMM_EDIT_PROPERTY_DIALOG_SAVE",
                                    dialogCancellationButtonTitle: "cmm.button.cancel",
                                    dialogCancellationButtonId: "CMM_EDIT_PROPERTY_DIALOG_CANCEL",
                                    dialogCloseTopic: "CMM_EDIT_PROPERTY_SUCCESS",
                                    additionalCssClasses: "edit-form-dialog",
                                    formSubmissionTopic: "CMM_EDIT_PROPERTY",
                                    fixedWidth: true,
                                    showValidationErrorsImmediately: false,
                                    widgets: properties.editForm,
                                    formValue: properties.propertyFormValue
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
                              name: "alfresco/renderers/PropertyLink",
                              config: {
                                 renderedValueClass: "alfresco-renderers-Property",
                                 propertyToRender: "prefixedName",
                                 publishTopic: "ALF_CREATE_FORM_DIALOG_REQUEST",
                                 publishGlobal: true,
                                 useCurrentItemAsPayload: false,
                                 publishPayloadType: "BUILD",
                                 publishPayload: {
                                    dialogId: "CMM_EDIT_PROPERTY_DIALOG",
                                    dialogTitle: "cmm.property.edit-title",
                                    dialogConfirmationButtonTitle: "cmm.button.save-changes",
                                    dialogConfirmationButtonId: "CMM_EDIT_PROPERTY_DIALOG_SAVE",
                                    dialogCancellationButtonTitle: "cmm.button.cancel",
                                    dialogCancellationButtonId: "CMM_EDIT_PROPERTY_DIALOG_CANCEL",
                                    dialogCloseTopic: "CMM_EDIT_PROPERTY_SUCCESS",
                                    additionalCssClasses: "edit-form-dialog",
                                    formSubmissionTopic: "CMM_EDIT_PROPERTY",
                                    fixedWidth: true,
                                    showValidationErrorsImmediately: false,
                                    widgets: properties.editActiveForm,
                                    formValue: properties.propertyFormValue
                                 },
                                 renderFilter: [
                                    {
                                       property: "status",
                                       values: ["ACTIVE"],
                                       renderOnAbsentProperty: true
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
                        additionalCssClasses: "displayLabelColumn smallpad",
                        widgets: [
                           {
                              name: "alfresco/renderers/Property",
                              config: {
                                 renderedValueClass: "alfresco-renderers-Property",
                                 propertyToRender: "title"
                              }
                           }
                        ]
                     }
                  },
                  {
                     name: "alfresco/lists/views/layouts/Cell",
                     config: {
                        additionalCssClasses: "datatypeColumn smallpad",
                        widgets: [
                           {
                              name: "alfresco/renderers/Property",
                              config: {
                                 renderedValueClass: "alfresco-renderers-Property",
                                 propertyToRender: "dataType"
                              }
                           }
                        ]
                     }
                  },
                  {
                     name: "alfresco/lists/views/layouts/Cell",
                     config: {
                        additionalCssClasses: "mandatoryColumn smallpad",
                        widgets: [
                           {
                              name: "alfresco/renderers/Property",
                              config: {
                                 renderedValueClass: "alfresco-renderers-Property",
                                 propertyToRender: "mandatoryDsp"
                              }
                           }
                        ]
                     }
                  },
                  {
                     name: "alfresco/lists/views/layouts/Cell",
                     config: {
                        additionalCssClasses: "defaultvalueColumn smallpad",
                        widgets: [
                           {
                              name: "alfresco/renderers/Property",
                              config: {
                                 renderedValueClass: "alfresco-renderers-Property",
                                 propertyToRender: "defaultValue"
                              }
                           }
                        ]
                     }
                  },
                  {
                     name: "alfresco/lists/views/layouts/Cell",
                     config: {
                        additionalCssClasses: "multivalueColumn smallpad",
                        widgets: [
                           {
                              name: "alfresco/renderers/Boolean",
                              config: {
                                 renderedValueClass: "alfresco-renderers-Property",
                                 propertyToRender: "multiValued",
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
                           properties.actionsMenu
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
 * The properties listing
 */
properties.list = {
   id: "PROPERTIES_LIST",
   name: "cmm/lists/CMMTPGPropertiesList",
   config: {
      loadDataPublishTopic: "CMM_GET_PROPERTIES",
      pubSubScope: "PROPERTIES_LIST_",
      reloadDataTopic: "CMM_RELOAD_PROPERTIES",
      itemsProperty: "entry.properties",
      useHash: true,
      // Trigger a data update only when these hash variables change
      hashVarsForUpdate: [
         "model",
         "type",
         "propertygroup"
      ],
      // Only proceed with the data update when the hashVar 'view' is equal to 'properties'
      hashVarsForUpdateMustEqual: [
         {
            name: "view",
            value: "properties"
         }
      ],
      noDataMessage: msg.get("cmm.message.no-properties-found"),
      dataFailureMessage: msg.get("cmm.message.error-loading-properties"),
      widgets: [
         properties.listLayout
      ]
   }
};
