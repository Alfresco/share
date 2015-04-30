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
 * DateRange component.
 * 
 * @namespace Alfresco
 * @class Alfresco.DateRange
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      KeyListener = YAHOO.util.KeyListener;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * DateRange constructor.
    * 
    * @param {String} htmlId The HTML id of the control element
    * @param {String} valueHtmlId The HTML id prefix of the value elements
    * @return {Alfresco.DateRange} The new DateRange instance
    * @constructor
    */
   Alfresco.DateRange = function(htmlId, valueHtmlId)
   {
      Alfresco.DateRange.superclass.constructor.call(this, "Alfresco.DateRange", htmlId, ["button", "calendar"]);
      
      this.valueHtmlId = valueHtmlId;
      this.currentFromDate = "";
      this.currentToDate = "";
      
      return this;
   };
   
   YAHOO.extend(Alfresco.DateRange, Alfresco.component.Base,
   {
      /**
       * Current From date value
       * 
       * @property currentFromDate
       * @type string
       */
      currentFromDate: null,
      
      /**
       * Current To date value
       * 
       * @property currentToDate
       * @type string
       */
      currentToDate: null,
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function DateRange_onReady()
      {
         var toDate = new Date();
         var fromDate = new Date();
         var fromISO8601 = Alfresco.util.fromISO8601;
         fromDate.setMonth(toDate.getMonth() - 1);
         
         if (Dom.get(this.valueHtmlId).value)
         {
            var fullDate = Dom.get(this.valueHtmlId).value.split("|");
            if (fullDate[0])
            {
               // Use Date.parse to support non ISO8601 date defaults
               fullDate[0] = Alfresco.util.toISO8601(Date.parse(fullDate[0])) || fullDate[0];
               var fromDateText = Alfresco.util.fromISO8601(fullDate[0]).toString(this.msg("form.control.date-picker.entry.date.format"));
               Dom.get(this.id + "-date-from").value = fromDateText;			 
               this.currentFromDate = fullDate[0];
            }
            if (fullDate[1])
            {
               // Use Date.parse to support non ISO8601 date defaults
               fullDate[1] = Alfresco.util.toISO8601(Date.parse(fullDate[1])) || fullDate[1];
               var toDateText = Alfresco.util.fromISO8601(fullDate[1]).toString(this.msg("form.control.date-picker.entry.date.format"));
               Dom.get(this.id + "-date-to").value = toDateText;
               this.currentToDate = fullDate[1];
            }
            // Write output back (as ISO8601)
            Dom.get(this.valueHtmlId).value = fullDate.join("|");
         }
 
         // construct the pickers
         var page = (fromDate.getMonth() + 1) + "/" + fromDate.getFullYear();
         var selected = (fromDate.getMonth() + 1) + "/" + fromDate.getDate() + "/" + fromDate.getFullYear();   
         var navConfig = Alfresco.util.getCalendarControlConfiguration();
         this.widgets.calendarFrom = new YAHOO.widget.Calendar(this.id + "-from", this.id + "-from", { title:this.msg("form.control.date-picker.choose"), close:true, navigator:navConfig });
         this.widgets.calendarFrom.cfg.setProperty("pagedate", page);
         this.widgets.calendarFrom.cfg.setProperty("selected", selected);
         this.widgets.calendarFrom.hideEvent.subscribe(function()
         {
            // Focus icon after calendar is closed
            Dom.getElementsByClassName("datepicker-icon", "img", this.id + "-icon-from")[0].focus();
         }, this, true);
         Alfresco.util.calI18nParams(this.widgets.calendarFrom);
         // setup keyboard enter events on the image instead of the link to get focus outline displayed
         Alfresco.util.useAsButton(Dom.getElementsByClassName("datepicker-icon", "img", this.id + "-icon-from")[0], this._showPickerFrom, null, this);

         page = (toDate.getMonth() + 1) + "/" + toDate.getFullYear();
         selected = (toDate.getMonth() + 1) + "/" + toDate.getDate() + "/" + toDate.getFullYear();   
         this.widgets.calendarTo = new YAHOO.widget.Calendar(this.id + "-to", this.id + "-to", { title:this.msg("form.control.date-picker.choose"), close:true, navigator:navConfig });
         this.widgets.calendarTo.cfg.setProperty("pagedate", page);
         this.widgets.calendarTo.cfg.setProperty("selected", selected);
         this.widgets.calendarTo.hideEvent.subscribe(function()
         {
            // Focus icon after calendar is closed
            Dom.getElementsByClassName("datepicker-icon", "img", this.id + "-icon-to")[0].focus();
         }, this, true);
         Alfresco.util.calI18nParams(this.widgets.calendarTo);
         // setup keyboard enter events on the image instead of the link to get focus outline displayed
         Alfresco.util.useAsButton(Dom.getElementsByClassName("datepicker-icon", "img", this.id + "-icon-to")[0], this._showPickerTo, null, this);

         // setup events
         this.widgets.calendarFrom.selectEvent.subscribe(this._handlePickerChangeFrom, this, true);
         Event.addListener(this.id + "-date-from", "keyup", this._handleFieldChangeFrom, this, true);
         Event.addListener(this.id + "-icon-from", "click", this._showPickerFrom, this, true);
         this.widgets.calendarTo.selectEvent.subscribe(this._handlePickerChangeTo, this, true);
         Event.addListener(this.id + "-date-to", "keyup", this._handleFieldChangeTo, this, true);
         Event.addListener(this.id + "-icon-to", "click", this._showPickerTo, this, true);
         
         // render the calendar controls
         this.widgets.calendarFrom.render();
         this.widgets.calendarTo.render();
      },
      
      /**
       * Handles the date picker icon being clicked.
       * 
       * @method _showPickerFrom
       * @param event The event that occurred
       * @private
       */
      _showPickerFrom: function DateRange__showPickerFrom(event)
      {
         this.widgets.calendarFrom.show();
      },
      
      /**
       * Handles the date picker icon being clicked.
       * 
       * @method _showPickerTo
       * @param event The event that occurred
       * @private
       */
      _showPickerTo: function DateRange__showPickerTo(event)
      {
         this.widgets.calendarTo.show();
      },
      
      /**
       * Setting a new value into textField without cursor position losses
       * 
       * @method _setValueAndSavePosition
       * @param obj
       * @param val
       * @private
       */
      _setValueAndSavePosition: function DateRange__setValueAndSavePosition(obj, val)
      {
         var startPos = obj.selectionStart;
         var endPos = obj.selectionEnd;
         var ie8 = false;
         if (typeof startPos == 'undefined')
         {
            ie8 = true;
            var range = this._getInputSelection(obj);
            startPos = range.start;
            endPos = range.end;
         }
         obj.value = val;
         if (startPos == endPos)
         {
            if (!ie8)
            {
               obj.selectionStart = startPos;
               obj.selectionEnd = endPos;
            } else
            {
               this._setCaretPosition(obj, startPos);
            }
         }
         else
         {
            // do nothing
         }
      },
      
      /**
       * Get a cursor (caret) positions (is used for ie8)
       * 
       * @method _getInputSelection
       * @param el
       * @private
       */
      _getInputSelection: function DateRange__getInputSelection(el)
      {
         var start = 0, end = 0, normalizedValue, range,
             textInputRange, len, endRange;
     
         range = document.selection.createRange();
 
         if (range && range.parentElement() == el) 
         {
             len = el.value.length;
             normalizedValue = el.value.replace(/\r\n/g, "\n");
 
             // Create a working TextRange that lives only in the input
             textInputRange = el.createTextRange();
             textInputRange.moveToBookmark(range.getBookmark());
 
             // Check if the start and end of the selection are at the very end
             // of the input, since moveStart/moveEnd doesn't return what we want
             // in those cases
             endRange = el.createTextRange();
             endRange.collapse(false);
 
             if (textInputRange.compareEndPoints("StartToEnd", endRange) > -1) 
             {
                 start = end = len;
             } else 
             {
                 start = -textInputRange.moveStart("character", -len);
                 start += normalizedValue.slice(0, start).split("\n").length - 1;
 
                 if (textInputRange.compareEndPoints("EndToEnd", endRange) > -1) 
                 {
                     end = len;
                 } else 
                 {
                     end = -textInputRange.moveEnd("character", -len);
                     end += normalizedValue.slice(0, end).split("\n").length - 1;
                 }
             }
         }
		 
         var res = { start: start, end: end };
         return res
      },
	  
      /**
       * Set cursor (caret) position (is used for ie8)
       * 
       * @method _setCaretPosition
       * @param elem
       * @param caretPos
       * @private
       */
      _setCaretPosition: function DateRange__setCaretPosition(elem, caretPos)
      {
         if(elem != null) 
         {
             if(elem.createTextRange) 
             {
                 var range = elem.createTextRange();
                 range.move('character', caretPos);
                 range.select();
             }
             else 
             {
                 if(elem.selectionStart) 
                 {
                     elem.focus();
                     elem.setSelectionRange(caretPos, caretPos);
                 }
                 else
                     elem.focus();
             }
         }
      },
      
      /**
       * Handles the from date being changed in the date picker YUI control.
       * 
       * @method _handlePickerChangeFrom
       * @param type
       * @param args
       * @param obj
       * @private
       */
      _handlePickerChangeFrom: function DateRange__handlePickerChangeFrom(type, args, obj)
      {
         // update the date field
         var selected = args[0];
         var selDate = this.widgets.calendarFrom.toDate(selected[0]);
         var dateEntry = selDate.toString(this.msg("form.control.date-picker.entry.date.format"));
         var obj = Dom.get(this.id + "-date-from");
         this._setValueAndSavePosition(obj, dateEntry);
         
         // if we have a valid date, convert to ISO format and set value on hidden field
         if (selDate != null)
         {
            var isoValue = Alfresco.util.toISO8601(selDate, {"milliseconds":false});
            this.currentFromDate = isoValue;
            this._updateCurrentValue();
            
            Dom.removeClass(this.id + "-date-from", "invalid");
         }
         
         // Hide calendar if the calendar was open (Unfortunately there is no proper yui api method for this)
         if (Dom.getStyle(this.id + "-from", "display") != "none")
         {
            this.widgets.calendarFrom.hide();
         }
      },
      
      /**
       * Handles the from date being changed in the date picker YUI control.
       * 
       * @method _handlePickerChangeTo
       * @param type
       * @param args
       * @param obj
       * @private
       */
      _handlePickerChangeTo: function DateRange__handlePickerChangeTo(type, args, obj)
      {
         // update the date field
         var selected = args[0];
         var selDate = this.widgets.calendarTo.toDate(selected[0]);
         var dateEntry = selDate.toString(this.msg("form.control.date-picker.entry.date.format"));
         var obj = Dom.get(this.id + "-date-to");
         this._setValueAndSavePosition(obj, dateEntry);
         
         // if we have a valid date, convert to ISO format and set value on hidden field
         if (selDate != null)
         {
            var isoValue = Alfresco.util.toISO8601(selDate, {"milliseconds":false});
            this.currentToDate = isoValue;
            this._updateCurrentValue();
            
            Dom.removeClass(this.id + "-date-to", "invalid");
         }
         
         // Hide calendar if the calendar was open (Unfortunately there is no proper yui api method for this)
         if (Dom.getStyle(this.id + "-to", "display") != "none")
         {
            this.widgets.calendarTo.hide();
         }
      },
      
      /**
       * Updates the currently stored date range value in the hidden form field.
       * 
       * @method _updateCurrentValue
       * @private
       */
      _updateCurrentValue: function DateRange__updateCurrentValue()
      {
         var value = this.currentFromDate + "|" + this.currentToDate;
         value = value == "|" ? "" : value;         
         Dom.get(this.valueHtmlId).value = value;
      },
      
      /**
       * Handles the date or time being changed in either input field.
       * 
       * @method _handleFieldChangeFrom
       * @param event The event that occurred
       * @private
       */
      _handleFieldChangeFrom: function DateRange__handleFieldChangeFrom(event)
      {
         var changedDate = Dom.get(this.id + "-date-from").value;
         if (changedDate.length > 0)
         {
            // Only set for actual value changes so tab or shift events doesn't remove the "text selection" of the input field
            if (event == undefined || (event.keyCode != KeyListener.KEY.TAB && event.keyCode != KeyListener.KEY.SHIFT))
            {

               // convert to format expected by YUI
               var parsedDate = Date.parseExact(changedDate, this.msg("form.control.date-picker.entry.date.format"));
               if (parsedDate != null)
               {
                  this.widgets.calendarFrom.select((parsedDate.getMonth() + 1) + "/" + parsedDate.getDate() + "/" + parsedDate.getFullYear());
                  var selectedDates = this.widgets.calendarFrom.getSelectedDates();
                  if (selectedDates.length > 0)
                  {
                     Dom.removeClass(this.id + "-date-from", "invalid");
                     var firstDate = selectedDates[0];
                     this.widgets.calendarFrom.cfg.setProperty("pagedate", (firstDate.getMonth()+1) + "/" + firstDate.getFullYear());
                     this.widgets.calendarFrom.render();
                  }
               }
               else
               {
                  Dom.addClass(this.id + "-date-from", "invalid");
               }
            }
         }
         else
         {
            // when the date is completely cleared remove the hidden field and remove the invalid class
            Dom.removeClass(this.id + "-date-from", "invalid");
            this.currentFromDate = "";
            this._updateCurrentValue();
         }
      },
      
      /**
       * Handles the date or time being changed in either input field.
       * 
       * @method _handleFieldChangeFrom
       * @param event The event that occurred
       * @private
       */
      _handleFieldChangeTo: function DateRange__handleFieldChangeTo(event)
      {
         var changedDate = Dom.get(this.id + "-date-to").value;
         if (changedDate.length > 0)
         {
            // convert to format expected by YUI
            var parsedDate = Date.parseExact(changedDate, this.msg("form.control.date-picker.entry.date.format"));
            if (parsedDate != null)
            {
               this.widgets.calendarTo.select((parsedDate.getMonth() + 1) + "/" + parsedDate.getDate() + "/" + parsedDate.getFullYear());
               var selectedDates = this.widgets.calendarTo.getSelectedDates();
               if (selectedDates.length > 0)
               {
                  Dom.removeClass(this.id + "-date-to", "invalid");
                  var firstDate = selectedDates[0];
                  this.widgets.calendarTo.cfg.setProperty("pagedate", (firstDate.getMonth()+1) + "/" + firstDate.getFullYear());
                  this.widgets.calendarTo.render();
               }
            }
            else
            {
               Dom.addClass(this.id + "-date-to", "invalid");
            }
         }
         else
         {
            // when the date is completely cleared remove the hidden field and remove the invalid class
            Dom.removeClass(this.id + "-date-to", "invalid");
            this.currentToDate = "";
            this._updateCurrentValue();            
         }
      }
   });
})();