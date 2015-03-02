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
 * This is the "StrobeMediaPlayback" plugin used to display video & play audio.
 *
 * For more information visit:
 * http://sourceforge.net/adobe/smp/wiki/Documentation/
 *
 * @param wp {Alfresco.WebPreview} The Alfresco.WebPreview instance that decides which plugin to use
 * @param attributes {Object} Arbitrary attributes brought in from the <plugin> element
 */
Alfresco.WebPreview.prototype.Plugins.StrobeMediaPlayback = function(wp, attributes)
{
   this.wp = wp;
   this.attributes = YAHOO.lang.merge(Alfresco.util.deepCopy(this.attributes), attributes);
   this.swfDiv = null;
   return this;
};

Alfresco.WebPreview.prototype.Plugins.StrobeMediaPlayback.prototype =
{
   /**
    * Attributes
    */
   attributes:
   {
      /**
       * Decides if the node's content or one of its thumbnails shall be displayed.
       * Leave it as it is if the node's content shall be used.
       * Set to a custom thumbnail definition name if a node thumbnail shall be displayed instead of the content.
       *
       * @property src
       * @type String
       * @default null
       */
      src: null,

      /**
       * Decides if a poster (an image representing the movie) shall be displayed before the movie is loaded or played.
       * Leave it as it is if no poster shall be used.
       * Set to a thumbnail definition name if the node's thumbnail shall be used.
       *
       * Example value: "imgpreview"
       *
       * @property poster
       * @type String
       * @default null
       */
      poster: null,

      /**
       * If a poster is used we must tell StrobeMediaPlayback what type of image it is by appending a file suffix on the url
       * when requesting the poster thumbnail. Must be given if a poster is in use.
       *
       * Example value: ".png"
       *
       * @property posterFileSuffix
       * @type String
       * @default null
       */
      posterFileSuffix: null,

      /**
       * Media stream type
       *
       * Possible values: "letterbox", "none", "stretch", "zoom"
       *
       * @property scaleMode
       * @type String
       * @default "letterbox"
       */
      scaleMode: "letterbox",

      /**
       * Looping behaviour
       *
       * Possible values: "true", "false"
       *
       * @property loop
       * @type String
       * @default "false"
       */
      loop: "false",

      /**
       * Automatic playback
       *
       * Possible values: "true", "false"
       *
       * @property autoPlay
       * @type String
       * @default "false"
       */
      autoPlay: "false",

      /**
       * Play button overlay
       *
       *
       * Possible values: "true", "false"
       *
       * @property playButtonOverlay
       * @type String
       * @default "true"
       */
      playButtonOverlay: "true",

      /**
       * Control bar position
       *
       * Possible values: "docked", "floating", "none"
       *
       * @property controlBarMode
       * @type String
       * @default "docked"
       */
      controlBarMode: "docked",

      /**
       * Control bar visibility
       *
       * Possible values: "true", "false"
       *
       * @property controlBarAutoHide
       * @type String
       * @default "true"
       */
      controlBarAutoHide: "true",

      /**
       * Duration of control bar visibility
       *
       * Number of seconds the control bar is displayed after last user action.
       *
       * @property controlBarAutoHideTimeout
       * @type String
       * @default "3"
       */
      controlBarAutoHideTimeout: "3",

      /**
       * Display buffering indicator
       *
       * Possible values: "true", "false"
       *
       * @property bufferingOverlay
       * @type String
       * @default "true"
       */
      bufferingOverlay: "true",

      /**
       * Muted audio
       *
       * Possible values: "true", "false"
       *
       * @property muted
       * @type String
       * @default "true"
       */
      muted: "false",

      /**
       * Set the volume
       *
       * Possible values: "0" (muted), "0.5" (half muted), "1" (full volume)
       *
       * @property volume
       * @type String
       * @default "1"
       */
      volume: "1",

      /**
       * Set the sound balance
       *
       * Possible values: "-1" (full pan left) to "1" (full pan right). "0" sets both sides to an equal volume.
       *
       * @property audioPan
       * @type String
       * @default "0"
       */
      audioPan: "0"
   },

   /**
    * Tests if the plugin can be used in the users browser.
    *
    * @method report
    * @return {String} Returns nothing if the plugin may be used, otherwise returns a message containing the reason
    *         it cant be used as a string.
    * @public
    */
   report: function StrobeMediaPlayback_report()
   {      
      if (!Alfresco.util.hasRequiredFlashPlayer(10, 0, 0))
      {
         return this.wp.msg("label.noFlash");
      }
   },

   /**
    * Display the node.
    *
    * @method display
    * @public
    */
   display: function StrobeMediaPlayback_display()
   {
      var ctx = this.resolveUrls();

      // Create flash web preview by using swfobject
      var swfId = "StrobeMediaPlayback_" + this.wp.id;
      var so = new YAHOO.deconcept.SWFObject(Alfresco.constants.URL_CONTEXT + "res/components/preview/StrobeMediaPlayback.swf",
            swfId, "100%", "100%", "9.0.45");
      
      so.addVariable("src", ctx.src);
      if (ctx.poster)
      {
         so.addVariable("poster", ctx.poster);
      }
      so.addVariable("loop", this.attributes.loop);
      so.addVariable("autoPlay", this.attributes.autoPlay);
      so.addVariable("playButtonOverlay", this.attributes.playButtonOverlay);
      so.addVariable("controlBarAutoHide", this.attributes.controlBarAutoHide);
      so.addVariable("scaleMode", this.attributes.scaleMode);
      so.addVariable("controlBarMode", this.attributes.controlBarMode);
      so.addVariable("controlBarAutoHideTimeout", this.attributes.controlBarAutoHideTimeout);
      so.addVariable("bufferingOverlay", this.attributes.bufferingOverlay);
      so.addVariable("muted", this.attributes.muted);
      so.addVariable("volume", this.attributes.volume);
      so.addVariable("audioPan", this.attributes.audioPan);

      so.addParam("allowScriptAccess", "sameDomain");
      so.addParam("allowFullScreen", "true");
      so.addParam("wmode", "transparent");

      // Finally create (or recreate) the flash web preview in the new div
      so.write(this.wp.getPreviewerElement().id);
   },

   /**
    * Helper method to get the urls to use depending on the given attributes.
    *
    * @method resolveUrls
    * @return {Object} An object containing urls.
    */
   resolveUrls: function StrobeMediaPlayback_resolveUrls()
   {
      var ctx = {
         src: this.attributes.src ? this.wp.getThumbnailUrl(this.attributes.src) : this.wp.getContentUrl()
      };
      if (this.attributes.poster && this.attributes.poster.length > 0 && this.attributes.posterFileSuffix)
      {
         ctx.poster = this.wp.getThumbnailUrl(this.attributes.poster, this.attributes.posterFileSuffix);
      }
      return ctx;
   }

};