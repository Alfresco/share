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
 * Alfresco.dashlet.RssFeed
 *
 * Aggregates events from all the sites the user belongs to.
 * For use on the user's dashboard.
 *
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * RssFeed constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.dashlet.RssFeed} The new RssFeed instance
    * @constructor
    */
   Alfresco.dashlet.RssFeed = function RssFeed_constructor(htmlId)
   {
      Alfresco.dashlet.RssFeed.superclass.constructor.call(this, "Alfresco.dashlet.RssFeed", htmlId);
      
      this.configDialog = null;
      
      return this;
   };

   YAHOO.extend(Alfresco.dashlet.RssFeed, Alfresco.component.Base,
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
          * The component id
          *
          * @property componentId
          * @type string
          * @default ""
          */
         componentId: "",

         /**
          * THe url to the feed to display
          *
          * @property feedURL
          * @type string
          * @default ""
          */
         feedURL: "",

         /**
          * The maximum limit of posts to display
          *
          * @property limit
          * @type string
          * @default "all"
          */
         limit: "all",
         
         /**
          * The target for the RSS link to open
          * 
          * @property target
          * @type string
          * @default "_self"
          */
         target: "_self"
      },  
      
      /**
       * The DOM element containing the feed title.
       * 
       * @property titleELement
       * @type element
       */
      titleElement: null,
      
      /**
       * The DOM element containing the feed list.
       * 
       * @property titleELement
       * @type element
       */
      feedElement: null,
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function RF_onReady()
      {
         // Get the DOM elements for the feed title and item list...
         this.titleElement = Dom.get(this.id + this.options.titleElSuffix);
         this.feedElement = Dom.get(this.id + this.options.targetElSuffix);
         this._loadFeed();
      },
      
      /**
       * Load the RSS feed
       *
       * @method loadFeed
       * @private
       */
      _loadFeed: function RF__loadFeed()
      {
         // Separate the protocol from the URI to ensure that WebScript request can be processed...
         // (it is not possible to use the entire URL as a REST token)...
         var uri = this.options.feedURL;
         var protocol = "http://";
         var protocolEndsAt = this.options.feedURL.indexOf("://");
         if (protocolEndsAt != -1)
         {
            protocol = this.options.feedURL.substring(0, protocolEndsAt);
            uri = this.options.feedURL.substring(protocolEndsAt + 3);
         }
         
         // Request the RSS feed...
         if (uri.indexOf(location.host + "/") != 0)
         {
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_CONTEXT + "service/components/dashlets/async-rssfeed/protocol/" + protocol + "/limit/" + this.options.limit + "/target/" + this.options.target + "?feed-url=" + encodeURIComponent(uri) + "",
               method: Alfresco.util.Ajax.GET,
               requestContentType: Alfresco.util.Ajax.JSON,
               successCallback:
               {
                  fn: this.onLoadFeedSuccess,
                  scope: this
               },
               failureCallback: 
               {
                  fn: this.onLoadFeedFailure,
                  scope: this
               }
            });
         }
         else
         {
            var feedURL = this.options.feedURL.replace("/feedservice/", "/page/").replace("/proxy/alfresco-feed/", "/proxy/alfresco/");
            if (feedURL.indexOf("?") > 0)
            {
               feedURL += "&loopback=1";
            }
            else
            {
               feedURL += "?loopback=1";
            }
            
            Alfresco.util.Ajax.request(
            {
               url: feedURL,
               method: Alfresco.util.Ajax.GET,
               successCallback:
               {
                  fn: function (response)
                  {
                     Alfresco.util.Ajax.request(
                     {
                        url: Alfresco.constants.URL_CONTEXT + "service/components/dashlets/async-rssfeed/protocol/" + protocol + "/limit/" + this.options.limit + "/target/" + this.options.target + "?feed-url=" + encodeURIComponent(uri) + "",
                        method: Alfresco.util.Ajax.POST,
                        dataStr: response.serverResponse.responseText,
                        requestContentType: "text/xml",
                        successCallback:
                        {
                           fn: this.onLoadFeedSuccess,
                           scope: this
                        },
                        failureCallback: 
                        {
                           fn: this.onLoadFeedFailure,
                           scope: this
                        }
                     });
                  },
                  scope: this
               },
               failureCallback: 
               {
                  fn: this.onLoadFeedFailure,
                  scope: this
               },
               noReloadOnAuthFailure: true
            });
         }
      },
      
      /**
       * RSS Feed loaded successfully
       * 
       * @method onLoadFeedSuccess
       * @param response {object} Response object from Alfresco.util.Ajax.request()
       */
      onLoadFeedSuccess: function RF_onLoadFeedSuccess(response)
      {
         // Update the dashlet with the RSS title and items...
         var json = Alfresco.util.parseJSON(response.serverResponse.responseText);
         this.feedElement.innerHTML = json.html;
         this.titleElement.innerHTML = json.title;
      },
      
      /**
       * RSS Feed failed to load
       * 
       * @method onLoadFeedSuccess
       * @param response {object} Response object from Alfresco.util.Ajax.request()
       */
      onLoadFeedFailure: function RF_onLoadFeedFailure(response)
      {
         this.titleElement.innerHTML = this.msg("title.error.unavailable");
         this.feedElement.innerHTML = this.msg("label.noItems");
      },

      /**
       * Called when the user clicks the config rss feed link.
       * Will open a rss config dialog
       *
       * @method onConfigFeedClick
       * @param e The click event
       */
      onConfigFeedClick: function RF_onConfigFeedClick(e)
      {
         Event.stopEvent(e);
         
         var actionUrl = Alfresco.constants.URL_SERVICECONTEXT + "modules/feed/config/" + encodeURIComponent(this.options.componentId);
         
         if (!this.configDialog)
         {
            this.configDialog = new Alfresco.module.SimpleDialog(this.id + "-configDialog").setOptions(
            {
               width: "50em",
               templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/feed/config",
               onSuccess:
               {
                  fn: function RssFeed_onConfigFeed_callback(response)
                  {
                     var rss = response.json;

                     // Save url for new config dialog openings
                     this.options.feedURL = (rss && rss.feedURL) ? rss.feedURL : this.options.feedURL;
                     this.options.limit = rss.limit;
                     this.options.target = rss.target;

                     // Update title and items are with new rss 
                     if (rss.content)
                     {
                        this.feedElement.innerHTML = rss.content;
                        this.titleElement.innerHTML = rss.title;
                     }
                     else
                     {
                        this._loadFeed();
                     }
                  },
                  scope: this
               },
               doSetupFormsValidation:
               {
                  fn: function RssFeed_doSetupForm_callback(form)
                  {
                     form.addValidation(this.configDialog.id + "-url", Alfresco.forms.validation.mandatory, null, "keyup");
                     form.addValidation(this.configDialog.id + "-url", Alfresco.forms.validation.url, null, "keyup");

                     Dom.get(this.configDialog.id + "-url").value = this.options.feedURL;
                     Dom.get(this.configDialog.id + "-new_window").checked = (this.options.target == "_blank");
                     
                     var select = Dom.get(this.configDialog.id + "-limit"), options = select.options, option, i, j;
                     // ALF-14482 fix. Remembering the checkbox state.
                     var newWindowCheckBox = Dom.get(this.configDialog.id + "-new_window");

                     newWindowCheckBox.defaultChecked = newWindowCheckBox.checked;

                     for (i = 0, j = options.length; i < j; i++)
                     {
                        option = options[i];
                        if (option.value === this.options.limit)
                        {
                           option.selected = true;
                           break;
                        }
                     }
                  },
                  scope: this
               }
            });
         }

         // ALF-14482 fix. Remembering the checkbox state.
         this.configDialog.onCancel = function RssFeed_doBeforeDialogShow_onCancel()
         {
            this.hide();
            var checkbox = this.dialog.form.new_window;
            checkbox.checked = checkbox.defaultChecked;
         };

         this.configDialog.setOptions(
         {
            actionUrl: actionUrl
         }).show();
      }
   });
})();