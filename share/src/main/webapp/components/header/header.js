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
 * Global Header
 * 
 * @namespace Alfresco
 * @class Alfresco.component.Header
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
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $siteURL = Alfresco.util.siteURL;

   Alfresco.component.Header = function(htmlId)
   {
      return Alfresco.component.Header.superclass.constructor.call(this, "Alfresco.component.Header", htmlId, ["button", "menu", "container"]);
   };

   YAHOO.extend(Alfresco.component.Header, Alfresco.component.Base,
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
          * @default ""
          */
         siteId: "",

         /**
          * Current site title.
          * 
          * @property siteTitle
          * @type string
          * @default ""
          */
         siteTitle: "",
         
         /**
          * Number of characters required for a search.
          *
          * @property minSearchTermLength
          * @type int
          * @default 1
          */
         minSearchTermLength: 1,
         
         /**
          * URI replacement tokens
          * 
          * @property tokens
          * @type object
          * @default {}
          */
         tokens: {}
      },

      /**
       * Application Item YAHOO.widget instances
       *
       * @property appItems
       * @type Array
       */
      appItems: null,

      /**
       * User Item YAHOO.widget instances
       *
       * @property userItems
       * @type Array
       */
      userItems: null,

      /**
       * Default search text
       *
       * @property defaultSearchText
       * @type string
       */
      defaultSearchText: null,

      /**
       * Last status update time
       *
       * @property statusUpdateTime
       * @type Date
       */
      statusUpdateTime: null,
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Initial History Manager event registration
       *
       * @method onReady
       */
      onReady: function Header_onReady()
      {
         Dom.removeClass(this.id + "-appItems", "hidden");
         this.replaceUriTokens();
         this.configureSearch();
         this.configureMyStatus();
      },
      
      /**
       * Called by header rendering code to register Application Item YAHOO.widget instances
       *
       * @method setAppItems
       */
      setAppItems: function Header_setAppItems(items)
      {
         this.appItems = items;
      },

      /**
       * Called by header rendering code to register User Item YAHOO.widget instances
       *
       * @method setUserItems
       */
      setUserItems: function Header_setUserItems(items)
      {
         this.userItems = items;
      },


      /**
       * About Share Handlers
       */

      /**
       * Show the About Share dialog
       *
       * @method showAboutShare
       */
      showAboutShare: function Header_showAboutShare()
      {
         Alfresco.module.getAboutShareInstance().show();
      },


      /**
       * Header Items Handlers
       */

      /**
       * Token replacement for header item URLs
       *
       * @method replaceUriTokens
       */
      replaceUriTokens: function Header_replaceUriTokens()
      {
         var tokens = YAHOO.lang.merge(Alfresco.constants.URI_TEMPLATES, Alfresco.constants.HELP_PAGES, this.options.tokens),
            links = Selector.query("a", this.id),
            link,
            attr;
         if (tokens.userid)
         {
             tokens.userid = encodeURIComponent(tokens.userid);
         }
         
         for (var i = 0, ii = links.length; i < ii; i++)
         {
            link = links[i];
            attr = Dom.getAttribute(link, "templateUri");
            if (attr != null)
            {
               link.href = Alfresco.util.renderUriTemplate(attr, tokens);
            }
         }
      },


      /**
       * Search Handlers
       */
      
      /**
       * Configure search area
       *
       * @method configureSearch
       */
      configureSearch: function Header_configureSearch()
      {
         this.widgets.searchBox = Dom.get(this.id + "-searchText");
         this.defaultSearchText = this.msg("header.search.default");
         
         Event.addListener(this.widgets.searchBox, "focus", this.onSearchFocus, null, this);
         Event.addListener(this.widgets.searchBox, "blur", this.onSearchBlur, null, this);
         
         this.setDefaultSearchText();
         
         // Register the "enter" event on the search text field
         var me = this;
         
         this.widgets.searchEnterListener = new YAHOO.util.KeyListener(this.widgets.searchBox,
         {
            keys: YAHOO.util.KeyListener.KEY.ENTER
         }, 
         {
            fn: me.submitSearch,
            scope: this,
            correctScope: true
         }, "keydown").enable();

         this.widgets.searchMore = new YAHOO.widget.Button(this.id + "-search_more",
         {
            type: "menu",
            menu: this.id + "-searchmenu_more"
         });
      },
      
      /**
       * Update image class when search box has focus.
       *
       * @method onSearchFocus
       */
      onSearchFocus: function Header_onSearchFocus()
      {
         if (this.widgets.searchBox.value == this.defaultSearchText)
         {
            Dom.removeClass(this.widgets.searchBox, "faded");
            this.widgets.searchBox.value = "";
         }
         else
         {
            this.widgets.searchBox.select();
         }
      },
      
      /**
       * Set default search text when box loses focus and is empty.
       *
       * @method onSearchBlur
       */
      onSearchBlur: function Header_onSearchBlur()
      {
         var searchText = YAHOO.lang.trim(this.widgets.searchBox.value);
         if (searchText.length === 0)
         {
            /**
             * Since the blur event occurs before the KeyListener gets
             * the enter we give the enter listener a chance of testing
             * against "" instead of the help text.
             */
            YAHOO.lang.later(100, this, this.setDefaultSearchText, []);
         }
      }, 
      
      /**
       * Set default search text for search box.
       *
       * @method setDefaultSearchText
       */
      setDefaultSearchText: function Header_setDefaultSearchText()
      {
         Dom.addClass(this.widgets.searchBox, "faded");
         this.widgets.searchBox.value = this.defaultSearchText;
      },

      /**
       * Get current search text from search box.
       *
       * @method getSearchText
       */
      getSearchText: function Header_getSearchText()
      {
         return YAHOO.lang.trim(this.widgets.searchBox.value);
      },
      
      /**
       * Will trigger a search, via a page refresh to ensure the Back button works correctly
       *
       * @method submitSearch
       */
      submitSearch: function Header_submitSearch()
      {
         var searchText = this.getSearchText();
         if (searchText.replace(/\*/g, "").length < this.options.minSearchTermLength)
         {
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.minimum-length", this.options.minSearchTermLength)
            });
         }
         else
         {
            // Redirect to the search page
            var url = "search?t=" + encodeURIComponent(searchText);
            // Append repository search argument if within repo browser page or previous repository search
            if (window.location.pathname.match("/repository$") == "/repository" ||
                (window.location.pathname.match("/search$") == "/search" && window.location.search.indexOf("r=true") != -1))
            {
               url += "&r=true";
            }
            window.location = $siteURL(url);
         }
      },
      
      /**
       * This is the user status at the time the page was loaded. It is also updated each time the user makes an update.
       * 
       * @property _currentStatus
       * @type string
       */
      _currentStatus: "",
      
      /**
       * Keeps track of if the user has clicked in the status box. The first time they click the current status should be
       * cleared, but the second time they click it shouldn't be (this is to allow editing within the status box).
       * 
       * @property _clickedStatusOnce
       * @type boolean
       */
      _clickedStatusOnce: false,
      
      /**
       * My Status handlers
       */
      
      /**
       * Configure My Status UI
       *
       * @method configureMyStatus
       */
      configureMyStatus: function Header_configureMyStatus()
      {
         this.widgets.statusBox = Dom.get(this.id + "-statusText");
         this.widgets.statusTime = Dom.get(this.id + "-statusTime");
         this._currentStatus = this.widgets.statusBox.value; // Store the loaded value.
         
         // Always reset the status when the menu is opened/closed. This ensures that entered
         // but non-posted status is not shown in the menu
         this.widgets.userMenu = Dom.get(this.id + "-user_user");
         Event.addListener(this.widgets.userMenu, "click", function() {
            this.widgets.statusBox.value = this._currentStatus;
         }, null, this);
         
         
         var statusISOTime = this.widgets.statusTime.attributes.title.value;
         if (statusISOTime !== "")
         {
            this.statusUpdateTime = Alfresco.util.fromISO8601(statusISOTime);
         }
         this.setStatusRelativeTime();

         // Find and siable the menuItem containing the My Status elements
         Alfresco.util.bind(function Header_configureMyStatus_fnDisableUserMenu()
         {
            var allItems = this.userItems.concat(this.appItems),
               item;

            for (var i = 0, ii = allItems.length; i < ii; i++)
            {
               item = allItems[i];
               if (item instanceof YAHOO.widget.Button && YAHOO.lang.isFunction(item.getMenu) && item.getMenu() !== null)
               {
                  var menuItems = item.getMenu().getItems(),
                     menuItem;

                  for (var j = 0, jj = menuItems.length; j < jj; j++)
                  {
                     menuItem = menuItems[j];
                     if (Dom.hasClass(menuItem.element, "HEADER-MARKER"))
                     {
                        // Found the menu item, now disable it to remove default event handler behaviour
                        menuItem.cfg.setProperty("disabled", true);
                        return;
                     }
                  }
               }
            }
         }, this)();

         // Stop the "click" event propagating to the menu handlers
         Event.on(this.widgets.statusBox, "click", function(p_oEvent)
         {
            Event.stopEvent(p_oEvent);
         });

         // When the user clicks in the status box, clear the previous status to make it easier to add new information...
         var _this = this;
         YAHOO.util.Event.addListener(this.id + "-statusText", "click", function(e)
         {
            if (_this._clickedStatusOnce)
            {
               // Don't clear if already clicked
            }
            else
            {
               _this._clickedStatusOnce = true;
               Dom.get(_this.id + "-statusText").value = "";
            }
         });
         
         // When the user clicks away from the status box, reset the previous status if they have not entered a value...
         YAHOO.util.Event.addListener(this.id + "-statusText", "blur", function(e)
         {
            _this._clickedStatusOnce = false;
         });
         
         this.widgets.submitStatus = new YAHOO.widget.Button(this.id + "-submitStatus");
         this.widgets.submitStatus.on("click", this.submitStatus, this.widgets.submitStatus, this);
      },

      /**
       * Get current status text from textarea.
       *
       * @method getStatusText
       */
      getStatusText: function Header_getStatusText()
      {
         return YAHOO.lang.trim(this.widgets.statusBox.value);
      },

      /**
       * Updates relative status time display.
       *
       * @method setStatusRelativeTime
       */
      setStatusRelativeTime: function Header_setStatusRelativeTime()
      {
         var relativeTime = (this.statusUpdateTime === null) ? this.msg("status.never-updated") : Alfresco.util.relativeTime(this.statusUpdateTime);
         this.widgets.statusTime.innerHTML = this.msg("status.updated", relativeTime);
      },
      
      
      /**
       * Submit status handler
       *
       * @method submitStatus
       */
      submitStatus: function Header_submitStatus()
      {
         this._clickedStatusOnce = false;
         Alfresco.util.Ajax.jsonPost(
         {
            url: Alfresco.constants.URL_SERVICECONTEXT + "components/profile/userstatus",
            dataObj:
            {
               status: this.getStatusText()
            },
            successCallback:
            {
               fn: this.onStatusUpdated,
               scope: this
            },
            failureMessage: this.msg("message.status.failure")
         });
      },

      /**
       * Status submitted handler
       *
       * @method onStatusUpdated
       */
      onStatusUpdated: function Header_onStatusUpdated(response)
      {
         this.statusUpdateTime = Alfresco.util.fromISO8601(response.json.userStatusTime.iso8601);
         this.setStatusRelativeTime();
         this._currentStatus = this.getStatusText();
         Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg("message.status.success")
         });
      }
   });
})();