/**
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
 * DocumentTags
 *
 * @namespace Alfresco
 * @class Alfresco.DocumentTags
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * DocumentTags constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.DocumentTags} The new DocumentTags instance
    * @constructor
    */
   Alfresco.DocumentTags = function DocumentTags_constructor(htmlId)
   {
      Alfresco.DocumentTags.superclass.constructor.call(this, "Alfresco.DocumentTags", htmlId);

      // Decoupled event listeners
      YAHOO.Bubbling.on("metadataRefresh", this.doRefresh, this);

      return this;
   };

   YAHOO.extend(Alfresco.DocumentTags, Alfresco.component.Base,
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
          * The nodeRefs to load the form for.
          *
          * @property nodeRef
          * @type string
          * @required
          */
         nodeRef: null,

         /**
          * Current siteId, if any.
          *
          * @property siteId
          * @type string
          */
         siteId: ""
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Template initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function DocumentTags_onReady()
      {
      },

      /**
       * Refresh component in response to metadataRefresh event
       *
       * @method doRefresh
       */
      doRefresh: function DocumentTags_doRefresh()
      {
         YAHOO.Bubbling.unsubscribe("metadataRefresh", this.doRefresh, this);
         this.refresh('components/document-details/document-tags?nodeRef={nodeRef}' + (this.options.siteId ? '&site={siteId}' :  ''));
      }
   });
})();
