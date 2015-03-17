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
 * Rules "Checkin" Action module.
 *
 * @namespace Alfresco.module
 * @class Alfresco.module.RulesCheckinAction
 */
(function()
{
   /**
   * YUI Library aliases
   */
   var Dom = YAHOO.util.Dom,
      KeyListener = YAHOO.util.KeyListener;

   /**
    * Alfresco Slingshot aliases
    */
    var $html = Alfresco.util.encodeHTML;

   Alfresco.module.RulesCheckinAction = function(htmlId)
   {
      Alfresco.module.RulesCheckinAction.superclass.constructor.call(this, "Alfresco.module.RulesCheckinAction", htmlId, ["button", "container", "connection"]);
      return this;
   };

   YAHOO.extend(Alfresco.module.RulesCheckinAction, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       */
      options:
      {
         /**
          * Template URL
          *
          * @property templateUrl
          * @type string
          * @default Alfresco.constants.URL_SERVICECONTEXT + "modules/rules/actions/checkin"
          */
         templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/rules/actions/checkin"
      },


      /**
       * Container element for template in DOM.
       *
       * @property containerDiv
       * @type HTMLElement
       */
      containerDiv: null,

      /**
       * Main entry point
       * @method showDialog
       * @param checkinConfig {object} Data to fill the form with
       *        checkinConfig.version {string} ["minor"|"version"|null]
       *        checkinConfig.comments {string}
       */
      showDialog: function RCIA_showDialog(checkinConfig)
      {
         if (!this.containerDiv)
         {
            // Load the UI template from the server
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
                  obj: checkinConfig,
                  scope: this
               },
               failureMessage: "Could not load template:" + this.options.templateUrl,
               execScripts: true
            });
         }
         else
         {
            // Show the dialog
            this._showDialog(checkinConfig);
         }
      },

      /**
       * Event callback when dialog template has been loaded
       *
       * @method onTemplateLoaded
       * @param response {object} Server response from load template XHR request
       * @param checkinConfig {object} Data to fill the form with
       */
      onTemplateLoaded: function RCIA_onTemplateLoaded(response, checkinConfig)
      {
         // Inject the template from the XHR request into a new DIV element
         this.containerDiv = document.createElement("div");
         this.containerDiv.setAttribute("style", "display:none");
         this.containerDiv.innerHTML = response.serverResponse.responseText;

         // The panel is created from the HTML returned in the XHR request, not the container
         var dialogDiv = Dom.getFirstChild(this.containerDiv);

         // Create and render the YUI dialog
         this.widgets.dialog = Alfresco.util.createYUIPanel(dialogDiv);

         // Buttons (note: ok buttons click will be handled in forms onBeforeAjaxSubmit)
         this.widgets.okButton = Alfresco.util.createYUIButton(this, "ok-button", null,
         {
            type: "submit"
         });
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel-button", this.onCancelClick);

         // Configure the forms runtime
         var form = new Alfresco.forms.Form(this.id + "-form");
         this.widgets.form = form;

         // ...and has a maximum length
         form.addValidation(this.id + "-comments", Alfresco.forms.validation.length,
         {
            max: 256,
            crop: true
         }, "keyup");

         form.setSubmitElements(this.widgets.okButton);

         // Stop the form from being submitted and fire and event from the collected information
         form.doBeforeAjaxRequest =
         {
            fn: function(p_config, p_obj)
            {
               // Fire event so other component know
               YAHOO.Bubbling.fire("checkinConfigCompleted",
               {
                  options:
                  {
                     version: p_config.dataObj.version,
                     comments: p_config.dataObj.comments
                  },
                  eventGroup: this
               });

               this.widgets.dialog.hide();

               // Return false so the form isn't submitted
               return false;
            },
            obj: null,
            scope: this
         };

         // We're in a popup, so need the tabbing fix
         form.applyTabFix();
         form.init();

         // Register the ESC key to close the dialog
         var escapeListener = new KeyListener(document,
         {
            keys: KeyListener.KEY.ESCAPE
         },
         {
            fn: function(id, keyEvent)
            {
               this.onCancelClick();
            },
            scope: this,
            correctScope: true
         });
         escapeListener.enable();

         // Show the dialog
         this._showDialog(checkinConfig);
      },

      /**
       * Internal show dialog function
       *
       * @method _showDialog
       * @param checkinConfig {object} Data to fill the form with
       */
      _showDialog: function RCIA__showDialog(checkinConfig)
      {
         // Display form data from config
         checkinConfig = checkinConfig ? checkinConfig : {};
         var majorEl = Dom.get(this.id + "-version-major"),
            minorEl = Dom.get(this.id + "-version-minor"),
            focusEl;

         if (checkinConfig.version == "minor" || checkinConfig.version == null || checkinConfig.version == "")
         {
            minorEl.checked = true;
            focusEl = minorEl;
            majorEl.checked = false;
         }
         else if (checkinConfig.version == "major")
         {
            majorEl.checked = true;
            focusEl = majorEl;
            minorEl.checked = false;
         }         
         Dom.get(this.id + "-comments").value = checkinConfig.comments ? checkinConfig.comments : "";
         this.widgets.form.validate();

         // Show the dialog
         this.widgets.dialog.show();

         // Focus when element is visible so IE is happy
         focusEl.focus();
      },

      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * Dialog Cancel button event handler
       *
       * @method onCancelClick
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onCancelClick: function RCIA_onCancelClick(e, p_obj)
      {
         this.widgets.dialog.hide();
      }

   });

   /* Dummy instance to load optional YUI components early */
   var dummyInstance = new Alfresco.module.RulesCheckinAction("null");
})();
