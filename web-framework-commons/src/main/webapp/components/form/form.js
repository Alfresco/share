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
 * Form UI component.
 * 
 * @namespace Alfresco
 * @class Alfresco.FormUI
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Element = YAHOO.util.Element;
   
   /**
    * FormUI constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.FormUI} The new FormUI instance
    * @constructor
    */
   Alfresco.FormUI = function FormUI_constructor(htmlId, parentId)
   {
      Alfresco.FormUI.superclass.constructor.call(this, "Alfresco.FormUI", htmlId, ["button", "menu", "container"]);

      // Initialise prototype properties
      this.parentId = parentId;
      this.buttons = {};
      this.formsRuntime = null;
      this.eventGroup = htmlId;
      
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("mandatoryControlValueUpdated", this.onMandatoryControlValueUpdated, this);
      YAHOO.Bubbling.on("registerValidationHandler", this.onRegisterValidationHandler, this);
      YAHOO.Bubbling.on("addSubmitElement", this.onAddSubmitElement, this);

      return this;
   };

   /**
    * Extend from Base component
    */
   YAHOO.extend(Alfresco.FormUI, Alfresco.component.Base,
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
          * Mode the current form is in, can be "view", "edit" or "create", defaults to "edit".
          * 
          * @property mode
          * @type string
          */ 
         mode: "edit",
         
         /**
          * Encoding type to be used when the form is submitted, can be "multipart/form-data",
          * "application/x-www-form-urlencoded" or "application/json", defaults to "multipart/form-data".
          * 
          * @property enctype
          * @type string
          */ 
         enctype: "multipart/form-data",
         
         /**
          * List of objects representing the id of each form field
          * 
          * @property fields
          * @type array[object]
          */
         fields: [],
         
         /**
          * List of objects representing the constraints to setup on the form fields
          * 
          * @property fieldConstraints
          * @type array[object]
          */
         fieldConstraints: [],

         /**
          * Arguments used to build the form.
          * Used to Ajax-rebuild the form when in "view" mode
          * 
          * @property arguments
          * @type object
          */
         arguments: {}
      },
      
      /**
       * Object container for storing YUI button instances.
       * 
       * @property buttons
       * @type object
       */
      buttons: null,
       
      /**
       * The forms runtime instance.
       * 
       * @property formsRuntime
       * @type object
       */
      formsRuntime: null, 
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function FormUI_onReady()
      {
         if (this.options.mode !== "view")
         {
            // make buttons YUI buttons
            
            if (Dom.get(this.id + "-submit") !== null)
            {
               this.buttons.submit = Alfresco.util.createYUIButton(this, "submit", null,
               {
                  type: "submit",
                  additionalClass:"alf-primary-button"
               });
   
               // force the generated button to have a name of "-" so it gets ignored in
               // JSON submit. TODO: remove this when JSON submit behaviour is configurable
               Dom.get(this.id + "-submit-button").name = "-";
            }
            
            if (Dom.get(this.id + "-reset") !== null)
            {
               this.buttons.reset = Alfresco.util.createYUIButton(this, "reset", null,
               {
                  type: "reset"
               });

               // force the generated button to have a name of "-" so it gets ignored in
               // JSON submit. TODO: remove this when JSON submit behaviour is configurable
               Dom.get(this.id + "-reset-button").name = "-";
            }

            if (Dom.get(this.id + "-cancel") !== null)
            {
               this.buttons.cancel = Alfresco.util.createYUIButton(this, "cancel", null);

               // force the generated button to have a name of "-" so it gets ignored in
               // JSON submit. TODO: remove this when JSON submit behaviour is configurable
               Dom.get(this.id + "-cancel-button").name = "-";
            }

            // fire event to inform any listening components that the form HTML is ready
            YAHOO.Bubbling.fire("formContentReady", this);

            this.formsRuntime = new Alfresco.forms.Form(this.id);
            this.formsRuntime.setSubmitElements(this.buttons.submit);
            if (this.options.disableSubmitButton != null && this.options.disableSubmitButton == true)
            {
                this.formsRuntime.setShowSubmitStateDynamically(true);
            }
            
            // setup JSON/AJAX mode if appropriate
            if (this.options.enctype === "application/json")
            {
               this.formsRuntime.setAJAXSubmit(true,
               {
                  successCallback:
                  {
                     fn: this.onJsonPostSuccess,
                     scope: this
                  },
                  failureCallback:
                  {
                     fn: this.onJsonPostFailure,
                     scope: this
                  }
               });
               this.formsRuntime.setSubmitAsJSON(true);
            }

            // add field help
            for (var f = 0; f < this.options.fields.length; f++)
            {
               var ff = this.options.fields[f],
                  iconEl = Dom.get(this.parentId + "_" + ff.id + "-help-icon");
               if (iconEl)
               {
                  Alfresco.util.useAsButton(iconEl, this.toggleHelpText, ff.id, this);
               }
            }

            // add any field constraints present
            for (var c = 0; c < this.options.fieldConstraints.length; c++)
            {
               var fc = this.options.fieldConstraints[c];
               
               // Check the number of events for the handler...
               var events = fc.event.split(",");
               for (var e = 0; e < events.length; e++)
               {
                  var trimmedEvent = events[e].replace(" ", "");
                  this.formsRuntime.addValidation(fc.fieldId, fc.handler, fc.params, trimmedEvent, fc.message);
               }
            }

            // fire event to inform any listening components that the form is about to be initialised
            YAHOO.Bubbling.fire("beforeFormRuntimeInit", 
            {
               eventGroup: this.eventGroup,
               component: this,
               runtime: this.formsRuntime 
            });
            
            this.formsRuntime.init();
            
            // fire event to inform any listening components that the form has finished initialising
            YAHOO.Bubbling.fire("afterFormRuntimeInit",
            {
               eventGroup: this.eventGroup,
               component: this,
               runtime: this.formsRuntime 
            });
         }
      },

      /**
       * Toggles help text for a field.
       *
       * @method toggleHelpText
       * @param event The user event
       * @param fieldId The id of the field to toggle help text for
       */
      toggleHelpText: function FormUI_toggleHelpText(event, fieldId)
      {
         Alfresco.util.toggleHelpText(this.parentId + "_" + fieldId + "-help");
      },
      
      /**
       * Default handler used when submit mode is JSON and the sumbission was successful
       *
       * @method onJsonPostSuccess
       * @param response The response from the submission
       */
      onJsonPostSuccess: function FormUI_onJsonPostSuccess(response)
      {
         // TODO: Display the JSON response here by default, when it's returned!
         
         Alfresco.util.PopupManager.displayPrompt(
         {
            text: response.serverResponse.responseText
         });
      },
      
      /**
       * Default handler used when submit mode is JSON and the sumbission failed
       *
       * @method onJsonPostFailure
       * @param response The response from the submission
       */
      onJsonPostFailure: function FormUI_onJsonPostFailure(response)
      {
         var errorMsg = this.msg("form.jsonsubmit.failed");
         if (response.json && response.json.message)
         {
            errorMsg = errorMsg + ": " + response.json.message;
         }
         
         Alfresco.util.PopupManager.displayPrompt(
         {
            text: errorMsg
         });
      },
      
      /**
       * Form refresh event handler
       *
       * @method onFormRefresh
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onFormRefresh: function FormUI_onFormRefresh(layer, args)
      {
         // Can't do anything if basic arguments weren't set
         if (this.options.arguments)
         {
            var itemKind = this.options.arguments.itemKind,
               itemId = this.options.arguments.itemId,
               formId = this.options.arguments.formId;
            
            if (itemKind && itemId)
            {
               var fnFormLoaded = function(response, p_formUI)
               {
                  Alfresco.util.populateHTML(
                     [ p_formUI.parentId, response.serverResponse.responseText ]
                  );
                  
                  var formEl = Dom.get(p_formUI.parentId),
                      me = this;
                  // MNT-10256 fix, update date fields so that they match common format
                  if (formEl)
                  {
                     Dom.getElementsByClassName("viewmode-value-date", "span", formEl, function()
                     {
                        var showTime = Dom.getAttribute(this, "data-show-time")
                        var dateFormat = (showTime=='false') ? me.msg("date-format.defaultDateOnly") : me.msg("date-format.default")
                        this.innerHTML = Alfresco.util.formatDate(Alfresco.util.fromISO8601(Dom.getAttribute(this, "data-date-iso8601")), dateFormat )
                     });
                  }
               };
               
               var data =
               {
                  htmlid: this.parentId,
                  formUI: false,
                  mode: this.options.mode,
                  itemKind: itemKind,
                  itemId: itemId,
                  formId: formId
               };

               Alfresco.util.Ajax.request(
               {
                  url: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
                  dataObj: data,
                  successCallback:
                  {
                     fn: fnFormLoaded,
                     obj: this,
                     scope: this
                  },
                  scope: this,
                  execScripts: true
               });
            }
         }
      },
      
      /**
       * Mandatory control value updated event handler
       *
       * @method onMandatoryControlValueUpdated
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onMandatoryControlValueUpdated: function FormUI_onMandatoryControlValueUpdated(layer, args)
      {
         // the value of a mandatory control on the page (usually represented by a hidden field)
         // has been updated, force the forms runtime to check if form state is still valid
         if (this.formsRuntime)
         {
            this.formsRuntime.validate();
         }
      },
      
      /**
       * Register validation handler event handler
       *
       * @method onRegisterValidationHandler
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onRegisterValidationHandler: function FormUI_onRegisterValidationHandler(layer, args)
      {
         if (this.formsRuntime)
         {
            // extract the validation arguments
            var validation = args[1];
            
            // check the minimim required data is provided
            if (validation && validation.fieldId && validation.handler)
            {
               // register with the forms runtime instance
               this.formsRuntime.addValidation(validation.fieldId, validation.handler, validation.args, 
                     validation.when, validation.message);
            }
         }
      },
      
      /**
       * Adds a submit element to the form runtime instance
       * 
       * @method onAddSubmitElement
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onAddSubmitElement: function FormUI_onAddSubmitElement(layer, args)
      {
         // extract the submit element to add
         var submitElement = args[1];
         
         // add to the forms runtime instance, if there is one
         if (this.formsRuntime != null)
         {
            this.formsRuntime.addSubmitElement(submitElement);
         }
      }
   });
})();


/**
 * Helper function to add the current state of the given multi-select list to
 * the given hidden field.
 * 
 * @method updateMultiSelectListValue
 * @param list {string} The id of the multi-select element
 * @param hiddenField {string} The id of the hidden field to populate the value with
 * @param signalChange {boolean} If true a bubbling event is sent to inform any
 *        interested listeners that the hidden field value changed
 * @static
 */
Alfresco.util.updateMultiSelectListValue = function(list, hiddenField, signalChange)
{
   var listElement = YUIDom.get(list);
   
   if (listElement !== null)
   {
      var values = new Array();
      for (var j = 0, jj = listElement.options.length; j < jj; j++)
      {
         if (listElement.options[j].selected)
         {
            values.push(listElement.options[j].value);
         }
      }
      
      YUIDom.get(hiddenField).value = values.join(",");
      
      if (signalChange)
      {
         YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
      }
   }
};

/**
 * Helper function to toggle the state of the help text element
 * represented by the given id.
 * 
 * @method toggleHelpText
 * @param helpTextId The id of the help text element to toggle
 * @static
 */
Alfresco.util.toggleHelpText = function(helpTextId)
{
   var helpElem = YUIDom.get(helpTextId);
   
   if (helpElem)
   {
      if (helpElem.style.display != "block")
      {
         helpElem.style.display = "block";
      }
      else
      {
         helpElem.style.display = "none";
      }
   }
};

