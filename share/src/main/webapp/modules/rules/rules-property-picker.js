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
 * Rules Property Picker.
 * 
 * @namespace Alfresco.module
 * @class Alfresco.module.RulesPropertyPicker
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;
   
   Alfresco.module.RulesPropertyPicker = function(htmlId)
   {
      // Call super class constructor
      Alfresco.module.RulesPropertyPicker.superclass.constructor.call(this, htmlId);

      // Re-register with our own name
      this.name = "Alfresco.module.RulesPropertyPicker";
      Alfresco.util.ComponentManager.reregister(this);

      // Instance properties
      this.preferencesService = new Alfresco.service.Preferences();

      // Instance variables
      this.rulePropertySettings = {};

      // Merge options
      this.options = YAHOO.lang.merge(this.options, Alfresco.util.deepCopy(Alfresco.module.RulesPropertyPicker.superclass.options,
      {
         copyFunctions: true
      }));

      // Override options to add an extra "Show in menu" column
      var me = this;
      this.options.dataTableColumnDefinitions.push(
      {
         key: "item",
         sortable: false,
         width: 100,
         formatter: function (elCell, oRecord, oColumn, oData)
         {
            // Make sure we call the renderer with a scope set to the component (rather than the datatable)
            me._formatShowInMenu(elCell, oRecord, oColumn, oData);
         }
      });

      return this;
   };


   /**
   * Alias to self
   */
   var RPP = Alfresco.module.RulesPropertyPicker;

   /**
   * View Mode Constants
   */
   YAHOO.lang.augmentObject(RPP,
   {
      /**
       * Says that property shall be visible in default menu.
       *
       * @property SHOW
       * @type string
       * @final
       * @default "show"
       */
      PROPERTY_SHOW: "show",

      /**
       * Says that property shall be hidden in default menu.
       *
       * @property HIDE
       * @type string
       * @final
       * @default "hide"
       */
      PROPERTY_HIDE: "hide"
   }),

   YAHOO.extend(Alfresco.module.RulesPropertyPicker, Alfresco.module.PropertyPicker,
   {

      /**
       * Object container for initialization options
       */
      options:
      {
         /**
          * The extra template to get i18n messages
          *
          * @property rulesPropertyPickerTemplateUrl
          * @type string
          * @default Alfresco.constants.URL_SERVICECONTEXT + "modules/rules/property-picker"
          */
         rulesPropertyPickerTemplateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/rules/property-picker"
      },

      /**
       * Preferences service used to tore which properties that shall be showed or hidden from the menu.
       *
       * @property preferencesService
       * @type {Alfresco.service.Preferences}
       */
      preferencesService: null,

      /**
       * The users rule property settings that decides which properties
       * that should be displayed as default in the condition menu.
       *
       * @property rulePropertySettings
       * @type {object}
       */
      rulePropertySettings: {},

      /**
       * Event callback when superclass' dialog template has been loaded.
       *
       * @method onTemplateLoaded
       * @override
       * @param response {object} Server response from load template XHR request
       */
      onTemplateLoaded: function RPP_onTemplateLoaded(response)
      {
         // Load the UI template, which will bring in additional i18n-messages from the server
         Alfresco.util.Ajax.request(
         {
            url: this.options.rulesPropertyPickerTemplateUrl,
            dataObj:
            {
               htmlid: this.id
            },
            successCallback:
            {
               fn: this.onRulesPropertyPickerTemplateLoaded,
               obj: response,
               scope: this
            },
            failureMessage: this.msg("message.load.template.error", this.options.rulesPropertyPickerTemplateUrl),
            execScripts: true
         });
      },

      /**
       * Event callback when this class' template has been loaded
       *
       * @method onRulesPropertyPickerTemplateLoaded
       * @override
       * @param response {object} Server response from load template XHR request
       */
      onRulesPropertyPickerTemplateLoaded: function RPP_onRulesPropertyPickerTemplateLoaded(response, superClassResponse)
      {
         // Inject the template from the XHR request into a new DIV element and
         var tmpEl = document.createElement("div");
         tmpEl.setAttribute("style", "display:none");
         tmpEl.innerHTML = response.serverResponse.responseText;

         // Load the users rules property settings before calling the super classes onTemplateLoaded method
         var prefs = this.preferencesService.get();

         // Save users rule property settings
         this.rulePropertySettings = Alfresco.util.findValueByDotNotation(prefs, "org.alfresco.share.rule.properties", {});

         // Let the original template get rendered.
         Alfresco.module.RulesPropertyPicker.superclass.onTemplateLoaded.call(this, superClassResponse);
      },

      /**
       * Gets a message from this class or the superclass
       *
       * @method msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @override
       */
      msg: function RPP_msg(messageId)
      {
         var result = Alfresco.util.message.call(this, messageId, this.name, Array.prototype.slice.call(arguments).slice(1));
         if (result == messageId)
         {
            result = Alfresco.util.message(messageId, "Alfresco.module.PropertyPicker", Array.prototype.slice.call(arguments).slice(1));
         }
         return result;
      },

      /**
       * Internal formatter for the show in menu column
       *
       * @method _formatShowInMenu
       * @method renderCellAvatar
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       * @private
       */
      _formatShowInMenu: function RPP__formatShowInMenu(elCell, oRecord, oColumn, oData)
      {
         var propertyData = oRecord.getData(),
            checkBoxEl = document.createElement("input");
         checkBoxEl.setAttribute("type", "checkbox");
         if (this.rulePropertySettings[propertyData.id] == RPP.PROPERTY_SHOW)
         {
            checkBoxEl.setAttribute("checked", "true");
         }
         else
         {
            checkBoxEl.removeAttribute("checked");
         }
         Event.addListener(checkBoxEl, "change", this.onCheckBoxClick,
         {
            checkBoxEl: checkBoxEl,
            propertyData: propertyData
         }, this);
         elCell.appendChild(checkBoxEl);
      },

      /**
       * Called when the user toggles the "Show in menu" checkbox
       *
       * @method onCheckBoxClick
       * @param p_oEvent THe change event
       * @param p_oObj The data object from the row
       * @private
       */
      onCheckBoxClick: function RPP__formatShowInMenu(p_oEvent, p_oObj)
      {
         // Disable checkbox
         p_oObj.checkBoxEl.setAttribute("disabled", "true");

         // Save the new state on obj that is passed around
         p_oObj.state = p_oObj.checkBoxEl.checked ? RPP.PROPERTY_SHOW : RPP.PROPERTY_HIDE;
         this.rulePropertySettings[p_oObj.propertyData.id] = p_oObj.state;

         var responseConfig =
         {
            failureCallback:
            {
               fn: function(p_oResponse, p_oObj)
               {
                  // Display error message
                  var propertyData = p_oObj.propertyData;
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     text: this.msg("message.addFavouriteProperty.failure", propertyData.label)
                  });

                  // Enable checkbox and reset values it to its previous state
                  p_oObj.checkBoxEl.removeAttribute("disabled");
                  if (p_oObj.checkBoxEl.checked)
                  {
                     this.rulePropertySettings = RPP.PROPERTY_HIDE;                      
                     p_oObj.checkBoxEl.removeAttribute("checked");
                  }
                  else
                  {
                     this.rulePropertySettings = RPP.PROPERTY_SHOW;
                     p_oObj.checkBoxEl.setAttribute("checked", "true");
                  }

               },
               obj: p_oObj,
               scope: this
            },
            successCallback:
            {
               fn: function(p_oResponse, p_oObj)
               {
                  // Enable checkbox again and tell other components about the change
                  p_oObj.checkBoxEl.removeAttribute("disabled");
                  YAHOO.Bubbling.fire("rulePropertySettingsChanged",
                  {
                     property: p_oObj.propertyData,
                     state: p_oObj.state
                  });
               },
               scope: this,
               obj: p_oObj
            }
         };

         // Add or remove property as a favourite
         this.preferencesService.set(Alfresco.service.Preferences.RULE_PROPERTY_SETTINGS + "." + p_oObj.propertyData.id,
               p_oObj.state, responseConfig);
      },

      /**
       * Internal show dialog function
       * @method _showDialog
       * @protected
       * @override
       */
      _showDialog: function RPP__showDialog()
      {
         // Add class so we can override styles in css
         Dom.addClass(this.widgets.dialog.body.parentNode, "rules-property-picker");

         // Show dialog as usual
         Alfresco.module.RulesPropertyPicker.superclass._showDialog.call(this);
      }

   });

   /* Dummy instance to load optional YUI components early */
   var dummyInstance = new Alfresco.module.RulesPropertyPicker("null");
})();
