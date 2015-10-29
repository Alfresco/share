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
 * Document Library "Site Folder" picker module for Document Library.
 * 
 * @namespace Alfresco.module
 * @class Alfresco.module.DoclibSiteFolder
 */
(function()
{
   /**
   * YUI Library aliases
   */
   var Dom = YAHOO.util.Dom,
      KeyListener = YAHOO.util.KeyListener;

   /**
    * Alfresco Slingshot aliases
    */
    var $html = Alfresco.util.encodeHTML,
       $combine = Alfresco.util.combinePaths;

   Alfresco.module.DoclibSiteFolder = function(htmlId)
   {
      Alfresco.module.DoclibSiteFolder.superclass.constructor.call(this, "Alfresco.module.DoclibSiteFolder", htmlId, ["button", "container", "connection", "json", "treeview"]);

      // Initialise prototype properties
      this.pathsToExpand = [];

      return this;
   };
   
   YAHOO.extend(Alfresco.module.DoclibSiteFolder, Alfresco.component.Base,
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
          * Initial path to expand on module load
          *
          * @property path
          * @type string
          * @default ""
          */
         path: "",

         /**
          * Width for the dialog
          *
          * @property width
          * @type integer
          * @default 30em
          */
         width: "40em",
         
         /**
          * Files to action
          *
          * @property files
          * @type object
          * @default null
          */
         files: null,
         
         /**
          * Template URL
          *
          * @property templateUrl
          * @type string
          * @example "Alfresco.constants.URL_SERVICECONTEXT + modules/documentlibrary/move-to"
          * @default null,
          */
         templateUrl: null,

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
         maximumFolderCount: -1
      },
      
      /**
       * Container element for template in DOM.
       * 
       * @property containerDiv
       * @type DOMElement
       */
      containerDiv: null,

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
       * Main entry point
       * @method showDialog
       */
      showDialog: function DLSF_showDialog()
      {
         if (!this.containerDiv)
         {
            // Load the UI template from the server
            Alfresco.util.Ajax.request(
            {
               url: this.options.templateUrl,
               dataObj:
               {
                  htmlid: this.id
               },
               successCallback:
               {
                  fn: this.onTemplateLoaded,
                  scope: this
               },
               failureMessage: "Could not load template:" + this.options.templateUrl,
               execScripts: true
            });
         }
         else
         {
            // Show the dialog
            this._showDialog();
         }
      },

      /**
       * Event callback when dialog template has been loaded
       *
       * @method onTemplateLoaded
       * @param response {object} Server response from load template XHR request
       */
      onTemplateLoaded: function DLSF_onTemplateLoaded(response)
      {
         // Reference to self - used in inline functions
         var me = this;
         
         // Inject the template from the XHR request into a new DIV element
         this.containerDiv = document.createElement("div");
         this.containerDiv.setAttribute("style", "display:none");
         this.containerDiv.innerHTML = response.serverResponse.responseText;

         // The panel is created from the HTML returned in the XHR request, not the container
         var dialogDiv = Dom.getFirstChild(this.containerDiv);
         
         // Create and render the YUI dialog
         this.widgets.dialog = Alfresco.util.createYUIPanel(dialogDiv,
         {
            width: this.options.width
         });
         
         // OK and cancel buttons
         this.widgets.okButton = Alfresco.util.createYUIButton(this, "ok", this.onOK);
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel", this.onCancel);

         /**
          * Dynamically loads TreeView nodes.
          * This MUST be inline in order to have access to the parent class.
          * @method fnLoadNodeData
          * @param node {object} Parent node
          * @param fnLoadComplete {function} Expanding node's callback function
          */
         this.fnLoadNodeData = function DLSF_fnLoadNodeData(node, fnLoadComplete)
         {
            // Get the path this node refers to
            var nodePath = node.data.path;

            // Prepare URI for XHR data request
            var uri = me._buildTreeNodeUrl.call(me, nodePath);

            // Prepare the XHR callback object
            var callback =
            {
               success: function DLSF_lND_success(oResponse)
               {
                  var results = Alfresco.util.parseJSON(oResponse.responseText);

                  if (results.items)
                  {
                     var item, tempNode;
                     for (var i = 0, j = results.items.length; i < j; i++)
                     {
                        item = results.items[i];
                        tempNode = new YAHOO.widget.TextNode(
                        {
                           label: $html(item.name),
                           path: $combine(nodePath, item.name),
                           nodeRef: item.nodeRef,
                           description: item.description,
                           userAccess: item.userAccess,
                           style: item.userAccess.create ? "" : "no-permission"
                        }, node, false);

                        if (!item.hasChildren)
                        {
                           tempNode.isLeaf = true;
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
                  }
                  
                  /**
                  * Execute the node's loadComplete callback method which comes in via the argument
                  * in the response object
                  */
                  oResponse.argument.fnLoadComplete();
               },

               // If the XHR call is not successful, fire the TreeView callback anyway
               failure: function DLSF_lND_failure(oResponse)
               {
                  try
                  {
                     var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                     
                     // Show the error in place of the root node
                     var rootNode = this.widgets.treeview.getRoot();
                     var docNode = rootNode.children[0];
                     docNode.isLoading = false;
                     docNode.isLeaf = true;
                     docNode.label = response.message;
                     docNode.labelStyle = "ygtverror";
                     rootNode.refresh();
                  }
                  catch(e)
                  {
                  }
               },
               
               // Callback function scope
               scope: me,

               // XHR response argument information
               argument:
               {
                  "node": node,
                  "fnLoadComplete": fnLoadComplete
               },

               // Timeout -- abort the transaction after 7 seconds
               timeout: 7000
            };

            // Make the XHR call using Connection Manager's asyncRequest method
            YAHOO.util.Connect.asyncRequest("GET", uri, callback);
         };
         
         // Show the dialog
         this._showDialog();
      },

      /**
       * Internal show dialog function
       * @method _showDialog
       */
      _showDialog: function DLSF__showDialog()
      {
         // Must have list of files
         if (this.options.files === null)
         {
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.no-files")
            });
            return;
         }

         // Enable buttons
         this.widgets.okButton.set("disabled", false);
         this.widgets.cancelButton.set("disabled", false);

         // Dialog title
         var titleDiv = Dom.get(this.id + "-title");
         if (YAHOO.lang.isArray(this.options.files))
         {
            titleDiv.innerHTML = this.msg("title.multi", this.options.files.length);
         }
         else
         {
            titleDiv.innerHTML = this.msg("title.single", '<span class="light">' + $html(this.options.files.displayName) + '</span>');
         }

         // Build the TreeView widget
         this._buildTree();
         
         // Register the ESC key to close the dialog
         var escapeListener = new KeyListener(document,
         {
            keys: KeyListener.KEY.ESCAPE
         },
         {
            fn: function(id, keyEvent)
            {
               this.onCancel();
            },
            scope: this,
            correctScope: true
         });
         escapeListener.enable();

         // Show the dialog
         this.widgets.dialog.show();

         // Kick-off navigation to initial path
         if (this.options.path !== null)
         {
            this.onPathChanged(this.options.path);
         }
      },

      
      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * Dialog OK button event handler
       *
       * @method onOK
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onOK: function DLSF_onOK(e, p_obj)
      {
         this.widgets.dialog.hide();
      },

      /**
       * Dialog Cancel button event handler
       *
       * @method onCancel
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onCancel: function DLSF_onCancel(e, p_obj)
      {
         this.widgets.dialog.hide();
      },

      /**
       * Fired by YUI TreeView when a node has finished expanding
       * @method onExpandComplete
       * @param oNode {YAHOO.widget.Node} the node recently expanded
       */
      onExpandComplete: function DLSF_onExpandComplete(oNode)
      {
         Alfresco.logger.debug("DLSF_onExpandComplete");

         // Make sure the tree's DOM has been updated
         this.widgets.treeview.render();
         // Redrawing the tree will clear the highlight
         this._showHighlight(true);
         
         if (this.pathsToExpand && this.pathsToExpand.length > 0)
         {
            var node = this.widgets.treeview.getNodeByProperty("path", this.pathsToExpand.shift());
            if (node !== null)
            {
               var el = node.getContentEl(),
                  container = Dom.get(this.id + "-treeview");
               
               container.scrollTop = Dom.getY(el) - (container.scrollHeight / 3);

               if (node.data.path == this.currentPath)
               {
                  this._updateSelectedNode(node);
               }
               node.expand();
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
      onNodeClicked: function DLSF_onNodeClicked(args)
      {
         Alfresco.logger.debug("DLSF_onNodeClicked");

         var node = args.node,
            userAccess = node.data.userAccess;

         if ((userAccess && userAccess.create) || (node.data.nodeRef == ""))
         {
            this.onPathChanged(node.data.path);
            this._updateSelectedNode(node);
         }
         return false;
      },
      
      /**
       * Update tree when the path has changed
       * @method onPathChanged
       * @param path {string} new path
       */
      onPathChanged: function DLSF_onPathChanged(path)
      {
         Alfresco.logger.debug("DLSF_onPathChanged");

         // ensure path starts with leading slash
         path = $combine("/", path);
         this.currentPath = path;
         
         // Search the tree to see if this path's node is expanded
         var node = this.widgets.treeview.getNodeByProperty("path", path);
         if (node !== null)
         {
            // Node found
            this._updateSelectedNode(node);
            node.expand();
            while (node.parent !== null)
            {
               node = node.parent;
               node.expand();
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
         // Check for root path special case
         if (path === "/")
         {
            paths = [""];
         }
         this.pathsToExpand = [];
         
         for (var i = 0, j = paths.length; i < j; i++)
         {
            // Push the path onto the list of paths to be expanded
            expandPath = $combine(expandPath, paths[i]);
            this.pathsToExpand.push(expandPath);
         }
         
         // Kick off the expansion process by expanding the first unexpanded path
         do
         {
            node = this.widgets.treeview.getNodeByProperty("path", this.pathsToExpand.shift());
         } while (this.pathsToExpand.length > 0 && node.expanded);

         if (node !== null)
         {
            node.expand();
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
      _buildTree: function DLSF__buildTree()
      {
         Alfresco.logger.debug("DLSF__buildTree");

         // Create a new tree
         var tree = new YAHOO.widget.TreeView(this.id + "-treeview");
         this.widgets.treeview = tree;

         // Having both focus and highlight are just confusing (YUI 2.7.0 addition)
         YAHOO.widget.TreeView.FOCUS_CLASS_NAME = "";

         // Turn dynamic loading on for entire tree
         tree.setDynamicLoad(this.fnLoadNodeData);

         // Add default top-level node
         var tempNode = new YAHOO.widget.TextNode(
         {
            label: this.msg("node.root"),
            path: "/",
            nodeRef: ""
         }, tree.getRoot(), false);

         // Register tree-level listeners
         tree.subscribe("clickEvent", this.onNodeClicked, this, true);
         tree.subscribe("expandComplete", this.onExpandComplete, this, true);

         // Render tree with this one top-level node
         tree.render();
      },

      /**
       * Highlights the currently selected node.
       * @method _showHighlight
       * @param isVisible {boolean} Whether the highlight is visible or not
       * @private
       */
      _showHighlight: function DLSF__showHighlight(isVisible)
      {
         Alfresco.logger.debug("DLSF__showHighlight");

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
      _updateSelectedNode: function DLSF__updateSelectedNode(node)
      {
         Alfresco.logger.debug("DLSF__updateSelectedNode");

         this._showHighlight(false);
         this.selectedNode = node;
         this._showHighlight(true);
      },

      /**
       * Build URI parameter string for treenode JSON data webscript
       *
       * @method _buildTreeNodeUrl
       * @param path {string} Path to query
       */
       _buildTreeNodeUrl: function DLSF__buildTreeNodeUrl(path)
       {
          var uriTemplate = Alfresco.constants.PROXY_URI + "slingshot/doclib/treenode/site/{site}/{container}{path}";
          uriTemplate += "?children=" + this.options.evaluateChildFolders + "&max=" + this.options.maximumFolderCount;

          var url = YAHOO.lang.substitute(uriTemplate,
          {
             site: encodeURIComponent(this.options.siteId),
             container: encodeURIComponent(this.options.containerId),
             path: Alfresco.util.encodeURIPath(path)
          });

          return url;
       }
   });

   /* Dummy instance to load optional YUI components early */
   var dummyInstance = new Alfresco.module.DoclibSiteFolder("null");
})();
