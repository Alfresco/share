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
 * Dashboard DocSummary component.
 *
 * @namespace Alfresco
 * @class Alfresco.dashlet.DocSummary
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
    * Dashboard DocSummary constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.dashlet.DocSummary} The new component instance
    * @constructor
    */
   Alfresco.dashlet.DocSummary = function DocSummary_constructor(htmlId)
   {
      return Alfresco.dashlet.DocSummary.superclass.constructor.call(this, htmlId);
   };

   YAHOO.extend(Alfresco.dashlet.DocSummary, Alfresco.component.SimpleDocList,
   {

      /**
       * Preferences
       */
      PREFERENCES_DOCSUMMARY_DASHLET: "",
      PREFERENCES_DOCSUMMARY_DASHLET_FILTER: "",
      PREFERENCES_DOCSUMMARY_DASHLET_VIEW: "",
      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function DocSummary_onReady()
      {
         this.PREFERENCES_DOCSUMMARY_DASHLET = this.services.preferences.getDashletId(this, "docsummary");
         this.PREFERENCES_DOCSUMMARY_DASHLET_FILTER = this.PREFERENCES_DOCSUMMARY_DASHLET + ".filter";
         this.PREFERENCES_DOCSUMMARY_DASHLET_VIEW = this.PREFERENCES_DOCSUMMARY_DASHLET + ".simpleView";

         // Create Dropdown filter
         this.widgets.filter = Alfresco.util.createYUIButton(this, "filters", this.onFilterChange,
         {
            type: "menu",
            menu: "filters-menu",
            lazyloadmenu: false
         });

         // Select the preferred filter in the ui
         var filter = this.options.validFilters[0].type;
         for (var i=0; i<this.options.validFilters.length; i++)
         {
            if (this.options.filter === this.options.validFilters[i].type)
            {
               filter = this.options.filter;
               break;
            }
         }
         this.widgets.filter.set("label", this.msg("filter." + filter) + " " + Alfresco.constants.MENU_ARROW_SYMBOL);
         this.widgets.filter.value = filter;

         // Detailed/Simple List button
         this.widgets.simpleDetailed = new YAHOO.widget.ButtonGroup(this.id + "-simpleDetailed");
         if (this.widgets.simpleDetailed !== null)
         {
            this.widgets.simpleDetailed.check(this.options.simpleView ? 0 : 1);
            this.widgets.simpleDetailed.on("checkedButtonChange", this.onSimpleDetailed, this.widgets.simpleDetailed, this);
         }

         // Display the toolbar now that we have selected the filter
         Dom.removeClass(Selector.query(".toolbar div", this.id, true), "hidden");

         // DataTable can now be rendered
         Alfresco.dashlet.DocSummary.superclass.onReady.apply(this, arguments);
      },

      /**
       * Calculate webscript parameters
       *
       * @method getParameters
       * @override
       */
      getParameters: function MyDocuments_getParameters()
      {
         return "filter=" + this.widgets.filter.value;
      },

      /**
       * Generate base webscript url.
       *
       * @method getWebscriptUrl
       * @override
       */
      getWebscriptUrl: function SimpleDocList_getWebscriptUrl()
      {
         return Alfresco.constants.PROXY_URI + "slingshot/doclib/doclist/documents/site/" + Alfresco.constants.SITE + "/documentLibrary?max=50";
      },

      /**
       * Filter Change menu handler
       *
       * @method onFilterChange
       * @param p_sType {string} The event
       * @param p_aArgs {array}
       */
      onFilterChange: function MyDocuments_onFilterChange(p_sType, p_aArgs)
      {
         var menuItem = p_aArgs[1];
         if (menuItem)
         {
            this.widgets.filter.set("label", menuItem.cfg.getProperty("text") + " " + Alfresco.constants.MENU_ARROW_SYMBOL);
            this.widgets.filter.value = menuItem.value;

            this.services.preferences.set(this.PREFERENCES_DOCSUMMARY_DASHLET_FILTER, this.widgets.filter.value);

            this.reloadDataTable();
         }
      },

      /**
       * Show/Hide detailed list buttongroup click handler
       *
       * @method onSimpleDetailed
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onSimpleDetailed: function DocSummary_onSimpleDetailed(e, p_obj)
      {
         this.options.simpleView = e.newValue.index === 0;
         this.services.preferences.set(this.PREFERENCES_DOCSUMMARY_DASHLET_VIEW, this.options.simpleView);
         if (e)
         {
            Event.preventDefault(e);
         }

         this.reloadDataTable();
      }
   });
})();
