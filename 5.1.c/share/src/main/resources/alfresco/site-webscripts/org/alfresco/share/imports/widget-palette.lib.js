
// This function can be used to create the form fields that are common to all widgets for determining
// whether or not the widget should be rendered...
function getRenderFilterConfig() {
   var rfc = [
      {
         name: "alfresco/forms/controls/DojoCheckBox",
         config: {
            fieldId: "SHOW_RENDERING_CONFIG",
            name: "showRenderingConfig",
            label: "Define rendering rules",
            description: "Check this box to configure the rules that determine whether or not this widget is rendered",
            value: false
         }
      },
      {
         name: "alfresco/forms/controls/DojoRadioButtons",
         config: {
            fieldId: "TARGET_FIELD",
            name: "defaultConfig.renderFilterMethod",
            label: "Condition handling",
            description: "Must all the conditions be true, or can just one condition be true to be rendered",
            value: "ALL",
            postWhenHiddenOrDisabled: false,
            optionsConfig: {
               fixed: [
                  { label: "All conditions must be true", value: "ALL"},
                  { label: "Only one condition needs to be true", value: "ANY"}
               ]
            },
            visibilityConfig: {
               initialValue: false,
               rules: [
                  {
                     targetId: "SHOW_RENDERING_CONFIG",
                     is: [true]
                  }
               ]
            }
         }
      },
      {
         name: "alfresco/forms/controls/MultipleEntryFormControl",
         config: {
            name: "defaultConfig.renderFilter",
            label: "Render filter configuration",
            description: "Define the conditions in which the widget should be rendered",
            postWhenHiddenOrDisabled: false,
            widgets: [
               {
                  name: "alfresco/forms/controls/DojoValidationTextBox",
                  config: {
                     label: "Enter a name for the rule",
                     description: "Please provide a name for this render condition",
                     name: "value",
                     value: "",
                     postWhenHiddenOrDisabled: false,
                     requirementConfig: {
                        initialValue: true
                     }
                  }
               },
               {
                  name: "alfresco/forms/controls/DojoRadioButtons",
                  config: {
                     fieldId: "TARGET_FIELD",
                     name: "target",
                     label: "Target Object Property",
                     description: "Select from a list of well-known object properties of the widget or choose to provide a custom object",
                     value: "groupMemberships",
                     noPostWhenValueIs: ["CUSTOM"],
                     optionsConfig: {
                        fixed: [
                           { label: "Group Membership", value: "groupMemberships"},
                           { label: "Current Item", value: "currentItem"},
                           { label: "Custom Attribute", value: "CUSTOM"}
                        ]
                     }
                  }
               },
               {
                  name: "alfresco/forms/controls/DojoValidationTextBox",
                  config: {
                     label: "Custom Target Object Property",
                     name: "target",
                     description: "Enter the target object property of the widget that should be tested",
                     value: "",
                     postWhenHiddenOrDisabled: false,
                     visibilityConfig: {
                        initialValue: false,
                        rules: [
                           {
                              targetId: "TARGET_FIELD",
                              is: ["CUSTOM"]
                           }
                        ]
                     }
                  }
               },
               {
                  name: "alfresco/forms/controls/DojoSelect",
                  config: {
                     fieldId: "GROUP_SELECT",
                     name: "property",
                     label: "User Group",
                     description: "Select the group to test the users membership of",
                     value: "GROUP_ALFRESCO_ADMINISTRATORS",
                     postWhenHiddenOrDisabled: false,
                     optionsConfig: {
                        publishTopic: "ALF_GET_FORM_CONTROL_OPTIONS",
                        publishPayload: {
                           url: url.context + "/proxy/alfresco/api/groups",
                           itemsAttribute: "data",
                           labelAttribute: "displayName",
                           valueAttribute: "fullName"
                        }
                        // ,
                        // fixed: [
                        //    { label: "Administrators", value: "GROUP_ALFRESCO_ADMINISTRATORS"},
                        //    { label: "Search Administrators", value: "GROUP_SEARCH_ADMINISTRATORS"}
                        // ]
                     },
                     visibilityConfig: {
                        initialValue: false,
                        rules: [
                           {
                              targetId: "TARGET_FIELD",
                              is: ["groupMemberships"]
                           }
                        ]
                     }
                  }
               },
               {
                  name: "alfresco/forms/controls/DojoValidationTextBox",
                  config: {
                     label: "Property to Compare",
                     description: "Enter the property of the object to test the value of (using a dot-notation style)",
                     name: "property",
                     value: "",
                     postWhenHiddenOrDisabled: false,
                     visibilityConfig: {
                        initialValue: false,
                        rules: [
                           {
                              targetId: "TARGET_FIELD",
                              is: ["CUSTOM","currentItem"]
                           }
                        ]
                     }
                  }
               },
               {
                  name: "alfresco/forms/controls/MultipleEntryFormControl",
                  config: {
                     name: "values",
                     label: "Values To Test",
                     description: "Add values that the property must be in order for the condition to be satisfied",
                     useSimpleValues: true
                  }
               },
               {
                  name: "alfresco/forms/controls/DojoCheckBox",
                  config: {
                     name: "negate",
                     label: "Negative Rule?",
                     description: "Check this box if you want this rule to prevent rather than ensure rendering",
                     value: false
                  }
               }
            ],
            visibilityConfig: {
               initialValue: false,
               rules: [
                  {
                     targetId: "SHOW_RENDERING_CONFIG",
                     is: [true]
                  }
               ]
            }
         }
      }
   ];
   return rfc;
}



function getCommonTopics() {
   return [
      {label:"Get all items in QuADDS",value:"ALF_GET_QUADDS_ITEMS"},
      {label:"Create a new QuADDS item",value:"ALF_CREATE_QUADDS_ITEM"},
      {label:"Update an existing QuADDS item",value:"ALF_UPDATE_QUADDS_ITEM"},
      {label:"Delete an existing QuADDS item",value:"ALF_DELETE_QUADDS_ITEM"},
      {label:"Reload data",value:"ALF_DOCLIST_RELOAD_DATA"},
      {label:"Switch to CRUD Create view",value:"ALF_CRUD_FORM_CREATE"},
      {label:"Switch to CRUD Update view",value:"ALF_CRUD_FORM_UPDATE"},
      {label:"Set search term", value:"ALF_SET_SEARCH_TERM"}
   ];
}

function getPublishTopicsConfig() {
   return [{
      name: "alfresco/forms/controls/DojoRadioButtons",
      config: {
         fieldId: "TOPIC_TYPE",
         name: "topicType",
         label: "Publish Topic Type",
         value: "CUSTOM",
         noValueUpdateWhenHiddenOrDisabled: false,
         postWhenHiddenOrDisabled: true,
         optionsConfig: {
            fixed: [
               {
                  value: "CUSTOM",
                  label: "Custom"
               },
               {
                  value: "SELECT",
                  label: "Select from list"
               }
            ]
         }
      }
   },
   {
      name: "alfresco/forms/controls/DojoValidationTextBox",
      config: {
         fieldId: "CUSTOM_PUBLISH_TOPIC",
         name: "defaultConfig.publishTopic",
         label: "Publish Topic",
         placeHolder: "Topic",
         postWhenHiddenOrDisabled: false,
         noValueUpdateWhenHiddenOrDisabled: false,
         visibilityConfig: {
            rules: [
               {
                  is: [
                     {
                        value: "CUSTOM"
                     }
                  ],
                  targetId: "TOPIC_TYPE"
               }
            ],
            initialValue: true
         },
         unitsLabel: "",
         description: "Enter the topic to be published",
         value: ""
      }
   },
   {
      name: "alfresco/forms/controls/DojoSelect",
      config: {
         noValueUpdateWhenHiddenOrDisabled: true,
         postWhenHiddenOrDisabled: false,
         name: "defaultConfig.publishTopic",
         label: "Select a topic from the list",
         value: "",
         visibilityConfig: {
            rules: [
               {
                  targetId: "TOPIC_TYPE",
                  is: [
                     {
                        value: "SELECT"
                     }
                  ]
               }
            ],
            initialValue: true
         },
         optionsConfig: {
            fixed: getCommonTopics()
         }
      }
   }];
}

function getPublishPayloadConfig() {
   return [{
      name: "alfresco/forms/controls/DojoRadioButtons",
      config: {
         fieldId: "PAYLOAD_CONFIG_TYPE",
         name: "payloadConfigurationType",
         label: "Publish Payload Type",
         value: "CUSTOM",
         noValueUpdateWhenHiddenOrDisabled: false,
         postWhenHiddenOrDisabled: true,
         optionsConfig: {
            fixed: [
               {
                  value: "CUSTOM",
                  label: "Custom"
               },
               {
                  value: "SELECT",
                  label: "Select from list"
               }
            ]
         }
      }
   },
   {
      name: "alfresco/forms/controls/DojoSelect",
      config: {
         fieldId: "PRE_DEFINED_PAYLOAD_SELECT",
         name: "predefinedPayloadConfig",
         label: "Select the pre-defined payload type",
         value: "DELETE_QUADDS_ITEM",
         noValueUpdateWhenHiddenOrDisabled: false,
         postWhenHiddenOrDisabled: true,
         optionsConfig: {
            fixed: [
               {label: "Delete QuADDS Item", value:"DELETE_QUADDS_ITEM"}
            ]
         },
         visibilityConfig: {
            rules: [
               {
                  targetId: "PAYLOAD_CONFIG_TYPE",
                  is: ["SELECT"]
               }
            ],
            initialValue: true
         }
      }
   },
   {
      name: "alfresco/forms/controls/DojoValidationTextBox",
      config: {
         name: "defaultConfig.publishPayload.quadds",
         label: "Enter QuADDS to use",
         value: "",
         noValueUpdateWhenHiddenOrDisabled: true,
         postWhenHiddenOrDisabled: false,
         visibilityConfig: {
            rules: [
               {
                  targetId: "PAYLOAD_CONFIG_TYPE",
                  is: ["SELECT"]
               },
               {
                  targetId: "PRE_DEFINED_PAYLOAD_SELECT",
                  is: ["DELETE_QUADDS_ITEM"]
               }
            ],
            initialValue: false
         }
      }
   },
   {
      name: "alfresco/forms/controls/DojoSelect",
      config: {
         fieldId: "PUBLISH_PAYLOAD_TYPE",
         name: "defaultConfig.publishPayloadType",
         label: "Select the publish payload type",
         value: "CONFIGURED",
         optionsConfig: {
            fixed: [
               {label: "Custom Configuration", value:"CONFIGURED"},
               {label: "Use Current Item", value: "CURRENT_ITEM"},
               {label: "Process", value: "PROCESS"},
               {label: "Build from config", value: "BUILD"}
            ]
         },
         visibilityConfig: {
            rules: [
               {
                  targetId: "PAYLOAD_CONFIG_TYPE",
                  is: ["CUSTOM"]
               }
            ],
            initialValue: true
         }
      }
   },
   {
      name: "alfresco/forms/controls/DojoCheckBox",
      config: {
         name: "defaultConfig.publishPayloadItemMixin",
         label: "Include the current item in the payload",
         value: false
         // ,
         // visibilityConfig: {
         //    rules: [
         //       {
         //          targetId: "PAYLOAD_CONFIG_TYPE",
         //          is: ["CUSTOM"]
         //       },
         //       {
         //          targetId: "PUBLISH_PAYLOAD_TYPE",
         //          isNot: ["CURRENT_ITEM"]
         //       }
         //    ],
         //    initialValue: false
         // }
      }
   },
   {
      name: "alfresco/forms/controls/MultipleKeyValuePairFormControl",
      config: {
         noValueUpdateWhenHiddenOrDisabled: true,
         postWhenHiddenOrDisabled: false,
         name: "defaultConfig.publishPayload",
         label: "Custom Payload",
         visibilityConfig: {
            rules: [
               {
                  targetId: "PAYLOAD_CONFIG_TYPE",
                  is: ["CUSTOM"]
               },
               {
                  targetId: "PUBLISH_PAYLOAD_TYPE",
                  isNot: ["CURRENT_ITEM"]
               }
            ],
            initialValue: false
         }
      }
   },
   {
      name: "alfresco/forms/controls/MultipleEntryFormControl",
      config: {
         noValueUpdateWhenHiddenOrDisabled: true,
         postWhenHiddenOrDisabled: false,
         name: "defaultConfig.publishPayloadModifiers",
         label: "Payload processing modifiers",
         visibilityConfig: {
            rules: [
               {
                  targetId: "PAYLOAD_CONFIG_TYPE",
                  is: ["CUSTOM"]
               },
               {
                  targetId: "PUBLISH_PAYLOAD_TYPE",
                  is: ["PROCESS"]
               }
            ],
            initialValue: false
         },
         widgets: [
            {
               name: "alfresco/forms/controls/DojoSelect",
               config: {
                  name: "value",
                  label: "Select the modifiers to use",
                  value: "",
                  optionsConfig: {
                     fixed: [
                        {label: "Substitute tokens with values from current item", value:"processCurrentItemTokens"},
                        {label: "Replace colons with underscores", value: "replaceColons"}
                     ]
                  }
               }
            }
         ]
      }
   }];
}

