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
 * This is the CMM listing for Types and Property Groups.
 *
 * @module cmm/lists/CMMTPGList
 * @extends alfresco/lists/AlfHashList
 * @author Richard Smith
 */
define(["dojo/_base/declare",
        "alfresco/lists/AlfHashList",
        "dojo/_base/lang",
        "cmm/CMMConstants"], 
        function(declare, AlfHashList, lang, CMMConstants) {
   
   return declare([AlfHashList], {

      /**
       * @instance
       * @param {object} hashParameters An object containing the current hash parameters
       */
      _updateCoreHashVars: function cmm_lists_CMMTPGList___updateCoreHashVars(hashParameters) {

         lang.mixin(this, hashParameters);
         this.updateLoadDataPayload({
            name: this.model
         });
         
         // Fire off an event to update labels that care about the model name
         this.alfPublish(CMMConstants.UPDATE_MODEL_HEADING, {
            label: this.model
         });
      },

      /**
       * @instance
       * @param {object} payload The details of the payload update
       */
      updateLoadDataPayload: function cmm_lists_CMMTPGList__updateLoadDataPayload(payload) {
         this.inherited(arguments);
         
         var name = lang.getObject("name", false, payload),
             type = lang.getObject("type", false, payload),
             propertygroup = lang.getObject("propertygroup", false, payload);

         this.loadDataPublishPayload = {
            name: name,
            type: type,
            propertygroup: propertygroup
         };
      },

      /**
       * Handles failed calls to get data from the repository.
       *
       * @instance
       * @param {object} response The response object
       * @param {object} originalRequestConfig The configuration that was passed to the the [serviceXhr]{@link module:alfresco/core/CoreXhr#serviceXhr} function
       */
      onDataLoadFailure: function cmm_lists_CMMTPGList__onDataLoadFailure(response, originalRequestConfig) {
         this.alfLog("info", "Data Load Failed", response, originalRequestConfig);
         this.currentData = null;
         this.showDataLoadFailure();
         this.alfPublish(this.documentLoadFailedTopic, {});
         this.alfPublish(this.requestFinishedTopic, {});
      }

   });
});