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
 * Alfresco.WikiCreateForm
 * 
 * @namespace Alfresco
 * @class Alfresco.Wiki
 * @extends Alfresco.component.Base
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
    * WikiCreateForm constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.LinksView} The new LinksView instance
    * @constructor
    */
   Alfresco.WikiCreateForm = function(htmlId)
   {
      Alfresco.WikiCreateForm.superclass.constructor.call(this, "Alfresco.WikiCreateForm", htmlId, ["button", "container", "connection", "editor"]);      
      return this;
   };
   
   YAHOO.extend(Alfresco.WikiCreateForm, Alfresco.component.Base,
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
          * Current siteId.
          *
          * @property siteId
          * @type string
          * @default ""
          */
         siteId: "",

         /**
          * The user locale
          *
          * @property locale
          * @type string
          * @default ""
          */
         locale: ""
      },

      /**
       * Indicates whether or not to display the page unload dialog when the onbeforeunload event is fired.
       * This value is returned by an unload callback function which is called before the page is unloaded.
       * The value only gets toggled to "false" when the page is saved as there is no point in warning the
       * user when they're in the process of saving data. 
       * 
       * @property _showUnloadDialog
       * @type boolean
       * @default true
       */
      _showUnloadDialog: true,
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Initialises components, including YUI widgets.
       *
       * @method onReady
       */
      onReady: function WikiCreateForm_onReady()
      {
         this.tagLibrary = new Alfresco.module.TagLibrary(this.id);
         this.tagLibrary.setOptions(
         {
            siteId: this.options.siteId
         });

         // TinyMCE
         this.widgets.editor = Alfresco.util.createImageEditor(this.id + '-content',
         {
            height: 300,
            toolbar: "styleselect | bold italic | forecolor backcolor | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image | print preview fullscreen | alfresco-imagelibrary alfresco-linklibrary",
            extended_valid_elements : "style[type]",
            valid_children : "+body[style]",
            siteId: this.options.siteId,
            language: this.options.locale
         });
         
         // This callback method is passed through for handling onbeforeunload events to stop the dialog being
         // shown when a page save is requested. This is because the dialog is only required if the user is 
         // navigating away from the page *without* saving it. They shouldn't be warned when attempting to save.
         var _this = this;
         var unloadCallback = function() 
         {
            return _this._showUnloadDialog; // This value gets toggled when saving the page.
         }
         
         this.widgets.editor.addPageUnloadBehaviour(this.msg("message.unsavedChanges.wiki"), unloadCallback);
         this.widgets.editor.render();

         this.widgets.saveButton = new YAHOO.widget.Button(this.id + "-save-button",
         {
            type: "submit"
         });
         this.widgets.saveButton.addClass("alf-primary-button");

         Alfresco.util.createYUIButton(this, "cancel-button", null,
         {
            type: "link"
         });

         // Create the form that does the validation/submit
         this.widgets.form = new Alfresco.forms.Form(this.id + "-form");
         var form = this.widgets.form;
         form.addValidation(this.id + "-title", Alfresco.forms.validation.mandatory, null, "blur");
         form.addValidation(this.id + "-title", Alfresco.forms.validation.wikiTitle, null, "keyup");
         form.addValidation(this.id + "-title", Alfresco.forms.validation.length,
         {
            max: 256,
            crop: true
         }, "keyup");

         form.setSubmitElements(this.widgets.saveButton);
         form.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onPageCreated,
               scope: this
            },
            failureCallback:
            {
               fn: this.onPageCreateFailed,
               scope: this
            }
         });

         form.setSubmitAsJSON(true);
         form.setAjaxSubmitMethod(Alfresco.util.Ajax.PUT);
         form.doBeforeFormSubmit =
         {
            fn: function WikiCreateForm_doBeforeFormSubmit(form, obj)
            {
               // Disable save button to prevent double-submission
               this.widgets.saveButton.set("disabled", true);
               // Put the HTML back into the text area
               this.widgets.editor.save();
               // Update the tags set in the form
               this.tagLibrary.updateForm(this.id + "-form", "tags");

               // Avoid submitting the input field used for entering tags
               var tagInputElem = Dom.get(this.id + "-tag-input-field");
               if (tagInputElem)
               {
                  tagInputElem.disabled = true;
               }
               
               var title = Dom.get(this.id + "-title").value;
               title = title.replace(/\s+/g, "_");
               // Set the "action" attribute of the form based on the page title
               form.action =  Alfresco.constants.PROXY_URI + "slingshot/wiki/page/" + this.options.siteId + "/" + Alfresco.util.encodeURIPath(title);
               
               // Display pop-up to indicate that the page is being saved
               this.widgets.savingMessage = Alfresco.util.PopupManager.displayMessage(
               {
                  displayTime: 0,
                  text: '<span class="wait">' + $html(this.msg("message.saving")) + '</span>',
                  noEscape: true
               });
            },
            scope: this
         };

         this.tagLibrary.initialize(form);
         form.init();
         Dom.get(this.id + "-title").focus();
      },

      /**
       * Event handler that gets called when the page is successfully created.
       * Redirects the user to the newly created page.
       *
       * @method onPageCreated
       * @param e {object} DomEvent
       */      
      onPageCreated: function WikiCreateForm_onPageCreated(e)
      {
         var title = "Main_Page"; // safe default
         
         var obj = YAHOO.lang.JSON.parse(e.serverResponse.responseText);
         if (obj)
         {
            title = obj.title;
         }
      
         this._showUnloadDialog = false; // Ensure that the dialog warning about leaving the page isn't shown on save requests.
         
         // Redirect to the page that has just been created
         window.location =  Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + "/wiki-page?title=" + encodeURIComponent(title);
      },

      /**
       * Event handler that gets called when the page has failed to be created.
       *
       * @method onPageCreateFailed
       * @param e {object} DomEvent
       */      
      onPageCreateFailed: function WikiCreateForm_onPageCreateFailed(e)
      {
         if (this.widgets.savingMessage)
         {
            this.widgets.savingMessage.destroy();
            this.widgets.savingMessage = null;
         }

         var pageTitle = e.config.dataObj.pageTitle;
         var me = this;

         if (e.serverResponse.status === 409)
         {
            Alfresco.util.PopupManager.displayPrompt(
            {
               title: this.msg("message.failure.title"),
               text: this.msg("message.failure.duplicate", pageTitle),
               buttons: [
               {
                  text: this.msg("button.ok"),
                  handler: function()
                  {
                     this.destroy();
                     Dom.get(me.id + "-title").focus();
                  },
                  isDefault: true
               }]
            });
         }
         else if (e.serverResponse.status == 401)
         {
            // Unauthenticated, which is probably due to a web-tier timeout or restart
            Alfresco.util.PopupManager.displayPrompt(
            {
               title: this.msg("message.sessionTimeout.title"),
               text: this.msg("message.sessionTimeout.text")
            });
         }
         else
         {
            Alfresco.util.PopupManager.displayPrompt(
            {
               title: this.msg("message.failure"),
               text: e.json.message
            });
         }

         // Enable the tags input field again
         var tagInputElem = Dom.get(this.id + "-tag-input-field");
         if (tagInputElem)
         {
            tagInputElem.disabled = false;
         }
      }

   });
      
})();      
