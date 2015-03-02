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
 * Alfresco WebView Dashlet
 *
 * @namespace Alfresco.dashlet
 * @class Alfresco.dashlet.WebView
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
   
   Alfresco.dashlet.WebView = function WebView_constructor(htmlId)
   {
      Alfresco.dashlet.WebView.superclass.constructor.call(this, "Alfresco.dashlet.WebView", htmlId);

      // Initialise prototype properties
      this.configDialog = null;

      /**
       * Decoupled event listeners
       */
      YAHOO.Bubbling.on("showPanel", this.onShowPanel, this);
      YAHOO.Bubbling.on("hidePanel", this.onHidePanel, this);

      return this;
   };

   YAHOO.extend(Alfresco.dashlet.WebView, Alfresco.component.Base,
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
          * ComponentId used for saving configuration
          * @property componentId
          * @type string
          */
         componentId: "",
         
         /**
          * URI for the web page to view
          * @property webviewURI
          * @type string
          */
         webviewURI: "",
         
         /**
          * Dashlet title
          * @property webviewTitle
          * @type string
          */
         webviewTitle: "",
         
         /**
          * Default web page
          * @property isDefault
          * @type boolean
          * @default true
          */
         isDefault: true
      },
      
      /**
       * Configuration dialog instance
       *
       * @property configDialog
       * @type object
       */
      configDialog: null,

      /**
       * Fired by YUI when parent element is available for scripting.
       * Initialises components, including YUI widgets.
       *
       * @method onReady
       */
      onReady: function WebView_onReady()
      {
         /**
          * Save reference to iframe wrapper so we can hide and show it depending
          * on how well the browser handles flash movies.
          */
         this.widgets.iframeWrapper = Dom.get(this.id + "-iframeWrapper");
         this.widgets.iframe = Dom.get(this.id + "-iframe");
         this.widgets.iframeTitle = Dom.get(this.id + "-iframe-title");
         this._syncIFrameOptions();
      },

      /**
       * Takes the iframe options (url & title) and reflects their values in the ui.
       *
       * @method _syncIFrameOptions
       * @private
       */
      _syncIFrameOptions: function()
      {
         Dom.removeClass(this.id, "webview-default");
         Dom.removeClass(this.id, "webview-notsecure");
         Dom.removeClass(this.id, "webview-iframe");

         if (this.options.isDefault)
         {
            Dom.addClass(this.id, "webview-default");
         }
         else if (!Alfresco.util.IFramePolicy.isUrlAllowed(this.options.webviewURI))
         {
            Dom.addClass(this.id, "webview-notsecure");
         }
         else
         {
            // Iframe itself
            this.widgets.iframe.src = this.options.webviewURI;

            // Title link
            this.widgets.iframeTitle.href = this.options.webviewURI;

            // Title label
            if (this.options.webviewTitle != "")
            {
               this.widgets.iframeTitle.innerHTML = $html(this.options.webviewTitle);
            }
            else if (!this.options.isDefault)
            {
               this.widgets.iframeTitle.innerHTML = $html(this.options.webviewURI);
            }
            Dom.addClass(this.id, "webview-iframe");
         }
      },

      /**
       * Event listener for configuration link click.
       *
       * @method onConfigWebViewClick
       * @param e {object} HTML event
       */
      onConfigWebViewClick: function WebView_onConfigWebViewClick(e)
      {
         Event.stopEvent(e);
         
         var actionUrl = Alfresco.constants.URL_SERVICECONTEXT + "modules/webview/config/" + encodeURIComponent(this.options.componentId);

         if (!this.configDialog)
         {
            this.configDialog = new Alfresco.module.SimpleDialog(this.id + "-configDialog").setOptions(
            {
               width: "50em",
               templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/webview/config",
               onSuccess:
               {
                  fn: function WebView_onConfigWebView_callback(response)
                  {
                     // MSIE6 doesn't redraw the IFRAME correctly, so tell it to refresh the page
                     if (YAHOO.env.ua.ie === 6)
                     {
                        window.location.reload(true);
                     }
                     else
                     {
                        var data = response.json;
                        this.options.webviewURI = data.uri;
                        this.options.webviewTitle = data.title;
                        this.options.isDefault = false;
                        this._syncIFrameOptions();
                     }
                  },
                  scope: this
               },
               doSetupFormsValidation:
               {
                  fn: function WebView_doSetupForm_callback(form)
                  {
                     form.addValidation(this.configDialog.id + "-url", Alfresco.forms.validation.mandatory, null, "keyup");
                     form.addValidation(this.configDialog.id + "-url", Alfresco.forms.validation.url, null, "keyup", this.msg("Alfresco.forms.validation.url.message"));
                     
                     // 511 characters is the maximum length of URL that IE appears to support without causing a page direct
                     // and preventing the user from returning to their dashboard. To avoid this occurring a check on the length
                     // is set. Rather than just adding this for IE it is added for all browsers because it is possible that
                     // a user could edit the URL on one browser to something greater than 511 characters and then attempt
                     // to view the page in another browser.
                     form.addValidation(this.configDialog.id + "-url", function(field, args, event, form, silent, message)
                     {
                        return (field.value.length < 512);
                     }, null, "keyup");
                     // Check that the url is from a trusted domain
                     form.addValidation(this.configDialog.id + "-url", function(field)
                     {
                        return field.value.length == 0 || Alfresco.util.IFramePolicy.isUrlAllowed(field.value);
                     }, null, "keyup", this.msg("form.url.validation.failure"));

                     /* Get the link title */
                     var elem = Dom.get(this.configDialog.id + "-webviewTitle");
                     if (elem)
                     {
                        elem.value = this.options.webviewTitle;
                     }

                     /* Get the url value */
                     elem = Dom.get(this.configDialog.id + "-url");
                     if (elem)
                     {
                        elem.value = this.options.isDefault ? "" : this.options.webviewURI;
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
      },

      /**
       * Called when any Panel in share created with createYUIPanel is shown.
       * Will hide the content for browsers that can't handle a flash movies properly,
       * since the flash movie could hide parts of the the panel.
       *
       * @method onShowPanel
       * @param p_layer {object} Event fired (unused)
       * @param p_args {array} Event parameters (unused)
       */
      onShowPanel: function WW_onShowPanel(p_layer, p_args)
      {
         if (this._browserDestroysPanel())
         {
            Dom.setStyle(this.widgets.iframeWrapper, "visibility", "hidden");
         }
      },

      /**
       * Called when any Panel in share created with createYUIPanel is hidden.
       * Will display the content again if it was hidden before.
       *
       * @method onHidePanel
       * @param p_layer {object} Event fired (unused)
       * @param p_args {array} Event parameters (unused)
       */
      onHidePanel: function WW_onHidePanel(p_layer, p_args)
      {
         if (this._browserDestroysPanel())
         {
            Dom.setStyle(this.widgets.iframeWrapper, "visibility", "visible");
         }
      },

      /**
       * Returns true if browser will make flash movie hide parts of a panel
       *
       * @method _browserDestroysPanel
       * @return {boolean} True if browser will let flash movie mess up panel
       */
      _browserDestroysPanel: function WW__browserDestroysPanel()
      {
         // All browsers on Windows (tested w FP 10) and FF2 and below on Mac
         return (navigator.userAgent.indexOf("Windows") !== -1 ||
                 (navigator.userAgent.indexOf("Macintosh") !== -1 && YAHOO.env.ua.gecko > 0 && YAHOO.env.ua.gecko < 1.9));
      }
   });
})();