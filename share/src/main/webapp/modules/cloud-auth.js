/**
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
 * Checks and adds Alfresco Cloud Authentication details
 *
 * @namespace Alfresco.module
 * @class Alfresco.module.CloudAuth
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      KeyListener = YAHOO.util.KeyListener;

   Alfresco.module.CloudAuth = function(htmlId)
   {
      Alfresco.module.CloudAuth.superclass.constructor.call(this, htmlId);

      // Re-register with our own name
      this.name = "Alfresco.module.CloudAuth";
      Alfresco.util.ComponentManager.reregister(this);

      // Set up decoupled events:
      // Decoupled event listeners
      if (htmlId != "null")
      {
         this.eventGroup = htmlId;
      }


      return this;
   };

   YAHOO.extend(Alfresco.module.CloudAuth, Alfresco.component.Base,
   {

   options:
   {
      getAuthURL: Alfresco.constants.PROXY_URI + "cloud/person/credentials"
   },

   authDetails: {},

   // Is Authenticated?

   /**
    * Is the current user authenticated?
    *
    * @method isAuthenticated
    * @return {Boolean}
    */
   isAuthenticated: function cloudAuth_isAuthenticated()
   {
      return (typeof(this.getAuthUser()) !== "undefined") ? true : false;
   },

   /**
    * Retrieves the authentication details for the current user
    *
    * @method retrieveAuthDetails
    * @return {Object} - The JSON response from the Auth API
    */
   checkAuth: function cloudAuth_checkAuth()
   {
      var noAuthFn = this.displayPrompt;

      YAHOO.Bubbling.on("authDetailsAvailable", this.onAuthDetailsAvailable, this);

      Alfresco.util.Ajax.jsonGet(
      {
         url: this.options.getAuthURL,
         successCallback:
         {
            fn: function cloudAuth_retrieveAuthDetailsSuccess(response)
            {
               if (response.json !== undefined)
               {
                  this.authDetails = response.json;

                  // Fire event to inform any listening components that the data is ready
                  YAHOO.Bubbling.fire("authDetailsAvailable",
                  {
                     authDetails: this.authDetails
                  });
               }
            },
            scope: this
         },
         failureMessage: "Error checking cloud authentication details"
      });
   },

   // Submit new authentication

   // check auth details

   /**
    * Returns the Cloud username associated with the current user
    *
    * @method getAuthUser
    * @return {String}
    */
   // retrieve authenticated username
   getAuthUser: function cloudAuth_getAuthUser()
   {
      return this.authDetails.username;
   },

   /**
    * Called once the Auth details have been returned.
    *
    *
    */
   onAuthDetailsAvailable: function cloudAuth_onAuthDetailsAvailable()
   {
      // unsubscribe. May need to subscribe next time.
      YAHOO.Bubbling.unsubscribe("authDetailsAvailable", this.onAuthDetailsAvailable, this);
      if (this.isAuthenticated())
      {
         this.callAuthCallback();
      }
      else
      {
         this.displayPrompt();
      }
   },

   /**
    * Displays a modal dialogue to collect the user's Cloud authentication credentials
    * Triggers functions to store details on the server and the current cached version.
    *
    * @method displayPrompt
    */
   displayPrompt: function cloudAuth_displayPrompt()
   {
      this.options.formId = this.id + "-authForm";

      // Load the gui (template) from the server
      Alfresco.util.Ajax.request(
      {
         url: Alfresco.constants.URL_SERVICECONTEXT + "cloud/cloud-auth-form" + "?htmlid=" + this.options.formId,
         successCallback:
         {
            fn: this.onTemplateLoaded,
            scope: this
         },
         failureMessage: "Could not load html template for cloud authentication",
         execScripts: true
      });


      // Register the ESC key to close the dialog
      this.widgets.escapeListener = new KeyListener(document,
      {
         keys: KeyListener.KEY.ESCAPE
      },
      {
         fn: this.onCancel,
         scope: this,
         correctScope: true
      });

   },

   onTemplateLoaded: function cloudAuth_onTemplateLoaded(response)
   {
      // Inject the template from the XHR request into a new DIV element
      var containerDiv = document.createElement("div");
      containerDiv.innerHTML = response.serverResponse.responseText;

      // Create the panel from the HTML returned in the server response
      this.widgets.panel = Alfresco.util.createYUIPanel(containerDiv,
      {
         width:"498px"
      });
      this._showPanel();
      this.setUpForm();
   },

   /**
    * Prepares the gui and shows the panel.
    *
    * @method _showPanel
    * @params error {Boolean}
    * @private
    */
   _showPanel: function cloudAuth__showPanel(error)
   {

      // Enable the Esc key listener
      this.widgets.escapeListener.enable();

      // Show the panel
      this.widgets.panel.show();

      if(error)
      {
         Dom.addClass(Dom.get(this.options.formId + "-form"), "show-error");
      }
   },

   /**
    * Calls AuthCallback
    *
    * A util method to ensure the auth callback is called with correct context
    */
   callAuthCallback: function cloudAuth_callAuthCallback()
   {
      this.options.authCallback.apply(this.options.authCallbackContext);
   },

   /**
    *
    * Set up the form and defines for actions.
    * Called when the webscript is loaded & panel containing it is displayed.
    */
   setUpForm: function cloudAuth_setUpForm()
   {

      // Buttons
      this.widgets.ok = Alfresco.util.createYUIButton(this, "authForm-button-ok", null,
      {
         type: "submit"
      });

      this.widgets.cancel = Alfresco.util.createYUIButton(this, "authForm-button-cancel", this.onCancel);

      // Form definition
      var form = new Alfresco.forms.Form(this.id + "-authForm-form");
      form.setSubmitElements(this.widgets.ok);
      form.setSubmitAsJSON(true);
      form.setAJAXSubmit(true,
      {
         successCallback:
         {
            fn: this.onFormSubmitSuccess,
            scope: this
         },
         failureCallback:
         {
            fn: this.onFormSubmitFailure,
            scope: this
         }
      });

      // Let the user know that auth details are being checked:
      form.doBeforeFormSubmit =
      {
         fn: function cloudAuth_onBeforeFormSubmit()
         {
            Alfresco.util.PopupManager.displayMessage(
            {
               text: Alfresco.util.message("label.validating")
            });
         },
         scope: this
      };

      form.init();
   },

   onCancel: function cloudAuth_onCancel()
   {
     // clean up:
      this.destroy();
   },

   onFormSubmitSuccess: function cloudAuth_onFormSubmitSuccess(response)
   {
      // Return depends on response. A 200 doesn't always mean success.
      // Login is only valid if the server could be contacted
      if (response.json.loginValid === true)
      {
         // Fire event to inform any listening components that the data is ready
         YAHOO.Bubbling.fire("authDetailsAvailable",
         {
          authDetails: response.json
         });
         this.callAuthCallback();

         //ALF-19795 fix. If login is valid then hide the Cloud login form.
         this.widgets.panel.destroy();
      } else if (response.json.loginValid === false && response.json.remoteSystemAvailable === true)
      {
         // If the server was available and the log in is false - the credentials are wrong:
         this._showPanel(true);
      } else
      {
         // Otherwise - something else went wrong (probably unable to connect - otherwise it wouldn't have been a 200)
         this.onFormSubmitFailure(response);
      }
   },

   onFormSubmitFailure: function cloudAuth_onFormSubmitFailure(response)
   {
      Alfresco.util.PopupManager.displayMessage(
      {
         text: Alfresco.util.message("label.validationError")
      });
   }


   });

   /* Dummy instance to load optional YUI components early */
   var dummyInstance = new Alfresco.module.CloudAuth("null");
})();