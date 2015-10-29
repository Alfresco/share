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
 * This is the "Image" plugin used to display images using the <img> element.
 *
 * Supports at least the following mime types: "imge/jpeg", "image/png", "image/gif".
 *
 * @param wp {Alfresco.WebPreview} The Alfresco.WebPreview instance that decides which plugin to use
 * @param attributes {Object} Arbitrary attributes brought in from the <plugin> element
 */
Alfresco.WebPreview.prototype.Plugins.Image = function(wp, attributes)
{
   this.wp = wp;
   this.attributes = YAHOO.lang.merge(Alfresco.util.deepCopy(this.attributes), attributes);
   return this;
};

Alfresco.WebPreview.prototype.Plugins.Image.prototype =
{
   /**
    * Attributes
    */
   attributes:
   {
      /**
       * Decides if the node's content or one of its thumbnails shall be displayed.
       * Leave it as it is if the node's content shall be used.
       * Set to a custom thumbnail definition name if the node's thumbnail contains the image to display.
       *
       * @property src
       * @type String
       * @default null
       */
      src: null,

      /**
       * Maximum size to display given in bytes if the node's content is used.
       * If the node content is larger than this value the image won't be displayed.
       * Note! This doesn't apply if src is set to a thumbnail.
       *
       * @property srcMaxSize
       * @type String
       * @default "2000000"
       */
      srcMaxSize: "2000000"
   },

   /**
    * Tests if the plugin can be used in the users browser.
    *
    * @method report
    * @return {String} Returns nothing if the plugin may be used, otherwise returns a message containing the reason
    *         it cant be used as a string.
    * @public
    */
   report: function Image_report()
   {
      // Report nothing since all browsers support the <img> element  ....well maybe not ascii browsers :-)
   },

   /**
    * Display the node.
    *
    * @method display
    * @public
    */
   display: function Image_display()
   {
      var srcMaxSize = this.attributes.srcMaxSize;
      if (!this.attributes.src && srcMaxSize.match(/^\d+$/) && this.wp.options.size > parseInt(srcMaxSize))
      {
         // The node's content was about to be used and its to big to display
         var msg = '';
         msg += this.wp.msg("Image.tooLargeFile", this.wp.options.name, Alfresco.util.formatFileSize(this.wp.options.size));
         msg += '<br/>';
         msg += '<a class="theme-color-1" href="' + this.wp.getContentUrl(true) + '">';
         msg += this.wp.msg("Image.downloadLargeFile");
         msg += '</a>';
         msg += '<br/>';
         msg += '<a style="cursor: pointer;" class="theme-color-1" onclick="javascript: this.parentNode.parentNode.innerHTML = \'<img src=' + this.wp.getContentUrl(false) + '>\';">';
         msg += this.wp.msg("Image.viewLargeFile");
         msg += '</a>';
         return '<div class="message">' + msg + '</div>';
      }
      else
      {
         var src = this.attributes.src ? this.wp.getThumbnailUrl(this.attributes.src) : this.wp.getContentUrl();

         var image = new Image;
         image.onload = function()
         {
            if ('naturalHeight' in this)
            {
               if (this.naturalHeight + this.naturalWidth === 0)
               {
                  this.onerror();
                  return;
               }
            } else if (this.width + this.height == 0)
            {
               this.onerror();
               return;
            }
            // At this point, there's no error.
            this.wp.widgets.previewerElement.innerHTML = '';
            this.wp.widgets.previewerElement.appendChild(image);
         };
         image.onerror = function()
         {
            //display error
            this.wp.widgets.previewerElement.innerHTML = '<div class="message">'
                  + this.wp.msg("label.noPreview", this.wp.getContentUrl(true))
                  + '</div>';
         };
         image.wp = this.wp;
         image.src = src;

         return null;
      }
   }
};