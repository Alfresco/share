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
 * CategoryManager tool component.
 * 
 * @namespace Alfresco
 * @class Alfresco.CategoryManager
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event,
       Element = YAHOO.util.Element,
       KeyListener = YAHOO.util.KeyListener;
    
    /**
     * Alfresco Slingshot aliases
     */
    var $html = Alfresco.util.encodeHTML,
       $combine = Alfresco.util.combinePaths;

   /**
    * CategoryManager constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.CategoryManager} The new CategoryManager instance
    * @constructor
    */
   Alfresco.CategoryManager = function(htmlId)
   {
      this.name = "Alfresco.CategoryManager";
      Alfresco.CategoryManager.superclass.constructor.call(this, htmlId);
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["treeview", "json"], this.onComponentsLoaded, this);
      
      /* Define panel handlers */
      var parent = this;
      
      /* CategoryPanelHandler Panel Handler */
      CategoryPanelHandler = function CategoryPanelHandler()
      {
         CategoryPanelHandler.superclass.constructor.call(this, "category");
      };
      
      YAHOO.extend(CategoryPanelHandler, Alfresco.ConsolePanelHandler, {});
      new CategoryPanelHandler();
      
      return this;
   };
   
   YAHOO.extend(Alfresco.CategoryManager, Alfresco.ConsoleTool,
   {
      /**
       * Holds IDs to register insitu editors with.
       *
       * @property insituEditors
       * @type array
       */
      insituEditors: null,

      /**
       * Fired by YUI when parent element is available for scripting.
       *
       * @method onReady
       */
      onReady: function ConsoleCategoryManager_onReady()
      {
         // Reference to self - used in inline functions
         var me = this;
         
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
                        docNode.label = response.message;
                        docNode.labelStyle = "ygtverror";
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
         // Register insitu editors
         var iEd;
         for (i = 0, j = this.insituEditors.length; i < j; i++)
         {
            iEd = this.insituEditors[i];
            Alfresco.util.createInsituEditor(iEd.context, iEd.params, iEd.callback);
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
         Event.stopEvent(args.event);
         // Prevent the tree node from expanding (TODO: user preference?)
         return false;
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
         this.insituEditors = []; 
         // Create a new tree
         var tree = new YAHOO.widget.TreeView(this.id + "-category-manager");
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
         }, root, true);

         // Register tree-level listeners
         tree.subscribe("clickEvent", this.onNodeClicked, this, true);
         tree.subscribe("expandComplete", this.onExpandComplete, this, true);
         tree.subscribe('dblClickEvent', tree.onEventEditNode);

         tree._onKeyDownEvent = function DLT__onKeyDownEvent()
         {
            // Disable the key down events for the tree so that the cursor behaves as it should when editing the node text
         }

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
                  
                  this.onExpandComplete(node);
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
       * Build a tree node using passed-in data
       *
       * @method _buildTreeNode
       * @param p_oData {object} Object literal containing required data for new node
       * @param p_oParent {object} Optional parent node
       * @param p_expanded {object} Optional expanded/collaped state flag
       * @return {YAHOO.widget.TextNode} The new tree node
       */
      _buildTreeNode: function DLT__buildTreeNode(p_oData, p_oParent, p_expanded)
      {
         var textNode = new YAHOO.widget.TextNode(
         {
            label: p_oData.name,
            path: p_oData.path,
            nodeRef: p_oData.nodeRef,
            description: p_oData.description,
            editable : false
         }, p_oParent, p_expanded);
         
         var scope = this;
         var filenameId = textNode.labelElId;
         var fnInsituCallback = function insitu_callback(response, asset)
         {
            asset.data.path = asset.data.path.replace(asset.label, response.json.name);
            asset.setUpLabel(response.json.name);
            asset.parent.toggle();
            asset.refresh();
            asset.parent.toggle();
            return true;
         };
         if (p_oData.nodeRef !== null)
         {
            // Insitu editors
            scope.insituEditors.push(
            {
               context: filenameId,
               params:
               {
                  treeNode: textNode,
                  component: this,
                  type: "textBoxCategory",
                  nodeRef: p_oData.nodeRef,
                  name: "name",
                  value: p_oData.name,
                  validations: [
                  {
                     type: Alfresco.forms.validation.nodeName,
                     when: "keyup",
                     message: scope.msg("validation-hint.nodeName")
                  },
                  {
                     type: Alfresco.forms.validation.length,
                     args: { min: 1, max: 255, crop: true },
                     when: "keyup",
                     message: scope.msg("validation-hint.length.min.max", 1, 255)
                  }],
                  title: scope.msg("tool.category-manager.edit-category"),
                  errorMessage: scope.msg("tool.category-manager.edit-category.failure"),
                  titleAdd: scope.msg("tool.category-manager.add-category"),
                  errorAddMessage: scope.msg("tool.category-manager.add-category.failure"),
                  titleDelete: scope.msg("tool.category-manager.delete-category"),
                  errorDeleteMessage: scope.msg("tool.category-manager.delete-category.failure")
               },
               callback:
               {
                  fn: fnInsituCallback,
                  scope: scope,
                  obj: textNode
               }
            });
         }
         
         return textNode;
      },
      
      /**
       * Build URI parameter string for treenode JSON data webscript
       *
       * @method _buildTreeNodeUrl
       * @param path {string} Path to query
       */
      _buildTreeNodeUrl: function DLT__buildTreeNodeUrl(path)
      {
         var nodeRef = new Alfresco.util.NodeRef(this.options.nodeRef),
         uriTemplate ="slingshot/doclib/categorynode/node/" + $combine(encodeURI(nodeRef.uri), Alfresco.util.encodeURIPath(path));
         
         return  Alfresco.constants.PROXY_URI + uriTemplate + "?perms=false&children=true";
      }
   });


   /**
    * Alfresco.widget.InsituEditor.textBox constructor.
    *
    * @param p_params {Object} Instance configuration parameters
    * @return {Alfresco.widget.InsituEditor.textBox} The new textBox editor instance
    * @constructor
    */
   Alfresco.widget.InsituEditor.textBoxCategory = function(p_params)
   {
      this.params = YAHOO.lang.merge({}, p_params);
      
      var nodeRef = new Alfresco.util.NodeRef(this.params.nodeRef),
         elEditForm = new Element(document.createElement("form"),
         {
            id: Alfresco.util.generateDomId(),
            method: "post",
            action: Alfresco.constants.PROXY_URI + "api/category/" + nodeRef.uri + "?alf_method=PUT"
         });
      
      this.elEditForm = elEditForm;
      this.editForm = elEditForm.get("element");
      
      // Form definition
      this.form = new Alfresco.forms.Form(this.editForm);
      this.form.setAJAXSubmit(true,
      {
         successCallback:
         {
            fn: function(response)
            {
               this.onPersistSuccess.apply(this, arguments);
               if (response.json.message)
               {
                  Alfresco.util.PopupManager.displayMessage({ text: response.json.message });
               }
            },
            scope: this
         },
         failureCallback:
         {
            fn: this.onPersistFailure,
            scope: this
         }
      });
      this.form.setSubmitAsJSON(true);
      
      elEditForm.addClass("insitu-edit");
      elEditForm.on("submit", function(e)
      {
         Event.stopEvent(e);
      });
      
      // Create Add icon instance
      this.addIcon = new Alfresco.widget.InsituEditorIconAdd(this, p_params);
      
      // Create Edit and Delete icon for all except category root
      if (this.params.nodeRef !== "")
      {
         this.editorIcon = new Alfresco.widget.InsituEditorIconEdit(this, p_params);
         this.deleteIcon = new Alfresco.widget.InsituEditorIconDelete(this, p_params);
      }

      this.params.context.parentNode.appendChild(this.editForm, this.params.context);

      this.balloon = null;
      this.contextStyle = null;
      this.keyListener = null;
      this.enterKeyListener = null;
      this.markupGenerated = false;
   
      return this;
   };

   /**
    * Alfresco.widget.InsituEditor.textBox
    */
   YAHOO.extend(Alfresco.widget.InsituEditor.textBoxCategory, Alfresco.widget.InsituEditor.textBox,
   {
      /**
       * Hide the editor
       *
       * @method doHide
       * @param restoreUI {boolean} Whether to restore the UI or rely on the caller to do it
       * @override
       */
      doHide: function InsituEditor_textBox_doHide(restoreUI)
      {
         Alfresco.widget.InsituEditor.textBoxCategory.superclass.doHide.call(this, restoreUI);
         this.enterKeyListener.disable();
      },
      
      /**
       * Show the editor
       *
       * @method doShow
       * @override
       */
      doShow: function InsituEditor_textBox_doShow()
      {
         Alfresco.widget.InsituEditor.textBoxCategory.superclass.doShow.call(this, null);
         this.enterKeyListener.enable();
      },
      
      /**
       * Generate mark-up
       *
       * @method _generateMarkup
       * @protected
       */
      _generateMarkup: function InsituEditor_textBox__generateMarkup()
      {
         if (this.markupGenerated)
         {
            return;
         }

         // Generate input box markup
         var eInput = new Element(document.createElement("input"),
            {
               type: "text",
               name: this.params.name,
               value: this.params.treeNode.label
            }),
            eSave = new Element(document.createElement("a"),
            {
               href: "#",
               innerHTML: Alfresco.util.message("button.save")
            }),
            eCancel = new Element(document.createElement("a"),
            {
               href: "#",
               innerHTML: Alfresco.util.message("button.cancel")
            });

         this.elEditForm.appendChild(eInput);
         this.elEditForm.appendChild(eSave);
         this.elEditForm.appendChild(eCancel);

         eInput.on("blur", function(e)
         {
            if (this.balloon)
            {
               this.balloon.hide();
            }
         }, this, true);

         eSave.on("click", function(e)
         {
            this.form._submitInvoked(e);
         }, this, true);

         eCancel.on("click", function(e)
         {
            Event.stopEvent(e);
            this.inputBox.value = this.params.treeNode.label;
            this.doHide(true);
         }, this, true);

         this.inputBox = eInput.get("element");

         // Key Listener for [Escape] to cancel
         this.keyListener = new KeyListener(this.inputBox,
         {
            keys: [KeyListener.KEY.ESCAPE]
         },
         {
            fn: function(id, keyEvent)
            {
               Event.stopEvent(keyEvent[1]);
               this.inputBox.value = this.params.treeNode.label;
               this.doHide(true);
            },
            scope: this,
            correctScope: true
         });
         
         // Key Listener for [ENTER] to accept
         this.enterKeyListener = new KeyListener(this.inputBox,
         {
            keys: [KeyListener.KEY.ENTER]
         },
         {
            fn: function(id, keyEvent)
            {
               Event.stopEvent(keyEvent[1]);
               this.form._submitInvoked(keyEvent);
               this.doHide(true);
            },
            scope: this,
            correctScope: true
         });

         // Balloon UI for errors
         this.balloon = Alfresco.util.createBalloon(this.inputBox);
         this.balloon.onClose.subscribe(function(e)
         {
            try
            {
               this.inputBox.focus();
            }
            catch (e)
            {
            }
         }, this, true);

         // Register validation handlers
         var vals = this.params.validations;
         for (var i = 0, ii = vals.length; i < ii; i++)
         {
            this.form.addValidation(this.inputBox, vals[i].type, vals[i].args, vals[i].when, vals[i].message);
         }

         // Override Forms Runtime's error handling
         var scope = this;
         this.form.addError = function InsituEditor_textBox_addError(msg, field)
         {
            scope.balloon.html(msg);
            scope.balloon.show();
         };

         // Initialise the form
         this.form.init();

         this.markupGenerated = true;
      }
   });

   Alfresco.widget.InsituEditorIconEdit = function(p_editor, p_params)
   {
      this.editor = p_editor;
      this.params = YAHOO.lang.merge({}, p_params);
      this.disabled = p_params.disabled;
      
      this.editIcon = document.createElement("span");
      this.editIcon.title = Alfresco.util.encodeHTML(p_params.title);
      Dom.addClass(this.editIcon, "insitu-edit-category");
      
      this.params.context.appendChild(this.editIcon, this.params.context);
      Event.on(this.params.context, "mouseover", this.onContextMouseOver, this);
      Event.on(this.params.context, "mouseout", this.onContextMouseOut, this);
      Event.on(this.editIcon, "mouseover", this.onContextMouseOver, this);
      Event.on(this.editIcon, "mouseout", this.onContextMouseOut, this);
   };
   
   YAHOO.extend(Alfresco.widget.InsituEditorIconEdit, Alfresco.widget.InsituEditorIcon, {});
   
   Alfresco.widget.InsituEditorIconAdd = function(p_editor, p_params)
   {
      this.editor = p_editor;
      this.params = YAHOO.lang.merge({}, p_params);
      this.disabled = p_params.disabled;
      
      this.editIcon = document.createElement("span");
      this.editIcon.title = Alfresco.util.encodeHTML(p_params.titleAdd);
      if (p_editor.params.nodeRef === "")
      {
         Dom.addClass(this.editIcon, "insitu-add-root-category");  
      }
      else
      {
         Dom.addClass(this.editIcon, "insitu-add-category");
      }
      
      this.params.context.appendChild(this.editIcon, this.params.context);
      Event.on(this.params.context, "mouseover", this.onContextMouseOver, this);
      Event.on(this.params.context, "mouseout", this.onContextMouseOut, this);
      Event.on(this.editIcon, "mouseover", this.onContextMouseOver, this);
      Event.on(this.editIcon, "mouseout", this.onContextMouseOut, this);
   };

   YAHOO.extend(Alfresco.widget.InsituEditorIconAdd, Alfresco.widget.InsituEditorIconEdit,
   {
      /**
       * The default event handler fired when the user clicks the icon element.
       *
       * @method onIconClick
       * @param {DOMEvent} e The current DOM event
       * @param {Object} obj The object argument
       */
      onIconClick: function InsituEditorIcon_onIconClick(e, obj)
      {
         if (obj.disabled)
         {
            return;
         }
         Event.stopEvent(e);

         if (Alfresco.logger.isDebugEnabled())
         {
            Alfresco.logger.debug("onIconClick", e);
         }
         
         Alfresco.util.PopupManager.getUserInput(
         {
            title: Alfresco.util.message("tool.category-manager.add-category"),
            text: Alfresco.util.message("tool.category-manager.label.category-name"),
            input: "text",
            callback:
            {
               fn: function promptCallback (newNodeName, obj)
               {
                  url = this._buildAddNodeUrl(this.params.nodeRef);
                  
                  var config =
                  {
                     method: "POST",
                     url: url,
                     successCallback:
                     {
                        fn: function (response, obj)
                        {
                           var treeNode = this.params.treeNode;
                           // Only sort children if it has any already loaded
                           if(treeNode.hasChildren())
                           {
                              this.params.component._sortNodeChildren(treeNode);
                           }
                           treeNode.toggle();
                           treeNode.refresh();
                           treeNode.toggle();

                           if (response.json.message)
                           {
                              Alfresco.util.PopupManager.displayMessage({ text: response.json.message });
                           }
                        },
                        scope: this
                     },
                     failureCallback:
                     {
                        fn: function (response, obj)
                        {
                           Alfresco.util.PopupManager.displayMessage(
                           {
                              text: Alfresco.util.message("tool.category-manager.add-category.failure")
                           });
                        },
                        scope: this
                     },
                     dataObj:
                     {
                        name : newNodeName
                     }
                  };
                  
                  Alfresco.util.Ajax.jsonRequest(config);
               },
               obj: {},
               scope: obj
            }
         });

         var elements = Dom.getElementsByClassName('yui-button', 'span', 'userInput');
         Dom.addClass(elements[0], 'alf-primary-button');
      },
      
      /**
       * Build URI parameter string for treenode JSON data webscript
       *
       * @method _buildTreeNodeUrl
       * @param path {string} Path to query
       */
       _buildAddNodeUrl: function _buildAddNodeUrl(nodeRef)
       {
          var nodeRef = new Alfresco.util.NodeRef(nodeRef),
            uriTemplate = "api/category/" + encodeURI(nodeRef.uri);

          return  Alfresco.constants.PROXY_URI + uriTemplate;
       }
   });

   Alfresco.widget.InsituEditorIconDelete = function(p_editor, p_params)
   {
      this.editor = p_editor;
      this.params = YAHOO.lang.merge({}, p_params);
      this.disabled = p_params.disabled;
      
      this.editIcon = document.createElement("span");
      this.editIcon.title = Alfresco.util.encodeHTML(p_params.titleDelete);
      Dom.addClass(this.editIcon, "insitu-delete-category");
      
      this.params.context.appendChild(this.editIcon, this.params.context);
      Event.on(this.params.context, "mouseover", this.onContextMouseOver, this);
      Event.on(this.params.context, "mouseout", this.onContextMouseOut, this);
      Event.on(this.editIcon, "mouseover", this.onContextMouseOver, this);
      Event.on(this.editIcon, "mouseout", this.onContextMouseOut, this);
   };
   
   YAHOO.extend(Alfresco.widget.InsituEditorIconDelete, Alfresco.widget.InsituEditorIconEdit,
   {
      /**
       * The default event handler fired when the user clicks the icon element.
       *
       * @method onIconClick
       * @param {DOMEvent} e The current DOM event
       * @param {Object} obj The object argument
       */
      onIconClick: function InsituEditorIcon_onIconClick(e, obj)
      {
         if (obj.disabled)
         {
            return;
         }
         Event.stopEvent(e);

         if (Alfresco.logger.isDebugEnabled())
         {
            Alfresco.logger.debug("onIconClick", e);
         }
         
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: Alfresco.util.message("tool.category-manager.message.confirm.delete.title"),
            text: Alfresco.util.message("tool.category-manager.message.confirm.delete", this, obj.params.treeNode.label),
            buttons: [
            {
               text: Alfresco.util.message("button.delete"),
               handler: function dlA_onActionDelete_delete()
               {
                  this.destroy();
                  
                  // perform the Delete REST operation
                  url = obj._buildDeleteNodeUrl(obj.params.nodeRef);
                  
                  var config =
                  {
                     method: "DELETE",
                     url: url,
                     successCallback:
                     {
                        fn: function (response, o)
                        {
                           var treeNode = obj.params.treeNode;
                           var tree = treeNode.tree;
                           var parent = treeNode.parent; 
                           tree.removeNode(treeNode);
                           parent.toggle();
                           parent.toggle();

                           if (response.json.message)
                           {
                              Alfresco.util.PopupManager.displayMessage({ text: response.json.message });
                           }
                        },
                        scope: obj
                     },
                     failureCallback:
                     {
                        fn: function (response, o)
                        {
                           Alfresco.util.PopupManager.displayMessage(
                           {
                              text: Alfresco.util.message("tool.category-manager.delete-category.failure")
                           });
                        },
                        scope: obj
                     }
                  };
                  Alfresco.util.Ajax.jsonRequest(config);
               }
            },
            {
               text: Alfresco.util.message("button.cancel"),
               handler: function dlA_onActionDelete_cancel()
               {
                  this.destroy();
               },
               isDefault: true
            }]
         });
      },
      
      /**
       * Build URI parameter string for treenode JSON data webscript
       *
       * @method _buildTreeNodeUrl
       * @param path {string} Path to query
       */
      _buildDeleteNodeUrl: function _buildDeleteNodeUrl(nodeRef)
      {
         var nodeRef = new Alfresco.util.NodeRef(nodeRef),
         uriTemplate ="api/category/" + encodeURI(nodeRef.uri);
         
         return  Alfresco.constants.PROXY_URI + uriTemplate;
      }
   });
})();