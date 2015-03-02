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
 * Tag Filter component.
 * 
 * @namespace Alfresco
 * @class Alfresco.TagFilter
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
    * Tag Filter constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.TagFilter} The new TagFilter instance
    * @constructor
    */
   Alfresco.TagFilter = function(htmlId)
   {
      Alfresco.TagFilter.superclass.constructor.call(this, "Alfresco.TagFilter", htmlId);
      
      // Override unique event key, as we want tag events to be page-global
      this.uniqueEventKey = "tag-link";
      
      // Decoupled event listeners
      YAHOO.Bubbling.on("tagRefresh", this.onTagRefresh, this);
      YAHOO.Bubbling.on("changeFilter", this.onTagRefresh, this);	  
      
      return this;
   };
   
   YAHOO.extend(Alfresco.TagFilter, Alfresco.component.BaseFilter,
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
          * @default ""
          */
         containerId: "",

         /**
          * Root node for use in non-Site mode
          * 
          * @property rootNode
          * @type string
          */
         rootNode: null,

         /**
          * Number of tags to show
          *
          * @property numTags
          * @type int
          * @default -1
          */
         numTags: -1
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       *
       * @method onReady
       * @override
       */   
      onReady: function TagFilter_onReady()
      {
         Alfresco.TagFilter.superclass.onReady.call(this);
      },
      
      /**
       * Called if this filter is the owner, but the filterId could be found in the DOM
       *
       * @method handleFilterIdNotFound
       * @override
       * @param filter {object} New filter trying to be set
       */
      handleFilterIdNotFound: function TagFilter_handleFilterIdNotFound(filter)
      {
         var domId = Alfresco.util.generateDomId(),
            newTag = this._generateTagMarkup(
         {
            name: filter.filterData,
            domId: domId
         });
         
         Dom.get(this.id + "-tags").innerHTML += newTag;

         // This component now owns the active filter
         this.selectedFilter = Dom.get(domId);
         YUIDom.addClass(this.selectedFilter, "selected");
      },


      /**
       * BUBBLING LIBRARY EVENT HANDLERS
       * Disconnected event handlers for inter-component event notification
       */
      
      /**
       * Function that gets called when another component fires "tagRefresh"
       * Issues a request to retrieve the latest tag data.
       *
       * @method onTagRefresh
       * @param layer {string} the event source
       * @param args {object} arguments object
       */
      onTagRefresh: function TagFilter_onRefresh(layer, args)
      {
         var url;
         
         if (this.options.siteId)
         {
            // Use the tag scope API
            url = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/tagscopes/site/{site}/{container}/tags?d={d}&topN={tn}",
            {
               site: this.options.siteId,
               container: this.options.containerId,
               d: new Date().getTime(),
               tn: this.options.numTags
            });
         }
         else
         {
            // Use the collaboration REST API (from Office add-in)
            url = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "collaboration/tagQuery?d={d}&m={m}&s={s}&n={n}",
            {
               d: new Date().getTime(),
               m: this.options.numTags,
               s: "count",
               n: encodeURIComponent(this.options.rootNode)
            });
         }
         
         Alfresco.util.Ajax.request(
         {
            method: Alfresco.util.Ajax.GET,
            url: url,
            successCallback:
            {
               fn: this.onTagRefresh_success,
               scope: this
            },
            failureCallback:
            {
               fn: this.onTagRefresh_failure,
               scope: this
            }
         });
      },
      
      /**
       * Event handler for when the tag data loads successfully.
       *
       * @method onTagRefresh_success
       * @param response {object} Server response object
       */ 
      onTagRefresh_success: function TagFilter_onTagRefresh_success(response)
      {
         if (response && response.json && !YAHOO.lang.isUndefined(response.json.tags))
         {
            var html = "",
               tags = response.json.tags;
            for (var i = 0, ii = tags.length; i < ii; i++)
            {
               html += this._generateTagMarkup(tags[i]);
            }
            
            Dom.get(this.id + "-tags").innerHTML = html;
         }
      },

      /**
       * Event handler for when the tag data fails to load.
       *
       * @method onTagRefresh_failure
       * @param response {object} Server response object
       */ 
      onTagRefresh_failure: function TagFilter_onTagRefresh_failure(response)
      {
         Dom.get(this.id + "-tags").innerHTML = '<span class="error-alt">' + this.msg("message.refresh.failure") + '</span>';
      },
      

      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Generates the HTML for a tag.
       *
       * @method _generateTagMarkup
       * @param tag {object} the tag to render
       */
      _generateTagMarkup: function TagFilter__generateTagMarkup(tag)
      {
         var idAttr = tag.domId ? ' id="' + $html(tag.domId) + '"' : "",
            html = '<li' + idAttr + '><span class="tag">';
         
         html += '<a href="#" class="' + this.uniqueEventKey + '" rel="' + $html(tag.name) + '">' + $html(tag.name) + '</a>';
         if (tag.count)
         {
            html += '&nbsp;(' + tag.count + ')';
         }
         html += '</span></li>';
         return html;
      }
   });
})();