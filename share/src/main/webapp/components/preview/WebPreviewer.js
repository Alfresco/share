/**
 * This is the "WebPreviewer" plugin (one of many plugins to "Alfresco.WebPreview")
 * used to display multi paged documents (i.e. text, word or pdf documents) that has a "webpreview" thumbmail,
 * in other words a .swf movie created by the "pdf2swf" utility.
 *
 * Supports the following thumbnails: "webpreview"
 *
 * @param wp {Alfresco.WebPreview} The Alfresco.WebPreview instance that decides which plugin to use
 * @param attributes {Object} Arbitrary attributes brought in from the <plugin> element
 */
Alfresco.WebPreview.prototype.Plugins.WebPreviewer = function(wp, attributes)
{
   this.wp = wp;
   this.attributes = YAHOO.lang.merge(Alfresco.util.deepCopy(this.attributes), attributes);
   this.swfDiv = null;
   this.fullWindowMode = false;
   return this;
};

Alfresco.WebPreview.prototype.Plugins.WebPreviewer.prototype =
{
   /**
    * Attributes
    */
   attributes:
   {
      src: null,
      paging: "false",

      /**
       * Decides if flash previewers shall disable the i18n input fix all browsers.
       * If it shall be disabled for certain a certain os/browser override the disableI18nInputFix() method.
       *
       * Fix solves the Flash i18n input keyCode bug when "wmode" is set to "transparent"
       * http://bugs.adobe.com/jira/browse/FP-479
       * http://issues.alfresco.com/jira/browse/ALF-1351
       *
       * ...see "Browser Testing" on this page to see supported browser/language combinations for AS2 version
       * http://analogcode.com/p/JSTextReader/
       *
       * ... We are using the AS3 version of the same fix
       * http://blog.madebypi.co.uk/2009/04/21/transparent-flash-text-entry/
       *
       * @property disableI18nInputFix
       * @type boolean
       */
      disableI18nInputFix: "false",
      showFullScreenButton: "true",
      showFullWindowButton: "true"
   },

   /**
    * Reference to the div in which the flash movie is placed.
    *
    * @type HTMLElement
    * @private
    */
   swfDiv: null,

   /**
    * Remember if we are in full window mode or not, if we are we shall not sync position with the previewer placeholder
    *
    * @type Boolean
    * @private
    */
   fullWindowMode: false,

   /**
    * Tests if the plugin can be used in the users browser.
    *
    * @method report
    * @return {String} Returns nothing if the plugin may be used, otherwise returns a message containing the reason
    *         it cant be used as a string.
    * @public
    */
   report: function WebPreviewer_report()
   {
      if (!Alfresco.util.hasRequiredFlashPlayer(9, 0, 124))
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
   display: function WebPreviewer_display()
   {
      var ctx = this.resolveUrls();

      // To support "full window" we create a new div that will float above the rest of the ui
      this.createSwfDiv();

      // Create flash web preview by using swfobject
      // Note! "WebPreviewer_" is used and must match in global js callback methods
      var swfId = "WebPreviewer_" + this.wp.id;
      var so = new YAHOO.deconcept.SWFObject(Alfresco.constants.URL_CONTEXT + "res/components/preview/WebPreviewer.swf",
            swfId, "100%", "100%", "9.0.45");
      so.addVariable("fileName", this.wp.options.name);
      so.addVariable("paging", this.attributes.paging);
      so.addVariable("url", ctx.url);
      so.addVariable("jsCallback", "Alfresco_WebPreview_WebPreviewerPlugin_onWebPreviewerEvent");
      so.addVariable("jsLogger", "Alfresco_WebPreview_WebPreviewerPlugin_onWebPreviewerLogging");
      so.addVariable("i18n_actualSize", this.wp.msg("preview.actualSize"));
      so.addVariable("i18n_fitPage", this.wp.msg("preview.fitPage"));
      so.addVariable("i18n_fitWidth", this.wp.msg("preview.fitWidth"));
      so.addVariable("i18n_fitHeight", this.wp.msg("preview.fitHeight"));
      so.addVariable("i18n_fullscreen", this.wp.msg("preview.fullscreen"));
      so.addVariable("i18n_fullwindow", this.wp.msg("preview.fullwindow"));
      so.addVariable("i18n_fullwindow_escape", this.wp.msg("preview.fullwindowEscape"));
      so.addVariable("i18n_page", this.wp.msg("preview.page"));
      so.addVariable("i18n_pageOf", this.wp.msg("preview.pageOf"));

      so.addVariable("show_fullscreen_button", this.attributes.showFullScreenButton);
      so.addVariable("show_fullwindow_button", this.attributes.showFullWindowButton);
      so.addVariable("disable_i18n_input_fix", this.disableI18nInputFix());

      so.addParam("allowNetworking", "all");
      so.addParam("allowScriptAccess", "sameDomain");
      so.addParam("allowFullScreen", "true");
      so.addParam("wmode", "transparent");

      // Finally create (or recreate) the flash web preview in the new div
      so.write(this.swfDiv.get("id"));

      /**
       * FF3 and SF4 hides the browser cursor if the flashmovie uses a custom cursor
       * when the flash movie is placed/hidden under a div (which is what happens if a dialog
       * is placed on top of the web previewer) so we must turn off custom cursor
       * when the html environment tells us to.
       */
      YAHOO.util.Event.addListener(swfId, "mouseover", function(e)
      {
         var swf = YAHOO.util.Dom.get(swfId);
         if (swf && YAHOO.lang.isFunction(swf.setMode))
         {
            YAHOO.util.Dom.get(swfId).setMode("active");
         }
      });
      YAHOO.util.Event.addListener(swfId, "mouseout", function(e)
      {
         var swf = YAHOO.util.Dom.get(swfId);
         if (swf && YAHOO.lang.isFunction(swf.setMode))
         {
            YAHOO.util.Dom.get(swfId).setMode("inactive");
         }
      });

      // Page unload / unsaved changes behaviour
      YAHOO.util.Event.addListener(window, "resize", function ()
      {
         // Only if not in maximize view
         if (this.swfDiv.getStyle("height") !== "100%")
         {
            this.synchronizeSwfDivPosition();
         }
      }, this, true);

      // Place the real flash preview div on top of the shadow div
      this.synchronizeSwfDivPosition();

      YAHOO.lang.later(500, this, this.synchronizeSwfDivPosition , [] , true);
   },

   /**
    * Helper method to get the urls to use depending on the given attributes.
    *
    * @method resolveUrls
    * @return {Object} An object containing urls.
    */
   resolveUrls: function WebPreviewer_resolveUrls()
   {
      return {
         url: this.attributes.src ? this.wp.getThumbnailUrl(this.attributes.src) : this.wp.getContentUrl()
      };
   },


   /**
    * Called from the WebPreviewer when a log message has been logged.
    *
    * @method onWebPreviewerLogging
    * @param msg {string} The log message
    * @param level {string} The log level
    * @param objectId {string} The id of the embed/object tag that holds WebPreviewer.swf
    */
   onWebPreviewerLogging: function WebPreviewer_onWebPreviewerLogging(msg, level, objectId)
   {
      if (YAHOO.lang.isFunction(Alfresco.logger[level]))
      {
         Alfresco.logger[level].call(Alfresco.logger, "WebPreviewer(" + objectId + "): " + msg);
      }
   },

   /**
    * Called from the WebPreviewer when an event or error is dispatched.
    *
    * @method onWebPreviewerEvent
    * @param event {object} an WebPreview message
    * @param objectId {string} The id of the embed/object tag that holds WebPreviewer.swf
    */
   onWebPreviewerEvent: function WebPreviewer_onWebPreviewerEvent(event, objectId)
   {
      if (event.event)
      {
         if (event.event.type == "onFullWindowClick")
         {
            this.fullWindowMode = true;
            var clientRegion = YAHOO.util.Dom.getClientRegion();
            this.swfDiv.setStyle("left", clientRegion.left + "px");
            this.swfDiv.setStyle("top", clientRegion.top + "px");
            this.swfDiv.setStyle("width", "100%");
            this.swfDiv.setStyle("height", "100%");
         }
         else if (event.event.type == "onFullWindowEscape")
         {
            this.fullWindowMode = false;
            this.synchronizeSwfDivPosition();
         }
      }
      else if (event.error)
      {
         // Inform the user about the failure
         var message = "Error";
         if (event.error.code)
         {
            message = this.wp.msg("error." + event.error.code);
         }
         Alfresco.util.PopupManager.displayMessage(
         {
            text: message
         });
      }
   },

   /**
    *
    * Overriding this method to implement a os/browser version dependent version that decides
    * if the i18n fix described for the disableI18nInputFix option shall be disabled or not.
    *
    * @method disableI18nInputFix
    * @return false
    */
   disableI18nInputFix: function WebPreviewer__resolvePreview(event)
   {
      // Override this method if you want to turn off the fix for a specific client
      return this.attributes.disableI18nInputFix;
   },

   /**
    * To support full window mode an extra div (realSwfDivEl) is created with absolute positioning
    * which will have the same position and dimensions as shadowSfwDivEl.
    * The realSwfDivEl element is to make sure the flash move is on top of all other divs and
    * the shadowSfwDivEl element is to make sure the previewer takes the screen real estate it needs.
    *
    * @method createSwfDiv
    */
   createSwfDiv: function WebPreviewer_createSwfDiv()
   {
      if (!this.swfDiv)
      {
         var realSwfDivEl = new YAHOO.util.Element(document.createElement("div"));
         realSwfDivEl.set("id", this.wp.id + "-full-window-div");
         realSwfDivEl.setStyle("position", "absolute");
         realSwfDivEl.addClass("web-preview");
         realSwfDivEl.addClass("real");
         realSwfDivEl.appendTo(document.body);
         this.swfDiv = realSwfDivEl;
      }
   },

   /**
    * Positions the one element over another
    *
    * @method synchronizePosition
    */
   synchronizeSwfDivPosition: function WebPreviewer_synchronizePosition()
   {
      if (!this.fullWindowMode)
      {
         var sourceYuiEl = new YAHOO.util.Element(this.wp.getPreviewerElement());
         var region = YAHOO.util.Dom.getRegion(sourceYuiEl.get("id"));
         this.swfDiv.setStyle("left", region.left + "px");
         this.swfDiv.setStyle("top", region.top + "px");
         this.swfDiv.setStyle("width", region.width + "px");
         this.swfDiv.setStyle("height", region.height + "px");
      }
   }

};

Alfresco_WebPreview_WebPreviewerPlugin_onWebPreviewerLogging = function Alfresco_WebPreview_WebPreviewerPlugin_onWebPreviewerLogging(msg, level, objectId)
{
   var webPreviewComponentId = objectId.substring("WebPreviewer_".length);
   var webPreviewPlugin = Alfresco.util.ComponentManager.get(webPreviewComponentId).plugin;
   return webPreviewPlugin.onWebPreviewerLogging.apply(webPreviewPlugin, arguments);
};

Alfresco_WebPreview_WebPreviewerPlugin_onWebPreviewerEvent = function Alfresco_WebPreview_WebPreviewerPlugin_onWebPreviewerEvent(event, objectId)
{
   var webPreviewComponentId = objectId.substring("WebPreviewer_".length);
   var webPreviewPlugin = Alfresco.util.ComponentManager.get(webPreviewComponentId).plugin;
   return webPreviewPlugin.onWebPreviewerEvent.apply(webPreviewPlugin, arguments);
};
