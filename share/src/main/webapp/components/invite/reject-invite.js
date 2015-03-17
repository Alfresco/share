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
 * RejectInvite component.
 * 
 * @namespace Alfresco
 * @class Alfresco.RejectInvite
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;

   /**
    * Alfresco Slingshot aliases
    */
    var $html = Alfresco.util.encodeHTML;

   /**
    * RejectInvite constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RejectInvite} The new RejectInvite instance
    * @constructor
    */
   Alfresco.RejectInvite = function(htmlId)
   {
      return Alfresco.RejectInvite.superclass.constructor.call(this, "Alfresco.RejectInvite", htmlId, ["json"]);
   };
   
   YAHOO.extend(Alfresco.RejectInvite, Alfresco.component.Base,
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
          * Current inviteId.
          * 
          * @property inviteId
          * @type string
          */
         inviteId: "",
         
         /**
          * Current ticket.
          * 
          * @property ticket
          * @type string
          */
         inviteTicket: "",
         
         /**
          * Current inviteeUserName.
          * 
          * @property inviteeUserName
          * @type string
          */
         inviteeUserName: ""
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function RejectInvite_onReady()
      {         
         // Create YUI buttons
         this.widgets.declineButton = Alfresco.util.createYUIButton(this, "decline-button", this.onDeclineClick);
         this.widgets.acceptButton = Alfresco.util.createYUIButton(this, "accept-button", this.onAcceptClick);
      },

      /**
       * Decline button click event handler
       *
       * @method onDeclineClick
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onDeclineClick: function RejectInvite_onDeclineClick(e, p_obj)
      {
         // success handler
         var success = function RejectInvite_onDeclineClick_success(response)
         {
            // show the decline confirmed message
            Dom.addClass(Dom.get(this.id + "-confirm"), "hidden");
            Dom.removeClass(Dom.get(this.id + "-declined"), "hidden");
         };
         
         // construct the url to call
         var url = YAHOO.lang.substitute(window.location.protocol + "//" + window.location.host +
            Alfresco.constants.URL_CONTEXT + "proxy/alfresco-noauth/api/invite/{inviteId}/{inviteTicket}/reject?inviteeUserName={inviteeUserName}",
            {
               inviteId : encodeURIComponent(this.options.inviteId),
               inviteTicket : this.options.inviteTicket,
               inviteeUserName : encodeURIComponent(this.options.inviteeUserName)
            });

         // make a backend call to decline the request
         Alfresco.util.Ajax.request(
         {
            method: "PUT",
            url: url,
            responseContentType: "application/json",
            successCallback:
            {
               fn: success,
               scope: this
            },
            failureMessage: this.msg("message.decline.failure")
         });
      },

      /**
       * Accept button click event handler
       *
       * @method onAcceptClick
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onAcceptClick: function RejectInvite_onAcceptClick(e, p_obj)
      {
         // redirect to the accept invite page
         var url = YAHOO.lang.substitute(Alfresco.constants.URL_PAGECONTEXT + "accept-invite" +
            "?inviteId={inviteId}&inviteTicket={inviteTicket}&inviteeUserName={inviteeUserName}",
            {
               inviteId : encodeURIComponent(this.options.inviteId),
               inviteTicket : this.options.inviteTicket,
               inviteeUserName : encodeURIComponent(this.options.inviteeUserName)
            });
         window.location = url;
      }
   });
})();
