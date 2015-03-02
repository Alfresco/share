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
 * CalendarAgendaView base component.
 * 
 * @namespace Alfresco
 * @class Alfresco.CalendarAgendaView
 */
(function()
{
   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Selector = YAHOO.util.Selector,
      fromISO8601 = Alfresco.util.fromISO8601,
      toISO8601 = Alfresco.util.toISO8601,
      formatDate = Alfresco.util.formatDate,
      DateMath = YAHOO.widget.DateMath;
   
YAHOO.lang.augmentObject(Alfresco.CalendarView.prototype, {

   /**
    * INIT
    */
   
   /**
    * Triggered after events have loaded - bind necessary Agenda specific events.
    */
   initAgendaEvents: function CalendarAgendaView_initAgendaEvents()
   {
      var navEls = Dom.getElementsByClassName("agendaNav");
      // Unhide the nav, now it is usable
      Dom.removeClass(navEls, "hidden");
      Event.addListener(navEls, "click", this.bind(this.onLoadEvents));
   },

   /**
    *  CELL RENDERERS
    */          

   /**
    * Called by the DataTable to render the 'start' cell, which contains the event time and icon..
    * 
    * @method renderCellStart
    * @param elCell {object}
    * @param oRecord {object}
    * @param oColumn {object}
    * @param oData {object|string}
    * 
    */
   renderCellStart : function CalendarAgendaView_renderCellStart(elCell, oRecord, oColumn, oData) 
   {
      var data = oRecord.getData(),
      html = "",
      start = formatDate(data.startAt.iso8601, this.msg("date-format.shortTime")),
      end = formatDate(data.endAt.iso8601, this.msg("date-format.shortTime"));
      
      // build up cell content
      if (data.isAllDay) 
      {
         html = this.msg("label.all-day")
      } else
      {
         var startDate = new Date(data.from.split('T')[0]),
         endDate = new Date(data.to.split('T')[0]),
         displayDate = new Date(data.renderDate);
         if (Alfresco.CalendarHelper.isSameDay(startDate, displayDate) && (startDate < endDate))
         {
            endDate.setHours(23,59,59,999);
            end = formatDate(endDate.toISOString(), this.msg("date-format.shortTime"));
         } else
         if (Alfresco.CalendarHelper.isSameDay(endDate, displayDate) && (startDate < endDate))
         {
            startDate.setHours(0,0,1,1);
            start = formatDate(startDate.toISOString(), this.msg("date-format.shortTime"));
         }
         html = start + " - " + end
      }
      // write to DOM
      elCell.innerHTML = html;
   },

   /**
    * Called by the DataTable to render the 'start' cell, which contains the event name (what) and link to more info/edit box.
    * 
    * @method renderCellName
    * @param elCell {object}
    * @param oRecord {object}
    * @param oColumn {object}
    * @param oData {object|string}
    * 
    */
   renderCellName : function CalendarAgendaView_renderCellName(elCell, oRecord, oColumn, oData) 
   {
      var data = oRecord.getData(),
      rel = this.getRel(data),
      html = "";
      
      // build up cell content
      html = '<a href="' + data.uri + '" rel="'+ rel + '" class="summary">' + data.name + '</a>';
      
      // write to DOM
      elCell.innerHTML = html;
   },

   /**
    * Called by the DataTable to render the 'description' cell, which contains the event description (notes).
    * 
    * @method renderCellDescription
    * @param elCell {object}
    * @param oRecord {object}
    * @param oColumn {object}
    * @param oData {object|string}
    * 
    */
   renderCellDescription : function CalendarAgendaView_renderCellDescription(elCell, oRecord, oColumn, oData) 
   {
      var data = oRecord.getData(),
         html = this.truncate(data); //run truncation
      
      // write to DOM
      elCell.innerHTML = html;
   },

   /**
    * Called by the DataTable to render the 'location' cell, which contains the event location (where) and icon.
    * 
    * @method renderCellLocation
    * @param elCell {object}
    * @param oRecord {object}
    * @param oColumn {object}
    * @param oData {object|string}
    * 
    */
   renderCellLocation : function CalendarAgendaView_renderCellLocation(elCell, oRecord, oColumn, oData) 
   {
      var data = oRecord.getData(),
      html = "";
      
      // build up cell content
      html = '<span class="agendaLocation">'+ data.where + '</span>' 
      if (data.where === "") 
      {
         Dom.addClass(elCell, "empty");
      }
      // write to DOM
      elCell.innerHTML = html;
   },

   /**
    * Called by the DataTable to render the 'actions' cell, which contains the action links.
    * 
    * @method renderCellActions
    * @param elCell {object}
    * @param oRecord {object}
    * @param oColumn {object}
    * @param oData {object|string}
    * 
    */
   renderCellActions : function CalendarAgendaView_renderCellActions(elCell, oRecord, oColumn, oData) 
   {
      var data = oRecord.getData(),
      html = "",
      actions = [],
      rel = this.getRel(data),
      template = '<a href="' + data.uri + '" class="{type}" title="{tooltip}" rel="' + rel + '"><span>{label}</span></a>',
      write = false,
      isEdit = false,
      isDelete = false,
      me = this;
      
      // build up cell content
      write = this.options.permitToCreateEvents;
      isEdit = data.permissions.isEdit;
      isDelete = data.permissions.isDelete;

      // NOTE: DOM order (Delete, Edit, Info) is reverse of display order (Info, Edit, Delete), due to right float.      
      if (write && !data.isoutlook) {
         // Delete
         if (isDelete) {
         actions.push(YAHOO.lang.substitute(template, 
         {
            type:"deleteAction",
            label: me.msg("agenda.action.delete.label"),
            tooltip: me.msg("agenda.action.delete.tooltip")
         }));
         }
         
         // Edit
         if (isEdit) {
         actions.push(YAHOO.lang.substitute(template, 
         {
            type:"editAction",
            label: me.msg("agenda.action.edit.label"),
            tooltip: me.msg("agenda.action.edit.tooltip")
         }));
      }
      }

      // Info
      actions.push(YAHOO.lang.substitute(template,  
         {
            type:"infoAction summary",
            label: me.msg("agenda.action.info.label"),
            tooltip: me.msg("agenda.action.info.tooltip")
         }));
      
      html = actions.join(" ");
      
      // write to DOM
      elCell.innerHTML = html;
   },
   
   /**
    * Render events to DOM
    *  
    *  Called by getEvents. Runs every time an event has been modified. 
    *  Delegates actual rendering to other functions. (e.g. renderDay & DataTable cell renderers)
    *  
    * @method addEvents
    * @param {Object} events - processed array of view relevant events from getEvents()
    */
   renderEvents : function CalendarAgendaView_renderEvents(events)
   {
      // using this.bind for inline function calls to ensure access to parent object.
      var numEvents = 0, // number of events for this view (dates already validated server side),
         grandParentEl = this.getCalendarContainer(), // this contains all the DataTables
         data = {}, // alias
         tag = this.options.tag || null, // which (if any) tag is selected?
         sortedEvents = {}, //temporary array used for comparing the new events with and existing events 
         modifiedDates = [], // this array contains updates days which will be used by the renderer.
         linkStart = '<a href="" class="addEvent">', // used in template below to wrap link text
         linkEnd = '</a>', // start/close tags seperated from template for i18n flexibility
         noEventsId = this.options.id + "-noEvent",
         noEventsNoEditTemplate ='<div id="' + noEventsId + '" class="noEvent">'+
                                '<p class="instructionTitle">{noevents}</p>'+
                                '</div>',
         noEventsEditTemplate = '<div id="' + noEventsId + '" class="noEvent">'+
                                '<p class="instructionTitle">{noevents}</p>'+
                                '<span>{link}</span>'+
                                '</div>';
                                
      // set up Data
      this.widgets.Data = this.widgets.Data || {}; // check object instantiation
      data = this.widgets.Data; // alias the data
      
      // check events arg was supplied (if it was, update memory), else read from memory
      if (events) 
      {
         // if it was passed in, filter it for multiday events
         events = this.filterMultiday(events);
         this.events = events;
      } else 
      {
         events = this.events;
      }

      // filter events for selected tag:
      events = this.tagFilter(events);

      numEvents = events.length;

      // Set View Title.
      this.updateTitle();
      
      // sort the events from the passed parameter into the data object according to date (in big endian ISO format YYYY-MM-DD)
      for (var i=0;i<numEvents;i++)
      {
         var event = events[i],
            date = event.displayFrom || event.from;
            
         date = date.split('T')[0];
         sortedEvents[date] = sortedEvents[date] || {events: []};
         sortedEvents[date].events.push(event);
      }

      // Check for days that are no longer needed.
      for (date in data) 
      {
         // if the date exists in the current object, but not the new one, remove it
         if(!sortedEvents[date]) 
         {
            // remove from DOM
            this.removeDay(date);
            // remove from Data
            delete data[date];
         }
      }

      if (numEvents > 0) 
      {
         // Remove the default text/noEvent text if it exists (both live in same div)
         var noEventsEl = Dom.get(noEventsId);
         if(noEventsEl) 
         {
            noEventsEl.parentNode.removeChild(noEventsEl);
         }
         
         // loop through each of the sorted events and write to Data store & render if new data has been supplied.
         for (date in sortedEvents) 
         {
            var render = false;
            
            // check to see if data exists (and whether it was rendered) and if it does (and has), only update it if it has changed.
            // Some dates have data but aren't rendered initially (e.g. multiday events that start before the view starts), but may 
            // need rendering this time. Rendering necessitates the dataTable object - hence the check.
            if (data[date] && data[date].dataTable) 
            {
               // Converting Objects to JSON strings to enable comparison
               if (YAHOO.lang.JSON.stringify(data[date].events) != YAHOO.lang.JSON.stringify(sortedEvents[date].events)) 
               {
                  render = true; // day exists, but event data modified
                  data[date].events = sortedEvents[date].events;
               }
            }
            else // day is new.
             {
               render = true;
               data[date] = sortedEvents[date]
            }
            
            // if it is new or has changed, update data and render.
            if (render) 
            {
               this.renderDay(date); // each day has a different data table to make it easier to render the day header & manage additional day/removing empty days.      
            }
         }
      } else 
      {
         // Display noEvent text
         var noEventsTemplate = (this.options.permitToCreateEvents)? noEventsEditTemplate : noEventsNoEditTemplate; // show different help text if the user can't create an event.
         
         grandParentEl.innerHTML = YAHOO.lang.substitute(noEventsTemplate, 
         {
            link: this.msg("agenda.add-events", linkStart, linkEnd),
            noevents: this.msg("agenda.no-events")
         });
      }
      
      // These two functions need to be called the first time this is run only.
      if (!this.eventsInitialised) 
      {
         this.initAgendaEvents();
         this.eventsInitialised = true;
      }
      
      // TODO: This should be triggered by the eventDataLoaded event.     
      // Has a DOM element been informing the user that data is being loaded?
      if (this.loadingLabelBuffer && this.loadingEl) 
      {
         this.loadingEl.innerHTML = this.loadingLabelBuffer;
         this.loadingEl = this.loadingLabelBuffer = null;
      }       
   },
   
   /**
    * Renders the DataTable and Title for each day.
    * 
    * @method renderDay
    * @param {String} date - the ISO Formatted (yyyy-mm-dd) string for the date to render
    */
   renderDay: function CalendarAgendaView_renderDay(date) 
   {
      var data = this.widgets.Data[date],
         schema = 
         [
            {key: "start", formatter:this.bind(this.renderCellStart)}, // both the start and end times.
            {key: "name", formatter:this.bind(this.renderCellName)},
            {key: "description", formatter:this.bind(this.renderCellDescription)},
            {key: "where", formatter:this.bind(this.renderCellLocation)},
            {key: "actions", formatter:this.bind(this.renderCellActions)}
         ],
      grandParentEl = this.getCalendarContainer();

      // Check we have the data to work with, and that the date to be rendered is valid for the view.
      // The events have already been filtered to ensure that a portion of them is valid for the current view, 
      // but some multiday events may extend (or begin) out side of the view parameters, so this filtering needs to occur again.
      if (!data || !this.isValidDateForView(fromISO8601(date))) 
      {
         return false;
      }

      for (var i=0; i<data.events.length; i++)
      {
         data.events[i].renderDate = date;
      }

      // instantiate or update DataSource:
      data.dataSource = new YAHOO.util.LocalDataSource(data.events);

      // does day already have a DataTable?
      if (data.dataTable) 
      {
         // already exists, so remove and recreate
         // TODO - there must be a better way to update an existing data table than this?
         this.removeDay(date);         
      } 
      
      var parentEl = document.createElement('div'), // the container for each day. 
         titleEl = document.createElement('h2'), // the day heading.
         kids = Dom.getChildren(grandParentEl), // all the elements containing DataTables
         dateTime = fromISO8601(date).getTime(), // makes date comparisons easier
         insertBeforeThisEl,
         today = Alfresco.util.toISO8601(new Date()).split("T")[0];
      parentEl.id = this.options.id + "-dt-" + date;
      titleEl.id = this.options.id + "-head-" + date;
      Dom.addClass(titleEl, "dayTitle");
      titleEl.innerHTML = Alfresco.util.relativeDate(fromISO8601(date), this.msg("date-format.dayDateMonth"), {limit: true});
      Dom.setAttribute(titleEl, "title", formatDate(fromISO8601(date), this.msg("date-format.fullDate")));
      
      // Add highlighting on today's element.
      if (date === today) 
      {
         Dom.addClass(parentEl, "theme-bg-color-2")
      }
      
      // Write elements to DOM
      // Magic insert location finding code.
      // returns an HTML element of the first heading after the date we're looking for. If blank, append to grandParent. else insert before match.
      for (i in kids) 
      {
         // the iso date is on the end of the element's id, compare this with current date
         if (fromISO8601(kids[i].id.slice(-10)).getTime() > dateTime) 
         {
            insertBeforeThisEl = kids[i];
            break;
         }
      }
      
      if (insertBeforeThisEl) 
      {
         Dom.insertBefore(titleEl, insertBeforeThisEl);
         Dom.insertBefore(parentEl, insertBeforeThisEl);
      }
      else // looks like this is the last date we've currently got, so stick it on the end.
      {
         grandParentEl.appendChild(titleEl);
         grandParentEl.appendChild(parentEl);
      }
         
      // instantiate the dataTable.
      data.dataTable = new YAHOO.widget.DataTable(parentEl, schema, data.dataSource, 
      {
         onEventHighlightRow: function(event, target){Dom.addClass(target, "yui-dt-highlight")}
      });
      
      // Note Event bindings occur automagically, so action links do not need to be bound to anything.
      
      //Add row hover effects.
      data.dataTable.subscribe("rowMouseoverEvent", data.dataTable.onEventHighlightRow); 
      data.dataTable.subscribe("rowMouseoutEvent", data.dataTable.onEventUnhighlightRow); 
   },

   /**
    * Removes All HTML elements associated with a given date
    * @param {string} date
    */
   removeDay: function CalendarAgendaView(date)
   {
      var elements = Selector.query("[id$="+date+"]");
      for (i in elements) {
         var rmEl = Dom.get(elements[i]);
         rmEl.parentNode.removeChild(rmEl);
      }
   },

   /**
    *  ACTION HANDLERS
    */
   
   /**
    * Triggered when the previous/next links are clicked.
    */
   onLoadEvents: function CalendarAgendaView_onLoadEvents(e) 
   {
      // prevent multiple load events happening simultaneously.
      if (!this.loadingEl) 
      {
         var step = 30, // number of days to add each time
            dayInMS = 24 * 60 * 60 * 1000; // milliseconds in one day

         // Let the user know we're loading new content
         var target = Event.getTarget(e);
         this.loadingLabelBuffer = target.innerHTML; // store for later so we can revert
         this.loadingEl = target;
         this.loadingEl.innerHTML = this.msg("message.loading");
         
         // Update the start or end date as appropriate
         if (YAHOO.util.Selector.test(this.loadingEl, 'a.previousEvents')) 
         {
            this.options.startDate = new Date(this.options.startDate.getTime() - (step * dayInMS));
         }
         else if (YAHOO.util.Selector.test(this.loadingEl, 'a.nextEvents')) 
         {
            this.options.endDate = new Date(this.options.endDate.getTime() + (step * dayInMS));
         }
         
         // get a fresh list of events from server, this calls the render functions on success
         this.getEvents();
      }      
      Event.preventDefault(e);
   },
   
   
   /**
    * UTIL METHODS
    */
   
   /**
    * Returns the root element for the calendar DataTables & titles.
    * 
    * @method getCalendarContainer
    * @return {HTML Element}
    */
   getCalendarContainer: function CalendarAgendaView_getCalendarContainer()
   {
      return Dom.get(this.options.id);
   },

   /**
    * Updates the Agenda title with the new date and tags (if any)
    *
    * @method updateTitle
    */
   updateTitle: function CalendarView_updateTitle()
      {

         var startDate = this.options.startDate,
            endDate = this.options.endDate,
            startDateString = "",
            withYear = this.msg("date-format.longDate"),
            noYear = this.msg("date-format.longDateNoYear"),
            endDateString = formatDate(endDate, withYear);

         // convert date objects to strings
         // only show year in start date if it differs to end date.
         if (startDate.getFullYear() === endDate.getFullYear())
         {
            startDateString = formatDate(startDate, noYear);
         } else
         {
            startDateString = formatDate(startDate, withYear)
         }

         this.titleEl.innerHTML = this.msg("title.agenda", startDateString, endDateString);

         // add tag info to title,
         tagTitleEl = Dom.getElementsByClassName('tagged', "span", this.titleEl);
         if (tagTitleEl.length > 1)
         {
            this.titleEl.removeChild(tagTitleEl[0]);
         }
         if (this.options.tag)
         {
            tagTitleEl = Alfresco.CalendarHelper.renderTemplate('taggedTitle',
            {
               taggedWith: this.msg('label.tagged-with'),
               tag: this.options.tag
            });
            this.titleEl.appendChild(tagTitleEl);
         }
      },

   /**
    * Truncates the text after a set number of characters and adds the show more link
    * 
    * Note: Breaks on previous word boundary and will not increase the visible string length 
    * - if the show more string added to the truncated text is greater than the original 
    * string, the original is used. 
    * 
    * @method truncate
    * @param {string} text - the text to truncate
    * @param {int} length - the number of characters to show before truncating.
    * 
    */
   truncate : function CalendarAgendaView_truncate(event, length)
   {
      var showMore = this.msg("agenda.truncate.show-more"),
         ellipsis = this.msg("agenda.truncate.ellipsis"),
         truncateTo = parseInt(length) || parseInt(this.options.truncateLength) || 100, // use default and ensure int.
         text = $html(event.description),
         result = text,
         resultReplace = "";
      
      // don't truncate unless we need to.
      // if we do truncate, we want to ensure that the overhead (showMore text and ellipsis) doesn't result in an actual
      // increase in the string's length.
      if (text.length > truncateTo + showMore.length + ellipsis.length)
      {
         result = text.substring(0,truncateTo);
         // truncate to previous one.
         resultReplace = result.replace(/\w+$/, '');
         // but ensure we don't remove the whole string if there are no word boundaries.
         result = (resultReplace.length > 0)? resultReplace : result ;
         
         // add in ellipsis and the html wrapped show more string
         result = '<span class="truncatedText">' + result + ellipsis + " " + '<a href="' + event.uri + '" rel="'+ this.getRel(event) +'" class="showMore">' + showMore + '</a>.'
      }
      return result;
   },
   
   expandDescription: function CalendarAgendaView_expandDescription(el) 
   {
      var event = this.getEventObj(el),
         containerEl = el.parentNode,
         text = $html(event.description),
         showLess = '<a href="' + event.uri + '" rel="' + el.rel + '" class="showLess">' + this.msg("agenda.truncate.show-less") + '</a>';
      
      containerEl.innerHTML = text + " " + showLess;
   },
   
   collapseDescription: function CalendarAgendaView_collapseDescription(el)
   {
      var event = this.getEventObj(el),
         containerEl = el.parentNode
      
      containerEl.innerHTML = this.truncate(event);
   }
}, true);
})();