/* *********************************************************************************
 *                                                                                 *
 * GENERAL FUNCTIONS                                                               *
 *                                                                                 *
 ***********************************************************************************/

function getDocumentsSourceConfigWidgets() {
   return [{
      name: "alfresco/forms/controls/DojoRadioButtons",
      config: {
         name: "defaultConfig.type",
         label: "All or Just Documents",
         value: "all",
         optionsConfig: {
            fixed: [
               {label:"All",value:"all"},
               {label:"Documents",value:"documents"}
            ]
         }
      }
   },
   {
      name: "alfresco/forms/controls/DojoRadioButtons",
      config: {
         fieldId: "selectDataSource",
         name: "defaultConfig.dataSource",
         label: "Data Source",
         value: "siteToken",
         optionsConfig: {
            fixed: [
               {label:"Well known node",value:"wellKnown"},
               {label:"Specific Site",value:"specificSite"},
               {label:"Site URL token",value:"siteToken"},
               {label:"NodeRef URL tokens",value:"nodeRefTokens"},
               {label:"Custom NodeRef",value:"customNodeRef"}
            ]
         }
      }
   },
   {
      name: "alfresco/forms/controls/DojoSelect",
      config: {
         name: "defaultConfig.nodeRef",
         label: "Select well known node",
         value: "siteToken",
         postWhenHiddenOrDisabled: false,
         noValueUpdateWhenHiddenOrDisabled: true,
         optionsConfig: {
            fixed: [
               {label:"Company home",value:"alfresco://company/home"},
               {label:"User Home",value:"alfresco://user/home"},
               {label:"Shared Files",value:"alfresco://company/shared"}
            ]
         },
         visibilityConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "selectDataSource",
                  is: ["wellKnown"]
               }
            ]
         },
         requirementConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "selectDataSource",
                  is: ["wellKnown"]
               }
            ]
         }
      }
   },
   {
      name: "alfresco/forms/controls/DojoValidationTextBox",
      config: {
         name: "defaultConfig.nodeRef",
         description: "Enter a custom NodeRef in the form <store_type>://<store_id>/<id>",
         label: "Custom NodeRef",
         value: "",
         postWhenHiddenOrDisabled: false,
         noValueUpdateWhenHiddenOrDisabled: true,
         visibilityConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "selectDataSource",
                  is: ["customNodeRef"]
               }
            ]
         },
         requirementConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "selectDataSource",
                  is: ["customNodeRef"]
               }
            ]
         },
         validationConfig: {
            regex: "^[A-Za-z0-9-]+://[A-Za-z0-9-]+/[A-Za-z0-9-]+$"
         }
      }
   },
   {
      name: "alfresco/forms/controls/DojoValidationTextBox",
      config: {
         name: "defaultConfig.site",
         label: "Specific Site Shortname",
         value: "",
         postWhenHiddenOrDisabled: false,
         noValueUpdateWhenHiddenOrDisabled: true,
         visibilityConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "selectDataSource",
                  is: ["specificSite"]
               }
            ]
         },
         requirementConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "selectDataSource",
                  is: ["specificSite"]
               }
            ]
         }
      }
   },
   {
      name: "alfresco/forms/controls/DojoValidationTextBox",
      config: {
         name: "defaultConfig.site",
         label: "Site URL token",
         value: "$$site$$",
         postWhenHiddenOrDisabled: false,
         noValueUpdateWhenHiddenOrDisabled: true,
         visibilityConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "selectDataSource",
                  is: ["siteToken"]
               }
            ]
         },
         requirementConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "selectDataSource",
                  is: ["siteToken"]
               }
            ]
         }
      }
   },
   {
      name: "alfresco/forms/controls/DojoValidationTextBox",
      config: {
         name: "defaultConfig.nodeRef",
         label: "NodeRef URL Tokens",
         value: "$$store_type$$://$$store_id$$/$$id$$",
         postWhenHiddenOrDisabled: false,
         noValueUpdateWhenHiddenOrDisabled: true,
         visibilityConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "selectDataSource",
                  is: ["nodeRefTokens"]
               }
            ]
         },
         requirementConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "selectDataSource",
                  is: ["nodeRefTokens"]
               }
            ]
         }
      }
   },
   {
      name: "alfresco/forms/controls/DojoValidationTextBox",
      config: {
         name: "defaultConfig.container",
         label: "Container Type",
         value: "documentlibrary"
      }
   },
   {
      name: "alfresco/forms/controls/DojoValidationTextBox",
      config: {
         name: "defaultConfig.page",
         label: "Page",
         value: "1",
         requirementConfig: {
            initialValue: true
         },
         validationConfig: {
            regex: "^[0-9]+$"
         }
      }
   },
   {
      name: "alfresco/forms/controls/DojoRadioButtons",
      config: {
         fieldId: "selectFilter",
         name: "defaultConfig.filter.filterId",
         label: "Filter",
         value: "path",
         optionsConfig: {
            fixed: [
               {label:"Path",value:"path"},
               {label:"All",value:"all"}
            ]
         }
      }
   },
   {
      name: "alfresco/forms/controls/DojoValidationTextBox",
      config: {
         name: "defaultConfig.filter.filterData",
         label: "Path",
         value: "/",
         postWhenHiddenOrDisabled: false,
         visibilityConfig: {
            initialValue: true,
            rules: [
               {
                  targetId: "selectFilter",
                  is: ["path"]
               }
            ]
         },
         requirementConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "selectFilter",
                  is: ["path"]
               }
            ]
         }
      }
   },
   {
      name: "alfresco/forms/controls/DojoValidationTextBox",
      config: {
         name: "defaultConfig.pageSize",
         label: "Number of results",
         value: "25",
         requirementConfig: {
            initialValue: true
         },
         validationConfig: {
            regex: "^[0-9]+$"
         }
      }
   },
   {
      name: "alfresco/forms/controls/DojoSelect",
      config: {
         name: "defaultConfig.sortAscending",
         label: "Sort Order",
         value: "true",
         optionsConfig: {
            fixed: [
               {label:"Ascending",value:"true"},
               {label:"Descending",value:"false"}
            ]
         }
      }
   },
   {
      name: "alfresco/forms/controls/DojoSelect",
      config: {
         name: "defaultConfig.sortField",
         label: "Sort Property",
         value: "all",
         optionsConfig: {
            fixed: [
               {label:"Name",value:"cm:name"},
               {label:"Popularity",value:"cm:likesRatingSchemeCount"},
               {label:"Last Modification",value:"cm:modified"},
               {label:"Size",value:"cm:content.size"}
            ]
         }
      }
   }];
}

/* *********************************************************************************
 *                                                                                 *
 * PUBLICATIONS                                                                    *
 *                                                                                 *
 ***********************************************************************************/

function getRetrieveSingleDocumentPublication() {
   return {
      type: ["publication"],
      name: "Retrieve Single Document",
      module: "ALF_RETRIEVE_SINGLE_DOCUMENT_REQUEST",
      itemNameKey: "publishTopic",
      itemConfigKey: "publishPayload",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {
         nodeRef: "alfresco://company/home"
      },
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoRadioButtons",
            config: {
               fieldId: "selectDataSource",
               name: "defaultConfig.dataSource",
               label: "Data Source",
               value: "nodeRefTokens",
               optionsConfig: {
                  fixed: [
                     {label:"NodeRef URL tokens",value:"nodeRefTokens"},
                     {label:"Custom NodeRef",value:"customNodeRef"}
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.nodeRef",
               label: "NodeRef URL Tokens",
               value: "$$store_type$$://$$store_id$$/$$id$$",
               postWhenHiddenOrDisabled: false,
               noValueUpdateWhenHiddenOrDisabled: true,
               visibilityConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "selectDataSource",
                        is: ["nodeRefTokens"]
                     }
                  ]
               },
               requirementConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "selectDataSource",
                        is: ["nodeRefTokens"]
                     }
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.nodeRef",
               description: "Enter a custom NodeRef in the form <store_type>://<store_id>/<id>",
               label: "Custom NodeRef",
               value: "",
               postWhenHiddenOrDisabled: false,
               noValueUpdateWhenHiddenOrDisabled: true,
               visibilityConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "selectDataSource",
                        is: ["customNodeRef"]
                     }
                  ]
               },
               requirementConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "selectDataSource",
                        is: ["customNodeRef"]
                     }
                  ]
               },
               validationConfig: {
                  regex: "^[A-Za-z0-9-]+://[A-Za-z0-9-]+/[A-Za-z0-9-]+$"
               }
            }
         }
      ],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,
      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Retrieve Single Document"
            }
         }
      ]
   }
}

