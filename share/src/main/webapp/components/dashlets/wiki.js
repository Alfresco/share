/**
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
 * Alfresco.dashlet.WikiDashlet
 * Aggregates events from all the sites the user belongs to.
 * For use on the user's dashboard.
 *
 * @namespace Alfresco
 * @class Alfresco.dashlet.WikiDashlet
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * WikiDashlet constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.dashlet.WikiDashlet} The new WikiDashlet instance
    * @constructor
    */
   Alfresco.dashlet.WikiDashlet = function WikiDashlet_constructor(htmlId)
   {
      Alfresco.dashlet.WikiDashlet.superclass.constructor.call(this, "Alfresco.dashlet.WikiDashlet", htmlId);
      
      this.parser = new Alfresco.WikiParser();
      
      return this;
   };

   YAHOO.extend(Alfresco.dashlet.WikiDashlet, Alfresco.component.Base,
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
          * The gui id.
          *
          * @property guid
          * @type string
          */
         guid: "",

         /**
          * Current siteId.
          *
          * @property siteId
          * @type string
          */
         siteId: "",

         /**
          * The pages on this site's wiki.
          *
          * @property pages
          * @type Array
          */
         pages: []
      },

      /**
       * Wiki mark-up parser instance.
       *
       * @property parser
       * @type Alfresco.WikiParser
       */
      parser: null,

      /**
       * Allows the user to configure the feed for the dashlet.
       *
       * @property configDialog
       * @type DOM node
       */
      configDialog: null,
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Initialises components, including YUI widgets.
       *
       * @method onReady
       */ 
      onReady: function WikiDashlet_onReady()
      {
         Event.addListener(this.id + "-wiki-link", "click", this.onConfigFeedClick, this, true);
         
         this.parser.URL = Alfresco.util.uriTemplate("sitepage",
         {
            site: this.options.siteId,
            pageid: "wiki-page?title="
         });

         var wikiDiv = Dom.get(this.id + "-scrollableList");
         // get all anchors and add them a style "theme-color-1"
         // see MNT-9901
         var anchors = wikiDiv.getElementsByTagName('a');
         for (i = 0; i < anchors.length; i++)
         {
            YAHOO.util.Dom.addClass(anchors[i], "theme-color-1");
         }
         wikiDiv.innerHTML = this.parser.parse(wikiDiv.innerHTML, this.options.pages);
      },
      
      /**
       * Configuration click handler
       *
       * @method onConfigFeedClick
       * @param e {object} HTML event
       */
      onConfigFeedClick: function WikiDashlet_onConfigFeedClick(e)
      {
         Event.stopEvent(e);

         var actionUrl = Alfresco.constants.URL_SERVICECONTEXT + "modules/wiki/config/" + encodeURIComponent(this.options.guid);
         
         if (!this.configDialog)
         {
            this.configDialog = new Alfresco.module.SimpleDialog(this.id + "-configDialog").setOptions(
            {
               width: "50em",
               templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/wiki/config/" + this.options.siteId,
               onSuccess:
               {
                  fn: function WikiDashlet_onConfigFeed_callback(e)
                  {
                     var obj = YAHOO.lang.JSON.parse(e.serverResponse.responseText);
                     if (obj)
                     {
                        // Update the content via the parser
                        Dom.get(this.id + "-scrollableList").innerHTML = this.parser.parse(obj["content"], this.options.pages);
                        
                        // Update the title
                        Dom.get(this.id + "-title").innerHTML = Alfresco.util.message("label.header-prefix", this.name) + (obj.title !== "" ? " - <a href=\"wiki-page?title=" + encodeURIComponent(e.config.dataObj.wikipage) + "\">" + obj.title + "</a>" : "");
                     }
                  },
                  scope: this
               }
            });
         }

         this.configDialog.setOptions(
         {
            actionUrl: actionUrl
         }).show();
      }
   });
})();