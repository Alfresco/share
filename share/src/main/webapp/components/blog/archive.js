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
 * BlogPostListArchive component.
 * 
 * @namespace Alfresco
 * @class Alfresco.BlogPostListArchive
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;

   /**
    * BlogPostListArchive constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.BlogPostListArchive} The new DoclistTags instance
    * @constructor
    */
   Alfresco.BlogPostListArchive = function(htmlId)
   {
      Alfresco.BlogPostListArchive.superclass.constructor.call(this, "Alfresco.BlogPostListArchive", htmlId);

      this.monthId =
      {
         id: 0,
         months: {}
      };
      
      // Decoupled event listeners
      YAHOO.Bubbling.on("archiveRefresh", this.onArchiveRefresh, this);
      
      return this;
   };
   
   YAHOO.extend(Alfresco.BlogPostListArchive, Alfresco.component.BaseFilter,
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
          * Current siteId.
          * 
          * @property siteId
          * @type string
          */
         siteId: "",

         /**
          * ContainerId representing root container
          *
          * @property containerId
          * @type string
          * @default "blog"
          */
         containerId: "blog"
      },

      /**
       * Object literal used to generate unique tag ids
       * 
       * @property tagId
       * @type object
       */
      monthId: null,

      /**
       * Fired by YUI when parent element is available for scripting.
       * Registers event handler on 'onTagRefresh' event. If a component wants to refresh
       * the tags component, they need to fire this event.
       *
       * @method onReady
       */   
      onReady: function BlogPostListArchive_onReady()
      {
         Alfresco.BlogPostListArchive.superclass.onReady.call(this);
         
         var me = this;
         
         YAHOO.Bubbling.addDefaultAction(this.uniqueEventKey, function(layer, args)
         {
            var link = args[1].target;
            if (link && !me.controlsDeactivated)
            {
               var liElem = Dom.getAncestorByTagName(link, 'li');
               var date = me._getMonthFromId(liElem.id);  
               YAHOO.Bubbling.fire("changeFilter",
               {
                  filterId: "bymonth",
                  filterOwner: me.name,
                  filterData:
                  {
                     year: date.getFullYear(),
                     month: date.getMonth()
                  }
               });
            }
            return true;
         }, true);
         
         // Kick-off tag population
         if (this.options.siteId && this.options.containerId)
         {
            YAHOO.Bubbling.fire("archiveRefresh");
         }
      },
      

      /**
       * BUBBLING LIBRARY EVENT HANDLERS
       * Disconnected event handlers for inter-component event notification
       */

      /**
       * Fired when the currently active filter has changed
       * @method onFilterChanged
       * @param layer {string} the event source
       * @param args {object} arguments object
       */
      onFilterChanged: function BlogPostListArchive_onFilterChanged(layer, args)
      {
         var obj = args[1];
         if ((obj !== null) && (obj.filterId !== null))
         {
            if (obj.filterOwner == this.name)
            {
               // Remove the old highlight, as it might no longer be correct
               if (this.selectedFilter !== null)
               {
                  Dom.removeClass(this.selectedFilter, "selected");
               }

               var date = new Date(obj.filterData.year, obj.filterData.month, 1);
               // Need to find the selectedFilter element, from the current filterId
               this.selectedFilter = Dom.get(this._generateIdFromMonth(date));
               
               // This component now owns the active filter
               Dom.addClass(this.selectedFilter, "selected");
            }
            else
            {
               // Currently filtering by something other than this component
               if (this.selectedFilter !== null)
               {
                  Dom.removeClass(this.selectedFilter, "selected");
                  this.selectedFilter = null;
               }
            }
         }
      },
      
      /**
       * Function that gets called when another component fires "tagRefresh"
       * Issues a request to retrieve the latest tag data.
       *
       * @method onArchiveRefresh
       * @param layer {string} the event source
       * @param args {object} arguments object
       */
      onArchiveRefresh: function BlogPostListArchive_onArchiveRefresh(layer, args)
      {
         var timestamp = new Date().getTime();
         var url = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/blog/site/{site}/{container}/postspermonth?d=" + timestamp,
         {
            site: this.options.siteId,
            container: this.options.containerId
         });
         
         Alfresco.util.Ajax.request(
         {
            method: Alfresco.util.Ajax.GET,
				url: url,
				successCallback:
				{
					fn: this.onArchiveRefresh_success,
					scope: this
				},
				failureCallback:
				{
					fn: this.onArchiveRefresh_success,
					scope: this
				}
			});
      },
      
      /**
       * Event handler for when the tag data loads successfully.
       *
       * @method onArchiveRefresh_success
       * @param response {object} Server response object
       */ 
      onArchiveRefresh_success: function BlogPostListArchive_onArchiveRefresh_successs(response)
      {
         if (response && response.json && !YAHOO.lang.isUndefined(response.json.items))
         {
            var html = "";
            var items = response.json.items;
            for (var i = 0, j = items.length; i < j; i++)
            {
               var date = new Date(items[i].year, items[i].month, 1);
               html += this._generateMonthMarkup(date);
            }
            
            var eMonths = Dom.get(this.id + "-archive");
            eMonths.innerHTML = html;
         }
      },
      

      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Generates the HTML for a month element.
       *
       * @method _generateMonthMarkup
       * @param date {date} the date to render
       */
      _generateMonthMarkup: function BlogPostListArchive__generateTagMarkup(date)
      {
         var html = '<li id="' + this._generateIdFromMonth(date) + '"><span class="nav-label">';
         html += '<a href="#" class="' + this.uniqueEventKey + ' filter-link">' + Alfresco.util.formatDate(date, Alfresco.util.message("date-format.monthYear")) + '</a>';
         html += '</span></li>';
         return html;
      },

      /**
       * Generate ID alias for month, suitable for DOM ID attribute
       *
       * @method _generateIdFromMonth
       * @param date {date} Date representing the month
       * @return {string} A unique DOM-safe ID for the month
       */
      _generateIdFromMonth: function BlogPostListArchive__generateMonthId(date)
      {
         return this.id + "-month-" + date.getTime();
      },
      
      /**
       * Returns the date object that got encoded into the id by _generateIdFromMonth.
       * @method _getMonthFromId
       * @param id an id encoded by _generateMonthId
       * @return a date object
       */
      _getMonthFromId: function BlogPostListArchive__getMonthFromId(id)
      {
         // get the date millis part
         var millis = parseInt(id.substring((this.id + "-month-").length), 10);
         return new Date(millis);
      }
   });
})();