function getRetrieveDocumentsPublication() {
   return {
      type: ["publication"],
      name: "Retrieve Documents",
      module: "ALF_RETRIEVE_DOCUMENTS_REQUEST",
      itemNameKey: "publishTopic",
      itemConfigKey: "publishPayload",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {
         path: "/",
         type: "all",
         site: "$$site$$",
         container: "documentlibrary",
         page: "1",
         pageSize: "25",
         sortAscending: "false",
         sortField: "cm:name",
         filter: {
            filterId: "path",
            filterData: ""
         }
      },
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [
         // {
         //    name: "alfresco/forms/controls/DojoValidationTextBox",
         //    config: {
         //       name: "defaultConfig.path",
         //       label: "Path",
         //       value: "/"
         //    }
         // },
         {
            name: "alfresco/forms/controls/DojoRadioButtons",
            config: {
               name: "defaultConfig.type",
               label: "All or Just Documents",
               value: "all",
               optionsConfig: {
                  fixed: [
                     {label:"All",value:"all"},
                     {label:"Documents",value:"documents"}
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoRadioButtons",
            config: {
               fieldId: "selectDataSource",
               name: "defaultConfig.dataSource",
               label: "Data Source",
               value: "siteToken",
               optionsConfig: {
                  fixed: [
                     {label:"Well known node",value:"wellKnown"},
                     {label:"Specific Site",value:"specificSite"},
                     {label:"Site URL token",value:"siteToken"},
                     {label:"NodeRef URL tokens",value:"nodeRefTokens"},
                     {label:"Custom NodeRef",value:"customNodeRef"}
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.nodeRef",
               label: "Select well known node",
               value: "siteToken",
               postWhenHiddenOrDisabled: false,
               noValueUpdateWhenHiddenOrDisabled: true,
               optionsConfig: {
                  fixed: [
                     {label:"Company home",value:"alfresco://company/home"},
                     {label:"User Home",value:"alfresco://user/home"},
                     {label:"Shared Files",value:"alfresco://company/shared"}
                  ]
               },
               visibilityConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "selectDataSource",
                        is: ["wellKnown"]
                     }
                  ]
               },
               requirementConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "selectDataSource",
                        is: ["wellKnown"]
                     }
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.nodeRef",
               description: "Enter a custom NodeRef in the form <store_type>://<store_id>/<id>",
               label: "Custom NodeRef",
               value: "",
               postWhenHiddenOrDisabled: false,
               noValueUpdateWhenHiddenOrDisabled: true,
               visibilityConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "selectDataSource",
                        is: ["customNodeRef"]
                     }
                  ]
               },
               requirementConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "selectDataSource",
                        is: ["customNodeRef"]
                     }
                  ]
               },
               validationConfig: {
                  regex: "^[A-Za-z0-9-]+://[A-Za-z0-9-]+/[A-Za-z0-9-]+$"
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.site",
               label: "Specific Site Shortname",
               value: "",
               postWhenHiddenOrDisabled: false,
               noValueUpdateWhenHiddenOrDisabled: true,
               visibilityConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "selectDataSource",
                        is: ["specificSite"]
                     }
                  ]
               },
               requirementConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "selectDataSource",
                        is: ["specificSite"]
                     }
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.site",
               label: "Site URL token",
               value: "$$site$$",
               postWhenHiddenOrDisabled: false,
               noValueUpdateWhenHiddenOrDisabled: true,
               visibilityConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "selectDataSource",
                        is: ["siteToken"]
                     }
                  ]
               },
               requirementConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "selectDataSource",
                        is: ["siteToken"]
                     }
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.nodeRef",
               label: "NodeRef URL Tokens",
               value: "$$store_type$$://$$store_id$$/$$id$$",
               postWhenHiddenOrDisabled: false,
               noValueUpdateWhenHiddenOrDisabled: true,
               visibilityConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "selectDataSource",
                        is: ["nodeRefTokens"]
                     }
                  ]
               },
               requirementConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "selectDataSource",
                        is: ["nodeRefTokens"]
                     }
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.container",
               label: "Container Type",
               value: "documentlibrary"
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.page",
               label: "Page",
               value: "1",
               requirementConfig: {
                  initialValue: true
               },
               validationConfig: {
                  regex: "^[0-9]+$"
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoRadioButtons",
            config: {
               fieldId: "selectFilter",
               name: "defaultConfig.filter.filterId",
               label: "Filter",
               value: "path",
               optionsConfig: {
                  fixed: [
                     {label:"Path",value:"path"},
                     {label:"All",value:"all"}
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.filter.filterData",
               label: "Path",
               value: "/",
               postWhenHiddenOrDisabled: false,
               visibilityConfig: {
                  initialValue: true,
                  rules: [
                     {
                        targetId: "selectFilter",
                        is: ["path"]
                     }
                  ]
               },
               requirementConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "selectFilter",
                        is: ["path"]
                     }
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.pageSize",
               label: "Number of results",
               value: "25",
               requirementConfig: {
                  initialValue: true
               },
               validationConfig: {
                  regex: "^[0-9]+$"
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.sortAscending",
               label: "Sort Order",
               value: "true",
               optionsConfig: {
                  fixed: [
                     {label:"Ascending",value:"true"},
                     {label:"Descending",value:"false"}
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.sortField",
               label: "Sort Property",
               value: "all",
               optionsConfig: {
                  fixed: [
                     {label:"Name",value:"cm:name"},
                     {label:"Popularity",value:"cm:likesRatingSchemeCount"},
                     {label:"Last Modification",value:"cm:modified"},
                     {label:"Size",value:"cm:content.size"}
                  ]
               }
            }
         }
      ],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,
      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Retrieve Documents"
            }
         }
      ]
   };
}

function getDialogRequestPublication() {
   return {
      type: ["publication"],
      name: "Dialog Request",
      module: "ALF_CREATE_FORM_DIALOG_REQUEST",
      itemNameKey: "publishTopic",
      itemConfigKey: "publishPayload",
      itemDroppedItemsKey: "publishPayload.widgets",
      defaultConfig: {
         dialogTitle: "Default Title",
         dialogConfirmationButtonTitle: "OK",
         dialogCancellationButtonTitle: "Cancel",
         formSubmissionTopic: "ALF_CREATE_CONTENT_REQUEST"
      },
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               fieldId: "test",
               name: "defaultConfig.dialogTitle",
               label: "Dialog Title",
               value: "Default Title"
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.dialogConfirmationButtonTitle",
               label: "Confirmation Button Label",
               value: "OK"
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.dialogCancellationButtonTitle",
               label: "Cancellation Button Label",
               value: "Cancel"
            }
         },
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.formSubmissionTopic",
               label: "Dialog Confirmation Topic",
               value: "ALF_CREATE_CONTENT_REQUEST",
               postWhenHiddenOrDisabled: false,
               noValueUpdateWhenHiddenOrDisabled: true,
               optionsConfig: {
                  fixed: getCommonTopics()
               }
            }
         }
      ],
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               acceptTypes: ["widget"],
               horizontal: false
            }
         }
      ]
   };
}

function getNavigationRequestPublication() {
   return {
      type: ["publication"],
      name: "Navigation Request",
      module: "ALF_NAVIGATE_TO_PAGE",
      itemNameKey: "publishTopic",
      itemConfigKey: "publishPayload",
      itemDroppedItemsKey: "publishPayload.widgets",
      defaultConfig: {
         url: "",
         type: "SHARE_PAGE_RELATIVE",
         target: "CURRENT"
      },
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.url",
               label: "The URL to use",
               value: "",
               requirementConfig: {
                  initialValue: true
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoRadioButtons",
            config: {
               name: "defaultConfig.type",
               label: "URL type",
               value: "SHARE_PAGE_RELATIVE",
               optionsConfig: {
                  fixed: [
                     {label:"Relative to Alfresco Share pages",value:"SHARE_PAGE_RELATIVE"},
                     {label:"Relative to Share application",value:"CONTEXT_RELATIVE"},
                     {label:"A full external path",value:"FULL_PATH"},
                     {label:"Hash the current URL",value:"HASH"}
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoRadioButtons",
            config: {
               name: "defaultConfig.target",
               label: "Where to load URL",
               value: "CURRENT",
               optionsConfig: {
                  fixed: [
                     {label:"Current Window",value:"CURRENT"},
                     {label:"New Window",value:"NEW"}
                  ]
               }
            }
         }
      ],
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Navigate"
            }
         }
      ]
   };
}

function getAllPublications() {
   return [
      getRetrieveSingleDocumentPublication(),
      getRetrieveDocumentsPublication(),
      getDialogRequestPublication(),
      getNavigationRequestPublication()
   ];
}

/* *********************************************************************************
 *                                                                                 *
 * SERVICES                                                                        *
 *                                                                                 *
 ***********************************************************************************/

function getNavigationService() {
   return {
      type: ["service"],
      name: "Navigation Service",
      module: "alfresco/services/NavigationService",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {},
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,
      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Navigation Service"
            }
         }
      ]
   };
}

function getOptionsService() {
   return {
      type: ["service"],
      name: "Options Service",
      module: "alfresco/services/OptionsService",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {},
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,
      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Options Service"
            }
         }
      ]
   };
}

function getActionService() {
   return {
      type: ["service"],
      name: "Action Service",
      module: "alfresco/services/ActionService",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {},
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,
      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Action Service"
            }
         }
      ]
   };
}

function getContentService() {
   return {
      type: ["service"],
      name: "Content Service",
      module: "alfresco/services/ContentService",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {},
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,
      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Content Service"
            }
         }
      ]
   };
}

function getDocumentService() {
   return {
      type: ["service"],
      name: "Document Service",
      module: "alfresco/services/DocumentService",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {},
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,
      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Document Service"
            }
         }
      ]
   };
}

function getDialogService() {
   return {
      type: ["service"],
      name: "Dialog Service",
      module: "alfresco/dialogs/AlfDialogService",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {},
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,
      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Dialog Service"
            }
         }
      ]
   };
}

function getLoggingService() {
   return {
      type: ["service"],
      name: "Logging Service",
      module: "alfresco/services/LoggingService",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {},
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,
      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Logging Service"
            }
         }
      ]
   };
}

function getPageService() {
   return {
      type: ["service"],
      name: "Page Service",
      module: "alfresco/services/PageService",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {},
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,
      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Page Service"
            }
         }
      ]
   };
}

function getPreferenceService() {
   return {
      type: ["service"],
      name: "Preference Service",
      module: "alfresco/services/PreferenceService",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {},
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,
      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Preference Service"
            }
         }
      ]
   };
}

function getSiteService() {
   return {
      type: ["service"],
      name: "Site Service",
      module: "alfresco/services/SiteService",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {},
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,
      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Site Service"
            }
         }
      ]
   };
}

function getUserService() {
   return {
      type: ["service"],
      name: "User Service",
      module: "alfresco/services/UserService",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {},
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,
      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "User Service"
            }
         }
      ]
   };
}

function getQuaddsService() {
   return {
      type: ["service"],
      name: "QuADDS Service",
      module: "alfresco/services/QuaddsService",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {},
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,
      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "QuADDS Service"
            }
         }
      ]
   };
}

function getSearchService() {
   return {
      type: ["service"],
      name: "Search Service",
      module: "alfresco/services/SearchService",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {},
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,
      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Search Service"
            }
         }
      ]
   };
}

function getNotificationService() {
   return {
      type: ["service"],
      name: "Notification Service",
      module: "alfresco/services/NotificationService",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {},
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,
      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Notification Service"
            }
         }
      ]
   };
}

function getAllServices() {
   return [
      getNavigationService(),
      getOptionsService(),
      getActionService(),
      getContentService(),
      getDocumentService(),
      getDialogService(),
      getLoggingService(),
      getPageService(),
      getPreferenceService(),
      getSiteService(),
      getUserService(),
      getQuaddsService(),
      getSearchService(),
      getNotificationService()
   ];
}

/* *********************************************************************************
 *                                                                                 *
 * GENERAL WIDGETS                                                                 *
 *                                                                                 *
 ***********************************************************************************/

function getSetTitleWidget() {
   return {
      type: ["widget"],
      name: "Page Title",
      module: "alfresco/header/SetTitle",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {
         title: ""
      },
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.title",
               label: "Page Title",
               value: ""
            }
         }
      ],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,
      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: true
            }
         }
      ]
   };
}

