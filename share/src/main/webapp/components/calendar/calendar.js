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

/*
 *** Alfresco.Calendar
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Element = YAHOO.util.Element;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;
   
   Alfresco.Calendar = function(htmlId)
   {
      this.name = "Alfresco.Calendar";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["calendar", "button", "treeview"], this.onComponentsLoaded, this);
      
      return this;
   }
   
   Alfresco.Calendar.prototype =
   {
      /**
       * AddEvent module instance.
       * 
       * @property eventDialog
       * @type Alfresco.module.AddEvent
       */
      eventDialog: null,

      /**
       * A reference to the YAHOO calendar component.
       * 
       * @property calendar
       * @type YAHOO.widget.Calendar
       */
      calendar: null,

      /**
       * Sets the current site for this component.
       * 
       * @property siteId
       * @type string
       */
      setSiteId: function(siteId)
      {
         this.siteId = siteId;
         return this;
      },
      
      /**
       * Set messages for this component
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       */
      setMessages: function(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },
      
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */   
      onComponentsLoaded: function()
       {
         Event.onContentReady(this.id, this.init, this, true);
      },
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Initialises components, including YUI widgets.
       *
       * @method init
       */
       init: function()
       {
         /* Add Event Button */
         Alfresco.util.createYUIButton(this, "thisMonth-button", this.onThisMonthClick);
      
          // Separate the (initial) rendering of the calendar from the data loading.
          // If for some reason the data fails to load, the calendar will still display.
         var navConfig = Alfresco.util.getCalendarControlConfiguration();
         this.calendar = new YAHOO.widget.Calendar("calendar", { navigator:navConfig });
         // Set localised properties
         Alfresco.util.calI18nParams(this.calendar);
         var calView = Alfresco.util.ComponentManager.findFirst('Alfresco.CalendarView');
         if (calView) 
         {
            var startDate = calView.options.startDate;
            this.calendar.setMonth(startDate.getMonth());
            this.calendar.setYear(startDate.getFullYear());
         }
         this.calendar.render();
         this.calendar.selectEvent.subscribe(this.onDateSelected, this, true);
         
         // Register for changes to the calendar data
         YAHOO.Bubbling.on("eventDataLoad", this.onEventDataLoad, this);
         YAHOO.Bubbling.on("eventSaved", this.onEventSaved, this);
      },
      
      /**
       * Calendar date selected event handler
       *
       * @method onDateSelected
       * @param p_type {string} Event type
       * @param p_args {array} Event arguments
       * @param p_obj {object} Object passed back from subscribe method
       */
      onDateSelected: function (p_type, p_args, p_obj)
      {
         var selected = p_args[0];
         var selDate = this.calendar.toDate(selected[0]);
         YAHOO.Bubbling.fire("dateChanged",
         {
            date: selDate
         })
      },
      
      /*
       * This method is called when the "eventSaved" event is fired; this
       * usually occurs when an event is successfully created. The calendar 
       * updates its view to hightlight the date of the event.
       *
       * @method onEventSaved
       * @param e {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onEventSaved: function(e, args)
      {
         var params = args[1];
         if (params && params.from)
         {
            var from = params.from;
            var selectedDates = this.calendar.getSelectedDates();
            
            dates.push(Alfresco.util.formatDate(Alfresco.thirdparty.fromISO8601(from), "mm/dd/yyyy"));

            this.calendar.cfg.setProperty("selected", dates.join(","));
            this.calendar.render();

            Alfresco.util.PopupManager.displayMessage(
            {
               text: this._msg("message.add.success", params.name)
            });
         }
      },
      
      /*
       * This method is called when the "eventDataLoad" event is fired; this
       * usually occurs when the page first loads. The calendar data is retrieved
       * and is used to update the view with the corresponding events.
       *
       * @method onEventDataLoad
       * @param e {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onEventDataLoad: function(e, args)
      {
         var events = args[1];
         if (events)
         {
            
             var selectedDates = [];

             for (var i=0;i<events.length;i++)
             {
               var event = events[i];
               if (event)
               {
                 var from = event.from || event.dtstart || event.when;
                 
                 selectedDates.push(Alfresco.util.formatDate(Alfresco.thirdparty.fromISO8601(from),"mm/dd/yyyy"));  
               }
             }
             // Get the data and refresh the view
             this.calendar.cfg.setProperty("selected", selectedDates.join(','));
             this.calendar.render();

         }
      },
   
      /**
       * Fired when the "This Month" button is clicked.
       *
       * @method  onThisMonthClick
       * @param e {object} DomEvent
       * @param obj {object} Object passed back from addListener method
       */
      onThisMonthClick: function(e, oValue)
      {
         var today = new Date();
         this.calendar.cfg.setProperty("pagedate", today.getMonth() + 1 + "/" + today.getFullYear());
         this.calendar.render();
         Event.preventDefault(e);
      },


      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function Calendar__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, this.name, Array.prototype.slice.call(arguments).slice(1));
      }
   };
})();
