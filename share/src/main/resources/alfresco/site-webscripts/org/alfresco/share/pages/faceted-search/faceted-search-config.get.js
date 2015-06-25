<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-header.lib.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-footer.lib.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/options/faceted-search/available-facets-controls.get.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/options/faceted-search/available-facets.get.js">

// Currently we're importing a WebScript that can be used to get the list of facetable
// properties. Because this data is used numerous times and involves post-processing of
// the data we only want to load and process it once per facet request.
var facetetableProperties = getAvailableFacetProperties();

// Get the initial header services and widgets...
var services = getHeaderServices(),
    widgets = getHeaderModel(msg.get("faceted-search-config.page.title"));

services.push("alfresco/services/CrudService",
              "alfresco/services/OptionsService");

/* *********************************************************************************
 *                                                                                 *
 * CREATE/EDIT FORM DEFINITION                                                     *
 *                                                                                 *
 ***********************************************************************************/

var availableSites = [];
var result = remote.call("/api/sites");
if (result.status.code == status.STATUS_OK)
{
   var unprocessedSites = JSON.parse(result);
   for (var i=0; i<unprocessedSites.length; i++)
   {
      var currSite = unprocessedSites[i];
      availableSites.push({
         label: currSite.title,
         value: currSite.shortName
      });
   }
}