function getLogoWidget() {
   return {
      type: ["widget"],
      name: "Logo",
      module: "alfresco/logo/Logo",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {
         logoClasses: "alfresco-logo-large"
      },
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.logoClasses",
               label: "Logo Classes",
               value: "",
               optionsConfig: {
                  fixed: [
                     {label:"Standard Alfresco",value:"alfresco-logo-large"},
                     {label:"Alfresco Logo Only",value:"alfresco-logo-only"},
                     {label:"3D Alfresco",value:"alfresco-logo-3d"},
                     {label:"Surf Large",value:"surf-logo-large"},
                     {label:"Surf Small",value:"surf-logo-small"}
                  ]
               }
            }
         }
      ].concat(getRenderFilterConfig()),
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,
      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: true
            }
         }
      ]
   };
}

function getButtonWidget() {
   return {
      type: ["widget"],
      name: "Button",
      module: "alfresco/buttons/AlfButton",
      mixDroppedItemsIntoConfig: true,
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {
         label: "New Button"
      },
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.label",
               label: "Button Label",
               description: "Enter the label to be displayed on the button",
               value: ""
            }
         },
         {
            name: "alfresco/forms/controls/DojoRadioButtons",
            config: {
               fieldId: "BUTTON_TYPE",
               name: "buttonType",
               label: "Button Type",
               description: "Select the type of button required (custom or form request)",
               value: "CUSTOM",
               optionsConfig: {
                  fixed: [
                     { label: "Custom", value: "CUSTOM"},
                     { label: "Dialog Form Request", value: "REQUEST_FORM"}
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoRadioButtons",
            config: {
               fieldId: "TOPIC_TYPE",
               name: "topicType",
               label: "Publish Topic Type",
               description: "Select the type of button required (custom or form request)",
               value: "CUSTOM",
               optionsConfig: {
                  fixed: [
                     { label: "Custom Topic", value: "CUSTOM"},
                     { label: "Well Known Topic", value: "WELL_KNOWN"}
                  ]
               },
               visibilityConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "BUTTON_TYPE",
                        is: ["CUSTOM"]
                     }
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.publishTopic",
               label: "Custom Publish Topic",
               description: "Enter the topic that will be published on when the button is clicked",
               value: "",
               postWhenHiddenOrDisabled: false,
               visibilityConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "TOPIC_TYPE",
                        is: ["CUSTOM"]
                     },
                     {
                        targetId: "BUTTON_TYPE",
                        is: ["CUSTOM"]
                     }
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.publishTopic",
               label: "Publish Topic",
               description: "Select the topic that will be published when the button is clicked",
               value: "",
               postWhenHiddenOrDisabled: false,
               optionsConfig: {
                  fixed: getCommonTopics()
               },
               visibilityConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "TOPIC_TYPE",
                        is: ["WELL_KNOWN"]
                     },
                     {
                        targetId: "BUTTON_TYPE",
                        is: ["CUSTOM"]
                     }
                  ]
               }
            }
         }
      ],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,
      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/creation/PublicationDropZone",
            config: {
               horizontal: false
            }
         }
      ]
   };
}

function getQuaddsWidgets() {
   return {
      type: ["widget"],
      name: "QuADDS Widgets",
      module: "alfresco/quadds/QuaddsWidgets",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {
         quadds: ""
      },
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.quadds",
               label: "",
               value: ""
            }
         }
      ],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,
      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "QuADDS Widgets"
            }
         }
      ]
   };
}

function getFacetFilters() {
   return {
      type: [ "widget" ],
      name: "Facet Filters",
      module: "alfresco/search/FacetFilters",
      defaultConfig: {
         label: "",
         facetQName: "{http://www.alfresco.org/model/content/1.0}content.mimetype",
         maxFilters: 10,
         hitThreshold: 5,
         sortBy: "ALPHABETICALLY"
      },
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.label",
               label: "Facet Label",
               value: ""
            }
         },
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.facetQName",
               label: "Facet QName",
               value: "{http://www.alfresco.org/model/content/1.0}content.mimetype",
               optionsConfig: {
                  fixed: [
                    {label: "Description", value: "{http://www.alfresco.org/model/content/1.0}description.__"},
                    {label: "MIME Type", value: "{http://www.alfresco.org/model/content/1.0}content.mimetype"},
                    {label: "Modifier", value: "{http://www.alfresco.org/model/content/1.0}modifier.__"},
                    {label: "Creator", value: "{http://www.alfresco.org/model/content/1.0}creator.__"}
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.sortBy",
               label: "Sorted",
               value: "ALPHABETICALLY",
               optionsConfig: {
                  fixed: [
                    {label: "Alphabetically", value: "ALPHABETICALLY"},
                    {label: "Filter hits (low to high)", value: "ASCENDING"},
                    {label: "Filter hits (high to low)", value: "DESCENDING"}
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.maxFilters",
               label: "Max. Displayer Filters",
               value: 10
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.hitThreshold",
               label: "Hit Threshold",
               value: 5
            }
         }
      ],
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Facet Filters"
            }
         }
      ]
   };
}


function getGeneralWidgets() {
   return [
      getSetTitleWidget(),
      getLogoWidget(),
      getButtonWidget(),
      getQuaddsWidgets(),
      getFacetFilters()
   ];
}

/* *********************************************************************************
 *                                                                                 *
 * DOCUMENT LIST WIDGETS                                                           *
 *                                                                                 *
 ***********************************************************************************/

function getDocumentWidget() {
   return {
      type: ["widget"],
      name: "Document",
      module: "alfresco/documentlibrary/AlfDocument",
      defaultConfig: {},
      widgetsForConfig: [],
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: true
            }
         }
      ]
   };
}

function getDocumentListWidget() {
   return {
      type: ["widget"],
      name: "Document List",
      module: "alfresco/documentlibrary/AlfDocumentList",
      defaultConfig: {
         path: "/",
         type: "all",
         site: "$$site$$",
         container: "documentlibrary",
         page: "1",
         pageSize: "25",
         sortAscending: "false",
         sortField: "cm:name",
         filter: {
            filterId: "path",
            filterData: ""
         }
      },
      widgetsForConfig: getDocumentsSourceConfigWidgets(),
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: true
            }
         }
      ]
   };
}

function getSearchListWidget() {
   return {
      type: ["widget"],
      name: "Search Results List",
      module: "alfresco/documentlibrary/AlfSearchList",
      defaultConfig: {},
      widgetsForConfig: [],
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: true
            }
         }
      ]
   };
}

function getAbstractDocListViewWidget() {
   return {
      type: ["widget"],
      name: "Abstract Document List View",
      module: "alfresco/documentlibrary/views/AlfDocumentListView",
      defaultConfig: {},
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoRadioButtons",
            config: {
               name: "defaultConfig.additionalCssClasses",
               label: "Additional Style",
               description: "Select an additional style to apply to the view.",
               value: "",
               optionsConfig: {
                  fixed: [
                     {label:"None",value:""},
                     {label:"Borders",value:"bordered"}
                  ]
               }
            }
         },
      ],
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: true
            }
         }
      ]
   };
}

function getDocListRowWidget() {
   return {
      type: ["widget"],
      name: "Row (for Document List View)",
      module: "alfresco/documentlibrary/views/layouts/Row",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {},
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,

      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: true
            }
         }
      ]
   };
}

function getDocListColumnWidget() {
   return {
      type: ["widget"],
      name: "Column (for Document List View)",
      module: "alfresco/documentlibrary/views/layouts/Column",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {},
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,

      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: true
            }
         }
      ]
   };
}

function getDocListCellWidget() {
   return {
      type: ["widget"],
      name: "Cell (for Document List View)",
      module: "alfresco/documentlibrary/views/layouts/Cell",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {
         width: ""
      },
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.width",
               label: "Width",
               description: "Please provide a width (in pixels) for the cell",
               value: ""
            }
         }
      ],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,

      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: true
            }
         }
      ]
   };
}

function getPropertyWidget() {
   return {
      type: ["widget"],
      name: "Property (for Document List View)",
      module: "alfresco/renderers/Property",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {
         propertyToRender: "node.properties.cm:name",
         postParam: "prop_cm_name",
         renderSize: "medium",
         renderedValuePrefix: "",
         renderedValueSuffix: ""
      },
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoRadioButtons",
            config: {
               fieldId: "PROPERTY_TYPE",
               label: "Property Type",
               description: "Do you want to configure a custom property or a well-known Document property",
               value: "DOCUMENT",
               optionsConfig: {
                  fixed: [
                     {label:"Document Property",value:"DOCUMENT"},
                     {label:"Custom",value:"CUSTOM"},
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               label: "Property Name",
               description: "Enter the property to be displayed",
               name: "defaultConfig.propertyToRender",
               postWhenHiddenOrDisabled: false,
               requirementConfig: {
                  initialValue: true
               },
               visibilityConfig: {
                  rules: [
                     {
                        targetId: "PROPERTY_TYPE",
                        is: ["CUSTOM"]
                     }
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.propertyToRender",
               label: "Property to render",
               value: "node.properties.cm:name",
               postWhenHiddenOrDisabled: false,
               optionsConfig: {
                  fixed: [
                     {label:"Name",value:"node.properties.cm:name"},
                     {label:"Title",value:"node.properties.cm:title"},
                     {label:"Description",value:"node.properties.cm:description"},
                     {label:"Version",value:"node.properties.cm:versionLabel"}
                  ]
               },
               visibilityConfig: {
                  rules: [
                     {
                        targetId: "PROPERTY_TYPE",
                        is: ["DOCUMENT"]
                     }
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.postParam",
               label: "Parameter to post",
               value: "prop_cm_name",
               postWhenHiddenOrDisabled: false,
               optionsConfig: {
                  fixed: [
                     {label:"Name",value:"prop_cm_name"},
                     {label:"Title",value:"prop_cm_title"},
                     {label:"Description",value:"prop_cm_description"},
                     {label:"Version",value:"prop_cm_versionLabel"}
                  ]
               },
               visibilityConfig: {
                  rules: [
                     {
                        targetId: "PROPERTY_TYPE",
                        is: ["DOCUMENT"]
                     }
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.renderSize",
               label: "Render Size",
               value: "medium",
               optionsConfig: {
                  fixed: [
                     {label:"Small",value:"small"},
                     {label:"Medium",value:"medium"},
                     {label:"Large",value:"large"}
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.renderedValuePrefix",
               label: "Prefix",
               value: ""
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.renderedValueSuffix",
               label: "Suffix",
               value: ""
            }
         }
      ],
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: true
            }
         }
      ]
   };
}

function getConfigPropertyWidget() {
   return {
      type: ["widget"],
      name: "Config Property",
      module: "alfresco/renderers/Property",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {
         propertyToRender: "name",
         postParam: "name"
      },
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.propertyToRender",
               label: "Property to render",
               description: "Enter the property to be rendered. Dot-notation properties are allowed",
               value: ""
            }
         }
      ],
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: true
            }
         }
      ]
   };
}

function getPropertyLinkWidget() {
   return {
      type: ["widget"],
      name: "Property Link",
      module: "alfresco/renderers/PropertyLink",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {
         propertyToRender: "name",
         publishTopic: ""
      },
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.propertyToRender",
               label: "Property to render",
               value: ""
            }
         },
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.publishTopic",
               label: "Link Topic",
               value: "",
               optionsConfig: {
                  fixed: getCommonTopics()
               }
            }
         }
      ],
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: true
            }
         }
      ]
   };
}

