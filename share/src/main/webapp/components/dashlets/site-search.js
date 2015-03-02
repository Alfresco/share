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
 * Dashboard SiteSearch component.
 *
 * @namespace Alfresco.dashlet
 * @class Alfresco.dashlet.SiteSearch
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
    * Dashboard SiteSearch constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.dashlet.SiteSearch} The new component instance
    * @constructor
    */
   Alfresco.dashlet.SiteSearch = function SiteSearch_constructor(htmlId)
   {
      Alfresco.dashlet.SiteSearch.superclass.constructor.call(this, "Alfresco.dashlet.SiteSearch", htmlId, ["container", "datasource", "datatable"]);

      // Services
      this.services.preferences = new Alfresco.service.Preferences();

      return this;
   };

   /**
    * Extend from Alfresco.component.SearchBase
    */
   YAHOO.extend(Alfresco.dashlet.SiteSearch, Alfresco.component.SearchBase,
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
          * Search root node
          *
          * @property searchRootNode
          * @type string
          * @default ""
          */
         searchRootNode: "",

         /**
          * Search term
          * 
          * @property searchTerm
          * @type string
          * @default ""
          */
         searchTerm: "",

         /**
          * Number of results to display in the search dashlet
          *
          * @property resultSize
          * @type string
          * @default "10"
          */
         resultSize: "10"
      },

      PREFERENCES_SITE_SEARCH_DASHLET_TERM: "",
      PREFERENCES_SITE_SEARCH_DASHLET_RESULTSIZE: "",

      /**
       * Fired by YUI when parent element is available for scripting
       *
       * @method onReady
       */
      onReady: function SiteSearch_onReady()
      {
         var me = this,
            id = this.id;

         // Preferences
         var PREFERENCES_SITE_SEARCH_DASHLET = this.services.preferences.getDashletId(this, "site.search");
         this.PREFERENCES_SITE_SEARCH_DASHLET_TERM = PREFERENCES_SITE_SEARCH_DASHLET + ".term";
         this.PREFERENCES_SITE_SEARCH_DASHLET_RESULTSIZE = PREFERENCES_SITE_SEARCH_DASHLET + ".resultSize";

         // Create result size menu
         this.widgets.resultSizeMenuButton = Alfresco.util.createYUIButton(this, "resultSize", this.onResultSizeSelected,
         {
            type: "menu",
            menu: "resultSize-menu",
            lazyloadmenu: false
         });

         // Load preferences
         var prefs = this.services.preferences.get();
         
         var term = Alfresco.util.findValueByDotNotation(prefs, this.PREFERENCES_SITE_SEARCH_DASHLET_TERM),
             resultSize = Alfresco.util.findValueByDotNotation(prefs, this.PREFERENCES_SITE_SEARCH_DASHLET_RESULTSIZE);

         if (term != null || resultSize != null)
         {
            this.options.searchTerm = Dom.get(this.id + "-search-text").value = (term ? term : "");
            this.options.resultSize = (resultSize ? resultSize : "10");
            this.doRequest();
         }

         var resultSize = this.getResultSize();
         this.widgets.resultSizeMenuButton.set("label", resultSize + " " + Alfresco.constants.MENU_ARROW_SYMBOL);
         this.widgets.resultSizeMenuButton.value = resultSize;

         // Display the toolbar now that we have selected the filter
         Dom.removeClass(Selector.query(".toolbar div", id, true), "hidden");

         // Enter key press
         var me = this;
         Dom.get(id + "-search-text").onkeypress = function(e)
         {
            if (e.keyCode == YAHOO.util.KeyListener.KEY.ENTER)
            {
               me.setSearchTerm(YAHOO.lang.trim(Dom.get(me.id + "-search-text").value));
               me.doRequest();
            }
         };

         // Search button click
         Dom.get(id + "-search-button").onclick = function(e)
         {
            me.setSearchTerm(YAHOO.lang.trim(Dom.get(me.id + "-search-text").value));
            me.doRequest();
         };
      },

      /**
       * Gets the search term
       *
       * @method getSearchTerm
       * @return {string} The trimmed and encoded search term
       */
      getSearchTerm: function SiteSearch_getSearchTerm()
      {
         return this.options.searchTerm;
      },

      /**
       * Sets the search term
       *
       * @method setSearchTerm
       * @param {string} searchTerm The search term to set
       */
      setSearchTerm: function SiteSearch_setSearchTerm(searchTerm)
      {
         if (this.options.searchTerm != searchTerm)
         {
            this.options.searchTerm = searchTerm;
            
            // Save preferences
            this.services.preferences.set(this.PREFERENCES_SITE_SEARCH_DASHLET_TERM, searchTerm);
         }
      },

      /**
       * Gets the result size
       *
       * @method getResultSize
       * @return {string} The number of results to display in the search dashlet
       */
      getResultSize: function SiteSearch_getResultSize()
      {
         return YAHOO.lang.trim(this.options.resultSize);
      },

      /**
       * Sets the result size
       *
       * @method setResultSize
       * @param {string} resultSize The result size to set
       */
      setResultSize: function SiteSearch_setResultSize(resultSize)
      {
         if (this.options.resultSize != resultSize)
         {
            this.options.resultSize = resultSize;
            
            // Save preferences
            this.services.preferences.set(this.PREFERENCES_SITE_SEARCH_DASHLET_RESULTSIZE, resultSize);
         }
      },

      /**
       * Gets the root node
       *
       * @method getRootNode
       * @return {string} The search root node
       */
      getRootNode: function SiteSearch_getRootNode()
      {
         return this.options.searchRootNode;
      },

      /**
       * Helper method called by "renderDescription" method
       *
       * @method buildNameWithHref
       * @param {string} href
       * @param {string} name
       * @return {string} The name (with href) for the found result
       */
      buildNameWithHref: function SiteSearch_buildNameWithHref(href, name)
      {
         return '<h3 class="itemname"> <a class="theme-color-1" href=' + href + '>' + name + '</a></h3>';
      },

      /**
       * Helper method called by "renderDescription" method
       *
       * @method buildDescription
       * @param {string} resultType
       * @param {string} siteShortName
       * @param {string} siteTitle
       * @return {string} The description for the found result
       */
      buildDescription: function SiteSearch_buildDescription(resultType, siteShortName, siteTitle)
      {
         var desc = '';

         var siteId = this.options.siteId;
         if (!(siteId && siteId != null))
         {
            desc = resultType + ' ' + this.msg("message.insite") + ' <a href="' + Alfresco.constants.URL_PAGECONTEXT + 'site/' + siteShortName + '/dashboard">' + $html(siteTitle) + '</a>';
         }

         return desc;
      },

      /**
       * Builds the url for the data table
       *
       * @method buildUrl
       * @retrun {string} The url for the data table
       */
      buildUrl: function SiteSearch_buildUrl()
      {
         var url = Alfresco.constants.PROXY_URI + "slingshot/search?term={term}&maxResults={maxResults}&rootNode={rootNode}";

         var siteId = this.options.siteId;
         if (siteId && siteId != null)
         {
            url += "&site=" + siteId;
         }

         return YAHOO.lang.substitute(url,
         {
            term: encodeURIComponent(this.getSearchTerm()),
            maxResults: this.getResultSize(),
            rootNode: encodeURIComponent(this.getRootNode())
         });
      },

      /**
       * Init the data table
       *
       * @method doRequest
       */
      doRequest: function SiteSearch_doRequest()
      {
         this.widgets.alfrescoDataTable = new Alfresco.util.DataTable(
         {
            dataSource:
            {
               url: this.buildUrl(),
               config:
               {
                  responseSchema:
                  {
                     resultsList: 'items'
                  }
               }
            },
            dataTable:
            {
               container: this.id + "-search-results",
               columnDefinitions:
               [
                  {key: "site", formatter: this.bind(this.renderThumbnail), width: 48},
                  {key: "path", formatter: this.bind(this.renderDescription)}
               ],
               config:
               {
                  MSG_EMPTY: this.msg("no.result")
               }
            }
         });
      },

      /**
       * Called by the DataTable to render the 'thumbnail' cell
       *
       * @method renderThumbnail
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderThumbnail: function SiteSearch_renderThumbnail(elCell, oRecord, oColumn, oData)
      {
         if (oRecord.getData("type") === "document")
         {
            Dom.addClass(elCell.parentNode, "thumbnail");
         }
         elCell.innerHTML = this.buildThumbnailHtml(oRecord, 48, 48);
      },

      /**
       * Called by the DataTable to render the 'description' cell
       *
       * @method renderThumbnail
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderDescription: function SiteSearch_renderDescription(elCell, oRecord, oColumn, oData)
      {
         var type = oRecord.getData("type"),
            name = oRecord.getData("name"),
            displayName = oRecord.getData("displayName"),
            site = oRecord.getData("site"),
            path = oRecord.getData("path"),
            nodeRef = oRecord.getData("nodeRef"),
            container = oRecord.getData("container"),
            modifiedOn = oRecord.getData("modifiedOn"),
            siteShortName = site.shortName,
            siteTitle = site.title,
            modified = Alfresco.util.formatDate(Alfresco.util.fromISO8601(modifiedOn)),
            resultType = this.buildTextForType(type),
            href = this.getBrowseUrl(name, type, site, path, nodeRef, container, modified);

         elCell.innerHTML = this.buildNameWithHref(href, displayName) + this.buildDescription(resultType, siteShortName, siteTitle) + this.buildPath(type, path, site);
      },

      /**
       * Changes the number of result to show in the search dashlet.
       * If a search has been done before a new search with the result number will be done.
       *
       * @param {string} p_sType The event
       * @param {array} p_aArgs Event arguments
       */
      onResultSizeSelected: function SiteSearch_onResultSizeSelected(p_sType, p_aArgs)
      {
         var menuItem = p_aArgs[1];

         if (menuItem)
         {
            this.widgets.resultSizeMenuButton.set("label", menuItem.cfg.getProperty("text") + " " + Alfresco.constants.MENU_ARROW_SYMBOL);
            this.widgets.resultSizeMenuButton.value = menuItem.value;

            this.setResultSize(menuItem.value);

            var dataTable = this.widgets.alfrescoDataTable;
            if (dataTable)
            {
               // change the inital value of "maxResults"
               var dataSource = dataTable.getDataTable().getDataSource(),
                  url = dataSource.liveData,
                  urlSplit = url.split("?"),
                  params = urlSplit[1].split("&");
               for (var i = 0; i < params.length; i++)
               {
                  if (params[i].split("=")[0] === "maxResults")
                  {
                     params[i] = "maxResults=" + menuItem.value;
                     url = urlSplit[0] + "?" + params.join("&");
                     break;
                  }
               }

               // change the inital url to the new one
               dataSource.liveData = url;

               // load data table
               dataTable.loadDataTable();
            }
         }
      }
   });
})();