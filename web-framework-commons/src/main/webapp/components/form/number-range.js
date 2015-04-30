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
 * NumberRange component.
 * 
 * @namespace Alfresco
 * @class Alfresco.NumberRange
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * NumberRange constructor.
    * 
    * @param {String} htmlId The HTML id of the control element
    * @param {String} valueHtmlId The HTML id prefix of the value elements
    * @return {Alfresco.NumberRange} The new NumberRange instance
    * @constructor
    */
   Alfresco.NumberRange = function(htmlId, valueHtmlId)
   {
      Alfresco.NumberRange.superclass.constructor.call(this, "Alfresco.NumberRange", htmlId);
      
      this.valueHtmlId = valueHtmlId;
      
      return this;
   };
   
   YAHOO.extend(Alfresco.NumberRange, Alfresco.component.Base,
   {
      /**
       * Current minimum number value
       * 
       * @property currentMinNumber
       * @type string
       */
      currentMinNumber: "",
      
      /**
       * Current maximum number value
       * 
       * @property currentMaxNumber
       * @type string
       */
      currentMaxNumber: "",
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function NumberRange_onReady()
      {
         // Add listener for input fields to keep the generated range value up-to-date
         Event.addListener(this.id + "-min", "keyup", this._handleFieldChange, this, true);
         Event.addListener(this.id + "-max", "keyup", this._handleFieldChange, this, true);
      },
      
      /**
       * Updates the currently stored range value in the hidden form field.
       * 
       * @method _updateCurrentValue
       * @private
       */
      _updateCurrentValue: function NumberRange__updateCurrentValue()
      {
         Dom.get(this.valueHtmlId).value = this.currentMinNumber + "|" + this.currentMaxNumber;
      },
      
      /**
       * Handles the value being changed in either input field.
       * 
       * @method _handleFieldChange
       * @param event The event that occurred
       * @private
       */
      _handleFieldChange: function NumberRange__handleFieldChange(event)
      {
         var strMinValue = YAHOO.lang.trim(Dom.get(this.id + "-min").value),
             strMaxValue = YAHOO.lang.trim(Dom.get(this.id + "-max").value);
         if (strMinValue.length !== 0)
         {
            var minValue = parseFloat(strMinValue);
            if (!isNaN(minValue))
            {
               Dom.removeClass(this.id + "-min", "invalid");
               this.currentMinNumber = strMinValue;
            }
            else
            {
               Dom.addClass(this.id + "-min", "invalid");
            }
         }
         if (strMaxValue.length !== 0)
         {
            var maxValue = parseFloat(strMaxValue);
            if (!isNaN(maxValue))
            {
               Dom.removeClass(this.id + "-max", "invalid");
               this.currentMaxNumber = strMaxValue;
            }
            else
            {
               Dom.addClass(this.id + "-max", "invalid");
            }
         }
         this._updateCurrentValue();
      }
   });
})();