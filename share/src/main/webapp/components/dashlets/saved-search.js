/**
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
 * Dashboard SavedSearch component.
 *
 * @namespace Alfresco.dashlet
 * @class Alfresco.dashlet.SavedSearch
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
    * Dashboard SavedSearch constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.dashlet.SavedSearch} The new component instance
    * @constructor
    */
   Alfresco.dashlet.SavedSearch = function SavedSearch_constructor(htmlId)
   {
      Alfresco.dashlet.SavedSearch.superclass.constructor.call(this, "Alfresco.dashlet.SavedSearch", htmlId, ["container", "datasource", "datatable"]);

      // Config dialog
      this.configDialog = null;

      return this;
   };

   /**
    * Extend Alfresco.component.SearchBase
    */
   YAHOO.extend(Alfresco.dashlet.SavedSearch, Alfresco.component.SearchBase,
   {
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function RF_onReady()
      {
         Dom.get(this.id + "-title").innerHTML = this.buildTitle(this.options.title);
         this.createDataTable(this.options.searchTerm, this.options.limit);
      },

      /**
       * Helper method called by "renderCellDescription" method
       *
       * @method buildNameWithHref
       * @param {string} href
       * @param {string} name
       * @return {string} The name (with href) for the found result
       */
      buildNameWithHref: function SavedSearch_buildNameWithHref(href, name)
      {
         return '<h3 class="itemname"> <a class="theme-color-1" href=' + href + '>' + name + '</a></h3>';
      },

      /**
       * Helper method called by "renderCellDescription" method
       *
       * @method buildDescription
       * @param {string} resultType
       * @param {string} siteShortName
       * @param {string} siteTitle
       * @return {string} The description for the found result
       */
      buildDescription: function SavedSearch_buildDescription(resultType, siteShortName, siteTitle)
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
       * Helper method for building the title of the dashlet
       *
       * @method getTitle
       * @param {title}
       * @return {string} The title of the dashlet
       */
      buildTitle: function SavedSearch_buildTitle(title)
      {
         var title = YAHOO.lang.trim(title);
         if (!title || title == null || title.length == 0)
         {
            title = this.msg("header.title");
         }
         return title;
      },

      /**
       * Builds the url for the data table
       *
       * @method buildUrl
       * @retrun {string} The url for the data table
       */
      buildUrl: function SavedSearch_buildUrl(searchTerm, limit)
      {
         var url = Alfresco.constants.PROXY_URI + "slingshot/search?term={term}&maxResults={maxResults}&rootNode={rootNode}";

         var siteId = this.options.siteId;
         if (siteId && siteId != null)
         {
            url += "&site=" + siteId;
         }

         return YAHOO.lang.substitute(url,
         {
            term: encodeURIComponent(YAHOO.lang.trim(searchTerm)),
            maxResults: YAHOO.lang.trim(limit),
            rootNode: encodeURIComponent(this.options.searchRootNode)
         });
      },

      /**
       * Creates the data table
       *
       * @method createDataTable
       * @param {string} searchTerm
       * @param {string} limit
       */
      createDataTable: function SavedSearch_createDataTable(searchTerm, limit)
      {
         this.widgets.alfrescoDataTable = new Alfresco.util.DataTable(
         {
            dataSource:
            {
               url: this.buildUrl(searchTerm, limit),
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
                  {key: "site", formatter: this.bind(this.renderCellThumbnail), width: 48},
                  {key: "path", formatter: this.bind(this.renderCellDescription)}
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
      renderCellThumbnail: function SavedSearch_renderCellThumbnail(elCell, oRecord, oColumn, oData)
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
      renderCellDescription: function SavedSearch_renderCellDescription(elCell, oRecord, oColumn, oData)
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
       * Called when the user clicks the config saved search link.
       * Will open a saved search config dialog
       *
       * @method onConfigSearchClick
       * @param e The click event
       */
      onConfigSearchClick: function SavedSearch_onConfigSearchClick(e)
      {
         Event.stopEvent(e);

         if (!this.configDialog)
         {
            this.configDialog = new Alfresco.module.SimpleDialog(this.id + "-configDialog").setOptions(
            {
               width: "50em",
               templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/search/config",
               onSuccess:
               {
                  fn: function SavedSearch_onConfigFeed_callback(response)
                  {
                     // Response
                     var dataObj = response.config.dataObj,
                     searchTerm = dataObj.searchTerm,
                     limit = dataObj.limit,
                     title = dataObj.title;

                     this.options.searchTerm = searchTerm;
                     this.options.limit = limit;
                     this.options.title = this.buildTitle(title);

                     Dom.get(this.id + "-title").innerHTML = this.options.title;
                     this.createDataTable(searchTerm, limit);
                  },
                  scope: this
               },
               doSetupFormsValidation:
               {
                  fn: function SavedSearch_doSetupForm_callback(form)
                  {
                     form.addValidation(this.configDialog.id + "-searchTerm", Alfresco.forms.validation.mandatory, null, "keyup");

                     Dom.get(this.configDialog.id + "-searchTerm").value = this.options.searchTerm;
                     Dom.get(this.configDialog.id + "-limit").value = this.options.limit;
                     Dom.get(this.configDialog.id + "-title").value = this.options.title;
                  },
                  scope: this
               }
            });
         }
         this.configDialog.setOptions(
         {
            actionUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/dashlet/config/" + this.options.componentId
         }).show();
      }
   });
})();