function getInlineEditPropertyWidget() {
   return {
      type: ["widget"],
      name: "Editable Property (for Document List View)",
      module: "alfresco/renderers/InlineEditProperty",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {
         propertyToRender: "node.properties.cm:name",
         postParam: "prop_cm_name",
         renderSize: "medium",
         renderAsLink: false,
         renderedValuePrefix: "",
         renderedValueSuffix: ""
      },
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.propertyToRender",
               label: "Property to render",
               value: "node.properties.cm:name",
               optionsConfig: {
                  fixed: [
                     {label:"Name",value:"node.properties.cm:name"},
                     {label:"Title",value:"node.properties.cm:title"},
                     {label:"Description",value:"node.properties.cm:description"},
                     {label:"Version",value:"node.properties.cm:versionLabel"}
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.postParam",
               label: "Parameter to post",
               value: "prop_cm_name",
               optionsConfig: {
                  fixed: [
                     {label:"Name",value:"prop_cm_name"},
                     {label:"Title",value:"prop_cm_title"},
                     {label:"Description",value:"prop_cm_description"},
                     {label:"Version",value:"prop_cm_versionLabel"}
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.renderSize",
               label: "Render Size",
               value: "medium",
               optionsConfig: {
                  fixed: [
                     {label:"Small",value:"small"},
                     {label:"Medium",value:"medium"},
                     {label:"Large",value:"large"}
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoCheckBox",
            config: {
               name: "defaultConfig.renderAsLink",
               label: "Render as a link",
               value: false
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.renderedValuePrefix",
               label: "Prefix",
               value: ""
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.renderedValueSuffix",
               label: "Suffix",
               value: ""
            }
         }
      ],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,

      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: true
            }
         }
      ]
   };
}

function getThumbnailWidget() {
   return {
      type: ["widget"],
      name: "Thumbnail (for Document List View)",
      module: "alfresco/renderers/Thumbnail",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {},
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,

      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: true
            }
         }
      ]
   };
}

function getPublishActionWidget() {

   var widgetsForConfig = getPublishTopicsConfig();
   widgetsForConfig = widgetsForConfig.concat(getPublishPayloadConfig());
   widgetsForConfig.push({
      name: "alfresco/forms/controls/DojoSelect",
      config: {
         label: "Icon",
         name: "defaultConfig.iconClass",
         value: "add-icon-16",
         optionsConfig: {
            fixed: [
               { label: "Add", value: "add-icon-16"},
               { label: "Delete", value: "delete-16"}
            ]
         }
      }
   });

   return {
      type: ["widget"],
      name: "Single Publish Action",
      module: "alfresco/renderers/PublishAction",
      defaultConfig: {
         iconClass: "add-icon-16",
         publishTopic: ""
      },
      widgetsForConfig: widgetsForConfig,
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: true
            }
         }
      ]
   };
}

function getDocListActionsWidget() {
   return {
      type: ["widget"],
      name: "Actions (for Document List View)",
      module: "alfresco/renderers/Actions",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {},
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,

      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: true
            }
         }
      ]
   };
}

function getDocListSelectorWidget() {
   return {
      type: ["widget"],
      name: "Selector (for Document List View)",
      module: "alfresco/renderers/Selector",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {},
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,

      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: true
            }
         }
      ]
   };
}

function getDocListIndicatorsWidget() {
   return {
      type: ["widget"],
      name: "Indicators (for Document List View)",
      module: "alfresco/renderers/Indicators",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {},
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,

      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: true
            }
         }
      ]
   };
}

function getDocListDateWidget() {
   return {
      type: ["widget"],
      name: "Date (for Document List View)",
      module: "alfresco/renderers/Date",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {},
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,

      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: true
            }
         }
      ]
   };
}

function getDocListSizeWidget() {
   return {
      type: ["widget"],
      name: "Size (for Document List View)",
      module: "alfresco/renderers/Size",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {},
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,

      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: true
            }
         }
      ]
   };
}

function getDocListDetailedView() {
   return {
      type: ["widget"],
      name: "Detailed Document List View",
      module: "alfresco/documentlibrary/views/AlfDetailedView",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {},
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,

      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Detailed View"
            }
         }
      ]
   };
}

function getDocListSimpleView() {
   return {
      type: ["widget"],
      name: "Simple Document List View",
      module: "alfresco/documentlibrary/views/AlfSimpleView",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {},
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,

      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Simple View"
            }
         }
      ]
   };
}

function getDocumentPreview() {
   return {
      type: ["widget"],
      name: "Document Preview",
      module: "alfresco/preview/AlfDocumentPreview",
      defaultConfig: {},
      widgetsForConfig: [],
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Preview"
            }
         }
      ]
   };
}

function getFileTypeWidget() {
   return {
      type: ["widget"],
      name: "File Type Image",
      module: "alfresco/renderers/FileType",
      defaultConfig: {},
      widgetsForConfig: [],
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "FileType"
            }
         }
      ]
   };
}

function getQuaddsListWidget() {
   return {
      type: ["widget"],
      name: "QuADDS List",
      module: "alfresco/documentlibrary/QuaddsList",
      defaultConfig: {},
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.noDataMessage",
               label: "No items message",
               description: "Enter a message to display when no items are available",
               value: "No data available"
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.quadds",
               description: "Enter the name of the QuADDS that you wish to retrieve data from",
               label: "QuADDS",
               value: "quadds",
            }
         }
      ],
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: true
            }
         }
      ]
   };
}

function getAllDocListWidgets() {
   return [
      getQuaddsListWidget(),
      getDocumentWidget(),
      getDocumentPreview(),
      getDocumentListWidget(),
      getSearchListWidget(),
      getDocListSimpleView(),
      getDocListDetailedView(),
      getAbstractDocListViewWidget(),
      getDocListRowWidget(),
      getDocListCellWidget(),
      getDocListColumnWidget(),
      getConfigPropertyWidget(),
      getPropertyWidget(),
      getPropertyLinkWidget(),
      getInlineEditPropertyWidget(),
      getDocListSelectorWidget(),
      getDocListIndicatorsWidget(),
      getThumbnailWidget(),
      getFileTypeWidget(),
      getDocListSizeWidget(),
      getDocListDateWidget(),
      getDocListActionsWidget(),
      getPublishActionWidget()
   ];
}

/* *********************************************************************************
 *                                                                                 *
 * MENU WIDGETS                                                                    *
 *                                                                                 *
 ***********************************************************************************/

function getMenuBarWidget() {
   return {
      type: ["widget"],
      name: "Menu Bar",
      module: "alfresco/menus/AlfMenuBar",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {
      },
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [
      ],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,
      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: true
            }
         }
      ]
   };
}

function getMenuBarItemWidget() {
   return {
      type: ["widget"],
      name: "Menu Bar Item",
      module: "alfresco/menus/AlfMenuBarItem",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {
         label: "default",
         iconClass: "",
         altText: ""
      },
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.label",
               label: "Label",
               value: "default"
            }
         },
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.iconClass",
               label: "Icon",
               value: "",
               optionsConfig: {
                  fixed: [
                     {label:"None",value:""},
                     {label:"Configure",value:"alf-configure-icon"},
                     {label:"Invite User",value:"alf-user-icon"},
                     {label:"Upload",value:"alf-upload-icon"},
                     {label:"Create",value:"alf-create-icon"},
                     {label:"All Selected",value:"alf-allselected-icon"},
                     {label:"Some Selected",value:"alf-someselected-icon"},
                     {label:"None Selected",value:"alf-noneselected-icon"},
                     {label:"Back",value:"alf-back-icon"},
                     {label:"Forward",value:"alf-forward-icon"}
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoTextarea",
            config: {
               name: "defaultConfig.altText",
               label: "Alt Text",
               value: ""
            }
         }
      ],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,
      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: true
            }
         }
      ]
   };
}

function getDropDownMenuWidget() {
   return {
      type: ["widget"],
      name: "Drop-down menu",
      module: "alfresco/menus/AlfMenuBarPopup",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {
         label: "default",
         iconClass: "",
         altText: ""
      },
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.label",
               label: "Label",
               value: "default"
            }
         },
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.iconClass",
               label: "Icon",
               value: "",
               optionsConfig: {
                  fixed: [
                     {label:"None",value:""},
                     {label:"Configure",value:"alf-configure-icon"},
                     {label:"Invite User",value:"alf-user-icon"},
                     {label:"Upload",value:"alf-upload-icon"},
                     {label:"Create",value:"alf-create-icon"},
                     {label:"All Selected",value:"alf-allselected-icon"},
                     {label:"Some Selected",value:"alf-someselected-icon"},
                     {label:"None Selected",value:"alf-noneselected-icon"},
                     {label:"Back",value:"alf-back-icon"},
                     {label:"Forward",value:"alf-forward-icon"}
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoTextarea",
            config: {
               name: "defaultConfig.altText",
               label: "Alt Text",
               value: ""
            }
         }
      ],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,
      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: false
            }
         }
      ]
   };
}

function getMenuGroupWidget() {
   return {
      type: ["widget"],
      name: "Menu Group",
      module: "alfresco/menus/AlfMenuGroup",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {
         label: "default"
      },
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.label",
               label: "Label",
               value: "default"
            }
         }
      ],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,
      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: false
            }
         }
      ]
   };
}

function getMenuItemWidget() {
   return {
      type: ["widget"],
      name: "Menu Item",
      module: "alfresco/menus/AlfMenuItem",
      mixDroppedItemsIntoConfig: true,
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {
         label: "default",
         iconClass: "",
         altText: "",
         publishTopic: "",
         publishPayload: ""
      },
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.label",
               label: "Label",
               value: "default"
            }
         },
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.iconClass",
               label: "Icon",
               value: "",
               optionsConfig: {
                  fixed: [
                     {label:"None",value:""},
                     {label:"Edit",value:"alf-edit-icon"},
                     {label:"Configure",value:"alf-cog-icon"},
                     {label:"Leave",value:"alf-leave-icon"},
                     {label:"User",value:"alf-profile-icon"},
                     {label:"Password",value:"alf-password-icon"},
                     {label:"Help",value:"alf-help-icon"},
                     {label:"Logout",value:"alf-logout-icon"},
                     {label:"Simple List",value:"alf-simplelist-icon"},
                     {label:"Detailed List",value:"alf-detailedlist-icon"},
                     {label:"Gallery",value:"alf-gallery-icon"},
                     {label:"Show Folders",value:"alf-showfolders-icon"},
                     {label:"Show Path",value:"alf-showpath-icon"},
                     {label:"Show Sidebar",value:"alf-showsidebar-icon"},
                     {label:"Text",value:"alf-textdoc-icon"},
                     {label:"HTML Selected",value:"alf-htmldoc-icon"},
                     {label:"XML",value:"alf-xmldoc-icon"}
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoTextarea",
            config: {
               name: "defaultConfig.altText",
               label: "Alt Text",
               value: ""
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.publishTopic",
               label: "Publish Topic",
               value: ""
            }
         },
         {
            name: "alfresco/forms/controls/MultipleKeyValuePairFormControl",
            config: {
               name: "defaultConfig.publishPayload",
               label: "Publish Payload",
               value: ""
            }
         }
      ],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,
      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/creation/PublicationDropZone",
            config: {
               horizontal: true
            }
         }
      ]
   };
}

