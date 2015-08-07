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
 * This is the CMM button that adds explicit value clearing behaviour for hash based dynamic payloads.
 * For CMM this is used as the stacked-panel based UI uses the URL hash to store multiple values for state.
 * Some of this state is mutually exclusive and values must be cleared (nulled) if another is set.
 * @see cmm/lists/CMMTPGPropertiesList for a similar pattern.
 *
 * @module cmm/buttons/CMMHashPayloadButton
 * @extends alfresco/buttons/AlfDynamicPayloadButton
 * @author Kevin Roast
 */
define(["dojo/_base/declare",
        "alfresco/buttons/AlfDynamicPayloadButton", 
        "dojo/_base/lang",
        "cmm/CMMConstants"], 
        function(declare, AlfButton, lang, CMMConstants) {
   
   return declare([AlfButton], {

      /**
       * Maps the data provided into the payload based on the dataMapping provided.
       *
       * @instance
       * @param {object} dataMapping The mapping to use for the data
       * @param {object} data The data to be mapped
       */
      mapData: function cmm_buttons_CMMHashPayloadButton__mapData(dataMapping, data) {
         for (var key in dataMapping)
         {
            if (dataMapping.hasOwnProperty(key))
            {
               var value = lang.getObject(key, false, data);
               // for null or undefined hash value we want to null the resulting payload value
               lang.setObject(dataMapping[key], value != null ? value : null, this.publishPayload);
            }
         }
      }

   });
});