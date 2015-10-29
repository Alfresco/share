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
 * Roles Tooltip module
 *
 * A tooltip for describing Alfresco roles both in and out of a site context.
 * This tooltip requires that there be on the page a containing html element with an html button inside it.
 * See invitationlist.get.html.ftl and invitationlist.js for usage of this module.
 *
 * @namespace Alfresco.module
 * @class Alfresco.module.RolesTooltip
 */
(function()
{
   
   var Dom = YAHOO.util.Dom;
   
   /**
    * RolesTooltip constructor.
    *
    * @param containerComponentId {string} The containing component's id
    * @param tooltipContextElementId {string} DOM id of the element where the tooltip content will be injected
    * @param buttonName {string} end of DOM id of the button to trigger the tooltip with id as htmlId + "-" + buttonName 
    * @param siteId {string} Id of the site if this is being called from a site context or null
    * @param noderef {string} Noderef of the content if this is being called from a content context or null
    * @return {Alfresco.RolesTooltip} The new RolesTooltip instance
    * @constructor
    */
   Alfresco.module.RolesTooltip = function(containerComponentId, tooltipContextElementId, buttonName, siteId, noderef)
   {
      var buttonId = containerComponentId + "-" + buttonName;
      Alfresco.module.RolesTooltip.superclass.constructor.call(this, "Alfresco.module.RolesTooltip", 
            buttonId + "-roles-tooltip", ["button", "json"]);

      this.tooltipDivId = containerComponentId + '-role-info-panel';
      this.tooltipContextElementId = tooltipContextElementId;
      this.siteId = siteId ? siteId : "";
      this.noderef = noderef ? noderef : "";
      
      // Button to show role info tooltip
      this.widgets.roleInfoButton = Alfresco.util.createYUIButton(this, buttonName, this.show, {}, buttonId);
      
      return this;
   };

   YAHOO.extend(Alfresco.module.RolesTooltip, Alfresco.component.Base,
   {
      /**
       * DOM id of the tooltip content Div 
       */
      tooltipDivId: "",
      
      /**
       * DOM id of the element where the tooltip will be injected
       */
      tooltipContextElementId: "",
      
      /**
       * Id of the site if this is being called from a site context or null otherwise
       */
      siteId: "",
      
      /**
       * Noderef of the content if this is being called from a content context or null otherwise
       */
      noderef: "",
      
      /**
       * Shows the Roles Tooltip to the user.
       *
       * @method show
       */
      show: function RolesTooltip_show()
      {
         if (!Alfresco.module.RolesTooltip.tooltipDiv)
         {
            /**
             * Load the gui and site info from the server and let the templateLoaded() method
             * handle the rest.
             */
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "modules/roles-tooltip",
               dataObj:
               {
                  siteId: this.siteId,
                  noderef: this.noderef
               },
               successCallback:
               {
                  fn: this.onTemplateLoaded,
                  scope: this
               },
               failureMessage: "Could not load Roles Tooltip"
            });
         }
         else
         {
            this._show();
         }
      },

      /**
       * Called when the RolesTooltip html template has been returned from the server.
       * Creates the YUI gui objects such as buttons and a balloon and shows it.
       *
       * @method onTemplateLoaded
       * @param response {object} a Alfresco.util.Ajax.request response object 
       */
      onTemplateLoaded: function RolesTooltip_onTemplateLoaded(response)
      {
         // Inject the template from the XHR request into a new DIV element
         var tooltipDiv = document.createElement("div");
         tooltipDiv.id = this.tooltipDivId;
         Dom.addClass(tooltipDiv, "hidden");
         tooltipDiv.innerHTML = response.serverResponse.responseText;
         Dom.get(this.tooltipContextElementId).appendChild(tooltipDiv);
         Alfresco.module.RolesTooltip.tooltipDiv = tooltipDiv;
         this._show();
      },
      
      _show: function RolesTooltip__show()
      {
         if (!this.widgets.roleTooltip)
         {
            this.widgets.roleTooltip = new Alfresco.util.createInfoBalloon(
               this.tooltipContextElementId,
               {
                  html: Alfresco.module.RolesTooltip.tooltipDiv.innerHTML,
                  width: "350px",
                  wrapperClass: "alf-info-balloon"
               });
         }
         this.widgets.roleTooltip.show();
         
         // Override default alignment
         this.widgets.roleTooltip.balloon.align(
               YAHOO.widget.Overlay.TOP_RIGHT, YAHOO.widget.Overlay.BOTTOM_LEFT, [30, 8]);
      }
   });
})();