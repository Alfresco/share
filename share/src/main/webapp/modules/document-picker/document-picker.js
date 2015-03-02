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
 * DocumentPicker module
 *
 * A dialog for creating selecting documents
 *
 * @namespace Alfresco.module
 * @class Alfresco.module.DocumentPicker
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
   var $html = Alfresco.util.encodeHTML,
      $combine = Alfresco.util.combinePaths,
      $hasEventInterest = Alfresco.util.hasEventInterest;

   /**
    * DocumentPicker constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @param {String} (optional) objectRendererClass Class Reference of ObjectRenderer subclass to use as picker's object renderer
    * 
    * @return {Alfresco.DocumentPicker} The new DocumentPicker instance
    * @constructor
    */
   Alfresco.module.DocumentPicker = function(htmlId, objectRendererClass)
   {
      Alfresco.module.DocumentPicker.superclass.constructor.call(this, "Alfresco.module.DocumentPicker", htmlId, ["button", "menu", "container", "resize", "datatable", "datasource"]);

      /**
       * Decoupled event listeners
       */
      this.eventGroup = htmlId;
      YAHOO.Bubbling.on("renderCurrentValue", this.onRenderCurrentValue, this);
      YAHOO.Bubbling.on("selectedItemAdded", this.onSelectedItemAdded, this);
      YAHOO.Bubbling.on("selectedItemRemoved", this.onSelectedItemRemoved, this);
      YAHOO.Bubbling.on("parentChanged", this.onParentChanged, this);
      YAHOO.Bubbling.on("parentDetails", this.onParentDetails, this);

      // Initialise prototype properties
      this.pickerId = htmlId + "-cntrl-picker";
      this.widgets = {};
      this.columns = [];
      this.currentValueMeta = [];
      this.selectedItems = [];

      //use specifed object renderer or default to default object renderer
      var objectRendererClass =  objectRendererClass || Alfresco.ObjectRenderer;
      this.options.objectRenderer = new objectRendererClass(this);

      return this;
   };
   
   YAHOO.extend(Alfresco.module.DocumentPicker, Alfresco.component.Base,
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
          * Instance of an ObjectRenderer class
          *
          * @property objectRenderer
          * @type object
          */
         objectRenderer: null,

         /**
          * The current value
          *
          * @property currentValue
          * @type string
          */
         currentValue: "",
         
         /**
          * The type of the item to find
          *
          * @property itemType
          * @type string
          */
         itemType: "cm:content",
         
         /**
          * types
          *  
          */

         /**
          * Compact mode flag
          * 
          * @property compactMode
          * @type boolean
          * @default false
          */
         compactMode: false,

         /**
          * Multiple Select mode flag
          * 
          * @property multipleSelectMode
          * @type boolean
          * @default false
          */
         multipleSelectMode: true,
         
         /**
          * Determines whether a link to the target
          * node should be rendered
          *
          * @property showLinkToTarget
          * @type boolean
          * @default false
          */
         showLinkToTarget: false,
         
         /**
          * Template to use for link to target nodes, must
          * be supplied when showLinkToTarget property is
          * set to true
          *
          * @property targetLinkTemplate
          * @type string
          */
         targetLinkTemplate: null,
         
         /**
          *** NOT IMPLEMENTED ***
          * Number of characters required for a search
          * 
          * @property minSearchTermLength
          * @type int
          * @default 3
          */
         minSearchTermLength: 3,
         
         /**
          * Maximum number of items to display in the results list
          * 
          * @property maxSearchResults
          * @type int
          * @default 100
          */
         maxSearchResults: 100,
         
         /**
          * Flag to determine whether the added and removed items
          * should be maintained and posted separately.
          * If set to true (the default) the picker will update
          * a "${field.name}_added" and a "${field.name}_removed"
          * hidden field, if set to false the picker will just
          * update a "${field.name}" hidden field with the current
          * value.
          * 
          * @property maintainAddedRemovedItems
          * @type boolean
          * @default true
          */
         maintainAddedRemovedItems: true,
         
         /**
          * Flag to determine whether the picker is in disabled mode
          *
          * @property disabled
          * @type boolean
          * @default false
          */
         disabled: false,
         
         /**
          * Flag to indicate whether the field is mandatory
          *
          * @property mandatory
          * @type boolean
          * @default false
          */
         mandatory: false,
         
         /**
          * Flag to indicate whether to restrict picker to doclib and its children
          *  
          * @property restrictParentNavigationToDocLib
          * @type boolean
          * @default false
          */
         restrictParentNavigationToDocLib: true,

         /**
          * Alias to use in UI for documentLibrary
          *  
          * @property docLibNameAlias
          * @type string
          * @default null
          */
         docLibNameAlias: null,
                  
         /**
          * Reference to class to use as the object renderer. Allows
          * specification of different renderers that have different
          * functionality.
          * 
          * @property objectRendererClass
          * @type Object (Class not an instance) Must be a subclass of Alfresco.module.ObjectRenderer
          * @default Alfresco.module.ObjectRenderer
          */
          objectRendererClass : null
      },

      /**
       * Resizable columns
       * 
       * @property columns
       * @type array
       * @default []
       */
      columns: null,

      /**
       * The current value of the association including metadata
       *
       * @property currentValueMeta
       * @type array
       */
      currentValueMeta: null,
      
      /**
       * Single selected item, for when in single select mode
       * 
       * @property singleSelectedItem
       * @type string
       */
      singleSelectedItem: null,

      /**
       * Selected items. Keeps a list of selected items for correct Add button state.
       * 
       * @property selectedItems
       * @type object
       */
      selectedItems: null,

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.module.DocumentPicker} returns 'this' for method chaining
       */
      setOptions: function DocumentPicker_setOptions(obj)
      {
         Alfresco.module.DocumentPicker.superclass.setOptions.call(this, obj);

         // TODO: Do we need to filter this object literal before passing it on..?
         this.options.objectRenderer.setOptions(obj);

         return this;
      },

      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.module.DocumentPicker} returns 'this' for method chaining
       */
      setMessages: function DocumentPicker_setMessages(obj)
      {
         Alfresco.module.DocumentPicker.superclass.setMessages.call(this, obj);

         this.options.objectRenderer.setMessages(obj);
         return this;
      },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function DocumentPicker_onComponentsLoaded()
      {
         /**
          * Load the gui from the server and let the templateLoaded() method
          * handle the rest.
          */
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.URL_SERVICECONTEXT + "modules/document-picker",
            dataObj:
            {
               htmlid: this.id
            },
            successCallback:
            {
               fn: this.onTemplateLoaded,
               scope: this
            },
            execScripts: true,
            failureMessage: "Could not load create site template"
         });
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function DocumentPicker_onReady()
      {
         /**
          * Load the gui from the server and let the templateLoaded() method
          * handle the rest.
          */
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.URL_SERVICECONTEXT + "modules/document-picker",
            dataObj:
            {
               htmlid: this.id
            },
            successCallback:
            {
               fn: this.onTemplateLoaded,
               scope: this
            },
            execScripts: true,
            failureMessage: "Could not load create site template"
         });
      },
      
      /**
       * Flag used to ensure that the template only gets loaded once. In some circumstances the
       * onTemplateLoaded method can be invoked twice. This flag ensures that two templates are
       * not inadvertently generated. This should be set to true once the template has been loaded
       * and not changed again for the remaining lifecycle of the instance.
       * 
       * @property _templateLoaded
       * @type boolean
       */
      _templateLoaded: false,
      
      /**
       * Called when the DocumentPicker html template has been returned from the server.
       * Creates the YUI gui objects such as buttons and a panel and shows it.
       *
       * @method onTemplateLoaded
       * @param response {object} a Alfresco.util.Ajax.request response object 
       */
      onTemplateLoaded: function DocumentPicker_onTemplateLoaded(response)
      {
         if (!this._templateLoaded)
         {
            // Inject the template from the XHR request into a new DIV element
            var containerDiv = document.createElement("div");
            containerDiv.innerHTML = response.serverResponse.responseText;

            // The panel is created from the HTML returned in the XHR request, not the container
            var panelDiv = Dom.getFirstChild(containerDiv);
            // document.body.appendChild(panelDiv);
            this.widgets.panel = Alfresco.util.createYUIPanel(panelDiv);

            this._getCurrentValueMeta();
            if (this.options.disabled == false)
            {
               if (this.options.compactMode)
               {
                  Dom.addClass(this.pickerId, "compact");
               }
         
               this._createNavigationControls();
               this._createSelectedItemsControls();

               this.widgets.showPicker = Alfresco.util.createYUIButton(this, "showPicker-button", this.onShowPicker);
               this.widgets.ok = Alfresco.util.createYUIButton(this, "cntrl-ok", this.onOK);
               this.widgets.cancel = Alfresco.util.createYUIButton(this, "cntrl-cancel", this.onCancel);

               // force the generated buttons to have a name of "-" so it gets ignored in
               // JSON submit. TODO: remove this when JSON submit behaviour is configurable
               this._renameGeneratedButton(this.id + "-showPicker-button-button");
               this._renameGeneratedButton(this.id + "-cntrl-ok-button");
               this._renameGeneratedButton(this.id + "-cntrl-cancel-button");
               this._getSavedItems();
            }
            this._templateLoaded = true;
         }
      },  
          
      /**
       * Renames buttons if they exist (will work for any element, but named for intended purpose)
       * 
       * @method _renameGeneratedButton
       * @param id The id of the button to rename.
       */
      _renameGeneratedButton: function DocuemtPicker__renameGeneratedButton(id)
      {
         var el = Dom.get(id);
         if (el != null)
         {
            Dom.setAttribute(el, "name", "-");
         }
      },
      
      /**
       * Show picker button click handler
       *
       * @method onShowPicker
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onShowPicker: function DocumentPicker_onShowPicker(e, p_obj)
      {
         if (p_obj != null)
         {
            p_obj.set("disabled", true);
         }

         this.widgets.panel.show();
         this._createResizer();
         this._populateSelectedItems();         
         this.options.objectRenderer.onPickerShow();
         this.widgets.ok.set('disabled', (this.currentValueMeta && this.currentValueMeta.length == 0) ? false : true);

         YAHOO.Bubbling.fire("refreshItemList",
         {
            eventGroup: this
         });
         
         if (e != null)
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
         var item = p_obj.get("value");

         YAHOO.Bubbling.fire("parentChanged",
         {
            eventGroup: this,
            label: item.name,
            nodeRef: item.nodeRef
         });
         Event.preventDefault(e);
      },

      /**
       * Picker OK button click handler. Fires "onDocumentsSelected" event and clears selection.
       *
       * @method onOK
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onOK: function DocumentPicker_onOK(e, p_obj)
      {

         var selItems = this.getSelectedItems();
         //extract nodeRefs
         var selItemsAsNodeRefs = [];
         for (var item in this.selectedItems)
         {
            selItemsAsNodeRefs.push(item);
         }
         
         this.options.currentValue = selItemsAsNodeRefs.join(',');
         
         // This is an IE8 specific workaround that has been added to address ALF-11433
         // We need to defer firing the event until after the document-picker panel has
         // been hidden in case this event is being listened to by TinyMCE so that focus
         // can be set on the editor. The event is now fired at the end of the method.
         if (YAHOO.env.ua.ie != 8)
         {
            YAHOO.Bubbling.fire("onDocumentsSelected",
            {
               items: selItems
            });
         }
         Alfresco.util.setVar('DocumentPickerSelection', selItems);
         
         this.widgets.panel.hide();
         Dom.setAttribute(this.widgets.showPicker, "disabled", false);
         this.resetSelection();
         Event.preventDefault(e);
         
         // Fire the event if IE8
         if (YAHOO.env.ua.ie == 8)
         {
            YAHOO.Bubbling.fire("onDocumentsSelected",
            {
               items: selItems
            });
         }
      },

      /**
       * Picker Cancel button click handler. Clears previous selections.
       *
       * @method onCancel
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onCancel: function DocumentPicker_onCancel(e, p_obj)
      {
         this.widgets.panel.hide();
         Dom.setAttribute(this.widgets.showPicker, "disabled", false);
         this.resetSelection();
         Event.preventDefault(e);
      },

      /**
       * Resets selection so that reload of page doesn't repopulate picker
       *  
       * @method resetSelection
       */
      resetSelection: function DocumentPicker_resetSelection()
      {
         this.widgets.dataTable.deleteRows(0, this.widgets.dataTable.getRecordSet().getLength());
         this.selectedItems = [];
         Alfresco.util.setVar('DocumentPickerSelection',[]);
      },
      
      /*
       * PUBLIC INTERFACE
       */

      /**
       * Returns if an item can be selected
       *
       * @method canItemBeSelected
       * @param id {string} Item id (nodeRef)
       * @return {boolean}
       */
      canItemBeSelected: function DocumentPicker_canItemBeSelected(id)
      {
         if (!this.options.multipleSelectMode && this.singleSelectedItem !== null)
         {
            return false;
         }
         return (this.selectedItems[id] === undefined);
      },

      /**
       * Returns currently selected items
       *
       * @method getSelectedItems
       * 
       * @return {array}
       */
      getSelectedItems: function DocumentPicker_getSelectedItems()
      {
         var selectedItems = [];

         for (var item in this.selectedItems)
         {
            if (this.selectedItems.hasOwnProperty(item))
            {
               selectedItems.push(this.selectedItems[item]);
            }
         }
         return selectedItems;
      },

      /**
       * Returns items that have been added to the current value
       *
       * @method getAddedItems
       * @return {array}
       */
      getAddedItems: function DocumentPicker_getAddedItems()
      {
         var addedItems = [],
            currentItems = Alfresco.util.arrayToObject(this.options.currentValue.split(","));
         
         for (var item in this.selectedItems)
         {
            if (this.selectedItems.hasOwnProperty(item))
            {
               if (!(item in currentItems))
               {
                  addedItems.push(item);
               }
            }
         }
         return addedItems;
      },

      /**
       * Returns items that have been removed from the current value
       *
       * @method getRemovedItems
       * @return {array}
       */
      getRemovedItems: function DocumentPicker_getRemovedItems()
      {
         var removedItems = [],
            currentItems = Alfresco.util.arrayToObject(this.options.currentValue.split(","));
         
         for (var item in currentItems)
         {
            if (currentItems.hasOwnProperty(item))
            {
               if (!(item in this.selectedItems))
               {
                  removedItems.push(item);
               }
            }
         }
         return removedItems;
      },

      
      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR PAGE EVENTS
       * Disconnected event handlers for inter-component event notification
       */

      /**
       * Renders current value in reponse to an event
       *
       * @method onRenderCurrentValue
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters
       */
      onRenderCurrentValue: function DocumentPicker_onRenderCurrentValue(layer, args)
      {
         // Check the event is directed towards this instance
         if ($hasEventInterest(this, args))
         {
            var items = this.currentValueMeta,
               displayValue = "";

            if (items === null)
            {
               displayValue = "<span class=\"error\">" + this.msg("label.document-picker-current.failure") + "</span>";
            }
            else
            {
               for (var i = 0, ii = items.length; i < ii; i++)
               {
                  if (this.options.showLinkToTarget && this.options.targetLinkTemplate != null)
                  {
                     displayValue += this.options.objectRenderer.renderItem(items[i], 16, 
                        "<div>{icon} <a href='" + this.options.targetLinkTemplate + "'>{name}</a></div>");
                  }
                  else
                  {
                     displayValue += this.options.objectRenderer.renderItem(items[i], 16, "<div>{icon} {name}</div>");
                  }
               }
            }

            var cvd = Dom.get(this.id + "-currentValueDisplay");
            if (cvd)
            {
               cvd.innerHTML = displayValue;
            }
         }
      },

      /**
       * Selected Item Added event handler
       *
       * @method onSelectedItemAdded
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onSelectedItemAdded: function DocumentPicker_onSelectedItemAdded(layer, args)
      {   
         // Check the event is directed towards this instance
         if ($hasEventInterest(this, args))
         {
            var obj = args[1];
            if (obj && obj.item)
            {
               this.addItem(obj.item);
            }
         }
      },
      
      addItem: function(item)
      {
         // Add the item to the selected list
         this.widgets.dataTable.addRow(item);
         this.selectedItems[item.nodeRef] = item;
         this.singleSelectedItem = item;
         this.widgets.ok.set('disabled',false);
      },
      
      /**
       * Selected Item Removed event handler
       *
       * @method onSelectedItemRemoved
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onSelectedItemRemoved: function DocumentPicker_onSelectedItemRemoved(layer, args)
      {
         // Check the event is directed towards this instance
         if ($hasEventInterest(this, args))
         {
            var obj = args[1];
            if (obj && obj.item)
            {
               delete this.selectedItems[obj.item.nodeRef];
               this.singleSelectedItem = null;
            }
            if (this.selectedItems.length==0)
            {
               this.widgets.ok.set('disabled',true);
            }
         }
      },
      
      /**
       * Parent changed event handler
       *
       * @method onParentChanged
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onParentChanged: function DocumentPicker_onParentChanged(layer, args)
      {
         // Check the event is directed towards this instance
         if ($hasEventInterest(this, args))
         {
            var obj = args[1];
            if (obj && obj.label)
            {
               this.widgets.navigationMenu.set("label", '<div><span class="item-icon"><img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/ajax_anim.gif" width="16" height="16" alt="' + this.msg("message.please-wait") + '"></span><span class="item-name">' + $html(obj.label) + '</span></div>');
            }
         }
      },
      
      /**
       * Parent Details updated event handler
       *
       * @method onParentDetails
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onParentDetails: function DocumentPicker_onParentDetails(layer, args)
      {
         // Check the event is directed towards this instance
         if ($hasEventInterest(this, args))
         {
            var obj = args[1];
            if (obj && obj.parent)
            {
               var arrItems = [],
                  item = obj.parent,
                  navButton = this.widgets.navigationMenu,
                  navMenu = navButton.getMenu(),
                  navGroup = navMenu.getItemGroups()[0],
                  indent = "";
               
               // Create array, deepest node first in final array
               while (item)
               {
                  arrItems = [item].concat(arrItems);

                  if (item.name == 'documentLibrary')
                  {
                     //use alias for doc lib if supplied
                     if (this.options.docLibNameAlias)
                     {
                        item.name = this.options.docLibNameAlias;
                     }
                     //restrict to doc lib if configured to
                     if (this.options.restrictParentNavigationToDocLib)
                     {
                        break;
                     }
                  }
                  item = item.parent;
               }

               var i, ii;
               for (i = 0, ii = navGroup.length; i < ii; i++)
               {
                  navMenu.removeItem(0, 0, true);
               }
               
               item = arrItems[arrItems.length - 1];
               navButton.set("label", this.options.objectRenderer.renderItem(item, 16, '<div><span class="item-icon">{icon}</span><span class="item-name">{name}</span></div>'));
               
               // Navigation Up button
               if (arrItems.length > 1)
               {
                  this.widgets.folderUp.set("value", arrItems[arrItems.length - 2]);
                  this.widgets.folderUp.set("disabled", false);
               }
               else
               {
                  this.widgets.folderUp.set("disabled", true);
               }
               
               var menuItem;
               for (i = 0, ii = arrItems.length; i < ii; i++)
               {
                  item = arrItems[i];
                  menuItem = new YAHOO.widget.MenuItem(this.options.objectRenderer.renderItem(item, 16, indent + '<span class="item-icon">{icon}</span><span class="item-name">{name}</span>'),
                  {
                     value: item.nodeRef
                  });
                  menuItem.cfg.addProperty("label",
                  {
                     value: item.name
                  });
                  navMenu.addItem(menuItem, 0);
                  indent += "&nbsp;&nbsp;&nbsp;";
               }
               
               navMenu.render();
            }
         }
      },

      /*
       * PRIVATE FUNCTIONS
       */

      /**
       * Gets current value metadata from the repository
       *
       * @method _getCurrentValueMeta
       * @private
       */
      _getCurrentValueMeta: function DocumentPicker__getCurrentValueMeta(p_div)
      {
         var arrItems = this.options.currentValue.split(",");
         var onSuccess = function OF_rCV_onSuccess(response)
         {
            this.currentValueMeta = response.json.data.items;
            YAHOO.Bubbling.fire("renderCurrentValue",
            {
               eventGroup: this
            });
         };
         
         var onFailure = function OF_rCv_onFailure(response)
         {
            this.currentValueMeta = null;
         };
         
         Alfresco.util.Ajax.jsonRequest(
         {
            url: Alfresco.constants.PROXY_URI + "api/forms/picker/items",
            method: "POST",
            dataObj:
            {
               items: arrItems
            },
            successCallback:
            {
               fn: onSuccess,
               scope: this
            },
            failureCallback:
            {
               fn: onFailure,
               scope: this
            }
         });
      },
      
      /**
       * Creates the UI Navigation controls
       *
       * @method _createNavigationControls
       * @private
       */
      _createNavigationControls: function DocumentPicker__createNavigationControls()
      {
         var me = this;

         // Up Navigation button
         this.widgets.folderUp = new YAHOO.widget.Button(this.pickerId + "-folderUp",
         {
            disabled: true
         });
         this.widgets.folderUp.on("click", this.onFolderUp, this.widgets.folderUp, this);
         // Navigation drop-down menu
         this.widgets.navigationMenu = new YAHOO.widget.Button(this.pickerId + "-navigator",
         { 
            type: "menu", 
            menu: this.pickerId + "-navigatorMenu",
            lazyloadmenu: false
         });

         // force the generated buttons to have a name of "-" so it gets ignored in
         // JSON submit. TODO: remove this when JSON submit behaviour is configurable
         Dom.get(this.pickerId + "-folderUp-button").name = "-";
         Dom.get(this.pickerId + "-navigator-button").name = "-";
         this.widgets.navigationMenu.getMenu().subscribe("click", function (p_sType, p_aArgs)
         {
            var menuItem = p_aArgs[1];
            if (menuItem)
            {
               YAHOO.Bubbling.fire("parentChanged",
               {
                  eventGroup: me,
                  label: menuItem.cfg.getProperty("label"),
                  nodeRef: menuItem.value
               });
            }
         });
      },

      /**
       * Creates UI controls to support Selected Items
       *
       * @method _createSelectedItemsControls
       * @private
       */
      _createSelectedItemsControls: function DocumentPicker__createSelectedItemsControls()
      {
         var me = this;

         // Setup a DataSource for the selected items list
         this.widgets.dataSource = new YAHOO.util.DataSource([],
         {
            responseType: YAHOO.util.DataSource.TYPE_JSARRAY
         });

         /**
          * Icon datacell formatter
          *
          * @method renderCellIcon
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellIcon = function OF__cSIC_renderCellIcon(elCell, oRecord, oColumn, oData)
         {
            var iconSize = me.options.compactMode ? 16 : 32;
            
            oColumn.width = iconSize - 6;
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            elCell.innerHTML = me.options.objectRenderer.renderItem(oRecord.getData(), iconSize, '<div class="icon' + iconSize + '">{icon}</div>');
         };

         /**
          * Name / description datacell formatter
          *
          * @method renderCellName
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellName = function OF__cSIC_renderCellName(elCell, oRecord, oColumn, oData)
         {
            var template;
            if (me.options.compactMode)
            {
               template = '<h3 class="name">{name}</h3>';
            }
            else
            {
               template = '<h3 class="name">{name}</h3><div class="description">{description}</div>';
            }

            elCell.innerHTML = me.options.objectRenderer.renderItem(oRecord.getData(), 0, template);
         };

         /**
          * Remove item custom datacell formatter
          *
          * @method renderCellRemove
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellRemove = function OF__cSIC_renderCellRemove(elCell, oRecord, oColumn, oData)
         {  
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            elCell.innerHTML = '<a href="#" class="remove-item remove-' + me.eventGroup + '" title="' + me.msg("label.document-picker-remove-item") + '"><span class="removeIcon">&nbsp;</span></a>';
         };

         // DataTable defintion
         var columnDefinitions = [
         {
            key: "nodeRef", label: "Icon", sortable: false, formatter: renderCellIcon, width: this.options.compactMode ? 10 : 26
         },
         {
            key: "name", label: "Item", sortable: false, formatter: renderCellName
         },
         {
            key: "remove", label: "Remove", sortable: false, formatter: renderCellRemove, width: 16
         }];
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.pickerId + "-selectedItems", columnDefinitions, this.widgets.dataSource,
         {
            MSG_EMPTY: this.msg("label.document-picker-selected-items-empty")
         });

         // Hook remove item action click events
         var fnRemoveItemHandler = function OF_cSIC_fnRemoveItemHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               var target, rowId, record, nodeRef;

               target = args[1].target;
               rowId = target.offsetParent;
               record = me.widgets.dataTable.getRecord(rowId);
               if (record)
               {
                  me.widgets.dataTable.deleteRow(rowId);
                  YAHOO.Bubbling.fire("selectedItemRemoved",
                  {
                     eventGroup: me,
                     item: record.getData()
                  });
               }
            }
            return true;
         };
         YAHOO.Bubbling.addDefaultAction("remove-" + this.eventGroup, fnRemoveItemHandler);
      },
      
      /**
       * Populate selected items
       *
       * @method _populateSelectedItems
       * @private
       */
      _populateSelectedItems: function DocumentPicker__populateSelectedItems()
      {
         // Empty results table
         this.widgets.dataTable.set("MSG_EMPTY", this.msg("label.document-picker-selected-items-empty"));
         this.widgets.dataTable.deleteRows(0, this.widgets.dataTable.getRecordSet().getLength());

         this.selectedItems = {};

         this.currentValueMeta = this._getSavedItems();
         
         for (var item in this.currentValueMeta)
         {
            if (this.currentValueMeta.hasOwnProperty(item))
            {
               this.selectedItems[this.currentValueMeta[item].nodeRef] = this.currentValueMeta[item];
               YAHOO.Bubbling.fire("selectedItemAdded",
               {
                  eventGroup: this,
                  item: this.currentValueMeta[item]
               });
            }
         }
      },
      
      /**
       * Create YUI resizer widget
       *
       * @method _createResizer
       * @private
       */
      _createResizer: function DocumentPicker__createResizer()
      {
         if (!this.widgets.resizer)
         {
            var size = parseInt(Dom.getStyle(this.pickerId + "-body", "width"), 10);
            this.columns[0] = Dom.get(this.pickerId + "-left");
            this.columns[1] = Dom.get(this.pickerId + "-right");
            this.widgets.resizer = new YAHOO.util.Resize(this.pickerId + "-left",
            {
                handles: ["r"],
                minWidth: 200,
                maxWidth: (size - 200)
            });
            this.widgets.resizer.on("resize", function(e)
            {
                var w = e.width;
                Dom.setStyle(this.columns[0], "height", "");
                Dom.setStyle(this.columns[1], "width", (size - w - 10) + "px");
            }, this, true);

            this.widgets.resizer.fireEvent("resize",
            {
               ev: 'resize',
               target: this.widgets.resizer,
               width: this.widgets.resizer.get("width")
            });
         }
      },
      
      /**
       * Retrieves any saved selections (for use between page navigations).
       * Also fires event so UI can be updated
       *  
       * @method _getSavedItems
       * @@returns {Array} array of items
       */
      _getSavedItems: function DocumentPicker__getSavedItems()
      {
         var savedSelections = Alfresco.util.getVar('DocumentPickerSelection') || [];
         if (savedSelections.length !== 0)
         {
            YAHOO.Bubbling.fire("onDocumentsSelected", {items:savedSelections});
         }
         return savedSelections;
      }
   });
})();