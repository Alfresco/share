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
 * ConsoleUsers tool component.
 *
 * @namespace Alfresco
 * @class Alfresco.ConsoleUsers
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
    * ConsoleUsers constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.ConsoleUsers} The new ConsoleUsers instance
    * @constructor
    */
   Alfresco.ConsoleUsers = function(htmlId)
   {
      this.name = "Alfresco.ConsoleUsers";
      Alfresco.ConsoleUsers.superclass.constructor.call(this, htmlId);

      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datasource", "datatable", "json", "history"], this.onComponentsLoaded, this);

      /* Decoupled event listeners */
      YAHOO.Bubbling.on("viewUserClick", this.onViewUserClick, this);

      /* Define panel handlers */
      var parent = this;

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
         onLoad: function onLoad()
         {
            // Buttons
            parent.widgets.searchButton = Alfresco.util.createYUIButton(parent, "search-button", parent.onSearchClick);
            parent.widgets.newuserButton = Alfresco.util.createYUIButton(parent, "newuser-button", parent.onNewUserClick);
            parent.widgets.uploadUsersButton = Alfresco.util.createYUIButton(parent, "uploadusers-button", parent.onUploadUsersClick);

            var newuserSuccess = function(res)
            {
               if (!res.json.data.creationAllowed)
               {
                  parent.widgets.newuserButton.set("disabled", true);
                  parent.widgets.uploadUsersButton.set("disabled", true);
               }
            };

            // make an ajax call to get authentication mutability - "creationAllowed" will be returned as true
            // in the response if the administrator is able to create new users on the alfresco server
            Alfresco.util.Ajax.jsonGet(
            {
               url: Alfresco.constants.PROXY_URI + "api/authentication",
               successCallback:
               {
                  fn: newuserSuccess,
                  scope: this
               },
               failureMessage: parent._msg("message.authenticationdetails-failure", $html(parent.group))
            });

            // DataTable and DataSource setup
            parent.widgets.dataSource = new YAHOO.util.DataSource(Alfresco.constants.PROXY_URI + "api/people",
            {
               responseType: YAHOO.util.DataSource.TYPE_JSON,
               responseSchema:
               {
                  resultsList: "people",
                  metaFields:
                  {
                     recordOffset: "startIndex",
                     totalRecords: "totalRecords"
                  }
               }
            });

            var me = this;

            // Work to be performed after data has been queried but before display by the DataTable
            parent.widgets.dataSource.doBeforeParseData = function PeopleFinder_doBeforeParseData(oRequest, oFullResponse)
            {
               var updatedResponse = oFullResponse;

               if (oFullResponse)
               {
                  var items = oFullResponse.people;

                  // remove GUEST(s)
                  for (var i = 0; i < items.length; i++)
                  {
                      if (items[i].userName == "guest" || items[i].userName.indexOf("guest&") == 0)
                      {
                         items.splice(i, 1);
                      }
                  }

                  // we need to wrap the array inside a JSON object so the DataTable gets the object it expects
                  updatedResponse =
                  {
                     "people": items
                  };
               }

               // update Results Bar message with number of results found
               if (items.length < parent.options.maxSearchResults)
               {
                  me._setResultsMessage("message.results", $html(parent.searchTerm), items.length);
               }
               else
               {
                  me._setResultsMessage("message.maxresults", parent.options.maxSearchResults);
               }

               return updatedResponse;
            };

            // Setup the main datatable
            this._setupDataTable();

            // register the "enter" event on the search text field
            var searchText = Dom.get(parent.id + "-search-text");

            new YAHOO.util.KeyListener(searchText,
            {
               keys: YAHOO.util.KeyListener.KEY.ENTER
            },
            {
               fn: function()
               {
                  parent.onSearchClick();
               },
               scope: parent,
               correctScope: true
            }, "keydown").enable();
         },

         onShow: function onShow()
         {
            Dom.get(parent.id + "-search-text").focus();
         },

         onUpdate: function onUpdate()
         {
            // update the text field - as this event could come from bookmark, navigation or a search button click
            var searchTermElem = Dom.get(parent.id + "-search-text");
            searchTermElem.value = parent.searchTerm;

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

               var successHandler = function ConsoleUsers__ps_successHandler(sRequest, oResponse, oPayload)
               {
                  me._enableSearchUI();
                  me._setDefaultDataTableErrors(parent.widgets.dataTable);
                  parent.widgets.dataTable.onDataReturnInitializeTable.call(parent.widgets.dataTable, sRequest, oResponse, oPayload);
               };

               var failureHandler = function ConsoleUsers__ps_failureHandler(sRequest, oResponse)
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
                        parent.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
                        me._setResultsMessage("message.noresults");
                     }
                     catch(e)
                     {
                        me._setDefaultDataTableErrors(parent.widgets.dataTable);
                     }
                  }
               };

               // Send the query to the server
               // ... with hint to use CQ for user admin page (note: passed via searchTerm in lieu of a change in the /api/people API)
               parent.widgets.dataSource.sendRequest(me._buildSearchParams(parent.searchTerm + " [hint:useCQ]"),
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
         _enableSearchUI: function _enableSearchUI()
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
         _setupDataTable: function _setupDataTable()
         {
            /**
             * DataTable Cell Renderers
             *
             * Each cell has a custom renderer defined as a custom function. See YUI documentation for details.
             * These MUST be inline in order to have access to the parent instance (via the "parent" variable).
             */

            /**
             * User avatar custom datacell formatter
             *
             * @method renderCellAvatar
             */
            var renderCellAvatar = function renderCellAvatar(elCell, oRecord, oColumn, oData)
            {
               Dom.setStyle(elCell, "min-height", "64px");
               Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
               Dom.setStyle(elCell.parentNode, "border-right", "1px solid #D7D7D7");

               // apply the avatar image as a background
               var avatarUrl = Alfresco.constants.URL_RESCONTEXT + "components/images/no-user-photo-64.png";
               if (oRecord.getData("avatar") !== undefined)
               {
                  avatarUrl = Alfresco.constants.PROXY_URI + oRecord.getData("avatar") + "?c=queue&ph=true";
               }
               Dom.setStyle(elCell, "background-image", "url('" + avatarUrl + "')");
               Dom.setStyle(elCell, "background-repeat", "no-repeat");
               Dom.setStyle(elCell, "background-position", "22px 50%");

               // overlay the account enabled/disabled indicator image
               var enabled = (oRecord.getData("enabled") ? 'enabled' : 'disabled');
               elCell.innerHTML = '<img class="indicator" alt="' + parent._msg("label." + enabled) + '" src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/account_' + enabled + '.png" alt="" />';
            };

            /**
             * User full name custom datacell formatter
             *
             * @method renderCellFullName
             */
            var renderCellFullName = function renderCellFullName(elCell, oRecord, oColumn, oData)
            {
               // Create view userlink
               var firstName = oRecord.getData("firstName"),
                  lastName = oRecord.getData("lastName"),
                  name = firstName + ' ' + (lastName ? lastName : ""),
                  viewUserLink = document.createElement("a");
               viewUserLink.innerHTML = $html(name);

               // fire the 'viewUserClick' event when the selected user in the list has changed
               YAHOO.util.Event.addListener(viewUserLink, "click", function(e)
               {
                  YAHOO.Bubbling.fire('viewUserClick',
                  {
                     username: oRecord.getData("userName")
                  });
               }, null, parent);
               elCell.appendChild(viewUserLink);
            };

            /**
             * Quota custom datacell formatter
             *
             * @method renderCellQuota
             */
            var renderCellQuota = function renderCellQuota(elCell, oRecord, oColumn, oData)
            {
               var quota = oRecord.getData("quota");
               var display = (quota !== -1 ? Alfresco.util.formatFileSize(quota) : "");
               elCell.innerHTML = display;
            };

            /**
             * Usage custom datacell formatter
             *
             * @method renderCellUsage
             */
            var renderCellUsage = function renderCellQuota(elCell, oRecord, oColumn, oData)
            {
               elCell.innerHTML = Alfresco.util.formatFileSize(oRecord.getData("sizeCurrent"));
            };

            /**
             * Generic HTML-safe custom datacell formatter
             */
            var renderCellSafeHTML = function renderCellSafeHTML(elCell, oRecord, oColumn, oData)
            {
               elCell.innerHTML = $html(oData);
            };

            /**
             * Usage custom datacell sorter
             */
            var sortCellUsage = function sortCellUsage(a, b, desc)
            {
               var numA = a.getData("sizeCurrent"),
                   numB = b.getData("sizeCurrent");

               if (desc)
               {
                  return (numA < numB ? 1 : (numA > numB ? -1 : 0));
               }
               return (numA < numB ? -1 : (numA > numB ? 1 : 0));
            };

            /**
             * Quota custom datacell sorter
             */
            var sortCellQuota = function sortCellQuota(a, b, desc)
            {
               var numA = a.getData("quota"),
                   numB = b.getData("quota");

               if (desc)
               {
                  return (numA < numB ? 1 : (numA > numB ? -1 : 0));
               }
               return (numA < numB ? -1 : (numA > numB ? 1 : 0));
            };

            // DataTable column defintions
            var columnDefinitions =
            [
               { key: "avatar", label: "", sortable: false, formatter: renderCellAvatar, width: 70 },
               { key: "fullName", label: parent._msg("label.name"), sortable: true, formatter: renderCellFullName },
               { key: "userName", label: parent._msg("label.username"), sortable: true, formatter: renderCellSafeHTML },
               { key: "jobtitle", label: parent._msg("label.jobtitle"), sortable: true, formatter: renderCellSafeHTML },
               { key: "email", label: parent._msg("label.email"), sortable: true, formatter: renderCellSafeHTML },
               { key: "usage", label: parent._msg("label.usage"), sortable: true, sortOptions: {sortFunction: sortCellUsage}, formatter: renderCellUsage },
               { key: "quota", label: parent._msg("label.quota"), sortable: true, sortOptions: {sortFunction: sortCellQuota}, formatter: renderCellQuota }
            ];

            // DataTable definition
            parent.widgets.dataTable = new YAHOO.widget.DataTable(parent.id + "-datatable", columnDefinitions, parent.widgets.dataSource,
            {
               initialLoad: false,
               renderLoopSize: 32,
               dynamicData: true,
               sortedBy:
               {
                  key: "userName",
                  dir: "asc"
               },
               generateRequest:  function(oState, oSelf) {

               // Set defaults
               oState = oState || {pagination:null, sortedBy:null};
               var sort = encodeURIComponent((oState.sortedBy) ? oState.sortedBy.key : oSelf.getColumnSet().keys[0].getKey());
               var dir = (oState.sortedBy && oState.sortedBy.dir === YAHOO.widget.DataTable.CLASS_DESC) ? "desc" : "asc";

               // Build the request
               var query =  "?sortBy=" + sort + "&dir=" + dir;

               if (parent.searchTerm)
               {
                  query = query + "&filter=" + encodeURIComponent(parent.searchTerm);
               }

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
          * @param dataTable {object} Instance of the DataTable
          * @private
          */
         _setDefaultDataTableErrors: function _setDefaultDataTableErrors(dataTable)
         {
            var msg = Alfresco.util.message;
            dataTable.set("MSG_EMPTY", parent._msg("message.empty", "Alfresco.ConsoleUsers"));
            dataTable.set("MSG_ERROR", parent._msg("message.error", "Alfresco.ConsoleUsers"));
         },

         /**
          * Build URI parameters for People List JSON data webscript
          *
          * @method _buildSearchParams
          * @param searchTerm {string} User search term
          * @private
          */
         _buildSearchParams: function _buildSearchParams(searchTerm)
         {
            return "?filter=" + encodeURIComponent(searchTerm) + "&maxResults=" + parent.options.maxSearchResults;
         },

         /**
          * Set the message in the Results Bar area
          *
          * @method _setResultsMessage
          * @param messageId {string} The messageId to display
          * @private
          */
         _setResultsMessage: function _setResultsMessage(messageId, arg1, arg2)
         {
            var resultsDiv = Dom.get(parent.id + "-search-bar");
            resultsDiv.innerHTML = parent._msg(messageId, arg1, arg2);
         }
      });
      new SearchPanelHandler();

      /* View Panel Handler */
      ViewPanelHandler = function ViewPanelHandler_constructor()
      {
         ViewPanelHandler.superclass.constructor.call(this, "view");
      };

      YAHOO.extend(ViewPanelHandler, Alfresco.ConsolePanelHandler,
      {
         onLoad: function onLoad()
         {
            // Buttons
            parent.widgets.gobackButton = Alfresco.util.createYUIButton(parent, "goback-button", parent.onGoBackClick);
            parent.widgets.deleteuserButton = Alfresco.util.createYUIButton(parent, "deleteuser-button", parent.onDeleteUserClick);
            parent.widgets.edituserButton = Alfresco.util.createYUIButton(parent, "edituser-button", parent.onEditUserClick);
         },

         onBeforeShow: function onBeforeShow()
         {
            // Hide the main panel area before it is displayed - so we don't show
            // old data to the user before the Update() method paints the results
            Dom.get(parent.id + "-view-title").innerHTML = "";
            Dom.setStyle(parent.id + "-view-main", "visibility", "hidden");
         },

         onShow: function onShow()
         {
            window.scrollTo(0, 0);
         },

         onUpdate: function onUpdate()
         {
            var success = function(res)
            {
               var fnSetter = function(id, val)
               {
                  Dom.get(parent.id + id).innerHTML = val ? $html(val) : "";
               };

               var person = YAHOO.lang.JSON.parse(res.serverResponse.responseText);

               // apply avatar image URL
               var photos = Dom.getElementsByClassName("view-photoimg", "img");
               for (var i in photos)
               {
                  photos[i].src = person.avatar ?
                        Alfresco.constants.PROXY_URI + person.avatar + "?c=force" :
                        Alfresco.constants.URL_RESCONTEXT + "components/images/no-user-photo-64.png";
               }

               // About section fields
               var firstName = person.firstName,
                  lastName = person.lastName,
                  fullName = firstName + ' ' + (lastName ? lastName : "");
               fnSetter("-view-title", fullName);
               fnSetter("-view-name", fullName);
               fnSetter("-view-jobtitle", person.jobtitle);
               fnSetter("-view-organization", person.organization);
               fnSetter("-view-bio", person.persondescription ? person.persondescription : "");

               // Contact section fields
               fnSetter("-view-location", person.location);
               fnSetter("-view-email", person.email);
               fnSetter("-view-telephone", person.telephone);
               fnSetter("-view-mobile", person.mobile);
               fnSetter("-view-skype", person.skype);
               fnSetter("-view-instantmsg", person.instantmsg);
               fnSetter("-view-googleusername", person.googleusername);

               // Company section fields
               fnSetter("-view-companyname", person.organization);
               // build the company address up and set manually - encoding each value
               var addr = "";
               addr += person.companyaddress1 ? ($html(person.companyaddress1) + "<br/>") : "";
               addr += person.companyaddress2 ? ($html(person.companyaddress2) + "<br/>") : "";
               addr += person.companyaddress3 ? ($html(person.companyaddress3) + "<br/>") : "";
               addr += person.companypostcode ? ($html(person.companypostcode) + "<br/>") : "";
               Dom.get(parent.id + "-view-companyaddress").innerHTML = addr;
               fnSetter("-view-companytelephone", person.companytelephone);
               fnSetter("-view-companyfax", person.companyfax);
               fnSetter("-view-companyemail", person.companyemail);

               // More section fields
               fnSetter("-view-username", parent.currentUserId);
               fnSetter("-view-enabled", person.enabled ? parent._msg("label.enabled") : parent._msg("label.disabled"));
               fnSetter("-view-quota", (person.quota !== -1 ? Alfresco.util.formatFileSize(person.quota) : ""));
               fnSetter("-view-usage", Alfresco.util.formatFileSize(person.sizeCurrent));
               var fnGroupToString = function()
               {
                  return this.displayName;
               }
               for (var i = 0, j = person.groups.length; i < j; person.groups[i++].toString = fnGroupToString) {}
               fnSetter("-view-groups", person.groups.join(", "));

               // Make main panel area visible
               Dom.setStyle(parent.id + "-view-main", "visibility", "visible");
            };

            // make an ajax call to get user details
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.PROXY_URI + "api/people/" + encodeURIComponent(parent.currentUserId) + "?groups=true",
               method: Alfresco.util.Ajax.GET,
               successCallback:
               {
                  fn: success,
                  scope: parent
               },
               failureMessage: parent._msg("message.getuser-failure", $html(parent.currentUserId))
            });
         }
      });
      new ViewPanelHandler();

      /* Create User Panel Handler */
      CreatePanelHandler = function CreatePanelHandler_constructor()
      {
         CreatePanelHandler.superclass.constructor.call(this, "create");
      };

      YAHOO.extend(CreatePanelHandler, Alfresco.ConsolePanelHandler,
      {
         _visible: false,

         _groups: [],

         _form: null,

         onLoad: function onLoad()
         {
            // events we are interested in
            YAHOO.Bubbling.on("itemSelected", this.onGroupSelected, this);
            YAHOO.Bubbling.on("removeGroupCreate", this.onRemoveGroupCreate, this);

            // Buttons
            parent.widgets.createuserOkButton = Alfresco.util.createYUIButton(parent, "createuser-ok-button", parent.onCreateUserOKClick);
            parent.widgets.createuserAnotherButton = Alfresco.util.createYUIButton(parent, "createuser-another-button", parent.onCreateUserAnotherClick);
            parent.widgets.createuserCancelButton = Alfresco.util.createYUIButton(parent, "createuser-cancel-button", parent.onCreateUserCancelClick);

            // Form definition
            var form = new Alfresco.forms.Form(parent.id + "-create-form");
            form.setSubmitElements([parent.widgets.createuserOkButton, parent.widgets.createuserAnotherButton]);

            // Form field validation
            form.addValidation(parent.id + "-create-firstname", Alfresco.forms.validation.mandatory, null, "keyup");
            form.addValidation(parent.id + "-create-email", Alfresco.forms.validation.mandatory, null, "keyup");
            form.addValidation(parent.id + "-create-email", Alfresco.forms.validation.email, null, "change", parent._msg("Alfresco.forms.validation.email.message"));
            form.addValidation(parent.id + "-create-username", Alfresco.forms.validation.mandatory, null, "keyup");
            form.addValidation(parent.id + "-create-username", Alfresco.forms.validation.nodeName, null, "keyup", parent._msg("Alfresco.forms.validation.nodeName.message"));
            form.addValidation(parent.id + "-create-username", Alfresco.forms.validation.length,
            {
               min: parent.options.minUsernameLength,
               max: 100,
               crop: true,
               includeWhitespace: false
            }, "keyup", parent._msg("Alfresco.forms.validation.length.message"));
            form.addValidation(parent.id + "-create-password", Alfresco.forms.validation.mandatory, null, "keyup");
            form.addValidation(parent.id + "-create-password", Alfresco.forms.validation.length,
            {
               min: parent.options.minPasswordLength,
               max: 100,
               crop: true
            }, "change", parent._msg("Alfresco.forms.validation.length.message"));
            form.addValidation(parent.id + "-create-verifypassword", Alfresco.forms.validation.mandatory, null, "keyup");
            form.addValidation(parent.id + "-create-verifypassword", Alfresco.forms.validation.length,
            {
               min: parent.options.minPasswordLength,
               max: 100,
               crop: true
            }, "change", parent._msg("Alfresco.forms.validation.length.message"));
            form.addValidation(parent.id + "-create-quota", Alfresco.forms.validation.number, null, "keyup");

            // Add an enter listener to the form
            new YAHOO.util.KeyListener(form.formId, {
               keys: [13]
            }, {
               fn: function(e) {
                  parent.onCreateUserOKClick();
               },
               scope: this,
               correctScope: true
            }).enable();

            // Initialise the form
            form.init();
            this._form = form;

            // Load in the Groups Finder component from the server
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "components/people-finder/group-finder",
               dataObj:
               {
                  htmlid: parent.id + "-create-groupfinder"
               },
               successCallback:
               {
                  fn: this.onGroupFinderLoaded,
                  scope: this
               },
               failureMessage: "Could not load Group Finder component",
               execScripts: true
            });
         },

         onGroupFinderLoaded: function onGroupFinderLoaded(res)
         {
            // Inject the component from the XHR request into it's placeholder DIV element
            var finderDiv = Dom.get(parent.id + "-create-groupfinder");
            finderDiv.innerHTML = res.serverResponse.responseText;

            // Find the Group Finder by container ID
            parent.modules.createGroupFinder = Alfresco.util.ComponentManager.get(parent.id + "-create-groupfinder");

            // Set the correct options for our use
            parent.modules.createGroupFinder.setOptions(
            {
               viewMode: Alfresco.GroupFinder.VIEW_MODE_COMPACT,
               singleSelectMode: false,
               wildcardPrefix: false
            });
         },

         /**
          * Group selected event handler.
          * This event can be fired from either Groups picker - so we much ensure
          * the event is for the current panel by checking panel visibility.
          *
          * @method onGroupSelected
          * @param e {object} DomEvent
          * @param args {array} Event parameters (depends on event type)
          */
         onGroupSelected: function onGroupSelected(e, args)
         {
            if (this._visible)
            {
               this.addGroup(args[1]);
            }
         },

         /**
          * Add a group to the list of selected groups
          *
          * @method addGroup
          * @param group {object} Group object
          */
         addGroup: function addGroup(group)
         {
            var found = false;
            for (var i=0, j=this._groups.length; i<j; i++)
            {
               if (this._groups[i] != null && this._groups[i].itemName === group.itemName)
               {
                  found = true;
                  break;
               }
            }

            if (!found)
            {
               this._groups.push(group);

               var groupDiv = Dom.get(parent.id + "-create-groups");
               var idx = (this._groups.length - 1);
               var groupEl = document.createElement("span");
               groupEl.setAttribute("id", parent.id + "_group" + idx);
               groupEl.setAttribute("title", parent._msg("label.removegroup"));
               Dom.addClass(groupEl, "group-item");
               groupEl.innerHTML = $html(group.displayName);
               groupDiv.appendChild(groupEl);

               Alfresco.util.useAsButton(groupEl, function(e, obj)
               {
                  // Remove group from ui
                  YAHOO.Bubbling.fire('removeGroupCreate', { id: obj.idx });

                  // Tell group finder to deselect the group
                  YAHOO.Bubbling.fire('itemDeselected', { eventGroup: parent.modules.createGroupFinder, itemName: obj.group.itemName });
               }, { idx: idx, group: group });
            }
         },

         getGroups: function getGroups()
         {
            var groups = [];
            for (var i=0, j=this._groups.length; i<j; i++)
            {
               if (this._groups[i] != null)
               {
                  groups.push(this._groups[i].itemName);
               }
            }
            return groups;
         },

         /**
          * Group removed event handler
          *
          * @method onRemoveGroupCreate
          * @param e {object} DomEvent
          * @param args {array} Event parameters (depends on event type)
          */
         onRemoveGroupCreate: function onRemoveGroupCreate(e, args)
         {
            var i = args[1].id;
            var el = Dom.get(parent.id + "_group" + i);
            el.parentNode.removeChild(el);
            this._groups[i] = null;
         },

         onBeforeShow: function onBeforeShow()
         {
            // Hide the main panel area before it is displayed - so we don't show
            // old data to the user before the onShow() method paints the results
            Dom.setStyle(parent.id + "-create-main", "visibility", "hidden");

            this.clear();
         },

         clear: function clear()
         {
            var fnClearEl = function(id)
            {
               Dom.get(parent.id + id).value = "";
            };

            // clear data fields
            fnClearEl("-create-firstname");
            fnClearEl("-create-lastname");
            fnClearEl("-create-email");
            fnClearEl("-create-username");
            fnClearEl("-create-password");
            fnClearEl("-create-verifypassword");
            fnClearEl("-create-quota");
            Dom.get(parent.id + "-create-disableaccount").checked = false;

            // reset quota selection drop-down
            Dom.get(parent.id + "-create-quotatype").value = "gb";

            // clear selected groups
            this._groups = [];
            Dom.get(parent.id + "-create-groups").innerHTML = "";
            if (parent.modules.createGroupFinder)
            {
               parent.modules.createGroupFinder.clearResults();
            }
            if (this._form !== null)
            {
               this._form.init();
            }

            // Notify group finder that no groups are selected
            YAHOO.Bubbling.fire("allItemsDeselected",
            {
               eventGroup: parent.modules.createGroupFinder
            });

         },

         onShow: function onShow()
         {
            this._visible = true;
            window.scrollTo(0, 0);

            // Make main panel area visible
            Dom.setStyle(parent.id + "-create-main", "visibility", "visible");

            Dom.get(parent.id + "-create-firstname").focus();
         },

         onHide: function onHide()
         {
            this._visible = false;
         }
      });
      new CreatePanelHandler();

      /* Update User Panel Handler */
      UpdatePanelHandler = function UpdatePanelHandler_constructor()
      {
         UpdatePanelHandler.superclass.constructor.call(this, "update");
      };

      YAHOO.extend(UpdatePanelHandler, Alfresco.ConsolePanelHandler,
      {
         _visible: false,

         _removedGroups: [],
         _addedGroups: [],
         _originalGroups: [],
         _groups: [],
         _photoReset: false,
         _form: null,

         onLoad: function onLoad()
         {
            // events we are interested in
            YAHOO.Bubbling.on("itemSelected", this.onGroupSelected, this);
            YAHOO.Bubbling.on("removeGroupUpdate", this.onRemoveGroupUpdate, this);

            // Buttons
            parent.widgets.updateuserSaveButton = Alfresco.util.createYUIButton(parent, "updateuser-save-button", parent.onUpdateUserOKClick);
            parent.widgets.updateuserCancelButton = Alfresco.util.createYUIButton(parent, "updateuser-cancel-button", parent.onUpdateUserCancelClick);
            parent.widgets.updateuserClearPhotoButton = Alfresco.util.createYUIButton(parent, "updateuser-clearphoto-button", parent.onUpdateUserClearPhotoClick);

            // Form definition
            var form = new Alfresco.forms.Form(parent.id + "-update-form");
            form.setSubmitElements(parent.widgets.updateuserSaveButton);

            // Form field validation
            form.addValidation(parent.id + "-update-firstname", Alfresco.forms.validation.mandatory, null, "keyup");
            form.addValidation(parent.id + "-update-email", Alfresco.forms.validation.mandatory, null, "keyup");
            form.addValidation(parent.id + "-update-email", Alfresco.forms.validation.email, null, "keyup");
            form.addValidation(parent.id + "-update-quota", Alfresco.forms.validation.number, null, "keyup");

            // Initialise the form
            form.init();
            this._form = form;

            // Load in the Groups Finder component from the server
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "components/people-finder/group-finder",
               dataObj:
               {
                  htmlid: parent.id + "-update-groupfinder"
               },
               successCallback:
               {
                  fn: this.onGroupFinderLoaded,
                  scope: this
               },
               failureMessage: "Could not load Group Finder component",
               execScripts: true
            });
         },

         onGroupFinderLoaded: function onGroupFinderLoaded(res)
         {
            // Inject the component from the XHR request into it's placeholder DIV element
            var finderDiv = Dom.get(parent.id + "-update-groupfinder");
            finderDiv.innerHTML = res.serverResponse.responseText;

            // Find the Group Finder by container ID
            parent.modules.updateGroupFinder = Alfresco.util.ComponentManager.get(parent.id + "-update-groupfinder");

            // Set the correct options for our use
            parent.modules.updateGroupFinder.setOptions(
            {
               viewMode: Alfresco.GroupFinder.VIEW_MODE_COMPACT,
               singleSelectMode: false,
               wildcardPrefix: false
            });
         },

         /**
          * Group selected event handler.
          * This event can be fired from either Groups picker - so we much ensure
          * the event is for the current panel by checking panel visibility.
          *
          * @method onGroupSelected
          * @param e {object} DomEvent
          * @param args {array} Event parameters (depends on event type)
          */
         onGroupSelected: function onGroupSelected(e, args)
         {
            if (this._visible)
            {
               this.addGroup(args[1]);
            }
         },

         /**
          * Add a group to the list of selected groups
          *
          * @method addGroup
          * @param group {object} Group object
          */
         addGroup: function addGroup(group)
         {
            var found = false;
            if (Alfresco.util.findInArray(this._groups, group.itemName, "itemName"))
            {
               found = true;
            }

            if (!found)
            {
               this._groups.push(group);

               var groupDiv = Dom.get(parent.id + "-update-groups"),
                  idx = (this._groups.length-1),
                  groupEl = document.createElement("span");
               groupEl.setAttribute("id", parent.id + "_group" + idx);
               groupEl.setAttribute("title", parent._msg("label.removegroup"));
               Dom.addClass(groupEl, "group-item");
               groupEl.innerHTML = $html(group.displayName);
               groupDiv.appendChild(groupEl);

               Alfresco.util.useAsButton(groupEl, function(e, obj)
               {
                  // Remove group from ui
                  YAHOO.Bubbling.fire('removeGroupUpdate', { id: obj.idx });

                  // Tell group finder to deselect the group
                  YAHOO.Bubbling.fire('itemDeselected', { eventGroup: parent.modules.updateGroupFinder, itemName: obj.group.itemName });
               }, { idx: idx, group: group });

               // if this group wasn't one of the original list, then add it to the addition list
               found = false;
               if (Alfresco.util.findInArray(this._originalGroups, group.itemName, "itemName"))
               {
                  found = true;
               }

               if (!found)
               {
                  this._addedGroups.push(group.itemName);
               }

               // if the group has been removed before, remove it from the removed groups list
               if (Alfresco.util.arrayContains(this._removedGroups, group.itemName))
               {
                  Alfresco.util.arrayRemove(this._removedGroups, group.itemName);
               }
            }
         },

         /**
          * Group removed event handler
          *
          * @method onRemoveGroupUpdate
          * @param e {object} DomEvent
          * @param args {array} Event parameters (depends on event type)
          */
         onRemoveGroupUpdate: function onRemoveGroupUpdate(e, args)
         {
            var id = args[1].id;
            var el = Dom.get(parent.id + "_group" + id);
            el.parentNode.removeChild(el);
            var group = this._groups[id];
            Alfresco.util.arrayRemove(this._groups, group);

            // if this group was one of the original list, then add it to the removed list
            if (Alfresco.util.findInArray(this._originalGroups, group.itemName, "itemName"))
            {
               this._removedGroups.push(group.itemName);
            }

            // also remove from the added groups list
            if (Alfresco.util.arrayContains(this._addedGroups, group.itemName))
            {
               Alfresco.util.arrayRemove(this._addedGroups, group.itemName);
            }
         },

         getAddedGroups: function getAddedGroups()
         {
            return this._addedGroups;
         },

         getRemovedGroups: function getRemovedGroups()
         {
            return this._removedGroups;
         },

         resetGroups: function resetGroups()
         {
            this._groups = [];
            this._addedGroups = [];
            this._removedGroups = [];
            Dom.get(parent.id + "-update-groups").innerHTML = "";
         },

         setPhotoReset: function setPhotoReset()
         {
            this._photoReset = true;
         },

         getPhotoReset: function getPhotoReset()
         {
            return this._photoReset;
         },

         onBeforeShow: function onBeforeShow()
         {
            // Hide the main panel area before it is displayed - so we don't show
            // old data to the user before the Update() method paints the results
            Dom.get(parent.id + "-update-title").innerHTML = "";
            Dom.setStyle(parent.id + "-update-main", "visibility", "hidden");
         },

         onShow: function onShow()
         {
            this._visible = true;
            window.scrollTo(0, 0);
         },

         onHide: function onHide()
         {
            this._visible = false;
         },

         onUpdate: function onUpdate()
         {
            var me = this;
            var success = function(res)
            {
               var fnSetter = function(id, val)
               {
                  Dom.get(parent.id + id).value = val;
               };
               var fnDisabler = function(id, propId, map)
               {
                  if (map["{http://www.alfresco.org/model/content/1.0}" + propId])
                  {
                     Dom.get(parent.id + id).setAttribute("disabled", true);
                  }
               };

               var person = YAHOO.lang.JSON.parse(res.serverResponse.responseText);

               // apply avatar image URL
               var photos = Dom.getElementsByClassName("update-photoimg", "img");
               for (var i in photos)
               {
                  photos[i].src = person.avatar ?
                        Alfresco.constants.PROXY_URI + person.avatar + "?c=force" :
                        Alfresco.constants.URL_RESCONTEXT + "components/images/no-user-photo-64.png";
               }

               // About section fields
               var firstName = person.firstName,
                  lastName = person.lastName,
                  fullName = firstName + ' ' + (lastName ? lastName : "");
               Dom.get(parent.id + "-update-title").innerHTML = $html(fullName);
               fnSetter("-update-firstname", firstName);
               fnDisabler("-update-firstname", "firstName", person.immutability);
               fnSetter("-update-lastname", lastName);
               fnDisabler("-update-lastname", "lastName", person.immutability);
               fnSetter("-update-email", person.email);
               fnDisabler("-update-email", "email", person.immutability);
               if (!person.capabilities.isMutable)
               {
                  Dom.get(parent.id + "-update-old-password").setAttribute("disabled", true);
                  Dom.get(parent.id + "-update-password").setAttribute("disabled", true);
                  Dom.get(parent.id + "-update-verifypassword").setAttribute("disabled", true);
                  Dom.get(parent.id + "-update-disableaccount").setAttribute("disabled", true);
               }
               fnSetter("-update-old-password", "");
               fnSetter("-update-password", "");
               fnSetter("-update-verifypassword", "");

               // convert quota to closest value type
               var quota = person.quota;
               if (quota !== -1)
               {
                  if (quota < Alfresco.util.BYTES_MB)
                  {
                     // show in kilobytes
                     quota = Math.round(quota / Alfresco.util.BYTES_KB);
                     Dom.get(parent.id + "-update-quotatype").value = "kb";
                  }
                  else if (quota < Alfresco.util.BYTES_GB)
                  {
                     // show in metabytes
                     quota = Math.round(quota / Alfresco.util.BYTES_MB);
                     Dom.get(parent.id + "-update-quotatype").value = "mb";
                  }
                  else
                  {
                     // show in gigabytes
                     quota = Math.round(quota / Alfresco.util.BYTES_GB);
                     Dom.get(parent.id + "-update-quotatype").value = "gb";
                  }
                  fnSetter("-update-quota", quota.toString());
               }
               else
               {
                  fnSetter("-update-quota", "");
               }

               // account enabled/disabled
               Dom.get(parent.id + "-update-disableaccount").checked = (person.enabled == false);

               // add groups the user is already assigned to and maintain a copy of the original group list
               me.resetGroups();
               YAHOO.Bubbling.fire("allItemsDeselected",
               {
                  eventGroup: parent.modules.updateGroupFinder
               });
               me._originalGroups = person.groups;
               for (var i=0, j=person.groups.length; i<j; i++)
               {
                  me.addGroup(
                  {
                     "itemName": person.groups[i].itemName,
                     "displayName": person.groups[i].displayName
                  });

                  // Make the group finder aware of which groups the user already has
                  YAHOO.Bubbling.fire("itemSelected",
                  {
                     eventGroup: parent.modules.updateGroupFinder,
                     "itemName": person.groups[i].itemName,
                     "displayName": person.groups[i].displayName
                  });
               }

               // Hide or show the old password field - only required if user changing own password
               if (parent.currentUserId.toLowerCase() === Alfresco.constants.USERNAME.toLowerCase())
               {
                  Dom.setStyle(parent.id + "-oldpassword-wrapper", "display", "block");
               }
               else
               {
                  Dom.setStyle(parent.id + "-oldpassword-wrapper", "display", "none");
               }

               // Make main panel area visible
               Dom.setStyle(parent.id + "-update-main", "visibility", "visible");

               me._form.validate();
            };

            // make an ajax call to get user details
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.PROXY_URI + "api/people/" + encodeURIComponent(parent.currentUserId) + "?groups=true",
               method: Alfresco.util.Ajax.GET,
               successCallback:
               {
                  fn: success,
                  scope: parent
               },
               failureMessage: parent._msg("message.getuser-failure", $html(parent.currentUserId))
            });
         }
      });
      new UpdatePanelHandler();

      CSVResultsPanelHandler = function CSVResultsPanelHandler_constructor()
      {
         CSVResultsPanelHandler.superclass.constructor.call(this, "csvresults");
      };

      YAHOO.extend(CSVResultsPanelHandler, Alfresco.ConsolePanelHandler,
      {
         /**
          * PANEL LIFECYCLE CALLBACKS
          */
         /**
          * Called by the ConsolePanelHandler when this panel shall be loaded
          *
          * @method onLoad
          */
         onLoad: function onLoad()
         {
            parent.widgets.csvGobackButton = Alfresco.util.createYUIButton(parent, "csv-goback-button", parent.onGoBackClick);
         },

         onShow: function onShow()
         {
            if (parent.csvResults)
            {
               var dataSource;
               var successful = parent.csvResults.successful;
               if (successful &&  successful.length > 0 && parent.csvResults.successful[0].response)
               {
                  successful = successful[0].response;

                  // If the response contains the "successful" array containing an element then it does not necessarily
                  // mean that the CSV upload succeeded. This simply means that the upload request was successfully processed
                  // (i.e. the file was received)
                  if (successful.data && successful.data.users)
                  {
                     parent.fileUpload.hide();

                     // If the successful response contains a data object with a "users" attribute then we at least know that
                     // some users have been processed so can construct a result table using that data...
                     dataSource = new YAHOO.util.DataSource(successful.data.users);
                     dataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
                     dataSource.responseSchema = { fields: [ "username", "uploadStatus" ]};

                     // Show a pop-up with the summary data...
                     if (successful.data.addedUsers == 0)
                     {
                        // No new users were added...
                        Alfresco.util.PopupManager.displayMessage(
                        {
                           text: parent._msg("message.csvupload.failure")
                        });
                     }
                     else if (successful.data.addedUsers == successful.data.totalUsers)
                     {
                        // All the users found in the CSV file were added
                        Alfresco.util.PopupManager.displayMessage(
                        {
                           text: parent._msg("message.csvupload.success", successful.data.addedUsers)
                        });
                     }
                     else
                     {
                        // Some of the users could not be added.
                        var failedUsers = successful.data.totalUsers - successful.data.addedUsers;
                        Alfresco.util.PopupManager.displayMessage(
                        {
                           text: parent._msg("message.csvupload.partialSuccess", successful.data.addedUsers, failedUsers)
                        });
                     }

                     var columnDefs = [{key:"username", label: parent._msg("label.username"), sortable: true, resizeable: true},
                                       {key:"uploadStatus", label: parent._msg("label.uploadStatus"), sortable: true, resizeable: true}];

                     var resultsTable = new YAHOO.widget.DataTable(parent.id + "-csvresults-datatable",
                                                                   columnDefs,
                                                                   dataSource);

                     Dom.removeClass(parent.id + "-csvresults-success", "hidden");
                     Dom.addClass(parent.id + "-csvresults-failure", "hidden");
                  }
                  else
                  {
                     parent.fileUpload.hide();

                     // The CSV upload failed
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: parent._msg("message.csvupload.error")
                     });

                     Dom.get(parent.id + "-csvresults-error").innerHTML = successful.message;

                     Dom.addClass(parent.id + "-csvresults-success", "hidden");
                     Dom.removeClass(parent.id + "-csvresults-failure", "hidden");
                  }


               }
               else
               {
                  // The upload did not work.
               }
            }
         }
      });
      new CSVResultsPanelHandler();

      return this;
   };

   YAHOO.extend(Alfresco.ConsoleUsers, Alfresco.ConsoleTool,
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
          * Minimum length of a username
          *
          * @property minUsernameLength
          * @type int
          * @default 2
          */
         minUsernameLength: 2,

         /**
          * Minimum length of a password
          *
          * @property minPasswordLength
          * @type int
          * @default 3
          */
         minPasswordLength: 3
      },

      /**
       * Current user id for an action.
       *
       * @property currentUserId
       * @type string
       */
      currentUserId: "",

      /**
       * Current search term, obtained from form input field.
       *
       * @property searchTerm
       * @type string
       */
      searchTerm: undefined,

      /**
       * The result of the last CSV upload.
       *
       * @property csvResults
       * @type object
       */
      csvResults: undefined,


      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function ConsoleUsers_onReady()
      {
         // Generate the popup dialog for confirmation of deleting a user
         this.popups.deleteDialog = Alfresco.util.createYUIPanel("deleteDialog",
         {
            width: "36em",
            text: '<div class="yui-u" style="text-align:center"><br/>' + this._msg("panel.delete.msg") + '<br/><br/>' + this._msg("panel.delete.note") + '<br/><br/></div>',
            buttons: [
            {
               text: this._msg("button.delete"),
               handler:
               {
                  fn: this.onDeleteUserOK,
                  scope: this
               }
            },
            {
               text: this._msg("button.cancel"),
               handler:
               {
                  fn: this.onDeleteUserCancel,
                  scope: this
               },
               isDefault: true
            }]
         },
         {
            type: YAHOO.widget.SimpleDialog
         });

         this.popups.deleteDialog.setHeader(this._msg("panel.delete.header"));

         // Call super-class onReady() method
         Alfresco.ConsoleUsers.superclass.onReady.call(this);
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
      onStateChanged: function ConsoleUsers_onStateChanged(e, args)
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

            this.updateCurrentPanel();
         }

         if (state.userid &&
             (this.currentPanelId === "view" ||
              this.currentPanelId === "create" ||
              this.currentPanelId === "update"))
         {
            this.currentUserId = state.userid;

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
      onSearchClick: function ConsoleUsers_onSearchClick(e, args)
      {
         var searchTermElem = Dom.get(this.id + "-search-text");
         var searchTerm = YAHOO.lang.trim(searchTermElem.value);

         // inform the user if the search term entered is too small
         if (searchTerm.replace(/\*/g, "").length < this.options.minSearchTermLength)
         {
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this._msg("message.minimum-length", this.options.minSearchTermLength)
            });
            return;
         }

         this.refreshUIState({"search": searchTerm});
      },

      /**
       * Upload Users button click event handler
       *
       * @method onUploadUsersClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onUploadUsersClick: function ConsoleUsers_onUploadUsersClick(e, args)
      {
         // Force the use of the HTML (rather than Flash) uploader because there are issues with the
         // Flash uploader in these circumstances when Sharepoint is being used. The Flash uploader
         // picks up the wrong JSESSIONID cookie which causes the upload to fail.
         if (!this.fileUpload)
         {
            this.fileUpload = Alfresco.util.ComponentManager.findFirst("Alfresco.HtmlUpload")
         }

         // Show uploader for single file select - override the upload URL to use appropriate upload service
         var uploadConfig =
         {
            uploadURL: "api/people/upload.html",
            mode: this.fileUpload.MODE_SINGLE_UPLOAD,
            onFileUploadComplete:
            {
               fn: this.onUsersUploadComplete,
               scope: this
            }
         };

         this.fileUpload.show(uploadConfig);

         // Make sure the "use Flash" tip is hidden just in case Flash is enabled...
         var singleUploadTip = Dom.get(this.fileUpload.id + "-singleUploadTip-span");
         Dom.addClass(singleUploadTip, "hidden");
         Event.preventDefault(e);
      },

      /**
       * Users Upload complete event handler
       *
       * @method onUsersUploadComplete
       * @param complete {object} Object literal containing details of successful upload
       */
      onUsersUploadComplete: function ConsoleUsers_onUsersUploadComplete(complete)
      {
         this.csvResults = complete;
         this.refreshUIState({"panel": "csvresults"});
      },

      /**
       * New User button click event handler
       *
       * @method onNewUserClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onNewUserClick: function ConsoleUsers_onNewUserClick(e, args)
      {
         this.refreshUIState({"panel": "create"});
      },

      /**
       * Edit User button click event handler
       *
       * @method onEditUserClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onEditUserClick: function ConsoleUsers_onEditUserClick(e, args)
      {
         this.refreshUIState({"panel": "update"});
      },

      /**
       * View User event handler
       *
       * @method onViewUserClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onViewUserClick: function ConsoleUsers_onViewUserClick(e, args)
      {
         var userid = args[1].username;
         this.refreshUIState({"panel": "view", "userid": userid});
      },

      /**
       * Go back button click event handler
       *
       * @method onGoBackClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onGoBackClick: function ConsoleUsers_onGoBackClick(e, args)
      {
         this.refreshUIState({"panel": "search"});
      },

      /**
       * Delete User button click event handler
       *
       * @method onDeleteUserClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onDeleteUserClick: function ConsoleUsers_onDeleteUserClick(e, args)
      {
         this.popups.deleteDialog.show();
      },

      /**
       * Fired when the admin confirms that they want to delete a User.
       *
       * @method onDeleteUserOK
       * @param e {object} DomEvent
       */
      onDeleteUserOK: function ConsoleUsers_onDeleteUserOK(e)
      {
         Alfresco.util.Ajax.request(
         {
            method: Alfresco.util.Ajax.DELETE,
            url: Alfresco.constants.PROXY_URI + "api/people/" + encodeURIComponent(this.currentUserId),
            successCallback:
            {
               fn: this.onDeletedUser,
               scope: this
            },
            failureMessage: this._msg("panel.delete.fail")
         });
      },

      /**
       * Fired on successful deletion of a user.
       *
       * @method onDeletedUser
       * @param e {object} DomEvent
       */
      onDeletedUser: function ConsoleUsers_onDeletedUser(e)
      {
         // return to the search screen - we can no longer view the user details
         this.popups.deleteDialog.hide();
         Alfresco.util.PopupManager.displayMessage(
         {
            text: this._msg("message.delete-success")
         });
         this.refreshUIState({"panel": "search"});
      },

      /**
       * Fired when the admin cancels the operation to delete a User.
       *
       * @method onDeleteUserCancel
       * @param e {object} DomEvent
       */
      onDeleteUserCancel: function ConsoleUsers_onDeleteUserCancel(e)
      {
         this.popups.deleteDialog.hide();
      },

      /**
       * Fired when the Create User OK button is clicked.
       *
       * @method onCreateUserOKClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onCreateUserOKClick: function ConsoleUsers_onCreateUserOKClick(e, args)
      {
         var form = this._getCurrentPanel()._form;
         if (form.validate())
         {
            var handler = function(res)
            {
               window.scrollTo(0, 0);
               Alfresco.util.PopupManager.displayMessage(
               {
                  text: this._msg("message.create-success")
               });
               this.refreshUIState({"panel": "search"});
            };
            this._createUser(handler);
         }
         else
         {
            form._setAllFieldsAsVisited();
         }
      },

      /**
       * Fired when the Create User and Create Another button is clicked.
       *
       * @method onCreateUserAnotherClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onCreateUserAnotherClick: function ConsoleUsers_onCreateUserAnotherClick(e, args)
      {
         var form = this._getCurrentPanel()._form;
         if (form.validate())
         {
            var me = this;
            var handler = function(res)
            {
               window.scrollTo(0, 0);
               Alfresco.util.PopupManager.displayMessage(
               {
                  text: me._msg("message.create-success")
               });
               // clear fields
               this._getCurrentPanel().clear();
               Dom.get(me.id + "-create-firstname").focus();
            };
            this._createUser(handler);
         }
         else
         {
            form._setAllFieldsAsVisited();
         }
      },

      /**
       * Fired when the Create User Cancel button is clicked.
       *
       * @method onCreateUserCancelClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onCreateUserCancelClick: function ConsoleUsers_onCreateUserCancelClick(e, args)
      {
         this.refreshUIState({"panel": "search"});
      },

      /**
       * Fired when the Update User OK button is clicked.
       *
       * @method onUpdateUserOKClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onUpdateUserOKClick: function ConsoleUsers_onUpdateUserOKClick(e, args)
      {
         var form = this._getCurrentPanel()._form;
         if (form.validate())
         {
            var me = this;
            var handler = function(res)
            {
               window.scrollTo(0, 0);
               Alfresco.util.PopupManager.displayMessage(
               {
                  text: me._msg("message.update-success")
               });
               me.refreshUIState({"panel": "view"});
            };
            this._updateUser(handler);
         }
         else
         {
            form._setAllFieldsAsVisited();
         }
      },

      /**
       * Fired when the Update User Cancel button is clicked.
       *
       * @method onUpdateUserCancelClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onUpdateUserCancelClick: function ConsoleUsers_onUpdateUserCancelClick(e, args)
      {
         this.refreshUIState({"panel": "view"});
      },

      /**
       * Fired when the Use Default button is clicked to clear user photo.
       *
       * @method onUpdateUserClearPhotoClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onUpdateUserClearPhotoClick: function ConsoleUsers_onUpdateUserClearPhotoClick(e, args)
      {
         Dom.get(this.id + "-update-photoimg").src = Alfresco.constants.URL_RESCONTEXT + "components/images/no-user-photo-64.png";
         this._getCurrentPanel().setPhotoReset();
      },

      /**
       * Encode state object into a packed string for use as url history value.
       * Override base class.
       *
       * @method encodeHistoryState
       * @param obj {object} state object
       * @private
       */
      encodeHistoryState: function ConsoleUsers_encodeHistoryState(obj)
      {
         // wrap up current state values
         var stateObj = {};
         if (this.currentPanelId !== "")
         {
            stateObj.panel = this.currentPanelId;
         }
         if (this.currentUserId !== "")
         {
            stateObj.userid = this.currentUserId;
         }
         if (this.searchTerm !== undefined)
         {
            stateObj.search = this.searchTerm;
         }

         // convert to encoded url history state - overwriting with any supplied values
         var state = "";
         if (obj.panel || stateObj.panel)
         {
            state += "panel=" + encodeURIComponent(obj.panel ? obj.panel : stateObj.panel);
         }
         if (obj.userid || stateObj.userid)
         {
            if (state.length !== 0)
            {
               state += "&";
            }
            state += "userid=" + encodeURIComponent(obj.userid ? obj.userid : stateObj.userid);
         }
         if (obj.search !== undefined || stateObj.search !== undefined)
         {
            if (state.length !== 0)
            {
               state += "&";
            }
            state += "search=" + encodeURIComponent(obj.search !== undefined ? obj.search : stateObj.search);
         }
         return state;
      },

      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Create a user - returning true on success, false on any error.
       *
       * @method _createUser
       * @param handler {function} Handler function to be called on successful creation
       * @private
       */
      _createUser: function ConsoleUsers__createUser(handler)
      {
         // TODO: respect minimum field length for username/password

         var me = this;
         var fnGetter = function(id)
         {
            return YAHOO.lang.trim(Dom.get(me.id + id).value);
         };

         // verify password against second field
         var password = fnGetter("-create-password");
         var verifypw = fnGetter("-create-verifypassword");
         if (password !== verifypw)
         {
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this._msg("message.password-validate-failure")
            });
            return;
         }

         // gather up the data for our JSON PUT request
         var username = fnGetter("-create-username");
         var quota = this._calculateQuota(me.id + "-create");

         // gather the selected groups from the panel
         var groups = this._getCurrentPanel().getGroups();

         var personObj =
         {
            userName: username,
            password: password,
            firstName: fnGetter("-create-firstname"),
            lastName: fnGetter("-create-lastname"),
            email: fnGetter("-create-email"),
            disableAccount: Dom.get(me.id + "-create-disableaccount").checked,
            quota: quota,
            groups: groups
         };

         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "api/people",
            method: Alfresco.util.Ajax.POST,
            dataObj: personObj,
            requestContentType: Alfresco.util.Ajax.JSON,
            successCallback:
            {
               fn: handler,
               scope: this
            },
            failureCallback:
            {
               fn: function(res)
               {
                  if (res.serverResponse.status === 409)
                  {
                     // username already exists
                     Alfresco.util.PopupManager.displayPrompt(
                     {
                        title: this._msg("message.failure"),
                        text: this._msg("message.create-user-exists")
                     });
                  }
                  else
                  {
                     // generic error
                     var json = Alfresco.util.parseJSON(res.serverResponse.responseText);
                     Alfresco.util.PopupManager.displayPrompt(
                     {
                        title: this._msg("message.failure"),
                        text: this._msg("message.create-failure", json ? json.message : res.serverResponse.statusText)
                     });
                  }
               },
               scope: this
            }
         });
      },

      /**
       * Update a user - returning true on success, false on any error.
       *
       * @method _updateUser
       * @param handler {function} Handler function to be called on successful update
       * @private
       */
      _updateUser: function ConsoleUsers__updateUser(handler)
      {
         var me = this;

         var isCurrentUser = (this.currentUserId.toLowerCase() === Alfresco.constants.USERNAME.toLowerCase());

         var fnGetter = function(id)
         {
            return Dom.get(me.id + id).value;
         };

         var updateSuccess = function(res)
         {
            var completed = function(res)
            {
               if (YAHOO.lang.trim(fnGetter("-update-password")).length !== 0)
               {
                  var passwordObj =
                  {
                     newpw: YAHOO.lang.trim(fnGetter("-update-password"))
                  };
                  if (isCurrentUser == true)
                  {
                     passwordObj.oldpw = YAHOO.lang.trim(fnGetter("-update-old-password"));
                  }

                  // update the password for the user
                  Alfresco.util.Ajax.request(
                  {
                     url: Alfresco.constants.PROXY_URI + "api/person/changepassword/" + encodeURIComponent(me.currentUserId),
                     method: Alfresco.util.Ajax.POST,
                     dataObj: passwordObj,
                     requestContentType: Alfresco.util.Ajax.JSON,
                     successCallback:
                     {
                        fn: handler,
                        scope: me
                     },
                     failureMessage: me._msg("message.password-failure")
                  });
               }
               else
               {
                  handler.call();
               }
            };

            if (this._getCurrentPanel().getPhotoReset())
            {
               Alfresco.util.Ajax.request(
               {
                  url: Alfresco.constants.PROXY_URI + "slingshot/profile/resetavatar/" + encodeURIComponent(this.currentUserId),
                  method: Alfresco.util.Ajax.PUT,
                  requestContentType: Alfresco.util.Ajax.JSON,
                  successCallback:
                  {
                     fn: completed,
                     scope: this
                  },
                  failureCallback:
                  {
                     fn: function(res)
                     {
                        // generic error
                        Alfresco.util.PopupManager.displayPrompt(
                        {
                           title: this._msg("message.failure"),
                           text: this._msg("message.clear-photo-failure")
                        });
                        completed.call();
                     },
                     scope: this
                  }
               });
            }
            else
            {
               completed.call();
            }
         };

         // verify password against second field
         var oldPw = fnGetter("-update-old-password");
         var password = fnGetter("-update-password");
         var verifypw = fnGetter("-update-verifypassword");
         if (YAHOO.lang.trim(password).length !== 0)
         {
            if (isCurrentUser == true && (YAHOO.lang.trim(oldPw).length === 0))
            {
               Alfresco.util.PopupManager.displayMessage(
               {
                  text: this._msg("message.password-validate-oldpw")
               });
               return;
            }
            if (YAHOO.lang.trim(password).length < this.options.minPasswordLength)
            {
               Alfresco.util.PopupManager.displayMessage(
               {
                  text: this._msg("message.password-validate-length", this.options.minPasswordLength)
               });
               return;
            }
            if (password !== verifypw)
            {
               Alfresco.util.PopupManager.displayMessage(
               {
                  text: this._msg("message.password-validate-failure")
               });
               return;
            }
         }

         // gather up the data for our JSON PUT request
         var quota = this._calculateQuota(me.id + "-update");

         // gather the groups for addition and groups for removal from the panel
         var addGroups = this._getCurrentPanel().getAddedGroups();
         var removeGroups = this._getCurrentPanel().getRemovedGroups();

         var personObj =
         {
            firstName: fnGetter("-update-firstname"),
            lastName: fnGetter("-update-lastname"),
            email: fnGetter("-update-email"),
            disableAccount: Dom.get(me.id + "-update-disableaccount").checked,
            quota: quota,
            addGroups: addGroups,
            removeGroups: removeGroups
         };

         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "api/people/" + encodeURIComponent(this.currentUserId),
            method: Alfresco.util.Ajax.PUT,
            dataObj: personObj,
            requestContentType: Alfresco.util.Ajax.JSON,
            successCallback:
            {
               fn: updateSuccess,
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
                     text: this._msg("message.update-failure", json.message)
                  });
               },
               scope: this
            }
         });
      },

      /**
       * Return the quota value as input by the user - converted to bytes.
       *
       * @method _calculateQuota
       * @param idPrefix {string} ID prefix of the quota UI elements
       * @return the quota value as input by the user - converted to bytes
       * @private
       */
      _calculateQuota: function ConsoleUsers__calculateQuota(idPrefix)
      {
         var quota = -1;
         var quotaValue = Dom.get(idPrefix + "-quota").value;
         if (quotaValue.length !== 0)
         {
            // convert from giga/mega/kilo bytes
            try
            {
               quota = parseInt(quotaValue);
               if (quota >= 0)
               {
                  var quotaType = Dom.get(idPrefix + "-quotatype").value;
                  if (quotaType === "gb")
                  {
                     quota *= Alfresco.util.BYTES_GB;
                  }
                  else if (quotaType === "mb")
                  {
                     quota *= Alfresco.util.BYTES_MB;
                  }
                  else if (quotaType === "kb")
                  {
                     quota *= Alfresco.util.BYTES_KB;
                  }
               }
               else
               {
                  quota = -1;
               }
            }
            catch (e)
            {
               // ignore if we cannot parse quota field
            }
         }
         return quota;
      },

      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function ConsoleUsers__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.ConsoleUsers", Array.prototype.slice.call(arguments).slice(1));
      }
   });
})();