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
 * ControlWrapper component.
 * 
 * @namespace Alfresco.module
 * @class Alfresco.module.ControlWrapper
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
   var $hasEventInterest = Alfresco.util.hasEventInterest;
   
   /**
    * ControlWrapper constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.ControlWrapper} The new ControlWrapper instance
    * @constructor
    */
   Alfresco.module.ControlWrapper = function(htmlId)
   {
      Alfresco.module.ControlWrapper.superclass.constructor.call(this, "Alfresco.ControlWrapper", htmlId, "json");
      
      if (htmlId !== "null")
      {
         YAHOO.Bubbling.on("formValueChanged", this.onFormValueChanged, this);
      }
      
      return this;
   };
   
   YAHOO.lang.extend(Alfresco.module.ControlWrapper, Alfresco.component.Base,
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
          * Control type
          *
          * @property type
          * @type string
          */
         type: "",

         /**
          * Override the default field name if required
          *
          * @property name
          * @type string
          * @default "wrapped-" + options.type
          */
         name: null,

         /**
          * Label to annotate control with
          *
          * @property label
          * @type string
          */
         label: "",

         /**
          * Value of the control
          *
          * @property value
          * @type string
          */
         value: null,

         /**
          * Control-specific custom parameters
          *
          * @property controlParams
          * @type object
          */
         controlParams: {},
         
         /**
          * Field-specific custom parameters
          *
          * @property field
          * @type object
          */
         field: {},
         
         /**
          * Container element.
          *
          * @property container
          * type string | element
          */
         container: null,
         
         /**
          * Callback function for when value on control is submitted back to the form
          *
          * @property fnValueChanged
          * @type object
          */
         fnValueChanged: null
      },

      /**
       * Render method. Prompts loading of Forms control via wrapper.
       *
       * @method render
       * @param fnCallback {object} Callback for successful request, should have the following form: {fn: fnHandler, scope: functionScope, obj: optionalParam}
       */
      render: function ControlWrapper_render(fnCallback)
      {
         var name = this.options.name || "wrapper-" + this.options.type,
            dataObj =
            {
               htmlid: this.id,
               type: this.options.type,
               name: name,
               label: this.options.label,
               value: this.options.value || "",
               controlParams: YAHOO.lang.JSON.stringify(this.options.controlParams),
               field: YAHOO.lang.JSON.stringify(this.options.field)
            };

         this.eventGroup = this.id + "_" + name + "-cntrl";

         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.URL_SERVICECONTEXT + "components/form/control-wrapper",
            method: Alfresco.util.Ajax.POST,
            dataObj: dataObj,
            successCallback:
            {
               fn: this.onTemplateLoaded,
               scope: this,
               obj: fnCallback
            },
            failureMessage: "Could not load control-wrapper template.",
            execScripts: true
         });
      },

      /**
       * Event callback when this component has been reloaded via AJAX call
       *
       * @method onTemplateLoaded
       * @param response {object} Server response from load template XHR request
       */
      onTemplateLoaded: function ControlWrapper_onTemplateLoaded(response, fnCallback)
      {
         // Inject the template from the XHR request into a new DIV element
         var containerEl = Dom.get(this.options.container);
         if (containerEl)
         {
            containerEl.innerHTML = response.serverResponse.responseText;
         }

         if (fnCallback && typeof fnCallback.fn == "function")
         {
            // Execute the callback in the relevant scope
            fnCallback.fn.call((typeof fnCallback.scope == "object" ? fnCallback.scope : this), fnCallback.obj);
         }
      },

      /**
       * Form value changed event handler
       *
       * @method onFormValueChanged
       */
      onFormValueChanged: function ControlWrapper_onFormValueChanged(layer, args)
      {
         var fnCallback = this.options.fnValueChanged;
         
         if ($hasEventInterest(this, args) && fnCallback && typeof fnCallback.fn == "function")
         {
            // Execute the callback in the relevant scope
            fnCallback.fn.call((typeof fnCallback.scope == "object" ? fnCallback.scope : this), Alfresco.util.cleanBubblingObject(args[1]));
         }
      }
   });

   /* Dummy instance to load optional YUI components early */
   var dummyInstance = new Alfresco.module.ControlWrapper("null");
})();
