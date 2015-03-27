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
 * InvitationList component.
 * 
 * @namespace Alfresco
 * @class Alfresco.InvitationList
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
    * InvitationList constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.InvitationList} The new InvitationList instance
    * @constructor
    */
   Alfresco.InvitationList = function(htmlId)
   {
      Alfresco.InvitationList.superclass.constructor.call(this, "Alfresco.InvitationList", htmlId, ["button", "container", "datasource", "datatable", "json"]);
      
      /* Initialise prototype properties */
      this.listWidgets = [];
      this.uniqueRecordId = 1;
      
      // Decoupled event listeners
      YAHOO.Bubbling.on("personSelected", this.onPersonSelected, this);
   
      return this;
   };
   
   YAHOO.extend(Alfresco.InvitationList, Alfresco.component.Base,
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
          * siteId to InvitationList in. "" if InvitationList should be cross-site
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
       *
       * @property listWidgets
       * @type Array
       */
      listWidgets: null,
      
      /**
       * Auto-incremented unique id for each element added to the table.
       *
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
      onReady: function InvitationList_onReady()
      {  
         // WebKit CSS fix
         if (YAHOO.env.ua.webkit > 0)
         {
            Dom.setStyle(this.id + "-backTo", "vertical-align", "sub");
         }
         
         // button to invite all people in the list 
         this.widgets.inviteButton = Alfresco.util.createYUIButton(this, "invite-button", this.inviteButtonClick);
         
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

         // make sure the invite button is initially disabled
         this._enableDisableInviteButton();

         // Hook remove invitee action handler
         var me = this,
            fnRemoveInviteeHandler = function InvitationList_fnRemoveInviteeHandler(layer, args)
            {
               // call the remove method
               me.removeInvitee.call(me, args[1].anchor);
               args[1].stop = true;
               return true;
            };

         YAHOO.Bubbling.addDefaultAction("remove-item-button", fnRemoveInviteeHandler);
         
         // show the component now, this avoids painting issues of the dropdown button
         Dom.setStyle(this.id + "-invitationBar", "visibility", "visible");
      },
      
      /**
       * Setup YUI DataTable widget
       *
       * @method _setupDataTable
       * @private
       */
      _setupDataTable: function InvitationList_setupDataTable()
      {
         /**
          * DataTable Cell Renderers
          *
          * Each cell has a custom renderer defined as a custom function. See YUI documentation for details.
          * These MUST be inline in order to have access to the Alfresco.InvitationList class (via the "me" variable).
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
         var renderCellDescription = function InvitationList_renderCellDescription(elCell, oRecord, oColumn, oData)
         {
            // we currently render all results the same way
            var record = oRecord.getData(),
               name = YAHOO.lang.trim(record.firstName + " " + record.lastName),
               userName = "",
               email = record.email;

            if (record.userName !== undefined && record.userName.length > 0)
            {
                userName = "(" + record.userName + ")";
            }
            elCell.innerHTML = '<h3 class="itemname">' + $html(name) + ' <span class="lighter theme-color-1">' + $html(userName) + '</span></h3><div class="detail">' + $html(email) + '</div>';
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
         var renderCellRole = function InvitationList_renderCellActions(elCell, oRecord, oColumn, oData)
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
          * Remove user datacell formatter
          *
          * @method renderCellRemoveButton
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellRemoveButton = function InvitationList_renderCellRemoveButton(elCell, oRecord, oColumn, oData)
         {  
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            var desc =
               '<span id="' + me.id + '-removeInvitee">' +
               '  <a href="#" class="remove-item-button"><span class="removeIcon">&nbsp;</span></a>' +
               '</span>';
            elCell.innerHTML = desc;
         };

         // DataTable column defintions
         var columnDefinitions =
         [
            { key: "user", label: "User", sortable: false, formatter: renderCellDescription },
            { key: "role", label: "Role", sortable: false, formatter: renderCellRole, width: 140 },
            { key: "remove", label: "Remove", sortable: false, formatter: renderCellRemoveButton, width: 30 }
         ];

         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-inviteelist", columnDefinitions, this.widgets.dataSource,
         {
            MSG_EMPTY: this.msg("invitationlist.empty-list")
         });
      },

      /**
       * Returns the role label for a given record.
       *
       * @method getRoleLabel
       * @param record {YAHOO.widget.Record} YUI DataTable record
       */
      getRoleLabel: function InvitationList_getRoleLabel(record)
      {
         if (record.getData("role") !== undefined)
         {
            return this.msg('role.' + record.getData("role"));
         }
         return this.msg("invitationlist.selectrole");
      },
      
      /**
       * Event handler for "personSelected" bubbling event.
       * Adds a person to the list.
       *
       * @method onPersonSelected
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */ 
      onPersonSelected: function Invitationlist_onPersonSelected(layer, args)
      {   
         var data = args[1],
            inviteData =
            {
               id: this.uniqueRecordId++,
               userName: data.userName || "",
               firstName: data.firstName,
               lastName: data.lastName,
               email: data.email
            };
         this.widgets.dataTable.addRow(inviteData);
         this._enableDisableInviteButton();
      },
      
      /**
       * Remove invitee action handler
       *
       * @method removeInvitee
       * @param row {HTMLElement} DOM reference to a TR element (or child thereof)
       */
      removeInvitee: function InvitationList_removeInvitee(row)
      {
         var record = this.widgets.dataTable.getRecord(row);
         
         // Fire the personDeselected event
         YAHOO.Bubbling.fire("personDeselected",
         {
            userName: record.getData("userName")
         });

         // remove the element
         this.widgets.dataTable.deleteRow(record);
         this._enableDisableInviteButton();
      },
      
      /**
       * Select all roles dropdown event handler
       *
       * @method onSelectAllRoles
       * @param sType {String} event type
       * @param aArgs {Array} event arguments
       * @param p_obj {Object} object containing record and role to set
       */
      onSelectAllRoles: function InvitationList_onFileSelect(sType, aArgs, p_obj)
      {
         var value = aArgs[1].value;
         if (value === "")
         {
            return;
         }
         
         this._setAllRolesImpl(value);
         this._enableDisableInviteButton();
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
      onRoleSelect: function InvitationList_onRoleSelect(sType, aArgs, p_obj)
      {
         // set the role for the passed-in record
         var role = p_obj.role,
            record = p_obj.record;

         this._setRoleForRecord(record, role);
         
         // update the invite button
         this._enableDisableInviteButton();
         Event.preventDefault(aArgs[0]);
      },
      
      /**
       * Implementation of set all roles functionality
       *
       * @method _setAllRolesImpl
       * @param roleName {String} The role to set all selected groups to
       * @private
       */
      _setAllRolesImpl: function InvitationList__setAllRolesImpl(roleName)
      {
         var recordSet = this.widgets.dataTable.getRecordSet(),
            record;
         for (var i = 0, ii = recordSet.getLength(); i < ii; i++)
         {
            record = recordSet.getRecord(i);
            this._setRoleForRecord(record, roleName);
         }
         
         // update the invite button
         this._enableDisableInviteButton();
      },
      
      /**
       * Sets the role for a given record
       *
       * @method _setRoleForRecord
       * @param record {YAHOO.widget.Record} The DataTable record
       * @param role {String} The role to set
       * @private
       */
      _setRoleForRecord: function InvitationList__setRoleForRecord(record, role)
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
      _checkAllRolesSet: function InvitationList__checkRolesSet()
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
       * Enables or disables the invite button.
       * The invite button is only enabled if a role has been selected for all invitees
       *
       * @method _enableDisableInviteButton
       */
      _enableDisableInviteButton: function InvitationList__enableDisableInviteButton()
      {
         var enabled = this.widgets.dataTable.getRecordSet().getLength() > 0 && this._checkAllRolesSet();
         this.widgets.inviteButton.set("disabled", !enabled);
      },
      
      /**
       * Event handler for "Invite" button click. Initiates the invite process
       *
       * @method inviteButtonClick
       * @param e {Object} Event arguments
       */
      inviteButtonClick: function InvitationList_inviteButtonClick(e)
      {
         // sanity check - the invite button shouldn't be clickable in this case
         var recordSet = this.widgets.dataTable.getRecordSet();
         if (recordSet.getLength() < 0 || !this._checkAllRolesSet())
         {
            this._enableDisableInviteButton();
            return;
         }

         // disable button
         this.widgets.inviteButton.set("disabled", true);

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
         var inviteData =
         {
             recs: recs,
             size : recs.length,
             index: 0,
             successes: [],
             failures: []
         };
         
         // kick off the processing
         this._processInviteData(inviteData);
      },
      
      /**
       * Processes the invite data.
       *
       * @method _processInviteData
       * @param inviteData {Object}
       * @private
       */
      _processInviteData: function InvitationList__processInviteData(inviteData)
      {   
         // check if we are already done
         if (inviteData.index >= inviteData.size)
         {  
            this._finalizeInvites(inviteData);
            return;
         }
         this._doInviteUser(inviteData);
      },
      
      /**
       * Invites one user and returns to _processInviteData on completion.
       * 
       * @method _doInviteUser
       * @param inviteData data about all invites including the info which invite should be processed
       * @private
       */
      _doInviteUser: function InvitationList__doInviteUser(inviteData)
      {
         // fetch the record to process
         var record = inviteData.recs[inviteData.index];
         
         // success handler
         var success = function InvitationList__doInviteUser_success(response)
         {
            inviteData.successes.push(inviteData.index);
            inviteData.index++;
            this._processInviteData(inviteData);
         };

         var failure = function InvitationList__doInviteUser_failure(response)
         {
            inviteData.failures.push(inviteData.index);
            inviteData.index++;
            this._processInviteData(inviteData);
         };
          
         // We have to do a backend call for each invited person
         var serverPath = window.location.protocol + "//" + window.location.host + Alfresco.constants.URL_CONTEXT;

         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "api/sites/" + this.options.siteId + "/invitations",
            method: "POST",
            requestContentType: "application/json",
            responseContentType: "application/json",
            dataObj:
            {
               invitationType: "NOMINATED",
               inviteeUserName: record.getData('userName') || "",
               inviteeRoleName: record.getData('role'),
               inviteeFirstName: record.getData('firstName'),
               inviteeLastName: record.getData('lastName'),
               inviteeEmail: record.getData('email'),
               serverPath: serverPath,
               acceptURL: 'page/accept-invite',
               rejectURL: 'page/reject-invite'
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
       * Called when all invites have been processed
       *
       * @method _finalizeInvites
       * @param resultData {Object}
       * @private
       */
      _finalizeInvites: function InvitationList__finalizeInvites(inviteData)
      {  
         // remove the entries that were successful
         for (var i = inviteData.successes.length - 1; i >= 0; i--)
         {
            this.widgets.dataTable.deleteRow(inviteData.successes[i]);
         }
         
         // remove wait message
         this.widgets.feedbackMessage.destroy();
         
         // inform the user
         Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg("message.inviteresult", inviteData.successes.length, inviteData.failures.length)
         });
         
         // update the invite button
         this._enableDisableInviteButton();
      }
   });
})();
