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
 * Folder actions component.
 * 
 * @namespace Alfresco
 * @class Alfresco.FolderActions
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
   var $html = Alfresco.util.encodeHTML,
      $combine = Alfresco.util.combinePaths,
      $siteURL = Alfresco.util.siteURL;
   
   /**
    * FolderActions constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.FolderActions} The new FolderActions instance
    * @constructor
    */
   Alfresco.FolderActions = function(htmlId)
   {
      Alfresco.FolderActions.superclass.constructor.call(this, "Alfresco.FolderActions", htmlId, ["button"]);
      
      // Initialise prototype properties
      this.actionsView = "details";

      // Decoupled event listeners
      YAHOO.Bubbling.on("filesPermissionsUpdated", this.doRefresh, this);
      YAHOO.Bubbling.on("metadataRefresh", this.doRefresh, this);
      YAHOO.Bubbling.on("registerAction", this.onRegisterAction, this);
      
      return this;
   };
   
   /**
    * Extend Alfresco.component.Base
    */
   YAHOO.extend(Alfresco.FolderActions, Alfresco.component.Base);
   
   /**
    * Augment prototype with Actions module
    */
   YAHOO.lang.augmentProto(Alfresco.FolderActions, Alfresco.doclib.Actions);

   /**
    * Augment prototype with main class implementation, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentObject(Alfresco.FolderActions.prototype,
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
          * Current siteId.
          * 
          * @property siteId
          * @type string
          */
         siteId: null,
         
         /**
          * ContainerId representing root container
          *
          * @property containerId
          * @type string
          * @default "documentLibrary"
          */
         containerId: "documentLibrary",

         /**
          * Root node
          *
          * @property rootNode
          * @type string
          */
         rootNode: "alfresco://company/home",

         /**
          * Replication URL Mapping details
          *
          * @property replicationUrlMapping
          * @type object
          */
         replicationUrlMapping: {},

         /**
          * JSON representation of folder details
          *
          * @property folderDetails
          * @type object
          */
         folderDetails: null,

         /**
          * Whether the Repo Browser is in use or not
          *
          * @property repositoryBrowsing
          * @type boolean
          */
         repositoryBrowsing: true
      },
      
      /**
       * The data for the folder
       * 
       * @property recordData
       * @type object
       */
      recordData: null,

      /**
       * Metadata returned by doclist data webscript
       *
       * @property doclistMetadata
       * @type object
       * @default null
       */
      doclistMetadata: null,

      /**
       * Path of asset being viewed - used to scope some actions (e.g. copy to, move to)
       * 
       * @property currentPath
       * @type string
       */
      currentPath: null,

      /**
       * Event handler called when "onReady"
       *
       * @method: onReady
       */
      onReady: function FolderActions_onReady()
      {
         var componentId = this.id;
         
         // Asset data
         this.recordData = this.options.folderDetails.item;
         this.doclistMetadata = this.options.folderDetails.metadata;
         this.currentPath = this.recordData.location.path;
         
         // Populate convenience property
         this.recordData.jsNode = new Alfresco.util.Node(this.recordData.node);
         
         // Retrieve the actionSet for this record
         var record = this.recordData,
            node = record.node,
            actions = record.actions,
            actionsEl = Dom.get(this.id + "-actionSet"),
            actionHTML = "",
            actionsSel;

         record.actionParams = {};
         for (var i = 0, ii = actions.length; i < ii; i++)
         {
            actionHTML += this.renderAction(actions[i], record);
         }

         // Token replacement
         actionsEl.innerHTML = YAHOO.lang.substitute(actionHTML, this.getActionUrls(record));

         Dom.addClass(actionsEl, "action-set");
         Dom.setStyle(actionsEl, "visibility", "visible");
         
         // Hook action events
         var fnActionHandler = function FolderActions_fnActionHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               var me = Alfresco.util.ComponentManager.get(componentId),
                   action = owner.id;
               if (typeof me[action] == "function")
               {
                  args[1].stop = true;
                  me[action].call(me, me.recordData, owner);
               }
            }
            return true;
         };
         
         YAHOO.Bubbling.addDefaultAction("action-link", fnActionHandler);
         
         // DocLib Actions module
         this.modules.actions = new Alfresco.module.DoclibActions();
      },

      /**
       * Delete Asset confirmed.
       *
       * @override
       * @method _onActionDeleteConfirm
       * @param asset {object} Object literal representing file or folder to be actioned
       * @private
       */
      _onActionDeleteConfirm: function FolderActions__onActionDeleteConfirm(asset)
      {
         var path = asset.location.path;
         
         // Update the path for My Files and Shared Files...
         if (Alfresco.constants.PAGECONTEXT == "mine" || Alfresco.constants.PAGECONTEXT == "shared")
         {
            // Get rid of the first "/"
            var tmpPath = path.substring(1); 
            if (Alfresco.constants.PAGECONTEXT == "mine")
            {
               tmpPath = tmpPath.substring(tmpPath.indexOf("/") + 1);
            }
            var slashIndex = tmpPath.indexOf("/");
            if (slashIndex != -1)
            {
               path = tmpPath.substring(slashIndex);
            }
            else
            {
               path = "";
            }
         }
         
         var fileName = asset.fileName,
            displayName = asset.displayName,
            nodeRef = new Alfresco.util.NodeRef(asset.nodeRef),
            parentNodeRef = new Alfresco.util.NodeRef(asset.parent.nodeRef),
            callbackUrl = "",
            encodedPath = path.length > 1 ? "?path=" + encodeURIComponent(path) : "";
         
         // Work out the correct Document Library to return to...
         if (Alfresco.constants.PAGECONTEXT == "mine")
         {
            callbackUrl = "myfiles";
         }
         else if (Alfresco.constants.PAGECONTEXT == "shared")
         {
            callbackUrl = "sharedfiles";
         }
         else
         {
            callbackUrl = Alfresco.util.isValueSet(this.options.siteId) ? "documentlibrary" : "repository";
         }
         
         this.modules.actions.genericAction(
         {
            success:
            {
               activity:
               {
                  siteId: this.options.siteId,
                  activityType: "folder-deleted",
                  page: "documentlibrary",
                  activityData:
                  {
                     fileName: fileName,
                     path: path,
                     nodeRef: nodeRef.toString(),
                     parentNodeRef: parentNodeRef.toString()
                  }
               },
               callback:
               {
                  fn: function FolderActions_oADC_success(data)
                  {
                     window.location = $siteURL(callbackUrl + encodedPath);
                  }
               }
            },
            failure:
            {
               message: this.msg("message.delete.failure", displayName)
            },
            webscript:
            {
               method: Alfresco.util.Ajax.DELETE,
               name: "file/node/{nodeRef}",
               params:
               {
                  nodeRef: nodeRef.uri
               }
            }
         });
      },

      /**
       * Refresh component in response to filesPermissionsUpdated event
       *
       * @method doRefresh
       */
      doRefresh: function FolderActions_doRefresh()
      {
         YAHOO.Bubbling.unsubscribe("filesPermissionsUpdated", this.doRefresh, this);
         YAHOO.Bubbling.unsubscribe("metadataRefresh", this.doRefresh, this);
         this.refresh('components/folder-details/folder-actions?nodeRef={nodeRef}' + (this.options.siteId ? '&site={siteId}' : ''));
      }
   }, true);
})();
