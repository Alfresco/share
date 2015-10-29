<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/calendar/enabledViews.js">

var CalendarScriptHelper = (function()
{
   var now = new Date();
   /* Number of ms in a day & hour */
   var HOUR = 60 * 60 * 1000;
   var DAY = 24 * HOUR;
   /* Days in a week */
   var DAYS_IN_WEEK = 7;
   
   //add n days to given date
   var addDays = function(d, nDays)
   {
      d.setDate(d.getDate() + nDays);
      return d;
   };
   //returns number of days in month
   var daysInMonth = function(iMonth, iYear)
   {
      return 32 - new Date(iYear, iMonth, 32).getDate();
   };
   
   var zeroPad = function(value)
   {
      return (value < 10) ? '0' + value : value;
   };
   // {
   // "name" : "1225808148178-998.ics",
   // "title" : "lunch",
   // "where" : "somewhere",
   // "when" : "04 Nov 2008",
   //   "url" : "page/site/testSite/calendar?date=2008-11-04",
   // "start" : "12:00",
   // "end" : "13:00",
   // "site" : "testSite"
   // }
   var convertToIcalFormat = function(event, eventDate, endDate)
   {
      var convertedEvent = {};
      convertedEvent.location = event.where;
      convertedEvent.dtstart = toISOString(eventDate).split('+')[0];
      convertedEvent.dtstartText = zeroPad(eventDate.getHours()) + ':' + zeroPad(eventDate.getMinutes());
      convertedEvent.dtend = toISOString(endDate).split('+')[0];
      convertedEvent.dtendText = zeroPad(endDate.getHours()) + ':' + zeroPad(endDate.getMinutes());
      convertedEvent.summary = event.title;
      // convertedEvent.url = event.url;
      convertedEvent.description = event.description || event.title;
      convertedEvent.location = event.where;
      convertedEvent.name = event.name;
      convertedEvent.tags = event.tags;
      convertedEvent.duration = event.duration;
      
      if (event.startAt.iso8601 === event.endAt.iso8601)
      {
         convertedEvent.allday = true;
      }
      else 
      {
         convertedEvent.allday = false;
      }
      
      
      return convertedEvent;
   };
   
   /**
    * Converts a JavaScript native Date object into a ISO8601-formatted string
    *
    * Original code:
    *    dojo.date.stamp.toISOString
    *    Copyright (c) 2005-2008, The Dojo Foundation
    *    All rights reserved.
    *    BSD license (http://trac.dojotoolkit.org/browser/dojo/trunk/LICENSE)
    *
    * @method toISO8601
    * @param dateObject {Date} JavaScript Date object
    * @param options {object} Optional conversion options
    *    zulu = true|false
    *    selector = "time|date"
    *    milliseconds = true|false
    * @return {string}
    * @static
    */
   var toISOString = function()
   {
      //	summary:
      //		Format a Date object as a string according a subset of the ISO-8601 standard
      //
      //	description:
      //		When options.selector is omitted, output follows [RFC3339](http://www.ietf.org/rfc/rfc3339.txt)
      //		The local time zone is included as an offset from GMT, except when selector=='time' (time without a date)
      //		Does not check bounds.  Only years between 100 and 9999 are supported.
      //
      //	dateObject:
      //		A Date object
      var _ = function(n)
      {
         return (n < 10) ? "0" + n : n;
      };
      
      return function(dateObject, options)
      {
         options = options || {};
         var formattedDate = [];
         var getter = options.zulu ? "getUTC" : "get";
         var date = "";
         if (options.selector != "time") 
         {
            var year = dateObject[getter + "FullYear"]();
            date = ["0000".substr((year + "").length) + year, _(dateObject[getter + "Month"]() + 1), _(dateObject[getter + "Date"]())].join('-');
         }
         formattedDate.push(date);
         if (options.selector != "date") 
         {
            var time = [_(dateObject[getter + "Hours"]()), _(dateObject[getter + "Minutes"]()), _(dateObject[getter + "Seconds"]())].join(':');
            var millis = dateObject[getter + "Milliseconds"]();
            if (options.milliseconds) 
            {
               time += "." + (millis < 100 ? "0" : "") + _(millis);
            }
            if (options.zulu) 
            {
               time += "Z";
            }
            else if (options.selector != "time") 
            {
               var timezoneOffset = dateObject.getTimezoneOffset();
               var absOffset = Math.abs(timezoneOffset);
               time += (timezoneOffset > 0 ? "-" : "+") +
               _(Math.floor(absOffset / 60)) +
               ":" +
               _(absOffset % 60);
            }
            formattedDate.push(time);
         }
         return formattedDate.join('T'); // String
      };
   }();
   /**
    * Converts an ISO8601-formatted date into a JavaScript native Date object
    *
    * Original code:
    *    dojo.date.stamp.fromISOString
    *    Copyright (c) 2005-2008, The Dojo Foundation
    *    All rights reserved.
    *    BSD license (http://trac.dojotoolkit.org/browser/dojo/trunk/LICENSE)
    *
    * @method Alfresco.thirdparty.fromISO8601
    * @param formattedString {string} ISO8601-formatted date string
    * @return {Date|null}
    * @static
    */
   var fromISOString = function()
   {
      //	summary:
      //		Returns a Date object given a string formatted according to a subset of the ISO-8601 standard.
      //
      //	description:
      //		Accepts a string formatted according to a profile of ISO8601 as defined by
      //		[RFC3339](http://www.ietf.org/rfc/rfc3339.txt), except that partial input is allowed.
      //		Can also process dates as specified [by the W3C](http://www.w3.org/TR/NOTE-datetime)
      //		The following combinations are valid:
      //
      //			* dates only
      //			|	* yyyy
      //			|	* yyyy-MM
      //			|	* yyyy-MM-dd
      // 			* times only, with an optional time zone appended
      //			|	* THH:mm
      //			|	* THH:mm:ss
      //			|	* THH:mm:ss.SSS
      // 			* and "datetimes" which could be any combination of the above
      //
      //		timezones may be specified as Z (for UTC) or +/- followed by a time expression HH:mm
      //		Assumes the local time zone if not specified.  Does not validate.  Improperly formatted
      //		input may return null.  Arguments which are out of bounds will be handled
      //		by the Date constructor (e.g. January 32nd typically gets resolved to February 1st)
      //		Only years between 100 and 9999 are supported.
      //
      //	formattedString:
      //		A string such as 2005-06-30T08:05:00-07:00 or 2005-06-30 or T08:05:00
      
      var isoRegExp = /^(?:(\d{4})(?:-(\d{2})(?:-(\d{2}))?)?)?(?:T(\d{2}):(\d{2})(?::(\d{2})(.\d+)?)?((?:[+-](\d{2}):(\d{2}))|Z)?)?$/;
      
      return function(formattedString)
      {
         var match = isoRegExp.exec(formattedString);
         var result = null;
         
         if (match) 
         {
            match.shift();
            if (match[1]) 
            {
               match[1]--;
            } // Javascript Date months are 0-based
            if (match[6]) 
            {
               match[6] *= 1000;
            } // Javascript Date expects fractional seconds as milliseconds
            result = new Date(match[0] || 1970, match[1] || 0, match[2] || 1, match[3] || 0, match[4] || 0, match[5] || 0, match[6] || 0);
            
            var offset = 0;
            var zoneSign = match[7] && match[7].charAt(0);
            if (zoneSign != 'Z') 
            {
               offset = ((match[8] || 0) * 60) + (Number(match[9]) || 0);
               if (zoneSign != '-') 
               {
                  offset *= -1;
               }
            }
            if (zoneSign) 
            {
               offset -= result.getTimezoneOffset();
            }
            if (offset) 
            {
               result.setTime(result.getTime() + offset * 60000);
            }
         }
         
         return result; // Date or null
      };
   }();
   
   return {
      /**
       * Retrieves user events for specified date
       *
       * @param {Date} d  Optional. Date from which to retrieve user events. If not specified,
       * the date used is one specified in request or the current date.
       *
       * @returns  {Array} eventList Array of events
       */
      getUserEvents: function(d)
      {
         var d = d || this.getContextDate(this.getDefaultDate());
         var uri = "/calendar/events/" + encodeURIComponent(page.url.templateArgs.site) +
         "/user?from=" +
         encodeURIComponent(toISOString(d, {
            selector: 'date'
         }));
         var connector = remote.connect("alfresco");
         var result = connector.get(uri);
         if (result.status == status.STATUS_OK) 
         {
            var eventList = JSON.parse(result.response).events;
            return eventList;
         }
      },
      
      getDefaultDate: function()
      {
         var d = new Date();
         return d;
      },
      
      /**
       *  Gets the current date
       *
       * @returns {Date} The current date
       */
      getCurrentDate: function()
      {
         return now;
      },
      
		/**
		 * Gets the name of the requested view & checks that view is enabled
		 * 
		 * @return {String} view name ["day"|"week"|"month"|"agenda"]
		 */
		
      getView: function()
      {
         return context.properties.filteredView;
      },
      
		/**
		 * Returns the number of views supported by this calendar install
		 * 
		 * @return {int}
		 */
		
		countViews: function()
		{
		   var j = 0;
		   for (var key in model.enabledViews) {
		      model.enabledViews[key] ? j++ : null ;
		   }
		   return j;
      },
		
		/*
		 * Returns a list of views supported
		 * 
		 * @return {array}
		 */
		
		listViews: function()
		{
			var views = [];
			for (var key in model.enabledViews) {
             model.enabledViews[key] ? views.push(key) : null ;
         }
			return views;
		},
		
      /**
       * Gets the requested date for the request or the specified default date (for the current view) if not specified
       *
       * @param   {Date} Date to use as default if not date param specified
       * @returns {String} The contextual date for the request.
       */
      getContextDate: function(defaultDate)
      {
         return fromISOString(getPageUrlParam('date', defaultDate.getFullYear() + "-" + zeroPad(defaultDate.getMonth() + 1) + "-" + zeroPad(defaultDate.getDate())));
      },

      /**
      * Initialises data used to render the calendar view
      * @return {object}
      **/
      initialiseViews: function(d)
      {
         var viewArgs = {};
         var firstDayOfMonth = new Date(d.getTime() - ((d.getDate() - 1) * DAY));
         //number of days in month
         var num_daysInMonth = daysInMonth(d.getMonth(), d.getFullYear());
         
         var lastDayOfMonth = new Date(((firstDayOfMonth.getTime() + (DAY * num_daysInMonth))));
         
         
         viewArgs.viewEvents = [];
         viewArgs.startDate = toISOString(d, {
            selector: 'date'
         });
         viewArgs.titleDate = viewArgs.startDate;
         viewArgs.endDate = toISOString(new Date(d.getTime() + (DAY * 31)), {
            selector: 'date'
         });
         return viewArgs;
      },
      isUserPermittedToCreateEvents: function()
      {
         var json = remote.call("/api/sites/" + page.url.templateArgs.site + "/memberships/" + encodeURIComponent(user.name)),
            membership = JSON.parse(json);

         // If the user isn't a member of the site, the response won't have role.
         return (membership.role && membership.role !== "SiteConsumer")? true : false;
      },

      /**
      * Initialises the data required for the view
      *
      * return {object}
      **/
      initView: function()
      {
         var viewArgs = {};
         viewArgs.viewType = CalendarScriptHelper.getView();
         viewArgs.enabledViews = CalendarScriptHelper.listViews();
         viewArgs.permitToCreateEvents = this.isUserPermittedToCreateEvents();
         viewArgs.view = this.initialiseViews(this.getContextDate(this.getDefaultDate()));
         return viewArgs;
      },

      convertFromISOString: function(formattedDate)
      {
         return fromISOString(formattedDate);
      },
      convertToISOString: function(dateObject, options)
      {
         return toISOString(dateObject, options);
      }
   };
   
}());
