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
 * Provides a simple help bubble tip for use within forms.
 * 
 * @module cmm/help/CMMHelpBubble
 * @extends module:dijit/_WidgetBase
 * @mixes module:alfresco/core/Core
 * @author Kevin Roast
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase",
        "dijit/_TemplatedMixin",
        "alfresco/core/Core",
        "dojo/text!./templates/CMMHelpBubble.html",
        "dojo/dom-class"],
        function(declare, _Widget, _Templated, AlfCore, template, domClass) {

   return declare([_Widget, _Templated, AlfCore], {

      /**
       * An array of the CSS files to use with this widget.
       * @instance
       * @type {object[]}
       * @default [{cssFile:"./css/CMMHelpBubble.css"}]
       */
      cssRequirements: [{cssFile:"./css/CMMHelpBubble.css"}],
      
      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {String}
       */
      templateString: template,
      
      /**
       * The label text to use for the widget.
       * @instance
       * @type {String}
       */
      label: "",
      
      /**
       * @instance
       */
      postMixInProperties: function alfresco_buttons_AlfButton__postMixInProperties() {
         this.label = this.message(this.label);
         this.inherited(arguments);
      },

      /**
       * @instance
       */
      postCreate: function alfresco_buttons_AlfButton__postCreate() {
         this.inherited(arguments);
         domClass.add(this.containerNode, (this.additionalCssClasses || ""));
      }
   });
});