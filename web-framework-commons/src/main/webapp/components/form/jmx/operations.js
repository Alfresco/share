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
 * Operations form component.
 * 
 * @namespace Alfresco
 * @class Alfresco.Operations
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
    * Operations constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.Operations} The new Operations instance
    * @constructor
    */
   Alfresco.Operations = function(htmlId)
   {
      Alfresco.Operations.superclass.constructor.call(this, "Alfresco.Operations", htmlId, ["button", "container"]);
      
      return this;
   };
   
   YAHOO.extend(Alfresco.Operations, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * The current value, it's a list of comma separated operations in the 
          * format op_name|label
          * 
          * @property currentValue
          * @type string
          */
         currentValue: "",
         
         /**
          * List of operation objects representing the operations for the MBean.
          * 
          * @property operations
          * @type array
          */
         operations: null
      },
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function Operations_onReady()
      {
         // setup the operations array
         this._processOperations();
         
         // generate buttons for each operation
         this._generateOperationButtons();
      },
      
      /**
       * Event handler called when an operation button is clicked.
       *
       * @method onClick
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onClick: function Operations_onClick(e, p_obj)
      {
         // determine what button was pressed by it's id
         var buttonId = p_obj.get("id");
         var operationId = buttonId.substring(this.id.length+1);
         
         // get the hidden field
         var hiddenField = this._getHiddenField();

         // set the hidden field value
         Dom.setAttribute(hiddenField, "value", operationId);
         
         if (Alfresco.logger.isDebugEnabled())
            Alfresco.logger.debug("Set operations hidden field to: " + operationId);
         
         // attempt to submit the form
         Alfresco.util.submitForm(p_obj.getForm());
      },
      
      /**
       * Processes the encoded operations string into.
       * 
       * @method _processOperations
       * @private
       */
      _processOperations: function Operations__processOperations()
      {
         // process the current value and create the list of operations
         this.options.operations = [];
         
         if (Alfresco.logger.isDebugEnabled())
            Alfresco.logger.debug("Processing operations for field '" + this.id + "': " + this.options.currentValue);
         
         // process the operations
         if (this.options.currentValue !== null && this.options.currentValue.length > 0)
         {
            var operationPairs = this.options.currentValue.split(",");
            for (var i = 0, ii = operationPairs.length; i < ii; i++)
            {
               // retrieve the operation info and split
               var operationInfo = operationPairs[i].split("|");
               
               // add the operation as an object
               this.options.operations.push(
               {
                  id: operationInfo[0],
                  label: operationInfo[1]
               });
            }
         }
         
         if (Alfresco.logger.isDebugEnabled())
            Alfresco.logger.debug("Built operations list: " + YAHOO.lang.dump(this.options.operations));
      },
      
      /**
       * Generates a YUI button for each operation.
       * 
       * @method _generateOperationButtons
       * @private
       */
      _generateOperationButtons: function Operations__generateOperationButtons()
      {
         // create a submit button for each operation
         for (var i = 0, ii = this.options.operations.length; i < ii; i++)
         {
            this._generateOperationButton(this.options.operations[i]);
         }
      },
      
      /**
       * Generates a YUI button for the given operation.
       * 
       * @method _generateOperationButton
       * @param operation {object} An object representing the operation
       * @private
       */
      _generateOperationButton: function Operations__generateOperationButton(operation)
      {
         // create a button and add to the DOM
         var button = document.createElement('input');
         button.setAttribute("id", this.id + "-" + operation.id);
         button.setAttribute("value", operation.label);
         button.setAttribute("type", "button");
         Dom.get(this.id + "-buttons").appendChild(button);
         
         // create the YUI button and register the event handler
         var button = Alfresco.util.createYUIButton(this, operation.id, this.onClick);
         
         // register the button as a submitElement with the forms runtime instance
         YAHOO.Bubbling.fire("addSubmitElement", button);
      },
      
      /**
       * Retrieves, creating if necessary, the hidden field used
       * to hold the selected operation.
       * 
       * @method _getHiddenField
       * @return The hidden field element
       * @private
       */
      _getHiddenField: function Operations__getHiddenField()
      {
         // create the hidden field (if necessary)
         var hiddenField = Dom.get(this.id + "-hidden");
         if (hiddenField === null)
         {
            hiddenField = document.createElement('input');
            hiddenField.setAttribute("id", this.id + "-hidden");
            hiddenField.setAttribute("type", "hidden");
            hiddenField.setAttribute("name", "prop_mbean_operations");
            
            Dom.get(this.id).appendChild(hiddenField);
         }
         
         return hiddenField;
      }
   });
})();