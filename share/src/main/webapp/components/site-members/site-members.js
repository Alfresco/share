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
 * Site Members component.
 * 
 * @namespace Alfresco
 * @class Alfresco.SiteMembers
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
    * SiteMembers constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.SiteMembers} The new SiteMembers instance
    * @constructor
    */
   Alfresco.SiteMembers = function(htmlId)
   {
      this.name = "Alfresco.SiteMembers";
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

   YAHOO.extend(Alfresco.SiteMembers, Alfresco.component.Base,
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
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function SiteMembers_onReady()
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
         this.widgets.dataSource.doBeforeParseData = function SiteMembers_doBeforeParseData(oRequest , oFullResponse)
         {
            var updatedResponse = oFullResponse;
               
            if (oFullResponse)
            {
               var items = [],
                  memberData, member;
               
               // create a data format that the DataTable can use
               for (var x = 0, xx = oFullResponse.length; x < xx; x++)
               {
                  memberData = oFullResponse[x];
                  
                  // create object to represent member
                  member = memberData.authority;
                  member.role = memberData.role;
                  member.isMemberOfGroup = memberData.isMemberOfGroup;
                  
                  // add member to list
                  items.push(member);
               }
               
               // Sort the memeber list by name
               items.sort(function (membership1, membership2)
               {
                  var name1 = membership1.firstName + membership1.lastName,
                     name2 = membership2.firstName + membership2.lastName;
                  
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
         this.widgets.searchButton = Alfresco.util.createYUIButton(this, "button", this.onSearch);
         if (Dom.get(this.id + "-invitePeople"))
         {
            this.widgets.invitePeople = Alfresco.util.createYUIButton(this, "invitePeople", null,
            {
               type: "link"
            });
         }

         // register the "enter" event on the search text field
         var searchInput = Dom.get(this.id + "-term"),
            enterListener = new YAHOO.util.KeyListener(searchInput,
         {
            keys:13
         },
         {
            fn: function() 
            {
               me.onSearch();
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
            this.onSearch();
         }
         
         // Finally show the component body here to prevent UI artifacts on YUI button decoration
         Dom.setStyle(this.id + "-body", "visibility", "visible");
      },

      _setupDataTable: function SiteMembers_setupDataTable()
      {
         /**
          * DataTable Cell Renderers
          *
          * Each cell has a custom renderer defined as a custom function. See YUI documentation for details.
          * These MUST be inline in order to have access to the Alfresco.SiteMembers class (via the "me" variable).
          */
         var me = this;

         // DataTable column defintions
         var columnDefinitions =
         [
            { key: "userName", label: "User Name", sortable: false, formatter: this.bind(this.renderCellAvatar), width: 64 },
            { key: "bio", label: "Bio", sortable: false, formatter: this.bind(this.renderCellDescription) },
            { key: "role", label: "Select Role", formatter: this.bind(this.renderCellRoleSelect), width: 140 },
            { key: "uninvite", label: "Uninvite", formatter: this.bind(this.renderCellUninvite), width: 80 }
         ];

         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-members", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: 32,
            initialLoad: false,
            MSG_EMPTY: '<span style="white-space: nowrap;">' + this.msg("site-members.enter-search-term") + '</span>'
         });

         // Override abstract function within DataTable to set custom error message
         this.widgets.dataTable.doBeforeLoadData = function SiteMembers_doBeforeLoadData(sRequest, oResponse, oPayload)
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
                  me.widgets.dataTable.set("MSG_EMPTY", '<span style="white-space: nowrap;">' + me.msg("message.empty") + '</span>');
               }
               me.renderLoopSize = Alfresco.util.RENDERLOOPSIZE;
            }
            
            // Must return true to have the "Searching..." message replaced by the error message
            return true;
         };
      },

      /**
       * User avatar custom datacell formatter
       *
       * @method renderCellAvatar
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderCellAvatar: function SiteMembers_renderCellAvatar(elCell, oRecord, oColumn, oData)
      {
         Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

         var userName = oRecord.getData("userName"),
            userUrl = Alfresco.constants.URL_PAGECONTEXT + "user/" + userName + "/profile",
            avatarUrl = Alfresco.constants.URL_RESCONTEXT + "components/images/no-user-photo-64.png";

         if (oRecord.getData("avatar") !== undefined)
         {
            avatarUrl = Alfresco.constants.PROXY_URI + oRecord.getData("avatar") + "?c=queue&ph=true";
         }

         elCell.innerHTML = '<a href="' + userUrl + '"><img src="' + avatarUrl + '" alt="avatar" /></a>';
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
      renderCellDescription: function SiteMembers_renderCellDescription(elCell, oRecord, oColumn, oData)
      {
         // Currently rendering all results the same way
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

         var url = Alfresco.constants.URL_PAGECONTEXT + "user/" + userName + "/profile",
            title = oRecord.getData("jobtitle") ? oRecord.getData("jobtitle") : "",
            organization = oRecord.getData("organization") ? oRecord.getData("organization") : "",
            desc = '<h3><a href="' + url + '">' + $html(name) + '</a></h3>';

         if (title.length > 0)
         {
            desc += '<div><span class="attr-name">' + this.msg('label.title') + ': </span>&nbsp;<span class="attr-value">' + $html(title) + '</span></div>';
         }
         if (organization.length > 0)
         {
            desc += '<div><span class="attr-name">' + this.msg('label.company') + ':</span>&nbsp;<span class="attr-value">' + $html(organization) + '</span></div>';
         }
         if (typeof userStatus != "undefined" && userStatus.length > 0)
         {
            desc += '<div class="user-status">' + $html(userStatus) + ' <span>(' + Alfresco.util.relativeTime(Alfresco.util.fromISO8601(userStatusTime.iso8601)) + ')</span></div>';
         }

         elCell.innerHTML = desc;
      },

      /**
       * Returns the base roles that was passed in using the options.
       * Suitable for override if the roles menu shall be user specific.
       *
       * @method getRoles
       * @param oRecordData {object}
       */
      getRoles: function(oRecordData)
      {
         return this.options.roles;
      },

      /**
       * Role select custom datacell formatter
       *
       * @method renderCellRoleSelect
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderCellRoleSelect: function SiteMembers_renderCellRoleSelect(elCell, oRecord, oColumn, oData)
      {
         Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
         Dom.setStyle(elCell.parentNode, "text-align", "right");
         Dom.addClass(elCell, "overflow");

         var currentRole = oRecord.getData("role");

         if (this.isCurrentUserSiteAdmin && oRecord.getData("userName") !== this.options.currentUser)
         {
            // create HTML for representing buttons
            var userName = oRecord.getData("userName");
            elCell.innerHTML = '<span id="' + this.id + '-roleSelector-' + userName + '"></span>';

            // create the roles menu
            var rolesMenu = [],
               roles = this.getRoles(oRecord.getData()),
               role;

            for (var x = 0, xx = roles.length; x < xx; x++)
            {
               role = roles[x];
               rolesMenu.push(
                  {
                     text: this.msg("role." + role),
                     value: role,
                     onclick:
                     {
                        fn: this.onRoleSelect,
                        obj:
                        {
                           user: userName,
                           currentRole: currentRole,
                           newRole: role,
                           recordId: oRecord.getId()
                        },
                        scope: this
                     }
                  }
               );
            }

            // create the role selector button
            var roleSelector = new YAHOO.widget.Button(
            {
               container: this.id + '-roleSelector-' + userName,
               type: "menu",
               label: this.msg("role." + currentRole) + " " + Alfresco.constants.MENU_ARROW_SYMBOL,
               menu: rolesMenu
            });

            // store a reference to the role selector button
            this.listWidgets[userName] =
            {
               roleSelector: roleSelector
            };

            // store the buttons
            this.buttons[userName + "-roleSelector"] =
            {
               roleSelector: roleSelector
            };
         }
         else
         {
            // output padding div so layout is not messed up due to missing buttons
            elCell.innerHTML = '<div>' + this.msg("role." + currentRole) + '</div>';
         }
      },

      /**
       * Uninvite button custom datacell formatter
       *
       * @method renderCellUninvite
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderCellUninvite: function SiteMembers_renderCellUninvite(elCell, oRecord, oColumn, oData)
      {
         Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
         Dom.addClass(elCell.parentNode, "uninvite");
       
         if (this.isCurrentUserSiteAdmin)
         {
            // create HTML for representing buttons
            var userName = oRecord.getData("userName");
            elCell.innerHTML = '<span id="' + this.id + '-button-' + userName + '"></span>';

            // create the uninvite button
            var button = new YAHOO.widget.Button(
            {
                container: this.id + '-button-' + userName,
                label: this.msg("site-members.uninvite"),
                disabled: oRecord.getData("isMemberOfGroup"),
                onclick:
                {
                   fn: this.doRemove,
                   obj: userName,
                   scope: this
                }
            });

            button.addClass("uninvite");

            // store the buttons
            this.buttons[userName + "-button"] =
            {
               button: button
            };
         }
         else
         {
            // output padding div so layout is not messed up due to missing buttons
            elCell.innerHTML = '<div></div>';
         }
      },

      /**
       * Search event handler
       *
       * @method onSearch
       */
      onSearch: function SiteMembers_onSearch()
      {
         var searchTerm = YAHOO.lang.trim(Dom.get(this.id + "-term").value);
         if (searchTerm.replace(/\*/g, "").length < this.options.minSearchTermLength)
         {
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.minimum-length", this.options.minSearchTermLength)
            });
            return;
         }

         this._performSearch(searchTerm);
      },
      
      /**
       * Remove user event handler
       * 
       * @method doRemove
       * @param event {object} The event object
       * @param user {string} The userName to remove
       */
      doRemove: function SiteMembers_doRemove(event, user)
      {
         // show a wait message
         this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg("message.removing"),
            spanClass: "wait",
            displayTime: 0
         });
         
         // request success handler
         var success = function SiteMembers_doRemove_success(response, user)
         {
            // hide the wait message
            this.widgets.feedbackMessage.hide();
             
            // show popup message to confirm
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("site-members.remove-success", user)
            });
         
            // remove the entry
            var recordIndex = this.widgets.dataTable.getRecordIndex(event.target.id);
            this.widgets.dataTable.deleteRow(recordIndex);
         };
         
         // request failure handler
         var failure = function SiteMembers_doRemove_failure(response)
         {
            // remove the message
            this.widgets.feedbackMessage.hide();
         };
          
         // make ajax call to site service to remove user
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "api/sites/" + this.options.siteId + "/memberships/" + encodeURIComponent(user),
            method: "DELETE",
            successCallback:
            {
               fn: success,
               obj: user,
               scope: this
            },
            failureMessage: this.msg("site-members.remove-failure", user, this.options.siteId),
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
      onRoleSelect: function SiteMembers_onRoleSelect(type, event, args)
      {
         // fetch the current and new roles to see whether we have to change the role
         var record = this.widgets.dataTable.getRecord(args.recordId)
         var data = record.getData();
         var recordIndex = this.widgets.dataTable.getRecordIndex(record);
         var currentRole = data.role;
         var selectedRole = args.newRole;
         var user = args.user;
         if (selectedRole !== currentRole)
         {
            // show a wait message
            this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.changingrole"),
               spanClass: "wait",
               effect: null,
               displayTime: 0
            });

            // request success handler
            var success = function SiteMembers_onRoleSelect_success(response, userRole)
            {
               // hide the wait message
               this.widgets.feedbackMessage.hide();

               // show popup message to confirm
               Alfresco.util.PopupManager.displayMessage(
               {
                  text: this.msg("site-members.change-role-success", userRole.user, this.msg("role." + userRole.role)),
                  effect: null
               });

               // update the data and table
               var data = this.widgets.dataTable.getRecord(userRole.recordIndex).getData();
               data.role = args.newRole;
               this.widgets.dataTable.updateRow(userRole.recordIndex, data);
            };

            // request failure handler
            var failure = function SiteMembers_onRoleSelect_failure(response)
            {
               // remove the message
               this.widgets.feedbackMessage.hide();
            };

            // make ajax call to site service to change role
            Alfresco.util.Ajax.jsonPut(
            {
               url: Alfresco.constants.PROXY_URI + "api/sites/" + this.options.siteId + "/memberships",
               dataObj:
               {
                  role: selectedRole,
                  person:
                  {
                     userName: user
                  }
               },
               successCallback:
               {
                  fn: success,
                  obj:
                  {
                     user: user,
                     role: selectedRole,
                     recordIndex: recordIndex
                  },
                  scope: this
               },
               failureMessage: this.msg("site-members.change-role-failure", user),
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
      onDeactivateAllControls: function SiteMembers_onDeactivateAllControls(layer, args)
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
      _setDefaultDataTableErrors: function SiteMembers__setDefaultDataTableErrors(dataTable)
      {
         var msg = Alfresco.util.message;
         dataTable.set("MSG_EMPTY", msg("message.empty", "Alfresco.SiteMembers"));
         dataTable.set("MSG_ERROR", msg("message.error", "Alfresco.SiteMembers"));
      },
      
      /**
       * Updates members list by calling data webscript with current search term
       *
       * @method _performSearch
       * @param searchTerm {string} The term to search for
       */
      _performSearch: function SiteMembers__performSearch(searchTerm)
      {
         if (!this.isSearching)
         {
            this.isSearching = true;

            // Reset the custom error messages
            this._setDefaultDataTableErrors(this.widgets.dataTable);

            // Display loading message
            this.widgets.dataTable.set("MSG_EMPTY", this.msg("site-members.searching"));

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
                     this.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
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
      _enableSearchUI: function SiteMembers__enableSearchUI()
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
      _buildSearchParams: function SiteMembers__buildSearchParams(searchTerm)
      {
         var params = YAHOO.lang.substitute("size={maxResults}&nf={term}&authorityType=USER",
         {
            maxResults: this.options.maxSearchResults,
            term: encodeURIComponent(searchTerm)
         });
         
         return params;
      }

   });
})();
