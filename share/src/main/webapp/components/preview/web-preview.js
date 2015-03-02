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
 * WebPreview component.
 *
 * Displays the node's content using various plugins depending on the node's content mime type & thumbnails available.
 * Document, video & image previewers are shipped out of the box.
 *
 * Now supports plugins to be able to render custom content, by adding new <plugin-condition> elements in .get.config.xml.
 *
 * @namespace Alfresco
 * @class Alfresco.WebPreview
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Selector = YAHOO.util.Selector,
      Element = YAHOO.util.Element,
      KeyListener = YAHOO.util.KeyListener;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * WebPreview constructor.
    *
    * @param {string} htmlId The HTML id of the parent element
    * @return {Alfresco.WebPreview} The new WebPreview instance
    * @constructor
    * @private
    */
   Alfresco.WebPreview = function(containerId)
   {
      // Note "uploader" is required so we get the YAHOO.deconcept.SWFObject
      Alfresco.WebPreview.superclass.constructor.call(this, "Alfresco.WebPreview", containerId, ["button", "container", "uploader"]);
      this.plugin = null;

      // MNT-9235: Listening on 'previewChangedEvent', because some actions
      // (those are updating current document) don't reload the page.
      // Modification dates of the thumbnails are never updated in this case... 
      // (ALF-6621)
      YAHOO.Bubbling.on("previewChangedEvent", this.onPreviewChanged, this);

      /* Decoupled event listeners are added in setOptions */
      return this;
   };

   YAHOO.extend(Alfresco.WebPreview, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         thumbnailModification: [],
         
         /**
          * Current siteId.
          * 
          * @property siteId
          * @type string
          */
         siteId: "",
         
         /**
          * Noderef to the content to display
          *
          * @property nodeRef
          * @type string
          */
         nodeRef: "",

         /**
          * The size of the content
          *
          * @property size
          * @type string
          */
         size: "0",

         /**
          * The file name representing root container
          *
          * @property name
          * @type string
          */
         name: "",

         /**
          * The mimeType of the node to display, needed to decide what plugin that should be used.
          *
          * @property mimeType
          * @type string
          */
         mimeType: "",

         /**
          * A list of previews available for this node, needed to decide which plugin that should be used.
          *
          * @property previews
          * @type Array
          */
         thumbnails: [],

         /**
          * A json representation of the .get.config.xml file.
          * This is evaluated on the client side since we need the plugins to make sure it is supported
          * the user's browser and browser plugins.
          *
          * @property pluginConditions
          * @type Array
          */
         pluginConditions: [],

         /**
          * The base to the rest api call for the node's content or thumbnails
          *
          * @property api
          * @type string
          * @default "api"
          */
         api: "api",

         /**
          * The proxy to use for the rest api call for the node's content or thumbnails.
          * I.e. "alfresco" (or "alfresco-noauth" for public content & pages)
          *
          * @property proxy
          * @type string
          * @default "alfresco"
          */
         proxy: "alfresco",

         /**
          * MNT-9235: This flag identifies whether content of current node modified.
          * That means that the cached thumbnail is no more valid and it should be updated
          *
          * @property avoidCachedThumbnail
          * @type boolean
          * @default false
          */
         avoidCachedThumbnail: false
      },

      /**
       * Space for preview "plugins" to register themselves in.
       * To provide a 3rd party plugin:
       *
       * 1. Create a javascript file and make it define a javascript class that defines a "plugin class" in this namespace.
       * 2. Override this component's .get.head.ftl file and make sure your javascript file (and its resources) are included.
       * 3. Override this component's .get.config.xml and define for which mimeTypes or thumbnails it shall be used.
       * 4. To make sure your plugin works in the browser, define a report() method that
       *    returns nothing if the browser is supported and otherwise a string with a message saying the reason the
       *    plugin can't be used in the browser.
       * 5. Define a display() method that will display the browser plugin or simply return a string of markup that shall be inserted.
       *
       * @property Plugins
       * @type Object
       */
      Plugins: {},

      /**
       * If a plugin was found to preview the content it will be stored here, for future reference.
       *
       * @property plugin One of the plugins that have registered themselves in Alfresco.WebPreview.Plugin
       * @type Object
       * @public
       */
      plugin: null,
      
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function WP_onComponentsLoaded()
      {
          // SWFObject patch to help flash plugins, will ensure all flashvars are URI encoded
         YAHOO.deconcept.SWFObject.prototype.getVariablePairs = function()
         {
            var variablePairs = [],
               key,
               variables = this.getVariables();
            for (key in variables)
            {
               if (variables.hasOwnProperty(key))
               {
                  variablePairs[variablePairs.length] = key + "=" + encodeURIComponent(variables[key]);
               }
            }
            return variablePairs;
         };
         Event.onContentReady(this.id, this.onReady, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting
       *
       * @method onReady
       */
      onReady: function WP_onReady()
      {
         // Convert the JSON string conditions back into an object...
         this.options.pluginConditions = eval(this.options.pluginConditions);
         
         // Setup web preview
         this.setupPreview(false);
         
         // Refresh preview on meta-data update
         YAHOO.Bubbling.on("previewChangedEvent", this.doRefresh, this);
         
         // Post an activity feed item to record the preview of the node
         if (this.options.siteId)
         {
            Alfresco.util.Ajax.jsonPost(
            {
               url: Alfresco.constants.PROXY_URI + "slingshot/doclib/activity",
               dataObj:
               {
                  site: this.options.siteId,
                  fileName: this.options.name,
                  nodeRef: this.options.nodeRef,
                  type: "file-previewed",
                  page: "document-details"
               },
               failureCallback:
               {
                  fn: function()
                  {
                     // do nothing - not important enough to bother the user about
                  }
               }
            });
         }
      },

      /**
       * Will find a previewer and set it up if one existed
       *
       * @method resolvePreviewer
       * @private
       */
      setupPreview: function WP_setupPreview()
      {
         // Save a reference to the HTMLElement displaying texts so we can alter the texts later
         this.widgets.previewerElement = Dom.get(this.id + "-previewer-div");

         // Display the preparing previewer message
         Selector.query(".message", this.widgets.previewerElement, true).innerHTML = this.msg("label.preparingPreviewer");

         // Parameter nodeRef is mandatory
         if (this.options.nodeRef === undefined)
         {
            throw new Error("A nodeRef must be provided");
         }

         if (this.options.size == "0")
         {
            // Shrink the web previewers real estate and tell user that node has no content
            this.widgets.previewerElement.innerHTML = '<div class="message">' + this.msg("label.noContent") + '</div>';
         }
         else
         {
            var condition, pluginDescriptor, plugin, messages = [];
            for (var i = 0, il = this.options.pluginConditions.length; i <il ; i++)
            {
               // Test that all conditions are valid
               condition = this.options.pluginConditions[i];
               if (!this.conditionsMatch(condition))
               {
                  continue;
               }

               // Conditions are valid, now create plugins and make sure they can run in this environment
               for (var pi = 0, pil = condition.plugins.length; pi < pil; pi++)
               {
                  pluginDescriptor = condition.plugins[pi];
                  // Check the plugin constructor actually exists, in case client-side dependencies
                  // have not been loaded (ALF-12798)
                  if (typeof Alfresco.WebPreview.prototype.Plugins[pluginDescriptor.name] == "function")
                  {
                     // Create plugin
                     plugin = new Alfresco.WebPreview.prototype.Plugins[pluginDescriptor.name](this, pluginDescriptor.attributes);

                     // Special case to ignore the WebPreviewer plugin on iOS - we don't want to report output either
                     // as the output is simply an HTML message unhelpfully informing the user to install Adobe Flash
                     if (YAHOO.env.ua.ios && pluginDescriptor.name === "WebPreviewer")
                     {
                        continue;
                     }

                     // Make sure it may run in this browser...
                     var report = plugin.report();
                     if (report)
                     {
                        // ...the plugin can't be used in this browser, save report and try another plugin
                        messages.push(report);
                     }
                     else
                     {
                        // ...yes, the plugin can be used in this browser, lets store a reference to it.
                        this.plugin = plugin;

                        // Ask the plugin to display the node
                        var markup;
                        try
                        {
                           Dom.addClass(this.widgets.previewerElement, pluginDescriptor.name);
                           markup = plugin.display();
                           if (markup)
                           {
                              // Insert markup if plugin provided it
                              this.widgets.previewerElement.innerHTML = markup;
                           }

                           // Finally! We found a plugin that works and didn't crash
                           YAHOO.Bubbling.fire('webPreviewSetupComplete');
                           return;
                        }
                        catch(e)
                        {
                           // Oops a plugin failure, log it and try the next one instead...
                           Alfresco.logger.error('Error, Alfresco.WebPreview.Plugins.' + pluginDescriptor.name + ' failed to display: ' + e);
                           messages.push(this.msg("label.error", pluginDescriptor.name, e.message));                        
                        }
                     }
                  }
                  else
                  {
                     // Plugin could not be instantiated, log it and try the next one instead...
                     Alfresco.logger.error('Error, Alfresco.WebPreview.Plugins.' + pluginDescriptor.name + ' does not exist');
                     messages.push(this.msg("label.errorMissing", pluginDescriptor.name));
                  }
               }
            }

            // Tell user that the content can't be displayed
            var noPreviewLabel = "label.noPreview";
            if (YAHOO.env.ua.ios)
            {
               noPreviewLabel = "label.noPreview.ios";
            }
            var message = this.msg(noPreviewLabel, this.getContentUrl(true));
            for (i = 0, il = messages.length; i < il; i++)
            {
               message += '<br/>' + messages[i];
            }
            this.widgets.previewerElement.innerHTML = '<div class="message">' + message + '</div>';
         }
      },

      /**
       * MNT-9235: Handles all the 'onPreviewChangedEvent' events
       * 
       * @method onPreviewChanged
       * @private
       */
      onPreviewChanged: function WebPreview_onPreviewChanged(event)
      {
         this.options.avoidCachedThumbnail = true;

         YAHOO.util.Event.preventDefault(event);
         YAHOO.util.Event.stopPropagation(event)
      },

      /**
       * Checks if the conditions are fulfilled.
       *
       * @method conditionsMatch
       * @param condition {Object} The condition to match gainst this components options
       * @return true of conditions are fulfilled for plugins to be used.
       * @public
       */
      conditionsMatch: function WP_conditionsMatch(condition)
      {
         if (condition.attributes.mimeType && condition.attributes.mimeType != this.options.mimeType)
         {
            return false;
         }
         if (condition.attributes.thumbnail && !Alfresco.util.arrayContains(this.options.thumbnails, condition.attributes.thumbnail))
         {
            return false;
         }
         return true;
      },

      /**
       * Helper method for plugins to create url tp the node's content.
       *
       * @method getContentUrl
       * @param {Boolean} (Optional) Default false. Set to true if the url shall be constructed so it forces the
       *        browser to download the document, rather than displaying it inside the browser. 
       * @return {String} The "main" element holding the actual previewer.
       * @public
       */
      getContentUrl: function WP_getContentUrl(download)
      {
         var proxy = window.location.protocol + "//" + window.location.host + Alfresco.constants.URL_CONTEXT + "proxy/" + this.options.proxy + "/",
            nodeRefAsLink = this.options.nodeRef.replace(":/", ""),
            noCache = "noCache=" + new Date().getTime();
         download = download ? "a=true" : "a=false";
         return proxy + this.options.api + "/node/" + nodeRefAsLink + "/content/" + encodeURIComponent(this.options.name) + "?c=force&" + noCache + "&" + download;
      },

      /**
       * Helper method for plugins to create a url to the thumbnail's content.
       *
       * @param thumbnail {String} The thumbnail definition name
       * @param fileSuffix {String} (Optional) I.e. ".png" if shall be inserted in the url to make certain flash
       *        plugins understand the mimetype of the thumbnail.
       * @return {String} The url to the thumbnail content.
       * @public
       */
      getThumbnailUrl: function WP_getThumbnailUrl(thumbnail, fileSuffix)
      {
         var proxy = window.location.protocol + "//" + window.location.host + Alfresco.constants.URL_CONTEXT + "proxy/" + this.options.proxy + "/",
            nodeRefAsLink = this.options.nodeRef.replace(":/", ""),
            noCache = "noCache=" + new Date().getTime(),
            force = "c=force";
         
         // Check to see if last modification data is available for the thumbnail...
         for (var i = 0; i < this.options.thumbnailModification.length; i++)
         {
            if (this.options.thumbnailModification[i].indexOf(thumbnail) != -1)
            {
               var timestampPostfix = noCache;

               noCache = "lastModified=" + encodeURIComponent(this.options.thumbnailModification[i]);

               // MNT-9235: Avoiding loading content of thumbnail from the cache
               // if current node is updated without reloading of the page
               if (this.options.avoidCachedThumbnail)
               {
                  noCache += "&" + timestampPostfix;

                  // Resetting to 'false' since thumbnail will be eventually updated...
                  this.options.avoidCachedThumbnail = false;
               }

               break;
            }
         }
         return proxy + this.options.api + "/node/" + nodeRefAsLink + "/content/thumbnails/" + thumbnail + (fileSuffix ? "/suffix" + fileSuffix : "") + "?" + force + "&" + noCache
      },

      /**
       * Makes it possible for plugins to get hold of the "previewer wrapper" HTMLElement.
       *
       * I.e. Useful for elements that use an "absolute" layout for their plugins (most likely flash), so they have
       * an element in the Dom to position their own elements after.
       *
       * @method getPreviewerElement
       * @return {HTMLElement} The "main" element holding the actual previewer.
       * @public
       */
      getPreviewerElement: function()
      {
         return this.widgets.previewerElement;
      },

     /**
       * Refreshes component by metadataRefresh event
       *
       * @method doRefresh
       */
      doRefresh: function WP_doRefresh()
      {
         if (this.plugin)
         {
            this.plugin.display();
         }
      }

   });
})();
