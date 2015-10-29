/*
 * Custom Model Manager
 * Miscellaneous imports
 * imports/cmm-misc.lib.js
 * 
 * @author Kevin Roast
 * @author Richard Smith
 */

/**
 * Page title and introLabel
 */
var pageTitle = {
   name: "alfresco/header/SetTitle",
   config: {
      title: msg.get("cmm.page.title")
   }
};

var introLabel1 = {
   name: "alfresco/html/Label",
   config: {
      label: "cmm.intro.1"
   }
};
var introLabel2 = {
   name: "alfresco/html/Label",
   config: {
      label: "cmm.intro.2"
   }
};
var editorIntroLabel1 = {
   name: "alfresco/html/Label",
   config: {
      label: "cmm.intro.editor1",
      additionalCssClasses: "instructionLabel"
   }
};

/**
 * Accessible headings and labels
 */
var heading = {
   properties: {
      name: "alfresco/html/Heading",
      config: {
         level: 2,
         label: msg.get("cmm.heading.properties")
      }
   },
   createModelMenu: {
      name: "alfresco/html/Heading",
      config: {
         level: 2,
         label: msg.get("cmm.heading.create-model-menu"),
         isHidden: true
      }
   },
   createTypesPropertyGroupsMenu: {
      name: "alfresco/html/Heading",
      config: {
         level: 2,
         label: msg.get("cmm.heading.create-menu"),
         isHidden: true
      }
   },
   existingModels: {
      name: "alfresco/html/Heading",
      config: {
         level: 3,
         label: msg.get("cmm.heading.existing-models")
      }
   },
   models: {
      name: "alfresco/html/Heading",
      config: {
         level: 3,
         label: msg.get("cmm.heading.models")
      }
   },
   types: {
      name: "alfresco/html/Heading",
      config: {
         level: 3,
         label: msg.get("cmm.heading.types")
      }
   },
   propertyGroups: {
      name: "alfresco/html/Heading",
      config: {
         level: 3,
         label: msg.get("cmm.heading.propertygroups")
      }
   },
   actionMenu: {
      name: "alfresco/html/Heading",
      config: {
         level: 2,
         label: msg.get("cmm.heading.action-menu"),
         isHidden: true
      }
   },
   currentModel: {
      name: "alfresco/layout/LeftAndRight",
      config: {
         widgets: [
            {
               name: "alfresco/html/Heading",
               config: {
                  subscriptionTopic: "TYPES_LIST_CMM_UPDATE_MODEL_HEADING",
                  level: 1,
                  label: "MODEL" // NOTE: cannot be empty string due to Heading widget check for empty
               }
            },
            {
               name: "alfresco/html/Label",
               align: "right",
               config: {
                  subscriptionTopic: "TYPES_LIST_CMM_UPDATE_MODEL_HEADING_STATUS",
                  label: "STATUS", // NOTE: cannot be empty string due to Heading widget check for empty
                  additionalCssClasses: "status",
                  visibilityConfig: {
                     initialValue: false,
                     rules: [
                        {
                           topic: "TYPES_LIST_CMM_UPDATE_MODEL_HEADING_STATUS",
                           attribute: "status",
                           useCurrentItem: false,
                           is: ["DRAFT"],
                           strict: true
                        }
                     ]
                  }
               }
            },
            {
               name: "alfresco/html/Label",
               align: "right",
               config: {
                  subscriptionTopic: "TYPES_LIST_CMM_UPDATE_MODEL_HEADING_STATUS",
                  label: "STATUS", // NOTE: cannot be empty string due to Heading widget check for empty
                  additionalCssClasses: "status active",
                  visibilityConfig: {
                     initialValue: false,
                     rules: [
                        {
                           topic: "TYPES_LIST_CMM_UPDATE_MODEL_HEADING_STATUS",
                           attribute: "status",
                           useCurrentItem: false,
                           isNot: ["DRAFT"],
                           strict: true
                        }
                     ]
                  }
               }
            }
         ]
      }
   },
   currentTypePropertyGroup: {
      name: "alfresco/layout/LeftAndRight",
      config: {
         widgets: [
            {
               name: "alfresco/html/Heading",
               config: {
                  subscriptionTopic: "PROPERTIES_LIST_CMM_UPDATE_TPG_HEADING",
                  level: 1,
                  label: "TYPE|PROPERTY_GROUP" // NOTE: cannot be empty string due to Heading widget check for empty
               }
            },
            {
               name: "alfresco/html/Label",
               align: "right",
               config: {
                  subscriptionTopic: "PROPERTIES_LIST_CMM_UPDATE_TPG_HEADING_STATUS",
                  label: "STATUS", // NOTE: cannot be empty string due to Heading widget check for empty
                  additionalCssClasses: "status",
                  visibilityConfig: {
                     initialValue: false,
                     rules: [
                        {
                           topic: "PROPERTIES_LIST_CMM_UPDATE_TPG_HEADING_STATUS",
                           attribute: "status",
                           useCurrentItem: false,
                           is: ["DRAFT"],
                           strict: true
                        }
                     ]
                  }
               }
            },
            {
               name: "alfresco/html/Label",
               align: "right",
               config: {
                  subscriptionTopic: "PROPERTIES_LIST_CMM_UPDATE_TPG_HEADING_STATUS",
                  label: "STATUS", // NOTE: cannot be empty string due to Heading widget check for empty
                  additionalCssClasses: "status active",
                  visibilityConfig: {
                     initialValue: false,
                     rules: [
                        {
                           topic: "PROPERTIES_LIST_CMM_UPDATE_TPG_HEADING_STATUS",
                           attribute: "status",
                           useCurrentItem: false,
                           isNot: ["DRAFT"],
                           strict: true
                        }
                     ]
                  }
               }
            }
         ]
      }
   },
   formEditorCurrentTypePropertyGroup: {
      name: "alfresco/layout/LeftAndRight",
      config: {
         widgets: [
            {
               name: "alfresco/html/Heading",
               config: {
                  subscriptionTopic: "CMM_UPDATE_TPG_HEADING",
                  level: 1,
                  label: "TYPE|PROPERTY_GROUP" // NOTE: cannot be empty string due to Heading widget check for empty
               }
            },
            {
               name: "alfresco/html/Label",
               align: "right",
               config: {
                  subscriptionTopic: "CMM_UPDATE_TPG_HEADING_STATUS",
                  label: "STATUS", // NOTE: cannot be empty string due to Heading widget check for empty
                  additionalCssClasses: "status",
                  visibilityConfig: {
                     initialValue: false,
                     rules: [
                        {
                           topic: "CMM_UPDATE_TPG_HEADING_STATUS",
                           attribute: "status",
                           useCurrentItem: false,
                           is: ["DRAFT"],
                           strict: true
                        }
                     ]
                  }
               }
            },
            {
               name: "alfresco/html/Label",
               align: "right",
               config: {
                  subscriptionTopic: "CMM_UPDATE_TPG_HEADING_STATUS",
                  label: "STATUS", // NOTE: cannot be empty string due to Heading widget check for empty
                  additionalCssClasses: "status active",
                  visibilityConfig: {
                     initialValue: false,
                     rules: [
                        {
                           topic: "CMM_UPDATE_TPG_HEADING_STATUS",
                           attribute: "status",
                           useCurrentItem: false,
                           isNot: ["DRAFT"],
                           strict: true
                        }
                     ]
                  }
               }
            }
         ]
      }
   },
   properties: {
      name: "alfresco/html/Heading",
      config: {
         level: 3,
         label: msg.get("cmm.heading.properties")
      }
   },
   createPropertyMenu: {
      name: "alfresco/html/Heading",
      config: {
         level: 2,
         label: msg.get("cmm.heading.create-property-menu"),
         isHidden: true
      }
   }
};

