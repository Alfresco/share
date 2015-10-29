/**
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
 * CreateSite module
 *
 * A dialog for creating sites
 *
 * @namespace Alfresco.module
 * @class Alfresco.module.CreateSite
 */
(function()
{
   
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Element = YAHOO.util.Element,
      KeyListener = YAHOO.util.KeyListener;
   
   /**
    * CreateSite constructor.
    *
    * @param htmlId {string} A unique id for this component
    * @return {Alfresco.CreateSite} The new DocumentList instance
    * @constructor
    */
   Alfresco.module.CreateSite = function(containerId)
   {
      var instance = Alfresco.util.ComponentManager.get(this.id);
      if (instance !== null)
      {
         throw new Error("An instance of Alfresco.module.CreateSite already exists.");
      }

      Alfresco.module.CreateSite.superclass.constructor.call(this, "Alfresco.module.CreateSite", containerId, ["button", "container", "connection", "selector", "json"]);

      return this;
   };

   YAHOO.extend(Alfresco.module.CreateSite, Alfresco.component.Base,
   {
      /**
       * Shows the CreateSite dialog to the user.
       *
       * @method show
       */
      show: function CreateSite_show()
      {
         if (this.widgets.panel)
         {
            // Panel is already in the DOM, so just show it
            this._showPanel();
         }
         else
         {
            /**
             * Load the gui from the server and let the templateLoaded() method
             * handle the rest.
             */
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "modules/create-site",
               dataObj:
               {
                  htmlid: this.id
               },
               successCallback:
               {
                  fn: this.onTemplateLoaded,
                  scope: this
               },
               execScripts: true,
               failureMessage: "Could not load create site template"
            });
         }
      },

      /**
       * Called when the CreateSite html template has been returned from the server.
       * Creates the YUI gui objects such as buttons and a panel and shows it.
       *
       * @method onTemplateLoaded
       * @param response {object} a Alfresco.util.Ajax.request response object 
       */
      onTemplateLoaded: function CreateSite_onTemplateLoaded(response)
      {
         // Inject the template from the XHR request into a new DIV element
         var containerDiv = document.createElement("div");
         containerDiv.innerHTML = response.serverResponse.responseText;

         // The panel is created from the HTML returned in the XHR request, not the container
         var panelDiv = Dom.getFirstChild(containerDiv);

         this.widgets.panel = Alfresco.util.createYUIPanel(panelDiv);

         // Create the cancel button
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel-button", this.onCancelButtonClick);

         // Create the ok button, the forms runtime will handle when its clicked
         this.widgets.okButton = Alfresco.util.createYUIButton(this, "ok-button", null,
         {
            type: "submit",
            additionalClass: "alf-primary-button"
         });
         
         // Site access form controls
         this.widgets.siteVisibility = Dom.get(this.id + "-visibility");
         this.widgets.isPublic = Dom.get(this.id + "-isPublic");
         this.widgets.isModerated = Dom.get(this.id + "-isModerated");
         this.widgets.isPrivate = Dom.get(this.id + "-isPrivate");

         // Configure the forms runtime
         var createSiteForm = new Alfresco.forms.Form(this.id + "-form");
         this.widgets.form = createSiteForm;

         this.elTitle = Dom.get(this.id + "-title");
         this.elShortName = Dom.get(this.id + "-shortName");

         /**
          * Title field
          */
         // Title is mandatory
         createSiteForm.addValidation(this.elTitle, Alfresco.forms.validation.mandatory, null, "keyup", this.msg("validation-hint.mandatory"));
         // ...and has a maximum length
         createSiteForm.addValidation(this.elTitle, Alfresco.forms.validation.length,
         {
            max: 256,
            crop: true
         }, "keyup");

         // Auto-generate a short name as long as the user hasn't manually entered one first
         Event.addListener(this.elTitle, "keyup", this.onSiteNameChange, this, true);
         Event.addListener(this.elTitle, "mouseout", this.onSiteNameChange, this, true);

         /**
          * Short name field
          */
         this.shortNameEdited = false;

         // Shortname is mandatory
         createSiteForm.addValidation(this.elShortName, Alfresco.forms.validation.mandatory, null, "keyup", this.msg("validation-hint.mandatory"));
         // ...and is restricted to a limited set of characters
         createSiteForm.addValidation(this.elShortName, Alfresco.forms.validation.regexMatch,
         {
            pattern: /^[ ]*[0-9a-zA-Z\-]+[ ]*$/
         }, "keyup", this.msg("validation-hint.siteName"));
         // ...and has a maximum length
         createSiteForm.addValidation(this.elShortName, Alfresco.forms.validation.length,
         {
            max: 72,
            crop: true
         }, "keyup");

         // Flag that the user has edited the short name
         Event.addListener(this.elShortName, "keyup", function CreateSite_shortName_keyUp()
         {
            this.shortNameEdited = this.elShortName.value.length > 0;
         }, this, true);

         /**
          * Description field
          */
         // Description kept to a reasonable length
         createSiteForm.addValidation(this.id + "-description", Alfresco.forms.validation.length,
         {
            max: 512,
            crop: true
         }, "keyup");

         var sitePresetEl = Dom.get(this.id + "-sitePreset");
         Event.addListener(sitePresetEl, "change", function CreateSite_sitePreset_change()
         {
            this.onSitePresetChange(sitePresetEl.options[sitePresetEl.selectedIndex].value);
         }, this, true);
         if (sitePresetEl.options.length > 0)
         {
            this.onSitePresetChange(sitePresetEl.options[sitePresetEl.selectedIndex].value);
         }

         // The ok button is the submit button, and it should be enabled when the form is ready
         createSiteForm.setSubmitElements(this.widgets.okButton);
         createSiteForm.doBeforeFormSubmit = {
            fn: this.doBeforeFormSubmit,
            obj: null,
            scope: this
         };

         // Submit as an ajax submit (not leave the page), in json format
         createSiteForm.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onCreateSiteSuccess,
               scope: this               
            },
            failureCallback:
            {
               fn: this.onCreateSiteFailure,
               scope: this
            }
         });
         createSiteForm.setSubmitAsJSON(true);
         // We're in a popup, so need the tabbing fix
         createSiteForm.applyTabFix();
         createSiteForm.doBeforeAjaxRequest = {
            fn: this.doBeforeAjaxRequest,
            scope: this
         };
         createSiteForm.init();

         // Show the panel
         this._showPanel();
      },

      /**
       * Called when a preset as been selected.
       * Implement to make it possible to dispay custom site property fields
       *
       * @method onSitePresetChange
       * @param sitePreset
       */
      onSitePresetChange: function(sitePreset) {},

      /**
       * Interceptor just before Ajax request is sent.
       *
       * @method doBeforeAjaxRequest
       * @param p_config {object} Object literal containing request config
       * @return {boolean} True to continue sending form, False to prevent it
       */
      doBeforeAjaxRequest: function CreateSite_doBeforeAjaxRequest(p_config)
      {
         return true;
      },

      /**
       * Called before the form is about to be submitted
       *
       * @method doBeforeFormSubmit
       * @param form {HTMLFormElement} The create site form
       * @param obj {Object} Callback object
       */
      doBeforeFormSubmit: function(form, obj)
      {
         var formEl = Dom.get(this.id + "-form");
         formEl.attributes.action.nodeValue = Alfresco.constants.URL_SERVICECONTEXT + "modules/create-site";

         this.widgets.cancelButton.set("disabled", true);

         // Site access
         var siteVisibility;
         if (this.widgets.isPublic.checked)
         {
            siteVisibility = "PUBLIC";
         }
         else if (this.widgets.isModerated.checked)
         {
            siteVisibility = "MODERATED";
         }
         else
         {
            siteVisibility = "PRIVATE";
         }
         this.widgets.siteVisibility.value = siteVisibility;

         this.widgets.panel.hide();
         this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: Alfresco.util.message("message.creating", this.name),
            spanClass: "wait",
            displayTime: 0
         });
      },

      /**
       * Converts a user-entered string into a "readable" safe URL by stripping characters
       *
       * @method safeURL
       * @param text {string} The string to convert
       * @return {string} Safe and readable URL
       */
      safeURL: function CreateSite_safeURL(text)
      {
         // Strip unwanted characters and trim leading and trailing spaces
         text = YAHOO.lang.trim(text.replace(/[^0-9a-zA-Z\-\s]/g, ""));
         // Replace remaining spaces with dash & convert the whole string to lower case
         text = text.replace(/\s+/g, "-").toLowerCase();

         return text;
      },
      
      /**
       * Called when user fills site name input
       *
       * @method onSiteNameChange
       * @param type {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onSiteNameChange: function CreateSite_onSiteNameChange(type, args)
      {
         if (!this.shortNameEdited)
         {
            this.elShortName.value = this.safeURL(this.elTitle.value).substring(0, 72);
            this.widgets.form.validate();
         }
      },
      
      /**
       * Called when user clicks on the cancel button.
       * Closes the CreateSite panel.
       *
       * @method onCancelButtonClick
       * @param type
       * @param args
       */
      onCancelButtonClick: function CreateSite_onCancelButtonClick(type, args)
      {
         // Reset the form fields
         try
         {
            Dom.get(this.id + "-title").value = "";
            Dom.get(this.id + "-shortName").value = "";
            Dom.get(this.id + "-description").value = "";
            Dom.get(this.id + "-sitePreset").selectedIndex = 0;
            Dom.get(this.id + "-isPublic").checked = "checked";
            Dom.get(this.id + "-isModerated").checked = "";
            Dom.get(this.id + "-isModerated").disabled = false;
         }
         catch(e)
         {
         }

         this.widgets.panel.hide();
      },

      /**
       * Called when a site has been succesfully created on the server.
       * Redirects the user to the new site.
       *
       * @method onCreateSiteSuccess
       * @param response
       */
      onCreateSiteSuccess: function CreateSite_onCreateSiteSuccess(response)
      {
         if (response.json !== undefined && response.json.success)
         {
            // The site has been successfully created - add it to the user's favourites and navigate to it
            var preferencesService = new Alfresco.service.Preferences(),
               shortName = response.config.dataObj.shortName;
            
            preferencesService.favouriteSite(shortName,
            {
               successCallback:
               {
                  fn: function CreateSite_onCreateSiteSuccess_successCallback()
                  {
                     document.location.href = Alfresco.constants.URL_PAGECONTEXT + "site/" + shortName + "/dashboard";
                  }
               }
            });
         }
         else
         {
            this._adjustGUIAfterFailure(response);
         }
      },

      /**
       * Called when a site failed to be created.
       *
       * @method onCreateSiteFailure
       * @param response
       */
      onCreateSiteFailure: function CreateSite_onCreateSiteFailure(response)
      {
         this._adjustGUIAfterFailure(response);
      },

      /**
       * Helper method that restores the gui and displays an error message.
       *
       * @method _adjustGUIAfterFailure
       * @param response
       */
      _adjustGUIAfterFailure: function CreateSite__adjustGUIAfterFailure(response)
      {
         this.widgets.feedbackMessage.destroy();
         this.widgets.cancelButton.set("disabled", false);
         this.widgets.panel.show();
         var text = Alfresco.util.message("message.failure", this.name);
         
         if (response.serverResponse.status === 403)
         {
            // User does not have permissions to create the site
            if (response.json.message) 
            {
               text = Alfresco.util.message(response.json.message, this.name)
            }
            else
            {
               text = Alfresco.util.message("error.noPermissions", this.name);
            }
         }
         else if (response.json.message)
         {
            var tmp = Alfresco.util.message(response.json.message, this.name);
            text = tmp ? tmp : text;
         }
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: Alfresco.util.message("message.failure", this.name), 
            text: text
         });
      },

      /**
       * Prepares the gui and shows the panel.
       *
       * @method _showPanel
       * @private
       */
      _showPanel: function CreateSite__showPanel()
      {
         // Reset form before displaying it
         this.widgets.form.reset();

         // Show the upload panel
         this.widgets.panel.show();

         // Firefox insertion caret fix
         Alfresco.util.caretFix(this.id + "-form");

         if (!this.widgets.escapeListener)
         {
            // Register the ESC key to close the dialog
            this.widgets.escapeListener = new KeyListener(document,
            {
               keys: KeyListener.KEY.ESCAPE
            },
            {
               fn: function(id, keyEvent)
               {
                  this.onCancelButtonClick();
               },
               scope: this,
               correctScope: true
            });
            this.widgets.escapeListener.enable();
         }

         // Set the focus on the first field
         Dom.get(this.id + "-title").focus();
      }
   });
})();

Alfresco.module.getCreateSiteInstance = function()
{
   var instanceId = "alfresco-createSite-instance";
   return Alfresco.util.ComponentManager.get(instanceId) || new Alfresco.module.CreateSite(instanceId);
};