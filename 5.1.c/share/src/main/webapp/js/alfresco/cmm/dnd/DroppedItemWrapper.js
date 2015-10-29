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
 * This is the Dropped Item Wrapper for use in the CMM form editor.
 *
 * @module cmm/dnd/DroppedItemWrapper
 * @extends alfresco/dnd/DroppedItemWrapper
 * @author Richard Smith
 */
define(["dojo/_base/declare",
        "alfresco/dnd/DroppedItemWrapper",
        "dojo/_base/lang",
        "dojo/_base/event",
        "dojo/on",
        "dojo/dom-class",
        "dojo/dom-construct",
        "dijit/registry"], 
        function(declare, DroppedItemWrapper, lang, event, on, domClass, domConstruct, registry) {

   return declare([DroppedItemWrapper], {

      /**
       * The array of file(s) containing internationalised strings.
       *
       * @instance
       * @type {object}
       * @default [{i18nFile: "./i18n/DroppedItemWrapper.properties"}]
       */
      i18nRequirements: [{i18nFile: "./i18n/DroppedItemWrapper.properties"}],

      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {Array}
       */
      cssRequirements: [{cssFile:"./css/DroppedItemWrapper.css"}],

      /**
       * The message to display as alt text for the show image. This will also be displayed as a title on the
       * image.
       *
       * @instance
       * @type {string}
       * @default "droppedItemWrapper.show.alt.text"
       */
      showAltText: "droppedItemWrapper.show.alt.text",

      /**
       * The name of the file to use for the show image. This is expected to be in the css/images relative path
       * of the widget.
       *
       * @instance
       * @type {string}
       * @default "show-16.png"
       */
      showImg: "show-16.png",

      /**
       * The topic upon which to publish when an item selection is made.
       *
       * @instance
       * @type {string}
       * @default ""
       */
      selectPublishTopic: "",

      /**
       * The topic upon which to publish when an item focus or focusout happens.
       *
       * @instance
       * @type {string}
       * @default ""
       */
      focusPublishTopic: "",

      /**
       * Sets up images and translations for alt text and titles.
       * 
       * @instance
       */
      postMixInProperties: function cmm_dnd_DroppedItemWrapper__postMixInProperties() {
         this.inherited(arguments);
         this.showAltText = this.encodeHTML(this.message(this.showAltText));
         this.showImageSrc = require.toUrl("cmm/dnd") + "/css/images/" + this.showImg;
      },

      /**
       * @instance
       */
      postCreate: function cmm_dnd_DroppedItemWrapper__postCreate() {

         this.inherited(arguments);

         // Attach item edit to click on the whole domNode
         on(this.domNode, "click", lang.hitch(this, this.onItemEdit));
         
         // Attach item focusing
         on(this.domNode, "focus, focusout", lang.hitch(this, this.onItemFocused));

         // Create the actions menu show node
         if(this.showNode == null)
         {
            this.showNode = domConstruct.create("span", {
               className: "action show",
               tabIndex: "0",
               innerHTML: "<img class=\"image\" src=\"" + this.showImageSrc + "\" alt=\"" + this.showAltText + "\" title=\"" + this.showAltText + "\"/>"
            }, this.editNode, "after");
         }
      },

      /**
       * @instance
       * @param {object} evt The click event that triggers the item edit.
       */
      onItemEdit: function cmm_dnd_DroppedItemWrapper__onItemEdit(evt) {

         var targetId = lang.getObject("currentTarget.id", false, evt);

         // Only run the edit sequence if the originating item is id-less or is still available
         if(targetId === "" || registry.byId(targetId))
         {
            this.inherited(arguments);
         }

         event.stop(evt);
         
         // Publish the clicked item for key listening
         if(this.selectPublishTopic != "")
         {
            this.alfPublish(this.selectPublishTopic, {
               item: targetId
            });
         }
      },

      /**
       * @instance
       * @param {object} evt The focus event that triggered this.
       */
      onItemFocused: function cmm_dnd_DroppedItemWrapper__onItemFocused(evt) {

         // Publish the focused item for key listening
         if(this.focusPublishTopic != "")
         {
            this.alfPublish(this.focusPublishTopic, {
               item: evt.type === "focus" ? lang.getObject("currentTarget.id", false, evt) : null
            });
         }
      },
      
      /**
       * @instance
       * @param {object} payload The updated value for the item.
       */
      onEditSave: function cmm_dnd_DroppedItemWrapper__onEditSave(payload) {
         this.inherited(arguments);

         if(type = lang.getObject("wrapperSettings.type", false, payload))
         {
            if(this.colsConfig)
            {
               var allStyles = lang.getObject("allStyles", false, this.colsConfig);
               if(newPanelConfig = lang.getObject(type, false, this.colsConfig))
               {
                  var node = this.domNode;
                  allStyles.wrappers.forEach(function(element) {
                     domClass.remove(node, element);
                  });
                  domClass.add(this.domNode, newPanelConfig.wrapperClass);

                  node = this.controlNode.firstChild;
                  allStyles.targets.forEach(function(element) {
                     domClass.remove(node, element);
                  });
                  domClass.add(this.controlNode.firstChild, newPanelConfig.targetClass);
                  
                  this.value.pseudonym = newPanelConfig.pseudonym;
                  this.value.label = newPanelConfig.label;
                  this.label = newPanelConfig.label;

                  if(typeof this.updateLabel === "function")
                  {
                     this.updateLabel();
                  }
               }
            }
         }
      }

   });
});