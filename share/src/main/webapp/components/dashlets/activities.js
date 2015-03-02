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
 * Dashboard Activities common component.
 * 
 * @namespace Alfresco.dashlet
 * @class Alfresco.dashlet.Activities
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
    * Preferences
    */
   var PREFERENCES_ACTIVITIES = "org.alfresco.share.activities",
       PREF_FILTER = ".filter",
       PREF_RANGE = ".range",
       PREF_ACTIVITIES = ".activities";
   
   /**
    * Dashboard Activities constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.dashlet.Activities} The new component instance
    * @constructor
    */
   Alfresco.dashlet.Activities = function Activities_constructor(htmlId)
   {
      Alfresco.dashlet.Activities.superclass.constructor.call(this, "Alfresco.dashlet.Activities", htmlId, ["button", "container", "calendar"]);
      
      // Preferences service
      this.services.preferences = new Alfresco.service.Preferences();
      
      return this;
   };

   YAHOO.extend(Alfresco.dashlet.Activities, Alfresco.component.Base,
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
          * Dashlet mode
          * 
          * @property mode
          * @type string
          * @default "site"
          */
         mode: "site",

         /**
          * Current siteId.
          * 
          * @property siteId
          * @type string
          */
         siteId: "",

         /**
          * Currently active filter.
          * 
          * @property activeFilter
          * @type string
          * @default "today"
          */
         activeFilter: "today",
         
         /**
          * Component region ID.
          * 
          * @property regionId
          * @type string
          */
         regionId: ""
      },

      /**
       * Activity list DOM container.
       * 
       * @property activityList
       * @type object
       */
      activityList: null,

      /**
       * URL to the RSS feed
       * 
       * @property link
       * @type String
       */
      link: "",
      
      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function Activities_onReady()
      {
         var me = this;
         
         // Create dropdown filter widgets
         this.widgets.range = Alfresco.util.createYUIButton(this, "range", this.onDateFilterChanged,
         {
            type: "menu",
            menu: "range-menu",
            lazyloadmenu: false
         });
         
         this.widgets.user = Alfresco.util.createYUIButton(this, "user", this.onExclusionFilterChanged,
         {
            type: "menu",
            menu: "user-menu",
            lazyloadmenu: false
         });
         
         this.widgets.activities = Alfresco.util.createYUIButton(this, "activities", this.onActivitiesFilterChanged,
         {
            type: "menu",
            menu: "activities-menu",
            lazyloadmenu: false
         });
         
         // The activity list container
         this.activityList = Dom.get(this.id + "-activityList");
         
         // Load preferences to override default filter and range
         this.widgets.range.set("label", this.msg("filter.7days"));
         this.widgets.range.value = "7";
         this.widgets.user.set("label", this.msg("filter.all"));
         this.widgets.user.value = "all";
         this.widgets.activities.set("label", this.msg("filter.allItems") + " " + Alfresco.constants.MENU_ARROW_SYMBOL);
         this.widgets.activities.value = "";

         var prefs = this.services.preferences.get();
         var activitiesPreference = Alfresco.util.findValueByDotNotation(prefs, this.buildPreferences(PREF_ACTIVITIES), "");
         if (activitiesPreference !== null)
         {
            this.widgets.activities.value = activitiesPreference;
            // set the correct menu label
            var menuItems = this.widgets.activities.getMenu().getItems();
            for (index in menuItems)
            {
               if (menuItems.hasOwnProperty(index))
               {
                  if (menuItems[index].value === activitiesPreference)
                  {
                     this.widgets.activities.set("label", menuItems[index].cfg.getProperty("text") + " " + Alfresco.constants.MENU_ARROW_SYMBOL);
                     break;
                  }
               }
            }
         }
         
         var rangePreference = Alfresco.util.findValueByDotNotation(prefs, this.buildPreferences(PREF_RANGE), "7");
         if (rangePreference !== null)
         {
            this.widgets.range.value = rangePreference;
            // set the correct menu label
            var menuItems = this.widgets.range.getMenu().getItems();
            for (index in menuItems)
            {
               if (menuItems.hasOwnProperty(index))
               {
                  if (menuItems[index].value === rangePreference)
                  {
                     this.widgets.range.set("label", menuItems[index].cfg.getProperty("text") + " " + Alfresco.constants.MENU_ARROW_SYMBOL);
                     break;
                  }
               }
            }
         }
         
         var filterPreference = Alfresco.util.findValueByDotNotation(prefs, this.buildPreferences(PREF_FILTER), "all");
         if (filterPreference !== null)
         {
            this.widgets.user.value = filterPreference;
            // set the correct menu label
            var menuItems = this.widgets.user.getMenu().getItems();
            for (index in menuItems)
            {
               if (menuItems.hasOwnProperty(index))
               {
                  if (menuItems[index].value === filterPreference)
                  {
                     this.widgets.user.set("label", menuItems[index].cfg.getProperty("text") + " " + Alfresco.constants.MENU_ARROW_SYMBOL);
                     break;
                  }
               }
            }
         }
         
         // Display the toolbar now that we have selected the filter
         Dom.removeClass(Selector.query(".toolbar div", this.id, true), "hidden");
         // Populate the activity list
         this.populateActivityList(this.widgets.range.value, this.widgets.user.value, this.widgets.activities.value);
      },
      
      /**
       * Build the Activities dashlet preferences name string with optional suffix.
       * The component region ID and the current siteId (if any) is used as part of the
       * preferences name - to uniquely identify the preference within the site or user
       * dashboard context.
       * 
       * @method buildPreferences
       * @param suffix {string} optional suffix to append to the preferences name
       */
      buildPreferences: function Activities_buildPreferences(suffix)
      {
         var opt = this.options;
         return PREFERENCES_ACTIVITIES + "." + opt.regionId + (opt.siteId ? ("." + opt.siteId) : "") + (suffix ? suffix : "");
      },
      
      /**
       * Populate the activity list via Ajax request
       * @method populateActivityList
       */
      populateActivityList: function Activities_populateActivityList(dateFilter, userFilter, activityFilter)
      {
         // Load the activity list
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.URL_SERVICECONTEXT + "components/dashlets/activities/list",
            dataObj:
            {
               site: this.options.siteId,
               mode: this.options.mode,
               dateFilter: dateFilter,
               userFilter: userFilter,
               activityFilter: activityFilter
            },
            successCallback:
            {
               fn: this.onListLoaded,
               scope: this,
               obj: dateFilter
            },
            failureCallback:
            {
               fn: this.onListLoadFailed,
               scope: this
            },
            scope: this,
            noReloadOnAuthFailure: true
         });
      },
      
      /**
       * List loaded successfully
       * @method onListLoaded
       * @param p_response {object} Response object from request
       */
      onListLoaded: function Activities_onListLoaded(p_response, p_obj)
      {
         this.options.activeFilter = p_obj;
         var html = p_response.serverResponse.responseText;
         if (YAHOO.lang.trim(html).length === 0)
         {
            this.activityList.innerHTML = Dom.get(this.id + "-empty").innerHTML;
         }
         else
         {
            this.activityList.innerHTML = html;
            Dom.getElementsByClassName("relativeTime", "span", this.activityList, function()
            {
               this.innerHTML = Alfresco.util.relativeTime(this.innerHTML);
            })
            var olderDates = this.msg("label.older-activities"),
               lastDay = "";
            Dom.getElementsByClassName("relativeDate", "span", this.activityList, function()
            {
               Dom.addClass(this, "body");
               
               // Get the relative Date
               var activityDay = Alfresco.util.relativeDate(this.innerHTML,
                  {
                     olderDates: olderDates
                  });

               // if the relativeDate is not the same as it was last time, output it and update the last day found.
               if (activityDay !== lastDay)
               {
                  this.innerHTML = activityDay;
                  lastDay = activityDay;
               }
               else
               {
                  // if the relative date is the same, remove the element that separates the days
                  var parent = this.parentNode;
                  parent.parentNode.removeChild(parent);
               }
            })
         }
         this.updateFeedLink(this.widgets.range.value, this.widgets.user.value, this.widgets.activities.value);
      },

      /**
       * List load failed
       * @method onListLoadFailed
       */
      onListLoadFailed: function Activities_onListLoadFailed()
      {
         this.activityList.innerHTML = '<div class="detail-list-item first-item last-item">' + this.msg("label.load-failed") + '</div>';
      },
      
      /**
       * Sets the the feed link
       * @method updateFeedLink
       */
      updateFeedLink: function Activities_updateFeedLink(dateFilter,userFilter,activityFilter)
      {         
         var url = Alfresco.constants.URL_FEEDSERVICECONTEXT + "components/dashlets/activities/list?";
         var dataObj =
         {
            format: "atomfeed",
            mode: this.options.mode,
            site: this.options.siteId,
            dateFilter: dateFilter,
            userFilter: userFilter,
            activityFilter: activityFilter
         };
         url += Alfresco.util.Ajax.jsonToParamString(dataObj, true);
         this.link = url;
      },
      
      openFeedLink: function Activities_openFeedLink()
      {
         window.open(this.link);
      },

      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * Date drop-down changed event handler
       *
       * @method onDateFilterChanged
       * @param p_sType {string} The event
       * @param p_aArgs {array} Event arguments
       */
      onDateFilterChanged: function Activities_onDateFilterChanged(p_sType, p_aArgs)
      {
         var menuItem = p_aArgs[1];
         
         if (menuItem)
         {
            this.widgets.range.set("label", menuItem.cfg.getProperty("text") + " " + Alfresco.constants.MENU_ARROW_SYMBOL);
            this.widgets.range.value = menuItem.value;
            this.populateActivityList(this.widgets.range.value, this.widgets.user.value, this.widgets.activities.value);
            this.services.preferences.set(this.buildPreferences(PREF_RANGE), this.widgets.range.value);
         }
      },
      
      /**
       * Exclusion drop-down changed event handler
       *
       * @method onExclusionFilterChanged
       * @param p_sType {string} The event
       * @param p_aArgs {array} Event arguments
       */
      onExclusionFilterChanged: function Activities_onExclusionFilterChanged(p_sType, p_aArgs)
      {
         var menuItem = p_aArgs[1];
         
         if (menuItem)
         {
            this.widgets.user.set("label", menuItem.cfg.getProperty("text") + " " + Alfresco.constants.MENU_ARROW_SYMBOL);
            this.widgets.user.value = menuItem.value;
            this.populateActivityList(this.widgets.range.value, this.widgets.user.value, this.widgets.activities.value);
            this.services.preferences.set(this.buildPreferences(PREF_FILTER), this.widgets.user.value);
         }
      },
      
      /**
       * Activities drop-down changed event handler
       *
       * @method onActivitiesFilterChanged
       * @param p_sType {string} The event
       * @param p_aArgs {array} Event arguments
       */
      onActivitiesFilterChanged: function Activities_onActivitiesFilterChanged(p_sType, p_aArgs)
      {
         var menuItem = p_aArgs[1];
         
         if (menuItem)
         {
            this.widgets.activities.set("label", menuItem.cfg.getProperty("text") + " " + Alfresco.constants.MENU_ARROW_SYMBOL);
            this.widgets.activities.value = menuItem.value;
            this.populateActivityList(this.widgets.range.value, this.widgets.user.value, this.widgets.activities.value);
            this.services.preferences.set(this.buildPreferences(PREF_ACTIVITIES), this.widgets.activities.value);
         }
      }
   });
})();