/**
 * Layout spacers
 */
var spacer = {
   eight: {
      name: "alfresco/html/Spacer",
      config: {
         height: "8px"
      }
   },
   twelve: {
      name: "alfresco/html/Spacer",
      config: {
         height: "12px"
      }
   },
   twenty: {
      name: "alfresco/html/Spacer",
      config: {
         height: "20px"
      }
   }
};

/**
 * Form field validation configurations
 */
var validation = {
   alphanumericURIMandatory: {
      validation: "regex",
      regex: "^[A-Za-z0-9:/_\.\-]+$",
      errorMessage: "cmm.validation.alphanumeric-uri-mandatory"
   },
   alphanumericMandatory: {
      validation: "regex",
      regex: "^[A-Za-z0-9_\-]+$",
      errorMessage: "cmm.validation.alphanumeric-mandatory"
   },
   alphanumericSpaces: {
      validation: "regex",
      regex: "^[A-Za-z0-9_\- ]*$",
      errorMessage: "cmm.validation.alphanumeric-spaces"
   },
   maxlength255: {
      validation: "maxLength",
      length: 255,
      errorMessage: "cmm.validation.maxlength255"
   },
   maxlength1024: {
      validation: "maxLength",
      length: 1024,
      errorMessage: "cmm.validation.maxlength1024"
   }
};
