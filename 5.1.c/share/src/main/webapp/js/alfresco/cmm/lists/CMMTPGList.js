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
         this.loadDataPublishPayload = payload;
      }

   });
});