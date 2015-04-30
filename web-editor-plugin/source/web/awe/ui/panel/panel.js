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

(function()
{
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event,
       KeyListener = YAHOO.util.KeyListener,
       Selector = YAHOO.util.Selector,
       Bubbling = YAHOO.Bubbling;

   YAHOO.namespace('org.alfresco.awe.ui.Panel');

   YAHOO.org.alfresco.awe.ui.Panel = function AWE_Panel_constructor(name, containerId, components)
   {
      var instance = Alfresco.util.ComponentManager.get(this.id);
      if (instance !== null) 
      {
         throw new Error("An instance of ' + name + ' already exists.");
      }

      YAHOO.org.alfresco.awe.ui.Panel.superclass.constructor.call(this, name, containerId, ["button", "container", "connection", "selector", "json"]);
      this.init();
      return this;
   };

   YAHOO.extend(YAHOO.org.alfresco.awe.ui.Panel, Alfresco.component.Base, 
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       * @default {}
       */
      options: 
      {
         /**
          * Flag denoting whether to destroy panel after panel is hidden,
          * forcing a reload of panel template if shown again
          *
          * @type boolean
          *
          */
         destroyPanelOnHide: true,

         /**
          * Flag denoting whether to grab focus on first input element when
          * panel is shown
          *
          * @type boolean
          */
         focusFirstInputElement: true
      },

      /**
       *
       */
      init: function AWE_Panel_init()
      {
      },

      /**
       * Shows the login dialog to the user.
       *
       * @method show
       */
      show: function AWE_Panel_show(callback)
      {
         // set callback object reference if specified
         // it will be called on success.
         if (callback) 
         {
            this.callback = callback;
         }

         if (this.widgets.panel) 
         {
            /**
             * The panel gui has been showed before and its gui has already
             * been loaded and created
             */
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
               url: this.options.templateUrl,
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
               failureMessage: "Could not load create " + this.name + " template"
            });
         }
      },

      /**
       * function AwePanel_hide
       *
       */
      hide: function Awe_Panel_hide()
      {
         this.widgets.panel.hide();
      },
	  
      /**
       * function AwePanel_destroy
       *
       */
      destroy: function Awe_Panel_destroy()
      {
         if (this.options.destroyPanelOnHide)
         {
            this.widgets.panel.destroy();
            this.widgets.panel = null;
         }
      },

      /**
       * Prepares the gui and shows the panel.
       *
       * @method _showPanel
       * @private
       */
      _showPanel: function Awe_Panel__showPanel()
      {
         // Show the upload panel
         this.widgets.panel.show();
         
         Alfresco.util.YUILoaderHelper.loadComponents(true);

         // Firefox insertion caret fix
         Alfresco.util.caretFix(this.id + "-form");

         // Set the focus on the first field
         this.widgets.panel.focusFirst();
      }
   });
})();

WEF.register("org.alfresco.awe.ui.panel", YAHOO.org.alfresco.awe.ui.Panel, {version: "1.0", build: "1"}, YAHOO);