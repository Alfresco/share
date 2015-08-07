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
 * This widget represents a button which provides a colour picker. It extends the [AlfButton]
 * {@link module:alfresco/buttons/AlfButton} and supports regular button functions. When 
 * clicked it opens a [ColorPickerDropDown]{@link module:cmm/forms/controls/ColorPickerDropDown}.
 * 
 * @module cmm/forms/controls/ColorButton
 * @extends module:alfresco/buttons/AlfButton
 * @mixes module:dijit/_HasDropDown
 * @author Richard Smith
 */
define(["alfresco/buttons/AlfButton",
        "dojo/_base/declare",
        "dojox/widget/ColorPicker",
        "dijit/_HasDropDown",
        "cmm/forms/controls/ColorPickerDropDown",
        "dojo/_base/lang",
        "dojo/dom-class",
        "dojo/dom-style",
        "dojo/dom-construct",
        "dojo/on"],
        function(AlfButton, declare, ColorPicker, _HasDropDown, ColorPickerDropDown, lang, domClass, domStyle, domConstruct, on) {

   return declare([AlfButton, _HasDropDown], {

      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {object[]}
       * @default [{cssFile:"./css/ColorButton.css"}]
       */
      cssRequirements: [{cssFile:"./css/ColorButton.css"}],
      
      /**
       * @instance
       */
      postCreate: function cmm_forms_controls_ColorButton__postCreate() {

         // Add a suitable class for this control and add any additionalCssClasses
         domClass.add(this.domNode, "cmm-forms-controls-ColorButton" + 
            (this.additionalCssClasses ? " " + this.additionalCssClasses : "")
         );

         // Create the colorBall - initially hidden
         this.colorBall = dojo.create("div", {
            className: "colorBall",
            style: {
               visibility: "hidden"
            }
         }, this.domNode, "first");

         // Attach a click event to the _buttonNode
         on(this._buttonNode, "click", this.openDropDown);

         // Watch the 'value'.
         // Perform colorBall updates and write the value through the onChange callback.
         this.watch("value", lang.hitch(this, function(attr, oldVal, newVal) {
            if (newVal === "")
            {
               domStyle.set(this.colorBall, "visibility", "hidden");
               this.onChange(newVal);
            }
            else if (newVal.match(/^#([0-9a-f]{3}|[0-9a-f]{6})$/i))
            {
               domStyle.set(this.colorBall, "visibility", "visible");
               domStyle.set(this.colorBall, "borderColor", newVal + " transparent");
               this.onChange(newVal);
            }
         }));
         this.inherited(arguments);
      },

      /**
       * Function to open this.dropdown
       * 
       * @instance
       */
      openDropDown: function cmm_forms_controls_ColorButton__openDropDown(/*Function*/ callback) {

         // Destroy any open dropDowns
         if (this.dropDown && typeof this.dropDown.destroyRecursive === 'function') {
            this.dropDown.destroyRecursive();
         }

         // Save the current value
         this.curVal = this.value != null ? this.value : "";

         // Launch a ColorPickerDropDown
         this.dropDown = new ColorPickerDropDown({
            parent: this,
            value: this.curVal,

            onSelect: lang.hitch(this.dropDown, function() {
               this.parent.set('value', this.picker.get('value'));
               this.parent.closeDropDown();
            }),

            onCancel: lang.hitch(this.dropDown, function() {
               this.parent.set('value', this.parent.curVal);
               this.parent.closeDropDown();
            }),

            onClear: lang.hitch(this.dropDown, function() {
               this.parent.set('value', "");
               this.parent.closeDropDown();
            })
         });

         if (typeof this.inherited === "function")
         {
            this.inherited(arguments);
         }
      },

      /**
       * Function to close and destroy this.dropdown
       * 
       * @instance
       */
      closeDropDown: function cmm_forms_controls_ColorButton__closeDropDown() {
         this.inherited(arguments);
         if (this.dropDown)
         {
            this.dropDown.destroy();
            this.dropDown = null;
         }
      }
   });
});