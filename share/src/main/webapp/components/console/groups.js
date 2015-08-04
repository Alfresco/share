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
 * ConsoleGroups tool component.
 *
 * @namespace Alfresco
 * @class Alfresco.ConsoleGroups
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
    * ConsoleGroups constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.ConsoleGroups} The new ConsoleGroups instance
    * @constructor
    */
   Alfresco.ConsoleGroups = function(htmlId)
   {
      this.name = "Alfresco.ConsoleGroups";
      Alfresco.ConsoleGroups.superclass.constructor.call(this, htmlId);

      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datasource", "datatable", "json", "history", "columnbrowser"], this.onComponentsLoaded, this);

      /* Decoupled event listeners */
      YAHOO.Bubbling.on("newGroup", this.onNewGroup, this);
      YAHOO.Bubbling.on("updateGroup", this.onUpdateGroup, this);

      /* Define panel handlers */
      var parent = this;
      this.panelHandlers = {};

      // NOTE: the panel registered first is considered the "default" view and is displayed first

      /* Search Panel Handler */
      SearchPanelHandler = function SearchPanelHandler_constructor()
      {
         SearchPanelHandler.superclass.constructor.call(this, "search");
      };

      YAHOO.extend(SearchPanelHandler, Alfresco.ConsolePanelHandler,
      {
         /**
          * INSTANCE VARIABLES
          */

         /**
          * Keeps track if this panel is visble or not
          *
          * @property _visible
          * @type Boolean
          */
         _visible: false,

         /**
          * Keeps track if this panel is searching or not
          *
          * @property isSearching
          * @type Boolean
          */
         isSearching: false,

         /**
          * When the Add User dialog or the Add Group dialog is shown this variable keeps track
          * of which group the selected user or group should be added to.
          *
          * @property _selectedParentGroupShortName
          * @type String
          */
         _selectedParentGroupShortName: null,

         /**
          * The value for sorting the result
          *
          * @property sortBy
          * @type String
          */
         sortBy: "displayName",

         /**
          * The number of max items to display for the search
          *
          * @property maxItems
          * @type int
          */
         maxItems: 250,

         /**
          * PANEL LIFECYCLE CALLBACKS
          */

         /**
          * Called by the ConsolePanelHandler when this panel shall be loaded
          *
          * @method onLoad
          */
         onLoad: function ConsoleGroups_SearchPanelHandler_onLoad()
         {
            var me = this;

            // Search Button
            var searchButton = new YAHOO.widget.Button(parent.id + "-search-button", {});
            searchButton.on("click", this.onSearchClick, searchButton, this);
            this.widgets.searchButton = searchButton;

            // ColumnBrowser
            this.widgets.columnbrowser = new YAHOO.extension.ColumnBrowser(parent.id + "-columnbrowser",
            {
               numVisible: 3,
               rootUrl: Alfresco.constants.PROXY_URI + "api/groups?" +
                        "sortBy=" + this.sortBy +
                        (Dom.get(parent.id + "-show-all").checked ? "" : "&zone=APP.DEFAULT") +
                        ((parent.query && parent.query != null) ? "&shortNameFilter=" + encodeURIComponent(parent.query) : ""),
               pagination:
               {
                  rowsPerPage: parent.options.maxPageSize,
                  rowsPerPageParam: 'maxItems',
                  recordOffsetParam: 'skipCount',
                  firstPageLinkLabel : parent._msg('tinyPagination.firstPageLinkLabel'),
                  lastPageLinkLabel : parent._msg('tinyPagination.lastPageLinkLabel'),
                  previousPageLinkLabel : parent._msg('tinyPagination.previousPageLinkLabel'),
                  nextPageLinkLabel : parent._msg('tinyPagination.nextPageLinkLabel'),
                  pageReportTemplate : parent._msg('tinyPagination.pageReportTemplate'),
                  template: parent._msg('tinyPagination.template')
               },
               columnInfoBuilder:
               {
                  fn: this.onBuildColumnInfo,
                  scope: this
               }
            });

            // ColumnBrowser Breadcrumb
            this.widgets.breadcrumb = new YAHOO.extension.ColumnBrowserBreadCrumb(parent.id + "-breadcrumb",
            {
               columnBrowser: this.widgets.columnbrowser,
               root: parent._msg("label.breadcrumb.root")
            });

            // Browse button
            var browse = new YAHOO.widget.Button(parent.id + "-browse-button", {});
            browse.on("click", this.onBrowseClick, browse, this);

            // Show All checkbox
            var elShowAll = Dom.get(parent.id + "-show-all");
            Event.addListener(elShowAll, "change", function()
               {
                  var state = {
                     "showAll": elShowAll.checked,
                     "refresh": this.getParameterValueFromUrl("refresh")
                  };
                  parent.refreshUIState(state);
               }, null, this);

            // DataTable and DataSource setup
            this.widgets.dataSource = new YAHOO.util.DataSource(Alfresco.constants.PROXY_URI + "api/groups?",
            {
               responseType: YAHOO.util.DataSource.TYPE_JSON,
               responseSchema:
               {
                  resultsList: "data",
                  metaFields:
                  {
                     recordOffset: "startIndex",
                     totalRecords: "totalRecords"
                  }
               }
            });

            // Work to be performed after data has been queried but before display by the DataTable
            this.widgets.dataSource.doBeforeParseData = function ConsoleGroups_SearchPanel_doBeforeParseData(oRequest, oFullResponse)
            {
               var updatedResponse = oFullResponse;

               if (oFullResponse)
               {
                  var items = oFullResponse.data;

                  // we need to wrap the array inside a JSON object so the DataTable gets the object it expects
                  updatedResponse =
                  {
                     "data": items
                  };
               }

               // update Results Bar message with number of results found
               if (items.length == 0)
               {
                  me._setResultsMessage("message.noresults");
               }
               else if (items.length < parent.options.maxSearchResults)
               {
                  me._setResultsMessage("message.results", $html(parent.query), items.length);
               }
               else
               {
                  me._setResultsMessage("message.maxresults", parent.options.maxSearchResults);
               }

               return updatedResponse;
            };

            // Setup the main datatable
            this._setupDataTable();

            this.widgets.dataTable.subscribe("theadCellClickEvent", function(oArgs)
            {
               var panel = null;
               for (var i = 0, length = parent.panels.length; i < length; i++)
               {
                  if (parent.panels[i].id === "search")
                  {
                     panel = parent.panels[i];
                     break;
                  }
               }
               if (panel != null)
               {
                  panel.sortBy = this.getColumn(oArgs.target).key;
               }
            });

            // register the "enter" event on the search text field
            var searchText = Dom.get(parent.id + "-search-text");
            new YAHOO.util.KeyListener(searchText,
            {
               keys: YAHOO.util.KeyListener.KEY.ENTER
            },
            {
               fn: function()
               {
                  this.onSearchClick();
               },
               scope: this,
               correctScope: true
            }, "keydown").enable();

            // Load in the People Finder component from the server
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "components/people-finder/people-finder",
               dataObj:
               {
                  htmlid: parent.id + "-search-peoplefinder"
               },
               successCallback:
               {
                  fn: this.onPeopleFinderLoaded,
                  scope: this
               },
               failureMessage: "Could not load People Finder component",
               execScripts: true
            });

            // Load in the Group Finder component from the server
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "components/people-finder/group-finder",
               dataObj:
               {
                  htmlid: parent.id + "-search-groupfinder"
               },
               successCallback:
               {
                  fn: this.onGroupFinderLoaded,
                  scope: this
               },
               failureMessage: "Could not load Group Finder component",
               execScripts: true
            });

            // Create delete group panel
            this.widgets.deleteGroupPanel = new Alfresco.util.createYUIPanel(parent.id + "-deletegroupdialog",
            {
               visible: false
            });

            // Add event listeners to buttons
            this.widgets.deleteGroupCancelButton = new YAHOO.widget.Button(parent.id + "-cancel-button", {});
            this.widgets.deleteGroupCancelButton.on("click", function()
            {
               this.widgets.deleteGroupPanel.hide();
            }, null, this);

            this.widgets.deleteGroupOkButton = Alfresco.util.createYUIButton(parent, "remove-button", null);
         },

         getParameterValueFromUrl: function ConsoleGroups_SearchPanelHandeler_getParameterValueFromUrl(param)
         {
            var token, 
               result = null,
               hash = window.location.hash,
               params = hash.replace('#', '').split("&");
            for (var i = 0; i < params.length; i++)
            {
               token = params[i].split("=");
               if (token[0] === param)
               {
                  result = token[1];
                  break;
               }
            }
            return result;
         },

         /**
          * Called by the ConsolePanelHandler when this panel is shown
          *
          * @method onShow
          */
         onShow: function ConsoleGroups_SearchPanelHandler_onShow()
         {
            this._visible = true;

            // Set focus to the search input field
            Dom.get(parent.id + "-search-text").focus();
            
            // Show All checkbox state from history
            var elShowAll = Dom.get(parent.id + "-show-all");
            elShowAll.checked = parent.showAll;
         },

         /**
          * Called by the ConsolePanelHandler when this panel shall update its appearance
          *
          * @method onUpdate
          */
         onUpdate: function ConsoleGroups_SearchPanelHandler_onUpdate()
         {
            if (parent.refresh == undefined || parent.query !== undefined)
            {
               /**
                * The search panel shall only be displayed if a query exists OR when the page is initially loaded.
                * The parent.refresh == undefined indicates that the page was reloaded when in browse view state.
                */

               // Lets display the search list since the state indicates a query has been used
               Dom.addClass(parent.id + "-browse-panel", "hidden");
               Dom.removeClass(parent.id + "-search-panel", "hidden");
               
               if (parent.query !== undefined)
               {
                  // update the text field - as this event could come from bookmark, navigation or a search button click
                  var queryElem = Dom.get(parent.id + "-search-text");
                  queryElem.value = parent.query;

                  // Redo the search
                  this.doSearch();
               }
            }
            else
            {
               // No query in the state then display the column browser
               Dom.addClass(parent.id + "-search-panel", "hidden");
               Dom.removeClass(parent.id + "-browse-panel", "hidden");

               var paths = this.widgets.columnbrowser.get("urlPath");
               if (!paths || paths.length == 0)
               {
                  // Load the root groups
                  this.widgets.columnbrowser.load();
               }
               else if (parent.refresh)
               {
                  // Refresh the column browser
                  this.widgets.columnbrowser.load(paths, true);
               }
            }
         },

         /**
          * Called by the ConsolePanelHandler when this panel is hidden
          *
          * @method onHide
          */
         onHide: function ConsoleGroups_SearchPanelHandler_onHide()
         {
            this._visible = false;
         },

         /**
          * BUTTON EVENT HANDLERS
          */

         /**
          * Called when the user clicks the search button
          *
          * @method onSearchClick
          */
         onSearchClick: function ConsoleGroups_SearchPanelHandler_onSearchClick()
         {
            var query = YAHOO.lang.trim(Dom.get(parent.id + "-search-text").value);

            // inform the user if the search term entered is too small
            if (query.replace(/\*/g, "").length < parent.options.minSearchTermLength)
            {
               Alfresco.util.PopupManager.displayMessage(
               {
                  text: parent._msg("message.minimum-length", parent.options.minSearchTermLength)
               });
               return;
            }

            parent.refreshUIState({"query": query});
         },

         /**
          * Called when the user clicks the browse button
          *
          * @method onBrowseClick
          */
         onBrowseClick: function ConsoleGroups_SearchPanelHandler_onBrowseClick()
         {
            parent.refreshUIState({"query": undefined, "refresh": "false"});

            // reset group browser url
            var showAll = Dom.get(parent.id + "-show-all").checked;
            var query = Dom.get(parent.id + "-search-text").value;
            var rootUrl = Alfresco.constants.PROXY_URI + "api/groups?" +
               "sortBy=" + this.sortBy +
               (showAll ? "" : "&zone=APP.DEFAULT") +
               ((query && query != null) ? "&shortNameFilter=" + encodeURIComponent(query) : "");
            parent.panelHandlers.searchPanelHandler.widgets.columnbrowser.set("rootUrl", rootUrl);
            parent.panelHandlers.searchPanelHandler.widgets.columnbrowser.load([rootUrl] , true);
         },

         /**
          * Called when the user clicks the delete button in the cofirm dialog.
          * Deletes the group from the repository or simply removes is from a parent group.
          *
          * @param e The click event
          * @param obj information about the group and its parent group
          * shall be removed from parent group. If now present group will be deleted.
          */
         onConfirmedDeleteGroupClick: function ConsoleGroups_SearchPanelHandler_onConfirmedDeleteGroupClick(e, obj)
         {
            // Hide the confirm dialog
            this.widgets.deleteGroupPanel.hide();

            if (obj.multiParentMode && Dom.get(parent.id + "-remove").checked)
            {
               // Just remove the group from the parent group
               this._removeGroup(obj.fullName, obj.parentShortName, obj.displayName);
            }
            else
            {
               // Delete the group form the repository
               this._deleteGroup(obj.shortName, obj.displayName);
            }
         },

         /**
          * Group selected event handler.
          * This event is fired from Group picker - so we much ensure
          * the event is for the current panel by checking panel visibility.
          *
          * @method onGroupSelected
          * @param e DomEvent
          * @param args Event parameters (depends on event type)
          */
         onGroupSelected: function ConsoleGroups_SearchPanelHandler_onGroupSelected(e, args)
         {
            // This is a "global" event so we ensure the event is for the current panel by checking panel visibility.
            if (this._visible)
            {
               var name = args[1].displayName;
               this.widgets.addGroupPanel.hide();
               this._addToGroup(
                     args[1].itemName,
                     this._selectedParentGroupShortName,
                     parent._msg("message.addgroup-success", name),
                     parent._msg("message.addgroup-failure", name));
            }
         },

         /**
          * Called when the user has selected a person from the add user dialog.
          *
          * @method onPersonSelected
          * @param e DomEvent
          * @param args Event parameters (depends on event type)
          */
         onPersonSelected: function ConsoleGroups_SearchPanelHandler_onPersonSelected(e, args)
         {
            // This is a "global" event so we ensure the event is for the current panel by checking panel visibility.
            if (this._visible)
            {
               var name = args[1].firstName + " " + args[1].lastName;
               this.widgets.addUserPanel.hide();
               this._addToGroup(
                     args[1].userName,
                     this._selectedParentGroupShortName,
                     parent._msg("message.adduser-success", name),
                     parent._msg("message.adduser-failure", name));
            }
         },

         /**
          * Called when the user clicks the new group icon in the column browser header
          *
          * @method onNewGroupClick
          * @param columnInfo
          */
         onNewGroupClick: function ConsoleGroups_SearchPanelHandler__onNewGroupClick(columnInfo)
         {
            // Send avenet so the create panel will be displayed
            YAHOO.Bubbling.fire('newGroup',
            {
               group: columnInfo.parent ? columnInfo.parent.shortName : undefined,
               groupDisplayName: columnInfo.parent ? columnInfo.parent.label : parent._msg("label.theroot")
            });
         },

         /**
          * Called when the user clicks the add gorup icon in the column browser header
          *
          * @method onAddGroupClick
          * @param columnInfo
          */
         onAddGroupClick: function ConsoleGroups_SearchPanelHandler__onAddGroupClick(columnInfo)
         {
            this._selectedParentGroupShortName = columnInfo.parent.shortName;
            this.modules.searchGroupFinder.clearResults();
            this.widgets.addGroupPanel.show();
         },

         /**
          * Called when the user clicks the add user icon in the column browser header
          *
          * @method onAddUserClick
          * @param columnInfo
          */
         onAddUserClick: function ConsoleGroups_SearchPanelHandler_onAddUserClick(columnInfo)
         {
            this._selectedParentGroupShortName = columnInfo.parent.shortName;
            this.modules.searchPeopleFinder.clearResults();
            this.widgets.addUserPanel.show();
         },

         /**
          * Called when the user clicks a groups delete icon in the column browser
          *
          * @method onDeleteClick
          * @param ctx An object describing the group and its parent group
          *        ctx.columnInfo describes the clicked groups column (and its parent group)
          *        ctx.itemInfo describes the clicked group
          */
         onDeleteClick: function ConsoleGroups_SearchPanelHandler_onDeleteClick(ctx)
         {
            this._confirmDeleteGroup(
                  ctx.itemInfo.shortName,
                  ctx.itemInfo.fullName,
                  ctx.itemInfo.label,
                  ctx.columnInfo.parent ? ctx.columnInfo.parent.shortName : null,
                  ctx.columnInfo.parent ? ctx.columnInfo.parent.label : parent._msg("label.theroot"));
         },

         /**
          * Called when the user clicks a users delete icon in the column browser
          *
          * @method onUserRemoveClick
          * @param ctx An object describing the group and its parent group
          *        ctx.columnInfo describes the clicked groups column (and its parent group)
          *        ctx.itemInfo describes the clicked group
          */
         onUserRemoveClick: function ConsoleGroups_SearchPanelHandler_onUserRemoveClick(ctx)
         {
            this._confirmRemoveUser(ctx.columnInfo.parent.shortName, ctx.itemInfo.shortName, ctx.itemInfo.label);
         },

         /**
          * Called when the user clicks a groups update icon in the column browser
          *
          * @method onUpdateClick
          * @param ctx An object describing the group and its parent group
          *        ctx.columnInfo describes the clicked groups column (and its parent group)
          *        ctx.itemInfo describes the clicked group
          */
         onUpdateClick: function ConsoleGroups_SearchPanelHandler_onUpdateClick(ctx)
         {
            YAHOO.Bubbling.fire('updateGroup', {group: ctx.itemInfo.shortName, groupDisplayName: ctx.itemInfo.label});
         },

         /**
          * MODULE TEMPLATE LOAD HANDLERS
          */

         /**
          * Called when the people finder template has been loaded.
          * Creates a dialog and inserts the people finder for choosing users to add.
          *
          * @method onPeopleFinderLoaded
          * @param response The server response
          */
         onPeopleFinderLoaded: function ConsoleGroups_SearchPanelHandler_onPeopleFinderLoaded(response)
         {
            // Inject the component from the XHR request into it's placeholder DIV element
            var finderDiv = Dom.get(parent.id + "-search-peoplefinder");
            finderDiv.innerHTML = response.serverResponse.responseText;

            // Create the Add User dialog
            this.widgets.addUserPanel = Alfresco.util.createYUIPanel(parent.id + "-peoplepicker");

            // Find the People Finder by container ID
            this.modules.searchPeopleFinder = Alfresco.util.ComponentManager.get(parent.id + "-search-peoplefinder");

            // Set the correct options for our use
            this.modules.searchPeopleFinder.setOptions(
            {
               singleSelectMode: true
            });

            // Make sure we listen for events when the user selects a person
            YAHOO.Bubbling.on("personSelected", this.onPersonSelected, this);
         },

         /**
          * Called when the group finder template has been loaded.
          * Creates a dialog and inserts the group finder for choosing groups to add.
          *
          * @method onGroupFinderLoaded
          * @param response The server response
          */
         onGroupFinderLoaded: function ConsoleGroups_SearchPanelHandler_onGroupFinderLoaded(response)
         {
            // Inject the component from the XHR request into it's placeholder DIV element
            var finderDiv = Dom.get(parent.id + "-search-groupfinder");
            finderDiv.innerHTML = response.serverResponse.responseText;

            // Create the Add Group dialog
            this.widgets.addGroupPanel = Alfresco.util.createYUIPanel(parent.id + "-grouppicker")

            // Find the Group Finder by container ID
            this.modules.searchGroupFinder = Alfresco.util.ComponentManager.get(parent.id + "-search-groupfinder");

            // Set the correct options for our use
            this.modules.searchGroupFinder.setOptions(
            {
               singleSelectMode: true
            });

            // Make sure we listen for events when the user selects a group
            YAHOO.Bubbling.on("itemSelected", this.onGroupSelected, this);
         },

         /**
          * COLUMN BROWSER CALLBACKS
          */

         /**
          * Called by the Column Browser to let this component transform the custom server reponse to a
          * columnInfo object that the Column Browser understands
          *
          * @method onBuildColumnInfo
          * @param serverResponse Response from the server containing column data OR null if a leaf was clicked
          * @param itemInfo The parent item that was clicked to get the column data OR null if it is the root column
          */
         onBuildColumnInfo: function ConsoleGroups_SearchPanelHandler_onBuildColumnInfo(serverResponse, itemInfo)
         {
            // Create columnInfo and its header
            var headerButtons = [];
            if (!itemInfo || itemInfo.cssClass == 'groups-item-group')
            {
               headerButtons.push({
                  title: (itemInfo ? parent._msg("button.newsubgroup") : parent._msg("button.newgroup")),
                  cssClass: "groups-newgroup-button",
                  click: {
                     fn: this.onNewGroupClick,
                     scope: this
                  }
               });
            }
            if (itemInfo && itemInfo.cssClass == 'groups-item-group')
            {
               // Only add the following button for NON root columns 
               headerButtons.push({
                  title: parent._msg("button.addgroup"),
                  cssClass: "groups-addgroup-button",
                  click: {
                     fn: this.onAddGroupClick,
                     scope: this
                  }
               });
               headerButtons.push({
                  title: parent._msg("button.adduser"),
                  cssClass: "groups-adduser-button",
                  click: {
                     fn: this.onAddUserClick,
                     scope: this
                  }
               });
            }

            // Create column descriptor
            var column = {
               parent: itemInfo,
               header: {
                  buttons: headerButtons
               },
               body: {
                  items: []
               }
            };

            // Get data from request
            var obj = {};
            if (serverResponse)
            {
               // Parse response if there was one
               obj = YAHOO.lang.JSON.parse(serverResponse.responseText);

               // Translate group paging attributes to columnbrowser pagination attributes 
               if (obj.paging)
               {
                  column.pagination = {
                     totalRecords : obj.paging.totalItems,
                     recordOffset: obj.paging.skipCount
                  };
               }
            }

            var updategroupButton =
            {
               title: parent._msg("button.updategroup"),
               cssClass: "groups-update-button",
               click: {
                  fn: this.onUpdateClick,
                  scope: this
               }
            };

            var deletegroupButton =
            {
               title: parent._msg("button.deletegroup"),
               cssClass: "groups-delete-button",
               click: {
                  fn: this.onDeleteClick,
                  scope: this
               }
            };

            var deletegroupButtonDisabled =
            {
               title: parent._msg("button.deletegroup"),
               cssClass: "groups-delete-button-disabled",
               click: {
                  fn: function()
                  {
                     return false;
                  },
                  scope: this
               }
            };

            // Create item buttons for users and groups
            var groupButtons = [];
            groupButtons.push(updategroupButton);
            groupButtons.push(deletegroupButton);

            var groupButtonsDisabled = [];
            groupButtonsDisabled.push(updategroupButton);
            groupButtonsDisabled.push(deletegroupButtonDisabled);

            var usersButtons = [
               {
                  title: parent._msg("button.removeuser"),
                  cssClass: "users-remove-button",
                  click: {
                     fn: this.onUserRemoveClick,
                     scope: this
                  }
               }
            ];

            // Transform server respons to itemInfos and add them to the columnInfo's body
            for (var i = 0; obj.data && i < obj.data.length; i++)
            {
               var o = obj.data[i];
               var label = o.displayName;
               if (o.displayName !== o.shortName)
               {
                  label += " (" + o.shortName + ")";
               }
               var item = {
                  shortName: o.shortName,
                  fullName: o.fullName,
                  url: o.authorityType == 'GROUP' ? Alfresco.constants.PROXY_URI + o.url + "/children?sortBy=" + this.sortBy : null,
                  hasNext: o.groupCount > 0 || o.userCount > 0,
                  label: label,
                  next : null,
                  cssClass: o.authorityType == 'GROUP' ? "groups-item-group" : "groups-item-user",
                  buttons: o.authorityType == 'GROUP' ? (Alfresco.util.arrayContains(o.zones, "APP.SHARE") ? groupButtonsDisabled : groupButtons) : usersButtons
               };
               column.body.items.push(item);
            }

            return column;
         },

         /**
          * PUBLIC METHODS
          */

         /**
          * Invoke search based on the "state", use the state-query parameter that is stored in the parent object
          * each time a state is set.
          *
          * @method doSearch
          */
         doSearch: function ConsoleGroups_SearchPanelHandler_doSearch()
         {
            // check search length again as we may have got here via history navigation
            if (!this.isSearching && parent.query !== undefined && parent.query.length >= parent.options.minSearchTermLength)
            {
               this.isSearching = true;
               
               var me = this;

               // Reset the custom error messages
               me._setDefaultDataTableErrors(me.widgets.dataTable);

               // Don't display any message
               me.widgets.dataTable.set("MSG_EMPTY", parent._msg("message.searching"));

               // Empty results table
               me.widgets.dataTable.deleteRows(0, me.widgets.dataTable.getRecordSet().getLength());

               var successHandler = function ConsoleGroups__ps_successHandler(sRequest, oResponse, oPayload)
               {
                  me._enableSearchUI();
                  me._setDefaultDataTableErrors(me.widgets.dataTable);
                  me.widgets.dataTable.onDataReturnInitializeTable.call(me.widgets.dataTable, sRequest, oResponse, oPayload);
               };

               var failureHandler = function ConsoleGroups__ps_failureHandler(sRequest, oResponse)
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
                        me.widgets.dataTable.set("MSG_ERROR", parent._msg("message.noresults.short"));
                        me.widgets.dataTable.showTableMessage(parent._msg("message.noresults.short"), YAHOO.widget.DataTable.CLASS_ERROR);
                        me._setResultsMessage("message.noresults");
                     }
                     catch(e)
                     {
                        me._setDefaultDataTableErrors(me.widgets.dataTable);
                     }
                  }
               };

               // Send the query to the server and disable search button
               me.widgets.dataSource.sendRequest(me._buildSearchParams(parent.query),
               {
                  success: successHandler,
                  failure: failureHandler,
                  scope: parent
               });
               me._setResultsMessage("message.searchingFor", $html(parent.query));

               // Disable search button and display a wait feedback message if the groups hasn't been found yet
               me.widgets.searchButton.set("disabled", true);
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
         _enableSearchUI: function ConsoleGroups_SearchPanelHandler__enableSearchUI()
         {
            // Enable search button and close the wait feedback message if present
            if (this.widgets.feedbackMessage && this.widgets.feedbackMessage.cfg.getProperty("visible"))
            {
               this.widgets.feedbackMessage.hide();
            }
            this.widgets.searchButton.set("disabled", false);
            this.isSearching = false;
         },

         /**
          * PRIVATE METHODS
          */

         /**
          * Asks the users if he is sure he wants to delete the group
          *
          * @param shortName shortName The id of the group to delete
          * @param fullName The fullName of the group to delete (needed only if removing group from parent group)
          * @param displayName The displayName of the group to delete
          * @param parentShortName The shortName of the parent group to remove group from (needed only if removing group from parent group)
          * @param parentDisplayName The displayName of the parent group to remove group from (needed only if removing group from parent group)
          */
         _confirmDeleteGroup: function ConsoleGroups_SearchPanelHandler_confirmDeleteGroup(shortName, fullName, displayName, parentShortName, parentDisplayName)
         {
            var me = this;
            parent.getParentGroups(shortName,
            {
               fn: function(groups)
               {
                  // Remove previous listeners so we don't make duplicate calls and add a new one later
                  this.widgets.deleteGroupOkButton.removeListener("click", this.onConfirmedDeleteGroupClick);
                  var callbackObj =
                  {
                     shortName: shortName,
                     fullName: fullName,
                     displayName: displayName,
                     parentShortName: parentShortName,
                     parentDisplayName: parentDisplayName
                  };

                  // Make sure the dialog is displayed correctly
                  if (!groups || groups.length == 0)
                  {
                     // Group is root group or has only 1 parent
                     Dom.addClass(parent.id + "-multiparent", "hidden");
                     Dom.removeClass(parent.id + "-singleparent", "hidden");
                     Dom.get(parent.id + "-singleparent-message").innerHTML = parent._msg("panel.deletegroup.singleparentmessage", $html(displayName));
                     this.widgets.deleteGroupOkButton.on("click", this.onConfirmedDeleteGroupClick, callbackObj, this);
                  }
                  else
                  {
                     // Group has multiple parents
                     Dom.addClass(parent.id + "-singleparent", "hidden");
                     Dom.removeClass(parent.id + "-multiparent", "hidden");
                     Dom.get(parent.id + "-multiparent-message").innerHTML = parent._msg("panel.deletegroup.oneormultiparentmessage", $html(displayName));
                     Dom.get(parent.id + "-remove-message").innerHTML = parent._msg("panel.deletegroup.removemessage", $html(displayName), $html(parentDisplayName));
                     Dom.get(parent.id + "-delete-message").innerHTML = parent._msg("panel.deletegroup.deletemessage", $html(displayName));
                     Dom.get(parent.id + "-searchdelete-message").innerHTML = parent._msg("panel.deletegroup.searchdeletemessage", $html(displayName));

                     // Lets display the groups parents to the user, but only the first 10
                     var parentStr = "", displayLimit = 10;
                     for (var i = 0; i < groups.length && i < displayLimit; i++)
                     {
                        parentStr += groups[i].displayName + (i < groups.length - 1 ? ", " : "");
                     }
                     if (i >= displayLimit)
                     {
                        parentStr += parent._msg("label.moregroups", groups.length - displayLimit);
                     }
                     Dom.get(parent.id + "-parents").innerHTML = $html(parentStr);

                     if (parentShortName)
                     {
                        // Display both the option to remove from parent group and delete the group
                        Dom.get(parent.id + "-remove").checked = true;
                        Dom.removeClass(parent.id + "-removerow", "hidden");
                        Dom.removeClass(parent.id + "-deleterow", "hidden");
                        Dom.addClass(parent.id + "-searchdeleterow", "hidden");
                     }
                     else
                     {
                        /**
                         * The group was clicked in a context where none of the parents was displayed,
                         * in other words in the search list. There fore we can't display the option of just
                         * removing the group.
                         */
                        Dom.get(parent.id + "-delete").checked = true;
                        Dom.addClass(parent.id + "-removerow", "hidden");
                        Dom.addClass(parent.id + "-deleterow", "hidden");
                        Dom.removeClass(parent.id + "-searchdeleterow", "hidden");
                     }

                     // Make sure the callback knows what mode the dialog was displayed in
                     callbackObj.multiParentMode = true;
                     this.widgets.deleteGroupOkButton.on("click", this.onConfirmedDeleteGroupClick, callbackObj, this);
                  }
                  // Show the dialog
                  this.widgets.deleteGroupPanel.show();
               },
               scope: this
            }, "message.delete-failure");
         },

         /**
          * Asks the users if he is sure he wants to remove the user from the group.
          *
          * @param groupId The id of the group to remove the user from
          * @param userId The id of the user to remove
          * @param userDisplayName The displayName of the user
          */
         _confirmRemoveUser: function ConsoleGroups_SearchPanelHandler__confirmRemoveUser(groupId, userId, userDisplayName)
         {
            var me = this;
            Alfresco.util.PopupManager.displayPrompt(
            {
               title: parent._msg("message.confirm.removeuser.title"),
               text: parent._msg("message.confirm.removeuser", userDisplayName),
               buttons: [
                  {
                     text: parent._msg("button.yes"),
                     handler: function ConsoleGroups__removeUser_confirmYes()
                     {
                        this.destroy();
                        me._removeUser.call(me, groupId, userId, userDisplayName);
                     }
                  },
                  {
                     text: parent._msg("button.no"),
                     handler: function ConsoleGroups__removeUser_confirmNo()
                     {
                        this.destroy();
                     },
                     isDefault: true
                  }]
            });
         },

         /**
          * Deletes the group from the repository.
          *
          * @param shortName The shortName of the group
          * @param displayName The displayName  of the group
          * shall be removed from parent group. If now present group will be deleted.
          */
         _deleteGroup: function ConsoleGroups_SearchPanelHandler__deleteGroup(shortName, displayName)
         {
            var url = Alfresco.constants.PROXY_URI + "api/groups/" + encodeURIComponent(shortName);
            this._doDeleteCall(url, displayName);
         },

         /**
          * Removes the group from a parent group
          *
          * @param fullName The full authority name of the group
          * @param parentShortName the shortname of the parent group
          * @param displayName The displayName  of the group
          */
         _removeGroup: function ConsoleGroups_SearchPanelHandler__removeGroup(fullName, parentShortName, displayName)
         {
            if (parentShortName == null)
            {
               // todo implement when webscript api supports it
               // This isn't supported by the webscript api yet
               Alfresco.util.PopupManager.displayPrompt(
               {
                  title: parent._msg("message.failure"),
                  text: parent._msg("message.noRemoveGroupFromRootSupport")
               });
               return;
            }
            var url = Alfresco.constants.PROXY_URI + "api/groups/" + encodeURIComponent(parentShortName) + "/children/" + encodeURIComponent(fullName);
            this._doDeleteCall(url, displayName);
         },

         /**
          * Deletes or removes a group depending on the url
          *
          * @param url Url to use to remove or delete group
          */
         _doDeleteCall: function ConsoleGroups_SearchPanelHandler__deleteCall(url, displayName)
         {
            var groupDisplayName = displayName;
            Alfresco.util.Ajax.request( 
            {
               method: Alfresco.util.Ajax.DELETE,
               url: url,
               successCallback:
               {
                  fn: function(o)
                  {
                     // Refresh column browser
                     var paths = this.widgets.columnbrowser.get("urlPath");
                     this.widgets.columnbrowser.load(paths, true);

                     // Refresh search table
                     this.doSearch();

                     // Display success message
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: parent._msg("message.delete-success", groupDisplayName)
                     });
                  },
                  scope: this
               },
               failureMessage: parent._msg("message.delete-failure", groupDisplayName)
            });
         },

         /**
          * Remove the user from the group
          *
          * @param groupId The id of the group
          * @param userId The id of the user
          * @param userDisplayName The displayName of the user
          */
         _removeUser: function ConsoleGroups_SearchPanelHandler__removeUser(groupId, userId, userDisplayName)
         {
            var name = userDisplayName;
            Alfresco.util.Ajax.request( 
            {
               method: Alfresco.util.Ajax.DELETE,
               url: Alfresco.constants.PROXY_URI + "api/groups/" + encodeURIComponent(groupId) + "/children/" + encodeURIComponent(userId),
               successCallback:
               {
                  fn: function(o)
                  {
                     // Refresh column browser
                     var paths = this.widgets.columnbrowser.get("urlPath");
                     this.widgets.columnbrowser.load(paths, true);

                     // Display success message
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: parent._msg("message.removeuser-success", name)
                     });
                  },
                  scope: this
               },
               failureMessage: parent._msg("message.removeuser-failure", name)
            });
         },

         /**
          * Adds a user or group to a parent group.
          *
          * @param objectId The id to a user (userName) or a group (fullName)
          * @param parentGroupShortName The shortName of the parent group that the object shall be added under
          * @param successMessage Message to display if the request is successful
          * @param failureMessage Message to display if the request fails
          */
         _addToGroup: function ConsoleGroups_SearchPanelHandler__addToGroup(objectId, parentGroupShortName, successMessage, failureMessage)
         {
            Alfresco.util.Ajax.jsonPost(
            {
               url: Alfresco.constants.PROXY_URI + "api/groups/" + encodeURIComponent(parentGroupShortName) + "/children/" + encodeURIComponent(objectId),
               successCallback:
               {
                  fn: function(o)
                  {
                     // Refresh column browser
                     var paths = this.widgets.columnbrowser.get("urlPath");
                     this.widgets.columnbrowser.load(paths, true);

                     // Display success message
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: successMessage
                     });
                  },
                  scope: this
               },
               failureMessage: failureMessage
            });
         },

         /**
          * Setup the YUI DataTable with custom renderers.
          *
          * @method _setupDataTable
          * @private
          */
         _setupDataTable: function ConsoleGroups_SearchPanelHandler__setupDataTable()
         {
            var me = this;

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
             * Group actions custom datacell formatter
             *
             * @method renderActions
             */
            var renderActions = function renderActions(elCell, oRecord, oColumn, oData)
            {
               // fire the 'updateGroupClick' event when the group has been clicked
               var updateLink = document.createElement("a");
               //updateLink.setAttribute("href", "#");
               Dom.addClass(updateLink, "update");
               updateLink.innerHTML = "&nbsp;";
               YAHOO.util.Event.addListener(updateLink, "click", function(e)
               {
                  YAHOO.Bubbling.fire('updateGroup',
                  {
                     group: oRecord.getData("shortName"),
                     groupDisplayName: oRecord.getData("displayName"),
                     query: this.query
                  });
               }, null, parent);
               elCell.appendChild(updateLink);

               if (Alfresco.util.arrayContains(oRecord.getData("zones"), "APP.SHARE"))
               {
                  var disabledDeleteLink = document.createElement("a");
                  Dom.addClass(disabledDeleteLink, "delete-disabled");
                  disabledDeleteLink.innerHTML = "&nbsp;";
                  elCell.appendChild(disabledDeleteLink);
               }
               else
               {
                  var deleteLink = document.createElement("a");
                  Dom.addClass(deleteLink, "delete");
                  deleteLink.innerHTML = "&nbsp;";
                  YAHOO.util.Event.addListener(deleteLink, "click", function(e)
                  {
                     me._confirmDeleteGroup(
                           oRecord.getData("shortName"),
                           null,
                           oRecord.getData("displayName"),
                           null,
                           null);
                  });
                  elCell.appendChild(deleteLink);
               }
            };

            // DataTable column defintions
            var columnDefinitions =
            [
               { key: "shortName", label: parent._msg("label.shortname"), sortable: true, formatter: renderCellSafeHTML },
               { key: "displayName", label: parent._msg("label.displayname"), sortable: true, formatter: renderCellSafeHTML },
               { key: "actions",     label: parent._msg("label.actions"), sortable: false, formatter: renderActions }
            ];

            // DataTable definition
            this.widgets.dataTable = new YAHOO.widget.DataTable(parent.id + "-datatable", columnDefinitions, this.widgets.dataSource,
            {
               initialLoad: false,
               renderLoopSize: 32,
               dynamicData: true,
               sortedBy:
               {
                  key: "displayName",
                  dir: "asc"
               },
               generateRequest:  function(oState, oSelf) {

                  // Set defaults
                  oState = oState || {pagination:null, sortedBy:null};
                 var sort = encodeURIComponent((oState.sortedBy) ? oState.sortedBy.key : oSelf.getColumnSet().keys[0].getKey());
                 var dir = (oState.sortedBy && oState.sortedBy.dir === YAHOO.widget.DataTable.CLASS_DESC) ? "desc" : "asc";

                 // Build the request
                 var query =  "sortBy=" + sort + "&dir=" + dir;

                 if (parent.query)
                 {
                     query = query + "&shortNameFilter=" + encodeURIComponent(parent.query);
                 }

                 // MNT-13002 fix, determine whether system groups should be displayed
                 var showAll = Dom.get(parent.id + "-show-all").checked;
                 query = (showAll ? query : query + "&zone=APP.DEFAULT")

                 return query;

               },
               MSG_EMPTY: parent._msg("message.empty")
            });
         },

         /**
          * Resets the YUI DataTable errors to our custom messages
          * NOTE: Scope could be YAHOO.widget.DataTable, so can't use "this"
          *
          * @method _setDefaultDataTableErrors
          * @param dataTable Instance of the DataTable
          * @private
          */
         _setDefaultDataTableErrors: function ConsoleGroups_SearchPanelHandler__setDefaultDataTableErrors(dataTable)
         {
            dataTable.set("MSG_EMPTY", parent._msg("message.empty", "Alfresco.ConsoleGroups"));
            dataTable.set("MSG_ERROR", parent._msg("message.error", "Alfresco.ConsoleGroups"));
         },

         /**
          * Build URI parameters for People List JSON data webscript
          *
          * @method _buildSearchParams
          * @param query User search term
          * @private
          */
         _buildSearchParams: function ConsoleGroups_SearchPanelHandler__buildSearchParams(query)
         {
            var query = "shortNameFilter=" + encodeURIComponent(query);
            var showAll = Dom.get(parent.id + "-show-all").checked;
            return (showAll ? query : query + "&zone=APP.DEFAULT") +  "&maxItems=" + this.maxItems + "&sortBy=" + this.sortBy;
         },

         /**
          * Set the message in the Results Bar area
          *
          * @method _setResultsMessage
          * @param messageId The messageId to display
          * @private
          */
         _setResultsMessage: function ConsoleGroups_SearchPanelHandler__setResultsMessage(messageId, arg1, arg2)
         {
            var resultsDiv = Dom.get(parent.id + "-search-bar-text");
            resultsDiv.innerHTML = parent._msg(messageId, arg1, arg2);
         }
      });
      this.panelHandlers.searchPanelHandler = new SearchPanelHandler();

      /* Create Group Panel Handler */
      CreatePanelHandler = function CreatePanelHandler_constructor()
      {
         CreatePanelHandler.superclass.constructor.call(this, "create");
      };

      YAHOO.extend(CreatePanelHandler, Alfresco.ConsolePanelHandler,
      {
         /**
          * INSTANCE VARIABLES
          */

         /**
          * Keeps track if this panel is visble or not
          *
          * @property _visible
          * @type Boolean
          */
         _visible: false,

         /**
          * Keeps track if this panel shall request the view panel to refresh after a cancel click
          *
          * @property _refresh
          * @type Boolean
          */
         _refresh: false,

         /**
          * PANEL LIFECYCLE CALLBACKS
          */

         /**
          * Called by the ConsolePanelHandler when this panel shall be loaded
          *
          * @method onLoad
          */
         onLoad: function ConsoleGroups_CreatePanelHandler_onLoad()
         {
            // Buttons
            this.widgets.creategroupOkButton = new YAHOO.widget.Button(parent.id + "-creategroup-ok-button",
            {
               type: "button"
            });
            this.widgets.creategroupOkButton.on("click", this.onCreateGroupOKClick, null, this);

            this.widgets.creategroupAnotherButton = new YAHOO.widget.Button(parent.id + "-creategroup-another-button",
            {
               type: "button"
            });
            this.widgets.creategroupAnotherButton.on("click", this.onCreateGroupAnotherClick, null, this);

            this.widgets.creategroupCancelButton = new YAHOO.widget.Button(parent.id + "-creategroup-cancel-button",
            {
               type: "button"
            });
            this.widgets.creategroupCancelButton.on("click", this.onCreateGroupCancelClick, null, this);

            // Form definition
            var form = new Alfresco.forms.Form(parent.id + "-create-form");
            form.setSubmitElements([this.widgets.creategroupOkButton, this.widgets.creategroupAnotherButton]);

            // Form field validation
            form.addValidation(parent.id + "-create-shortname", Alfresco.forms.validation.mandatory, null, "keyup");
            form.addValidation(parent.id + "-create-shortname", Alfresco.forms.validation.nodeName, null, "keyup");
            form.addValidation(parent.id + "-create-shortname", Alfresco.forms.validation.length,
            {
               max: 100,
               crop: true,
               includeWhitespace: false
            }, "keyup");
            form.addValidation(parent.id + "-create-displayname", Alfresco.forms.validation.mandatory, null, "keyup");
            form.addValidation(parent.id + "-create-displayname", Alfresco.forms.validation.length,
            {
               max: 255,
               crop: true,
               includeWhitespace: false
            }, "keyup");

            // Initialize form
            form.init();
            this.forms.createForm = form;
         },

         /**
          * Called by the ConsolePanelHandler when this panel shall be loaded
          *
          * @method onBeforeShow
          */
         onBeforeShow: function ConsoleGroups_CreatePanelHandler_onBeforeShow()
         {
            // Hide the main panel area before it is displayed - so we don't show
            // old data to the user before the onShow() method paints the results
            Dom.setStyle(parent.id + "-create-main", "visibility", "hidden");
            this.clear();
         },

         /**
          * Clears the form fields, makes sure buttons are in correct state and sets focus
          *
          * @method clear
          */
         clear: function clear()
         {
            var elName = Dom.get(parent.id + "-create-shortname");
            if (elName.value.length !== 0)
            {
               elName.value = "";
            }
            var elDisplay = Dom.get(parent.id + "-create-displayname");
            if (elDisplay.value.length !== 0)
            {
               elDisplay.value = "";
            }
            if (this.forms.createForm !== null)
            {
               this.forms.createForm.validate(Alfresco.forms.Form.NOTIFICATION_LEVEL_NONE);
            }
         },

         /**
          * Called by the ConsolePanelHandler when this panel is shown
          *
          * @method onShow
          */
         onShow: function ConsoleGroups_CreatePanelHandler_onShow()
         {
            this._visible = true;
            this._refresh = false;
            window.scrollTo(0, 0);

            // Make main panel area visible
            Dom.setStyle(parent.id + "-create-main", "visibility", "visible");

            Dom.get(parent.id + "-create-shortname").focus();
         },

         /**
          * Called by the ConsolePanelHandler when this panel is hidden
          *
          * @method onHide
          */
         onHide: function ConsoleGroups_CreatePanelHandler_onHide()
         {
            this._visible = false;
         },

         /**
          * BUTTON EVENT HANDLERS
          */

         /**
          * Fired when the Create Group OK button is clicked.
          *
          * @method onCreateGroupOKClick
          * @param e DomEvent
          * @param args Event parameters (depends on event type)
          */
         onCreateGroupOKClick: function ConsoleGroups_CreatePanelHandler_onCreateGroupOKClick(e, args)
         {
            var successHandler = function(response)
            {
               window.scrollTo(0, 0);
               Alfresco.util.PopupManager.displayMessage(
               {
                  text: parent._msg("message.create-success")
               });
               parent.refreshUIState(
               {
                  "panel": "search",
                  "refresh": "true"
               });
            };
            this._createGroup(successHandler);
         },

         /**
          * Fired when the Create Group Cancel button is clicked.
          *
          * @method onCreateGroupCancelClick
          * @param e {object} DomEvent
          * @param args {array} Event parameters (depends on event type)
          */
         onCreateGroupCancelClick: function ConsoleGroups_CreatePanelHandler_onCreateGroupCancelClick(e, args)
         {
            parent.refreshUIState(
            {
               "panel": "search",
               "refresh": this._refresh ? "true" : "false"
            });
         },

         /**
          * Fired when the Create Another Group button is clicked.
          *
          * @method onCreateGroupAnotherClick
          * @param e DomEvent
          * @param args Event parameters (depends on event type)
          */
         onCreateGroupAnotherClick: function ConsoleGroups_CreatePanelHandler_onCreateGroupAnotherClick(e, args)
         {
            var successHandler = function(response)
            {
               // Scroll to top and notify user
               window.scrollTo(0, 0);
               Alfresco.util.PopupManager.displayMessage(
               {
                  text: parent._msg("message.create-success")
               });

               // Make sure we refresh view panel if cancel is clicked
               this._refresh = true;

               // Clear old values so new ones can be entered
               this.clear();
               Dom.get(parent.id + "-create-shortname").focus();
            };
            this._createGroup(successHandler);
         },

         /**
          * PRIVATE METHODS
          */

         /**
          * Create a group, but first check if it already exists
          *
          * @method _createGroup
          * @param handler Handler function to be called on successful creation
          * @private
          */
         _createGroup: function ConsoleGroups_CreatePanelHandler__createGroup(successHandler)
         {
            var me = this;
            
            var form = this.forms.createForm;
            if (!form.validate())
            {
               form._setAllFieldsAsVisited();
               return;
            }
            
            var shortName = YAHOO.lang.trim(Dom.get(parent.id + "-create-shortname").value);
            parent.getParentGroups(shortName,
            {
               fn: function(groups)
               {
                  if (groups)
                  {
                     // The group alredy existed, now let's see if the identifer already is placed under this group
                     var alreadyThere = false;
                     var parentStr = "";
                     for (var i = 0; i < groups.length; i++)
                     {
                        parentStr += groups[i].displayName + (i < groups.length - 1 ? ", " : "");
                     }
                     parentStr = parentStr.length > 0 ? parentStr : parent._msg("label.theroot");
                     Alfresco.util.PopupManager.displayPrompt(
                     {
                        text: parent._msg("message.confirm.add", shortName, parentStr, parent.group ? parent.group : parent._msg("label.theroot")),
                        buttons: [
                           {
                              text: parent._msg("button.ok"),
                              handler: function ConsoleGroups__createGroup_confirmOk()
                              {
                                 // Hide Prompt
                                 this.destroy();
                                 if (parent.group)
                                 {
                                    me._createGroupAfterExistCheck.call(me, successHandler);
                                 }
                                 else
                                 {
                                    // todo implement when webscript api supports it
                                    // This isn't supported by the webscript api yet
                                    Alfresco.util.PopupManager.displayPrompt(
                                    {
                                       title: parent._msg("message.failure"),
                                       text: parent._msg("message.noAddGroupFromRootSupport")
                                    });
                                    return;

                                 }
                              }
                           },
                           {
                              text: parent._msg("button.cancel"),
                              handler: function ConsoleGroups__createGroup_confirmCancel()
                              {
                                 // Hide prompt
                                 this.destroy();
                              },
                              isDefault: true
                           }]
                     });
                  }
                  else
                  {
                     // Group didn't exist go ahead and create it
                     me._createGroupAfterExistCheck.call(me, successHandler);
                  }
               },
               scope: this
            }, "message.create-failure");
         },

         /**
          * Create the group
          *
          * @method _createGroupAfterExistCheck
          * @param handler {function} Handler function to be called on successful creation
          * @private
          */
         _createGroupAfterExistCheck: function ConsoleGroups_CreatePanelHandler__createGroupAfterExistCheck(successHandler)
         {
            // gather up the data for our JSON PUT request
            var shortName = YAHOO.lang.trim(Dom.get(parent.id + "-create-shortname").value);
            var displayName = YAHOO.lang.trim(Dom.get(parent.id + "-create-displayname").value);
            displayName = displayName == "" ? undefined : displayName;
            var groupObj = {};

            var url = Alfresco.constants.PROXY_URI + "api/";
            var sh = successHandler;
            if (parent.group && parent.group.length > 0)
            {
               url += "groups/" + encodeURIComponent(parent.group) + "/children/GROUP_" + encodeURIComponent(shortName);
               sh = function(response)
               {
                  if (displayName && shortName != displayName)
                  {
                     /**
                      * When a group is created by adding it to a parent group its not possible to
                      * set the displayName in the same call, then another call must be made to
                      * update the display name.
                      */
                     groupObj.displayName = displayName;
                     parent.panelHandlers.updatePanelHandler.updateGroupRequest(shortName, groupObj,
                     {
                        fn: successHandler,
                        scope: this
                     });
                  }
                  else
                  {
                     successHandler.call(this, response);
                  }
               };
            }
            else
            {
               url += "rootgroups/" + encodeURIComponent(shortName);
               if (displayName)
               {
                  groupObj.displayName = displayName;
               }
            }

            Alfresco.util.Ajax.jsonPost(
            {
               url: url,
               dataObj: groupObj,
               successCallback:
               {
                  fn: sh,
                  scope: this
               },
               failureCallback:
               {
                  fn: function(o)
                  {
                     var obj = YAHOO.lang.JSON.parse(o.serverResponse.responseText);
                     Alfresco.util.PopupManager.displayPrompt(
                     {
                        title: parent._msg("message.failure"),
                        text: parent._msg("message.create-failure", obj.message)
                     });
                  },
                  scope: this
               }
            });
         }

      });
      this.panelHandlers.createPanelHandler = new CreatePanelHandler();


      /* Update Group Panel Handler */
      UpdatePanelHandler = function UpdatePanelHandler_constructor()
      {
         UpdatePanelHandler.superclass.constructor.call(this, "update");
      };

      YAHOO.extend(UpdatePanelHandler, Alfresco.ConsolePanelHandler,
      {
         /**
          * INSTANCE VARIABLES
          */

         /**
          * Keeps track if this panel is visble or not
          *
          * @property _visible
          * @type Boolean
          */
         _visible: false,

         /**
          * PANEL LIFECYCLE CALLBACKS
          */

         /**
          * Called by the ConsolePanelHandler when this panel shall be loaded
          *
          * @method onLoad
          */
         onLoad: function ConsoleGroups_UpdatePanelHandler_onLoad()
         {
            // Buttons
            this.widgets.updategroupSaveButton = new YAHOO.widget.Button(parent.id + "-updategroup-save-button",
            {
               type: "button"
            });
            this.widgets.updategroupSaveButton.on("click", this.onUpdateGroupOKClick, null, this);
            this.widgets.updategroupCancelButton = new YAHOO.widget.Button(parent.id + "-updategroup-cancel-button",
            {
               type: "button"
            });
            this.widgets.updategroupCancelButton.on("click", this.onUpdateGroupCancelClick, null, this);

            // Form definition
            var form = new Alfresco.forms.Form(parent.id + "-update-form");
            form.setSubmitElements(this.widgets.updategroupSaveButton);

            // Form field validation
            form.addValidation(parent.id + "-update-displayname", Alfresco.forms.validation.mandatory, null, "keyup");
            form.addValidation(parent.id + "-update-displayname", Alfresco.forms.validation.length,
            {
               max: 255,
               crop: true,
               includeWhitespace: false
            }, "keyup");

            // Initialise the form
            form.init();
            this.forms.updateForm = form;
         },

         /**
          * Called by the ConsolePanelHandler just before this panel is about to be shown
          *
          * @method onBeforeShow
          */
         onBeforeShow: function ConsoleGroups_UpdatePanelHandler_onBeforeShow()
         {
            // Hide the main panel area before it is displayed - so we don't show
            // old data to the user before the Update() method paints the results
            Dom.setStyle(parent.id + "-update-main", "visibility", "hidden");
         },

         /**
          * Called by the ConsolePanelHandler when this panel is shown
          *
          * @method onShow
          */
         onShow: function ConsoleGroups_UpdatePanelHandler_onShow()
         {
            this._visible = true;
         },

         /**
          * Called by the ConsolePanelHandler when this panel is hidden
          *
          * @method onHide
          */
         onHide: function ConsoleGroups_UpdatePanelHandler_onHide()
         {
            this._visible = false;
         },

         /**
          * Called by the ConsolePanelHandler when this panel shall update its appearance
          *
          * @method onUpdate
          */
         onUpdate: function ConsoleGroups_UpdatePanelHandler_onUpdate()
         {
            var success = function(o)
            {
               // Properties section fields
               var group = o.json.data;
               Dom.get(parent.id + "-update-title").innerHTML = $html(group.displayName);
               Dom.get(parent.id + "-update-shortname").innerHTML = $html(group.shortName);
               Dom.get(parent.id + "-update-displayname").value = group.displayName;

               // Make sure buttons are in the correct state
               if (this.forms.updateForm)
               {
                  this.forms.updateForm.init();
               }

               // Make main panel area visible and focus
               window.scrollTo(0, 0);
               Dom.setStyle(parent.id + "-update-main", "visibility", "visible");
               Dom.get(parent.id + "-update-displayname").focus();
            };

            // make an ajax call to get group details
            Alfresco.util.Ajax.jsonGet(
            {
               url: Alfresco.constants.PROXY_URI + "api/groups/" + encodeURIComponent(parent.group),
               successCallback:
               {
                  fn: success,
                  scope: this
               },
               failureMessage: parent._msg("message.getgroup-failure", $html(parent.group))
            });
         },

         /**
          * BUTTON EVENT HANDLERS
          */

         /**
          * Fired when the Update User OK button is clicked.
          *
          * @method onUpdateGroupOKClick
          * @param e DomEvent
          * @param args Event parameters (depends on event type)
          */
         onUpdateGroupOKClick: function ConsoleGroups_UpdatePanelHandler_onUpdateGroupOKClick(e, args)
         {
            var handler = function(res)
            {
               window.scrollTo(0, 0);
               Alfresco.util.PopupManager.displayMessage(
               {
                  text: parent._msg("message.update-success")
               });

               var state = {"panel": "search", "refresh": "true"};
               if (parent.query)
               {
                  // If this panel was triggered from the search list, send back the query so the list will be displayed
                  state.query = parent.query;
               }
               parent.refreshUIState(state);
            };
            this._updateGroup(handler);
         },

         /**
          * Fired when the Update Group Cancel button is clicked.
          *
          * @method onUpdateGroupCancelClick
          * @param e {object} DomEvent
          * @param args {array} Event parameters (depends on event type)
          */
         onUpdateGroupCancelClick: function ConsoleGroups_UpdatePanelHandler_onUpdateGroupCancelClick(e, args)
         {
            var state = {"panel": "search", "refresh": "false"};
            if (parent.query)
            {
               // If this panel was triggered from the search list, send back the query so the list will be displayed
               state.query = parent.query;
            }
            parent.refreshUIState(state);
         },

         /**
          * PUBLIC METHODS
          */

         /**
          * Update a group - returning true on success, false on any error.
          *
          * @method updateGroupRequest
          * @param successCallback {function} Success callback to be called on successful update
          * @private
          */
         updateGroupRequest: function ConsoleGroups_UpdatePanelHandler_updateGroupRequest(shortName, groupObj, successCallback)
         {
            Alfresco.util.Ajax.jsonPut(
            {
               url: Alfresco.constants.PROXY_URI + "api/groups/" + encodeURIComponent(shortName),
               dataObj: groupObj,
               successCallback: successCallback,
               failureCallback:
               {
                  fn: function(o)
                  {
                     Alfresco.util.PopupManager.displayPrompt(
                     {
                        title: parent._msg("message.failure"),
                        text: parent._msg("message.update-failure", o.json.message ? o.json.message : "")
                     });
                  },
                  scope: this
               }
            });
         },

         /**
          * PRIVATE METHODS
          */

         /**
          * Update a group - returning true on success, false on any error.
          *
          * @method _updateGroup
          * @param successHandler Handler function to be called on successful update
          * @private
          */
         _updateGroup: function ConsoleGroups_UpdatePanelHandler__updateGroup(successHandler)
         {
            var form = this.forms.updateForm;
            if (!form.validate())
            {
               form._setAllFieldsAsVisited();
               return;
            }
            this.updateGroupRequest(parent.group,
            {
               displayName: YAHOO.lang.trim(Dom.get(parent.id + "-update-displayname").value)
            },
            {
               fn: successHandler,
               scope: this
            });
         }

      });
      this.panelHandlers.updatePanelHandler = new UpdatePanelHandler();

      return this;
   };

   YAHOO.extend(Alfresco.ConsoleGroups, Alfresco.ConsoleTool,
   {

      /* STATES */

      /**
       * The query to use in a search in the panel
       *
       * @property query
       * @type string
       * @default null
       */
      query: null,

      /**
       * Decides if browse panels data needs to be refreshed
       *
       * @property refresh
       * @type boolean
       * @default false
       */
      refresh: false,

      /**
       * The current group
       *
       * @property group
       * @type string
       * @default null
       */
      group: null,

      /**
       * The display name for the current group
       *
       * @property groupDisplayName
       * @type string
       * @default null
       */
      groupDisplayName: null,

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
          * Maximum number of groups & users to rdisplay at the same time in each column
          *
          * @property maxPageSize
          * @type int
          * @default 50
          */
         maxPageSize: 50
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function ConsoleGroups_onReady()
      {
         Alfresco.ConsoleGroups.superclass.onReady.call(this);
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
      onStateChanged: function ConsoleGroups_onStateChanged(e, args)
      {
         // Clear old states
         this.query = undefined;
         this.refresh = undefined;
         this.group = undefined;
         this.groupDisplayName = undefined;

         var state = this.decodeHistoryState(args[1].state);
         if (state.query !== undefined)
         {
            this.query = state.query;
         }
         if (state.refresh)
         {
            this.refresh = state.refresh == "true" ? true : false;
         }
         if (state.group)
         {
            this.group = state.group;
         }
         if (state.groupDisplayName)
         {
            this.groupDisplayName = state.groupDisplayName;
         }
         if (state.showAll)
         {
            this.showAll = state.showAll == "true" ? true : false;
         }
         
         // test if panel has actually changed?
         if (state.panel)
         {
            this.showPanel(state.panel);
         }

         if (this.currentPanelId === "search")
         {
            this.updateCurrentPanel();
         }
         else if (this.currentPanelId === "create" ||
                  (state.group && (this.currentPanelId === "view" || this.currentPanelId === "update")))
         {
            this.updateCurrentPanel();
         }
      },

      /**
       * New Group event handler
       *
       * @method onNewGroup
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onNewGroup: function ConsoleGroups_onNewGroup(e, args)
      {
         var parentGroup = args[1].group;
         this.refreshUIState(
         {
            "panel": "create",
            "group": parentGroup
         });
      },

      /**
       * Update Group event handler
       *
       * @method onUpdateGroup
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onUpdateGroup: function ConsoleGroups_onUpdateGroup(e, args)
      {
         var group = args[1].group;
         var query = args[1].query;
         var state = {
            "panel": "update",
            "group": group
         };
         // Remember query if cancel is clicked
         if (query)
         {
            state.query = query;
         }
         this.refreshUIState(state);
      },

      /**
       * Encode state object into a packed string for use as url history value.
       * Override base class.
       *
       * @method encodeHistoryState
       * @param obj {object} state object
       * @private
       */
      encodeHistoryState: function ConsoleGroups_encodeHistoryState(obj)
      {
         // wrap up current state values
         var stateObj = {};
         if (this.currentPanelId !== "")
         {
            stateObj.panel = this.currentPanelId;
         }

         // convert to encoded url history state - overwriting with any supplied values
         var state = "";
         if (obj.panel || stateObj.panel)
         {
            state += "panel=" + encodeURIComponent(obj.panel ? obj.panel : stateObj.panel);
         }
         if (obj.group)
         {
            state += state.length > 0 ? "&" : "";
            state += "group=" + encodeURIComponent(obj.group);
         }
         if (obj.query !== undefined)
         {
            state += state.length > 0 ? "&" : "";
            state += "query=" + encodeURIComponent(obj.query);
         }
         if (obj.refresh)
         {
            state += state.length > 0 ? "&" : "";
            state += "refresh=" + encodeURIComponent(obj.refresh);
         }
         if (obj.showAll !== undefined)
         {
            state += state.length > 0 ? "&" : "";
            state += "showAll=" + encodeURIComponent(obj.showAll);
         }
         return state;
      },

      /**
       * Helper method for getting the parent groups for group with identifier shortName
       *
       * @method _getParentGroups
       * @param shortName the group identifier
       * @param successCallback Callback object Called with the groups as the argument or null if group doesn't exist
       * @param failureMessage Displayed if an error (other than 404) occurs
       */
      getParentGroups: function ConsoleGroups_getParentGroups(shortName, successCallback, failureMessage)
      {
         Alfresco.util.Ajax.jsonGet(
         {
            url:  Alfresco.constants.PROXY_URI + "api/groups/" + encodeURIComponent(shortName) + "/parents?level=ALL&maxSize=10",
            successCallback:
            {
               fn: function(o)
               {
                  var groups = o.json.data ? o.json.data : [];

                  // Since we do
                  successCallback.fn.call(successCallback.scope ? successCallback.scope : this, groups);
               },
               scope: this
            },
            failureCallback:
            {
               fn: function(o)
               {
                  if (o.serverResponse.status == 404)
                  {
                     // group didn't exist just continue
                     successCallback.fn.call(successCallback.scope ? successCallback.scope : this, null);
                  }
                  else
                  {
                     // Notify the user that an error occured
                     Alfresco.util.PopupManager.displayPrompt(
                     {
                        title: this._msg("message.failure"),
                        text: this._msg(failureMessage, o.json.message ? o.json.message : "")
                     });
                  }
               },
               scope: this
            }
         });
      },

      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function ConsoleGroups__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.ConsoleGroups", Array.prototype.slice.call(arguments).slice(1));
      }
   });
})();