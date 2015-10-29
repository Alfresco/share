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
 * Manage Permissions component.
 * 
 * @namespace Alfresco
 * @class Alfresco.component.ManagePermissions
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
    * Permissions constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.component.ManagePermissions} The new Permissions instance
    * @constructor
    */
   Alfresco.component.ManagePermissions = function(htmlId)
   {
      Alfresco.component.ManagePermissions.superclass.constructor.call(this, "Alfresco.component.ManagePermissions", htmlId, ["button", "menu", "container", "datasource", "datatable", "paginator", "json"]);
      
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("nodeDetailsAvailable", this.onNodeDetailsAvailable, this);
      YAHOO.Bubbling.on("itemSelected", this.onAuthoritySelected, this);
      
      /* Deferred until details available and DOM ready */
      this.deferredReady = new Alfresco.util.Deferred(["onPermissionsLoaded", "onNodeDetailsAvailable"],
      {
         fn: this.onDeferredReady,
         scope: this
      });
      
      // Initialise prototype properties
      this.nodeData = null;
      this.settableRoles = null;
      this.settableRolesMenuData = null;
      this.permissions =
      {
         isInherited: false,
         inherited: [],
         current: []
      };
      this.showingAuthorityFinder = false;
      this.inheritanceWarning = false;
      
      return this;
   };
   
   YAHOO.extend(Alfresco.component.ManagePermissions, Alfresco.component.Base,
   {
      /**
       * Deferred ready object. Handles deferring component main functions until all async events have been received.
       *
       * @property deferredReady
       * @type object
       */
      deferredReady: null,

      /**
       * Initial node metadata as returned from Repository
       *
       * @property nodeData
       * @type object
       */
      nodeData: null,

      /**
       * Data structure for populating the Roles dropdowns
       *
       * @property settableRoles
       * @type object
       */
      settableRoles: null,

      /**
       * Array of roles suitable for building a YUI menu
       *
       * @property settableRolesMenuData
       * @type object
       */
      settableRolesMenuData: null,

      /**
       * Object literal containing current client-state of permissions
       *
       * @property permissions
       * @type object
       */
      permissions: null,

	  sitePermissions: {},

      /**
       * Visibility state of Authority Finder
       *
       * @property showingAuthorityFinder
       * @type boolean
       */
      showingAuthorityFinder: null,

      /**
       * One-time display when switching off inheritance for the first time
       *
       * @property inheritanceWarning
       * @type boolean
       */
      inheritanceWarning: null,

      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * The roles that are required to be set by the system on a node when the node doesn't inherit permissions.
          * These roles shall NOT be editable.
          *
          * @property nonEditableRoles
          * @type object
          * @default [ "SiteManager" ]
          */
         nonEditableNames: [ "^GROUP_site_.*_SiteManager$" ],
         nonEditableRoles: [ "SiteManager" ],
         unDeletableRoles: [ "^GROUP_site_.*_SiteManager$", "^GROUP_site_.*_SiteCollaborator$", "^GROUP_site_.*_SiteContributor$", "^GROUP_site_.*_SiteConsumer$" ],
         showGroups: true,
         
         /**
          * Number of characters required for an authority search.
          *
          * @property minAuthSearchTermLength
          * @type int
          * @default 1
          */
         minAuthSearchTermLength: 1
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function Permissions_onReady()
      {
         // YUI Buttons
         this.widgets.inherited = Alfresco.util.createYUIButton(this, "inheritedButton", this.onInheritedButton);
         this.widgets.saveButton = Alfresco.util.createYUIButton(this, "okButton", this.onSaveButton);
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancelButton", this.onCancelButton);
         this.widgets.rolesTooltip = new Array();
         
         // DataSource set-up and event registration
         this._setupDataSources();
         
         // DataTable set-up and event registration
         this._setupDataTables();

         // Load the Authority Finder component
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.URL_SERVICECONTEXT + "components/people-finder/authority-finder",
            dataObj:
            {
               htmlid: this.id + "-authorityFinder"
            },
            successCallback:
            {
               fn: this.onAuthorityFinderLoaded,
               scope: this
            },
            failureMessage: this.msg("message.authorityFinderFail"),
            execScripts: true
         });

         if (this.options.site)
         {
            Alfresco.util.Ajax.request( 
            {
               url: Alfresco.constants.PROXY_URI + "api/sites/" + encodeURIComponent(this.options.site) + "/memberships/",
               successCallback:
               {
                  fn: function(o)
                  {
                     for (var i = 0; i < o.json.length; i++) 
                     {
                        this.sitePermissions[o.json[i].authority.fullName] = o.json[i].role;
                     }
                  },
                  scope: this
               }
            });
         }
		 

         // Finally show the component body here to prevent UI artifacts on YUI button decoration
         Dom.setStyle(this.id + "-body", "visibility", "visible");
      },
      
      /**
       * Toggle inherited permissions flag
       *
       * @method onInheritedButton
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onInheritedButton: function Permissions_onInheritedButton(e, p_obj)
      {
         var me = this;
         
         if (this.permissions.isInherited && !this.inheritanceWarning)
         {
            // Switching off inheritance for the first time this page session, so confirm action
            Alfresco.util.PopupManager.displayPrompt(
            {
               title: this.msg("message.confirm.inheritance.title"),
               text: this.msg("message.confirm.inheritance.description"),
               noEscape: true,
               buttons: [
               {
                  text: this.msg("button.yes"),
                  handler: function Permissions_onInheritanceButton_yes()
                  {
                     this.destroy();
                     me.inheritanceWarning = true;
                     me.permissions.isInherited = !me.permissions.isInherited;
                     me._updateInheritedUI();
                  }
               },
               {
                  text: this.msg("button.no"),
                  handler: function Permissions_onInheritanceButton_no()
                  {
                     this.destroy();
                  },
                  isDefault: true
               }]
            });
         }
         else
         {
            this.permissions.isInherited = !this.permissions.isInherited;
            this._updateInheritedUI();
         }
      },
      
      /**
       * Update current state of inherited flag in UI
       *
       * @method _updateInheritedUI
       */
      _updateInheritedUI: function Permissions__updateInheritedUI()
      {
         var inherits = this.permissions.isInherited;
         // Button
         Dom.removeClass(this.id + "-inheritedButtonContainer", "inherited-" + (inherits ? "off" : "on"));
         Dom.addClass(this.id + "-inheritedButtonContainer", "inherited-" + (inherits ? "on" : "off"));
         // DataTable
         if (inherits)
         {
            Dom.removeClass(this.id + "-inheritedContainer", "hidden");
         }
         else
         {
            Dom.addClass(this.id + "-inheritedContainer", "hidden");
         }
      },

      /**
       * Called when the authority finder template has been loaded.
       * Creates a dialog and inserts the authority finder for choosing groups and users to add.
       *
       * @method onAuthorityFinderLoaded
       * @param response The server response
       */
      onAuthorityFinderLoaded: function Permissions_onAuthorityFinderLoaded(response)
      {
         // Inject the component from the XHR request into it's placeholder DIV element
         var finderDiv = Dom.get(this.id + "-authorityFinder");
         if (finderDiv)
         {
            finderDiv.innerHTML = response.serverResponse.responseText;

            this.widgets.authorityFinder = finderDiv;

            // Find the Authority Finder by container ID
            this.modules.authorityFinder = Alfresco.util.ComponentManager.get(this.id + "-authorityFinder");

            // Set the correct options for our use
            this.modules.authorityFinder.setOptions(
            {
               dataWebScript: Alfresco.constants.URL_SERVICECONTEXT + "components/people-finder/authority-query",
               viewMode: Alfresco.AuthorityFinder.VIEW_MODE_COMPACT,
               siteId: this.options.site,
               singleSelectMode: true,
               minSearchTermLength: this.options.minAuthSearchTermLength,
               authorityType: (this.options.showGroups) ? Alfresco.AuthorityFinder.AUTHORITY_TYPE_ALL : Alfresco.AuthorityFinder.AUTHORITY_TYPE_USERS
            });

            // Add User/Group button
            this.widgets.addUserGroup = Alfresco.util.createYUIButton(this, "addUserGroupButton", this.onAddUserGroupButton,
            {
               label: (this.options.showGroups) ? this.msg("button.addUserGroup") : this.msg("button.addUser")
            });
            
            var btnRegion = Dom.getRegion(this.id + "-addUserGroupButton");
            Dom.setStyle(this.widgets.authorityFinder, "top", (btnRegion.bottom + 4) + "px");
         }

         // Retrieve current permissions and settable roles for this node
         Alfresco.util.Ajax.jsonGet(
         {
            url: Alfresco.constants.PROXY_URI + 'slingshot/doclib/permissions/' + Alfresco.util.NodeRef(this.options.nodeRef).uri,
            successCallback: 
            { 
               fn: this.onPermissionsLoaded, 
               scope: this 
            },
            failureMessage: this.msg("message.permissionsGetFail")
         });
      },

      /**
       * Event handler called when the "nodeDetailsAvailable" event is received
       *
       * @method: onNodeDetailsAvailable
       */
      onNodeDetailsAvailable: function Permissions_onNodeDetailsAvailable(layer, args)
      {
         this.nodeData = args[1].nodeDetails;
         this.deferredReady.fulfil("onNodeDetailsAvailable");
      },

      /**
       * Success handler called when the AJAX call to the doclist permissions web script returns successfully
       *
       * @method onPermissionsLoaded
       * @param response {object} Ajax response details
       */
      onPermissionsLoaded: function Permissions_onPermissionsLoaded(response)
      {
         var data = response.json;
         
         // Update local copy of permissions
         this.permissions =
         {
            originalIsInherited: data.isInherited,
            isInherited: data.isInherited,
            canReadInherited: data.canReadInherited,
            inherited: data.inherited,
            original: Alfresco.util.deepCopy(data.direct),
            current: Alfresco.util.deepCopy(data.direct)
         };
         
         // Does the user have permissions to read the parent node's permissions?
         if (!this.permissions.canReadInherited)
         {
            this.widgets.dtInherited.set("MSG_EMPTY", this.msg("message.empty.no-permission"));
         }
         
         // Need the inheritance warning?
         this.inheritanceWarning = !data.isInherited;
         
         // Roles the user is allowed to select from
         this.settableRoles = data.settable;
         this.settableRolesMenuData = [];
         for (var i = 0, ii = data.settable.length; i < ii; i++)
         {
            this.settableRoles[data.settable[i]] = true;
            this.settableRolesMenuData.push(
            {
               text: data.settable[i],
               value: data.settable[i]
            });
         }

         this.deferredReady.fulfil("onPermissionsLoaded");
      },

      /**
       * Event handler called when both the "nodeDetailsAvailable" and "onPermissionsLoaded" events have been received
       *
       * @method: onDeferredReady
       */
      onDeferredReady: function Permissions_onDeferredReady()
      {
         // Component title
         Dom.get(this.id + "-title").innerHTML = this.msg("title", this.nodeData.displayName);

         var me = this;

         // Hook action events
         var fnActionHandler = function Permissions_fnActionHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               if (typeof me[owner.className] == "function")
               {
                  args[1].stop = true;
                  var asset = me.widgets.dtDirect.getRecord(args[1].target.offsetParent).getData();
                  me[owner.className].call(me, asset, owner);
               }
            }
            return true;
         };
         YAHOO.Bubbling.addDefaultAction("action-link", fnActionHandler);

         this.render();
      },

      /**
       * Event handler for the Add User/Group button.
       * 
       * @method onAddUserGroupButton
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onAddUserGroupButton: function Permissions_onAddUserGroupButton(e, args)
      {
         if (!this.showingAuthorityFinder)
         {
            this.modules.authorityFinder.clearResults();
            Dom.addClass(this.widgets.authorityFinder, "active");
            Dom.addClass(this.id + "-inheritedContainer", "table-mask");
            Dom.addClass(this.id + "-directContainer", "table-mask");
            Dom.get(this.id + "-authorityFinder-search-text").focus();
            this.showingAuthorityFinder = true;            
         }
         else
         {
            Dom.removeClass(this.widgets.authorityFinder, "active");
            Dom.removeClass(this.id + "-inheritedContainer", "table-mask");
            Dom.removeClass(this.id + "-directContainer", "table-mask");
            this.showingAuthorityFinder = false;
         }
      },

      /**
       * Authority selected event handler. This event is fired from Authority picker.
       * 
       * @method onAuthoritySelected
       * @param e DomEvent
       * @param args Event parameters (depends on event type)
       */
      onAuthoritySelected: function Permissions_onAuthoritySelected(e, args)
      {
         var defaultRole = this.sitePermissions[args[1].itemName];
         if (defaultRole == null)
         {
            //set default role
            defaultRole = this.settableRoles[this.settableRoles.length - 1];
         }
         // Construct permission descriptor and add permission row.
         this.permissions.current.push(
         {
            authority:
            {
               name: args[1].itemName,
               displayName: args[1].displayName,
               iconUrl: args[1].iconUrl
            },
            role: defaultRole,
            created: true
         });
         
         // Remove authority selector popup
         this.widgets.addUserGroup.set("checked", false);
         Dom.removeClass(this.widgets.authorityFinder, "active");
         Dom.removeClass(this.id + "-inheritedContainer", "table-mask");
         Dom.removeClass(this.id + "-directContainer", "table-mask");
         this.showingAuthorityFinder = false;
         
         this.render();
      },


      /**
       * DataTable Cell Renderers
       */

      /**
       * Returns authority icon custom datacell formatter
       *
       * @method fnRenderCellAuthorityIcon
       */
      fnRenderCellAuthorityIcon: function Permissions_fnRenderCellAuthorityIcon()
      {
         var scope = this;
         
         /**
          * Authority icon custom datacell formatter
          *
          * @method renderCellAuthorityIcon
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function Permissions_renderCellAuthorityIcon(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            var authority = oRecord.getData("authority"),
               isGroup = authority.name.indexOf("GROUP_") === 0,
               iconUrl = Alfresco.constants.URL_RESCONTEXT + "components/images/" + (isGroup ? "group" : "no-user-photo") + "-64.png";

            if (authority.avatar && authority.avatar.length !== 0)
            {
               iconUrl = Alfresco.constants.PROXY_URI + authority.avatar + "?c=queue&ph=true";
            }
            else if (authority.iconUrl)
            {
               // As passed-back from the Authority Finder component
               iconUrl = authority.iconUrl;
            }
            elCell.innerHTML = '<img class="icon32" src="' + iconUrl + '" alt="icon" />';
         };
      },

      /**
       * Returns text custom datacell formatter
       *
       * @method fnRenderCellText
       */
      fnRenderCellText: function Permissions_fnRenderCellText()
      {
         var scope = this;
         
         /**
          * Text custom datacell formatter.
          * YUI default text renderer does not set column width style.
          *
          * @method renderCellText
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function Permissions_renderCellText(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            elCell.innerHTML = $html(oData);
         };
      },

      /**
       * Returns text custom datacell formatter
       *
       * @method fnRenderCellText
       */
      fnRenderPermissionCellText: function Permissions_fnRenderPermissionCellText()
      {
         var scope = this;

         /**
          * Text custom datacell formatter.
          *
          * @method renderPermissionCellText
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function Permissions_renderPermissionCellText(elCell, oRecord, oColumn, oData)
         {
            elCell.innerHTML = $html(oData);
         };
      },

      /**
       * Converts a role name into a localised role name.
       *
       * @method _i18nRole
       * @param untranslatedRole {string}
       */
      _i18nRole: function Permissions__i18nRole(untranslatedRole)
      {
         return this.msg("roles." + untranslatedRole.toLowerCase())
      },

      /**
       * Returns custom datacell formatter that adds i18n support for role names.
       *
       * @method fnRenderCellRoleText
       */
      fnRenderCellRoleText: function Premissions_fnRenderCellRoleText(elCell, oRecord, oColumn, oData)
      {
         var scope = this;

         /**
          *
          * custom datacell formatter that adds i18n support for role names.
          *
          */
         return function Permissions_renderCellRoleText(elCell, oRecord, oColumn, oData)
         {
            arguments[3] = scope._i18nRole(arguments[3]);
            scope.fnRenderCellText().apply(scope, arguments);
         }
      },

      /**
       * Returns role custom datacell formatter
       *
       * @method fnRenderCellRole
       */
      fnRenderCellRole: function Permissions_fnRenderCellRole()
      {
         var scope = this;
         
         /**
          * Role custom datacell formatter
          *
          * @method renderCellRole
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function Permissions_renderCellRole(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            var role = oRecord.getData("role"),
               index = oRecord.getData("index"),
               menuId = "roles-" + oRecord.getId(),
               menuData = [];

            // Special case handling for non-settable roles
            if (!scope._isGroupEditable(oRecord) || !scope.settableRoles.hasOwnProperty(role))
            {
               elCell.innerHTML = '<span>' + $html(scope._i18nRole(oRecord.getData("role"))) + '</span>';
            }
            else
            {
               menuData = menuData.concat(scope.settableRolesMenuData);

               // Internationalise the roles strings displayed:
               for (var j = 0, jj = menuData.length; j < jj; j++)
               {
                  menuData[j].text = scope._i18nRole(menuData[j].value);
               }

               elCell.innerHTML = '<span id="' + menuId + '"></span>';

               // Roles
               var rolesButton = new YAHOO.widget.Button(
               {
                  container: menuId,
                  type: "menu",
                  menu: menuData
               });
               rolesButton.getMenu().subscribe("click", function(p_sType, p_aArgs)
               {
                  return function Permissions_rolesButtonClicked(p_button, p_index)
                  {
                     var menuItem = p_aArgs[1];
                     if (menuItem)
                     {
                        p_button.set("label", scope._i18nRole(menuItem.value));
                        scope.onRoleChanged.call(scope, p_aArgs[1], p_index);
                     }
                  }(rolesButton, index);
               });
               rolesButton.set("label", $html(scope._i18nRole(oRecord.getData("role"))));
            }
         };
      },

      /**
       * Returns actions custom datacell formatter
       *
       * @method fnRenderCellActions
       */
      fnRenderCellActions: function Permissions_fnRenderCellActions()
      {
         var scope = this;
         
         /**
          * Actions custom datacell formatter
          *
          * @method renderCellActions
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function Permissions_renderCellActions(elCell, oRecord, oColumn, oData)
         {
            var role = oRecord.getData("role");

            Dom.setStyle(elCell, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            var html = '<div id="' + scope.id + '-actions-' + oRecord.getId() + '" class="hidden action-set">';
            if (scope._isGroupEditable(oRecord) && scope._isGroupDeletable(oRecord))
            {
               html += '<div class="onActionDelete"><a class="action-link" title="' + scope.msg("button.delete") + '"><span>' + scope.msg("button.delete") + '</span></a></div>';
            }            
            html += '</div>';
            elCell.innerHTML = html;
         };
      },

      /**
       * Prompt the DataTable widget to re-render
       *
       * @method render
       */
      render: function Permissions_render()
      {
         this._updateInheritedUI();

         this.widgets.dsInherited.sendRequest(this.permissions.inherited,
         {
            success: this.widgets.dtInherited.onDataReturnInitializeTable,
            failure: this.widgets.dtInherited.onDataReturnInitializeTable,
            scope: this.widgets.dtInherited
         });

         this.widgets.dsDirect.sendRequest(this.permissions.current,
         {
            success: this.widgets.dtDirect.onDataReturnInitializeTable,
            failure: this.widgets.dtDirect.onDataReturnInitializeTable,
            scope: this.widgets.dtDirect
         });
      },
      
      /**
       * Data provider function, called from DataSource widgets in response to sendRequest()
       *
       * @method generateData
       * @param p_permissions {Array} Array of permissions to build dataset for
       * @return {Array} Data array
       */
      generateData: function Permissions_generateData(p_permissions)
      {
         var data = [],
            perm;
         
         for (var i = 0, ii = p_permissions.length; i < ii; i++)
         {
            perm = p_permissions[i];
            if (!perm.removed && !(perm.authority.name === "GROUP_EVERYONE" && perm.role === "ReadPermissions"))
            {
               data.push(
               {
                  index: i,
                  authority: perm.authority,
                  displayName: perm.authority.displayName,
                  isGroup: perm.authority.name.indexOf("GROUP_") == 0,
                  role: perm.role
               });
            }
         }

         /**
          * Sort groups to the top, then in alphabetical order
          */
         function sortData(a, b)
         {
            return (!a.isGroup && b.isGroup) ? 1 : (a.isGroup && !b.isGroup) ? -1 : (a.displayName > b.displayName) ? 1 : (a.displayName < b.displayName) ? -1 : 0;
         }

         return data.sort(sortData);
      },

      /**
       * DataSource set-up and event registration
       *
       * @method _setupDataSources
       * @protected
       */
      _setupDataSources: function Permissions__setupDataSources()
      {
         // DataSource definition - Inherited
         this.widgets.dsInherited = new YAHOO.util.DataSource(this.generateData,
         {
            responseType: YAHOO.util.DataSource.TYPE_JSFUNCTION,
            scope: this
         });

         // DataSource definition - Direct
         this.widgets.dsDirect = new YAHOO.util.DataSource(this.generateData,
         {
            responseType: YAHOO.util.DataSource.TYPE_JSFUNCTION,
            scope: this
         });
      },
      
      /**
       * DataTable set-up and event registration
       *
       * @method _setupDataTables
       * @protected
       */
      _setupDataTables: function Permissions__setupDataTables()
      {
         // Labels change in site mode.
         var authlabel = (this.options.showGroups)? this.msg("column.authority") : this.msg("column.user") ;

         // DataTable column defintions
         var columnDefinitions =
         [
            { key: "icon", label: "", sortable: false, formatter: this.fnRenderCellAuthorityIcon(), width: 32 },
            { key: "displayName", label: authlabel, sortable: false, formatter: this.fnRenderPermissionCellText() },
            { key: "role", label: this.msg("column.role"), sortable: false, formatter: this.fnRenderCellRoleText(), width: 240 }
         ];
         
         // DataTable definition - Inherited
         this.widgets.dtInherited = new YAHOO.widget.DataTable(this.id + "-inheritedPermissions", columnDefinitions, this.widgets.dsInherited,
         {
            initialLoad: false,
            MSG_EMPTY: this.msg("message.empty"),
            MSG_LOADING: this.msg("message.loading")
         });

         // Extra column defintion for direct permissions
         columnDefinitions =
         [
            { key: "icon", label: "", sortable: false, formatter: this.fnRenderCellAuthorityIcon(), width: 32 },
            { key: "displayName", label: authlabel, sortable: false, formatter: this.fnRenderPermissionCellText() },
            { key: "role", label: this.msg("column.role"), sortable: false, formatter: this.fnRenderCellRole(), width: 240 },
            { key: "actions", label: this.msg("column.actions"), sortable: false, formatter: this.fnRenderCellActions(), width: 120 }
         ];

         // DataTable definition - Direct
         this.widgets.dtDirect = new YAHOO.widget.DataTable(this.id + "-directPermissions", columnDefinitions, this.widgets.dsDirect,
         {
            initialLoad: false,
            MSG_EMPTY: this.msg("message.empty"),
            MSG_LOADING: this.msg("message.loading")
         });
         // Enable row highlighting 
         this.widgets.dtDirect.subscribe("rowMouseoverEvent", this.onEventHighlightRow, this, true);
         this.widgets.dtDirect.subscribe("rowMouseoutEvent", this.onEventUnhighlightRow, this, true);

         this._injectRoleTooltip(this.id + "-inheritedPermissions", "-role");
         this._injectRoleTooltip(this.id + "-directPermissions", "-role");
      },
      
      /**
       * Finds the YUI data table column corresponding to the given headerIdSuffix
       * and injects and initializes a role info tooltip.
       *
       * @method _injectRoleTooltip
       * @param role {String} Event object.
       * @return The role info tooltip
       * @private
       */
      _injectRoleTooltip: function Permissions__injectRoleTooltip(dataTableId, headerIdSuffix)
      {
         // Find the role column header
         var roleColumnHeaders = Dom.getElementsBy(
               function (foundElement) { return foundElement.id.indexOf(headerIdSuffix) > 0; },
               "th",
               dataTableId
         );
         if (roleColumnHeaders.length > 0)
         {
            var counter = this.widgets.rolesTooltip.length;
            // Inject the role tooltip
            var roleTooltipId = this.id + '-role-info' + counter;
            var roleTooltip = document.createElement('div');
            roleTooltip.id = roleTooltipId;
            roleTooltip.className = 'alf-role-info-tooltip';
            var buttonName = 'role-info-button' + counter;
            roleTooltip.innerHTML = '<button id="' + this.id + '-' + buttonName + '">&nbsp;</button>';
            
            roleColumnHeaders[0].children[0].children[0].className += ' alf-role-column-label';
            roleColumnHeaders[0].children[0].appendChild(roleTooltip);
            
            this.widgets.rolesTooltip.push(new Alfresco.module.RolesTooltip(
                  this.id, roleTooltipId, buttonName, 
                  this.options.site, this.options.nodeRef));
            return this.widgets.rolesTooltip;
         }
         return null;
      },

      /**
       * Checks if the node hasn't got inherited permissions and if so if the role is required for the system to work.
       * If so it shall not be allowed to be edited.
       *
       * @method _isGroupEditable
       * @param role {String} Event object.
       * @return True if the node 
       * @private
       */
      _isGroupEditable: function Permissions__isGroupEditable(oRecord)
      {
         if(this.permissions.isInherited)
         {
             return true;
         }      
         var groupName = oRecord.getData("authority").name;
         var groupRole = oRecord.getData("role");
         if (oRecord.getData("isGroup") == true)
         {
             for (var i = 0; i < this.options.nonEditableNames.length; i++)
             {
                if (groupName.search(this.options.nonEditableNames[i]) !== -1)
                {
                   for (var j = 0; j < this.options.nonEditableRoles.length; j++)
                   {
                      if (groupRole.search(this.options.nonEditableRoles[j]) !== -1)
                      {
                         return false;
                      }
                   }
                }
             }
         }
         return true;
      },
      
      /**
       * Checks if the group is deletable.
       *
       * @method _isGroupDeletable
       * @param role {String} Event object.
       * @private
       */	  
      _isGroupDeletable: function Permissions__isGroupDeletable(oRecord)
	  {
         if(this.permissions.isInherited)
         {
             return true;
         }
         // MNT-13639: allow to remove rules (default behavior befor fix for MNT-10984) under site
         if (Alfresco.constants.SITE != null && Alfresco.constants.SITE.length > 0)
         {
             return true;
         }
         // MNT-13639: allow to remove rules (default behavior befor fix for MNT-10984) under site
         if (Alfresco.constants.SITE != null && Alfresco.constants.SITE.length > 0)
         {
             return true;
         }
         var groupName = oRecord.getData("authority").name;
         if (oRecord.getData("isGroup") == true)
         {
             for (var i = 0; i < this.options.unDeletableRoles.length; i++)
             {
                if (groupName.search(this.options.unDeletableRoles[i]) !== -1)
                {
                    return false;
                }
             }
         }
         return true;
      },
      
      /**
       * Custom event handler to highlight row.
       *
       * @method onEventHighlightRow
       * @param oArgs.event {HTMLEvent} Event object.
       * @param oArgs.target {HTMLElement} Target element.
       */
      onEventHighlightRow: function Permissions_onEventHighlightRow(oArgs)
      {
         // Call through to get the row highlighted by YUI
         this.widgets.dtDirect.onEventHighlightRow.call(this.widgets.dtDirect, oArgs);
         Dom.removeClass(this.id + "-actions-" + oArgs.target.id, "hidden");
      },

      /**
       * Custom event handler to unhighlight row.
       *
       * @method onEventUnhighlightRow
       * @param oArgs.event {HTMLEvent} Event object.
       * @param oArgs.target {HTMLElement} Target element.
       */
      onEventUnhighlightRow: function Permissions_onEventUnhighlightRow(oArgs)
      {
         // Call through to get the row unhighlighted by YUI
         this.widgets.dtDirect.onEventUnhighlightRow.call(this.widgets.dtDirect, oArgs);
         Dom.addClass(this.id + "-actions-" + oArgs.target.id, "hidden");
      },

      /**
       * Role drop-down changed event handler
       *
       * @method onRoleChanged
       * @param p_oMenuItem {object} Selected menu item
       */
      onRoleChanged: function Permissions_onRoleChanged(p_oMenuItem, p_index)
      {
         var perm = this.permissions.current[p_index],
            original = this.permissions.original;
         
         perm.role = p_oMenuItem.value;
         perm.modified = (p_index <= original.length && original[p_index] != null && perm.role !== original[p_index].role);
      },

      /**
       * Delete permission
       *
       * @method onActionDelete
       * @param oRecord {object} Object literal representing permission being actioned
       */
      onActionDelete: function Permission_onActionDelete(p_recordData)
      {
         var perm = this.permissions.current[p_recordData.index];
         perm.removed = true;
         this.render();
      },

      /**
       * Called when user clicks on the save button.
       *
       * @method onSaveButtonClick
       * @param type
       * @param args
       */
      onSaveButton: function Permissions_onSaveButton(type, args)
      {
         this.widgets.saveButton.set("disabled", true);
         
         var permissions = [],
            perm;
         
         for (var i = 0, ii = this.permissions.current.length; i < ii; i++)
         {
            perm = this.permissions.current[i];
            // Newly created, or existing and removed or modified
            if ((perm.created && !perm.removed) || (!perm.created && (perm.removed || perm.modified)))
            {
               // Modified existing
               // First add a new one, see MNT-11725
               permissions.push(
               {
                  authority: perm.authority.name,
                  role: perm.role,
                  remove: perm.removed
               });

               // Remove old permission
               if (perm.modified && !perm.created)
               {
                  permissions.push(
                  {
                     authority: perm.authority.name,
                     role: this.permissions.original[i].role,
                     remove: true
                  });
               }
            }
         }

         if (permissions.length > 0 || this.permissions.isInherited !== this.permissions.originalIsInherited)
         {
            Alfresco.util.Ajax.jsonPost(
            {
               url: Alfresco.constants.PROXY_URI + "slingshot/doclib/permissions/" + Alfresco.util.NodeRef(this.options.nodeRef).uri,
               dataObj:
               {
                  permissions: permissions,
                  isInherited: this.permissions.isInherited
               },
               successCallback:
               {
                  fn: function(res)
                  {
                     // Return to appropriate location
                     this._navigateForward();
                  },
                  scope: this
               },
               failureCallback:
               {
                  fn: function(response)
                  {
                     var json = Alfresco.util.parseJSON(response.serverResponse.responseText);
                     Alfresco.util.PopupManager.displayPrompt(
                     {
                        title: this.msg("message.failure"),
                        text: this.msg("message.permissionsSaveFail", json.message)
                     });
                     this.widgets.saveButton.set("disabled", false);
                  },
                  scope: this
               }
            });
         }
         else
         {
            // Nothing to save
            this._navigateForward();
         }
      },

      /**
       * Called when user clicks on the cancel button.
       *
       * @method onCancelButtonClick
       * @param type
       * @param args
       */
      onCancelButton: function Permissions_onCancelButton(type, args)
      {
         this.widgets.cancelButton.set("disabled", true);
         this._navigateForward();
      },

      /**
       * Displays the corresponding details page for the current node
       *
       * @method _navigateForward
       * @private
       */
      _navigateForward: function Permissions__navigateForward()
      {
         window.history.go(-1);
      }
   });
})();