function getCascadingMenuWidget() {
   return {
      type: ["widget"],
      name: "Cascading Menu",
      module: "alfresco/menus/AlfCascadingMenu",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {
         label: "default"
      },
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.label",
               label: "Label",
               value: "default"
            }
         }
      ],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,
      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: false
            }
         }
      ]
   };
}

function getAllMenuWidgets() {
   return [
      getMenuBarWidget(),
      getMenuBarItemWidget(),
      getDropDownMenuWidget(),
      getMenuGroupWidget(),
      getMenuItemWidget(),
      getCascadingMenuWidget()
   ];
}

/* *********************************************************************************
 *                                                                                 *
 * LAYOUT WIDGETS                                                                  *
 *                                                                                 *
 ***********************************************************************************/


function getVerticalLayoutWidget() {
   return {
      type: ["widget"],
      name: "Vertical Layout",
      module: "alfresco/layout/VerticalWidgets",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {},
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,
      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: false
            }
         }
      ]
   };
}

function getHorizontalLayoutWidgetsForConfig() {
   return [
      {
         name: "alfresco/forms/ControlRow",
         config: {
            title: "Sub-Widget Margins",
            description: "Configure the left and right margins for every widget added as a child of this.",
            widgets: [
               {
                  name: "alfresco/forms/controls/DojoValidationTextBox",
                  config: {
                     name: "defaultConfig.widgetMarginLeft",
                     label: "Widget margin left",
                     description: "The number of pixels to place to the left of every nested widget.",
                     unitsLabel: "px",
                     value: "0",
                     validationConfig: {
                        regex: "^([0-9]+)$"
                     }
                  }
               },
               {
                  name: "alfresco/forms/controls/DojoValidationTextBox",
                  config: {
                     name: "defaultConfig.widgetMarginRight",
                     label: "Widget margin right",
                     description: "The number of pixels to place to the right of every nested widget.",
                     unitsLabel: "px",
                     value: "0",
                     validationConfig: {
                        regex: "^([0-9]+)$"
                     }
                  }
               }
            ]
         }
      }
   ];
}

function getHorizontalLayoutWidgetsForNestedConfig() {
   return [
      {
         name: "alfresco/forms/controls/DojoSelect",
         config: {
            fieldId: "widthType",
            name: "widthType",
            label: "Widget width",
            description: "Choose how the width of this widget. Selecting 'Auto' will give the widget a fair share of any remaining horizontal space, selecting 'Size in Pixels' will allow a fixed width to be defined and selecting 'Size as percentage' will give the widget a percentage of any remaining space",
            value: "AUTO",
            optionsConfig: {
               fixed: [
                  {label:"Auto",value:"AUTO"},
                  {label:"Size in pixels",value:"PIXELS"},
                  {label:"Size as percentage",value:"PERCENTAGE"}
               ]
            }
         }
      },
      {
         name: "alfresco/forms/controls/DojoValidationTextBox",
         config: {
            name: "additionalConfig.widthPx",
            label: "Widget width (in pixels)",
            unitsLabel: "px",
            value: "",
            postWhenHiddenOrDisabled: false,
            noValueUpdateWhenHiddenOrDisabled: true,
            visibilityConfig: {
               initialValue: false,
               rules: [
                  {
                     targetId: "widthType",
                     is: ["PIXELS"]
                  }
               ]
            },
            requirementConfig: {
               initialValue: false,
               rules: [
                  {
                     targetId: "widthType",
                     is: ["PIXELS"]
                  }
               ]
            },
            validationConfig: {
               regex: "^([0-9]+)$"
            }
         }
      },
      {
         name: "alfresco/forms/controls/DojoValidationTextBox",
         config: {
            name: "additionalConfig.widthPc",
            label: "Widget width (as percentage)",
            unitsLabel: "%",
            value: "",
            postWhenHiddenOrDisabled: false,
            noValueUpdateWhenHiddenOrDisabled: true,
            visibilityConfig: {
               initialValue: false,
               rules: [
                  {
                     targetId: "widthType",
                     is: ["PERCENTAGE"]
                  }
               ]
            },
            requirementConfig: {
               initialValue: false,
               rules: [
                  {
                     targetId: "widthType",
                     is: ["PERCENTAGE"]
                  }
               ]
            },
            validationConfig: {
               regex: "^([0-9]+)$"
            }
         }
      }
   ];
}

function getHorizontalLayoutWidget() {
   return {
      type: ["widget"],
      name: "Horizontal Layout",
      module: "alfresco/layout/HorizontalWidgets",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {},
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: getHorizontalLayoutWidgetsForConfig(),
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,
      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: false,
               widgetsForNestedConfig: getHorizontalLayoutWidgetsForNestedConfig()
            }
         }
      ]
   };
}

function getLeftAndRightWidget() {
   return {
      type: ["widget"],
      name: "Sliding Tabs",
      module: "alfresco/layout/LeftAndRight",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {},
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,
      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: false,
               widgetsForNestedConfig: [
                  {
                     name: "alfresco/forms/controls/DojoSelect",
                     config: {
                        name: "additionalConfig.align",
                        label: "Alignment",
                        value: "left",
                        optionsConfig: {
                           fixed: [
                              {label:"Align Left",value:"left"},
                              {label:"Align Right",value:"right"}
                           ]
                        }
                     }
                  }
               ]
            }
         }
      ]
   };
}

function getTitleDescAndContentWidget() {
   return {
      type: ["widget"],
      name: "Title, Description And Content",
      module: "alfresco/layout/TitleDescriptionAndContent",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {
         title: "title",
         description: "description"
      },
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.title",
               label: "Title",
               value: "title"
            }
         },
         {
            name: "alfresco/forms/controls/DojoTextarea",
            config: {
               name: "defaultConfig.description",
               label: "Description",
               value: "description"
            }
         }
      ],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,
      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: false
            }
         }
      ]
   };
}



function getClassicWindowWidget() {
   return {
      type: ["widget"],
      name: "Classic Window",
      module: "alfresco/layout/ClassicWindow",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {
         title: "Default Title"
      },
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.title",
               label: "Title",
               value: "Default Title"
            }
         }
      ],
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: false
            }
         }
      ]
   };
}

function getSideBarWidget() {
   return {
      type: ["widget"],
      name: "Sidebar Container",
      module: "alfresco/layout/AlfSideBarContainer",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {},
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,
      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: false,
               widgetsForNestedConfig: [
                  {
                     name: "alfresco/forms/controls/DojoSelect",
                     config: {
                        name: "additionalConfig.align",
                        label: "Sidebar or body",
                        value: "sidebar",
                        optionsConfig: {
                           fixed: [
                              {label:"Add to sidebar",value:"sidebar"},
                              {label:"Add to main content",value:"main"}
                           ]
                        }
                     }
                  }
               ]
            }
         }
      ]
   };
}

function getSlidingTabsWidget() {
   return {
      type: ["widget"],
      name: "Sliding Tabs",
      module: "alfresco/layout/SlidingTabs",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {},
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,
      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: false,
               widgetsForNestedConfig: [
                  {
                     name: "alfresco/forms/controls/DojoValidationTextBox",
                     config: {
                        name: "additionalConfig.title",
                        label: "Tab Title",
                        value: "title"
                     }
                  }
               ]
            }
         }
      ]
   };
}


function getAllLayoutWidgets() {
   return [
      getVerticalLayoutWidget(),
      getHorizontalLayoutWidget(),
      getLeftAndRightWidget(),
      getTitleDescAndContentWidget(),
      getSlidingTabsWidget(),
      getClassicWindowWidget(),
      getSideBarWidget()
   ];
}

/* *********************************************************************************
 *                                                                                 *
 * FORM WIDGETS                                                                  *
 *                                                                                 *
 ***********************************************************************************/

function getForm() {
   return {
      type: [ "widget" ],
      name: "Form",
      module: "alfresco/forms/Form",
      iconClass: "checkbox",
      defaultConfig: {
         displayButtons: true,
         okButtonLabel: "OK",
         cancelButtonLabel: "Cancel"
      },
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoCheckBox",
            config: {
               fieldId: "showFormButtons",
               name: "defaultConfig.displayButtons",
               label: "Display Buttons?",
               description: "Controls whether or not any buttons are displayed in the form.",
               value: true
            }
         },
         {
            name: "alfresco/forms/ControlRow",
            config: {
               widgets: [
                  {
                     name: "alfresco/forms/controls/DojoCheckBox",
                     config: {
                        fieldId: "showOkButton",
                        name: "defaultConfig.showOkButton",
                        label: "Display Confirmation Button?",
                        description: "Controls whether or not the 'confirmation' button is displayed for the form or not",
                        value: false,
                        postWhenHiddenOrDisabled: false,
                        noValueUpdateWhenHiddenOrDisabled: true,
                        visibilityConfig: {
                           initialValue: true,
                           rules: [
                              {
                                 targetId: "showFormButtons",
                                 is: [true]
                              }
                           ]
                        }
                     }
                  },
                  {
                     name: "alfresco/forms/controls/DojoValidationTextBox",
                     config: {
                        name: "defaultConfig.okButtonLabel",
                        label: "Confirmation Button Label",
                        description: "The label to show on the 'confirmation' button",
                        value: "OK",
                        postWhenHiddenOrDisabled: false,
                        noValueUpdateWhenHiddenOrDisabled: true,
                        visibilityConfig: {
                           initialValue: true,
                           rules: [
                              {
                                 targetId: "showFormButtons",
                                 is: [true]
                              },
                              {
                                 targetId: "showOkButton",
                                 is: [true]
                              }
                           ]
                        },
                        requirementConfig: {
                           initialValue: true,
                           rules: [
                              {
                                 targetId: "showFormButtons",
                                 is: [true]
                              }
                           ]
                        }
                     }
                  }
               ]
            }
         },
         {
            name: "alfresco/forms/ControlRow",
            config: {
               widgets: [
                  {
                     name: "alfresco/forms/controls/DojoCheckBox",
                     config: {
                        fieldId: "showCancelButton",
                        name: "defaultConfig.showCancelButton",
                        label: "Display Cancel Button",
                        description: "Controls whether or not the 'cancellation' button is displayed for the form or not",
                        value: false,
                        postWhenHiddenOrDisabled: false,
                        noValueUpdateWhenHiddenOrDisabled: true,
                        visibilityConfig: {
                           initialValue: true,
                           rules: [
                              {
                                 targetId: "showFormButtons",
                                 is: [true]
                              }
                           ]
                        }
                     }
                  },
                  {
                     name: "alfresco/forms/controls/DojoValidationTextBox",
                     config: {
                        name: "defaultConfig.cancelButtonLabel",
                        label: "Cancellation Button Label",
                        description: "The label to show on the 'cancellation' button",
                        value: "Cancel",
                        postWhenHiddenOrDisabled: false,
                        noValueUpdateWhenHiddenOrDisabled: true,
                        visibilityConfig: {
                           initialValue: true,
                           rules: [
                              {
                                 targetId: "showFormButtons",
                                 is: [true]
                              },
                              {
                                 targetId: "showCancelButton",
                                 is: [true]
                              }
                           ]
                        },
                        requirementConfig: {
                           initialValue: true,
                           rules: [
                              {
                                 targetId: "showFormButtons",
                                 is: [true]
                              }
                           ]
                        }
                     }
                  }
               ]
            }
         },
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.okButtonPublishTopic",
               label: "Confirmation Button Topic",
               description: "This is the topic to publish when the 'confirmation' button is pressed. The value of the form will be published as the payload",
               value: "",
               postWhenHiddenOrDisabled: false,
               noValueUpdateWhenHiddenOrDisabled: true,
               visibilityConfig: {
                  initialValue: true,
                  rules: [
                     {
                        targetId: "showFormButtons",
                        is: [true]
                     },
                     {
                        targetId: "showOkButton",
                        is: [true]
                     }
                  ]
               },
               requirementConfig: {
                  initialValue: true,
                  rules: [
                     {
                        targetId: "showFormButtons",
                        is: [true]
                     }
                  ]
               },
               optionsConfig: {
                  fixed: getCommonTopics()
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoCheckBox",
            config: {
               name: "defaultConfig.okButtonPublishGlobal",
               label: "Global Publish",
               description: "Controls whether the topic is published globally or not. Most services subscribe to globally published topics",
               value: true,
               visibilityConfig: {
                  initialValue: true,
                  rules: [
                     {
                        targetId: "showFormButtons",
                        is: [true]
                     },
                     {
                        targetId: "showOkButton",
                        is: [true]
                     }
                  ]
               }
            }
         }
      ],
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: false
            }
         }
      ]
   };
}

