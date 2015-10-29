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
 * @module cmm/CMMDisplayController
 * @extends module:alfresco/core/Core
 * @mixes module:alfresco/core/CoreXhr
 * @author Richard Smith
 * @author Kevin Roast
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase",
        "alfresco/core/Core",
        "alfresco/core/CoreXhr",
        "dojo/_base/lang",
        "service/constants/Default",
        "cmm/CMMConstants",
        "alfresco/util/hashUtils",
        "dojo/io-query"],
        function(declare, _WidgetBase, AlfCore, AlfXhr, lang, AlfConstants, CMMConstants, hashUtils, ioQuery) {

   return declare([_WidgetBase, AlfCore, AlfXhr], {

      /**
       * JavaScript interval timer to maintain Session during Form Editor panel visibility
       * @instance
       * @type {Object}
       */
      interval: null,
      
      /**
       * Sets up the subscriptions for the CMMDisplayService
       *
       * @instance
       * @param {array} args The constructor arguments.
       */
      constructor: function alfresco_cmm_services_CMMDisplayController__constructor(args) {

         lang.mixin(this, args);
         this.alfSubscribe(CMMConstants.DISPLAY_MODELS_TOPIC, lang.hitch(this, this.showModels));
         this.alfSubscribe(CMMConstants.DISPLAY_TYPES_AND_PROPERTYGROUPS_TOPIC, lang.hitch(this, this.showTypesAndPropertyGroups));
         this.alfSubscribe(CMMConstants.DISPLAY_PROPERTIES_TOPIC, lang.hitch(this, this.showProperties));
         this.alfSubscribe(CMMConstants.DISPLAY_FORM_EDITOR_TOPIC, lang.hitch(this, this.showFormEditor));
      },
      
      /**
       * @instance
       */
      postCreate: function alfresco_cmm_services_CMMDisplayController__postCreate() {
         this.formEditorReloadOnLoad();
      },

      /**
       * Display the models listing screen
       * 
       * @instance
       * @param {object} payload The details of the request
       */
      showModels: function alfresco_cmm_services_CMMDisplayController__showModelListing() {

         var publishPayload = {};
         publishPayload[CMMConstants.PANE_SELECTION_HASH_VAR] = CMMConstants.PANE_TITLE_MODELS;
         
         this._setDisplay(publishPayload);
         
         // Reload the models list
         this.alfPublish(CMMConstants.MODELS_LIST_SCOPE + CMMConstants.MODEL_LIST_RELOAD, {});
      },
      
      /**
       * Display the types and property groups listing screen
       * 
       * @instance
       * @param {object} payload The details of the request
       */
      showTypesAndPropertyGroups: function alfresco_cmm_services_CMMDisplayController__showTypesAndPropertyGroups(payload) {

         // Select the types / property groups pane and set the model hash
         var publishPayload = {};
         publishPayload[CMMConstants.PANE_SELECTION_HASH_VAR] = CMMConstants.PANE_TITLE_TYPE_AND_PROPERTYGROUPS;
         publishPayload[CMMConstants.MODEL_SELECTION_HASH_VAR] = payload.modelName;

         this._setDisplay(publishPayload);
      },

      /**
       * Display the properties listing screen
       * 
       * @instance
       * @param {object} payload The details of the request
       */
      showProperties: function alfresco_cmm_services_CMMDisplayController__showProperties(payload) {

         // Select the properties pane and set the model and type hashes
         var publishPayload = {};
         publishPayload[CMMConstants.PANE_SELECTION_HASH_VAR] = CMMConstants.PANE_TITLE_PROPERTIES;
         publishPayload[CMMConstants.MODEL_SELECTION_HASH_VAR] = payload.modelName;
         publishPayload[CMMConstants.TYPE_SELECTION_HASH_VAR] = payload.type;
         publishPayload[CMMConstants.PROPERTYGROUP_SELECTION_HASH_VAR] = payload.propertygroup;

         this._setDisplay(publishPayload);
      },

      /**
       * Display the form editor screen
       * 
       * @instance
       * @param {object} payload The details of the request
       */
      showFormEditor: function alfresco_cmm_services_CMMDisplayController__showFormEditor(payload) {

         // Clear the editor form
         this.alfPublish(CMMConstants.FORM_EDITOR_GET_VALUE + "_SUCCESS", {
            model: "",
            type: "",
            propertygroup: ""
         });

         // Clear the editor canvas
         this.alfPublish(CMMConstants.FORM_EDITOR_CLEAR);

         // Select the form editor pane and set the model and type hashes
         var publishPayload = {};
         publishPayload[CMMConstants.PANE_SELECTION_HASH_VAR] = CMMConstants.PANE_TITLE_FORM_EDITOR;
         publishPayload[CMMConstants.MODEL_SELECTION_HASH_VAR] = payload.modelName;
         publishPayload[CMMConstants.TYPE_SELECTION_HASH_VAR] = payload.type;
         publishPayload[CMMConstants.PROPERTYGROUP_SELECTION_HASH_VAR] = payload.propertygroup;

         // Get (set) the editor value
         var publishPayloadValueSet = lang.clone(publishPayload);
         publishPayloadValueSet.responseTopic = CMMConstants.FORM_EDITOR_GET_VALUE;
         this.alfPublish(CMMConstants.FORM_EDITOR_GET_VALUE, publishPayloadValueSet);

         // Set the title on the editor pane
         this.alfPublish(CMMConstants.UPDATE_TPG_HEADING, {
            label: payload.modelName + " - " + (payload.type || payload.propertygroup)
         });

         this._setDisplay(publishPayload);
         
         // for this panel - we keep the user logged in to avoid losing data - once a minute
         this.interval = setInterval(lang.hitch(this, function() {
            this.serviceXhr({
               url: AlfConstants.URL_CONTEXT + "service/modules/authenticated?noCache=" + new Date().getTime(),
               method: "GET"
            });
         }) , 1000*60);
      },

      /**
       * Reload the form editor if required
       * 
       * @instance
       */
      formEditorReloadOnLoad: function alfresco_cmm_services_CMMDisplayController__formEditorReloadOnLoad() {

         var hashString = hashUtils.getHashString();
         if (hashString)
         {
            var currHash = ioQuery.queryToObject(hashString);
            if(currHash.view && currHash.view === CMMConstants.PANE_TITLE_FORM_EDITOR && 
               currHash.model && (currHash.type || currHash.propertygroup))
            {
               this.alfPublishDelayed(CMMConstants.DISPLAY_FORM_EDITOR_TOPIC, {
                  modelName: currHash.model,
                  type: currHash.type,
                  propertygroup: currHash.propertygroup
               }, 1*1000);
            }
            else if (currHash.view && currHash.view === CMMConstants.PANE_TITLE_FORM_EDITOR)
            {
               this.alfPublish(CMMConstants.DISPLAY_MODELS_TOPIC);
            }
         }
      },
      
      /**
       * Set the display
       * 
       * @instance
       * @private
       * @param {object} payload The details of the hash to set
       */
      _setDisplay: function alfresco_cmm_services_CMMDisplayController___setDisplay(payload) {

         if (payload)
         {
            this.alfPublish("ALF_NAVIGATE_TO_PAGE", {
               type: "HASH",
               url: ioQuery.objectToQuery(payload)
            });
         }
         
         if (this.interval) clearInterval(this.interval);
      }

   });
});