/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
 * Percentage Approve component.
 *
 * @namespace Alfresco
 * @class Alfresco.PercentageApprove
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * PercentageApprove constructor.
    *
    * @param {String} htmlId The HTML id of the control element
    * @return {Alfresco.PercentageApprove} The new PercentageApprove instance
    * @constructor
    */
   Alfresco.PercentageApprove = function(htmlId)
   {
      Alfresco.PercentageApprove.superclass.constructor.call(this, "Alfresco.PercentageApprove", htmlId);

      return this;
   };

   YAHOO.extend(Alfresco.PercentageApprove, Alfresco.component.Base,
   {
      options:
      {
         /**
          * Current value
          *
          * @property currentValue
          * @type int
          */
         currentValue: 50,

         /**
          * Max value
          *
          * @property maxValue
          * @type int
          */
         maxValue: 100,

         /**
          * Min value
          *
          * @property minValue
          * @type int
          */
         minValue: 1
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function PercentageApprove_onReady()
      {
         Dom.get(this.id).value = this.options.currentValue;
         this._updateCurrentValue();

         // Add listener for input field to keep the generated value up-to-date
         Event.addListener(this.id, "keyup", this._handleFieldChange, this, true);
      },

      /**
       * Updates the currently stored range value in the hidden form field.
       *
       * @method _updateCurrentValue
       * @private
       */
      _updateCurrentValue: function PercentageApprove__updateCurrentValue()
      {
         Dom.get(this.id + "-value").value = this.options.currentValue;
      },

      /**
       * Handles the value being changed in input field.
       *
       * @method _handleFieldChange
       * @param event The event that occurred
       * @private
       */
      _handleFieldChange: function PercentageApprove__handleFieldChange(event)
      {
         var strValue = YAHOO.lang.trim(Dom.get(this.id).value);
         if (strValue.length > 0)
         {
            var value = Number(strValue);
            if (!isNaN(value) && value >= this.options.minValue && value <= this.options.maxValue)
            {
               Dom.removeClass(this.id, "invalid");
               this.options.currentValue = strValue;
            }
            else
            {
               Dom.addClass(this.id, "invalid");
            }
         }
         else
         {
            Dom.addClass(this.id, "invalid");
         }
         this._updateCurrentValue();
      }
   });
})();