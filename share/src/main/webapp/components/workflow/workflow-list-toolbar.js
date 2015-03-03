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
 * WorkflowListToolbar component.
 *
 * @namespace Alfresco
 * @class Alfresco.component.WorkflowListToolbar
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Selector = YAHOO.util.Selector;

   /**
    * WorkflowListToolbar constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.component.WorkflowListToolbar} The new WorkflowListToolbar instance
    * @constructor
    */
   Alfresco.component.WorkflowListToolbar = function TDH_constructor(htmlId)
   {
      Alfresco.component.WorkflowListToolbar.superclass.constructor.call(this, "Alfresco.component.WorkflowListToolbar", htmlId, ["button"]);
      return this;
   };

   YAHOO.extend(Alfresco.component.WorkflowListToolbar, Alfresco.component.Base,
   {
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function WLT_onReady()
      {
         this.widgets.startWorkflowButton = Alfresco.util.createYUIButton(this, "startWorkflow-button", this.onStartWorkflowButtonClick, {});
         Dom.removeClass(Selector.query(".hidden", this.id + "-body", true), "hidden");
      },

      /**
       * Start workflow button click handler
       *
       * @method onNewFolder
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onStartWorkflowButtonClick: function WLT_onNewFolder(e, p_obj)
      {
         document.location.href = Alfresco.util.siteURL("start-workflow?referrer=workflows&myWorkflowsLinkBack=true");
      }

   });

})();
