/**
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
 * Document permissions component.
 * 
 * @namespace Alfresco
 * @class Alfresco.DocumentPermissions
 */
(function()
{
   /**
    * DocumentPermissions constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.DocumentPermissions} The new DocumentPermissions instance
    * @constructor
    */
   Alfresco.DocumentPermissions = function(htmlId)
   {
      Alfresco.DocumentPermissions.superclass.constructor.call(this, "Alfresco.DocumentPermissions", htmlId);
      
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("filesPermissionsUpdated", this.doRefresh, this);
      
      return this;
   };

   /**
    * Extend Alfresco.component.Base
    */
   YAHOO.extend(Alfresco.DocumentPermissions, Alfresco.component.Base,
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
         siteId: "",

         /**
          * The display name of the current document, will be used in the manage permissions panel.
          *
          * @property displayName
          * @type string
          */
         displayName: null,

         /**
          * The permission roles for the groups on this document.
          *
          * @property roles
          * @type Array
          */
         roles: null
      },

      /**
       * Called when the onManagePermissionsClick link has been clicked, will display the manage permissions dialog.
       *
       * @method onManagePermissionsClick
       */
      onManagePermissionsClick: function DocumentPermissions_onManagePermissionsClick()
      {
         if (!this.modules.permissions)
         {
            this.modules.permissions = new Alfresco.module.DoclibPermissions(this.id + "-permissions");
         }

         this.modules.permissions.setOptions(
         {
            siteId: this.options.siteId,
            files:
            {
               displayName: this.options.displayName,
               node:
               {
                  nodeRef: this.options.nodeRef,
                  permissions:
                  {
                     roles: this.options.roles
                  }
               }
            }
         }).showDialog();
      },

      /**
       * Called when the "filesPermissionsUpdated" has been fired from the manage permissions dialog.
       * Will refresh this component.
       *
       * @method doRefresh
       */
      doRefresh: function DocumentPermissions_doRefresh()
      {
         YAHOO.Bubbling.unsubscribe("filesPermissionsUpdated", this.doRefresh, this);
         this.refresh('components/document-details/document-permissions?nodeRef={nodeRef}' + (this.options.siteId ? '&site={siteId}' :  ''));
      }
   });
})();