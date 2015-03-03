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
 * Document Details WOrkflow component.
 *
 * @namespace Alfresco
 * @class Alfresco.DocumentWorkflows
 */
(function()
{
   /**
    * Alfresco Slingshot aliases
    */
   var $siteURL = Alfresco.util.siteURL;

   /**
    * DocumentWorkflows constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.DocumentWorkflows} The new component instance
    * @constructor
    */
   Alfresco.DocumentWorkflows = function DocumentWorkflows_constructor(htmlId)
   {
      Alfresco.DocumentWorkflows.superclass.constructor.call(this, "Alfresco.DocumentWorkflows", htmlId, []);
      return this;
   };

   YAHOO.extend(Alfresco.DocumentWorkflows, Alfresco.component.Base,
   {
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
         siteId: "",

         /**
          * Reference to the parent which will be used when POSTing to the start workflow page.
          *
          * @property destination
          * @type string
          */
         destination: null
      },

      /**
       * Assign Workflow click handler
       *
       * @method onAssignWorkflowClick
       */
      onAssignWorkflowClick: function DocumentWorkflows_onAssignWorkflowClick()
      {
         Alfresco.util.navigateTo($siteURL("start-workflow"), "POST",
         {
            selectedItems: this.options.nodeRef,
            destination: this.options.destination
         });
      }
   });
})();
