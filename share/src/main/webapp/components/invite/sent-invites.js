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
 * SentInvites component.
 * 
 * @namespace Alfresco
 * @class Alfresco.SentInvites
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
   var $html = Alfresco.util.encodeHTML;

   /**
    * SentInvites constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.SentInvites} The new SentInvites instance
    * @constructor
    */
   Alfresco.SentInvites = function(htmlId)
   {
      Alfresco.SentInvites.superclass.constructor.call(this, "Alfresco.SentInvites", htmlId, ["button", "container", "datasource", "datatable", "json"]);
      
      /* Initialise prototype properties */
      this.actionButtons = {};
      this.searchTerm = "";
      this.isSearching = false;
      
      return this;
   };
   
   YAHOO.extend(Alfresco.SentInvites, Alfresco.component.Base,
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
      onReady: function SentInvites_onReady()
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

         // DataSource definition  
         var inviteeSearchUrl = Alfresco.constants.PROXY_URI + "api/invites?";
         this.widgets.dataSource = new YAHOO.util.DataSource(inviteeSearchUrl,
         {
            responseType: YAHOO.util.DataSource.TYPE_JSON,
            responseSchema:
            {
                resultsList: "invites"
            }
         });

         this.widgets.dataSource.doBeforeParseData = function SentInvites_doBeforeParseData(oRequest, oFullResponse)
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
                  for (var i = 0, j = oFullResponse.invites.length; i < j; i++)
                  {
                     personData = oFullResponse.invites[i].invitee;
                     userName = (personData.userName || "").toLowerCase();
                     firstName = (personData.firstName || "").toLowerCase();
                     lastName = (personData.lastName || "").toLowerCase();
                     fullName = (firstName + " " + lastName).toLowerCase();
                     
                     userNameRegMatch = regTerm.exec(userName);
                     firstNameRegMatch = regTerm.exec(firstName);
                     lastNameRegMatch = regTerm.exec(lastName);
                     fullNameRegMatch = regTerm.exec(fullName);
                     // Determine if person matches search term
                     if ((userNameRegMatch!=null && userNameRegMatch[0] != "") ||
                         (firstNameRegMatch!=null && firstNameRegMatch[0] != "") ||
                         (lastNameRegMatch!=null && lastNameRegMatch[0] != "") ||
                         (userNameRegMatch=null && fullNameRegMatch[0] != ""))
                     {
                        // Add user to list
                        if (ignoreIndex)
                        {
                           items.push(oFullResponse.invites[i]);
                        }
                        else if ((userNameRegMatch!=null && userNameRegMatch.index == 0) ||
                                 (firstNameRegMatch!=null && firstNameRegMatch.index == 0) ||
                                 (lastNameRegMatch!=null && lastNameRegMatch.index == 0) ||
                                 (userNameRegMatch=null && fullNameRegMatch.index == 0))
                        {
                           items.push(oFullResponse.invites[i]);
                        }
                     }
                  }
               }
               else
               {
                  items = oFullResponse.invites;
               }

               // Sort the invites list by the invitee's name
               items.sort(function (invite1, invite2)
               {
                  var name1 = invite1.invitee.firstName + invite1.invitee.lastName,
                     name2 = invite2.invitee.firstName + invite2.invitee.lastName;
                  return (name1 > name2) ? 1 : (name1 < name2) ? -1 : 0;
               });

               // we need to wrap the array inside a JSON object so the DataTable is happy
               updatedResponse =
               {
                  "invites": items
               };
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
         var onKeyEnter = function SentInvites_onKeyEnter(id, keyEvent)
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
      _setupDataTable: function SentInvites__setupDataTable()
      {
         /**
          * DataTable Cell Renderers
          *
          * Each cell has a custom renderer defined as a custom function. See YUI documentation for details.
          * These MUST be inline in order to have access to the Alfresco.SentInvites class (via the "me" variable).
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
         var renderCellAvatar = function SentInvites_renderCellAvatar(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            var avatarUrl = Alfresco.constants.URL_RESCONTEXT + "components/images/no-user-photo-64.png",
               invitee = oRecord.getData("invitee");
            if (invitee.avatar !== undefined)
            {
               avatarUrl = Alfresco.constants.PROXY_URI + invitee.avatar + "?c=queue&ph=true";
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
         var renderCellDescription = function SentInvites_renderCellDescription(elCell, oRecord, oColumn, oData)
         {
            // Currently rendering all results the same way
            var invitee = oRecord.getData("invitee"),
               sentDate = Alfresco.util.formatDate(oRecord.getData('sentInviteDate')),
               role = me.msg("role." + oRecord.getData("role"));

            var desc = '<div class="to-invitee"><span class="attr-label">' + me.msg('info.to') + ': </span>';
            desc += '<span class="attr-value">' + generateUserNameLink(invitee) + '</span>';
            desc += '</div>';
            desc += '<div>';
            desc += '<span class="attr-label">' + me.msg('info.sent') + ': </span>';
            desc += '<span class="attr-value">' + $html(sentDate) + '</span>';
            desc += '<span class="separator"> | </span>';
            desc += '<span class="attr-label">' + me.msg('info.role') + ': </span>';
            desc += '<span class="attr-value">' + $html(role) + '</span>';
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
         var renderCellActionButton = function SentInvites_renderCellActionButton(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            
            var userName = oRecord.getData("invitee").userName;
            elCell.innerHTML = '<span id="' + me.id + '-action-' + userName + '"></span>';

            // create button
            var buttonLabel;
            if (oRecord.getData("invitationStatus") == 'pending')
            {
               buttonLabel = me.msg("button.cancel");
            }
            else
            {
               buttonLabel = me.msg("button.clear");
            }
            
            var button = new YAHOO.widget.Button(
            {
               type: "button",
               label: buttonLabel,
               name: me.id + "-selectbutton-" + userName,
               container: me.id + '-action-' + userName,
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
            { key: "actions", label: "Actions", sortable: false, formatter: renderCellActionButton, width: 100 }
         ];

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
       * Public function to clear the results DataTable
       */
      clearResults: function SentInvites_clearResults()
      {
         // Clear results DataTable
         if (this.widgets.dataTable)
         {
            var recordCount = this.widgets.dataTable.getRecordSet().getLength();
            if (recordCount > 0)
            {
               this.widgets.dataTable.deleteRows(0, recordCount);
            }
         }
         Dom.get(this.id + "-search-text").value = "";
      },

      /**
       * Cancel an invitation.
       */
      cancelInvite: function SentInvites_cancelInvite(record)
      {
         // disable the button
         this.actionButtons[record.getData('invitee').userName].set('disabled', true);
          
         // show a wait message
         this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg("message.removing"),
            spanClass: "wait",
            displayTime: 0
         });
         
         // ajax request success handler
         var success = function SentInvites_cancelInvite_success(response)
         {
            // hide the wait message
            this.widgets.feedbackMessage.destroy();
             
            // remove the record from the list
            var index = this.widgets.dataTable.getRecordIndex(record);
            if (index !== null)
            {
               this.widgets.dataTable.deleteRow(index);
            }
         };
         
         // request failure handler
         var failure = function SentInvites_cancelInvite_failure(response)
         {
            // remove the message
            this.widgets.feedbackMessage.destroy();

            this.actionButtons[record.getData('invitee').userName].set('disabled', true);
         };
         
         // get the url to call
         var url = Alfresco.constants.PROXY_URI + "api/invite/cancel";
         
         // execute ajax request
         Alfresco.util.Ajax.request(
         {
            url: url,
            method: "GET",
            dataObj:
            {
               inviteId: record.getData('inviteId'),
               siteShortName: encodeURIComponent(this.options.siteId)
            },
            responseContentType : "application/json",
            successMessage: this.msg("message.cancel.success"),
            successCallback:
            {
               fn: success,
               scope: this
            },
            failureMessage: this.msg("message.cancel.failure"),
            failureCallback:
            {
               fn: failure,
               scope: this
            }
         });
         
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
      onActionClick: function SentInvites_onActionClick(event, p_obj)
      {
         this.cancelInvite(p_obj);
      },

      /**
       * Search button click event handler
       *
       * @method onSearchClick
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onSearchClick: function SentInvites_onSearchClick(e, p_obj)
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
      _setDefaultDataTableErrors: function SentInvites__setDefaultDataTableErrors(dataTable)
      {
         var msg = Alfresco.util.message;
         dataTable.set("MSG_EMPTY", msg("message.empty", "Alfresco.SentInvites"));
         dataTable.set("MSG_ERROR", msg("message.error", "Alfresco.SentInvites"));
      },
      
      /**
       * Updates people list by calling data webscript
       *
       * @method _performSearch
       * @param searchTerm {string} Search term from input field
       */
      _performSearch: function SentInvites__performSearch(searchTerm)
      {
         if (!this.isSearching)
         {
            this.isSearching = true;

            // Reset the custom error messages
            this._setDefaultDataTableErrors(this.widgets.dataTable);

            // Don't display any message
            // this.widgets.dataTable.set("MSG_EMPTY", "");

            // Empty results table
            var recordCount = this.widgets.dataTable.getRecordSet().getLength();
            if (recordCount > 0)
            {
               this.widgets.dataTable.deleteRows(0, recordCount);
            }

            var successHandler = function SentInvites__pS_successHandler(sRequest, oResponse, oPayload)
            {
               this._enableSearchUI();
               this._setDefaultDataTableErrors(this.widgets.dataTable);
               if (oResponse.results.length > 0)
               {
                  this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
               }
            };

            var failureHandler = function SentInvites__pS_failureHandler(sRequest, oResponse)
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
      _enableSearchUI: function SentInvites__enableSearchUI()
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
      _buildSearchParams: function SentInvites__buildSearchParams(searchTerm)
      {
         return YAHOO.lang.substitute("siteShortName={siteShortName}",
         {
            siteShortName: encodeURIComponent(this.options.siteId)
         });
      }
   });
})();