// The form definition is returned by a function because it is not possible to edit
// the filterID when editing, so in order to avoid duplicating the definition entirely
// it is defined in a function...
function getFormDefinition(canEditFilterId) {

   var filterIdValidation = null;
   if (canEditFilterId)
   {
      filterIdValidation = [
         {
            validation: "validateUnique",
            itemsProperty: "response.facets",
            errorMessage: msg.get("faceted-search-config.filterId.error.isUse"),
            publishTopic: "ALF_CRUD_GET_ALL",
            publishPayload: {
               url: "api/facet/facet-config"
            }
         },
         {
            validation: "regex",
            regex: "([%\"\*\\\\>\<\?\/\:\|]+)|([\.]?[\.]+$)",
            errorMessage: msg.get("faceted-search-config.filterId.error.invalid"),
            invertRule: true
         }
      ];
   }

   var formWidgets = [
      {
         id: "FORM_HIDDEN_URL",
         name: "alfresco/forms/controls/DojoValidationTextBox",
         config: {
            fieldId: "HIDDEN_UPDATE_URL",
            name: "url",
            value: "api/facet/facet-config",
            visibilityConfig: {
               initialValue: false
            }
         }
      },
      {
         id: "FORM_HIDDEN_CUSTOM_PROPS",
         name: "alfresco/forms/controls/HiddenValue",
         config: {
            fieldId: "CUSTOM_PROPERTIES",
            name: "customProperties",
            value: null,
            autoSetConfig: [
               {
                  rulePassValue: {
                     blockIncludeFacetRequest: {
                        name: "{http://www.alfresco.org/model/solrfacetcustomproperty/1.0}blockIncludeFacetRequest",
                        title: null,
                        type: null,
                        value: "true"
                     }
                  },
                  ruleFailValue: {},
                  rules: [
                     {
                        targetId: "FACET_QNAME",
                        is: ["{http://www.alfresco.org/model/content/1.0}created","{http://www.alfresco.org/model/content/1.0}modified","{http://www.alfresco.org/model/content/1.0}content.size"]
                     }
                  ]
               }
            ]
         }
      },
      {
         id: "FORM_FILTER_ID",
         name: "alfresco/forms/controls/DojoValidationTextBox",
         config: {
            fieldId: "FILTER_ID",
            name: "filterID",
            value: "",
            label: "faceted-search-config.filterId.label",
            description: "faceted-search-config.filterId.description",
            placeHolder: "faceted-search-config.filterId.placeHolder",
            visibilityConfig: {
               initialValue: true
            },
            requirementConfig: {
               initialValue: true
            },
            disablementConfig: {
               initialValue: !canEditFilterId
            },
            validationConfig: filterIdValidation
         }
      },
      {
         id: "FORM_DISPLAY_NAME",
         name: "alfresco/forms/controls/DojoValidationTextBox",
         config: {
            fieldId: "DISPLAY_NAME",
            name: "displayName",
            value: "",
            label: "faceted-search-config.displayName.label",
            placeHolder: "faceted-search-config.displayName.placeHolder",
            description: "faceted-search-config.displayName.description",
            visibilityConfig: {
               initialValue: true
            },
            requirementConfig: {
               initialValue: true
            }
         }
      },
      {
         id: "FORM_IS_ENABLED",
         name: "alfresco/forms/controls/DojoCheckBox",
         config: {
            fieldId: "IS_ENABLED",
            name: "isEnabled",
            value: "true",
            label: "faceted-search-config.isEnabled.label",
            description: "faceted-search-config.isEnabled.description",
            _convertStringValuesToBooleans: true
         }
      },
      {
         id: "FORM_IS_DEFAULT",
         name: "alfresco/forms/controls/DojoCheckBox",
         config: {
            fieldId: "IS_DEFAULT",
            name: "isDefault",
            value: "false",
            label: "faceted-search-config.isDefault.label",
            description: "faceted-search-config.isDefault.description",
            postWhenHiddenOrDisabled: false,
            _convertStringValuesToBooleans: true,
            disablementConfig: {
               initialValue: true
            }
         }
      },
      {
         id: "FORM_FACET_QNAME",
         name: "alfresco/forms/controls/FilteringSelect",
         config: {
            fieldId: "FACET_QNAME",
            name: "facetQName",
            value: "",
            label: "faceted-search-config.facetQName.label",
            description: "faceted-search-config.facetQName.description",
            optionsConfig: {
               fixed: facetetableProperties,
               queryAttribute: "displayName",
               labelAttribute: "displayName",
               valueAttribute: "longqname",
               // publishTopic: "ALF_CRUD_GET_ALL",
               // publishPayload: {
               //    url: "api/facet/facetable-properties?maxItems=0&locale=" + locale,
               //    resultsProperty: "response.data.properties",
               //    itemsAttribute: "data.properties"
               // }
            },
            requirementConfig: {
               initialValue: true
            }
         }
      },
      {
         id: "FORM_DISPLAY_CONTROL",
         name: "alfresco/forms/controls/DojoSelect",
         config: {
            fieldId: "DISPLAY_CONTROL",
            name: "displayControl",
            value: "alfresco/search/FacetFilters",
            label: "faceted-search-config.displayControl.label",
            description: "faceted-search-config.displayControl.description",
            optionsConfig: {
               fixed: getAvailableFacetControls()
            }
         }
      },
      {
         id: "FORM_SORTBY",
         name: "alfresco/forms/controls/DojoSelect",
         config: {
            fieldId: "SORTBY",
            name: "sortBy",
            value: "ALPHABETICALLY",
            label: "faceted-search-config.sortBy.label",
            description: "faceted-search-config.sortBy.description",
            optionsConfig: {
               fixed: [
                  {
                     label: "faceted-search-config.sortBy.AtoZ.label",
                     value: "ALPHABETICALLY"
                  },
                  {
                     label: "faceted-search-config.sortBy.ZtoA.label",
                     value: "REVERSE_ALPHABETICALLY"
                  },
                  {
                     label: "faceted-search-config.sortBy.highToLow.label",
                     value: "ASCENDING"
                  },
                  {
                     label: "faceted-search-config.sortBy.lowToHigh.label",
                     value: "DESCENDING"
                  },
                  {
                     label: "faceted-search-config.sortBy.index.label",
                     value: "INDEX"
                  }
               ]
            }
         }
      },
      {
         id: "FORM_MAX_FILTERS",
         name: "alfresco/forms/controls/NumberSpinner",
         config: {
            fieldId: "MAXFILTERS",
            name: "maxFilters",
            value: "10",
            label: "faceted-search-config.maxFilters.label",
            description: "faceted-search-config.maxFilters.description",
            min: 1,
            max: 20,
            validationConfig: {
               regex: "^[0-9]+$"
            }
         }
      },
      {
         id: "FORM_MIN_FILTER_VALUE_LENGTH",
         name: "alfresco/forms/controls/NumberSpinner",
         config: {
            fieldId: "MIN_FILTER_VALUE_LENGTH",
            name: "minFilterValueLength",
            value: "1",
            label: "faceted-search-config.minFilterValueLength.label",
            description: "faceted-search-config.minFilterValueLength.description",
            min: 1,
            max: 20,
            validationConfig: {
               regex: "^[0-9]+$"
            }
         }
      },
      {
         id: "FORM_HIT_THRESHOLD",
         name: "alfresco/forms/controls/NumberSpinner",
         config: {
            fieldId: "HIT_THRESHOLD",
            name: "hitThreshold",
            value: "1",
            label: "faceted-search-config.hitThreshold.label",
            description: "faceted-search-config.hitThreshold.description",
            min: 1,
            max: 20,
            validationConfig: {
               regex: "^[0-9]+$"
            }
         }
      },
      {
         id: "FORM_SCOPE",
         name: "alfresco/forms/controls/DojoSelect",
         config: {
            fieldId: "SCOPE",
            name: "scope",
            value: "",
            label: "faceted-search-config.scope.label",
            description: "faceted-search-config.scope.description",
            optionsConfig: {
               fixed: [
                  {
                     label: "faceted-search-config.scope.none.label",
                     value: "ALL"
                  },
                  {
                     label: "faceted-search-config.scope.site.label",
                     value: "SCOPED_SITES"
                  }
               ]
            }
         }
      },
      {
         id: "FORM_SCOPED_SITES",
         name: "alfresco/forms/controls/MultipleEntryFormControl",
         config: {
            fieldId: "SCOPED_SITES",
            name: "scopedSites",
            value: "",
            label: "faceted-search-config.scopedSites.label",
            description: "faceted-search-config.scopedSites.description",
            useSimpleValues: true,
            widgets: [
               {
                  name: "alfresco/forms/controls/DojoSelect",
                  config: {
                     fieldId: "SCOPED_SITES_SITE",
                     name: "value",
                     value: "",
                     label: "faceted-search-config.scopedSites.site.label",
                     description: "faceted-search-config.scopedSites.site.description",
                     optionsConfig: {
                        fixed: availableSites
                        // publishTopic: "ALF_GET_FORM_CONTROL_OPTIONS",
                        // publishPayload: {
                        //    url: url.context + "/proxy/alfresco/api/sites",
                        //    itemsAttribute: "",
                        //    labelAttribute: "title",
                        //    valueAttribute: "shortName"
                        // }
                     }
                  }
               }
            ],
            visibilityConfig: {
               initialValue: false,
               rules: [
                  {
                     targetId: "SCOPE",
                     is: ["SCOPED_SITES"]
                  }
               ]
            },
            valueDisplayMap: availableSites
         }
      }
   ];
   return formWidgets;
}



