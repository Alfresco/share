/**
 * Copyright (C) 2005-2015 Alfresco Software Limited.
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
 * WelcomePreference component.
 *
 * @namespace Alfresco
 * @class Alfresco.WelcomePreference
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * Alfresco.WelcomePreference constructor.
    *
    * @param {string} htmlId The HTML id of the parent element
    * @return {Alfresco.WelcomePreference} The new WelcomePreference instance
    * @constructor
    */
   Alfresco.WelcomePreference = function(htmlId)
   {
      Alfresco.WelcomePreference.superclass.constructor.call(this, "Alfresco.WelcomePreference", htmlId, ["button", "container", "datasource"]);
      return this;
   };

   YAHOO.extend(Alfresco.WelcomePreference, Alfresco.component.Base,
   {

      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: {},

      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
      },

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.WelcomePreference} returns 'this' for method chaining
       */
      setOptions: function WP_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },

      /**
       * Set messages for this module.
       *       
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.WelcomePreference} returns 'this' for method chaining
       */
      setMessages: function WP_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onReady
       */
      onReady: function WP_onReady()
      {
         // Shortcut for dummy instance
         if (this.id === null)
         {
            return;
         }

         // Save reference to buttons so we can change label and such later
         this.widgets.saveButton = Alfresco.util.createYUIButton(this, "save-button", this.onSaveButtonClick);
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel-button", this.onCancelButtonClick);
      },

      /**
       * Fired when the user clicks save button.
       * Shows the layout list.
       *
       * @method onSaveButtonClick
       * @param event {object} an "click" event
       */
      onSaveButtonClick: function WP_onSaveButtonClick(event)
      {
         var welcomePanelEnabledValue = Dom.get(this.id + "-welcomePanelEnabled").checked;
         Alfresco.util.Ajax.jsonPost(
         {
            url: Alfresco.constants.URL_SERVICECONTEXT + "components/dashboard/welcome-preference",
            dataObj:
            {
               welcomePanelEnabled: welcomePanelEnabledValue
            },
            successMessage: this.msg("message.saveSuccess"),
            failureMessage: this.msg("message.saveFailure")
         });
      },

      /**
       * Fired when the user clicks cancel button
       *
       * @method onCancelButtonClick
       * @param event {object} an "click" event
       */
      onCancelButtonClick: function WP_onCancelButtonClick(event)
      {
         Dom.get(this.id + "-welcomePanelEnabled").checked = this.options.welcomePanelEnabled;
         Dom.get(this.id + "-welcomePanelDisabled").checked = !this.options.welcomePanelEnabled;
      }
   });
})();

/* Dummy instance to load optional YUI components early */
new Alfresco.WelcomePreference(null);
