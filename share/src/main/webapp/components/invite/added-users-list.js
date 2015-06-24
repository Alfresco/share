/**
 * Copyright (C) 2005-2015 Alfresco Software Limited.
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
 * AddedUsersList component.
 * 
 * @namespace Alfresco
 * @class Alfresco.AddedUsersList
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
    * AddedUsersList constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.AddedUsersList} The new AddedUsersList instance
    * @constructor
    */
    Alfresco.AddedUsersList = function(htmlId)
    {
       Alfresco.AddedUsersList.superclass.constructor.call(this, "Alfresco.AddedUsersList", htmlId, 
             ["button", "container", "datasource", "datatable", "json"]);
       return this;
    };

    YAHOO.extend(Alfresco.AddedUsersList, Alfresco.component.Base,
    {
       /**
        * Fired by YUI when parent element is available for scripting.
        * Component initialisation, including instantiation of YUI widgets and event listener binding.
        *
        * @method onReady
        */
       onReady: function AddedUsersList_onReady()
       {
          // Move the button to the added users panel
          var parentDiv = document.getElementById(this.id + "-add-users-button");
          var sinviteDiv = Dom.getElementsByClassName("sinvite", "div", "bd")[0];
          var inviteButton = sinviteDiv.getElementsByTagName("button")[0];
          inviteButton.innerHTML = this.msg("added-users-list.add-button-text");
          parentDiv.appendChild(sinviteDiv.firstElementChild);
          
          // setup the datasource
          this.widgets.dataSource = new YAHOO.util.DataSource([],
          {
             responseType: YAHOO.util.DataSource.TYPE_JSARRAY
          });
          
          // setup of the datatable
          this._setupDataTable();
          
          // Decoupled event listeners
          YAHOO.Bubbling.on("usersAdded", this.onUsersAdded, this);
       },
       
       onUsersAdded: function AddedUsersList_onUsersAdded(layer, args)
       {   
          var addedUsers = args[1].users;
          this.widgets.dataTable.addRows(addedUsers, 0);
          var tallyDiv = Dom.getElementsByClassName("added-users-list-tally", "div", "bd")[0];
          tallyDiv.innerHTML = this.msg("added-users-list.tally", this.widgets.dataTable.getRecordSet().getLength());
          Dom.removeClass(tallyDiv, "hidden");
       },
       
       /**
        * Setup YUI DataTable widget
        *
        * @method _setupDataTable
        * @private
        */
       _setupDataTable: function AddedUsersList_setupDataTable()
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
          var renderCellDescription = function AddedUsersList_renderCellDescription(elCell, oRecord, oColumn, oData)
          {
             // we currently render all results the same way
             var record = oRecord.getData(),
                name = YAHOO.lang.trim(record.firstName + " " + record.lastName),
                roleName = me.msg("role." + record.role);
             elCell.innerHTML = '<h3 class="itemname">' + $html(name) + '</h3><div class="detail">' + $html(roleName) + '</div>';
          };
          
          var renderCellCheckbox = function AddedUsersList_renderCellCheckbox(elCell, oRecord, oColumn, oData)
          {
             // TODO In the future may want to display users that had an error as well
             elCell.innerHTML = '<div class="alf-green-check"></div>';
          };
          
          // DataTable column definitions
          var columnDefinitions =
          [
             { key: "user", label: "User", sortable: false, formatter: renderCellDescription },
             { key: "checkbox", label: "Confirmation", sortable: false, formatter: renderCellCheckbox }
          ];
          
          var msgEmpty = this.msg("added-users-list.empty");
          
          // DataTable definition
          this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-added-users-list-content", columnDefinitions, this.widgets.dataSource,
          {
             MSG_EMPTY: msgEmpty
          });
       }
   });
})();
