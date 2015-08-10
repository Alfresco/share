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
 * This is the title pane service that provides TitlePane forms for editing the properties of 
 * form editor elements in CMM.
 *
 * @module cmm/services/TitlePaneService
 * @extends module:alfresco/core/Core
 * @author Richard Smith
 */
define(["dojo/_base/declare",
        "alfresco/core/Core",
        "dojo/_base/lang",
        "cmm/layout/TitlePane",
        "dojo/dom"],
        function(declare, AlfCore, lang, TitlePane, dom) {

   return declare([AlfCore], {
      
      /**
       * @instance
       * @type {string}
       * @default null
       */
      clearAllTopic: null,

      /**
       * The default configuration for form TitlePanes. This is used as a base when requests are received.
       *
       * @instance
       * @type {string}
       * @default ""
       */
      defaultFormTitlePaneConfig: {
         titlePaneTitle: "",
         titlePaneOpen: true,
         titlePaneToggleable: false,
         titlePaneMoveable: false
      },

      /**
       * Initialises the TitlePane map and subscribes to create and clear topics
       *
       * @instance
       * @listens module:cmm/services/TitlePaneService~event:ALF_CREATE_FORM_TITLE_PANE_REQUEST
       */
      constructor: function cmm_services_TitlePaneService__constructor(args) {
         lang.mixin(this, args);
         this.alfSubscribe("ALF_CREATE_FORM_TITLE_PANE_REQUEST", lang.hitch(this, this.onCreateFormTitlePaneRequest));
         if(this.clearAllTopic != null)
         {
            this.alfSubscribe(this.clearAllTopic, lang.hitch(this, this.cleanUpAllPreviousTitlePanes));
         }
         this.idToTitlePaneMap = {};
      },

      /**
       * Maps the id requested for the TitlePane to the TitlePane created so that it can be destroyed when a
       * request is made to create a TitlePane with the same id. If an id has not been requested then the
       * TitlePane will be mapped to null. Only one TitlePane on requested id can exist at any one time.
       *
       * @instance
       * @param {object} payload The payload passed when requesting to create the TitlePane
       * @param {object} titlePane The TitlePane created
       */
      mapRequestedIdToTitlePane: function cmm_services_TitlePaneService__mapRequestedIdToTitlePane(payload, titlePane) {
         if (payload.titlePaneId)
         {
            // Map the TitlePane id to the TitlePane (so that it can be destroyed if another is requested)...
            this.idToTitlePaneMap[payload.titlePaneId] = titlePane;
         }
         else
         {
            // If no id was provided for the TitlePane we'll store it against null...
            this.idToTitlePaneMap[null] = titlePane;
         }
      },
      
      /**
       * This deletes any previously created TitlePane that was requested with the same id.
       *
       * @instance
       * @param {object} payload The payload for the new TitlePane request.
       */
      cleanUpAnyPreviousTitlePane: function cmm_services_TitlePaneService__cleanUpPreviousTitlePane(payload) {
         if (this.idToTitlePaneMap[payload.titlePaneId])
         {
            // We have a reference to an existing TitlePane, so we'll destroy it
            this.idToTitlePaneMap[payload.titlePaneId].onClose();
            delete this.idToTitlePaneMap[payload.titlePaneId];
         }
         else if (this.idToTitlePaneMap[null])
         {
            this.idToTitlePaneMap[null].onClose();
            delete this.idToTitlePaneMap[null];
         }
      },

      /**
       * This deletes all previously created TitlePanes.
       *
       * @instance
       */
      cleanUpAllPreviousTitlePanes: function cmm_services_TitlePaneService__cleanUpAllPreviousTitlePane() {
         for (var titlePaneId in this.idToTitlePaneMap)
         {
            this.cleanUpAnyPreviousTitlePane({
               titlePaneId: titlePaneId
            });
         }
      },

      /**
       * Handles requests to create the TitlePane containining a [form]{@link module:alfresco/forms/Form}. 
       * It will delete any previously created TitlePane (to ensure
       * no stale data is displayed) and create a new TitlePane containing the form defined.
       * 
       * @instance
       * @param {module:cmm/services/TitlePaneService~event:ALF_CREATE_FORM_CONTENT_PANE_REQUEST} payload The payload published on the request topic.
       */
      onCreateFormTitlePaneRequest: function cmm_services_TitlePaneService__onCreateFormTitlePaneRequest(payload) {

         this.cleanUpAnyPreviousTitlePane(payload);

         if (!payload.widgets)
         {
            this.alfLog("warn", "A request was made to display a TitlePane but no 'widgets' attribute has been defined", payload, this);
         }
         else if (!payload.titlePaneContainer)
         {
            this.alfLog("warn", "A request was made to display a TitlePane but no 'titlePaneContainer' attribute has been defined", payload, this);
         }
         else if (!payload.formSubmissionTopic)
         {
            this.alfLog("warn", "A request was made to display a TitlePane but no 'formSubmissionTopic' attribute has been defined", payload, this);
         }
         else
         {
            try
            {
               // Create a new pubSubScope just for this request (to allow multiple TitlePanes to behave independently)...
               var pubSubScope = this.generateUuid();

               // Take a copy of the default configuration and mixin in the supplied config to override defaults
               var config = lang.clone(this.defaultFormTitlePaneConfig);
               lang.mixin(config, lang.clone(payload));
               config.pubSubScope = pubSubScope;
               config.parentPubSubScope = this.parentPubSubScope;

               // Construct the form widgets
               var formValue = config.formValue ? config.formValue: {};
               var formConfig = this.createFormConfig(config.widgets, formValue, config);
               
               var tp = new TitlePane({
                  id: config.titlePaneId,
                  title: config.titlePaneTitle,
                  open: config.titlePaneOpen,
                  toggleable: config.titlePaneToggleable,
                  moveable: config.titlePaneMoveable,
                  widgetsContent: [formConfig]
               });
               dom.byId(config.titlePaneContainer).appendChild(tp.domNode);
               tp.startup();

               this.mapRequestedIdToTitlePane(payload, tp);
               
               tp.autoPosition(payload.titlePanePositionContainer);
            }
            catch (e)
            {
               this.alfLog("error", "The following error occurred creating a TitlePane for defined configuration", e, this.titlePaneConfig, this);
            }
         }
      },

      /**
       * Creates and returns the [form]{@link module:alfresco/forms/Form} configuration to be added to the TitlePane
       *
       * @instance
       * @param {object} widgets This is the configuration of the fields to be included in the form.
       * @param {object} formValue The initial value to set in the form.
       * @returns {object} The configuration for the form to add to the TitlePane
       */
      createFormConfig: function cmm_services_TitlePaneService__createFormConfig(widgets, formValue, config) {

         var formConfig = {
            name: "alfresco/forms/Form",
            config: {
               widgets: widgets,
               value: formValue,
               waitForPageWidgets: false,
               autoSavePublishTopic: config.formSubmissionTopic,
               autoSavePublishGlobal: true,
               autoSaveOnInvalid: false
            }
         };
         return formConfig;

      }
   });
});