/*
 *** Alfresco.BlogToolbar
*/
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Element = YAHOO.util.Element;

   Alfresco.BlogToolbar = function(containerId)
   {
      this.name = "Alfresco.BlogToolbar";
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

   Alfresco.BlogToolbar.prototype =
   {
      /**
       * Object container for storing YUI widget instances.
       *
       * @property widgets
       * @type object
       */
      widgets : null,
            
      /**
       * Object container for storing module instances.
       *
       * @property modules
       * @type object
       */
      modules: null,

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
         allowCreate: null,

         /**
           * Decides if the Configure button should be enabled or not
           *
           * @property allowConfigure
           * @type string
           */
         allowConfigure: null
      },


      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.DocumentList} returns 'this' for method chaining
       */
      setOptions: function BlogToolbar_setOptions(obj)
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
      setMessages: function BlogToolbar_setMessages(obj)
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
      onComponentsLoaded: function BlogToolbar_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Initialises components, including YUI widgets.
       *
       * @method onReady
       */
      onReady: function BlogToolbar_onReady()
      {
         // Create button
         this.widgets.createButton = Alfresco.util.createYUIButton(this, "create-button", this.onNewBlogClick,
         {
            disabled: !this.options.allowCreate
         });

         // Rss Feed button
         this.widgets.rssFeedButton = Alfresco.util.createYUIButton(this, "rssFeed-button", null,
         {
            type: "link"
         });

         if (this.widgets.rssFeedButton !== null)
         {
            // initialize rss feed link
            this._generateRSSFeedUrl();
         }
      },

      /**
       * Dispatches the browser to the create blog post
       *
       * @method onNewBlogClick
       * @param e {object} DomEvent
       */
      onNewBlogClick: function BlogToolbar_onNewBlogClick(e)
      {         
         window.location.href = Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + "/blog-postedit";
      },

      /**
       * Generates the HTML mark-up for the RSS feed link
       *
       * @method _generateRSSFeedUrl
       * @private
       */
      _generateRSSFeedUrl: function BlogToolbar__generateRSSFeedUrl()
      {
         var url = YAHOO.lang.substitute(Alfresco.constants.URL_FEEDSERVICECONTEXT + "components/blog/rss?site={site}",
         {
            site: this.options.siteId
         });
         this.widgets.rssFeedButton.set("href", url);
      },

      /**
       * Action handler for the configure blog button
       */
      onConfigureBlog: function BlogToolbar_onConfigureBlog(e, p_obj)
      {
         // load the module if not yet done
         if (!this.modules.configblog)
         {
            this.modules.configblog = new Alfresco.module.ConfigBlog(this.id + "-configblog");
         }

         this.modules.configblog.setOptions(
         {
            siteId: this.options.siteId,
            containerId: this.options.containerId
         });

         this.modules.configblog.showDialog();

         Event.preventDefault(e);
      },

      /**
       * Deactivate All Controls event handler
       *
       * @method onDeactivateAllControls
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDeactivateAllControls: function BlogToolbar_onDeactivateAllControls(layer, args)
      {
         var index, widget, fnDisable = Alfresco.util.disableYUIButton;
         for (index in this.widgets)
         {
            if (this.widgets.hasOwnProperty(index))
            {
               fnDisable(this.widgets[index]);
            }
         }
      }
   };

})();