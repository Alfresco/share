/**
 * Copyright (C) 2005-2015 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * @module cmm/services/CMMService
 * @extends module:alfresco/core/Core
 * @mixes module:alfresco/core/CoreXhr
 * @author Richard Smith
 * @author Kevin Roast
 */
define(["dojo/_base/declare",
        "alfresco/core/Core",
        "alfresco/core/CoreXhr",
        "dojo/request/xhr",
        "dojo/json",
        "dojo/_base/lang",
        "dojo/_base/array",
        "dojox/uuid",
        "dojo/on",
        "dojo/query",
        "dojo/dom-class",
        "alfresco/buttons/AlfButton",
        "service/constants/Default",
        "cmm/CMMConstants"],
        function(declare, AlfCore, AlfXhr, xhr, JSON, lang, array, uuid, on, query, domClass, AlfButton, AlfConstants, CMMConstants) {

   return declare([AlfCore, AlfXhr], {

      /**
       * A map of the type namespaces to be blacklisted from parent selection lists.
       *
       * @instance
       * @type {Object}
       */
      typesBlackList: {},
      
      /**
       * A map of the aspect namespaces to be blacklisted from parent selection lists.
       *
       * @instance
       * @type {Object}
       */
      aspectsBlackList: {},
      
      /**
       * An array of the i18n files to use with this widget.
       *
       * @instance
       * @type {Array}
       */
      i18nRequirements: [{i18nFile: "./i18n/CMMService.properties"}],

      /**
       * An object containing details of the API end points used by this service.
       * Ideally changes to the API should require modifications to the CMMService which are limited to this 
       * configuration object.
       * 
       * @instance
       * @type {object}
       */
      serviceUrl: {
         
         modelServiceUri: AlfConstants.URL_CONTEXT + "service/cmm/model-service",
         
         // Model
         getModels: {
            method: "GET",
            uri: AlfConstants.URL_CONTEXT + "proxy/alfresco-api/-default-/private/alfresco/versions/1/cmm"
         },
         getModel: {
            method: "GET",
            uri: AlfConstants.URL_CONTEXT + "proxy/alfresco-api/-default-/private/alfresco/versions/1/cmm/{name}"
         },
         exportModel: {
            method: "POST",
            uri: AlfConstants.URL_CONTEXT + "proxy/alfresco-api/-default-/private/alfresco/versions/1/cmm/{name}/download?extModule=true"
         },
         exportModelStatus: {
            method: "GET",
            uri: AlfConstants.PROXY_URI + "api/internal/downloads/{nodeRef}/status"
         },
         exportModelDownload: {
            method: "GET",
            uri: AlfConstants.PROXY_URI + "api/node/content/{nodeRef}/{fileName}.zip"
         },
         importModel: {
            method: "POST",
            uri: AlfConstants.PROXY_URI + "api/cmm/upload"
         },
         getModelForms: {
            method: "GET",
            uri: AlfConstants.URL_CONTEXT + "service/cmm/model-service/{name}/forms"
         },
         
         // Type
         getTypes: {
            method: "GET",
            uri: AlfConstants.URL_CONTEXT + "proxy/alfresco-api/-default-/private/alfresco/versions/1/cmm/{name}/types"
         },
         getParentTypes: {
            method: "GET",
            uri: AlfConstants.PROXY_URI + "api/classes/cm_content/subclasses"
         },
         getParentFolderTypes: {
            method: "GET",
            uri: AlfConstants.PROXY_URI + "api/classes/cm_folder/subclasses"
         },
         
         // Property group (aspect)
         getPropertyGroups: {
            method: "GET",
            uri: AlfConstants.URL_CONTEXT + "proxy/alfresco-api/-default-/private/alfresco/versions/1/cmm/{name}/aspects"
         },
         getParentPropertyGroups: {
            method: "GET",
            uri: AlfConstants.URL_CONTEXT + "service/cmm/dictionary/aspects"
         },

         // Editor for a type or aspect
         getEditorData: {
            method: "GET",
            uri: AlfConstants.URL_CONTEXT + "service/cmm/model-service/{name}/{entityName}"
         },

         // Properties for a type or aspect
         getProperties: {
            method: "GET",
            uri: AlfConstants.URL_CONTEXT + "proxy/alfresco-api/-default-/private/alfresco/versions/1/cmm/{name}/{entityClass}/{entityName}"
         },
         getEditorProperties: {
            method: "GET",
            uri: AlfConstants.URL_CONTEXT + "proxy/alfresco-api/-default-/private/alfresco/versions/1/cmm/{name}/{entityClass}/{entityName}?select=allProps"
         }
      },
      
      /**
       * An array of the default built-in properties to be displayed for all custom types - these are extracted from common content model
       * aspects and the cm:content base type. See share-form-config.xml for the original form definitions that use these.
       * 
       * @instance
       * @type {Array}
       */
      standardProperties: [
         {
            id: "cm:name",
            label: "cmm.cm_name",
            dataType: "d:text"
         },
         {
            id: "cm:title",
            label: "cmm.cm_title",
            dataType: "d:text",
            config: {
               "force": true
            }
         },
         {
            id: "cm:description",
            label: "cmm.cm_description",
            dataType: "d:text",
            config: {
               "force": true
            }
         },
         {
            id: "cm:creator",
            label: "cmm.cm_creator",
            dataType: "d:text",
            config: {
               "for-mode": "view"
            }
         },
         {
            id: "cm:created",
            label: "cmm.cm_created",
            dataType: "d:datetime",
            config: {
               "for-mode": "view"
            }
         },
         {
            id: "cm:modifier",
            label: "cmm.cm_modifier",
            dataType: "d:text",
            config: {
               "for-mode": "view"
            }
         },
         {
            id: "cm:modified",
            label: "cmm.cm_modified",
            dataType: "d:datetime",
            config: {
               "for-mode": "view"
            }
         },
         {
            id: "size",
            label: "cmm.cm_size",
            dataType: "size",
            config: {
               "for-mode": "view",
               "controlType": "size"
            }
         },
         {
            id: "mimetype",
            label: "cmm.cm_mimetype",
            dataType: "mimetype",
            config: {
               "controlType": "mimetype"
            }
         },
         {
            id: "cm:author",
            label: "cmm.cm_author",
            dataType: "d:text",
            config: {
               "force": true
            }
         },
         {
            id: "cm:taggable",
            label: "cmm.cm_taggable",
            dataType: "taggable",
            config: {
               "for-mode": "edit",
               "force": true,
               "controlType": "taggable"
            }
         },
         {
            id: "cm:categories",
            label: "cmm.cm_categories",
            dataType: "categories",
            config: {
               "controlType": "categories"
            }
         }
      ],
      
      /**
       * A map of the properties to ignore from super type property list (e.g. from cm:content super type) when building the
       * list of standard properties to display in the Layout editor.
       * 
       * @instance
       * @type {Object}
       */
      blackListProperties: {
         "cm:content": true,
         "cm:name": true
      },

      /**
       * A map of indexing options for translating the user selected indexing option into the indexed, facetable and 
       * tokenisation mode options when creating or updating a property
       */
      propertyIndexingOptions: {
         "nontxt-none": {
            type: "nontxt",
            indexed: false,
            facetable: "UNSET",
            indexTokenisationMode: "TRUE"
         },
         "nontxt-standard": {
            type: "nontxt",
            indexed: true,
            facetable: "UNSET",
            indexTokenisationMode: "TRUE"
         },
         "nontxt-enhanced": {
            type: "nontxt",
            indexed: true,
            facetable: "TRUE",
            indexTokenisationMode: "TRUE"
         },
         "boolean-none": {
             type: "boolean",
             indexed: false,
             facetable: "UNSET",
             indexTokenisationMode: "TRUE"
         },
         "boolean-standard": {
             type: "boolean",
             indexed: true,
             facetable: "UNSET",
             indexTokenisationMode: "TRUE"
         },
         "txt-none": {
            type: "txt",
            indexed: false,
            facetable: "UNSET",
            indexTokenisationMode: "TRUE"
         },
         "txt-standard": {
            type: "txt",
            indexed: true,
            facetable: "UNSET",
            indexTokenisationMode: "TRUE"
         },
         "txt-free": {
            type: "txt",
            indexed: true,
            facetable: "FALSE",
            indexTokenisationMode: "TRUE"
         },
         "txt-list-whole": {
            type: "txt",
            indexed: true,
            facetable: "TRUE",
            indexTokenisationMode: "FALSE"
         },
         "txt-list-partial": {
            type: "txt",
            indexed: true,
            facetable: "TRUE",
            indexTokenisationMode: "BOTH"
         },
         "txt-pattern-unique": {
            type: "txt",
            indexed: true,
            facetable: "FALSE",
            indexTokenisationMode: "FALSE"
         },
         "txt-pattern-many": {
            type: "txt",
            indexed: true,
            facetable: "FALSE",
            indexTokenisationMode: "BOTH"
         }
      },

      /**
       * An object containing details of the xhr response hander and error msg key.
       * 
       * @instance
       */
      errorResponse: {
         handler: "json",
         msgKey: "response.data.error.briefSummary"
      },

      /**
       * Sets up the subscriptions for the CMMService
       *
       * @instance
       * @param {array} args The constructor arguments.
       */
      constructor: function alfresco_cmm_services_CMMService__constructor(args) {

         lang.mixin(this, args);

         // Model
         this.alfSubscribe(CMMConstants.GET_MODELS_TOPIC, lang.hitch(this, this.getModels));
         this.alfSubscribe(CMMConstants.CREATE_MODEL, lang.hitch(this, this.createModel));
         this.alfSubscribe(CMMConstants.EDIT_MODEL, lang.hitch(this, this.editModel));
         this.alfSubscribe(CMMConstants.ACTIVATE_MODEL, lang.hitch(this, this.activateModel));
         this.alfSubscribe(CMMConstants.DEACTIVATE_MODEL, lang.hitch(this, this.deactivateModel));
         this.alfSubscribe(CMMConstants.DELETE_MODEL, lang.hitch(this, this.deleteModelAction));
         this.alfSubscribe(CMMConstants.EXPORT_MODEL, lang.hitch(this, this.exportModelAction));
         this.alfSubscribe(CMMConstants.IMPORT_MODEL, lang.hitch(this, this.importModel));

         // Type
         this.alfSubscribe(CMMConstants.GET_TYPES_TOPIC, lang.hitch(this, this.getTypes));
         this.alfSubscribe(CMMConstants.GET_PARENT_TYPES, lang.hitch(this, this.getParentTypes));
         this.alfSubscribe(CMMConstants.CREATE_CUSTOMTYPE, lang.hitch(this, this.createCustomType));
         this.alfSubscribe(CMMConstants.EDIT_CUSTOMTYPE, lang.hitch(this, this.editCustomType));
         this.alfSubscribe(CMMConstants.DELETE_TYPE, lang.hitch(this, this.deleteTypeAction));
         this.alfSubscribe(CMMConstants.FIND_TYPES, lang.hitch(this, this.findTypesAction));

         // Property group
         this.alfSubscribe(CMMConstants.GET_PROPERTYGROUPS_TOPIC, lang.hitch(this, this.getPropertyGroups));
         this.alfSubscribe(CMMConstants.GET_PARENT_PROPERTYGROUPS, lang.hitch(this, this.getParentPropertyGroups));
         this.alfSubscribe(CMMConstants.CREATE_PROPERTYGROUP, lang.hitch(this, this.createPropertyGroup));
         this.alfSubscribe(CMMConstants.EDIT_PROPERTYGROUP, lang.hitch(this, this.editPropertyGroup));
         this.alfSubscribe(CMMConstants.DELETE_PROPERTYGROUP, lang.hitch(this, this.deletePropertyGroupAction));
         this.alfSubscribe(CMMConstants.FIND_PROPERTYGROUPS, lang.hitch(this, this.findPropertyGroupsAction));

         // Property
         this.alfSubscribe(CMMConstants.GET_PROPERTIES_TOPIC, lang.hitch(this, this.getProperties));
         this.alfSubscribe(CMMConstants.GET_EDITOR_PROPERTIES_TOPIC, lang.hitch(this, this.getEditorProperties));
         this.alfSubscribe(CMMConstants.CREATE_PROPERTY, lang.hitch(this, this.createProperty));
         this.alfSubscribe(CMMConstants.EDIT_PROPERTY, lang.hitch(this, this.editProperty));
         this.alfSubscribe(CMMConstants.DELETE_PROPERTY, lang.hitch(this, this.deletePropertyAction));
         
         // Form Editor
         this.alfSubscribe(CMMConstants.FORM_EDITOR_SAVE_TOPIC, lang.hitch(this, this.saveFormAction));
         this.alfSubscribe(CMMConstants.FORM_EDITOR_GET_VALUE, lang.hitch(this, this.getEditorValue));
         this.alfSubscribe(CMMConstants.FORM_EDITOR_CLEAR_TO_PALETTE_ACTION, lang.hitch(this, this.clearToPaletteAction));
         this.alfSubscribe(CMMConstants.FORM_EDITOR_DEFAULT_LAYOUT_ACTION, lang.hitch(this, this.defaultLayoutAction));
         this.alfSubscribe(CMMConstants.FORM_EDITOR_CANVAS_VALUE_CHANGED, lang.hitch(this, this.canvasValueChanged));
      },

      /**
       * Retrieve the list of custom models
       * 
       * @instance
       * @param {object} payload The details of the request
       */
      getModels: function alfresco_cmm_services_CMMService__getModels(payload) {
         
         this.serviceXhr({
            url: this.serviceUrl.getModels.uri,
            method: this.serviceUrl.getModels.method,
            callbackScope: this,
            successCallback: function(res) {
               // augment response to add parent model name and the model status
               var models = res.list.entries;
               models.sort(function (a, b) {
                  return (a.entry.name < b.entry.name) ? -1 : 1;
               });
               for (var i=0; i<models.length; i++)
               {
                  models[i].entry.statusLabel = this.message(models[i].entry.status === 'ACTIVE' ? "cmm.label.status.grid.active" : "cmm.label.status.grid.draft");
               }
               // call original success topic as the list widget is expecting this data
               var responseTopic = payload.alfResponseTopic || payload.responseTopic;
               if (responseTopic != null)
               {
                  this.alfPublish(responseTopic + "_SUCCESS", res);
               }
            }
         });
      },

      /**
       * Retrieve the list of custom types for a model
       * 
       * @instance
       * @param {object} payload The details of the request - payload.name current item
       */
      getTypes: function alfresco_cmm_services_CMMService__getTypes(payload) {
         
         var name = lang.getObject("name", false, payload),
             responseTopic = payload.alfResponseTopic || payload.responseTopic;

         if (name != null)
         {
            // Get the model
            this.serviceXhr({
               url: lang.replace(this.serviceUrl.getModel.uri, {name: encodeURIComponent(name)}),
               method: this.serviceUrl.getModel.method,
               callbackScope: this,
               successCallback: function(res) {

                  // Get the status of the model
                  var status = res.entry.status,
                      statusLabel = this.message(status === 'ACTIVE' ? "cmm.label.status.active" : "cmm.label.status.draft");

                  // Update the status header
                  this.alfPublish(CMMConstants.TYPES_LIST_SCOPE + CMMConstants.UPDATE_MODEL_HEADING_STATUS, {
                     status: status,
                     label: statusLabel
                  });

                  // Get the types
                  this.serviceXhr({
                     url: lang.replace(this.serviceUrl.getTypes.uri, {name: encodeURIComponent(name)}),
                     method: this.serviceUrl.getTypes.method,
                     callbackScope: this,
                     successCallback: function(res) {
                        // get form layout status for types
                        this.serviceXhr({
                           url: lang.replace(this.serviceUrl.getModelForms.uri, {name: encodeURIComponent(name)}),
                           method: this.serviceUrl.getModelForms.method,
                           successCallback: function(formsResponse) {
                              // augment response to add parent model name and the model status
                              // and the form layout status boolean
                              var types = res.list.entries;
                              types.sort(function (a, b) {
                                 return (a.entry.name < b.entry.name) ? -1 : 1;
                              });
                              for (var i=0; i<types.length; i++)
                              {
                                 types[i].modelName = name;
                                 types[i].status = status;
                                 // augment with boolean true or false depending on form state - cannot be null/undefined
                                 types[i].layout = (formsResponse.forms[types[i].entry.name] === true);
                              }
                              
                              // call original success topic as the list widget is expecting this data
                              if (responseTopic != null)
                              {
                                 this.alfPublish(responseTopic + "_SUCCESS", res);
                              }
                           },
                           callbackScope: this
                        });
                     }
                  });
               }
            });
         }
         else
         {
            this.alfLog("warn", "A request was made to get types for a model but no 'name' attribute was provided", payload, this);
            this.alfPublish(responseTopic + "_FAILURE", {}, payload);
         }
      },

      /**
       * Retrieve the list of property groups for a model
       * 
       * @instance
       * @param {object} payload The details of the request - payload.name current item
       */
      getPropertyGroups: function alfresco_cmm_services_CMMService__getPropertyGroups(payload) {
         
         var name = lang.getObject("name", false, payload),
             responseTopic = payload.alfResponseTopic || payload.responseTopic;

         if (name != null)
         {
            // Get the model
            this.serviceXhr({
               url: lang.replace(this.serviceUrl.getModel.uri, {name: encodeURIComponent(name)}),
               method: this.serviceUrl.getModel.method,
               callbackScope: this,
               successCallback: function(res) {
                  
                  // Get the status of the model
                  var status = res.entry.status;

                  // Get the aspects
                  this.serviceXhr({
                     url: lang.replace(this.serviceUrl.getPropertyGroups.uri, {name: encodeURIComponent(name)}),
                     method: this.serviceUrl.getPropertyGroups.method,
                     callbackScope: this,
                     successCallback: function(res) {
                        // get form layout status for aspects
                        this.serviceXhr({
                           url: lang.replace(this.serviceUrl.getModelForms.uri, {name: encodeURIComponent(name)}),
                           method: this.serviceUrl.getModelForms.method,
                           successCallback: function(formsResponse) {
                              // augment response to add parent model name and the model status
                              // and the form layout status boolean
                              var aspects = res.list.entries;
                              aspects.sort(function (a, b) {
                                 return (a.entry.name < b.entry.name) ? -1 : 1;
                              });
                              for (var i=0; i<aspects.length; i++)
                              {
                                 aspects[i].modelName = name;
                                 aspects[i].status = status;
                                 // augment with boolean true or false depending on form state - cannot be null/undefined
                                 aspects[i].layout = (formsResponse.forms[aspects[i].entry.name] === true);
                              }
                              
                              // call original success topic as the list widget is expecting this data
                              if (responseTopic != null)
                              {
                                 this.alfPublish(responseTopic + "_SUCCESS", res);
                              }
                           },
                           callbackScope: this
                        });
                     }
                  });
               }
            });
         }
         else
         {
            this.alfLog("warn", "A request was made to get property groups for a model but no 'name' attribute was provided", payload, this);
            this.alfPublish(responseTopic + "_FAILURE", {}, payload);
         }
      },
      
      /**
       * Create a new custom model based on the given form payload
       * 
       * @instance
       * @param {object} payload Form payload
       */
      createModel: function alfresco_cmm_services_CMMService__createModel(payload) {
         
         // apply payload to the JSON structure for our POST create request
         var model = {
            status: "DRAFT",
            namespaceUri: payload.namespace,
            namespacePrefix: payload.prefix,
            name: payload.name,
            description: payload.description,
            author: payload.author
         };
         this.serviceXhr({
            url: this.serviceUrl.modelServiceUri,
            method: "POST",
            data: {
               modelName: payload.name,
               operation: "createModel",
               data: model
            },
            handleAs: this.errorResponse.handler,
            successCallback: function(res) {
               this.alfPublish(CMMConstants.CREATE_MODEL + "_SUCCESS");
               this._reloadModelList();
            },
            failureCallback: function(res) {
               console.log("FAILURE: " + res);
               this.alfPublish(CMMConstants.NOTIFICATION_TOPIC, {
                  message: this._cleanError(lang.getObject(this.errorResponse.msgKey, false, res)) || this.message("cmm.message.unknown-error")
               });
            },
            callbackScope: this
         });
      },
      
      /**
       * Edit a custom model based on the given form payload
       * 
       * @instance
       * @param {object} payload The details of the model to put
       */
      editModel: function alfresco_cmm_services_CMMService__editModel(payload) {
         
         var name = lang.getObject("name", false, payload);
         if (name != null)
         {
            var model = {
               namespaceUri: payload.namespace,
               namespacePrefix: payload.prefix,
               description: payload.description,
               author: payload.author
            };
            this.serviceXhr({
               url: this.serviceUrl.modelServiceUri,
               method: "PUT",
               data: {
                  modelName: name,
                  operation: "editModel",
                  arguments: {
                     name: name
                  },
                  data: model
               },
               handleAs: this.errorResponse.handler,
               successCallback: function(res) {
                  this.alfPublish(CMMConstants.EDIT_MODEL + "_SUCCESS");
                  this._reloadModelList();
               },
               failureCallback: function(res) {
                  console.log("FAILURE: " + res);
                  this.alfPublish(CMMConstants.NOTIFICATION_TOPIC, {
                     message: this._cleanError(lang.getObject(this.errorResponse.msgKey, false, res)) || this.message("cmm.message.unknown-error")
                  });
               },
               callbackScope: this
            });
         }
         else
         {
            this.alfLog("warn", "A request was made to edit a model but no 'name' attribute was provided", payload, this);
         }
      },

      /**
       * Activate a custom model
       * 
       * @instance
       * @param {object} payload The details of the model to activate
       */
      activateModel: function alfresco_cmm_services_CMMService__activateModel(payload) {

         var name = lang.getObject("name", false, payload);
         if (name != null)
         {
            // activate the model in the repository and activate the associated Share Form configuration extension
            this.serviceXhr({
               url: this.serviceUrl.modelServiceUri,
               method: "PUT",
               data: {
                  modelName: name,
                  operation: "activateModel",
                  data: {
                     status: "ACTIVE"
                  },
                  arguments: {
                     name: name
                  }
               },
               handleAs: this.errorResponse.handler,
               alfTopic: payload.alfResponseTopic || payload.responseTopic,
               successCallback: function(res) {
                  this._reloadModelList();
               },
               failureCallback: function(res) {
                  console.log("FAILURE: " + res);
                  this.alfPublish(CMMConstants.NOTIFICATION_TOPIC, {
                     message: this._cleanError(lang.getObject(this.errorResponse.msgKey, false, res)) || this.message("cmm.message.unknown-error")
                  });
               },
               callbackScope: this
            });
         }
         else
         {
            this.alfLog("warn", "A request was made to activate a model but no 'name' attribute was provided", payload, this);
         }
      },

      /**
       * Deactivate a custom model
       * 
       * @instance
       * @param {object} payload The details of the model to deactivate
       */
      deactivateModel: function alfresco_cmm_services_CMMService__deactivateModel(payload) {
         
         var name = lang.getObject("name", false, payload);
         if (name != null)
         {
            this.serviceXhr({
               url: this.serviceUrl.modelServiceUri,
               method: "PUT",
               data: {
                  modelName: name,
                  operation: "deactivateModel",
                  data: {
                     status: "DRAFT"
                  },
                  arguments: {
                     name: name
                  }
               },
               handleAs: this.errorResponse.handler,
               alfTopic: payload.alfResponseTopic || payload.responseTopic,
               successCallback: function(res) {
                  this._reloadModelList();
               },
               failureCallback: function(res) {
                  console.log("FAILURE: " + res);
                  this.alfPublish(CMMConstants.NOTIFICATION_TOPIC, {
                     message: this._cleanError(lang.getObject(this.errorResponse.msgKey, false, res)) || this.message("cmm.message.unknown-error")
                  });
               },
               callbackScope: this
            });
         }
         else
         {
            this.alfLog("warn", "A request was made to deactivate a model but no 'name' attribute was provided", payload, this);
         }
      },
      
      /**
       * Start the delete process for a model
       * 
       * @instance
       * @param {object} payload The details of the model to delete
       */
      deleteModelAction: function alfresco_cmm_services_CMMService__deleteModelAction(payload) {
         
         var name = lang.getObject("name", false, payload);
         if (name != null)
         {
            var responseTopic = this.generateUuid();
            this._deleteHandle = this.alfSubscribe(responseTopic, lang.hitch(this, this.deleteModel));

            this.alfPublish("ALF_CREATE_DIALOG_REQUEST", {
               dialogId: "CMM_DELETE_MODEL_DIALOG",
               dialogTitle: this.message("cmm.model.delete-title"),
               textContent: this.message("cmm.model.delete-message", {"0": name}),
               widgetsButtons: [
                  {
                     name: "alfresco/buttons/AlfButton",
                     config: {
                        label: this.message("cmm.model.delete-button.confirm-label"),
                        publishTopic: this.pubSubScope + responseTopic,
                        publishPayload: payload
                     }
                  },
                  {
                     name: "alfresco/buttons/AlfButton",
                     config: {
                        label: this.message("cmm.model.delete-button.cancel-label"),
                        publishTopic: "close"
                     }
                  }
               ]
            });
         }
         else
         {
            this.alfLog("warn", "A request was made to delete a model but no 'name' attribute was provided", payload, this);
         }
      },
      
      /**
       * Start the Import process for a model
       * 
       * @instance
       * @param {object} payload The details of the model file to import
       */
      importModel: function alfresco_cmm_services_CMMService__importModel(payload) {
         
         if (payload.file && payload.file.length === 1)
         {
            var formData = new FormData();
            formData.append("file", payload.file[0]);
            var request = new XMLHttpRequest(),
                url = this.serviceUrl.importModel.uri;
            if (this.isCsrfFilterEnabled())
            {
               url += "?" + this.getCsrfParameter() + "=" + encodeURIComponent(this.getCsrfToken());
            }
            
            // bind to handle success on upload complete and response from server is ready
            on(request.upload, "load", lang.hitch(this, function successListener(res) {
               var _this = this;
               request.onreadystatechange = function _onreadystatechange() {
                  if (request.readyState === 4)
                  {
                     var json = JSON.parse(request.response);
                     switch (request.status)
                     {
                        case 200:
                           // inform Share tier of success and post forms
                           _this.serviceXhr({
                              url: _this.serviceUrl.modelServiceUri,
                              method: "POST",
                              data: {
                                 modelName: json.modelName,
                                 forms: json.shareExtModule
                              }
                           });
                           _this._reloadModelList();
                           break;
                        case 400:
                           _this.alfPublish(CMMConstants.NOTIFICATION_TOPIC, {
                              message: _this.message("cmm.message.error-import") + " " + json.message.substring(8)
                           });
                           break;
                        case 409:
                           _this.alfPublish(CMMConstants.NOTIFICATION_TOPIC, {
                              message: _this.message("cmm.message.error-conflict") + " " + json.message.substring(8)
                           });
                           break;
                        default:
                           _this.alfPublish(CMMConstants.NOTIFICATION_TOPIC, {
                              message: _this.message("cmm.message.unknown-error") + " " + json.message.substring(8)
                           });
                           break;
                     }
                  }
               };
            }));
            
            // bind to handle error from server
            on(request.upload, "error", lang.hitch(this, function failureListener() {
               console.log("FAILURE: " + request.state);
               this.alfPublish(CMMConstants.NOTIFICATION_TOPIC, {
                  message: this.message("cmm.message.error-import")
               });
            }));
            
            // perform the upload to API service
            request.open(this.serviceUrl.importModel.method, url, true);
            request.send(formData);
            
            // close the dialog immediately - process must be restarted to try again
            this.alfPublish(CMMConstants.IMPORT_MODEL + "_SUCCESS");
         }
         else
         {
            this.alfPublish(CMMConstants.IMPORT_MODEL + "_SUCCESS");
            this._reloadModelList();
         }
      },
      
      /**
       * Start the Export process for a model
       * 
       * @instance
       * @param {object} payload The details of the model to export
       */
      exportModelAction: function alfresco_cmm_services_CMMService__exportModelAction(payload) {
         
         var name = lang.getObject("name", false, payload);
         if (name != null)
         {
            this.serviceXhr({
               url: lang.replace(this.serviceUrl.exportModel.uri, {name: encodeURIComponent(name)}),
               method: this.serviceUrl.exportModel.method,
               data: {},
               callbackScope: this,
               successCallback: function(res) {
                  var nodeRef = res.entry.nodeRef;
                  var maxRetries = 10;
                  var fnCheckStatus = function() {
                     this.serviceXhr({
                        url: lang.replace(this.serviceUrl.exportModelStatus.uri, {nodeRef: nodeRef.replace(":/","")}),
                        method: this.serviceUrl.exportModelStatus.method,
                        callbackScope: this,
                        successCallback: function(res) {
                           if (res.status === "DONE")
                           {
                              window.open(
                                 lang.replace(this.serviceUrl.exportModelDownload.uri, {
                                    nodeRef: nodeRef.replace(":/",""),
                                    fileName: encodeURIComponent(name)
                                 }),
                                 "_blank"
                              );
                           }
                           else
                           {
                              if (--maxRetries)
                              {
                                 window.setTimeout(fnCheckStatus, 1000);
                              }
                              else
                              {
                                 this.alfPublish(CMMConstants.NOTIFICATION_TOPIC, {
                                    message: this._cleanError(lang.getObject(this.errorResponse.msgKey, false, res)) || this.message("cmm.message.download-status-error")
                                 });
                              }
                           }
                        },
                        failureCallback: function(res) {
                           console.log("FAILURE: " + res);
                           this.alfPublish(CMMConstants.NOTIFICATION_TOPIC, {
                              message: this._cleanError(lang.getObject(this.errorResponse.msgKey, false, res)) || this.message("cmm.message.download-status-error")
                           });
                        }
                     });
                  };
                  window.setTimeout(lang.hitch(this, fnCheckStatus), 1000);
               },
               failureCallback: function(res) {
                  console.log("FAILURE: " + res);
                  this.alfPublish(CMMConstants.NOTIFICATION_TOPIC, {
                     message: this._cleanError(lang.getObject(this.errorResponse.msgKey, false, res)) || this.message("cmm.message.unknown-error")
                  });
               },
               callbackScope: this
            });
         }
         else
         {
            this.alfLog("warn", "A request was made to export a model but no 'name' attribute was provided", payload, this);
         }
      },

      /**
       * Delete a custom model
       * 
       * @instance
       * @param {object} payload The details of the model to delete
       */
      deleteModel: function alfresco_cmm_services_CMMService__deleteModel(payload) {

         var name = lang.getObject("name", false, payload);
         if (name != null)
         {
            this.serviceXhr({
               url: this.serviceUrl.modelServiceUri,
               method: "DELETE",
               data: {
                  modelName: name,
                  operation: "deleteModel",
                  arguments: {
                     name: name
                  }
               },
               handleAs: this.errorResponse.handler,
               alfTopic: payload.alfResponseTopic || payload.responseTopic,
               successCallback: function(res) {
                  var subscriptionHandle = lang.getObject("requestConfig.subscriptionHandle", false, payload);
                  if (subscriptionHandle != null)
                  {
                     this.alfUnsubscribe(subscriptionHandle);
                  }
                  this._reloadModelList();
               },
               failureCallback: function(res) {
                  console.log("FAILURE: " + res);
                  this.alfPublish(CMMConstants.NOTIFICATION_TOPIC, {
                     message: this._cleanError(lang.getObject(this.errorResponse.msgKey, false, res)) || this.message("cmm.message.unknown-error")
                  });
               },
               callbackScope: this
            });
         }
         else
         {
            this.alfLog("warn", "A request was made to delete a model but no 'name' attribute was provided", payload, this);
         }
      },

      /**
       * Retrieve the list of parent types
       * 
       * @instance
       */
      getParentTypes: function alfresco_cmm_services_CMMService__getParentTypes(payload) {

         var resTypes = [];
         
         this.serviceXhr({
            url: this.serviceUrl.getParentTypes.uri,
            method: this.serviceUrl.getParentTypes.method,
            successCallback: function(types) {
               if (types)
               {
                  for (var i=0; i<types.length; i++)
                  {
                     var name = types[i].name;
                     // filter using namespace blacklist
                     if (!this.typesBlackList[name.split(':')[0]])
                     {
                        var title = types[i].title;
                        resTypes.push({
                           value: name,
                           label: name + (title ? " ("+title+")" : "") 
                        });
                     }
                  }
               }
               
               this.serviceXhr({
                  url: this.serviceUrl.getParentFolderTypes.uri,
                  method: this.serviceUrl.getParentFolderTypes.method,
                  successCallback: function(folders) {
                     if (folders)
                     {
                        for (var i=0; i<folders.length; i++)
                        {
                           var name = folders[i].name;
                           // filter using namespace blacklist
                           if (!this.typesBlackList[name.split(':')[0]])
                           {
                              var title = folders[i].title;
                              resTypes.push({
                                 value: name,
                                 label: name + (title ? " ("+title+")" : "") 
                              });
                           }
                        }
                     }
                     
                     // finally we sort the type list
                     resTypes.sort(function (a,b) {
                        return (a.label < b.label) ? -1 : 1;
                     });
                     
                     // call original success topic as the list widget is expecting this data
                     var responseTopic = payload.alfResponseTopic || payload.responseTopic;
                     if (responseTopic != null)
                     {
                        this.alfPublish(responseTopic, {
                           options: resTypes
                        });
                     }
                  }
               });
            }
         });
      },

      /**
       * Create a new custom Type based on the given form payload
       * 
       * @instance
       * @param {object} payload Form payload
       */
      createCustomType: function alfresco_cmm_services_CMMService__createCustomType(payload) {
         
         // see createMenu definition for how model_name is added to the form payload via the AlfDynamicPayloadButton config
         var model_name = lang.getObject("model_name", false, payload);
         if (model_name != null)
         {
            // apply payload to the JSON structure for our PUT create request
            var customType = {
               name: payload.name,
               description: payload.description,
               title: payload.title,
               parentName: payload.parent
            };
            this.serviceXhr({
               url: this.serviceUrl.modelServiceUri,
               method: "POST",
               data: {
                  modelName: model_name,
                  operation: "createType",
                  data: customType,
                  arguments: {
                     name: model_name
                  }
               },
               handleAs: this.errorResponse.handler,
               successCallback: function(res) {
                  this.alfPublish(CMMConstants.CREATE_CUSTOMTYPE + "_SUCCESS");
                  this._reloadTypesList();
               },
               failureCallback: function(res) {
                  console.log("FAILURE: " + res);
                  this.alfPublish(CMMConstants.NOTIFICATION_TOPIC, {
                     message: this._cleanError(lang.getObject(this.errorResponse.msgKey, false, res)) || this.message("cmm.message.unknown-error")
                  });
               },
               callbackScope: this
            });
         }
         else
         {
            this.alfLog("warn", "A request was made to create a custom type but no 'model_name' attribute was provided", payload, this);
         }
      },

      /**
       * Edit a custom type based on the given form payload
       * 
       * @instance
       * @param {object} payload The details of the type to put
       */
      editCustomType: function alfresco_cmm_services_CMMService__editCustomType(payload) {

         var model_name = lang.getObject("model_name", false, payload),
             type = lang.getObject("name", false, payload);
         if (model_name != null && type != null)
         {
            // apply payload to the JSON structure for our PUT create request
            var customType = {
               name: payload.name,
               description: payload.description,
               title: payload.title,
               parentName: payload.parent
            };
            this.serviceXhr({
               url: this.serviceUrl.modelServiceUri,
               method: "PUT",
               data: {
                  modelName: model_name,
                  operation: "editType",
                  arguments: {
                     name: model_name,
                     typeName: type
                  },
                  data: customType
               },
               handleAs: this.errorResponse.handler,
               successCallback: function(res) {
                  this.alfPublish(CMMConstants.EDIT_CUSTOMTYPE + "_SUCCESS");
                  this._reloadTypesList();
               },
               failureCallback: function(res) {
                  console.log("FAILURE: " + res);
                  this.alfPublish(CMMConstants.NOTIFICATION_TOPIC, {
                     message: this._cleanError(lang.getObject(this.errorResponse.msgKey, false, res)) || this.message("cmm.message.unknown-error")
                  });
               },
               callbackScope: this
            });
         }
         else
         {
            this.alfLog("warn", "A request was made to edit a custom type but no 'model_name' or 'name' attribute was provided", payload, this);
         }
      },
      
      /**
       * Navigate to search page using query to locate specific TYPE
       */
      findTypesAction: function alfresco_cmm_services_CMMService__findTypesAction(payload) {
         
         var name = lang.getObject("name", false, payload);
         if (name != null)
         {
            var typeParts = name.split(":");
            this.alfPublish("ALF_NAVIGATE_TO_PAGE", {
               type: "SHARE_PAGE_RELATIVE",
               url: "dp/ws/faceted-search#searchTerm=TYPE%3A%22" + encodeURIComponent(typeParts[0]) + "%3A" + encodeURIComponent(typeParts[1]) + "%22&scope=repo"
            });
         }
      },
      
      /**
       * Navigate to search page using query to locate specific ASPECT
       */
      findPropertyGroupsAction: function alfresco_cmm_services_CMMService__findPropertyGroupsAction(payload) {
         
         var name = lang.getObject("name", false, payload);
         if (name != null)
         {
            var aspectParts = name.split(":");
            this.alfPublish("ALF_NAVIGATE_TO_PAGE", {
               type: "SHARE_PAGE_RELATIVE",
               url: "dp/ws/faceted-search#searchTerm=ASPECT%3A%22" + encodeURIComponent(aspectParts[0]) + "%3A" + encodeURIComponent(aspectParts[1]) + "%22&scope=repo"
            });
         }
      },

      /**
       * Start the delete process for a type
       * 
       * @instance
       * @param {object} payload Request payload
       */
      deleteTypeAction: function alfresco_cmm_services_CMMService__deleteTypeAction(payload) {
         
         var name = lang.getObject("name", false, payload);
         if (name != null)
         {
            var responseTopic = this.generateUuid();
            this._deleteHandle = this.alfSubscribe(responseTopic, lang.hitch(this, this.deleteType));

            this.alfPublish("ALF_CREATE_DIALOG_REQUEST", {
               dialogId: "CMM_DELETE_TYPE_DIALOG",
               dialogTitle: this.message("cmm.type.delete-title"),
               textContent: this.message("cmm.type.delete-message", {"0": name}),
               widgetsButtons: [
                  {
                     name: "alfresco/buttons/AlfButton",
                     config: {
                        label: this.message("cmm.model.delete-button.confirm-label"),
                        publishTopic: this.pubSubScope + responseTopic,
                        publishPayload: payload
                     }
                  },
                  {
                     name: "alfresco/buttons/AlfButton",
                     config: {
                        label: this.message("cmm.model.delete-button.cancel-label"),
                        publishTopic: "close"
                     }
                  }
               ]
            });
         }
         else
         {
            this.alfLog("warn", "A request was made to delete a type but no 'name' attribute was provided", payload, this);
         }
      },

      /**
       * Delete a type
       * 
       * @instance
       * @param {object} payload Request payload
       */
      deleteType: function alfresco_cmm_services_CMMService__deleteType(payload) {
         var typeName = lang.getObject("name", false, payload),
             model_name = lang.getObject("modelName", false, payload);
         if (typeName != null && model_name != null)
         {
            this.serviceXhr({
               url: this.serviceUrl.modelServiceUri,
               method: "DELETE",
               data: {
                  modelName: model_name,
                  operation: "deleteType",
                  arguments: {
                     name: model_name,
                     typeName: typeName
                  }
               },
               handleAs: this.errorResponse.handler,
               alfTopic: payload.alfResponseTopic || payload.responseTopic,
               successCallback: function(res) {
                  var subscriptionHandle = lang.getObject("requestConfig.subscriptionHandle", false, payload);
                  if (subscriptionHandle != null)
                  {
                     this.alfUnsubscribe(subscriptionHandle);
                  }
                  this._reloadTypesList();
               },
               failureCallback: function(res) {
                  console.log("FAILURE: " + res);
                  this.alfPublish(CMMConstants.NOTIFICATION_TOPIC, {
                     message: this._cleanError(lang.getObject(this.errorResponse.msgKey, false, res)) || this.message("cmm.message.unknown-error")
                  });
               },
               callbackScope: this
            });
         }
         else
         {
            this.alfLog("warn", "A request was made to delete a type but no 'name' attribute was provided", payload, this);
         }
      },

      /**
       * Retrieve the list of parent property groups
       * 
       * @instance
       */
      getParentPropertyGroups: function alfresco_cmm_services_CMMService__getParentPropertyGroups(payload) {

         this.serviceXhr({
            url: this.serviceUrl.getParentPropertyGroups.uri,
            method: this.serviceUrl.getParentPropertyGroups.method,
            successCallback: function(aspects) {

               if (aspects && aspects.entities)
               {
                  var resAspects = [];
                  for (var i=0; i<aspects.entities.length; i++)
                  {
                     var name = aspects.entities[i].name;
                     // filter using namespace blacklist
                     if (!this.aspectsBlackList[name.split(':')[0]])
                     {
                        var title = aspects.entities[i].title;
                        resAspects.push({
                           value: name,
                           label: name + (title ? " ("+title+")" : "") 
                        });
                     }
                  }
                  
                  resAspects.sort(function (a,b) {
                     return (a.label < b.label) ? -1 : 1;
                  });
                  
                  resAspects.unshift({
                     value: " ",
                     label: this.message("cmm.message.none")
                  });
                  
                  // call original success topic as the list widget is expecting this data
                  var responseTopic = payload.alfResponseTopic || payload.responseTopic;
                  if (responseTopic != null)
                  {
                     this.alfPublish(responseTopic, {
                        options: resAspects
                     });
                  }
               }
            }
         });
      },
      
      /**
       * Create a new Property Group based on the given form payload
       * 
       * @instance
       * @param {object} payload Form payload
       */
      createPropertyGroup: function alfresco_cmm_services_CMMService__createPropertyGroup(payload) {
         
         // see createMenu definition for how model_name is added to the form payload via the AlfDynamicPayloadButton config
         var model_name = lang.getObject("model_name", false, payload);
         if (model_name != null)
         {
            // apply payload to the JSON structure for our PUT create request
            var propertyGroup = {
               name: payload.name,
               description: payload.description,
               title: payload.title,
               parentName: payload.parent.trim() // Trimmed to cope with Aikau Select fault
            };
            this.serviceXhr({
               url: this.serviceUrl.modelServiceUri,
               method: "POST",
               data: {
                  modelName: model_name,
                  operation: "createPropertyGroup",
                  data: propertyGroup,
                  arguments: {
                     name: model_name
                  }
               },
               handleAs: this.errorResponse.handler,
               successCallback: function(res) {
                  this.alfPublish(CMMConstants.CREATE_PROPERTYGROUP + "_SUCCESS");
                  this._reloadPropertyGroupsList();
               },
               failureCallback: function(res) {
                  console.log("FAILURE: " + res);
                  this.alfPublish(CMMConstants.NOTIFICATION_TOPIC, {
                     message: this._cleanError(lang.getObject(this.errorResponse.msgKey, false, res)) || this.message("cmm.message.unknown-error")
                  });
               },
               callbackScope: this
            });
         }
         else
         {
            this.alfLog("warn", "A request was made to create a property group but no 'model_name' attribute was provided", payload, this);
         }
      },
      
      /**
       * Edit a property group based on the given form payload
       * 
       * @instance
       * @param {object} payload The details of the type to put
       */
      editPropertyGroup: function alfresco_cmm_services_CMMService__editPropertyGroup(payload) {

         var model_name = lang.getObject("model_name", false, payload),
             aspect = lang.getObject("name", false, payload);
         if (model_name != null && aspect != null)
         {
            // apply payload to the JSON structure for our PUT create request
            var propertyGroup = {
               name: payload.name,
               description: payload.description,
               title: payload.title,
               parentName: payload.parent.trim() // Trimmed to cope with Aikau Select fault
            };
            this.serviceXhr({
               url: this.serviceUrl.modelServiceUri,
               method: "PUT",
               data: {
                  modelName: model_name,
                  operation: "editPropertyGroup",
                  arguments: {
                     name: model_name,
                     aspectName: aspect
                  },
                  data: propertyGroup
               },
               handleAs: this.errorResponse.handler,
               successCallback: function(res) {
                  this.alfPublish(CMMConstants.EDIT_PROPERTYGROUP + "_SUCCESS");
                  this._reloadPropertyGroupsList();
               },
               failureCallback: function(res) {
                  console.log("FAILURE: " + res);
                  this.alfPublish(CMMConstants.NOTIFICATION_TOPIC, {
                     message: this._cleanError(lang.getObject(this.errorResponse.msgKey, false, res)) || this.message("cmm.message.unknown-error")
                  });
               },
               callbackScope: this
            });
         }
         else
         {
            this.alfLog("warn", "A request was made to edit a property group but no 'model_name' or 'name' attribute was provided", payload, this);
         }
      },
      
      /**
       * Start the delete process for a property group
       * 
       * @instance
       * @param {object} payload Request payload
       */
      deletePropertyGroupAction: function alfresco_cmm_services_CMMService__deletePropertyGroupAction(payload) {
         
         var name = lang.getObject("name", false, payload);
         if (name != null)
         {
            var responseTopic = this.generateUuid();
            this._deleteHandle = this.alfSubscribe(responseTopic, lang.hitch(this, this.deletePropertyGroup));

            this.alfPublish("ALF_CREATE_DIALOG_REQUEST", {
               dialogId: "CMM_DELETE_PROPERTYGROUP_DIALOG",
               dialogTitle: this.message("cmm.propertygroup.delete-title"),
               textContent: this.message("cmm.propertygroup.delete-message", {"0": name}),
               widgetsButtons: [
                  {
                     name: "alfresco/buttons/AlfButton",
                     config: {
                        label: this.message("cmm.model.delete-button.confirm-label"),
                        publishTopic: this.pubSubScope + responseTopic,
                        publishPayload: payload
                     }
                  },
                  {
                     name: "alfresco/buttons/AlfButton",
                     config: {
                        label: this.message("cmm.model.delete-button.cancel-label"),
                        publishTopic: "close"
                     }
                  }
               ]
            });
         }
         else
         {
            this.alfLog("warn", "A request was made to delete a property group but no 'name' attribute was provided", payload, this);
         }
      },

      /**
       * Delete a property group
       * 
       * @instance
       * @param {object} payload Request payload
       */
      deletePropertyGroup: function alfresco_cmm_services_CMMService__deletePropertyGroup(payload) {

         var aspectName = lang.getObject("name", false, payload),
             model_name = lang.getObject("modelName", false, payload);
         if (aspectName != null && model_name != null)
         {
            this.serviceXhr({
               url: this.serviceUrl.modelServiceUri,
               method: "DELETE",
               data: {
                  modelName: model_name,
                  operation: "deletePropertyGroup",
                  arguments: {
                     name: model_name,
                     aspectName: aspectName
                  }
               },
               handleAs: this.errorResponse.handler,
               alfTopic: payload.alfResponseTopic || payload.responseTopic,
               successCallback: function(res) {
                  var subscriptionHandle = lang.getObject("requestConfig.subscriptionHandle", false, payload);
                  if (subscriptionHandle != null)
                  {
                     this.alfUnsubscribe(subscriptionHandle);
                  }
                  this._reloadPropertyGroupsList();
               },
               failureCallback: function(res) {
                  console.log("FAILURE: " + res);
                  this.alfPublish(CMMConstants.NOTIFICATION_TOPIC, {
                     message: this._cleanError(lang.getObject(this.errorResponse.msgKey, false, res)) || this.message("cmm.message.unknown-error")
                  });
               },
               callbackScope: this
            });
         }
         else
         {
            this.alfLog("warn", "A request was made to delete a property group but no 'name' attribute was provided", payload, this);
         }
      },
      
      /**
       * Retrieve the list of properties for an entity
       * 
       * @instance
       * @param {object} payload The details of the request
       */
      getProperties: function alfresco_cmm_services_CMMService__getProperties(payload) {
         
         // this method excepts either a type or a propertygroup name in the payload
         var name = lang.getObject("name", false, payload),
             type = lang.getObject("type", false, payload),
             propertygroup = lang.getObject("propertygroup", false, payload),
             responseTopic = payload.alfResponseTopic || payload.responseTopic;

         if (name != null && (type != null || propertygroup != null))
         {
            // Get the model
            this.serviceXhr({
               url: lang.replace(this.serviceUrl.getModel.uri, {name: encodeURIComponent(name)}),
               method: this.serviceUrl.getModel.method,
               callbackScope: this,
               successCallback: function(res) {
                  
                  // Get the status of the model
                  var status = res.entry.status,
                      statusLabel = this.message(status === 'ACTIVE' ? "cmm.label.status.active" : "cmm.label.status.draft");

                  // Update the status header
                  this.alfPublish(CMMConstants.PROPERTIES_LIST_SCOPE + CMMConstants.UPDATE_TPG_HEADING_STATUS, {
                     status: status,
                     label: statusLabel
                  });

                  this.serviceXhr({
                     url: lang.replace(this.serviceUrl.getProperties.uri, {
                        name: encodeURIComponent(name),
                        entityClass: encodeURIComponent(type != null ? "types" : "aspects"),
                        entityName: encodeURIComponent(type || propertygroup)
                     }),
                     method: this.serviceUrl.getProperties.method,
                     successCallback: function(res) {
                        // augment response to create UI manadatory strings
                        var properties = res.entry.properties;
                        properties.sort(function (a, b) {
                           return (a.name < b.name) ? -1 : 1;
                        });
                        for (var i=0; i<properties.length; i++)
                        {
                           var property = properties[i];
                           property.modelName = name,
                           property.status = status;
                           property.type_name = type;
                           property.propertygroup_name = propertygroup;
                           
                           // Handle mandatory display and edit
                           if (property.mandatory)
                           {
                              property.mandatoryDsp = this.message("cmm.property.mandatory.mandatory-not-enforced");
                              property.mandatoryEdit = "mandatory";
                           }
                           else
                           {
                              property.mandatoryDsp = this.message("cmm.property.mandatory.optional");
                              property.mandatoryEdit = "optional";
                           }
                           
                           // Deconstruct constraint data
                           if(property.constraints && property.constraints instanceof Array)
                           {
                              var constraint = property.constraints[0];
                              
                              // Work out the type - CLASS type is a bit special
                              property.constraint = constraint.type;
                              if(constraint.name.substring(0,5) === "CLASS")
                              {
                                 property.constraint = "CLASS";
                                 property.constraintClass = constraint.type;
                              }

                              // Find all the properties
                              if(constraint.parameters && constraint.parameters instanceof Array)
                              {
                                 for(var j=0; j<constraint.parameters.length; j++)
                                 {
                                    var param = constraint.parameters[j];
                                    switch (param.name) {
                                       case "expression":
                                          property.constraintExpression = param.simpleValue;
                                          break;
                                       case "minLength":
                                          property.constraintMinLength = param.simpleValue;
                                          break;
                                       case "maxLength":
                                          property.constraintMaxLength = param.simpleValue;
                                          break;
                                       case "minValue":
                                          property.constraintMinValue = Number(param.simpleValue).noExponents(); // Sci notation and decimal discarded
                                          break;
                                       case "maxValue":
                                          property.constraintMaxValue = Number(param.simpleValue).noExponents(); // Sci notation and decimal discarded
                                          break;
                                       case "allowedValues":
                                          property.constraintAllowedValues = (param.listValue).join('\n');
                                          break;
                                       case "sorted":
                                          property.constraintSorted = (param.simpleValue == 'true');
                                          break;
                                    }
                                 }
                              }
                           }
                           else
                           {
                              property.constraint = "NONE";
                           }

                           // Parse indexing data
                           var indexingData = this._parsePropertyIndexing(property);
                           property.indexing_txt = indexingData.idxtxt;
                           property.indexing_nontxt = indexingData.idxnontxt;
                           property.indexing_boolean = indexingData.idxboolean;

                        }
                        // call original success topic as the list widget is expecting this data
                        if (responseTopic != null)
                        {
                           this.alfPublish(responseTopic + "_SUCCESS", res);
                        }
                     },
                     callbackScope: this
                  });
               }
            });
         }
         else
         {
            this.alfLog("warn", "A request was made to get properties but no model 'name' or 'type' or 'propertygroup' attributes were provided", payload, this);
            this.alfPublish(responseTopic + "_FAILURE", {}, payload);
         }
      },

      /**
       * Retrieve the list of editor properties for an entity
       * 
       * @instance
       * @param {object} payload The details of the request
       */
      getEditorProperties: function alfresco_cmm_services_CMMService__getEditorProperties(payload) {
         
         // this method excepts either a type or a propertygroup name in the payload
         var name = lang.getObject("name", false, payload),
             type = lang.getObject("type", false, payload),
             propertygroup = lang.getObject("propertygroup", false, payload),
             responseTopic = payload.alfResponseTopic || payload.responseTopic;

         if (name != null && (type != null || propertygroup != null))
         {
            // Get the model
            this.serviceXhr({
               url: lang.replace(this.serviceUrl.getModel.uri, {name: encodeURIComponent(name)}),
               method: this.serviceUrl.getModel.method,
               callbackScope: this,
               successCallback: function(res) {
                  
                  // Get the status of the model
                  var status = res.entry.status,
                      statusLabel = this.message(status === 'ACTIVE' ? "cmm.label.status.active" : "cmm.label.status.draft");

                  // Update the status header
                  this.alfPublish(CMMConstants.UPDATE_TPG_HEADING_STATUS, {
                     status: status,
                     label: statusLabel
                  });
            
                  this.serviceXhr({
                     url: lang.replace(this.serviceUrl.getEditorProperties.uri, {
                        name: encodeURIComponent(name),
                        entityClass: encodeURIComponent(type != null ? "types" : "aspects"),
                        entityName: encodeURIComponent(type || propertygroup)
                     }),
                     method: this.serviceUrl.getEditorProperties.method,
                     successCallback: function(res) {
                        var properties = lang.getObject("entry.properties", false, res),
                            propertyItems = {items: []};
                        
                        // add 'standard' properties if we are working with a Type
                        if (type)
                        {
                           this.standardProperties.forEach(lang.hitch(this, function(p) {
                              var dtype = p.dataType.replace("d:", "");
                              var item = {
                                 type: [ "property" ],
                                 label: this.message(p.label) + ' ['+p.id+']',
                                 iconClass: "editor-" + dtype,
                                 value: {
                                    pseudonym: "cmm/editor/property/" + dtype,
                                    id: p.id,
                                    elementconfig: {
                                       infoname: p.id,
                                       infolabel: this.message(p.label),
                                       infotype: p.dataType
                                    }
                                 }
                              };
                              if (p.config)
                              {
                                 if (p.config["for-mode"]) item.value.elementconfig["for-mode"] = p.config["for-mode"];
                                 if (p.config["force"]) item.value.elementconfig["force"] = p.config["force"];
                                 if (p.config["controlType"]) item.value.elementconfig["controltype"] = p.config["controlType"];
                              }
                              propertyItems.items.push(item);
                           }));
                        }
                        
                        // Generate the property items payload - the values here will eventually
                        // be used when persisting the form definition - so care must be taken when
                        // modifying the value names as they may be used by the web-tier service internally.
                        array.forEach(properties, lang.hitch(this, function(item, i) {
                           if (!this.blackListProperties[item.prefixedName])
                           {
                              var dtype = item.dataType.replace("d:", "");
                              propertyItems.items.push({
                                 type: [ "property" ],
                                 label: item.title ? (item.title + ' ['+item.name+']') : item.name,
                                 iconClass: "editor-" + dtype,
                                 value: {
                                    pseudonym: "cmm/editor/property/" + dtype,
                                    id: item.prefixedName,
                                    elementconfig: {
                                       infoname: item.name,
                                       infolabel: item.title ? item.title : item.name,
                                       infotype: item.dataType
                                    }
                                 }
                              });
                           }
                        }));
                        
                        // call original success topic as the list widget is expecting this data
                        if (responseTopic != null)
                        {
                           this.alfPublish(responseTopic + "_SUCCESS", propertyItems);
                        }
                     }
                  });
               }
            });
         }
         else
         {
            this.alfLog("warn", "A request was made to get editor properties but no model 'name' or 'type' or 'propertygroup' attributes were provided", payload, this);
            this.alfPublish(responseTopic + "_FAILURE", {}, payload);
         }
      },

      /**
       * Create a new property based on the given form payload - property
       * 
       * @instance
       * @param {object} payload Form payload
       */
      createProperty: function alfresco_cmm_services_CMMService__createProperty(payload) {
         
         var model_name = lang.getObject("model_name", false, payload),
             type = lang.getObject("type_name", false, payload),
             propertygroup = lang.getObject("propertygroup_name", false, payload);
         
         if (model_name != null && (type != null || propertygroup != null))
         {
            // apply payload to the JSON structure for our PUT create request
            var property = {
               name: type || propertygroup,
               properties: [{
                  name: payload.name,
                  title: payload.title,
                  description: payload.description,
                  multiValued: payload.multiple,
                  mandatory: payload.mandatory !== "optional",
                  mandatoryEnforced: false,
                  defaultValue: payload.defaultValue,
                  dataType: payload.datatype
               }]
            };
            
            // Are we adding a constraint?
            if (constraintPayload = this._processPropertyConstraint(payload))
            {
               property.properties[0].constraints = [constraintPayload];
            }

            // Are we indexing?
            if (indexingPayload = this._processPropertyIndexing(payload))
            {
               property.properties[0].indexed = indexingPayload.indexed;
               property.properties[0].facetable = indexingPayload.facetable;
               property.properties[0].indexTokenisationMode = indexingPayload.indexTokenisationMode;
            }

            this.serviceXhr({
               url: this.serviceUrl.modelServiceUri,
               method: "PUT",
               data: {
                  modelName: model_name,
                  operation: "createProperty",
                  data: property,
                  arguments: {
                     name: model_name,
                     entityClass: type != null ? "types" : "aspects",
                     entityName: type || propertygroup
                  }
               },
               handleAs: this.errorResponse.handler,
               successCallback: function(res) {
                  this.alfPublish(CMMConstants.CREATE_PROPERTY + "_SUCCESS");
                  this._reloadPropertiesList();
                  this._reloadFormEditorProperties();
               },
               failureCallback: function(res) {
                  console.log("FAILURE: " + res);
                  this.alfPublish(CMMConstants.NOTIFICATION_TOPIC, {
                     message: this._cleanError(lang.getObject(this.errorResponse.msgKey, false, res)) || this.message("cmm.message.unknown-error")
                  });
               },
               callbackScope: this
            });
         }
         else
         {
            this.alfLog("warn", "A request was made to create a property but no model 'name' or 'type' or 'propertygroup' attributes were provided", payload, this);
         }
      },

      /**
       * Edit a property based on the given form payload - property
       * 
       * @instance
       * @param {object} payload Form payload
       */
      editProperty: function alfresco_cmm_services_CMMService__editProperty(payload) {
         
         var model_name = lang.getObject("model_name", false, payload),
             type = lang.getObject("type_name", false, payload),
             propertygroup = lang.getObject("propertygroup_name", false, payload),
             propertyName = lang.getObject("name", false, payload);
         
         if (model_name != null && (type != null || propertygroup != null) && propertyName != null)
         {
            // apply payload to the JSON structure for our PUT edit request
            var property = {
               name: type || propertygroup,
               properties: [{
                  name: payload.name,
                  title: payload.title,
                  description: payload.description,
                  multiValued: payload.multiple,
                  mandatory: payload.mandatory !== "optional",
                  mandatoryEnforced: false,
                  defaultValue: payload.defaultValue,
                  dataType: payload.datatype
               }]
            };
            
            // Are we adding a constraint?
            if (constraintPayload = this._processPropertyConstraint(payload))
            {
               property.properties[0].constraints = [constraintPayload];
            }

            // Are we indexing?
            if (indexingPayload = this._processPropertyIndexing(payload))
            {
               property.properties[0].indexed = indexingPayload.indexed;
               property.properties[0].facetable = indexingPayload.facetable;
               property.properties[0].indexTokenisationMode = indexingPayload.indexTokenisationMode;
            }

            this.serviceXhr({
               url: this.serviceUrl.modelServiceUri,
               method: "PUT",
               data: {
                  modelName: model_name,
                  operation: "editProperty",
                  data: property,
                  arguments: {
                     name: model_name,
                     entityClass: type != null ? "types" : "aspects",
                     entityName: type || propertygroup,
                     propertyName: propertyName
                  }
               },
               handleAs: this.errorResponse.handler,
               successCallback: function(res) {
                  this.alfPublish(CMMConstants.EDIT_PROPERTY + "_SUCCESS");
                  this._reloadPropertiesList();
               },
               failureCallback: function(res) {
                  console.log("FAILURE: " + res);
                  this.alfPublish(CMMConstants.NOTIFICATION_TOPIC, {
                     message: this._cleanError(lang.getObject(this.errorResponse.msgKey, false, res)) || this.message("cmm.message.unknown-error")
                  });
               },
               callbackScope: this
            });
         }
         else
         {
            this.alfLog("warn", "A request was made to edit a property but no model 'name' or 'type' or 'propertygroup' or property 'name' attributes were provided", payload, this);
         }
      },

      /**
       * Start the delete process for a property
       * 
       * @instance
       * @param {object} payload Request payload
       */
      deletePropertyAction: function alfresco_cmm_services_CMMService__deletePropertyAction(payload) {

         var propertyName = lang.getObject("name", false, payload);
         if (propertyName != null)
         {
            var responseTopic = this.generateUuid();
            this._deleteHandle = this.alfSubscribe(responseTopic, lang.hitch(this, this.deleteProperty));

            this.alfPublish("ALF_CREATE_DIALOG_REQUEST", {
               dialogId: "CMM_DELETE_PROPERTY_DIALOG",
               dialogTitle: this.message("cmm.property.delete-title"),
               textContent: this.message("cmm.property.delete-message", {"0": propertyName}),
               widgetsButtons: [
                  {
                     name: "alfresco/buttons/AlfButton",
                     config: {
                        label: this.message("cmm.model.delete-button.confirm-label"),
                        publishTopic: this.pubSubScope + responseTopic,
                        publishPayload: payload
                     }
                  },
                  {
                     name: "alfresco/buttons/AlfButton",
                     config: {
                        label: this.message("cmm.model.delete-button.cancel-label"),
                        publishTopic: "close"
                     }
                  }
               ]
            });
         }
         else
         {
            this.alfLog("warn", "A request was made to delete a property but no 'name' attribute was provided", payload, this);
         }
      },

      /**
       * Delete a property
       * 
       * @instance
       * @param {object} payload Request payload
       */
      deleteProperty: function alfresco_cmm_services_CMMService__deleteProperty(payload) {
         
         var propertyName = lang.getObject("name", false, payload),
             model_name = lang.getObject("modelName", false, payload),
             type = lang.getObject("type_name", false, payload),
             propertygroup = lang.getObject("propertygroup_name", false, payload);
         
         if (propertyName != null && model_name != null && (type != null || propertygroup != null))
         {
            // apply payload to the JSON structure for our PUT create request
            var property = {
               name: type || propertygroup
            };

            this.serviceXhr({
               url: this.serviceUrl.modelServiceUri,
               method: "PUT", // yes this is a PUT and yes data is duplicated in the body - Public API limitations...
               data: {
                  modelName: model_name,
                  operation: "deleteProperty",
                  data: property,
                  arguments: {
                     name: model_name,
                     entityClass: type != null ? "types" : "aspects",
                     entityName: type || propertygroup,
                     propertyName: propertyName
                  }
               },
               handleAs: this.errorResponse.handler,
               alfTopic: payload.alfResponseTopic || payload.responseTopic,
               successCallback: function(res) {
                  var subscriptionHandle = lang.getObject("requestConfig.subscriptionHandle", false, payload);
                  if (subscriptionHandle != null)
                  {
                     this.alfUnsubscribe(subscriptionHandle);
                  }
                  this._reloadPropertiesList();
               },
               failureCallback: function(res) {
                  console.log("FAILURE: " + res);
                  this.alfPublish(CMMConstants.NOTIFICATION_TOPIC, {
                     message: this._cleanError(lang.getObject(this.errorResponse.msgKey, false, res)) || this.message("cmm.message.unknown-error")
                  });
               },
               callbackScope: this
            });
         }
         else
         {
            this.alfLog("warn", "A request was made to delete a property but a 'name', 'model_name', 'type_name' or 'propertygroup_name' attribute was missing", payload, this);
         }
      },
      
      /**
       * Hook called based on a changing value of the contents of the Form Editor.
       * Currently used to toggle the display of the Help overlay containers.
       * 
       * @instance
       * @param {object} payload Request payload
       */
      canvasValueChanged: function alfresco_cmm_services_CMMService__canvasValueChanged(payload) {
         
         if (payload.value && payload.value.length !== 0)
         {
            array.forEach(query(".speech-bubble", "FORM_EDITOR_CONSOLE"), function(node) {
               domClass.add(node, "hidden");
            }, this);
         }
         else
         {
            array.forEach(query(".speech-bubble", "FORM_EDITOR_CONSOLE"), function(node) {
               domClass.remove(node, "hidden");
            }, this);
         }
      },
      
      /**
       * Save the contents of the Form Editor
       * 
       * @instance
       * @param {object} payload Request payload
       */
      saveFormAction: function alfresco_cmm_services_CMMService__saveFormAction(payload) {
         
         // these values all come from useHash support on the outer Form used as the Editor Canvas
         // in the original widget definition see cmm-editor.lib.js - editor.form
         var modelName = lang.getObject("model", false, payload),
             type = lang.getObject("type", false, payload),
             propertygroup = lang.getObject("propertygroup", false, payload);
         
         // the editor form contents
         var formData = lang.getObject("formData", false, payload);
         if (formData)
         {
            if (!formData || formData.length === 0)
            {
               // clear the form - remove it completely
               var form = "";
               var formOperation = "Delete";
            }
            else
            {
               var form = JSON.stringify(formData);
               var formOperation = "Update";
            }
            this.serviceXhr({
               url: this.serviceUrl.modelServiceUri,
               method: "POST",
               data: {
                  modelName: modelName,
                  entity: type || propertygroup,
                  form: form,
                  formOperation: formOperation
               },
               handleAs: this.errorResponse.handler,
               alfTopic: payload.alfResponseTopic || payload.responseTopic,
               successCallback: function(res) {
                  this.alfPublish(CMMConstants.NOTIFICATION_TOPIC, {
                     message: this.message("cmm.form-editor.message.saved.success")
                  });
                  this._reloadPropertiesList();
               },
               failureCallback: function(res) {
                  console.log("FAILURE: " + res);
                  this.alfPublish(CMMConstants.NOTIFICATION_TOPIC, {
                     message: this._cleanError(lang.getObject(this.errorResponse.msgKey, false, res)) || this.message("cmm.message.unknown-error")
                  });
               },
               callbackScope: this
            });
         }
      },

      /**
       * Gets the persisted value of the Form Editor
       * 
       * @instance
       * @param {object} payload Request payload
       */
      getEditorValue: function alfresco_cmm_services_CMMService__getEditorValue(payload) {

         // this method excepts either a type or a propertygroup name in the payload
         var model = lang.getObject("model", false, payload),
             type = lang.getObject("type", false, payload),
             propertygroup = lang.getObject("propertygroup", false, payload);
         if (model != null && (type != null || propertygroup != null))
         {
            // Get the form data
            this.serviceXhr({
               url: lang.replace(this.serviceUrl.getEditorData.uri, {
                  name: encodeURIComponent(model),
                  entityName: encodeURIComponent(type || propertygroup)
               }),
               method: this.serviceUrl.getEditorData.method,
               successCallback: function(res) {

                  var responsePayload = {
                     formData: res.form
                  };
                  
                  // call original success topic as the list widget is expecting this data
                  var responseTopic = payload.alfResponseTopic || payload.responseTopic;
                  if (responseTopic != null)
                  {
                     this.alfPublish(responseTopic + "_SUCCESS", responsePayload);
                  }
               }
            });
         }
         else
         {
            this.alfLog("warn", "A request was made to get editor form data but no model 'name' or 'type' or 'propertygroup' attributes were provided", payload, this);
         }
      },

      /**
       * Process a property constraint payload
       * 
       * @instance
       * @param {object} payload Request payload
       */
      _processPropertyConstraint: function alfresco_cmm_services_CMMService___processPropertyConstraint (payload) {
         
         var constraint = lang.getObject("constraint", false, payload),
             constraintPayload;
         
         // Create a REGEX constraint
         if(constraint === "REGEX" 
            && (constraintExpression = lang.getObject("constraintExpression", false, payload)) != null)
         {
            constraintPayload = {
               name: "REGEX_" + uuid.generateRandomUuid(),
               type: "REGEX",
               parameters: [
                  {
                     name: "expression",
                     simpleValue: constraintExpression
                  },
                  {
                     name: "requiresMatch",
                     simpleValue: true
                  }
               ]
            };
         }
         
         // Create a LENGTH constraint
         else if(constraint === "LENGTH" 
            && (constraintMinLength = lang.getObject("constraintMinLength", false, payload)) != null 
            && (constraintMaxLength = lang.getObject("constraintMaxLength", false, payload)) != null)
         {
            constraintPayload = {
               name: "LENGTH_" + uuid.generateRandomUuid(),
               type: "LENGTH",
               parameters: [
                  {
                     name: "minLength",
                     simpleValue: constraintMinLength
                  },
                  {
                     name: "maxLength",
                     simpleValue: constraintMaxLength
                  }
               ]
            };
         }
         
         // Create a MINMAX constraint
         else if(constraint === "MINMAX" 
            && (constraintMinValue = lang.getObject("constraintMinValue", false, payload)) != null 
            && (constraintMaxValue = lang.getObject("constraintMaxValue", false, payload)) != null)
         {
            constraintPayload = {
               name: "MINMAX_" + uuid.generateRandomUuid(),
               type: "MINMAX",
               parameters: [
                  {
                     name: "minValue",
                     simpleValue: constraintMinValue
                  },
                  {
                     name: "maxValue",
                     simpleValue: constraintMaxValue
                  }
               ]
            };
         }
         
         // Create a LIST constraint
         else if(constraint === "LIST" 
            && (constraintAllowedValues = lang.getObject("constraintAllowedValues", false, payload)) != null 
            && (constraintSorted = lang.getObject("constraintSorted", false, payload)) != null)
         {
            
            // Convert textarea value to array by spliting on carriage returns
            var valueArray = constraintAllowedValues.split(/\r*\n\r*/),
                filValueArray = new Array();

            for(var i=0; i < valueArray.length; i++){
               var item = valueArray[i];
               if(item != undefined && item !== ""){
                  filValueArray.push(item);
               }
            }
            
            constraintPayload = {
               name: "LIST_" + uuid.generateRandomUuid(),
               type: "LIST",
               parameters: [
                  {
                     name: "allowedValues",
                     listValue: filValueArray
                  },
                  {
                     name: "sorted",
                     simpleValue: constraintSorted
                  }
               ]
            };
         }

         // Create a CLASS constraint
         else if(constraint === "CLASS" 
            && (constraintClass = lang.getObject("constraintClass", false, payload)) != null)
         {
            constraintPayload = {
               name: "CLASS_" + uuid.generateRandomUuid(),
               type: constraintClass
            };
         }
         
         return constraintPayload;
      },
      
      /**
       * Process a property indexing payload
       * 
       * @instance
       * @param {object} payload Request payload
       */
      _processPropertyIndexing: function alfresco_cmm_services_CMMService___processPropertyIndexing(payload) {
 
         if (payload.datatype === "d:text" || payload.datatype === "d:mltext" || payload.datatype === "d:content")
         {
            payload.indexing = payload.indexing_txt;
         }
         else if (payload.datatype === "d:boolean")
         {
            payload.indexing = payload.indexing_boolean;
         }
         else
         {
            payload.indexing = payload.indexing_nontxt;
         }

         if(!payload.indexing)
         {
            return null;
         }
         else
         {
            var indexingData = lang.getObject(payload.indexing, false, this.propertyIndexingOptions);
            return {
               indexed: indexingData.indexed,
               facetable: indexingData.facetable,
               indexTokenisationMode: indexingData.indexTokenisationMode
            };
         }
      },

      /**
       * Parse a property for its indexing detail
       * 
       * @instance
       * @param {object} payload Request payload
       */
      _parsePropertyIndexing: function alfresco_cmm_services_CMMService___parsePropertyIndexing(property) {

         var dTyp = "nontxt",
             idxd = property.indexed,
             fct = property.facetable,
             idxTMode = property.indexTokenisationMode,
             idxSelectionTxt = "txt-standard",
             idxSelectionNonTxt = "nontxt-standard",
             idxSelectionBoolean = "boolean-standard";

         if (property.dataType === "d:text" || property.dataType === "d:mltext" || property.dataType === "d:content")
         {
            dTyp = "txt";
         }
         else if (property.dataType === "d:boolean")
         {
            dTyp = "boolean";
         }

         for (var key in this.propertyIndexingOptions) {
            if (this.propertyIndexingOptions.hasOwnProperty(key)) {
               var pio = this.propertyIndexingOptions[key];
               if(pio.type === dTyp && 
                  pio.indexed == idxd && 
                  pio.facetable == fct && 
                  pio.indexTokenisationMode == idxTMode)
               {
                  if(dTyp === "txt")
                  {
                     idxSelectionTxt = key;
                  }
                  else if(dTyp === "boolean")
                  {
                     idxSelectionBoolean = key;
                  }
                  else
                  {
                     idxSelectionNonTxt = key;
                  }
                  break;
               }
            }
         }

         return {
            idxtxt: idxSelectionTxt,
            idxnontxt: idxSelectionNonTxt,
            idxboolean: idxSelectionBoolean
         }
      },

      /**
       * Start the process to clear the editor
       * 
       * @instance
       */
      clearToPaletteAction: function alfresco_cmm_services_CMMService__clearToPaletteAction() {
         
         var responseTopic = this.generateUuid();
         this._clearToPaletteHandle = this.alfSubscribe(responseTopic, lang.hitch(this, this.clearToPalette));

         this.alfPublish("ALF_CREATE_DIALOG_REQUEST", {
            dialogId: "CMM_EDITOR_CLEAR_DIALOG",
            dialogTitle: this.message("cmm.form-editor.clear-title"),
            textContent: this.message("cmm.form-editor.clear-message"),
            widgetsButtons: [
               {
                  name: "alfresco/buttons/AlfButton",
                  config: {
                     label: this.message("cmm.form-editor.clear-button.confirm-label"),
                     publishTopic: this.pubSubScope + responseTopic
                  }
               },
               {
                  name: "alfresco/buttons/AlfButton",
                  config: {
                     label: this.message("cmm.form-editor.clear-button.cancel-label"),
                     publishTopic: "close"
                  }
               }
            ]
         });
      },
      
      /**
       * Clear the editor
       * 
       * @instance
       * @param {object} payload
       */
      clearToPalette: function alfresco_cmm_services_CMMService__clearToPalette(payload) {

         var subscriptionHandle = lang.getObject("requestConfig.subscriptionHandle", false, payload);
         if (subscriptionHandle != null)
         {
            this.alfUnsubscribe(subscriptionHandle);
         }
         
         this.alfPublish(CMMConstants.FORM_EDITOR_CLEAR_TO_PALETTE);
      },

      /**
       * Start the process to populate the editor with the default layout
       * 
       * @instance
       */
      defaultLayoutAction: function alfresco_cmm_services_CMMService__defaultLayoutAction(payload) {
         
         var responseTopic = this.generateUuid();
         this._defaultLayoutDataHandle = this.alfSubscribe(responseTopic, lang.hitch(this, this.defaultLayoutData));

         this.alfPublish("ALF_CREATE_DIALOG_REQUEST", {
            dialogId: "CMM_EDITOR_DEFAULT_LAYOUT_DIALOG",
            dialogTitle: this.message("cmm.form-editor.default-layout-title"),
            textContent: this.message("cmm.form-editor.default-layout-message"),
            widgetsButtons: [
               {
                  name: "alfresco/buttons/AlfButton",
                  config: {
                     label: this.message("cmm.form-editor.default-layout-button.confirm-label"),
                     publishTopic: this.pubSubScope + responseTopic,
                     publishPayload: payload
                  }
               },
               {
                  name: "alfresco/buttons/AlfButton",
                  config: {
                     label: this.message("cmm.form-editor.default-layout-button.cancel-label"),
                     publishTopic: "close"
                  }
               }
            ]
         });
      },
      
      /**
       * Get data for default layout
       * 
       * @instance
       * @param {object} payload The details of the type or propertygroup
       */
      defaultLayoutData: function alfresco_cmm_services_CMMService__defaultLayoutData(payload) {

         var processTopic = this.generateUuid();
         this._defaultLayoutProcessHandle = this.alfSubscribe(processTopic, lang.hitch(this, this.defaultLayoutProcess));
         
         // this method excepts either a type or a propertygroup name in the payload
         var name = lang.getObject("model", false, payload),
             type = lang.getObject("type", false, payload),
             propertygroup = lang.getObject("propertygroup", false, payload);
         if (name != null && (type != null || propertygroup != null))
         {
            this.serviceXhr({
               url: lang.replace(this.serviceUrl.getEditorProperties.uri, {
                  name: encodeURIComponent(name),
                  entityClass: encodeURIComponent(type ? "types" : "aspects"),
                  entityName: encodeURIComponent(type || propertygroup)
               }),
               method: this.serviceUrl.getEditorProperties.method,
               successCallback: function(res) {
                  var properties = lang.getObject("entry.properties", false, res),
                      propertyItems = {items: []};
                  
                  // add 'standard' properties if we are working with a Type
                  if (type)
                  {
                     this.standardProperties.forEach(lang.hitch(this, function(p) {
                        var dtype = p.dataType.replace("d:", "");
                        var item = {
                           pseudonym: "cmm/editor/property/" + dtype,
                           id: p.id,
                           elementconfig: {
                              infoname: p.id,
                              infolabel: this.message(p.label),
                              infotype: p.dataType
                           },
                           label: this.message(p.label) + ' ['+p.id+']',
                           type: ["property"]
                        };
                        if (p.config)
                        {
                           if (p.config["for-mode"]) item.elementconfig["for-mode"] = p.config["for-mode"];
                           if (p.config["force"]) item.elementconfig["force"] = p.config["force"];
                           if (p.config["controlType"]) item.elementconfig["controltype"] = p.config["controlType"];
                        }
                        propertyItems.items.push(item);
                     }));
                  }
                  
                  // Generate the property items payload - the values here will eventually
                  // be used when persisting the form definition - so care must be taken when
                  // modifying the value names as they may be used by the web-tier service internally.
                  array.forEach(properties, lang.hitch(this, function(item, i) {
                     if (!this.blackListProperties[item.prefixedName])
                     {
                        var dtype = item.dataType.replace("d:", "");
                        propertyItems.items.push({
                           pseudonym: "cmm/editor/property/" + dtype,
                           id: item.prefixedName,
                           elementconfig: {
                              infoname: item.name,
                              infolabel: item.title ? item.title : item.name,
                              infotype: item.dataType
                           },
                           label: item.title ? (item.title + ' ['+item.name+']') : item.name,
                           type: ["property"]
                        });
                     }
                  }));

                  this.alfPublish(this.pubSubScope + processTopic, propertyItems);
               }
            });
         }
         else
         {
            this.alfLog("warn", "A request was made to apply a default layout but no model 'name' or 'type' or 'propertygroup' attributes were provided", payload, this);
         }
      },

      /**
       * Process data for default layout
       * 
       * @instance
       * @param {object} payload The details of the type or propertygroup
       */
      defaultLayoutProcess: function alfresco_cmm_services_CMMService__defaultLayoutProcess(payload) {
      
         var subscriptionHandle = lang.getObject("requestConfig.subscriptionHandle", false, payload);
         if (subscriptionHandle != null)
         {
            this.alfUnsubscribe(subscriptionHandle);
         }

         // Wrap all properties in a single column panel container
         var defaultValue = {
            formData: [
               {
                  elementconfig: {
                     label: "",
                     appearance: "title"
                  },
                  pseudonym: "cmm/editor/layout/1cols",
                  label: "cmm.form-editor.palette.one-column",
                  column: payload.items
               }
            ]
         };

         // Clear the canvas
         this.alfPublish(CMMConstants.FORM_EDITOR_CLEAR);
         
         // Publish the default value, as a get editor value _success
         this.alfPublish(CMMConstants.FORM_EDITOR_GET_VALUE + "_SUCCESS", defaultValue);

         // Reload the properties panel, which has the effect of triggering the SYNC process
         this._reloadFormEditorProperties();

      },

      /**
       * Remove unwanted API error codes from error messages
       * 
       * @instance
       * @private
       */
      _cleanError: function alfresco_cmm_services_CMMService___cleanError(msg) {
         return msg.replace(/[0-9]{8}(\s[0-9]{8})?/g, "");
      },

      /**
       * Reload model list
       * 
       * @instance
       * @private
       */
      _reloadModelList: function alfresco_cmm_services_CMMService___reloadModelList() {
         this.alfPublish(CMMConstants.MODELS_LIST_SCOPE + CMMConstants.MODEL_LIST_RELOAD, {});
      },
      
      /**
       * Reload types list
       * 
       * @instance
       * @private
       */
      _reloadTypesList: function alfresco_cmm_services_CMMService___reloadTypesList() {
         this.alfPublish(CMMConstants.TYPES_LIST_SCOPE + CMMConstants.TYPE_LIST_RELOAD, {});
      },
      
      /**
       * Reload property groups list
       * 
       * @instance
       * @private
       */
      _reloadPropertyGroupsList: function alfresco_cmm_services_CMMService___reloadPropertyGroupsList() {
         this.alfPublish(CMMConstants.PROPERTYGROUPS_LIST_SCOPE + CMMConstants.PROPERTYGROUP_LIST_RELOAD, {});
      },
      
      /**
       * Reload properties list
       * 
       * @instance
       * @private
       */
      _reloadPropertiesList: function alfresco_cmm_services_CMMService___reloadPropertiesList() {
         this.alfPublish(CMMConstants.PROPERTIES_LIST_SCOPE + CMMConstants.PROPERTIES_LIST_RELOAD, {});
      },
      
      /**
       * Reload form editor properties
       * 
       * @instance
       * @private
       */
      _reloadFormEditorProperties: function alfresco_cmm_services_CMMService___reloadFormEditorProperties() {
         this.alfPublish(CMMConstants.FORM_EDITOR_SCOPE + CMMConstants.FORM_EDITOR_PROPERTIES_RELOAD, {});
      }

   });
});

/**
 * Add a noExponents function on the Number prototype
 */
Number.prototype.noExponents = function() {
   var data = String(this).split(/[eE]/);
   if(data.length == 1)
   {
      return data[0]; 
   }

   var z = '',
       sign = this < 0 ? '-' : '',
       str = data[0].replace('.', ''),
       mag = Number(data[1]) + 1;

   if(mag < 0)
   {
      z = sign + '0.';
      while(mag++) z += '0';
      return z + str.replace(/^\-/,'');
   }
   mag -= str.length;  
   while(mag--) z += '0';
   return str + z;
}