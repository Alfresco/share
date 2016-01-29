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
 * DocumentList TreeView component.
 * 
 * @namespace Alfresco
 * @class Alfresco.DocListTree
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
    var $combine = Alfresco.util.combinePaths;

   /**
    * DocumentList TreeView constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.DocListTree} The new DocListTree instance
    * @constructor
    */
   Alfresco.DocListTree = function DLT_constructor(htmlId)
   {
      Alfresco.DocListTree.superclass.constructor.call(this, "Alfresco.DocListTree", htmlId, ["treeview", "json"]);

      // Path filterId
      this.filterId = "path";
      
      // Register with Filter Manager
      Alfresco.util.FilterManager.register(this.name, this.filterId);
      
      // Initialise prototype properties
      this.currentFilter = {};
      this.pathsToExpand = [];

      // Decoupled event listeners
      YAHOO.Bubbling.on("folderCopied", this.onFolderCopied, this);
      YAHOO.Bubbling.on("folderCreated", this.onFolderCreated, this);
      YAHOO.Bubbling.on("folderDeleted", this.onFolderDeleted, this);
      YAHOO.Bubbling.on("folderMoved", this.onFolderMoved, this);
      YAHOO.Bubbling.on("folderRenamed", this.onFolderRenamed, this);
      YAHOO.Bubbling.on("filterChanged", this.onFilterChanged, this);
      YAHOO.Bubbling.on("dropTargetOwnerRequest", this.onDropTargetOwnerRequest, this);
      YAHOO.Bubbling.on("documentDragOver", this.onDocumentDragOver, this);
      YAHOO.Bubbling.on("documentDragOut", this.onDocumentDragOut, this);

      return this;
   };
   
   YAHOO.extend(Alfresco.DocListTree, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       */
      options:
      {
         /**
          * Current siteId.
          * 
          * @property siteId
          * @type string
          */
         siteId: "",

         /**
          * ContainerId representing root container
          *
          * @property containerId
          * @type string
          * @default "documentLibrary"
          */
         containerId: "documentLibrary",
         
         /**
          * Evaluate child folders flag
          *
          * @property evaluateChildFolders
          * @type boolean
          * @default true
          */
         evaluateChildFolders: true,

         /**
          * Maximum folder count configuration setting
          *
          * @property maximumFolderCount
          * @type int
          * @default -1
          */
         maximumFolderCount: -1,
         
         /**
          * Indicates whether or not to set each tree node as a YUI Drag and Drop
          * target.
          * 
          * @property setDropTargets
          * @type boolean
          * @default false
          */
         setDropTargets: false,
         
         customFolderStyleConfig: null
      },
      
      /**
       * Flag set after TreeView instantiated.
       * 
       * @property isReady
       * @type boolean
       */
      isReady: false,

      /**
       * Initial filter on page load.
       * 
       * @property initialFilter
       * @type string
       */
      initialFilter: null,

      /**
       * Current path being browsed.
       * 
       * @property currentPath
       * @type string
       */
      currentPath: "",

      /**
       * Currently active filter
       * 
       * @property currentFilter
       * @type object
       */
       currentFilter: null,

      /**
       * Tracks if this component is the active filter owner.
       * 
       * @property isFilterOwner
       * @type boolean
       */
      isFilterOwner: false,

      /**
       * Paths we have to expand as a result of a deep navigation event.
       * 
       * @property pathsToExpand
       * @type array
       */
      pathsToExpand: null,

      /**
       * Selected tree node.
       * 
       * @property selectedNode
       * @type {YAHOO.widget.Node}
       */
      selectedNode: null,

      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function DLT_onReady()
      {
         // Reference to self - used in inline functions
         var me = this;
         
         // Create twister from our H2 tag
         Alfresco.util.createTwister(this.id + "-h2", this.name.substring(this.name.lastIndexOf(".") + 1));
         
         /**
          * Dynamically loads TreeView nodes.
          * This MUST be inline in order to have access to the Alfresco.DocListTree class.
          * @method fnLoadNodeData
          * @param node {object} Parent node
          * @param fnLoadComplete {function} Expanding node's callback function
          */
         this.fnLoadNodeData = function DLT_oR_fnLoadNodeData(node, fnLoadComplete)
         {
            // Get the path this node refers to
            var nodePath = node.data.path;

            // Prepare URI for XHR data request
            var uri = me._buildTreeNodeUrl.call(me, nodePath);

            // Prepare the XHR callback object
            var callback =
            {
               success: function DLT_lND_success(oResponse)
               {
                  var results = YAHOO.lang.JSON.parse(oResponse.responseText), item, treeNode;
                  
                  // Update parent node's nodeRef if we didn't have it before
                  if (results.parent && node.data.nodeRef.length === 0)
                  {
                     node.data.nodeRef = results.parent.nodeRef;
                  }

                  if (results.items)
                  {
                     for (var i = 0, j = results.items.length; i < j; i++)
                     {
                        item = results.items[i];
                        item.path = $combine(nodePath, item.name);
                        treeNode = this._buildTreeNode(item, node, false);

                        if (!item.hasChildren)
                        {
                           treeNode.isLeaf = true;
                        }
                     }
                  }
                  
                  if (results.resultsTrimmed)
                  {
                     tempNode = new YAHOO.widget.TextNode(
                     {
                        label: "<" + this.msg("message.folders-trimmed", results.items.length) + ">",
                        hasIcon: false,
                        style: "folders-trimmed"
                     }, node, false);
                  }
                  
                  /**
                  * Execute the node's loadComplete callback method which comes in via the argument
                  * in the response object
                  */
                  oResponse.argument.fnLoadComplete();

                  YAHOO.Bubbling.fire("docLibTreeLoadComplete");
               },

               // If the XHR call is not successful, fire the TreeView callback anyway
               failure: function DLT_lND_failure(oResponse)
               {
                  if (oResponse.status == 401)
                  {
                     // Our session has likely timed-out, so refresh to offer the login page
                     window.location.reload();
                  }
                  else
                  {
                     try
                     {
                        var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                        
                        // Get the "Documents" node
                        var rootNode = this.widgets.treeview.getRoot();
                        var docNode = rootNode.children[0];
                        docNode.isLoading = false;
                        docNode.isLeaf = true;
                        if (oResponse.status == 403)
                        {
                           docNode.label = this.msg("message.refresh.failure.forbidden");
                        }
                        else
                        {
                           docNode.label = response.message;
                           docNode.labelStyle = "ygtverror";
                        }
                        rootNode.refresh();
                     }
                     catch(e)
                     {
                     }
                  }
               },
               
               // Callback function scope
               scope: me,

               // XHR response argument information
               argument:
               {
                  "node": node,
                  "fnLoadComplete": fnLoadComplete
               }
            };

            // Make the XHR call using Connection Manager's asyncRequest method
            YAHOO.util.Connect.asyncRequest('GET', uri, callback);
         };

         // Build the TreeView widget
         this._buildTree();
         
         this.isReady = true;
         if (this.initialFilter !== null)
         {
            // We weren't ready for the first filterChanged event, so fake it here
            this.onFilterChanged("filterChanged",
            [
               null,
               this.initialFilter
            ]);
         }
      },

      /**
       * Handles "dropTargetOwnerRequest" by determining whether or not the target belongs to the TreeView
       * widget, and if it does determines it's nodeRef and uses the callback function with it.
       * 
       * @method onDropTargetOwnerRequest
       * @property layer The name of the event
       * @property args The event payload
       */
      onDropTargetOwnerRequest: function DLT_onDropTargetOwnerRequest(layer, args)
      {
         if (args && args[1] && args[1].elementId)
         {
            var node = this.widgets.treeview.getNodeByElement(Dom.get(args[1].elementId));
            if (node != null)
            {
               // Perform the drag out to clear the highlight...
               this.onDocumentDragOut(layer, args);
               
               var nodeRef = node.data.nodeRef;
               var path = node.data.path;
               args[1].callback.call(args[1].scope, nodeRef, path);
            }
         }
      },
      
      /**
       * Handles applying the styling and node creation required when a document is dragged
       * over a tree node.
       * 
       * @method onDocumentDragOver
       * @property layer The name of the event
       * @property args The event payload
       */
      onDocumentDragOver: function DLTB_onDocumentDragOver(layer, args)
      {
         if (args && args[1] && args[1].elementId)
         {
            var rootEl = this.widgets.treeview.getEl();
            if (args[1].event.clientX > rootEl.clientWidth)
            {
               // If the current x co-ordinate of the mouse pointer is greater than the width
               // of the tree element then we shouldn't add a highlight. This is to address
               // the issue where the overflow of wide tree nodes is hidden behind the 
               // document list. Without this test it is possible to show a highlight on 
               // a tree node when it appears as though the mouse is not over it.
            }
            else
            {
               // The current x co-ordinate of the mouse pointer is within the tree element so
               // the node can be highlighted...
               var dropTargetEl = Dom.get(args[1].elementId); 
               if (dropTargetEl != this.widgets.treeview.getEl())
               {
                  var node = this.widgets.treeview.getNodeByElement(dropTargetEl);
                  if (node != null)
                  {
                     var currEl = dropTargetEl;
                     while (currEl.tagName != "TABLE")
                     {
                        currEl = currEl.parentNode;
                     }
                     Dom.addClass(currEl, "documentDragOverHighlight");
                     
                     var folderCell = dropTargetEl.children[dropTargetEl.children.length - 2];
                     while (folderCell.children.length == 1)
                     {
                        var arrowSpan = document.createElement("span");
                        Dom.addClass(arrowSpan, "documentDragOverArrow");
                        folderCell.appendChild(arrowSpan);
                     }
                  }
               }
            }
         }
      },
      
      /**
       * Handles applying the styling and node deletion required when a document is dragged
       * out of a tree node.
       *
       * @method onDocumentDragOut
       * @property layer The name of the event
       * @property args The event payload
       */
      onDocumentDragOut: function DLTB_onDocumentDragOut(layer, args)
      {
         if (args && args[1] && args[1].elementId)
         {
            var dropTargetEl = Dom.get(args[1].elementId); 
            if (dropTargetEl == this.widgets.treeview.getEl())
            {
               // If the document has been dragged out of the tree element then we need 
               // to remove any highlight and arrow from previously highlighted tree nodes.
               // This would be the case if the highlighted tree node is wider than the 
               // tree element and the mouse has moved to the right of the splitter so is
               // outside of the tree but still over the tree node...
               var highlights = Dom.getElementsByClassName("documentDragOverHighlight", "table", dropTargetEl); // Should be only one
               for (var i = 0, j = highlights.length; i < j ; i++)
               {
                  Dom.removeClass(highlights[i], "documentDragOverHighlight");
               }
               var arrows = Dom.getElementsByClassName("documentDragOverArrow", "span", dropTargetEl);
               for (var i = 0, j = arrows.length; i < j ; i++)
               {
                  arrows[i].parentNode.removeChild(arrows[i]);
               }
            }
            else
            {
               // If the document has been dragged out of a tree node then we need to 
               // remove the highlight and arrow previously added when the document was
               // dragged over it...
               var node = this.widgets.treeview.getNodeByElement(dropTargetEl);
               if (node != null)
               {
                  var currEl = dropTargetEl;
                  while (currEl.tagName != "TABLE")
                  {
                     currEl = currEl.parentNode;
                  }
                  Dom.removeClass(currEl, "documentDragOverHighlight");
                  var folderCell = dropTargetEl.children[dropTargetEl.children.length - 2];
                  while (folderCell.children.length > 1)
                  {
                     folderCell.removeChild(Dom.getLastChild(folderCell));
                  }
               }
            }
         }
      },
      
      /**
       * Fired by YUI TreeView when a node has finished expanding
       * @method onExpandComplete
       * @param oNode {YAHOO.widget.Node} the node recently expanded
       */
      onExpandComplete: function DLT_onExpandComplete(oNode)
      {
         // Make sure the tree's Dom has been updated
         this.widgets.treeview.render();
         
         // Redrawing the tree will clear the highlight
         if (this.isFilterOwner)
         {
            this._showHighlight(true);
         }
         
         if (this.pathsToExpand && this.pathsToExpand.length > 0)
         {
            var node = this.widgets.treeview.getNodeByProperty("path", this.pathsToExpand.shift());
            if (node !== null)
            {
               if (node.data.path == this.currentPath)
               {
                  this._updateSelectedNode(node);
               }
               Alfresco.logger.debug("node.expand: DLT_onExpandComplete");
               node.expand();
            }
         }
         else if (this.initialFilter !== null)
         {
            // We missed the filterChanged event, so fake it here
            this.onFilterChanged("filterChanged",
            [
               null,
               {
                  filterId: this.initialFilter.filterId,
                  filterOwner: this.initialFilter.filterOwner,
                  filterData: this.initialFilter.filterData
               }
            ]);
            this.initialFilter = null;
         }
         else
         {
            // Finished expanding, can now safely set DND targets...
            this._applyDropTargets();
         }
      },

      /**
       * Creates the drag and drop targets within the tree. The targets get removed
       * each time that the tree is refreshed in anyway, so it is imperative that they
       * get reset when required.
       * 
       * @method _applyDropTargets
       */
      _applyDropTargets: function DLT__applyDropTargets()
      {
         if (this.options.setDropTargets)
         {
            var rootEl = this.widgets.treeview.getEl();
            
            // Set the root element of the tree as a drop target. This is necessary in order
            // to handle the specific problem of the hidden overflow of tree nodes being at
            // the same location of the screen as the main DocumentList drop targets. Drop events
            // will be ignored for this element, but dragOut events will be used to ensure that
            // all tree highlights are cleared.
            new YAHOO.util.DDTarget(rootEl);
            Dom.addClass(rootEl, "documentDroppableHighlights");
            
            var dndTargets = Dom.getElementsByClassName("ygtvrow", "tr", rootEl);
            for (var i = 0, j = dndTargets.length; i < j; i++)
            {
               new YAHOO.util.DDTarget(dndTargets[i]);
               Dom.addClass(dndTargets[i], "documentDroppable");
               Dom.addClass(dndTargets[i], "documentDroppableHighlights");
            }
         }
      },
         
      /**
       * Fired by YUI TreeView when a node label is clicked
       * @method onNodeClicked
       * @param args.event {HTML Event} the event object
       * @param args.node {YAHOO.widget.Node} the node clicked
       * @return allowExpand {boolean} allow or disallow node expansion
       */
      onNodeClicked: function DLT_onNodeClicked(args)
      {
         var node = args.node;
         
         if (this.isFilterOwner && node == this.selectedNode)
         {
            YAHOO.Bubbling.fire("metadataRefresh");
         }
         else
         {
            this._updateSelectedNode(node);

            // Fire the change filter event
            YAHOO.Bubbling.fire("changeFilter",
            {
               filterOwner: this.name,
               filterId: this.filterId,
               filterData: node.data.path
            });
         }

         Event.stopEvent(args.event);
         // Prevent the tree node from expanding (TODO: user preference?)
         return false;
      },

      /**
       * Path changed handler
       * @method pathChanged
       * @param path {string} New path
       * @param flags {object} Logic control flags
       */
      pathChanged: function DLT_pathChanged(path, flags)
      {
         // ensure path starts with leading slash
         path = $combine("/", path);
         this.currentPath = path;

         // Search the tree to see if this path's node is expanded
         var node = this.widgets.treeview.getNodeByProperty("path", path);
         if (node !== null)
         {
            // Node found
            this._updateSelectedNode(node);
            if (!node.expanded)
            {
               Alfresco.logger.debug("node.expand: DLT_pathChanged", path);
               node.expand();
            }
            while (node.parent !== null)
            {
               node = node.parent;
               if (!node.expanded)
               {
                  Alfresco.logger.debug("node.expand: DLT_onPathChanged (parent)", path);
                  node.expand();
               }
            }
            return;
         }
         
         /**
          * The path's node hasn't been loaded into the tree. Create a stack
          * of parent paths that we need to expand one-by-one in order to
          * eventually display the current path's node
          */
         var paths = path.split("/"),
            expandPath = "/";
         // Check for root path special case (split will have created 2 empty array members)
         if (path === "/")
         {
            paths = [""];
         }
         for (var i = 0; i < paths.length; i++)
         {
            // Push the path onto the list of paths to be expanded
            expandPath = $combine(expandPath, paths[i]);
            this.pathsToExpand.push(expandPath);
         }
         
         // Kick off the expansion process by expanding the first unexpanded path
         do
         {
            node = this.widgets.treeview.getNodeByProperty("path", this.pathsToExpand.shift());
         } while (this.pathsToExpand.length > 0 && node && node.expanded);
         
         if (node !== null)
         {
            Alfresco.logger.debug("node.expand: DLT_onPathChanged (pathsToExpand)", this.pathsToExpand);
            node.expand();
         }
      },


      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR PAGE EVENTS
       * Disconnected event handlers for inter-component event notification
       */
      
      /**
       * Fired when a folder has been renamed
       * @method onFolderRenamed
       * @param layer {string} the event source
       * @param args {object} arguments object
       */
      onFolderRenamed: function DLT_onFolderRenamed(layer, args)
      {
         var obj = args[1];
         if (obj && (obj.file !== null))
         {
            var node = this.widgets.treeview.getNodeByProperty("nodeRef", obj.file.node.nodeRef);
            if (node !== null)
            {
               var index = -1;
               var replacePaths = function(node)
               {
                  // rebuild path based on existing node path data - otherwise repointed root node may not be taken into account
                  var paths = node.data.path.split("/");

                  if (index < 0)
                  {
                     paths.pop();
                     index = paths.length;
                     node.data.path = $combine(paths.join("/"), obj.file.location.file);
                  }
                  else
                  {
                     paths[index] = obj.file.displayName;
                     node.data.path = paths.join("/");
                  }

                  if (node.hasChildren)
                  {
                     for (var i = 0; i < node.children.length; i++)
                     {
                        replacePaths(node.children[i]);
                     }
                  }
               };

               // Node found, so rename it and replace the paths
               replacePaths(node);
               node.label = obj.file.displayName;

               this.widgets.treeview.render();
               this._showHighlight(true);
            }
         }
         
         // Make sure that the drag and drop targets are correctly set...
         this._applyDropTargets();
      },

      /**
       * Fired when a folder has been copied
       *
       * Event data contains:
       *    nodeRef - the nodeRef of the newly copied object
       *    destination - new parent path
       * @method onFolderCopied
       * @param layer {string} the event source
       * @param args {object} arguments object
       */
      onFolderCopied: function DLT_onFolderCopied(layer, args)
      {
         var obj = args[1];
         if (obj !== null)
         {
            if (obj.nodeRef && obj.destination)
            {
               // Do we have the parent of the node's copy loaded?
               var nodeDest = this.widgets.treeview.getNodeByProperty("path", $combine("/", obj.destination));
               if (nodeDest)
               {
                  if (nodeDest.expanded)
                  {
                     this._sortNodeChildren(nodeDest);
                  }
                  else
                  {
                     nodeDest.isLeaf = false;
                  }
               }
               
               this.widgets.treeview.render();
               this._showHighlight(true);
            }
         }
         
         // Make sure that the drag and drop targets are correctly set...
         this._applyDropTargets();
      },

      /**
       * Fired when a folder has been created
       * @method onFolderCreated
       * @param layer {string} the event source
       * @param args {object} arguments object
       */
      onFolderCreated: function DLT_onFolderCreated(layer, args)
      {
         var obj = args[1];
         if (obj && (obj.parentNodeRef !== null))
         {
            var parentNode = this.widgets.treeview.getNodeByProperty("nodeRef", obj.parentNodeRef);
            if (parentNode !== null)
            {
               if (!parentNode.hasChildren() && parentNode.isLeaf === true)
               {
                  // load children dynamically on render (ACE-2341, MNT-11763)
                  parentNode.dynamicLoadComplete = false;
               }
               this._sortNodeChildren(parentNode);
            }
         }
      },

      /**
       * Fired when a folder has been deleted
       * @method onFolderDeleted
       * @param layer {string} the event source
       * @param args {object} arguments object
       */
      onFolderDeleted: function DLT_onFolderDeleted(layer, args)
      {
         var obj = args[1];
         if (obj !== null)
         {
            var node = null;
            
            if (obj.path)
            {
               // ensure path starts with leading slash
               node = this.widgets.treeview.getNodeByProperty("path", $combine("/", obj.path));
            }
            else if (obj.nodeRef)
            {
               node = this.widgets.treeview.getNodeByProperty("nodeRef", obj.nodeRef);
            }
            
            if (node !== null)
            {
               var parentNode = node.parent;
               // Node found, so delete it
               this.widgets.treeview.removeNode(node);
               // Have all the parent child nodes been removed now?
               if (parentNode !== null)
               {
                  if (!parentNode.hasChildren())
                  {
                     parentNode.isLeaf = true;
                  }
               }
               this.widgets.treeview.render();
               this._showHighlight(true);
            }
         }
         
         // Make sure that the drag and drop targets are correctly set...
         this._applyDropTargets();
      },

      /**
       * Fired when a folder has been moved
       *
       * Event data contains:
       *    nodeRef - the nodeRef of the moved object
       *    destination - new parent path
       * @method onFolderMoved
       * @param layer {string} the event source
       * @param args {object} arguments object
       */
      onFolderMoved: function DLT_onFolderMoved(layer, args)
      {
         var obj = args[1];
         if (obj !== null)
         {
            if (typeof obj.nodeRef !== "undefined" && typeof obj.destination !== "undefined")
            {
               var nodeSrc = null;
               
               // we should be able to find the original node
               nodeSrc = this.widgets.treeview.getNodeByProperty("nodeRef", obj.nodeRef);
            
               if (nodeSrc !== null)
               {
                  var parentNode = nodeSrc.parent;
                  // Node found, so delete it
                  this.widgets.treeview.removeNode(nodeSrc, true);
                  // Have all the parent's child nodes been removed now?
                  if (parentNode !== null)
                  {
                     if (!parentNode.hasChildren())
                     {
                        parentNode.isLeaf = true;
                     }
                  }
                  // Do we have the node's new parent loaded?
                  var nodeDest = this.widgets.treeview.getNodeByProperty("path", $combine("/", obj.destination));
                  if (nodeDest)
                  {
                     // The node may already be loading if this was a multiple-folder move
                     if (!nodeDest.isLoading)
                     {
                        if (nodeDest.isLeaf)
                        {
                           nodeDest.isLeaf = false;
                        }
                        else if (nodeDest.expanded)
                        {
                           this._sortNodeChildren(nodeDest);
                        }
                        this.widgets.treeview.render();
                        this._showHighlight(true);
                     }
                  }
               }
            }
         }
         
         // Make sure that the drag and drop targets are correctly set...
         this._applyDropTargets();
      },

      /**
       * Fired when the currently active filter has changed
       * @method onFilterChanged
       * @param layer {string} the event source
       * @param args {object} arguments object
       */
      onFilterChanged: function DLT_onFilterChanged(layer, args)
      {
         var obj = args[1];
         if ((obj !== null) && (obj.filterId !== null))
         {
            obj.filterOwner = obj.filterOwner || Alfresco.util.FilterManager.getOwner(obj.filterId);
            
            // Defer if event received before we're ready
            if (!this.isReady)
            {
               this.initialFilter = Alfresco.util.cleanBubblingObject(obj);
               Alfresco.logger.debug("DLT_onFilterChanged (deferring)", this.initialFilter);
               return;
            }
            
            Alfresco.logger.debug("DLT_onFilterChanged", obj);
            this.initialFilter = null;
            
            this.currentFilter = Alfresco.util.cleanBubblingObject(obj);
            this.isFilterOwner = (obj.filterOwner == this.name);
            if (this.isFilterOwner)
            {
               this.pathChanged(this.currentFilter.filterData, obj);
            }
            this._showHighlight(this.isFilterOwner);
         }
      },


      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Creates the TreeView control and renders it to the parent element.
       * @method _buildTree
       * @private
       */
      _buildTree: function DLT__buildTree()
      {
         // Create a new tree
         var tree = new YAHOO.widget.TreeView(this.id + "-treeview");
         this.widgets.treeview = tree;
         
         // Having both focus and highlight are just confusing (YUI 2.7.0 addition)
         YAHOO.widget.TreeView.FOCUS_CLASS_NAME = "";

         // Turn dynamic loading on for entire tree
         tree.setDynamicLoad(this.fnLoadNodeData);

         // Get root node for tree
         var root = tree.getRoot();

         // Add default top-level node
         this._buildTreeNode(
         {
            name: Alfresco.util.message("node.root", this.name),
            path: "/",
            nodeRef: ""
         }, root, false);

         // Register tree-level listeners
         tree.subscribe("clickEvent", this.onNodeClicked, this, true);
         tree.subscribe("expandComplete", this.onExpandComplete, this, true);

         // Render tree with this one top-level node
         tree.render();
      },
      
      /**
       * @method _sortNodeChildren
       * @param node {object} Parent node
       * @param onSortComplete {object} Optional callback object literal
       * @private
       */
      _sortNodeChildren: function DLT__sortNodeChildren(node, onSortComplete)
      {
         // Is the node a leaf?
         if (node.isLeaf)
         {
            // Yes, so clearing the leaf flag and redrawing will automatically query the child nodes
            node.isLeaf = false;
            this.widgets.treeview.render();
            this._showHighlight(true);
            return;
         }
         
         // Get the path this node refers to
         var nodePath = node.data.path;

         // Prepare URI for XHR data request
         var uri = this._buildTreeNodeUrl(nodePath);

         // Prepare the XHR callback object
         var callback =
         {
            success: function DLT_sNC_success(oResponse)
            {
               var results = YAHOO.lang.JSON.parse(oResponse.responseText);

               if (results.items)
               {
                  var kids = oResponse.argument.node.children;
                  var items = results.items;
                  for (var i = 0, j = items.length; i < j; i++)
                  {
                     if ((kids.length <= i) || (kids[i].data.nodeRef != items[i].nodeRef))
                     {
                        // Node has moved - search for correct node for this position and swap if found
                        var kidFound = false;
                        for (var m = i, n = kids.length; m < n; m++)
                        {
                           if (kids[m].data.nodeRef == items[i].nodeRef)
                           {
                              var temp = kids[i];
                              kids[i] = kids[m];
                              kids[m] = temp;
                              kidFound = true;
                              break;
                           }
                        }
                           
                        // If we get here we couldn't find the node, so create one and insert it
                        if (!kidFound)
                        {
                           var item = items[i];
                           item.path = $combine(oResponse.argument.node.data.path, item.name);
                           var tempNode = this._buildTreeNode(item);

                           if (!item.hasChildren)
                           {
                              tempNode.isLeaf = true;
                           }
                           
                           if (kids.length === 0)
                           {
                              var parentNode = oResponse.argument.node;
                              parentNode.isLeaf = false;
                              tempNode.appendTo(parentNode);
                           }
                           else if (kids.length > i)
                           {
                              tempNode.insertBefore(kids[i]);
                           }
                           else
                           {
                              tempNode.insertAfter(kids[kids.length - 1]);
                           }
                        }
                     }
                  }
                  
                  // Update the tree
                  this.widgets.treeview.render();
                  this._showHighlight(true);
                  
                  // Execute the onSortComplete callback
                  var callback = oResponse.argument.onSortComplete;
                  if (callback && typeof callback.fn == "function")
                  {
                     callback.fn.call(callback.scope ? callback.scope : this, callback.obj);
                  }
                  
                  // Make sure that the drag and drop targets are correctly set...
                  this._applyDropTargets();
               }
            },

            // If the XHR call is not successful, no further processing - tree may not be sorted correctly
            failure: function DLT_sNC_failure(oResponse)
            {
               Alfresco.logger.error("DLT_sNC_failure", oResponse);
            },

            // XHR response argument information
            argument:
            {
               node: node,
               onSortComplete: onSortComplete
            },
            
            scope: this,

            // Timeout -- abort the transaction after 7 seconds
            timeout: 7000
         };

         // Make the XHR call using Connection Manager's asyncRequest method
         YAHOO.util.Connect.asyncRequest('GET', uri, callback);
      },

      /**
       * Highlights the currently selected node.
       * @method _showHighlight
       * @param isVisible {boolean} Whether the highlight is visible or not
       * @private
       */
      _showHighlight: function DLT__showHighlight(isVisible)
      {
         if (this.selectedNode !== null)
         {
            if (isVisible)
            {
               Dom.addClass(this.selectedNode.getEl(), "selected");
            }
            else
            {
               Dom.removeClass(this.selectedNode.getEl(), "selected");
            }
         }
      },
      
      /**
       * Updates the currently selected node.
       * @method _updateSelectedNode
       * @param node {object} New node to set as currently selected one
       * @private
       */
      _updateSelectedNode: function DLT__updateSelectedNode(node)
      {
         if (this.isFilterOwner)
         {
            this._showHighlight(false);
            this.selectedNode = node;
            this._showHighlight(true);
         }
         else
         {
            this.selectedNode = node;
         }
      },

      /**
       * Build a tree node using passed-in data
       *
       * @method _buildTreeNode
       * @param p_oData {object} Object literal containing required data for new node
       * @param p_oParent {object} Optional parent node
       * @param p_expanded {object} Optional expanded/collaped state flag
       * @return {YAHOO.widget.TextNode} The new tree node
       */
      _buildTreeNode : function DLT__buildTreeNode(p_oData, p_oParent, p_expanded)
      {
         var treeNode = new YAHOO.widget.TextNode(
         {
            label : p_oData.name,
            path : p_oData.path,
            nodeRef : p_oData.nodeRef,
            description : p_oData.description
         }, p_oParent, p_expanded);
         var customStyleClass = this._buildCustomStyleClass(p_oData);
         treeNode.customCls = customStyleClass;
         return treeNode;
      },
      /**
       * Gets resource style specified in the {style} configuration that corresponds with matching filter 
       * from share-documentlibrary-config.xml [CommonComponentStyle][component-style], {browse.folder} component, or null if the filter does not match.
       * 
       * The returned value is used to be set to the treeNode as customCls attribute, used for rendering custom icons in treeView. 
       * @param p_oData
       */
      _buildCustomStyleClass : function DLT__buildCustomStyleClass(p_oData)
      {
         var customStyleClass = null;
         if (this.options.customFolderStyleConfig)
         {
            var filterChain = new Alfresco.CommonComponentStyleFilterChain(p_oData,
                  this.options.customFolderStyleConfig.browse.folder);
            customStyleClass = filterChain.createCustomStyle();
         }
         return customStyleClass;
      },

      /**
       * Build URI parameter string for treenode JSON data webscript
       *
       * @method _buildTreeNodeUrl
       * @param path {string} Path to query
       */
       _buildTreeNodeUrl: function DLT__buildTreeNodeUrl(path)
       {
          var uriTemplate ="slingshot/doclib/treenode/site/" + $combine(encodeURIComponent(this.options.siteId), encodeURIComponent(this.options.containerId), Alfresco.util.encodeURIPath(path));
          uriTemplate += "?perms=false&children=" + this.options.evaluateChildFolders + "&max=" + this.options.maximumFolderCount;
          return  Alfresco.constants.PROXY_URI + uriTemplate;
       }
   });
})();