/* *********************************************************************************
 *                                                                                 *
 * FACET CLICK PUBLICATION                                                         *
 *                                                                                 *
 ***********************************************************************************/

var facetClickConfig = {
   propertyToRender: "filterID",
   useCurrentItemAsPayload: false,
   publishTopic: "ALF_CREATE_FORM_DIALOG_REQUEST",
   publishPayloadType: "PROCESS",
   publishPayloadModifiers: ["processCurrentItemTokens","setCurrentItem"],
   publishPayload: {
      dialogTitle: "{filterID}",
      dialogConfirmationButtonTitle: msg.get("faceted-search-config.form.save.label"),
      dialogCancellationButtonTitle: msg.get("faceted-search-config.form.cancel.label"),
      formSubmissionTopic: "ALF_CRUD_UPDATE",
      widgets: getFormDefinition(false),
      fixedWidth: true,
      formValue: "___AlfCurrentItem"
   }
};

/* *********************************************************************************
 *                                                                                 *
 * CREATE FACET PUBLICATION                                                        *
 *                                                                                 *
 ***********************************************************************************/

var createFacetButton = {
   id: "CREATE_FACET_BUTTON",
   name: "alfresco/buttons/AlfButton",
   config: {
      label: msg.get("faceted-search-config.create-facet.label"),
      additionalCssClasses: "call-to-action",
      publishTopic: "ALF_CREATE_FORM_DIALOG_REQUEST",
      publishPayloadType: "PROCESS",
      publishPayloadModifiers: ["processCurrentItemTokens"],
      publishPayload: {
         dialogTitle: "faceted-search-config.create-facet.label",
         dialogConfirmationButtonTitle: msg.get("faceted-search-config.form.save.label"),
         dialogCancellationButtonTitle: msg.get("faceted-search-config.form.cancel.label"),
         formSubmissionTopic: "ALF_CRUD_CREATE",
         fixedWidth: true,
         widgets: getFormDefinition(true)
      }
   }
};


