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
 * GroupsList component.
 * 
 * @namespace Alfresco
 * @class Alfresco.GroupsList
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
    * GroupsList constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.GroupsList} The new GroupsList instance
    * @constructor
    */
   Alfresco.GroupsList = function(htmlId)
   {
      Alfresco.GroupsList.superclass.constructor.call(this, "Alfresco.GroupsList", htmlId, ["button", "container", "datasource", "datatable", "json"]);
      
      /* Initialise prototype properties */
      this.listWidgets = [];
      this.uniqueRecordId = 1;
      
      // Decoupled event listeners
      YAHOO.Bubbling.on("itemSelected", this.onItemSelected, this);
   
      return this;
   };
   
   YAHOO.extend(Alfresco.GroupsList, Alfresco.component.Base,
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
          * siteId to GroupsList in. "" if GroupsList should be cross-site
          * 
          * @property siteId
          * @type string
          */
         siteId: "",
         
         /**
          * Available roles for the site.
          * 
          * @property roles
          * @type Array
          */
         roles: []
      },

      /**
       * Object container for storing YUI widget instances used in the list cells
       * @property listWidgets
       * @type array
       */
      listWidgets: null,
      
      /**
       * Auto-incremented unique id for each element added to the table.
       * @property uniqueRecordId
       * @type integer
       */
      uniqueRecordId: null,
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function GroupsList_onReady()
      {   
         // WebKit CSS fix
         if (YAHOO.env.ua.webkit > 0)
         {
            Dom.setStyle(this.id + "-backTo", "vertical-align", "sub");
         }

         // button to add all groups in the list 
         this.widgets.addButton = Alfresco.util.createYUIButton(this, "add-button", this.addButtonClick);
         
         // File Select menu button
         this.widgets.allRolesSelect = Alfresco.util.createYUIButton(this, "selectallroles-button", this.onSelectAllRoles,
         {
            type: "menu", 
            menu: "selectallroles-menu"
         });
         
         // setup the datasource
         this.widgets.dataSource = new YAHOO.util.DataSource([],
         {
            responseType: YAHOO.util.DataSource.TYPE_JSARRAY
         });
         
         // setup of the datatable
         this._setupDataTable();

         // make sure the add button is initially disabled
         this._enableDisableAddButton();

         // Hook remove action handler
         var me = this,
            fnRemoveItemHandler = function GroupsList_fnRemoveItemHandler(layer, args)
            {
               // call the remove method
               me.removeItem.call(me, args[1].anchor);
               args[1].stop = true;
               return true;
            };
         
         YAHOO.Bubbling.addDefaultAction("remove-item-button", fnRemoveItemHandler);
         
         // show the component now, this avoids painting issues of the dropdown button
         Dom.setStyle(this.id + "-invitationBar", "visibility", "visible");
      },
      
      /**
       * Setup YUI DataTable widget
       *
       * @method _setupDataTable
       * @private
       */
      _setupDataTable: function GroupsList_setupDataTable()
      {
         /**
          * DataTable Cell Renderers
          *
          * Each cell has a custom renderer defined as a custom function. See YUI documentation for details.
          * These MUST be inline in order to have access to the Alfresco.GroupsList class (via the "me" variable).
          */
         var me = this;

         /**
          * Description/detail custom datacell formatter
          *
          * @method renderCellDescription
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellDescription = function GroupsList_renderCellDescription(elCell, oRecord, oColumn, oData)
         {
            // we currently render all results the same way
            var itemName = oRecord.getData("itemName"),
               displayName = oRecord.getData("displayName");

            elCell.innerHTML = '<h3 class="itemname">' + $html(displayName) + ' <span class="lighter theme-color-1">(' + $html(itemName) + ')</span></h3>';
         };

         /**
          * Role selector datacell formatter
          *
          * @method renderCellRole
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellRole = function GroupsList_renderCellActions(elCell, oRecord, oColumn, oData)
         {  
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            Dom.setStyle(elCell, "overflow", "visible");

            // cell where to add the element
            var cell = new Element(elCell),
               id = oRecord.getData('id'),
               buttonId = me.id + '-roleselector-' + id;
            
            // create a clone of the template
            var actionsColumnTemplate = Dom.get(me.id + '-role-column-template'),
               templateInstance = actionsColumnTemplate.cloneNode(true);
            templateInstance.setAttribute("id", "actionsDiv" + id);
            Dom.setStyle(templateInstance, "display", "");

            // define the role dropdown menu and the event listeners
            var rolesMenu = [], role;
            for (var i = 0, j = me.options.roles.length; i < j; i++)
            {
               role = me.options.roles[i];
               rolesMenu.push(
               {
                  text: me.msg("role." + role),
                  value: role,
                  onclick:
                  {
                     fn: me.onRoleSelect,
                     obj:
                     {
                        record: oRecord,
                        role: role
                     },
                     scope: me
                  }
               });
            }

            // Insert the templateInstance to the column.
            cell.appendChild (templateInstance);

            // Create a yui button for the role selector.
            var fButton = Dom.getElementsByClassName("role-selector-button", "button", templateInstance);
            var button = new YAHOO.widget.Button(fButton[0],
            {
               type: "menu",
               name: buttonId,
               label: me.getRoleLabel(oRecord) + " " + Alfresco.constants.MENU_ARROW_SYMBOL,
               menu: rolesMenu
            });
            me.listWidgets[id] =
            {
               button: button
            };
         };

         /**
          * Remove item datacell formatter
          *
          * @method renderCellRemoveButton
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellRemoveButton = function GroupsList_renderCellRemoveButton(elCell, oRecord, oColumn, oData)
         {  
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            var desc =
               '<span id="' + me.id + '-removeItem">' +
               '  <a href="#" class="remove-item-button"><span class="removeIcon">&nbsp;</span></a>' +
               '</span>';
            elCell.innerHTML = desc;
         };

         // DataTable column defintions
         var columnDefinitions =
         [
            { key: "item", label: "Item", sortable: false, formatter: renderCellDescription },
            { key: "role", label: "Role", sortable: false, formatter: renderCellRole, width: 140 },
            { key: "remove", label: "Remove", sortable: false, formatter: renderCellRemoveButton, width: 30 }
         ];

         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-inviteelist", columnDefinitions, this.widgets.dataSource,
         {
            MSG_EMPTY: this.msg("groupslist.empty-list")
         });
      },

      /**
       * Returns the role label for a given record.
       *
       * @method getRoleLabel
       * @param record {YAHOO.widget.Record} YUI DataTable record
       */
      getRoleLabel: function GroupsList_getRoleLabel(record)
      {
         if (record.getData("role") !== undefined)
         {
            return this.msg('role.' + record.getData("role"));
         }
         return this.msg("groupslist.selectrole");
      },
      
      /**
       * Event handler for "itemSelected" bubbling event.
       * Adds an item to the list.
       *
       * @method onItemSelected
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */ 
      onItemSelected: function GroupsList_onItemSelected(layer, args)
      {   
         var data = args[1],
            itemData =
            {
               id: this.uniqueRecordId++,
               itemName: data.itemName,
               displayName: data.displayName
            };
         this.widgets.dataTable.addRow(itemData);
         this._enableDisableAddButton();
      },
      
      /**
       * Remove item action handler
       *
       * @method removeItem
       * @param row {HTMLElement} DOM reference to a TR element (or child thereof)
       */
      removeItem: function GroupsList_removeItem(row)
      {
         var record = this.widgets.dataTable.getRecord(row);
         
         // Fire the personDeselected event
         YAHOO.Bubbling.fire("itemDeselected",
         {
            itemName: record.getData("itemName")
         });

         // remove the element
         this.widgets.dataTable.deleteRow(record);
         this._enableDisableAddButton();
      },
      
      /**
       * Select all roles dropdown event handler
       *
       * @method onSelectAllRoles
       * @param sType {String} event type
       * @param aArgs {Array} event arguments
       * @param p_obj {Object} object containing record and role to set
       */
      onSelectAllRoles: function GroupsList_onFileSelect(sType, aArgs, p_obj)
      {
         var value = aArgs[1].value;
         if (value === "")
         {
            return;
         }
         
         this._setAllRolesImpl(value);
         this._enableDisableAddButton();
         Event.preventDefault(aArgs[0]);
      },
      
      /**
       * Called when the user select a role in the role dropdown
       *
       * @method onRoleSelect
       * @param sType {String} event type
       * @param aArgs {Array} event arguments
       * @param p_obj {Object} object containing record and role to set
       */
      onRoleSelect: function GroupsList_onRoleSelect(sType, aArgs, p_obj)
      {
         // set the role for the passed-in record
         var role = p_obj.role,
            record = p_obj.record;

         this._setRoleForRecord(record, role);
         
         // update the add button
         this._enableDisableAddButton();
         Event.preventDefault(aArgs[0]);
      },
      
      /**
       * Implementation of set all roles functionality
       *
       * @method _setAllRolesImpl
       * @param roleName {String} The role to set all selected groups to
       * @private
       */
      _setAllRolesImpl: function GroupsList__setAllRolesImpl(roleName)
      {
         var recordSet = this.widgets.dataTable.getRecordSet(),
            record;
         for (var i = 0, ii = recordSet.getLength(); i < ii; i++)
         {
            record = recordSet.getRecord(i);
            this._setRoleForRecord(record, roleName);
         }
         
         // update the add button
         this._enableDisableAddButton();
      },
      
      /**
       * Sets the role for a given record
       *
       * @method _setRoleForRecord
       * @param record {YAHOO.widget.Record} The DataTable record
       * @param role {String} The role to set
       * @private
       */
      _setRoleForRecord: function GroupsList__setRoleForRecord(record, role)
      {
         // set the new role
         record.setData("role", role);
          
         // update the button
         this.listWidgets[record.getData("id")].button.set("label", this.getRoleLabel(record) + " " + Alfresco.constants.MENU_ARROW_SYMBOL);   
      },
      
      /**
       * Returns whether all items have their role set correctly
       *
       * @method _checkAllRolesSet
       * @private
       */
      _checkAllRolesSet: function GroupsList__checkRolesSet()
      {
         var recordSet = this.widgets.dataTable.getRecordSet(),
            record;
         for (var i = 0, j = recordSet.getLength(); i < j; i++)
         {
            record = recordSet.getRecord(i);
            if (record.getData("role") === undefined)
            {
               return false;
            }
         }
         return true;
      },
      
      /**
       * Enables or disables the add button.
       * The add button is only enabled if a role has been selected for all items
       *
       * @method _enableDisableAddButton
       */
      _enableDisableAddButton: function GroupsList__enableDisableAddButton()
      {
         var enabled = this.widgets.dataTable.getRecordSet().getLength() > 0 && this._checkAllRolesSet();
         this.widgets.addButton.set("disabled", !enabled);
      },
      
      /**
       * Event handler for "Add" button click. Initiates the add process
       *
       * @method addButtonClick
       * @param e {Object} Event arguments
       */
      addButtonClick: function GroupsList_addButtonClick(e)
      {
         // sanity check - the add button shouldn't be clickable in this case
         var recordSet = this.widgets.dataTable.getRecordSet();
         if (recordSet.getLength() < 0 || !this._checkAllRolesSet())
         {
            this._enableDisableAddButton();
            return;
         }

         // disable button
         this.widgets.addButton.set("disabled", true);

         // show a wait message
         this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg("message.please-wait"),
            spanClass: "wait",
            displayTime: 0
         });
         
         // copy over all records
         var recs = [];
         for (var i = 0, j = recordSet.getLength(); i < j; i++)
         {
            recs.push(recordSet.getRecord(i));
         }
         var resultData =
         {
             recs: recs,
             size: recs.length,
             index: 0,
             successes: [],
             failures: []
         };
         
         // kick off the processing
         this._processResultData(resultData);
      },
      
      /**
       * Processes the result data.
       *
       * @method _processResultData
       * @param resultData {Object}
       * @private
       */
      _processResultData: function GroupsList__processResultData(resultData)
      {   
         // check if we are already done
         if (resultData.index >= resultData.size)
         {  
            this._finalizeResults(resultData);
            return;
         }

         this._doAddResult(resultData);
      },
      
      /**
       * Adds one result and returns to _processResultData on completion.
       * 
       * @method _doAddResult
       * @param resultData {Object}
       * @private
       */
      _doAddResult: function GroupsList__doAddResult(resultData)
      {
         // fetch the record to process
         var record = resultData.recs[resultData.index];

         // success handler
         var success = function GroupsList__doAddResult_success(response)
         {
            resultData.successes.push(resultData.index);
            resultData.index++;
            this._processResultData(resultData);
         };

         var failure = function GroupsList__doAddResult_failure(response)
         {
            resultData.failures.push(resultData.index);
            resultData.index++;
            this._processResultData(resultData);
         };
          
         // Repository call for each group
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "api/sites/" + this.options.siteId + "/memberships",
            method: "POST",
            requestContentType: "application/json",
            responseContentType: "application/json",
            dataObj:
            {
               group:
               {
                  fullName: record.getData('itemName')
               },
               role: record.getData('role')
            },
            successCallback:
            {
               fn: success,
               scope: this
            },
            failureCallback:
            {
               fn: failure,
               scope: this
            }
         });
      },

      /**
       * Called when all results have been processed
       *
       * @method _finalizeResults
       * @param resultData {Object}
       * @private
       */
      _finalizeResults: function GroupsList__finalizeResults(resultData)
      {  
         // remove the entries that were successful
         for (var i = resultData.successes.length - 1; i >= 0; i--)
         {
            this.widgets.dataTable.deleteRow(resultData.successes[i]);
         }
         
         // remove wait message
         this.widgets.feedbackMessage.destroy();
         
         // inform the user
         Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg("message.result", resultData.successes.length, resultData.failures.length)
         });
         
         // re-enable invite button
         this.widgets.addButton.set("disabled", false);
      }
   });
})();
