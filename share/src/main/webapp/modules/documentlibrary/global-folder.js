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
 * Document Library "Global Folder" picker module for Document Library.
 *
 * @namespace Alfresco.module
 * @class Alfresco.module.DoclibGlobalFolder
 */
(function()
{
   /**
   * YUI Library aliases
   */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      KeyListener = YAHOO.util.KeyListener,
      Selector = YAHOO.util.Selector;

   /**
    * Alfresco Slingshot aliases
    */
    var $html = Alfresco.util.encodeHTML,
       $combine = Alfresco.util.combinePaths,
       $hasEventInterest = Alfresco.util.hasEventInterest;

   Alfresco.module.DoclibGlobalFolder = function(htmlId)
   {
      Alfresco.module.DoclibGlobalFolder.superclass.constructor.call(this, "Alfresco.module.DoclibGlobalFolder", htmlId, ["button", "container", "connection", "json", "treeview"]);

      // Initialise prototype properties
      this.containers = {};

      // Decoupled event listeners
      if (htmlId != "null")
      {
         this.eventGroup = htmlId;
         try
         {
            YAHOO.Bubbling.unsubscribe("siteChanged", null, this);
            YAHOO.Bubbling.unsubscribe("containerChanged", null, this);
         }
         catch(err){/*ignore, because error is thrown when event isn't registred*/};
         YAHOO.Bubbling.on("siteChanged", this.onSiteChanged, this);
         YAHOO.Bubbling.on("containerChanged", this.onContainerChanged, this);
      }

      return this;
   };

   /**
   * Alias to self
   */
   var DLGF = Alfresco.module.DoclibGlobalFolder;

   /**
   * View Mode Constants
   */
   YAHOO.lang.augmentObject(DLGF,
   {
      /**
       * "Site" view mode constant.
       *
       * @property VIEW_MODE_SITE
       * @type integer
       * @final
       * @default 0
       */
      VIEW_MODE_SITE: 0,

      /**
       * "Repository" view mode constant.
       *
       * @property VIEW_MODE_REPOSITORY
       * @type integer
       * @final
       * @default 1
       */
      VIEW_MODE_REPOSITORY: 1,

      /**
       * "My Files" view mode constant.
       *
       * @property VIEW_MODE_USERHOME
       * @type integer
       * @final
       * @default 2
       */
      VIEW_MODE_USERHOME: 2,

      /**
       * "Recent Sites" view mode constant.
       *
       * @property VIEW_MODE_RECENT_SITES
       * @type integer
       * @final
       * @default 3
       */
      VIEW_MODE_RECENT_SITES: 3,

      /**
       * "Favourite Sites" view mode constant.
       *
       * @property VIEW_MODE_FAVOURITE_SITES
       * @type integer
       * @final
       * @default 4
       */
      VIEW_MODE_FAVOURITE_SITES: 4,

      /**
       * "Shared" view mode constant.
       *
       * @property VIEW_MODE_SHARED
       * @type integer
       * @final
       * @default 5
       */
      VIEW_MODE_SHARED: 5
   });

   YAHOO.extend(Alfresco.module.DoclibGlobalFolder, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       */
      options:
      {
         /**
          * Current siteId for site view mode.
          *
          * @property siteId
          * @type string
          */
         siteId: "",

         /**
          * Current site's title for site view mode.
          *
          * @property siteTitle
          * @type string
          */
         siteTitle: "",

         /**
          * ContainerId representing root container in site view mode
          *
          * @property containerId
          * @type string
          * @default "documentLibrary"
          */
         containerId: "documentLibrary",

         /**
          * ContainerType representing root container in site view mode
          *
          * @property containerType
          * @type string
          * @default "cm:folder"
          */
         containerType: "cm:folder",

         /**
          * Root node representing root container in repository view mode
          *
          * @property rootNode
          * @type string
          * @default "alfresco://company/home"
          */
         rootNode: "alfresco://company/home",

         /**
          * Root node representing root container in repository view mode
          *
          * @property sharedRoot
          * @type string
          * @default "alfresco://company/shared"
          */
         sharedRoot: "alfresco://company/shared",

         /**
          * NodeRef representing root container in user home view mode
          *
          * @property userHome
          * @type string
          * @default "alfresco://user/home"
          */
         userHome: "alfresco://user/home",

         /**
          * Initial path to expand on module load
          *
          * @property path
          * @type string
          * @default ""
          */
         path: "",

         /**
          * Initial node to expand on module load.
          *
          * If given this module will make a call to repo and find the path for the node and figure
          * out if its inside a site or not. If inside a site the site view mode  will be used, otherwise
          * it will switch to repo mode.
          *
          * @property pathNodeRef
          * @type string
          * @default ""
          */
         pathNodeRef: null,

         /**
          * Width for the dialog
          *
          * @property width
          * @type integer
          * @default 40em
          */
         width: "60em",

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
          * @default Alfresco.constants.URL_SERVICECONTEXT + "modules/documentlibrary/global-folder"
          */
         templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/documentlibrary/global-folder",

         /**
          * Dialog view mode: site or repository
          *
          * @property viewMode
          * @type integer
          * @default Alfresco.modules.DoclibGlobalFolder.VIEW_MODE_SITE
          */
         viewMode: DLGF.VIEW_MODE_RECENT_SITES,

         /**
          * Default view mode
          *
          */
         defaultView: DLGF.VIEW_MODE_RECENT_SITES,

         /**
          * Allowed dialog view modes
          *
          * @property allowedViewModes
          * @type array
          * @default [VIEW_MODE_SITE, VIEW_MODE_REPOSITORY]
          */
         allowedViewModes:
         [
            DLGF.VIEW_MODE_SITE,
            DLGF.VIEW_MODE_RECENT_SITES,
            DLGF.VIEW_MODE_FAVOURITE_SITES,
            DLGF.VIEW_MODE_SHARED,
            DLGF.VIEW_MODE_REPOSITORY, // For Admins only
            DLGF.VIEW_MODE_USERHOME // My Files
         ],

         /**
          * Evaluate child folders flag (Site mode)
          *
          * @property evaluateChildFoldersSite
          * @type boolean
          * @default true
          */
         evaluateChildFoldersSite: true,

         /**
          * Maximum folder count configuration setting (Site mode)
          *
          * @property maximumFolderCountSite
          * @type int
          * @default -1
          */
         maximumFolderCountSite: -1,

         /**
          * Timeout for retrieving results from Repository
          *
          * @property webscriptTimeout
          * @type int
          * @default 7000
          */
         webscriptTimeout: 7000,

         /**
          * Evaluate child folders flag (Repo mode)
          *
          * @property evaluateChildFoldersRepo
          * @type boolean
          * @default true
          */
         evaluateChildFoldersRepo: true,

         /**
          * Maximum folder count configuration setting (Repo mode)
          *
          * @property maximumFolderCountRepo
          * @type int
          * @default -1
          */
         maximumFolderCountRepo: -1,


         /**
          * Config for sites with specific container types
          *
          * @property siteTreeContainerTypes
          * @type Object
          */
         siteTreeContainerTypes: {},

         /**
          * Sites API
          *
          * The URL to the API that returns site information
          *
          * @property sitesAPI
          * @type {String} Absolute URL
          */
         sitesAPI: Alfresco.constants.PROXY_URI + "api/people/" + encodeURIComponent(Alfresco.constants.USERNAME) + "/sites",

         /**
          * Sites API
          *
          * The URL to the API that returns site information
          *
          * @property recentSitesAPI
          * @type {String} Absolute URL
          */
         recentSitesAPI: Alfresco.constants.PROXY_URI + "api/people/" + encodeURIComponent(Alfresco.constants.USERNAME) + "/sites/recent",

         /**
          * Sites API
          *
          * The URL to the API that returns site information
          *
          * @property favouriteSitesAPI
          * @type {String} Absolute URL
          */
         favouriteSitesAPI: Alfresco.constants.PROXY_URI + "api/people/" + encodeURIComponent(Alfresco.constants.USERNAME) + "/sites/favourites",

         /**
          * Containers API
          *
          * The URL to the API that returns the container listing.
          *
          * @property containersAPI
          * @type {String} Absolute URL
          */
         containersAPI: Alfresco.constants.PROXY_URI + "slingshot/doclib/containers/",

         /**
          * The message that gets displayed if the template cannot be loaded.
          *
          *
          * @property templateFailMessage
          * @type {string} text of message to be displayed to the user in a dialogue
          */
         templateFailMessage: "Could not load 'global-folder' template",
         customFolderStyleConfig: null
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
       * Current list of containers.
       *
       * @property containers
       * @type {object}
       */
      containers: null,

      /**
       * Main entry point
       * @method showDialog
       */
      showDialog: function DLGF_showDialog()
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
               failureMessage: this.options.templateFailMessage,
               execScripts: true
            });
         }
         else
         {
            // Show the dialog
            this._beforeShowDialog();
         }
      },

      /**
       * Event callback when dialog template has been loaded
       *
       * @method onTemplateLoaded
       * @param response {object} Server response from load template XHR request
       */
      onTemplateLoaded: function DLGF_onTemplateLoaded(response)
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

         // OK button
         this.widgets.okButton = Alfresco.util.createYUIButton(this, "ok", this.onOK, {additionalClass: "alf-primary-button"});

         // Cancel button
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel", this.onCancel);

         // Mode buttons
         var modeButtons = new YAHOO.widget.ButtonGroup(this.id + "-modeGroup");
         modeButtons.on("checkedButtonChange", this.onViewModeChange, this.widgets.modeButtons, this);
         this.widgets.modeButtons = modeButtons;

         // Make user enter-key-strokes also trigger a change
         var buttons = this.widgets.modeButtons.getButtons(),
            fnEnterListener = function(e)
            {
               if (KeyListener.KEY.ENTER == e.keyCode)
               {
                  this.set("checked", true);
               }
            };

         for (var i = 0; i < buttons.length; i++)
         {
            buttons[i].addListener("keydown", fnEnterListener);
         }

         /**
          * Dynamically loads TreeView nodes.
          * This MUST be inline in order to have access to the parent class.
          * @method fnLoadNodeData
          * @param node {object} Parent node
          * @param fnLoadComplete {function} Expanding node's callback function
          */
         this.fnLoadNodeData = function DLGF_oR_fnLoadNodeData(node, fnLoadComplete)
         {
            // Get the path this node refers to
            var nodePath = node.data.path;

            // Prepare URI for XHR data request
            var uri = me._buildTreeNodeUrl.call(me, nodePath);

            // Prepare the XHR callback object
            var callback =
            {
               success: function DLGF_lND_success(oResponse)
               {
                  var results = Alfresco.util.parseJSON(oResponse.responseText);

                  if (results.parent)
                  {
                     if (node.data.nodeRef.indexOf("alfresco://") === 0)
                     {
                        node.data.nodeRef = results.parent.nodeRef;
                     }

                     if (typeof node.data.userAccess == "undefined")
                     {
                        node.data.userAccess = results.parent.userAccess;
                        node.setUpLabel(
                        {
                           label: node.label,
                           style: results.parent.userAccess.create ? "" : "no-permission"
                        });
                        if (results.parent.userAccess.create == false)
                        {
                           node.parent.refresh();
                           if (this.selectedNode == node)
                           {
                              this.widgets.okButton.set("disabled", true);
                           }
                        }
                     }
                  }

                  if (results.items)
                  {
                     var item, tempNode;
                     for (var i = 0, j = results.items.length; i < j; i++)
                     {
                        item = results.items[i];
                        var isSyncSetMemberNode = this.options.mode == 'sync' && Alfresco.util.arrayContains(item.aspects, "sync:syncSetMemberNode");
                        tempNode = new YAHOO.widget.TextNode(
                        {
                           label: item.name,
                           path: $combine(nodePath, item.name),
                           nodeRef: item.nodeRef,
                           description: item.description,
                           userAccess: isSyncSetMemberNode ? false : item.userAccess,
                           style: isSyncSetMemberNode ? "no-permission" : (item.userAccess.create ? "" : "no-permission")
                        }, node, false);
                        var customStyleClass = this._buildCustomStyleClass(item);
                        tempNode.customCls = customStyleClass;

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
               failure: function DLGF_lND_failure(oResponse)
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

               // Timeout -- abort the transaction after configurable period (default is 7 sec)
               timeout: me.options.webscriptTimeout
            };

            // Add a noCache parameter to the URL to ensure that XHR requests are always made to the
            // server when using IE. Otherwise IE7/8 will cache the response.
            if (YAHOO.env.ua.ie > 0)
            {
               uri += (uri.indexOf("?") == -1 ? "?" : "&") + "noCache=" + new Date().getTime();
            }

            // Make the XHR call using Connection Manager's asyncRequest method
            YAHOO.util.Connect.asyncRequest("GET", uri, callback);
         };

         // Show the dialog
         this._beforeShowDialog();
      },

      /**
       * Internal function called before show dialog function so additional information may be loaded
       * before _showDialog (which might be overriden) is called.
       *
       * @method _beforeShowDialog
       */
      _beforeShowDialog: function DLGF__beforeShowDialog()
      {
         if (this.options.pathNodeRef)
         {
            // If pathNodeRef is given the user of this component doesn't know what viewmode to display
            var url = Alfresco.constants.PROXY_URI + "slingshot/doclib/node/" + this.options.pathNodeRef.uri + "/location";
            if (this.options.rootNode)
            {
               // Repository mode
               url += "?libraryRoot=" + encodeURIComponent(this.options.rootNode.toString());
            }
            Alfresco.util.Ajax.jsonGet(
            {
               url: url,
               successCallback:
               {
                  fn: function(response)
                  {
                     if (response.json !== undefined)
                     {
                        var locations = response.json;
                        if (locations.site)
                        {
                           this.options.viewMode = DLGF.prototype.options.defaultView;
                           this.options.path = $combine(locations.site.path, locations.site.file);
                           this.options.siteId = locations.site.site;
                           this.options.siteTitle = locations.site.siteTitle;
                        }
                        else
                        {
                           this.options.viewMode = DLGF.VIEW_MODE_REPOSITORY;
                           this.options.path = $combine(locations.repo.path, locations.repo.file);
                           this.options.siteId = null;
                           this.options.siteTitle = null;
                        }
                        this._showDialog();
                     }
                  },
                  scope: this
               },
               failureMessage: this.msg("message.failure")
            });
         }
         else
         {
            this._showDialog();
         }
      },

      /**
       * Internal show dialog function
       * @method _showDialog
       */
      _showDialog: function DLGF__showDialog()
      {
         // Enable buttons
         this.widgets.okButton.set("disabled", false);
         this.widgets.cancelButton.set("disabled", false);

         // Dialog title
         var titleDiv = Dom.get(this.id + "-title");
         if (this.options.title)
         {
             titleDiv.innerHTML = this.options.title;
         }
         else
         {
            if (YAHOO.lang.isArray(this.options.files))
            {
               titleDiv.innerHTML = this.msg("title.multi", this.options.files.length);
            }
            else
            {
               titleDiv.innerHTML = this.msg("title.single", '<span class="light">' + $html(this.options.files.displayName) + '</span>');
            }
         }

         // Dialog view mode
         var allowedViewModes = Alfresco.util.arrayToObject(this.options.allowedViewModes);
         
         // Remove any views that should be hidden...
         for (var i = 0; i < Alfresco.constants.HIDDEN_PICKER_VIEW_MODES.length; i++)
         {
            delete allowedViewModes[DLGF[Alfresco.constants.HIDDEN_PICKER_VIEW_MODES[i]]];
         }
         
         var modeButtons = this.widgets.modeButtons.getButtons(),
             modeButton, viewMode;

         if (!(this.options.viewMode in allowedViewModes))
         {
            this.options.viewMode = this.options.allowedViewModes[0];
         }
         for (var i = 0, ii = modeButtons.length; i < ii; i++)
         {
            modeButton = modeButtons[i];
            viewMode = parseInt(modeButton.get("name"), 10);
            modeButton.set("disabled", !(viewMode in allowedViewModes));
            modeButton.setStyle("display", viewMode in allowedViewModes ? "block" : "none");
            if (viewMode == this.options.viewMode)
            {
               if (modeButton.get("checked"))
               {
                  // Will trigger the path expansion
                  this.setViewMode(viewMode);
               }
               else
               {
                  modeButton.set("checked", true);
               }
            }
         }

         // Register the ESC key to close the dialog
         if (!this.widgets.escapeListener)
         {
            this.widgets.escapeListener = new KeyListener(document,
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
         }

         // Add the dialog to the dom
         this.widgets.dialog.render(this.options.parentElement || document.body);

         // MNT-11084 Full screen/window view: Actions works incorrectly;
         if (this.options.zIndex !== undefined && this.options.zIndex > 0)
         {
            var index = this.options.zIndex + 2;
            var dialog = this.widgets.dialog;
            var onBeforeShow = function () 
            {
               elements = Dom.getElementsByClassName("mask");
               //there can be more "mask"s on a page; make sure all of them have lower zIndexes
               for (i = 0, j = elements.length; i < j; i++)
               {
                  Dom.setStyle(elements[i], "zIndex", index - 1);
               }

               Dom.setStyle(dialog.element, "zIndex", index);
               dialog.cfg.setProperty("zIndex", index, true);
            }
            this.widgets.dialog.beforeShowEvent.subscribe(onBeforeShow, this.widgets.dialog, true);
         }

         // Show the dialog
         this.widgets.escapeListener.enable();
         this.widgets.dialog.show();
      },

      /**
       * Public function to set current dialog view mode
       *
       * @method setViewMode
       * @param viewMode {integer} New dialog view mode constant
       */
      setViewMode: function DLGF_setViewMode(viewMode)
      {
         this.options.viewMode = viewMode;

         if (this._isSiteViewMode(viewMode))
         {
            Dom.get(this.id + "-treeview").innerHTML = "";
            Dom.removeClass(this.id + "-wrapper", "repository-mode");
            this._populateSitePicker(viewMode);
         }
         else
         {
            Dom.addClass(this.id + "-wrapper", "repository-mode");
            // Build the TreeView widget
            var treeLocation = this.options.rootNode;

            if (viewMode == DLGF.VIEW_MODE_USERHOME)
            {
               treeLocation = this.options.userHome
            } else if (viewMode == DLGF.VIEW_MODE_SHARED)
            {
               treeLocation = this.options.sharedRoot;
            }

            this._buildTree(treeLocation);
            this.onPathChanged(this.options.path ? this.options.path : "/");
         }
      },


      /**
       * BUBBLING LIBRARY EVENT HANDLERS
       * Disconnected event handlers for event notification
       */

      /**
       * Site Changed event handler
       *
       * @method onSiteChanged
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onSiteChanged: function DLGF_onSiteChanged(layer, args)
      {
         // Check the event is directed towards this instance
         if ($hasEventInterest(this, args))
         {
            var obj = args[1];
            if (obj !== null)
            {
               // Should be a site in the arguments
               if (obj.site !== null)
               {
                  this.options.siteId = obj.site;
                  this.options.siteTitle = obj.siteTitle;
                  this._populateContainerPicker();
                  var sites = Selector.query("a", this.id + "-sitePicker"), site, i, j,
                     picker = Dom.get(this.id + "-sitePicker");

                  for (i = 0, j = sites.length; i < j; i++)
                  {
                     site = sites[i];
                     if (site.getAttribute("rel") == obj.site)
                     {
                        Dom.addClass(site, "selected");
                        if (obj.scrollTo)
                        {
                           picker.scrollTop = Dom.getY(site) - Dom.getY(picker);
                        }
                     }
                     else
                     {
                        Dom.removeClass(site, "selected");
                     }
                  }
               }
            }
         }
      },

      /**
       * Container Changed event handler
       *
       * @method onContainerChanged
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onContainerChanged: function DLGF_onContainerChanged(layer, args)
      {
         // Check the event is directed towards this instance
         if ($hasEventInterest(this, args))
         {
            var obj = args[1];
            if (obj !== null)
            {
               // Should be a container in the arguments
               if (obj.container !== null)
               {
                  this.options.containerId = obj.container;
                  this.options.containerType = this.containers[obj.container].type;
                  this._buildTree(this.containers[obj.container].nodeRef);
                  // Kick-off navigation to current path
                  this.onPathChanged(this.options.path);
                  var containers = Selector.query("a", this.id + "-containerPicker"), container, i, j,
                     picker = Dom.get(this.id + "-containerPicker");

                  for (i = 0, j = containers.length; i < j; i++)
                  {
                     container = containers[i];
                     if (container.getAttribute("rel") == obj.container)
                     {
                        Dom.addClass(container, "selected");
                        if (obj.scrollTo)
                        {
                           picker.scrollTop = Dom.getY(container) - Dom.getY(picker);
                        }
                     }
                     else
                     {
                        Dom.removeClass(container, "selected");
                     }
                  }
               }
            }
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
      onOK: function DLGF_onOK(e, p_obj)
      {
         // Close dialog and fire event so other components may use the selected folder
         this.widgets.escapeListener.disable();
         this.widgets.dialog.hide();

         var selectedFolder = this.selectedNode ? this.selectedNode.data : null;
         if (selectedFolder && this._isSiteViewMode(this.options.viewMode))
         {
            selectedFolder.siteId = this.options.siteId;
            selectedFolder.siteTitle = this.options.siteTitle;
            selectedFolder.containerId = this.options.containerId;
         }

         YAHOO.Bubbling.fire("folderSelected",
         {
            selectedFolder: selectedFolder,
            eventGroup: this
         });
      },

      /**
       * Dialog Cancel button event handler
       *
       * @method onCancel
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onCancel: function DLGF_onCancel(e, p_obj)
      {
         this.widgets.escapeListener.disable();
         this.widgets.dialog.hide();
      },

      /**
       * Mode change buttongroup event handler
       *
       * @method onViewModeChange
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onViewModeChange: function DLGF_onViewModeChange(e, p_obj)
      {
         var viewMode = this.options.viewMode;
         try
         {
            viewMode = parseInt(e.newValue.get("name"), 10);
            this.setViewMode(viewMode);
         }
         catch(ex)
         {
            // Remain in current view mode
         }
      },

      /**
       * Fired by YUI TreeView when a node has finished expanding
       * @method onExpandComplete
       * @param oNode {YAHOO.widget.Node} the node recently expanded
       */
      onExpandComplete: function DLGF_onExpandComplete(oNode)
      {
         Alfresco.logger.debug("DLGF_onExpandComplete");

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
      onNodeClicked: function DLGF_onNodeClicked(args)
      {
         Alfresco.logger.debug("DLGF_onNodeClicked");

         var e = args.event,
            node = args.node,
            userAccess = node.data.userAccess;

         if ((userAccess && userAccess.create) || (node.data.nodeRef == "") || (node.data.nodeRef.indexOf("alfresco://") === 0))
         {
            this.onPathChanged(node.data.path);
            this._updateSelectedNode(node);
         }

         Event.preventDefault(e);
         return false;
      },


      /**
       * Update tree when the path has changed
       * @method onPathChanged
       * @param path {string} new path
       */
      onPathChanged: function DLGF_onPathChanged(path)
      {
         this._showHighlight(false);
         this.selectedNode = null;
		 
         Alfresco.logger.debug("DLGF_onPathChanged:" + path);

         // ensure path starts with leading slash if not the root node
         if (path.charAt(0) != "/")
         {
            path = "/" + path;
         }
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
            expandPath = $combine("/", expandPath, paths[i]);
            this.pathsToExpand.push(expandPath);
         }
         Alfresco.logger.debug("DLGF_onPathChanged paths to expand:" + this.pathsToExpand.join(","));
         // Kick off the expansion process by expanding the first unexpanded path
         do
         {
            node = this.widgets.treeview.getNodeByProperty("path", this.pathsToExpand.shift());
            if (this.selectedNode == null)
            {
               this._updateSelectedNode(node);
            }
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
       * Creates the Site Picker control.
       * @method _populateSitePicker
       * @param viewMode {integer}
       * @private
       */
      _populateSitePicker: function DLGF__populateSitePicker(viewMode)
      {
         var sitePicker = Dom.get(this.id + "-sitePicker"),
            me = this;

         sitePicker.innerHTML = "";

         var fnSuccess = function DLGF__pSP_fnSuccess(response, sitePicker)
         {
            var sites = response.json, element, site, i, j, firstSite = null;

            var fnClick = function DLGF_pSP_onclick(site)
            {
               return function()
               {
                  YAHOO.Bubbling.fire("siteChanged",
                     {
                        site: site.shortName,
                        siteTitle: site.title,
                        eventGroup: me
                     });
                  return false;
               };
            };

            if (sites.length > 0)
            {
               firstSite = sites[0];
            }

            for (i = 0, j = sites.length; i < j; i++)
            {
               site = sites[i];

               if (Alfresco.util.arrayToObject(site.shortName))
               {
                  if (firstSite == null)
                  {
                     firstSite = site;
                  }

                  element = document.createElement("div");
                  if (i == j - 1)
                  {
                     Dom.addClass(element, "last");
                  }

                  element.innerHTML = '<a rel="' + site.shortName + '" href="#""><h4>' + $html(site.title) + '</h4>' + '<span>' + $html(site.description) + '</span></a>';
                  element.onclick = fnClick(site);
                  sitePicker.appendChild(element);
               }
            }

            // Select current site, or first site retrieved
            if (firstSite != null)
            {
               YAHOO.Bubbling.fire("siteChanged",
                  {
                     site: (this.options.siteId && this.options.siteId.length > 0) ? this.options.siteId : firstSite.shortName,
                     siteTitle: (this.options.siteId && this.options.siteId.length > 0) ? this.options.siteTitle : firstSite.title,
                     eventGroup: this,
                     scrollTo: true
                  });
            }
         }

         var sitesAPI = this.options.sitesAPI;

         // Filter sites list by favourites or recent, as applicable.
         if (viewMode === DLGF.VIEW_MODE_RECENT_SITES)
         {
            sitesAPI = this.options.recentSitesAPI;
         }
         else if (viewMode === DLGF.VIEW_MODE_FAVOURITE_SITES)
         {
            sitesAPI = this.options.favouriteSitesAPI;
         }

         var config =
         {
            url: sitesAPI,
            responseContentType: Alfresco.util.Ajax.JSON,
            successCallback:
            {
               fn: fnSuccess,
               scope: this,
               obj: sitePicker
            },
            failureCallback: null
         };

         Alfresco.util.Ajax.request(config);
      },

      /**
       * Creates the Container Picker control.
       * @method _populateContainerPicker
       * @private
       */
      _populateContainerPicker: function DLGF__populateContainerPicker()
      {
         var containerPicker = Dom.get(this.id + "-containerPicker"),
            me = this;

         containerPicker.innerHTML = "";

         var fnSuccess = function DLGF__pCP_fnSuccess(response, containerPicker)
         {
            var containers = response.json.containers, element, container, i, j;
            this.containers = {};

            var fnClick = function DLGF_pCP_onclick(containerName)
            {
               return function()
               {
                  YAHOO.Bubbling.fire("containerChanged",
                  {
                     container: containerName,
                     eventGroup: me
                  });
                  return false;
               };
            };

            for (i = 0, j = containers.length; i < j; i++)
            {
               container = containers[i];
               this.containers[container.name] = container;
               element = document.createElement("div");
               if (i == j - 1)
               {
                  Dom.addClass(element, "last");
               }

               element.innerHTML = '<a rel="' + container.name + '" href="#"><h4>' + container.name + '</h4>' + '<span>' + container.description + '</span></a>';
               element.onclick = fnClick(container.name);
               containerPicker.appendChild(element);
            }

            // Select current container
            YAHOO.Bubbling.fire("containerChanged",
            {
               container: this.options.containerId,
               eventGroup: this,
               scrollTo: true
            });
         };

         var fnFailure = function DLGF_pCP_fnFailure(response)
         {
            try
            {
               // Show a message in place of the root node
               var rootNode = this.widgets.treeview.getRoot(),
                  docNode = rootNode.children[0];

               docNode.isLoading = false;
               docNode.isLeaf = true;
               docNode.label = this.msg("message.error");
               docNode.labelStyle = "ygtverror";
               rootNode.refresh();
            }
            catch(e)
            {
            }
            containerPicker.innerHTML = '';
         };

         var containerURL = Alfresco.util.parseURL(this.options.containersAPI);
         containerURL.pathname += this.options.siteId;

         var config =
         {
            url: containerURL.getUrl(),
            responseContentType: Alfresco.util.Ajax.JSON,
            successCallback:
            {
               fn: fnSuccess,
               scope: this,
               obj: containerPicker
            },
            failureCallback:
            {
               fn: fnFailure,
               scope: this
            }
         };

         Alfresco.util.Ajax.request(config);
      },

      /**
       * Creates the TreeView control and renders it to the parent element.
       * @method _buildTree
       * @param p_rootNodeRef {string} NodeRef of root node for this tree
       * @private
       */
      _buildTree: function DLGF__buildTree(p_rootNodeRef)
      {
         Alfresco.logger.debug("DLGF__buildTree");

         // Create a new tree
         var tree = new YAHOO.widget.TreeView(this.id + "-treeview");
         this.widgets.treeview = tree;

         // Having both focus and highlight are just confusing (YUI 2.7.0 addition)
         YAHOO.widget.TreeView.FOCUS_CLASS_NAME = "";

         // Turn dynamic loading on for entire tree
         tree.setDynamicLoad(this.fnLoadNodeData);

         var rootLabel = "location.path.repository";
         if (this._isSiteViewMode(this.options.viewMode))
         {
            var treeConfig = this.options.siteTreeContainerTypes[this.options.containerType] || {};
            rootLabel = treeConfig.rootLabel || "location.path.documents";
         }
         else if (this.options.viewMode == DLGF.VIEW_MODE_USERHOME)
         {
            rootLabel = "location.path.myfiles";
         }
         else if (this.options.viewMode == DLGF.VIEW_MODE_SHARED)
         {
            rootLabel = "location.path.shared";
         }

         // Add default top-level node
         var tempNode = new YAHOO.widget.TextNode(
         {
            label: this.msg(rootLabel),
            path: "/",
            nodeRef: p_rootNodeRef
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
      _showHighlight: function DLGF__showHighlight(isVisible)
      {
         Alfresco.logger.debug("DLGF__showHighlight");

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
      _updateSelectedNode: function DLGF__updateSelectedNode(node)
      {
         Alfresco.logger.debug("DLGF__updateSelectedNode");

         this._showHighlight(false);
         this.selectedNode = node;
         this._showHighlight(true);

         // ALF-20094 fix, don't allow user to press ok button if he has no create access in selected target
         if (node.data.userAccess && !node.data.userAccess.create)
         {
            this.widgets.okButton.set("disabled", true);
         }
         else
         {
            this.widgets.okButton.set("disabled", false);
         }
      },

      /**
       * Build URI parameter string for treenode JSON data webscript
       *
       * @method _buildTreeNodeUrl
       * @param path {string} Path to query
       */
      _buildTreeNodeUrl: function DLGF__buildTreeNodeUrl(path)
      {
         var uriTemplate = Alfresco.constants.PROXY_URI;
         if (this._isSiteViewMode(this.options.viewMode))
         {
            var treeConfig = this.options.siteTreeContainerTypes[this.options.containerType] || {};
            if (treeConfig.uri)
            {
               uriTemplate += treeConfig.uri;
            }
            else
            {
               uriTemplate += "slingshot/doclib/treenode/site/{site}/{container}{path}";
               uriTemplate += "?children={evaluateChildFoldersSite}";
               uriTemplate += "&max={maximumFolderCountSite}";
            }
         }
         else
         {
            if (this.options.viewMode == DLGF.VIEW_MODE_USERHOME)
            {
               uriTemplate += "slingshot/doclib/treenode/node/{userHome}{path}";
               uriTemplate += "?children={evaluateChildFoldersRepo}";
            }
            else if (this.options.viewMode == DLGF.VIEW_MODE_SHARED)
            {
               uriTemplate += "slingshot/doclib/treenode/node/{sharedRootPath}{path}";
               uriTemplate += "?children={evaluateChildFoldersRepo}";
               uriTemplate += "&libraryRoot={sharedRoot}";
            }
            else
            {
               uriTemplate += "slingshot/doclib/treenode/node/alfresco/company/home{path}";
               uriTemplate += "?children={evaluateChildFoldersRepo}";
               uriTemplate += "&libraryRoot={rootNode}";
            }
            uriTemplate += "&max={maximumFolderCountRepo}";
         }

         var url = YAHOO.lang.substitute(uriTemplate,
         {
            site: encodeURIComponent(this.options.siteId),
            container: encodeURIComponent(this.options.containerId),
            rootNode: this.options.rootNode,
            userHome: (this.options.userHome || "").replace(":/", ""),
            sharedRoot: this.options.sharedRoot,
            sharedRootPath: this.options.sharedRoot.replace(":/", ""),
            path: Alfresco.util.encodeURIPath(path),
            evaluateChildFoldersSite: this.options.evaluateChildFoldersSite + '',
            maximumFolderCountSite: this.options.maximumFolderCountSite,
            evaluateChildFoldersRepo: this.options.evaluateChildFoldersRepo + '',
            maximumFolderCountRepo: this.options.maximumFolderCountRepo
         });

         return url;
      },
      
      /**
       * Gets resource style specified in the {style} configuration that corresponds with matching filter 
       * from share-documentlibrary-config.xml [CommonComponentStyle][component-style], {browse.folder} component, or null if the filter does not match.
       * 
       * The returned value is used to be set to the treeNode as customCls attribute, used for rendering custom icons in treeView. 
       * @param p_oData
       */
      _buildCustomStyleClass : function DLGF__buildCustomStyleClass(p_oData)
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
       *
       * Is the view mode a view on sites?
       *
       * @method _isSiteViewMode
       * @param viewMode
       * @return {boolean}
       */
      _isSiteViewMode: function DLGF__isSiteViewMode(viewMode)
      {
         var siteModes = [DLGF.VIEW_MODE_SITE, DLGF.VIEW_MODE_FAVOURITE_SITES, DLGF.VIEW_MODE_RECENT_SITES];
         return (Alfresco.util.arrayContains(siteModes, viewMode))
      }
   });

   /* Dummy instance to load optional YUI components early */
   var dummyInstance = new Alfresco.module.DoclibGlobalFolder("null");
})();
