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
 * Document links component.
 * 
 * @namespace Alfresco
 * @class Alfresco.DocumentLinks
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Selector = YAHOO.util.Selector;

   /**
    * DocumentLinks constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.DocumentLinks} The new DocumentLinks instance
    * @constructor
    */
   Alfresco.DocumentLinks = function(htmlId)
   {
      Alfresco.DocumentLinks.superclass.constructor.call(this, "Alfresco.DocumentLinks", htmlId, []);

      // Initialise prototype properties
      this.hasClipboard = window.clipboardData && window.clipboardData.setData;
      
      return this;
   };
   
   YAHOO.extend(Alfresco.DocumentLinks, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * Reference to the current document
          *
          * @property nodeRef
          * @type string
          */
         nodeRef: null,

         /**
          * Current siteId, if any.
          *
          * @property siteId
          * @type string
          */
         siteId: null
      },

      /**
       * Does the browser natively support clipboard data?
       * 
       * @property hasClipboard
       * @type boolean
       */
      hasClipboard: null,

      /**
       *
       *
       * @method: onReady
       */
      onReady: function DocumentLinks_onReady()
      {
         // Display copy links
         if (this.hasClipboard)
         {
            Dom.removeClass(Selector.query("a.hidden", this.id), "hidden");
         }

         // Make sure text fields auto select the text on focus
         Event.addListener(Selector.query("input", this.id), "focus", this._handleFocus);

         // Prefix some of the urls with values from the client
         Dom.get(this.id + "-page").value = document.location.href;
      },

      /**
       * called when the "onCopyLinkClick" link has been clicked.
       * Tries to copy URLs to the system clipboard.
       * 
       * @method onCopyLinkClick
       * @param rel {string} The Dom Id of the element holding the URL to copy
       */
      onCopyLinkClick: function DocumentLinks_onCopyLinkClick(rel, anchor)
      {
         var link = Dom.getPreviousSibling(anchor);
         window.clipboardData.setData("Text", link.value);
      },

      /**
       * Event handler used to select text in the field when focus is received
       *
       * @method _handleFocus
       */
      _handleFocus: function DocumentLinks__handleFocus()
      {
         this.select();
      }
   });
})();
