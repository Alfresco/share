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
 * Sites Module
 * 
 * @namespace Alfresco.module
 * @class .description
*/
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   Alfresco.module.Sites = function(htmlId)
   {
      return Alfresco.module.Sites.superclass.constructor.call(this, "Alfresco.module.Sites", htmlId, ["button", "menu", "container"]);
   };

   YAHOO.extend(Alfresco.module.Sites, Alfresco.component.Base,
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
          * Favourite sites
          * 
          * @property favouriteSites
          * @type object
          * @default {}
          */
         favouriteSites: {}
      },
      
      /**
       * Fired by YUI when parent element is available for scripting.
       *
       * @method onReady
       */
      onReady: function Sites_onReady()
      {
         // Notifications that the favourite sites have been updated
         YAHOO.Bubbling.on("favouriteSiteAdded", this.onFavouriteSiteAdded, this);
         YAHOO.Bubbling.on("favouriteSiteRemoved", this.onFavouriteSiteRemoved, this);
         YAHOO.Bubbling.on("siteDeleted", this.onSiteDeleted, this);

         this.preferencesService = new Alfresco.service.Preferences();
         var favsites = Alfresco.util.findValueByDotNotation(this.preferencesService.get(), Alfresco.service.Preferences.FAVOURITE_SITES, null);
         var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "modules/header/sites",
            dataObj =
            {
               htmlid: this.id
            };
         if (favsites)
         {
            dataObj.favsites = YAHOO.lang.JSON.stringify(favsites);
         }
         
         if (this.options.siteId !== "")
         {
            dataObj.siteId = this.options.siteId;
         }
         
         Alfresco.util.Ajax.request(
         {
            url: templateUrl,
            dataObj: dataObj,
            successCallback:
            {
               fn: this.onTemplateLoaded,
               scope: this
            },
            failureMessage: "Could not load module template from '" + templateUrl + "'.",
            scope: this,
            execScripts: true
         });
      },
      
      /**
       * Event callback when dialog template has been loaded
       *
       * @method onTemplateLoaded
       * @param response {object} Server response from load template XHR request
       */
      onTemplateLoaded: function Sites_onTemplateLoaded(response)
      {
         // Inject the template from the XHR request into a new DIV element
         var containerDiv = document.createElement("div");
         containerDiv.innerHTML = response.serverResponse.responseText;
         document.body.insertBefore(containerDiv, document.body.firstChild);

         this.widgets.sitesButton = new YAHOO.widget.Button(this.id,
         {
            type: "menu",
            menu: this.id + "-sites-menu",
            lazyloadmenu: false
         });
      },
      
      /**
       * Show the Create Site dialog
       *
       * @method showCreateSite
       */
      showCreateSite: function Sites_showCreateSite()
      {
         Alfresco.module.getCreateSiteInstance().show();
      },
      
      /**
       * Favourite Site has been added
       *
       * @method onFavouriteSiteAdded
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onFavouriteSiteAdded: function Sites_onFavouriteSiteAdded(layer, args)
      {
         var obj = args[1];
         if (obj && obj.shortName !== null)
         {
            this.options.favouriteSites[obj.shortName] = obj.title;
            this._renderFavouriteSites();
         }
      },

      /**
       * Favourite Site has been removed
       *
       * @method onFavouriteSiteAdded
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onFavouriteSiteRemoved: function Sites_onFavouriteSiteRemoved(layer, args)
      {
         var obj = args[1];
         if (obj && obj.shortName !== null)
         {
            if (obj.shortName in this.options.favouriteSites)
            {
               delete this.options.favouriteSites[obj.shortName];
               this._renderFavouriteSites();
            }
         }
      },

      /**
       * Site has been deleted - maybe remove from favourites menu
       *
       * @method onSiteDeleted
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onSiteDeleted: function Sites_onSiteDeleted(layer, args)
      {
         var obj = args[1];
         if (obj && obj.site !== null)
         {
            if (obj.site.shortName in this.options.favouriteSites)
            {
               delete this.options.favouriteSites[obj.site.shortName];
               this._renderFavouriteSites();
            }
         }
      },

      /**
       * Renders favourite sites into menu
       *
       * @method renderFavouriteSites
       * @private
       */
      _renderFavouriteSites: function Sites__renderFavouriteSites()
      {
         var sites = [], site, sitesMenu = this.widgets.sitesButton.getMenu(), sitesGroup, i, ii;
         
         // Create a sorted list of our current favourites
         for (site in this.options.favouriteSites)
         {
            if (this.options.favouriteSites.hasOwnProperty(site))
            {
               sites.push(site);
            }
         }
         sites.sort();

         sitesGroup = sitesMenu.getItemGroups()[0];
         for (i = 0, ii = sitesGroup.length; i < ii; i++)
         {
            sitesMenu.removeItem(0, 0, true);
         }
         
         Dom.setStyle(this.id + "-favouritesContainer", "display", sites.length > 0 ? "block" : "none");
         Dom.setStyle(this.id + "-favouriteSites", "display", sites.length > 0 ? "block" : "none");

         for (i = 0, ii = sites.length; i < ii; i++)
         {
            sitesMenu.addItem(
            {
               text: $html(this.options.favouriteSites[sites[i]]),
               url: Alfresco.util.uriTemplate("sitedashboardpage",
               {
                  site: sites[i]
               })
            }, 0);
         }
         
         // Show/hide "Add to favourites" menu item if we're in a site
         if (this.options.siteId.length !== 0)
         {
            Dom.setStyle(this.id + "-addFavourite", "display", this.options.siteId in this.options.favouriteSites ? "none" : "block");
         }
         
         sitesMenu.render();
      },

      /**
       * Adds the current site as a favourite
       *
       * @method addAsFavourite
       */
      addAsFavourite: function Sites_addAsFavourite()
      {
         var site =
         {
            shortName: this.options.siteId,
            title: this.options.siteTitle
         },
            me = this;

         var responseConfig =
         {
            failureCallback:
            {
               fn: function(event, obj)
               {
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     text: me.msg("message.save.failure")
                  });
               },
               scope: this
            },
            successCallback:
            {
               fn: function(event, obj)
               {
                  YAHOO.Bubbling.fire("favouriteSiteAdded", obj.site);
               },
               scope: this,
               obj:
               {
                  site: site
               }
            }
         };
         
         this.preferencesService.favouriteSite(site.shortName, responseConfig);
      }      
   });
})();
/**
 * Dummy instance to load optional YUI components early.
 * Use fake "null" id, which is tested later in onComponentsLoaded()
*/
var moduleSites = new Alfresco.module.Sites("null");