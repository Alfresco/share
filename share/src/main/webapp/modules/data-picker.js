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
 * Data picker module
 *
 * An easy extensible data picker dialog that lets overriden class define a number of tabs
 * containing a tree and/or a list and the data sources to them.
 *
 * @namespace Alfresco.module
 * @class Alfresco.module.DataPicker
 */
(function()
{
   /**
   * YUI Library aliases
   */
   var Dom = YAHOO.util.Dom,
      KeyListener = YAHOO.util.KeyListener,
      Selector = YAHOO.util.Selector;

   /**
    * Alfresco Slingshot aliases
    */
    var $html = Alfresco.util.encodeHTML,
       $combine = Alfresco.util.combinePaths,
       $hasEventInterest = Alfresco.util.hasEventInterest;

   Alfresco.module.DataPicker = function(htmlId)
   {
      Alfresco.module.DataPicker.superclass.constructor.call(this, "Alfresco.module.DataPicker", htmlId, ["button", "container", "connection", "datasource", "datatable", "json", "tabview", "treeview", "resize"]);
      this.widgets.dataSources = {};
      this.widgets.dataTables = {};
      return this;
   };

   YAHOO.extend(Alfresco.module.DataPicker, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       */
      options:
      {

         /**
          * Template URL
          *
          * @property templateUrl
          * @type string
          * @default Alfresco.constants.URL_SERVICECONTEXT + "modules/data-picker"
          */
         templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/data-picker",

         /**
          * Tokens that can be used when constructing the url to get treeNodes or listItems in the tabs option below.
          *
          * Note that additional properties from the clicked tree node will be added as well under the "data" namespace.
          * I.e. Its possible to create a url like "{url.proxy}api/sites?nf={data.id}"
          * where "data.id" will be the "id" property of the clicked node. (oNode.node.data.id)
          *
          * @property urlTokens
          * @type string
          */
         urlTokens:
         {
            "person.username": encodeURIComponent(Alfresco.constants.USERNAME),
            "url.proxy": Alfresco.constants.PROXY_URI_RELATIVE,
            "url.service": Alfresco.constants.URL_SERVICECONTEXT
         },

         /**
          * Default tab configuration.
          * Other tabs may be added by overriding setOptions() and adding a new tab.
          *
          * @property tabs
          * @type object
          * @default []
          */
         tabs: [],

         /**
          * The selection mode to use in the lists
          *
          * @property dataTableSelectionMode
          * @type string
          * @default "single"
          */
         dataTableSelectionMode: "single",

         /**
          * The dataTable dolumn definition to use in the lists
          *
          * @property dataTableColumnDefinitions
          * @type []
          * @default A single column displaying the label
          */
         dataTableColumnDefinitions: [
            {
               key: "label", sortable: true
            }
         ]

      },

      /**
       *
       *
       * @property selectedItem
       * @type object
       */
      selectedItem: null,

      /**
       * Main entry point
       * @method showDialog
       */
      showDialog: function PP_showDialog()
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
      onTemplateLoaded: function PP_onTemplateLoaded(response)
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
         this.widgets.dialog = Alfresco.util.createYUIPanel(dialogDiv);

         // Buttons
         this.widgets.okButton = Alfresco.util.createYUIButton(this, "ok-button", this.onOkButtonClick);
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel-button", this.onCancelButtonClick);

         // Create the tabs
         this._createTabs();

         // And finally show the dialog
         this._showDialog();
      },

      /**
       * Creates the tabs accodring to the options
       *
       * @method _createTabs
       * @private
       */
      _createTabs: function PP_onTemplateLoaded()
      {
         // Only use tab view if there is more than one tab
         var il = this.options.tabs.length;
         if (il > 1)
         {
            this.widgets.tabView = new YAHOO.widget.TabView(this.id + "-tabs");
         }
         else
         {
            Dom.setStyle(this.id + "-tabs", "display", "none");
         }

         // Create a tab from options
         var counter = 0, tabObj, tabRootEl, tab;
         for (var i = 0; i < il; i++)
         {
            // Tab
            tabObj = this.options.tabs[i];
            if (il > 1)
            {
               tab = new YAHOO.widget.Tab(
               {
                  label: this.msg("tabs." + tabObj.id),
                  content: '',
                  active: counter == 0
               });
               this.widgets.tabView.addTab(tab);
               tabRootEl = tab.get("contentEl");
               Dom.addClass(tabRootEl, tabObj.id);
            }
            else
            {
               tabRootEl = this.widgets.dialog.body;
            }
            if (tabObj.treeNodes)
            {
               this._setupTree(tabObj, tabRootEl);
            }

            // List
            if (tabObj.listItems)
            {
               // Create list
               this._setupList(tabObj, tabRootEl);

               // Load list with data if present
               this._loadListItems(tabObj, null, tabObj.listItems, "tabs." + tabObj.id + ".list.data");
            }

            counter++;
         }
      },

      /**
       * Create tree in tab
       *
       * @method _setupList
       * @param tabObj {object} Object describing the tab
       * @param tabRootEl {HTMLElement} Element to append tree to
       * @private
       */
      _setupTree: function PP__setupList(tabObj, tabRootEl)
      {
         var me = this;

         // Tree View
         var treeEl = document.createElement("div");
         Dom.addClass(treeEl, "data-picker-tree");
         tabRootEl.appendChild(treeEl);
         var tree = new YAHOO.widget.TreeView(treeEl),
               root = tree.getRoot();

         // Load/Create provided tree nodes
         this._loadTreeNodes(root, null, tabObj.treeNodes, "tabs." + tabObj.id + ".tree.data", null);

         // Tree node load function on user clicks
         var loadTreeNodes = function(oNode, yuiTreeCallback)
         {
            var nodeObj = oNode.data.node;
            var n = oNode,
                  path = "";
            do
            {
               path = "." + n.data.node.id + path;
               n = n.parent;
            }
            while (!n.isRoot());
            me._loadTreeNodes(oNode, nodeObj, nodeObj.treeNodes, "tabs." + tabObj.id + ".tree.data" + path, yuiTreeCallback);
         };

         // List item load function on user clicks
         var loadListItems = function(oEvent)
         {
            var nodeObj = oEvent.node.data,
               listItems = nodeObj.listItems;
            if (listItems)
            {
               // Load list
               var n = oEvent.node,
                  path = "";
               do
               {
                  path = "." + n.data.id + path;
                  n = n.parent;
               }
               while (!n.isRoot());
               me._loadListItems(tabObj, nodeObj, listItems, "tabs." + tabObj.id + ".tree" + path + ".list.data");
            }
         };

         tree.setDynamicLoad(loadTreeNodes);
         tree.subscribe("clickEvent", loadListItems);
         tree.render(treeEl);
      },


      /**
       * Create list in tab
       *
       * @method _setupList
       * @param tabObj {object} Object describing the tab
       * @param tabRootEl {HTMLElement} Element to append tree to
       * @private
       */
      _setupList: function PP__setupList(tabObj, tabRootEl)
      {
         // The data tables underlying data source.
         var dataSource = new YAHOO.util.DataSource([],
         {
            responseType: YAHOO.util.DataSource.TYPE_JSARRAY
         });

         // Get the data tables column labels
         for (var i = 0, il = this.options.dataTableColumnDefinitions.length, def; i < il; i++)
         {
            def = this.options.dataTableColumnDefinitions[i];
            if (!def.label)
            {
               def.label = this.msg("tabs." + tabObj.id + ".list." + def.key);
            }
         }

         // DataTable definition
         var dataTableEl = document.createElement("div");
         Dom.addClass(dataTableEl, "data-picker-list");
         tabRootEl.appendChild(dataTableEl);
         var dataTable = new YAHOO.widget.DataTable(dataTableEl, this.options.dataTableColumnDefinitions, dataSource,
         {
            renderLoopSize: 32,
            initialLoad: false,
            MSG_EMPTY: this.msg("message.instructions"),
            selectionMode: this.options.dataTableSelectionMode
         });

         // Subscribe to events for row selection
         dataTable.subscribe("rowMouseoverEvent", dataTable.onEventHighlightRow);
         dataTable.subscribe("rowMouseoutEvent", dataTable.onEventUnhighlightRow);
         dataTable.subscribe("rowClickEvent", function (oEvent, obj) {
            for (var key in this.widgets.dataTables)
            {
               // Unselect possible selected item in dataTable in other tab
               var dt = this.widgets.dataTables[key];
               dt.unselectAllRows();
               if (key == obj.tabObj.id)
               {
                  // Set selected row in dataTable and selected item in this component
                  dt.selectRow(oEvent.target);
                  this.selectedItem = dt.getRecord(oEvent.target).getData();
               }
            }
         }, {
            tabObj: tabObj
         }, this);

         this.widgets.dataTables[tabObj.id] = dataTable;
      },

      /**
       * Creates a url from a url pattern and obj with values
       *
       * @method _buildUrl
       * @param obj {object} The object to get the value form
       * @param url {string} The url to build using values from obj and this.options.urlTokens
       * @private
       */
      _buildUrl: function PP__buildUrl(obj, url)
      {
         // Build url
         var keys = {},
            val;
         if (obj)
         {
            for (var key in obj)
            {
               if (obj.hasOwnProperty(key))
               {
                  val = obj[key];
                  keys["node." + key] = YAHOO.lang.isString(val) ? val.replace(":", "_") : val;
               }
            }
         }
         return YAHOO.lang.substitute(url, YAHOO.lang.merge(keys, this.options.urlTokens));
      },

      /**
       * Get a property value from an object.
       *
       * I.e. The following will use shortName as the "id" and return "engineering"
       * this._getValue({shortName: "engineering"}, "id", {"id": "{item.shortName}"}, "item")
       *
       * @method _getValue
       * @param dataObj {object} The object to get the value form
       * @param propertyName {string} The name of the property to get form the dataObj
       * @param descriptorObj {object} Object describing which property in dataObj that matches the propertyName
       * @param dataObjType {string} String describing the type of the dataObj ["node"|"item"]
       * @private
       */
      _getValue: function PP__getValue(dataObj, propertyName, descriptorObj, dataObjType)
      {
         // If descriptor describes what property to use
         if (descriptorObj && descriptorObj[propertyName])
         {

            if (descriptorObj[propertyName].indexOf("{") > -1)
            {
               var keys = {};
               for (var key in dataObj)
               {
                  if (dataObj.hasOwnProperty(key))
                  {
                     keys[dataObjType + "." + key] = dataObj[key];
                  }
               }
               var val = YAHOO.lang.substitute(descriptorObj[propertyName], keys);
               if (val != descriptorObj[propertyName])
               {
                  return val;
               }
               else
               {
                  return null;
               }
            }
            else
            {
               return descriptorObj[propertyName];
            }
         }

         // If object has the given propertyName use that
         if (dataObj[propertyName])
         {
            return dataObj[propertyName];
         }
      },

      /**
       * Get the label from an object.
       *
       * @method _getLabel
       * @dataObj {object} The object to get the value form
       * @descriptorObj {object} Object describing which property in dataObj that matches the propertyName
       * @private
       */
      _getLabel: function PP__getLabel(dataObj, descriptorObj, msgPath, dataObjType)
      {
         var label = this._getValue(dataObj, "label", descriptorObj, dataObjType);
         if (label)
         {
            return label;
         }
         else
         {
            var id = this._getValue(dataObj, "id", descriptorObj, dataObjType),
               msgKey = msgPath + "." + id;
            label = this.msg(msgKey);
            return label != msgKey ? label : id;
         }
      },

      /**
       * Create tree nodes
       *
       * @method _loadTreeNodes
       * @param parent {Node} The parent to the nodes to create
       * @param parentNodeObj {object|null} An object with info describing the clicked parent node
       *                      or null if treeNodeObjs conatins the top nodes
       * @param treeNodeObjs {array|object} Either array of objects with info describing how node shall appear
       *                     or an object describing how to retreive a json array from the server
       * @param msgPath {string} Path possibly matching an entry in the i18n .properties file
       * @param yuiTreeCallback {function} Method to call after loading is finished
       * @private
       */
      _loadTreeNodes: function PP__loadTreeNodes(parent, parentNodeObj, treeNodeObjs, msgPath, yuiTreeCallback)
      {
         if (YAHOO.lang.isObject(treeNodeObjs) && treeNodeObjs.url)
         {
            // Request the treeNodes from the server
            var url = this._buildUrl(parentNodeObj, treeNodeObjs.url, "node");
            Alfresco.util.Ajax.jsonGet(
            {
               url: url,
               successCallback:
               {
                  fn: function (response, obj)
                  {
                     // Get the data array with tree nodes from the response
                     var nodeObjs = response.json;
                     if (obj.treeNodeObjs.path)
                     {
                        nodeObjs = Alfresco.util.findValueByDotNotation(nodeObjs, obj.treeNodeObjs.path);
                     }
                     if (YAHOO.lang.isArray(nodeObjs))
                     {
                        // Create the tree nodes
                        this._loadTreeNodes(obj.parentNode, obj.parentNodeObj, nodeObjs, obj.msgPath);
                        obj.yuiTreeCallback();
                     }
                     else
                     {
                        throw Error("Could not find an array in the response from: " + url);
                     }
                  },
                  obj:
                  {
                     parentNode: parent,
                     parentNodeObj: parentNodeObj,
                     treeNodeObjs: treeNodeObjs,
                     yuiTreeCallback: yuiTreeCallback,
                     msgPath: msgPath
                  },
                  scope: this
               }
            });
         }
         else if (YAHOO.lang.isArray(treeNodeObjs))
         {
            // Create the tree nodes in the tree view
            var descriptorObj = parentNodeObj ? parentNodeObj.treeNodes : {},
               n;

            if (descriptorObj.dataModifier)
            {
               treeNodeObjs = descriptorObj.dataModifier.call(this, treeNodeObjs, descriptorObj);
            }

            // First prepare the node objects with the correct label etc
            var nodes = [],
               node;
            for (var j = 0, jl = treeNodeObjs.length; j < jl; j++)
            {
               n = treeNodeObjs[j];
               node =  {
                  id: this._getValue(n, "id", descriptorObj, "node"),
                  title: this._getValue(n, "title", descriptorObj, "node"),
                  label: this._getLabel(n, descriptorObj, msgPath, "node"),
                  listItems: n.listItems ? n.listItems : descriptorObj.listItems,
                  treeNodes: n.treeNodes ? n.treeNodes : descriptorObj.treeNodes,
                  node: n
               };
               node.isLeaf = !n.treeNodes;
               nodes.push(node);
            }

            // Sort the nodes
            nodes.sort(this._sortList);

            // Add the nodes to the tree
            for (j = 0, jl = nodes.length; j < jl; j++)
            {
               new YAHOO.widget.TextNode(nodes[j], parent);
            }
         }
      },


      /**
       * Load list items
       *
       * @method _loadListItems
       * @param tabObj {object} Object describing the tab that the list lives in
       * @param parentNodeObj {object|null} An object describing the cliecked tree node or null if none was clicked
       * @param listItemObjs {array|object} Either array of objects with info describing how items shall appear
       *                    or an object describing how to retreive a json array from the server
       * @param msgPath {string} Path possibly matching an entry in the i18n .properties file
       * @private
       */
      _loadListItems: function PP__loadListItems(tabObj, parentNodeObj, listItemObjs, msgPath)
      {
         // Empty results table
         var dataTable = this.widgets.dataTables[tabObj.id];
         dataTable.deleteRows(0, dataTable.getRecordSet().getLength());

         if (YAHOO.lang.isObject(listItemObjs) && listItemObjs.url)
         {
            // Request the treeNodes from the server
            var url = this._buildUrl(parentNodeObj.node, listItemObjs.url);
            Alfresco.util.Ajax.jsonGet(
            {
               url: url,
               successCallback:
               {
                  fn: function (response, obj)
                  {
                     // Get the data array with tree nodes from the response
                     var itemObjs = response.json;
                     if (obj.listItemObjs.path)
                     {
                        itemObjs = Alfresco.util.findValueByDotNotation(itemObjs, obj.listItemObjs.path);
                     }
                     if (YAHOO.lang.isArray(itemObjs))
                     {
                        // Create the items
                        this._loadListItems(obj.tabObj, obj.parentNodeObj, itemObjs, obj.msgPath);
                     }
                     else
                     {
                        throw Error("Could not find an array in the response from: " + url);
                     }
                  },
                  obj:
                  {
                     tabObj: tabObj,
                     dataTable: dataTable,
                     parentNodeObj: parentNodeObj,
                     listItemObjs: listItemObjs,
                     msgPath: msgPath
                  },
                  scope: this
               }
            });
         }
         else if (YAHOO.lang.isArray(listItemObjs))
         {
            // Create the items in the data table
            var descriptorObj = parentNodeObj ? parentNodeObj.listItems : {},
               items = [],
               itemObj;

            if (descriptorObj.dataModifier)
            {
               listItemObjs = descriptorObj.dataModifier.call(this, listItemObjs, descriptorObj);
            }

            for (var j = 0, jl = listItemObjs.length; j < jl; j++)
            {
               itemObj = listItemObjs[j];
               items.push({
                  id: this._getValue(itemObj, "id", descriptorObj, "item"),
                  type: this._getValue(itemObj, "type", descriptorObj, "item"),
                  label: this._getLabel(itemObj, descriptorObj, msgPath, "item"),
                  item: itemObj
               });
            }

            // Sort items
            items.sort(this._sortList);

            // Add all items to table
            dataTable.addRows(items, 0);
         }
      },

      /**
       * Internal show dialog function
       * @method _showDialog
       * @protected
       */
      _showDialog: function PP__showDialog()
      {
         // Set dialog title
         Dom.get(this.id + "-title").innerHTML = this.msg("header");

         // Enable buttons
         this.widgets.okButton.set("disabled", false);
         this.widgets.cancelButton.set("disabled", false);

         // Show the dialog
         this.widgets.dialog.show();
      },

      /**
       * Sort list elements  by label
       *
       * @method _sortList
       * @protected
       */
      _sortList: function PP__showDialog(obj1, obj2)
      {
         var label1 = obj1.label ? obj1.label.toLowerCase() : "",
            label2 = obj2.label ? obj2.label.toLowerCase() : "";
         return (label1 > label2) ? 1 : (label1 < label2) ? -1 : 0;
      },

      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * Dialog OK button event handler
       *
       * @method onOkButtonClick
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onOkButtonClick: function PP_onOkButtonClick(e, p_obj)
      {
         // Close dialog and fire event so other components may use the selected folder
         this.widgets.dialog.hide();
         YAHOO.Bubbling.fire("dataItemSelected",
         {
            selectedItem: this.selectedItem,
            eventGroup: this
         });
      },

      /**
       * Dialog Cancel button event handler
       *
       * @method onCancelButtonClick
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onCancelButtonClick: function PP_onCancelButtonClick(e, p_obj)
      {
         this.widgets.dialog.hide();
         YAHOO.Bubbling.fire("dataItemSelectionCancelled",
         {
            eventGroup: this
         });
      }

   });

   /* Dummy instance to load optional YUI components early */
   var dummyInstance = new Alfresco.module.DataPicker("null");
})();

