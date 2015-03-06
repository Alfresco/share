/**
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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
 * Dashboard UserCalendar component.
 *
 * @namespace Alfresco.dashlet
 * @class Alfresco.dashlet.UserCalendar
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
   var $html = Alfresco.util.encodeHTML,
      formatDate = Alfresco.util.formatDate,
      fromISO8601 = Alfresco.util.fromISO8601,
      toISO8601 = Alfresco.util.toISO8601,
      dateMsg = Alfresco.util.message("date-format.longDate"),
      timeMsg = Alfresco.util.message("date-format.shortTime"),
      dateTimeMsg = dateMsg + " " + timeMsg;

   /**
    * Dashboard UserCalendar constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.dashlet.UserCalendar} The new component instance
    * @constructor
    */
   Alfresco.dashlet.UserCalendar = function UserCalendar_constructor(htmlId)
   {
      Alfresco.dashlet.UserCalendar.superclass.constructor.call(this, "Alfresco.dashlet.UserCalendar", htmlId, ["container", "datasource", "datatable"]);
      return this;
   };

   YAHOO.extend(Alfresco.dashlet.UserCalendar, Alfresco.component.Base,
   {
      events: null,
      
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * Result list size maximum
          *
          * @property listSize
          * @type integer
          * @default 100
          */
         listSize: 100,

         /**
          * Maximum number of days in the future for events to be shown
          *
          * @property maxDays
          * @type integer
          * @default 60
          */
         maxDays: 60
      },

      /**
       * Load the list of events to process
       * 
       * @method loadSites
       */
      loadEvents: function()
      {
         var showUntil = new Date();
         showUntil.setDate(showUntil.getDate() + this.options.maxDays);
         showUntil = toISO8601(showUntil).split("T")[0];

         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "calendar/events/user?from=now&to=" + showUntil + "&size=" + this.options.listSize,
            successCallback:
            {
               fn: this.onEventsLoaded,
               scope: this
            }
         });
      },

      /**
       * Events loaded handler
       *
       * @method onEventsLoaded
       * @param response {object} Response from events query
       */
      onEventsLoaded: function UserCalendar_onEventsLoaded(response)
      {
         this.events = response.json.events;
         
         var successHandler = function DT_success(sRequest, oResponse, oPayload)
         {
            oResponse.results = this.events;
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
         };
         
         this.widgets.dataSource.sendRequest(this.sites,
         {
            success: successHandler,
            scope: this
         });
      },

      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function UserCalendar_onReady()
      {
         // DataSource definition
         this.widgets.dataSource = new YAHOO.util.DataSource(this.events,
         {
            responseType: YAHOO.util.DataSource.TYPE_JSARRAY
         });
         
         // DataTable column defintions
         var columnDefinitions =
         [
            { key: "icon", label: "", sortable: false, formatter: this.bind(this.renderCellIcon), width: 32 },
            { key: "event", label: "", sortable: false, formatter: this.bind(this.renderCellEvent) }
         ];
         
         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-events", columnDefinitions, this.widgets.dataSource,
         {
            MSG_EMPTY: this.msg("label.noEvents")
         });
         
         // Load user calendar events
         this.loadEvents();
      },

      /**
       * Icon custom datacell formatter
       */
      renderCellIcon: function UserCalendar_renderCellIcon(elCell, oRecord, oColumn, oData)
      {
         Dom.setStyle(elCell, "width", oColumn.width + "px");
         var desc = '<div class="icon"><img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/calendar/images/calendar-16.png" alt="event"/></div>';
         elCell.innerHTML = desc;
      },

      /**
       * Event information custom datacell formatter
       */
      renderCellEvent: function UserCalendar_renderCellEvent(elCell, oRecord, oColumn, oData)
      {
         var startDate = fromISO8601(oRecord.getData("startAt").iso8601),
            endDate = fromISO8601(oRecord.getData("endAt").iso8601),
            isSameDay = (formatDate(startDate, dateMsg) === formatDate(endDate, dateMsg)) ? true : false,
            isSameTime = (formatDate(startDate, timeMsg) === formatDate(endDate, timeMsg)) ? true : false,
            isAllDay = (oRecord.getData("allday") === "true")? true : false,
            desc = '<div class="detail"><h4><a href="' + Alfresco.constants.URL_CONTEXT + oRecord.getData("url") + '" class="theme-color-1">' + $html(oRecord.getData("title")) + '</a></h4>';

         desc += '<div>';

         // Build up the display string depending on which elements are relevant.
         if (isSameDay)
         {
            // Starts/Ends on same day: only need to show start date
            desc += formatDate(startDate, dateMsg)
            if (!isAllDay)
            {
               // Timed Event: show the start time
               desc += " " + formatDate(startDate, timeMsg)
               // Single Day Timed
               if (!isSameTime)
               {
                  // End Time Differs: show the end time
                  desc += " - " + formatDate(endDate, timeMsg)
               }
            }
         }
         else
         {
            // Multiday event: both start and end dates need showing.
            if (isAllDay)
            {
               // All Day: no times needed.
               desc += formatDate(startDate, dateMsg) + ' - ' + formatDate(endDate, dateMsg);
            }
            else
            {
               // Multiday timed event: show date and time for start/end
               desc += formatDate(startDate, dateTimeMsg) + ' - ' + formatDate(endDate, dateTimeMsg);
            }
         }

         desc += '</div><div><a href="' + Alfresco.constants.URL_PAGECONTEXT + 'site/' + oRecord.getData("site") + '/dashboard" class="theme-link-1">' + $html(oRecord.getData("siteTitle")) + '</a></div></div>';
         
         elCell.innerHTML = desc;
      }
   });
})();