function getSingleEntryForm() {
   return {
      type: [ "widget" ],
      name: "Single Field Form",
      module: "alfresco/forms/SingleEntryForm",
      iconClass: "textbox",
      defaultConfig: {
      },
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.entryFieldName",
               label: "Entry field name",
               value: ""
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.okButtonLabel",
               label: "Submit Button Label",
               value: "OK"
            }
         },
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.okButtonPublishTopic",
               label: "Confirmation Button Topic",
               value: "",
               optionsConfig: {
                  fixed: getCommonTopics()
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoCheckBox",
            config: {
               name: "defaultConfig.okButtonPublishGlobal",
               label: "Global Publish",
               value: true
            }
         }
      ],
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Single Field Form"
            }
         }
      ]
   };
}

function getCrudForm() {
   return {
      type: [ "widget" ],
      name: "CRUD Form",
      module: "alfresco/forms/CrudForm",
      iconClass: "checkbox",
      defaultConfig: {
         createButtonLabel: "Create",
         createButtonPublishTopic: "ALF_CREATE_QUADDS_ITEM",
         createButtonPublishPayload: {},
         createButtonPublishGlobal: true,
         updateButtonLabel: "Update",
         updateButtonPublishTopic: "ALF_UPDATE_QUADDS_ITEM",
         updateButtonPublishPayload: {},
         updateButtonPublishGlobal: true,
         deleteButtonLabel: "Delete",
         deleteButtonPublishTopic: "ALF_DELETE_QUADDS_ITEM",
         deleteButtonPublishPayload: {},
         deleteButtonPublishGlobal: true
      },
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.createButtonLabel",
               label: "Create Button Label",
               value: "Create",
               postWhenHiddenOrDisabled: false,
               noValueUpdateWhenHiddenOrDisabled: true,
               requirementConfig: {
                  initialValue: true
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.createButtonPublishTopic",
               label: "Create Button Topic",
               value: "ALF_CREATE_QUADDS_ITEM",
               postWhenHiddenOrDisabled: false,
               noValueUpdateWhenHiddenOrDisabled: true,
               requirementConfig: {
                  initialValue: true
               },
               optionsConfig: {
                  fixed: getCommonTopics()
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoCheckBox",
            config: {
               name: "defaultConfig.createButtonPublishGlobal",
               label: "Create Button Global Publish",
               value: true
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.updateButtonLabel",
               label: "Update Button Label",
               value: "Update",
               postWhenHiddenOrDisabled: false,
               noValueUpdateWhenHiddenOrDisabled: true,
               requirementConfig: {
                  initialValue: true
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.updateButtonPublishTopic",
               label: "Update Button Topic",
               value: "ALF_UPDATE_QUADDS_ITEM",
               postWhenHiddenOrDisabled: false,
               noValueUpdateWhenHiddenOrDisabled: true,
               requirementConfig: {
                  initialValue: true
               },
               optionsConfig: {
                  fixed: getCommonTopics()
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoCheckBox",
            config: {
               name: "defaultConfig.updateButtonPublishGlobal",
               label: "Update Button Global Publish",
               value: true
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.deleteButtonLabel",
               label: "Delete Button Label",
               value: "Delete",
               postWhenHiddenOrDisabled: false,
               noValueUpdateWhenHiddenOrDisabled: true,
               requirementConfig: {
                  initialValue: true
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.deleteButtonPublishTopic",
               label: "Delete Button Topic",
               value: "ALF_DELETE_QUADDS_ITEM",
               postWhenHiddenOrDisabled: false,
               noValueUpdateWhenHiddenOrDisabled: true,
               requirementConfig: {
                  initialValue: true
               },
               optionsConfig: {
                  fixed: getCommonTopics()
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoCheckBox",
            config: {
               name: "defaultConfig.deleteButtonPublishGlobal",
               label: "Delete Button Global Publish",
               value: true
            }
         },
      ],
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: false
            }
         }
      ]
   };
}

function getCommonFormControlConfigWidgets() {
   return [
      {
         name: "alfresco/forms/controls/RandomValueGenerator",
         config: {
            name: "defaultConfig.fieldId",
            visibilityConfig: {
               initialValue: false
            }
         }
      },
      {
         name: "alfresco/forms/ControlRow",
         config: {
            title: "Data Settings",
            description: "Configure the data settings for this form control.",
            widgets: [
               {
                  name: "alfresco/forms/controls/DojoValidationTextBox",
                  config: {
                     name: "defaultConfig.name",
                     label: "Post parameter",
                     description: "This is will be used as the name of the request parameter when the form is posted.",
                     value: "default"
                  }
               },
               {
                  name: "alfresco/forms/controls/DojoValidationTextBox",
                  config: {
                     name: "defaultConfig.value",
                     label: "Initial Value",
                     description: "The will be the value that is initially displayed when the control is first shown (if no other value is already set)",
                     value: ""
                  }
               }
            ]
         }
      },
      {
         name: "alfresco/forms/ControlRow",
         config: {
            title: "Labels",
            description: "Configure the labels for this form control",
            widgets: [
               {
                  name: "alfresco/forms/controls/DojoValidationTextBox",
                  config: {
                     name: "defaultConfig.label",
                     label: "Label",
                     description: "This is a short description of what the form control is being used to capture (e.g.'Name')",
                     value: "Default Label"
                  }
               },
               {
                  name: "alfresco/forms/controls/DojoValidationTextBox",
                  config: {
                     name: "defaultConfig.unitsLabel",
                     label: "Units Label",
                     description: "This is a label that will be placed after the form control to indicate the type of data being captured (e.g. 'milliseconds', '%', 'miles', etc)",
                     value: "units"
                  }
               }
            ]
         }
      },
      {
         name: "alfresco/forms/controls/DojoTextarea",
         config: {
            name: "defaultConfig.description",
            label: "Description",
            description: "This is a longer description of what the form control is being used to capture (this text is an example of a description)",
            value: "Default description"
         }
      },
      {
         name: "alfresco/forms/controls/DojoCheckBox",
         config: {
            fieldId: "SHOW_DYNAMIC_BEHAVIOUR_CONFIG",
            name: "showDynamicBehaviourConfig",
            label: "Configure dynamic behaviour",
            description: "Check this box to configure the rules that control visibility, requirement and disablement",
            value: false
         }
      },
      {
         name: "alfresco/forms/ControlRow",
         config: {
            title: "Visibility",
            description: "Configure the visibility behaviour.",
            widgets: [
               {
                  name: "alfresco/forms/controls/DojoCheckBox",
                  config: {
                     name: "defaultConfig.visibilityConfig.initialValue",
                     label: "Initially visible",
                     description: "Check this box if the control should be initially visible when the form is first rendered",
                     value: true
                  }
               },
               {
                  name: "alfresco/forms/creation/FormRulesConfigControl",
                  config: {
                     name: "defaultConfig.visibilityConfig.rules",
                     label: "Dynamic visibility behaviour configuration",
                     description: "Set the visibility of this control to change based on the values of other controls within the same form. This makes it possible to make a control progressively closed as the user enters data. For example, selecting a specific field in a drop-down menu might reveal more fields."
                  }
               }
            ],
            visibilityConfig: {
               initialValue: false,
               rules: [
                  {
                     topic: "_valueChangeOf_SHOW_DYNAMIC_BEHAVIOUR_CONFIG",
                     attribute: "value",
                     is: [true]
                  }
               ]
            }
         }
      },
      {
         name: "alfresco/forms/ControlRow",
         config: {
            title: "Requirement",
            description: "Configure the requirement behaviour.",
            widgets: [
               {
                  name: "alfresco/forms/controls/DojoCheckBox",
                  config: {
                     name: "defaultConfig.requirementConfig.initialValue",
                     label: "Initially required",
                     description: "Check this box if the field should be intially required when the form is first rendered. A required field must have a value in order for the form's submit button to be enabled",
                     value: false,
                     visibilityConfig: {
                        initialValue: false,
                        rules: [
                           {
                              targetId: "SHOW_DYNAMIC_BEHAVIOUR_CONFIG",
                              is: [true]
                           }
                        ]
                     }
                  }
               },
               {
                  name: "alfresco/forms/creation/FormRulesConfigControl",
                  config: {
                     name: "defaultConfig.requirementConfig.rules",
                     label: "Dynamic requirement behaviour configuration",
                     description: "Create rules that change when the field must have a value entered. For example, a field may only be required when a specific value is selected from a drop-down menu.",
                     visibilityConfig: {
                        initialValue: false,
                        rules: [
                           {
                              targetId: "SHOW_DYNAMIC_BEHAVIOUR_CONFIG",
                              is: [true]
                           }
                        ]
                     }
                  }
               }
            ],
            visibilityConfig: {
               initialValue: false,
               rules: [
                  {
                     topic: "_valueChangeOf_SHOW_DYNAMIC_BEHAVIOUR_CONFIG",
                     attribute: "value",
                     is: [true]
                  }
               ]
            }
         }
      },
      {
         name: "alfresco/forms/ControlRow",
         config: {
            title: "Disablement",
            description: "Configure the disablement behaviour.",
            widgets: [
               {
                  name: "alfresco/forms/controls/DojoCheckBox",
                  config: {
                     name: "defaultConfig.disablementConfig.initialValue",
                     label: "Initially disabled",
                     value: false,
                     description: "Check this box if the field should be initially disabled when the form is first rendered. A disabled field cannot have it's value changed.",
                     visibilityConfig: {
                        initialValue: false,
                        rules: [
                           {
                              targetId: "SHOW_DYNAMIC_BEHAVIOUR_CONFIG",
                              is: [true]
                           }
                        ]
                     }
                  }
               },
               {
                  name: "alfresco/forms/creation/FormRulesConfigControl",
                  config: {
                     name: "defaultConfig.disablementConfig.rules",
                     label: "Dynamic disablement behaviour configuration",
                     description: "Create rules that change when the field is disabled. A field might want to be disabled until other fields have been populated",
                     visibilityConfig: {
                        initialValue: false,
                        rules: [
                           {
                              targetId: "SHOW_DYNAMIC_BEHAVIOUR_CONFIG",
                              is: [true]
                           }
                        ]
                     }
                  }
               }
            ],
            visibilityConfig: {
               initialValue: false,
               rules: [
                  {
                     topic: "_valueChangeOf_SHOW_DYNAMIC_BEHAVIOUR_CONFIG",
                     attribute: "value",
                     is: [true]
                  }
               ]
            }
         }
      },
      {
         name: "alfresco/forms/ControlRow",
         config: {
            title: "Advanced Settings",
            description: "Configure when the values are posted and when they are updated",
            widgets: [
               {
                  name: "alfresco/forms/controls/DojoCheckBox",
                  config: {
                     name: "defaultConfig.postWhenHiddenOrDisabled",
                     label: "Include value when hidden or disabled",
                     description: "If checked, the value of this field will always be included in the form value regardless of whether or not it is hidden or disabled. Uncheck the box to only include the value when displayed and enabled.",
                     value: true
                  }
               },
               {
                  name: "alfresco/forms/controls/DojoCheckBox",
                  config: {
                     name: "defaultConfig.noValueUpdateWhenHiddenOrDisabled",
                     label: "Update when hidden or disabled",
                     description: "If checked, this field will not be initially set with a value if it is hidden or disabled",
                     value: false
                  }
               }
            ]
         }
      }
   ];
}

function getFormControlRow() {
   return {
      type: [ "widget" ],
      name: "Form Control Row",
      module: "alfresco/forms/ControlRow",
      defaultConfig: {
         title: "",
         description: ""
      },
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.title",
               label: "Title",
               description: "This is the title for the row of form controls. Leave blank to have no title",
               value: ""
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.description",
               label: "Description",
               description: "Adds a sub-title for the row of form controls. Leave blank to have no description.",
               value: ""
            }
         }
      ].concat(getHorizontalLayoutWidgetsForConfig()),
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: false,
               widgetsForNestedConfig: getHorizontalLayoutWidgetsForNestedConfig()
            }
         }
      ]
   };
}


