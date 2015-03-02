/**
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
 * Dashboard MySites component.
 *
 * @namespace Alfresco.dashlet
 * @class Alfresco.dashlet.MySites
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event,
       Selector = YAHOO.util.Selector;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
       $links = Alfresco.util.activateLinks;

   /**
    * Use the getDomId function to get some unique names for global event handling
    */
   var FAV_EVENTCLASS = Alfresco.util.generateDomId(null, "fav-site"),
       IMAP_EVENTCLASS = Alfresco.util.generateDomId(null, "imap-site"),
       DELETE_EVENTCLASS = Alfresco.util.generateDomId(null, "del-site");

   /**
    * Dashboard MySites constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.dashlet.MySites} The new component instance
    * @constructor
    */
   Alfresco.dashlet.MySites = function MySites_constructor(htmlId)
   {
      Alfresco.dashlet.MySites.superclass.constructor.call(this, "Alfresco.dashlet.MySites", htmlId, ["datasource", "datatable", "animation"]);

      // Initialise prototype properties
      this.sites = [];
      this.createSite = null;

      // Services
      this.services.preferences = new Alfresco.service.Preferences();

      // Listen for events from other components
      YAHOO.Bubbling.on("siteDeleted", this.onSiteDeleted, this);

      return this;
   };

   YAHOO.extend(Alfresco.dashlet.MySites, Alfresco.component.Base,
   {
      /**
       * Preferences
       */
      PREFERENCES_SITES_DASHLET: "",
      PREFERENCES_SITES_DASHLET_FILTER: "",

      /**
       * Site data
       *
       * @property sites
       * @type array
       */
      sites: null,

      /**
       * CreateSite module instance.
       *
       * @property createSite
       * @type Alfresco.module.CreateSite
       */
      createSite: null,
      
      /**
       * Favorite sites list
       * 
       * @property favSites
       * @type Object
       */
      favSites: null,
      
      /**
       * IMAP Favorite sites list
       * 
       * @property imapfavSites
       * @type Object
       */
      imapfavSites: null,
      
      /**
       * Selected filter value
       * 
       * @property filter
       * @type String
       */
      filter: null,

      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * List of valid filters
          *
          * @property validFilters
          * @type object
          */
         validFilters:
         {
            "all": true,
            "favSites": true,
            "recentSites": true
         },
         
         /**
          * Flag if IMAP server is enabled
          *
          * @property imapEnabled
          * @type boolean
          * @default false
          */
         imapEnabled: false,
         
         /**
          * Result list size maximum
          *
          * @property listSize
          * @type integer
          * @default 100
          */
         listSize: 100
      },

      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function MySites_onReady()
      {
         var me = this;

         // Fetch preferences
         this.PREFERENCES_SITES_DASHLET = this.services.preferences.getDashletId(this, "sites");
         this.PREFERENCES_SITES_DASHLET_FILTER = this.PREFERENCES_SITES_DASHLET + ".filter";

         // Create Dropdown filter
         this.widgets.type = Alfresco.util.createYUIButton(this, "type", this.onTypeFilterChanged,
         {
            type: "menu",
            menu: "type-menu",
            lazyloadmenu: false
         });

         // Listen on clicks for the create site link
         var createSiteLink = Dom.get(this.id + "-createSite-button");
         if (createSiteLink)
         {
            Event.addListener(createSiteLink, "click", this.onCreateSite, this, true);
         }

         // DataSource definition
         this.widgets.dataSource = new YAHOO.util.DataSource(this.sites,
         {
            responseType: YAHOO.util.DataSource.TYPE_JSARRAY
         });

         // DataTable column defintions
         var columnDefinitions =
         [
            { key: "icon", label: "Icon", sortable: false, formatter: this.bind(this.renderCellIcon), width: 52 },
            { key: "detail", label: "Description", sortable: false, formatter: this.bind(this.renderCellDetail) },
            { key: "actions", label: "Actions", sortable: false, formatter: this.bind(this.renderCellActions), width: 24 }
         ];

         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-sites", columnDefinitions, this.widgets.dataSource,
         {
            MSG_EMPTY: this.msg("message.datatable.loading")
         });

         // Override abstract function within DataTable to set custom empty message
         this.widgets.dataTable.doBeforeLoadData = function MySites_doBeforeLoadData(sRequest, oResponse, oPayload)
         {
            if ((oResponse.results.length === 0) || (oResponse.results.length === 1 && oResponse.results[0].shortName === "swsdp"))
            {
               oResponse.results.unshift(
               {
                  isInfo: true,
                  title: me.msg("empty.title"),
                  description: me.msg("empty.description") + (oResponse.results.length === 1 ? "<p>" + me.msg("empty.description.sample-site") + "</p>" : "")
               });
            }
            return true;
         };

         // Add animation to row delete
         this.widgets.dataTable._deleteTrEl = function MySites__deleteTrEl(row)
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
         var registerEventHandler = function MySites_onReady_registerEventHandler(cssClass, fnHandler)
         {
            var fnEventHandler = function MySites_onReady_fnEventHandler(layer, args)
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

         registerEventHandler(FAV_EVENTCLASS, this.onFavouriteSite);
         registerEventHandler(IMAP_EVENTCLASS, this.onImapFavouriteSite);
         registerEventHandler(DELETE_EVENTCLASS, this.onDeleteSite);

         // Enable row highlighting
         this.widgets.dataTable.subscribe("rowMouseoverEvent", this.widgets.dataTable.onEventHighlightRow);
         this.widgets.dataTable.subscribe("rowMouseoutEvent", this.widgets.dataTable.onEventUnhighlightRow);

         // Load sites & preferences
         this.initPreferences();
         this.loadSites();
      },

      /**
       * Date drop-down changed event handler
       *
       * @method onTypeFilterChanged
       * @param p_sType {string} The event
       * @param p_aArgs {array}
       */
      onTypeFilterChanged: function MySites_onTypeFilterChanged(p_sType, p_aArgs)
      {
         var menuItem = p_aArgs[1];
         if (menuItem)
         {
            this.widgets.type.set("label", menuItem.cfg.getProperty("text") + " " + Alfresco.constants.MENU_ARROW_SYMBOL);
            this.widgets.type.value = menuItem.value;

            // Save preferences
            this.services.preferences.set(this.PREFERENCES_SITES_DASHLET_FILTER, menuItem.value,
            {
               successCallback:
               {
                  fn: function() 
                  {
                     // Update local cached copy of current filter
                     this.filter = menuItem.value;

                     // Reload the sites list
                     this.loadSites();
                  },
                  scope: this
               }
            });
         }
      },

      /**
       * Init cached state from User Preferences
       *
       * @method initPreferences
       */
      initPreferences: function MySites_initPreferences()
      {
         var prefs = this.services.preferences.get();

         // Retrieve fav sites preferences
         var favSites = Alfresco.util.findValueByDotNotation(prefs, "org.alfresco.share.sites.favourites");
         if (favSites === null)
         {
            favSites = {};
         }
         this.favSites = favSites;
         var imapfavSites = Alfresco.util.findValueByDotNotation(prefs, "org.alfresco.share.sites.imapFavourites");
         if (imapfavSites === null)
         {
            imapfavSites = {};
         }
         this.imapfavSites = imapfavSites;

         var recentSites = Alfresco.util.findValueByDotNotation(prefs, "org.alfresco.share.sites.recent");
         if (recentSites === null)
         {
            recentSites = {};
         }

         var recentSitesArray = [];

         for (key in recentSites)
         {
            recentSitesArray.push(recentSites[key]);
         }

         this.recentSites = recentSites;
         this.recentSitesArray = recentSitesArray;



         // Retrieve the preferred filter for the UI
         var filter = Alfresco.util.findValueByDotNotation(prefs, this.PREFERENCES_SITES_DASHLET_FILTER, "all");
         this.filter = this.options.validFilters.hasOwnProperty(filter) ? filter : "all";
      },

      /**
       * Load sites list
       *
       * @method loadSites
       */
      loadSites: function MySites_loadSites()
      {
         var filter;
         switch (this.filter)
         {
            case "favSites":
               filter = "/favourites";
               break;
            case "recentSites":
               filter = "/recent";
               break;
            default:
               filter = "";
         }
         // Load sites
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "api/people/" + encodeURIComponent(Alfresco.constants.USERNAME) + "/sites" + filter + "?roles=user&size=" + this.options.listSize,
            successCallback:
            {
               fn: this.onSitesLoaded,
               scope: this
            }
         });
      },

      /**
       * Process user preferences after sites data has loaded and display the sites list
       *
       * @method onSitesLoaded
       * @param p_response {object} Response from "api/people/{userId}/sites" query
       */
      onSitesLoaded: function MySites_onSitesLoaded(p_response)
      {
         var p_items = p_response.json,
             siteManagers, i, j, k, l, ii = 0;

         // Select the preferred filter in the ui
         this.widgets.type.set("label", this.msg("filter." + this.filter) + " " + Alfresco.constants.MENU_ARROW_SYMBOL);
         this.widgets.type.value = this.filter;

         // Display the toolbar now that we have selected the filter
         Dom.removeClass(Selector.query(".toolbar div", this.id, true), "hidden");

         for (i = 0, j = p_items.length; i < j; i++)
         {
            p_items[i].isSiteManager = p_items[i].siteRole === "SiteManager";
            p_items[i].isFavourite = !this.favSites[p_items[i].shortName] ? false : this.favSites[p_items[i].shortName];
            p_items[i].isRecent = Alfresco.util.arrayContains(this.recentSitesArray, p_items[i].shortName);
            if (this.imapfavSites)
            {
               p_items[i].isIMAPFavourite = !this.imapfavSites[p_items[i].shortName] ? false : this.imapfavSites[p_items[i].shortName];
            }
         }

         this.sites = [];
         for (i = 0, j = p_items.length; i < j; i++)
         {
            var site = YAHOO.lang.merge({}, p_items[i]);

            if (this.filterAccept(this.widgets.type.value, site))
            {
               this.sites[ii] = site;
               ii++;
            }
         }

         this.sites.sort(function(a, b)
         {
            var name1 = a.title ? a.title.toLowerCase() : a.shortName.toLowerCase(),
                name2 = b.title ? b.title.toLowerCase() : b.shortName.toLowerCase();
            return (name1 > name2) ? 1 : (name1 < name2) ? -1 : 0;
         });

         var successHandler = function MySites_onSitesUpdate_success(sRequest, oResponse, oPayload)
         {
            oResponse.results=this.sites;
            this.widgets.dataTable.set("MSG_EMPTY", "");
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
         };

         this.widgets.dataSource.sendRequest(this.sites,
         {
            success: successHandler,
            scope: this
         });
      },

      /**
       * Determine whether a given site should be displayed or not depending on the current filter selection
       * @method filterAccept
       * @param filter {string} Filter to set
       * @param site {object} Site object literal
       * @return {boolean}
       */
      filterAccept: function MySites_filterAccept(filter, site)
      {
         switch (filter)
         {
            case "all":
               return true;

            case "favSites":
               return (site.isFavourite || (this.options.imapEnabled && site.isIMAPFavourite));

            case "recentSites":
               return (site.isRecent);
         }
         return false;
      },

      /**
       * Generate "Favourite" UI
       *
       * @method generateFavourite
       * @param record {object} DataTable record
       * @return {string} HTML mark-up for Favourite UI
       */
      generateFavourite: function MySites_generateFavourite(record)
      {
         var html = "";

         if (record.getData("isFavourite"))
         {
            html = '<a class="favourite-action ' + FAV_EVENTCLASS + ' enabled" title="' + this.msg("favourite.site.remove.tip") + '" tabindex="0"></a>';
         }
         else
         {
            html = '<a class="favourite-action ' + FAV_EVENTCLASS + '" title="' + this.msg("favourite.site.add.tip") + '" tabindex="0">' + this.msg("favourite.site.add.label") + '</a>';
         }

         return html;
      },

      /**
       * Generate "IMAP Favourite" UI
       *
       * @method generateIMAPFavourite
       * @param record {object} DataTable record
       * @return {string} HTML mark-up for Favourite UI
       */
      generateIMAPFavourite: function MySites_generateIMAPFavourite(record)
      {
         var html = "";

         if (record.getData("isIMAPFavourite"))
         {
            html = '<a class="favourite-action favourite-imap ' + IMAP_EVENTCLASS + ' enabled" title="' + this.msg("favourite.imap-site.remove.tip") + '" tabindex="0"></a>';
         }
         else
         {
            html = '<a class="favourite-imap ' + IMAP_EVENTCLASS + '" title="' + this.msg("favourite.imap-site.add.tip") + '" tabindex="0">' + this.msg("favourite.imap-site.add.label") + '</a>';
         }

         return html;
      },

      /**
       * Icon custom datacell formatter
       *
       * @method renderCellIcon
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderCellIcon: function MySites_renderCellIcon(elCell, oRecord, oColumn, oData)
      {
         Dom.setStyle(elCell, "width", oColumn.width + "px");
         Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
         
         var site = oRecord.getData(),
            img = site.isInfo ? "help-site-bw-32.png" : "filetypes/generic-site-32.png";

         elCell.innerHTML = '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/' + img + '" />';
      },

      /**
       * Name & description custom datacell formatter
       *
       * @method renderCellDetail
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderCellDetail: function MySites_renderCellDetail(elCell, oRecord, oColumn, oData)
      {
         var site = oRecord.getData(),
            description = '<span class="faded">' + this.msg("details.description.none") + '</span>',
            desc = "";

         if (site.isInfo)
         {
            desc += '<div class="empty"><h3>' + site.title + '</h3>';
            desc += '<span>' + site.description + '</span></div>';
         }
         else
         {
            // Description non-blank?
            if (site.description && site.description !== "")
            {
               description = $links($html(site.description));
            }

            desc += '<h3 class="site-title"><a href="' + Alfresco.constants.URL_PAGECONTEXT + 'site/' + site.shortName + '/dashboard" class="theme-color-1">' + $html(site.title) + '</a></h3>';
            desc += '<div class="detail"><span>' + description + '</span></div>';

            /* Favourite / IMAP */
            desc += '<div class="detail detail-social">';
            desc +=    '<span class="item item-social">' + this.generateFavourite(oRecord) + '</span>';
            if (this.options.imapEnabled)
            {
               desc +=    '<span class="item item-social item-separator">' + this.generateIMAPFavourite(oRecord) + '</span>';
            }
            desc += '</div>';
         }

         elCell.innerHTML = desc;
      },

      /**
       * Actions custom datacell formatter
       *
       * @method renderCellActions
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderCellActions: function MySites_renderCellActions(elCell, oRecord, oColumn, oData)
      {
         Dom.setStyle(elCell, "width", oColumn.width + "px");
         Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

         var desc = "";

         if (oRecord.getData("isSiteManager"))
         {
            desc += '<a class="delete-site ' + DELETE_EVENTCLASS + '" title="' + this.msg("link.deleteSite") + '">&nbsp;</a>';
         }
         elCell.innerHTML = desc;
      },

      /**
       * Adds an event handler for bringing up the delete site dialog for the specific site
       *
       * @method onDeleteSite
       * @param row {object} DataTable row representing site to be actioned
       */
      onDeleteSite: function MySites_onDeleteSite(row)
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
      onSiteDeleted: function MySites_onSiteDeleted(layer, args)
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
       * @param row {object} DataTable row representing site to be actioned
       */
      onFavouriteSite: function MySites_onFavouriteSite(row)
      {
         var record = this.widgets.dataTable.getRecord(row),
            site = record.getData(),
            siteId = site.shortName;

         site.isFavourite = !site.isFavourite;

         this.widgets.dataTable.updateRow(record, site);
         var fnPref = site.isFavourite ? "favouriteSite" : "unFavouriteSite";
         
         // Assume the call will succeed, but register a failure handler to replace the UI state on failure
         var responseConfig =
         {
            failureCallback:
            {
               fn: function MySites_onFavouriteSite_failure(event, obj)
               {
                  // Reset the flag to it's previous state
                  var record = obj.record,
                     site = record.getData();

                  site.isFavourite = !site.isFavourite;
                  this.widgets.dataTable.updateRow(record, site);
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     text: this.msg("message.save.failure")
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
               fn: function MySites_onFavouriteSite_success(event, obj)
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

         this.services.preferences[fnPref].call(this.services.preferences, siteId, responseConfig);

         // Update cached local copy
         this.favSites[siteId] = site.isFavourite;
      },

      /**
       * Adds an event handler that adds or removes the site as favourite site
       *
       * @method onImapFavouriteSite
       * @param row {object} DataTable row representing site to be actioned
       */
      onImapFavouriteSite: function MySites_onImapFavouriteSite(row)
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
               fn: function MySites_onImapFavouriteSite_failure(event, obj)
               {
                  // Reset the flag to it's previous state
                  var record = obj.record,
                     site = record.getData();

                  site.isIMAPFavourite = !site.isIMAPFavourite;
                  this.widgets.dataTable.updateRow(record, site);
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     text: this.msg("message.save.failure")
                  });
               },
               scope: this,
               obj:
               {
                  record: record
               }
            }
         };

         // Update user preferences
         this.services.preferences.set(Alfresco.service.Preferences.IMAP_FAVOURITE_SITES + "." + siteId, site.isIMAPFavourite, responseConfig);

         // Update cached local copy
         this.imapfavSites[siteId] = site.isIMAPFavourite;
      },

      /**
       * Fired by YUI Link when the "Create site" label is clicked
       * @method onCreateSite
       * @param event {domEvent} DOM event
       */
      onCreateSite: function MySites_onCreateSite(event)
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
      _findRecordByParameter: function MySites__findRecordByParameter(p_value, p_parameter)
      {
        var recordSet = this.widgets.dataTable.getRecordSet();
        for (var i = 0, j = recordSet.getLength(); i < j; i++)
        {
           if (recordSet.getRecord(i).getData(p_parameter) === p_value)
           {
              return recordSet.getRecord(i);
           }
        }
        return null;
      }
   });
})();