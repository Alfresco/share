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
 * This is the Dropped Item Value Wrapper for use in the CMM form editor.
 *
 * @module cmm/dnd/DroppedItemValueWrapper
 * @extends cmm/dnd/DroppedItemWrapper
 * @author Richard Smith
 */
define(["dojo/_base/declare",
        "cmm/dnd/DroppedItemWrapper",
        "dojo/_base/lang"], 
        function(declare, DroppedItemWrapper, lang) {

   return declare([DroppedItemWrapper], {

      /**
       * The key for the value property to be displayed.
       *
       * @instance
       * @type {string}
       * @default ""
       */
      displayValueProperty: "",

      /**
       * The maximum length of the value property to be displayed.
       *
       * @instance
       * @type {number}
       * @default 100
       */
      displayValueMaxLength: 100,

      /**
       * @instance
       */
      postCreate: function cmm_dnd_DroppedItemValueWrapper__postCreate() {
         this.inherited(arguments);
         this.updateLabel();
      },

      /**
       * @instance
       */
      updateLabel: function cmm_dnd_DroppedItemValueWrapper__updateLabel() {
         var valueProperty = "";
         if(this.displayValueProperty)
         {
            valueProperty = lang.getObject(this.displayValueProperty, false, this.value);
         }
         
         if(valueProperty)
         {
            this.labelNode.innerHTML = this._truncate(this.encodeHTML(valueProperty));
         }
         else if (this.label)
         {
            this.labelNode.innerHTML = this._truncate(this.encodeHTML(this.message(this.label)));
         }
      },

      /**
       * Function to provide 'ellipses-d' truncation of a string for display purposes
       * 
       * @instance
       * @param {string} string The string to truncate
       * @return {string} The truncated string
       */
      _truncate: function cmm_dnd_DroppedItemValueWrapper___truncate(string) {
         return string.length > this.displayValueMaxLength ? (string.substring(0, this.displayValueMaxLength-1) + '...') : string;
      }

   });
});