function getTextField() {
   return {
      type: [ "widget" ],
      name: "Text Box",
      module: "alfresco/forms/controls/DojoValidationTextBox",
      iconClass: "textbox",
      defaultConfig: {
         name: "default",
         label: "Text box",
         description: "Default description",
         unitsLabel: "units"
      },
      widgetsForConfig: getCommonFormControlConfigWidgets().concat([
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.validationConfig.regex",
               label: "Expected Value Type",
               description: "Select the type of text that the user must provide",
               optionsConfig: {
                  fixed: [
                     { label: "Any", value: ".*"},
                     { label: "E-mail", value: "^([0-9a-zA-Z]([-.\w]*[0-9a-zA-Z])*@([0-9a-zA-Z][-\w]*[0-9a-zA-Z]\.)+[a-zA-Z]{2,9})$"},
                     { label: "Number", value: "^([0-9]+)$"}
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.placeHolder",
               label: "Placeholder text",
               description: "Placeholder text is a short label that is placed inside the form control to indicate what data the user should be providing (e.g. 'Enter you name')"
            }
         }
      ]),
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Text Box"
            }
         }
      ]
   };
}

function getTextArea() {
   return {
      type: [ "widget" ],
      name: "Text Area",
      module: "alfresco/forms/controls/DojoTextarea",
      iconClass: "textarea",
      defaultConfig: {
         name: "default",
         label: "Text area",
         description: "Default description"
      },
      widgetsForConfig: getCommonFormControlConfigWidgets(),
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Text Area"
            }
         }
      ]
   };
}

function getCommonOptionsWidgetsForConfig() {
   return [
      {
         name: "alfresco/forms/ControlRow",
         config: {
            title: "Options Configuration",
            description: "Configure how the displayed options are retrieved for this form control",
            widgets: [
               {
                  name: "alfresco/forms/controls/DojoRadioButtons",
                  config: {
                     fieldId: "OPTIONS_TYPE",
                     name: "selectOptionType",
                     label: "Options Type",
                     description: "Select either to populate the form control with a fixed set of options or to dynamically retrieve them",
                     value: "FIXED",
                     optionsConfig: {
                        fixed: [
                           { label: "Fixed Options", value: "FIXED" },
                           { label: "Dynamic Options", value: "DYNAMIC" }
                        ]
                     }
                  }
               }
            ]
         }
      },
      {
         name: "alfresco/forms/controls/MultipleKeyValuePairFormControl",
         config: {
            name: "defaultConfig.optionsConfig.fixed",
            label: "Fixed Options",
            description: "Create the list of fixed options to be shown in the menu",
            visibilityConfig: {
               initialValue: false,
               rules: [
                  {
                     targetId: "OPTIONS_TYPE",
                     is: ["FIXED"]
                  }
               ]
            }
         }
      },
      {
         name: "alfresco/forms/controls/DojoValidationTextBox",
         config: {
            label: "The topic to publish to retrieve options",
            name: "defaultConfig.optionsConfig.publishTopic",
            description: "Enter a topic that will be published to request options.",
            value: "ALF_GET_FORM_CONTROL_OPTIONS",
            visibilityConfig: {
               initialValue: false
            },
            disablementConfig: {
               initialValue: true
            }
         }
      },
      {
         name: "alfresco/forms/ControlRow",
         config: {
            widgets: [
               {
                  name: "alfresco/forms/controls/DojoValidationTextBox",
                  config: {
                     label: "URL",
                     name: "defaultConfig.optionsConfig.publishPayload.url",
                     description: "The URL to retrieve options from",
                     value: url.context + "/proxy/alfresco/api/groups",
                     visibilityConfig: {
                        initialValue: false,
                        rules: [
                           {
                              targetId: "OPTIONS_TYPE",
                              is: ["DYNAMIC"]
                           }
                        ]
                     }
                  }
               },
               {
                  name: "alfresco/forms/controls/DojoValidationTextBox",
                  config: {
                     label: "Options Attribute",
                     name: "defaultConfig.optionsConfig.publishPayload.itemsAttribute",
                     description: "The attribute in the response body that identifies the options to display (can be in dot-notation form).",
                     value: "data",
                     visibilityConfig: {
                        initialValue: false,
                        rules: [
                           {
                              targetId: "OPTIONS_TYPE",
                              is: ["DYNAMIC"]
                           }
                        ]
                     }
                  }
               }
            ]
         }
      },
      {
         name: "alfresco/forms/ControlRow",
         config: {
            widgets: [
               {
                  name: "alfresco/forms/controls/DojoValidationTextBox",
                  config: {
                     label: "Label Attribute",
                     name: "defaultConfig.optionsConfig.publishPayload.labelAttribute",
                     description: "The attribute each option object to use as the display label for that option.",
                     value: "displayName",
                     visibilityConfig: {
                        initialValue: false,
                        rules: [
                           {
                              targetId: "OPTIONS_TYPE",
                              is: ["DYNAMIC"]
                           }
                        ]
                     }
                  }
               },
               {
                  name: "alfresco/forms/controls/DojoValidationTextBox",
                  config: {
                     label: "Value Attribute",
                     name: "defaultConfig.optionsConfig.publishPayload.valueAttribute",
                     description: "The attribute each option object to use as the value that option.",
                     value: "fullName",
                     visibilityConfig: {
                        initialValue: false,
                        rules: [
                           {
                              targetId: "OPTIONS_TYPE",
                              is: ["DYNAMIC"]
                           }
                        ]
                     }
                  }
               }
            ]
         }
      }
   ];
}

function getSelectField() {
   return {
      type: [ "widget" ],
      name: "Select Menu",
      module: "alfresco/forms/controls/DojoSelect",
      iconClass: "dropdown",
      defaultConfig: {
         name: "default",
         label: "Drop down",
         description: "Default description",
         unitsLabel: "units",
         optionsConfig: {
            fixed: [
               { label: "Option1", value: "Value1"},
               { label: "Option2", value: "Value2"}
            ]
         }
      },
      widgetsForConfig: getCommonFormControlConfigWidgets().concat(getCommonOptionsWidgetsForConfig()),
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Select Menu"
            }
         }
      ]
   };
}

function getRadioButtonsField() {
   return {
      type: [ "widget" ],
      name: "Radio Buttons",
      module: "alfresco/forms/controls/DojoRadioButtons",
      defaultConfig: {
         name: "default",
         label: "Radio Buttons",
         description: "Default description",
         unitsLabel: "units",
         optionsConfig: {
            fixed: [
               { label: "Option1", value: "Value1"},
               { label: "Option2", value: "Value2"}
            ]
         }
      },
      widgetsForConfig: getCommonFormControlConfigWidgets().concat(getCommonOptionsWidgetsForConfig()),
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Radio Buttons"
            }
         }
      ]
   };
}

function getCheckBox() {
   return {
      type: [ "widget" ],
      name: "Check box",
      module: "alfresco/forms/controls/DojoCheckBox",
      iconClass: "checkbox",
      defaultConfig: {
         name: "default",
         label: "Check box",
         description: "Default description"
      },
      widgetsForConfig: getCommonFormControlConfigWidgets(),
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Check Box"
            }
         }
      ]
   };
}

function getAceEditor() {
   return {
      type: [ "widget" ],
      name: "ACE Editor",
      module: "alfresco/forms/controls/AceEditor",
      iconClass: "checkbox",
      defaultConfig: {
         name: "default",
         label: "ACE Editor",
         editMode: "text",
         description: "Default description"
      },
      widgetsForConfig: getCommonFormControlConfigWidgets().concat([
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.editMode",
               label: "Edit Mode",
               optionsConfig: {
                  fixed: [
                     {label:"Text",value:"text"},
                     {label:"XML",value:"xml"},
                     {label:"JavaScript",value:"javascript"},
                     {label:"FreeMarker",value:"freemarker"},
                     {label:"JSON",value:"json"}
                  ]
               }
            }
         }
      ]),
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "ACE Editor"
            }
         }
      ]
   };
}

function getMultipleEntryFormControl() {
   return {
      type: [ "widget" ],
      name: "Multi-Entry",
      module: "alfresco/forms/controls/MultipleEntryFormControl",
      defaultConfig: {
         name: "default",
         label: "Multi-Entry"
      },
      widgetsForConfig: getCommonFormControlConfigWidgets(),
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: false
            }
         }
      ]
   };
}

function getRandomValueControl() {
   return {
      type: [ "widget" ],
      name: "Random Value",
      module: "alfresco/forms/controls/RandomValueGenerator",
      defaultConfig: {
         visibilityConfig: {
            initialValue: false
         }
      },
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               label: "Property",
               name: "defaultConfig.name"
            }
         },
         {
            name: "alfresco/forms/controls/DojoCheckBox",
            config: {
               label: "Visible?",
               name: "defaultConfig.visibilityConfig.initialValue",
               value: false
            }
         }
      ],
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Random Value"
            }
         }
      ]
   };
}

function getDocumentPickerControl() {
   return {
      type: [ "widget" ],
      name: "Document Picker",
      module: "alfresco/forms/controls/DocumentPicker",
      defaultConfig: {
         visibilityConfig: {
            initialValue: false
         }
      },
      widgetsForConfig: getCommonFormControlConfigWidgets(),
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Document Picker"
            }
         }
      ]
   };
}

function getAllFormWidgets() {
   return [
      getForm(),
      getCrudForm(),
      getSingleEntryForm(),
      getFormControlRow(),
      getTextField(),
      getTextArea(),
      getSelectField(),
      getRadioButtonsField(),
      getCheckBox(),
      // getAceEditor(),
      getMultipleEntryFormControl(),
      getRandomValueControl(),
      getDocumentPickerControl()
   ];
}
