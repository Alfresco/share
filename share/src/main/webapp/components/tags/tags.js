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
 * Alfresco.TagComponent
 * 
 * @namespace Alfresco
 * @class Alfresco.TagComponent
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * TagComponent constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.TagComponent} The new TagComponent instance
    * @constructor
    */
   Alfresco.TagComponent = function(htmlId)
   {
      Alfresco.TagComponent.superclass.constructor.call(this, "Alfresco.TagComponent", htmlId);
      
      // Decoupled event listeners
      YAHOO.Bubbling.on("tagSelected", this.onTagSelected, this);
      
      return this;
   };
   
   YAHOO.extend(Alfresco.TagComponent, Alfresco.component.Base,
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
          */
         siteId: "",

         /**
          * ContainerId representing root container
          *
          * @property containerId
          * @type string
          */
         containerId: ""
      },      

      /**
       * Fired by YUI when parent element is available for scripting.
       * Registers event handler on "tagRefresh" event. If a component wants to refresh
       * the tags component, they need to fire this event.
       *
       * @method onReady
       */   
      onReady: function TagComponent_onReady()
      {
         this._registerDefaultActionHandler();

         // Create twister from our H2 tag
         Alfresco.util.createTwister(this.id + "-h2", "TagComponent");
         
         YAHOO.Bubbling.on("tagRefresh", this.onTagRefresh, this);
      },
      
      /**
       * Registers a default action listener on <em>all</em> of the tag links in the 
       * component. Fires "tagSelected" event with the name of the tag that was selected.
       *
       * To register for the event, interested components should do something like this:
       * YAHOO.Bubbling.on("tagSelected", this.onTagSelected, this); 
       *
       * @method _registerDefaultActionHandler
       */
      _registerDefaultActionHandler: function TagComponent_registerDefaultActionHandler()
      {
         YAHOO.Bubbling.addDefaultAction('tag-link', function(layer, args)
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
      },
      
      /**
       * Handler for the "tagRefresh" event
       * Issues a request to the repo to retrieve the latest tag data.
       *
       * @method onTagRefresh
       * @param e {object} DomEvent
       */
      onTagRefresh: function TagComponent_onRefresh(e)
      {
         var uri = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/tagscopes/site/{site}/{container}/tags?d={d}",
         {
            site: this.options.siteId,
            container: this.options.containerId,
            d: new Date().getTime()
         });
         
         Alfresco.util.Ajax.request(
         {
            method: Alfresco.util.Ajax.GET,
            url: uri,
            successCallback:
            {
              fn: this.onTagsLoaded,
              scope: this
            },
          failureMessage: "Couldn't refresh tag data"
        });
      },
      
      /**
       * Event handler for tagSelected event
       *
       * @method onTagSelected
       */
      onTagSelected: function TagComponent_onTagSelected(layer, args)
      {
         var tagname = args[1].tagname,
            candidates = YAHOO.util.Selector.query("a[rel='" + tagname.replace("'", "\\'") + "']", this.id),
            liTags = YAHOO.util.Selector.query("li", this.id);
         
         Dom.removeClass(liTags, "selected");
         if (candidates.length == 1)
         {
            Dom.addClass(candidates[0].parentNode.parentNode, "selected");
         }
      },
      
      /**
       * Event handler that gets called when the tag data 
       * loads successfully.
       *
       * @method onTagsLoaded
       * @param e {object} DomEvent
       */ 
      onTagsLoaded: function TagComponent_onTagsLoaded(e)
      {
         var resp = YAHOO.lang.JSON.parse(e.serverResponse.responseText);
         if (resp && !YAHOO.lang.isUndefined(resp.tags))
         {
            var html = '<li><span class="tag"><a href="#" class="tag-link" rel="-all-">' + this.msg("label.all-tags") + '</a></span></li>',
               tags = resp.tags, tag, i, ii;

            for (i = 0, ii = tags.length; i < ii; i++)
            {
               tag = tags[i];
               html += this._generateTagMarkup(tag);
            }
            
            Dom.get(this.id + '-ul').innerHTML = html;
         }
      },
      
      /**
       * Generates the HTML for a tag.
       *
       * @method _generateTagMarkup
       * @param tag {Object} the tag to render
       */
      _generateTagMarkup: function TagComponent__generateTagMarkup(tag)
      {
         var html = '<li><span class="tag">';
         html += '<a href="#" class="tag-link" rel="' + $html(tag.name) + '">' + $html(tag.name) + '</a>&nbsp;(' + tag.count + ')';
         html += '</span></li>';
         return html;
      }
   });
})();