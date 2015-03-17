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
 * User Trashcan component.
 * 
 * @namespace Alfresco
 * @class Alfresco.UserTrashcan
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
   var $html = Alfresco.util.encodeHTML;
   
   /**
    * UserTrashcan constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.UserTrashcan} The new UserTrashcan instance
    * @constructor
    */
   Alfresco.UserTrashcan = function(htmlId)
   {
      Alfresco.UserTrashcan.superclass.constructor.call(this, "Alfresco.UserTrashcan", htmlId, ["button", "container", "datasource", "datatable", "paginator"]);
      this.searchText = "";
      return this;
   }
   
   YAHOO.extend(Alfresco.UserTrashcan, Alfresco.component.Base,
   {
      searchText: null,
      pageSize: 50,
      skipCount: 0,
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function UT_onReady()
      {
         // Reference to self used by inline functions
         var me = this;
         
         // Buttons and menus
         this.widgets.empty = Alfresco.util.createYUIButton(this, "empty-button", this.onEmpty);
         this.widgets.search = Alfresco.util.createYUIButton(this, "search-button", this.onSearch);
         this.widgets.clear = Alfresco.util.createYUIButton(this, "clear-button", this.onClear);
         this.widgets.pageLess = Alfresco.util.createYUIButton(this, "paginator-less-button", this.onPageLess);
         this.widgets.pageMore = Alfresco.util.createYUIButton(this, "paginator-more-button", this.onPageMore);
         this.widgets.actionMenu = Alfresco.util.createYUIButton(this, "selected", this.onActionItemClick,
         {
            disabled: true,
            type: "menu",
            menu: "selectedItems-menu"
         });
         this.widgets.selectMenu = Alfresco.util.createYUIButton(this, "select-button", this.onSelectItemClick,
         {
            type: "menu",
            menu: "selectItems-menu"
         });
         
         // Enter key press handler for search text field
         var me = this;
         Dom.get(this.id + "-search-text").onkeypress = function(e)
         {
            if (e.keyCode === YAHOO.util.KeyListener.KEY.ENTER)
            {
               me.performSearch();
            }
         };
         
         // Configure datatable
         var url = Alfresco.constants.PROXY_URI + "api/archive/workspace/SpacesStore";
         this.widgets.dataTable = new Alfresco.util.DataTable(
         {
            dataTable:
            {
               container: this.id + "-datalist",
               columnDefinitions:
               [
                  { key: "select", sortable: false, formatter: this.bind(this.renderCellSelect), width: 16 },
                  { key: "thumbnail", sortable: false, formatter: this.bind(this.renderCellIcon), width: 32 },
                  { key: "description", sortable: false, formatter: this.bind(this.renderCellDescription) },
                  { key: "actions", sortable: false, formatter: this.bind(this.renderCellActions), width: 250 }
               ]
            },
            dataSource:
            {
               url: url,
               initialParameters: "maxItems=" + (this.pageSize + 1),
               config:
               {
                  responseSchema:
                  {
                     resultsList: "data.deletedNodes"
                  },
                  doBeforeParseData: function _doBeforeParseData(oRequest, oResponse)
                  {
                     // process the paging meta data to correctly set paginator button enabled state
                     me.widgets.pageLess.set("disabled", ((me.skipCount = oResponse.paging.skipCount) === 0));
                     if (oResponse.paging.totalItems > me.pageSize)
                     {
                        // remove the last item as it's only for us to evaluate the "more" button state
                        oResponse.data.deletedNodes.pop();
                        me.widgets.pageMore.set("disabled", false);
                     }
                     else
                     {
                        me.widgets.pageMore.set("disabled", true);
                     }
                     return oResponse;
                  }
               }
            }
         });
      },
      
      /**
       * DataTable Cell Renderers
       */
      
      /**
       * Select checkbox custom datacell formatter
       *
       * @method UserTrashcan_renderCellSelect
       */
      renderCellSelect: function UserTrashcan_renderCellSelect(elCell, oRecord, oColumn, oData)
      {
         Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
         
         var me = this;
         elCell.innerHTML = '<input id="checkbox-' + oRecord.getId() + '" type="checkbox" value="'+ oRecord.getData("nodeRef") + '">';
         elCell.firstChild.onclick = function() {
            me._updateSelectedItemsMenu();
         };
      },
      
      /**
       * File/Folder icon custom datacell formatter
       *
       * @method UserTrashcan_renderCellIcon
       */
      renderCellIcon: function UserTrashcan_renderCellIcon(elCell, oRecord, oColumn, oData)
      {
         Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
         
         var name = oRecord.getData("name"),
             type = oRecord.getData("nodeType");
         
         elCell.innerHTML = '<span class="icon32"><img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/filetypes/' + Alfresco.util.getFileIcon(name, oRecord.getData("isContentType") ? "cm:content" : type) + '" alt="' + $html(name) + '" /></span>';
      },
      
      /**
       * Description metadata custom datacell formatter
       * 
       * @method UserTrashcan_renderCellDescription
       */
      renderCellDescription: function UserTrashcan_renderCellDescription(elCell, oRecord, oColumn, oData)
      {
         var fullName = oRecord.getData("firstName") + " " + oRecord.getData("lastName");
         var viewUrl = Alfresco.constants.PROXY_URI_RELATIVE + "api/node/content/" + oRecord.getData("nodeRef").replace(":/", "") + "/" + encodeURIComponent(oRecord.getData("name"));
         var profileLink = '<a href="' + Alfresco.constants.URL_PAGECONTEXT + 'user/' + encodeURI(oRecord.getData("archivedBy")) + '/profile">' + $html(fullName) + '</a>';
         var meta = this.msg("message.metadata", Alfresco.util.formatDate(Alfresco.util.fromISO8601(oRecord.getData("archivedDate"))), profileLink);
         var item = oRecord.getData("isContentType") ? '<a href="' + viewUrl + '?a=true">' + $html(oRecord.getData("name")) + '</a>' : $html(oRecord.getData("name"));
         
         var desc = '<div class="name">' + item + '</div><div class="desc">' + meta + '</div>'
                  + '<div class="desc">' + $html(oRecord.getData("displayPath")) + '</div>';
         
         elCell.innerHTML = desc;
      },
      
      /**
       * Actions custom datacell formatter
       *
       * @method UserTrashcan_renderCellActions
       */
      renderCellActions: function UserTrashcan_renderCellActions(elCell, oRecord, oColumn, oData)
      {
         Dom.setStyle(elCell.parentNode, "vertical-align", "middle");
         Dom.setStyle(elCell.parentNode, "text-align", "right");
         
         var nodeRef = oRecord.getData("nodeRef"),
             nodeName = oRecord.getData("name");
         
         this._createActionButton(
            elCell, nodeRef.split("/")[3], "button.recover",
            function(event, obj) 
            {
               if (!this.isRecoverEnabled())
               {
                  return;
               }
               
               this._disableRecover();
               
               this.restoringPopup = Alfresco.util.PopupManager.displayMessage(
               {
                  displayTime: 0,
                  effect: null,
                  spanClass: "wait",
                  text: me.msg("message.recover.inprogress")
               });

               // make ajax call to Recover the item
               Alfresco.util.Ajax.request(
               {
                  url: Alfresco.constants.PROXY_URI + "api/archive/" + obj.nodeRef.replace(":/",""),
                  method: "PUT",
                  successCallback:
                  {
                     fn: this._onRecoverSuccess,
                     obj: obj,
                     scope: this
                  },
                  failureCallback: 
                  {
                     fn: this._onRecoverFailure,
                     obj: obj,
                     scope: this
                  }
               });
            },
            {
               nodeRef: nodeRef,
               name: nodeName
            }
         );
         var me = this;
         this._createActionButton(
            elCell, nodeRef.split("/")[3], "button.delete",
            function(event, obj) 
            {
               // confirm this brutal operation with the user
               Alfresco.util.PopupManager.displayPrompt(
               {
                  title: me.msg("button.delete"),
                  text: me.msg("message.delete.confirm"),
                  buttons: [
                     {
                        text: me.msg("button.ok"),
                        handler: function()
                        {
                           this.destroy();
                           // make ajax call to Delete the item
                           Alfresco.util.Ajax.request(
                           {
                              url: Alfresco.constants.PROXY_URI + "api/archive/" + obj.nodeRef.replace(":/",""),
                              method: "DELETE",
                              successCallback:
                              {
                                 fn: me._onDeleteSuccess,
                                 obj: obj,
                                 scope: me
                              },
                              failureMessage: me.msg("message.delete.failure", nodeName)
                           });
                        }
                     },
                     {
                        text: me.msg("button.cancel"),
                        handler: function()
                        {
                           this.destroy();
                        },
                        isDefault: true
                     }
                  ]
               });
            },
            {
               nodeRef: nodeRef,
               name: nodeName
            }
         );
      },
      
      /**
       * Enables recover actions
       */
      _enableRecover: function UT_enableRecover()
      {
         this._isRecoverEnabled = true;
      },
      
      /**
       * Disables recover actions
       */
      _disableRecover: function UT_disableRecover()
      {
         this._isRecoverEnabled = false;
      },
      
      /**
       * Indicates is recover action is enabled
       */
      isRecoverEnabled: function UT_isRecoverEnabled()
      {
         return this._isRecoverEnabled !== false;
      },
      
      /**
       * Callback handler used when a deleted item was recovered
       * 
       * @method _onRecoverSuccess
       * @param response {object}
       * @param obj {object}
       */
      _onRecoverSuccess: function UT__onRecoverSuccess(response, obj)
      {
         this.restoringPopup.destroy();
         this._enableRecover();
         
         Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg("message.recover.success", obj.name)
         });
         
         this.refreshDataTable();
      },
      
      /**
       * Callback handler used when a deleted item wasn't recovered
       * 
       * @method _onRecoverSuccess
       * @param response {object}
       * @param obj {object}
       */
      _onRecoverFailure: function UT__onRecoverFailure(response, obj)
      {
         this.restoringPopup.destroy();
         this._enableRecover();
         
         Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg("message.recover.failure", obj.name)
         });
         
         this.refreshDataTable();
      },
      
      /**
       * Callback handler used when a deleted item was purged
       * 
       * @method _onDeleteSuccess
       * @param response {object}
       * @param obj {object}
       */
      _onDeleteSuccess: function UT__onDeleteSuccess(response, obj)
      {
         Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg("message.delete.success", obj.name)
         });
         
         this.refreshDataTable();
      },

      /**
       * YUI WIDGET EVENT HANDLERS
       */
       
      /**
       * Selected items action menu event handler
       * @method onActionItemClick.
       * @param sType, aArgs, p_obj
       */
      onActionItemClick: function UT_onActionItemClick(sType, aArgs, p_obj)
      {
         var items = [],
             dt = this.widgets.dataTable.getDataTable(),
             rows = dt.getTbodyEl().rows;
         for (var i = 0; i < rows.length; i++)
         {
            if (rows[i].cells[0].getElementsByTagName('input')[0].checked)
            {
               var data = dt.getRecord(i);
               if (data)
               {
                  items.push(data);
               }
            }
         }
         
         var me = this;
         switch (aArgs[1]._oAnchor.className.split(" ")[0])
         {
            case "delete-item":
               // confirm this brutal operation with the user
               Alfresco.util.PopupManager.displayPrompt(
               {
                  title: me.msg("button.delete"),
                  text: me.msg("message.delete.confirm"),
                  buttons: [
                     {
                        text: me.msg("button.ok"),
                        handler: function()
                        {
                           this.destroy();
                           var failed = [],
                               total = 0;
                           for (var i=0; i<items.length; i++)
                           {
                              // make ajax calls to Delete the items
                              Alfresco.util.Ajax.request(
                              {
                                 url: Alfresco.constants.PROXY_URI + "api/archive/" + items[i].getData("nodeRef").replace(":/",""),
                                 method: "DELETE",
                                 failureCallback: {
                                    fn: function() {
                                       failed.push(items[i].getData("name"));
                                       total++;
                                    },
                                    obj: items[i],
                                    scope: me
                                 },
                                 successCallback: {
                                    fn: function() {
                                       total++;
                                    },
                                    obj: items[i],
                                    scope: me
                                 }
                              });
                           }
                           var completeFn = function() {
                              if (total === items.length)
                              {
                                 Alfresco.util.PopupManager.displayPrompt(
                                 {
                                    title: me.msg("message.delete.report"),
                                    text: me.msg("message.delete.report-info", (items.length-failed.length), failed.length)
                                 });
                                 me.refreshDataTable();
                              }
                              else
                              {
                                 setTimeout(completeFn, 500);
                              }
                           };
                           setTimeout(completeFn, 500);
                        }
                     },
                     {
                        text: me.msg("button.cancel"),
                        handler: function()
                        {
                           this.destroy();
                        },
                        isDefault: true
                     }
                  ]
               });
               break;
            case "recover-item":
               var failed = [],
                   total = 0;
               
               if (!me.isRecoverEnabled())
               {
                  return;
               }
               
               me._disableRecover();
               
               me.restoringPopup = Alfresco.util.PopupManager.displayMessage(
               {
                  displayTime: 0,
                  effect: null,
                  spanClass: "wait",
                  text: me.msg("message.recover.inprogress")
               });

               for (var i=0; i<items.length; i++)
               {
                  var index = i;
                  // make ajax call to Recover the item
                  Alfresco.util.Ajax.request(
                  {
                     url: Alfresco.constants.PROXY_URI + "api/archive/" + items[i].getData("nodeRef").replace(":/",""),
                     method: "PUT",
                     failureCallback: {
                        fn: function() {
                           failed.push(items[index].getData("name"));
                           total++;
                        },
                        obj: items[i],
                        scope: me
                     },
                     successCallback: {
                        fn: function() {
                           total++;
                        },
                        obj: items[i],
                        scope: me
                     }
                  });
               }
               var completeFn = function() {
                  if (total === items.length)
                  {
                     me.restoringPopup.destroy();
                     me._enableRecover();
                     
                     Alfresco.util.PopupManager.displayPrompt(
                     {
                        title: me.msg("message.recover.report"),
                        text: me.msg("message.recover.report-info", (items.length-failed.length), failed.length)
                     });
                     me.refreshDataTable();
                  }
                  else
                  {
                     setTimeout(completeFn, 250);
                  }
               };
               setTimeout(completeFn, 250);
               break;
         }
      },
      
      /**
       * Select items menu item event handler
       * @method onSelectItemClick.
       * @param sType, aArgs, p_obj
       */
      onSelectItemClick: function UT_onSelectItemClick(sType, aArgs, p_obj)
      {
         switch (aArgs[1]._oAnchor.className.split(" ")[0])
         {
            case "select-all":
               this._selectAll();
               break;
            case "select-invert":
               this._invertSelection();
               break;
            case "select-none":
               this._deselectAll();
               break;
         }
      },
      
      /**
       * Select all items.
       * @method _selectAll
       */
      _selectAll: function UT__selectAll()
      {
         var rows = this.widgets.dataTable.getDataTable().getTbodyEl().rows;
         for (var i = 0; i < rows.length; i++)
         {
            rows[i].cells[0].getElementsByTagName('input')[0].checked = true;
         }
         this._updateSelectedItemsMenu();
      },
      
      /**
       * Deselect all items.
       * @method _deselectAll
       */
      _deselectAll: function UT__deselectAll()
      {
         var rows = this.widgets.dataTable.getDataTable().getTbodyEl().rows;
         for (var i = 0; i < rows.length; i++)
         {
            rows[i].cells[0].getElementsByTagName('input')[0].checked = false;
         }
         this._updateSelectedItemsMenu();
      },
      
      /**
       * Invert selection of items.
       * @method _invertSelection
       */
      _invertSelection: function UT__invertSelection()
      {
         var rows = this.widgets.dataTable.getDataTable().getTbodyEl().rows;
         for (var i = 0; i < rows.length; i++)
         {
            var check = rows[i].cells[0].getElementsByTagName('input')[0];
            check.checked = !check.checked;
         }
         this._updateSelectedItemsMenu();
      },
      
      /**
       * Update the disabled status of the multi-select action menu based on the state of the item checkboxes
       * @method _updateSelectedItemsMenu
       */
      _updateSelectedItemsMenu: function UT__updateSelectedItemsMenu()
      {
         this.widgets.actionMenu.set("disabled", true);
         var rows = this.widgets.dataTable.getDataTable().getTbodyEl().rows;
         for (var i = 0; i < rows.length; i++)
         {
            if (rows[i].cells[0].getElementsByTagName('input')[0].checked)
            {
               this.widgets.actionMenu.set("disabled", false);
               break;
            }
         }
      },
      
      /**
       * OnSearch button click handler
       * 
       * @method onSearch
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onSearch: function UT_onSearch(e, p_obj)
      {
         this.performSearch();
      },
      
      /**
       * onClear button click handler
       * 
       * @method onClear
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onClear: function UT_onClear(e, p_obj)
      {
         Dom.get(this.id + "-search-text").value = "";
         if (this.searchText.length !== 0)
         {
            this.searchText = "";
            this.refreshDataTable();
         }
      },
      
      /**
       * OnEmpty button click handler
       * 
       * @method onEmpty
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onEmpty: function UT_onEmpty(e, p_obj)
      {
         var me = this;
         
         // confirm this brutal operation with the user
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: me.msg("button.empty"),
            text: me.msg("message.empty.confirm"),
            buttons: [
            {
               text: me.msg("button.ok"),
               handler: function()
               {
                  this.destroy();
                  
                  // call api to remove all items from the trashcan
                  // use the progress animation as this operation may take a while
                  var progressPopup = Alfresco.util.PopupManager.displayMessage(
                  {
                     displayTime: 0,
                     effect: null,
                     text: me.msg("message.empty.inprogress")
                  });
                  
                  Alfresco.util.Ajax.request(
                  {
                     url: Alfresco.constants.PROXY_URI + "api/archive/workspace/SpacesStore",
                     method: "DELETE",
                     successCallback:
                     {
                        fn: function success(data)
                        {
                            progressPopup.destroy();
                            me.refreshDataTable();
                        }
                     },
                     failureCallback:
                     {
                        fn: function failure(data)
                        {
                           progressPopup.destroy();
                           
                           Alfresco.util.PopupManager.displayPrompt(
                           {
                              text: me.msg("message.recover.failure")
                           });
                        }
                     }
                  });
               }
            },
            {
               text: me.msg("button.cancel"),
               handler: function()
               {
                  this.destroy();
               },
               isDefault: true
            }]
         });
      },
      
      /**
       * onPageLess button click handler
       * 
       * @method onPageLess
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onPageLess: function UT_onPageLess(e, p_obj)
      {
         if (this.skipCount > 0)
         {
            this.skipCount -= this.pageSize;
         }
         this.refreshDataTable();
      },
      
      /**
       * onPageMore button click handler
       * 
       * @method onPageMore
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onPageMore: function UT_onPageMore(e, p_obj)
      {
         this.skipCount += this.pageSize;
         this.refreshDataTable();
      },
      
      /**
       * Create a generic YUI action button hooked into the appropriate parent element
       * 
       * @method createActionButton
       * @param el {object} Parent element to add button
       * @param id {string} Button ID
       * @param labelid {string} Button label message ID
       * @param action {function} Action event function
       * @param obj {object} Action event parameter object
       * @private
       */
      _createActionButton: function UT_createActionButton(el, id, labelId, action, obj)
      {
         var me = this;
         var span = document.createElement("span");
         span.id = me.id + id;
         var btn = new YAHOO.widget.Button(
         {
             container: me.id + id
         });
         btn.set("label", me.msg(labelId));
         btn.set("onclick",
         {
            fn: action,
            obj: obj,
            scope: me
         });
         el.appendChild(span);
      },
      
      /**
       * Update the current search terms from the search text field and perform a search.
       * 
       * @method performSearch
       */
      performSearch: function UT_performSearch()
      {
         // MNT-12799: resetting 'skipCount' to show search results from the first page
         this.skipCount = 0;

         var searchText = YAHOO.lang.trim(Dom.get(this.id + "-search-text").value);
         if (searchText.length !== 0)
         {
            this.searchText = searchText;
            this.refreshDataTable();
         }
      },
      
      /**
       * Refresh the list after an action has occured
       * 
       * @method refreshDataTable
       */
      refreshDataTable: function UT_refreshDataTable()
      {
         // we alway ask for an extra item to see if there are more for the next page
         var params = "maxItems=" + (this.pageSize + 1) + "&skipCount=" + this.skipCount;
         if (this.searchText.length !== 0)
         {
            var search = this.searchText;
            if (search.match("\\*") != "*")
            {
               search += "*";
            }
            params += "&nf=" + encodeURIComponent(search);
         }
         this.widgets.dataTable.loadDataTable(params);
      }
   });
})();
