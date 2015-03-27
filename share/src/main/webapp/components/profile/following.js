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
 * Following component.
 *
 * @namespace Alfresco
 * @class Alfresco.Following
 * @extends Alfresco.component.Base
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * Following constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.Following} The new Following instance
    * @constructor
    */
   Alfresco.Following = function(htmlId)
   {
      Alfresco.Following.superclass.constructor.call(this, "Alfresco.Following", htmlId, ["button"]);
      return this;
   };

   YAHOO.extend(Alfresco.Following, Alfresco.component.Base,
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
          * Set to true if the current user's following list is private.
          *
          * @property isPrivate
          * @type Boolean
          * @default false
          */
         isPrivate: false
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function F_onReady()
      {
         // Listen to "private" checkbox clicks
         this.widgets.following = Event.addListener(this.id + "-checkbox-following-private", "click", this.onPrivateClick, this, true);

         // onFollowingClick is listened to using the "alfresco-button" class name
         // picked up by the Alfresco.component.BAse.createYUIButtons
      },

      /**
       * Toggles if following list shall be private for the current user and reloads the page.
       *
       * @method onPrivateClick
       * @param e
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onPrivateClick: function F_onPrivateClick(e, p_obj)
      {
         var webscript = "/api/subscriptions/" + encodeURIComponent(Alfresco.constants.USERNAME) + "/private";
         Alfresco.util.Ajax.jsonPut(
         {
            url: Alfresco.constants.PROXY_URI + webscript,
            dataObj: { "private": !this.options.isPrivate },
            successCallback:
            {
               fn: function()
               {
                  window.location.reload();
               },
               scope: this
            }
         });
      },

      /**
       * Makes the current user Unfollow the clicked user and reloads the page.
       *
       * @method onFollowingClick
       * @param value
       * @param name
       */
      onFollowingClick: function F_onPrivateClick(value, name)
      {
         var webscript = "/api/subscriptions/" + encodeURIComponent(Alfresco.constants.USERNAME) + "/unfollow";
         Alfresco.util.Ajax.jsonPost(
         {
            url: Alfresco.constants.PROXY_URI + webscript,
            dataObj: [ value ],
            successCallback:
            {
               fn: function()
               {
                  window.location.reload();
               },
               scope: this
            }
         });
      }

   });
})();