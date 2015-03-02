/**
 * Copyright (C) 2011 IP-TECH <http://www.iptech-offshore.net>.
 *
 * This file is added to Alfresco
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
 * ConsoleTagManagement tool component.
 *
 * @namespace Alfresco
 * @class Alfresco.ConsoleTagManagement
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
         $date = function $date(date, format) { return Alfresco.util.formatDate(Alfresco.util.fromISO8601(date), format) };

   /**
    * ConsoleTagManagement tool component constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.ConsoleTagManagement} The new component instance
    * @constructor
    */
   Alfresco.ConsoleTagManagement = function TagManagement_constructor(htmlId)
   {
      Alfresco.ConsoleTagManagement.superclass.constructor.call(this, 
           "Alfresco.ConsoleTagManagement", 
           htmlId, 
           ["button", "container", "datasource", "datatable", "paginator", "history", "animation"]
      );
      
      return this;
   };

   /**
    * Extend from Alfresco.component.Base
    */
   YAHOO.extend(Alfresco.ConsoleTagManagement, Alfresco.component.Base,
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
         * Maximum number of tags to display per page.
         *
         * @property pageSize
         * @type int
         * @default 15
         */
        pageSize: 15
      },
      
      searchTerm:"",
      
      /**
       * Number of search results.
       */
      resultsCount: 0,
      
      /**
       * Current visible page index - counts from 1
       */
      currentPage: 1,
      
      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function ConsoleTagManagement_onReady()
      {
         // Setup DataTable
         this._setupDataTable();
         
         // Search Button definition
         this.widgets.searchButton = Alfresco.util.createYUIButton(this, "search-button", this.onSearchClick);
       
         // Search text field definition
         var queryInput = Dom.get(this.id + "-search-text");
         this.widgets.enterListener = new YAHOO.util.KeyListener(
               queryInput,
               {
                  keys: YAHOO.util.KeyListener.KEY.ENTER
               },
               {
                  fn: this.onSearchClick,
                  scope: this,
                  correctScope: true
               },
               "keydown"
            ).enable();
         
         // Show all tags : perform the initial search
         queryInput.value = this.searchTerm;
         this._performSearch({
            searchTerm : this.searchTerm
         });
      },

      _setDefaultDataTableErrors: function ConsoleTagManagement__setDefaultDataTableErrors(dataTable)
      {
         dataTable.set("MSG_EMPTY", this.msg("no-tag-found"));
      },

      /**
       * DataTable definition and setup
       *
       * @method _setupDataTable
       */
      _setupDataTable: function ConsoleTagManagement_setupDataTable()
      {
         var me = this;
         
         // DataTable Cell Renderers definitions
         var renderCellTagInfo = function ConsoleTagManagement_onReady_renderCellTagInfo(elCell, oRecord, oColumn, oData)
         {
            var tagName = oRecord.getData().name;

            var truncatedTagName;
            if (tagName.length > 30)
            {
               truncatedTagName = $html(tagName.substring(0, 30) + "...");
            }
            else
            {
               truncatedTagName = $html(tagName);
            }
            var messageDesc = '<span><a class="theme-color-1" title="' + $html(tagName) + '" href="' + Alfresco.constants.URL_PAGECONTEXT + 'repository#filter=tag|' + encodeURIComponent(tagName) + '&page=1"><b>' + truncatedTagName + '</b></a></span>';

            elCell.innerHTML = messageDesc ;
         };
         
         var renderCellTagOwner = function ConsoleTagManagement_onReady_renderCellTagOwner(elCell, oRecord, oColumn, oData)
         {
            var tagOwner = oRecord.getData().modifier;
            var messageDesc = '<span><a class="theme-color-1" href="' + Alfresco.constants.URL_PAGECONTEXT + 'user/' + encodeURIComponent(tagOwner) + '/profile">' + $html(tagOwner) + '</a></span>';
            elCell.innerHTML = messageDesc;
         };
         
         var renderCellModificationDate = function ConsoleTagManagement_onReady_renderCellModificationDate(elCell, oRecord, oColumn, oData)
         {
            var modificationDate = oRecord.getData().modified;
            var messageDesc = '<span>' + $date(modificationDate, me.msg("date-format.default")) + '</span>';
            elCell.innerHTML = messageDesc ;
         };
         
         var renderCellActions = function ConsoleTagManagement_onReady_renderCellActions(elCell, oRecord, oColumn, oData)
         {
            var tag = oRecord.getData();
            var actions = "", editLink, deleteLink, index=oRecord._nCount;
            
            actions += '<a id="' + me.id + '-edit-link-'+index+'" href="#" class="edit-tag" title="' + me.msg("title.editTag") + '">&nbsp;</a>';
            actions += '<a id="' + me.id + '-delete-link-'+index+'" href="#" class="delete-tag" title="' + me.msg("title.deleteTag") + '">&nbsp;</a>';
            
            elCell.innerHTML = actions;
            
            deleteLink=Dom.getChildrenBy(elCell, function(el){
                 return el.id === me.id+"-delete-link-" + index;
              })[0];
            editLink=Dom.getChildrenBy(elCell, function(el){
              return el.id === me.id+"-edit-link-" + index;
              })[0];
            
            Event.addListener(oRecord.getId(), "mouseover", function()
            {
               Dom.addClass(deleteLink, "delete-tag-active");
               Dom.addClass(editLink, "edit-tag-active");
            });
            Event.addListener(oRecord.getId(), "mouseout", function()
            {
               Dom.removeClass(deleteLink, "delete-tag-active");
               Dom.removeClass(editLink, "edit-tag-active");
            });
            
            Event.addListener(deleteLink, 'click', function () {me.onActionDelete(tag.name);}, deleteLink, false);
            Event.addListener(editLink, 'click', function () {me.onActionEdit(tag.name);}, editLink, false);
         };
         
         // DataSource definition
         var tagSearchResultsURI = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/tags/{store_type}/{store_id}?details=true",
         {
            store_type : "workspace",
            store_id : "SpacesStore"
         });
         
         this.widgets.dataSource = new YAHOO.util.DataSource(tagSearchResultsURI);
         this.widgets.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
         this.widgets.dataSource.connXhrMode = "queueRequests";
         this.widgets.dataSource.responseSchema = {
            resultsList: "data.items",
            fields: ["name","modifier","modified"],
            metaFields: { totalRecords: "data.totalRecords" }
         };
         
         // YUI Paginator definition
         this.widgets.paginator = new YAHOO.widget.Paginator({
            containers: this.id + "-paginator",
            rowsPerPage: this.options.pageSize,
            initialPage: 1,
            template: this.msg("pagination.template"),
            pageReportTemplate: this.msg("pagination.template.page-report"),
            previousPageLinkLabel: this.msg("pagination.previousPageLinkLabel"),
            nextPageLinkLabel: this.msg("pagination.nextPageLinkLabel")
         });
         
         // DataTable column defintions
         var columnDefinitions = [
             { key: "name", label: me.msg("title.tagName"), sortable: false, formatter: renderCellTagInfo,width: 120 },
             { key: "modifiedBy", label: me.msg("title.modifiedBy"), sortable: false, formatter: renderCellTagOwner,width: 150 },
             { key: "modifiedOn", label: me.msg("title.modifiedOn"), sortable: false, formatter: renderCellModificationDate,width: 150 },
             { key: "action", label: me.msg("title.actions"),sortable: false, formatter: renderCellActions, width: 45 }
         ];

         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-tags", columnDefinitions, this.widgets.dataSource,{
            renderLoopSize: Alfresco.util.RENDERLOOPSIZE,
            initialLoad: false,
            paginator: this.widgets.paginator,
            MSG_LOADING: this.msg("loading-tags"),
            MSG_EMPTY: this.msg("no-tag-found")
         });
         
         // Override abstract function within DataTable to set custom error message
         this.widgets.dataTable.doBeforeLoadData = function Search_doBeforeLoadData(sRequest, oResponse, oPayload)
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
                  me.widgets.dataTable.render();
               }
            }
            else if (oResponse.results)
            {
               // clear the empty error message
               me.widgets.dataTable.set("MSG_EMPTY", "");
               
               // update the results count, update hasMoreResults.
               me.hasMoreResults = (oResponse.results.length > me.options.maxSearchResults);
               if (me.hasMoreResults)
               {
                  oResponse.results = oResponse.results.slice(0, me.options.maxSearchResults);
               }
               me.resultsCount = oResponse.results.length;
               
               if (me.resultsCount > 0)
               {
                  Dom.removeClass(me.id + "-paginator", "hidden");
                  Dom.removeClass(me.id + "-tags-list-bar-bottom", "hidden");
                  Dom.removeClass(me.id + "-tags", "hidden");
                
                  // Remove comment to show search result count
                  // Dom.get(this.id + '-tags-list-info').innerHTML = = this.msg("tags-found", '<b>' + me.resultsCount + '</b>');
                  Dom.addClass(me.id + "-tags-list-info", "hidden");
               }
               else
               {
                  Dom.addClass(me.id + "-paginator", "hidden");
                  Dom.addClass(me.id + "-tags-list-bar-bottom", "hidden");
                  Dom.addClass(me.id + "-tags", "hidden");
                   
                  // set the text
                  Dom.get(me.id + '-tags-list-info').innerHTML = me.msg("no-tag-found");
                  Dom.removeClass(me.id + "-tags-list-info", "hidden");
               }
            }
            // Must return true to have the "Loading..." message replaced by the error message
            return true;
         };

         // Paginator event handler
         this.widgets.paginator.subscribe("changeRequest", function(state, scope){
            var request = "&tf=" + encodeURIComponent(scope.searchTerm) + "&from="+state.recordOffset + "&size=" + state.rowsPerPage;
            scope.widgets.dataSource.sendRequest(request,
            {
              success: function(sRequest, oResponse, oPayload) {
                  scope.widgets.dataTable.onDataReturnSetRows.call(scope.widgets.dataTable, sRequest, oResponse, oPayload);
                  scope.resultsCount = oResponse.meta.totalRecords;
              },
              failure: this.onRequestFailure,
              scope: this
            });

            scope.currentPage = state.page;
            scope.widgets.paginator.setState(state);
         }, this);
         
         // Rendering complete event handler
         this.widgets.dataTable.subscribe("renderEvent", function(){
            // Update the paginator
            me.widgets.paginator.setState(
            {
               page: me.currentPage,
               totalRecords: me.resultsCount
            });
            me.widgets.paginator.render();
         });
      },
      
      /**
       * Search button click event handler
       *
       * @method onSearchClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onSearchClick: function ConsoleTagManagement_onSearchClick(e, args)
      {
         var searchTermElem = Dom.get(this.id + "-search-text");

         this._performSearch({
            searchTerm : searchTermElem.value  
         });
      },
      
      // Failure handler
      onRequestFailure: function failureHandler(sRequest, oResponse)
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
               this.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
            }
            catch(e)
            {
               this._setDefaultDataTableErrors(this.widgets.dataTable);
               this.widgets.dataTable.render();
            }
         }
      },
      
      /**
       * Updates search results list by calling data webscript with current query term
       *
       * @method _performSearch
       * @param args {object} search args
       */
      _performSearch: function ConsoleTagManagement__performSearch(args)
      {
         if (args.searchTerm != undefined) {
            this.searchTerm = YAHOO.lang.trim(args.searchTerm);
         }
         
         // empty results table
         this.widgets.dataTable.deleteRows(0, this.widgets.dataTable.getRecordSet().getLength());
         
         // update the ui to show that a search is on-going
         this.widgets.dataTable.set("MSG_EMPTY", "");
         this.widgets.dataTable.render();
         
         // Success handler
         function successHandler(sRequest, oResponse, oPayload)
         {
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
            this.resultsCount = oResponse.meta.totalRecords;
            this.currentPage = 1;
           
            // set focus to search input textbox
            Dom.get(this.id + "-search-text").focus();
         }
         
         // Reload data sending new request for dataSource
         if (this.searchTerm == "" || this.searchTerm == "*")
         {
            this.widgets.dataSource.sendRequest("&from=0&size=" + this.options.pageSize,
            {
               success: successHandler,
               failure: this.onRequestFailure,
               scope: this
            });
         }
         else
         {
            this.widgets.dataSource.sendRequest("&tf=" + encodeURIComponent(this.searchTerm) + "&from=0&size=" + this.options.pageSize,
            {
               success: successHandler,
               failure: this.onRequestFailure,
               scope: this
            });
         }
      },      
      
      /**
       * Action onActionEdit
       * @param tagName {string}
       */
      onActionEdit: function ConsoleTagManagement_onActionEdit(tagName)
      {
         var actionUrl = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI+ "api/tags/{store_type}/{store_id}/{tagName}?alf_method=PUT",
         {
            store_type : "workspace",
            store_id : "SpacesStore",
            tagName : encodeURIComponent(tagName)
         });
        
         var doSetupFormsValidation = function sA_oACT_doSetupFormsValidation(p_form)
         {
            p_form.addValidation(this.id + "-edit-tag-name", Alfresco.forms.validation.mandatory, null, "keyup");
         };
      
         var editTag = new Alfresco.module.SimpleDialog(this.id + "-edit-tag");
         editTag.setOptions({
            width : "30em",
            templateUrl :
               YAHOO.lang.substitute(
                  Alfresco.constants.URL_PAGECONTEXT+ "components/admin/edit-tag?htmlid={id}&tagName={tagName}", 
                  {
                     id: this.id + "-edit-tag",
                     tagName: encodeURIComponent(tagName)
                  }),
            actionUrl : actionUrl,
            destroyOnHide: true,
            doSetupFormsValidation:
            {
                fn: doSetupFormsValidation,
                scope: this
            },
            onSuccess:
            {
               fn: function dlA_onActionEditTag_success(response)
               {
                  if (response.json.result == true){
                     Alfresco.util.PopupManager.displayMessage({
                        text: this.msg(response.json.msg)
                     });
                     this._performSearch({
                        searchTerm : this.searchTerm
                     });
                  }
                  else
                  {                          
                     Alfresco.util.PopupManager.displayPrompt({
                           title: this.msg("title.error"),
                     text : this.msg(response.json.msg),
                     buttons : [{
                        text: this.msg("button.close"),
                        handler : function onEditTag_failure_close() {
                           this.destroy();
                        }
                     }]
                  });
                  }
               },
               scope: this
            },
            onFailure:
            {                          
               fn: function onActionEditTag_failure(response)
               {           
                  if(response.serverResponse.status === 400 && response.json != undefined && response.json.result === false)
                  {
                    Alfresco.util.PopupManager.displayMessage(
                      {
                         text: this.msg(response.json.msg)
                      });
                  } else {
                     Alfresco.util.PopupManager.displayMessage({
                           text: this.msg("message.edit.failure")
                     });
                  }
              },
               scope: this
            }
        }).show();
      },
      
      /**
       * Action onActionDelete
       * 
       * @param tagName {string}
       */
      onActionDelete: function ConsoleTagManagement_onActionDelete(tagName)
      {
         var me = this;
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg("message.confirm.delete.title"),
            text: this.msg("message.confirm.delete", tagName),
            buttons: [
            {
               text: this.msg("button.delete"),
               handler: function dlA_onActionDelete_delete()
               {
                  this.destroy();
                  me._onActionDeleteConfirm(tagName);
               }
            },
            {
               text: this.msg("button.cancel"),
               handler: function dlA_onActionDelete_cancel()
               {
                  this.destroy();
               },
               isDefault: true
            }]
         });
      },
      
      /**
       * Action onActionDeleteConfirm
       * 
       * @param tagName {string}
       */
      _onActionDeleteConfirm: function ConsoleTagManagement_onActionDeleteConfirm(tagName)
      {
         Alfresco.util.Ajax.request(
         {
            method: Alfresco.util.Ajax.DELETE,
            url: YAHOO.lang.substitute(Alfresco.constants.PROXY_URI+ "api/tags/{store_type}/{store_id}/{tagName}",
            {
               store_type : "workspace",
               store_id : "SpacesStore",
               tagName: encodeURIComponent(tagName)
            }),
            successCallback:
            {
               fn: function onDelete_success(response)
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg(response.json.msg)
                  });
                  if (response.json.result == true)
                  {
                     this._performSearch(
                     {
                        searchTerm : this.searchTerm
                     });
                  }
               },
               scope: this
            },
            failureMessage: this.msg("message.delete.failure")
         });
      }
   });
})();
