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
 * This the simple style form control. It extends the [BaseFormControl]
 * {@link module:alfresco/forms/controls/BaseFormControl} and supports the standard
 * form control configuration.
 * 
 * @module cmm/forms/controls/SimpleStyle
 * @extends module:alfresco/forms/controls/BaseFormControl
 * @author Richard Smith
 */
define(["alfresco/forms/controls/BaseFormControl",
        "dojo/_base/declare",
        "alfresco/core/CoreWidgetProcessing",
        "dijit/form/ToggleButton",
        "cmm/forms/controls/ColorButton",
        "dojo/_base/lang",
        "dojo/dom-class",
        "dijit/registry"], 
        function(BaseFormControl, declare, CoreWidgetProcessing, ToggleButton, ColorButton, lang, domClass, Registry) {
   
   return declare([BaseFormControl, CoreWidgetProcessing], {

      /**
       * An array of the i18n files to use with this widget.
       *
       * @instance
       * @type {Array}
       */
      i18nRequirements: [{i18nFile: "./i18n/SimpleStyle.properties"}],
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {object[]}
       * @default [{cssFile:"./css/SimpleStyle.css"}]
       */
      cssRequirements: [{cssFile:"./css/SimpleStyle.css"}],
      
      /**
       * The css string to use for bold selection.
       * 
       * @instance
       * @type {string}
       * @default "font-weight:bold;"
       */
      _cssForBold: "font-weight:bold;",

      /**
       * The css string to use for italic selection.
       * 
       * @instance
       * @type {string}
       * @default "font-style:italic;"
       */
      _cssForItalic: "font-style:italic;",

      /**
       * The css string to use for underline selection.
       * 
       * @instance
       * @type {string}
       * @default "text-decoration:underline;"
       */
      _cssForUnderline: "text-decoration:underline;",

      /**
       * The css string to use for colour selection.
       * 
       * @instance
       * @type {string}
       * @default "color:*;"
       */
      _cssForColor: "color:*;",

      /**
       * The css string to use for background colour selection.
       * 
       * @instance
       * @type {string}
       * @default "background-color:*;"
       */
      _cssForBackgroundColor: "background-color:*;",

      /**
       * The style widgets.
       * 
       * @instance
       * @type {array}
       * @default null
       */
      widgetsForControl: null,
      
      /**
       * The default inner control states.
       * 
       * @instance
       * @type {object}
       */
      _defControlState: {
         bold: false,
         italic: false,
         underline: false,
         color: "",
         background: ""
      },

      /**
       * @instance
       */
      getWidgetConfig: function cmm_forms_controls_SimpleStyle__getWidgetConfig() {
         // Return the configuration for the widget
         return {
            id : this.generateUuid(),
            name: this.name
         };
      },
      
      /**
       * @instance
       */
      createFormControl: function cmm_forms_controls_SimpleStyle__createFormControl(config, /*jshint unused:false*/ domNode) {

         _this = this;

         // Copy the defControlState
         this.controlState = lang.clone(this._defControlState);
         
         // Add in a control specific class
         domClass.add(this.domNode, "cmm-forms-controls-SimpleStyle" + 
            (this.additionalCssClasses ? " " + this.additionalCssClasses : "")
         );

         // Create the control widgets if required
         var widgetsForControl = null;
         if (this.widgetsForControl === null)
         {
            widgetsForControl = [
               {
                  id: this.fieldId + "_bold",
                  name: "dijit/form/ToggleButton",
                  config: {
                     label: this.message("button.label.bold"),
                     showLabel: false,
                     iconClass: "bold",
                     onChange: function(val){
                        _this.onValueSet({
                           property: "bold",
                           value: val
                        });
                     }
                  }
               },
               {
                  id: this.fieldId + "_italic",
                  name: "dijit/form/ToggleButton",
                  config: {
                     label: this.message("button.label.italic"),
                     showLabel: false,
                     iconClass: "italic",
                     onChange: function(val){
                        _this.onValueSet({
                           property: "italic",
                           value: val
                        });
                     }
                  }
               },
               {
                  id: this.fieldId + "_underline",
                  name: "dijit/form/ToggleButton",
                  config: {
                     label: this.message("button.label.underlined"),
                     showLabel: false,
                     iconClass: "underlined",
                     onChange: function(val){
                        _this.onValueSet({
                           property: "underline",
                           value: val
                        });
                     }
                  }
               },
               {
                  name: "dijit/ToolbarSeparator"
               },
               {
                  id: this.fieldId + "_color",
                  name: "cmm/forms/controls/ColorButton",
                  config: {
                     label: this.message("button.label.color"),
                     showLabel: false,
                     iconClass: "color",
                     onChange: function(val){
                        _this.onValueSet({
                           property: "color",
                           value: val
                        });
                     }
                  }
               },
               {
                  id: this.fieldId + "_background",
                  name: "cmm/forms/controls/ColorButton",
                  config: {
                     label: this.message("button.label.background"),
                     showLabel: false,
                     iconClass: "background",
                     onChange: function(val){
                        _this.onValueSet({
                           property: "background",
                           value: val
                        });
                     }
                  }
               }
            ];
         }
         else
         {
            // If widgetsForControl has been defined then use it...
            widgetsForControl = lang.clone(this.widgetsForControl);
            this.processObject(["processInstanceTokens"], widgetsForControl);
         }

         // Process into the control node
         return this.processWidgets(widgetsForControl, this._controlNode);
         
      },
      
      /**
       * Gets the value of the control.
       * 
       * @instance
       * @returns {string} The composed output value of the control
       */
      getValue: function cmm_forms_controls_SimpleStyle__getValue() {
         var value = this._composeValue();
         this.alfLog("log", "Returning value for field: '" + this.name + "': ", value);
         return value;
      },
      
      /**
       * Called by individual child controls to update the state of the parent
       * 
       * @instance
       * @param {object} payload
       */
      onValueSet: function cmm_forms_controls_SimpleSelect__onValueSet(payload) {
         this.controlState[lang.getObject("property", false, payload)] = lang.getObject("value", false, payload);
         var newValue = this._composeValue();
         this.onValueChangeEvent(this.name, this.lastValue, newValue);
         this.lastValue = newValue;
      },
      
      /**
       * Sets the various value properties in the control.
       * 
       * @instance
       * @param {object} value The value to set.
       */
      setValue: function cmm_forms_controls_SimpleSelect__setValue(value) {
         if (this.deferValueAssigment)
         {
            this.inherited(arguments);
         }
         else
         {
            this.alfLog("log", "Setting field: '" + this.name + "' with value: ", value);

            this.controlState = lang.clone(this._defControlState);
            
            value.split(";").forEach(function (item) {
               if(item != "")
               {
                  var keyVal = item.split(":");
                  switch(keyVal[0]) {

                     case "font-weight":
                        this.controlState.bold = keyVal[1] === "bold";
                        break;

                     case "font-style":
                        this.controlState.italic = keyVal[1] === "italic";
                        break;
                     
                     case "text-decoration":
                        this.controlState.underline = keyVal[1] === "underline";
                        break;
                     
                     case "color":
                        this.controlState.color = keyVal[1];
                        break;
                     
                     case "background-color":
                        this.controlState.background = keyVal[1];
                        break;

                     default:
                        
                        break;
                  }
               }
            }, this);

            // Apply the controlStates to the actual controls
            this._setControlStates();
         }
      },
      
      /**
       * There is no wrapped widget so we override this function
       * 
       * @instance
       */
      placeWidget: function cmm_forms_controls_SimpleSelect__placeWrappedWidget() {
      },

      /**
       * There is no wrapped widget so we override this function
       * 
       * @instance
       */
      setupChangeEvents: function cmm_forms_controls_SimpleSelect__setupChangeEvents() {
      },
      
      /**
       * Compose the output value of the control
       * 
       * @instance
       * @returns {string} The composed output value of the control
       */
      _composeValue: function cmm_forms_controls_SimpleSelect___composeValue() {
         var value = "";
         if(this.controlState.bold)
         {
            value = value + this._cssForBold;
         }
         if(this.controlState.italic)
         {
            value = value + this._cssForItalic;
         }
         if(this.controlState.underline)
         {
            value = value + this._cssForUnderline;
         }
         if(this.controlState.color)
         {
            value = value + this._cssForColor.replace("*", this.controlState.color);
         }
         if(this.controlState.background)
         {
            value = value + this._cssForBackgroundColor.replace("*", this.controlState.background);
         }
         return value;
      },

      /**
       * Set the states of the individual controls
       * 
       * @instance
       */
      _setControlStates: function cmm_forms_controls_SimpleSelect___setControlStates() {
         Registry.byId(this.fieldId + "_bold").set("checked", this.controlState.bold);
         Registry.byId(this.fieldId + "_italic").set("checked", this.controlState.italic);
         Registry.byId(this.fieldId + "_underline").set("checked", this.controlState.underline);
         Registry.byId(this.fieldId + "_color").set("value", this.controlState.color);
         Registry.byId(this.fieldId + "_background").set("value", this.controlState.background);
      }
      
   });
});