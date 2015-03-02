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
 * PeopleFinder component.
 * 
 * @namespace Alfresco
 * @class Alfresco.PeopleFinder
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
      $userProfile = Alfresco.util.userProfileLink;

   /**
    * PeopleFinder constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.PeopleFinder} The new PeopleFinder instance
    * @constructor
    */
   Alfresco.PeopleFinder = function(htmlId)
   {
      Alfresco.PeopleFinder.superclass.constructor.call(this, "Alfresco.PeopleFinder", htmlId, ["button", "container", "datasource", "datatable", "json"]);
      
      // Initialise prototype properties
      this.userSelectButtons = {};
      this.searchTerm = "";
      this.singleSelectedUser = "";
      this.selectedUsers = {};
      this.notAllowed = {};
      this.following = {};

      /**
       * Decoupled event listeners
       */
      YAHOO.Bubbling.on("personSelected", this.onPersonSelected, this);
      YAHOO.Bubbling.on("personDeselected", this.onPersonDeselected, this);

      return this;
   };
   
   YAHOO.lang.augmentObject(Alfresco.PeopleFinder,
   {
      VIEW_MODE_DEFAULT: "",
      VIEW_MODE_COMPACT: "COMPACT",
      VIEW_MODE_FULLPAGE: "FULLPAGE"
   });
   
   YAHOO.lang.extend(Alfresco.PeopleFinder, Alfresco.component.Base,
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
         siteId: "",

         /**
          * View mode
          * 
          * @property viewMode
          * @type string
          * @default Alfresco.PeopleFinder.VIEW_MODE_DEFAULT
          */
         viewMode: Alfresco.PeopleFinder.VIEW_MODE_DEFAULT,

         /**
          * Single Select mode flag
          * 
          * @property singleSelectMode
          * @type boolean
          * @default false
          */
         singleSelectMode: false,
         
         /**
          * Whether we show the current user or not flag
          * 
          * @property showSelf
          * @type boolean
          * @default true
          */
         showSelf: true,
         
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
          * Whether to set UI focus to this component or not
          * 
          * @property setFocus
          * @type boolean
          * @default false
          */
         setFocus: false,

         /**
          * Label to add button .
          *
          * @property addButtonLabel
          * @type string
          */
         addButtonLabel: null,

         /**
          * Suffix to add button label.
          *
          * @property addButtonSuffix
          * @type string
          */
         addButtonSuffix: "",

         /**
          * Override the default data webscript
          *
          * @property dataWebScript
          * @type string
          */
         dataWebScript: "",
         
         /**
          * Current userId.
          * 
          * @property userId
          * @type string
          */
         userId: ""
      },

      /**
       * Object container for storing YUI button instances, indexed by username.
       * 
       * @property userSelectButtons
       * @type object
       */
      userSelectButtons: null,
      
      /**
       * Current search term, obtained from form input field.
       * 
       * @property searchTerm
       * @type string
       */
      searchTerm: null,
      
      /**
       * Single selected user, for when in single select mode
       * 
       * @property singleSelectedUser
       * @type string
       */
      singleSelectedUser: null,

      /**
       * Selected users. Keeps a list of selected users for correct Add button state.
       * 
       * @property selectedUsers
       * @type object
       */
      selectedUsers: null,

      /**
       * Users for whom the action is not allowed
       * 
       * @property notAllowed
       * @type array
       */
      notAllowed: null,

      /**
       * Keeps track if this component is searching or not
       *
       * @property isSearching
       * @type Boolean
       */
      isSearching: false,
      
      /**
       * Is "Follow" action on server allowed
       *
       * @property followingAllowed
       * @type Boolean
       */
      followingAllowed: false,
      
      /**
       * Map of the users the current user is following
       * 
       * @property following
       * @type object
       */
      following: null,

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function PeopleFinder_onReady()
      {  
         var me = this;
         
         // View mode specific setup
         if (this.options.viewMode == Alfresco.PeopleFinder.VIEW_MODE_COMPACT)
         {
            Dom.addClass(this.id + "-body", "compact");
            Dom.removeClass(this.id + "-results", "hidden");
         }
         else if (this.options.viewMode == Alfresco.PeopleFinder.VIEW_MODE_FULLPAGE)
         {
            Dom.setStyle(this.id + "-results", "height", "auto");
            Dom.removeClass(this.id + "-help", "hidden");
            
            // Kick off ajax request to get the users the current user is following
            Alfresco.util.Ajax.jsonGet(
            {
               url: Alfresco.constants.PROXY_URI + "api/subscriptions/" + encodeURIComponent(this.options.userId) + "/following",
               successCallback:
               {
                  fn: function(response)
                  {
                     if (response.json.people)
                     {
                        var following = {};
                        var people = response.json.people;
                        for (var i=0; i<people.length; i++)
                        {
                           following[people[i].userName] = true;
                        }
                        me.following = following;
                     }
                     me.followingAllowed = true;
                  },
                  scope: this
               }
            });
         }
         else
         {
            Dom.setStyle(this.id + "-results", "height", "300px");
            Dom.removeClass(this.id + "-results", "hidden");
         }
         
         // Search button
         this.widgets.searchButton = Alfresco.util.createYUIButton(this, "search-button", this.onSearchClick);

         // DataSource definition  
         var peopleSearchUrl = Alfresco.constants.PROXY_URI + YAHOO.lang.substitute(this.options.dataWebScript, this.options);
         peopleSearchUrl += (peopleSearchUrl.indexOf("?") < 0) ? "?" : "&";
         this.widgets.dataSource = new YAHOO.util.DataSource(peopleSearchUrl,
         {
            responseType: YAHOO.util.DataSource.TYPE_JSON,
            connXhrMode: "queueRequests",
            responseSchema:
            {
                resultsList: "people"
            }
         });

         this.widgets.dataSource.doBeforeParseData = function PeopleFinder_doBeforeParseData(oRequest, oFullResponse)
         {
            var updatedResponse = oFullResponse;
            
            if (oFullResponse)
            {
               var items = oFullResponse.people, i, ii;

               // crop item list to max length if required
               if (items.length > me.options.maxSearchResults)
               {
                  items = items.slice(0, me.options.maxSearchResults-1);
               }

               // Remove the current user from the list?
               if (!me.options.showSelf)
               {
                  for (i = 0, ii = items.length; i < ii; i++)
                  {
                      if (items[i].userName == Alfresco.constants.USERNAME)
                      {
                         items.splice(i, 1);
                         break;
                      }
                  }
               }

               me.notAllowed = {};
               if (oFullResponse.notAllowed)
               {
                  me.notAllowed = Alfresco.util.arrayToObject(oFullResponse.notAllowed);
               }

               // we need to wrap the array inside a JSON object so the DataTable is happy
               updatedResponse =
               {
                  people: items
               };
            }
            
            return updatedResponse;
         };
         
         // Setup the DataTable
         this._setupDataTable();
         
         // register the "enter" event on the search text field
         var searchText = Dom.get(this.id + "-search-text");
         
         // declare variable to keep JSLint and YUI Compressor happy
         var enterListener = new YAHOO.util.KeyListener(searchText,
         {
            keys: YAHOO.util.KeyListener.KEY.ENTER
         },
         {
            fn: function(eventName, event, obj)
            {
               me.onSearchClick();
               Event.stopEvent(event[1]);
               return false;
            },
            scope: this,
            correctScope: true
         }, YAHOO.env.ua.ie > 0 ? YAHOO.util.KeyListener.KEYDOWN : "keypress");
         enterListener.enable();
         
         // Set initial focus?
         if (this.options.setFocus)
         {
            searchText.focus();
         }
      },
      
      /**
       * DataTable Cell Renderers
       *
       * Each cell has a custom renderer defined as a custom function. See YUI documentation for details.
       */
      
      /**
       * User avatar custom datacell formatter
       *
       * @method renderCellAvatar
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      fnRenderCellAvatar: function PeopleFinder_renderCellAvatar()
      {
         var me = this;
         
         return function PeopleFinder_renderCellAvatar(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
   
            var avatarUrl = Alfresco.constants.URL_RESCONTEXT + "components/images/no-user-photo-64.png";
            if (oRecord.getData("avatar") !== undefined)
            {
               avatarUrl = Alfresco.constants.PROXY_URI + oRecord.getData("avatar") + "?c=queue&ph=true";
            }
   
            elCell.innerHTML = '<img class="avatar" src="' + avatarUrl + '" alt="avatar" />';
         };
      },
      
      /**
       * Description/detail custom datacell formatter
       *
       * @method renderCellDescription
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      fnRenderCellDescription: function PeopleFinder_renderCellDescription()
      {
         var me = this;
         
         return function PeopleFinder_renderCellDescription(elCell, oRecord, oColumn, oData)
         {
            var userName = oRecord.getData("userName"),
               name = userName,
               firstName = oRecord.getData("firstName"),
               lastName = oRecord.getData("lastName"),
               userStatus = oRecord.getData("userStatus"),
               userStatusTime = oRecord.getData("userStatusTime");
            
            if ((firstName !== undefined) || (lastName !== undefined))
            {
               name = firstName ? firstName + " " : "";
               name += lastName ? lastName : "";
            }
            
            var title = oRecord.getData("jobtitle") || "",
               organization = oRecord.getData("organization") || "";
            
            var desc = '<h3 class="itemname">' + $userProfile(userName, name, 'class="theme-color-1" tabindex="0"') + ' <span class="lighter">(' + $html(userName) + ')</span></h3>';
            if (title.length !== 0)
            {
               if (me.options.viewMode == Alfresco.PeopleFinder.VIEW_MODE_COMPACT)
               {
                  desc += '<div class="detail">' + $html(title) + '</div>';
               }
               else
               {
                  desc += '<div class="detail"><span>' + me.msg("label.title") + ":</span> " + $html(title) + '</div>';
               }
            }
            if (organization.length !== 0)
            {
               if (me.options.viewMode == Alfresco.PeopleFinder.VIEW_MODE_COMPACT)
               {
                  desc += '<div class="detail">&nbsp;(' + $html(organization) + ')</div>';
               }
               else
               {
                  desc += '<div class="detail"><span>' + me.msg("label.company") + ":</span> " + $html(organization) + '</div>';
               }
            }
            if (userStatus !== null && userStatus.length > 0 && me.options.viewMode !== Alfresco.PeopleFinder.VIEW_MODE_COMPACT)
            {
               desc += '<div class="user-status">' + $html(userStatus) + ' <span>(' + Alfresco.util.relativeTime(Alfresco.util.fromISO8601(userStatusTime.iso8601)) + ')</span></div>';
            }
            elCell.innerHTML = desc;
         };
      },
      
      /**
       * Actions datacell formatter
       * 
       * @method renderCellActions
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      fnRenderCellActions: function PeopleFinder_renderCellActions()
      {
         var me = this;
         
         return function PeopleFinder_renderCellActions(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "text-align", "right");
            
            var userName = oRecord.getData("userName"),
               desc = '<span id="' + me.id + '-action-' + userName + '"></span>';
            elCell.innerHTML = desc;
            
            // This component is used as part of the People Search page and various People Picker components
            // so create the Add button if required - it is not displayed in the full people search list mode.
            if (me.options.viewMode !== Alfresco.PeopleFinder.VIEW_MODE_FULLPAGE)
            {
               var button = new YAHOO.widget.Button(
               {
                  type: "button",
                  label: (me.options.addButtonLabel ? me.options.addButtonLabel : me.msg("button.add")) + " " + me.options.addButtonSuffix,
                  name: me.id + "-selectbutton-" + userName,
                  container: me.id + '-action-' + userName,
                  tabindex: 0,
                  disabled: userName in me.notAllowed,
                  onclick:
                  {
                     fn: me.onPersonSelect,
                     obj: oRecord,
                     scope: me
                  }
               });
               me.userSelectButtons[userName] = button;
               
               if ((userName in me.selectedUsers) || (me.options.singleSelectMode && me.singleSelectedUser !== ""))
               {
                  me.userSelectButtons[userName].set("disabled", true);
               }
            }
            
            // Create the Follow/Unfollow buttons for the people
            if (me._renderFollowingActions(oRecord))
            {
               var following = me.following[userName];
               var button = new YAHOO.widget.Button(
               {
                  type: "button",
                  label: following ? me.msg("button.unfollow") : me.msg("button.follow"),
                  name: me.id + "-followbutton-" + userName,
                  container: me.id + '-action-' + userName,
                  tabindex: 0
               });
               button.set("onclick",
               {
                  fn: following ? me.onPersonUnfollow : me.onPersonFollow,
                  obj:
                  {
                     record: oRecord,
                     button: button
                  },
                  scope: me
               });
            }
         };
      },
      
      /**
       * Helper to retrun whether to render the Following actions for a record.
       * 
       * @return true to render the Following actions for the given datagrid record
       */
      _renderFollowingActions: function PeopleFinder__renderFollowingActions(oRecord)
      {
         return (this.followingAllowed &&
                 this.options.viewMode === Alfresco.PeopleFinder.VIEW_MODE_FULLPAGE &&
                 this.options.userId !== oRecord.getData("userName"));
      },
      
      /**
       * Setup the YUI DataTable with custom renderers.
       *
       * @method _setupDataTable
       * @private
       */
      _setupDataTable: function PeopleFinder__setupDataTable()
      {
         // DataTable column defintions
         var columnDefinitions =
         [
            { key: "avatar", label: "Avatar", sortable: false, formatter: this.fnRenderCellAvatar(), width: this.options.viewMode == Alfresco.PeopleFinder.VIEW_MODE_COMPACT ? 36 : 70 },
            { key: "person", label: "Description", sortable: false, formatter: this.fnRenderCellDescription() },
            { key: "actions", label: "Actions", sortable: false, formatter: this.fnRenderCellActions(), width: 120 }
         ];

         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-results", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: Alfresco.util.RENDERLOOPSIZE,
            initialLoad: false,
            MSG_EMPTY: this.msg("message.instructions")
         });

         this.widgets.dataTable.doBeforeLoadData = function PeopleFinder_doBeforeLoadData(sRequest, oResponse, oPayload)
         {
            if (oResponse.results)
            {
               this.renderLoopSize = Alfresco.util.RENDERLOOPSIZE;
            }
            return true;
         };

         // Enable row highlighting
         this.widgets.dataTable.subscribe("rowMouseoverEvent", this.widgets.dataTable.onEventHighlightRow);
         this.widgets.dataTable.subscribe("rowMouseoutEvent", this.widgets.dataTable.onEventUnhighlightRow);
      },
      
      /**
       * Public function to clear the results DataTable
       */
      clearResults: function PeopleFinder_clearResults()
      {
         // Clear results DataTable
         if (this.widgets.dataTable)
         {
            var recordCount = this.widgets.dataTable.getRecordSet().getLength();
            this.widgets.dataTable.deleteRows(0, recordCount);
         }
         Dom.get(this.id + "-search-text").value = "";
         this.singleSelectedUser = "";
         this.selectedUsers = {};
      },


      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * Select person button click handler
       *
       * @method onPersonSelect
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onPersonSelect: function PeopleFinder_onPersonSelect(event, p_obj)
      {
         var userName = p_obj.getData("userName");
         
         // Fire the personSelected bubble event
         YAHOO.Bubbling.fire("personSelected",
         {
            eventGroup: this,
            userName: userName,
            firstName: p_obj.getData("firstName"),
            lastName: p_obj.getData("lastName"),
            email: p_obj.getData("email")
         });
      },
      
      /**
       * Follow person button click handler
       *
       * @method onPersonFollow
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onPersonFollow: function PeopleFinder_onPersonFollow(event, p_obj)
      {
         var userName = p_obj.record.getData("userName");
         
         // follow the user - on success, update the button label and bind appropriate event handler
         
         // disable the button to prohibit multiple presses before we get a response
         p_obj.button.set("disabled", true);
         
         // execute call to follow the user
         var me = this;
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "api/subscriptions/" + encodeURIComponent(this.options.userId) + "/follow",
            method: Alfresco.util.Ajax.POST,
            dataObj: [userName],
            requestContentType: Alfresco.util.Ajax.JSON,
            successCallback:
            {
               fn: function(res)
               {
                  // on success, update the label and event handler
                  p_obj.button.set("label", me.msg("button.unfollow"));
                  p_obj.button.set("onclick",
                  {
                     fn: me.onPersonUnfollow,
                     obj:
                     {
                        record: p_obj.record,
                        button: p_obj.button
                     },
                     scope: me
                  });
                  // update our following cache
                  me.following[userName] = true;
                  // enable the button again
                  p_obj.button.set("disabled", false);
               },
               scope: this
            },
            failureCallback:
            {
               fn: function(res)
               {
                  var json = Alfresco.util.parseJSON(res.serverResponse.responseText);
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     title: this._msg("message.failure"),
                     text: json.message
                  });
                  // enable the button again
                  p_obj.button.set("disabled", false);
               },
               scope: this
            }
         });
      },
      
      /**
       * Unfollow person button click handler
       *
       * @method onPersonUnfollow
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onPersonUnfollow: function PeopleFinder_onPersonUnfollow(event, p_obj)
      {
         var userName = p_obj.record.getData("userName");
         
         // unfollow the user - on success, update the button label and bind appropriate event handler
         
         // disable the button to prohibit multiple presses before we get a response
         p_obj.button.set("disabled", true);
         
         // execute call to follow the user
         var me = this;
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "api/subscriptions/" + encodeURIComponent(this.options.userId) + "/unfollow",
            method: Alfresco.util.Ajax.POST,
            dataObj: [userName],
            requestContentType: Alfresco.util.Ajax.JSON,
            successCallback:
            {
               fn: function(res)
               {
                  // on success, update the label and event handler
                  p_obj.button.set("label", me.msg("button.follow"));
                  p_obj.button.set("onclick",
                  {
                     fn: me.onPersonFollow,
                     obj:
                     {
                        record: p_obj.record,
                        button: p_obj.button
                     },
                     scope: me
                  });
                  // update our following cache
                  me.following[userName] = false;
                  // enable the button again
                  p_obj.button.set("disabled", false);
               },
               scope: this
            },
            failureCallback:
            {
               fn: function(res)
               {
                  var json = Alfresco.util.parseJSON(res.serverResponse.responseText);
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     title: this._msg("message.failure"),
                     text: json.message
                  });
                  // enable the button again
                  p_obj.button.set("disabled", false);
               },
               scope: this
            }
         });
      },

      /**
       * Search button click event handler
       *
       * @method onSearchClick
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onSearchClick: function PeopleFinder_onSearchClick(e, p_obj)
      {
         var searchTermElem = Dom.get(this.id + "-search-text");
         var searchTerm = YAHOO.lang.trim(searchTermElem.value);
          
         // inform the user if the search term entered is too small
         if (searchTerm.replace(/\*/g, "").length < this.options.minSearchTermLength)
         {
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.minimum-length", this.options.minSearchTermLength)
            });
            return;
         }
         
         this.userSelectButtons = {};
         this._performSearch(searchTerm);
      },


      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR PAGE EVENTS
       * Disconnected event handlers for inter-component event notification
       */

      /**
       * Person Selected event handler
       *
       * @method onPersonSelected
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onPersonSelected: function PeopleFinder_onPersonSelected(layer, args)
      {
         var obj = args[1];
         // Should be person details in the arguments
         if (obj && (obj.userName !== undefined))
         {
            var userName = obj.userName;
            // Add the userName to the selectedUsers object
            this.selectedUsers[userName] = true;
            this.singleSelectedUser = userName;
         
            // Disable the add button(s)
            if (this.options.singleSelectMode)
            {
               for (var button in this.userSelectButtons)
               {
                  if (this.userSelectButtons.hasOwnProperty(button))
                  {
                     this.userSelectButtons[button].set("disabled", true);
                  }
               }
            }
            else
            {
               if (this.userSelectButtons[userName])
               {
                  this.userSelectButtons[userName].set("disabled", true);
               }
            }
         }
      },

      /**
       * Person Deselected event handler
       *
       * @method onPersonDeselected
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onPersonDeselected: function PeopleFinder_onPersonDeselected(layer, args)
      {
         var obj = args[1];
         // Should be person details in the arguments
         if (obj && (obj.userName !== undefined))
         {
            delete this.selectedUsers[obj.userName];
            this.singleSelectedUser = "";
            // Re-enable the add button(s)
            if (this.options.singleSelectMode)
            {
               for (var button in this.userSelectButtons)
               {
                  if (this.userSelectButtons.hasOwnProperty(button))
                  {
                     this.userSelectButtons[button].set("disabled", false);
                  }
               }
            }
            else
            {
               if (this.userSelectButtons[obj.userName])
               {
                  this.userSelectButtons[obj.userName].set("disabled", false);
               }
            }
         }
      },


      /**
       * PRIVATE FUNCTIONS
       */
      
      /**
       * Resets the YUI DataTable errors to our custom messages
       * NOTE: Scope could be YAHOO.widget.DataTable, so can't use "this"
       *
       * @method _setDefaultDataTableErrors
       * @param dataTable {object} Instance of the DataTable
       */
      _setDefaultDataTableErrors: function PeopleFinder__setDefaultDataTableErrors(dataTable)
      {
         var msg = Alfresco.util.message;
         dataTable.set("MSG_EMPTY", msg("message.empty", "Alfresco.PeopleFinder"));
         dataTable.set("MSG_ERROR", msg("message.error", "Alfresco.PeopleFinder"));
      },
      
      _displayResultInfo: function PeopleFinder__displayResultInfo(resultNumber, maxItems)
      {
         var infoElementId = this.id + "-results-info";
         var showResultInfo = function(infoElementId, newState)
         {
            if (newState)
            {
               Dom.removeClass(infoElementId, "hidden");
            }
            else
            {
               Dom.addClass(infoElementId, "hidden");
            }
            
         };
         
         var msg = Alfresco.util.message;
         if (resultNumber == 0)
         {
            // no results found
            document.getElementById(infoElementId).innerHTML = "";
            showResultInfo(infoElementId, false);
         }
         else if (resultNumber < maxItems)
         {
            // results found
            document.getElementById(infoElementId).innerHTML = msg("message.results", "Alfresco.PeopleFinder", this.searchTerm, resultNumber);
            showResultInfo(infoElementId, true);
         }
         else
         {
            // to many results
            document.getElementById(infoElementId).innerHTML = msg("message.maxresults", "Alfresco.PeopleFinder", maxItems);
            showResultInfo(infoElementId, true);
         }
      },
      
      /**
       * Updates people list by calling data webscript
       *
       * @method _performSearch
       * @param searchTerm {string} Search term from input field
       */
      _performSearch: function PeopleFinder__performSearch(searchTerm)
      {
         if (!this.isSearching)
         {
            this.isSearching = true;

            // Reset the custom error messages
            this._setDefaultDataTableErrors(this.widgets.dataTable);

            // Don't display any message
            this.widgets.dataTable.set("MSG_EMPTY", this.msg("message.searching"));

            // Empty results table
            this.widgets.dataTable.deleteRows(0, this.widgets.dataTable.getRecordSet().getLength());

            var successHandler = function PeopleFinder__pS_successHandler(sRequest, oResponse, oPayload)
            {
               if (this.options.viewMode != Alfresco.PeopleFinder.VIEW_MODE_COMPACT)
               {
                  if (Dom.hasClass(this.id + "-results", "hidden"))
                  {
                     Dom.removeClass(this.id + "-results", "hidden");
                     Dom.addClass(this.id + "-help", "hidden");
                  }
               }
               this._enableSearchUI();
               this._setDefaultDataTableErrors(this.widgets.dataTable);
               this._displayResultInfo(oResponse.results.length, this.options.maxSearchResults);
               this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
            };

            var failureHandler = function PeopleFinder__pS_failureHandler(sRequest, oResponse)
            {
               this._enableSearchUI();
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
                     this._setDefaultDataTableErrors(this.widgets.dataTable);
                  }
               }
            };

            this.searchTerm = searchTerm;
            this.widgets.dataSource.sendRequest(this._buildSearchParams(searchTerm),
            {
               success: successHandler,
               failure: failureHandler,
               scope: this
            });

            // Display a wait feedback message if the people hasn't been found yet
            this.widgets.searchButton.set("disabled", true);
            YAHOO.lang.later(2000, this, function(){
               if (this.isSearching)
               {
                  if (!this.widgets.feedbackMessage)
                  {
                     this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
                     {
                        text: Alfresco.util.message("message.searching", this.name),
                        spanClass: "wait",
                        displayTime: 0
                     });
                  }
                  else if (!this.widgets.feedbackMessage.cfg.getProperty("visible"))
                  {
                     this.widgets.feedbackMessage.show();
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
      _enableSearchUI: function PeopleFinder__enableSearchUI()
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
       * Build URI parameter string for People Finder JSON data webscript
       *
       * @method _buildSearchParams
       * @param searchTerm {string} Search terms to query
       */
      _buildSearchParams: function PeopleFinder__buildSearchParams(searchTerm)
      {
         return "sortBy=fullName&dir=asc&filter=" + encodeURIComponent(searchTerm) + "&maxResults=" + this.options.maxSearchResults;
      }
   });
})();