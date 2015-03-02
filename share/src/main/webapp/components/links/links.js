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
 * Links component
 *
 * @namespace Alfresco
 * @class Alfresco.Links
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
      $links = Alfresco.util.activateLinks;

   /**
    * Links constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.SiteFinder} The new SiteFinder instance
    * @constructor
    */
   Alfresco.Links = function(htmlId)
   {
      Alfresco.Links.superclass.constructor.call(this, "Alfresco.Links", htmlId, ["button", "container", "datasource", "datatable", "paginator", "json"]);

      this.currentFilter = {};
      this.tagId =
      {
         id: 0,
         tags: {}
      };
      this.busy = false;

      /**
       * The deleted link CSS style.
       */
      this.DELETEDCLASS = "delete-link";

      /**
       * The edited link CSS style.
       */
      this.EDITEDCLASS = "edit-link";
      
      this.PROTOCOL_STR_DELIM = /((.*):\/\/(.*))/;
      this.PORT_STR_DELIM = /^((.*):(\d{1,})((\/.*){0,}))/;

      YAHOO.Bubbling.on("changeFilter", this.onChangeFilter, this);
      YAHOO.Bubbling.on("linksListRefresh", this.onLinksListRefresh, this);
      YAHOO.Bubbling.on("deactivateAllControls", this.onDeactivateAllControls, this);
   };

   YAHOO.extend(Alfresco.Links, Alfresco.component.Base,
   {
      /**
       * Object literal used to generate unique tag ids
       *
       * @property tagId
       * @type object
       */
      tagId: null,

      /**
       * Tells whether an action is currently ongoing.
       *
       * @property busy
       * @type boolean
       * @see _setBusy/_releaseBusy
       */
      busy: null,

      /**
       * Offset of first record on page
       * 
       * @property recordOffset
       * @type int
       * @default 0
       */
      recordOffset: 0,

      /**
       * Total number of posts in the current view (across all pages)
       * 
       * @property totalRecords
       * @type int
       * @default 0
       */
      totalRecords: 0,
      
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
          * Initially used filter name and id.
          */
         initialFilter: {},

         /**
          * Number of items displayed per page
          *
          * @property pageSize
          * @type int
          */
         pageSize: 10,

         /**
          * Flag indicating whether the list shows a detailed view or a simple one.
          *
          * @property simpleView
          * @type boolean
          */
         simpleView: false,

         /**
          * permission delete
          */
         permissionDelete: true,

         /**
          * permission update
          */
         permissionUpdate:true,

         /**
          * Length of preview content loaded for each topic
          */
         maxContentLength: 512,

         /**
          * Minimal length of filter panel
          */
         MIN_FILTER_PANEL_WIDTH: 150,

         /**
          * Maximal length of filter panel
          */
         MAX_FILTER_PANEL_WIDTH: 640 - ((YAHOO.env.ua.ie > 0) && (YAHOO.env.ua.ie < 7) ? 160 : 0),

         /**
          * The pagination flag.
          *
          * @property: usePagination
          * @type: boolean
          * @default: true
          */
         usePagination: true,

         /**
          * Minimal height of filter panel
          */
         MAX_FILTER_PANEL_HEIGHT: 200,

         /**
          * ContainerId representing root container
          *
          * @property containerId
          * @type string
          */
         containerId: ""
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function Links_onReady()
      {
         this.activate();
      },

      /**
       * init DataSource
       * @method createDataSource
       * @return {Alfresco.Links} returns 'this' for method chaining
       */
      createDataSource: function Links_createDataSource()
      {
         var uriResults = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/links/site/{site}/{container}",
         {
            site: this.options.siteId,
            container: this.options.containerId
         });

         this.widgets.dataSource = new YAHOO.util.DataSource(uriResults,
         {
            responseType: YAHOO.util.DataSource.TYPE_JSON,
            connXhrMode: 'queueRequests',
            responseSchema:
            {
               resultsList: 'items',
               metaFields:
               {
                  recordOffset: 'startIndex',
                  totalRecords: 'total',
                  metadata: 'metadata',
                  totalRecordsUpper: 'totalRecordsUpper'
               }
            }
         });

         return this;
      },

      /**
       * Updates the toolbar using the passed permissions
       * @method updateToolbar
       * @param linkPermissions {object} Container permissions
       */
      updateToolbar: function Links_updateToolbar(linkPermissions)
      {  
         if (linkPermissions.create === "false")
         {
            this.widgets.newLinkBtn.set("disabled", true);
         }
      },

      /**
       * Initialise DataTable
       *
       * @method createDataTable
       */
      createDataTable: function Links_createDataTable()
      {
         var me = this;

         /**
          * Selector custom datacell formatter
          *
          * @method renderCellSelected
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellSelected = function Links_renderCellSelected(elCell, oRecord, oColumn, oData)
         {
            elCell.innerHTML = '<input class="checkbox-column" type="checkbox" />';
            elCell.firstChild.onclick = function()
            {
               var arr = me.getSelectedLinks();
               // Add 'Delete' item to 'Selected Items' menu, if 'delete' permission is true
               me.addItemToSelectedMenu(arr);
               me.widgets.linksMenu.set("disabled", arr.length === 0);
            };
         };

         /**
          * Description custom datacell formatter
          *
          * @method renderCellDescription
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellDescription = function Links_renderCellDescription(elCell, oRecord, oColumn, oData)
         {
            var data = oRecord.getData();
            var name = data.title,
               url = data.url,
               description = data.description,
               createdOn = data.createdOn,
               author = data.author,
               tags = data.tags,
               internal = data.internal;
            
            var linksViewUrl = me.generateLinksViewUrl(me.options.siteId, me.options.containerId, data.name);
            var tagsStr = "";
            if (tags.length > 0)
            {
               for (var i = 0; i < tags.length; i++)
               {
                  tagsStr += me._generateTagLink(tags[i]);
                  if (i != (tags.length - 1))
                  {
                     tagsStr += ', &nbsp;';
                  }
               }
            }
            else
            {
               tagsStr = me.msg("dialog.tags.none");
            }
            var innerHtml = '<h3 class="link-title"><a href="' + linksViewUrl + '" class="theme-color-1">' + $html(name) + '</a></h3>';
            
            var needHttpPrefix = function(userUrl)
            {
               // check for "://" in URI
               if (me.PROTOCOL_STR_DELIM.test(userUrl))
               {
                  return false;
               }
               
               // check for digits after ":"
               if (me.PORT_STR_DELIM.test(userUrl))
               {
                  return true;
               }
               
               // URI with port was filtered in previous block. Therefore URI with ":" no need "http" prefix
               if (userUrl.indexOf(":")> -1)
               {
                  return false;
               }
               
               // default value
               return true;
            }
            
            innerHtml += '<div class="detail"><span class="item"><em style="padding-right: 2px; float: left">' + me.msg("details.url") + ':</em> ' +
                         '<a style="float: left;" class="theme-color-1"' +  (internal ? '' : ' target="_blank" class="external"') + ' href=' + (needHttpPrefix(url) ? 'http://' : '') +
                         encodeURI(decodeURI(url)) + '>' + $html(url).replace(/'/g, "") + '</a></span></div>';

            if (!me.options.simpleView)
            {
               innerHtml += '<div class="detail"><span class="item"><em>' + me.msg("details.created.on") + ':</em> ' + Alfresco.util.formatDate(createdOn) + '</span>' +
                            '<span class="item"><em>' + me.msg("details.created.by") + ':</em> ' + Alfresco.util.people.generateUserLink(author) + '</span></div>';

               innerHtml += '<div class="detail"><span class="item"><em>' + me.msg("details.description") + ':</em> ' + $links($html(description)) + '</span></div>';

               innerHtml += '<div class="detail"><span class="tag-item"><em>' + me.msg("details.tags") + ': </em>' + tagsStr + '</span></div>';
            }

            elCell.innerHTML = innerHtml;
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
         var renderCellActions = function Links_renderCellActions(elCell, oRecord, oColumn, oData)
         {
            var title = oRecord.getData("title"),
               permissions = oRecord.getData("permissions");
               
            elCell.style.visibility = "hidden";
            elCell.innerHTML = "<div class='" + me.EDITEDCLASS +  "'><a><span class='theme-color-1'>" + me.msg("links.edit") + "</a></span></div>" +
               "<div class='" + me.DELETEDCLASS + "'><a><span class='theme-color-1'>" + me.msg("links.delete") + "</a></span></div>";

            var ec = elCell.childNodes[0],
               dc = elCell.childNodes[1];

            // Edit permission?
            if (permissions.edit)
            {
               ec.onclick = function Links_onEditLink()
               {
                  window.location = YAHOO.lang.substitute(Alfresco.constants.URL_PAGECONTEXT + "site/{site}/links-linkedit?linkId={linkId}",
                  {
                     site: me.options.siteId,
                     linkId: oRecord.getData('name')
                  });
               };

               ec.onmouseover = function()
               {
                  Dom.addClass(this, "over");
               };

               ec.onmouseout = function()
               {
                  Dom.removeClass(this, "over");
               };
            }
            else
            {
               Dom.addClass(ec, 'hidden');
            }
            
            // Delete permission?
            if (permissions["delete"])
            {
               dc.onclick = function ()
               {
                  var mes = me.msg("dialog.confirm.message.delete", title);
                  var callback = function()
                  {
                     me.deleteLinks([oRecord]);
                  };
                  me.showConfirmDialog(mes, callback);
               };

               dc.onmouseover = function()
               {
                  Dom.addClass(this, "over");
               };

               dc.onmouseout = function()
               {
                  Dom.removeClass(this, "over");
               };

            }
            else
            {
               Dom.addClass(dc, 'hidden');
            }

            // Styling
            Dom.setStyle(elCell.parentNode, "border-left", "3px solid #fff");
            if (me.options.simpleView)
            {
               Dom.addClass(elCell.parentNode, 'simple-view');
            }
            else
            {
               Dom.removeClass(elCell.parentNode, 'simple-view');
            }
         };
         
         var columnDefinitions =
         [
            {
               key: 'selected', label: 'Selected', sortable: false, formatter: renderCellSelected
            },
            {
               key: 'title', label: 'Title', sortable: false, formatter: renderCellDescription
            },
            {
               key: 'description', label: 'Description', formatter: renderCellActions
            }
         ];

         this.widgets.paginator = new YAHOO.widget.Paginator(
         {
            containers: [this.id + "-paginator"],
            rowsPerPage: this.options.pageSize,
            initialPage: 1,
            template: this.msg("pagination.template"),
            pageReportTemplate: this.msg("pagination.template.page-report"),
            previousPageLinkLabel: this.msg("pagination.previousPageLinkLabel"),
            nextPageLinkLabel: this.msg("pagination.nextPageLinkLabel")
         });

         // called by the paginator on state changes
         var handlePagination = function Links_handlePagination (state, dt)
         {
            me.updateLinks(
            {
               page: state.page
            });
         };

         this.widgets.paginator.subscribe("changeRequest", handlePagination);

         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + '-links', columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: 32,
            initialLoad: false,
            MSG_EMPTY: '<span class="datatable-msg-empty">' + this.msg("links.empty") + '</span>'
         });

         YAHOO.widget.DataTable.CLASS_SELECTED = "links-selected-row";

         this.widgets.dataTable.doBeforeLoadData = function Links_doBeforeLoadData(sRequest, oResponse, oPayload)
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
               }

            }
            else if (oResponse.results && !me.options.usePagination)
            {
               this.renderLoopSize = Alfresco.util.RENDERLOOPSIZE;
            }

            // Must return true to have the "Loading..." message replaced by the error message
            return true;
         };

         // Update totalRecords on the fly with value from server
         this.widgets.dataTable.handleDataReturnPayload = function Links_handleDataReturnPayload(oRequest, oResponse, oPayload)
         {
            // Save totalRecords for Paginator update later
            me.recordOffset = oResponse.meta.recordOffset;
            me.totalRecords = oResponse.meta.totalRecords;
            me.totalRecordsUpper = oResponse.meta.totalRecordsUpper;

            oPayload = oPayload || {};
            oPayload.recordOffset = oResponse.meta.recordOffset;
            oPayload.totalRecords = oResponse.meta.totalRecords;
            return oPayload;
         };

         // Prevent the DataTable from updating the Paginator widget
         this.widgets.dataTable.doBeforePaginatorChange = function Links_doBeforePaginatorChange(oPaginatorState)
         {
            return false;
         };

         // Rendering complete event handler
         this.widgets.dataTable.subscribe("renderEvent", function()
         {
            // Update the paginator if it's been created
            this.widgets.paginator.setState(
            {
               recordOffset: this.recordOffset,
               totalRecords: this.totalRecords
            });
            if (this.totalRecordsUpper && this.totalRecordsUpper == true)
            {
               this.widgets.paginator.set("pageReportTemplate", this.msg("pagination.template.page-report.more"));
            }
            else
            {
               this.widgets.paginator.set("pageReportTemplate", this.msg("pagination.template.page-report"));
            }
            this.widgets.paginator.render();
         }, this, true);

         this.widgets.dataTable.subscribe("tableMsgShowEvent", function(oArgs)
         {
            // NOTE: Scope needs to be DataTable
            this._elMsgTbody.parentNode.style.width = "";
         });

         this.widgets.dataTable.set("selectionMode", "single");

         var onRowMouseover = function Links_onRowMouseover(e)
         {
            me.widgets.dataTable.selectRow(e.target);
            if (e.target.cells && e.target.cells.length > 1)
            {
               e.target.cells[2].childNodes[0].style.visibility = "visible";
               e.target.cells[2].childNodes[0].style.width = "100px";
               e.target.cells[2].style.borderLeft = "1px solid #ccc";
            }
         };

         var onRowMouseout = function Links_onRowMouseout(e)
         {
            me.widgets.dataTable.unselectRow(e.target);
            if (e.target.cells && e.target.cells.length > 1)
            {
               e.target.cells[2].childNodes[0].style.visibility = "hidden";
               e.target.cells[2].style.borderLeft = "1px solid #fff";
            }
         };

         this.widgets.dataTable.subscribe("rowMouseoverEvent", onRowMouseover);
         this.widgets.dataTable.subscribe("rowMouseoutEvent", onRowMouseout);

         var filterObj = YAHOO.lang.merge(
         {
            filterId: "all",
            filterOwner: "Alfresco.LinkFilter",
            filterData: null
         }, this.options.initialFilter);
         
         YAHOO.Bubbling.fire("changeFilter", filterObj);
      },

      /**
      * Generate a view url for a given site, link id.
      *
      * @param linkId the id/name of the link
      * @return an url to access the link
      */
      generateLinksViewUrl: function Links_generateLinksViewUrl(site, container, linkId)
      {
         var url = YAHOO.lang.substitute(Alfresco.constants.URL_PAGECONTEXT + "site/{site}/links-view?linkId={linkId}&listViewLinkBack=true",
         {
            site: site,
            container: container,
            linkId: linkId
         });
         return url;
      },

      /**
       * Links Filter changed event handler
       *
       * @method onChangeFilter
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (new filterId)
       */
      onChangeFilter: function Links_onChangeFilter(layer, args)
      {
         var obj = args[1];
         if ((obj !== null) && (obj.filterId !== null))
         {
            this.currentFilter =
            {
               filterId: obj.filterId,
               filterOwner: obj.filterOwner,
               filterData: obj.filterData
            };
            this.updateLinks(
            {
               page: 1
            });
            YAHOO.Bubbling.fire("filterChanged", this.currentFilter);
         }
      },

      /**
       * Links Refresh Required event handler
       *
       * @method onLinksListRefresh
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (unused)
       */
      onLinksListRefresh: function Links_onLinksListRefresh(layer, args)
      {
         this.updateLinks();
      },

      /**
       * Updates links list by calling data webscript with current site and filter information
       *
       * @method updateLinks
       */
      updateLinks: function Links_updateLinks(p_obj)
      {
         function successHandler(sRequest, oResponse, oPayload)
         {
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
            this.updateListTitle();
            this.widgets.linksMenu.set("disabled", this.getSelectedLinks().length === 0);
            var perm = oResponse.meta.metadata.linkPermissions;
            this.options.permissionDelete = perm["delete"];
            this.options.permissionUpdate = perm.edit;
            this.updateToolbar(perm);
         }

         function failureHandler(sRequest, oResponse)
         {
            if (oResponse.status == 401)
            {
               // Our session has likely timed-out, so refresh to offer the login page
               window.location.reload(true);
            }
            else
            {
               try
               {
                  var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                  this.widgets.dataTable.set("MSG_ERROR", response.message);
                  this.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
                  if (oResponse.status == 404)
                  {
                     YAHOO.Bubbling.fire("deactivateAllControls");
                  }
               }
               catch(e)
               {
               }
            }
         }
         this.widgets.dataSource.sendRequest(this._buildLinksParams(p_obj || {}),
         {
            success: successHandler,
            failure: failureHandler,
            scope: this
         });

      },

      /**
       * Update the list title.
       * @method updateListTitle
       */
      updateListTitle: function Links_updateListTitle()
      {
         var elem = Dom.get(this.id + '-listTitle');
         var title = this.msg("title.generic");

         var filterOwner = this.currentFilter.filterOwner;
         var filterId = this.currentFilter.filterId;
         var filterData = this.currentFilter.filterData;
         if (filterOwner == "Alfresco.LinkFilter")
         {
            switch (filterId)
            {
               case "all":
                  title = this.msg("title.all");
                  break;
               case "user":
                  title = this.msg("title.user");
                  break;
               case "recent":
                  title = this.msg("title.recent");
                  break;
            }

         }
         else if (filterOwner == "Alfresco.TagFilter")
         {
            title = this.msg("title.bytag", $html(filterData));
         }

         elem.innerHTML = title;
      },

      /**
       * Deactivate All Controls event handler
       *
       * @method onDeactivateAllControls
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDeactivateAllControls: function Links_onDeactivateAllControls(layer, args)
      {
         var index, fnDisable = Alfresco.util.disableYUIButton;
         for (index in this.widgets)
         {
            if (this.widgets.hasOwnProperty(index))
            {
               fnDisable(this.widgets[index]);
            }
         }
      },

      /**
       * activation of components
       * @method activate.
       */
      activate: function Links_activate()
      {
         this.attachButtons();
         Dom.setStyle(this.id + '-links-header', 'visibility', 'visible');
         Dom.setStyle(this.id + '-body', 'visibility', 'visible');

         this.createDataSource();
         this.createDataTable();
      },

      /**
      * menu item event handler
      * @method onMenuItemClick.
      * @param sType, aArgs, p_obj
      */
      onMenuItemClick: function Links_onMenuItemClick(sType, aArgs, p_obj)
      {
         var me = this;
         switch (aArgs[1]._oAnchor.className.split(" ")[0])
         {
            case "delete-item":
               var callback = function()
               {
                  var arrLinks = me.getSelectedLinks();
                  me.deleteLinks(arrLinks);
               };
               this.showConfirmDialog(this.msg("dialog.confirm.message.delete.selected"), callback);
               break;
               
            case "deselect-item":
               this.deselectAll();
               this.widgets.linksMenu.set("disabled", true);
               break;
         }

      },

      /**
      * deselect all links
      * @method deselectAll.
      * @param no params
      */
      deselectAll: function Links_deselectAll()
      {
         var rows = this.widgets.dataTable.getTbodyEl().rows;
         for (var i = 0; i < rows.length; i++)
         {
            rows[i].cells[0].getElementsByTagName('input')[0].checked = false;

         }
         this.addItemToSelectedMenu(this.getSelectedLinks());
      },

      /**
       * init links buttons
       * @method attachButtons.
       */
      attachButtons: function Links_attachButtons()
      {
         this.widgets.newLinkBtn = Alfresco.util.createYUIButton(this, "create-link-button", this.showCreateLinkDlg,
         {
            disabled: false,
            value: "create"
         });

         this.widgets.linksMenu = Alfresco.util.createYUIButton(this, "selected-i-dd", this.onMenuItemClick,
         {
            disabled: true,
            type: "menu",
            menu:"selectedItems-menu"
         });

         this.widgets.changeListViewBtn = Alfresco.util.createYUIButton(this, "viewMode-button", this.changeListView,
         {
         });

         this.linksSelectMenu = Alfresco.util.createYUIButton(this, "select-button", this.onSelectItemClick,
         {
            type: "menu",
            menu: "selecItems-menu"
         });

         this.widgets.rssFeed = Alfresco.util.createYUIButton(this, "rss-feed", null,
         {
            type: "link"
         });
         if (this.widgets.rssFeed !== null)
         {
            this.widgets.rssFeed.set("href", this._generateRSSFeedUrl());
         }
      },

      /**
       * Handler on Menu Item Click
       * @param sType
       * @param aArgs
       * @param p_obj
       * @method onSelectItemClick
       */
      onSelectItemClick: function Links_onSelectItemClick(sType, aArgs, p_obj)
      {
         var elem = YAHOO.env.ua.ie ? aArgs[0].srcElement : aArgs[0].target;
         if (elem.tagName.toLocaleLowerCase() != "span")
         {
            elem = elem.getElementsByTagName("span")[0];
         }
         switch (elem.className.split(" ")[0])
         {
            case "links-action-deselect-all":
               this.deselectAll();
               this.widgets.linksMenu.set("disabled", true);
               break;

            case "links-action-select-all":
               this.selectAll();
               this.widgets.linksMenu.set("disabled", !this.getSelectedLinks().length);
               break;

            case "links-action-invert-selection":
               this.invertAll();
               break;
         }
      },

      /**
       * Invert All Selection on the page
       * @method invertAll
       */
      invertAll: function Links_invertAll()
      {
         var isDisable = false;
         var rows = this.widgets.dataTable.getTbodyEl().rows;
         for (var i = 0; i < rows.length; i++)
         {
            var ipt = rows[i].cells[0].getElementsByTagName('input')[0];
            ipt.checked = !ipt.checked;
            isDisable = ipt.checked ? true : isDisable;
         }
         // Add 'Delete' item to 'Selected Items' menu, if 'delete' permission is true
         this.addItemToSelectedMenu(this.getSelectedLinks());
         this.widgets.linksMenu.set("disabled", !isDisable);
      },

      /**
       * select All Tags on the page
       * @method selectAll
       */
      selectAll: function Links_selectAll()
      {
         var rows = this.widgets.dataTable.getTbodyEl().rows;
         for (var i = 0; i < rows.length; i++)
         {
            rows[i].cells[0].getElementsByTagName('input')[0].checked = true;
         }
         // Add 'Delete' item to 'Selected Items' menu, if 'delete' permission is true
         this.addItemToSelectedMenu(this.getSelectedLinks());
      },

      /**
       * show 'Create Link' dialog
       * @method showCreateLinkDlg.
       */
      showCreateLinkDlg: function Links_showCreateLinkDlg()
      {
         var url = YAHOO.lang.substitute(Alfresco.constants.URL_PAGECONTEXT + "site/{site}/links-linkedit",
         {
            site: this.options.siteId
         });
         window.location = url;
      },

      /**
       * change list view
       * @method changeListView.
       */
      changeListView: function Links_changeListView()
      {
         var records = this.widgets.dataTable.getRecordSet().getRecords();
         var rows = this.widgets.dataTable.getTbodyEl().rows;
         var colDefinitions = this.widgets.dataTable.getColumnSet().getDefinitions();

         this.options.simpleView = !this.options.simpleView;
         var j = 0;
         for (var i in records)
         {
            if (i)
            {
               colDefinitions[1].formatter.call(this, rows[j].cells[1].firstChild, records[i]);
               colDefinitions[2].formatter.call(this, rows[j].cells[2].firstChild, records[i]);
               j++;
            }
         }
         this.widgets.changeListViewBtn.set("label", this.msg(this.options.simpleView ? "header.detailedList" : "header.simpleList"));
      },

      /**
       * @method deleteLinks
       * @param arr {array}
       */
      deleteLinks: function Links_deleteLinks(arr)
      {
         if (!this._setBusy(this.msg('message.wait')))
         {
            return;
         }

         // get the url to call
         var ids = [];
         for (var i in arr)
         {
            if (i)
            {
               ids.push(arr[i].getData().name);
            }
         }

         var url = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/links/delete/site/{site}/{container}",
         {
            site: this.options.siteId,
            container: this.options.containerId
         });

         // ajax request success handler
         var onDeletedSuccess = function Links_deleteLinkConfirm_onDeletedSuccess(response)
         {
            // remove busy message
            this._releaseBusy();

            // reload the table data
            this.updateLinks();
            YAHOO.Bubbling.fire("tagRefresh");
         };

         // execute ajax request
         Alfresco.util.Ajax.request(
         {
            url: url,
            method: "POST",
            requestContentType: "application/json",
            successMessage: this.msg("message.delete.success"),
            successCallback:
            {
               fn: onDeletedSuccess,
               scope: this
            },
            failureMessage: this.msg("message.delete.failure"),
            failureCallback:
            {
               fn: function(response)
               {
                  response.config.failureMessage = YAHOO.lang.JSON.parse(response.serverResponse.responseText).message;
                  this._releaseBusy();
               },
               scope: this
            },
            dataObj:
            {
               items: ids
            }
         });
      },

      /**
       * Adds the link.
       *
       * @param rowData {object} the row's data.
       * @method createLink.
       */
      createLink: function Links_createLink(data)
      {
         this.updateLinks(
         {
            page: 1
         });
      },

      /**
       * Updates the link.
       *
       * @param rowData {object} the row's data.
       * @param row {YAHOO.widget.Record}.
       * @method updateLink.
       */
      onUpdateLink: function Links_onUpdateLink(rowData, row)
      {
         this.updateLinks();
      },

      /**
       * Show delete confirm dialog.
       * @param row {YAHOO.widget.Record} the row which needs for delete.
       */
      showConfirmDialog: function Links_showConfirmDialog(mes, callback)
      {
         Alfresco.util.PopupManager.displayPrompt(
         {
            text: mes,
            title: this.msg("actions.link.delete"),
            buttons: [
            {
               text: this.msg("button.delete"),
               handler: function()
               {
                  callback();
                  this.destroy();
               }
            },
            {
               text: this.msg("button.cancel"),
               handler: function()
               {
                  this.destroy();
               },
               isDefault: true
            }]
         });
      },

      /**
       * Add 'Delete' item to 'Selected Items' menu.
       * @param arr 
       * @method addItemToSelectedMenu
       */
      addItemToSelectedMenu: function Links_addItemToSelectedMenu(arr)
      {
         var menu = this.widgets.linksMenu.getMenu();
		 
         if (menu.element.hasChildNodes())
         {
            if (this.checkPermissionSelectedLinks(arr))
            {
               if (Dom.getElementsByClassName("delete-item", "a")[0] == null)
               {
                  if (menu.getItems().length == 0)
                  {
                     var tagLi = document.createElement("li");  

                     tagLi.innerHTML = "<a class='delete-item' rel='' href='#'><span class='links-action-delete'>" + this.msg("links.delete") + "</span></a>";

                     var elementUl = menu.element.getElementsByTagName("ul")[0];

                     var lastTagLi = Dom.getLastChild(elementUl);

                     if (lastTagLi == null)
                     {
                        elementUl.appendChild(tagLi);
                     }
                     else
                     {
                        Dom.insertBefore(tagLi, lastTagLi);
                     }
                  }
                  else
                  {
                     menu.getItem(0, 0).index = 1;
                     // Add new menu item
                     var deleteItem = menu.insertItem("<span class='links-action-delete'>" + this.msg("links.delete") + "</span>",0,0);
						
                     //Add 'delete-item' class to the first place (for onMenuItemClick)
                     var tagADelete = Dom.getFirstChild(deleteItem.element);
                     var className = tagADelete.className;	
                     tagADelete.className = 'delete-item ' + className;			
                  }
               }
            }
            else
            {
               if (Dom.getElementsByClassName("delete-item", "a")[0] != null)
               {
                  menu.removeItem(menu.getItem(0, 0), 0);
               }
            }
         }
      },

      /**
       * Check 'delete' permission of selected links
       *
       * @method checkPermissionSelectedLinks
       * @param arr
       * @return {boolean} 'delete' permission of selected links
       */
      checkPermissionSelectedLinks: function Links_checkPermissionSelectedLinks(arr)
      {
        var result = false;
        for (var i = 0; i < arr.length; i++)
        {
           result = arr[i]._oData.permissions["delete"];
           if (!result)
           {
              break;
           }
        }
        return result;
      },
     
      /**
       * Gets the array of selected links.
       *
       * @method getSelectedLinks
       */
      getSelectedLinks: function Links_getSelectedLinks()
      {
         var arr = [];
         var rows = this.widgets.dataTable.getTbodyEl().rows;
         for (var i = 0; i < rows.length; i++)
         {
            if (rows[i].cells[0].getElementsByTagName('input')[0].checked)
            {
               var data = this.widgets.dataTable.getRecord(i);
               if (data)
               {
                  arr.push(data);
               }
            }
         }

         return arr;
      },

      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Generate ID alias for tag, suitable for DOM ID attribute
       *
       * @method _generateTagId
       * @param tagName {string} Tag name
       * @return {string} A unique DOM-safe ID for the tag
       */
      _generateTagId: function Links__generateTagId(tagName)
      {
         var id = 0;
         var tagId = this.tagId;
         if (tagName in tagId.tags) {
            id = tagId.tags[tagName];
         }
         else
         {
            tagId.id++;
            id = tagId.tags[tagName] = tagId.id;
         }
         return this.id + "-tagId-" + id;
      },

      /**
       * Build URI parameter string for doclist JSON data webscript
       *
       * @method _buildDocListParams
       * @param p_obj.page {string} Page number
       * @param p_obj.pageSize {string} Number of items per page
       */
      _buildLinksParams: function Links_buildLinksParams(p_obj)
      {
         var params =
         {
            contentLength: this.options.maxContentLength,
            fromDate: null,
            toDate: null,
            tag: null,
            page: this.widgets.paginator.getCurrentPage() || "1",
            pageSize: this.widgets.paginator.getRowsPerPage()
         };

         // Passed-in overrides
         if (typeof p_obj == "object")
         {
            params = YAHOO.lang.merge(params, p_obj);
         }

         // calculate the startIndex param
         params.startIndex = (params.page - 1) * params.pageSize;

         // check what url to call and with what parameters
         var filterOwner = this.currentFilter.filterOwner;
         var filterId = this.currentFilter.filterId;
         var filterData = this.currentFilter.filterData;

         // check whether we got a filter or not
         var url = "";
         var isFirstParam = true;
         if (filterOwner == "Alfresco.LinkFilter")
         {
            url = "?filter=" + filterId;
            isFirstParam = false;
         }
         else if (filterOwner == "Alfresco.TagFilter")
         {
            url = "?filter=tag";
            params.tag = filterData;
            isFirstParam = false;
         }

         // build the url extension
         var urlExt = "";
         for (var paramName in params)
         {
            if (params[paramName] !== null)
            {
               urlExt += "&" + paramName + "=" + encodeURIComponent(params[paramName]);
            }
         }
         if (urlExt.length > 0)
         {
            urlExt = urlExt.substring(1);
         }
         return url + (isFirstParam ? "?" : "&") + urlExt;
      },

      /**
       * Removes the busy message and marks the component as non-busy
       */
      _releaseBusy: function Links_releaseBusy()
      {
         if (this.busy)
         {
            if (this.widgets.busyMessage.destroyWithAnimationsStop != undefined)
            {
               this.widgets.busyMessage.destroyWithAnimationsStop();
            }
            else
            {
               this.widgets.busyMessage.destroy();
            }
            this.busy = false;
            return true;
         }
         else
         {
            return false;
         }
      },

      /**
       * Displays the provided busyMessage but only in case
       * the component isn't busy set.
       *
       * @return true if the busy state was set, false if the component is already busy
       */
      _setBusy: function Links__setBusy(busyMessage)
      {
         if (this.busy)
         {
            return false;
         }
         this.busy = true;
         this.widgets.busyMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: busyMessage,
            spanClass: "wait",
            displayTime: 0
         });
         return true;
      },

      /**
       * Generate the html markup for a tag link.
       *
       * @method _generateTagLink
       * @param tagName {string} the tag to create a link for
       * @return {string} the markup for a tag
       */
      _generateTagLink: function Links_generateTagLink(tagName)
      {
         var encodedTagName = $html(tagName);
         return '<span class="tag"><a href="#" class="tag-link" rel="' + encodedTagName + '" title="' + encodedTagName + '">' + encodedTagName + '</a></span>';
      },

      /**
       * Generates the HTML mark-up for the RSS feed link
       *
       * @method _generateRSSFeedUrl
       * @private
       */
      _generateRSSFeedUrl: function Links__generateRSSFeedUrl()
      {
         var url = YAHOO.lang.substitute(Alfresco.constants.URL_FEEDSERVICECONTEXT + "components/links/rss?site={site}",
         {
            site: this.options.siteId
         });

         return url;
      }
   });
})();