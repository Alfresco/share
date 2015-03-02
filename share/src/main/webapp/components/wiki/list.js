/*
 *** Alfresco.WikiList
*/
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   Alfresco.WikiList = function(containerId)
   {
      this.name = "Alfresco.WikiList";
      this.id = containerId;
      this.options = {};

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "connection", "editor", "tabview"], this.onComponentsLoaded, this);
      return this;
   };
   
   Alfresco.WikiList.prototype = 
   {
      _selectedTag: "",

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
          */
         siteId: "",

         /**
          * The pages on this sites wiki.
          *
          * @property pages
          * @type Array
          */
         pages: [],
         
         /**
          * Error state.
          *
          * @property error
          * @type boolean
          */
         error: false,
         
         /**
          * Widget permissions.
          *
          * @property permissions
          * @type object
          */
         permissions: {}
      },

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.DocListToolbar} returns 'this' for method chaining
       */
      setOptions: function WikiList_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);

         // Make sure the parser is using the current site
         this.$parser = new Alfresco.WikiParser();
         this.$parser.URL = Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + "/wiki-page?title=";

         return this;
      },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function WikiList_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },
       
      /**
       * Fired by YUI when parent element is available for scripting.
       * Initialises components, including YUI widgets.
       *
       * @method onReady
       */
      onReady: function WikiList_onReady()
      {
         if (this.options.error)
         {
            // Site or container not found - deactivate controls
            YAHOO.Bubbling.fire("deactivateAllControls");
            return;
         }
          
         this._initMouseOverListeners();

         // Render any mediawiki markup
         // TODO: look at doing this on the server
         var divs = Dom.getElementsByClassName('pageCopy', 'div');
         var div;
         for (var i=0; i < divs.length; i++)
         {
            div = divs[i];
            div.innerHTML = this.$parser.parse(div.innerHTML, this.options.pages);
         }

         Dom.getElementsByClassName("parseTime", "span", this.id, function()
         {
            this.innerHTML = Alfresco.util.formatDate(Alfresco.util.fromISO8601(this.innerHTML), Alfresco.util.message("date-format.default"));
         })

         YAHOO.Bubbling.addDefaultAction('delete-link', function(layer, args)
         {
            var link = args[1].target;
            if (link)
            {
               var title, node;
               // Search for the "title" attribute as that has the page title
               for (var i = 0, ii = link.attributes.length; i < ii; i++)
               {
                  node = link.attributes[i];
                  if (node.nodeName.toLowerCase() === 'title')
                  {
                     title = node.nodeValue;
                     break;
                  }
               }

               if (title)
               {
                  // Trigger the delete dialog in the toolbar
                  YAHOO.Bubbling.fire('deletePage',
                  {
                     title: title
                  });
               }
            }

            return true;
         });

         YAHOO.Bubbling.addDefaultAction('wiki-tag-link', function(layer, args)
         {
            var link = args[1].target;
            if (link)
            {
               var tagName = link.firstChild.nodeValue;
               YAHOO.Bubbling.fire("tagSelected",
               {
                  "tagname": tagName
               });
            }
            return true;
         });

         YAHOO.Bubbling.on("tagSelected", this.onTagSelected, this);

         // Fire permissions event to allow other components to update their UI accordingly
         YAHOO.Bubbling.fire("userAccess",
         {
            userAccess: this.options.permissions
         });

         YAHOO.Bubbling.fire("filterChanged",
         {
            filterId: this.options.filterId,
            filterOwner: "Alfresco.WikiFilter",
            filterdata: ""
         });
      },
       
      onTagSelected: function WikiList_onTagSelected(e, args)
      {
         var tagname = args[1].tagname;

         if (tagname === Alfresco.util.message('label.all-tags', 'Alfresco.TagComponent'))
         {
            var divs = Dom.getElementsByClassName('wikiPageDeselect', 'div');
            for (var i=0; i < divs.length; i++)
            {
               Dom.removeClass(divs[i], 'wikiPageDeselect');
            }

            this._tagSelected = "";
         }
         else if (this._tagSelected !== tagname)
         {
            var divs = Dom.getElementsByClassName('wikipage', 'div');
            var div, i, j;

            for (i = 0, j = divs.length; i < j; i++)
            {
               div = divs[i];

               if (Dom.hasClass(div, 'wikiPageDeselect'))
               {
                  Dom.removeClass(div, 'wikiPageDeselect');
               }

               if (!Dom.hasClass(div, 'wp-' + tagname))
               {
                  Dom.addClass(divs[i], 'wikiPageDeselect');
               }
            }

            this._tagSelected = tagname;
         }
       },

      _initMouseOverListeners: function WikiList__initMouseOverListeners()
      {
         var divs = Dom.getElementsByClassName('wikipage', 'div');
         for (var x=0; x < divs.length; x++)
         {
            Event.addListener(divs[x], 'mouseover', this.mouseOverHandler);
            Event.addListener(divs[x], 'mouseout', this.mouseOutHandler);
         }
      },

      mouseOverHandler: function WikiList_mouseOverHandler(e)
      {
         var currentTarget = e.currentTarget;
         Dom.addClass(currentTarget, 'over');
      },

      mouseOutHandler: function WikiList_mouseOutHandler(e)
      {
         var currentTarget = e.currentTarget;
         Dom.removeClass(currentTarget, 'over');
      }
   };
   
})();
