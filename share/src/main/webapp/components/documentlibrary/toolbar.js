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
 * DocumentList Toolbar component.
 * 
 * @namespace Alfresco
 * @class Alfresco.DocListToolbar
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Element = YAHOO.util.Element;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $combine = Alfresco.util.combinePaths,
      $siteURL = Alfresco.util.siteURL;

   /**
    * Preferences
    */
   var PREFERENCES_ROOT = "org.alfresco.share.documentList",
      PREF_HIDE_NAVBAR = PREFERENCES_ROOT + ".hideNavBar";
   
   /**
    * DocListToolbar constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @param {boolean} registerListeners Indicates whether or not to register the listeners
    * @return {Alfresco.DocListToolbar} The new DocListToolbar instance
    * @constructor
    */
   Alfresco.DocListToolbar = function(htmlId, registerListeners)
   {
      Alfresco.DocListToolbar.superclass.constructor.call(this, "Alfresco.DocListToolbar", htmlId, ["button", "menu", "container"]);
      
      // Initialise prototype properties
      this.selectedFiles = [];
      this.currentFilter = {};
      this.dynamicControls = [];
      this.doclistMetadata = {};
      this.actionsView = "browse";

//      // This block allows us to not register listeners if told not to. It has been added
//      // to support 4.2 Enterprise changes that require an instance of the toolbar that
//      // does not respond to events (but does allow access to the action handling functions).
//      if (registerListeners == true || registerListeners === undefined)
//      {
         // Decoupled event listeners
         YAHOO.Bubbling.on("filterChanged", this.onFilterChanged, this);
         YAHOO.Bubbling.on("deactivateAllControls", this.onDeactivateAllControls, this);
         YAHOO.Bubbling.on("deactivateDynamicControls", this.onDeactivateDynamicControls, this);
         YAHOO.Bubbling.on("selectedFilesChanged", this.onSelectedFilesChanged, this);
         YAHOO.Bubbling.on("userAccess", this.onUserAccess, this);
         YAHOO.Bubbling.on("doclistMetadata", this.onDoclistMetadata, this);
         YAHOO.Bubbling.on("showFileUploadDialog", this.onFileUpload, this);
         YAHOO.Bubbling.on("dropTargetOwnerRequest", this.onDropTargetOwnerRequest, this);
         YAHOO.Bubbling.on("documentDragOver", this.onDocumentDragOver, this);
         YAHOO.Bubbling.on("documentDragOut", this.onDocumentDragOut, this);
         YAHOO.Bubbling.on("registerAction", this.onRegisterAction, this);
//      }

      return this;
   };

   /**
    * Extend from Alfresco.component.Base
    */
   YAHOO.extend(Alfresco.DocListToolbar, Alfresco.component.Base);
   
   /**
    * Augment prototype with Actions module
    */
   YAHOO.lang.augmentProto(Alfresco.DocListToolbar, Alfresco.doclib.Actions);
   
   /**
    * Augment prototype with main class implementation, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentObject(Alfresco.DocListToolbar.prototype,
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
          * Number of multi-file uploads before grouping the Activity Post
          *
          * @property groupActivitiesAt
          * @type int
          * @default 5
          */
         groupActivitiesAt: 5,
         
         /**
          * Flag indicating whether navigation bar is visible or not.
          * 
          * @property hideNavBar
          * @type boolean
          */
         hideNavBar: false,

         /**
          * Whether the Repo Browser is in use or not
          *
          * @property repositoryBrowsing
          * @type boolean
          */
         repositoryBrowsing: true,

         /**
          * Decides it the title shall be displayed next to the name if it contains a value that is different from the name
          *
          * @property useTitle
          * @type boolean
          * @default true
          */
         useTitle: true
      },
      
      /**
       * Current path being browsed.
       * 
       * @property currentPath
       * @type string
       */
      currentPath: "",

      /**
       * Current filter to choose toolbar view and populate description.
       * 
       * @property currentFilter
       * @type string
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
       * Array of selected states for visible files.
       * 
       * @property selectedFiles
       * @type array
       */
      selectedFiles: null,

      /**
       * Folder Details Url for last breadcrumb
       * 
       * @property folderDetailsUrl
       * @type string
       */
      folderDetailsUrl: null,

      /**
       * Dynamic controls that take part in the deactivateDynamicControls event
       * 
       * @property dynamicControls
       * @type array
       */
      dynamicControls: null,

      /**
       * Metadata returned by doclist data webscript
       *
       * @property doclistMetadata
       * @type object
       * @default null
       */
      doclistMetadata: null,

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function DLTB_onReady()
      {
         if (Dom.get(this.id + "-tb-body") != null)
         {
            // Create Content menu button
            if (Dom.get(this.id + "-createContent-button"))
            {
               // Create menu button that
               this.widgets.createContent = Alfresco.util.createYUIButton(this, "createContent-button", this.onCreateContent,
               {
                  type: "menu",
                  menu: "createContent-menu",
                  lazyloadmenu: false,
                  disabled: true,
                  value: "CreateChildren"
               });

               // Make sure we load sub menu lazily with data on each click
               var createContentMenu = this.widgets.createContent.getMenu(),
                   groupIndex = 0;

               // MNT-11142 Create menu in the document library not disappearing when clicking outside the menu
               createContentMenu.cfg.config.clicktohide.value = true;
               // Create content actions
               if (this.options.createContentActions.length !== 0)
               {
                  var menuItems = [], menuItem, content, url, config, html, li;
                  for (var i = 0; i < this.options.createContentActions.length; i++)
                  {
                     // Create menu item from config
                     content = this.options.createContentActions[i];
                     config = { parent: createContentMenu };
                     url = null;

                     // Check config type
                     if (content.type == "javascript")
                     {
                        config.onclick =
                        {
                           fn: function(eventName, eventArgs, obj)
                           {
                              // Copy node so we can safely pass it to an action
                              var node = Alfresco.util.deepCopy(this.doclistMetadata.parent);

                              // Make it more similar to a usual doclib action callback object
                              var currentFolderItem = {
                                 nodeRef: node.nodeRef,
                                 node: node,
                                 jsNode: new Alfresco.util.Node(node)
                              };
                              this[obj.params["function"]].call(this, currentFolderItem);
                           },
                           obj: content,
                           scope: this
                        };

                        url = '#';
                     }
                     else if (content.type == "pagelink")
                     {
                        url = $siteURL(content.params.page);
                     }
                     else if (content.type == "link")
                     {
                        url = content.params.href;
                     }

                     // Create menu item
                     html = '<a href="' + url + '" rel="' + content.permission + '"><span style="background-image:url(' + Alfresco.constants.URL_RESCONTEXT + 'components/images/filetypes/' + content.icon + '-file-16.png)" class="' + content.icon + '-file">' + this.msg(content.label) + '</span></a>';
                     li = document.createElement("li");
                     li.innerHTML = html;
                     menuItem = new YAHOO.widget.MenuItem(li, config);

                     menuItems.push(menuItem);
                  }
                  createContentMenu.addItems(menuItems, groupIndex);
                  groupIndex++;
               }

               // Create content by template menu item
               if (this.options.createContentByTemplateEnabled)
               {
                  // Create menu item elements
                  var li = document.createElement("li");
                  li.innerHTML = '<a href="#"><span>' + this.msg("menu.create-content.by-template-node") + '</span></a>';

                  // Make sure to stop clicks on the sub menu link to close the entire menu
                  YAHOO.util.Event.addListener(Selector.query("a", li, true), "click", function(e)
                  {
                     Event.preventDefault(e);
                     Event.stopEvent(e);
                  });

                  // Create placeholder menu
                  var div = document.createElement("div");
                  div.innerHTML = '<div class="bd"><ul></ul></div>';

                  // 
                  var li2 = document.createElement("li");
                  li2.innerHTML = '<a href="#"><span>' + this.msg("menu.create-content.by-template-folder") + '</span></a>';

                  // Make sure to stop clicks on the sub menu link to close the entire menu
                  YAHOO.util.Event.addListener(Selector.query("a", li2, true), "click", function(e)
                  {
                     Event.preventDefault(e);
                     Event.stopEvent(e);
                  });

                  // Create placeholder menu
                  var div2 = document.createElement("div");
                  div2.innerHTML = '<div class="bd"><ul></ul></div>';
                  
                  // Add menu item
                  var createContentByTemplate = new YAHOO.widget.MenuItem(li, {
                     parent: createContentMenu,
                     submenu: div
                  });
                  
                  // Add menu item
                  var createFolderByTemplate = new YAHOO.widget.MenuItem(li2, {
                     parent: createContentMenu,
                     submenu: div2
                  });
                  
                  createContentMenu.addItems([ createContentByTemplate, createFolderByTemplate], groupIndex);
                  groupIndex++;

                  // Make sure that the available template are lazily loaded
                  var templateNodesMenus = this.widgets.createContent.getMenu().getSubmenus(),
                        templateNodesMenu = templateNodesMenus.length > 0 ? templateNodesMenus[0] : null;
                  if (templateNodesMenu)
                  {
                     templateNodesMenu.subscribe("beforeShow", this.onCreateByTemplateNodeBeforeShow, this, true);
                     templateNodesMenu.subscribe("click", this.onCreateByTemplateNodeClick, this, true);
                  }
                  
                  var templateFoldersMenu = templateNodesMenus.length > 1 ? templateNodesMenus[1] : null;
                  if (templateFoldersMenu)
                  {
                     templateFoldersMenu.subscribe("beforeShow", this.onCreateByTemplateFolderBeforeShow, this, true);
                     templateFoldersMenu.subscribe("click", this.onCreateByTemplateFolderClick, this, true);
                  }
               }

               // Render menu with all new menu items
               createContentMenu.render();
               this.dynamicControls.push(this.widgets.createContent);
            }

            // New Folder button: user needs "create" access
         this.widgets.newFolder = Alfresco.util.createYUIButton(this, "newFolder-button", this.onNewFolder,
         {
            disabled: true,
            value: "CreateChildren"
         });
         this.dynamicControls.push(this.widgets.newFolder);
            
         // File Upload button: user needs  "CreateChildren" access
         this.widgets.fileUpload = Alfresco.util.createYUIButton(this, "fileUpload-button", this.onFileUpload,
         {
            disabled: true,
            value: "CreateChildren"
         });
         this.dynamicControls.push(this.widgets.fileUpload);

         // Sync to Cloud button
         this.widgets.syncToCloud = Alfresco.util.createYUIButton(this, "syncToCloud-button", this.onSyncToCloud,
         {
            disabled: true,
            value: "CreateChildren"
         });
         this.dynamicControls.push(this.widgets.syncToCloud);
            
         // Unsync from Cloud button
         this.widgets.unsyncFromCloud = Alfresco.util.createYUIButton(this, "unsyncFromCloud-button", this.onUnsyncFromCloud,
         {
            disabled: true,
            value: "CreateChildren"
         });
         this.dynamicControls.push(this.widgets.unsyncFromCloud);
            
         // Selected Items menu button
         this.widgets.selectedItems = Alfresco.util.createYUIButton(this, "selectedItems-button", this.onSelectedItems,
         {
            type: "menu", 
            menu: "selectedItems-menu",
            lazyloadmenu: false,
            disabled: true
         });
         this.dynamicControls.push(this.widgets.selectedItems);

            if (Dom.get(this.id + "hideNavBar-button"))
            {
               // Hide/Show NavBar button
               this.widgets.hideNavBar = Alfresco.util.createYUIButton(this, "hideNavBar-button", this.onHideNavBar,
               {
                  type: "checkbox",
                  checked: !this.options.hideNavBar
               });
               if (this.widgets.hideNavBar !== null)
               {
                  this.widgets.hideNavBar.set("title", this.msg(this.options.hideNavBar ? "button.navbar.show" : "button.navbar.hide"));
                  this.dynamicControls.push(this.widgets.hideNavBar);
               }
            }

            // Hide or show the nav bar depending on the current settings...
            Dom.setStyle(this.id + "-navBar", "display", this.options.hideNavBar ? "none" : "block");

            // RSS Feed link button
            this.widgets.rssFeed = Alfresco.util.createYUIButton(this, "rssFeed-button", null, 
            {
               type: "link"
            });
            this.dynamicControls.push(this.widgets.rssFeed);

            // Folder Up Navigation button
            this.widgets.folderUp =  Alfresco.util.createYUIButton(this, "folderUp-button", this.onFolderUp,
            {
               disabled: true,
               title: this.msg("button.up")
            });
            this.dynamicControls.push(this.widgets.folderUp);

            // Finally show the component body here to prevent UI artifacts on YUI button decoration
            Dom.setStyle(this.id + "-tb-body", "visibility", "visible");
         }
         
         // DocLib Actions module
         this.modules.actions = new Alfresco.module.DoclibActions();
         
         // Reference to Document List component
         this.modules.docList = Alfresco.util.ComponentManager.findFirst("Alfresco.DocumentList");

         // Preferences service
         this.services.preferences = new Alfresco.service.Preferences();
      },

      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * Create Content menu click handler for create content menu items (not create by template node menu items)
       *
       * @method onCreateContent
       * @param sType {string} Event type, e.g. "click"
       * @param aArgs {array} Arguments array, [0] = DomEvent, [1] = EventTarget
       * @param p_obj {object} Object passed back from subscribe method
       */
      onCreateContent: function DLTB_onCreateContent(sType, aArgs, p_obj)
      {
         var eventTarget = aArgs[1],
            anchor = eventTarget.element.getElementsByTagName("a")[0];

         // Make sure a create content menu item was clicked (not a template node)
         if (eventTarget.parent === this.widgets.createContent.getMenu() && anchor && anchor.nodeName == "A")
         {
            anchor.href = YAHOO.lang.substitute(anchor.href,
            {
               nodeRef: this.doclistMetadata.parent.nodeRef
            });
         }
      },

      /**
       * Create Content Template Node menu beforeShow handler
       *
       * @method onCreateByTemplateNodeBeforeShow
       */
      onCreateByTemplateNodeBeforeShow: function DLTB_onCreateByTemplateNodeBeforeShow()
      {
         // Display loading message
         var templateNodesMenu = this.widgets.createContent.getMenu().getSubmenus()[0];
         if (templateNodesMenu.getItems().length == 0)
         {
            templateNodesMenu.clearContent();
            templateNodesMenu.addItem(this.msg("label.loading"));
            templateNodesMenu.render();

            // Load template nodes
            Alfresco.util.Ajax.jsonGet(
            {
               url: Alfresco.constants.PROXY_URI + "slingshot/doclib/node-templates",
               successCallback:
               {
                  fn: function(response, menu)
                  {
                     var nodes = response.json.data,
                        menuItems = [],
                        name;
                     for (var i = 0, il = nodes.length; i < il; i++)
                     {
                        node = nodes[i];
                        name = $html(node.name);
                        if (node.title && node.title !== node.name && this.options.useTitle)
                        {
                           name += '<span class="title">(' + $html(node.title) + ')</span>';
                        }
                        menuItems.push(
                        {
                           text: '<span title="' + $html(node.description) + '">' + name +'</span>',
                           value: node
                        });
                     }
                     if (menuItems.length == 0)
                     {
                        menuItems.push(this.msg("label.empty"));
                     }
                     templateNodesMenu.clearContent();
                     templateNodesMenu.addItems(menuItems);
                     templateNodesMenu.render();
                  },
                  scope: this
               }
            });
         }
      },
      
      

      /**
       * Create Content Template Node sub menu click handler
       *
       * @method onCreateContentTemplateNode
       * @param sType {string} Event type, e.g. "click"
       * @param aArgs {array} Arguments array, [0] = DomEvent, [1] = EventTarget
       * @param p_obj {object} Object passed back from subscribe method
       */
      onCreateByTemplateNodeClick: function DLTB_onCreateContentTemplateNode(sType, aArgs, p_obj)
      {
         // Create content based on a template
         var node = aArgs[1].value,
            destination = this.doclistMetadata.parent.nodeRef,
            siteId = this.options.siteId;

         // If node is undefined the loading or empty menu items were clicked
         if (node)
         {
            Alfresco.util.Ajax.jsonPost(
            {
               url: Alfresco.constants.PROXY_URI + "slingshot/doclib/node-templates",
               dataObj:
               {
                  sourceNodeRef: node.nodeRef,
                  parentNodeRef: destination
               },
               successCallback:
               {
                  fn: function (response)
                  {
                     Alfresco.Share.postActivity(siteId, "org.alfresco.documentlibrary.file-created", "{cm:name}", "document-details?nodeRef=" + response.json.nodeRef, 
                     {
                        appTool: "documentlibrary",
                        nodeRef: response.json.nodeRef
                     });
                     // Make sure we get other components to update themselves to show the new content
                     YAHOO.Bubbling.fire("nodeCreated",
                     {
                        name: node.name,
                        parentNodeRef: destination,
                        highlightFile: response.json.name
                     });
                  }
               },
               successMessage: this.msg("message.create-content-by-template-node.success", node.name),
               failureMessage: this.msg("message.create-content-by-template-node.failure", node.name)
            });
         }
      },

      /**
       * Create Content Template Node menu beforeShow handler
       *
       * @method onCreateByTemplateFolderBeforeShow
       */
      onCreateByTemplateFolderBeforeShow: function DLTB_onCreateByTemplateFolderBeforeShow()
      {
         // Display loading message
         var templateNodesMenu = this.widgets.createContent.getMenu().getSubmenus()[1];
         if (templateNodesMenu.getItems().length == 0)
         {
            templateNodesMenu.clearContent();
            templateNodesMenu.addItem(this.msg("label.loading"));
            templateNodesMenu.render();

            // Load template nodes
            Alfresco.util.Ajax.jsonGet(
            {
               url: Alfresco.constants.PROXY_URI + "slingshot/doclib/folder-templates",
               successCallback:
               {
                  fn: function(response, menu)
                  {
                     var nodes = response.json.data,
                        menuItems = [],
                        name;
                     for (var i = 0, il = nodes.length; i < il; i++)
                     {
                        node = nodes[i];
                        name = $html(node.name);
                        if (node.title && node.title !== node.name && this.options.useTitle)
                        {
                           name += '<span class="title">(' + $html(node.title) + ')</span>';
                        }
                        menuItems.push(
                        {
                           text: '<span title="' + $html(node.description) + '">' + name +'</span>',
                           value: node
                        });
                     }
                     if (menuItems.length == 0)
                     {
                        menuItems.push(this.msg("label.empty"));
                     }
                     templateNodesMenu.clearContent();
                     templateNodesMenu.addItems(menuItems);
                     templateNodesMenu.render();
                  },
                  scope: this
               }
            });
         }
      },
      
      /**
       * Create Content Template Folder sub menu click handler
       *
       * @method onCreateContentTemplateNode
       * @param sType {string} Event type, e.g. "click"
       * @param aArgs {array} Arguments array, [0] = DomEvent, [1] = EventTarget
       * @param p_obj {object} Object passed back from subscribe method
       */
      onCreateByTemplateFolderClick: function DLTB_onCreateContentTemplateFolder(sType, aArgs, p_obj)
      {
         // Generate the standard "New Folder" dialog but update the XHR configuration to redirect
         // the request to the WebScript for copying space templates. The WebScript will update the
         // copy with any name, title and description provided.
         var dialog = this.onNewFolder(null, p_obj);
         dialog.options.doBeforeDialogShow = {
            fn: function DLTB_onNewFolderFromTemplate_doBeforeDialogShow(p_form, p_dialog)
            {
               Dom.get(p_dialog.id + "-dialogTitle").innerHTML = this.title;
               Dom.get(p_dialog.id + "-dialogHeader").innerHTML = this.header;
               Dom.get(p_dialog.id + "_prop_cm_name").value = this.node.name;
               Dom.get(p_dialog.id + "_prop_cm_title").value = this.node.title;
               Dom.get(p_dialog.id + "_prop_cm_description").value = this.node.description;
            },
            scope: {
               node: aArgs[1].value,
               title: this.msg("label.new-folder-from-template.title"),
               header: this.msg("label.new-folder-from-template.header")
            }
         };
         dialog.options.successCallback =
         {
            fn: function (response)
            {
               // Make sure we get other components to update themselves to show the new content
               YAHOO.Bubbling.fire("nodeCreated",
               {
                  name: node.name,
                  parentNodeRef: destination,
                  highlightFile: response.json.name
               });
            }
         };
         dialog.options.successMessage = this.msg("message.create-content-by-template-node.success", node.name);
         dialog.options.failureMessage = this.msg("message.create-content-by-template-node.failure", node.name);
         dialog.options.doBeforeFormSubmit = {
            fn: function DLTB_onNewFolderFromTemplate_doBeforeFormSubmit(form, obj)
            {
               form.attributes.action.nodeValue = Alfresco.constants.PROXY_URI + "slingshot/doclib/folder-templates";
            },
            scope: this
         };
         dialog.options.doBeforeAjaxRequest = {
            fn: function DLTB_onNewFolderFromTemplate_doBeforeAjaxRequest(p_config, p_obj)
            {
               p_config.dataObj.sourceNodeRef = this.node.nodeRef;
               p_config.dataObj.parentNodeRef = this.destination;
               return true;
            },
            scope: {
               node: aArgs[1].value,
               destination: this.doclistMetadata.parent.nodeRef
            }
         };
      },
      
      /**
       * New Folder button click handler
       *
       * @method onNewFolder
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onNewFolder: function DLTB_onNewFolder(e, p_obj)
      {
         var destination = this.doclistMetadata.parent.nodeRef;

         // Intercept before dialog show
         var doBeforeDialogShow = function DLTB_onNewFolder_doBeforeDialogShow(p_form, p_dialog)
         {
            Dom.get(p_dialog.id + "-dialogTitle").innerHTML = this.msg("label.new-folder.title");
            Dom.get(p_dialog.id + "-dialogHeader").innerHTML = this.msg("label.new-folder.header");
         };
         
         var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true",
         {
            itemKind: "type",
            itemId: "cm:folder",
            destination: destination,
            mode: "create",
            submitType: "json",
            formId: "doclib-common"
         });

         // Using Forms Service, so always create new instance
         var createFolder = new Alfresco.module.SimpleDialog(this.id + "-createFolder");

         createFolder.setOptions(
         {
            width: "33em",
            templateUrl: templateUrl,
            actionUrl: null,
            destroyOnHide: true,
            doBeforeDialogShow:
            {
               fn: doBeforeDialogShow,
               scope: this
            },
            onSuccess:
            {
               fn: function DLTB_onNewFolder_success(response)
               {
                  var activityData;
                  var folderName = response.config.dataObj["prop_cm_name"];
                  var folderNodeRef = response.json.persistedObject;
                  
                  activityData =
                  {
                     fileName: folderName,
                     nodeRef: folderNodeRef,
                     path: this.currentPath + (this.currentPath !== "/" ? "/" : "") + folderName
                  };
                  this.modules.actions.postActivity(this.options.siteId, "folder-added", "documentlibrary", activityData);
                  
                  YAHOO.Bubbling.fire("folderCreated",
                  {
                     name: folderName,
                     parentNodeRef: destination
                  });
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.new-folder.success", folderName)
                  });
               },
               scope: this
            },
            onFailure:
            {
               fn: function DLTB_onNewFolder_failure(response)
               {
                  if (response)
                  {
                     var folderName = response.config.dataObj["prop_cm_name"];
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: this.msg("message.new-folder.failure", folderName)
                     });
                  }
                  else
                  {
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: this.msg("message.failure")
                     });
                  }
                  createFolder.widgets.cancelButton.set("disabled", false);
               },
               scope: this
            }
         });
         createFolder.show();
         return createFolder;
      },
      
      /**
       * Sync to Cloud button click handler
       *
       * @method onSyncToCloud
       * @param e {object} DomEvent
       * @param p_obj {object|array} Object passed back from addListener method or args from Bubbling event
       */
      onSyncToCloud: function DLTB_onSyncToCloud(e, p_obj)
      {
         var record = new Object();
         var parent = this.doclistMetadata.parent;

         // Display name
         record["displayName"] = parent.properties["cm:name"];

         // NodeRef
         record["nodeRef"] = parent.nodeRef;

         // jsNode
         var jsNode = new Object();
         jsNode["isContainer"] = parent.isContainer;
         record["jsNode"] = jsNode;

         this.onActionCloudSync(record);
      },

      /**
       * Unsync from Cloud button click handler
       *
       * @method onUnsyncFromCloud
       * @param e {object} DomEvent
       * @param p_obj {object|array} Object passed back from addListener method or args from Bubbling event
       */
      onUnsyncFromCloud: function DLTB_onUnsyncFromCloud(e, p_obj)
      {
         var record = new Object();
         var parent = this.doclistMetadata.parent;

         // jsNode
         var jsNode = new Object();
         jsNode["isContainer"] = parent.isContainer;
         record["jsNode"] = jsNode;

         // NodeRef
         var nodeRef = new Object();
         nodeRef["uri"] = parent.nodeRef.replace(":/", "");
         jsNode["nodeRef"] = nodeRef;

         // Display name
         record["displayName"] = parent.properties["cm:name"];

         this.onActionCloudUnsync(record);
      },
      
      /**
       * File Upload button click handler
       *
       * @method onFileUpload
       * @param e {object} DomEvent
       * @param p_obj {object|array} Object passed back from addListener method or args from Bubbling event
       */
      onFileUpload: function DLTB_onFileUpload(e, p_obj)
      {
         if (this.fileUpload === null)
         {
            this.fileUpload = Alfresco.getFileUploadInstance();
         }
         
         // Show uploader for multiple files
         var multiUploadConfig =
         {
            siteId: this.options.siteId,
            containerId: this.options.containerId,
            uploadDirectory: this.currentPath,
            filter: [],
            mode: this.fileUpload.MODE_MULTI_UPLOAD,
            thumbnails: "doclib",
            onFileUploadComplete:
            {
               fn: this.onFileUploadComplete,
               scope: this
            }
         };
         this.fileUpload.show(multiUploadConfig);

         if (YAHOO.lang.isArray(p_obj) && p_obj[1].tooltip)
         {
            var balloon = Alfresco.util.createBalloon(this.fileUpload.uploader.id + "-dialog",
            {
               html: p_obj[1].tooltip,
               width: "30em"
            });
            balloon.show();

            this.fileUpload.uploader.widgets.panel.hideEvent.subscribe(function()
            {
               balloon.hide()
            });
         }
      },
      
      /**
       * Calls the file Upload button click handler but creates an additional tooltip
       * with information on Drag-and-Drop as an alternative method for uploading content
       *
       * @method onFileUploadWithTooltip
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onFileUploadWithTooltip: function DLTB_onFileUploadWithTooltip(e, p_obj)
      {
         this.onFileUpload(e, p_obj);
      },
      
      /**
       * File Upload complete event handler
       *
       * @method onFileUploadComplete
       * @param complete {object} Object literal containing details of successful and failed uploads
       */
      onFileUploadComplete: function DLTB_onFileUploadComplete(complete)
      {
         var success = complete.successful.length, activityData, file;
         if (success > 0)
         {
            if (success < (this.options.groupActivitiesAt || 5))
            {
               // Below cutoff for grouping Activities into one
               for (var i = 0; i < success; i++)
               {
                  file = complete.successful[i];
                  activityData =
                  {
                     fileName: file.fileName,
                     nodeRef: file.nodeRef
                  };
                  this.modules.actions.postActivity(this.options.siteId, "file-added", "document-details", activityData);
               }
            }
            else
            {
               // grouped into one message
               activityData =
               {
                  fileCount: success,
                  path: this.currentPath,
                  parentNodeRef : this.doclistMetadata.parent.nodeRef
               };
               this.modules.actions.postActivity(this.options.siteId, "files-added", "documentlibrary", activityData);
            }
         }
      },

      /**
       * Selected Items button click handler
       *
       * @method onSelectedItems
       * @param sType {string} Event type, e.g. "click"
       * @param aArgs {array} Arguments array, [0] = DomEvent, [1] = EventTarget
       * @param p_obj {object} Object passed back from subscribe method
       */
      onSelectedItems: function DLTB_onSelectedItems(sType, aArgs, p_obj)
      {
         var domEvent = aArgs[0],
            eventTarget = aArgs[1];

         // Check mandatory docList module is present
         if (this.modules.docList)
         {
            // Get the function related to the clicked item
            var fn = Alfresco.util.findEventClass(eventTarget);
            if (fn && (typeof this[fn] == "function"))
            {
               this[fn].call(this, this.modules.docList.getSelectedFiles());
            }
         }

         Event.preventDefault(domEvent);
      },
      
      /**
       * Delete Multiple Records.
       *
       * @method onActionDelete
       * @param records {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionDelete: function DLTB_onActionDelete(records)
      {
         var me = this,
            fileNames = [];
         
         // Handle a single record being provided...
         if (typeof records.length === "undefined")
         {
            records = [records];
         }
         for (var i = 0, j = records.length; i < j; i++)
         {
            fileNames.push("<span class=\"" + (records[i].jsNode.isContainer ? "folder" : "document") + "\">" + $html(records[i].displayName) + "</span>");
         }
         
         var confirmTitle = this.msg("title.multiple-delete.confirm"),
            confirmMsg = this.msg("message.multiple-delete.confirm", records.length);

         confirmMsg += "<div class=\"toolbar-file-list\">" + fileNames.join("") + "</div>";

         Alfresco.util.PopupManager.displayPrompt(
         {
            title: confirmTitle,
            text: confirmMsg,
            noEscape: true,
            modal: true,
            buttons: [
            {
               text: this.msg("button.delete"),
               handler: function DLTB_onActionDelete_delete()
               {
                  this.destroy();
                  me._onActionDeleteConfirm.call(me, records);
               }
            },
            {
               text: this.msg("button.cancel"),
               handler: function DLTB_onActionDelete_cancel()
               {
                  this.destroy();
               },
               isDefault: true
            }]
         });
      },

      /**
       * Delete Multiple Records confirmed.
       *
       * @method _onActionDeleteConfirm
       * @param records {array} Array containing records to be deleted
       * @private
       */
      _onActionDeleteConfirm: function DLTB__onActionDeleteConfirm(records)
      {
         var multipleRecords = [], i, ii;
         for (i = 0, ii = records.length; i < ii; i++)
         {
            multipleRecords.push(records[i].jsNode.nodeRef.nodeRef);
         }
         
         // Success callback function
         var fnSuccess = function DLTB__oADC_success(data, records)
         {
            var result;
            var successFileCount = 0;
            var successFolderCount = 0;
            
            // Did the operation succeed?
            if (!data.json.overallSuccess)
            {
               Alfresco.util.PopupManager.displayMessage(
               {
                  text: this.msg("message.multiple-delete.failure")
               });
               return;
            }
            
            this.modules.docList.totalRecords -= data.json.totalResults;
            YAHOO.Bubbling.fire("filesDeleted");
            
            for (i = 0, ii = data.json.totalResults; i < ii; i++)
            {
               result = data.json.results[i];
               
               if (result.success)
               {
                  if (result.type == "folder")
                  {
                     successFolderCount++;
                  }
                  else
                  {
                     successFileCount++;
                  }
                  
                  YAHOO.Bubbling.fire(result.type == "folder" ? "folderDeleted" : "fileDeleted",
                  {
                     multiple: true,
                     nodeRef: result.nodeRef
                  });
               }
            }
            
            // Activities, in Site mode only
            var successCount = successFolderCount + successFileCount;
            if (Alfresco.util.isValueSet(this.options.siteId))
            {
               var activityData;
               
               if (successCount > 0)
               {
                  if (successCount < this.options.groupActivitiesAt)
                  {
                     // Below cutoff for grouping Activities into one
                     for (i = 0; i < successCount; i++)
                     {
                        activityData =
                        {
                           fileName: data.json.results[i].id,
                           nodeRef: data.json.results[i].nodeRef,
                           path: this.currentPath,
                           parentNodeRef : this.doclistMetadata.parent.nodeRef
                        };
                        
                        if (data.json.results[i].type == "folder")
                        {
                           this.modules.actions.postActivity(this.options.siteId, "folder-deleted", "documentlibrary", activityData);
                        }
                        else
                        {
                           this.modules.actions.postActivity(this.options.siteId, "file-deleted", "documentlibrary", activityData);
                        }
                     }
                  }
                  else
                  {
                     if (successFileCount > 0)
                     {
                        // grouped into one message
                        activityData =
                        {
                           fileCount: successFileCount,
                           path: this.currentPath,
                           parentNodeRef : this.doclistMetadata.parent.nodeRef
                        };
                        this.modules.actions.postActivity(this.options.siteId, "files-deleted", "documentlibrary", activityData);
                     }
                     if (successFolderCount > 0)
                     {
                        // grouped into one message
                        activityData =
                        {
                           fileCount: successFolderCount,
                           path: this.currentPath,
                           parentNodeRef : this.doclistMetadata.parent.nodeRef
                        };
                        this.modules.actions.postActivity(this.options.siteId, "folders-deleted", "documentlibrary", activityData);
                     }
                  }
               }
            }

            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.multiple-delete.success", successCount)
            });
         };
         
         // Construct the data object for the genericAction call
         this.modules.actions.genericAction(
         {
            success:
            {
               callback:
               {
                  fn: fnSuccess,
                  scope: this,
                  obj: records
               }
            },
            failure:
            {
               message: this.msg("message.multiple-delete.failure")
            },
            webscript:
            {
               method: Alfresco.util.Ajax.DELETE,
               name: "files"
            },
            wait:
            {
               message: this.msg("message.multiple-delete.please-wait")
            },
            config:
            {
               requestContentType: Alfresco.util.Ajax.JSON,
               dataObj:
               {
                  nodeRefs: multipleRecords
               }
            }
         });
      },

      /**
       * Deselect currectly selected records.
       *
       * @method onActionDeselectAll
       */
      onActionDeselectAll: function DLTB_onActionDeselectAll()
      {
         if (this.modules.docList)
         {
            this.modules.docList.selectFiles("selectNone");
         }
      },

      /**
       * Show/Hide navigation bar button click handler
       *
       * @method onHideNavBar
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onHideNavBar: function DLTB_onHideNavBar(e, p_obj)
      {
         this.options.hideNavBar = !this.widgets.hideNavBar.get("checked");
         this.widgets.hideNavBar.set("title", this.msg(this.options.hideNavBar ? "button.navbar.show" : "button.navbar.hide"));
         Dom.setStyle(this.id + "-navBar", "display", this.options.hideNavBar ? "none" : "block");
         this.services.preferences.set(PREF_HIDE_NAVBAR, this.options.hideNavBar);
         if (e)
         {
            Event.preventDefault(e);
         }
      },

      /**
       * Folder Up Navigate button click handler
       *
       * @method onFolderUp
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onFolderUp: function DLTB_onFolderUp(e, p_obj)
      {
         var newPath = this.currentPath.substring(0, this.currentPath.lastIndexOf("/")),
            filter = this.currentFilter;
         
         filter.filterData = newPath;

         YAHOO.Bubbling.fire("changeFilter", filter);
         Event.preventDefault(e);
      },


      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR PAGE EVENTS
       * Disconnected event handlers for inter-component event notification
       */

      /**
       * Filter Changed event handler
       *
       * @method onFilterChanged
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onFilterChanged: function DLTB_onFilterChanged(layer, args)
      {
         this._handleSyncButtons();
         
         var obj = args[1];
         if (obj && (typeof obj.filterId !== "undefined"))
         {
            obj.filterOwner = obj.filterOwner || Alfresco.util.FilterManager.getOwner(obj.filterId);

            if (obj.filterOwner)
            {
               if (this.currentFilter.filterOwner != obj.filterOwner || this.currentFilter.filterId != obj.filterId)
               {
                  var filterOwner = obj.filterOwner.split(".")[1],
                     ownerIdClass = filterOwner + "_" + obj.filterId;

                  // Obtain array of DIVs we might want to hide
                  var divs = YAHOO.util.Selector.query('div.hideable', Dom.get(this.id)), div;
                  for (var i = 0, j = divs.length; i < j; i++)
                  {
                     div = divs[i];
                     if (Dom.hasClass(div, filterOwner) || Dom.hasClass(div, ownerIdClass))
                     {
                        Dom.removeClass(div, "toolbar-hidden");
                     }
                     else
                     {
                        Dom.addClass(div, "toolbar-hidden");
                     }
                  }
               }
            }
            
            Alfresco.logger.debug("DLTB_onFilterChanged", "Old Filter", this.currentFilter);
            this.currentFilter = Alfresco.util.cleanBubblingObject(obj);
            Alfresco.logger.debug("DLTB_onFilterChanged", "New Filter", this.currentFilter);
            
            if (this.currentFilter.filterId == "path" || this.currentFilter.filterId == "category")
            {
               this.currentPath = $combine("/", this.currentFilter.filterData);
               this._generateBreadcrumb();

               // Enable/disable the Folder Up button
               var paths = this.currentPath.split("/");
               // Check for root path special case
               if (this.currentPath === "/")
               {
                  paths = ["/"];
               }
               if (this.widgets.folderUp)
               {
                  this.widgets.folderUp.set("disabled", paths.length < 2);
               }
            }
            else
            {
               this._generateDescription();
            }
            this._generateRSSFeedUrl();
         }
      },

      /**
       * Helper method for handling the visibility of sync buttons
       */
      _handleSyncButtons: function DLTB__onHandleSyncButtons()
      {
         var syncToCloudButtonDiv = Dom.get(this.id + "-syncToCloud-button");
         var unsyncFromCloudButtonDiv = Dom.get(this.id + "-unsyncFromCloud-button");

         var parent = this.doclistMetadata.parent;

         if (parent)
         {
            var aspects = parent.aspects;
            if (aspects)
            {
               if (Alfresco.util.arrayContains(aspects, "sync:syncSetMemberNode"))
               {
                  Dom.removeClass(unsyncFromCloudButtonDiv, "hidden");
                  Dom.addClass(syncToCloudButtonDiv, "hidden");
               }
               else
               {
                  Dom.removeClass(syncToCloudButtonDiv, "hidden");
                  Dom.addClass(unsyncFromCloudButtonDiv, "hidden");
               }
            }

            var properties = parent.properties;
            if (properties && (properties["cm:name"] === "documentLibrary" || properties["sync:directSync"] === "false") || this.options.syncMode !== "ON_PREMISE")
            {
               Dom.addClass(unsyncFromCloudButtonDiv, "hidden");
               Dom.addClass(syncToCloudButtonDiv, "hidden");
            }
         }
      },
      
      /**
       * Deactivate All Controls event handler
       *
       * @method onDeactivateAllControls
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDeactivateAllControls: function DLTB_onDeactivateAllControls(layer, args)
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
       * Deactivate Dynamic Controls event handler.
       * Only deactivates those controls whose enabled state is evaluated on each update.
       *
       * @method onDeactivateDynamicControls
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDeactivateDynamicControls: function DLTB_onDeactivateDynamicControls(layer, args)
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
       * User Access event handler
       *
       * @method onUserAccess
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onUserAccess: function DLTB_onUserAccess(layer, args)
      {
         var fnSetWidgetAccess = function DLTB_onUserAccess_fnSetWidgetAccess(p_widget, p_userAccess)
         {
            var perms, widgetPermissions, orPermissions, permissionTokens, permission, orMatch, isMenuItem = false, fnEnable, fnDisable, shallMatch;
            if (p_widget instanceof YAHOO.widget.MenuItem && p_widget.element.firstChild)
            {
               isMenuItem = true;
               // MenuItems have to store permission values in the <a> tag's "rel" attribute
               perms = p_widget.element.firstChild.rel;
               fnEnable = Alfresco.util.bind(p_widget.cfg.setProperty, p_widget.cfg, "className", "");
               fnDisable = Alfresco.util.bind(p_widget.cfg.setProperty, p_widget.cfg, "className", "hidden");
            }
            else
            {
               // Buttons store the permission value in the "value" config variable
               perms = p_widget.get("value");
               fnEnable = Alfresco.util.bind(p_widget.set, p_widget, "disabled", false);
               fnDisable = Alfresco.util.bind(p_widget.set, p_widget, "disabled", true);
            }
            // Default to enabled: disabled via missing permission
            fnEnable();
            if (typeof perms == "string" && perms !== "")
            {
               // Comma-separation indicates "AND"
               widgetPermissions = perms.split(",");
               for (var i = 0, ii = widgetPermissions.length; i < ii; i++)
               {
                  // Pipe-separation is a special case and indicates an "OR" match. The matched permission is stored in "activePermission" on the widget.
                  if (widgetPermissions[i].indexOf("|") !== -1)
                  {
                     orMatch = false;
                     orPermissions = widgetPermissions[i].split("|");
                     for (var j = 0, jj = orPermissions.length; j < jj; j++)
                     {
                        permissionTokens = orPermissions[j].split(":");
                        permission = permissionTokens[0];
                        shallMatch = permissionTokens.length == 2 ? permissionTokens[1] == "true" : true;
                        if ((p_userAccess[permission] && shallMatch) || (!p_userAccess[permission] && !shallMatch))
                        {
                           orMatch = true;
                           if (!isMenuItem)
                           {
                              p_widget.set("activePermission", orPermissions[j], true);
                           }
                           break;
                        }
                     }
                     if (!orMatch)
                     {
                        fnDisable();
                        break;
                     }
                  }
                  else
                  {
                     permissionTokens = widgetPermissions[i].split(":");
                     permission = permissionTokens[0];
                     shallMatch = permissionTokens.length == 2 ? permissionTokens[1] == "true" : true;
                     if ((p_userAccess[permission] && !shallMatch) || (!p_userAccess[permission] && shallMatch))
                     {
                     fnDisable();
                     break;
                  }
               }
            }
            }
         };
         
         var obj = args[1];
         if (obj && obj.userAccess)
         {
            var widget, index, menuItems;
            for (index in this.widgets)
            {
               if (this.widgets.hasOwnProperty(index))
               {
                  widget = this.widgets[index];
                  // Skip if this action specifies "no-access-check"
                  if (widget && widget.get("srcelement").className != "no-access-check" && (!(widget._button != null && widget._button.className == "no-access-check")))					  				  				  
                  {
                     fnSetWidgetAccess(widget, obj.userAccess);
                     if (widget.getMenu() !== null)
                     {
                        menuItems = widget.getMenu().getItems();
                        for (var j = 0, jj = menuItems.length; j < jj; j++)
                        {
                           fnSetWidgetAccess(menuItems[j], obj.userAccess);
                        }
                     }
                  }
               }
            }
         }
      },

      /**
       * Selected Files Changed event handler.
       * Determines whether to enable or disable the multi-file action drop-down
       *
       * @method onSelectedFilesChanged
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onSelectedFilesChanged: function DLTB_onSelectedFilesChanged(layer, args)
      {
         if (this.modules.docList)
         {
            var files = this.modules.docList.getSelectedFiles(), fileTypes = [], file,
               fileType, userAccess = {}, fileAccess, index,
               menuItems = this.widgets.selectedItems == null ? null : this.widgets.selectedItems.getMenu().getItems(), menuItem,
               actionPermissions, typeGroups, typesSupported, disabled,
               commonAspects = [], allAspects = [],
               i, ii, j, jj;
            
            var fnFileType = function fnFileType(file)
            {
               return (file.node.isContainer ? "folder" : "document");
            };

            // Check each file for user permissions
            for (i = 0, ii = files.length; i < ii; i++)
            {
               file = files[i];
               
               // Required user access level - logical AND of each file's permissions
               fileAccess = file.node.permissions.user;
               for (index in fileAccess)
               {
                  if (fileAccess.hasOwnProperty(index))
                  {
                     userAccess[index] = (userAccess[index] === undefined ? fileAccess[index] : userAccess[index] && fileAccess[index]);
                  }
               }
               
               // Make a note of all selected file types Using a hybrid array/object so we can use both array.length and "x in object"
               fileType = fnFileType(file);
               if (!(fileType in fileTypes))
               {
                  fileTypes[fileType] = true;
                  fileTypes.push(fileType);
               }

               // Build a list of common aspects


               if (i === 0)
               {
                  // first time around fill with aspects from first node -
                  // NOTE copy so we don't remove aspects from file node.
                  commonAspects = Alfresco.util.deepCopy(file.node.aspects);
               } else
               {
                  // every time after that remove aspect if it isn't present on the current node.
                  for (j = 0, jj = commonAspects.length; j < jj; j++)
                  {
                     if (!Alfresco.util.arrayContains(file.node.aspects, commonAspects[j]))
                     {
                        Alfresco.util.arrayRemove(commonAspects, commonAspects[j])
                     }
                  }
               }

               // Build a list of all aspects
               for (j = 0, jj = file.node.aspects.length; j < jj; j++)
               {
                  if (!Alfresco.util.arrayContains(allAspects, file.node.aspects[j]))
                  {
                     allAspects.push(file.node.aspects[j])
                  }
               }

            }

            // Now go through the menu items, setting the disabled flag appropriately
            for (index in menuItems)
            {
               if (menuItems.hasOwnProperty(index))
               {
                  // Defaulting to enabled
                  menuItem = menuItems[index];
                  disabled = false;

                  if (menuItem.element.firstChild)
                  {
                     // Check permissions required - stored in "rel" attribute in the DOM
                     if (menuItem.element.firstChild.rel && menuItem.element.firstChild.rel !== "")
                     {
                        // Comma-separated indicates and "AND" match
                        actionPermissions = menuItem.element.firstChild.rel.split(",");
                        for (i = 0, ii = actionPermissions.length; i < ii; i++)
                        {
                           // Disable if the user doesn't have ALL the permissions
                           if (!userAccess[actionPermissions[i]])
                           {
                              disabled = true;
                              break;
                           }
                        }
                     }

                     // Check required aspects.
                     // Disable if any node DOES NOT have ALL required aspects
                     var hasAspects = Dom.getAttribute(menuItem.element.firstChild, "data-has-aspects");
                     if (hasAspects && hasAspects !== "")
                     {
                        hasAspects = hasAspects.split(",");
                        for (i = 0, ii = hasAspects.length; i < ii; i++)
                        {
                           if (!Alfresco.util.arrayContains(commonAspects, hasAspects[i]))
                           {
                              disabled = true;
                              break;
                           }
                        }
                     }

                     // Check forbidden aspects.
                     // Disable if any node DOES have ANY forbidden aspect
                     var notAspects = Dom.getAttribute(menuItem.element.firstChild, "data-not-aspects");
                     if (notAspects && notAspects !=="")
                     {
                        notAspects = notAspects.split(",");
                        for (i = 0, ii = notAspects.length; i < ii; i++)
                        {
                           if(Alfresco.util.arrayContains(allAspects, notAspects[i]))
                           {
                              disabled = true;
                              break;
                           }
                        }
                     }

                     if (!disabled)
                     {
                        // Check filetypes supported
                        if (menuItem.element.firstChild.type && menuItem.element.firstChild.type !== "")
                        {
                           // Pipe-separation indicates grouping of allowed file types
                           typeGroups = menuItem.element.firstChild.type.split("|");
                           
                           for (i = 0; i < typeGroups.length; i++) // Do not optimize - bounds updated within loop
                           {
                              typesSupported = Alfresco.util.arrayToObject(typeGroups[i].split(","));

                              for (j = 0, jj = fileTypes.length; j < jj; j++)
                              {
                                 if (!(fileTypes[j] in typesSupported))
                                 {
                                    typeGroups.splice(i, 1);
                                    --i;
                                    break;
                                 }
                              }
                           }
                           disabled = (typeGroups.length === 0);
                        }
                     }
                     menuItem.cfg.setProperty("disabled", disabled);
                  }
               }
            }
            if (this.widgets.selectedItems != null)
            {
               this.widgets.selectedItems.set("disabled", (files.length === 0));
            }
         }
      },

      /**
       * Document List Metadata event handler
       * NOTE: This is a temporary fix to enable access to the View Details action from the breadcrumb.
       *       A more complete solution is to present the full list of parent folder actions.
       *
       * @method onDoclistMetadata
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDoclistMetadata: function DLTB_onDoclistMetadata(layer, args)
      {
         var obj = args[1];
         this.folderDetailsUrl = null;
         if (obj && obj.metadata)
         {
            this.doclistMetadata = Alfresco.util.deepCopy(obj.metadata);
            if (obj.metadata.parent && obj.metadata.parent.nodeRef)
            {
               this.folderDetailsUrl = $siteURL("folder-details?nodeRef=" + obj.metadata.parent.nodeRef);
            }
         }
      },
      
      /**
       * Handles "dropTargetOwnerRequest" by determining whether or not the target belongs to the breacrumb
       * trail, and if it does determines it's path and uses it with the container nodeRef on the callback 
       * function.
       * 
       * @method onDropTargetOwnerRequest
       * @property layer The name of the event
       * @property args The event payload
       */
      onDropTargetOwnerRequest: function DLTB_onDropTargetOwnerRequest(layer, args)
      {
         if (args && args[1] && args[1].elementId)
         {
            var crumb = Dom.get(args[1].elementId);
            var trail = Dom.get(this.id + "-breadcrumb");
            if (Dom.isAncestor(trail, crumb))
            {
               // The current element is part of the breadcrumb trail. 
               // Calculate the path by working out its index within the breadcrumb trail
               // and then apply that to the path (remembering to compensate for the SPAN
               // elements that just contain the ">" separators !
               var targetPath = "";
               var paths = this.currentPath.split("/");
               for (var i = 0, j = trail.children.length; i < j; i++)
               {
                  if (i % 2 == 0)
                  {
                     // Only use the current index if it's even (odd indexes indicate
                     // the SPAN containing the ">" separator character...
                     targetPath = targetPath + "/" + paths[i/2];
                  }
                  
                  // If we've reached the target element then break out of the loop...
                  if (crumb == trail.children[i])
                  {
                     break;
                  }
               }
               
               // Use the callback method with a nodeRef built from the the container nodeRef
               // concatonated with the constructed path...
               var nodeRef = this.doclistMetadata.container + targetPath;
               args[1].callback.call(args[1].scope, nodeRef, targetPath);
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
            var crumb = Dom.get(args[1].elementId);
            var trail = Dom.get(this.id + "-breadcrumb");
            if (Dom.isAncestor(trail, crumb))
            {
               Dom.addClass(crumb, "documentDragOverHighlight");
               var firstCrumbChild = Dom.getFirstChild(crumb);
               if (firstCrumbChild != null && firstCrumbChild.tagName != "SPAN")
               {
                  var arrow = document.createElement("span");
                  Dom.addClass(arrow, "documentDragOverArrow");
                  Dom.insertBefore(arrow, firstCrumbChild);
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
            var crumb = Dom.get(args[1].elementId);
            var trail = Dom.get(this.id + "-breadcrumb");
            if (Dom.isAncestor(trail, crumb))
            {
               Dom.removeClass(crumb, "documentDragOverHighlight");
               var firstCrumbChild = Dom.getFirstChild(crumb);
               if (firstCrumbChild != null && firstCrumbChild.tagName == "SPAN")
               {
                  crumb.removeChild(firstCrumbChild);
               }
            }
         }
      },
      
      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Generates the HTML mark-up for the breadcrumb from the currentPath
       *
       * @method _generateBreadcrumb
       * @private
       */
      _generateBreadcrumb: function DLTB__generateBreadcrumb()
      {
         var divBC = Dom.get(this.id + "-breadcrumb");
         if (divBC === null)
         {
            return;
         }
         divBC.innerHTML = "";
         
         var paths = this.currentPath.split("/");
         // Check for root path special case
         if (this.currentPath === "/")
         {
            paths = ["/"];
         }
         // Clone the array and re-use the root node name from the DocListTree
         var me = this,
            displayPaths = paths.concat();
         
         displayPaths[0] = Alfresco.util.message("node.root", this.currentFilter.filterOwner);

         var fnCrumbIconClick = function DLTB__fnCrumbIconClick(e, path)
         {
            Dom.addClass(e.target.parentNode, "highlighted");
            Event.stopEvent(e);
         };

         var fnBreadcrumbClick = function DLTB__fnBreadcrumbClick(e, path)
         {
            var filter = me.currentFilter;
            filter.filterData = path;
            
            YAHOO.Bubbling.fire("changeFilter", filter);
            Event.stopEvent(e);
         };
         
         var eBreadcrumb = new Element(divBC),
            newPath,
            eCrumb,
            eIcon,
            eFolder;
         
         for (var i = 0, j = paths.length; i < j; ++i)
         {
            newPath = paths.slice(0, i+1).join("/");
            eCrumb = new Element(document.createElement("div"));
            eCrumb.addClass("crumb");
            eCrumb.addClass("documentDroppable"); // This class allows documents to be dropped onto the element
            eCrumb.addClass("documentDroppableHighlights"); // This class allows drag over/out events to be processed
            
            // First crumb doesn't get an icon
            if (i > 0)
            {
               eIcon = new Element(document.createElement("a"),
               {
                  href: "#",
                  innerHTML: "&nbsp;"
               });
               eIcon.on("click", fnBreadcrumbClick, newPath);
               eIcon.addClass("icon");
               eIcon.addClass("filter-" + $html(this.currentFilter.filterId));
               eCrumb.appendChild(eIcon);
            }

            // Last crumb is rendered as a link if folderDetailsUrl is available (via doclistMetadata)
            if (j - i < 2)
            {
               eFolder = new Element(document.createElement("span"),
               {
                  innerHTML: (this.folderDetailsUrl) ? '<a href="' + this.folderDetailsUrl + '">' + $html(displayPaths[i]) + '</a>' : $html(displayPaths[i])
               });
               eFolder.addClass("label");
               eCrumb.appendChild(eFolder);
               eBreadcrumb.appendChild(eCrumb);
            }
            else
            {
               eFolder = new Element(document.createElement("a"),
               {
                  href: "",
                  innerHTML: $html(displayPaths[i])
               });
               eFolder.addClass("folder");
               eFolder.on("click", fnBreadcrumbClick, newPath);
               eCrumb.appendChild(eFolder);
               eBreadcrumb.appendChild(eCrumb);
               eBreadcrumb.appendChild(new Element(document.createElement("div"),
               {
                  innerHTML: "&gt;",
                  className: "separator"
               }));
            }
         }
         
         var rootEl = Dom.get(this.id + "-breadcrumb");
         var dndTargets = Dom.getElementsByClassName("crumb", "div", rootEl);
         for (var i = 0, j = dndTargets.length; i < j; i++)
         {
            new YAHOO.util.DDTarget(dndTargets[i]);
         }
      },

      /**
       * Generates the HTML mark-up for the description from the currentFilter
       *
       * @method _generateDescription
       * @private
       */
      _generateDescription: function DLTB__generateDescription()
      {
         var divDesc, eDivDesc, eDescMsg, eDescMore, filterDisplay;
         
         divDesc = Dom.get(this.id + "-description");
         if (divDesc === null)
         {
            return;
         }
         
         while (divDesc.hasChildNodes())
         {
            divDesc.removeChild(divDesc.lastChild);
         }
         
         // If filterDisplay is provided, then use that instead (e.g. for cases where filterData is a nodeRef)
         filterDisplay = typeof this.currentFilter.filterDisplay !== "undefined" ? this.currentFilter.filterDisplay : (this.currentFilter.filterData || "");
         
         eDescMsg = new Element(document.createElement("div"),
         {
            innerHTML: this.msg("description." + this.currentFilter.filterId, filterDisplay)
         });
         eDescMsg.addClass("message");

         // If filterData is populated and a ".more.filterData" i18n message exists, then use that
         var i18n = "description." + this.currentFilter.filterId + ".more",
            i18nAlt = i18n + ".filterDisplay";
         
         if (filterDisplay !== "" && this.msg(i18nAlt) !== i18nAlt)
         {
            i18n = i18nAlt;
         }

         eDescMore = new Element(document.createElement("span"),
         {
            innerHTML: this.msg(i18n, $html(filterDisplay))
         });
         eDescMore.addClass("more");

         eDescMsg.appendChild(eDescMore);
         eDivDesc = new Element(divDesc);
         eDivDesc.appendChild(eDescMsg);
      },
      
      /**
       * @method _getRssFeedUrl
       * @private
       */
      _getRssFeedUrl: function DLTB__getRssFeedUrl()
      {
         var params = YAHOO.lang.substitute("{type}/site/{site}/{container}" + (this.currentPath !== "/" ? "{path}" : ""),
         {
            type: this.modules.docList.options.showFolders ? "all" : "documents",
            site: encodeURIComponent(this.options.siteId),
            container: encodeURIComponent(this.options.containerId),
	    path: Alfresco.util.encodeURIPath(this.currentPath)
         });

         params += "?filter=" + encodeURIComponent(this.currentFilter.filterId);
         if (this.currentFilter.filterData)
         {
            params += "&filterData=" + encodeURIComponent(this.currentFilter.filterData);
         }
         params += "&format=rss";
         
         return Alfresco.constants.URL_FEEDSERVICECONTEXT + "components/documentlibrary/feed/" + params;
      },
      
      /**
       * Generates the HTML mark-up for the RSS feed link
       *
       * @method _generateRSSFeedUrl
       * @private
       */
      _generateRSSFeedUrl: function DLTB__generateRSSFeedUrl()
      {
         if (this.widgets.rssFeed && this.modules.docList)
         {
            var href = this._getRssFeedUrl();
            this.widgets.rssFeed.set("href", href);
            Alfresco.util.enableYUIButton(this.widgets.rssFeed);
         }
      }
   }, true);
})();