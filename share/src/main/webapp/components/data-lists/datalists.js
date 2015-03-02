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
 * Data Lists: DataLists component.
 * 
 * Displays a list of datalists
 * 
 * @namespace Alfresco
 * @class Alfresco.component.DataLists
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Selector = YAHOO.util.Selector,
      Bubbling = YAHOO.Bubbling;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $combine = Alfresco.util.combinePaths;

   /**
    * DataLists constructor.
    * 
    * @param htmlId {String} The HTML id of the parent element
    * @return {Alfresco.component.DataLists} The new DataLists instance
    * @constructor
    */
   Alfresco.component.DataLists = function(htmlId)
   {
      Alfresco.component.DataLists.superclass.constructor.call(this, "Alfresco.component.DataLists", htmlId, ["button", "container"]);

      /**
       * Decoupled event listeners
       */
      Bubbling.on("dataListCreated", this.onDataListCreated, this);
      Bubbling.on("dataListDetailsUpdated", this.onDataListDetailsUpdated, this);
      
      // Initialise prototype properties
      this.dataLists = {};
      this.dataListsLength = null;
      this.containerNodeRef = null;
      this.listTypeTitles = null;
      
      return this;
   };
   
   /**
    * Extend from Alfresco.component.Base
    */
   YAHOO.extend(Alfresco.component.DataLists, Alfresco.component.Base,
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
           * @default ""
           */
          siteId: "",

          /**
           * ContainerId representing root container.
           *
           * @property containerId
           * @type string
           * @default "dataLists"
           */
          containerId: "dataLists",
          
          /**
           * ListId representing currently selected list
           *
           * @property listId
           * @type string
           */
          listId: "",

          /**
           * List types when creating new Data Lists
           *
           * @property listTypes
           * @type Array
           */
          listTypes: []
      },

      /**
       * Data Lists metadata retrieved from the Repository
       *
       * @param dataLists
       * @type Object
       */
      dataLists: null,

      /**
       * Number of data lists in the Repository
       *
       * @param dataListsLength
       * @type Object
       */
      dataListsLength: null,

      /**
       * NodeRef of the Data Lists container retrieved from the Repository
       *
       * @param containerNodeRef
       * @type Object
       */
      containerNodeRef: null,

      /**
       * List title look-up from listType
       *
       * @property listTypeTitles
       * @type Object
       */
      listTypeTitles: null,

      /**
       * Fired by YUI when parent element is available for scripting.
       *
       * @method onReady
       */
      onReady: function DataLists_onReady()
      {
         this.widgets.newList = Alfresco.util.createYUIButton(this, "newListButton", this.onNewList,
         {
            disabled: true
         });
         // Retrieve the lists from the specified Site & Container
         this.populateDataLists(
         {
            fn: function DataLists_onReady_callback()
            {
               this.renderDataLists();

               // Select current list, if relevant
               if (this.options.listId.length > 0)
               {
                  Bubbling.fire("activeDataListChanged",
                  {
                     dataList: this.dataLists[this.options.listId],
                     scrollTo: true
                  });
               }
               else
               {
                  YAHOO.Bubbling.fire("hideFilter");
               }
               
               if (this.dataListsLength === 0 || window.location.hash === "#new")
               {
                  this.widgets.newList.fireEvent("click");
               }
            },
            scope: this
         });
      },
      
      /**
       * Retrieves the Data Lists from the Repo
       *
       * @method populateDataLists
       * @param callback {Object} Optional callback literal {fn, scope, obj} whose function is invoked once list has been retrieved
       */
      populateDataLists: function DataLists_populateDataLists(p_callback)
      {
         /**
          * Success handler for Data Lists request
          * @method fnSuccess
          * @param response {Object} Ajax response object literal
          * @param obj {Object} Callback object from original function call
          */
         var fnSuccess = function DataLists_pDL_fnSuccess(response, p_obj)
         {
            var lists = response.json.datalists,
               list;
            
            this.dataLists = {};
            this.containerNodeRef = new Alfresco.util.NodeRef(response.json.container);
            this.widgets.newList.set("disabled", !response.json.permissions.create);
            
            for (var i = 0, ii = lists.length; i < ii; i++)
            {
               list = lists[i];
               this.dataLists[list.name] = list;
            }
            this.dataListsLength = lists.length;
            
            if (p_callback && (typeof p_callback.fn == "function"))
            {
               p_callback.fn.call(p_callback.scope || this, p_callback.obj);
            }
         };

         /**
          * Failure handler for Data Lists request
          * @method fnFailure
          * @param response {Object} Ajax response object literal
          */
         var fnFailure = function DataLists_pDL_fnFailure(response)
         {
            if (response.status == 401)
            {
               // Our session has likely timed-out, so refresh to offer the login page
               window.location.reload();
            }
            else
            {
               this.dataLists = null;
               this.containerNodeRef = null;
               this.widgets.newList.set("disabled", true);
               var errorMsg = "";
               try
               {
                  errorMsg = $html(YAHOO.lang.JSON.parse(response.responseText).message);
               }
               catch(e)
               {
                  errorMsg = this.msg("message.error-unknown");
               }
            }
         };
         
         Alfresco.util.Ajax.jsonGet(
         {
            url: $combine(Alfresco.constants.PROXY_URI, "slingshot/datalists/lists/site", this.options.siteId, this.options.containerId),
            successCallback:
            {
               fn: fnSuccess,
               obj: p_callback,
               scope: this
            },
            failureCallback:
            {
               fn: fnFailure,
               scope: this
            }
         });
      },

      /**
       * Renders the Data Lists into the DOM
       *
       * @method renderDataLists
       * @param highlightName {String} Optional name of list to highlight after rendering
       */
      renderDataLists: function DataLists_renderDataLists(p_highlightName)
      {
         var me = this,
            listsContainer = Dom.get(this.id + "-lists"),
            selectedClass = "selected";
         
         listsContainer.innerHTML = "";
         
         /**
          * Click handler for selecting Data List
          * @method fnOnClick
          */
         var fnOnClick = function DataLists_renderDataLists_fnOnClick()
         {
            return function DataLists_renderDataLists_onClick()
            {
               var lis = Selector.query("li", listsContainer);
               Dom.removeClass(lis, selectedClass);
               Dom.addClass(this, selectedClass);
               return true;
            };
         };

         /**
          * Click handler for edit Data List
          * @method fnEditOnClick
          * @param listName {String} Name of the Data List
          */
         var fnEditOnClick = function DataLists_renderDataLists_fnEditOnClick(listName, enabled)
         {
            return function DataLists_renderDataLists_onEditClick(e)
            {
               if (enabled)
               {
                  me.onEditList(listName);
               }
               Event.stopEvent(e || window.event);
            };
         };

         /**
          * Click handler for edit Data List
          * @method fnDeleteOnClick
          * @param listName {String} Name of the Data List
          */
         var fnDeleteOnClick = function DataLists_renderDataLists_fnDeleteOnClick(listName, enabled)
         {
            return function DataLists_renderDataLists_onEditClick(e)
            {
               if (enabled)
               {
                  me.onDeleteList(listName);
               }
               Event.stopEvent(e || window.event);
            };
         };

         try
         {
            var lists = this.dataLists,
               list,
               permissions,
               elHighlight = null,
               container, el, elEdit, elDelete, elLink, elText;

            if (this.dataListsLength === 0)
            {
               listsContainer.innerHTML = '<div class="no-lists">' + this.msg("message.no-lists") + '</div>';
            }
            else
            {
               container = document.createElement("ul");
               listsContainer.appendChild(container);

               // Create the DOM structure: <li onclick><a class='filter-link' title href><span class='edit' onclick></span><span class='delete' onclick></span>"text"</a></li>
               for (var index in lists)
               {
                  if (lists.hasOwnProperty(index))
                  {
                     list = lists[index];
                     permissions = list.permissions;
                     
                     // Build the DOM elements
                     el = document.createElement("li");
                     el.onclick = fnOnClick();
                     elEdit = document.createElement("span");
                     if (permissions["edit"])
                     {
                        elEdit.className = "edit";
                        elEdit.title = this.msg("label.edit-list");
                        elEdit.onclick = fnEditOnClick(list.name, true);
                     }
                     else
                     {
                        elEdit.className = "edit-disabled";
                        elEdit.onclick = fnEditOnClick(list.name, false);
                     }
                     elDelete = document.createElement("span");
                     if (permissions["delete"])
                     {
                        elDelete.className = "delete";
                        elDelete.title = this.msg("label.delete-list");
                        elDelete.onclick = fnDeleteOnClick(list.name, true);
                     }
                     else
                     {
                        elDelete.className = "delete-disabled";
                        elDelete.onclick = fnDeleteOnClick(list.name, false);
                     }
                     elLink = document.createElement("a");
                     elLink.className = "filter-link";
                     elLink.title = list.description;
                     elLink.href = "data-lists?list=" + $html(list.name);
                     elText = document.createTextNode(list.title);

                     // Build the DOM structure with the new elements
                     elLink.appendChild(elDelete);
                     elLink.appendChild(elEdit);
                     elLink.appendChild(elText);
                     el.appendChild(elLink);
                     container.appendChild(el);

                     // Mark current list as selected
                     if (list.name == this.options.listId)
                     {
                        Dom.addClass(el, "selected");
                     }
                     
                     // Make a note of a highlight request match
                     if (list.name == p_highlightName)
                     {
                        elHighlight = el;
                     }
                  }
               }
               
               if (elHighlight)
               {
                  Alfresco.util.Anim.pulse(elHighlight);
               }
            }
         }
         catch(e)
         {
            listsContainer.innerHTML = '<span class="error">' + this.msg("message.error-unknown") + '</span>';
         }
      },
      
      /**
       * Look-up a list title from it's type
       *
       * @method getListTypeTitle
       * @param listType {string} List type
       * @return {string} List type title
       */
      getListTypeTitle: function DataList_getListTitle(listType)
      {
         if (this.listTypeTitles === null)
         {
            var list;
            this.listTypeTitles = {};
            
            for (var i = 0, ii = this.options.listTypes.length; i < ii; i++)
            {
               list = this.options.listTypes[i];
               this.listTypeTitles[list.name] = list.title;
            }
         }
         return this.listTypeTitles[listType] || "";
      },


      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR PAGE EVENTS
       * Disconnected event handlers for inter-component event notification
       */

      /**
       * Data List created event handler
       *
       * @method onDataListCreated
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (unused)
       */
      onDataListCreated: function DataList_onDataListCreated(layer, args)
      {
         var obj = args[1];
         if ((obj !== null) && (obj.name !== null))
         {
            this.populateDataLists(
            {
               fn: function DataList_onDataListCreated_callback(p_obj)
               {
                  this.renderDataLists(p_obj);
               },
               obj: obj.name,
               scope: this
            });
         }
      },

      /**
       * Data List modified event handler
       *
       * @method onDataListDetailsUpdated
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (unused)
       */
      onDataListDetailsUpdated: function DataList_onDataListDetailsUpdated(layer, args)
      {
         var obj = args[1];
         if ((obj !== null) && (obj.dataList !== null))
         {
            this.renderDataLists(obj.dataList.name);
         }
      },


      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * New List button click handler
       *
       * @method onNewList
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onNewList: function DataLists_onNewList(e, p_obj)
      {
         var destination = this.containerNodeRef.nodeRef,
            selectedClass = "theme-bg-selected";

         var fnPopulateItemTypes = function DataLists_onNewList_fnPopulateItemTypes(domId, formFieldId, p_form)
         {
            var fnOnClick = function DataLists_oNL_fnOnClick(myDiv, listType)
            {
               return function DataLists_oNL_onClick()
               {
                  var divs = Selector.query("div", domId);
                  Dom.removeClass(divs, selectedClass);
                  Dom.addClass(myDiv, selectedClass);
                  Dom.get(formFieldId).value = listType;
                  p_form.validate();
                  return false;
               };
            };

            var list, el, containerEl = Dom.get(domId);
            
            for (var i = 0, ii = this.options.listTypes.length; i < ii; i++)
            {
               list = this.options.listTypes[i];
               el = document.createElement("div");

               el.innerHTML = '<h4><a href="#" tabindex="0">' + $html(list.title) + '</a></h4><span>' + $html(list.description) + '</span>';
               el.onclick = fnOnClick(el, list.name);
               containerEl.appendChild(el);
            }
         };

         // Intercept before dialog show
         var doBeforeDialogShow = function DataLists_onNewList_doBeforeDialogShow(p_form, p_dialog)
         {
            Alfresco.util.populateHTML(
               [ p_dialog.id + "-dialogTitle", this.msg("label.new-list.title") ],
               [ p_dialog.id + "-dialogHeader", this.msg("label.new-list.header") ],
               [ p_dialog.id + "-dataListItemType", this.msg("label.item-type") ]
            );
            fnPopulateItemTypes.apply(this, [p_dialog.id + "-itemTypesContainer", p_dialog.id + "-dataListItemType-field", p_form]);

            // Must choose a list type
            var fnValidateListChoice = function DataLists_oNL_dBDS_fnValidateListChoice(field, args, event, form, silent, message)
            {
               return (field.value.length > 0);
            };
            p_form.addValidation(p_dialog.id + "-dataListItemType-field", fnValidateListChoice, null, null, null, { validationType: "mandatory" });
         };

         var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&showCancelButton=true",
         {
            itemKind: "type",
            itemId: "dl:dataList",
            destination: destination,
            mode: "create",
            submitType: "json"
         });

         // Using Forms Service, so always create new instance
         var newList = new Alfresco.module.SimpleDialog(this.id + "-newList");

         newList.setOptions(
         {
            width: "33em",
            templateUrl: templateUrl,
            actionUrl: null,
            destroyOnHide: true,
            doBeforeDialogShow:
            {
               fn: doBeforeDialogShow,
               scope: this
            },
            onSuccess:
            {
               fn: function DataLists_onNewList_success(response)
               {
                  var nodeRef = new Alfresco.util.NodeRef(response.json.persistedObject),
                     listTitle = response.config.dataObj["prop_cm_title"],
                     listTypeTitle = this.getListTypeTitle(response.config.dataObj["prop_dl_dataListItemType"]);

                  Bubbling.fire("dataListCreated");
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.new-list.success", listTitle)
                  });
                  // Activity post
                  Alfresco.Share.postActivity(this.options.siteId, "org.alfresco.datalists.list-created", listTitle + " (" + listTypeTitle + ")", "data-lists?list={cm:name}",
                  {
                     appTool: "datalists",
                     nodeRef: nodeRef.toString()
                  });
               },
               scope: this
            },
            onFailure:
            {
               fn: function DataLists_onNewList_failure(response)
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.new-list.failure")
                  });
               },
               scope: this
            }
         }).show();
      },

      /**
       * Edit List event handler
       *
       * @method onEditList
       * @param listName {string} Name of the list to edit
       */
      onEditList: function DataLists_onEditList(p_listName)
      {
         var datalist = this.dataLists[p_listName];

         // Intercept before dialog show
         var doBeforeDialogShow = function DataLists_onEditList_doBeforeDialogShow(p_form, p_dialog)
         {
            Alfresco.util.populateHTML(
               [ p_dialog.id + "-dialogTitle", this.msg("message.edit-list.title") ],
               [ p_dialog.id + "-dialogHeader", this.msg("message.edit-list.header") ]
            );

            // Must set a title (UI constraint for usability)
            p_form.addValidation(p_dialog.id + "_prop_cm_title", Alfresco.forms.validation.mandatory, null, "keyup");
         };

         var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&mode={mode}&submitType={submitType}&showCancelButton=true",
         {
            itemKind: "node",
            itemId: datalist.nodeRef,
            mode: "edit",
            submitType: "json"
         });

         // Using Forms Service, so always create new instance
         var editList = new Alfresco.module.SimpleDialog(this.id + "-editList");

         editList.setOptions(
         {
            width: "33em",
            templateUrl: templateUrl,
            actionUrl: null,
            destroyOnHide: true,
            doBeforeDialogShow:
            {
               fn: doBeforeDialogShow,
               scope: this
            },
            onSuccess:
            {
               fn: function DataLists_onEditList_success(response, p_obj)
               {
                  var dataObj = response.config.dataObj,
                     nodeRef = new Alfresco.util.NodeRef(p_obj.nodeRef),
                     oldTitle = p_obj.title,
                     listTypeTitle = this.getListTypeTitle(p_obj.itemType);

                  p_obj.title = dataObj["prop_cm_title"];
                  p_obj.description = dataObj["prop_cm_description"];
                  
                  Bubbling.fire("dataListDetailsUpdated",
                  {
                     dataList: p_obj
                  });
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.edit-list.success", p_obj.title)
                  });
                  // Activity post
                  Alfresco.Share.postActivity(this.options.siteId, "org.alfresco.datalists.list-updated", p_obj.title + " (" + listTypeTitle + ")", "data-lists?list={cm:name}",
                  {
                     appTool: "datalists",
                     nodeRef: nodeRef.toString()
                  });
               },
               obj: datalist,
               scope: this
            },
            onFailure:
            {
               fn: function DataLists_onEditList_failure(response)
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.edit-list.failure")
                  });
               },
               scope: this
            }
         }).show();
      },

      /**
       * Delete List event handler
       *
       * @method onDeleteList
       * @param listName {string} Name of the list to edit
       */
      onDeleteList: function DataLists_onDeleteList(p_listName)
      {
         var datalist = this.dataLists[p_listName],
            me = this;

         var fnActionDeleteConfirm = function DataLists_onDeleteList_confirm(p_datalist)
         {
            var nodeRef = new Alfresco.util.NodeRef(p_datalist.nodeRef),
               listTypeTitle = this.getListTypeTitle(p_datalist.itemType);
            
            Alfresco.util.Ajax.request(
            {
               method: Alfresco.util.Ajax.DELETE,
               url: Alfresco.constants.PROXY_URI + "slingshot/datalists/list/node/" + nodeRef.uri,
               successCallback:
               {
                  fn: function DataLists_onDeleteList_confirm_success(response, p_obj)
                  {
                     // Activity post
                     Alfresco.Share.postActivity(this.options.siteId, "org.alfresco.datalists.list-deleted", datalist.title + " (" + listTypeTitle + ")", "data-lists",
                     {
                        appTool: "datalists",
                        nodeRef: nodeRef.toString()
                     }, 

                     this.bind(function()
                     {
                        // If we deleted the current list, then redirect to "data-lists"
                        if (p_obj.name == this.options.listId)
                        {
                           window.location = "data-lists";
                           return;
                        }

                        Alfresco.util.PopupManager.displayMessage(
                        {
                           text: this.msg("message.delete-list.success")
                        });
                     
                        delete this.dataLists[p_datalist.name];
                        this.dataListsLength--;
                        this.renderDataLists();
                     }));
                  },
                  obj: p_datalist,
                  scope: this
               },
               failureCallback:
               {
                  fn: function DataLists_onDeleteList_confirm_failure(response)
                  {
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: this.msg("message.delete-list.failure")
                     });
                  },
                  scope: this
               }
            });
         };

         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg("message.delete-list.title"),
            text: this.msg("message.delete-list.description", $html(datalist.title)),
            buttons: [
            {
               text: this.msg("button.delete"),
               handler: function DataLists_onDeleteList_delete()
               {
                  this.destroy();
                  fnActionDeleteConfirm.call(me, datalist);
               }
            },
            {
               text: this.msg("button.cancel"),
               handler: function DataLists_onDeleteList_cancel()
               {
                  this.destroy();
               },
               isDefault: true
            }]
         });
      }
   });
})();