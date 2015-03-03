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
 * ConsoleApplication tool component.
 * 
 * @namespace Alfresco
 * @class Alfresco.ConsoleApplication
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
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * ConsoleApplication constructor.
    * 
    * @param {String} htmlId The HTML id üof the parent element
    * @return {Alfresco.ConsoleApplication} The new ConsoleApplication instance
    * @constructor
    */
   Alfresco.ConsoleApplication = function(htmlId)
   {
      this.name = "Alfresco.ConsoleApplication";
      Alfresco.ConsoleApplication.superclass.constructor.call(this, htmlId);
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "json", "history"], this.onComponentsLoaded, this);
      
      /* Define panel handlers */
      var parent = this;
      
      // NOTE: the panel registered first is considered the "default" view and is displayed first
      
      /* Options Panel Handler */
      OptionsPanelHandler = function OptionsPanelHandler_constructor()
      {
         OptionsPanelHandler.superclass.constructor.call(this, "options");
      };
      
      YAHOO.extend(OptionsPanelHandler, Alfresco.ConsolePanelHandler,
      {
         /**
          * Called by the ConsolePanelHandler when this panel shall be loaded
          *
          * @method onLoad
          */
         onLoad: function onLoad()
         {
            // Buttons
            parent.widgets.applyButton = Alfresco.util.createYUIButton(parent, "apply-button", null,
            {
               type: "submit"
            });
            parent.widgets.upload = Alfresco.util.createYUIButton(parent, "upload-button", this.onUpload);
            parent.widgets.reset = Alfresco.util.createYUIButton(parent, "reset-button", this.onReset);
            
            // Form definition
            var form = new Alfresco.forms.Form(parent.id + "-options-form");
            form.setSubmitElements([parent.widgets.applyButton]);
            form.setSubmitAsJSON(true);
            form.setAJAXSubmit(true,
            {
               successCallback:
               {
                  fn: this.onSuccess,
                  scope: this
               }
            });
            form.init();
         },
         
         /**
          * Successfully applied options event handler
          *
          * @method onSuccess
          * @param response {object} Server response object
          */
         onSuccess: function OptionsPanel_onSuccess(response)
         {
            if (response && response.json)
            {
               if (response.json.success)
               {
                  // refresh the browser to force the themed components to reload
                  window.location.reload(true);
               }
               else if (response.json.message)
               {
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     text: response.json.message
                  });
               }
            }
            else
            {
               Alfresco.util.PopupManager.displayPrompt(
               {
                  text: Alfresco.util.message("message.failure")
               });
            }
         },
         
         /**
          * Upload button click handler
          *
          * @method onUpload
          * @param e {object} DomEvent
          * @param p_obj {object} Object passed back from addListener method
          */
         onUpload: function OptionsPanel_onUpload(e, p_obj)
         {
            if (!this.fileUpload)
            {
               this.fileUpload = Alfresco.getFileUploadInstance();
            }
            
            // Show uploader for single file select - override the upload URL to use appropriate upload service
            var uploadConfig =
            {
               flashUploadURL: "slingshot/application/uploadlogo",
               htmlUploadURL: "slingshot/application/uploadlogo.html",
               mode: this.fileUpload.MODE_SINGLE_UPLOAD,
               onFileUploadComplete:
               {
                  fn: this.onFileUploadComplete,
                  scope: this
               }
            };
            this.fileUpload.show(uploadConfig);
            Event.preventDefault(e);
         },
         
         /**
          * Reset button click handler
          *
          * @method onReset
          * @param e {object} DomEvent
          * @param p_obj {object} Object passed back from addListener method
          */
         onReset: function OptionsPanel_onReset(e, p_obj)
         {
            // replace logo image URL with the default one
            var logoImg = Dom.get(this.id + "-logoimg");
            logoImg.src = parent.options.defaultlogo;
            
            // set 'reset' value in hidden field ready for options form submit
            Dom.get("console-options-logo").value = "reset";
         }
      });
      new OptionsPanelHandler();
      
      return this;
   };
   
   YAHOO.extend(Alfresco.ConsoleApplication, Alfresco.ConsoleTool,
   {
      /**
       * File Upload complete event handler
       *
       * @method onFileUploadComplete
       * @param complete {object} Object literal containing details of successful and failed uploads
       */
      onFileUploadComplete: function onFileUploadComplete(complete)
      {
         var success = complete.successful.length;
         if (success != 0)
         {
            var noderef = complete.successful[0].nodeRef;
            
            // replace image URL with the updated one
            var logoImg = Dom.get(this.id + "-logoimg");
            logoImg.src = Alfresco.constants.PROXY_URI + "api/node/" + noderef.replace("://", "/") + "/content";
            
            // set noderef value in hidden field ready for options form submit
            Dom.get("console-options-logo").value = noderef;
         }
      }
   });
})();