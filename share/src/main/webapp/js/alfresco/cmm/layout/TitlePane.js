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
 * @module cmm/layout/TitlePane
 * @extends external:dijit/TitlePane
 * @mixes alfresco/core/Core
 * @mixes alfresco/core/CoreWidgetProcessing
 * @author Richard Smith
 */
define(["dojo/_base/declare",
        "dijit/TitlePane",
        "alfresco/core/Core",
        "alfresco/core/CoreWidgetProcessing",
        "dojo/_base/lang",
        "dojo/dnd/Moveable",
        "dojo/dom",
        "dojo/dom-class",
        "dojo/dom-geometry",
        "dojo/dom-style",
        "dojo/text!./templates/TitlePane.html"],
        function(declare, TitlePane, AlfCore, CoreWidgetProcessing, lang, Moveable, dom, domClass, domGeom, 
              domStyle, template) {

   return declare([TitlePane, AlfCore, CoreWidgetProcessing], {

      /**
       * The array of file(s) containing internationalised strings.
       *
       * @instance
       * @type {object}
       * @default [{i18nFile: "./i18n/TitlePane.properties"}]
       */
      i18nRequirements: [{i18nFile: "./i18n/TitlePane.properties"}],
      
      /**
       * An array of the CSS files to use with this widget
       * 
       * @instance
       * @type {object[]}
       * @default [{cssFile:"./css/TitlePane.css"}]
       */
      cssRequirements: [{cssFile:"./css/TitlePane.css"}],

      /**
       * The HTML template to use for the widget
       * 
       * @instance
       * @type {string}
       */
      templateString: template,

      /**
       * The name of the file to use for the close image. This is expected to be in the css/images relative path
       * of the widget.
       *
       * @instance
       * @type {string}
       * @default "close-16.png"
       */
      closeImg: "close-16.png",

      /**
       * The message to display as alt text for the close image. This will also be displayed as a title on the
       * image.
       *
       * @instance
       * @type {string}
       * @default "titlePane.close.alt.text"
       */
      closeAltText: "titlePane.close.alt.text",

      /**
       * Should this TitlePane be moveable?
       *
       * @instance
       * @type {boolean}
       * @default false
       */
      moveable: false,
      
      /**
       * Right margin for auto positioning
       *
       * @instance
       * @type {integer}
       * @default 33
       */
      autoPositionRightMargin: 33,

      /**
       * Widgets to be processed into the containerNode
       * 
       * @instance
       * @type {Object[]}
       * @default null 
       */
      widgetsContent: null,
      
      /**
       * Sets up images and translations for alt text and titles.
       * 
       * @instance
       */
      postMixInProperties: function cmm_layout_TitlePane__postMixInProperties() {
         this.closeImageSrc = require.toUrl("cmm/layout") + "/css/images/" + this.closeImg;
         this.closeAltText = this.encodeHTML(this.message(this.closeAltText));
      },

      /**
       * Extends the superclass implementation to process the widgets defined by 
       * [widgetsContent]{@link module:cmm/layout/TitlePane#widgetsContent} into the 
       * containerNode of the TitlePane. It also optionally adds some additional css 
       * and sets up the domNode to be moveable if requested.
       * 
       * @instance
       */
      postCreate: function cmm_layout_TitlePane__postCreate() {
         this.inherited(arguments);

         // Add in any additional CSS classes...
         if (this.additionalCssClasses)
         {
            domClass.add(this.domNode, this.additionalCssClasses);
         }

         if(this.widgetsContent)
         {
            this.processWidgets(this.widgetsContent, this.containerNode);
         }

         if(this.moveable)
         {
            this._mover = new Moveable(this.domNode);
            domClass.add(this.domNode, "moveable");
         }
      },

      /**
       * Repositions the domNode against a specified container if overflowing.
       * 
       * @instance
       * @param {string} positionContainer The container against which this should be positioned.
       */
      autoPosition: function cmm_layout_TitlePane__autoPosition(positionContainer) {
         if(positionContainer)
         {
            if(container = dom.byId(positionContainer))
            {
               var containerDims = domGeom.position(container, false),
                   paneDims = domGeom.position(this.domNode, false);
               
               if((paneDims.x + paneDims.w) > (containerDims.x + containerDims.w - this.autoPositionRightMargin))
               {
                  domStyle.set(this.domNode, "left", (containerDims.w - paneDims.w - this.autoPositionRightMargin) + "px");
               }
            }
         }
         this.alfPublish("ALF_WIDGET_PROCESSING_COMPLETE", {}, true);
      },

      /**
       * Destroy the widget to close it.
       * 
       * @instance
       * @param {object} evt The click event that triggers the close.
       */
      onClose: function cmm_layout_TitlePane__onClose(/* jshint unused:false */ evt) {
         this.destroy();
         this.destroyRecursive();
      }
   });
});