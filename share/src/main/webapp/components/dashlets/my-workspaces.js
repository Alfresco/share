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
 * Dashboard MyWorkspaces component.
 * 
 * @namespace Alfresco.dashlet
 * @class Alfresco.dashlet.MyWorkspaces
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
    * Dashboard MyWorkspaces constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.dashlet.MyWorkspaces} The new component instance
    * @constructor
    */
   Alfresco.dashlet.MyWorkspaces = function MyWorkspaces_constructor(htmlId)
   {
      Alfresco.dashlet.MyWorkspaces.superclass.constructor.call(this, "Alfresco.dashlet.MyWorkspaces", htmlId, ["button", "container", "datasource", "datatable", "animation"]);

      // Initialise prototype properties
      this.preferencesService = new Alfresco.service.Preferences();

      // Listen for events from other components
      YAHOO.Bubbling.on("siteDeleted", this.onSiteDeleted, this);

      return this;
   };

   YAHOO.extend(Alfresco.dashlet.MyWorkspaces, Alfresco.component.Base,
   {
      /**
       * CreateSite module instance.
       * 
       * @property createSite
       * @type Alfresco.module.CreateSite
       */
      createSite: null,

      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * Site data
          * 
          * @property sites
          * @type array
          */
         sites: [],

         /**
          * Flag if IMAP server is enabled
          * 
          * @property imapEnabled
          * @type boolean
          * @default false
          */
         imapEnabled: false
      },

      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function MyWorkspaces_onReady()
      {
         var me = this;
         
         // Listen on clicks for the create site link
         Event.addListener(this.id + "-createSite-button", "click", this.onCreateSiteLinkClick, this, true);
         
         // DataSource definition
         this.widgets.dataSource = new YAHOO.util.DataSource(this.options.sites,
         {
            responseType: YAHOO.util.DataSource.TYPE_JSARRAY
         });

         /**
          * Use the getDomId function to get some unique names for global event handling
          */
         var favEventClass = Alfresco.util.generateDomId(null, "fav-site"),
            imapEventClass = Alfresco.util.generateDomId(null, "imap-site"),
            deleteEventClass = Alfresco.util.generateDomId(null, "del-site");

         /**
          * Favourites custom datacell formatter
          */
         var renderCellFavourite = function MS_oR_renderCellFavourite(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            var siteId = oRecord.getData("shortName"),
               isFavourite = oRecord.getData("isFavourite"),
               isIMAPFavourite = oRecord.getData("isIMAPFavourite");

            var desc = '<div class="site-favourites">';
            desc += '<a class="favourite-site ' + favEventClass + (isFavourite ? ' enabled' : '') + '" title="' + me.msg("link.favouriteWorkspace") + '">&nbsp;</a>';
            if (me.options.imapEnabled)
            {
               desc += '<a class="imap-favourite-site ' + imapEventClass + (isIMAPFavourite ? ' imap-enabled' : '') + '" title="' + me.msg("link.imap_favouriteWorkspace") + '">&nbsp;</a>';
            }
            desc += '</div>';
            elCell.innerHTML = desc;
         };
         
         /**
          * Name & description custom datacell formatter
          */
         var renderCellName = function MS_oR_renderCellName(elCell, oRecord, oColumn, oData)
         {
            var siteId = oRecord.getData("shortName"),
               siteTitle = oRecord.getData("title"),
               siteDescription = oRecord.getData("description");
            
            var desc = '<div class="site-title"><a href="' + Alfresco.constants.URL_PAGECONTEXT + 'site/' + siteId + '/dashboard" class="theme-color-1">' + $html(siteTitle) + '</a></div>';
            desc += '<div class="site-description">' + $html(siteDescription) + '</div>';

            elCell.innerHTML = desc;
         };
         
         /**
          * Actions custom datacell formatter
          */
         var renderCellActions = function MS_oR_renderCellActions(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            var isSiteManager = oRecord.getData("isSiteManager"),
               desc = "";
            
            if (isSiteManager)
            {
               desc = '<a class="delete-site ' + deleteEventClass + '" title="' + me.msg("link.deleteWorkspace") + '">&nbsp;</a>';
            }
            elCell.innerHTML = desc;
         };

         // DataTable column defintions
         var columnDefinitions =
         [
            { key: "siteId", label: "Favourite", sortable: false, formatter: renderCellFavourite, width: this.options.imapEnabled ? 40 : 20 },
            { key: "title", label: "Description", sortable: false, formatter: renderCellName },
            { key: "description", label: "Actions", sortable: false, formatter: renderCellActions, width: 32 }
         ];

         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-workspaces", columnDefinitions, this.widgets.dataSource,
         {
            MSG_EMPTY: '<h3>' + this.msg("label.noWorkspaces") + '</h3>'
         });

         // Add animation to row delete
         this.widgets.dataTable._deleteTrEl = function(row)
         { 
            var scope = this,
               trEl = this.getTrEl(row);

            var changeColor = new YAHOO.util.ColorAnim(trEl,
            {
               opacity:
               {
                  to: 0
               }
            }, 0.25);
            changeColor.onComplete.subscribe(function()
            {
               YAHOO.widget.DataTable.prototype._deleteTrEl.call(scope, row);
            });
            changeColor.animate();
         };

         /**
          * Hook favourite site events
          */
         var registerEventHandler = function(cssClass, fnHandler)
         {
            var fnEventHandler = function MS_oR_fnEventHandler(layer, args)
            {
               var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
               if (owner !== null)
               {
                  fnHandler.call(me, args[1].target.offsetParent, owner);
               }

               return true;
            };
            YAHOO.Bubbling.addDefaultAction(cssClass, fnEventHandler);
         };
         
         registerEventHandler(favEventClass, this.onFavouriteSite);
         registerEventHandler(imapEventClass, this.onImapFavouriteSite);
         registerEventHandler(deleteEventClass, this.onDeleteSite);

         // Enable row highlighting
         this.widgets.dataTable.subscribe("rowMouseoverEvent", this.widgets.dataTable.onEventHighlightRow);
         this.widgets.dataTable.subscribe("rowMouseoutEvent", this.widgets.dataTable.onEventUnhighlightRow);
      },

      /**
       * Adds an event handler for bringing up the delete site dialog for the specific site
       *
       * @method onDeleteSite
       * @param row {object} DataTable row representing file to be actioned
       */
      onDeleteSite: function MyWorkspaces_onDeleteSite(row)
      {
         var record = this.widgets.dataTable.getRecord(row);

         // Display the delete dialog for the site
         Alfresco.module.getDeleteSiteInstance().show(
         {
            site: record.getData()
         });
      },

      /**
       * Fired by DeleteSite module when a site has been deleted.
       *
       * @method onSiteDeleted
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (unused)
       */
      onSiteDeleted: function MyWorkspaces_onSiteDeleted(layer, args)
      {
         var site = args[1].site,
            siteId = site.shortName;

         // Find the record corresponding to this site
         var record = this._findRecordByParameter(siteId, "shortName");
         if (record !== null)
         {
            this.widgets.dataTable.deleteRow(record);
         }
      },

      /**
       * Adds an event handler that adds or removes the site as favourite site
       *
       * @method onFavouriteSite
       * @param row {object} DataTable row representing file to be actioned
       */
      onFavouriteSite: function MyWorkspaces_onFavouriteSite(row)
      {
         var record = this.widgets.dataTable.getRecord(row),
            site = record.getData(),
            siteId = site.shortName;
         
         site.isFavourite = !site.isFavourite;
         
         this.widgets.dataTable.updateRow(record, site);
         
         // Assume the call will succeed, but register a failure handler to replace the UI state on failure
         var responseConfig =
         {
            failureCallback:
            {
               fn: function MS_oFS_failure(event, obj)
               {
                  // Reset the flag to it's previous state
                  var record = obj.record,
                     site = record.getData();
                  
                  site.isFavourite = !site.isFavourite;
                  this.widgets.dataTable.updateRow(record, site);
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     text: this.msg("message.workspaceFavourite.failure", site.title)
                  });
               },
               scope: this,
               obj:
               {
                  record: record
               }
            },
            successCallback:
            {
               fn: function MS_oFS_success(event, obj)
               {
                  var record = obj.record,
                     site = record.getData();

                  YAHOO.Bubbling.fire(site.isFavourite ? "favouriteSiteAdded" : "favouriteSiteRemoved", site);
               },
               scope: this,
               obj:
               {
                  record: record
               }
            }
         };
         
         this.preferencesService.favouriteSite(siteId, responseConfig);
      },

	   /**
       * Adds an event handler that adds or removes the site as favourite site
       *
       * @method _addImapFavouriteHandling
       * @param row {object} DataTable row representing file to be actioned
       */
      onImapFavouriteSite: function MyWorkspaces_onImapFavouriteSite(row)
      {
         var record = this.widgets.dataTable.getRecord(row),
            site = record.getData(),
            siteId = site.shortName;
         
         site.isIMAPFavourite = !site.isIMAPFavourite;
         
         this.widgets.dataTable.updateRow(record, site);
         
         // Assume the call will succeed, but register a failure handler to replace the UI state on failure
         var responseConfig =
         {
            failureCallback:
            {
               fn: function MS_oIFS_failure(event, obj)
               {
                  // Reset the flag to it's previous state
                  var record = obj.record,
                     site = record.getData();
                  
                  site.isIMAPFavourite = !site.isIMAPFavourite;
                  this.widgets.dataTable.updateRow(record, site);
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     text: this.msg("message.workspaceFavourite.failure", site.title)
                  });
               },
               scope: this,
               obj:
               {
                  record: record
               }
            }
         };

         this.preferencesService.set(Alfresco.service.Preferences.IMAP_FAVOURITE_SITES + "." + siteId, site.isIMAPFavourite, responseConfig);
      },
      
      /**
       * Fired by YUI Link when the "Create site" label is clicked
       * @method onCreateSiteLinkClick
       * @param event {domEvent} DOM event
       */
      onCreateSiteLinkClick: function MyWorkspaces_onCreateSiteLinkClick(event)
      {
         Alfresco.module.getCreateSiteInstance().show();
         Event.preventDefault(event);
      },

      /**
       * Searches the current recordSet for a record with the given parameter value
       *
       * @method _findRecordByParameter
       * @param p_value {string} Value to find
       * @param p_parameter {string} Parameter to look for the value in
       */
      _findRecordByParameter: function MyWorkspaces__findRecordByParameter(p_value, p_parameter)
      {
        var recordSet = this.widgets.dataTable.getRecordSet();
        for (var i = 0, j = recordSet.getLength(); i < j; i++)
        {
           if (recordSet.getRecord(i).getData(p_parameter) == p_value)
           {
              return recordSet.getRecord(i);
           }
        }
        return null;
      }
   });
})();