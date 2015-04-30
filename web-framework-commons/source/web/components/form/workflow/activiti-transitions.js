/**
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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
 * Activiti Transitions form component.
 * 
 * @namespace Alfresco
 * @class Alfresco.ActivitiTransitions
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
    * ActivitiTransitions constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.ActivitiTransitions} The new ActivitiTransitions instance
    * @constructor
    */
   Alfresco.ActivitiTransitions = function(htmlId)
   {
      Alfresco.ActivitiTransitions.superclass.constructor.call(this, "Alfresco.ActivitiTransitions", htmlId, ["button", "container"]);
      
      return this;
   };
   
   YAHOO.extend(Alfresco.ActivitiTransitions, Alfresco.component.Base,
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
          * The current value, it's a list of comma separated
          * transitions in the format transition_id|label
          * 
          * @property currentValue
          * @type string
          */
         currentValue: "",
         
         /**
          * The name of the hidden input field to use to submit the
          * selected transition.
          * 
          * @property inputFieldName
          * @type string
          */
         hiddenFieldName: "",
         
         /**
          * List of transition objects representing
          * the transitions for the task.
          * 
          * @property transitions
          * @type array
          */
         transitions: null
      },
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function ActivitiTransitions_onReady()
      {
         // setup the transitions array
         this._processTransitions();
         
         // generate buttons for each transition
         this._generateTransitionButtons();
      },
      
      /**
       * Event handler called when a transition button is clicked.
       *
       * @method onClick
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onClick: function ActivitiTransitions_onClick(e, p_obj)
      {
         //MNT-2196 fix, disable transition button to prevent multiple execution
         p_obj.set("disabled", true);
         // determine what button was pressed by it's id
         var buttonId = p_obj.get("id");
         var transitionId = buttonId.substring(this.id.length+1);
         
         // get the hidden field
         var hiddenField = this._getHiddenField();

         // set the hidden field value
         Dom.setAttribute(hiddenField, "value", transitionId);
         
         if (Alfresco.logger.isDebugEnabled())
            Alfresco.logger.debug("Set transitions hidden field to: " + transitionId);
         
         // generate the hidden transitions field
         this._generateTransitionsHiddenField();
         
         // attempt to submit the form
         Alfresco.util.submitForm(p_obj.getForm());
      },
      
      /**
       * Processes the encoded transitions string into.
       * 
       * @method _processTransitions
       * @private
       */
      _processTransitions: function ActivitiTransitions__processTransitions()
      {
         // process the current value and create the list of transitions
         this.options.transitions = [];
         
         if (Alfresco.logger.isDebugEnabled())
            Alfresco.logger.debug("Processing transitions for field '" + this.id + "': " + this.options.currentValue);
         
         // process the transitions
         if (this.options.currentValue !== null && this.options.currentValue.length > 0)
         {
            var transitionPairs = this.options.currentValue.split("#alf#");
            for (var i = 0, ii = transitionPairs.length; i < ii; i++)
            {
               // retrieve the transition info and split
               var transitionInfo = transitionPairs[i].split("|");
               
               // add the transition as an object
               this.options.transitions.push(
               {
                  id: transitionInfo[0],
                  label: transitionInfo[1]
               });
            }
         }
         
         if (Alfresco.logger.isDebugEnabled())
            Alfresco.logger.debug("Built transitions list: " + YAHOO.lang.dump(this.options.transitions));
      },
      
      /**
       * Generates a YUI button for each transition.
       * 
       * @method _generateTransitionButtons
       * @private
       */
      _generateTransitionButtons: function ActivitiTransitions__generateTransitionButtons()
      {
         // create a submit button for each transition
         for (var i = 0, ii = this.options.transitions.length; i < ii; i++)
         {
            this._generateTransitionButton(this.options.transitions[i]);
         }
      },
      
      /**
       * Generates a YUI button for the given transition.
       * 
       * @method _generateTransitionButton
       * @param transition {object} An object representing the transition
       * @private
       */
      _generateTransitionButton: function ActivitiTransitions__generateTransitionButton(transition)
      {
         // create a button and add to the DOM
         var button = document.createElement('input');
         button.setAttribute("id", this.id + "-" + transition.id);
         button.setAttribute("value", transition.label);
         button.setAttribute("type", "button");
         Dom.get(this.id + "-buttons").appendChild(button);
         
         // create the YUI button and register the event handler
         var button = Alfresco.util.createYUIButton(this, transition.id, this.onClick);
         
         // register the button as a submitElement with the forms runtime instance
         YAHOO.Bubbling.fire("addSubmitElement", button);
      },
      
      /**
       * Retrieves, creating if necessary, the hidden field used
       * to hold the selected transition.
       * 
       * @method _getHiddenField
       * @return The hidden field element
       * @private
       */
      _getHiddenField: function ActivitiTransitions__getHiddenField()
      {
         // create the hidden field (if necessary)
         var hiddenField = Dom.get(this.id + "-hidden");
         if (hiddenField === null)
         {
            hiddenField = document.createElement('input');
            hiddenField.setAttribute("id", this.id + "-hidden");
            hiddenField.setAttribute("type", "hidden");
            hiddenField.setAttribute("name", this.options.hiddenFieldName);
            
            Dom.get(this.id).appendChild(hiddenField);
         }
         
         return hiddenField;
      },
      
      _generateTransitionsHiddenField: function ActivitiTransitions__generateTransitionsHiddenField()
      {
         // create the hidden transitions field (if necessary)
         var hiddenField = Dom.get(this.id + "-transitions-hidden");
         if (hiddenField === null)
         {
            hiddenField = document.createElement('input');
            hiddenField.setAttribute("id", this.id + "-transitions-hidden");
            hiddenField.setAttribute("type", "hidden");
            hiddenField.setAttribute("name", "prop_transitions");
            hiddenField.setAttribute("value", "Next");
            
            Dom.get(this.id).appendChild(hiddenField);
         }
      }
   });
})();