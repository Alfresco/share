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
 * Site Groups Members component.
 * 
 * @namespace Alfresco
 * @class Alfresco.SiteGroups
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
    * SiteGroups constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.SiteGroups} The new SiteGroups instance
    * @constructor
    */
   Alfresco.SiteGroups = function(htmlId)
   {
      this.name = "Alfresco.SiteGroups";
      this.id = htmlId;
      
      // initialise prototype properties
      this.widgets = {};
      this.listWidgets = {};
      this.buttons = [];
      this.modules = {};
      this.isCurrentUserSiteAdmin = false;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datasource", "datatable", "json"], this.onComponentsLoaded, this);
   
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("deactivateAllControls", this.onDeactivateAllControls, this);

      return this;
   };
   
   Alfresco.SiteGroups.prototype =
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
          * Number of characters required for a search.
          * 
          * @property minSearchTermLength
          * @type int
          * @default 0
          */
         minSearchTermLength: 0,
         
         /**
          * Maximum number of search results displayed.
          * 
          * @property maxSearchResults
          * @type int
          * @default 100
          */
         maxSearchResults: 100,
         
         /**
          * The userid of the current user
          * 
          * @property currentUser
          * @type string
          */
         currentUser: "",
         
         /**
          * The role of the current user in the current site
          * 
          * @property currentUserRole
          * @type string
          */
         currentUserRole: "",
         
         /**
          * Holds the list of roles available in the site
          */
         roles: [],

         /**
          * Set to an error string is an error occurred
          */
         error: null,

         /**
          * Whether to set UI focus to this component or not
          * 
          * @property setFocus
          * @type boolean
          * @default false
          */
         setFocus: false
      },

      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: null,
      
      /**
       * Object container for storing YUI widget instances.
       * 
       * @property listWidgets
       * @type object
       */
      listWidgets: null,
 
      /**
       * List of uninvite buttons
       * 
       * @property buttons
       * @type array
       */
      buttons: null,

      /**
       * Object container for storing module instances.
       * 
       * @property modules
       * @type object
       */
      modules: null,

      /**
       * Flag to determine whether the current user is a site administrator
       * 
       * @property isCurrentUserSiteAdmin
       * @type boolean
       */
      isCurrentUserSiteAdmin: null,

      /**
       * Keeps track if this component is searching or not
       *
       * @property isSearching
       * @type Boolean
       */
      isSearching: false,

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.Search} returns 'this' for method chaining
       */
      setOptions: function SiteGroups_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.Search} returns 'this' for method chaining
       */
      setMessages: function SiteGroups_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },
      
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function SiteGroups_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function SiteGroups_onReady()
      {  
         var me = this;
         
         // DataSource definition
         var uriSearchResults = Alfresco.constants.PROXY_URI + "api/sites/" + me.options.siteId + "/memberships?";
         this.widgets.dataSource = new YAHOO.util.DataSource(uriSearchResults,
         {
            responseType: YAHOO.util.DataSource.TYPE_JSON,
            connXhrMode: "queueRequests",
            responseSchema:
            {
                resultsList: "items"
            }
         });
         this.widgets.dataSource.doBeforeParseData = function SiteGroups_doBeforeParseData(oRequest , oFullResponse)
         {
            var updatedResponse = oFullResponse;
               
            if (oFullResponse)
            {
               var items = [];
               
               // create a data format that the DataTable can use
               for (var x = 0, xx = oFullResponse.length; x < xx; x++)
               {
                  var memberData = oFullResponse[x];
                  
                  // create object to represent member
                  var member =
                  {
                     "displayName": memberData.authority.displayName,
                     "fullName": memberData.authority.fullName,
                     "role": memberData.role
                  };
                  
                  // add member to list
                  items.push(member);
               }
               
               // Sort the memeber list by name
               items.sort(function (membership1, membership2)
               {
                  var name1 = membership1.displayName,
                     name2 = membership2.displayName;
                  return (name1 > name2) ? 1 : (name1 < name2) ? -1 : 0;
               });
               
               // we need to wrap the array inside a JSON object so the DataTable is happy
               updatedResponse =
               {
                  "items": items
               };
            }
            
            return updatedResponse;
         };
         
         // determine if current user is a site administrator
         if (me.options.currentUserRole !== undefined &&
             me.options.currentUserRole === "SiteManager")
         {
            this.isCurrentUserSiteAdmin = true;
         }
         
         // setup of the datatable.
         this._setupDataTable();
         
         // setup the buttons
         this.widgets.searchButton = Alfresco.util.createYUIButton(this, "button", this.doSearch);
         this.widgets.addGroups = Alfresco.util.createYUIButton(this, "addGroups", null, 
         {
            type: "link"
         });
         
         // register the "enter" event on the search text field
         var searchInput = Dom.get(this.id + "-term"),
            enterListener = new YAHOO.util.KeyListener(searchInput,
         {
            keys:13
         },
         {
            fn: function() 
            {
               me.doSearch();
            },
            scope: this,
            correctScope: true
         }, "keydown");
         enterListener.enable();
         
         if (this.options.error)
         {
            enterListener.disable();
            this.widgets.dataTable.set("MSG_ERROR", this.options.error);
            this.widgets.dataTable.showTableMessage(this.options.error, YAHOO.widget.DataTable.CLASS_ERROR);
            // Deactivate controls
            YAHOO.Bubbling.fire("deactivateAllControls");
         }

         // Set initial focus?
         if (this.options.setFocus)
         {
            searchInput.focus();
         }
         
         // This is a quick solution for showing all members of the site. The default search string 
         // is empty and equates to a global search.
         if (window.location.hash === "#showall" || this.options.minSearchTermLength === 0)
         {
            this.doSearch();
         }
         
         // Finally show the component body here to prevent UI artifacts on YUI button decoration
         Dom.setStyle(this.id + "-body", "visibility", "visible");
      },

      _setupDataTable: function SiteGroups_setupDataTable()
      {
         /**
          * DataTable Cell Renderers
          *
          * Each cell has a custom renderer defined as a custom function. See YUI documentation for details.
          * These MUST be inline in order to have access to the Alfresco.SiteGroups class (via the "me" variable).
          */
         var me = this;
          
         /**
          * Group icon custom datacell formatter
          *
          * @method renderCellIcon
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellIcon = function SiteGroups_renderCellIcon(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            elCell.innerHTML = '<img src="' + Alfresco.constants.URL_RESCONTEXT + "components/images/group-64.png" + '" alt="group" />';
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
         var renderCellDescription = function SiteGroups_renderCellDescription(elCell, oRecord, oColumn, oData)
         {
            // Currently rendering all results the same way
            var displayName = oRecord.getData("displayName"),
               fullName = oRecord.getData("fullName");

            var desc = '<h3>' + $html(displayName) + '</h3>';
            desc += '<div><span class="attr-name">' + me._msg('label.name') + ': </span>&nbsp;<span class="attr-value">' + $html(fullName) + '</span></div>';
            
            elCell.innerHTML = desc;
         };
         
         /**
          * Role select custom datacell formatter
          *
          * @method renderCellRoleSelect
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellRoleSelect = function SiteGroups_renderCellRoleSelect(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "text-align", "right");
            Dom.addClass(elCell, "overflow");
            
            var currentRole = oRecord.getData("role");
            
            if (me.isCurrentUserSiteAdmin)
            {
               // create HTML for representing buttons
               var groupName = oRecord.getData("fullName");
               elCell.innerHTML = '<span id="' + me.id + '-roleSelector-' + groupName + '"></span>';
               
               // create the roles menu
               var rolesMenu = [],
                  role;
               
               for (var x = 0, xx = me.options.roles.length; x < xx; x++)
               {
                  role = me.options.roles[x];
                  rolesMenu.push(
                  {
                     text: me._msg("role." + role),
                     value: role,
                     onclick:
                     {
                        fn: me.onRoleSelect,
                        obj:
                        {
                           group: groupName,
                           currentRole: currentRole,
                           newRole: role,
                           recordId: oRecord.getId()
                        },
                        scope: me
                     }
                  }
                  );
               }
               
               // create the role selector button
               var roleSelector = new YAHOO.widget.Button(
               {
                  container: me.id + '-roleSelector-' + groupName,
                  type: "menu",
                  label: me._msg("role." + currentRole) + " " + Alfresco.constants.MENU_ARROW_SYMBOL,
                  menu: rolesMenu
               });
               
               // store a reference to the role selector button
               me.listWidgets[groupName] =
               {
                  roleSelector: roleSelector
               };
               
               // store the buttons
               me.buttons[groupName + "-roleSelector"] =
               {
                  roleSelector: roleSelector
               };
            }
            else
            {
               // output padding div so layout is not messed up due to missing buttons
               elCell.innerHTML = '<div>' + me._msg("role." + currentRole) + '</div>';
            }
         };
         
         /**
          * Uninvite button custom datacell formatter
          *
          * @method renderCellUninvite
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellUninvite = function SiteGroups_renderCellUninvite(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            if (me.isCurrentUserSiteAdmin)
            {
               // create HTML for representing buttons
               var groupName = oRecord.getData("fullName");
               elCell.innerHTML = '<span id="' + me.id + '-button-' + groupName + '"></span>';
               
               // create the uninvite button
               var button = new YAHOO.widget.Button(
               {
                   container: me.id + '-button-' + groupName,
                   label: me._msg("site-groups.remove"),
                   onclick:
                   {
                      fn: me.doRemove,
                      obj: oRecord.getData(),
                      scope: me
                     }
               });
               
               // store the buttons
               me.buttons[groupName + "-button"] =
               {
                  button: button
               };
            }
            else
            {
               // output padding div so layout is not messed up due to missing buttons
               elCell.innerHTML = '<div></div>';
            }
         };

         // DataTable column defintions
         var columnDefinitions = [
         {
            key: "displayName", label: "Group Name", sortable: false, formatter: renderCellIcon, width: 64
         },
         {
            key: "fullName", label: "Details", sortable: false, formatter: renderCellDescription
         },
         {
            key: "role", label: "Select Role", formatter: renderCellRoleSelect, width: 140
         },
         {
            key: "uninvite", label: "Uninvite", formatter: renderCellUninvite, width: 80
         }
         ];

         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-groups", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: 32,
            initialLoad: false,
            MSG_EMPTY: '<span style="white-space: nowrap;">' + this._msg("site-groups.enter-search-term") + '</span>'
         });

         // Override abstract function within DataTable to set custom error message
         this.widgets.dataTable.doBeforeLoadData = function SiteGroups_doBeforeLoadData(sRequest, oResponse, oPayload)
         {
            if (oResponse.error)
            {
               try
               {
                  var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                  me.widgets.dataTable.set("MSG_ERROR", response.message);
               }
               catch(e)
               {
                  me._setDefaultDataTableErrors(me.widgets.dataTable);
               }
            }
            else if (oResponse.results)
            {
               if (oResponse.results.length === 0)
               {
                  me.widgets.dataTable.set("MSG_EMPTY", '<span style="white-space: nowrap;">' + me._msg("message.empty") + '</span>');
               }
               me.renderLoopSize = Alfresco.util.RENDERLOOPSIZE;
            }
            
            // Must return true to have the "Searching..." message replaced by the error message
            return true;
         };
      },
      
      /**
       * Search event handler
       *
       * @method doSearch
       */
      doSearch: function SiteGroups_doSearch()
      {
         var searchTerm = YAHOO.lang.trim(Dom.get(this.id + "-term").value);
         if (searchTerm.length < this.options.minSearchTermLength)
         {
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this._msg("message.minimum-length", this.options.minSearchTermLength)
            });
            return;
         }

         this._performSearch(searchTerm);
      },
      
      /**
       * Remove group event handler
       * 
       * @method doRemove
       * @param event {object} The event object
       * @param group {object} Object literal containing group details
       */
      doRemove: function SiteGroups_doRemove(event, group)
      {
         // show a wait message
         this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: this._msg("message.removing"),
            spanClass: "wait",
            displayTime: 0
         });
         
         // request success handler
         var success = function SiteGroups_doRemove_success(response, group)
         {
            // hide the wait message
            this.widgets.feedbackMessage.destroy();
             
            // show popup message to confirm
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this._msg("site-groups.remove-success", group.displayName)
            });
         
            // remove the entry
            var recordIndex = this.widgets.dataTable.getRecordIndex(event.target.id);
            this.widgets.dataTable.deleteRow(recordIndex);
         };
         
         // request failure handler
         var failure = function SiteGroups_doRemove_failure(response)
         {
            // remove the wait message
            this.widgets.feedbackMessage.destroy();
         };
          
         // make ajax call to site service to remove group
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "api/sites/" + this.options.siteId + "/memberships/" + encodeURIComponent(group.fullName),
            method: "DELETE",
            successCallback:
            {
               fn: success,
               obj: group,
               scope: this
            },
            failureMessage: this._msg("site-groups.remove-failure", group.displayName),
            failureCallback:
            {
               fn: failure,
               scope: this
            }
         });
      },
      
      /**
       * Called when the user selects a role in the role dropdown
       * 
       * @parma p_obj: object containing record and role to set
       */
      onRoleSelect: function SiteGroups_onRoleSelect(type, event, args)
      {
         // fetch the current and new roles to see whether we have to change the role
         var record = this.widgets.dataTable.getRecord(args.recordId),
            data = record.getData(),
            recordIndex = this.widgets.dataTable.getRecordIndex(record),
            currentRole = data.role,
            selectedRole = args.newRole,
            group = args.group;
         
         if (selectedRole !== currentRole)
         {
            // show a wait message
            this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
            {
               text: this._msg("message.changingrole"),
               spanClass: "wait",
               displayTime: 0
            });

            // request success handler
            var success = function SiteGroups_onRoleSelect_success(response, groupRole)
            {
               // hide the wait message
               this.widgets.feedbackMessage.destroy();

               // show popup message to confirm
               Alfresco.util.PopupManager.displayMessage(
               {
                  text: this._msg("site-groups.change-role-success", groupRole.groupDisplayName, this._msg("role." + groupRole.role))
               });

               // update the data and table
               var data = this.widgets.dataTable.getRecord(groupRole.recordIndex).getData();
               data.role = args.newRole;
               this.widgets.dataTable.updateRow(groupRole.recordIndex, data);
            };

            // request failure handler
            var failure = function SiteGroups_onRoleSelect_failure(response)
            {
               // remove the message
               this.widgets.feedbackMessage.destroy();
            };

            // make ajax call to site service to change role
            Alfresco.util.Ajax.jsonPut(
            {
               url: Alfresco.constants.PROXY_URI + "api/sites/" + this.options.siteId + "/memberships",
               dataObj:
               {
                  role: selectedRole,
                  group:
                  {
                     fullName: group
                  }
               },
               successCallback:
               {
                  fn: success,
                  obj:
                  {
                     groupDisplayName: data.displayName,
                     role: selectedRole,
                     recordIndex: recordIndex
                  },
                  scope: this
               },
               failureMessage: this._msg("site-groups.change-role-failure", group),
               failureCallback:
               {
                  fn: failure,
                  scope: this
               }
            });
         }
      },

      /**
       * Deactivate All Controls event handler
       *
       * @method onDeactivateAllControls
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDeactivateAllControls: function SiteGroups_onDeactivateAllControls(layer, args)
      {
         var index, widget, fnDisable = Alfresco.util.disableYUIButton;
         for (index in this.widgets)
         {
            if (this.widgets.hasOwnProperty(index))
            {
               fnDisable(this.widgets[index]);
            }
         }
      },

      
      /**
       * Resets the YUI DataTable errors to our custom messages
       * NOTE: Scope could be YAHOO.widget.DataTable, so can't use "this"
       *
       * @method _setDefaultDataTableErrors
       * @param dataTable {object} Instance of the DataTable
       */
      _setDefaultDataTableErrors: function SiteGroups__setDefaultDataTableErrors(dataTable)
      {
         var msg = Alfresco.util.message;
         dataTable.set("MSG_EMPTY", msg("message.empty", "Alfresco.SiteGroups"));
         dataTable.set("MSG_ERROR", msg("message.error", "Alfresco.SiteGroups"));
      },
      
      /**
       * Updates members list by calling data webscript with current search term
       *
       * @method _performSearch
       * @param searchTerm {string} The term to search for
       */
      _performSearch: function SiteGroups__performSearch(searchTerm)
      {
         if (!this.isSearching)
         {
            this.isSearching = true;

            // Reset the custom error messages
            this._setDefaultDataTableErrors(this.widgets.dataTable);

            // Display loading message
            this.widgets.dataTable.set("MSG_EMPTY", this._msg("site-groups.searching"));

            // empty results table
            this.widgets.dataTable.deleteRows(0, this.widgets.dataTable.getRecordSet().getLength());

            function successHandler(sRequest, oResponse, oPayload)
            {
               this._enableSearchUI();
               this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
               this._setDefaultDataTableErrors(this.widgets.dataTable);
            }

            function failureHandler(sRequest, oResponse)
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
                     this.widgets.dataTable.showTableMessage(Alfresco.util.encodeHTML(response.message), YAHOO.widget.DataTable.CLASS_ERROR);
                  }
                  catch(e)
                  {
                     this._setDefaultDataTableErrors(this.widgets.dataTable);
                  }
               }
            }

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
      _enableSearchUI: function SiteGroups__enableSearchUI()
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
       * Build URI parameter string for finding site members
       *
       * @method _buildSearchParams
       * @param searchTerm {string} Path to query
       */
      _buildSearchParams: function SiteGroups__buildSearchParams(searchTerm)
      {
         var params = YAHOO.lang.substitute("size={maxResults}&nf={term}&authorityType=GROUP",
         {
            maxResults: this.options.maxSearchResults,
            term: encodeURIComponent(searchTerm)
         });
         
         return params;
      },
      
      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function SiteGroups__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.SiteGroups", Array.prototype.slice.call(arguments).slice(1));
      }
   };
})();
