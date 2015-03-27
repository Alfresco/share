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
 * User Notifications component.
 * 
 * @namespace Alfresco
 * @class Alfresco.UserNotifications
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;
      
   /**
    * UserNotifications constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.UserNotifications} The new UserNotifications instance
    * @constructor
    */
   Alfresco.UserNotifications = function(htmlId)
   {
      Alfresco.UserNotifications.superclass.constructor.call(this, "Alfresco.UserNotifications", htmlId, ["button"]);
      return this;
   }
   
   YAHOO.extend(Alfresco.UserNotifications, Alfresco.component.Base,
   {
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function UP_onReady()
      {
         // Reference to self used by inline functions
         var me = this;
         
         // Buttons
         this.widgets.ok = Alfresco.util.createYUIButton(this, "button-ok", null,
            {
               type: "submit"
            });
         this.widgets.cancel = Alfresco.util.createYUIButton(this, "button-cancel", this.onCancel);
         
         // Form definition
         var form = new Alfresco.forms.Form(this.id + "-form");
         form.setSubmitElements(this.widgets.ok);
         form.setSubmitAsJSON(true);
         form.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onSuccess,
               scope: this
            }
         });
         
         // Initialise the form
         form.init();
         
         // Finally show the main component body here to prevent UI artifacts on YUI button decoration
         Dom.setStyle(this.id + "-body", "display", "block");
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
      onSuccess: function UP_onSuccess(response)
      {
         if (response && response.json)
         {
            if (response.json.success)
            {
               // succesfully updated details - refresh back to the user profile main page
               Alfresco.util.PopupManager.displayMessage(
               {
                  text: Alfresco.util.message("message.success", this.name)
               });
               this.navigateToProfile();
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
               text: Alfresco.util.message("message.failure", this.name)
            });
         }
      },
      
      /**
       * Cancel Changes button click handler
       *
       * @method onCancel
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onCancel: function UP_onCancel(e, p_obj)
      {
         this.navigateToProfile();
      },
      
      /**
       * Perform URL navigation back to user profile main page
       * 
       * @method navigateToProfile
       */
      navigateToProfile: function UP_navigateToProfile()
      {
         var pageIndex = document.location.href.lastIndexOf('/');
         document.location.href = document.location.href.substring(0, pageIndex + 1) + "profile";
      }
   });
})();