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
 * This is a "Flash" plugin that could be used to display custom flash thumbnail previews.
 *
 * If a custom thumbnail preview has been setup to return a flash representation of the content this plugin could be
 * used to display that. If you have a custom thumbnail transformation that results in a flash movie, use a config like
 * below and place it in an overriden web-preview.get.config.xml file.
 *
 * <condition thumbnail="{name-of-custom-flashpreview}">
 *    <plugin src="{name-of-custom-flashpreview}" version="{flash-player-version-required-to-view-custom-flashpreview}">Flash</plugin>
 * </condition>
 *
 *
 * NOTE!
 *
 * The proxy does NOT allow content of mime-type "application/x-shockwave-flash" to be streamed from the
 * "repository's content webscript" for security concerns. If however an Alfresco installation is customized to allow
 * Flash content to be streamed, something which NOT IS RECOMMENDED OR SUPPORTED BY ALFRESCO, it could be used to
 * display that content as well by using a config like below.
 *
 * <condition mimeType="application/x-shockwave-flash">
 *    <plugin version="{flash-play-version-that-you-trust-at-your-own-risk}">Flash</plugin>
 * </condition>
 *
 * @param wp {Alfresco.WebPreview} The Alfresco.WebPreview instance that decides which plugin to use
 * @param attributes {Object} Arbitrary attributes brought in from the <plugin> element
 */
Alfresco.WebPreview.prototype.Plugins.Flash = function(wp, attributes)
{
   this.wp = wp;
   this.attributes = YAHOO.lang.merge(Alfresco.util.deepCopy(this.attributes), attributes);
   this.swfDiv = null;
   return this;
};

Alfresco.WebPreview.prototype.Plugins.Flash.prototype =
{
   /**
    * Attributes
    */
   attributes:
   {
      /**
       * Decides if the node's content or one of its thumbnails shall be displayed.
       * Leave it as it is if the node's content shall be used (NOT RECOMMENDED OR SUPPORTED OOTB).
       * Set to a custom thumbnail definition name if the node's thumbnail contains the flash movie to display (RECOMMENDED).
       *
       * @property src
       * @type String
       * @default null
       */
      src: null,

      /**
       * The Flash Player version the user must have installed to view the flash movie, this should be based on the
       * result of a custom flash previewer thumbnail (if such has been configured on the repository).
       *
       * @required
       * @property src
       * @type String
       */
      version: null
   },

   /**
    * Tests if the plugin can be used in the users browser.
    *
    * @method report
    * @return {String} Returns nothing if the plugin may be used, otherwise returns a message containing the reason
    *         it cant be used as a string.
    * @public
    */
   report: function Flash_report()
   {
      var versionNumbers = this.attributes.version ? this.attributes.version.split(".") : null;
      if (!versionNumbers || !versionNumbers.length == 3 || !Alfresco.util.hasRequiredFlashPlayer.apply(Alfresco.util, versionNumbers))
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
   display: function Flash_display()
   {
      var url = this.attributes.src ? this.wp.getThumbnailUrl(this.attributes.src) : this.wp.getContentUrl();

      // Embed flash movie by using swfobject
      var swfId = "Flash_" + this.wp.id;
      var so = new YAHOO.deconcept.SWFObject(url, swfId, "100%", "100%", this.attributes.version);
      so.addParam("allowScriptAccess", "never");
      so.addParam("allowNetworking", "none");
      so.addParam("allowFullScreen", "false");
      so.addParam("wmode", "opaque");
      so.write(this.wp.widgets.previewerElement.id);
   }
};