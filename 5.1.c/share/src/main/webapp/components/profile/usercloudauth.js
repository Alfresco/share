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
 * User Cloud Auth component.
 * 
 * @namespace Alfresco
 * @class Alfresco.UserCloudAuth
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;
      
   /**
    * UserCloudAuth constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.UserCloudAuth} The new UserNotifications instance
    * @constructor
    */
   Alfresco.UserCloudAuth = function(htmlId)
   {
      Alfresco.UserCloudAuth.superclass.constructor.call(this, "Alfresco.UserCloudAuth", htmlId, ["button"]);
      return this;
   };
   
   YAHOO.extend(Alfresco.UserCloudAuth, Alfresco.component.Base,
   {
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function UCA_onReady()
      {
         this.widgets.signIn = Alfresco.util.createYUIButton(this, "button-signIn", this.onSignInClick);
         this.widgets.edit = Alfresco.util.createYUIButton(this, "button-edit", this.onEditSettings);
         this.widgets.remove  = Alfresco.util.createYUIButton(this, "button-delete", this.onDeleteSettings);

         if(!this.modules.cloudAuth)
         {
            this.modules.cloudAuth = new Alfresco.module.CloudAuth(this.id + "cloudAuth");
         }

         this.modules.cloudAuth.setOptions(
         {
            authCallback: this.onSuccess,
            authCallbackContext: this
         });

      },

      onSignInClick: function UCA_onSignInClick(e, p_obj)
      {
         this.modules.cloudAuth.displayPrompt();
      },

      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */
      
      /**
       * Save Changes form submit success handler
       *
       * @method onSuccess
       * @param response {object} Server response object
       */
      onSuccess: function UCA_onSuccess(response)
      {
         Alfresco.util.PopupManager.displayMessage(
         {
            text: Alfresco.util.message("message.valid", this.name)
         });
         YAHOO.lang.later(1000, this, function()
         {
            // Reload the page:
            window.location.reload();
         });
      },

      /**
       * Load the edit screen
       *
       */
      onEditSettings: function UCA_onEditSettings(e, p_obj)
      {
         this.onSignInClick(e, p_obj);
      },

      /**
       * Load the delete screen
       *
       */
      onDeleteSettings: function UCA_onDeleteSettings(e, p_obj)
      {
         var me = this;

         Alfresco.util.PopupManager.displayPrompt(
         {
            title: Alfresco.util.message("confirm.delete.title", this.name),
            text: Alfresco.util.message("confirm.delete.message", this.name),
            buttons: [
            {
               text: this.msg("button.delete"),
               handler: function UCA_onDeleteSettings_confirm()
               {
                  // Remove the prompt
                  this.destroy();

                  // Post the delete
                  Alfresco.util.Ajax.jsonDelete(
                  {
                     url: Alfresco.constants.PROXY_URI + "cloud/person/credentials",
                     successCallback:
                     {
                        fn: function UCA_onDeleteSettings_confirm_success(response)
                        {
                           // succesfully deleted details - refresh page
                           Alfresco.util.PopupManager.displayMessage(
                           {
                              text: Alfresco.util.message("message.delete.success", me.name)
                           });
                           YAHOO.lang.later(1000, me, function()
                           {
                              // Reload the page:
                              window.location.reload();
                           });
                        },
                        scope: me
                     },
                     failureMessage: Alfresco.util.message("message.delete.error", me.name)
                  });
               }
            },
            {
               text: this.msg("button.cancel"),
               handler: function UCA_onDeleteSettings_cancel()
               {
                  this.destroy();
               },
               isDefault: true
            }]
         });

      }
   });
})();