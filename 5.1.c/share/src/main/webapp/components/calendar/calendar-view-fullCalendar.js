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
 * CalendarFullCalendar base component.
 * 
 * @namespace Alfresco
 * @class Alfresco.CalendarView
 */
( function() 
{
   
   /**
    * Alfresco Slingshot aliases
    */
   // Uses jQuery and FullCalendar functions for FullCalendar functions and YUI ones for Alfresco data
   var fc = $.fullCalendar,
      formatDate = fc.formatDate,
      parseISO8601 = fc.parseISO8601,
      addDays = fc.addDays,
      applyAll = fc.applyAll,
      $html = Alfresco.util.encodeHTML,
      fromISO8601 = Alfresco.util.fromISO8601,
      toISO8601 = Alfresco.util.toISO8601,
      dateFormat = Alfresco.thirdparty.dateFormat,
      History = YAHOO.util.History,
      $jCalendar = ""; // Cache the jQuery selector. Populated in renderEvents after DOM has rendered.
      document.jquery = $;  // MNT-13070: IE replaces jQuery object with latest initialized
   
   YAHOO.lang.augmentObject(Alfresco.CalendarView.prototype,
   {

      /**
       * Converts Alfresco view type into FullCalendar view type
       *
       * Note: FullCalendar refers to views that include time slots as agenda views, Alfresco terminology for these views is "day" and "week"
       * The FullCalendar agenda views are not to be confused with the Alfresco agenda view which is an event overview page.
       *
       * @method getFullCalendarViewType
       * @param AlfrescoViewType {String} [day|week|month]
       */
      getFullCalendarViewType: function CalendarFullCalendar_getFullCalendarViewType(AlfrescoViewType)
      {
         switch(AlfrescoViewType){
            case Alfresco.CalendarView.VIEWTYPE_DAY:
               return this.options.fcOpts.dayView
            case Alfresco.CalendarView.VIEWTYPE_WEEK:
               return this.options.fcOpts.weekView
            case Alfresco.CalendarView.VIEWTYPE_MONTH:
               return this.options.fcOpts.monthView
         }
      },

      /**
       * Loads the jQuery Plugin Full Calendar, fetches the events and renders them.
       * Called by CalendarView_render()
       *
       * @method renderEvents
       */
      renderEvents: function CalendarFullCalendar_renderEvents()
      {
         // YUI History
         var bookmarkedView = History.getBookmarkedState("view") || this.options.view,
            bookmarkedDate = History.getBookmarkedState("date") || dateFormat(this.options.startDate, "yyyy-mm-dd");

         // Register History Manager callbacks
         History.register("view", bookmarkedView, function CalendarFullCalendar_onHistoryManagerViewNavigate(view)
         {
            this.onViewNav(view);
         }, {}, this);
         History.register("date", bookmarkedDate, function CalendarFullCalendar_onHistoryManagerDateNavigate(date)
         {
            this.onDateNav(date);
         }, {}, this);

         // Initialize the browser history management library
         try
         {
             History.initialize("yui-history-field", "yui-history-iframe");
         }
         catch(e)
         {
            /*
             * The only exception that gets thrown here is when the browser is
             * not supported (Opera, or not A-grade)
             */
            Alfresco.logger.error("Alfresco.CalendarView: Couldn't initialize HistoryManager.", e);
         }

         // Prevent unnecessary DOM lookups and cache the jQuery object for the calendar container.
         $jCalendar = document.jquery('#' + this.options.id);

         // invoke Full Calendar
         this.initFullCalendar();

         // Edit dialogue events
         YAHOO.Bubbling.on("eventEdited", this.onEventEdited, this);
         YAHOO.Bubbling.on("eventSaved", this.onEventSaved, this);
         YAHOO.Bubbling.on("eventDeleted", this.onEventDeleted, this);

         // Tag events
         YAHOO.Bubbling.on("tagSelected", this.onTagSelected, this);

         // Nav events.
         YAHOO.Bubbling.on("todayNav", function CalendarFullCalendar_onTodayNav()
         {
            $jCalendar.fullCalendar("today");
         }, this);

         YAHOO.Bubbling.on("nextNav", function CalendarFullCalendar_onNextNav()
         {
            $jCalendar.fullCalendar("next");
         }, this);

         YAHOO.Bubbling.on("prevNav", function CalendarFullCalendar_onPrevNav()
         {
            $jCalendar.fullCalendar("prev");
         }, this);

         YAHOO.Bubbling.on("toggleWorkHours", this.onToggleWorkHours, this);
         
         YAHOO.Bubbling.on("viewChanged", function CalendarFullCalendar_onViewChanged(e, args)
         {
            // gets the view changed to from the index of the button in the event object of the passed in parameters
            var view = Alfresco.util.ComponentManager.findFirst("Alfresco.CalendarToolbar").enabledViews[args[1].activeView];
            if (view === Alfresco.CalendarView.VIEWTYPE_AGENDA)
            {
               this.onViewChanged.apply(this, arguments);
            }
            else
            {
               History.navigate("view", view);
            }
         }, this);

         // Mini Calendar
         YAHOO.Bubbling.on("dateChanged", function CalendarFullCalendar_onDateChanged(e, args)
         {
            History.navigate("date", dateFormat(args[1].date, "yyyy-mm-dd"));
         }, this);

         // Override Resizer callback function (while keeping the old one):
         var oldResizerFn = Alfresco.widget.Resizer.prototype.onResizeNotification
         Alfresco.widget.Resizer.prototype.onResizeNotification = function CalendarFullCalendar_onResizeNotification()
         {
            oldResizerFn();
            $jCalendar.fullCalendar("render");
         }

      },

      /**
       * Invokes the jQuery FullCalendar initialisation and rendering
       *
       * @method initFullCalendar
       *
       */
      initFullCalendar: function CalendarFullCalendar_initFullCalendar()
      {
         var date = fromISO8601(History.getBookmarkedState("date")) || this.options.startDate,
            view = History.getBookmarkedState("view") || this.options.view;

         // jQuery resets context so 'this' becomes the HTML element passed in, but we need a handle to the CalendarFullCalendar component:
         var me = this;

         // Standard jQuery wrapper maintained for clarity
         $(document).ready(function()
         {
            if (me.options.permitToCreateEvents)
            {
               $jCalendar.addClass("calendar-editable");
            }
            // FullCalendar init, inc. inline function declarations.
            $jCalendar.fullCalendar(
            {
               // Take view & date from settings:
               defaultView: me.getFullCalendarViewType(view),
               date: date.getDate(),
               month: date.getMonth(),
               year: date.getFullYear(),

               // Config:
               weekMode: me.options.fcOpts.weekMode,
               weekends: me.options.fcOpts.weekends,
               allDaySlot: me.options.fcOpts.allDaySlot,
               firstDay: parseInt(Alfresco.util.message("calendar.widget_config.start_weekday")),
               firstHour: me.options.fcOpts.firstHour,
               minTime: (me.options.fcOpts.showWorkHours) ? me.options.fcOpts.minTimeWorkHours : me.options.fcOpts.minTimeToggle,
               maxTime: (me.options.fcOpts.showWorkHours) ? me.options.fcOpts.maxTimeWorkHours : me.options.fcOpts.maxTimeToggle,
               aspectRatio: me.options.fcOpts.aspectRatio,
               slotMinutes: me.options.fcOpts.slotMinutes,
               disableDragging: me.options.fcOpts.disableDragging,
               disableResizing: me.options.fcOpts.disableResizing,

               // This is treated as a maximum height for day and week views - if it's set too small, you'll get an internal scrollbar.
               height: 2000,

               // Why would you want to ignoreTimezones?
               ignoreTimezone: false,

               // Internationalisation:
               monthNames: me.msg("months.long").split(","),
               monthNamesShort: me.msg("months.short").split(","),
               dayNames: me.msg("days.long").split(","),
               dayNamesShort: me.msg("days.medium").split(","),
               buttonText:
               {
                  month: me.msg("label.month"),
                  week: me.msg("label.week"),
                  day: me.msg("label.day")
               },
               allDayText: me.msg("label.all-day"),
               timeFormat:
               {
                  month: me.msg("fullCalendar.timeFormat.month"),
                  week: me.msg("fullCalendar.timeFormat.week"),
                  day: me.msg("fullCalendar.timeFormat.day")
               },
               columnFormat:
               {
                  month: me.msg("fullCalendar.columnFormat.month"),
                  week: me.msg("fullCalendar.columnFormat.week"),
                  day: me.msg("fullCalendar.columnFormat.day")
               },
               titleFormat:
               {
                  month: me.msg("fullCalendar.titleFormat.month"),
                  week: me.msg("fullCalendar.titleFormat.week"),
                  day: me.msg("fullCalendar.titleFormat.day")
               },
               axisFormat: me.msg("fullCalendar.axisFormat"),

               // Remove header navigation and just show the title. Nav is handled by toolbar.js to remain consistent across views.
               header:
               {
                  left: 'title',
                  center: '',
                  right: ''
               },

               // Has the user got permissions to create events?
               editable: me.options.permitToCreateEvents,

               // Define the event source as the Alfresco Calendar Event API
               eventSources:
               [
                  {
                     url: Alfresco.constants.PROXY_URI + "calendar/events/" + me.options.siteId + "/user?repeating=all",
                     startParam: "from",
                     startParamFn: function(rangeStart)
                     {
                        return toISO8601(rangeStart).split('T')[0];
                     },
                     endParam: "to",
                     endParamFn: function(rangeEnd)
                     {
                        return toISO8601(rangeEnd).split('T')[0];
                     },
                     success: function(data)
                     {
                        var parsedEvents = [];
                        if (data.events) {
                           var filteredEvents = me.tagFilter(data.events);

                           // trigger Mini Calendar's rendering:
                           YAHOO.Bubbling.fire("eventDataLoad", filteredEvents);

                           $.each(filteredEvents, function(i, event)
                           {
                              // Map Alfresco Event object to FullCalendar Event Object (ensuring that existing properties are still present)
                              // Parse user input strings for XSS
                              parsedEvents.push(YAHOO.lang.augmentObject(
                              {
                                 id: $html(event.name),
                                 start: parseISO8601(event.startAt.iso8601),
                                 end: parseISO8601(event.endAt.iso8601),
                                 allDay: (event.allday === "true") ? true : false,
                                 location: $html(event.where),
                                 uri: "/calendar/event/" + me.options.siteId + "/" + $html(event.name) + "?date=" + event.startAt.iso8601.split("T")[0],
                                 description: $html(event.description),
                                 title: event.title,
                                 where: $html(event.where),
                                 url: Alfresco.constants.URL_CONTEXT + event.url
                              }, event));
                           });
                        }

                        return parsedEvents;
                     },
                     error: function(data)
                     {
                        if (data.status == 404)
                        {
                           Alfresco.util.PopupManager.displayPrompt(
                           {
                              title: me.msg("calendar.site-does-not-exist-title", me.options.siteId),
                              text: me.msg("calendar.site-does-not-exist"),
                              buttons: [
                              {
                                 text: me.msg("button.ok"),
                                 handler: function ()
                                 {
                                    this.destroy();
                                 },
                                 isDefault: true
                              }]
                           });
                        }
                     }
                  }
               ],

               // Trigger the Event Info Dialogue
               eventClick: function CalendarFullCalendar_onEventClick(calEvent, jsEvent, view)
               {
                  me.showDialog(jsEvent, calEvent);
                  return false;
                },

               // Update the event following drag and drop.
               eventDrop: function CalendarFullCalendar_onEventDrop(event, dayDelta, minuteDelta, revertFunc, jsEvent, ui, view)
               {
                  me.updateEvent(event, dayDelta, minuteDelta, revertFunc, jsEvent, ui, view);
               },

               eventResize: function CalendarFullCalendar_onEventResize(event, dayDelta, minuteDelta, revertFunc, jsEvent, ui, view)
               {
                  me.updateEvent(event, dayDelta, minuteDelta, revertFunc, jsEvent, ui, view);
               },

               viewDisplay: function CalendarFullCalendar_onViewDisplay(view)
               {
                  // reset height for month view to ensure use of aspect ratio so we get square boxes.
                  if (view.name === me.options.fcOpts.monthView)
                  {
                     $jCalendar.fullCalendar("option", "height", null);
                  }
               },

               dayClick: function CalendarFullCalendar_onDayClick(date, allDay, jsEvent, view)
               {
                  // Only show add dialogue if user has create events permission.
                  if (me.options.permitToCreateEvents)
                  {
                     me.showAddDialog(date);
                  }
               },

               /**
                * Triggered by FullCalendar after an event has been updated (e.g. by drag and drop)
                *
                * @method afterEventChange
                * @param eventID - the unique ID of the event that has moved
                */
               afterEventChange: function CalendarFullCalendar_afterEventChange(eventID)
               {
                  // the filter returns an array for when multiple (e.g. repeated) events share an ID)
                  var events = $jCalendar.fullCalendar("clientEvents", eventID);

                  // keep the Alfresco properties in sync w/ the event object ones.
                  for (var i=0; i < events.length; i++)
                  {
                     var event = events[i],
                     startISO8601 = toISO8601(event.start),
                     endISO8601 = toISO8601(event.end) || startISO8601;
                     event.startAt =
                     {
                        iso8601: startISO8601
                     }
                     event.endAt =
                     {
                        iso8601: endISO8601
                     }
                  }
               }

            });

         });
      },

      /**
       * Retrieves the events and refreshes the calendar
       *
       * @method getEvents
       */
      getEvents: function CalendarFullCalendar_getEvents()
      {
         $jCalendar.fullCalendar("refetchEvents");

         this.refreshTags();
      },

      /**
       * Updates an event following a drag interaction
       *
       * @method updateEvent
       */
      updateEvent: function CalendarFullCalendar_updateEvent(event, dayDelta, minuteDelta, revertFunc, jsEvent, ui, view)
      {
         // Map FullCalendar event object back to an Alfresco Event object:
         // For now this is in the same format as the EventInfo form submits - except it uses the ISO8601 datetime strings to help with timezone support.
         var end = event.end || event.start, // event.end is null for all day events that only span a single day
            alfEvent =
            {
               desc: event.description,
               docfolder: event.docfolder || "",
               startAt:
               {
                  iso8601: toISO8601(event.start)
               },
               endAt:
               {
                  iso8601: toISO8601(end)
               },
               page: Alfresco.constants.PAGEID,
               site: Alfresco.constants.SITE,
               tags: (event.tags) ? event.tags.join() : [],
               what: event.title,
               where: event.where
            };
         // allday property needs to be missing for it to be false.
         if (event.allDay)
         {
            alfEvent.allday = "true";
         }

         Alfresco.util.Ajax.jsonPut(
         {
            url: Alfresco.constants.PROXY_URI + "calendar/event/" + Alfresco.constants.SITE + "/" + event.name,
            dataObj: alfEvent,
            failureCallback:
            {
               fn: revertFunc
            }
         });
      },

      /**
       * Triggered when the date gets changed
       * this is usually when the mini calendar is clicked on
       *
       * @method onDateNav
       * @param date {Date Object|ISO8601 compatible string}
       */
      onDateNav: function CalendarFullCalendar_onDateNav(date)
      {
         if (typeof(date) === "string")
         {
            date =  fromISO8601(date);
         }
         $jCalendar.fullCalendar("gotoDate", date);
      },

      /**
       * Triggered when the view changes
       * this is usually when the toolbar has been clicked on
       *
       * @method onViewNav
       * @param view {string} ["day"|"week"|"month"]
       */
      onViewNav: function CalendarFullCalendar_onViewNav(view)
      {
         $jCalendar.fullCalendar("changeView", this.getFullCalendarViewType(view));
      },

      /**
       *
       * Toggles the display of Work Hours or not
       *
       * @method onToggleWorkHours
       */
      onToggleWorkHours: function CalendarFullCalendar_onToggleWorkHours()
      {
         // Remove the Calendar
         $jCalendar.fullCalendar("destroy");
         // Reverse the state of the showWorkHours option.
         this.options.fcOpts.showWorkHours = (this.options.fcOpts.showWorkHours) ? false : true ;
         // Rerender the Calendar
         this.initFullCalendar();
      }
   },
   true);
})();
