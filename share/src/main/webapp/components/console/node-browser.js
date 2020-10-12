/**
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
 * ConsoleNodeBrowser tool component.
 * 
 * @namespace Alfresco
 * @class Alfresco.ConsoleNodeBrowser
 * @author wabson
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
   var $html = Alfresco.util.encodeHTML;

   /**
    * ConsoleNodeBrowser constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.ConsoleNodeBrowser} The new ConsoleNodeBrowser instance
    * @constructor
    */
   Alfresco.ConsoleNodeBrowser = function(htmlId)
   {
      this.name = "Alfresco.ConsoleNodeBrowser";
      Alfresco.ConsoleNodeBrowser.superclass.constructor.call(this, htmlId);
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datasource", "datatable", "json", "history"], this.onComponentsLoaded, this);

      /* Decoupled event listeners */
      YAHOO.Bubbling.on("viewNodeClick", this.onViewNodeClick, this);

      /* Define panel handlers */
      var parent = this;
      
      // NOTE: the panel registered first is considered the "default" view and is displayed first
      
      /* Search Panel Handler */
      SearchPanelHandler = function ConsoleNodeBrowser_SearchPanelHandler_constructor()
      {
         SearchPanelHandler.superclass.constructor.call(this, "search");
      };
      
      YAHOO.extend(SearchPanelHandler, Alfresco.ConsolePanelHandler,
      {

         /**
          * INSTANCE VARIABLES
          */

         /**
          * Keeps track if this panel is searching or not
          *
          * @property isSearching
          * @type Boolean
          */
         isSearching: false,

         /**
          * PANEL LIFECYCLE CALLBACKS
          */
         
         /**
          * Called by the ConsolePanelHandler when this panel shall be loaded
          *
          * @method onLoad
          */
         onLoad: function ConsoleNodeBrowser_SearchPanelHandler_onLoad()
         {
             // selectedMenuItemChange handler to make menu buttons act like select lists
             // shared by store menu and language menu
             var onSelectedMenuItemChange = function (event) {
                 var oMenuItem = event.newValue;
                 this.set("label", (oMenuItem.cfg.getProperty("text")));
                 this.set("value", (oMenuItem.cfg.getProperty("text")));
                 return;
            };
            
            var onMenuClick = function (p_sType, p_aArgs) {
                var oEvent = p_aArgs[0],    //  DOM event
                    oMenuItem = p_aArgs[1]; //  MenuItem instance that was the target of the event
                
                if (oMenuItem)
                {
                    this.set("label", (oMenuItem.cfg.getProperty("text")));
                    this.set("value", (oMenuItem.cfg.getProperty("text")));
                }
            };
            
            var onMenuRender = function (type, args, button) {
                button.set("selectedMenuItem", this.getItem(0));
            };
            
            // Search button
            parent.widgets.searchButton = Alfresco.util.createYUIButton(parent, "search-button", parent.onSearchClick);
            
            // Store menu button
            Alfresco.util.Ajax.request({
                url: Alfresco.constants.PROXY_URI + "slingshot/node/stores",
                successCallback:
                {
                   fn: function(p_obj) {
                       var stores = p_obj.json.stores;
                       parent.widgets.storeMenuButton = new YAHOO.widget.Button(parent.id + "-store-menu-button", { 
                           type: "menu",
                           menu: stores,
                           lazyloadmenu: false
                        });
                        parent.widgets.storeMenuButton.set("value", parent.store);
                        if(stores.indexOf(parent.store) >= 0)
                        {
                           parent.widgets.storeMenuButton.set("label", parent.store);
                        }
                        else
                        {
                           parent.widgets.storeMenuButton.set("label", encodeURIComponent(parent.store));
                        }

                       parent.widgets.storeMenuButton.on("selectedMenuItemChange", onSelectedMenuItemChange);
                   },
                   scope: this
                },
                failureMessage: parent._msg("message.getstores-failure")
            });
            
            // Query language button
            parent.widgets.langMenuButton = new YAHOO.widget.Button(parent.id + "-lang-menu-button", { 
                type: "menu",
                menu: parent.id + "-lang-menu-select"
             });
            parent.widgets.langMenuButton.on("selectedMenuItemChange", function() {
               onSelectedMenuItemChange.apply(this, arguments);
               // Disable the query field if 'storeroot' is selected
               Dom.get(parent.id + "-search-text").disabled = (parent.widgets.langMenuButton.get("value") == "storeroot");
            });
            parent.widgets.langMenuButton.set("value", parent.searchLanguage);
            parent.widgets.langMenuButton.set("label", encodeURIComponent(parent.searchLanguage));
            
            // DataTable and DataSource setup
            parent.widgets.dataSource = new YAHOO.util.DataSource(Alfresco.constants.PROXY_URI + "slingshot/node/search",
            {
               responseType: YAHOO.util.DataSource.TYPE_JSON,
               responseSchema:
               {
                  resultsList: "results",
                  metaFields:
                  {
                     recordOffset: "startIndex",
                     totalRecords: "totalResults",
                     searchElapsedTime: "searchElapsedTime"
                  }
               }
            });
            
            var me = this;
            
            // Work to be performed after data has been queried but before display by the DataTable
            parent.widgets.dataSource.doBeforeParseData = function PeopleFinder_doBeforeParseData(oRequest, oFullResponse)
            {
               var updatedResponse = oFullResponse;
               
               if (oFullResponse && oFullResponse.results)
               {
                  var items = oFullResponse.results,
                     searchElapsedTime = parseInt(oFullResponse.searchElapsedTime, 10);
                  
                  // initial sort by username field
                  items.sort(function(a, b)
                  {
                     return (a.name > b.name);
                  });
                  
                  // we need to wrap the array inside a JSON object so the DataTable gets the object it expects
                  updatedResponse =
                  {
                     "results": items
                  };
               }

               var elapsedTimeMsg = "";
               if (searchElapsedTime < 1000) // Less than 1s
               {
                  elapsedTimeMsg = "" + searchElapsedTime + parent.msg("label.elapsedMilliseconds"); // e.g. 15ms
               }
               else if (searchElapsedTime >= 1000 && searchElapsedTime < 10000) // Between 1 and 10s
               {
                  elapsedTimeMsg = "" + (Math.round(searchElapsedTime/100) / 10) + 
                     parent.msg("label.elapsedSeconds"); // e.g. 2.6s
               }
               else // More than 10s
               {
                  elapsedTimeMsg = "" + Math.round(searchElapsedTime/1000) + parent.msg("label.elapsedSeconds"); // e.g. 18s
               }

               // update Results Bar message with number of results found and how long it took
               if (items && items.length < parent.options.maxSearchResults)
               {
                  me._setResultsMessage("message.results", $html(parent.searchTerm), items.length, elapsedTimeMsg);
               }
               else
               {
                  me._setResultsMessage("message.maxresults", $html(parent.searchTerm), parent.options.maxSearchResults, elapsedTimeMsg);
               }
               
               return updatedResponse;
            };
            
            // Setup the main datatable
            this._setupDataTable();
         },
         
         onShow: function ConsoleNodeBrowser_SearchPanelHandler_onShow()
         {
            if (!Dom.get(parent.id + "-search-text").disabled)
            {
               Dom.get(parent.id + "-search-text").focus();
               Dom.get(parent.id + "-search-text").select();
            }
         },
         
         onUpdate: function ConsoleNodeBrowser_SearchPanelHandler_onUpdate()
         {
            // update the text field - as this event could come from bookmark, navigation or a search button click
            var searchTermElem = Dom.get(parent.id + "-search-text");
            searchTermElem.value = parent.searchTerm;
            
            // Update language menu
            parent.widgets.langMenuButton.set("value", parent.searchLanguage);
            parent.widgets.langMenuButton.set("label", encodeURIComponent(parent.searchLanguage));
            
            // Disable the query field if 'storeroot' is selected
            Dom.get(parent.id + "-search-text").disabled = (parent.widgets.langMenuButton.get("value") == "storeroot");
            
            // Update store menu
            if (parent.widgets.storeMenuButton)
            {
                Alfresco.util.Ajax.request({
                    url: Alfresco.constants.PROXY_URI + "slingshot/node/stores",
                    successCallback:
                    {
                       fn: function(p_obj) {
                           var stores = p_obj.json.stores;
                           if(stores.indexOf(parent.store) >= 0)
                           {
                              parent.widgets.storeMenuButton.set("label", parent.store);
                           }
                           else
                           {
                              parent.widgets.storeMenuButton.set("label", encodeURIComponent(parent.store));
                           }
                       },
                       scope: this
                    },
                    failureMessage: parent._msg("message.getstores-failure")
                });
            }

            // check search length again as we may have got here via history navigation
            if (!this.isSearching && parent.searchTerm !== undefined && parent.searchTerm.length >= parent.options.minSearchTermLength)
            {
               this.isSearching = true;

               var me = this;
               
               // Reset the custom error messages
               me._setDefaultDataTableErrors(parent.widgets.dataTable);
               
               // Don't display any message
               parent.widgets.dataTable.set("MSG_EMPTY", parent._msg("message.searching"));
               
               // Empty results table
               parent.widgets.dataTable.deleteRows(0, parent.widgets.dataTable.getRecordSet().getLength());
               
               var successHandler = function ConsoleNodeBrowser_SearchPanelHandler_onUpdate_successHandler(sRequest, oResponse, oPayload)
               {
                  me._enableSearchUI();                  
                  me._setDefaultDataTableErrors(parent.widgets.dataTable);
                  parent.widgets.dataTable.onDataReturnInitializeTable.call(parent.widgets.dataTable, sRequest, oResponse, oPayload);
               };
               
               var failureHandler = function ConsoleNodeBrowser_SearchPanelHandler_onUpdate_failureHandler(sRequest, oResponse)
               {
                  me._enableSearchUI();
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
                        parent.widgets.dataTable.set("MSG_ERROR", response.message);
                        parent.widgets.dataTable.showTableMessage(Alfresco.util.encodeHTML(response.message), YAHOO.widget.DataTable.CLASS_ERROR);
                        me._setResultsMessage("message.noresults");
                     }
                     catch(e)
                     {
                        me._setDefaultDataTableErrors(parent.widgets.dataTable);
                     }
                  }
               };

               // Send the query to the server
               parent.widgets.dataSource.sendRequest(me._buildSearchParams(parent.searchTerm, parent.searchLanguage, parent.store),
               {
                  success: successHandler,
                  failure: failureHandler,
                  scope: parent
               });
               me._setResultsMessage("message.searchingFor", $html(parent.searchTerm));

               // Disable search button and display a wait feedback message if the users hasn't been found yet
               parent.widgets.searchButton.set("disabled", true);
               YAHOO.lang.later(2000, me, function(){
                  if (me.isSearching)
                  {
                     if (!me.widgets.feedbackMessage)
                     {
                      me.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
                      {
                         text: Alfresco.util.message("message.searching", parent.name),
                         spanClass: "wait",
                         displayTime: 0
                      });
                     }
                     else if (!me.widgets.feedbackMessage.cfg.getProperty("visible"))
                     {
                      me.widgets.feedbackMessage.show();
                     }
                  }
               }, []);
            }
         },

         /**
          * Enable search button, hide the pending wait message and set the panel as not searching.
          *
          * @method _enableSearchUI
          * @private
          */
         _enableSearchUI: function ConsoleNodeBrowser_SearchPanelHandler_enableSearchUI()
         {
            // Enable search button and close the wait feedback message if present
            if (this.widgets.feedbackMessage && this.widgets.feedbackMessage.cfg.getProperty("visible"))
            {
               this.widgets.feedbackMessage.hide();
            }
            parent.widgets.searchButton.set("disabled", false);
            this.isSearching = false;
         },

         /**
          * Setup the YUI DataTable with custom renderers.
          *
          * @method _setupDataTable
          * @private
          */
         _setupDataTable: function ConsoleNodeBrowser_SearchPanelHandler_setupDataTable()
         {
            /**
             * DataTable Cell Renderers
             *
             * Each cell has a custom renderer defined as a custom function. See YUI documentation for details.
             * These MUST be inline in order to have access to the parent instance (via the "parent" variable).
             */
            
            /**
             * Generic HTML-safe custom datacell formatter
             */
            var renderCellSafeHTML = function renderCellSafeHTML(elCell, oRecord, oColumn, oData)
            {
               elCell.innerHTML = $html(oData);
            };

            /**
             * Qname renderer
             *
             * @method renderQName
             */
            var renderQName = function renderQName(elCell, oRecord, oColumn, oData)
            {
                elCell.innerHTML = $html(oData[parent._qnamePropertyName()]);
            }

            /**
             * Parent Qname path renderer
             *
             * @method renderParentPath
             */
            var renderParentPath = function renderParentPath(elCell, oRecord, oColumn, oData)
            {
                if (oData != "")
                {
                    var path = oData[parent._qnamePropertyName()];
                    // Remove last part
                    var index = parent.options.shortQNames ? path.lastIndexOf("/") : path.lastIndexOf("/{");
                    path = index != -1 ? path.substring(0, index) : path;
                    elCell.innerHTML = $html(path);
                }
            }

            /**
             * Node name renderer
             *
             * @method renderNodeName
             */
            var renderNodeName = function renderNodeName(elCell, oRecord, oColumn, oData)
            {
                if (oData != "")
                {
                    renderNodeLink(elCell, oRecord, oColumn, $html(oData[parent._qnamePropertyName()]));
                }
            }
            
            /**
             * Node name custom datacell formatter
             *
             * @method renderName
             */
            var renderNodeLink = function renderNodeLink(elCell, oRecord, oColumn, oData)
            {
               // Create view userlink
               var viewNodeLink = document.createElement("a");
               Dom.setAttribute(viewNodeLink, "href", "#");
               viewNodeLink.innerHTML = $html(oData);

               // fire the 'viewUserClick' event when the selected user in the list has changed
               YAHOO.util.Event.addListener(viewNodeLink, "click", function(e)
               {
                  YAHOO.util.Event.preventDefault(e);
                  YAHOO.Bubbling.fire('viewNodeClick',
                  {
                     nodeRef: oRecord.getData("nodeRef")
                  });
               }, null, parent);
               elCell.appendChild(viewNodeLink);
            };
            
            // DataTable column defintions
            var columnDefinitions =
            [
               { key: "name", label: parent._msg("label.name"), sortable: true, formatter: renderNodeName },
               { key: "qnamePath", label: parent._msg("label.parent_path"), sortable: true, formatter: renderParentPath },
               { key: "nodeRef", label: parent._msg("label.node-ref"), sortable: true, formatter: renderNodeLink }
            ];
            
            // DataTable definition
            parent.widgets.dataTable = new YAHOO.widget.DataTable(parent.id + "-datatable", columnDefinitions, parent.widgets.dataSource,
            {
               initialLoad: false,
               renderLoopSize: 32,
               sortedBy:
               {
                  key: "name",
                  dir: "asc"
               },
               MSG_EMPTY: parent._msg("message.empty")
            });
         },
         
         /**
          * Resets the YUI DataTable errors to our custom messages
          * NOTE: Scope could be YAHOO.widget.DataTable, so can't use "this"
          *
          * @method _setDefaultDataTableErrors
          * @param dataTable {object} Instance of the DataTable
          * @private
          */
         _setDefaultDataTableErrors: function ConsoleNodeBrowser_SearchPanelHandler_setDefaultDataTableErrors(dataTable)
         {
            dataTable.set("MSG_EMPTY", parent._msg("message.datatable.empty"));
            dataTable.set("MSG_ERROR", parent._msg("message.datatable.error"));
         },
         
         /**
          * Build URI parameters for People List JSON data webscript
          *
          * @method _buildSearchParams
          * @param searchTerm {string} User search term
          * @param store {string} Store name
          * @private
          */
         _buildSearchParams: function ConsoleNodeBrowser_SearchPanelHandler_buildSearchParams(searchTerm, searchLanguage, store)
         {
            return "?q=" + encodeURIComponent(searchTerm) + 
               "&lang=" + encodeURIComponent(searchLanguage) + 
               "&store=" + encodeURIComponent(store) + 
               "&maxResults=" + parent.options.maxSearchResults;
         },
         
         /**
          * Set the message in the Results Bar area
          * 
          * @method _setResultsMessage
          * @param messageId {string} The messageId to display
          * @private
          */
         _setResultsMessage: function ConsoleNodeBrowser_SearchPanelHandler_setResultsMessage(messageId)
         {
            var resultsDiv = Dom.get(parent.id + "-search-bar");
            resultsDiv.innerHTML = parent._msg.apply(this, arguments);
         },
         
         /**
          * Successfully applied options event handler
          *
          * @method onSuccess
          * @param response {object} Server response object
          */
         onSuccess: function ConsoleNodeBrowser_SearchPanelHandler_onSuccess(response)
         {
            if (response && response.json)
            {
               if (response.json.success)
               {
                  // refresh the browser to force the themed components to reload
                  window.location.reload(true);
               }
               else if (response.json.message)
               {
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     text: response.json.message
                  });
               }
            }
            else
            {
               Alfresco.util.PopupManager.displayPrompt(
               {
                  text: Alfresco.util.message("message.failure")
               });
            }
         }
      });
      new SearchPanelHandler();
      
      /* View Panel Handler */
      ViewPanelHandler = function ConsoleNodeBrowser_ViewPanelHandler_constructor()
      {
         ViewPanelHandler.superclass.constructor.call(this, "view");
      };
      
      YAHOO.extend(ViewPanelHandler, Alfresco.ConsolePanelHandler,
      {
         onLoad: function ConsoleNodeBrowser_ViewPanelHandler_onLoad()
         {
            // Buttons
            Alfresco.util.createYUIButton(parent, "goback-button", parent.onGoBackClick);
            Alfresco.util.createYUIButton(parent, "goback-button-top", parent.onGoBackClick);
         },
         
         onBeforeShow: function ConsoleNodeBrowser_ViewPanelHandler_onBeforeShow()
         {
            // Hide the main panel area before it is displayed - so we don't show
            // old data to the user before the Update() method paints the results
            Dom.get(parent.id + "-view-title").innerHTML = "";
            Dom.setStyle(parent.id + "-view-main", "visibility", "hidden");
         },
         
         onShow: function ConsoleNodeBrowser_ViewPanelHandler_onShow()
         {
            window.scrollTo(0, 0);
         },
         
         onUpdate: function ConsoleNodeBrowser_ViewPanelHandler_onUpdate()
         {
            window.scrollTo(0, 0);
            
            // Use a XHR call to get node data
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.PROXY_URI + "slingshot/node/" + parent.currentNodeRef.replace("://", "/"),
               method: Alfresco.util.Ajax.GET,
               successCallback:
               {
                  fn: this.onDataLoad,
                  scope: parent
               },
               failureMessage: parent._msg("message.getnode-failure", $html(parent.currentUserId))   
            });
         },

         /**
          * Node data loaded successfully. Sets up YUI DataTable instances and other UI elements.
          *
          * @method onDataLoad
          * @param p_obj {object} Result object, defining serverResponse and json objects
          */
         onDataLoad: function ConsoleNodeBrowser_ViewPanelHandler_onDataLoad(p_obj)
         {
             var me = this, node = p_obj.json, nodeRef = node.nodeRef;
             
             var fnSetter = function(id, val)
             {
                Dom.get(parent.id + id).innerHTML = val ? $html(val) : "";
             };

             /**
              * Node link custom datacell formatter
              *
              * @method renderName
              */
             var renderNodeLink = function renderNodeLink(elCell, oRecord, oColumn, oData, oParams)
             {
                oParams = oParams || {};
                var viewNodeLink = document.createElement("a");
                YAHOO.util.Dom.setAttribute(viewNodeLink, "href", "#");
                viewNodeLink.innerHTML = $html(oData);

                // fire the 'viewNodeClick' event when the selected node in the list has changed
                YAHOO.util.Event.addListener(viewNodeLink, "click", function(e)
                {
                   YAHOO.util.Event.preventDefault(e);
                   YAHOO.Bubbling.fire('viewNodeClick',
                   {
                      nodeRef: oParams.nodeRef || oRecord.getData("nodeRef")
                   });
                }, null, parent);
                elCell.appendChild(viewNodeLink);
             };

             /**
              * QName custom formatter
              *
              * @method renderQName
              */
             var renderQName = function renderQName(elCell, oRecord, oColumn, oData)
             {
                elCell.innerHTML = $html(oData[parent._qnamePropertyName()]);
             };

             /**
              * Child name formatter
              *
              * @method renderChildName
              */
             var renderChildName = function renderChildName(elCell, oRecord, oColumn, oData)
             {
                 renderNodeLink(elCell, oRecord, oColumn, oData[parent._qnamePropertyName()]);
             };

             /**
              * Assoc nodeRef formatter
              *
              * @method renderSourceNodeRef
              */
             var renderAssocNodeRef = function renderChildName(elCell, oRecord, oColumn, oData)
             {
                 renderNodeLink(elCell, oRecord, oColumn, oData, { nodeRef: oData });
             };
             
             /**
              * Property value custom datacell formatter
              *
              * @method renderPropertyValue
              */
             var renderPropertyValue = function renderPropertyValue(elCell, oRecord, oColumn, oData)
             {
                var renderValue = function(val, el)
                {
                    if (val.isNullValue)
                    {
                        Dom.addClass(el, "node-value-label");
                        el.innerHTML = parent.msg("label.node-value-null");
                    }
                    else
                    {
                        if (val.dataType == "{http://www.alfresco.org/model/dictionary/1.0}content")
                        {
                           // Create new link
                           var html = "<a ";
                           html += "href=\"" + Alfresco.constants.PROXY_URI + "api/node/" + nodeRef.replace("://", "/") + "/content" + "\">";
                           html += $html(val.value);
                           html += "</a>";
                           // Create new link
                           var contentLink = document.createElement("a");
                           contentLink.innerHTML = $html(val.value);
                           YAHOO.util.Dom.setAttribute(contentLink, "href", Alfresco.constants.PROXY_URI + "api/node/" + nodeRef.replace("://", "/") + "/content;" + oRecord.getData("name").prefixedName);
                           el.appendChild(contentLink);
                        }
                        else if (val.dataType == "{http://www.alfresco.org/model/dictionary/1.0}noderef")
                        {
                            renderNodeLink(el, oRecord, oColumn, val.value, { nodeRef: val.value });
                        }
                        else
                        {
                            el.innerHTML = $html(val.value);
                        }
                    }
                };

                if (oRecord.getData("multiple") == false)
                {
                    renderValue(oData[0], elCell.appendChild(document.createElement("div"), elCell));
                }
                else
                {
                    var labelEl = elCell.appendChild(document.createElement("div"), elCell);
                    Dom.addClass(labelEl, "node-value-label");
                    labelEl.innerHTML = parent.msg("label.node-value-collection");
                    for (var i = 0; i < oData.length; i++)
                    {
                        renderValue(oData[i], elCell.appendChild(document.createElement("div"), elCell));
                    }
                }
                
             };

             Dom.get(parent.id + "-view-title").innerHTML = node.name[parent._qnamePropertyName()];
             
             // About section fields
             fnSetter("-view-node-ref", node.nodeRef);
             fnSetter("-view-node-path", node.qnamePath[parent._qnamePropertyName()]);
             fnSetter("-view-node-type", node.type[parent._qnamePropertyName()]);

             Dom.get(parent.id + "-view-node-parent").innerHTML = "";
             // Add parent noderef link
             if (node.parent !== null)
             {
                var nodeLink = document.createElement("a");
                Dom.setAttribute(nodeLink, "href", "#");
                nodeLink.innerHTML = $html(node.parentNodeRef);
                YAHOO.util.Event.addListener(nodeLink, "click", function(e)
                {
                   YAHOO.util.Event.preventDefault(e);
                   YAHOO.Bubbling.fire('viewNodeClick',
                   {
                      nodeRef: node.parentNodeRef
                   });
                }, null, parent);
                Dom.get(parent.id + "-view-node-parent").appendChild(nodeLink);
             }

             var dtConfig = {
                     MSG_EMPTY: parent._msg("message.datatable.empty"),
                     MSG_ERROR: parent._msg("message.datatable.error")
             };

             var propsDT = new YAHOO.widget.DataTable(parent.id + "-view-node-properties", 
                [
                   { key: "name", label: parent.msg("label.properties-name"), formatter: renderQName },
                   { key: "type", label: parent.msg("label.properties-type"), formatter: renderQName },
                   { key: "values", label: parent.msg("label.properties-value"), formatter: renderPropertyValue },
                   { key: "residual", label: parent.msg("label.properties-residual") }
                ], 
                new YAHOO.util.LocalDataSource(node.properties),
                dtConfig
             );
             
             var aspects = "";
             for ( var i = 0; i < node.aspects.length; i++)
             {
                aspects += (i != 0 ? "<br />" : "") + $html(node.aspects[i][parent._qnamePropertyName()]);
             }
             Dom.get(parent.id + "-view-node-aspects").innerHTML = aspects;
             
             var childrenDT = new YAHOO.widget.DataTable(parent.id + "-view-node-children", 
                [
                   { key: "name", label: parent.msg("label.children-name"), formatter: renderChildName },
                   { key: "type", label: parent.msg("label.children-type"), formatter: renderQName },
                   { key: "nodeRef", label: parent.msg("label.children-node-ref"), formatter: renderNodeLink },
                   { key: "primary", label: parent.msg("label.children-primary") },
                   { key: "assocType", label: parent.msg("label.children-assoc-type"), formatter: renderQName },
                   { key: "index", label: parent.msg("label.children-index") }
                ], 
                new YAHOO.util.LocalDataSource(node.children),
                dtConfig
             );
             
             var parentsDT = new YAHOO.widget.DataTable(parent.id + "-view-node-parents", 
                 [
                   { key: "name", label: parent.msg("label.parents-name"), formatter: renderChildName },
                   { key: "type", label: parent.msg("label.parents-type"), formatter: renderQName },
                   { key: "nodeRef", label: parent.msg("label.parents-node-ref"), formatter: renderNodeLink },
                   { key: "primary", label: parent.msg("label.parents-primary") },
                   { key: "assocType", label: parent.msg("label.parents-assoc-type"), formatter: renderQName }
                   
                ], 
                new YAHOO.util.LocalDataSource(node.parents),
                dtConfig
             );

             var assocsDT = new YAHOO.widget.DataTable(parent.id + "-view-node-assocs", 
                [
                   { key: "assocType", label: parent.msg("label.assocs-assoc-type"), formatter: renderQName },
                   { key: "targetRef", label: parent.msg("label.assocs-node-ref"), formatter: renderAssocNodeRef },
                   { key: "type", label: parent.msg("label.assocs-type"), formatter: renderQName }
                ], 
                new YAHOO.util.LocalDataSource(node.assocs),
                dtConfig
             );
             
             var sourceAssocsDT = new YAHOO.widget.DataTable(parent.id + "-view-node-source-assocs", 
                [
                   { key: "assocType", label: parent.msg("label.source-assocs-assoc-type"), formatter: renderQName },
                   { key: "sourceRef", label: parent.msg("label.source-assocs-node-ref"), formatter: renderAssocNodeRef },
                   { key: "type", label: parent.msg("label.source-assocs-type"), formatter: renderQName }
                ], 
                new YAHOO.util.LocalDataSource(node.sourceAssocs),
                dtConfig
             );

             var permissionsDT = new YAHOO.widget.DataTable(parent.id + "-view-node-permissions", 
                [
                   { key: "permission", label: parent.msg("label.permissions-permission") },
                   { key: "authority", label: parent.msg("label.permissions-authority") },
                   { key: "rel", label: parent.msg("label.permissions-access") }
                ], 
                new YAHOO.util.LocalDataSource(node.permissions.entries),
                dtConfig
             );
             
             var storePermissionsDT = new YAHOO.widget.DataTable(parent.id + "-view-node-store-permissions", 
                [
                   { key: "permission", label: parent.msg("label.permissions-store-permission") },
                   { key: "authority", label: parent.msg("label.permissions-authority") },
                   { key: "rel", label: parent.msg("label.permissions-access") }
                ], 
                new YAHOO.util.LocalDataSource(node.permissions.masks),
                dtConfig
             );
             
             fnSetter("-view-node-inherits-permissions", "" + node.permissions.inherit);
             fnSetter("-view-node-owner", node.permissions.owner);
             
             // Make main panel area visible
             Dom.setStyle(parent.id + "-view-main", "visibility", "visible");
         },
         
         /**
          * View Node event handler
          *
          * @method onNodeClick
          * @param e {object} DomEvent
          * @param args {array} Event parameters (depends on event type)
          */
         onNodeClick: function ConsoleNodeBrowser_ViewPanelHandler_onNodeClick(e, args)
         {
            var nodeRef = args[1].nodeRef;
            this.refreshUIState({"panel": "view", "nodeRef": nodeRef});
         }
         
      });
      new ViewPanelHandler();
      
      return this;
   };
   
   YAHOO.extend(Alfresco.ConsoleNodeBrowser, Alfresco.ConsoleTool,
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
          * Number of characters required for a search.
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
          * Whether to use short QNames when displaying node information
          * 
          * @property shortQNames
          * @type boolean
          * @default true
          */
         shortQNames: true
      },
      
      /**
       * Current node ref if viewing a node.
       * 
       * @property currentNodeRef
       * @type string
       */
      currentNodeRef: "",
      
      /**
       * Name of the store to search against
       * 
       * @property store
       * @type string
       */
      store: "workspace://SpacesStore",
      
      /**
       * Current search term, obtained from form input field.
       * 
       * @property searchTerm
       * @type string
       */
      searchTerm: "PATH:\"/\"",
      
      /**
       * Current search language, obtained from drop-down.
       * 
       * @property searchLanguage
       * @type string
       */
      searchLanguage: "fts-alfresco",
      
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function ConsoleNodeBrowser_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function ConsoleNodeBrowser_onReady()
      {
         // Call super-class onReady() method
         Alfresco.ConsoleNodeBrowser.superclass.onReady.call(this);
      },
      
      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */
      
      /**
       * History manager state change event handler (override base class)
       *
       * @method onStateChanged
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onStateChanged: function ConsoleNodeBrowser_onStateChanged(e, args)
      {
         var state = this.decodeHistoryState(args[1].state);
         
         // test if panel has actually changed?
         if (state.panel)
         {
            this.showPanel(state.panel);
         }
         
         if (state.search !== undefined && this.currentPanelId === "search")
         {
            // keep track of the last search performed
            var searchTerm = state.search;
            this.searchTerm = searchTerm;
            
            // keep track of search language
            var searchLanguage = state.lang;
            this.searchLanguage = searchLanguage;
            
            // keep track of store name
            var store = state.store;
            this.store = store;
            
            this.updateCurrentPanel();
         }
         
         if (state.nodeRef &&
             (this.currentPanelId === "view"))
         {
            this.currentNodeRef = state.nodeRef;
            
            this.updateCurrentPanel();
         }
      },
      
      /**
       * Search button click event handler
       *
       * @method onSearchClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onSearchClick: function ConsoleNodeBrowser_onSearchClick(e, args)
      {
         var searchTermElem = Dom.get(this.id + "-search-text");
         var searchTerm = YAHOO.lang.trim(searchTermElem.value);
         
         // Search language
         var searchLanguage = this.widgets.langMenuButton.get("value");
         
         // Search language
         var store = this.widgets.storeMenuButton.get("value");
         
         // inform the user if the search term entered is too small
         if (searchTerm.length < this.options.minSearchTermLength)
         {
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this._msg("message.minimum-length", this.options.minSearchTermLength)
            });
            return;
         }
         
         this.refreshUIState({"search": searchTerm, "lang": searchLanguage, "store": store});
      },
      
      /**
       * View Node event handler
       *
       * @method onViewNodeClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onViewNodeClick: function ConsoleNodeBrowser_onViewNodeClick(e, args)
      {
         var nodeRef = args[1].nodeRef;
         this.refreshUIState({"panel": "view", "nodeRef": nodeRef});
      },

      /**
       * Go back button click event handler
       *
       * @method onGoBackClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onGoBackClick: function ConsoleNodeBrowser_onGoBackClick(e, args)
      {
         this.refreshUIState({"panel": "search"});
      },
      
      /**
       * Encode state object into a packed string for use as url history value.
       * Override base class.
       * 
       * @method encodeHistoryState
       * @param obj {object} state object
       * @private
       */
      encodeHistoryState: function ConsoleNodeBrowser_encodeHistoryState(obj)
      {
         // wrap up current state values
         var stateObj = {};
         if (this.currentPanelId !== "")
         {
            stateObj.panel = this.currentPanelId;
         }
         if (this.currentNodeRef !== "")
         {
            stateObj.nodeRef = this.currentNodeRef;
         }
         if (this.searchTerm !== undefined)
         {
            stateObj.search = this.searchTerm;
         }
         if (this.searchLanguage !== undefined)
         {
            stateObj.lang = this.searchLanguage;
         }
         if (this.store !== undefined)
         {
            stateObj.store = this.store;
         }
         
         // convert to encoded url history state - overwriting with any supplied values
         var state = "";
         if (obj.panel || stateObj.panel)
         {
            state += "panel=" + encodeURIComponent(obj.panel ? obj.panel : stateObj.panel);
         }
         if (obj.nodeRef || stateObj.nodeRef)
         {
            if (state.length !== 0)
            {
               state += "&";
            }
            state += "nodeRef=" + encodeURIComponent(obj.nodeRef ? obj.nodeRef : stateObj.nodeRef);
         }
         if (obj.search !== undefined || stateObj.search !== undefined)
         {
            if (state.length !== 0)
            {
               state += "&";
            }
            state += "search=" + encodeURIComponent(obj.search !== undefined ? obj.search : stateObj.search);
         }
         if (obj.lang !== undefined || stateObj.lang !== undefined)
         {
            if (state.length !== 0)
            {
               state += "&";
            }
            state += "lang=" + encodeURIComponent(obj.lang !== undefined ? obj.lang : stateObj.lang);
         }
         if (obj.store !== undefined || stateObj.store !== undefined)
         {
            if (state.length !== 0)
            {
               state += "&";
            }
            state += "store=" + encodeURIComponent(obj.store !== undefined ? obj.store : stateObj.store);
         }
         return state;
      },
      
      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function ConsoleNodeBrowser__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.ConsoleNodeBrowser", Array.prototype.slice.call(arguments).slice(1));
      },
      
      /**
       * Returns the qname property name to use for display purposes, based on shortQNames setting
       *
       * @method _qnamePropertyName
       * @return {string} The name of the property to display from QName objects
       * @private
       */
      _qnamePropertyName: function ConsoleNodeBrowser__qnamePropertyName()
      {
         return this.options.shortQNames == true ? "prefixedName": "name";
      }
   });
})();