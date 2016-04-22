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
 * Alfresco webditor plugin
 * @module AWE
 *  
 */
(function() 
{
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event,
       KeyListener = YAHOO.util.KeyListener,
       Selector = YAHOO.util.Selector,
       Bubbling = YAHOO.Bubbling,
       Cookie = YAHOO.util.Cookie,
       WebEditor = YAHOO.org.springframework.extensions.webeditor;

   var markerSpan;

   YAHOO.namespace('org.alfresco.awe.app');

   /**
    * Alfresco webeditor plugin constructor
    * @constructor
    * @class AWE.app
    * @namespace YAHOO.org.alfresco
    * @extends WEF.App
    */
   YAHOO.org.alfresco.awe.app = function()
   {
      YAHOO.org.alfresco.awe.app.superclass.constructor.apply(this, Array.prototype.slice.call(arguments));
   };

   YAHOO.extend(YAHOO.org.alfresco.awe.app, WEF.App,
   {
      init: function AWE_App_init()
      {
         YAHOO.org.alfresco.awe.app.superclass.init.apply(this);

         YAHOO.Bubbling.unsubscribe = function(layer, handler, scope)
         {
            this.bubble[layer].unsubscribe(handler, scope);
         };

         // handle events
         // edit content icon event
         Bubbling.on(YAHOO.org.alfresco.awe.app.AWE_EDIT_CONTENT_CLICK_EVENT, function onEditContent_click(e, args) 
         {
	    if (args[1].hasWritePermission == true)
	    {
               this.loadEditForm(args[1]);
	    }
	    else
	    {
               this.showAccessDeniedDialog(args[1].title);
	    }
         }, this);
         // create content icon event
         Bubbling.on(YAHOO.org.alfresco.awe.app.AWE_NEW_CONTENT_CLICK_EVENT, function onNewContent_click(e, args) 
         {
            if (args[1].hasWritePermission == true)
            {
               this.loadCreateForm(args[1]);
            }
            else
            {
               this.showAccessDeniedDialog(args[1].title);
            }
         }, this);
         // delete content icon event
         Bubbling.on(YAHOO.org.alfresco.awe.app.AWE_DELETE_CONTENT_CLICK_EVENT, function onDeleteContent_click(e, args) 
         {
            if (args[1].hasDeletePermission == true)
            {
               this.confirmDeleteNode(args[1]);
            }
            else
            {
               this.showAccessDeniedDialog(Alfresco.util.message.call(this, 'title.confirm.delete'));
            }
         }, this);

         // login/logoff
         Bubbling.on('awe' + WEF.SEPARATOR + 'loggedIn', this.onLoggedIn, this, true);
         Bubbling.on('awe' + WEF.SEPARATOR + 'loggedout', this.onLoggedOut, this, true);
         Bubbling.on('awe' + WEF.SEPARATOR + 'loginCancelled', this.loginCancelled, this, true);

         Bubbling.on(this.config.name + WebEditor.SEPARATOR + 'quickcreateClick', this.onQuickCreateClick, this, true);
         Bubbling.on(this.config.name + WebEditor.SEPARATOR + 'quickeditClick', this.onQuickEditClick, this, true);
         Bubbling.on(this.config.name + WebEditor.SEPARATOR + 'quickdeleteClick', this.onQuickDeleteClick, this, true);
         Bubbling.on(this.config.name + WebEditor.SEPARATOR + 'show-hide-edit-markersClick', this.onShowHideClick, this, true);

         //Bubbling.on(this.config.name + WebEditor.SEPARATOR + 'loggedOutClick', this.onLogoutClick, this, true);
         Bubbling.on(this.config.name + WebEditor.SEPARATOR + 'loginToggleClick', this.onLoginToggleClick, this, true);

         // Hover States
         var els = [], 
         suffix = "-over",
         ext = ".png";
         
         els.push(Dom.getElementsByClassName("alfresco-content-delete")); 
         els.push(Dom.getElementsByClassName("alfresco-content-new"));
         els.push(Dom.getElementsByClassName("alfresco-content-edit"));
         
         Event.addListener(els, "mouseover", function()
         {
            var el = Dom.getChildren(this)[0];
            Dom.setAttribute(el, "src", Dom.getAttribute(el, "src").replace(ext, suffix+ext));
         });
         Event.addListener(els, "mouseout", function()
         {
            var el = Dom.getChildren(this)[0];
            Dom.setAttribute(el, "src", Dom.getAttribute(el, "src").replace(suffix+ext, ext));
         });

         this.initAttributes(this.config);
         this.initEditor();

         // A Dynamically loaded TinyMCE instance needs this setting:
         if (tinyMCE)
         {
            tinyMCE.dom.Event.domLoaded = true;
         }
         
         return this;
      },
      
      setLoggedInStatus: function AWE_setLoggedInStatus(loggedIn)
      {
         this.set('loggedInStatus', loggedIn);
      },
      
      isLoggedIn: function AWE_isLoggedIn()
      {
         return this.get('loggedInStatus');
      },
      
      initEditor: function AWE_App_initEditor()
      {
         this.getNodeInfo();
      },

      deleteNode: function AWE_App_deleteNode(editable)
      {
         var storeType = null;
         var storeId = null;
         var uuid = null;

         var remainder = null;
         var nodeRef = editable.nodeRef;

         // TODO could use a reg exp here
         var storeTypeIdx = nodeRef.indexOf('://');
	     storeType = nodeRef.substring(0, storeTypeIdx);
         remainder = nodeRef.substring(storeTypeIdx + 3);

	     var storeIdIdx = remainder.indexOf('/');
	     storeId = remainder.substring(0, storeIdIdx);
         uuid = remainder.substring(storeIdIdx + 1);

         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + 'slingshot/doclib/action/file/node/' + storeType + '/' + storeId + '/' + uuid,
            method: Alfresco.util.Ajax.DELETE,
            noReloadOnAuthFailure: true,
            dataObj:
            {
            },
            redirectUrl: editable.redirectUrl,
            successCallback:
            {
               fn: this.onNodeDeleted,
               scope: this
            },
            object:
            {
               editable: editable
            },
            failureCallback:
            {
               fn: function AWE_fn(args)
               {
                  if (args.serverResponse.status == 401)
                  {
                     this.login({
                        fn: function retryDeleteNode()
                        {
                           this.deleteNode(editable);
                        },
                        scope: this
                     });
                  }
                  else
                  {
                     this.handleAJAXErrors(args);
                  }
               },
               scope: this
            },
            execScripts: true
         });      
      },
      
      handleAJAXErrors: function AWE_App_handleAJAXErrors(args)
      {
         Alfresco.util.PopupManager.displayPrompt({
            title: Alfresco.util.message("message.error.title", this.name),
            text: args.serverResponse.message || Alfresco.util.message('message.error.fatal')
         }, Dom.get('wef'));
      },
      
      onNodeDeleted: function AWE_App_onNodeDelete(args)
      {
         var nodeDeletedMessage = Alfresco.util.PopupManager.displayMessage({
                  text: this.getMessage('message.confirm.nodeDeleted'),
                  spanClass: "wait",
                  effect: null
                  });
         document.location.replace(args.config.redirectUrl);
      },
      
     showAccessDeniedDialog: function AWE_App_showAccessDeniedDialog(title)
     {
       var configDialog = Alfresco.util.PopupManager.displayPrompt(
         {
            title: title,
            text: this.getMessage('message.do-not-have-permission'),
            modal: true,
            spanClass: "wait",
            displayTime: 0,
            buttons: [
            {
               text: this.getMessage("button.ok"),
               handler: function AWE_Ok()
               {
               this.hide();
                  this.destroy();
               }
            }
            ]
         }, Dom.get('wef'));     
     },
      
      confirmDeleteNode: function AWE_App_confirmDeleteNode(editable)
      {
         if(this.isLoggedIn() === false)
         {
            return;
         }

         var title = Alfresco.util.message.call(this, 'title.confirm.delete');
         var msg = Alfresco.util.message.call(this, 'message.confirm.delete', '', editable.title.replace("Edit ",""));
	     var me = this;
         var configDialog = Alfresco.util.PopupManager.displayPrompt(
         {
            title: title,
            text: msg,
            modal: true,
            spanClass: "wait",
            displayTime: 0,
            buttons: [
            {
               text: this.getMessage("button.ok"),
               handler: function AWE_deleteNode_Ok()
               {
                  this.destroy();
                  me.deleteNode(editable);
               }
            },
            {
               text: this.getMessage("button.cancel"),
               handler: function AWE_deleteNode_cancel()
               {
                  this.hide();
                  this.destroy();
               }
            }]
         }, Dom.get('wef'));
      },

      getNodeInfo: function AWE_App_getNodeInfo()
      {
         var editables = this.get('editables');
         if(editables != null && editables.length > 0)
	     {
	        return;
	     }

         // get node info for each marked content node
         var nodeRefs = [];
         for (var i = 0; i < this.config.editables.length; i++) 
         {
            var config = this.config.editables[i];
            nodeRefs.push(config.nodeRef);
         }

         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + 'api/bulkmetadata',
            method: Alfresco.util.Ajax.POST,
            responseContentType: Alfresco.util.Ajax.JSON,
            requestContentType: Alfresco.util.Ajax.JSON,
            noReloadOnAuthFailure: true,
            dataObj:
            {
               nodeRefs: nodeRefs
            },
            successCallback:
            {
               fn: function successCallback(args)
               {
                  this.onNodesLoaded(args);

                  this.render();
                  this.showControls();
               },
               scope: this
            },
            object:
            {
               config: this.config
            },
            failureCallback: 
            {
               fn: function handleErrors(args)
               {
                  if (args.serverResponse.status == 401)
                  {
                     this.login({
                        fn: this.getNodeInfo,
                        scope: this
                     });
                  }
                  else
                  {
                     this.handleAJAXErrors(args);
                  }
               },
               scope: this
            },
            execScripts: true
         });
      },

      onNodesLoaded: function AWE_App_nodesLoaded(o)
      {
         var config = o.config.object.config;
         var nodes = o.json.nodes;
         this.registerEditableContent(config.editables, nodes);
         this.setLoggedInStatus(true);
      },

      initAttributes : function AWE_App_initAttributes(attr)
      {
         this.setAttributeConfig('editables',
         {
            value: [],
            validator: YAHOO.lang.isObject 
         });
         
         this.setAttributeConfig('loggedInStatus',
         {
            value: (WEF.getCookieValue(this.config.name, 'loggedInStatus') == 'true'),
            validator: YAHOO.lang.isBoolean
         });

         this.on('loggedInStatusChange', this.onLoginStatusChangeEvent);
      },
      
      /*
       * Renders the editor. Hence, the checks for existence of elements in the
       * DOM and creation of these elements if they don't exist.
       *
       */
      render: function AWE_render()
      {
         // innerHTML causes issues with rendering so use DOM
	     var wefEl = Dom.get('wef');
	     var div = Dom.get('wef-login-panel');
	     if(div == null)
	     {
	        div = document.createElement('div');
	        div.id = 'wef-login-panel';
	        wefEl.appendChild(div);
	     }

         div = Dom.get('wef-panel');
         if(div == null)
         {
            div = document.createElement('div');
            div.id = 'wef-panel';
            wefEl.appendChild(div);
         }

         // get the current context path
         var contextPath = WEF.get("contextPath");

         var primaryToolBar = WebEditor.module.Ribbon.getToolbar('WEF-'+WebEditor.ui.Ribbon.PRIMARY_TOOLBAR+'-root');

         if(primaryToolBar == null)
         {
            primaryToolBar = WebEditor.module.Ribbon.addToolbar('WEF-'+WebEditor.ui.Ribbon.PRIMARY_TOOLBAR+'-root',
            {
               id: 'WEF-'+WebEditor.ui.Ribbon.PRIMARY_TOOLBAR+'-root',
               name: 'WEF-'+WebEditor.ui.Ribbon.PRIMARY_TOOLBAR+'-root',
               label: '<img src="' + contextPath + '/res/awe/images/edit.png" alt="'+ this.getMessage('awe.toolbar-tab-label') +'" />',
               title: this.getMessage('awe.toolbar-tab-label'),
               content: '',
               active: true,
               pluginOwner:this
            }, WebEditor.ui.Toolbar);
         }

         var editables = this.get('editables');
         if(editables == null)
         {
            editables = [];
         }

         var loggedIn = this.isLoggedIn();

         if (loggedIn)
         {

            // If primary toolbar doesn't have buttons then create required buttons
            // otherwise make buttons enabled.
            if (!this.hasToolbarButtons(primaryToolBar) && editables.length != 0)
            {
               primaryToolBar.addButtons(
               [
                  {
                     type: 'menu',
                     label: '<img src="' + contextPath + '/res/awe/images/quick-new.png" alt="'+ this.getMessage('awe.toolbar-quick-new-icon-label') +'" />',
                     title: this.getMessage('awe.toolbar-quick-new-icon-label'),
                     value: this.config.name + WebEditor.SEPARATOR + 'quickcreate',
                     id: this.config.name + WebEditor.SEPARATOR + 'quickcreate',
                     icon: true,
                     disabled: !loggedIn,
                     menu: this.renderCreateContentMenu(editables)
                  },
                  {
                     type: 'menu',
                     label: '<img src="' + contextPath + '/res/awe/images/quick-edit.png" alt="'+ this.getMessage('awe.toolbar-quick-edit-icon-label') +'" />',
                     title: this.getMessage('awe.toolbar-quick-edit-icon-label'),
                     value: this.config.name + WebEditor.SEPARATOR + 'quickedit',
                     id: this.config.name + WebEditor.SEPARATOR + 'quickedit',
                     icon: true,
                     disabled: !loggedIn,
                     menu: this.renderEditableContentMenu(editables)
                  },
                  {
                     type: 'push',
                     label: '<img src="' + contextPath + '/res/awe/images/toggle-edit-off.png" alt="'+ this.getMessage('awe.toolbar-toggle-markers-icon-label') +'" />',
                     title: this.getMessage('awe.toolbar-toggle-markers-icon-label'),
                     value: this.config.name + WebEditor.SEPARATOR + 'show-hide-edit-markers',
                     disabled: !loggedIn,
                     id: this.config.name + WebEditor.SEPARATOR + 'show-hide-edit-markers',
                     icon: true
                  }
               ]);
            } 
            else 
            {
               this.toggleToolbar(true);
            }
         }

         var quickeditMenuButton = primaryToolBar.getButtonById(this.config.name + WebEditor.SEPARATOR + 'quickedit').getMenu();

         if (quickeditMenuButton != null)
         {
           quickeditMenuButton.subscribe('mouseover', this.onQuickEditMouseOver, this, true); 
         }

         // set up toolbar as a managed attribute so it can be exposed to other plugins
         if(this.setAttributeConfig('toolbar') == null)
         {
            this.setAttributeConfig('toolbar',
            {
               value: primaryToolBar
            });
         }

         var secondaryToolBar = WebEditor.module.Ribbon.getToolbar(WebEditor.ui.Ribbon.SECONDARY_TOOLBAR);

         // assume secondary toolbar exists, it is created by WEF
         if(secondaryToolBar.getButtonById(this.config.name + WebEditor.SEPARATOR + 'loginToggle') == null)
         {
            secondaryToolBar.addButtons(
            [ 
               {
                  type: 'push',
                  label: loggedIn ? this.getMessage('awe.toolbar-logout-label') : this.getMessage('awe.toolbar-login-label'),
                  title: loggedIn ? this.getMessage('awe.toolbar-logout-label') : this.getMessage('awe.toolbar-login-label'),
                  value: 'loginToggle',
                  id: this.config.name + WebEditor.SEPARATOR + 'loginToggle',
                  icon: true
               }
            ]);
         }
      },

      /**
       * Checks has toolbar any buttons or not
       *
       * @method hasToolbarButtons
       *
       * @param toolBar to check for buttons
       * @return {Boolean} true if ttoolBar has butons, otherwise - false.
       *
       */
      hasToolbarButtons: function AWE_hasToolbarButtons(toolBar)
      {
         return toolBar.widgets.buttons.length > 0;
      },

      renderCreateContentMenu: function AWE_renderCreateContentMenu(editables)
      {
         // determine set of Alfresco types from the editable content items on the page
         var types = {};
	     var menuConfig = [];
	     for (var i = 0; i < editables.length; i++) 
	     {
	        var editable = editables[i].config;
                if (editable.hasWritePermission == false)
                {
                    continue;
                }
	        if(types[editable.type] == null)
	        {
		       var o = Alfresco.util.deepCopy(editable);
		       types[editable.type] = o;
            }
	     }

         // create a menu item for each distinct Alfresco type
         for(var t in types)
	     {
            var type = types[t];
            menuConfig.push({
               text: Alfresco.util.message.call(this, 'message.create', '', type.typeTitle),
               value: type
            });
         }

         return menuConfig;
      },

      renderEditableContentMenu: function AWE_renderEditableContentMenu(editables)
      {
         // construct a menu item for each editable item on the page
         var menuConfig = [];
         for (var i = 0; i < editables.length; i++) 
         {
            var editable = editables[i].config, modifiedTitle = editable.title.replace("Edit ","");
	    if (editable.hasWritePermission == false)
            {
               continue;
            }
            menuConfig.push(
            {
	           text: Alfresco.util.message.call(this, 'message.edit', '', Alfresco.util.encodeHTML(modifiedTitle)),
               value: editable
            });
         }
         return menuConfig;
      },

      renderDeleteContentMenu: function AWE_renderDeleteContentMenu(editables)
      {
         // construct a menu item for each editable item on the page
         var menuConfig = [];
         for (var i = 0; i < editables.length; i++) 
         {
            var editable = editables[i].config, modifiedTitle = editable.title.replace("Edit ","");
            menuConfig.push(
            {
               text: Alfresco.util.message.call(this, 'message.delete', '', Alfresco.util.encodeHTML(modifiedTitle)),
               value: editable
            });
         }
         return menuConfig;
      },

      /**
       * Initaliases login module
       *
       * @method login
       *
       * @param o {Object} Callback object to pass to login module
       * @return {Object} Login module object
       *
       */
      login: function AWE_login(o)
      {
         if (YAHOO.lang.isUndefined(this.widgets.loginModule))
         {
            this.widgets.loginModule = new YAHOO.org.alfresco.awe.ui.LoginPanel('wef-login-panel').setOptions(
            {
               templateUrl : Alfresco.constants.URL_SERVICECONTEXT + "modules/login/login",
               destroyPanelOnHide: false
            });
         }
         this.widgets.loginModule.show(o);
      },

      /**
       * Loads an edit form
       *
       * @method loadEditForm
       * @param o {object} Config object; must have an dom element id and a nodeRef properties
       *                   e.g 
       *                   {
       *                      id: 'elementId' // Id of content element,
       *                      nodeRef: '..'   // NodeRef of content
       *                   }
       */
      loadEditForm : function AWE_loadEditForm(o)
      {
         if(this.isLoggedIn() === false)
         {
            return;
         }
         
         // formId is optional so use appropriate substitute string
         var formUri = null;
         if (o.formId)
         {
            formUri = YAHOO.lang.substitute(WEF.get("contextPath") + '/service/components/form?itemKind=node&itemId={nodeRef}&formId={formId}&nodeRef={nodeRef}&redirect={redirectUrl}',o);
         }
         else
         {
            formUri = YAHOO.lang.substitute(WEF.get("contextPath") + '/service/components/form?itemKind=node&itemId={nodeRef}&nodeRef={nodeRef}&redirect={redirectUrl}',o);
         }

         this.module.getFormPanelInstance('wef-panel').setOptions(
         {
            formName: 'wefPanel',
            formId: o.formId,
            formUri: formUri,
            nodeRef: o.nodeRef,
            domContentId: o.id,
            title: Alfresco.util.message.call(this, 'message.edit', '', Alfresco.util.encodeHTML(o.title, false)),
            nested: o.nested,
            redirectUrl: o.redirectUrl
         }).show();
      },

      /**
       * Loads a create form
       *
       * @method loadCreateForm
       * @param o {object} Config object; must have an dom element id and a nodeRef properties
       *                   e.g 
       *                   {
       *                      id: 'elementId' // Id of content element,
       *                      nodeRef: '..'   // NodeRef of content
       *                   }
       */
      loadCreateForm : function AWE_loadCreateForm(o)
      {
         if(this.isLoggedIn() === false)
         {
            return;
         }

         // formId is optional so use appropriate substitute string
         var formUri = null;
         if (o.formId)
         {
            formUri = YAHOO.lang.substitute(WEF.get("contextPath") + '/service/components/form?mode=create&mimeType=text/html&itemKind=type&itemId={shortType}&formId={formId}&nodeRef={nodeRef}&redirect={redirectUrl}&destination={parentNodeRef}',o);
         }
         else
         {
            formUri = YAHOO.lang.substitute(WEF.get("contextPath") + '/service/components/form?mode=create&mimeType=text/html&itemKind=type&itemId={shortType}&nodeRef={nodeRef}&redirect={redirectUrl}&destination={parentNodeRef}',o);
         }

         this.module.getFormPanelInstance('wef-panel').setOptions(
         {
            formName: 'wefPanel',
	        formId: o.formId,
	        formUri: encodeURI(formUri),
            mimeType: 'text/html',
            parentNodeRef: o.parentNodeRef,
            domContentId: o.id,
            title: Alfresco.util.message.call(this, 'message.create', '', o.typeTitle),
            nested: o.nested,
            redirectUrl: o.redirectUrl
         }).show();
      },

      /**
       * Registers editable content on page. Adds click events to load form.
       *
       * @method registerEditableContent
       *
       */
      registerEditableContent : function AWE_registerEditableContent(configs, nodes)
      {
         var editables = [];
         for (var i=0,len = nodes.length; i<len; i++)
         {
            var config = configs[i];
            var node = nodes[i];
            var id = config.id;
            markerSpan = Dom.get(id);

            if(node.error)
            {
               if(node.errorCode == 'invalidNodeRef')
               {
                  // ignore invalid nodes
                  Dom.setStyle(markerSpan, 'display', 'none');
               }

               // TODO better handling here
               continue;
            }

            // make a copy of the returned node information and add
            // config properties
            var editableConfig = Alfresco.util.deepCopy(node);
            editableConfig.id = id;
            editableConfig.nested = config.nested;
            editableConfig.redirectUrl = config.redirectUrl;
            editableConfig.formId = config.formId;
            
            // construct the title. The title from the markup takes precedence,
            // then the node title, then the node name.
            if(config.title && config.title != '')
            {
               editableConfig.title = config.title;
            }
            else if(!editableConfig.title)
            {
               editableConfig.title = editableConfig.name;
            }

            editables.push({
               config: editableConfig
            });

            // add title tooltip and click event handler for edit content icon
            var elem = Selector.query('a.alfresco-content-edit', markerSpan, true);
            if (elem)
            {
               var imgElem = Selector.query('img', elem, true);
               if(imgElem)
               {
                  imgElem.setAttribute("title", Alfresco.util.message.call(this, 'message.edit', '', Alfresco.util.encodeHTML(editableConfig.title))); 
               }

               Event.addListener(elem, 'click', function AWE_EDIT_CONTENT_CLICK_EVENT(e, o)
               {
                  Event.preventDefault(e);
                  Bubbling.fire(YAHOO.org.alfresco.awe.app.AWE_EDIT_CONTENT_CLICK_EVENT, o);
               },
               editableConfig);
            }

            // add title tooltip and click event handler for create content icon
            var newElem = Selector.query('a.alfresco-content-new', markerSpan, true);
            if (newElem)
            {
               var imgElem = Selector.query('img', newElem, true);
               if(imgElem)
               {
                  imgElem.setAttribute("title", Alfresco.util.message.call(this, 'message.create', '', editableConfig.typeTitle)); 
               }

               Event.addListener(newElem, 'click', function AWE_NEW_CONTENT_CLICK_EVENT(e, o)
               {
                  Event.preventDefault(e);
                  Bubbling.fire(YAHOO.org.alfresco.awe.app.AWE_NEW_CONTENT_CLICK_EVENT, o);
               },
               editableConfig);
            }

            // add title tooltip and click event handler for delete content icon
            var deleteElem = Selector.query('a.alfresco-content-delete', markerSpan, true);
            if (deleteElem)
            {
               var imgElem = Selector.query('img', deleteElem, true);
               if(imgElem)
               {
                  imgElem.setAttribute("title", Alfresco.util.message.call(this, 'message.delete', '', editableConfig.title)); 
               }

               Event.addListener(deleteElem, 'click', function AWE_DELETE_CONTENT_CLICK_EVENT(e, o)
               {
                  Event.preventDefault(e);
                  Bubbling.fire(YAHOO.org.alfresco.awe.app.AWE_DELETE_CONTENT_CLICK_EVENT, o);
               },
               editableConfig);
            }
         }
         this.set('editables', editables);
      },

      getEditableContentMarkers : function AWE_getEditableContentMarkers()
      {
         return this.get('editables');
      },

      /**
       * Event handler that fires when user logs in
       *
       * @param e {Object} Object literal describing previous and new value of
       *                   attribute
       * @param args {Object} Args passed into event
       *
       */
      onLoggedIn: function AWE_onLoggedIn(e, args)
      {
         this.setLoggedInStatus(true);
      },
      
      setLoggedIn: function AWE_setLoggedIn(loggedIn)
      {
         if(loggedIn)
         {
            this.showControls();
            this.toggleToolbar(true);
         }
         else
         {
            this.hideControls();
            this.toggleToolbar(false);
         }
      },

      /**
       * Event handler that fires when user logs out
       *
       * @param e {Object} Object literal describing previous and new value of
       *                   attribute
       * @param args {Object} Args passed into event
       *
       */
      onLoggedOut: function AWE_onLoggedOut(e, args)
      {
         this.setLoggedInStatus(false);
         this.set('editables', []);
         this.setLoggedIn(false);
      },

      /**
       * Event handler that enables or disables button on primary toolbar.
       *
       * @param e {Boolean} 'true' for enabling buttons,
       *                   'false' for disabling buttons
       *
       */
      toggleToolbar: function AWE_toggleToolbar(enabled)
      {
         var primaryToolBarButtons = this.getAttributeConfig('toolbar').value.widgets.buttons;

         for (var button = 0; button < primaryToolBarButtons.length; button++ )
         {
            primaryToolBarButtons[button].set('disabled', !enabled);
         }
      },
      
      loginCancelled: function AWE_loginCancelled()
      {
         // TODO currently not used - need to hook into login dialog cancel
         // functionality (which does not appear to exist at present)
         this.setLoggedInStatus(false);
         this.hideControls();
         this.toggleToolbar(false);
      },

      onRenderedStatusChangeEvent: function AWE_onRenderedStatusChangeEvent(e)
      {
         if(e.newValue === true)
         {
            this.showControls();
            this.toggleToolbar(true);
         }
      },

      /*
       * Handler for when login status changes. Enables/disables the logout btn.
       *
       * @param e {Object} Object literal describing previous and new value of
       *                   attribute
       */
      onLoginStatusChangeEvent: function AWE_onLoginStatusChangeEvent(e)
      {
         var btn = WebEditor.module.Ribbon.getToolbar(WebEditor.ui.Ribbon.SECONDARY_TOOLBAR).getButtonById(
            this.config.name + WebEditor.SEPARATOR + 'loginToggle');
         if (e.newValue === true) 
         {
            btn.set('title', this.getMessage("awe.toolbar-logout-label"));
            btn.set('label', this.getMessage('awe.toolbar-logout-label'));
         }
         else 
         {
            btn.set('title', this.getMessage("awe.toolbar-login-label"));
            btn.set('label', this.getMessage('awe.toolbar-login-label'));
         }

         if (e.prevValue !== e.newValue)
         {
            WEF.setCookieValue(this.config.name,'loggedInStatus', e.newValue);
         }
      },

      onQuickCreateClick: function AWE_onQuickCreateClick(e, args)
      {
        this.loadCreateForm(args[1]);
      },
      
      onQuickEditClick: function AWE_onQuickEditClick(e, args)
      {
        this.loadEditForm(args[1]);
      },

      onQuickDeleteClick: function AWE_onQuickDeleteClick(e, args)
      {
        this.confirmDeleteNode(args[1]);
      },

      showControls: function AWE_showControls()
      {
         var editMarkers = Selector.query('span.alfresco-content-marker');

         Dom.setStyle(editMarkers, 'opacity', '1');
         Dom.setStyle(editMarkers, 'display', 'inline');
         this.onShowHideClick.isHidden = false;
      },

      hideControls: function AWE_hideControls()
      {
         var editMarkers = Selector.query('span.alfresco-content-marker');
         Dom.setStyle(editMarkers, 'display', 'none');

         this.onShowHideClick.isHidden = true;
      },
      
      onShowHideClick: function AWE_onShowHideClick(e, args)
      {
         if(this.isLoggedIn() === true)
         {
            var butImg = Dom.get(args[1]+'-button').getElementsByTagName('img')[0];
    
            this.onShowHideClick.isHidden = this.onShowHideClick.isHidden || false;
    
            if (this.onShowHideClick.isHidden)
            {
               this.showControls();
               butImg.src = butImg.src.replace('-on.png','-off.png');
            }
            else
            {
               this.hideControls();
               butImg.src = butImg.src.replace('-off.png','-on.png');
            }
         }
      },

      doLogout: function AWE_doLogout()
      {
         var ribbonObj = this;
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: Alfresco.util.message('message.logout-confirmation-title','org.alfresco.awe.ui.LoginPanel'),
            text: Alfresco.util.message('message.logout-confirmation','org.alfresco.awe.ui.LoginPanel'),
            buttons: 
            [
               {
                  text: 'OK',
                  handler: function()
                  {
                     var config = 
                     {
                        url: WEF.get("contextPath") + '/page/dologout',
                        method: "POST",
                        noReloadOnAuthFailure: true,
                        successCallback: 
                        {
                           fn: function logoutSuccess(e)
                           {
                              Bubbling.fire('awe' + WEF.SEPARATOR + 'loggedout', ribbonObj);
                              var elem = Selector.query('a.alfresco-content-edit', markerSpan, true);
                              Event.removeListener(elem, 'click');
                              var newElem = Selector.query('a.alfresco-content-new', markerSpan, true);
                              Event.removeListener(newElem, 'click');
                              var deleteElem = Selector.query('a.alfresco-content-delete', markerSpan, true);
                              Event.removeListener(deleteElem, 'click');
                              
                              // Removing primary toolbar buttons.
                              var primaryToolBar = WebEditor.module.Ribbon.getToolbar('WEF-'+WebEditor.ui.Ribbon.PRIMARY_TOOLBAR+'-root');
                              primaryToolBar.widgets.buttons = [];
                              var node = primaryToolBar.widgets.buttonContainer;
                              while (node.hasChildNodes())
                              {
                                 node.removeChild(node.firstChild);
                              }
                              this.hide();
                              this.destroy();
                           },
                           scope: this
                        },
                        failureCallback: 
                        {
                           fn: function logoutFailure(e)
                           {
                              if (e.serverResponse.status == 401)
                              {
                                 // MNT-13085 fix, 401 status code is expected status code for logout operation, call success handler instead
                                 var callback = e.config.successCallback;
                                 callback.fn.call((typeof callback.scope == "object" ? callback.scope : this),
                                 {
                                    config: e.config,
                                    serverResponse: e.serverResponse
                                 }, callback.obj);
                                 return;
                              }
                              this.hide();
                              this.destroy();
                           },
                           scope: this
                        }
                     };
                     Alfresco.util.Ajax.request(config);
                  }
               },
               {
                  text: 'Cancel',
                  handler: function()
                  {
                     this.hide();
                     this.destroy();
                  }
               }
            ]
         },
         Dom.get('wef'));
      },
      
      onLoginToggleClick: function AWE_onLoginToggleClick(e, args)
      {
         if(this.isLoggedIn() === false)
         {
            this.initEditor();
         }
         else
         {
            this.doLogout();
         }
      },
      
      onHelp: function AWE_onHelp()
      {
         window.open('http://docs.alfresco.com/3.4/topic/com.alfresco.Enterprise_3_4_0.doc/concepts/awe-introduction.html','wefhelp');
      },

      onQuickEditMouseOver: function AWE_onQuickEditMouseOver(e, args)
      {
         if (args.length>0)
         {
            var targetContentEl = (args[1].value.nested) ? Dom.get(args[1].value.id).parentNode : Dom.get(args[1].value.id),
                targetContentElRegion = Dom.getRegion(targetContentEl),
                defaultColor = this.getMessage('awe.bgColor'),
                hoverColor =  this.getMessage('awe.highlightColor'),
                fadeIn = function fade(el)
                {
                   var anim = new YAHOO.util.ColorAnim(el,
                   {
                      backgroundColor: 
                      {
                         from: defaultColor,
                         to: hoverColor,
                         duration: '0.5'
                      }
                   });
                   anim.onComplete.subscribe(function(el)
                   {
                      return function()
                      {
                         fadeOut(el);
                      };
                   }(el));
                   anim.animate();
                },
                fadeOut = function fade(el)
                {
                   var anim = new YAHOO.util.ColorAnim(el, 
                   {
                      backgroundColor: 
                      {
                         from: hoverColor,
                         to: defaultColor,
                         duration: '0.5'
                      }
                   });
                   anim.animate();
                };

            // if not visible in viewport
            if (!(targetContentElRegion.intersect(Dom.getClientRegion())))
            {
               if (this.scrollAnimation)
               {
                  this.scrollAnimation.stop();
               }

               //set up animation
               this.scrollAnimation = new YAHOO.util.Scroll( (YAHOO.env.ua.webkit | (document.compatMode=='BackCompat' && YAHOO.env.ua.ie)) ? document.body : document.documentElement, 
               {
                  scroll: 
                  {
                     to: [0, Math.max(0, targetContentElRegion.top - 125)]
                  }
               }, 1, YAHOO.util.Easing.easeOut);

               this.scrollAnimation.onComplete.subscribe(function(el)
               {
                  return function()
                  {
                     fadeIn(el);
                  };
               }(targetContentEl));

               this.scrollAnimation.animate();
            }
            else
            {
               fadeIn(targetContentEl);
            }
         }
      }, 
      module: 
      {
         getFormPanelInstance : function(id)
         {
            return Alfresco.util.ComponentManager.get(id) || new YAHOO.org.alfresco.awe.ui.FormPanel(id);
         }
      },
      component: {}
   });

   YAHOO.org.alfresco.awe.app.AWE_NEW_CONTENT_CLICK_EVENT = 'AWE_NewContent_click';
   YAHOO.org.alfresco.awe.app.AWE_EDIT_CONTENT_CLICK_EVENT = 'AWE_EditContent_click';
   YAHOO.org.alfresco.awe.app.AWE_DELETE_CONTENT_CLICK_EVENT = 'AWE_DeleteContent_click';
})();

WEF.register("org.alfresco.awe", YAHOO.org.alfresco.awe.app, {version: "1.0.1", build: "1"}, YAHOO);
