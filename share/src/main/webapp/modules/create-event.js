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

// Ensure namespaces exist
Alfresco.module.event =  Alfresco.module.event || {}; 
Alfresco.module.event.validation = Alfresco.module.event.validation || {};

/*
 *** Alfresco.module.AddEvent
*/
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;
   
   /**
    * Internal date formats
    */
   var DATE_LONG = "dddd, d mmmm yyyy",
      DATE_SHORT = "yyyy/mm/dd";

   Alfresco.module.AddEvent = function(containerId)
   {
      this.name = "Alfresco.module.AddEvent";
      this.id = containerId;

      this.panel = null;

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "calendar", "container", "connection"], this.onComponentsLoaded, this);

      return this;
   };

   Alfresco.module.AddEvent.prototype =
   {
      /**
       * AddModule module instance.
       *
       * @property panel
       * @type Alfresco.module.AddEvent
       */
      panel: null,
            
      /**
        * Object container for initialization options
        *
        * @property options
        * @type object
        */
       options:
       {
        /**
         *  The current site's id
         *
         * @property siteId
         * @type String
         */
         siteId: "",

         /**
         * Stores the URI of the event IF an edit is happening
         *
         * @property eventURI
         * @type String
         */
         eventURI: null,

         /**
         * If the dialog is in "add" or "edit" mode
         *
         * @property eventURI
         * @type String
         */
         mode: "add",

         /**
         * Default date to use instead of today
         *
         * @property displayDate
         * @type object
         */
         displayDate: null
       },      

       /**
        * Set multiple initialization options at once.
        *
        * @method setOptions
        * @param obj {object} Object literal specifying a set of options
        */
       setOptions: function AddEvent_setOptions(obj)
       {
          this.options = YAHOO.lang.merge(this.options, obj);
          return this;
       },
       
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function AddEvent_onComponentsLoaded()
      {
         /* Shortcut for dummy instance */
         if (this.id === null)
         {
            return;
         }
      },
      
      /**
       * Renders the event create form. If the form has been previously rendered
       * it clears the form of any previously entered values otherwise fires off a
       * request to web script that generates the form.
       *
       * @method show
       */
      show: function AddEvent_show()
      {
         var args =
         {
            htmlid: this.id,
            site: this.options.siteId
         };
         
         if (this.options.eventURI)
         {
            args.uri = this.options.eventURI;
         }
         
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.URL_SERVICECONTEXT + "components/calendar/add-event",
            dataObj: args,
            successCallback:
            {
               fn: this.templateLoaded,
               scope: this
            },
            execScripts: true,
            failureMessage: "Could not load add event form"
         });
      },

      /**
       * Fired when the event create form has loaded successfully.
       * Sets up the various widgets on the form and initialises the forms runtime.
       *
       * @method templateLoaded
       * @param response {object} DomEvent
       */
       templateLoaded: function AddEvent_templateLoaded(response)
       {
          // Inject the template from the XHR request into a new DIV element
          var containerDiv = document.createElement("div");
          containerDiv.innerHTML = response.serverResponse.responseText;

          // The panel is created from the HTML returned in the XHR request, not the container
          var panelDiv = Dom.getFirstChild(containerDiv);

          this.panel = Alfresco.util.createYUIPanel(panelDiv);

          // Set the title depending on the mode
          Dom.get(this.id + "-title-div").innerHTML = Alfresco.util.message("title." + this.options.mode + "Event", this.name);
          // "All day" check box
          var allDay = Dom.get(this.id + "-allday");
          if (allDay)
          {
             Event.addListener(allDay, "click", this.onAllDaySelect, allDay, this);
          }
         
         var eventForm = new Alfresco.forms.Form(this.id + "-addEvent-form");
         eventForm.addValidation(this.id + "-title", Alfresco.forms.validation.mandatory, null, "keyup");
         
         var dateElements = ["td", "fd", this.id + "-start", this.id + "-end"], i, ii;
         for (i = 0, ii = dateElements.length; i < ii; i++)
         {
            eventForm.addValidation(dateElements[i], this._onDateValidation,
            {
               obj: this
            }, "blur");
         }
         
         // Setup date validation
         eventForm.addValidation("td", this._onDateValidation, { "obj": this }, "focus");
         eventForm.addValidation("fd", this._onDateValidation, { "obj": this }, "focus");
         eventForm.addValidation(this.id + "-start", this._onDateValidation, { "obj": this }, "blur");
         eventForm.addValidation(this.id + "-end", this._onDateValidation, { "obj": this }, "blur");
                     
         // OK Button
         this.okButton = Alfresco.util.createYUIButton(this, "ok-button", null,
         {
            type: "submit"
         });
         
         eventForm.setSubmitElements(this.okButton);

         if (!this.options.eventURI) // Create
         {
            eventForm.setAJAXSubmit(true,
            {
               successCallback:
               {
                  fn: this.onCreateEventSuccess,
                  scope: this
               }
            });
         
            // Initialise the start and end dates to today
            var today = this.options.displayDate || new Date();
            // Pretty formatting
            var dateStr = Alfresco.util.formatDate(today, DATE_LONG);
            Dom.get("fd").value = dateStr;
            Dom.get("td").value = dateStr;
            // Machine-readable formatting
            dateStr = Alfresco.util.formatDate(today, DATE_SHORT);
            Dom.get(this.id + "-from").value = dateStr;
            Dom.get(this.id + "-to").value = dateStr;
         }
         else  // Event Edit
         {   
            var form = Dom.get(this.id + "-addEvent-form");
            // Reset the "action" attribute
            form.attributes.action.nodeValue = Alfresco.constants.PROXY_URI + this.options.eventURI;
            
            eventForm.setAjaxSubmitMethod(Alfresco.util.Ajax.PUT);
            eventForm.setAJAXSubmit(true,
            {
               successCallback:
               {
                  fn: this.onEventUpdated,
                  scope: this
               }
            });        
            
            // Is this an all day event?
            var startTime = Dom.get(this.id + "-start"),
               endTime = Dom.get(this.id + "-end");
            
            // TODO: perhaps "allday" property to calendar event
            if (startTime.value === "00:00" && (startTime.value === endTime.value))
            {
               allDay.setAttribute("checked", "checked");
               this._displayTimeFields(false);
            }
         }
         
         eventForm.setSubmitAsJSON(true);
         // We're in a popup, so need the tabbing fix
         eventForm.applyTabFix();
         eventForm.init();
         
         var cancelButton = Alfresco.util.createYUIButton(this, "cancel-button", this.onCancelButtonClick);

         /**
          * Button declarations that, when clicked, display
          * the calendar date picker widget.
          */
         var startButton = new YAHOO.widget.Button(
         {
             type: "push",
             id: "calendarpicker",
             container: this.id + "-startdate"
         });
         startButton.on("click", this.onDateSelectButton, this);

         var endButton = new YAHOO.widget.Button(
         {
            type: "push",
            id: "calendarendpicker",
            container: this.id + "-enddate"
         });
         endButton.on("click", this.onDateSelectButton, this);

         // Display the panel
         this.panel.show();
         
         // Fix Firefox caret issue
         Alfresco.util.caretFix(this.id + "-addEvent-form");

         // Register the ESC key to close the dialog
         var escapeListener = new YAHOO.util.KeyListener(document,
         {
            keys: YAHOO.util.KeyListener.KEY.ESCAPE
         },
         {
            fn: function(id, keyEvent)
            {
               this.onCancelButtonClick();
            },
            scope: this,
            correctScope: true
         });
         escapeListener.enable();

         // Set intial focus
         Dom.get(this.id + "-title").focus();
      },
      
      _onDateValidation: function AddEvent__onDateValidation(field, args, event, form, silent)
      {
         // Check that the end date is after the start date
         var start = Alfresco.util.formatDate(Dom.get("fd").value, DATE_SHORT);
         var startDate = new Date(start + " " + Dom.get(args.obj.id + "-start").value);
         
         var to = Alfresco.util.formatDate(Dom.get("td").value, DATE_SHORT);
         var toDate = new Date(to + " " + Dom.get(args.obj.id + "-end").value);
         
         var after = YAHOO.widget.DateMath.after(toDate, startDate);
         
         if (Alfresco.logger.isDebugEnabled())
         {
            Alfresco.logger.debug("Current start date: " + start + " " + Dom.get(args.obj.id + "-start").value);
            Alfresco.logger.debug("Current end date: " + to + " " + Dom.get(args.obj.id + "-end").value);
            Alfresco.logger.debug("End date is after start date: " + after);
         }
         
         if (!after && !silent)
         {
            form.addError(form.getFieldLabel(field.id) + " cannot be before the start date.", field);
         }
         
         return after;
      },
      
      /**
       * Event handler that gets called when the user clicks on the "All day" event
       * checkbox in the event create / edit form. If selected, hides the time fields
       * from view.
       *
       * @method onAllDaySelect
       * @param e {object} DomEvent
       */
      onAllDaySelect: function AddEvent_onAllDaySelect(e, checkbox)
      {
         var display = !(checkbox.checked);
         this._displayTimeFields(display);
      },
      
      /**
       * If the user selectes the "All day" event option, then the start and end
       * time fields are hidden from view. The date field remains active.
       *
       * @method _displayTimeFields
       * @param display {Boolean} if true, displays the start / end time fields
       */   
      _displayTimeFields: function AddEvent__displayTimeFields(display)
      {
        var ids = [this.id + "-starttime", this.id + "-endtime"];
        var elem;
        for (var i=0; i < ids.length; i++)
        {
           elem = Dom.get(ids[i]);
           if (elem)
           {
             elem.style.display = (display ? "inline" : "none");
           }
        } 
      },
      
      onEventUpdated: function AddEvent_onEventUpdated(e)
      {
         this.panel.destroy();
         // Fire off "eventUpdated" event
         YAHOO.Bubbling.fire('eventUpdated');
      },

      /**
       * Event handler that gets fired when a user clicks on the date selection
       * button in the event creation form. Displays a mini YUI calendar.
       * Gets called for both the start and end date buttons.
       *
       * @method onDateSelectButton
       * @param e {object} DomEvent
       */
      onDateSelectButton: function AddEvent_onDateSelectButton(e)
      {
         var oCalendarMenu = new YAHOO.widget.Overlay("calendarmenu");
         oCalendarMenu.setBody("&#32;");
         oCalendarMenu.body.id = "calendarcontainer";

         var container = this.get("container");
         // Render the Overlay instance into the Button's parent element
         oCalendarMenu.render(container);

         // Align the Overlay to the Button instance
         oCalendarMenu.align();

         var navConfig = Alfresco.util.getCalendarControlConfiguration();
         var oCalendar = new YAHOO.widget.Calendar("buttoncalendar", oCalendarMenu.body.id, {close:true, navigator:navConfig});
         Alfresco.util.calI18nParams(oCalendar);
         oCalendar.render();

         oCalendar.changePageEvent.subscribe(function () {
            window.setTimeout(function () 
            {
               oCalendarMenu.show();
            }, 0);
         });
         
         oCalendar.selectEvent.subscribe(function (type, args) 
         {
            var date;

            if (args)
            {
               var prettyId, hiddenId;
               if (container.indexOf("enddate") > -1)
               {
                  prettyId = "td";
                  hiddenId = me.id + "-to";
               }
               else
               {
                  prettyId = "fd";
                  hiddenId = me.id + "-from";
               }

               date = args[0][0];
               var selectedDate = new Date(date[0], (date[1]-1), date[2]);

               var elem = Dom.get(prettyId);
               elem.value = Alfresco.util.formatDate(selectedDate, DATE_LONG);
               elem.focus();

               var hiddenEl = Dom.get(hiddenId);
               hiddenEl.value = Alfresco.util.formatDate(selectedDate, DATE_SHORT);

               if (prettyId == "fd")
               {
                  // If a new fromDate was selected
                  var toDate = new Date(Alfresco.util.formatDate(Dom.get("td").value, DATE_SHORT));
                  if(YAHOO.widget.DateMath.before(toDate, selectedDate))
                  {                     
                     //...adjust the toDate if toDate is earlier than the new fromDate
                     var tdEl = Dom.get("td");
                     tdEl.value = Alfresco.util.formatDate(selectedDate, DATE_LONG);
                  }
               }
            }

            oCalendarMenu.hide();
         }, me);
      },

      /**
       * Event handler that gets fired when a user clicks
       * on the cancel button in the event create form.
       *
       * @method onCancelButtonClick
       * @param e {object} DomEvent
       * @param obj {object} Object passed back from addListener method
       */
      onCancelButtonClick: function AddEvent_onCancelButtonClick(e, obj)
      {
           this.panel.destroy();
      },

      /**
       * Event handler that gets fired when an event is (successfully) created.
       * It in turns fires an "eventSaved" event passing in the name and start date
       * of the newly created event.
       *
       * @method onCreateEventSuccess
       * @param e {object} DomEvent
       */
      onCreateEventSuccess: function AddEvent_onCreateEventSuccess(e)
      {
         this.panel.destroy();

         var result = YAHOO.lang.JSON.parse(e.serverResponse.responseText);
         if (result.event)
         {
            YAHOO.Bubbling.fire("eventSaved",
            {
               name: result.event.name,
               from: result.event.from,
               start: result.event.start,
               end: result.event.end,
               uri: result.event.uri,
               tags: result.event.tags
            });
            // Refresh the tag component
            YAHOO.Bubbling.fire("tagRefresh");
         }
      }
   };
})();

/* Dummy instance to load optional YUI components early */
new Alfresco.module.AddEvent(null);
