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
 * Folder permissions component.
 * 
 * @namespace Alfresco
 * @class Alfresco.FolderPermissions
 */
(function()
{
   /**
    * FolderPermissions constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.FolderPermissions} The new FolderPermissions instance
    * @constructor
    */
   Alfresco.FolderPermissions = function(htmlId)
   {
      Alfresco.FolderPermissions.superclass.constructor.call(this, "Alfresco.FolderPermissions", htmlId);
      
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("filesPermissionsUpdated", this.doRefresh, this);
      
      return this;
   };

   /**
    * Extend Alfresco.component.Base
    */
   YAHOO.extend(Alfresco.FolderPermissions, Alfresco.component.Base,
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
          * Reference to the current folder
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
          * The display name of the current folder, will be used in the manage permissions panel.
          *
          * @property displayName
          * @type string
          */
         displayName: null,

         /**
          * The permission roles for the groups on this folder.
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
      onManagePermissionsClick: function FolderPermissions_onManagePermissionsClick()
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
      doRefresh: function FolderPermissions_doRefresh()
      {
         YAHOO.Bubbling.unsubscribe("filesPermissionsUpdated", this.doRefresh, this);
         this.refresh('components/folder-details/folder-permissions?nodeRef={nodeRef}' + (this.options.siteId ? '&site={siteId}' :  ''));
      }
   });
})();