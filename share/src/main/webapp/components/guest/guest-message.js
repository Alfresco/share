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
 * GuestMessage component.
 *
 * @namespace Alfresco
 * @class Alfresco.component.GuestMessage
 */
(function()
{
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Selector = YAHOO.util.Selector;

   /**
    * GuestMessage constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.component.GuestMessage} The new GuestMessage instance
    * @constructor
    */
   Alfresco.component.GuestMessage = function GuestMessage_constructor(htmlId)
   {
      Alfresco.component.GuestMessage.superclass.constructor.call(this, "Alfresco.component.GuestMessage", htmlId, ["button"]);
      return this;
   };

   YAHOO.extend(Alfresco.component.GuestMessage, Alfresco.component.Base,
   {
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function GuestMessage_onReady()
      {
         var overlay = Alfresco.util.createYUIOverlay(Dom.get(this.id),
         {
            effect:
            {
               effect: YAHOO.widget.ContainerEffect.FADE,
               duration: 0.25
            }
         }, { render: false });
         Dom.removeClass(this.id + "-body", "hidden");
         overlay.render(document.body);
         overlay.center();
         overlay.show();
      }

   });
})();
