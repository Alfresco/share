/**
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
 * Alfresco.DiscussionsToolbar
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Element = YAHOO.util.Element;

   Alfresco.DiscussionsToolbar = function(containerId)
   {
      this.name = "Alfresco.DiscussionsToolbar";
      this.id = containerId;
      this.widgets = {};
      this.modules = {};
      this.options = {};

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "connection"], this.onComponentsLoaded, this);

      // Decoupled event listeners
      YAHOO.Bubbling.on("deactivateAllControls", this.onDeactivateAllControls, this);

      return this;
   };

   Alfresco.DiscussionsToolbar.prototype =
   {

      /**
       * Object container for storing YUI widget instances.
       *
       * @property widgets
       * @type object
       */
      widgets : null,

      /**
       * Object container for initialization options
       *
       * @property options
       * @type {object} object literal
       */
      options:
      {
         /**
        * Sets the current site for this component.
        *
        * @property siteId
        * @type string
        */
         siteId: null,

         /**
           * The containerId in the current site
           *
           * @property containerId
           * @type string
           */
         containerId: null,

         /**
           * Decides if the create button should be enabled or not
           *
           * @property allowCreate
           * @type string
           */
         allowCreate: null

      },


      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.DocumentList} returns 'this' for method chaining
       */
      setOptions: function DToolbar_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },

      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.DocListTree} returns 'this' for method chaining
       */
      setMessages: function DToolbar_setMessages(obj)
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
      onComponentsLoaded: function DToolbar_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Initialises components, including YUI widgets.
       *
       * @method onReady
       */
      onReady: function DToolbar_onReady()
      {
         // Create button
         this.widgets.createButton = Alfresco.util.createYUIButton(this, "create-button", this.onNewTopicClick,
         {
            disabled: !this.options.allowCreate
         });

         // Rss Feed button
         this.widgets.rssFeedButton = Alfresco.util.createYUIButton(this, "rssFeed-button", null,
         {
            type: "link"
         });

         // initialize rss feed link
         if (this.widgets.rssFeedButton !== null)
         {
            this._generateRSSFeedUrl();
         }
      },

      /**
       * Dispatches the browser to the create a new forum topic
       *
       * @method onNewTopicClick
       * @param e {object} DomEvent
       */
      onNewTopicClick: function DToolbar_onNewTopicClick(e)
      {         
         window.location.href = Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + "/discussions-createtopic";
      },

      /**
       * Deactivate All Controls event handler
       *
       * @method onDeactivateAllControls
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDeactivateAllControls: function DToolbar_onDeactivateAllControls(layer, args)
      {
         var index, widget, fnDisable = Alfresco.util.disableYUIButton;
         for (index in this.widgets)
         {
            if (this.widgets.hasOwnProperty(index))
            {
               fnDisable(this.widgets[index]);
            }
         }
      },

      /**
       * Generates the HTML mark-up for the RSS feed link
       *
       * @method _generateRSSFeedUrl
       * @private
       */
      _generateRSSFeedUrl: function DiscussionsTopicList__generateRSSFeedUrl()
      {
         var url = Alfresco.constants.URL_FEEDSERVICECONTEXT + "components/discussions/rss?site=" + this.options.siteId;
         this.widgets.rssFeedButton.set("href", url);
      }

   };

})();