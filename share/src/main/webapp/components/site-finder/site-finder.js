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
 * Site Finder component.
 * 
 * @namespace Alfresco
 * @class Alfresco.SiteFinder
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      KeyListener = YAHOO.util.KeyListener;
      
   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * SiteFinder constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.SiteFinder} The new SiteFinder instance
    * @constructor
    */
   Alfresco.SiteFinder = function(htmlId)
   {
      Alfresco.SiteFinder.superclass.constructor.call(this, "Alfresco.SiteFinder", htmlId, ["button", "container", "datasource", "datatable", "json"]);
      
      // Initialise prototype properties
      this.buttons = [];
      this.searchTerm = "";
      this.memberOfSites = {};
      this.pendingInvites = {};
      
      YAHOO.Bubbling.on("siteDeleted", this.onSiteDeleted, this);

      return this;
   };
   
   YAHOO.extend(Alfresco.SiteFinder, Alfresco.component.Base,
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
          * @default 0
          */
         minSearchTermLength: 0,

         /**
          * Maximum number of items to display in the results list
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
          * Invite data for pending invitations
          *
          * @property @inviteData
          * @type array
          */
         inviteData: [],

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
       * List of Join/Leave buttons
       * 
       * @property buttons
       * @type array
       */
      buttons: null,

      /**
       * Search term used for the site search.
       * 
       * @property searchTerm
       * @type string
       */
      searchTerm: null,
      
      /**
       * List of sites the current user is a member of
       * 
       * @property memberOfSites
       * @type object
       */
      memberOfSites: null,
      
      /**
       * Provides easy look-up of pending invites for this user
       * 
       * @property pendingInvites
       * @type object
       */
      pendingInvites: null,
      
      /**
       * Is Search Finished. SearchButton becomes enabled if True
       * 
       * @property isSearchFinished
       * @type boolean
       * @default true
       */
      isSearchFinished: true,
      
      /**
       * Is search enable 
       * 
       * @property isSearchEnable
       * @type boolean
       * @default true
       */
      isSearchEnable : true,

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function SiteFinder_onReady()
      {  
         var me = this;
         
         // Copy the pending invite data
         var invites = this.options.inviteData, invite;
         for (i = 0, j = invites.length; i < j; i++)
         {
            invite = invites[i];
            this.pendingInvites[invite.siteId] = invite.id;
            this.memberOfSites[invite.siteId] = "PENDING";
         }
         
         // DataSource definition
         var uriSearchResults = Alfresco.constants.PROXY_URI + "api/sites?roles=user&";
         this.widgets.dataSource = new YAHOO.util.DataSource(uriSearchResults,
         {
            responseType: YAHOO.util.DataSource.TYPE_JSON,
            connXhrMode: "queueRequests",
            responseSchema:
            {
                resultsList: "items"
            }
         });
         this.widgets.dataSource.doBeforeParseData = function SiteFinder_doBeforeParseData(oRequest , oFullResponse)
         {
            var updatedResponse = oFullResponse;
            
            if (oFullResponse)
            {
               var items = [];
               
               // determine list of sites to show
               if (me.searchTerm.length === 0)
               {
                  items = oFullResponse;
               }
               else
               {
                  var siteData, shortName, title, siteVisibility, isMemberOfGroup;
                  
                  for (var x = 0, y = oFullResponse.length; x < y; x++)
                  {
                     siteData = oFullResponse[x];
                     shortName = siteData.shortName;
                     title = siteData.title;
                     siteVisibility = siteData.visibility;
                     isMemberOfGroup = siteData.isMemberOfGroup;
                     
                     // add site to list
                     items.push(siteData);
                  }
               }

               // Resolve what sites the user is site admin for
               var siteManagers, i, j, k, l;
               for (i = 0, j = items.length; i < j; i++)
               {
                  items[i].isSiteManager = (items[i].siteRole === "SiteManager");
               }

               // we need to wrap the array inside a JSON object so the DataTable is happy
               updatedResponse =
               {
                  "items": items
               };
            }
            
            return updatedResponse;
         };
         
         // setup of the datatable.
         this._setupDataTable();
         
         // setup the button
         this.widgets.searchButton = Alfresco.util.createYUIButton(this, "button", this.doSearch);
         
         // register the "enter" event on the search text field
         var searchInput = Dom.get(this.id + "-term"),
            keyListener = new KeyListener(searchInput,
         {
            keys:13
         },
         {
            fn: function() 
            {
               if(me.isSearchEnable) me.doSearch();
            },
            scope:this,
            correctScope:true
         }, "keydown").enable();

         // Set initial focus?
         if (this.options.setFocus)
         {
            searchInput.focus();
         }
         
         // Finally show the component body here to prevent UI artifacts on YUI button decoration
         Dom.setStyle(this.id + "-body", "visibility", "visible");
      },
      
      /**
       * Declare inline DataTable renderes and create YUI object
       *
       * @method _setupDataTable
       * @private
       */
      _setupDataTable: function SiteFinder_setupDataTable()
      {
         /**
          * DataTable Cell Renderers
          *
          * Each cell has a custom renderer defined as a custom function. See YUI documentation for details.
          * These MUST be inline in order to have access to the Alfresco.SiteFinder class (via the "me" variable).
          */
         var me = this;
          
         /**
          * Thumbnail custom datacell formatter
          *
          * @method renderCellThumbnail
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         renderCellThumbnail = function SiteFinder_renderCellThumbnail(elCell, oRecord, oColumn, oData)
         {
            var shortName = oRecord.getData("shortName"),
               url = Alfresco.constants.URL_PAGECONTEXT + "site/" + shortName + "/dashboard",
               siteName = $html(oRecord.getData("title"));

            // Render the icon
            elCell.innerHTML = '<a href="' + url + '"><img src="' + 
               Alfresco.constants.URL_RESCONTEXT + 'components/site-finder/images/site-64.png' + 
               '" alt="' + siteName + '" title="' + siteName + '" /></a>';
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
         renderCellDescription = function SiteFinder_renderCellDescription(elCell, oRecord, oColumn, oData)
         {
            var siteVisibility = oRecord.getData("visibility").toUpperCase(),
               url = Alfresco.constants.URL_PAGECONTEXT + "site/" + oRecord.getData("shortName") + "/dashboard";         
            
            // title/link to site page
            var details = '<h3 class="sitename"><a href="' + url + '" class="theme-color-1">' + $html(oRecord.getData("title")) + '</a></h3>';
            // description
            details += '<div class="sitedescription">' + $html(oRecord.getData("description")) + '</div>';
            
            // Private / Moderated flag
            if (siteVisibility == "MODERATED")
            {
               details += '<span class="visibility theme-color-3 theme-bg-color-1">' + me.msg("site-finder.moderated")  + '</span>';
            }
            else if (siteVisibility == "PRIVATE")
            {
               details += '<span class="visibility theme-color-3 theme-bg-color-1">' + me.msg("site-finder.private")  + '</span>';
            }
            
            elCell.innerHTML = details;
         };
         
         /**
          * Actions custom datacell formatter
          *
          * @method renderCellActions
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         renderCellActions = function SiteFinder_renderCellActions(elCell, oRecord, oColumn, oData)
         {
            var siteVisibility = oRecord.getData("visibility").toUpperCase(),
               shortName = oRecord.getData("shortName"),
               isSiteManager = oRecord.getData("isSiteManager"),
               title = $html(oRecord.getData("title")),
               isMember = oRecord.getData("siteRole") !== "";

            var hasDelete = (isMember && isSiteManager);

            // Create the mark-up for at least one button, adding delete if appropriate
            var action = '<span id="' + me.id + '-button-' + shortName + '"></span>';
            if (hasDelete)
            {
               action = '<span id="' + me.id + '-deleteButton-' + shortName + '"></span>&nbsp;' + action;
            }
            elCell.innerHTML = action;

            if (hasDelete)
            {
               // Delete site button can now be YUI'd
               var deleteButton = new YAHOO.widget.Button(
               {
                   container: me.id + '-deleteButton-' + shortName
               });
               deleteButton.set("label", me.msg("site-finder.delete"));
               deleteButton.set("onclick",
               {
                  fn: me.doDelete,
                  obj:
                  {
                     shortName: shortName,
                     title: title
                  },
                  scope: me
               });
            }

            // Create generic button - action populated later depending on state
            var button = new YAHOO.widget.Button(
            {
                container: me.id + '-button-' + shortName,
                disabled: oRecord.getData("isMemberOfGroup")
            });

            switch (siteVisibility)
            {
               case "PUBLIC":
                  // If already a member of the site then show leave action, otherwise show join
                  if (isMember)
                  {
                     // Leave site action
                     button.set("label", me.msg("site-finder.leave"));
                     button.set("onclick",
                     {
                        fn: me.doLeave,
                        obj:
                        {
                           shortName: shortName,
                           title: title
                        },
                        scope: me
                     });
                  }
                  else
                  {
                     // Join site action
                     button.set("label", me.msg("site-finder.join"));
                     button.set("onclick",
                     {
                        fn: me.doJoin,
                        obj:
                        {
                           shortName: shortName, 
                           title: title
                        },
                        scope: me
                     });
                  }

                  me.buttons[shortName] =
                  {
                     button: button
                  };
                  break;
               
               case "PRIVATE":
                  if (isMember)
                  {
                     // Must already be a member of the site so show leave action
                     button.set("label", me.msg("site-finder.leave"));
                     button.set("onclick",
                     {
                        fn: me.doLeave,
                        obj:
                        {
                           shortName: shortName,
                           title: title
                        },
                        scope: me
                     });

                     me.buttons[shortName] =
                     {
                        button: button
                     };
                     break;
                  }                  
               case "MODERATED":
                  // If already a member of the site then show leave action, otherwise show join request
                  if (isMember)
                  {
                     // Leave site action
                     button.set("label", me.msg("site-finder.leave"));
                     button.set("onclick",
                     {
                        fn: me.doLeave,
                        obj:
                        {
                           shortName: shortName,
                           title: title
                        },
                        scope: me
                     });
                  }
                  else if (me.memberOfSites[shortName] == "PENDING")
                  {
                     var skip = false;
                     for (i = 0; i < me.options.inviteData.length; i++)
                     {
                        if (me.options.inviteData[i].siteId == shortName)
                        {
                           if (me.options.inviteData[i].type != "MODERATED")
                           {
                              skip = true;
                           }
                           break;
                        }
                     }
                     
                     if (!skip)
                     {
                        // Leave site action
                        button.set("label", me.msg("site-finder.cancel-request"));
                        button.set("onclick",
                        {
                           fn: me.doCancelRequest,
                           obj:
                           {
                              shortName: shortName,
                              title: title
                           },
                           scope: me
                        });
                     }
                     else
                     {
                        elCell.innerHTML = '<div></div>';
                     }
                  }
                  else
                  {
                     // Join site action
                     button.set("label", me.msg("site-finder.request-join"));
                     button.set("onclick",
                     {
                        fn: me.doRequestJoin,
                        obj:
                        {
                           shortName: shortName, 
                           title: title
                        },
                        scope: me
                     });
                  }

                  me.buttons[shortName] =
                  {
                     button: button
                  };
                  break;
               
               default:
                  // output padding div so layout is not messed up due to missing buttons
                  elCell.innerHTML = '<div></div>';
                  break;
            }
         };

         // DataTable column defintions
         var columnDefinitions =
         [
            {
               key: "shortName", label: "Short Name", sortable: false, formatter: renderCellThumbnail
            },
            {
               key: "description", label: "Description", sortable: false, formatter: renderCellDescription
            },
            {
               key: "button", label: "Actions", formatter: renderCellActions
            }
         ];

         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-sites", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: 32,
            initialLoad: false,
            MSG_EMPTY: this.msg("message.instructions")
         });
         this.widgets.dataTable.subscribe("rowDeleteEvent", this.onRowDeleteEvent, this, true);
         this.widgets.dataTable.subscribe("postRenderEvent",this.onPostRenderEvent, this, true);
         
         // Override abstract function within DataTable to set custom error message
         this.widgets.dataTable.doBeforeLoadData = function SiteFinder_doBeforeLoadData(sRequest, oResponse, oPayload)
         {
            if (oResponse.error)
            {
               try
               {
                  var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                  this.widgets.dataTable.set("MSG_ERROR", response.message);
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
       * Search event handler
       *
       * @method doSearch
       */
      doSearch: function SiteFinder_doSearch()
      {
         this.searchTerm = YAHOO.lang.trim(Dom.get(this.id + "-term").value);

         // inform the user if the search term entered is too small
         if (this.searchTerm.replace(/\*/g, "").length < this.options.minSearchTermLength)
         {
            Alfresco.util.PopupManager.displayMessage(
            {
               text: parent._msg("message.minimum-length", this.options.minSearchTermLength)
            });
            return;
         }

         this._performSearch(this.searchTerm);
      },
      
      /**
       * Generic failure callback handler
       *
       * @method _failureCallback
       * @private
       * @param message {string} Display message
       */
      _failureCallback: function SiteFinder__failureCallback(obj, message)
      {
         this._clearFeedbackMessage();
         if (message)
         {
            Alfresco.util.PopupManager.displayPrompt(
            {
               title: Alfresco.util.message("message.failure"),
               text: message
            });
         }
      },
      
      /**
       * Join event handler
       *
       * @method doJoin
       * @param event {object} The event object
       * @param site {string} The shortName of the site to join
       */
      doJoin: function SiteFinder_doJoin(event, site)
      {
         var user = this.options.currentUser;
         
         // make ajax call to site service to join user
         Alfresco.util.Ajax.jsonPut(
         {
            url: Alfresco.constants.PROXY_URI + "api/sites/" + site.shortName + "/memberships",
            dataObj:
            {
               role: "SiteConsumer",
               person:
               {
                  userName: user
               }
            },
            successCallback:
            {
               fn: this._joinSuccess,
               obj: site,
               scope: this
            },
            failureCallback:
            {
               fn: this._failureCallback,
               obj: this.msg("site-finder.join-failure", this.options.currentUser, site.title),
               scope: this
            }
         });
      },
      
      /**
       * Callback handler used when a user is successfully added to a site
       * 
       * @method _joinSuccess
       * @param response {object}
       * @param siteData {object}
       */
      _joinSuccess: function SiteFinder__joinSuccess(response, site)
      {
         // show popup message to confirm
         Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg("site-finder.join-success", this.options.currentUser, site.title)
         });
         
         // redo the search again to get updated info
         this.doSearch();
      },
      
      /**
       * Leave event handler
       * 
       * @method doLeave
       * @param event {object} The event object
       * @param site {string} The shortName of the site to leave
       */
      doLeave: function SiteFinder_doLeave(event, site)
      {
         var user = this.options.currentUser;
         
         // make ajax call to site service to join user
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "api/sites/" + site.shortName + "/memberships/" + encodeURIComponent(user),
            method: "DELETE",
            successCallback:
            {
               fn: this._leaveSuccess,
               obj: site,
               scope: this
            },
            failureCallback:
            {
               fn: this._failureCallback,
               obj: this.msg("site-finder.leave-failure", this.options.currentUser, site.title),
               scope: this
            }
         });
      },
      
      /**
       * Callback handler used when a user is successfully removed from a site
       * 
       * @method _leaveSuccess
       * @param response {object}
       * @param siteData {object}
       */
      _leaveSuccess: function SiteFinder__leaveSuccess(response, site)
      {
         // remove site from site membership list
         delete this.memberOfSites[site.shortName];
         
         // show popup message to confirm
         Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg("site-finder.leave-success", this.options.currentUser, site.title)
         });
         
         // redo the search again to get updated info
         this.doSearch();
      },

      /**
       * Request Join event handler
       *
       * @method doRequestJoin
       * @param event {object} The event object
       * @param site {string} The shortName of the site to request joining
       */
      doRequestJoin: function SiteFinder_doRequestJoin(event, site)
      {
         var user = this.options.currentUser;
         
         // show a wait message
         this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg("message.please-wait"),
            spanClass: "wait",
            displayTime: 0
         });

         // make ajax call to site service to request joining
         Alfresco.util.Ajax.jsonRequest(
         {
            url: Alfresco.constants.PROXY_URI + "api/sites/" + site.shortName + "/invitations",
            method: "POST",
            dataObj:
            {
               invitationType: "MODERATED",
               inviteeUserName: user,
               inviteeComments: "",
               inviteeRoleName: "SiteConsumer"
            },
            successCallback:
            {
               fn: this._requestJoinSuccess,
               obj: site,
               scope: this
            },
            failureCallback:
            {
               fn: this._failureCallback,
               obj: this.msg("site-finder.request-join-failure", this.options.currentUser, site.title),
               scope: this
            }
         });
      },
      
      /**
       * Callback handler used when a request has successfully been made to join a site
       * 
       * @method _requestJoinSuccess
       * @param response {object}
       * @param siteData {object}
       */
      _requestJoinSuccess: function SiteFinder__requestJoinSuccess(response, site)
      {
         var data = response.json.data,
            siteId = site.shortName;

         // add site to site membership list
         this.memberOfSites[siteId] = "PENDING";
         
         // Get data.inviteId for the cancel request
         this.pendingInvites[siteId] = data.inviteId;
         
         // show popup message to confirm
         Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg("site-finder.request-join-success", this.options.currentUser, site.title)
         });
         
         // redo the search again to get updated info
         this.doSearch();
      },

      /**
       * Cancel Join Request event handler
       *
       * @method doCancelRequest
       * @param event {object} The event object
       * @param site {string} The shortName of the site to cancel join request for
       */
      doCancelRequest: function SiteFinder_doCancelRequest(event, site)
      {
         var user = this.options.currentUser,
            siteId = site.shortName;
         
         // show a wait message
         this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg("message.please-wait"),
            spanClass: "wait",
            displayTime: 0
         });

         // make ajax call to site service to request joining
         Alfresco.util.Ajax.jsonRequest(
         {
            url: Alfresco.constants.PROXY_URI + "api/sites/" + siteId + "/invitations/" + encodeURIComponent(this.pendingInvites[siteId]),
            method: "DELETE",
            successCallback:
            {
               fn: this._cancelRequestSuccess,
               obj: site,
               scope: this
            },
            failureCallback:
            {
               fn: this._failureCallback,
               obj: this.msg("site-finder.cancel-request-failure", this.options.currentUser, site.title),
               scope: this
            }
         });
      },
      
      /**
       * Callback handler used when a request has successfully been cancelled to join a site
       * 
       * @method _cancelRequestSuccess
       * @param response {object}
       * @param siteData {object}
       */
      _cancelRequestSuccess: function SiteFinder__cancelRequestSuccess(response, site)
      {
         // reset site status
         this.memberOfSites[site.shortName] = "MODERATED";
         
         // show popup message to confirm
         Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg("site-finder.cancel-request-success", this.options.currentUser, site.title)
         });
         
         // redo the search again to get updated info
         this.doSearch();
      },

      /**
       * Delete event handler
       *
       * @method doDelete
       * @param event {object} The event object
       * @param site {object} An object literal of the site to delete
       */
      doDelete: function SiteFinder_doDelete(event, site)
      {
         Alfresco.module.getDeleteSiteInstance().show(
         {
            site: site
         });
      },

      /**
       * Resets the YUI DataTable errors to our custom messages
       *
       * NOTE: Scope could be YAHOO.widget.DataTable, so can't use "this"
       *
       * @method _setDefaultDataTableErrors
       * @param dataTable {object} Instance of the DataTable
       */
      _setDefaultDataTableErrors: function SiteFinder__setDefaultDataTableErrors(dataTable)
      {
         var msg = Alfresco.util.message;
         dataTable.set("MSG_EMPTY", msg("message.empty", "Alfresco.SiteFinder"));
         dataTable.set("MSG_ERROR", msg("message.error", "Alfresco.SiteFinder"));
      },
      
      /**
       * Clears any current feedback message pop-up
       *
       * @method _cleanFeedbackMessage
       */
      _clearFeedbackMessage: function SiteFinder__clearFeedbackMessage()
      {
         if (this.widgets.feedbackMessage)
         {
            try
            {
               this.widgets.feedbackMessage.destroy();
            }
            catch(e)
            {
               
            }
            this.widgets.feeedbackMessage = null;
         }
      },
      
      /**
       * Updates site list by calling data webscript with current search term
       *
       * @method _performSearch
       * @param searchTerm {string} The term to search for
       */
      _performSearch: function SiteFinder__performSearch(searchTerm)
      {
         // Reset the custom error messages
         this._setDefaultDataTableErrors(this.widgets.dataTable);
         
         // Display loading message
         this.widgets.dataTable.set("MSG_EMPTY", Alfresco.util.message("site-finder.searching", "Alfresco.SiteFinder"));
         
         // empty results table
         this.widgets.dataTable.deleteRows(0, this.widgets.dataTable.getRecordSet().getLength());
         
         // loading message function
         var loadingMessage = null;
         var fnShowLoadingMessage = function SiteFinder__pS_fnShowLoadingMessage()
         {
            loadingMessage = Alfresco.util.PopupManager.displayMessage(
            {
               displayTime: 0,
               text: '<span class="wait">' + $html(this.msg("message.loading")) + '</span>',
               noEscape: true
            });
         };
         
         // slow data webscript message
         var timerShowLoadingMessage = YAHOO.lang.later(2000, this, fnShowLoadingMessage);
         
         var successHandler = function SiteFinder__pS_successHandler(sRequest, oResponse, oPayload)
         {
            if (timerShowLoadingMessage)
            {
               timerShowLoadingMessage.cancel();
            }
            if (loadingMessage)
            {
               loadingMessage.destroy();
            }
            
            this.isSearchFinished = true;
            this.searchTerm = searchTerm;
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
         };
         
         var failureHandler = function SiteFinder__pS_failureHandler(sRequest, oResponse)
         {
            this.widgets.searchButton.set("disabled", false);
            this.isSearchEnable = true;
            this.isSearchFinished = true;
            
            if (timerShowLoadingMessage)
            {
               timerShowLoadingMessage.cancel();
            }
            if (loadingMessage)
            {
               loadingMessage.destroy();
            }
            
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
         
         this.widgets.dataSource.sendRequest(this._buildSearchParams(searchTerm),
         {
               success: successHandler,
               failure: failureHandler,
               scope: this
         });

         this.isSearchFinished = false;
         this.isSearchEnable = false;
         this.widgets.searchButton.set("disabled", true);
      },

      /**
       * Build URI parameter string for finding sites
       *
       * @method _buildSearchParams
       * @param searchTerm {string} Path to query
       */
      _buildSearchParams: function SiteFinder__buildSearchParams(searchTerm)
      {
         var params = YAHOO.lang.substitute("size={maxResults}&nf={term}",
         {
            maxResults : this.options.maxSearchResults,
            term : encodeURIComponent(searchTerm)
         });

         return params;
      },

      /**
       * Fired any another component, DeleteSite, to let other components know
       * that a site has been deleted.
       * Performs the search again.
       *
       * @method onSiteDeleted
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (unused)
       */
      onSiteDeleted: function SiteFinder_onSiteDeleted(layer, args)
      {
         var site = args[1].site;
         var rs = this.widgets.dataTable.getRecordSet();
         var length = rs.getLength();
         for (var i = 0; i < length; i++)
         {
            var record = rs.getRecord(i);
            if (record.getData("shortName") == site.shortName)
            {
               this.widgets.dataTable.deleteRow(record);
            }
         }
         if (rs.getLength() == 0)
         {
            this.widgets.dataTable.set("MSG_EMPTY", Alfresco.util.message("message.empty", "Alfresco.SiteFinder"));
         }
      },
      
      /**
      * Event, fires on dataTable rendition end
      * @method onPostRenderEvent
       */
      onPostRenderEvent: function SiteFinder__onPostRenderEvent()
      {
         if (this.isSearchFinished)
         {
            this.widgets.searchButton.set("disabled", false);
            this.isSearchEnable = true;
         }
      },

      /**
       * Fired by YUI:s DataTable when a row has been added to the data table list.
       * Keeps track of added files.
       *
       * @method onRowDeleteEvent
       * @param event {object} a DataTable "rowDelete" event
       */
      onRowDeleteEvent: function SiteFinder_onRowDeleteEvent(event)
      {
         if (this.widgets.dataTable.getRecordSet().getLength() === 0)
         {
            this.widgets.dataTable.showTableMessage(this.msg("site-finder.enter-search-term", this.name), "siteFinderTableMessage");
         }
      }
   });
})();