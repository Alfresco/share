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
 * AuthorityFinder component.
 * 
 * @namespace Alfresco
 * @class Alfresco.AuthorityFinder
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
    * AuthorityFinder constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.AuthorityFinder} The new AuthorityFinder instance
    * @constructor
    */
   Alfresco.AuthorityFinder = function(htmlId)
   {
      Alfresco.AuthorityFinder.superclass.constructor.call(this, "Alfresco.AuthorityFinder", htmlId, ["button", "container", "datasource", "datatable", "json"]);
      
      // Initialise prototype properties
      this.itemSelectButtons = {};
      this.searchTerm = "";
      this.singleSelectedItem = "";
      this.selectedItems = {};
      this.notAllowed = {};

      /**
       * Decoupled event listeners
       */
      YAHOO.Bubbling.on("itemSelected", this.onItemSelected, this);
      YAHOO.Bubbling.on("itemDeselected", this.onItemDeselected, this);

      return this;
   };

   /**
    * View Modes
    */
   YAHOO.lang.augmentObject(Alfresco.AuthorityFinder,
   {
      VIEW_MODE_DEFAULT: "",
      VIEW_MODE_COMPACT: "COMPACT",
      VIEW_MODE_FULLPAGE: "FULLPAGE"
   });

   /**
    * Type Modes
    */
   YAHOO.lang.augmentObject(Alfresco.AuthorityFinder,
   {
      AUTHORITY_TYPE_ALL: "all",
      AUTHORITY_TYPE_USERS: "user",
      AUTHORITY_TYPE_GROUPS: "group"
   });

   YAHOO.lang.extend(Alfresco.AuthorityFinder, Alfresco.component.Base,
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
          * @default Alfresco.AuthorityFinder.VIEW_MODE_DEFAULT
          */
         viewMode: Alfresco.AuthorityFinder.VIEW_MODE_DEFAULT,

         /**
          * Authority type
          * 
          * @property authorityType
          * @type string
          * @default Alfresco.AuthorityFinder.AUTHORITY_TYPE_ALL
          */
         authorityType: Alfresco.AuthorityFinder.AUTHORITY_TYPE_ALL,

         /**
          * Single Select mode flag
          * 
          * @property singleSelectMode
          * @type boolean
          * @default false
          */
         singleSelectMode: false,
         
         /**
          * Number of characters required for a search.
          * 
          * @property minSearchTermLength
          * @type number
          * @default 3
          */
         minSearchTermLength: 3,
         
         /**
          * Maximum number of items to display in the results list
          * 
          * @property maxSearchResults
          * @type number
          * @default 100
          */
         maxSearchResults: 100,
         
         /**
          * If true, then automatically prefix a wildcard character to the search term
          * 
          * @property wildcardPrefix
          * @type boolean
          * @default false
          */
         wildcardPrefix: false,

         /**
          * Whether to set UI focus to this component or not
          * 
          * @property setFocus
          * @type boolean
          * @default false
          */
         setFocus: false,

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
         dataWebScript: Alfresco.constants.URL_SERVICECONTEXT + "components/people-finder/authority-query"
      },

      /**
       * Object container for storing YUI button instances
       * 
       * @property itemSelectButtons
       * @type object
       */
      itemSelectButtons: null,
      
      /**
       * Current search term, obtained from form input field.
       * 
       * @property searchTerm
       * @type string
       */
      searchTerm: null,
      
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
       * Authorities for whom the action is not allowed
       * 
       * @property notAllowed
       * @type array
       */
      notAllowed: null,

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function AuthorityFinder_onReady()
      {  
         var me = this;
         
         // View mode
         if (this.options.viewMode == Alfresco.AuthorityFinder.VIEW_MODE_COMPACT)
         {
            Dom.addClass(this.id + "-body", "compact");
         }
         else if (this.options.viewMode == Alfresco.AuthorityFinder.VIEW_MODE_FULLPAGE)
         {
            Dom.setStyle(this.id + "-results", "height", "auto");
         }
         else
         {
            Dom.setStyle(this.id + "-results", "height", "300px");
         }
         
         // Search button
         this.widgets.searchButton = Alfresco.util.createYUIButton(this, "authority-search-button", this.onSearchClick);

         // DataSource definition  
         var searchUrl = YAHOO.lang.substitute(this.options.dataWebScript, this.options);
         searchUrl += (searchUrl.indexOf("?") < 0) ? "?" : "&";
         searchUrl += "authorityType=" + this.options.authorityType + "&";
         searchUrl += "maxResults=" + this.options.maxSearchResults + "&";
         if (this.options.siteScope)
         {
            searchUrl += "site=" + this.options.siteScope + "&";
         }
         this.widgets.dataSource = new YAHOO.util.DataSource(searchUrl,
         {
            responseType: YAHOO.util.DataSource.TYPE_JSON,
            connXhrMode: "queueRequests",
            responseSchema:
            {
                resultsList: "authorities"
            }
         });

         this.widgets.dataSource.doBeforeParseData = function AuthorityFinder_doBeforeParseData(oRequest, oFullResponse)
         {
            var updatedResponse = oFullResponse;
            
            if (oFullResponse && oFullResponse.authorities)
            {
               var items = oFullResponse.authorities;
               
               // crop item list to max length if required
               if (items.length > me.options.maxSearchResults)
               {
                  items = items.slice(0, me.options.maxSearchResults - 1);
               }

               me.notAllowed = {};
               if (oFullResponse.notAllowed)
               {
                  me.notAllowed = Alfresco.util.arrayToObject(oFullResponse.notAllowed);
               }

               // we need to wrap the array inside a JSON object so the DataTable is happy
               updatedResponse =
               {
                  authorities: items
               };
            }
            
            return updatedResponse;
         };
         
         // Setup the DataTable
         this._setupDataTable();

         // Update the title based on the mode
         Dom.get(this.id + "-title").innerHTML = this.msg("title." + this.options.authorityType);
         
         // Register the "enter" event on the search text field
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
       * Setup the YUI DataTable with custom renderers.
       *
       * @method _setupDataTable
       * @private
       */
      _setupDataTable: function AuthorityFinder__setupDataTable()
      {
         /**
          * DataTable Cell Renderers
          *
          * Each cell has a custom renderer defined as a custom function. See YUI documentation for details.
          * These MUST be inline in order to have access to the Alfresco.AuthorityFinder class (via the "me" variable).
          */
         var me = this;
          
         /**
          * Icon custom datacell formatter
          *
          * @method renderCellIcon
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellIcon = function AuthorityFinder_renderCellIcon(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            
            var authType = oRecord.getData("authorityType"),
               metadata = oRecord.getData("metadata") || {},
               avatarUrl = Alfresco.constants.URL_RESCONTEXT + "components/images/" + (authType == "USER" ? "no-user-photo-64.png" : "group-64.png");

            if (metadata.avatar && metadata.avatar.length !== 0)
            {
               avatarUrl = Alfresco.constants.PROXY_URI + metadata.avatar + "?c=queue&ph=true";
            }
            
            // Store calculated URL to icon
            oRecord.setData("calc_iconUrl", avatarUrl);

            elCell.innerHTML = '<img class="avatar" src="' + avatarUrl + '" alt="avatar" />';
         };

         /**
          * Description/detail custom datacell formatter - compact mode
          *
          * @method renderCellDescriptionCompact
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellDescriptionCompact = function AuthorityFinder_renderCellDescriptionCompact(elCell, oRecord, oColumn, oData)
         {
            var authType = oRecord.getData("authorityType"),
               metadata = oRecord.getData("metadata"),
               desc = '';
            
            if (authType == "USER")
            {
               var userName = oRecord.getData("shortName"),
                  jobTitle = metadata.jobTitle || "",
                  organization = metadata.organization || "";
               
               desc = '<h3 class="itemname">' + $userProfile(userName, oRecord.getData("displayName"), 'class="theme-color-1"') + ' <span class="lighter">(' + $html(userName) + ')</span></h3>';
               if (jobTitle.length > 0)
               {
                  desc += '<div class="detail">' + $html(jobTitle) + '</div>';
               }
               if (organization.length > 0)
               {
                  desc += '<div class="detail">&nbsp;(' + $html(organization) + ')</div>';
               }
            }
            else if (authType == "GROUP")
            {
               desc = '<h3 class="itemname">' + $html(oRecord.getData("displayName")) + ' <span class="lighter">(' + $html(oRecord.getData("fullName")) + ')</span></h3>';
            }
            elCell.innerHTML = desc;
         };

         /**
          * Description/detail custom datacell formatter
          *
          * @method renderCellDescription
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellDescription = function AuthorityFinder_renderCellDescription(elCell, oRecord, oColumn, oData)
         {
            var authType = oRecord.getData("authorityType"),
               metadata = oRecord.getData("metadata"),
               desc = '';
            
            if (authType == "USER")
            {
               var userName = oRecord.getData("shortName"),
                  jobTitle = metadata.jobTitle || "",
                  organization = metadata.organization || "";
               
               desc = '<h3 class="itemname">' + $userProfile(userName, oRecord.getData("displayName"), 'class="theme-color-1"') + ' <span class="lighter">(' + $html(userName) + ')</span></h3>';
               if (jobTitle.length > 0)
               {
                  desc += '<div class="detail"><span>' + me.msg("label.title") + ":</span> " + $html(jobTitle) + '</div>';
               }
               if (organization.length > 0)
               {
                  desc += '<div class="detail"><span>' + me.msg("label.company") + ":</span> " + $html(organization) + '</div>';
               }
            }
            else if (authType == "GROUP")
            {
               desc = '<h3 class="itemname">' + $html(oRecord.getData("displayName")) + '</h3>';
               desc += '<div class="detail"><span>' + me.msg("label.name") + ":</span> " + $html(oRecord.getData("fullName")) + '</div>';
            }
            elCell.innerHTML = desc;
         };
         
         /**
          * Add button datacell formatter
          *
          * @method renderCellIcon
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellAddButton = function AuthorityFinder_renderCellAddButton(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "text-align", "right");

            var domId = Alfresco.util.generateDomId(),
               desc = '<span id="' + domId + '"></span>',
               itemName = oRecord.getData("fullName");
            
            elCell.innerHTML = desc;
            
            // create button if require - it is not required in the fullpage view mode
            if (me.options.viewMode !== Alfresco.AuthorityFinder.VIEW_MODE_FULLPAGE)
            {
               var button = new YAHOO.widget.Button(
               {
                  type: "button",
                  label: me.msg("button.add") + " " + me.options.addButtonSuffix,
                  name: domId + "-name",
                  container: domId,
                  disabled: itemName in me.notAllowed,
                  onclick:
                  {
                     fn: me.onItemSelect,
                     obj: oRecord,
                     scope: me
                  }
               });
               me.itemSelectButtons[itemName] = button;
               
               if ((itemName in me.selectedItems) || (me.options.singleSelectMode && me.singleSelectedItem !== ""))
               {
                  me.itemSelectButtons[itemName].set("disabled", true);
               }
            }
         };

         // DataTable column defintions
         var isCompact = this.options.viewMode == Alfresco.AuthorityFinder.VIEW_MODE_COMPACT,
            columnDefinitions =
            [
               { key: "authorityType", label: "Icon", sortable: false, formatter: renderCellIcon, width: (isCompact ? 36 : 70) },
               { key: "fullName", label: "Description", sortable: false, formatter: (isCompact ? renderCellDescriptionCompact : renderCellDescription) },
               { key: "actions", label: "Actions", sortable: false, formatter: renderCellAddButton, width: 80 }
            ];

         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-results", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: 32,
            initialLoad: false,
            MSG_EMPTY: this.msg("message.instructions")
         });

         this.widgets.dataTable.doBeforeLoadData = function AuthorityFinder_doBeforeLoadData(sRequest, oResponse, oPayload)
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
      clearResults: function AuthorityFinder_clearResults()
      {
         // Clear results DataTable
         if (this.widgets.dataTable)
         {
            var recordCount = this.widgets.dataTable.getRecordSet().getLength();
            this.widgets.dataTable.deleteRows(0, recordCount);
         }
         Dom.get(this.id + "-search-text").value = "";
         this.singleSelectedItem = "";
         this.selectedItems = {};
      },


      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * Select person button click handler
       *
       * @method onItemSelect
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onItemSelect: function AuthorityFinder_onItemSelect(event, p_obj)
      {
         // Fire the personSelected bubble event
         YAHOO.Bubbling.fire("itemSelected",
         {
            itemName: p_obj.getData("fullName"),
            shortName: p_obj.getData("shortName"),
            displayName: p_obj.getData("displayName"),
            iconUrl: p_obj.getData("calc_iconUrl"),
            eventGroup: this
         });
      },

      /**
       * Search button click event handler
       *
       * @method onSearchClick
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onSearchClick: function AuthorityFinder_onSearchClick(e, p_obj)
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
         
         this.itemSelectButtons = {};
         this._performSearch(searchTerm);
      },


      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR PAGE EVENTS
       * Disconnected event handlers for inter-component event notification
       */

      /**
       * Item Selected event handler
       *
       * @method onItemSelected
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onItemSelected: function AuthorityFinder_onItemSelected(layer, args)
      {
         var obj = args[1];
         // Should be person details in the arguments
         if (obj && (obj.itemName !== null))
         {
            var itemName = obj.itemName;
            // Add the itemName to the selectedItems object
            this.selectedItems[itemName] = true;
            this.singleSelectedItem = itemName;

            // Disable the add button(s)
            if (this.options.singleSelectMode)
            {
               for (var button in this.itemSelectButtons)
               {
                  if (this.itemSelectButtons.hasOwnProperty(button))
                  {
                     this.itemSelectButtons[button].set("disabled", true);
                  }
               }
            }
            else
            {
               this.itemSelectButtons[itemName].set("disabled", true);
            }
         }
      },

      /**
       * Item Deselected event handler
       *
       * @method onItemDeselected
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onItemDeselected: function AuthorityFinder_onItemDeselected(layer, args)
      {
         var obj = args[1];
         // Should be item details in the arguments
         if (obj && (obj.itemName !== null))
         {
            delete this.selectedItems[obj.itemName];
            this.singleSelectedItem = "";
            // Re-enable the add button(s)
            if (this.options.singleSelectMode)
            {
               for (var button in this.itemSelectButtons)
               {
                  if (this.itemSelectButtons.hasOwnProperty(button))
                  {
                     this.itemSelectButtons[button].set("disabled", false);
                  }
               }
            }
            else
            {
               this.itemSelectButtons[obj.itemName].set("disabled", false);
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
      _setDefaultDataTableErrors: function AuthorityFinder__setDefaultDataTableErrors(dataTable)
      {
         var msg = Alfresco.util.message;
         dataTable.set("MSG_EMPTY", msg("message.empty", "Alfresco.AuthorityFinder"));
         dataTable.set("MSG_ERROR", msg("message.error", "Alfresco.AuthorityFinder"));
      },
      
      /**
       * Updates results list by calling data webscript
       *
       * @method _performSearch
       * @param searchTerm {string} Search term from input field
       */
      _performSearch: function AuthorityFinder__performSearch(searchTerm)
      {
         // Reset the custom error messages
         this._setDefaultDataTableErrors(this.widgets.dataTable);
         
         // Don't display any message
         this.widgets.dataTable.set("MSG_EMPTY", this.msg("message.searching"));
         
         // Empty results table
         this.widgets.dataTable.deleteRows(0, this.widgets.dataTable.getRecordSet().getLength());
         this.widgets.dataTable.render();
         
         var successHandler = function AuthorityFinder__pS_successHandler(sRequest, oResponse, oPayload)
         {
            this._setDefaultDataTableErrors(this.widgets.dataTable);
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
         };
         
         var failureHandler = function AuthorityFinder__pS_failureHandler(sRequest, oResponse)
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
                  this.widgets.dataTable.showTableMessage(Alfresco.util.encodeHTML(response.message), YAHOO.widget.DataTable.CLASS_ERROR);
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
      },

      /**
       * Build URI parameter string for Group Finder JSON data webscript
       *
       * @method _buildSearchParams
       * @param searchTerm {string} Search terms to query
       */
      _buildSearchParams: function AuthorityFinder__buildSearchParams(searchTerm)
      {
         return "filter=" + (this.options.wildcardPrefix ? "*" : "") + encodeURIComponent(searchTerm) + (this.options.siteId ? "&defGroupsFor=" + encodeURIComponent(this.options.siteId) : "");
      }
   });
})();