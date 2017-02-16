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
 * Pending component.
 * 
 * @namespace Alfresco
 * @class Alfresco.Pending
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;
   
   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $siteURL = Alfresco.util.siteURL;

   /**
    * Polyfill for ES6 String.prototype.includes() (IE compatibility)
    */
   if (!String.prototype.includes) {
      String.prototype.includes = function(search, start) {
         'use strict';

         if (typeof start !== 'number') {
            start = 0;
         }

         if (start + search.length > this.length) {
            return false;
         }
         else {
            return this.indexOf(search, start) !== -1;
         }
      };
   }

   /**
    * AbstractPendingBase constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.AbstractPendingBase} reference to this. Not to be instantiated directly. Use one of the implementing classes instead.
    * @constructor
    */
   Alfresco.AbstractPendingBase = function(htmlId, getRuntimeClassName, getPendingApiUrl, getResultsListName, 
     getActionButtonLabel, getActionButtonTitle, getActionButtonColumnWidth,
     getPersonData, getUpdatedResponse, performCustomCellRendering, getDate, performOnAction, addCustomColumnDefinitions)
   {
      Alfresco.AbstractPendingBase.superclass.constructor.call(this, getRuntimeClassName(), htmlId, ["button", "container", "datasource", "datatable", "json"]);
      
      /* Initialise prototype properties */
      this.actionButtons = {};
      this.searchTerm = "";
      this.isSearching = false;

      this.getRuntimeClassName = getRuntimeClassName;
      this.getPendingApiUrl = getPendingApiUrl;
      this.getResultsListName = getResultsListName;
      this.getActionButtonLabel = getActionButtonLabel;
      this.getActionButtonTitle = getActionButtonTitle;
      this.getActionButtonColumnWidth = getActionButtonColumnWidth;
      this.getPersonData = getPersonData;
      this.getUpdatedResponse = getUpdatedResponse;
      this.performCustomCellRendering = performCustomCellRendering;
      this.getDate = getDate;
      this.performOnAction = performOnAction;
      this.addCustomColumnDefinitions = addCustomColumnDefinitions;

      return this;
   };
   
   YAHOO.extend(Alfresco.AbstractPendingBase, Alfresco.component.Base,
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
          * Whether to set UI focus to this component or not
          * 
          * @property setFocus
          * @type boolean
          * @default false
          */
         setFocus: false
      },

      /**
       * Object container for storing YUI button instances, indexed by username.
       * 
       * @property userSelectButtons
       * @type object
       */
      actionButtons: null,
      
      /**
       * Current search term, obtained from form input field.
       * 
       * @property searchTerm
       * @type string
       */
      searchTerm: null,

      /**
       * Keeps track if this component is searching or not
       *
       * @property isSearching
       * @type Boolean
       */
      isSearching: null,

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function AbstractPendingBase_onReady()
      {  
         var me = this;

         // register the "enter" event on the search text field
         var searchText = Dom.get(this.id + "-search-text");

         // declare variable to keep JSLint and YUI Compressor happy
         var enterListener = new YAHOO.util.KeyListener(searchText,
         {
            keys: YAHOO.util.KeyListener.KEY.ENTER
         },
         {
            fn: function()
            {
               me.onSearchClick();
            },
            scope: this,
            correctScope: true
         }, "keydown");

         enterListener.enable();

         // Set initial focus?
         if (this.options.setFocus)
         {
            searchText.focus();
         }

         // Search button
         this.widgets.searchButton = Alfresco.util.createYUIButton(this, "search-button", this.onSearchClick);

         this.widgets.dataSource = new YAHOO.util.DataSource(me.getPendingApiUrl(this.options.siteId),
         {
            responseType: YAHOO.util.DataSource.TYPE_JSON,
            responseSchema:
            {
                resultsList: me.getResultsListName()
            }
         });

         this.widgets.dataSource.doBeforeParseData = function Pending_doBeforeParseData(oRequest, oFullResponse)
         {
            var updatedResponse = oFullResponse;
               
            if (oFullResponse)
            {
               var items = [];
               
               // Determine list of sites to show
               if (me.searchTerm.length > 0)
               {
                  // Filter the results for the search term
                  var lowerCaseTerm = me.searchTerm.toLowerCase();
                  var lowerCaseTermRegStr=lowerCaseTerm;
                  var regRepl=["\/","\\\\","\\?","\\+","\\$","\\.","\\^","\\(","\\)"];

                  for (var i = 0, j = regRepl.length; i < j; i++)
                  {
                     lowerCaseTermRegStr=lowerCaseTermRegStr.replace(new RegExp(regRepl[i],'g'),regRepl[i]);
                  }

                  var ignoreIndex=lowerCaseTermRegStr.search("\\*")==0;

                  if (ignoreIndex)
                  {
                      lowerCaseTermRegStr=lowerCaseTermRegStr.substring(1);
                  }

                  lowerCaseTermRegStr=lowerCaseTermRegStr.replace(new RegExp("\\*",'g'),".*");

                  var regTerm=new RegExp(lowerCaseTermRegStr, "i");
                  var personData, userName, firstName, lastName, fullName;
                  var userNameRegMatch, firstNameRegMatch, lastNameRegMatch, fullNameRegMatch;

                  for (var i = 0, j = oFullResponse[me.getResultsListName()].length; i < j; i++)
                  {
                     personData = me.getPersonData(oFullResponse[me.getResultsListName()][i]);

                     userName = (personData.userName || "").toLowerCase();
                     firstName = (personData.firstName || "").toLowerCase();
                     lastName = (personData.lastName || "").toLowerCase();
                     fullName = (firstName + " " + lastName).toLowerCase();
                     
                     userNameRegMatch = regTerm.exec(userName);
                     firstNameRegMatch = regTerm.exec(firstName);
                     lastNameRegMatch = regTerm.exec(lastName);
                     fullNameRegMatch = regTerm.exec(fullName);

                     // Determine if person matches search term
                     if (((userNameRegMatch!=null && userNameRegMatch[0] != "") ||
                         (firstNameRegMatch!=null && firstNameRegMatch[0] != "") ||
                         (lastNameRegMatch!=null && lastNameRegMatch[0] != "") ||
                         (userNameRegMatch=null && fullNameRegMatch[0] != "")) &&
                         (personData.userName !== Alfresco.constants.USERNAME))
                     {
                        // Add user to list
                        if (ignoreIndex)
                        {
                           items.push(oFullResponse[me.getResultsListName()][i]);
                        }
                        else if ((userNameRegMatch!=null && userNameRegMatch.index == 0) ||
                                 (firstNameRegMatch!=null && firstNameRegMatch.index == 0) ||
                                 (lastNameRegMatch!=null && lastNameRegMatch.index == 0) ||
                                 (userNameRegMatch=null && fullNameRegMatch.index == 0))
                        {
                           items.push(oFullResponse[me.getResultsListName()][i]);
                        }
                     }
                  }
               }
               else
               {
            	  for (var i = 0; i < oFullResponse[me.getResultsListName()].length; i++)
            	  {
                     var personData = me.getPersonData(oFullResponse[me.getResultsListName()][i]);

                     if (personData.userName !== Alfresco.constants.USERNAME) {
                        items.push(oFullResponse[me.getResultsListName()][i]);
                     }
            	  }
               }

               // Sort the pending list by the initiator's name
               items.sort(function (data1, data2)
               {
                  var name1 = me.getPersonData(data1).firstName + me.getPersonData(data1).lastName,
                     name2 = me.getPersonData(data2).firstName + me.getPersonData(data2).lastName;

                  return (name1 > name2) ? 1 : (name1 < name2) ? -1 : 0;
               });

               // we need to wrap the array inside a JSON object so the DataTable is happy
               updatedResponse = me.getUpdatedResponse(items);
            }
            
            return updatedResponse;
         };
         
         // Setup the DataTable
         this._setupDataTable();

         // Set initial focus?
         if (this.options.setFocus)
         {
            Dom.get(this.id + "-search-text").focus();
         }

         /*
          * Enter key listener function needs to be enclosed due to having "window" scope
          *
          * @method: onKeyEnter
          * @param id
          * @param keyEvent {object} The key event details
          */
         var onKeyEnter = function Pending_onKeyEnter(id, keyEvent)
         {
            me.onSearchClick.call(me, keyEvent, null);
            return false;
         };

         // Enter key listener for button
         var buttonListener = new YAHOO.util.KeyListener(this.id + "-search-button",
         {
            keys: YAHOO.util.KeyListener.KEY.ENTER
         }, onKeyEnter, "keydown");
         buttonListener.enable();
      },
      
      /**
       * Setup the YUI DataTable with custom renderers.
       *
       * @method _setupDataTable
       * @private
       */
      _setupDataTable: function Pending__setupDataTable()
      {
         /**
          * DataTable Cell Renderers
          *
          * Each cell has a custom renderer defined as a custom function. See YUI documentation for details.
          * These MUST be inline in order to have access to the Alfresco.Pending class (via the "me" variable).
          */
         var me = this;

         /**
          * User avatar custom datacell formatter
          *
          * @method renderCellAvatar
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellAvatar = function Pending_renderCellAvatar(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            var avatarUrl = Alfresco.constants.URL_RESCONTEXT + "components/images/no-user-photo-64.png",
               person = me.getPersonData(oRecord.getData());

            if (person.avatar !== undefined)
            {
               avatarUrl = Alfresco.constants.PROXY_URI + person.avatar + "?c=queue&ph=true";
            }

            elCell.innerHTML = '<img class="avatar" src="' + avatarUrl + '" alt="avatar" />';
         };

         var generateUserNameLink = function(person)
         {
            var name = person.userName,
               firstName = person.firstName,
               lastName = person.lastName,
               linkExtra = "";
            
            if ((firstName !== undefined) || (lastName !== undefined))
            {
               name = firstName ? firstName + " " : "";
               name += lastName ? lastName : "";
               linkExtra =  ' <span class="lighter">(' + $html(person.userName) + ')</span>';
            }

            return Alfresco.util.userProfileLink(person.userName, name) + linkExtra;
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
         var renderCellDescription = function Pending_renderCellDescription(elCell, oRecord, oColumn, oData)
         {
            // Currently rendering all results the same way
            var person = me.getPersonData(oRecord.getData()),
               sentDate = Alfresco.util.formatDate(me.getDate(oRecord));

            var desc = '<div class="to-invitee"><span class="attr-label">' + me.msg('info.from') + ': </span>';

            desc += '<span class="attr-value">' + generateUserNameLink(person) + '</span>';
            desc += '</div>';
            desc += '<div>';
            desc += '<span class="attr-label">' + me.msg('info.sent') + ': </span>';
            desc += '<span class="attr-value">' + $html(sentDate) + '</span>';
            desc += me.performCustomCellRendering(me, oRecord);
            desc += '</div>';

            elCell.innerHTML = desc;
         };
         
         /**
          * Action button datacell formatter
          *
          * @method renderCellActionButton
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellActionButton = function Pending_renderCellActionButton(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            
            var userName = me.getPersonData(oRecord.getData()).userName;
            var containerId = me.id + '-action-' + userName;
            elCell.innerHTML = '<span id="' + containerId + '"></span>';

            // create action button
            var buttonLabel = me.getActionButtonLabel(oRecord);
            
            var button = new YAHOO.widget.Button(
            {
               type: "button",
               label: buttonLabel,
               title: me.getActionButtonTitle(),
               name: me.id + "-selectbutton-" + userName,
               container: containerId,
               onclick:
               {
                  fn: me.onActionClick,
                  obj: oRecord,
                  scope: me
               }
            });
            me.actionButtons[userName] = button;
         };

         // DataTable column defintions
         var columnDefinitions =
         [
            { key: "avatar", label: "Avatar", sortable: false, formatter: renderCellAvatar, width: 70 },
            { key: "person", label: "Description", sortable: false, formatter: renderCellDescription },
            { key: "actions", label: "Actions", sortable: false, formatter: renderCellActionButton, width: this.getActionButtonColumnWidth() }
         ];

         this.addCustomColumnDefinitions(columnDefinitions);

         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-results", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: 32,
            initialLoad: false,
            MSG_EMPTY: this.msg("message.empty")
         });
         
         // Enable row highlighting
         this.widgets.dataTable.subscribe("rowMouseoverEvent", this.widgets.dataTable.onEventHighlightRow);
         this.widgets.dataTable.subscribe("rowMouseoutEvent", this.widgets.dataTable.onEventUnhighlightRow);
         
         // trigger the search
         if (this.options.minSearchTermLength <= 0)
         {
            this._performSearch("");
         }
      },
      
      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * Action button click handler
       *
       * @method onActionClick
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onActionClick: function Pending_onActionClick(event, p_obj)
      {
         this.performOnAction(p_obj);
      },

      /**
       * Search button click event handler
       *
       * @method onSearchClick
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onSearchClick: function Pending_onSearchClick(e, p_obj)
      {
         var searchTerm = YAHOO.lang.trim(Dom.get(this.id + "-search-text").value);
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
       * PRIVATE FUNCTIONS
       */
      
      /**
       * Resets the YUI DataTable errors to our custom messages
       * NOTE: Scope could be YAHOO.widget.DataTable, so can't use "this"
       *
       * @method _setDefaultDataTableErrors
       * @param dataTable {object} Instance of the DataTable
       */
      _setDefaultDataTableErrors: function Pending__setDefaultDataTableErrors(dataTable)
      {
         var msg = Alfresco.util.message;
         dataTable.set("MSG_EMPTY", msg("message.empty", this.getRuntimeClassName()));
         dataTable.set("MSG_ERROR", msg("message.error", this.getRuntimeClassName()));
      },
      
      /**
       * Updates people list by calling data webscript
       *
       * @method _performSearch
       * @param searchTerm {string} Search term from input field
       */
      _performSearch: function Pending__performSearch(searchTerm)
      {
         if (!this.isSearching)
         {
            this.isSearching = true;

            // Reset the custom error messages
            this._setDefaultDataTableErrors(this.widgets.dataTable);

            // Empty results table
            var recordCount = this.widgets.dataTable.getRecordSet().getLength();
            if (recordCount > 0)
            {
               this.widgets.dataTable.deleteRows(0, recordCount);
            }

            var successHandler = function Pending__pS_successHandler(sRequest, oResponse, oPayload)
            {
               this._enableSearchUI();
               this._setDefaultDataTableErrors(this.widgets.dataTable);
               if (oResponse.results.length > 0)
               {
                  this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
               }
            };

            var failureHandler = function Pending__pS_failureHandler(sRequest, oResponse)
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
            YAHOO.lang.later(2000, this, function()
            {
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
            });
         }
      },

      /**
       * Enable search button, hide the pending wait message and set the panel as not searching.
       *
       * @method _enableSearchUI
       * @private
       */
      _enableSearchUI: function Pending__enableSearchUI()
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
       * Build URI parameter string for doclist JSON data webscript
       *
       * @method _buildSearchParams
       * @param path {string} Path to query
       */
      _buildSearchParams: function Pending__buildSearchParams(searchTerm)
      {
         return YAHOO.lang.substitute("siteShortName={siteShortName}",
         {
            siteShortName: encodeURIComponent(this.options.siteId)
         });
      }
   });

   /**
    * SentInvites constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.SentInvites} The new SentInvites instance
    * @constructor
    */
   Alfresco.SentInvites = function(htmlId)
   {
      var that = this;

      Alfresco.SentInvites.superclass.constructor.call(this, htmlId,

         function SentInvites_getRuntimeClassName() {
            return "Alfresco.SentInvites";
         },

         function SentInvites_getPendingApiUrl(siteId) {
            return Alfresco.constants.PROXY_URI + "api/invites?";
         },

         function SentInvites_getResultsListName() {
            return "invites";
         },

         function SentInvites_getActionButtonLabel(record) {
            if (record.getData("invitationStatus") == 'pending') {
               return that.msg("button.cancel");
            }
            else {
               return that.msg("button.clear");
            }
         },

         function SentInvites_getActionButtonTitle() {
        	 return "";
         },

         function SentInvites_getActionButtonColumnWidth() {
        	 return 100;
         },

         function SentInvites_getPersonData(responseDataItem) {
            return responseDataItem.invitee;
         },

         function SentInvites_getUpdatedResponse(items) {
            return {
               "invites": items
            }
         },

         function SentInvites_performCustomCellRendering(me, record) {
             var role = me.msg("role." + record.getData("role"));
             var desc = "";

             desc += '<span class="separator"> | </span>';
             desc += '<span class="attr-label">' + me.msg('info.role') + ': </span>';
             desc += '<span class="attr-value">' + $html(role) + '</span>';

             return desc;
         },

         function SentInvites_getDate(record) {
            return record.getData('sentInviteDate');
         },

         function SentInvites_performOnAction(record) {
            that.cancelInvite(record);
         },

         function SentInvites_addCustomColumnDefinitions(columnDefinitions) {
        	 // Do nothing in this particular implementation.
         }
      );


      // Extra implementation-specific methods:

      /**
       * Public function to clear the results DataTable
       */
      this.clearResults = function SentInvites_clearResults()
      {
         // Clear results DataTable
         if (that.widgets.dataTable)
         {
            var recordCount = that.widgets.dataTable.getRecordSet().getLength();

            if (recordCount > 0)
            {
               that.widgets.dataTable.deleteRows(0, recordCount);
            }
         }

         Dom.get(that.id + "-search-text").value = "";
      }

      /**
       * Cancel an invitation.
       */
      this.cancelInvite = function SentInvites_cancelInvite(record)
      {
         // disable the button
         that.actionButtons[record.getData('invitee').userName].set('disabled', true);

         // show a wait message
         that.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: that.msg("message.removing"),
            spanClass: "wait",
            displayTime: 0
         });

         // ajax request success handler
         var success = function SentInvites_cancelInvite_success(response)
         {
            // hide the wait message
            that.widgets.feedbackMessage.hide();

            // remove the record from the list
            var index = that.widgets.dataTable.getRecordIndex(record);

            if (index !== null)
            {
               that.widgets.dataTable.deleteRow(index);
            }
         };

         // request failure handler
         var failure = function SentInvites_cancelInvite_failure(response)
         {
            // remove the message
            that.widgets.feedbackMessage.hide();
            that.actionButtons[record.getData('invitee').userName].set('disabled', true);
         };

         // get the url to call
         var url = Alfresco.constants.PROXY_URI + "api/sites/" + this.options.siteId + "/invitations/" + record.getData('inviteId');

         // execute ajax request
         Alfresco.util.Ajax.request(
         {
            url: url,
            method: "DELETE",
            responseContentType : "application/json",
            successMessage: that.msg("message.cancel.success"),
            successCallback:
            {
               fn: success,
               scope: that
            },
            failureMessage: that.msg("message.cancel.failure"),
            failureCallback:
            {
               fn: failure,
               scope: that
            }
         });
      }

      return this;
   };

   YAHOO.extend(Alfresco.SentInvites, Alfresco.AbstractPendingBase, {});


   /**
    * PendingRequests constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.PendingRequests} The new PendingRequests instance
    * @constructor
    */
   Alfresco.PendingRequests = function(htmlId)
   {
	  var that = this;

      Alfresco.PendingRequests.superclass.constructor.call(this, htmlId, 

         function PendingRequests_getRuntimeClassName() {
            return "Alfresco.PendingRequests";
         },

         function PendingRequests_getPendingApiUrl(siteId) {
            return Alfresco.constants.PROXY_URI +
               YAHOO.lang.substitute("api/task-instances?authority={authority}&property=imwf:resourceName/{siteId}&properties={properties}&exclude={exclude}",
               {
                  authority: encodeURIComponent(Alfresco.constants.USERNAME),
                  siteId: siteId,
                  properties: ["bpm_priority", "bpm_status", "bpm_dueDate", "bpm_description", "bpm_id"].join(","),
                  exclude: (this.options.hiddenTaskTypes || ["wcmwf:*"]).join(",")
               })
         },

         function PendingRequests_getResultsListName() {
            return "data";
         },

         function PendingRequests_getActionButtonLabel(record) {
            return that.msg("button.view.label");
         },

         function PendingRequests_getActionButtonTitle() {
            return that.msg("button.view.title");
         },

         function SentInvites_getActionButtonColumnWidth() {
            return 70;
         },

         function PendingRequests_getPersonData(responseDataItem) {
            return responseDataItem.workflowInstance.initiator;
         },

         function PendingRequests_getUpdatedResponse(items) {
            return {
               "data": items
            }
         },

         function PendingRequests_performCustomCellRendering(me, record) {
            return "";
         },

         function PendingRequests_getDate(record) {
            return record.getData('workflowInstance').startDate;
         },

         function PendingRequests_performOnAction(record) {
            var joinRequestTaskUrl = '';

            if (record.getData().isEditable)
            {
               joinRequestTaskUrl = $siteURL('task-edit?taskId=' + record.getData().id);
            }
            else
            {
               joinRequestTaskUrl = $siteURL('task-details?taskId=' + record.getData().id);
            }

            window.open(joinRequestTaskUrl, '_self');
         },

         function SentInvites_addCustomColumnDefinitions(columnDefinitions) {
        	 // Add Approve button.
        	 columnDefinitions.push({
        		 key: "approve", label: "Approve", sortable: false, formatter: that.renderApproveButton, width: 111
             });
         }
      );


      // Extra implementation-specific methods:

      /**
       * Render "Approve" button
       */
      this.renderApproveButton = function Pending_renderApproveButton(elCell, oRecord, oColumn, oData)
      {
         Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

         var userName = that.getPersonData(oRecord.getData()).userName;
         var taskId = oRecord.getData().id;
         var containerId = that.id + '-action-' + userName + '_approve';

         elCell.innerHTML = '<span id="' + containerId + '"></span>';

         var button = new YAHOO.widget.Button(
         {
            type: "button",
            label: that.msg("button.approve.label"),
            name: that.id + "-selectbutton-" + userName + '_approve',
            container: containerId,
            onclick:
            {
               fn: that.onApproveButtonClick,
               obj: oRecord,
               scope: that
            }
         });
         that.actionButtons[userName + '_approve'] = button;
      };

      /**
       * "Approve" button on-click event handler
       */
      this.onApproveButtonClick = function (event, p_obj) {
          var userName = that.getPersonData(p_obj.getData()).userName;
          var taskId = p_obj.getData().id;

          // disable the button
          that.actionButtons[userName + '_approve'].set('disabled', true);

          // show a wait message
          that.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
          {
              text: that.msg("message.approve.removing"),
              spanClass: "wait",
              displayTime: 0
          });

          // ajax request success handler
          var success = function approve_success(response)
          {
              // hide the wait message
              that.widgets.feedbackMessage.hide();

              // remove the record from the list
              var index = that.widgets.dataTable.getRecordIndex(p_obj);

              if (index !== null)
              {
                  that.widgets.dataTable.deleteRow(index);
              }
          };

          // request failure handler
          var failure = function approve_failure(response)
          {
              // remove the message
              that.widgets.feedbackMessage.hide();
              that.actionButtons[userName + '_approve'].set('disabled', true);
          };

          // get the url to call
          var url = Alfresco.constants.PROXY_URI + "api/task/" + encodeURIComponent(taskId) + "/formprocessor";

          // execute ajax request
          Alfresco.util.Ajax.jsonRequest(
          {
              url: url,
              method: "POST",
              dataObj:
              {
                  prop_bpm_comment: "",
                  prop_imwf_reviewOutcome: "approve",
                  prop_transitions: "Next"
              },
              responseContentType : "application/json",
              successMessage: that.msg("message.approve.success"),
              successCallback:
              {
                  fn: success,
                  scope: that
              },
              failureMessage: that.msg("message.approve.failure"),
              failureCallback:
              {
                  fn: failure,
                  scope: that
              }
          });
       };

      return this;
   };

   YAHOO.extend(Alfresco.PendingRequests, Alfresco.AbstractPendingBase, {});

})();
