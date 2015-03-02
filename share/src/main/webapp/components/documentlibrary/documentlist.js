/**
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
 * DocumentList component.
 *
 * @namespace Alfresco
 * @class Alfresco.DocumentList
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event,
       DDM = YAHOO.util.DragDropMgr;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $links = Alfresco.util.activateLinks,
      $combine = Alfresco.util.combinePaths,
      $userProfile = Alfresco.util.userProfileLink,
      $siteURL = Alfresco.util.siteURL,
      $date = function $date(date, format) { return Alfresco.util.formatDate(Alfresco.util.fromISO8601(date), format); },
      $relTime = Alfresco.util.relativeTime,
      $isValueSet = Alfresco.util.isValueSet;

   /**
    * Preferences
    */
   var PREFERENCES_DOCLIST = "org.alfresco.share.documentList",
      PREF_SORT_ASCENDING = PREFERENCES_DOCLIST + ".sortAscending",
      PREF_SORT_FIELD = PREFERENCES_DOCLIST + ".sortField",
      PREF_SHOW_FOLDERS = PREFERENCES_DOCLIST + ".showFolders",
      PREF_HIDE_NAVBAR = PREFERENCES_DOCLIST + ".hideNavBar",
      PREF_SIMPLE_VIEW = PREFERENCES_DOCLIST + ".simpleView",           // deprecated
      PREF_VIEW_RENDERER = PREFERENCES_DOCLIST + ".viewRendererName",   // viewRender was previously simpleView
      PREF_GALLERY_COLUMNS = PREFERENCES_DOCLIST + ".galleryColumns";

   /**
    * Document Library Drag and Drop object declaration.
    */
   Alfresco.DnD = function(id, docLib, sGroup, config)
   {
      Alfresco.DnD.superclass.constructor.call(this, id, sGroup, config);
      var el = this.getDragEl();
      Dom.setStyle(el, "opacity", 0.67);
      this.docLib = docLib;
   };

   /**
    * Extend the default YUI drag and drop proxy object to handle DocumentLibrary move operations.
    */
   YAHOO.extend(Alfresco.DnD, YAHOO.util.DDProxy,
   {
      /**
       * A flag used to indicate whether or not an asynchronous move operation request is in progress.
       */
      _inFlight: false,

      /**
       * Handles the beginning of a drag operation by setting up the proxy image element.
       */
      startDrag: function DL_DND_startDrag(x, y)
      {
          var dragEl = this.getDragEl();
          var clickEl = this.getEl();
          Dom.setStyle(clickEl, "visibility", "hidden");
          var proxyImg = document.createElement("img");
          proxyImg.src = clickEl.src;
          dragEl.removeChild(dragEl.firstChild);
          dragEl.appendChild(proxyImg);
          Dom.setStyle(dragEl, "border", "none");
      },

      /**
       * Handles the end of the drag operation. Because the move operation is asynchronous
       * it is not know if the operation has been a success at the time this function is
       * invoked so it uses the _inFlight variable to check whether or not a valid drop
       * target was used.
       *
       * @param The event object
       */
      endDrag: function DL_DND_endDrag(e)
      {
         if (!this._inFlight)
         {
            var srcEl = this.getEl();
            var proxy = this.getDragEl();
            this.animateResult(proxy, srcEl);
         }
      },

      /**
       * Animates an object to move it to the location of a target object. This should typically
       * be animating the proxy object to return to its source.
       *
       * @param objectToAnimate The object to animate
       * @param animationTarget The object to create a motion animation to
       */
      animateResult: function DL_DND_animateResult(objectToAnimate, animationTarget)
      {
          Dom.setStyle(objectToAnimate, "visibility", "");
          var a = new YAHOO.util.Motion(
                objectToAnimate, {
                  points: {
                      to: Dom.getXY(animationTarget)
                  }
              },
              0.2,
              YAHOO.util.Easing.easeOut
          );
          var proxyid = objectToAnimate.id;
          var thisid = this.id;

          a.onComplete.subscribe(function() {
                  Dom.setStyle(proxyid, "visibility", "hidden");
                  Dom.setStyle(thisid, "visibility", "");
              });
          a.animate();
      },

      /**
       * Handles a drop operation by determining whether or not a valid drop has been performed (e.g.
       * a document or folder onto a folder - NOT a document) and then fires a request to perform
       * the move operation.
       *
       * @param e The event object
       * @param id The id of the element that the proxy has been dropped onto
       */
      onDragDrop: function DL_DND_onDragDrop(e, id)
      {
          var dropTarget = Dom.get(id);
          if (DDM.interactionInfo.drop.length > 0)
          {
             // See if the element exists within the table...
             if (Dom.isAncestor(this.docLib.widgets.dataTable.getContainerEl(), dropTarget))
             {
                // If the drop target is contained within the data table then process "normally"...
                var targetRecord = this.docLib.widgets.dataTable.getRecord(Dom.get(id)),
                targetNode = targetRecord.getData();

                if (targetNode.node.isContainer)
                {
                   // Indicate that a request is about to be made - this will prevent the endDrag
                   // function from animating the proxy to return to its source...
                   this._inFlight = true;

                   // Make sure we handle linked folders...
                   var nodeRef;
                   if (targetNode.node.isLink)
                   {
                      nodeRef = new Alfresco.util.NodeRef(targetNode.node.linkedNode.nodeRef);
                   }
                   else
                   {
                      nodeRef = new Alfresco.util.NodeRef(targetNode.node.nodeRef);
                   }

                   // Move the document/folder...
                   this._performMove(nodeRef, targetNode.location.path + "/" + targetNode.location.file);
                }
             }
             else if (Dom.hasClass(dropTarget, "documentDroppable"))
             {
                // The "documentDroppable" class is not defined in any CSS files but is simply used as
                // a marker to indicate that the element can be used as a document drop target. Only
                // documents are dragged and dropped onto these elements should result in the drop
                // target request being fired (it's possible that an element could be specified as a
                // YUI drag and drop target for the purposes of controlling drag events without actually
                // allowing drops to occur
		var fpanel = Dom.get("alf-filters"),
		offset = (fpanel ? parseInt(fpanel.style.width, 10) : 160);
				
		if(e.clientX > offset)
                {
		    // If the current x co-ordinate of the mouse pointer is greater than the width
		    //of the tree element then we shouldn't move folder/documents.
		}
                else
		{
                    var payload =
                    {
                        elementId: id,
                        callback: this.onDropTargetOwnerCallBack,
                        scope: this
                    }
		    YAHOO.Bubbling.fire("dropTargetOwnerRequest", payload);           
		}
                this._inFlight = true;
                this._setFailureTimeout();
             }
          }
      },

      /**
       * Moves the document or folder associated with the drag proxy to the nodeRef supplied. This
       * method is either called when dropping onto the DocumentList directly or onto any other
       * valid drop target that can process "dropTargetOwnerRequest" events.
       *
       * @method _performMove
       * @property nodeRef The nodeRef onto which the proxy should be moved.
       */
      _performMove: function DL_DND__performMove(nodeRef, path)
      {
         // Set variables required for move...
         var toMoveRecord = this.docLib.widgets.dataTable.getRecord(this.getEl()),
             webscriptName = "move-to/node/{nodeRef}",
             multipleFiles = [];

         multipleFiles.push(this.getEl().id);

         // Success callback function:
         // If the operation succeeded then update the tree and refresh the document list.
         var fnSuccess = function DLCMT__onOK_success(p_data)
         {
            this._inFlight = false; // Indicate that a request is no longer "in-flight"

            var result,
                successCount = p_data.json.successCount,
                failureCount = p_data.json.failureCount;

            // Did the operation NOT succeed?
            if (!p_data.json.overallSuccess)
            {
               this.animateResult(this.getDragEl(), this.getEl());
               Alfresco.util.PopupManager.displayMessage(
               {
                  text: this.docLib.msg("message.file-dnd-move.failure")
               });
               Dom.removeClass(this.dragFolderHighlight, "dndFolderHighlight");
               return;
            }

            // Refresh the document list...
            this.docLib._updateDocList.call(this.docLib);

            // Update the tree if a folder has been moved...
            var moved = toMoveRecord.getData();
            if (moved.node.isContainer)
            {
               YAHOO.Bubbling.fire("folderMoved",
               {
                  multiple: true,
                  nodeRef: moved.nodeRef,
                  destination: path
               });
            }
         };
         // destination: targetNode.location.path + "/" + targetNode.location.file

         // Failure callback function:
         // If the move operation has failed then animate the proxy to return it to the
         // location from which it was dragged. Also, post a failure message.
         var fnFailure = function DLCMT__onOK_failure(p_data)
         {
            this._inFlight = false; // Indicate that a request is no longer "in-flight"
            this.animateResult(this.getDragEl(), this.getEl());
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.docLib.msg("message.file-dnd-move.failure")
            });
            Dom.removeClass(this.dragFolderHighlight, "dndFolderHighlight");
         };

         // Make the request to move the dragged object to the target
         this.docLib.modules.actions.genericAction(
         {
            success:
            {
               callback:
               {
                  fn: fnSuccess,
                  scope: this
               }
            },
            failure:
            {
               callback:
               {
                  fn: fnFailure,
                  scope: this
               }
            },
            webscript:
            {
               method: Alfresco.util.Ajax.POST,
               name: webscriptName,
               params:
               {
                  nodeRef: nodeRef.uri
               }
            },
            wait:
            {
               message: this.docLib.msg("message.please-wait")
            },
            config:
            {
               requestContentType: Alfresco.util.Ajax.JSON,
               dataObj:
               {
                  nodeRefs: multipleFiles,
                  parentId: this.docLib.doclistMetadata.parent.nodeRef
               }
            }
         });
      },

      /**
       * The id of the current window timeout. This should only be non-null if a proxy has been
       * dropped onto a valid drop target that was NOT part of the DocumentList DataTable widget.
       * This id is used to clear the current timeout associated with a drop if the target owner
       * responds with the node ref.
       *
       * @property _currTimeoutId
       * @type int
       */
      _currTimeoutId: null,

      /**
       * Callback function that is included in the payload of the "dropTargetOwnerRequest" event.
       * This can then be used by a subscriber to the event that claims ownership of the target to
       * generate the move using the associated nodeRef.
       *
       * @method onDropTargetOwnerCallBack
       * @property nodeRef The nodeRef to move the dragged object to.
       */
      onDropTargetOwnerCallBack: function DL_DND_onDropTargetOwnerCallBack(nodeRef, path)
      {
         // Clear the timeout that was set...
         this._clearTimeout();

         // Move the document/folder...
         var node = new Alfresco.util.NodeRef(nodeRef);
         this._performMove(node, path);
      },

      /**
       * Clears the timeout that is set when a proxy is dropped onto a valid drop target that is
       * NOT part of the DocumentList DataTable widget. This clears the timeout, resets the timeout
       * id to null and removes the inflight status of the drop operation.
       *
       * @method _clearTimeout
       */
      _clearTimeout: function DL_DND__clearTimeout()
      {
         if (this._currTimeoutId != null)
         {
            window.clearTimeout(this._currTimeoutId);
            this._currTimeoutId = null;
            this._inFlight = false;
         }
      },

      /**
       * Creates a timeout for handling drops onto valid drop targets that are NOT part of the
       * DocumentList DataTable widget. This method is called after firing a "dropTargetOwnerRequest"
       * to wait for the owner of the target to respond with the nodeRef associated with the target.
       * If a response is not sent then a failure will be registered.
       *
       * @method _setFailureTimeout
       */
      _setFailureTimeout: function DL_DND__setFailureTimeout()
      {
         // Clear any previous timeout...
         this._clearTimeout();
         var _this = this;
         this._currTimeoutId = window.setTimeout(function()
         {
            // An attempt was made to drop a document or folder into a document - NOT a folder
            _this.animateResult(_this.getDragEl(), _this.getEl());
            _this._inFlight = false
            _this._currTimeoutId = null;
         }, 500);
      },

      /**
       * If the element the proxy has been dragged over is a folder, then style class indicating
       * that it is a viable drop target is added.
       *
       * @param e The event object
       * @param id The id of the element that the proxy has been dragged over
       */
      onDragOver: function DL_DND_onDragOver(e, id)
      {
          var destEl = Dom.get(id);
          if (destEl.tagName == "IMG" || destEl.className == "droppable")
          {
             this.dragFolderHighlight = Dom.getAncestorByClassName(destEl, "folder");
             Dom.addClass(this.dragFolderHighlight, "dndFolderHighlight");
          }
          else if (Dom.hasClass(destEl, "documentDroppableHighlights"))
          {
             // Fire an event indicating a document drag over
             var payload =
             {
                elementId: id,
                event: e
             }
             YAHOO.Bubbling.fire("documentDragOver", payload);
          }
      },

      /**
       * If the element the proxy has been dragged out of is a folder, then style class indicating
       * that it is a viable drop target is removed.
       *
       * @param e The event object
       * @param id The id of the element that the proxy has been dragged out of
       */
      onDragOut: function DL_DND_onDragOut(e, id)
      {
         var destEl = Dom.get(id);
         if (destEl.tagName == "IMG" || destEl.className == "droppable")
         {
            this.dragFolderHighlight = Dom.getAncestorByClassName(destEl, "folder");
            Dom.removeClass(this.dragFolderHighlight, "dndFolderHighlight");
         }
         else if (Dom.hasClass(destEl, "documentDroppableHighlights"))
         {
            // Fire an event indicating a document drag out
            var payload =
            {
               elementId: id,
               event: e
            }
            YAHOO.Bubbling.fire("documentDragOut", payload);
         }
      }
   });

   /**
    * DocumentList constructor.
    *
    * @param htmlId {String} The HTML id of the parent element
    * @return {Alfresco.DocumentList} The new DocumentList instance
    * @constructor
    */
   Alfresco.DocumentList = function(htmlId)
   {
      Alfresco.DocumentList.superclass.constructor.call(this, "Alfresco.DocumentList", htmlId, ["button", "menu", "container", "datasource", "datatable", "paginator", "json", "history"]);

      /*
       * Initialise prototype properties
       */
      this.currentPath = "";
      this.currentPage = 1;
      this.totalRecords = 0;
      this.totalRecordsUpper = null;
      this.hideMoreActionsFn = null;
      this.state =
      {
         actionEditOfflineActive: false
      };
      this.currentFilter =
      {
         filterId: "path",
         filterData: ""
      };
      this.actions = {};
      this.selectedFiles = {};
      this.afterDocListUpdate = [];
      this.doclistMetadata = {};
      this.previewTooltips = [];
      this.insituEditors = [];
      this.dynamicControls = [];
      this.sortControls = [];
      this.dragAndDropAllowed = true;
      this.dragAndDropEnabled = false;
      this.dragEventRefCount = 0;
      this.actionsView = "browse";
      this.renderers = {};
      this.viewRenderers = {};
      this.dataSourceUrl = $combine(Alfresco.constants.URL_SERVICECONTEXT, "components/documentlibrary/data/doclist/");

      /**
       * Decoupled event listeners
       */
      // Specific event handlers
      YAHOO.Bubbling.on("activateDynamicControls", this.onActivateDynamicControls, this);
      YAHOO.Bubbling.on("activateSortControls", this.onActivateSortControls, this);
      YAHOO.Bubbling.on("deactivateAllControls", this.onDeactivateAllControls, this);
      YAHOO.Bubbling.on("deactivateDynamicControls", this.onDeactivateDynamicControls, this);
      YAHOO.Bubbling.on("deactivateSortControls", this.onDeactivateSortControls, this);
      YAHOO.Bubbling.on("metadataRefresh", this.onDocListRefresh, this);
      YAHOO.Bubbling.on("fileRenamed", this.onFileRenamed, this);
      YAHOO.Bubbling.on("changeFilter", this.onChangeFilter, this);
      YAHOO.Bubbling.on("filterChanged", this.onFilterChanged, this);
      YAHOO.Bubbling.on("folderCreated", this.onDocListRefresh, this);
      YAHOO.Bubbling.on("nodeCreated", this.onDocListRefresh, this);
      YAHOO.Bubbling.on("folderRenamed", this.onFileRenamed, this);
      YAHOO.Bubbling.on("highlightFile", this.onHighlightFile, this);
      YAHOO.Bubbling.on("registerRenderer", this.onRegisterRenderer, this);
      YAHOO.Bubbling.on("registerViewRenderer", this.onRegisterViewRenderer, this);
      YAHOO.Bubbling.on("registerAction", this.onRegisterAction, this);
      YAHOO.Bubbling.on("resizerChanged", this.onResizerChanged, this);
      // File actions which may be part of a multi-file action set
      YAHOO.Bubbling.on("fileCopied", this.onFileAction, this);
      YAHOO.Bubbling.on("fileDeleted", this.onFileAction, this);
      YAHOO.Bubbling.on("fileMoved", this.onFileAction, this);
      YAHOO.Bubbling.on("filePermissionsUpdated", this.onFileAction, this);
      YAHOO.Bubbling.on("folderCopied", this.onFileAction, this);
      YAHOO.Bubbling.on("folderDeleted", this.onFileAction, this);
      YAHOO.Bubbling.on("folderMoved", this.onFileAction, this);
      YAHOO.Bubbling.on("folderPermissionsUpdated", this.onFileAction, this);
      // Multi-file actions
      YAHOO.Bubbling.on("filesCopied", this.onDocListRefresh, this);
      YAHOO.Bubbling.on("filesDeleted", this.onDocListRefresh, this);
      YAHOO.Bubbling.on("filesMoved", this.onDocListRefresh, this);
      YAHOO.Bubbling.on("filesPermissionsUpdated", this.onDocListRefresh, this);

      return this;
   };

   /**
    * Extend from Alfresco.component.Base
    */
   YAHOO.extend(Alfresco.DocumentList, Alfresco.component.Base);

   /**
    * Augment prototype with Actions module
    */
   YAHOO.lang.augmentProto(Alfresco.DocumentList, Alfresco.doclib.Actions);

   /**
    * Custom field generator functions
    */

   /**
    * Generate "changeFilter" event mark-up suitable for element attribute.
    *
    * @method generateFilterMarkup
    * @param filter {object} Object literal containing new filter parameters
    * @return {string} Mark-up for use in node attribute
    */
   Alfresco.DocumentList.generateFilterMarkup = function DL_generateFilterMarkup(filter)
   {
      var filterObj = Alfresco.util.cleanBubblingObject(filter);
      return YAHOO.lang.substitute("{filterOwner}|{filterId}|{filterData}|{filterDisplay}", filterObj, function(p_key, p_value, p_meta)
      {
         return typeof p_value === "undefined" ? "" : window.escape(p_value);
      });
   };

   /**
    * Generate "changeFilter" event mark-up specifically for path changes
    *
    * @method generatePathMarkup
    * @param locn {object} Location object containing path and folder name to navigate to
    * @return {string} Mark-up for use in node attribute
    */
   Alfresco.DocumentList.generatePathMarkup = function DL_generatePathMarkup(locn)
   {
      return Alfresco.DocumentList.generateFilterMarkup(
      {
         filterId: "path",
         filterData: $combine(locn.path, locn.file)
      });
   };

   /**
    * Generate "changeFilter" event mark-up specifically for category changes
    *
    * @method generateCategoryMarkup
    * @param category {Array} category[0] is name, category[1] is qnamePath
    * @return {string} Mark-up for use in node attribute
    */
   Alfresco.DocumentList.generateCategoryMarkup = function DL_generateCategoryMarkup(category)
   {
      return Alfresco.DocumentList.generateFilterMarkup(
      {
         filterId: "category",
         filterData: $combine(category[1], category[0])
      });
   };

   /**
    * Generate URL for a file- or folder-link that may be located within a different Site
    *
    * @method generateFileFolderLinkMarkup
    * @param record {object} Item record
    * @return {string} Mark-up for use in node attribute
    * <pre>
    *       Folders: Navigate into the folder (ajax)
    *       Documents: Navigate to the details page (page)
    *    Links: Same site (or Repository mode)
    *       Links to folders: Navigate into the folder (ajax)
    *       Links to documents: Navigate to the details page (page)
    *    Links: Different site
    *       Links to folders: Navigate into the site & folder (page)
    *       Links to documents: Navigate to the details page within the site (page)
    * </pre>
    */
   Alfresco.DocumentList.generateFileFolderLinkMarkup = function DL_generateFileFolderLinkMarkup(scope, record)
   {
      var jsNode = record.jsNode,
         html;

      if (jsNode.isLink && $isValueSet(scope.options.siteId) && record.location.site && record.location.site.name !== scope.options.siteId)
      {
         if (jsNode.isContainer)
         {
            html = $siteURL("documentlibrary?path=" + encodeURIComponent(record.location.path),
            {
               site: record.location.site.name
            });
         }
         else
         {
            html = scope.getActionUrls(record, record.location.site.name).documentDetailsUrl;
         }
      }
      else
      {
         if (jsNode.isContainer)
         {
            if (record.parent.isContainer || record.node.isContainer)
            {
               // handle folder parent node
               html = '#" class="filter-change" rel="' + Alfresco.DocumentList.generatePathMarkup(record.location);
            }
            else if (record.location.path === "/")
            {
               // handle Repository root parent node (special store_root type - not a folder)
               html = '#" class="filter-change" rel="' + Alfresco.DocumentList.generateFilterMarkup(
                  {
                     filterId: "path",
                     filterData: $combine(record.location.path, "")
                  });
            }
            else
            {
               // handle unknown parent node types
               html = '#';
            }
         }
         else
         {
            var actionUrls = scope.getActionUrls(record);
            if (jsNode.isLink && jsNode.linkedNode.isContainer)
            {
               html = actionUrls.folderDetailsUrl;
            }
            else
            {
               html = actionUrls.documentDetailsUrl;
            }
         }
      }

      return '<a href="' + html + '">';
   };

   /**
    * Generate URL to thumbnail image
    *
    * @method generateThumbnailUrl
    * @param record {object} File record
    * @param renditionName {string} the named thumbnail rendition to grab, default is doclib
    * @return {string} URL to thumbnail
    */
   Alfresco.DocumentList.generateThumbnailUrl = function DL_generateThumbnailUrl(record, renditionName)
   {
      var jsNode = record.jsNode,
         nodeRef = jsNode.isLink ? jsNode.linkedNode.nodeRef : jsNode.nodeRef;
      if (jsNode.isLink && !nodeRef)
      {
         // broken link has no thumbnail
         return '';
      }
      if (renditionName == null)
          renditionName = "doclib";
      return Alfresco.util.generateThumbnailUrl(jsNode, renditionName);
   };

   /**
    * Generate "Favourite" UI
    *
    * @method generateFavourite
    * @param scope {object} DocumentLibrary instance
    * @param record {object} File record
    * @return {string} HTML mark-up for Favourite UI
    */
   Alfresco.DocumentList.generateFavourite = function DL_generateFavourite(scope, record)
   {
      var jsNode = record.jsNode,
         i18n = "favourite." + (jsNode.isContainer ? "folder." : "document."),
         html = "";

      if (record.isFavourite)
      {
         html = '<a href="#" class="favourite-action enabled" title="' + scope.msg(i18n + "remove.tip") + '" tabindex="0"></a>';
      }
      else
      {
         html = '<a href="#" class="favourite-action" title="' + scope.msg(i18n + "add.tip") + '" tabindex="0">' + scope.msg(i18n + "add.label") + '</a>';
      }

      return html;
   };

   /**
    * Generate "Likes" UI
    *
    * @method generateLikes
    * @param scope {object} DocumentLibrary instance
    * @param record {object} File record
    * @return {string} HTML mark-up for Likes UI
    */
   Alfresco.DocumentList.generateLikes = function DL_generateLikes(scope, record)
   {
      var node = record.node,
         likes = record.likes,
         i18n = "like." + (node.isContainer ? "folder." : "document."),
         html = "";

      if (likes.isLiked)
      {
         html = '<a href="#" class="like-action enabled" title="' + scope.msg(i18n + "remove.tip") + '" tabindex="0"></a>';
      }
      else
      {
         html = '<a href="#" class="like-action" title="' + scope.msg(i18n + "add.tip") + '" tabindex="0">' + scope.msg(i18n + "add.label") + '</a>';
      }

      html += '<span class="likes-count">' + $html(likes.totalLikes) + '</span>';

      return html;
   };

   /**
    * Generate "Comments" UI
    *
    * @method generateComments
    * @param scope {object} DocumentLibrary instance
    * @param record {object} File record
    * @return {string} HTML mark-up for Comments UI
    */
   Alfresco.DocumentList.generateComments = function DL_generateComments(scope, record)
   {
      var node = record.node,
         actionUrls = scope.getActionUrls(record),
         url = actionUrls[node.isContainer ? "folderDetailsUrl" : "documentDetailsUrl"] + "#comment",
         i18n = "comment." + (node.isContainer ? "folder." : "document.");

      var hasComments = (node.properties["fm:commentCount"] !== undefined);
      
      var html = '<a href="' + url + '" class="comment' + (hasComments ? " hasComments" : "") +'" title="' + scope.msg(i18n + "tip") + '" tabindex="0">' + scope.msg(i18n + "label") + '</a>';
      if (hasComments)
      {
         html += '<span class="comment-count">' + $html(node.properties["fm:commentCount"]) + '</span>';
      }
      return html;
   };

   /**
    * Generate "Quickshare" UI
    *
    * @method generateQuickShare
    * @param scope {object} DocumentLibrary instance
    * @param record {object} File record
    * @return {string} HTML mark-up for Comments UI
    */
   Alfresco.DocumentList.generateQuickShare = function DL_generateQuickShare(scope, record)
   {
      // Create QuickShare widget (will returns its own markup)
      return new Alfresco.QuickShare().setOptions({
         nodeRef: record.jsNode.nodeRef,
         displayName: record.displayName
      }).display(record.jsNode.properties.qshare_sharedId, record.jsNode.properties.qshare_sharedBy);
   };

   /**
    * Generate User Profile link
    *
    * @method generateUserLink
    * @param scope {object} DocumentLibrary instance
    * @param oUser {object} Object literal container user data
    * @return {string} HTML mark-up for user profile link
    */
   Alfresco.DocumentList.generateUserLink = function DL_generateUserLink(scope, oUser)
   {
      if (oUser.isDeleted === true)
      {
         return '<span>' + scope.msg("details.user.deleted", $html(oUser.userName)) + '</span>';
      }
      return $userProfile(oUser.userName, YAHOO.lang.trim(oUser.firstName + " " + oUser.lastName));
   };


   /**
    * Augment prototype with main class implementation, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentObject(Alfresco.DocumentList.prototype,
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
          * Flag indicating whether sort direction is ascending (true) or descending (false)
          *
          * @property sortAscending
          * @type boolean
          */
         sortAscending: true,

         /**
          * Flag indicating whether folders are visible or not.
          *
          * @property showFolders
          * @type boolean
          */
         showFolders: false,

         /**
          * Flag indicating whether the list shows a detailed view or a simple one.
          *
          * @property simpleView
          * @type boolean
          * @deprecated use viewRendererName instead
          */
         simpleView: null,

         /**
          * Indicates which viewRenderer to use when displaying the content set.
          *
          * Similar to the previous boolean switch called simpleView.
          *
          * @property viewRendererName
          * @type string
          * @default "detailed"
          */
         viewRendererName: "detailed",

         /**
          * An array containing the order of the viewRenderer keys
          *
          * @property viewRendererNames
          * @type array
          * @default ["simple", "detailed", "gallery"]
          */
         viewRendererNames: [ "simple", "detailed", "gallery" ],

         /**
          * @property viewRenderers
          * @type array
          * @default []
          */
         viewRenderers: [],

         /**
          * Flag indicating whether pagination is available or not.
          *
          * @property usePagination
          * @type boolean
          * @default false
          */
         usePagination: false,

         /**
          * Current siteId. Not used in Repository working mode.
          *
          * @property siteId
          * @type string
          */
         siteId: "",

         /**
          * ContainerId representing root container. Not used in Repository working mode.
          *
          * @property containerId
          * @type string
          * @default "documentLibrary"
          */
         containerId: "documentLibrary",

         /**
          * Current root node. Not used in Site working mode.
          *
          * @property rootNode
          * @type string
          */
         rootNode: null,

         /**
          * Initial page to show on load (otherwise taken from URL hash).
          *
          * @property initialPage
          * @type int
          */
         initialPage: 1,

         /**
          * Number of items per page
          *
          * @property pageSize
          * @type int
          */
         pageSize: 50,

         /**
          * Initial filter to show on load.
          *
          * @property initialFilter
          * @type object
          */
         initialFilter: {},

         /**
          * Delay before showing "loading" message for slow data requests
          *
          * @property loadingMessageDelay
          * @type int
          * @default 1000
          */
         loadingMessageDelay: 1000,

         /**
          * FileName to highlight on initial DataTable render.
          *
          * @property highlightFile
          * @type string
          */
         highlightFile: null,

         /**
          * Holds IDs to register preview tooltips with.
          *
          * @property previewTooltips
          * @type array
          */
         previewTooltips: null,

         /**
          * Holds IDs to register insitu editors with.
          *
          * @property insituEditors
          * @type array
          */
         insituEditors: null,

         /**
          * Number of multi-file uploads before grouping the Activity Post
          *
          * @property groupActivitiesAt
          * @type int
          * @default 5
          */
         groupActivitiesAt: 5,

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
          * Replication URL Mapping details
          *
          * @property replicationUrlMapping
          * @type object
          */
         replicationUrlMapping: {},

         /**
          * Flag to indicate the list may be updated as a results of a REST API call
          *
          * @property listUpdated
          * @type boolean
          */
         listUpdated: false,

         /**
          * Whether the Repo Browser is in use or not
          *
          * @property repositoryBrowsing
          * @type boolean
          */
         repositoryBrowsing: true,

         /**
          * Whether the cm:title property is in use or not
          *
          * @property useTitle
          * @type boolean
          */
         useTitle: true,

         /**
          * Whether the user viewing the document library is a manager of the site.
          *
          * @property userIsSiteManager
          * @type boolean
          */
         userIsSiteManager: false,

         /**
          * Where to insert the "More..." actions split
          *
          * @property actionsSplitAt
          * @type number
          * @default 3
          */
         actionsSplitAt: 3
      },

      /**
       * Keeps track of different states
       */
      state:
      {
         /**
          * True if an an edit offline ajax call is in process
          *
          * @property: actionEditOfflineActive
          * @type: boolean
          * @default: false
          */
         actionEditOfflineActive: false
      },

      /**
       * Current path being browsed.
       *
       * @property currentPath
       * @type string
       */
      currentPath: null,

      /**
       * Current page being browsed.
       *
       * @property currentPage
       * @type int
       * @default 1
       */
      currentPage: null,

      /**
       * Total number of records (documents + folders) in the currentPath.
       *
       * @property totalRecords
       * @type int
       * @default 0
       */
      totalRecords: null,

      /**
       * Current filter to filter document list.
       *
       * @property currentFilter
       * @type object
       */
      currentFilter: null,

      /**
       * FileUpload module instance.
       *
       * @property fileUpload
       * @type Alfresco.FileUpload
       */
      fileUpload: null,

      /**
       * Object container for storing action markup elements.
       *
       * @property actions
       * @type object
       */
      actions: null,

      /**
       * Object literal of selected states for visible files (indexed by nodeRef).
       *
       * @property selectedFiles
       * @type object
       */
      selectedFiles: null,

      /**
       * Current actions menu being shown
       *
       * @property currentActionsMenu
       * @type object
       * @default null
       */
      currentActionsMenu: null,

      /**
       * "More Actions" pop-up handler
       *
       * @property hideMoreActionsFn
       * @type function
       * @default null
       */
      hideMoreActionsFn: null,

      /**
       * Deferred function calls for after a document list update
       *
       * @property afterDocListUpdate
       * @type array
       */
      afterDocListUpdate: null,

      /**
       * Metadata returned by doclist data webscript
       *
       * @property doclistMetadata
       * @type object
       * @default null
       */
      doclistMetadata: null,

      /**
       * Dynamic controls that take part in the deactivateDynamicControls event
       *
       * @property dynamicControls
       * @type array
       */
      dynamicControls: null,

      /**
       * List of controls that are related to sorting - they can be enabled or disabled
       * as a group depending on the filter that is currently displayed to the user.
       *
       * @property sortControls
       * @type array
       */
      sortControls: null,

      /**
       * Indicates whether or not we allow the HTML5 drag and drop capability
       *
       * @property dragAndDropAllowed
       * @type boolean
       */
      dragAndDropAllowed: null,

      /**
       * Indicates whether or not the browser supports the HTML5 drag and drop capability
       *
       * @property dragAndDropEnabled
       * @type boolean
       */
      dragAndDropEnabled: null,

      /**
       * Drag and drop dragEnter / dragLeave event reference counter
       *
       * @property dragEventRefCount
       * @type number
       */
      dragEventRefCount: null,

      /**
       * Tracks currently highlighted folder when dragging files
       *
       * @property dragFolderHighlight
       * @type boolean
       */
      dragFolderHighlight: null,

      /**
       * Registered metadata renderers.
       * Register new renderers via registerRenderer() or "registerRenderer" bubbling event
       *
       * @property renderers
       * @type object
       */
      renderers: null,

      /**
       * Registered view renderers.
       * Register new renderers via registerviewRenderer() or "registerViewRenderer" bubbling event
       *
       * @property viewRenderers
       * @type array
       */
      viewRenderers: null,

      /**
       * Indicates whether or not the current user has permission to upload documents. This is initialised to
       * false and will remain that way until it is updated by the _setupDataSource function.
       *
       * @property _userCanUpload
       * @type boolean
       * @default false
       */
      _userCanUpload: false,

      /**
       * Fired by YUI when parent element is available for scripting.
       * Initial History Manager event registration
       *
       * @method onReady
       */
      onReady: function DL_onReady()
      {
         // Reference to self used by inline functions
         var me = this;

         // Detect whether or not HTML5 drag and drop is supported...
         this.dragAndDropEnabled = this.dragAndDropAllowed && ('draggable' in document.createElement('span')) && YAHOO.env.ua.mobile === null;

         // If the viewRenderer in the user preference is no longer available, use first available
         // Also determine the index value of the selected viewRenderer from viewRendererNames
         var isViewRendererAvailable = false;
         var selectedViewRendererIndex = 1;
         for (var i = 0, ii = this.options.viewRendererNames.length; i < ii; i++)
         {
            if (this.options.viewRendererNames[i] === this.options.viewRendererName)
            {
               isViewRendererAvailable = true;
               selectedViewRendererIndex = i;
               break;
            }
         }
         if (!isViewRendererAvailable && this.options.viewRendererNames.length > 0)
         {
            this.options.viewRendererName = this.options.viewRendererNames[0];
         }

         // Keep a local copy of the users view preference... we need to ensure that this is kept up-to-date
         // as preferences are set to ensure we can switch back to the user preference if when the path changes
         // from a container with no default view to a container without a default view...
         this.userPreferredView = this.options.viewRendererName;

         // Set-up default metadata renderers
         this._setupMetadataRenderers();

         // Set-up YUI History Managers
         this._setupHistoryManagers();

         // This checks to see whether or not the toolbar are in the template...
         // It has been intentionally placed in a conditional section as the Dojo based DocumentList widget that wraps
         // this one intentionally removes the toolbar. Checking the existing of the main <div> element in the template
         // we are able to have this one JavaScript file work in both YUI and Dojo widgets without error
         if (Dom.get(this.id + "-doclistBar"))
         {
            // Sort Direction button
            this.widgets.sortAscending = Alfresco.util.createYUIButton(this, "sortAscending-button", this.onSortAscending);
            if (this.widgets.sortAscending !== null)
            {
               this.widgets.sortAscending.set("title", this.msg(this.options.sortAscending ? "button.sort.descending" : "button.sort.ascending"));
               if (!this.options.sortAscending)
               {
                  Dom.addClass(this.widgets.sortAscending.get("element"), "sort-descending");
               }
               this.sortControls.push(this.widgets.sortAscending);
            }

            // Sort Field menu button
            this.widgets.sortField = Alfresco.util.createYUIButton(this, "sortField-button", this.onSortField, {
               type: "menu",
               menu: "sortField-menu",
               lazyloadmenu: false
            });
            if (this.widgets.sortField !== null)
            {
               this.sortControls.push(this.widgets.sortField);

               // Set the initial menu label
               var menuItems = this.widgets.sortField.getMenu().getItems(),
                   index;

               for (index in menuItems)
               {
                  if (menuItems.hasOwnProperty(index))
                  {
                     if (menuItems[index].value === this.options.sortField)
                     {
                        this.widgets.sortField.set("label", menuItems[index].cfg.getProperty("text") + " " + Alfresco.constants.MENU_ARROW_SYMBOL);
                        break;
                     }
                  }
               }
            }

            // Hide/Show Folders button
            this.widgets.showFolders = Alfresco.util.createYUIButton(this, "showFolders-button", this.onShowFolders, {
               type: "checkbox",
               checked: this.options.showFolders
            });
            if (this.widgets.showFolders !== null)
            {
               this.widgets.showFolders.set("title", this.msg(this.options.showFolders ? "button.folders.hide" : "button.folders.show"));
               this.dynamicControls.push(this.widgets.showFolders);
            }

            // ViewRenderer Select List button, HTML id not renamed yet for backwards compatibility
            if (Dom.get(this.id + "-simpleDetailed"))
            {
               this.widgets.viewRendererSelect = new YAHOO.widget.ButtonGroup(this.id + "-simpleDetailed");
               if (this.widgets.viewRendererSelect !== null)
               {
                  this.widgets.viewRendererSelect.check(selectedViewRendererIndex);
                  this.widgets.viewRendererSelect.on("checkedButtonChange", this.onViewRendererSelect, this.widgets.viewRendererSelect, this);
                  this.dynamicControls.push(this.widgets.viewRendererSelect);
               }
            }

            // File Select menu button
            this.widgets.fileSelect = Alfresco.util.createYUIButton(this, "fileSelect-button", this.onFileSelect, {
               type: "menu",
               menu: "fileSelect-menu"
            });
            if (this.widgets.fileSelect !== null)
            {
               this.dynamicControls.push(this.widgets.fileSelect);
            }

            if (Dom.get(this.id + "-options-button"))
            {
               this.widgets.optionsMenu = Alfresco.util.createYUIButton(this, "options-button", this.onOptionSelect, {
                  type: "menu",
                  menu: "options-menu"
               });
               if (this.widgets.optionsMenu !== null)
               {
                  this.dynamicControls.push(this.widgets.optionsMenu);
               }
            }
         }

         // Services
         this.services.preferences = new Alfresco.service.Preferences();
         this.services.likes = new Alfresco.service.Ratings(Alfresco.service.Ratings.LIKES);

         // Set-up default view renderers
         this._setupViewRenderers();

         // DataSource set-up and event registration
         this._setupDataSource();

         // DataTable set-up and event registration
         this._setupDataTable();

         // Hook action events
         var fnActionHandler = function DL_fnActionHandler(layer, args)
         {
            // Hide the "more actions" menu to avoid double clicking
            // ACE-849
            if (me.hideMoreActionsFn)
            {
               me.hideMoreActionsFn.call(me);
            }
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               if (typeof me[owner.id] === "function")
               {
                  args[1].stop = true;
                  var elIdentifier = args[1].target;
                  if (typeof me.viewRenderers[me.options.viewRendererName] === "object")
                  {
                     elIdentifier = me.viewRenderers[me.options.viewRendererName].getDataTableRecordIdFromRowElement(me, args[1].target);
                  }
                  var record = me.widgets.dataTable.getRecord(elIdentifier).getData();
                  me[owner.id].call(me, record, owner);
               }
            }
            return true;
         };
         YAHOO.Bubbling.addDefaultAction("action-link", fnActionHandler);
         YAHOO.Bubbling.addDefaultAction("show-more", fnActionHandler);

         // Hook like/unlike events
         var fnLikesHandler = function DL_fnLikesHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               me.onLikes.call(me, args[1].target.offsetParent, owner);
            }
            return true;
         };
         YAHOO.Bubbling.addDefaultAction("like-action", fnLikesHandler);

         // Hook favourite document/folder events
         var fnFavouriteHandler = function DL_fnFavouriteHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               me.onFavourite.call(me, args[1].target.offsetParent, owner);
            }
            return true;
         };
         YAHOO.Bubbling.addDefaultAction("favourite-action", fnFavouriteHandler);

         // Hook filter change events
         var fnChangeFilterHandler = function DL_fnChangeFilterHandler(layer, args)
         {
            var owner = args[1].anchor;
            if (owner !== null)
            {
               var filter = owner.rel,
                  filters,
                  filterObj = {};
               if (filter && filter !== "")
               {
                  args[1].stop = true;
                  filters = filter.split("|");
                  filterObj =
                  {
                     filterOwner: window.unescape(filters[0] || ""),
                     filterId: window.unescape(filters[1] || ""),
                     filterData: window.unescape(filters[2] || ""),
                     filterDisplay: window.unescape(filters[3] || "")
                  };
                  Alfresco.logger.debug("DL_fnChangeFilterHandler", "changeFilter =>", filterObj);
                  YAHOO.Bubbling.fire("changeFilter", filterObj);
               }
            }
            return true;
         };
         YAHOO.Bubbling.addDefaultAction("filter-change", fnChangeFilterHandler);

         // DocLib Actions module
         this.modules.actions = new Alfresco.module.DoclibActions();

         // Resize event handler - adjusts the filename container DIV to a size relative to the container width
         Event.addListener(window, "resize", this._resizeRowContainers, this, true);

         // Continue only when History Manager fires its onReady event
         YAHOO.util.History.onReady(this.onHistoryManagerReady, this, true);

         // Initialize the browser history management library
         try
         {
             YAHOO.util.History.initialize("yui-history-field", "yui-history-iframe");
         }
         catch(e)
         {
            /*
             * The only exception that gets thrown here is when the browser is
             * not supported (Opera, or not A-grade)
             */
            Alfresco.logger.error(this.name + ": Couldn't initialize HistoryManager.", e);
            this.onHistoryManagerReady();
         }

         var documentListFullScreen = new Alfresco.widget.FullScreen(this.id);
         this.fullscreen = documentListFullScreen;
         YAHOO.util.Event.addListener(Dom.get(this.id + '-fullscreen-exit-button'), 'click', function(e)
         {
              documentListFullScreen.toggleFullScreen();
         });
         YAHOO.Bubbling.on("fullScreenEnterComplete", function(layer, args)
         {
            if (args[1] && args[1].eventGroup == this.id &&
                  (typeof this.viewRenderers[this.options.viewRendererName] === "object") &&
                  this.viewRenderers[this.options.viewRendererName].currentResizeCallback)
            {
                  this.viewRenderers[this.options.viewRendererName].currentResizeCallback();
            }
         }, this);
         YAHOO.Bubbling.on("fullScreenExitComplete", function(layer, args)
         {
            if (args[1] && args[1].eventGroup == this.id &&
                  (typeof this.viewRenderers[this.options.viewRendererName] === "object") &&
                  this.viewRenderers[this.options.viewRendererName].currentResizeCallback)
            {
                  this.viewRenderers[this.options.viewRendererName].currentResizeCallback();
            }
         }, this);

         // Remove the display:none style set in the documentlist_v2.lib.ftl that ensure the options menu DIV
         // isn't shown before styles are applied to it...
         Dom.setStyle(this.id + "-options-menu", "display", "inherit");

         YAHOO.Bubbling.fire("postDocumentListOnReady",
         {
             scope: this,
             eventGroup: this.id
         });
      },

      /**
       * Insitu Editor callback function
       *
       * @method _insituCallback
       * @protected
       * @param response {object} AJAX response
       * @param record {YAHOO.widget.Record} Record for the item being edited
       */
      _insituCallback: function DL__insituCallback(response, record)
      {
         // Reload the node's metadata
         var jsNode = record.jsNode,
            nodeRef = jsNode.nodeRef,
            webscriptPath = "components/documentlibrary/data",
            libraryRootParam = "";
         if ($isValueSet(this.options.siteId))
         {
            // Site mode
            webscriptPath += "/site/" + encodeURIComponent(this.options.siteId)
         }
         else
         {
            // Repository mode
            libraryRootParam = "&libraryRoot=" + encodeURIComponent(this.options.rootNode.toString());
         }
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.URL_SERVICECONTEXT + webscriptPath + "/node/" + nodeRef.uri +
                  "?filter=" + encodeURIComponent(this.currentFilter.filterId) +
                  "&view=" + this.actionsView + "&noCache=" + new Date().getTime() +
                  libraryRootParam,
            successCallback:
            {
               fn: function DL_insituCallback_refreshSuccess(response)
               {
                  response.json.item.jsNode = new Alfresco.util.Node(response.json.item.node);
                  YAHOO.Bubbling.fire(jsNode.isContainer ? "folderRenamed" : "fileRenamed",
                  {
                     file: response.json.item
                  });
                  // Prevent hide call which briefly shows stale data
                  return false;
               },
               scope: this
            },
            failureCallback:
            {
               fn: function DL_insituCallback_refreshFailure(response)
               {
                  // No-op for now
               },
               scope: this
            }
         });
      },


      /**
       * DataTable Cell Renderers
       */

      /**
       * Returns selector custom datacell formatter
       *
       * @method fnRenderCellSelected
       */
      fnRenderCellSelected: function DL_fnRenderCellSelected()
      {
         var scope = this;

         /**
          * Selector custom datacell formatter
          *
          * @method renderCellSelected
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function DL_renderCellSelected(elCell, oRecord, oColumn, oData)
         {
            if (typeof scope.viewRenderers[scope.options.viewRendererName] === "object")
            {
               scope.viewRenderers[scope.options.viewRendererName].renderCellSelected(scope, elCell, oRecord, oColumn, oData);
            }
         };
      },

      /**
       * Returns status custom datacell formatter
       *
       * @method fnRenderCellStatus
       */
      fnRenderCellStatus: function DL_fnRenderCellStatus()
      {
         var scope = this;

         /**
          * Status custom datacell formatter
          *
          * @method renderCellStatus
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function DL_renderCellStatus(elCell, oRecord, oColumn, oData)
         {
            if (typeof scope.viewRenderers[scope.options.viewRendererName] === "object")
            {
               scope.viewRenderers[scope.options.viewRendererName].renderCellStatus(scope, elCell, oRecord, oColumn, oData);
            }
         };
      },

      /**
       * Returns thumbnail custom datacell formatter
       *
       * @method fnRenderCellThumbnail
       */
      fnRenderCellThumbnail: function DL_fnRenderCellThumbnail()
      {
         var scope = this;

         /**
          * Thumbnail custom datacell formatter
          *
          * @method renderCellThumbnail
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function DL_renderCellThumbnail(elCell, oRecord, oColumn, oData)
         {
            if (typeof scope.viewRenderers[scope.options.viewRendererName] === "object")
            {
               scope.viewRenderers[scope.options.viewRendererName].renderCellThumbnail(scope, elCell, oRecord, oColumn, oData);
            }
         };
      },

      /**
       * Returns description/detail custom datacell formatter
       *
       * @method fnRenderCellDescription
       */
      fnRenderCellDescription: function DL_fnRenderCellDescription()
      {
         var scope = this;

         /**
          * Description/detail custom datacell formatter
          *
          * @method renderCellDescription
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function DL_renderCellDescription(elCell, oRecord, oColumn, oData)
         {
            if (typeof scope.viewRenderers[scope.options.viewRendererName] === "object")
            {
               scope.viewRenderers[scope.options.viewRendererName].renderCellDescription(scope, elCell, oRecord, oColumn, oData);
            }
         };
      },

      /**
       * Returns actions custom datacell formatter
       *
       * @method fnRenderCellActions
       */
      fnRenderCellActions: function DL_fnRenderCellActions()
      {
         var scope = this;

         /**
          * Actions custom datacell formatter
          *
          * @method renderCellActions
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function DL_renderCellActions(elCell, oRecord, oColumn, oData)
         {
            if (typeof scope.viewRenderers[scope.options.viewRendererName] === "object")
            {
               scope.viewRenderers[scope.options.viewRendererName].renderCellActions(scope, elCell, oRecord, oColumn, oData);
            }
         };
      },

      /**
       * Renders a node property value as best as it can using the rules described below:
       *
       * String - As is
       * boolean - As is
       * Number -  As is
       * Object - Will be inspected for the following attributes:
       * - iso8601: Will format and render date using systems default format
       * - displayName & username: Will render the displayName as a user profile link
       * - displayName: Will render the displayName
       * - title: Will render the title
       * - name: Will render the name
       * Array - Number of items in the array
       *
       * Note! This method should be used as a last resort when no specific property renderers have been registered
       * for the property.
       *
       * @method renderProperty
       * @param property
       * return {String} html escaped string
       */
      renderProperty: function(property)
      {
         if (YAHOO.lang.isString(property))
         {
            return $html(property);
         }
         else if (YAHOO.lang.isArray(property))
         {
            return property.length;
         }
         else if (YAHOO.lang.isBoolean(property))
         {
            return property;
         }
         else if (YAHOO.lang.isNumber(property))
         {
            return property;
         }
         else if (YAHOO.lang.isObject(property))
         {
            if (property.hasOwnProperty("iso8601"))
            {
               return $date(property.iso8601);
            }
            else if (property.hasOwnProperty("userName") && property.hasOwnProperty("displayName"))
            {
               return Alfresco.util.userProfileLink(property.userName, property.displayName);
            }
            else if (property.hasOwnProperty("displayName"))
            {
               return $html(property.displayName || "");
            }
            else if (property.hasOwnProperty("title"))
            {
               return $html(property.title || "");
            }
            else if (property.hasOwnProperty("name"))
            {
               return $html(property.name || "");
            }
         }
         return "";
      },

      /**
       * Register a metadata renderer via Bubbling event
       *
       * @method onRegisterRenderer
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (property name, rendering function)
       */
      onRegisterRenderer: function DL_onRegisterRenderer(layer, args)
      {
         var obj = args[1];
         if (obj && $isValueSet(obj.propertyName) && $isValueSet(obj.renderer))
         {
            this.registerRenderer(obj.propertyName, obj.renderer);
         }
         else
         {
            Alfresco.logger.error("DL_onRegisterRenderer: Custom renderer registion invalid: " + obj);
         }
      },

      /**
       * Register a view renderer via Bubbling event
       *
       * @method onRegisterViewRenderer
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (property name, rendering function)
       */
      onRegisterViewRenderer: function DL_onRegisterViewRenderer(layer, args)
      {
         var obj = args[1];
         if (obj && $isValueSet(obj.renderer))
         {
            this.registerViewRenderer(obj.renderer);
         }
         else
         {
            Alfresco.logger.error("DL_onRegisterViewRenderer: Custom view renderer registion invalid: " + obj);
         }
      },

      /**
       * Register a metadata renderer
       *
       * @method registerRenderer
       * @param propertyName {string} Property name to attach this renderer to
       * @param renderer {function} Rendering function
       * @return {boolean} Success status of registration
       */
      registerRenderer: function DL_registerRenderer(propertyName, renderer)
      {
         if ($isValueSet(propertyName) && $isValueSet(renderer))
         {
            this.renderers[propertyName] = renderer;
            return true;
         }
         return false;
      },

      /**
       * Register a view renderer and call its setupRenderer method
       *
       * @method registerViewRenderer
       * @param renderer {object} Rendering object
       * @return {boolean} Success status of registration
       */
      registerViewRenderer: function DL_registerViewRenderer(renderer)
      {
         if ($isValueSet(renderer))
         {
            this.viewRenderers[renderer.name] = renderer;
            this.viewRenderers[renderer.name].setupRenderer(this);
            return true;
         }
         return false;
      },

      /**
       * Configure standard metadata renderers
       *
       * @method _setupMetadataRenderers
       */
      _setupMetadataRenderers: function DL__setupMetadataRenderers()
      {
         this.registerRenderer("i18nLabel", function(record, label)
         {
            // Just return the label, removing the trailing ": "
            return label.replace(/:\s$/, "");
         });

         /**
          * Locked / Working Copy banner
          */
         this.registerRenderer("lockBanner", function(record, label)
         {
            var properties = record.jsNode.properties,
               bannerUser = properties.lockOwner || properties.workingCopyOwner,
               bannerLink = Alfresco.DocumentList.generateUserLink(this, bannerUser),
               isContainer = record.jsNode.isContainer,
               nodeTypePrefix = "details.banner.",
               html = "";

            if (isContainer)
            {
               nodeTypePrefix += "folder."
            }

            /* Working Copy handling */
            if (record.workingCopy && bannerUser.userName === Alfresco.constants.USERNAME)
            {
               html = this.msg(nodeTypePrefix + (record.workingCopy.isWorkingCopy ? "editing" : "lock-owner"));
            }
            else
            {
               if (bannerUser.userName === Alfresco.constants.USERNAME)
               {
                  html = this.msg(nodeTypePrefix + "lock-owner");
               }
               else
               {
                  html = this.msg(nodeTypePrefix + "locked", bannerLink);
               }
            }
            return html;
         });

         this.registerRenderer("syncFailed", function(record, label)
         {
            return '<span class="banner-more-info-link">' + this.msg("details.banner.more-info") + '</span>' + this.msg("details.banner.sync-failed");
         });

         this.registerRenderer("syncTransientError", function(record, label)
         {
            return '<span class="banner-more-info-link">' + this.msg("details.banner.more-info") + '</span>' + this.msg("details.banner.sync-transient-error");
         });

         /**
          * Date
          */
         this.registerRenderer("date", function(record, label)
         {
            var jsNode = record.jsNode,
               properties = jsNode.properties,
               html = "";

            var dateI18N = "modified", dateProperty = properties.modified.iso8601;
            if (record.workingCopy && record.workingCopy.isWorkingCopy)
            {
               dateI18N = "editing-started";
            }
            else if (dateProperty === properties.created.iso8601)
            {
               dateI18N = "created";
            }

            html = '<span class="item">' + label + this.msg("details." + dateI18N + "-by", $relTime(dateProperty), Alfresco.DocumentList.generateUserLink(this, properties.modifier)) + '</span>';

            return html;
         });

         /**
          * File size
          */
         this.registerRenderer("size", function(record, label)
         {
            var jsNode = record.jsNode,
               properties = jsNode.properties,
               html = "";

            if (!jsNode.isContainer && !jsNode.isLink)
            {
               html += '<span class="item">' + label + Alfresco.util.formatFileSize(jsNode.size) + '</span>';
            }

            return html;
         });

         /**
          * Version
          */
         this.registerRenderer("name", function(record, label)
         {
            var jsNode = record.jsNode,
               properties = jsNode.properties,
               html = "";

            // Insitu editing for title (filename)
            if (jsNode.hasPermission("Write") && !jsNode.isLocked && !jsNode.hasAspect("cm:workingcopy"))
            {
               this.insituEditors.push(
               {
                  // The "___" looks a bit weird, but it's just to avoid clashes with the another instance of the
                  // a filename insitu editor that is guaranteed to be on the page this renderer is used in a
                  // table view (which is quite likely)... Note that the DOM fragment created below also uses it
                  // they need to match...
                  context: "___" + record._filenameId,
                  params:
                  {
                     type: "textBox",
                     nodeRef: jsNode.nodeRef.toString(),
                     name: "prop_cm_name",
                     value: record.fileName,
                     fnSelect: function fnSelect(elInput, value)
                     {
                        // If the file has an extension, omit it from the edit selection
                        var extnPos = value.lastIndexOf(Alfresco.util.getFileExtension(value)) - 1;
                        if (extnPos > 0)
                        {
                           Alfresco.util.selectText(elInput, 0, extnPos);
                        }
                        else
                        {
                           elInput.select();
                        }
                     },
                     validations: [
                     {
                        type: Alfresco.forms.validation.length,
                        args: { min: 1, max: 255, crop: true },
                        when: "keyup",
                        message: this.msg("validation-hint.length.min.max", 1, 255)
                     },
                     {
                        type: Alfresco.forms.validation.nodeName,
                        when: "keyup",
                        message: this.msg("validation-hint.nodeName")
                     }],
                     title: this.msg("tip.insitu-rename"),
                     errorMessage: this.msg("message.insitu-edit.name.failure")
                  },
                  callback:
                  {
                     fn: this._insituCallback,
                     scope: this,
                     obj: record
                  }
               });
            }

            // The "___" is explained in a comment further up... don't change it though.
            html += '<span id="' + "___" + record._filenameId + '">' + Alfresco.DocumentList.generateFileFolderLinkMarkup(this, record);
            html += $html(record.displayName) + '</a></span>';
            return html;
         });


         /**
          * Version
          */
         this.registerRenderer("version", function(record, label)
         {
            var jsNode = record.jsNode,
               properties = jsNode.properties,
               html = "";

            if (!jsNode.isContainer && !jsNode.isLink)
            {
               html += '<span class="item">' + label + record.version + '</span>';
            }

            return html;
         });

         /**
          * Description
          */
         this.registerRenderer("description", function(record, label)
         {
            var jsNode = record.jsNode,
               properties = jsNode.properties,
               id = Alfresco.util.generateDomId(),
               html = '<span id="' + id + '" class="faded">' + label + this.msg("details.description.none") + '</span>';

            // Description non-blank?
            if (properties.description && properties.description !== "")
            {
               html = '<span id="' + id + '" class="item">' + label + $links($html(properties.description)) + '</span>';
            }

            return html;
         });

         /**
          * File mimetype
          */
         this.registerRenderer("mimetype", function(record, label)
         {
            var jsNode = record.jsNode,
               html = "",
               mimetypeDisplayName = "";

            if (!jsNode.isContainer && !jsNode.isLink)
            {
               if(typeof jsNode.mimetypeDisplayName !== "undefined")
               {
                  mimetypeDisplayName = jsNode.mimetypeDisplayName;
               }
               else
               {
                  mimetypeDisplayName = this.msg("label.mimetype.unknown");
               }
               html += '<span class="item">' + label + mimetypeDisplayName + '</span>';
            }

            return html;
         });

         /**
          * Tags
          */
         this.registerRenderer("tags", function(record, label)
         {
            var jsNode = record.jsNode,
               properties = jsNode.properties,
               id = Alfresco.util.generateDomId(),
               html = "";

            var tags = jsNode.tags, tag;
            if (jsNode.hasAspect("cm:taggable") && tags.length > 0)
            {
               for (var i = 0, j = tags.length; i < j; i++)
               {
                  tag = $html(tags[i]);
                  html += '<span class="tag" style="display: inline-block"><a href="#" class="tag-link" rel="' + tag + '">' + tag + '</a></span>';
               }
            }
            else
            {
               html += '<span class="faded">' + label + this.msg("details.tags.none") + '</span>';
            }

            if (jsNode.hasPermission("Write") && !jsNode.isLocked)
            {
               // Add the tags insitu editor
               this.insituEditors.push(
               {
                  context: id,
                  params:
                  {
                     type: "tagEditor",
                     nodeRef: jsNode.nodeRef.toString(),
                     name: "prop_cm_taggable",
                     value: record.node.properties["cm:taggable"],
                     validations: [
                     {
                        type: Alfresco.forms.validation.nodeName,
                        when: "keyup",
                        message: this.msg("validation-hint.nodeName")
                     },
                     {
                        type: Alfresco.forms.validation.length,
                        args: { min: 1, max: 255, crop: true, ignoreEmpty: true },
                        when: "keyup",
                        message: this.msg("validation-hint.length.min.max", 1, 255)
                     }],
                     title: this.msg("tip.insitu-tag"),
                     errorMessage: this.msg("message.insitu-edit.tag.failure")
                  },
                  callback:
                  {
                     fn: this._insituCallback,
                     scope: this,
                     obj: record
                  }
               });
            }

            return '<span id="' + id + '" class="item">' + label + html + '</span>';
         });

         /**
          * Categories
          */
         this.registerRenderer("categories", function(record, label)
         {
            var jsNode = record.jsNode,
               properties = jsNode.properties,
               html = "";

            if (jsNode.hasAspect("cm:generalclassifiable"))
            {
               var categories = jsNode.categories, category;
               html += '<span class="category-item item">&nbsp;</span><span class="item">' + label;
               if (categories.length > 0)
               {
                  for (var i = 0, j = categories.length; i < j; i++)
                  {
                     category = categories[i];
                     html += '<span class="category"><a href="#" class="filter-change" rel="' + Alfresco.DocumentList.generateCategoryMarkup(category) + '">' + $html(category[0]) + '</a></span>' + (j - i > 1 ? ", " : "");
                  }
               }
               else
               {
                  html += '<span class="faded">' + label + this.msg("details.categories.none") + '</span>';
               }
               html += '</span>';
            }

            return html;
         });

         /**
          * Social
          */
         this.registerRenderer("social", function(record)
         {
            var jsNode = record.jsNode,
               html = "";

            /* Favourite / Likes / Comments */
            html += '<span class="item item-social">' + Alfresco.DocumentList.generateFavourite(this, record) + '</span>';
            html += '<span class="item item-social item-separator">' + Alfresco.DocumentList.generateLikes(this, record) + '</span>';
            if (jsNode.permissions.user.CreateChildren)
            {
               html += '<span class="item item-social item-separator">' + Alfresco.DocumentList.generateComments(this, record) + '</span>';
            }
            if (!record.node.isContainer && Alfresco.constants.QUICKSHARE_URL)
            {
               html += '<span class="item item-separator">' + Alfresco.DocumentList.generateQuickShare(this, record) + '</span>';
            }

            return html;
         });
      },

      /**
       * Configure standard view renderers
       *
       * @method _setupViewRenderers
       */
      _setupViewRenderers: function DL__setupViewRenderers()
      {
         for (var i=0; i<this.options.viewRenderers.length; i++)
         {
            // Get a function to use "new" with to instantiate a view renderer WITHOUT calling eval...
            var widget = this.options.viewRenderers[i].widget;
            if (widget != null)
            {
               var widgetTokens = widget.split(".");
               var instanceFunction = window;
               for (var j=0;j<widgetTokens.length;j++)
               {
                  instanceFunction = instanceFunction[widgetTokens[j]];
               }
               if (typeof instanceFunction === "function")
               {
                  this.registerViewRenderer(new instanceFunction(this.options.viewRenderers[i].id, this, this.options.viewRenderers[i].jsonConfig));
               }
            }
         }
         // TODO: Gallery columns isn't handled
         YAHOO.Bubbling.fire("postSetupViewRenderers",
         {
             scope: this,
             eventGroup: this.id
         });
      },

      /**
       * History Manager set-up and event registration
       *
       * @method _setupHistoryManagers
       */
      _setupHistoryManagers: function DL__setupHistoryManagers()
      {
         /**
          * YUI History - filter
          */
         var bookmarkedFilter = YAHOO.util.History.getBookmarkedState("filter");
         bookmarkedFilter = bookmarkedFilter || "path|/";

         try
         {
            while (bookmarkedFilter !== (bookmarkedFilter = decodeURIComponent(bookmarkedFilter.replace(/%25/g, "%2525")))){}
         }
         catch (e)
         {
            // Catch "malformed URI sequence" exception
         }

         var fnDecodeBookmarkedFilter = function DL_fnDecodeBookmarkedFilter(strFilter)
         {
            var filters = strFilter.split("|"),
               filterObj =
               {
                  filterId: window.unescape(filters[0] || "")
               };

            if (typeof filters[1] !== "undefined")
            {
               filterObj.filterData = window.unescape(filters[1]);
            }
            if (typeof filters[2] !== "undefined")
            {
               filterObj.filterDisplay = window.unescape(filters[2]);
            }
            filterObj.filterOwner = Alfresco.util.FilterManager.getOwner(filterObj.filterId);
            return filterObj;
         };

         this.options.initialFilter = fnDecodeBookmarkedFilter(bookmarkedFilter);

         // Register History Manager filter update callback
         YAHOO.util.History.register("filter", bookmarkedFilter, function DL_onHistoryManagerFilterChanged(newFilter)
         {
            Alfresco.logger.debug("HistoryManager: filter changed:" + newFilter);
            // MNT-9702 Browser back button doesn't work properly in Document Library (possible regression)
            var bookmarkedPage = YAHOO.util.History.getBookmarkedState("page") || "1";
            while (bookmarkedPage !== (bookmarkedPage = decodeURIComponent(bookmarkedPage))){}
            var page = parseInt(bookmarkedPage || this.options.initialPage, 10);
            if (this.currentPage != page) 
            {
               this.currentPage = page;
            }

            this._updateDocList.call(this,
            {
               filter: fnDecodeBookmarkedFilter(newFilter),
               page: this.currentPage
            });
         }, null, this);


         /**
          * YUI History - page
          */
         var handlePagination = function DL_handlePagination(state, me)
         {
            me.widgets.paginator.setState(state);
            YAHOO.util.History.navigate("page", String(state.page));
         };

         if (this.options.usePagination)
         {
            var bookmarkedPage = YAHOO.util.History.getBookmarkedState("page") || "1";
            while (bookmarkedPage !== (bookmarkedPage = decodeURIComponent(bookmarkedPage))){}
            this.currentPage = parseInt(bookmarkedPage || this.options.initialPage, 10);

            // Register History Manager page update callback
            YAHOO.util.History.register("page", bookmarkedPage, function DL_onHistoryManagerPageChanged(newPage)
            {
               Alfresco.logger.debug("HistoryManager: page changed:" + newPage);
               // Update the DocList
               if (this.currentPage != newPage)
               {
                  this._updateDocList.call(this,
                  {
                     page: newPage
                  });
               }
               else
               {
                  Alfresco.logger.debug("...page changed event ignored.");
               }
            }, null, this);

            // YUI Paginator definition
            this.widgets.paginator = new YAHOO.widget.Paginator(
            {
               containers: [this.id + "-paginator", this.id + "-paginatorBottom"],
               rowsPerPage: this.options.pageSize,
               initialPage: this.currentPage,
               template: this.msg("pagination.template"),
               pageReportTemplate: this.msg("pagination.template.page-report"),
               previousPageLinkLabel: this.msg("pagination.previousPageLinkLabel"),
               nextPageLinkLabel: this.msg("pagination.nextPageLinkLabel")
            });

            this.widgets.paginator.subscribe("changeRequest", handlePagination, this);

            // Display the bottom paginator bar
            Dom.setStyle(this.id + "-doclistBarBottom", "display", "block");
         }
      },

      /**
       * DataSource set-up and event registration
       *
       * @method _setupDataSource
       * @protected
       */
      _setupDataSource: function DL__setupDataSource()
      {
         var me = this;

         // DataSource definition
         this.widgets.dataSource = new YAHOO.util.DataSource(this.dataSourceUrl,
         {
            responseType: YAHOO.util.DataSource.TYPE_JSON,
            responseSchema:
            {
               resultsList: "items",
               metaFields:
               {
                  paginationRecordOffset: "startIndex",
                  totalRecords: "totalRecords",
                  totalRecordsUpper : "totalRecordsUpper" // if null then totalRecords is accurate else totalRecords is lower estimate (if -1 upper estimate is unknown)
               }
            }
         });

         // Intercept data returned from data webscript to extract custom metadata
         this.widgets.dataSource.doBeforeCallback = function DL_doBeforeCallback(oRequest, oFullResponse, oParsedResponse)
         {
            me.doclistMetadata = oFullResponse.metadata;

            // Fire event with parent metadata
            YAHOO.Bubbling.fire("doclistMetadata",
            {
               metadata: me.doclistMetadata
            });

            // Check for parent node - won't be one for multi-parent queries (e.g. tags)
            var permissions = null;
            if (me.doclistMetadata.parent)
            {
               permissions = me.doclistMetadata.parent.permissions;
               if (permissions && permissions.user)
               {
                  // Container userAccess event
                  YAHOO.Bubbling.fire("userAccess",
                  {
                     userAccess: permissions.user
                  });
               }
            }

            if (typeof me.viewRenderers[me.options.viewRendererName] === "object")
            {
               me.viewRenderers[me.options.viewRendererName].renderEmptyDataSourceHtml(me, permissions);
            }

            return oParsedResponse;
         };
      },

      /**
       * DataTable set-up and event registration
       *
       * @method _setupDataTable
       * @protected
       */
      _setupDataTable: function DL__setupDataTable()
      {
         var me = this;

         // DataTable column defintions
         var fpanel = Dom.get("alf-filters"),
             offset = (fpanel ? parseInt(fpanel.style.width, 10) : 160) + 390;
         var columnDefinitions =
         [
            { key: "nodeRef", label: "Select", sortable: false, formatter: this.fnRenderCellSelected(), width: 16 },
            { key: "status", label: "Status", sortable: false, formatter: this.fnRenderCellStatus(), width: 16 },
            { key: "thumbnail", label: "Preview", sortable: false, formatter: this.fnRenderCellThumbnail(), width: 100 },
            { key: "fileName", label: "Description", sortable: false, formatter: this.fnRenderCellDescription(), width: (Dom.getViewportWidth() - offset) },
            { key: "actions", label: "Actions", sortable: false, formatter: this.fnRenderCellActions(), width: 200 }
         ];

         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-documents", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: this.options.usePagination ? 16 : Alfresco.util.RENDERLOOPSIZE,
            initialLoad: false,
            dynamicData: true,
            MSG_EMPTY: this.msg("message.loading")
         });

         // Update the empty data section of the table so that the information always spreads out across
         // as many columns as could be likely (there is unlikely to ever be more than 100 columns in a table!)
         // This is a bit hacky but is the most straightforward way of addressing the variability of columns
         // introduced by configurable table view instances...
         var messageEl = Dom.getElementsByClassName("yui-dt-empty");
         if (messageEl.length > 0)
         {
            messageEl[0].colSpan=100;
         }

         // Update totalRecords on the fly with value from server
         this.widgets.dataTable.handleDataReturnPayload = function DL_handleDataReturnPayload(oRequest, oResponse, oPayload)
         {
            me.totalRecords = oResponse.meta.totalRecords;
            me.totalRecordsUpper = oResponse.meta.totalRecordsUpper;
            return oResponse.meta;
         };

         // Custom error messages
         this._setDefaultDataTableErrors(this.widgets.dataTable);

         // Hook tableMsgShowEvent to clear out fixed-pixel width on <table> element (breaks resizer)
         this.widgets.dataTable.subscribe("tableMsgShowEvent", function(oArgs)
         {
            // NOTE: Scope needs to be DataTable
            this._elMsgTbody.parentNode.style.width = "";

            // Check to see whether or not the message is the HTML5 drag and drop instructions. This
            // will be set if there are no documents or folders in the displayed location. If the HTML5
            // drag and drop instructions are shown then we want to attach events to the "upload" and
            // "share" images...
            if (oArgs.html && oArgs.html.indexOf("docListInstructions") !== -1)
            {
               var toolbar = Alfresco.util.ComponentManager.findFirst("Alfresco.DocListToolbar");
               if (toolbar !== null)
               {
                  // This code is reliant upon the template in documentlist.lib.ftl not being changed.
                  // It allows us to attach events to the image and anchor, but not the remaining text.
                  var standardUpload = Dom.get(me.id + "-standard-upload-link-template-instance");
                  if (standardUpload !== null)
                  {
                     var standardUploadChildren = Dom.getChildren(standardUpload);
                     Event.addListener(standardUploadChildren[0], "click", toolbar.onFileUpload, toolbar, true);
                     Event.addListener(Dom.getFirstChild(standardUploadChildren[1]), "click", toolbar.onFileUpload, toolbar, true);
                  }
                  var newFolder = Dom.get(me.id + "-new-folder-link-template-instance");
                  if (newFolder !== null)
                  {
                     var newFolderChildren = Dom.getChildren(newFolder);
                     Event.addListener(newFolderChildren[0], "click", toolbar.onNewFolder, toolbar, true);
                     Event.addListener(Dom.getFirstChild(newFolderChildren[1]), "click", toolbar.onNewFolder, toolbar, true);
                  }
               }

               // See if the document list contains instructions on showing hidden sub-folders and if
               // it does, attach the appropriate events to the relevant nodes...
               var showFolders = Dom.get(me.id + "-show-folders-template-instance");
               if (showFolders !== null)
               {
                  var showFoldersFunc = function fnShowFolders()
                  {
                     // Find new menu item, see MNT-10195
                     var menuItemElement = Dom.getElementsByClassName("showFolders")[0];

                     me.options.showFolders = true;
                     menuItemElement.innerHTML = this.msg("button.folders.hide");
                     Dom.replaceClass(menuItemElement, "showFolders", "hideFolders");
                     me.services.preferences.set(PREF_SHOW_FOLDERS, me.options.showFolders);
                     YAHOO.Bubbling.fire("metadataRefresh");
                  };
                  var showFoldersChildren = Dom.getChildren(showFolders);
                  Event.addListener(showFoldersChildren[0], "click", showFoldersFunc, me, true);
                  Event.addListener(showFoldersChildren[1], "click", showFoldersFunc, me, true);
               }
            }
         });

         this.widgets.dataTable.sortColumn = function(oColumn, sDir)
         {
            // Get the current sort information...
            var sSortDir = this.getColumnSortDir(oColumn);
            var oSortedBy = this.get("sortedBy") || {};
            var bSorted = (oSortedBy.key === oColumn.key) ? true : false;

            var oState = this.getState();
            oState.sortedBy = {
                key: oColumn.key,
                dir: sSortDir
            };
            this.set('sortedBy', oState.sortedBy);

            // Determine the direction in terms that the documentlist widget understands...
            var direction = "ascending";
            if (sSortDir != "yui-dt-asc")
            {
               direction = "descending";
            }

            // Call a function to perform the sort...
            me.onSortFieldEvent(oColumn.field, direction);
         };

         // Override abstract function within DataTable to set custom error message
         this.widgets.dataTable.doBeforeLoadData = function DL_doBeforeLoadData(sRequest, oResponse, oPayload)
         {
            if (oResponse.error)
            {
               try
               {
                  var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                  me.widgets.dataTable.set("MSG_ERROR", response.message);
               }
               catch(e)
               {
                  me._setDefaultDataTableErrors(me.widgets.dataTable);
               }
            }
            else if (oResponse.results.length === 0)
            {
               // We don't get an renderEvent for an empty recordSet, but we'd like one anyway
               Dom.setStyle(me.widgets.dataTable._elThead, "display", "none");
               this.fireEvent("renderEvent",
               {
                  type: "renderEvent"
               });
            }
            else
            {
               // Add an Alfresco.util.Node instance to each result
               for (var i = 0, ii = oResponse.results.length; i < ii; i++)
               {
                  oResponse.results[i].jsNode = new Alfresco.util.Node(oResponse.results[i].node);
               }
            }

            // Must return true to have the "Loading..." message replaced by the error message
            return true;
         };

         // File checked handler
         this.widgets.dataTable.subscribe("checkboxClickEvent", function DL_checkboxClickEvent(e)
         {
            var id = e.target.value;
            this.selectedFiles[id] = e.target.checked;
            YAHOO.Bubbling.fire("selectedFilesChanged");
         }, this, true);

         // Rendering complete event handler
         this.widgets.dataTable.subscribe("renderEvent", function DL_renderEvent()
         {
            Alfresco.logger.debug("DataTable renderEvent");

            // IE6 fix for long filename rendering issue
            if (0 < YAHOO.env.ua.ie && YAHOO.env.ua.ie < 7)
            {
               var ie6fix = this.widgets.dataTable.getTableEl().parentNode;
               //noinspection SillyAssignmentJS
               ie6fix.className = ie6fix.className;
            }

            // Update the paginator if it's been created
            if (this.widgets.paginator)
            {
               Alfresco.logger.debug("Setting paginator state: page=" + this.currentPage + ", totalRecords=" + this.totalRecords);

               this.widgets.paginator.setState(
               {
                  page: this.currentPage,
                  totalRecords: this.totalRecords
               });

               if (this.totalRecordsUpper)
               {
                  this.widgets.paginator.set("pageReportTemplate", this.msg("pagination.template.page-report.more"));
               }
               else
               {
                  this.widgets.paginator.set("pageReportTemplate", this.msg("pagination.template.page-report"));
               }

               this.widgets.paginator.render();
            }

            // Need to highlight a file now the data is available?
            if (this.options.highlightFile)
            {
               YAHOO.Bubbling.fire("highlightFile",
               {
                  fileName: window.unescape(this.options.highlightFile)
               });
            }
            else if (this.listUpdated)
            {
               // Scroll up (only) to the top of the documents
               var yPos = Dom.getY(this.id + "-documents"),
                  yScroll = YAHOO.env.ua.ie > 0 ? ((document.compatMode && document.compatMode !== "BackCompat") ? document.documentElement : document.body).scrollTop : window.scrollY;

               if (yScroll > yPos)
               {
                  window.scrollTo(0, yPos);
               }
            }

            // Deferred functions specified?
            var i, j;
            for (i = 0, j = this.afterDocListUpdate.length; i < j; i++)
            {
               this.afterDocListUpdate[i].call(this);
            }
            this.afterDocListUpdate = [];

            // Register preview tooltips
            if (typeof this.widgets.previewTooltip !== "undefined")
            {
               this.widgets.previewTooltip.cfg.setProperty("context", this.previewTooltips);
            }

            // Register insitu editors
            var iEd;
            for (i = 0, j = this.insituEditors.length; i < j; i++)
            {
               iEd = this.insituEditors[i];
               Alfresco.util.createInsituEditor(iEd.context, iEd.params, iEd.callback);
            }

            this.widgets.dataTable.set("renderLoopSize", this.options.usePagination ? 16 : Alfresco.util.RENDERLOOPSIZE);
         }, this, true);

         // Enable row highlighting
         this.widgets.dataTable.subscribe("rowMouseoverEvent", this.onEventHighlightRow, this, true);
         this.widgets.dataTable.subscribe("rowMouseoutEvent", this.onEventUnhighlightRow, this, true);

         // Enable actions on indicators
         Event.delegate(this.id, "click", function DL__setupDataTable_onIndicatorAction(event, target)
         {
            var action = target.getAttribute("data-action"),
               rec = me.widgets.dataTable.getRecord(target),
               record;
			 
            // MNT-10630 Sync Info Link is not working on Gallery View of document library page.			 
            if (rec == null)
            {
               var id = me.currentActionsMenu.id;
               var records = me.widgets.dataTable.getRecordSet( )._records;
               for (i = 0; i < records.length; i++)
               {
		  if (id.indexOf(records[i]._sId) > 0)
		  {
		     rec = me.widgets.dataTable.getRecord(records[i]._sId);
		     target =  rec._sId;
		  }
               } 				
            }	 
			
            if (rec != null)
            {
               record = rec.getData();
            }

            // Look for method in Alfresco.DocumentList or Global scopes & call it with the record as the only param
            if (YAHOO.lang.isFunction(me[action]))
            {
               me[action].call(me, record, target);
            } else if (YAHOO.lang.isFunction(window[action]))
            {
               window[action](record, target);
            }
         }, "a.indicator-action");

         // Feature detection for drag and drop support (by not attempting to attach the drag events
         // to anything we can prevent attempted uploads happening - this is particularly important
         // with IE8 which would otherwise render the highlights, but not process the upload).
         if (this.dragAndDropEnabled)
         {
            // Disable drop for the document body (we're then going to do a specific override on the DocumentList nodes)
            Event.addListener(document.body, "dragenter", this._swallowDragEnter, this, true);
            Event.addListener(document.body, "dragover", this._swallowDragOver, this, true);
            Event.addListener(document.body, "drop", this._swallowDragDrop, this, true);
            Event.addListener(document.body, "dragstart", this._swallowDragStart, this, true);
         }
      },

      /**
       * Removes HTML5 drag and drop listeners from the document list.
       *
       * @method _removeDragAndDrop
       */
      _removeDragAndDrop: function DL__removeDragAndDrop()
      {
         if (this.dragAndDropEnabled)
         {
            // Make the entire DocumentList available for dropping files for uploading onto.
            try
            {
               // Add listeners to the HTML5 drag and drop events fired from the entire doc list
               var doclist = Dom.get(this.id + "-dl-body");
               Event.removeListener(doclist, "dragenter");
               Event.removeListener(doclist, "dragover");
               Event.removeListener(doclist, "dragleave");
               Event.removeListener(doclist, "drop");
            }
            catch(exception)
            {
               Alfresco.logger.error("_removeDragAndDrop: The following exception occurred: ", exception);
            }
         }
      },

      /**
       * Adds HTML5 drag and drop listeners to the document list.
       *
       * @method _addDragAndDrop
       */
      _addDragAndDrop: function DL__addDragAndDrop()
      {
         if (this._userCanUpload && this.dragAndDropEnabled)
         {
            // Make the entire DocumentList available for dropping files for uploading onto.
            try
            {
               // Add listeners to the HTML5 drag and drop events fired from the entire doc list
               var doclist = Dom.get(this.id + "-dl-body");
               Event.addListener(doclist, "dragenter", this.onDocumentListDragEnter, this, true);
               Event.addListener(doclist, "dragover", this.onDocumentListDragOver, this, true);
               Event.addListener(doclist, "dragleave", this.onDocumentListDragLeave, this, true);
               Event.addListener(doclist, "drop", this.onDocumentListDrop, this, true);
            }
            catch(exception)
            {
               Alfresco.logger.error("_addDragAndDrop: The following exception occurred: ", exception);
            }
         }
      },

      /**
       * Fired when an object starts getting dragged. The event is swallowed because we only want to
       * allow drag and drop events that begin outside the browser window (e.g. for files). This prevents
       * users attempting to drag and drop the document and folder images as if they could re-arrange
       * the document lib structure.
       *
       * @param e {object} HTML5 drag and drop event
       * @method _swallowDragStart
       */
      _swallowDragStart: function DL__swallowDragStart(e)
      {
         e.stopPropagation();
         e.preventDefault();
      },

      /**
       * Fired when an object is dragged onto any node in the document body (unless the node has
       * been explicitly overridden to invoke another function). Swallows the event.
       *
       * @param e {object} HTML5 drag and drop event
       * @method _swallowDragEnter
       */
      _swallowDragEnter: function DL__swallowDragEnter(e)
      {
         e.stopPropagation();
         e.preventDefault();
      },

      /**
       * Fired when an object is dragged over any node in the document body (unless the node has
       * been explicitly overridden to invoke another function). Updates the drag behaviour to
       * indicate that drops are not allowed and then swallows the event.
       *
       * @param e {object} HTML5 drag and drop event
       * @method _swallowDragOver
       */
      _swallowDragOver: function DL__swallowDragOver(e)
      {
         e.dataTransfer.dropEffect = "none";
         e.stopPropagation();
         e.preventDefault();
      },

      /**
       * Fired when an object is dropped onto any node in the document body (unless the node has
       * been explicitly overridden to invoke another function). Swallows the event to prevent
       * default browser behaviour (i.e. attempting to open the file).
       *
       * @param e {object} HTML5 drag and drop event
       * @method _swallowDrop
       */
      _swallowDrop: function DL__swallowDrop(e)
      {
         e.stopPropagation();
         e.preventDefault();
      },

      /**
       * Fired when an object is dragged into the DocumentList DOM element and then again when dragged into a Folder
       * icon image DOM element.
       *
       * @param e {object} HTML5 drag and drop event
       * @method onDocumentListDragEnter
       */
      onDocumentListDragEnter: function DL_onDocumentListDragEnter(e)
      {
         // Providing that the drop target a <TR> (or a child node of a <TR>) in the DocumentList data table then a record
         // will be returned from this call. If nothing is returned then we cannot proceed with the file upload operation.
         var el = e.target,
            oRecord = this.widgets.dataTable.getRecord(el),
            column = this.widgets.dataTable.getColumn(el),
            containerEl = this.widgets.dataTable.getContainerEl(),
            node;

         if (oRecord && column && (el.tagName == "IMG" || el.className == "droppable") && column.getKey() == "thumbnail")
         {
            node = oRecord.getData("jsNode");
            if (node && node.isContainer)
            {
               this.dragFolderHighlight = Dom.getAncestorByClassName(el, "folder");
               Dom.addClass(this.dragFolderHighlight, "dndFolderHighlight");
               Dom.removeClass(containerEl, "dndDocListHighlight");
            }
         }
         else
         {
            // Firefox is a bit buggy with it's enter/leave event matching
            this.dragEventRefCount = Math.min(++this.dragEventRefCount, 2);
            Dom.addClass(containerEl, "dndDocListHighlight");
            if (this.dragFolderHighlight)
            {
               Dom.removeClass(this.dragFolderHighlight, "dndFolderHighlight");
               this.dragFolderHighlight = null;
            }
         }

         e.stopPropagation();
         e.preventDefault();
      },

      /**
       * Fired when an object is dragged into the DocumentList DOM element and then again when dragged into a Folder
       * icon image DOM element.
       *
       * @param e {object} HTML5 drag and drop event
       * @method onDocumentListDragOver
       */
      onDocumentListDragOver: function DL_onDocumentListDragOver(e)
      {
         // Firefox 3.6 set effectAllowed = "move" for files, however the "copy" effect is more accurate for uploads
         e.dataTransfer.dropEffect = Math.floor(YAHOO.env.ua.gecko) === 1 ? "move" : "copy";
         e.stopPropagation();
         e.preventDefault();
      },

      /**
       * Fired when an object is dragged out of the DocumentList DOM element or the Folder icon image DOM element.
       *
       * @param e {object} HTML5 drag and drop event
       * @method onDocumentListDragLeave
       */
      onDocumentListDragLeave: function DL_onDocumentListDragLeave(e)
      {
         // Providing that the drop target a <TR> (or a child node of a <TR>) in the DocumentList data table then a record
         // will be returned from this call. If nothing is returned then we cannot proceed with the file upload operation.
         var el = e.target,
            oRecord = this.widgets.dataTable.getRecord(el),
            oColumn = this.widgets.dataTable.getColumn(el),
            record = oRecord ? oRecord.getData() : null;

         if (oRecord && oColumn && (el.tagName == "IMG" || el.className == "droppable") && record.node.isContainer && oColumn.getKey() == "thumbnail")
         {
            // Ignore the folder dragLeave event
         }
         else
         {
            if (--this.dragEventRefCount === 0)
            {
               Dom.removeClass(this.widgets.dataTable.getContainerEl(), "dndDocListHighlight");
            }
         }

         e.stopPropagation();
         e.preventDefault();
      },

      /**
       * Fired when an object is dropped onto the DocumentList DOM element.
       * Checks that files are present for upload, determines the target (either the current document list or
       * a specific folder rendered in the document list and then calls on the DNDUpload singleton component
       * to perform the upload.
       *
       * @param e {object} HTML5 drag and drop event
       * @method onDocumentListDrop
       */
      onDocumentListDrop: function DL_onDocumentListDrop(e)
      {
         try
         {
            // Only perform a file upload if the user has *actually* dropped some files!
            if (e.dataTransfer.files !== undefined && e.dataTransfer.files !== null && e.dataTransfer.files.length > 0)
            {
               // We need to get the upload progress dialog widget so that we can display it.
               // The function called has been added to file-upload.js and ensures the dialog is a singleton.
               var progressDialog = Alfresco.getDNDUploadProgressInstance();

               var continueWithUpload = false;

               // Check that at least one file with some data has been dropped...
               var zeroByteFiles = "", i, j;

               j = e.dataTransfer.files.length;
               for (i = 0; i < j; i++)
               {
                  if (e.dataTransfer.files[i].size > 0)
                  {
                     continueWithUpload = true;
                  }
                  else
                  {
                     zeroByteFiles += '"' + e.dataTransfer.files[i].fileName + '", ';
                  }
               }

               if (!continueWithUpload)
               {
                  zeroByteFiles = zeroByteFiles.substring(0, zeroByteFiles.lastIndexOf(", "));
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: progressDialog.msg("message.zeroByteFiles", zeroByteFiles)
                  });
               }

               // Perform some checks on based on the browser and selected files to ensure that we will
               // support the upload request.
               if (continueWithUpload && progressDialog.uploadMethod === progressDialog.INMEMORY_UPLOAD)
               {
                  // Add up the total size of all selected files to see if they exceed the maximum allowed.
                  // If the user has requested to upload too large a file or too many files in one operation
                  // then generate an error dialog and abort the upload...
                  var totalRequestedUploadSize = 0;

                  j = e.dataTransfer.files.length;
                  for (i = 0; i < j; i++)
                  {
                     totalRequestedUploadSize += e.dataTransfer.files[i].size;
                  }
                  if (totalRequestedUploadSize > progressDialog.getInMemoryLimit())
                  {
                     continueWithUpload = false;
                     Alfresco.util.PopupManager.displayPrompt(
                     {
                         text: progressDialog.msg("inmemory.uploadsize.exceeded", Alfresco.util.formatFileSize(progressDialog.getInMemoryLimit()))
                     });
                  }
               }

               // If all tests are passed...
               if (continueWithUpload)
               {
                  // Initialise the target directory as the current path represented by the current rendering of the DocumentList.
                  // If we determine that the user has actually dropped some files onto the a folder icon (which we're about to check
                  // for) then we'll change this value to be that of the folder targeted...
                  var directory = this.currentPath,
                     directoryName = directory.substring(directory.lastIndexOf("/") + 1),
                     destination = this.doclistMetadata.parent ? this.doclistMetadata.parent.nodeRef : null;

                  // Providing that the drop target a <TR> (or a child node of a <TR>) in the DocumentList data table then a record
                  // will be returned from this call. If nothing is returned then we cannot proceed with the file upload operation.
                  var oRecord = this.widgets.dataTable.getRecord(e.target);
                  if (oRecord !== null)
                  {
                     // Dropped onto a folder icon?
                     var oColumn = this.widgets.dataTable.getColumn(e.target),
                        record = oRecord.getData();

                     if (e.target.tagName == "IMG" || e.target.className == "droppable")
                     {
                        var location = record.location;
                        directoryName = location.file;
                        // Site mode
                        directory = $combine(location.path, location.file);
                        // Repository mode
                        destination = record.nodeRef;
                     }
                     // else: The file(s) were not not dropped onto a folder icon, so we will just upload to the current path
                  }
                  // else: If a record is not returned, then it means that we dropped into an empty folder.

                  // Remove all the highlighting
                  Dom.removeClass(this.widgets.dataTable.getTrEl(e.target), "dndFolderHighlight");
                  Dom.removeClass(this.widgets.dataTable.getContainerEl(), "dndDocListHighlight");

                  // Show uploader for multiple files
                  var multiUploadConfig =
                  {
                     files: e.dataTransfer.files,
                     uploadDirectoryName: directoryName,
                     filter: [],
                     mode: progressDialog.MODE_MULTI_UPLOAD,
                     thumbnails: "doclib",
                     onFileUploadComplete:
                     {
                        fn: this.onFileUploadComplete,
                        scope: this
                     }
                  };

                  // Extra parameters depending on current mode
                  if ($isValueSet(this.options.siteId))
                  {
                     multiUploadConfig.siteId = this.options.siteId;
                     multiUploadConfig.containerId = this.options.containerId;
                     multiUploadConfig.uploadDirectory = directory;
                  }
                  else
                  {
                     multiUploadConfig.destination = destination;
                  }

                  progressDialog.show(multiUploadConfig);
               }
            }
            else
            {
               Alfresco.logger.debug("DL_onDocumentListDrop: A drop event was detected, but no files were present for upload: ", e.dataTransfer);
            }
         }
         catch(exception)
         {
            Alfresco.logger.error("DL_onDocumentListDrop: The following error occurred when files were dropped onto the Document List: ", exception);
         }
         e.stopPropagation();
         e.preventDefault();
      },

      /**
       * Fired by YUI when History Manager is initialised and available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onHistoryManagerReady
       */
      onHistoryManagerReady: function DL_onHistoryManagerReady()
      {
         // Check whether to show upload dialog after initial load. Suppressed if HTML5 drag-and-drop is available.
         if (this.options.initialFilter.filterId === "path" && window.location.hash == "#upload")
         {
            window.location.hash = "";
            var fnAfterUpdate = function DL_onHistoryManagerReady_afterUpdate()
            {
               YAHOO.Bubbling.fire("showFileUploadDialog",
               {
                  tooltip: this.msg("dnd.upload.tooltip")
               });
            };
            this.afterDocListUpdate.push(fnAfterUpdate);
         }

         // Fire changeFilter event for first-time population
         Alfresco.logger.debug("DL_onHistoryManagerReady", "changeFilter =>", this.options.initialFilter);
         YAHOO.Bubbling.fire("changeFilter", YAHOO.lang.merge(
         {
            doclistFirstTimeNav: true
         }, this.options.initialFilter));

         // Finally show the component body here to prevent UI artifacts on YUI button decoration
         Dom.setStyle(this.id + "-dl-body", "visibility", "visible");
      },

      /**
       * Public functions
       *
       * Functions designed to be called form external sources
       */

      /**
       * Public function to get array of selected files
       *
       * @method getSelectedFiles
       * @return {Array} Currently selected files
       */
      getSelectedFiles: function DL_getSelectedFiles()
      {
         var files = [],
            oRecordSet = this.widgets.dataTable.getRecordSet(),
            oRecord, node, i, j;

         for (i = 0, j = oRecordSet.getLength(); i < j; i++)
         {
            oRecord = oRecordSet.getRecord(i);
            node = oRecord.getData("node");
            if (this.selectedFiles[node.nodeRef])
            {
               files.push(oRecord.getData());
            }
         }

         return files;
      },

      /**
       * Public function to select files by specified groups
       *
       * @method selectFiles
       * @param p_selectType {string} Can be one of the following:
       * <pre>
       * selectAll - all documents and folders
       * selectNone - deselect all
       * selectInvert - invert selection
       * selectDocuments - select all documents
       * selectFolders - select all folders
       * </pre>
       */
      selectFiles: function DL_selectFiles(p_selectType)
      {
         var containerElement;
         if (typeof this.viewRenderers[this.options.viewRendererName] === "object")
         {
            containerElement = Dom.get(this.id + this.viewRenderers[this.options.viewRendererName].parentElementIdSuffix);
         }
         var oRecordSet = this.widgets.dataTable.getRecordSet(),
            checks = YAHOO.util.Selector.query('input[type="checkbox"]', containerElement),
            len = checks.length,
            oRecord, record, i, fnCheck;

         switch (p_selectType)
         {
            case "selectAll":
               fnCheck = function chkSelectAll(record, isChecked)
               {
                  return true;
               };
               break;

            case "selectNone":
               fnCheck = function chkSelectNone(record, isChecked)
               {
                  return false;
               };
               break;

            case "selectInvert":
               fnCheck = function chkSelectInvert(record, isChecked)
               {
                  return !isChecked;
               };
               break;

            case "selectDocuments":
               fnCheck = function chkSelectDocuments(record, isChecked)
               {
                  return !(record.node.isContainer);
               };
               break;

            case "selectFolders":
               fnCheck = function chkSelectDocuments(record, isChecked)
               {
                  return record.node.isContainer;
               };
               break;

            default:
               fnCheck = function chkDefault(record, isChecked)
               {
                  return isChecked;
               };
         }

         for (i = 0; i < len; i++)
         {
            oRecord = oRecordSet.getRecord(i);
            record = oRecord.getData();
            this.selectedFiles[record.nodeRef] = checks[i].checked = fnCheck(record, checks[i].checked);
         }

         YAHOO.Bubbling.fire("selectedFilesChanged");
      },


      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * Sort direction button click handler
       *
       * @method onSortAscending
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onSortAscending: function DL_onSortAscending(e, p_obj)
      {
         // Clear sort information when the sort button is clicked (this shouldn't
         // be called when the table view sorts via column header click)...
         this.widgets.dataTable.set("sortedBy", null);

         this.options.sortAscending = !this.options.sortAscending;
         this.widgets.sortAscending.set("title", this.msg(this.options.sortAscending ? "button.sort.descending" : "button.sort.ascending"));
         if (this.options.sortAscending)
         {
            Dom.removeClass(this.widgets.sortAscending.get("element"), "sort-descending");
         }
         else
         {
            Dom.addClass(this.widgets.sortAscending.get("element"), "sort-descending");
         }
         this.services.preferences.set(PREF_SORT_ASCENDING, this.options.sortAscending);
         YAHOO.Bubbling.fire("metadataRefresh");
         if (e)
         {
            Event.preventDefault(e);
         }
      },

      /**
       * Sort direction event handler
       *
       * @method onSortEvent
       * @param direction {string} The direction to sort (if not "descending" will be "ascending")
       */
      onSortEvent: function DL_onSortEvent(direction)
      {
         this.options.sortAscending = (direction != "descending");
         this.services.preferences.set(PREF_SORT_ASCENDING, this.options.sortAscending);
         YAHOO.Bubbling.fire("metadataRefresh");
      },

      /**
       * Sort Field select button click handler
       *
       * @method onSortField
       * @param sType {string} Event type, e.g. "click"
       * @param aArgs {array} Arguments array, [0] = DomEvent, [1] = EventTarget
       * @param p_obj {object} Object passed back from subscribe method
       */
      onSortField: function DL_onSortField(sType, aArgs, p_obj)
      {
         // Clear sort information when the sort button is clicked (this shouldn't
         // be called when the table view sorts via column header click)...
         this.widgets.dataTable.set("sortedBy", null);

         var domEvent = aArgs[0],
            eventTarget = aArgs[1];

         if (eventTarget)
         {
            var me = this;
            this.options.sortField = eventTarget.value;
            this.widgets.sortField.set("label", eventTarget.cfg.getProperty("text") + " " + Alfresco.constants.MENU_ARROW_SYMBOL);
            this.services.preferences.set(PREF_SORT_FIELD, this.options.sortField, {
               successCallback:
               {
                  fn: function()
                  {
                     // Default sort order configured?
                     var title = eventTarget.srcElement.getAttribute("title");
                     if (title === "true" || title === "false")
                     {
                        me.options.sortAscending = (title === "false");
                        // onSortAscending will fire the metadatRefresh event
                        me.onSortAscending();
                     }
                     else
                     {
                        YAHOO.Bubbling.fire("metadataRefresh");
                     }
                  }
               }
            });
         }
         Event.preventDefault(domEvent);
      },

      /**
       * Sort Field select event handler
       *
       * @method onSortFieldEvent
       * @param field {string} The field
       * @param direction {string} The direction to sort the field in
       */
      onSortFieldEvent: function DL_onSortFieldEvent(field, direction) {
         this.options.sortField = field;
         var me = this;
         this.services.preferences.set(PREF_SORT_FIELD, field, {
            successCallback:
            {
               fn: function()
               {
                  // Default sort order configured?
                  if (direction)
                  {
                     me.options.sortAscending = (direction === "ascending");
                     // onSortAscending will fire the metadatRefresh event
                     me.onSortEvent(direction);
                  }
                  else
                  {
                     YAHOO.Bubbling.fire("metadataRefresh");
                  }
               }
            }
         });
      },

      /**
       * Show/Hide folders button checked handler
       *
       * @method onShowFolders
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onShowFolders: function DL_onShowFolders(e, p_obj)
      {
         this.options.showFolders = this.widgets.showFolders.get("checked");
         this.widgets.showFolders.set("title", this.msg(this.options.showFolders ? "button.folders.hide" : "button.folders.show"));
         this.services.preferences.set(PREF_SHOW_FOLDERS, this.options.showFolders);
         YAHOO.Bubbling.fire("metadataRefresh");
         if (e)
         {
            Event.preventDefault(e);
         }
      },

      /**
       * Show/Hide folders event handler (added to support wrapped widget)
       *
       * @method onShowFoldersEvent
       * @param showFolders {boolean} Whether or not to show folders
       */
      onShowFoldersEvent: function DL_onShowFoldersEvent(showFolders)
      {
         this.options.showFolders = showFolders
         this.services.preferences.set(PREF_SHOW_FOLDERS, this.options.showFolders);
         YAHOO.Bubbling.fire("metadataRefresh");
      },

      /**
       * Select viewRenderer list buttongroup click handler
       *
       * @method onViewRendererSelect
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onViewRendererSelect: function DL_onViewRendererSelect(e, p_obj)
      {
         // Clear previous sort data...
         this.widgets.dataTable.set("sortedBy", null);

         // Get the name of the viewRenderer from the order array as YUI button likes to work with an index
         this.options.viewRendererName = this.options.viewRendererNames[e.newValue.index];
         this.userPreferredView = this.options.viewRendererName;
         this.services.preferences.set(PREF_VIEW_RENDERER, this.options.viewRendererName);
         YAHOO.Bubbling.fire("metadataRefresh");
         if (e)
         {
            Event.preventDefault(e);
         }
      },

      /**
       * Select viewRenderer event handler
       *
       * @method onViewRendererSelectEvent
       * @param view {string} The name of the view to render
       */
      onViewRendererSelectEvent: function DL_onViewRendererSelectEvent(view)
      {
         // Get the name of the viewRenderer from the order array as YUI button likes to work with an index
         var foundView = false;
         for (var i=0; i<this.options.viewRendererNames.length && !foundView; i++)
         {
            foundView = (this.options.viewRendererNames[i] == view);
         }
         if (foundView)
         {
            // Make sure to clear any sort information when a view is changed. This is especially important
            // for table views because if there is more than one table view and a column has been sorted in
            // one that does not exist in the other then an error will be generated (and we wouldn't want that,
            // would we?)
            this.widgets.dataTable.set("sortedBy", null);
            this.options.viewRendererName = view;
            this.userPreferredView = this.options.viewRendererName;
            this.services.preferences.set(PREF_VIEW_RENDERER, this.options.viewRendererName);

            // Update the labels for the setting default views...
            for (var i=0; i<this.options.viewRenderers.length; i++)
            {
               if (this.options.viewRenderers[i].id == this.options.viewRendererName)
               {
                  var setViewSpan = Dom.getElementsByClassName("setDefaultView");
                  setViewSpan[0].innerHTML = this.msg("button.setDefaultView", this.options.viewRenderers[i].label);
                  break;
               }
            }

            YAHOO.Bubbling.fire("metadataRefresh");
         }
      },

      /**
       * Multi-file select button click handler
       *
       * @method onFileSelect
       * @param sType {string} Event type, e.g. "click"
       * @param aArgs {array} Arguments array, [0] = DomEvent, [1] = EventTarget
       * @param p_obj {object} Object passed back from subscribe method
       */
      onFileSelect: function DL_onFileSelect(sType, aArgs, p_obj)
      {
         var domEvent = aArgs[0],
            eventTarget = aArgs[1];

         // Select based upon the className of the clicked item
         this.selectFiles(Alfresco.util.findEventClass(eventTarget));
      },

      /**
       * @method onOptionSelect
       * @param sType {string} Event type, e.g. "click"
       * @param aArgs {array} Arguments array, [0] = DomEvent, [1] = EventTarget
       * @param p_obj {object} Object passed back from subscribe method
       */
      onOptionSelect: function DL_onOptionSelect(sType, aArgs, p_obj)
      {
         var domEvent = aArgs[0],
         eventTarget = aArgs[1];

         // Select based upon the className of the clicked item
         var menuItemClass = Alfresco.util.findEventClass(eventTarget);

         // This piece of code is "borrowed" from Alfresco.util.findEventClass() to actually get the
         // span element so that it can be modified...
         var src = eventTarget.element;
         while ((src !== null) && ((src.tagName.toLowerCase() != "span") || (src.className.indexOf("yui") === 0)))
         {
            src = src.firstChild;
         }
         var menuItemElement = src;

         if (menuItemClass == "showFolders")
         {
            this.options.showFolders = true;
            menuItemElement.innerHTML = this.msg("button.folders.hide");
            Dom.replaceClass(menuItemElement, "showFolders", "hideFolders");
            this.services.preferences.set(PREF_SHOW_FOLDERS, this.options.showFolders);
            YAHOO.Bubbling.fire("metadataRefresh");
         }
         else if (menuItemClass == "hideFolders")
         {
            this.options.showFolders = false;
            menuItemElement.innerHTML = this.msg("button.folders.show");
            Dom.replaceClass(menuItemElement, "hideFolders", "showFolders");
            this.services.preferences.set(PREF_SHOW_FOLDERS, this.options.showFolders);
            YAHOO.Bubbling.fire("metadataRefresh");
         }
         else if (menuItemClass == "showPath")
         {
            this.options.hideNavBar = false;
            menuItemElement.innerHTML = this.msg("button.navbar.hide");
            Dom.replaceClass(menuItemElement, "showPath", "hidePath");
            this.services.preferences.set(PREF_HIDE_NAVBAR, this.options.hideNavBar);
            Dom.setStyle(this.options.associatedToolbar.id + "-navBar", "display", this.options.hideNavBar ? "none" : "block");
         }
         else if (menuItemClass == "hidePath")
         {
            this.options.hideNavBar = true;
            menuItemElement.innerHTML = this.msg("button.navbar.show");
            Dom.replaceClass(menuItemElement, "hidePath", "showPath");
            this.services.preferences.set(PREF_HIDE_NAVBAR, this.options.hideNavBar);
            Dom.setStyle(this.options.associatedToolbar.id + "-navBar", "display", this.options.hideNavBar ? "none" : "block");
         }
         else if (menuItemClass == "fullWindow")
         {
            this.fullscreen.toggleFullScreen(true);
         }
         else if (menuItemClass == "fullScreen")
         {
            this.fullscreen.toggleFullScreen(false);
         }
         else if (menuItemClass == "rss")
         {
            window.location = this.options.associatedToolbar._getRssFeedUrl();
         }
         else if (menuItemClass == "removeDefaultView" || menuItemClass == "setDefaultView")
         {
            if (menuItemClass == "setDefaultView")
            {
               for (var i=0; i<this.options.viewRenderers.length; i++)
               {
                  if (this.options.viewRenderers[i].id == this.options.viewRendererName)
                  {
                     var removeViewSpan = Dom.getElementsByClassName("removeDefaultView");
                     removeViewSpan[0].innerHTML = this.msg("button.removeDefaultView", this.options.viewRenderers[i].label);
                     break;
                  }
               }
            }

            var nodeRef = this.doclistMetadata.parent.nodeRef;
            nodeRef = nodeRef.replace(/:\//g, "");

            var _this = this;
            var dataObj = { "prop_app_defaultViewId" : (menuItemClass == "removeDefaultView" ? "" : this.options.viewRendererName) };
            Alfresco.util.Ajax.jsonRequest({
               method: "POST",
               url: Alfresco.constants.PROXY_URI + "api/node/" + nodeRef + "/formprocessor",
               dataObj: dataObj,
               successCallback: {
                  fn: function() {
                     this._updateDocList.call(this);
                  },
                  scope: this
               },
               failure:
               {
                  message: _this.msg("message.save.failure")
               }
            });
         }
         else if (menuItemClass.indexOf == "view " != -1)
         {
            var viewRequested = menuItemClass.substring(5);
            this.onViewRendererSelectEvent(viewRequested);
         }

         Event.preventDefault(domEvent);
      },

      /**
       * Custom event handler to highlight row.
       *
       * @method onEventHighlightRow
       * @param oArgs.event {HTMLEvent} Event object.
       * @param oArgs.target {HTMLElement} Target element.
       */
      onEventHighlightRow: function DL_onEventHighlightRow(oArgs)
      {
         if (typeof this.viewRenderers[this.options.viewRendererName] === "object")
         {
            this.viewRenderers[this.options.viewRendererName].onEventHighlightRow(this, oArgs);
         }
      },

      /**
       * Custom event handler to unhighlight row.
       *
       * @method onEventUnhighlightRow
       * @param oArgs.event {HTMLEvent} Event object.
       * @param oArgs.target {HTMLElement} Target element.
       */
      onEventUnhighlightRow: function DL_onEventUnhighlightRow(oArgs)
      {
         if (typeof this.viewRenderers[this.options.viewRendererName] === "object")
         {
            this.viewRenderers[this.options.viewRendererName].onEventUnhighlightRow(this, oArgs);
         }
      },


      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR ACTIONS
       * Disconnected event handlers for action event notification
       */

      /**
       * Show more actions pop-up.
       *
       * @method onActionShowMore
       * @param record {object} Object literal representing file or folder to be actioned
       * @param elMore {element} DOM Element of "More Actions" link
       */
      onActionShowMore: function DL_onActionShowMore(record, elMore)
      {
         if (typeof this.viewRenderers[this.options.viewRendererName] === "object")
         {
            this.viewRenderers[this.options.viewRendererName].onActionShowMore(this, record, elMore);
         }
      },

      /**
       * Edit Offline.
       *
       * @override
       * @method onActionEditOffline
       * @param record {object} Object literal representing file or folder to be actioned
       */
      onActionEditOffline: function DL_onActionEditOffline(record)
      {
         if (!this.state.actionEditOfflineActive)
         {
            // Make sure we don't call edit offline twice
            this.state.actionEditOfflineActive = true;

            var nodeRef = record.jsNode.nodeRef,
               displayName = record.displayName;

            this.modules.actions.genericAction(
            {
               success:
               {
                  event:
                  {
                     name: "metadataRefresh"
                  },
                  callback:
                  {
                     fn: function DL_oAEO_success(data)
                     {
                        this.state.actionEditOfflineActive = false;
                        this.options.highlightFile = displayName;

                        // The filterChanged event causes the DocList to update, so we need to run these functions afterwards
                        var fnAfterUpdate = function DL_oAEO_success_afterUpdate()
                        {
                           var isEditingCanceled = false;
                           YAHOO.Bubbling.on("editingCanceled", function(layer, args)
                           {
                              if (data.config.object.nodeRef == args[1].record.workingCopy.sourceNodeRef.replace(/:\//g, ""))
                              {
                                 isEditingCanceled = true;
                              }
                           }, this);

                           var downloadUrl = Alfresco.constants.PROXY_URI + data.json.results[0].downloadUrl;
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
                                    handler: function DL_oAEO_success_download()
                                    {
                                       window.location = downloadUrl;
                                       this.destroy();
                                    },
                                    isDefault: true
                                 },
                                 {
                                    text: this.msg("button.close"),
                                    handler: function DL_oAEO_success_close()
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
                        };
                        this.afterDocListUpdate.push(fnAfterUpdate);
                     },
                     scope: this
                  }
               },
               failure:
               {
                  callback:
                  {
                     fn: function DL_oAEO_failure()
                     {
                        this.state.actionEditOfflineActive = false;
                     },
                     scope: this
                  },
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
         }
      },

      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR PAGE EVENTS
       * Disconnected event handlers for inter-component event notification
       */

      /**
       * MNT-10180: if current page of DocList to update
       * is empty then go to the first page of the DocList
       */
      _checkIfUpdatedPageIsEmpty: function DL__checkIfUpdatedPageIsEmpty()
      {
          var pageOffset = (this.currentPage - 1) * this.options.pageSize;
          if (pageOffset >= this.totalRecords)
          {
              this.widgets.paginator.setPage(1);
              this.currentPage = 1;
          }
      },

      /**
       * Generic file action event handler
       *
       * @method onFileAction
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onFileAction: function DL_onFileAction(layer, args)
      {
         var obj = args[1];
         if (obj)
         {
            if (!obj.multiple)
            {
               this._checkIfUpdatedPageIsEmpty();
               this._updateDocList.call(this);
            }
         }
      },

      /**
       * File or folder renamed event handler
       *
       * @method onFileRenamed
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onFileRenamed: function DL_onFileRenamed(layer, args)
      {
         if (typeof this.viewRenderers[this.options.viewRendererName] === "object")
         {
            this.viewRenderers[this.options.viewRendererName].onFileRenamed(this, layer, args);
         }
      },

      /**
       * DocList Refresh Required event handler
       *
       * @method onDocListRefresh
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (unused)
       */
      onDocListRefresh: function DL_onDocListRefresh(layer, args)
      {
         var obj = args[1];
         if (obj && (obj.highlightFile !== null))
         {
            this.options.highlightFile = obj.highlightFile;
         }
         this._checkIfUpdatedPageIsEmpty();
         this._updateDocList.call(this);
      },

      /**
       * DocList View change filter request event handler
       *
       * @method onChangeFilter
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (new filterId)
       */
      onChangeFilter: function DL_onChangeFilter(layer, args)
      {
         var obj = args[1];

         if ((obj !== null) && (obj.filterId !== null))
         {
            // Should be a filter in the arguments
            var filter = Alfresco.util.cleanBubblingObject(obj),
               strFilter = window.escape(obj.filterId) +
                  (typeof obj.filterData !== "undefined" ?
                     "|" + window.escape(obj.filterData) + (typeof obj.filterDisplay !== "undefined" ? "|" + window.escape(obj.filterDisplay) : "") :
                     "");

            Alfresco.logger.debug("DL_onChangeFilter: ", filter);

            // Only allow drag and drop behaviour if the filter is changed to an actual
            // path (if the filter is anything else such as tags then there won't be a specific
            // location to upload to!)...
            this._removeDragAndDrop();
            if (obj.filterId === "path" || obj.filterId === "favourites")
            {
               this._addDragAndDrop();
            }

            // Flag to indicate the list will likely change
            this.listUpdated = true;

            var objNav =
            {
               filter: strFilter
            };

            // Initial navigation won't fire the History event.
            // multiNavigate won't do anything if the filter is the same, so trigger an update instead, see ACE-2000.
            if (obj.doclistFirstTimeNav || strFilter === YAHOO.util.History.getCurrentState("filter") )
            {
               this._updateDocList.call(this,
               {
                  filter: filter,
                  page: this.currentPage
               });
            }
            else
            {
               if (this.options.usePagination)
               {
                  this.currentPage = 1;
                  objNav.page = "1";
               }

               Alfresco.logger.debug("DL_onChangeFilter: objNav = ", objNav);

               // Do we think the history state will change?
               if (this.options.highlightFile && objNav.filter === YAHOO.util.History.getCurrentState("filter"))
               {
                  YAHOO.Bubbling.fire("highlightFile",
                  {
                     fileName: this.options.highlightFile
                  });
               }

               YAHOO.util.History.multiNavigate(objNav);
            }
         }
      },

      /**
       * DocList View Filter changed event handler
       *
       * @method onFilterChanged
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (new filterId)
       */
      onFilterChanged: function DL_onFilterChanged(layer, args)
      {
         var obj = args[1];
         if ((obj !== null) && (obj.filterId !== null))
         {
            obj.filterOwner = obj.filterOwner || Alfresco.util.FilterManager.getOwner(obj.filterId);

            // Should be a filterId in the arguments
            this.currentFilter = Alfresco.util.cleanBubblingObject(obj);
            Alfresco.logger.debug("DL_onFilterChanged: ", this.currentFilter);
         }
      },

      /**
       * Highlight file event handler
       * Used when a component (including the DocList itself on loading) wants to scroll to and highlight a file
       *
       * @method onHighlightFile
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (filename to be highlighted)
       */
      onHighlightFile: function DL_onHighlightFile(layer, args)
      {
         if (typeof this.viewRenderers[this.options.viewRendererName] === "object")
         {
            this.viewRenderers[this.options.viewRendererName].onHighlightFile(this, layer, args);
         }
      },

      /**
       * Deactivate All Controls event handler
       *
       * @method onDeactivateAllControls
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDeactivateAllControls: function DL_onDeactivateAllControls(layer, args)
      {
         var index, fnDisable = Alfresco.util.disableYUIButton;
         for (index in this.widgets)
         {
            if (this.widgets.hasOwnProperty(index))
            {
               fnDisable(this.widgets[index]);
            }
         }
      },

      /**
       * Deactivate Dynamic Controls event handler
       * Only deactivates specifically defined controls.
       *
       * @method onDeactivateDynamicControls
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDeactivateDynamicControls: function DL_onDeactivateDynamicControls(layer, args)
      {
         var index, fnDisable = Alfresco.util.disableYUIButton;
         for (index in this.dynamicControls)
         {
            if (this.dynamicControls.hasOwnProperty(index))
            {
               fnDisable(this.dynamicControls[index]);
            }
         }
      },

      /**
       * Deactivate Sort Controls event handler
       * Only deactivates specifically defined controls.
       *
       * @method onDeactivateSortControls
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDeactivateSortControls: function DL_onDeactivateSortControls(layer, args)
      {
         var index, fnDisable = Alfresco.util.disableYUIButton;
         for (index in this.sortControls)
         {
            if (this.sortControls.hasOwnProperty(index))
            {
               fnDisable(this.sortControls[index]);
            }
         }
      },

      /**
       * Activate Sort Controls event handler
       * (Re-)Activates controls taking part in dynamic deactivation
       *
       * @method onActivateSortControls
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onActivateSortControls: function DL_onActivateSortControls(layer, args)
      {
         var index, fnEnable = Alfresco.util.enableYUIButton;
         for (index in this.sortControls)
         {
            if (this.sortControls.hasOwnProperty(index))
            {
               fnEnable(this.sortControls[index]);
            }
         }
      },

      /**
       * Activate Dynamic Controls event handler
       * (Re-)Activates controls taking part in dynamic deactivation
       *
       * @method onActivateDynamicControls
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onActivateDynamicControls: function DL_onActivateDynamicControls(layer, args)
      {
         var index, fnEnable = Alfresco.util.enableYUIButton;
         for (index in this.dynamicControls)
         {
            if (this.dynamicControls.hasOwnProperty(index))
            {
               fnEnable(this.dynamicControls[index]);
            }
         }
      },

      /**
       * Like/Unlike event handler
       *
       * @method onLikes
       * @param row {HTMLElement} DOM reference to a TR element (or child thereof)
       */
      onLikes: function DL_onLikes(row)
      {
         var elIdentifier = row;
         if (typeof this.viewRenderers[this.options.viewRendererName] === "object")
         {
            elIdentifier = this.viewRenderers[this.options.viewRendererName].getDataTableRecordIdFromRowElement(this, row);
         }
         var oRecord = this.widgets.dataTable.getRecord(elIdentifier),
            record = oRecord.getData(),
            nodeRef = record.jsNode.nodeRef,
            likes = record.likes;

         likes.isLiked = !likes.isLiked;
         likes.totalLikes += (likes.isLiked ? 1 : -1);

         var responseConfig =
         {
            successCallback:
            {
               fn: function DL_onLikes_success(event, p_nodeRef)
               {
                  var data = event.json.data;
                  if (data)
                  {
                     // Update the record with the server's value
                     var oRecord = this._findRecordByParameter(p_nodeRef, "nodeRef"),
                        record = oRecord.getData(),
                        node = record.node,
                        likes = record.likes;

                     likes.totalLikes = data.ratingsCount;
                     this.widgets.dataTable.updateRow(oRecord, record);

                     // Post to the Activities Service on the "Like" action
                     if (likes.isLiked)
                     {
                        var activityData =
                        {
                           fileName: record.fileName,
                           nodeRef: node.nodeRef
                        };
                        if (node.isContainer)
                        {
                           this.modules.actions.postActivity(this.options.siteId, "folder-liked", "folder-details", activityData);
                        }
                        else
                        {
                           this.modules.actions.postActivity(this.options.siteId, "file-liked", "document-details", activityData);
                        }
                     }
                  }
               },
               scope: this,
               obj: nodeRef.toString()
            },
            failureCallback:
            {
               fn: function DL_onLikes_failure(event, p_nodeRef)
               {
                  // Reset the flag to it's previous state
                  var oRecord = this._findRecordByParameter(p_nodeRef, "nodeRef"),
                     record = oRecord.getData(),
                     likes = record.likes;

                  likes.isLiked = !likes.isLiked;
                  likes.totalLikes += (likes.isLiked ? 1 : -1);
                  this.widgets.dataTable.updateRow(oRecord, record);
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     text: this.msg("message.save.failure", record.displayName)
                  });
               },
               scope: this,
               obj: nodeRef.toString()
            }
         };

         if (likes.isLiked)
         {
            this.services.likes.set(nodeRef, 1, responseConfig);
         }
         else
         {
            this.services.likes.remove(nodeRef, responseConfig);
         }
         this.widgets.dataTable.updateRow(oRecord, record);
      },

      /**
       * Handler to set/reset favourite for document or folder
       *
       * @method onFavourite
       * @private
       * @param row {HTMLElement} DOM reference to a TR element (or child thereof)
       * @param prefKey {String} The preferences key
       */
      onFavourite: function DL_onFavourite(row)
      {
         var elIdentifier = row;
         if (typeof this.viewRenderers[this.options.viewRendererName] === "object")
         {
            elIdentifier = this.viewRenderers[this.options.viewRendererName].getDataTableRecordIdFromRowElement(this, row);
         }
         var oRecord = this.widgets.dataTable.getRecord(elIdentifier),
            record = oRecord.getData(),
            node = record.node,
            nodeRef = node.nodeRef;

         record.isFavourite = !record.isFavourite;
         this.widgets.dataTable.updateRow(oRecord, record);

         var fnPref = record.isFavourite ? "favouriteDocumentOrFolder" : "unFavouriteDocumentOrFolder";
         var responseConfig =
         {
            failureCallback:
            {
               fn: function DL_oFD_failure(event, p_oRow)
               {
                  // Reset the flag to it's previous state
                  var oRecord = this.widgets.dataTable.getRecord(p_oRow),
                     record = oRecord.getData(),
                     node = record.node;

                  record.isFavourite = !record.isFavourite;
                  this.widgets.dataTable.updateRow(oRecord, record);
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     text: this.msg("message.save.failure", record.displayName)
                  });
               },
               scope: this,
               obj: row
            }
         };
         this.services.preferences[fnPref].call(this.services.preferences, node, responseConfig);
      },

      /**
       * Handler to inform of a resize to resizable panel
       *
       * @method onResizerChanged
       * @private
       */
      onResizerChanged: function DL_onResizerChanged()
      {
         this._resizeRowContainers();
      },

      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Helper to resize the container elements for each row to ensure the wrapping
       * of long filenames is correctly maintained when the window or filters panel is resized.
       * 
       * @method _resizeRowContainers
       */
      _resizeRowContainers: function DL__resizeRowContainers(width)
      {
         if (!YAHOO.lang.isNumber(width))
         {
            var fpanel = Dom.get("alf-filters"),
                offset = (fpanel ? parseInt(fpanel.style.width, 10) : 160) + 390,
                width = (Dom.getViewportWidth() - offset) + "px";
         }
         else
         {
            // Width supplied, no action required.
            width = (width - 390) + "px";
         }
         var nodes = YAHOO.util.Selector.query('h3.filename', this.id + "-documents");
         for (var i=0; i<nodes.length; i++)
         {
            nodes[i].parentNode.style.width = width;
         }
      },

      /**
       * Resets the YUI DataTable errors to our custom messages
       * NOTE: Scope could be YAHOO.widget.DataTable, so can't use "this"
       *
       * @method _setDefaultDataTableErrors
       * @param dataTable {object} Instance of the DataTable
       */
      _setDefaultDataTableErrors: function DL__setDefaultDataTableErrors(dataTable)
      {
         var msg = Alfresco.util.message;
         dataTable.set("MSG_EMPTY", msg("message.empty", "Alfresco.DocumentList"));
         dataTable.set("MSG_ERROR", msg("message.error", "Alfresco.DocumentList"));
      },

      /**
       * Update the set default and remove default view menu options based on the current
       * user permissions, the current view and any currently set view default.
       *
       * @method _updateDefaultViewOptions
       * @param pathChanged {boolean} Indicates whether or not a path change has triggered this update
       */
      _updateDefaultViewOptions: function DL__updateDefaultViewOptions(pathChanged) {
         // Handle changes to the set default view and remove default view options...
         // Get references to the menu options:
         var removeViewSpan = Dom.getElementsByClassName("removeDefaultView");
         var removeViewOption = null;
         if (removeViewSpan.length > 0)
         {
            removeViewOption = removeViewSpan[0].parentNode.parentNode;
         }
         var setViewSpan = Dom.getElementsByClassName("setDefaultView");
         var setViewOption = null;
         if (setViewSpan.length > 0)
         {
            setViewOption = setViewSpan[0].parentNode.parentNode;
         }

         if (this.options.userIsSiteManager ||
             (this.doclistMetadata.parent.properties != null &&
              this.doclistMetadata.parent.properties["cm:creator"].userName == Alfresco.constants.USERNAME))
         {
            // If the user is either the site manager or the creator of the container...
            if (this.doclistMetadata.parent.properties != null &&
                this.doclistMetadata.parent.properties["app:defaultViewId"] != null &&
                this.doclistMetadata.parent.properties["app:defaultViewId"] != "")
            {
               // Update the option labels for setting default views...
               for (var i=0; i<this.options.viewRenderers.length; i++)
               {
                  if (this.options.viewRenderers[i].id == this.doclistMetadata.parent.properties["app:defaultViewId"])
                  {
                     removeViewSpan[0].innerHTML = this.msg("button.removeDefaultView", this.options.viewRenderers[i].label);
                     break;
                  }
               }

               // A default view has been set so the option to remove it is valid...
               Dom.removeClass(removeViewOption, "hidden");
               if (this.doclistMetadata.parent.properties["app:defaultViewId"] == this.options.viewRendererName)
               {
                  // The current view is the default view so there is no need to show the set default view option...
                  Dom.addClass(setViewOption, "hidden");
               }
               else
               {
                  // The current view is NOT the default view so we need to offer it as an option...
                  Dom.removeClass(setViewOption, "hidden");
               }
            }
            else
            {
               // No default view has been set yet, so the set view as default option can be shown...
               Dom.removeClass(setViewOption, "hidden");
               // ...but the remove option can't be...
               Dom.addClass(removeViewOption, "hidden");
            }
         }
         else
         {
            // If the user does not have write permissions then we want to hide both options...
            Dom.addClass(setViewOption, "hidden");
            Dom.addClass(removeViewOption, "hidden");
         }

         if (pathChanged)
         {
            if (this.doclistMetadata.parent.properties &&
                this.doclistMetadata.parent.properties["app:defaultViewId"] != null &&
                this.doclistMetadata.parent.properties["app:defaultViewId"] != "")
            {
               // If the path has changed then we need to reset the view renderer to the currently
               // renderer set as the default...
               this.options.viewRendererName = this.doclistMetadata.parent.properties["app:defaultViewId"];
               Dom.addClass(setViewOption, "hidden");
            }
            else
            {
               // Return the view to the user preference...
               if (this.userPreferredView != this.options.viewRendererName)
               {
                  this.options.viewRendererName = this.userPreferredView;
               }
            }
         }

         // Update the option labels for setting default views...
         for (var i=0; i<this.options.viewRenderers.length; i++)
         {
            if (this.options.viewRenderers[i].id == this.options.viewRendererName)
            {
               setViewSpan[0].innerHTML = this.msg("button.setDefaultView", this.options.viewRenderers[i].label);
               break;
            }
         }
      },

      /**
       * Updates document list by calling data webscript with current site and path
       *
       * @method _updateDocList
       * @param p_obj.filter {object} Optional filter to navigate with
       * @param p_obj.page {string} Optional page to navigate to (defaults to this.currentPage)
       */
      _updateDocList: function DL__updateDocList(p_obj)
      {
         p_obj = p_obj || {};
         Alfresco.logger.debug("DL__updateDocList: ", p_obj.filter, p_obj.page);
         var successFilter = YAHOO.lang.merge(
            {
               doclistFirstTimeNav: false
            }, p_obj.filter !== undefined ? p_obj.filter : this.currentFilter),
            successPage = p_obj.page !== undefined ? p_obj.page : this.currentPage,
            loadingMessage = null,
            timerShowLoadingMessage = null,
            me = this,
            params =
            {
               filter: successFilter,
               page: successPage
            };

         // Clear the current document list if the data webscript is taking too long
         var fnShowLoadingMessage = function DL_fnShowLoadingMessage()
         {
            Alfresco.logger.debug("DL__uDL_fnShowLoadingMessage: slow data webscript detected.");
            // Check the timer still exists. This is to prevent IE firing the event after we cancelled it. Which is "useful".
            if (timerShowLoadingMessage)
            {
               loadingMessage = Alfresco.util.PopupManager.displayMessage(
               {
                  displayTime: 0,
                  text: '<span class="wait">' + $html(this.msg("message.loading")) + '</span>',
                  noEscape: true
               });

               if (YAHOO.env.ua.ie > 0)
               {
                  this.loadingMessageShowing = true;
               }
               else
               {
                  loadingMessage.showEvent.subscribe(function()
                  {
                     this.loadingMessageShowing = true;
                  }, this, true);
               }
            }
         };

         // Reset the custom error messages
         this._setDefaultDataTableErrors(this.widgets.dataTable);

         // Reset preview tooltips array
         this.previewTooltips = [];

         this.insituEditors = [];

         // More Actions menu no longer relevant
         this.hideMoreActionsFn = null;

         // Slow data webscript message
         this.loadingMessageShowing = false;
         timerShowLoadingMessage = YAHOO.lang.later(this.options.loadingMessageDelay, this, fnShowLoadingMessage);

         var destroyLoaderMessage = function DL__uDL_destroyLoaderMessage()
         {
            if (timerShowLoadingMessage)
            {
               // Stop the "slow loading" timed function
               timerShowLoadingMessage.cancel();
               timerShowLoadingMessage = null;
            }

            if (loadingMessage)
            {
               if (this.loadingMessageShowing)
               {
                  // Safe to destroy
                  loadingMessage.destroy();
                  loadingMessage = null;
               }
               else
               {
                  // Wait and try again later. Scope doesn't get set correctly with "this"
                  YAHOO.lang.later(100, me, destroyLoaderMessage);
               }
            }
         };

         var successHandler = function DL__uDL_successHandler(sRequest, oResponse, oPayload)
         {
            destroyLoaderMessage();
            // Updating the Doclist may change the file selection
            var fnAfterUpdate = function DL__uDL_sH_fnAfterUpdate()
            {
               YAHOO.Bubbling.fire("activateDynamicControls");
               YAHOO.Bubbling.fire("selectedFilesChanged");
               this.listUpdated = false;
               if (this.currentFilter.filterId == "recentlyModified" || this.currentFilter.filterId == "recentlyAdded")
               {
                  YAHOO.Bubbling.fire("deactivateSortControls");
               }
               else
               {
                  YAHOO.Bubbling.fire("activateSortControls");
               }
            };
            this.afterDocListUpdate.push(fnAfterUpdate);

            Alfresco.logger.debug("currentFilter was:", this.currentFilter, "now:", successFilter);
            Alfresco.logger.debug("currentPage was [" + this.currentPage + "] now [" + successPage + "]");
            this.currentFilter = successFilter;
            this.currentPage = successPage;
            var pathChanged = false;
            if (successFilter.filterId === "path")
            {
               Alfresco.logger.debug("currentPath was [" + this.currentPath + "] now [" + successFilter.filterData + "]");
               pathChanged = (this.currentPath != successFilter.filterData);
               this.currentPath = successFilter.filterData;
            }

            delete successFilter.doclistFirstTimeNav;
            YAHOO.Bubbling.fire("filterChanged", successFilter);

            // Ensure that the correct view is selected for the current folder/user preference...
            this._updateDefaultViewOptions(pathChanged);

            // Call destroy view on all viewRenderers then renderView on the selected view
            for (var i = 0, ii = this.options.viewRendererNames.length; i < ii; i++)
            {
               if (typeof this.viewRenderers[this.options.viewRendererNames[i]] === "object")
               {
                  this.viewRenderers[this.options.viewRendererNames[i]].destroyView(this, sRequest, oResponse, oPayload);
               }
            }
            if (typeof this.viewRenderers[this.options.viewRendererName] === "object")
            {
               this.viewRenderers[this.options.viewRendererName].renderView(this, sRequest, oResponse, oPayload);
            }
            else
            {
               // TODO Display message that no view renderers are available and render detailed anyway
            }
         };

         var failureHandler = function DL__uDL_failureHandler(sRequest, oResponse)
         {
            destroyLoaderMessage();
            // Clear out deferred functions
            this.afterDocListUpdate = [];

            if (oResponse.status === 401)
            {
               // Our session has likely timed-out, so refresh to offer the login page
               window.location.reload(true);
            }
            else
            {
               try
               {
                  if (oResponse.status === 404)
                  {
                     // Folder not found (via the HTTP "404 Not Found" response) - deactivate dynamic controls only
                     YAHOO.Bubbling.fire("deactivateDynamicControls");
                     YAHOO.Bubbling.fire("deactivateSortControls");
                  }
                  else
                  {
                     // Site or container not found (e.g. via the HTTP "410 Gone" response) or more serious - deactivate all controls
                     YAHOO.Bubbling.fire("deactivateAllControls");
                  }

                  var fnAfterFailedUpdate = function DL__uDL_failureHandler_fnAfterUpdate(responseMsg, responseStatus)
                  {
                     return function DL__uDL_failureHandler_afterUpdate()
                     {
                        this.widgets.paginator.setState(
                        {
                           totalRecords: 0
                        });
                        this.widgets.paginator.render();
                        this.widgets.dataTable.set("MSG_ERROR", responseMsg);
                        if (responseStatus === 410)
                        {
                           this.widgets.dataTable.showTableMessage(responseMsg, YAHOO.widget.DataTable.CLASS_DISABLED);
                        }
                        else
                        {
                           this.widgets.dataTable.showTableMessage(responseMsg, YAHOO.widget.DataTable.CLASS_ERROR);
                        }
                     };
                  };

                  this.afterDocListUpdate.push(fnAfterFailedUpdate(YAHOO.lang.JSON.parse(oResponse.responseText).message, oResponse.status));
                  this.widgets.dataTable.initializeTable();
                  this.widgets.dataTable.render();
                  this.listUpdated = false;
               }
               catch(e)
               {
                  Alfresco.logger.error(e);
                  this._setDefaultDataTableErrors(this.widgets.dataTable);
               }
            }
         };

         // Update the DataSource
         if (params.filter && params.filter.filterId === "path")
         {
            params.path = params.filter.filterData;

            // ALF-17145
            if (this.currentFilter.filterData != params.path)
            {
               Alfresco.logger.debug("currentFilter.filterData was [" + this.currentFilter.filterData + "] now [" + params.path + "]");
               this.currentFilter.filterData = params.path;
            }
         }
         var requestParams = this._buildDocListParams(params);
         Alfresco.logger.debug("DataSource requestParams: ", requestParams);
         this.widgets.dataSource.sendRequest(requestParams,
         {
            success: successHandler,
            failure: failureHandler,
            scope: this
         });
      },

      /**
       * Build URI parameter string for doclist JSON data webscript
       *
       * @method _buildDocListParams
       * @param p_obj.page {string} Page number
       * @param p_obj.pageSize {string} Number of items per page
       * @param p_obj.path {string} Path to query
       * @param p_obj.type {string} Filetype to filter: "all", "documents", "folders"
       * @param p_obj.site {string} Current site
       * @param p_obj.container {string} Current container
       * @param p_obj.filter {string} Current filter
       */
      _buildDocListParams: function DL__buildDocListParams(p_obj)
      {
         // Essential defaults
         var siteMode = $isValueSet(this.options.siteId),
            obj =
            {
               path: this.currentPath,
               type: this.options.showFolders ? "all" : "documents",
               site: this.options.siteId,
               container: this.options.containerId,
               filter: this.currentFilter
            };

         // Pagination in use?
         if (this.options.usePagination)
         {
            obj.page = this.widgets.paginator.getCurrentPage() || this.currentPage;
            obj.pageSize = this.widgets.paginator.getRowsPerPage();
         }

         // Passed-in overrides
         if (typeof p_obj === "object")
         {
            obj = YAHOO.lang.merge(obj, p_obj);
         }

         // Build the URI stem
         var isSharedFiles = ("alfresco://company/shared" == this.options.rootNode);
         var uriPart = siteMode ? "site/{site}/{container}" : isSharedFiles ? "node/alfresco/company/shared" : "node/alfresco/user/home",
            params = YAHOO.lang.substitute("{type}/" + uriPart + (obj.filter.filterId === "path" ? "{path}" : ""),
            {
               type: encodeURIComponent(obj.type),
               site: encodeURIComponent(obj.site),
               container: encodeURIComponent(obj.container),
               path: $combine("/", Alfresco.util.encodeURIPath(obj.path))
            });

         // Filter parameters
         params += "?filter=" + encodeURIComponent(obj.filter.filterId);
         if (obj.filter.filterData && obj.filter.filterId !== "path")
         {
            params += "&filterData=" + encodeURIComponent(obj.filter.filterData);
         }

         // Paging parameters
         if (this.options.usePagination)
         {
            params += "&size=" + obj.pageSize  + "&pos=" + obj.page;
         }

         // Sort parameters
         params += "&sortAsc=" + this.options.sortAscending + "&sortField=" + encodeURIComponent(this.options.sortField);

         if (!siteMode)
         {
            // Repository mode (don't resolve Site-based folders)
            params += "&libraryRoot=" + encodeURIComponent(this.options.rootNode.toString());
         }

         // View mode and No-cache
         params += "&view=" + this.actionsView + "&noCache=" + new Date().getTime();

         return params;
      },

      /**
       * Searches the current recordSet for a record with the given parameter value
       *
       * @private
       * @method _findRecordByParameter
       * @param p_value {string} Value to find
       * @param p_parameter {string} Parameter to look for the value in
       * @return {YAHOO.widget.Record} Successful search result or null
       */
      _findRecordByParameter: function DL__findRecordByParameter(p_value, p_parameter)
      {
         var oRecordSet = this.widgets.dataTable.getRecordSet(),
            oRecord, record, i, j;

         for (i = 0, j = oRecordSet.getLength(); i < j; i++)
         {
            oRecord = oRecordSet.getRecord(i);
            record = oRecord.getData();

            if (record[p_parameter] === p_value || (record.node && record.node[p_parameter] === p_value))
            {
               return oRecord;
            }
         }
         return null;
      }
   }, true);
})();
