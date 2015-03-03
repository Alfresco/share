/**
 * This is the "Video" plugin used to display videos in the browser using the <video> element.
 * Since the <video> element accepts videos of different mime type depending on which browser that is used,
 * it is hard to tell if the video will be successfully displayed. 
 *
 * @param wp {Alfresco.WebPreview} The Alfresco.WebPreview instance that decides which plugin to use
 * @param attributes {Object} Arbitrary attributes brought in from the <plugin> element
 */
Alfresco.WebPreview.prototype.Plugins.Video = function(wp, attributes)
{
   this.wp = wp;
   this.attributes = YAHOO.lang.merge(Alfresco.util.deepCopy(this.attributes), attributes);
   return this;
};

Alfresco.WebPreview.prototype.Plugins.Video.prototype =
{
   /**
    * Attributes
    */
   attributes:
   {
      /**
       * The thumbnail to display, If thumbnail isn't specified, the document's content will be displayed.
       * Will create a url to access node's content. If specified will create url based on the thumbnail definition name.
       *
       * @type String
       * @default null
       */
      src: null,

      /**
       * Specify thumbnail's mimeType if src has been set to a thumbnail.
       * will be "null" by default, and the use the node's content's mimeType.
       *
       * @type String
       * @default null
       */
      srcMimeType: null,

      poster: null,

      posterFileSuffix: null
   },

   /**
    * Tests if the plugin can be used in the users browser.
    *
    * @method report
    * @return {String} Returns nothing if the plugin may be used, otherwise returns a message containing the reason
    *         it cant be used as a string.
    * @public
    */
   report: function Video_report()
   {
      // Should ideally use a future proof algorithm for testing if the browsers video element can display video of the current mimetype
      if ((YAHOO.env.ua.ie > 0 && YAHOO.env.ua.ie < 9) || // IE 9
            (YAHOO.env.ua.gecko > 0 && YAHOO.env.ua.gecko < 1.91) || // FireFox 3.5
            (YAHOO.env.ua.webkit > 0 &&  YAHOO.env.ua.webkit < 523.12)) // Safari 3
      {
         // We at least know that the current setups DON'T support the video element
         return this.wp.msg("label.browserReport", "&lt;video&gt;");
      }
   },

   /**
    * Display the node.
    *
    * @method display
    * @public
    */
   display: function Video_display()
   {
      var src = this.attributes.src ? this.wp.getThumbnailUrl(this.attributes.src) : this.wp.getContentUrl(),
         mimeType = this.attributes.srcMimeType ? this.attributes.srcMimeType : this.wp.options.mimeType;
      var str = '';
      str += '<video width="100%" height="100%" controls alt="' + this.wp.options.name  + '" title="' + this.wp.options.name  + '">';
      str += '   <source src="' + src + '"  type=\'' + mimeType + '\'>';
      str += '</video>';
      return str;
   }
};