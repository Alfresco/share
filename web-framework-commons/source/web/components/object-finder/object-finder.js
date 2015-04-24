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
 * ObjectFinder component.
 * 
 * @namespace Alfresco
 * @class Alfresco.ObjectFinder
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      KeyListener = YAHOO.util.KeyListener;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $hasEventInterest = Alfresco.util.hasEventInterest,
      $combine = Alfresco.util.combinePaths;
   
   /**
    * ObjectFinder constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @param {String} currentValueHtmlId The HTML id of the parent element
    * @return {Alfresco.ObjectFinder} The new ObjectFinder instance
    * @constructor
    */
   Alfresco.ObjectFinder = function Alfresco_ObjectFinder(htmlId, currentValueHtmlId)
   {
      Alfresco.ObjectFinder.superclass.constructor.call(this, "Alfresco.ObjectFinder", htmlId, ["button", "menu", "container", "resize", "datasource", "datatable"]);
      this.currentValueHtmlId = currentValueHtmlId;

      /**
       * Decoupled event listeners
       */
      this.eventGroup = htmlId;
      YAHOO.Bubbling.on("renderCurrentValue", this.onRenderCurrentValue, this);
      YAHOO.Bubbling.on("selectedItemAdded", this.onSelectedItemAdded, this);
      YAHOO.Bubbling.on("selectedItemRemoved", this.onSelectedItemRemoved, this);
      YAHOO.Bubbling.on("parentChanged", this.onParentChanged, this);
      YAHOO.Bubbling.on("parentDetails", this.onParentDetails, this);
      YAHOO.Bubbling.on("formContainerDestroyed", this.onFormContainerDestroyed, this);
      YAHOO.Bubbling.on("removeListItem", this.onRemoveListItem, this);

      // Initialise prototype properties
      this.pickerId = htmlId + "-picker";
      this.columns = [];
      this.selectedItems = {};
      this.isReady = false;
      
      this.options.objectRenderer = new Alfresco.ObjectRenderer(this);

      return this;
   };
   
   YAHOO.extend(Alfresco.ObjectFinder, Alfresco.component.Base,
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
          * The selected value to be displayed (but not yet persisted)
          *
          * @property selectedValue
          * @type string
          * @default null
          */
         selectedValue: null,

         /**
          * The current value
          *
          * @property currentValue
          * @type string
          */
         currentValue: "",
         
         /**
          * The id of the item being edited
          * 
          * @property currentItem
          * @type string
          */
         currentItem: null,
         
         /**
          * Value type.
          * Whether values are passed into and out of the control as nodeRefs or other data types
          *
          * @property valueType
          * @type string
          * @default "nodeRef"
          */
         valueType: "nodeRef",

         /**
          * The name of the field that the object finder displays
          *
          * @property field
          * @type string
          */
         field: null,

         /**
          * The type of the item to find
          *
          * @property itemType
          * @type string
          */
         itemType: "cm:content",
         
         /**
          * The 'family' of the item to find can be one of the following:
          * 
          * - node
          * - category
          * - authority
          * 
          * default is "node".
          * 
          * @property itemFamily
          * @type string
          */
         itemFamily: "node",

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
          * Template string or function to use for link to target nodes, must
          * be supplied when showLinkToTarget property is
          * set to true
          *
          * @property targetLinkTemplate If of type string it will be used as a template, if of type function an
          * item object will be passed as argument and link is expected to be returned by the function
          * @type (string|function)
          */
         targetLinkTemplate: null,
         
         /**
          * Number of characters required for a search
          * 
          * @property minSearchTermLength
          * @type int
          * @default 1
          */
         minSearchTermLength: 1,
         
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
          * Relative URI of "create new item" data webscript.
          *
          * @property createNewItemUri
          * @type string
          * @default ""
          */
         createNewItemUri: "",
         
         /**
          * Icon type to augment "create new item" row.
          *
          * @property createNewItemIcon
          * @type string
          * @default ""
          */
         createNewItemIcon: "",

         /**
          * The display mode to use for the current values.
          * Allowed values are "items" or "list"
          *
          * @property extendedMode
          * @type string
          * @default "items"
          */
         displayMode: "items",

         /**
          * The actions to display next to each item/current value in "list" mode.
          * - if "event" has been set: A click will fire an event with name as defined by "event" and item info as attribute.
          * - if "link" has been set: A normal html link will be displayed with href set to the value of "link"
          * {
          *    name: {String},  // The name of the action (used as a css class name for styling)
          *    event: {Object}, // If present will be the name of the event to send
          *    link: {String|function},  // If present will set the browser to display the link provided
          *    label: {String}  // The message label key use to get the display label
          * }
          *
          * @property listActions
          * @type Array
          * @default [ ] // Note! If allowRemoveAction equals true and
          *                       options.disabled is false and
          *                       displayMode equals "list"
          *                       a remove action will be added
          */
         listItemActions: [ ],

         /**
          * Determines if items shall be removable in "list" display mode
          *
          * @property allowRemoveAction
          * @type boolean
          * @default true
          */
         allowRemoveAction: true,

         /**
          * Determines if an "Remove all" button shall be displayed in "list" display mode
          *
          * @property allowRemoveAllAction
          * @type boolean
          * @default true
          */
         allowRemoveAllAction: true,

         /**
          * Determines if an "Add/Select" button shall be displayed that will display an items picker
          *
          * @property allowSelectAction
          * @type boolean
          * @default true
          */
         allowSelectAction: true,

         /**
          * Determines if a link is rendered for content that has children, if true
          * the content's children can be navigated.
          *
          * @property allowNavigationToContentChildren
          * @type boolean
          * @default false
          */
         allowNavigationToContentChildren: false,
         
         /**
          * The label of the select button that triggers the object finder dialog
          *
          * @property selectActionLabel
          * @type string
          */
         selectActionLabel: null,
         
         /**
          * The resource id for the label of the select button that triggers the object finder dialog
          *
          * @property selectActionLabelId
          * @type string
          */
         selectActionLabelId: null,
         
         /**
          * Specifies the location the object finder should start, the following
          * values are supported:
          * 
          * - {companyhome}
          * - {userhome}
          * - {siteshome}
          * - {doclib}
          * - {self}
          * - {parent}
          * - A NodeRef
          * - An XPath
          * 
          * @property startLocation
          * @type string
          */
         startLocation: null,
         
         /**
          * Specifies the parameters to pass to the node locator service
          * when determining the start location node.
          * 
          * @property startLocationParams
          * @type string
          */
         startLocationParams: null,
         
         /**
          * Specifies the Root Node, above which the object picker will not navigate.
          * Values supported are:
          *
          * - {companyhome}
          * - {userhome}
          * - {siteshome}
          * - A NodeRef
          * - An XPath
          */
         rootNode: null,
         
         /**
          * Specifies the API-URL used to find the items. If null, the default API URL will be used.
          * Can contain a placeholder, {itemFamily}.
          * 
          * @property finderAPI
          * @type string
          */
         finderAPI: null,
         
         /**
          * Specifies the API-URL used to get details on items. If null, the default API URL will be used.
          * 
          * @property finderAPI
          * @type string
          */
         itemsAPI: null
         
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
       * Determines if this component is ready (to be called from outside)
       *
       * @property isReady
       * @type boolean
       */
      isReady: false,

      /**
       * Set multiple initialization options at once.
       *
       * @override
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.ObjectFinder} returns 'this' for method chaining
       */
      setOptions: function ObjectFinder_setOptions(obj)
      {
         Alfresco.ObjectFinder.superclass.setOptions.call(this, obj);
         // TODO: Do we need to filter this object literal before passing it on..?
         this.options.objectRenderer.setOptions(obj);
         
         return this;
      },
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.ObjectFinder} returns 'this' for method chaining
       */
      setMessages: function ObjectFinder_setMessages(obj)
      {
         Alfresco.ObjectFinder.superclass.setMessages.call(this, obj);
         this.options.objectRenderer.setMessages(obj);
         return this;
      },

      /**
       * Populate selected items.
       *
       * @method selectItems
       * @param items {Array} Array of item ids to populate the current value with
       */
      selectItems: function ObjectFinder_selectItems(items)
      {
         this.options.selectedValue = items;
         this._loadSelectedItems();
      },
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function ObjectFinder_onReady()
      {
         this._createSelectedItemsControls();
         if (!this.options.disabled)
         {
            // Control is NOT in view mode
            if (this.options.compactMode)
            {
               Dom.addClass(this.pickerId, "compact");
            }
         
            this._createNavigationControls();
            var itemGroupActionsContainerEl = Dom.get(this.id + "-itemGroupActions");
            if (itemGroupActionsContainerEl)
            {
               // Create an "Add/Select" button that will display a picker to add items
               if (this.options.allowSelectAction)
               {
                  var addButtonEl = document.createElement("button");
                  itemGroupActionsContainerEl.appendChild(addButtonEl);
                  
                  var addButtonLabel = this.options.selectActionLabel;
                  if (this.options.selectActionLabelId && this.options.selectActionLabelId.length !== "")
                  {
                     addButtonLabel = this.msg(this.options.selectActionLabelId);
                  }
                  this.widgets.addButton = Alfresco.util.createYUIButton(this, null, this.onAddButtonClick,
                  {
                     label: addButtonLabel,
                     disabled: true
                  }, addButtonEl);
               }
               // Create a "Remove all" button to remove all items (if component is in "list" mode)
               if (this.options.allowRemoveAllAction && this.options.displayMode == "list")
               {
                  var removeAllButtonEl = document.createElement("button");
                  itemGroupActionsContainerEl.appendChild(removeAllButtonEl);
                  this.widgets.removeAllButton = Alfresco.util.createYUIButton(this, null, this.onRemoveAllButtonClick,
                  {
                     label: this.msg("button.removeAll"),
                     disabled: true
                  }, removeAllButtonEl);
               }
            }
            if (this.options.allowRemoveAction && this.options.displayMode == "list")
            {
               this.options.listItemActions.push(
               {
                  name: "remove-list-item",
                  event: "removeListItem",
                  label: "form.control.object-picker.remove-item"
               });
            }
            this.widgets.ok = Alfresco.util.createYUIButton(this, "ok", this.onOK);
            this.widgets.cancel = Alfresco.util.createYUIButton(this, "cancel", this.onCancel);
            
            // force the generated buttons to have a name of "-" so it gets ignored in
            // JSON submit. TODO: remove this when JSON submit behaviour is configurable
            Dom.get(this.id + "-ok-button").name = "-";
            Dom.get(this.id + "-cancel-button").name = "-";
            
            this.widgets.dialog = Alfresco.util.createYUIPanel(this.pickerId,
            {
               width: "60em"
            });
            this.widgets.dialog.hideEvent.subscribe(this.onCancel, null, this);
            Dom.addClass(this.pickerId, "object-finder");
         }
         
         this._loadSelectedItems();
      },

      /**
       * Destroy method - deregister Bubbling event handlers
       *
       * @method destroy
       */
      destroy: function ObjectFinder_destroy()
      {
         try
         {
            YAHOO.Bubbling.unsubscribe("renderCurrentValue", this.onRenderCurrentValue, this);
            YAHOO.Bubbling.unsubscribe("selectedItemAdded", this.onSelectedItemAdded, this);
            YAHOO.Bubbling.unsubscribe("selectedItemRemoved", this.onSelectedItemRemoved, this);
            YAHOO.Bubbling.unsubscribe("parentChanged", this.onParentChanged, this);
            YAHOO.Bubbling.unsubscribe("parentDetails", this.onParentDetails, this);
            YAHOO.Bubbling.unsubscribe("formContainerDestroyed", this.onFormContainerDestroyed, this);
            YAHOO.Bubbling.unsubscribe("removeListItem", this.onRemoveListItem, this);
         }
         catch (e)
         {
            // Ignore
         }
         Alfresco.ObjectFinder.superclass.destroy.call(this);
      },
      
      /**
       * Add button click handler, shows picker
       *
       * @method onAddButtonClick
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onAddButtonClick: function ObjectFinder_onAddButtonClick(e, p_obj)
      {
         // Register the ESC key to close the dialog
         if (!this.widgets.escapeListener)
         {
            this.widgets.escapeListener = new KeyListener(this.pickerId,
            {
               keys: KeyListener.KEY.ESCAPE
            },
            {
               fn: function ObjectFinder_onAddButtonClick_fn(eventName, keyEvent)
               {
                  this.onCancel();
                  Event.stopEvent(keyEvent[1]);
               },
               scope: this,
               correctScope: true
            });
         }
         this.widgets.escapeListener.enable();

         this.widgets.dialog.show();
         this._createResizer();
         this._populateSelectedItems();
         this.options.objectRenderer.onPickerShow();
         
         if (!this.options.objectRenderer.startLocationResolved && (this.options.startLocation || this.options.rootNode))
         {
            this._resolveStartLocation();
         }
         else
         {
            this._fireRefreshEvent();
         }
         
         p_obj.set("disabled", true);
         Event.preventDefault(e);
      },


      /**
       * Removes all list itesm from the current value list used in "list" display mode
       *
       * @method onRemoveAllButtonClick
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onRemoveAllButtonClick: function ObjectFinder_onRemoveAllButtonClick(e, p_obj)
      {
         this.widgets.currentValuesDataTable.deleteRows(0, this.widgets.currentValuesDataTable.getRecordSet().getLength());
         this.selectedItems = {};
         this.singleSelectedItem = null;
         this._adjustCurrentValues();
         Event.preventDefault(e);
      },

      /**
       * Folder Up Navigate button click handler
       *
       * @method onFolderUp
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onFolderUp: function ObjectFinder_onFolderUp(e, p_obj)
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
       * Create New OK button click handler
       *
       * @method onCreateNewOK
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onCreateNewOK: function ObjectFinder_onCreateNewOK(e, p_obj)
      {
         Event.preventDefault(e);
      },

      /**
       * Create New Cancel button click handler
       *
       * @method onCreateNewCancel
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onCreateNewCancel: function ObjectFinder_onCreateNewCancel(e, p_obj)
      {
         Event.preventDefault(e);
      },

      /**
       * Picker OK button click handler
       *
       * @method onOK
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onOK: function ObjectFinder_onOK(e, p_obj)
      {
         this.widgets.escapeListener.disable();
         this.widgets.dialog.hide();
         this.widgets.addButton.set("disabled", false);
         if (e)
         {
            Event.preventDefault(e);
         }

         YAHOO.Bubbling.fire("renderCurrentValue",
         {
            eventGroup: this
         });
      },

      /**
       * Adjust the current values, added, removed input elements according to the new selections
       * and fires event to notify form listeners about the changes.
       *
       * @method _adjustCurrentValues
       */
      _adjustCurrentValues: function ObjectFinder__adjustCurrentValues()
      {
         if (!this.options.disabled)
         {
            var addedItems = this.getAddedItems(),
               removedItems = this.getRemovedItems(),
               selectedItems = this.getSelectedItems();

            if (this.options.maintainAddedRemovedItems)
            {
               Dom.get(this.id + "-added").value = addedItems.toString();
               Dom.get(this.id + "-removed").value = removedItems.toString();
            }
            Dom.get(this.currentValueHtmlId).value = selectedItems.toString();
            if (Alfresco.logger.isDebugEnabled())
            {
               Alfresco.logger.debug("Hidden field '" + this.currentValueHtmlId + "' updated to '" + selectedItems.toString() + "'");
            }
                                 
            // inform the forms runtime that the control value has been updated (if field is mandatory)
            if (this.options.mandatory)
            {
               YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
            }

            YAHOO.Bubbling.fire("formValueChanged",
            {
               eventGroup: this,
               addedItems: addedItems,
               removedItems: removedItems,
               selectedItems: selectedItems,
               selectedItemsMetaData: Alfresco.util.deepCopy(this.selectedItems)
            });

            this._enableActions();
         }
      },

      /**
       * Picker Cancel button click handler
       *
       * @method onCancel
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onCancel: function ObjectFinder_onCancel(e, p_obj)
      {
         this.widgets.escapeListener.disable();
         this.widgets.dialog.hide();
         this.widgets.addButton.set("disabled", false);
         if (e)
         {
            Event.preventDefault(e);
         }
      },
      
      /**
       * Triggers a search
       *
       * @method onSearch
       */
      onSearch: function ObjectFinder_onSearch()
      {
         var searchTerm = YAHOO.lang.trim(Dom.get(this.pickerId + "-searchText").value);
         if (searchTerm.length < this.options.minSearchTermLength)
         {
            // show error message
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("form.control.object-picker.search.enter-more", this.options.minSearchTermLength)
            });
         }
         else
         {
            // execute search
            YAHOO.Bubbling.fire("refreshItemList",
            {
               eventGroup: this,
               searchTerm: searchTerm
            });
         }
      },

      /**
       * PUBLIC INTERFACE
       */

      /**
       * Returns if an item can be selected
       *
       * @method canItemBeSelected
       * @param id {string} Item id (nodeRef)
       * @return {boolean}
       */
      canItemBeSelected: function ObjectFinder_canItemBeSelected(id)
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
       * @return {array}
       */
      getSelectedItems: function ObjectFinder_getSelectedItems()
      {
         var selectedItems = [];

         for (var item in this.selectedItems)
         {
            if (this.selectedItems.hasOwnProperty(item))
            {
               selectedItems.push(this.selectedItems[item].nodeRef);
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
      getAddedItems: function ObjectFinder_getAddedItems()
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
      getRemovedItems: function ObjectFinder_getRemovedItems()
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
      onRenderCurrentValue: function ObjectFinder_onRenderCurrentValue(layer, args)
      {
         // Check the event is directed towards this instance
         if ($hasEventInterest(this, args))
         {
            this._adjustCurrentValues();

            var items = this.selectedItems,
               displayValue = "";

            if (items === null)
            {
               displayValue = "<span class=\"error\">" + this.msg("form.control.object-picker.current.failure") + "</span>";            
            }
            else
            {
               var item, link;
               if (this.options.displayMode == "list")
               {
                  var l = this.widgets.currentValuesDataTable.getRecordSet().getLength();
                  if (l > 0)
                  {
                     this.widgets.currentValuesDataTable.deleteRows(0, l);
                  }
               }
               
               for (var key in items)
               {
                  if (items.hasOwnProperty(key))
                  {
                     item = items[key];

                     // Special case for tags, which we want to render differently to categories
                     if (item.type == "cm:category" && item.displayPath.indexOf("/categories/Tags") !== -1)
                     {
                        item.type = "tag";
                     }

                     if (this.options.showLinkToTarget && this.options.targetLinkTemplate !== null)
                     {
                        if (this.options.displayMode == "items")
                        {
                           link = null;
                           if (YAHOO.lang.isFunction(this.options.targetLinkTemplate))
                           {
                              link = this.options.targetLinkTemplate.call(this, item);
                           }
                           else
                           {
                              //Discard template, build link from scratch
                              var linkTemplate = (item.site) ? Alfresco.constants.URL_PAGECONTEXT + "site/{site}/document-details?nodeRef={nodeRef}" : Alfresco.constants.URL_PAGECONTEXT + "document-details?nodeRef={nodeRef}";
                              link = YAHOO.lang.substitute(linkTemplate,
                              {
                                 nodeRef : item.nodeRef,
                                 site : item.site
                              });
                           }
                           displayValue += this.options.objectRenderer.renderItem(item, 16,
                                 "<div>{icon} <a href='" + link + "'>{name}</a></div>");
                        }
                        else if (this.options.displayMode == "list")
                        {
                           this.widgets.currentValuesDataTable.addRow(item);
                        }
                     }
                     else
                     {
                        if (this.options.displayMode == "items")
                        {
                           if (item.type === "tag")
                           {
                              displayValue += this.options.objectRenderer.renderItem(item, null, "<div class='itemtype-tag'>{name}</div>");
                           }
                           else
                           {
                              displayValue += this.options.objectRenderer.renderItem(item, 16, "<div class='itemtype-" + $html(item.type) + "' style='word-wrap: break-word;'>{icon} {name}</div>");
                           }
                        }
                        else if (this.options.displayMode == "list")
                        {
                           this.widgets.currentValuesDataTable.addRow(item);
                        }
                     }
                  }
               }
               if (this.options.displayMode == "items")
               {
                  Dom.get(this.id + "-currentValueDisplay").innerHTML = displayValue;
               }
            }
            this._enableActions();
         }
      },

      /**
       * Selected Item Added event handler
       *
       * @method onSelectedItemAdded
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onSelectedItemAdded: function ObjectFinder_onSelectedItemAdded(layer, args)
      {
         // Check the event is directed towards this instance
         if ($hasEventInterest(this, args))
         {
            var obj = args[1];
            if (obj && obj.item)
            {
               // Add the item at the correct position (sorted by name) in the selected list (if it hadn't been added already)
               var records = this.widgets.dataTable.getRecordSet().getRecords(),
                  i = 0,
                  il = records.length;
               
               for (; i < il; i++)
               {
                  if (obj.item.nodeRef == records[i].getData().nodeRef)
                  {
                     break;
                  }
               }
               if (i == il)
               {
                  this.widgets.dataTable.addRow(obj.item);
                  this.selectedItems[obj.item.nodeRef] = obj.item;
                  this.singleSelectedItem = obj.item;

                  if (obj.highlight)
                  {
                     // Make sure we scroll to the bottom of the list and highlight the new item
                     var dataTableEl = this.widgets.dataTable.get("element");
                     dataTableEl.scrollTop = dataTableEl.scrollHeight;
                     Alfresco.util.Anim.pulse(this.widgets.dataTable.getLastTrEl());
                  }
               }
               else
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.item-already-added", $html(obj.item.name))
                  });
               }
            }
         }
      },

      /**
       * Selected Item Removed event handler
       *
       * @method onSelectedItemRemoved
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onSelectedItemRemoved: function ObjectFinder_onSelectedItemRemoved(layer, args)
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
         }
      },
      
      /**
       * Parent changed event handler
       *
       * @method onParentChanged
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onParentChanged: function ObjectFinder_onParentChanged(layer, args)
      {
         // Check the event is directed towards this instance
         if ($hasEventInterest(this, args))
         {
            var obj = args[1];
            if (obj && obj.label)
            {
               this.widgets.navigationMenu.set("label", '<div><span class="item-icon"><img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/form/images/ajax_anim.gif" width="16" height="16" alt="' + this.msg("message.please-wait") + '"></span><span class="item-name">' + $html(obj.label) + '</span></div>');
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
      onParentDetails: function ObjectFinder_onParentDetails(layer, args)
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
                  item = item.parent;
               }

               var i, ii;
               for (i = 0, ii = navGroup.length; i < ii; i++)
               {
                  navMenu.removeItem(0, 0, true);
               }
               
               item = arrItems[arrItems.length - 1];
               var value = item.type == "st:site" ? "{title}" : "{name}";
               navButton.set("label", this.options.objectRenderer.renderItem(item, 16, '<div><span class="item-icon">{icon}</span><span class="item-name">' + value + '</span></div>'));
               
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
				  
                  var value = item.type == "st:site" ? "{title}" : "{name}";
                  menuItem = new YAHOO.widget.MenuItem(this.options.objectRenderer.renderItem(item, 16, indent + '<span class="item-icon">{icon}</span><span class="item-name">' + value + '</span>'),
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
      
      /**
       * Notification that form is being destroyed.
       *
       * @method onFormContainerDestroyed
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters
       */
      onFormContainerDestroyed: function ObjectFinder_onFormContainerDestroyed(layer, args)
      {
         if (this.widgets.dialog)
         {
            this.widgets.dialog.destroy();
            delete this.widgets.dialog;
         }
         if (this.widgets.resizer)
         {
            this.widgets.resizer.destroy();
            delete this.widgets.resizer;
         }
      },


      /**
       * Removes selected item from datatable used in "list" mode
       *
       * @method onRemoveListItem
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters
       */
      onRemoveListItem: function ObjectFinder_onRemoveListItem(event, args)
      {
         if ($hasEventInterest(this, args))
         {
            var data = args[1].value,
                  rowId = args[1].rowId;
            this.widgets.currentValuesDataTable.deleteRow(rowId);
            delete this.selectedItems[data.nodeRef];
            this.singleSelectedItem = null;
            this._adjustCurrentValues();
         }
      },

      /**
       * Returns Icon datacell formatter
       *
       * @method fnRenderCellIcon
       */
      fnRenderCellIcon: function ObjectFinder_fnRenderCellIcon()
      {
         var scope = this;

         /**
          * Icon datacell formatter
          *
          * @method renderCellIcon
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function ObjectFinder_renderCellIcon(elCell, oRecord, oColumn, oData)
         {
            var iconSize = scope.options.compactMode ? 16 : 32;
         
            oColumn.width = iconSize - 6;
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            elCell.innerHTML = scope.options.objectRenderer.renderItem(oRecord.getData(), iconSize, '<div class="icon' + iconSize + '">{icon}</div>');
         };
      },

      /**
       * Returns Icon with generic width datacell formatter
       *
       * @method fnRenderCellGenericIcon
       */
      fnRenderCellGenericIcon: function ObjectFinder_fnRenderCellGenericIcon()
      {
         var scope = this;

         /**
          * Icon datacell formatter
          *
          * @method renderCellGenericIcon
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function ObjectFinder_renderCellGenericIcon(elCell, oRecord, oColumn, oData)
         {
            Alfresco.logger.debug("ObjectFinder_renderCellGenericIcon(" + elCell + ", " + oRecord + ", " + oColumn.width + ", " + oData + ")");
            var iconSize = scope.options.compactMode ? 16 : 32;
            if (oColumn.width)
            {
               Alfresco.logger.debug("ObjectFinder_renderCellGenericIcon setting width!");
               Dom.setStyle(elCell, "width", oColumn.width + (YAHOO.lang.isNumber(oColumn.width) ? "px" : ""));
               Dom.setStyle(elCell.parentNode, "width", oColumn.width + (YAHOO.lang.isNumber(oColumn.width) ? "px" : ""));
            }
            elCell.innerHTML = scope.options.objectRenderer.renderItem(oRecord.getData(), iconSize, '<div class="icon' + iconSize + '">{icon}</div>');
         };
      },

      /**
       * Returns Name / description datacell formatter
       *
       * @method fnRenderCellName
       */
      fnRenderCellName: function ObjectFinder_fnRenderCellName()
      {
         var scope = this;

         /**
          * Name / description datacell formatter
          *
          * @method renderCellName
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function ObjectFinder_renderCellName(elCell, oRecord, oColumn, oData)
         {
            var template;
            if (scope.options.compactMode)
            {
               template = '<h3 class="name">' + scope.options.objectRenderer.resolveName(oRecord) + '</h3>';
            }
            else
            {
               template = '<h3 class="name">' + scope.options.objectRenderer.resolveName(oRecord) + '</h3><div class="description">{description}</div>';
            }

            elCell.innerHTML = scope.options.objectRenderer.renderItem(oRecord.getData(), 0, template);
         };
      },

      /**
       * Returns Remove item custom datacell formatter
       *
       * @method fnRenderCellRemove
       */
      fnRenderCellRemove: function ObjectFinder_fnRenderCellRemove()
      {
         var scope = this;

         /**
          * Remove item custom datacell formatter
          *
          * @method renderCellRemove
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function ObjectFinder_renderCellRemove(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            elCell.innerHTML = '<a href="#" class="remove-item remove-' + scope.eventGroup + '" title="' + scope.msg("form.control.object-picker.remove-item") + '" tabindex="0"><span class="removeIcon">&nbsp;</span></a>';
         };
      },


      /**
       * Returns Action item custom datacell formatter
       *
       * @method fnRenderCellListItemName
       */
      fnRenderCellListItemName: function ObjectFinder_fnRenderCellListItemName()
      {
         var scope = this;

         /**
          * Action item custom datacell formatter
          *
          * @method fnRenderCellListItemName
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function ObjectFinder_fnRenderCellListItemName(elCell, oRecord, oColumn, oData)
         {
            var item = oRecord.getData(),
               description =  item.description ? $html(item.description) : scope.msg("label.none"),
               modifiedOn = item.modified ? Alfresco.util.formatDate(Alfresco.util.fromISO8601(item.modified)) : null,
               title = $html(item.name);
            if (scope.options.showLinkToTarget && scope.options.targetLinkTemplate !== null)
            {
               var link;
               if (YAHOO.lang.isFunction(scope.options.targetLinkTemplate))
               {
                  link = scope.options.targetLinkTemplate.call(scope, oRecord.getData());
               }
               else
               {
                  //Discard template, build link from scratch
                  var linkTemplate = (item.site) ? Alfresco.constants.URL_PAGECONTEXT + "site/{site}/document-details?nodeRef={nodeRef}" : Alfresco.constants.URL_PAGECONTEXT + "document-details?nodeRef={nodeRef}";
                  link = YAHOO.lang.substitute(linkTemplate,
                  {
                     nodeRef : item.nodeRef,
                     site : item.site
                  });
               }
               title = '<a href="' + link + '">' + $html(item.displayName?item.displayName:item.name) + '</a>';
            }
            var template = '<h3 class="name">' + title + '</h3>';
            template += '<div class="description">' + scope.msg("form.control.object-picker.description") + ': ' + description + '</div>';
            template += '<div class="viewmode-label">' + scope.msg("form.control.object-picker.modified-on") + ': ' + (modifiedOn ? modifiedOn : scope.msg("label.none")) + '</div>';
            elCell.innerHTML = template;
         };
      },


      /**
       * Returns Action item custom datacell formatter
       *
       * @method fnRenderCellListItemActions
       */
      fnRenderCellListItemActions: function ObjectFinder_fnRenderCellListItemActions()
      {
         var scope = this;

         /**
          * Action item custom datacell formatter
          *
          * @method fnRenderCellListItemActions
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function ObjectFinder_fnRenderCellListItemActions(elCell, oRecord, oColumn, oData)
         {
            if (oColumn.width)
            {
               Dom.setStyle(elCell, "width", oColumn.width + (YAHOO.lang.isNumber(oColumn.width) ? "px" : ""));
               Dom.setStyle(elCell.parentNode, "width", oColumn.width + (YAHOO.lang.isNumber(oColumn.width) ? "px" : ""));
            }
            
            // While waiting for the package item actions, only render the actions (remove) in non editable mode
            if (scope.options.disabled === false) 
            {
               var links = "", link, listAction;
               for (var i = 0, il = scope.options.listItemActions.length; i < il; i++)
               {
                  listAction = scope.options.listItemActions[i];
                  if (listAction.event)
                  {
                     links += '<div class="list-action"><a href="#" class="' + listAction.name + ' ' + ' list-action-event-' + scope.eventGroup + ' ' + listAction.event+ '" title="' + scope.msg(listAction.label) + '" tabindex="0">' + scope.msg(listAction.label) + '</a></div>';
                  }
                  else
                  {
                     link = null;
                     if (YAHOO.lang.isFunction(listAction.link))
                     {
                        link = listAction.link.call(this, oRecord.getData());
                     }
                     else if (YAHOO.lang.isString(listAction.link))
                     {
                        link = YAHOO.lang.substitute(listAction.link, oRecord.getData());
                     }
                     links += '<div class="list-action"><a href="' + link + '" class="' + listAction.name + '" title="' + scope.msg(listAction.label) + '" tabindex="0">' + scope.msg(listAction.label) + '</a></div>';
                  }
               }
               elCell.innerHTML = links;
            }
         };
      },

      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Gets selected or current value's metadata from the repository
       *
       * @method _loadSelectedItems
       * @private
       */
      _loadSelectedItems: function ObjectFinder__loadSelectedItems(useOptions)
      {
         var arrItems = "";
         if (this.options.selectedValue)
         {
            arrItems = this.options.selectedValue;
         }
         else
         {
            arrItems = this.options.currentValue;
         }
         
         // populate with previous if no value set
         if (arrItems === "")
         {
            var arrItemsEl = Dom.get(this.currentValueHtmlId);
            if (arrItemsEl != null)
            {
               arrItems = arrItemsEl.value;
            }
         }
         
         var onSuccess = function ObjectFinder__loadSelectedItems_onSuccess(response)
         {
            var items = response.json.data.items,
               item;
            this.selectedItems = {};

            for (var i = 0, il = items.length; i < il; i++)
            {
               item = items[i];
               this.selectedItems[item.nodeRef] = item;
            }

            YAHOO.Bubbling.fire("renderCurrentValue",
            {
               eventGroup: this
            });
         };
         
         var onFailure = function ObjectFinder__loadSelectedItems_onFailure(response)
         {
            this.selectedItems = null;
         };

         if (arrItems !== "")
         {
            // Determine right URL to use
            var itemsUrl = null;
            if(this.options.itemsAPI != null)
            {
               itemsUrl = this.options.itemsAPI;
            }
            else
            {
               itemsUrl = Alfresco.constants.PROXY_URI + "api/forms/picker/items";
            }
            Alfresco.util.Ajax.jsonRequest(
            {
               url: itemsUrl,
               method: "POST",
               dataObj:
               {
                  items: arrItems.split(","),
                  itemValueType: this.options.valueType
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
         }
         else
         {
            // if disabled show the (None) message
            if (this.options.disabled && this.options.displayMode == "items")
            {
               Dom.get(this.id + "-currentValueDisplay").innerHTML = this.msg("form.control.novalue");
            }
            
            this._enableActions();
         }
      },

      /**
       * Creates the UI Navigation controls
       *
       * @method _createNavigationControls
       * @private
       */
      _createNavigationControls: function ObjectFinder__createNavigationControls()
      {
         var me = this;
         
         if (this._inAuthorityMode())
         {
            // only show the search box for authority mode
            Dom.setStyle(this.pickerId + "-folderUpContainer", "display", "none");
            Dom.setStyle(this.pickerId + "-navigatorContainer", "display", "none");
            Dom.setStyle(this.pickerId + "-searchContainer", "display", "block");
            
            // setup search widgets
            this.widgets.searchButton = new YAHOO.widget.Button(this.pickerId + "-searchButton");
            this.widgets.searchButton.on("click", this.onSearch, this.widgets.searchButton, this);
            
            // force the generated buttons to have a name of "-" so it gets ignored in
            // JSON submit. TODO: remove this when JSON submit behaviour is configurable
            Dom.get(this.pickerId + "-searchButton").name = "-";
            
            // register the "enter" event on the search text field
            var zinput = Dom.get(this.pickerId + "-searchText");
            new YAHOO.util.KeyListener(zinput, 
            {
               keys: 13
            }, 
            {
               fn: me.onSearch,
               scope: this,
               correctScope: true
            }, "keydown").enable();
         }
         else
         {
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
            
            // Optional "Create New" UI controls
            if (Dom.get(this.pickerId + "-createNew"))
            {
               // Create New - OK button
               this.widgets.createNewOK = new YAHOO.widget.Button(this.pickerId + "-createNewOK",
               {
                  disabled: true
               });
               this.widgets.createNewOK.on("click", this.onCreateNewOK, this.widgets.createNewOK, this);
   
               // Create New - Cancel button
               this.widgets.createNewCancel = new YAHOO.widget.Button(this.pickerId + "-createNewCancel",
               {
                  disabled: true
               });
               this.widgets.createNewCancel.on("click", this.onCreateNewCancel, this.widgets.createNewCancel, this);
            }
         }
      },

      /**
       * Creates UI controls to support Selected Items
       *
       * @method _createSelectedItemsControls
       * @private
       */
      _createSelectedItemsControls: function ObjectFinder__createSelectedItemsControls()
      {
         var doBeforeParseDataFunction = function ObjectFinder__createSelectedItemsControls_doBeforeParseData(oRequest, oFullResponse)
         {
            var updatedResponse = oFullResponse;

            if (oFullResponse && oFullResponse.length > 0)
            {
               var items = oFullResponse.data.items;

               // Special case for tags, which we want to render differently to categories
               var index, item;
               for (index in items)
               {
                  if (items.hasOwnProperty(index))
                  {
                     item = items[index];
                     if (item.type == "cm:category" && item.displayPath.indexOf("/categories/Tags") !== -1)
                     {
                        item.type = "tag";
                     }
                  }
               }

               // we need to wrap the array inside a JSON object so the DataTable is happy
               updatedResponse =
               {
                  items: items
               };
            }

            return updatedResponse;
         };

         var me = this;

         if (this.options.disabled === false)
         {

            // Setup a DataSource for the selected items list
            this.widgets.dataSource = new YAHOO.util.DataSource([],
            {
               responseType: YAHOO.util.DataSource.TYPE_JSARRAY,
               doBeforeParseData: doBeforeParseDataFunction
            });

            // Picker DataTable definition
            var columnDefinitions =
            [
               { key: "nodeRef", label: "Icon", sortable: false, formatter: this.fnRenderCellIcon(), width: this.options.compactMode ? 10 : 26 },
               { key: "name", label: "Item", sortable: false, formatter: this.fnRenderCellName() },
               { key: "remove", label: "Remove", sortable: false, formatter: this.fnRenderCellRemove(), width: 16 }
            ];

            this.widgets.dataTable = new YAHOO.widget.DataTable(this.pickerId + "-selectedItems", columnDefinitions, this.widgets.dataSource,
            {
               MSG_EMPTY: this.msg("form.control.object-picker.selected-items.empty")
            });

            // Hook remove item action click events
            var fnRemoveItemHandler = function ObjectFinder__createSelectedItemsControls_fnRemoveItemHandler(layer, args)
            {
               var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
               if (owner !== null)
               {
                  var target, rowId, record;

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
            YAHOO.Bubbling.addDefaultAction("remove-" + this.eventGroup, fnRemoveItemHandler, true);
         }

         // Add displayMode as class so we can separate the styling of the currentValue element
         var currentValueEl = Dom.get(this.id + "-currentValueDisplay");
         Dom.addClass(currentValueEl, "object-finder-" + this.options.displayMode);
         
         if (this.options.displayMode == "list")
         {
            // Setup a DataSource for the selected items list
            var ds = new YAHOO.util.DataSource([],
            {
               responseType: YAHOO.util.DataSource.TYPE_JSARRAY,
               doBeforeParseData: doBeforeParseDataFunction
            });

            // Current values DataTable definition
            var currentValuesColumnDefinitions =
            [
               { key: "nodeRef", label: "Icon", sortable: false, formatter: this.fnRenderCellGenericIcon(), width: 50 },
               { key: "name", label: "Item", sortable: false, formatter: this.fnRenderCellListItemName() },
               { key: "action", label: "Actions", sortable: false, formatter: this.fnRenderCellListItemActions(), width: 200 }
            ];

            // Make sure the currentValues container is a div rather than a span to make sure it may become a datatable
            var currentValueId = this.id + "-currentValueDisplay";
            currentValueEl = Dom.get(currentValueId);
            if (currentValueEl.tagName.toLowerCase() == "span")
            {
               var currentValueDiv = document.createElement("div");
               currentValueDiv.setAttribute("class", currentValueEl.getAttribute("class"));
               currentValueEl.parentNode.appendChild(currentValueDiv);
               currentValueEl.parentNode.removeChild(currentValueEl);
               currentValueEl = currentValueDiv;
            }
            this.widgets.currentValuesDataTable = new YAHOO.widget.DataTable(currentValueEl, currentValuesColumnDefinitions, ds,
            {
               MSG_EMPTY: this.msg("form.control.object-picker.selected-items.empty")
            });
            this.widgets.currentValuesDataTable.subscribe("rowMouseoverEvent", this.widgets.currentValuesDataTable.onEventHighlightRow);
            this.widgets.currentValuesDataTable.subscribe("rowMouseoutEvent", this.widgets.currentValuesDataTable.onEventUnhighlightRow);

            Dom.addClass(currentValueEl, "form-element-border");
            Dom.addClass(currentValueEl, "form-element-background-color");

            // Hook action item click events
            var fnActionListItemHandler = function ObjectFinder__createSelectedItemsControls_fnActionListItemHandler(layer, args)
            {
               var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
               if (owner !== null)
               {
                  var target, rowId, record;

                  target = args[1].target;
                  rowId = target.offsetParent;
                  record = me.widgets.currentValuesDataTable.getRecord(rowId);
                  if (record)
                  {
                     var data = record.getData(),
                        name = YAHOO.util.Dom.getAttribute(args[1].target, "class").split(" ")[0];
                     for (var i = 0, il = me.options.listItemActions.length; i < il; i++)
                     {
                        if (me.options.listItemActions[i].name == name)
                        {
                           YAHOO.Bubbling.fire(me.options.listItemActions[i].event,
                           {
                              eventGroup: me,
                              value: data,
                              rowId: rowId
                           });
                           return true;
                        }
                     }
                  }
               }
               return true;
            };
            YAHOO.Bubbling.addDefaultAction("list-action-event-" + this.eventGroup, fnActionListItemHandler, true);
         }
      },
      
      /**
       * Populate selected items
       *
       * @method _populateSelectedItems
       * @private
       */
      _populateSelectedItems: function ObjectFinder__populateSelectedItems()
      {
         // Empty results table
         this.widgets.dataTable.set("MSG_EMPTY", this.msg("form.control.object-picker.selected-items.empty"));
         this.widgets.dataTable.deleteRows(0, this.widgets.dataTable.getRecordSet().getLength());

         for (var item in this.selectedItems)
         {
            if (this.selectedItems.hasOwnProperty(item))
            {
               YAHOO.Bubbling.fire("selectedItemAdded",
               {
                  eventGroup: this,
                  item: this.selectedItems[item]
               });
            }
         }
      },
      
      /**
       * Resolves the start location provided to the component and refreshes
       * the picker to show the children of that start location.
       * 
       * @method _resolveStartLocation
       * @private
       */
      _resolveStartLocation: function ObjectFinder__resolveStartLocation()
      {
         if (this.options.startLocation || this.options.rootNode)
         {
            this.options.startLocation = (this.options.startLocation || this.options.rootNode);
            
            if (Alfresco.logger.isDebugEnabled())
            {
               Alfresco.logger.debug("Resolving startLocation of '" + this.options.startLocation + "'");
            }
            
            var startingNodeRef = null;
            
            // check first for the start locations that don't require a remote call
            if (this.options.startLocation.charAt(0) == "{")
            {
               if (this.options.startLocation == "{companyhome}")
               {
                  startingNodeRef = "alfresco://company/home";
               }
               else if (this.options.startLocation == "{userhome}")
               {
                  startingNodeRef = "alfresco://user/home";
               }
               else if (this.options.startLocation == "{siteshome}")
               {
                  startingNodeRef = "alfresco://sites/home";
               }
               else if (this.options.startLocation == "{shared}")
               {
                  startingNodeRef = "alfresco://company/shared";
               }
               else if (this.options.startLocation == "{self}")
               {
                  if (this.options.currentItem && this.options.currentItem !== null)
                  {
                     startingNodeRef = this.options.currentItem;
                  }
                  else
                  {
                     startingNodeRef = "alfresco://company/home";
                     
                     if (Alfresco.logger.isDebugEnabled())
                     {
                        Alfresco.logger.warn("To use a start location of {self} a 'currentItem' parameter is required, defaulting to company home");
                     }
                  }
               }
            }
            else if (this.options.startLocation.charAt(0) == "/")
            {
               // start location is an XPath, this will be dealt with later so set to empty string to ignore it
               startingNodeRef = "";
            }
            else
            {
               // start location must be a hardcoded nodeRef
               startingNodeRef = this.options.startLocation;
            }
            
            if (startingNodeRef != null)
            {
               // we already know the start location so just refresh
               this.options.objectRenderer.options.parentNodeRef = startingNodeRef;
               this._fireRefreshEvent();
            }
            else
            {
               // we don't know the start location so try the remote node locator service
               this._locateStartingNode();
            }
         }
         else
         {
            this._fireRefreshEvent();
         }
      },
      
      /**
       * Locates the NodeRef for the start location by calling the remote node locator
       * service and refreshes the picker.
       * 
       * @method _locateStartingNode
       * @private
       */
      _locateStartingNode: function ObjectFinder__locateStartingNode()
      {
         if (this.options.startLocation && this.options.currentItem && this.options.currentItem !== null)
         {
            var nodeLocator = "companyhome";
            
            // for backwards compatibility support the well known {parent} start location
            if (this.options.startLocation == "{parent}")
            {
               nodeLocator = "ancestor";
            }
            else if (this.options.startLocation.length > 2 && 
                     this.options.startLocation.charAt(0) == "{" &&
                     this.options.startLocation.charAt(this.options.startLocation.length-1) == "}")
            {
               // strip off the { } characters
               nodeLocator = this.options.startLocation.substring(1, this.options.startLocation.length-1);
            }
            
            // build the base URL for the nodelocator service call
            var url = $combine(Alfresco.constants.PROXY_URI, "/api/", this.options.currentItem.replace("://", "/"), 
                  "nodelocator", nodeLocator);
            
            // add parameters for the call to the node locator service, if there are any
            if (this.options.startLocationParams && this.options.startLocationParams != null)
            {
               url += "?" + encodeURI(this.options.startLocationParams);
            }
            
            // define success handler
            var successHandler = function ObjectFinder__locateStartingNode_successHandler(response)
            {
               var startingNodeRef = response.json.data.nodeRef;
               
               if (Alfresco.logger.isDebugEnabled())
               {
                  Alfresco.logger.debug("startLocation resolved to: " + startingNodeRef);
               }
               
               this.options.objectRenderer.options.parentNodeRef = startingNodeRef;
               this._fireRefreshEvent();
            };
            
            // define failure handler
            var failureHandler = function ObjectFinder__locateStartingNode_failureHandler(response)
            {
               if (Alfresco.logger.isDebugEnabled())
               {
                  Alfresco.logger.error("Failed to locate node: " + response.serverResponse.responseText);
               }
               
               // just use the defaults, normally company home
               this._fireRefreshEvent();
            };
            
            if (Alfresco.logger.isDebugEnabled())
            {
               Alfresco.logger.debug("Generated nodelocator url: " + url);
            }
            
            // call the node locator webscript
            var config =
            {
               method: "GET",
               url: url,
               successCallback: 
               { 
                  fn: successHandler, 
                  scope: this
               },
               failureCallback:
               {
                  fn: failureHandler,
                  scope: this
               }
            };
            Alfresco.util.Ajax.request(config);
         }
         else 
         {
            if (Alfresco.logger.isDebugEnabled())
            {
               Alfresco.logger.warn("To use a start location of " + this.options.startLocation + 
                     " a 'currentItem' parameter is required");
            }
            
            this._fireRefreshEvent();
         } 
      },
      
      /**
       * Fires the refreshItemList event to refresh the contents of the picker.
       * 
       * @method _fireRefreshEvent
       * @private
       */
      _fireRefreshEvent: function ObjectFinder__fireRefreshEvent()
      {
         if (this._inAuthorityMode() === false)
         {
            YAHOO.Bubbling.fire("refreshItemList",
            {
               eventGroup: this
            });
         }
         else
         {
            // get the current search term
            var searchTermInput = Dom.get(this.pickerId + "-searchText");
            var searchTerm = searchTermInput.value;
            if (searchTerm.length >= this.options.minSearchTermLength)
            {
               // refresh the previous search
               YAHOO.Bubbling.fire("refreshItemList",
               {
                  eventGroup: this,
                  searchTerm: searchTerm
               });
            }
            else
            {
               // focus ready for a search
               searchTermInput.focus();
            }
         }
      },
      
      /**
       * Create YUI resizer widget
       *
       * @method _createResizer
       * @private
       */
      _createResizer: function ObjectFinder__createResizer()
      {
         if (!this.widgets.resizer)
         {
            var size = parseInt(Dom.get(this.pickerId + "-body").offsetWidth, 10) - 2,
               heightFix = 0;
            this.columns[0] = Dom.get(this.pickerId + "-left");
            this.columns[1] = Dom.get(this.pickerId + "-right");
            this.widgets.resizer = new YAHOO.util.Resize(this.pickerId + "-left",
            {
                handles: ["r"],
                minWidth: 200,
                maxWidth: (size - 200)
            });
            // The resize handle doesn't quite get the element height correct, so it's saved here
            heightFix = this.widgets.resizer.get("height");
            
            this.widgets.resizer.on("resize", function(e)
            {
                var w = e.width;
                Dom.setStyle(this.columns[0], "height", "");
                Dom.setStyle(this.columns[1], "width", (size - w - 8) + "px");
            }, this, true);

            this.widgets.resizer.on("endResize", function(e)
            {
               // Reset the resize handle height to it's original value
               this.set("height", heightFix);
            });

            this.widgets.resizer.fireEvent("resize",
            {
               ev: 'resize',
               target: this.widgets.resizer,
               width: size / 2
            });
         }
      },
      
      /**
       * Determines whether the picker is in 'authority' mode.
       * 
       * @method _inAuthorityMode
       * @return true if the picker is being used to find authorities i.e. users and groups
       * @private
       */
      _inAuthorityMode: function ObjectFinder__inAuthorityMode()
      {
         return (this.options.itemFamily == "authority");
      },


      /**
       * Determines whether the picker is in 'authority' mode.
       *
       * @method _enableActions
       * @private
       */
      _enableActions: function ObjectFinder__enableActions()
      {
         if (this.widgets.removeAllButton)
         {
            // Enable the remove all button if there is any items
            this.widgets.removeAllButton.set("disabled", this.widgets.currentValuesDataTable.getRecordSet().getLength() === 0);
         }
         if (this.widgets.addButton)
         {
            // Enable the add button
            this.widgets.addButton.set("disabled", false);                  
         }

         if (!this.options.disabled && !this.isReady)
         {
            this.isReady = true;
            YAHOO.Bubbling.fire("objectFinderReady",
            {
               eventGroup: this
            });
         }
      }
   });
})();


/**
 * ObjectRenderer component.
 * 
 * @namespace Alfresco
 * @class Alfresco.ObjectRenderer
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      KeyListener = YAHOO.util.KeyListener;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $hasEventInterest = Alfresco.util.hasEventInterest,
      $combine = Alfresco.util.combinePaths;

   /**
    * Internal constants
    */
   var IDENT_CREATE_NEW = "~CREATE~NEW~";


   /**
    * ObjectRenderer constructor.
    * 
    * @param {object} Instance of the ObjectFinder
    * @return {Alfresco.ObjectRenderer} The new ObjectRenderer instance
    * @constructor
    */
   Alfresco.ObjectRenderer = function(objectFinder)
   {
      this.objectFinder = objectFinder;
      
      Alfresco.ObjectRenderer.superclass.constructor.call(this, "Alfresco.ObjectRenderer", objectFinder.pickerId, ["button", "menu", "container", "datasource", "datatable"]);
      /**
       * Decoupled event listeners
       */
      this.eventGroup = objectFinder.eventGroup;
      YAHOO.Bubbling.on("refreshItemList", this.onRefreshItemList, this);
      YAHOO.Bubbling.on("parentChanged", this.onParentChanged, this);
      YAHOO.Bubbling.on("selectedItemAdded", this.onSelectedItemChanged, this);
      YAHOO.Bubbling.on("selectedItemRemoved", this.onSelectedItemChanged, this);

      // Initialise prototype properties
      this.addItemButtons = {};
      this.startLocationResolved = false;
      this.createNewItemId = null;

      return this;
   };
   
   YAHOO.extend(Alfresco.ObjectRenderer, Alfresco.component.Base,
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
          * Parent node for browsing
          *
          * @property parentNodeRef
          * @type string
          */
         parentNodeRef: "",

         /**
          * The type of the item to find
          *
          * @property itemType
          * @type string
          */
         itemType: "cm:content",
         
         /**
          * The 'family' of the item to find can be one of the following:
          * 
          * - node
          * - category
          * - authority
          * 
          * default is "node".
          * 
          * @property itemFamily
          * @type string
          */
         itemFamily: "node",

         /**
          * Parameters to be passed to the data webscript
          *
          * @property params
          * @type string
          */
         params: "",

         /**
          * Compact mode flag
          * 
          * @property compactMode
          * @type boolean
          * @default false
          */
         compactMode: false,

         /**
          * Maximum number of items to display in the results list
          * 
          * @property maxSearchResults
          * @type int
          * @default 100
          */
         maxSearchResults: 100,
         
         /**
          * Relative URI of "create new item" data webscript.
          *
          * @property createNewItemUri
          * @type string
          * @default ""
          */
         createNewItemUri: "",
         
         /**
          * Icon type to augment "create new item" row.
          *
          * @property createNewItemIcon
          * @type string
          * @default ""
          */
         createNewItemIcon: ""
      },

      /**
       * Object container for storing button instances, indexed by item id.
       * 
       * @property addItemButtons
       * @type object
       */
      addItemButtons: null,

      /**
       * Create new item input control Dom Id
       * 
       * @property createNewItemId
       * @type string
       */
      createNewItemId: null,
      
      /**
       * Flag to indicate whether the start location (if present)
       * has been resolved yet or not
       * 
       * @property startLocationResolved
       * @type boolean
       */
      startLocationResolved: false,
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function ObjectRenderer_onReady()
      {
         this._createControls();
      },

      /**
       * Destroy method - deregister Bubbling event handlers
       *
       * @method destroy
       */
      destroy: function ObjectRenderer_destroy()
      {
         try
         {
            YAHOO.Bubbling.unsubscribe("refreshItemList", this.onRefreshItemList, this);
            YAHOO.Bubbling.unsubscribe("parentChanged", this.onParentChanged, this);
            YAHOO.Bubbling.unsubscribe("selectedItemAdded", this.onSelectedItemChanged, this);
            YAHOO.Bubbling.unsubscribe("selectedItemRemoved", this.onSelectedItemChanged, this);
         }
         catch (e)
         {
            // Ignore
         }
         Alfresco.ObjectRenderer.superclass.destroy.call(this);
      },

      
      /**
       * PUBLIC INTERFACE
       */

      /**
       * The picker has just been shown
       *
       * @method onPickerShow
       */
      onPickerShow: function ObjectRenderer_onPickerShow()
      {
         this.addItemButtons = {};
         Dom.get(this.objectFinder.pickerId).focus();
      },

      /**
       * Generate item icon URL
       *
       * @method getIconURL
       * @param item {object} Item object literal
       * @param size {number} Icon size (16, 32)
       */
      getIconURL: function ObjectRenderer_getIconURL(item, size)
      {
         return Alfresco.constants.URL_RESCONTEXT + 'components/images/filetypes/' + Alfresco.util.getFileIcon(item.name, item.type, size, item.parentType);
      },
      
      /**
       * Render item using a passed-in template
       *
       * @method renderItem
       * @param item {object} Item object literal
       * @param iconSize {number} Icon size (16, 32)
       * @param template {string} String with "{parameter}" style placeholders
       */
      renderItem: function ObjectRenderer_renderItem(item, iconSize, template)
      {
         var me = this;
         
         var renderHelper = function ObjectRenderer_renderItem_renderHelper(p_key, p_value, p_metadata)
         {
            if (p_key.toLowerCase() == "icon")
            {
               if (item.parentType == null && item.parent && item.parent.type)
               {
                  item.parentType = item.parent.type;
               }
               return '<img src="' + me.getIconURL(item, iconSize) + '" style="border-style:none;"' + '" width="' + iconSize + '" alt="' + $html(item.description) + '" title="' + $html(item.name) + '" />'; 
            }
            return $html(p_value);
         };
         
         return YAHOO.lang.substitute(template, item, renderHelper);
      },

      
      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR PAGE EVENTS
       * Disconnected event handlers for inter-component event notification
       */

      /**
       * Refresh item list event handler
       *
       * @method onRefreshItemList
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onRefreshItemList: function ObjectRenderer_onRefreshItemList(layer, args)
      {   
         // Check the event is directed towards this instance
         if ($hasEventInterest(this, args))
         {
            var searchTerm = "";
            var obj = args[1];
            if (obj && obj.searchTerm)
            {
               searchTerm = obj.searchTerm;
            }
            this._updateItems(this.options.parentNodeRef, searchTerm);
         }
      },

      /**
       * Parent changed event handler
       *
       * @method onParentChanged
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onParentChanged: function ObjectRenderer_onParentChanged(layer, args)
      {   
         // Check the event is directed towards this instance
         if ($hasEventInterest(this, args))
         {
            var obj = args[1];
            if (obj && obj.nodeRef)
            {
               this._updateItems(obj.nodeRef, "");
            }
         }
      },

      /**
       * Selected Item Changed event handler
       * Handles selectedItemAdded and selectedItemRemoved events
       *
       * @method onSelectedItemChanged
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onSelectedItemChanged: function ObjectRenderer_onSelectedItemChanged(layer, args)
      {   
         // Check the event is directed towards this instance
         if ($hasEventInterest(this, args))
         {
            var obj = args[1];
            if (obj && obj.item)
            {
               var button;
               for (var id in this.addItemButtons)
               {
                  if (this.addItemButtons.hasOwnProperty(id))
                  {
                     button = this.addItemButtons[id];
                     Dom.setStyle(button, "display", this.objectFinder.canItemBeSelected(id) ? "inline" : "none");
                  }
               }
            }
         }
      },

      /**
       * Returns Icon datacell formatter
       *
       * @method fnRenderItemIcon
       */
      fnRenderItemIcon: function ObjectRenderer_fnRenderItemIcon()
      {
         var scope = this;
      
         /**
          * Icon datacell formatter
          *
          * @method renderItemIcon
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function ObjectRenderer_renderItemIcon(elCell, oRecord, oColumn, oData)
         {
            var iconSize = scope.options.compactMode ? 16 : 32;

            oColumn.width = iconSize - 6;
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            // Create New item cell type
            if (oRecord.getData("type") == IDENT_CREATE_NEW)
            {
               Dom.addClass(this.getTrEl(elCell), "create-new-row");
               var obj =
               {
                  type: scope.options.createNewItemIcon,
                  description: scope.msg("form.control.object-picker.create-new")
               };
               elCell.innerHTML = scope.renderItem(obj, iconSize, '<div class="icon' + iconSize + '"><span class="new-item-overlay"></span>{icon}</div>');
               return;
            }

            elCell.innerHTML = scope.renderItem(oRecord.getData(), iconSize, '<div class="icon' + iconSize + '">{icon}</div>');
         };
      },

      /**
       * Returns Name datacell formatter
       *
       * @method fnRenderItemName
       */
      fnRenderItemName: function ObjectRenderer_fnRenderItemName()
      {
         var scope = this;
      
         /**
          * Name datacell formatter
          *
          * @method renderItemName
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function ObjectRenderer_renderItemName(elCell, oRecord, oColumn, oData)
         {
            var template = '';

            // Create New item cell type
            if (oRecord.getData("type") == IDENT_CREATE_NEW)
            {
               scope.createNewItemId = Alfresco.util.generateDomId();
               elCell.innerHTML = '<input id="' + scope.createNewItemId + '" type="text" class="create-new-input" tabindex="0" />';
               return;
            }

            if (oRecord.getData("isContainer") ||
                (!oRecord.getData("isContainer") && (scope.options.allowNavigationToContentChildren || oRecord.getData("type") == "cm:category")))
            {
               template += '<h3 class="item-name"><a href="#" class="theme-color-1 parent-' + scope.eventGroup + '">' + scope.resolveName(oRecord) + '</a></h3>';
            }
            else
            {
               template += '<h3 class="item-name">' + scope.resolveName(oRecord) + '</h3>';
            }

            if (!scope.options.compactMode)
            {
               template += '<div class="description">{description}</div>';
            }

            elCell.innerHTML = scope.renderItem(oRecord.getData(), 0, template);
         };
      },

      /**
       * Returns Add button datacell formatter
       *
       * @method fnRenderCellAdd
       */
      fnRenderCellAdd: function ObjectRenderer_fnRenderCellAdd()
      {
         var scope = this;
      
         /**
          * Add button datacell formatter
          *
          * @method renderCellAvatar
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function ObjectRenderer_renderCellAdd(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            
            var containerId = Alfresco.util.generateDomId(),
               button;

            // Create New item cell type
            if (oRecord.getData("type") == IDENT_CREATE_NEW)
            {
               elCell.innerHTML = '<a href="#" class="create-new-item create-new-item-' + scope.eventGroup + '" title="' + scope.msg("form.control.object-picker.create-new") + '" tabindex="0"><span class="createNewIcon">&nbsp;</span></a>';
               return;
            } 

            if (oRecord.getData("selectable"))
            {
               var nodeRef = oRecord.getData("nodeRef"),
                  style = "";

               if (!scope.objectFinder.canItemBeSelected(nodeRef))
               {
                  style = 'style="display: none"';
               }

               elCell.innerHTML = '<a id="' + containerId + '" href="#" ' + style + ' class="add-item add-' + scope.eventGroup + '" title="' + scope.msg("form.control.object-picker.add-item") + '" tabindex="0"><span class="addIcon">&nbsp;</span></a>';
               scope.addItemButtons[nodeRef] = containerId;
            }
         };
      },

      /**
       * Create New Item button click handler
       *
       * @method onCreateNewItem
       */
      onCreateNewItem: function ObjectRenderer_onCreateNewItem()
      {
         var elInput = Dom.get(this.createNewItemId),
            uri = $combine("/", this.options.createNewItemUri).substring(1),
            itemName;
         
         if (elInput)
         {
            itemName = elInput.value;
            if (itemName === null || itemName.length < 1)
            {
                return;
            }
            /**
             * TODO: Validation?!
             */
            Alfresco.util.Ajax.jsonPost(
            {
               url: Alfresco.constants.PROXY_URI + uri,
               dataObj:
               {
                  name: itemName
               },
               successCallback:
               {
                  fn: function ObjectRenderer_onCreateNewItem_successCallback(p_obj)
                  {
                     var response = p_obj.json;
                     if (response && response.nodeRef)
                     {
                        var item =
                        {
                           type: this.options.itemType,
                           name: response.name,
                           nodeRef: response.nodeRef,
                           selectable: true
                        };

                        // Special case for tags, which we want to render differently to categories
                        if (item.type == "cm:category" && response.displayPath.indexOf("/categories/Tags") !== -1)
                        {
                           item.type = "tag";
                        }

                        if (!response.itemExists)
                        {
                           // Item didn't exist - display success message
                           Alfresco.util.PopupManager.displayMessage(
                           {
                              text: this.msg("form.control.object-picker.create-new.success", response.name)
                           });
                           // Add the new item to the DataTable
                           this.widgets.dataTable.addRow(item);
                        }

                        // Automatically select the new item
                        YAHOO.Bubbling.fire("selectedItemAdded",
                        {
                           eventGroup: this,
                           item: item,
                           highlight: true
                        });
                     }
                     elInput.value = "";
                  },
                  scope: this
               },
               failureMessage: this.msg("form.control.object-picker.create-new.failure")
            });
         }
      },

      /**
       * Resolves {name} or {title} should be used to display of the given item.
       * 
       * @method _resolveName
       * @param oRecord
       * @return {name} or {title}
       */
      resolveName: function ObjectRenderer_resolveName(oRecord)
      {
         var value;
         if (oRecord.getData("container") && oRecord.getData("title"))
         {
            switch(oRecord.getData("container")){
              case 'wiki': oRecord._oData.title = oRecord._oData.title.replace(/_/g, " ");
              case 'blog':
              case 'discussions':
              case 'calendar':
              case 'links': value = "{title}"; break;
              default: value = "{name}";
            }
         }
         else
         {
            switch(oRecord.getData("type")){
              case 'dl:dataList':
              case 'fm:topic':
              case 'st:site': value = "{title}"; break;
              default: value = "{name}";
            }
         }
         
         return value;
      },

      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Creates UI controls
       *
       * @method _createControls
       */
      _createControls: function ObjectRenderer__createControls()
      {
         var me = this;

         // DataSource definition  
         var pickerChildrenUrl = null;
         
         if(this.options.finderAPI != null) {
            var substitutionOptions = 
            {
                  itemFamily: this.options.itemFamily
            };
            
            pickerChildrenUrl = YAHOO.lang.substitute(this.options.finderAPI, substitutionOptions)
         }
         else
         {
           // Revert to default
            pickerChildrenUrl = Alfresco.constants.PROXY_URI + "api/forms/picker/" + this.options.itemFamily;
         }
         
         this.widgets.dataSource = new YAHOO.util.DataSource(pickerChildrenUrl,
         {
            responseType: YAHOO.util.DataSource.TYPE_JSON,
            connXhrMode: "queueRequests",
            responseSchema:
            {
               resultsList: "items",
               metaFields:
               {
                  parent: "parent"
               }
            }
         });

         this.widgets.dataSource.doBeforeParseData = function ObjectRenderer_doBeforeParseData(oRequest, oFullResponse)
         {
            var updatedResponse = oFullResponse;
            
            if (oFullResponse)
            {
               var items = oFullResponse.data.items;

               // Crop item list to max length if required
               if (me.options.maxSearchResults > -1 && items.length > me.options.maxSearchResults)
               {
                  items = items.slice(0, me.options.maxSearchResults-1);
               }
               
               // Add the special "Create new" record if required
               if (me.options.createNewItemUri !== "" && me.createNewItemId === null)
               {
                  items = [{ type: IDENT_CREATE_NEW }].concat(items);
               }
               
               // Special case for tags, which we want to render differently to categories
               var index, item;
               for (index in items)
               {
                  if (items.hasOwnProperty(index))
                  {
                     item = items[index];
                     if (item.type == "cm:category" && item.displayPath.indexOf("/categories/Tags") !== -1)
                     {
                        item.type = "tag";
                        // Also set the parent type to display the drop-down correctly. This may need revising for future type support.
                        oFullResponse.data.parent.type = "tag";
                     }
                  }
               }
               
               // Notify interested parties of the parent details
               YAHOO.Bubbling.fire("parentDetails",
               {
                  eventGroup: me,
                  parent: oFullResponse.data.parent
               });

               // we need to wrap the array inside a JSON object so the DataTable is happy
               updatedResponse =
               {
                  parent: oFullResponse.data.parent,
                  items: items
               };
            }
            
            return updatedResponse;
         };

         // DataTable column defintions
         var columnDefinitions =
         [
            { key: "nodeRef", label: "Icon", sortable: false, formatter: this.fnRenderItemIcon(), width: this.options.compactMode ? 10 : 26 },
            { key: "name", label: "Item", sortable: false, formatter: this.fnRenderItemName() },
            { key: "add", label: "Add", sortable: false, formatter: this.fnRenderCellAdd(), width: 16 }
         ];
         
         var initialMessage = this.msg("form.control.object-picker.items-list.loading");
         if (this._inAuthorityMode())
         {
            initialMessage = this.msg("form.control.object-picker.items-list.search");
         }

         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-results", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: 100,
            initialLoad: false,
            MSG_EMPTY: initialMessage
         });

         // Rendering complete event handler
         this.widgets.dataTable.subscribe("renderEvent", function()
         {
            if (this.options.createNewItemUri !== "")
            {
               if (!this.widgets.enterListener)
               {
                  this.widgets.enterListener = new KeyListener(this.createNewItemId,
                  {
                     keys: KeyListener.KEY.ENTER
                  },
                  {
                     fn: function ObjectRenderer__createControls_fn(eventName, keyEvent, obj)
                     {
                        // Clear any previous autocomplete timeout
                        if (this.autocompleteDelayId != -1)
                        {
                           window.clearTimeout(this.autocompleteDelayId);
                        }
                        this.onCreateNewItem();
                        Event.stopEvent(keyEvent[1]);
                        return false;
                     },
                     scope: this,
                     correctScope: true
                  }, YAHOO.env.ua.ie > 0 ? KeyListener.KEYDOWN : "keypress");
                  this.widgets.enterListener.enable();
               }
               
               me.autocompleteDelayId = -1;
               Event.addListener(this.createNewItemId, "keyup", function(p_event)
               {
                  var sQuery = this.value;

                  // Filter out keys that don't trigger queries
                  if (!Alfresco.util.isAutocompleteIgnoreKey(p_event.keyCode))
                  {
                     // Clear previous timeout
                     if (me.autocompleteDelayId != -1)
                     {
                        window.clearTimeout(me.autocompleteDelayId);
                     }
                     // Set new timeout
                     me.autocompleteDelayId = window.setTimeout(function()
                     {
                        YAHOO.Bubbling.fire("refreshItemList",
                        {
                           eventGroup: me,
                           searchTerm: sQuery
                        });
                     }, 500);
                  }
               });
               
               Dom.get(this.createNewItemId).focus();
            }
         }, this, true);
         
         // Hook add item action click events (for Compact mode)
         var fnAddItemHandler = function ObjectRenderer__createControls_fnAddItemHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               var target, rowId, record;

               target = args[1].target;
               rowId = target.offsetParent;
               record = me.widgets.dataTable.getRecord(rowId);
               if (record)
               {
                  YAHOO.Bubbling.fire("selectedItemAdded",
                  {
                     eventGroup: me,
                     item: record.getData(),
                     highlight: true
                  });
               }
            }
            return true;
         };
         YAHOO.Bubbling.addDefaultAction("add-" + this.eventGroup, fnAddItemHandler, true);

         // Hook create new item action click events (for Compact mode)
         var fnCreateNewItemHandler = function ObjectRenderer__createControls_fnCreateNewItemHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               me.onCreateNewItem();
            }
            return true;
         };
         YAHOO.Bubbling.addDefaultAction("create-new-item-" + this.eventGroup, fnCreateNewItemHandler, true);

         // Hook navigation action click events
         var fnNavigationHandler = function ObjectRenderer__createControls_fnNavigationHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               var target, rowId, record;
         
               target = args[1].target;
               rowId = target.offsetParent;
               record = me.widgets.dataTable.getRecord(rowId);
               if (record)
               {
                  YAHOO.Bubbling.fire("parentChanged",
                  {
                     eventGroup: me,
                     label: record.getData("name"),
                     nodeRef: record.getData("nodeRef")
                  });
               }
            }
            return true;
         };
         YAHOO.Bubbling.addDefaultAction("parent-" + this.eventGroup, fnNavigationHandler, true);
      },
      
      /**
       * Updates item list by calling data webscript
       *
       * @method _updateItems
       * @param nodeRef {string} Parent nodeRef
       * @param searchTerm {string} Search term
       */
      _updateItems: function ObjectRenderer__updateItems(nodeRef, searchTerm)
      {
         // Empty results table - leave tag entry if it's been rendered
         if (this.createNewItemId !== null)
         {
            this.widgets.dataTable.deleteRows(1, this.widgets.dataTable.getRecordSet().getLength() - 1);
         }
         else
         {
            this.widgets.dataTable.set("MSG_EMPTY", this.msg("form.control.object-picker.items-list.loading"));
            this.widgets.dataTable.deleteRows(0, this.widgets.dataTable.getRecordSet().getLength());
         }
         
         var successHandler = function ObjectRenderer__updateItems_successHandler(sRequest, oResponse, oPayload)
         {
            this.options.parentNodeRef = oResponse.meta.parent ? oResponse.meta.parent.nodeRef : nodeRef;
            this.widgets.dataTable.set("MSG_EMPTY", this.msg("form.control.object-picker.items-list.empty"));
            if (this.createNewItemId !== null)
            {
               this.widgets.dataTable.onDataReturnAppendRows.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
            }
            else
            {
               this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
            }
         };
         
         var failureHandler = function ObjectRenderer__updateItems_failureHandler(sRequest, oResponse)
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
                  this.widgets.dataTable.set("MSG_ERROR", response.message);
                  this.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
               }
               catch(e)
               {
               }
            }
         };
         
         // build the url to call the pickerchildren data webscript
         var url = this._generatePickerChildrenUrlPath(nodeRef) + this._generatePickerChildrenUrlParams(searchTerm);
         
         if (Alfresco.logger.isDebugEnabled())
         {
            Alfresco.logger.debug("Generated pickerchildren url fragment: " + url);
         }
         
         // call the pickerchildren data webscript
         this.widgets.dataSource.sendRequest(url,
         {
            success: successHandler,
            failure: failureHandler,
            scope: this
         });
         
         // the start location is now resolved
         this.startLocationResolved = true;
      },
      
      /**
       * Determines whether the picker is in 'authority' mode.
       * 
       * @method _inAuthorityMode
       * @return true if the picker is being used to find authorities i.e. users and groups
       */
      _inAuthorityMode: function ObjectRenderer__inAuthorityMode()
      {
         return (this.options.itemFamily == "authority");
      },
      
      /**
       * Generates the path fragment of the pickerchildren webscript URL.
       * 
       * @method _generatePickerChildrenUrlPath
       * @param nodeRef NodeRef of the parent
       * @return The generated URL
       */
      _generatePickerChildrenUrlPath: function ObjectRenderer__generatePickerChildrenUrlPath(nodeRef)
      {
         // generate the path portion of the url
         return $combine("/", nodeRef.replace("://", "/"), "children");
      },
      
      /**
       * Generates the query parameters for the pickerchildren webscript URL.
       * 
       * @method _generatePickerChildrenUrlParams
       * @param searchTerm The search term
       * @return The generated URL
       */
      _generatePickerChildrenUrlParams: function ObjectRenderer__generatePickerChildrenUrlParams(searchTerm)
      {
         var params = "?selectableType=" + this.options.itemType + "&searchTerm=" + encodeURIComponent(searchTerm) + 
                      "&size=" + this.options.maxSearchResults;
         
         // if an XPath start location has been provided and it has not been resolved 
         // yet, pass it to the pickerchildren script as a parameter
         if (!this.startLocationResolved && this.objectFinder.options.startLocation &&
              this.objectFinder.options.startLocation.charAt(0) == "/")
         {
            params += "&xpath=" + encodeURIComponent(this.objectFinder.options.startLocation);
         }
         
         // has a rootNode been specified?
         if (this.objectFinder.options.rootNode)
         {
            var rootNode = null;

            if (this.objectFinder.options.rootNode.charAt(0) == "{")
            {
               if (this.objectFinder.options.rootNode == "{companyhome}")
               {
                  rootNode = "alfresco://company/home";
               }
               else if (this.objectFinder.options.rootNode == "{userhome}")
               {
                  rootNode = "alfresco://user/home";
               }
               else if (this.objectFinder.options.rootNode == "{siteshome}")
               {
                  rootNode = "alfresco://sites/home";
               }
               else if (this.options.rootNode == "{shared}")
               {
                  rootNode = "alfresco://company/shared";
               }
            }
            else
            {
               // rootNode is either an xPath expression or a nodeRef
               rootNode = this.objectFinder.options.rootNode;
            }
            if (rootNode !== null)
            {
               params += "&rootNode=" + encodeURIComponent(rootNode);
            }
         }
         
         if (this.objectFinder.options.params)
         {
            params += "&" + encodeURI(this.objectFinder.options.params);
         }
         
         return params;
      }
   });
})();