/* *********************************************************************************
 *                                                                                 *
 * MAIN PAGE DEFINIITION                                                           *
 *                                                                                 *
 ***********************************************************************************/

var main = {
   id: "SEARCH_CONFIG_MAIN_VERTICAL",
   name: "alfresco/layout/VerticalWidgets",
   config: {
      currentItem: {
         user: _processedUserData
      },
      baseClass: "side-margins",
      widgets: [
         {
            id: "SEARCH_CONFIG_VSPACER",
            name: "alfresco/html/Spacer",
            config: {
               height: "18px"
            }
         },
         {
            id: "SEARCH_CONFIG_NO_PERMISSION_SECTION",
            name: "alfresco/layout/HorizontalWidgets",
            config: {
               renderFilterMethod: "ALL",
               renderFilter: [
                  {
                     property: "user.isAdmin",
                     values: [false]
                  },
                  {
                     property: "user.groups.GROUP_ALFRESCO_SEARCH_ADMINISTRATORS",
                     renderOnAbsentProperty: true,
                     values: [false]
                  },
                  {
                     property: "user.isNetworkAdmin",
                     values: [false]
                  }
               ],
               widgets: [
                  {
                     id: "SEARCH_CONFIG_NO_PERMISSION_WARNING",
                     name: "alfresco/header/Warning",
                     config: {
                        warnings: [
                           {
                              message: msg.get("faceted-search-config.page.no-permissions"),
                              level: 3
                           }
                        ]
                     }
                  }
               ]
            }
         },
         {
            id: "SEARCH_CONFIG_MAIN_HORIZONTAL",
            name: "alfresco/layout/HorizontalWidgets",
            config: {
               renderFilterMethod: "ANY",
               renderFilter: [
                  {
                     property: "user.isAdmin",
                     values: [true]
                  },
                  {
                     property: "user.groups.GROUP_ALFRESCO_ADMINISTRATORS",
                     values: [true]
                  },
                  {
                     property: "user.groups.GROUP_ALFRESCO_SEARCH_ADMINISTRATORS",
                     values: [true]
                  },
                  {
                     property: "user.isNetworkAdmin",
                     values: [true]
                  }
               ],
               widgetMarginRight: "10",
               widgets: [
                  {
                     name: "alfresco/layout/VerticalWidgets",
                     config: {
                        widgetMarginBottom: "10",
                        widgets: [
                           createFacetButton,
                           {
                              id: "SEARCH_CONFIG_FACET_LIST",
                              name: "alfresco/lists/AlfList",
                              config: {
                                 loadDataPublishTopic: "ALF_CRUD_GET_ALL",
                                 loadDataPublishPayload: {
                                    url: "api/facet/facet-config"
                                 },
                                 itemsProperty: "facets",
                                 widgets: [
                                    {
                                       id: "SEARCH_CONFIG_FACET_LIST_VIEW",
                                       name: "alfresco/documentlibrary/views/AlfDocumentListView",
                                       config: {
                                          additionalCssClasses: "bordered",
                                          widgetsForHeader: [
                                             {
                                                id: "SEARCH_CONFIG_REORDER_HEADER",
                                                name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                                config: {
                                                   label: "",
                                                   sortable: false
                                                }
                                             },
                                             {
                                                id: "SEARCH_CONFIG_FILTER_ID_HEADER",
                                                name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                                config: {
                                                   label: msg.get("faceted-search-config.filterId.label"),
                                                   sortable: false
                                                }
                                             },
                                             {
                                                id: "SEARCH_CONFIG_DISPLAY_NAME_HEADER",
                                                name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                                config: {
                                                   label: msg.get("faceted-search-config.displayName.label"),
                                                   sortable: false
                                                }
                                             },
                                             {
                                                id: "SEARCH_CONFIG_FACET_QNAME_HEADER",
                                                name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                                config: {
                                                   label: msg.get("faceted-search-config.facetQName.label"),
                                                   sortable: false
                                                }
                                             },
                                             {
                                                id: "SEARCH_CONFIG_DISPLAY_CONTROL_HEADER",
                                                name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                                config: {
                                                   label: msg.get("faceted-search-config.displayControl.label"),
                                                   sortable: false
                                                }
                                             },
                                             {
                                                id: "SEARCH_CONFIG_IS_ENABLED_HEADER",
                                                name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                                config: {
                                                   label: msg.get("faceted-search-config.isEnabled.label")
                                                }
                                             },
                                             {
                                                id: "SEARCH_CONFIG_IS_DEFAULT_HEADER",
                                                name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                                config: {
                                                   label: msg.get("faceted-search-config.isDefault.label")
                                                }
                                             },
                                             {
                                                id: "SEARCH_CONFIG_SCOPE_HEADER",
                                                name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                                config: {
                                                   label: msg.get("faceted-search-config.scope.label")
                                                }
                                             },
                                             {
                                                id: "SEARCH_CONFIG_ACTIONS_HEADER",
                                                name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                                config: {
                                                   label: ""
                                                }
                                             }
                                          ],
                                          widgets: [
                                             {
                                                id: "SEARCH_CONFIG_FACET_LIST_VIEW_ROW",
                                                name: "alfresco/documentlibrary/views/layouts/Row",
                                                config: {
                                                   widgets: [
                                                      {
                                                         id: "SEARCH_CONFIG_REORDER_CELL",
                                                         name: "alfresco/documentlibrary/views/layouts/Cell",
                                                         config: {
                                                            additionalCssClasses: "mediumpad",
                                                            width: "50px",
                                                            widgets: [
                                                               {
                                                                  id: "SEARCH_CONFIG_REORDER",
                                                                  name: "alfresco/renderers/Reorder",
                                                                  config: {
                                                                     propertyToRender: "filterID",
                                                                     moveUpPublishTopic: "ALF_CRUD_UPDATE",
                                                                     moveUpPublishPayloadType: "PROCESS",
                                                                     moveUpPublishPayloadModifiers: ["processCurrentItemTokens"],
                                                                     moveUpPublishPayloadItemMixin: true,
                                                                     moveUpPublishPayload: {
                                                                        url: "api/facet/facet-config/{filterID}?relativePos=-1"
                                                                     },
                                                                     moveDownPublishTopic: "ALF_CRUD_UPDATE",
                                                                     moveDownPublishPayloadType: "PROCESS",
                                                                     moveDownPublishPayloadModifiers: ["processCurrentItemTokens"],
                                                                     moveDownPublishPayloadItemMixin: true,
                                                                     moveDownPublishPayload: {
                                                                        url: "api/facet/facet-config/{filterID}?relativePos=1"
                                                                     }
                                                                  }
                                                               }
                                                            ]
                                                         }
                                                      },
                                                      {
                                                         id: "SEARCH_CONFIG_FILTER_ID_CELL",
                                                         name: "alfresco/documentlibrary/views/layouts/Cell",
                                                         config: {
                                                            additionalCssClasses: "mediumpad",
                                                            width: "",
                                                            widgets: [
                                                               {
                                                                  id: "SEARCH_CONFIG_FILTER_ID",
                                                                  name: "alfresco/renderers/PropertyLink",
                                                                  config: facetClickConfig
                                                               }
                                                            ]
                                                         }
                                                      },
                                                      {
                                                         id: "SEARCH_CONFIG_DISPLAY_NAME_CELL",
                                                         name: "alfresco/documentlibrary/views/layouts/Cell",
                                                         config: {
                                                            additionalCssClasses: "mediumpad",
                                                            width: "",
                                                            widgets: [
                                                               {
                                                                  id: "SEARCH_CONFIG_DISPLAY_NAME",
                                                                  name: "alfresco/renderers/InlineEditProperty",
                                                                  config: {
                                                                     propertyToRender: "displayName",
                                                                     refreshCurrentItem: true,
                                                                     requirementConfig: {
                                                                        initialValue: true
                                                                     },
                                                                     publishTopic: "ALF_CRUD_UPDATE",
                                                                     publishPayloadType: "PROCESS",
                                                                     publishPayloadModifiers: ["processCurrentItemTokens"],
                                                                     publishPayloadItemMixin: false,
                                                                     publishPayload: {
                                                                        filterID: "{filterID}",
                                                                        url: "api/facet/facet-config/{filterID}",
                                                                        noRefresh: true,
                                                                        successMessage: msg.get("faceted-search-config.update.successMessage")
                                                                     }
                                                                  }
                                                               }
                                                            ]
                                                         }
                                                      },
                                                      {
                                                         id: "SEARCH_CONFIG_FACET_QNAME_CELL",
                                                         name: "alfresco/documentlibrary/views/layouts/Cell",
                                                         config: {
                                                            additionalCssClasses: "mediumpad",
                                                            width: "",
                                                            widgets: [
                                                               {
                                                                  id: "SEARCH_CONFIG_FACET_QNAME",
                                                                  name: "alfresco/renderers/InlineEditSelect",
                                                                  config: {
                                                                     propertyToRender: "facetQName",
                                                                     refreshCurrentItem: true,
                                                                     publishTopic: "ALF_CRUD_UPDATE",
                                                                     publishPayloadType: "PROCESS",
                                                                     publishPayloadModifiers: ["processCurrentItemTokens"],
                                                                     publishPayloadItemMixin: false,
                                                                     publishPayload: {
                                                                        filterID: "{filterID}",
                                                                        url: "api/facet/facet-config/{filterID}",
                                                                        noRefresh: true,
                                                                        successMessage: msg.get("faceted-search-config.update.successMessage")
                                                                     },
                                                                     optionsConfig: {
                                                                        fixed: facetetableProperties
                                                                        // publishTopic: "ALF_GET_FORM_CONTROL_OPTIONS",
                                                                        // publishPayload: {
                                                                        //    url: url.context + "/proxy/alfresco/api/facet/facetable-properties",
                                                                        //    itemsAttribute: "data.properties",
                                                                        //    labelAttribute: "displayName",
                                                                        //    valueAttribute: "longqname"
                                                                        // }
                                                                     },
                                                                     valueDisplayMap: facetetableProperties,
                                                                     hiddenDataRules: [
                                                                        {
                                                                           name: "customProperties",
                                                                           rulePassValue: {
                                                                              blockIncludeFacetRequest: {
                                                                                 name: "{http://www.alfresco.org/model/solrfacetcustomproperty/1.0}blockIncludeFacetRequest",
                                                                                 title: null,
                                                                                 type: null,
                                                                                 value: "true"
                                                                              }
                                                                           },
                                                                           ruleFailValue: {},
                                                                           is: ["{http://www.alfresco.org/model/content/1.0}created","{http://www.alfresco.org/model/content/1.0}modified","{http://www.alfresco.org/model/content/1.0}content.size"]
                                                                        }
                                                                     ]
                                                                  }
                                                               }
                                                            ]
                                                         }
                                                      },
                                                      {
                                                         id: "SEARCH_CONFIG_DISPLAY_CONTROL_CELL",
                                                         name: "alfresco/documentlibrary/views/layouts/Cell",
                                                         config: {
                                                            additionalCssClasses: "mediumpad",
                                                            width: "",
                                                            widgets: [
                                                               {
                                                                  id: "SEARCH_CONFIG_DISPLAY_CONTROL",
                                                                  name: "alfresco/renderers/InlineEditSelect",
                                                                  config: {
                                                                     propertyToRender: "displayControl",
                                                                     refreshCurrentItem: true,
                                                                     publishTopic: "ALF_CRUD_UPDATE",
                                                                     publishPayloadType: "PROCESS",
                                                                     publishPayloadModifiers: ["processCurrentItemTokens"],
                                                                     publishPayloadItemMixin: false,
                                                                     publishPayload: {
                                                                        filterID: "{filterID}",
                                                                        url: "api/facet/facet-config/{filterID}",
                                                                        noRefresh: true,
                                                                        successMessage: msg.get("faceted-search-config.update.successMessage")
                                                                     },
                                                                     optionsConfig: {
                                                                        fixed: getAvailableFacetControls() 
                                                                     },
                                                                     valueDisplayMap: getAvailableFacetControls()
                                                                  }
                                                               }
                                                            ]
                                                         }
                                                      },
                                                      {
                                                         id: "SEARCH_CONFIG_IS_ENABLED_CELL",
                                                         name: "alfresco/documentlibrary/views/layouts/Cell",
                                                         config: {
                                                            additionalCssClasses: "mediumpad",
                                                            width: "50px",
                                                            widgets: [
                                                               {
                                                                  id: "SEARCH_CONFIG_IS_ENABLED",
                                                                  name: "alfresco/renderers/InlineEditSelect",
                                                                  config: {
                                                                     propertyToRender: "isEnabled",
                                                                     refreshCurrentItem: true,
                                                                     publishTopic: "ALF_CRUD_UPDATE",
                                                                     publishPayloadType: "PROCESS",
                                                                     publishPayloadModifiers: ["processCurrentItemTokens"],
                                                                     publishPayloadItemMixin: false,
                                                                     publishPayload: {
                                                                        filterID: "{filterID}",
                                                                        url: "api/facet/facet-config/{filterID}",
                                                                        noRefresh: true,
                                                                        successMessage: msg.get("faceted-search-config.update.successMessage")
                                                                     },
                                                                     optionsConfig: {
                                                                        fixed: [
                                                                           {
                                                                              label: msg.get("faceted-search-config.isEnabled.yes"),
                                                                              value: "true"
                                                                           },
                                                                           {
                                                                              label: msg.get("faceted-search-config.isEnabled.no"),
                                                                              value: "false"
                                                                           }
                                                                        ]
                                                                     },
                                                                     valueDisplayMap: [
                                                                        {
                                                                           label: msg.get("faceted-search-config.isEnabled.yes"),
                                                                           value: true
                                                                        },
                                                                        {
                                                                           label: msg.get("faceted-search-config.isEnabled.no"),
                                                                           value: false
                                                                        }
                                                                     ]
                                                                  }
                                                               }
                                                            ]
                                                         }
                                                      },
                                                      {
                                                         id: "SEARCH_CONFIG_IS_DEFAULT_CELL",
                                                         name: "alfresco/documentlibrary/views/layouts/Cell",
                                                         config: {
                                                            additionalCssClasses: "mediumpad",
                                                            width: "50px",
                                                            widgets: [
                                                               {
                                                                  id: "SEARCH_CONFIG_IS_DEFAULT",
                                                                  name: "alfresco/renderers/Boolean",
                                                                  config: {
                                                                     propertyToRender: "isDefault"
                                                                  }
                                                               }
                                                            ]
                                                         }
                                                      },
                                                      {
                                                         id: "SEARCH_CONFIG_SCOPE_CELL",
                                                         name: "alfresco/documentlibrary/views/layouts/Cell",
                                                         config: {
                                                            additionalCssClasses: "mediumpad",
                                                            width: "",
                                                            widgets: [
                                                               {
                                                                  id: "SEARCH_CONFIG_SCOPE",
                                                                  name: "alfresco/renderers/Property",
                                                                  config: {
                                                                     propertyToRender: "scope",
                                                                     valueDisplayMap: [
                                                                        {
                                                                           label: msg.get("faceted-search-config.scope.none.label"),
                                                                           value: "ALL"
                                                                        },
                                                                        {
                                                                           label: msg.get("faceted-search-config.scope.site.label"),
                                                                           value: "SCOPED_SITES"
                                                                        }
                                                                     ]
                                                                  }
                                                               }
                                                            ]
                                                         }
                                                      },
                                                      {
                                                         id: "SEARCH_CONFIG_ACTIONS_CELL",
                                                         name: "alfresco/documentlibrary/views/layouts/Cell",
                                                         config: {
                                                            additionalCssClasses: "mediumpad",
                                                            width: "50px",
                                                            widgets: [
                                                               {
                                                                  id: "SEARCH_CONFIG_ACTIONS",
                                                                  name: "alfresco/renderers/PublishAction",
                                                                  config: {
                                                                     iconClass: "delete-16",
                                                                     propertyToRender: "filterID",
                                                                     altText: msg.get("faceted-search-config.delete.altText"),
                                                                     publishTopic: "ALF_CRUD_DELETE",
                                                                     publishPayloadType: "PROCESS",
                                                                     publishPayload: {
                                                                        requiresConfirmation: true,
                                                                        url: "api/facet/facet-config/{filterID}",
                                                                        confirmationTitle: msg.get("faceted-search-config.delete.confirmationTitle"),
                                                                        confirmationPrompt: msg.get("faceted-search-config.delete.confirmationPrompt"),
                                                                        successMessage: msg.get("faceted-search-config.delete.successMessage")
                                                                     },
                                                                     publishPayloadModifiers: ["processCurrentItemTokens"],
                                                                     renderFilter: [
                                                                        {
                                                                           property: "isDefault",
                                                                           values: [false],
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

widgets.push(main);

// Push services and widgets into the getFooterModel to return with a sticky footer wrapper
model.jsonModel = getFooterModel(services, widgets);