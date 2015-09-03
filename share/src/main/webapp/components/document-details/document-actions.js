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
 * Document actions component.
 *
 * @namespace Alfresco
 * @class Alfresco.DocumentActions
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
    * DocumentActions constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.DocumentActions} The new DocumentActions instance
    * @constructor
    */
   Alfresco.DocumentActions = function(htmlId)
   {
      Alfresco.DocumentActions.superclass.constructor.call(this, "Alfresco.DocumentActions", htmlId, ["button"]);

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
   YAHOO.extend(Alfresco.DocumentActions, Alfresco.component.Base);

   /**
    * Augment prototype with Actions module
    */
   YAHOO.lang.augmentProto(Alfresco.DocumentActions, Alfresco.doclib.Actions);

   /**
    * Augment prototype with main class implementation, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentObject(Alfresco.DocumentActions.prototype,
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
          * Valid inline edit mimetypes
          * Currently allowed are plain text, HTML and XML only
          *
          * @property inlineEditMimetypes
          * @type object
          */
         inlineEditMimetypes:
         {
            "text/plain": true,
            "text/html": true,
            "text/xml": true
         },

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
          * JSON representation of document details
          *
          * @property documentDetails
          * @type object
          */
         documentDetails: null,

         /**
          * Whether the Repo Browser is in use or not
          *
          * @property repositoryBrowsing
          * @type boolean
          */
         repositoryBrowsing: true
      },

      /**
       * The data for the document
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
      onReady: function DocumentActions_onReady()
      {
         var me = this;

         // Asset data
         this.recordData = this.options.documentDetails.item;
         this.doclistMetadata = this.options.documentDetails.metadata;
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
             if (i + 1 < ii && actions[i].subgroup != actions[i + 1].subgroup)
             {
                actions[i]["lastActionInSubgroup"] = true;
             }
            
            actionHTML += this.renderAction(actions[i], record);
         }

         // Token replacement (actionUrls is re-used further down)
         var actionUrls = this.getActionUrls(record);
         actionsEl.innerHTML = YAHOO.lang.substitute(actionHTML, actionUrls);

         Dom.addClass(actionsEl, "action-set");
         Dom.setStyle(actionsEl, "visibility", "visible");

         var displayName = record.displayName,
            downloadUrl = actionUrls.downloadUrl;

         // Hook action events
         var fnActionHandler = function DocumentActions_fnActionHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               if (typeof me[owner.id] === "function")
               {
                  args[1].stop = true;
                  try
                  {
                     me[owner.id].call(me, me.recordData, owner);
                  }
                  catch (e)
                  {
                     Alfresco.logger.error("DocumentActions_fnActionHandler", owner.id, e);
                  }
               }
            }
            return true;
         };
         YAHOO.Bubbling.addDefaultAction("action-link", fnActionHandler, true);

         // DocLib Actions module
         this.modules.actions = new Alfresco.module.DoclibActions();

         // Prompt auto-download (after Edit Offline action)?
         if (window.location.hash == "#editOffline")
         {
            window.location.hash = "";

            var isEditingCanceled = false;
            YAHOO.Bubbling.on("editingCanceled", function(layer, args)
            {
               if (record.workingCopy.sourceNodeRef == args[1].record.workingCopy.sourceNodeRef)
               {
                  isEditingCanceled = true;
               }
            }, this);

            if (YAHOO.env.ua.ie > 6)
            {
               // MSIE7 blocks the download and gets the wrong URL in the "manual download bar"
               Alfresco.util.PopupManager.displayPrompt(
               {
                  title: this.msg("message.edit-offline.success", displayName),
                  text: this.msg("message.edit-offline.success.ie7"),
                  buttons: [
                  {
                     text: this.msg("button.download"),
                     handler: function DocumentActions_oAEO_success_download()
                     {
                        window.open(downloadUrl, "_blank");
                        this.destroy();
                     },
                     isDefault: true
                  },
                  {
                     text: this.msg("button.close"),
                     handler: function DocumentActions_oAEO_success_close()
                     {
                        this.destroy();
                     }
                  }]
               });
            }
            else
            {
               Alfresco.util.PopupManager.displayMessage(
               {
                  text: this.msg("message.edit-offline.success", displayName)
               });
               // Kick off the download 3 seconds after the confirmation message
               YAHOO.lang.later(3000, this, function()
               {
                  if (!isEditingCanceled)
                  {
                     window.location = downloadUrl;
                  }
               });
            }
         }

         if (window.location.hash == "#editCancelled")
         {
            window.location.hash = "";
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.edit-cancel.success", displayName)
            });
         }
		 
         if (window.location.hash == "#unlockDocument")
         {
            window.location.hash = "";
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.unlock-document.success", displayName)
            });
         }
	 
         if (window.location.hash == "#newVersionUpload")
         {
            window.location.hash = "";
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.new-version-upload.success")
            });
         }
      },

      /**
       * Edit Offline.
       *
       * @override
       * @method onActionEditOffline
       * @param asset {object} Object literal representing file or folder to be actioned
       */
      onActionEditOffline: function DocumentActions_onActionEditOffline(asset)
      {
         var displayName = asset.displayName,
            nodeRef = new Alfresco.util.NodeRef(asset.nodeRef);

         this.modules.actions.genericAction(
         {
            success:
            {
               callback:
               {
                  fn: function DocumentActions_oAEO_success(data)
                  {
                     this.recordData.jsNode.setNodeRef(data.json.results[0].nodeRef);
                     window.location = this.getActionUrls(this.recordData).documentDetailsUrl + "#editOffline";
                  },
                  scope: this
               }
            },
            failure:
            {
               message: this.msg("message.edit-offline.failure", displayName)
            },
            webscript:
            {
               method: Alfresco.util.Ajax.POST,
               name: "checkout/node/{nodeRef}",
               params:
               {
                  nodeRef: nodeRef.uri
               }
            }
         });
      },

      /**
       * Cancel editing.
       *
       * @override
       * @method onActionCancelEditing
       * @param asset {object} Object literal representing file or folder to be actioned
       */
      onActionCancelEditing: function DocumentActions_onActionCancelEditing(asset)
      {
         var displayName = asset.displayName,
            nodeRef = new Alfresco.util.NodeRef(asset.nodeRef);

         this.modules.actions.genericAction(
         {
            success:
            {
               callback:
               {
                  fn: function DocumentActions_oACE_success(data)
                  {
                      var oldNodeRef = this.recordData.jsNode.nodeRef.nodeRef,
                      newNodeRef = data.json.results[0].nodeRef;
                      this.recordData.jsNode.setNodeRef(newNodeRef);
                      window.location = this.getActionUrls(this.recordData).documentDetailsUrl + "#editCancelled";
                      // ALF-16598 fix, page is not refreshed if only hash was changed, force page reload for cancel online editing
                      if (oldNodeRef == newNodeRef)
                      {
                          window.location.reload();
                      }
                  },
                  scope: this
               }
            },
            failure:
            {
               message: this.msg("message.edit-cancel.failure", displayName)
            },
            webscript:
            {
               method: Alfresco.util.Ajax.POST,
               name: "cancel-checkout/node/{nodeRef}",
               params:
               {
                  nodeRef: nodeRef.uri
               }
            }
         });

         YAHOO.Bubbling.fire("editingCanceled",
         {
            record: asset
         });
      },
	  
      /**
       * Unlock document.
       *
       * @override
       * @method onActionCancelEditing
       * @param asset {object} 
       */
      onActionUnlockDocument: function DocumentActions_onActionUnlockDocument(asset)
      {
         var displayName = asset.displayName,
            nodeRef = new Alfresco.util.NodeRef(asset.nodeRef);

         this.modules.actions.genericAction(
         {
            success:
            {
               callback:
               {
                  fn: function DocumentActions_oACE_success(data)
                  {
                      var oldNodeRef = this.recordData.jsNode.nodeRef.nodeRef,
                      newNodeRef = data.json.results[0].nodeRef;
                      this.recordData.jsNode.setNodeRef(newNodeRef);
                      window.location = this.getActionUrls(this.recordData).documentDetailsUrl + "#unlockDocument";
                      if (oldNodeRef == newNodeRef)
                      {
                          window.location.reload();
                      }
                  },
                  scope: this
               }
            },
            failure:
            {
               message: this.msg("message.unlock-document.failure", displayName)
            },
            webscript:
            {
               method: Alfresco.util.Ajax.POST,
               name: "unlock-document/node/{nodeRef}",
               params:
               {
                  nodeRef: nodeRef.uri
               }
            }
         });

         YAHOO.Bubbling.fire("editingCanceled",
         {
            record: asset
         });
      },

      /**
       * Upload new version.
       *
       * @override
       * @method onActionUploadNewVersion
       * @param asset {object} Object literal representing the file to be actioned upon
       */
      onActionUploadNewVersion: function DocumentActions_onActionUploadNewVersion(asset)
      {
         var displayName = asset.displayName,
            nodeRef = new Alfresco.util.NodeRef(asset.nodeRef),
            version = asset.version;

         if (!this.fileUpload)
         {
            this.fileUpload = Alfresco.getFileUploadInstance();
         }

         // Show uploader for multiple files
         var description = this.msg("label.filter-description", displayName),
            extensions = "*";

         if (displayName && new RegExp(/[^\.]+\.[^\.]+/).exec(displayName))
         {
            // Only add a filtering extension if filename contains a name and a suffix
            extensions = "*" + displayName.substring(displayName.lastIndexOf("."));
         }

         if (asset.workingCopy && asset.workingCopy.workingCopyVersion)
         {
            version = asset.workingCopy.workingCopyVersion;
         }

         var singleUpdateConfig =
         {
            updateNodeRef: nodeRef.toString(),
            updateFilename: displayName,
            updateVersion: version,
            suppressRefreshEvent: true,
            overwrite: true,
            filter: [
            {
               description: description,
               extensions: extensions
            }],
            mode: this.fileUpload.MODE_SINGLE_UPDATE,
            onFileUploadComplete:
            {
               fn: this.onNewVersionUploadCompleteCustom,
               scope: this
            }
         };
         if (Alfresco.util.isValueSet(this.options.siteId))
         {
            singleUpdateConfig.siteId = this.options.siteId;
            singleUpdateConfig.containerId = this.options.containerId;
         }
         this.fileUpload.show(singleUpdateConfig);
      },

      /**
       * Called from the uploader component after a the new version has been uploaded.
       *
       * @method onNewVersionUploadCompleteCustom
       * @param complete {object} Object literal containing details of successful and failed uploads
       */
      onNewVersionUploadCompleteCustom: function DocumentActions_onNewVersionUploadCompleteCustom(complete)
      {
        this.recordData.jsNode.setNodeRef(complete.successful[0].nodeRef);
        var mydocumentDetailsUrl = this.getActionUrls(this.recordData).documentDetailsUrl + "#newVersionUpload";
        var oldNodeRef = this.recordData.jsNode.nodeRef.nodeRef,
            newNodeRef = this.recordData.nodeRef;
        
        Alfresco.Share.postActivity(this.options.siteId, "org.alfresco.documentlibrary.file-updated", complete.successful[0].fileName, "document-details?nodeRef="+complete.successful[0].nodeRef, 
        {
            fileName: complete.successful[0].fileName,
            nodeRef: complete.successful[0].nodeRef

        }, function() {
              window.location = mydocumentDetailsUrl;
              if (oldNodeRef == newNodeRef)
              {
                 window.location.reload();
              }
        }); 

      },

      /**
       * Delete Asset confirmed.
       *
       * @override
       * @method _onActionDeleteConfirm
       * @param asset {object} Object literal representing file or folder to be actioned
       * @private
       */
      _onActionDeleteConfirm: function DocumentActions__onActionDeleteConfirm(asset)
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
                  activityType: "file-deleted",
                  page: "documentlibrary",
                  activityData:
                  {
                     fileName: fileName,
                     path: path,
                     nodeRef: nodeRef.toString()
                  }
               },
               callback:
               {
                  fn: function DocumentActions_oADC_success(data)
                  {
                     window.location = asset.parent.nodeRef ? $siteURL(callbackUrl + encodedPath) : Alfresco.constants.URL_CONTEXT;
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
       * Refresh component in response to metadataRefresh event
       *
       * @method doRefresh
       */
      doRefresh: function DocumentActions_doRefresh()
      {
         YAHOO.Bubbling.unsubscribe("filesPermissionsUpdated", this.doRefresh, this);
         YAHOO.Bubbling.unsubscribe("metadataRefresh", this.doRefresh, this);
         this.refresh('components/document-details/document-actions?nodeRef={nodeRef}' + (this.options.siteId ? '&site={siteId}' : ''));
      }
   }, true);
})();
