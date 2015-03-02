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
 * Blog configuration dialog module.
 * 
 * @namespace Alfresco.module
 * @class Alfresco.module.ConfigBlog
 */
(function()
{
   /**
   * YUI Library aliases
   */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event,
       Element = YAHOO.util.Element;

   Alfresco.module.ConfigBlog = function(htmlId)
   {
      this.name = "Alfresco.module.ConfigBlog";
      this.id = htmlId;

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "connection", "json"], this.onComponentsLoaded, this);

      return this;
   };
   
   Alfresco.module.ConfigBlog.prototype =
   {
      /**
       * Object container for initialization options
       */
      options:
      {
         /**
          * ShortName of the site the blog to configure belongs to
          * 
          * @property siteId
          * @type object
          */
         siteId: "",

         /**
          * Name of the container representing the blog
          */
         containerId: "blog",

         /**
          * Width for the dialog
          *
          * @property: width
          * @type: integer
          * @default: 50em
          */
         width: "40em"
      },
      
      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: {},

      /**
       * Object container for storing module instances.
       * 
       * @property modules
       * @type object
       */
      modules: {},

      /**
       * Container element for template in DOM.
       * 
       * @property containerDiv
       * @type DOMElement
       */
      containerDiv: null,

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.module.ConfigBlog} returns 'this' for method chaining
       */
      setOptions: function ConfigBlog_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },

      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.module.ConfigBlog} returns 'this' for method chaining
       */
      setMessages: function ConfigBlog_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function ConfigBlog_onComponentsLoaded()
      {
      },

      /**
       * Main entry point
       * @method showDialog
       */
      showDialog: function ConfigBlog_showDialog()
      {  
         if (!this.containerDiv)
         {
            // Load the UI template from the server
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "modules/blog/config/config-blog",
               dataObj:
               {
                  htmlid: this.id,
                  siteId: this.options.siteId,
                  containerId: this.options.containerId
               },
               successCallback:
               {
                  fn: this.onTemplateLoaded,
                  scope: this
               },
               failureMessage: "Unable to load dialog", // TODO: message bundle won't be available here
               execScripts: true
            });
         }
         else
         {
            // Show the dialog
            this.loadDialogData();
         }
      },

      /**
       * Event callback when dialog template has been loaded
       *
       * @method onTemplateLoaded
       * @param response {object} Server response from load template XHR request
       */
      onTemplateLoaded: function ConfigBlog_onTemplateLoaded(response)
      {
         // Inject the template from the XHR request into a new DIV element
         this.containerDiv = document.createElement("div");
         this.containerDiv.setAttribute("style", "display:none");
         this.containerDiv.innerHTML = response.serverResponse.responseText;

         // The panel is created from the HTML returned in the XHR request, not the container
         var dialogDiv = Dom.getFirstChild(this.containerDiv);
         while (dialogDiv && dialogDiv.tagName.toLowerCase() != "div")
         {
            dialogDiv = Dom.getNextSibling(dialogDiv);
         }
         
         // Create and render the YUI dialog
         this.widgets.dialog = Alfresco.util.createYUIPanel(dialogDiv,
         {
            width: this.options.width
         });
         
         // OK button
         this.widgets.okButton = Alfresco.util.createYUIButton(this, "ok", null,
         {
            type: "submit"
         });

         // Cancel button
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel", this.onCancel);

         // Form definition
         this.modules.form = new Alfresco.forms.Form(this.id + "-form");

         // Validation

         // OK button submits the form
         this.modules.form.setSubmitElements(this.widgets.okButton);

         // JSON submit type
         this.modules.form.setAJAXSubmit(true,
         {
            successMessage: this._msg("message.saveconfig.success"),
            successCallback:
            {
               fn: this.onSuccess,
               scope: this
            },
            failureMessage: this._msg("message.saveconfig.failure")
         });
         this.modules.form.setSubmitAsJSON(true);

         // Show the dialog
         this.loadDialogData();
      },

      /**
       * Loads the configuration data of the blog from the server.
       * @method loadDialogData
       */
      loadDialogData: function ConfigBlog_loadDialogData()
      {
         // Load the current blog configuration data (no blog post content is loaded)
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "api/blog/site/" + this.options.siteId + "/" + this.options.containerId,
            successCallback:
            {
               fn: this.onLoadDialogDataSuccess,
               scope: this
            },
            failureCallback:
            {
               fn: this.onLoadDialogDataFailed,
               scope: this
            }
         });
      },

      /**
       * Load dialog data success handler
       *
       * @method onLoadDialogDataSuccess
       * @param response {object} Server response object
       */
      onLoadDialogDataSuccess: function ConfigBlog_onLoadDialogDataSuccess(response)
      {
         var blogConfigData = response.json.item;

         this._showDialog(blogConfigData);
      },

      /**
       * Load dialog data failure handler
       *
       * @method onLoadDialogDataFailed
       * @param response {object} Server response object
       */
      onLoadDialogDataFailed: function ConfigBlog_onLoadDialogDataFailed(response)
      {
         // Display success message anyway
         Alfresco.util.PopupManager.displayMessage(
         {
            text: this._msg("message.loaddata.failure")
         });
         
         this._hideDialog();
      },

      /**
       * Config blog form submit success handler
       *
       * @method onSuccess
       * @param response {object} Server response object
       */
      onSuccess: function ConfigBlog_onSuccess(response)
      {
         // Fire "blogConfigChanged" event
         YAHOO.Bubbling.fire("blogConfigChanged");

         this._hideDialog();
      },

      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * Dialog Cancel button event handler
       *
       * @method onCancel
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onCancel: function ConfigBlog_onCancel(e, p_obj)
      {
         this._hideDialog();
      },


      /**
       * PRIVATE FUNCTIONS
       */      

      /**
       * Internal show dialog function
       * @method _showDialog
       * @param data blog configuration data to be edited
       */
      _showDialog: function ConfigBlog__showDialog(data)
      {
         // Grab the form element
         var formElement = Dom.get(this.id + "-form");

         // update the form with the passed data
         var option = YAHOO.util.Selector.query("option[value=\"" + data.type + "\"]", this.id + "-blogType")[0];
         if (option)
         {
            Dom.get(this.id + "-blogType").selectedIndex = option.index;
         }
         else
         {
            Dom.get(this.id + "-blogType").selectedIndex = 0;
         }
         Dom.get(this.id + "-blogid").value = data.id ? data.id : "";
         Dom.get(this.id + "-title").value = data.name ? data.name : "";
         Dom.get(this.id + "-description").value = data.description ? data.description : "";
         Dom.get(this.id + "-url").value = data.url ? data.url : "";
         Dom.get(this.id + "-username").value = data.username ? data.username : "";
         Dom.get(this.id + "-password").value = data.password ? data.password : "";

         // Initialise the form
         this.modules.form.init();

         // Show the dialog
         this.widgets.dialog.show();

         // Fix Firefox caret issue
         Alfresco.util.caretFix(this.id + "-form");

         // We're in a popup, so need the tabbing fix
         this.modules.form.applyTabFix();
         
         // Set focus to fileName input
         Dom.get(this.id + "-blogType").focus();
      },

      /**
       * Hide the dialog, removing the caret-fix patch
       *
       * @method _hideDialog
       * @private
       */
      _hideDialog: function ConfigBlog__hideDialog()
      {
         // Grab the form element
         var formElement = Dom.get(this.id + "-form");

         // Undo Firefox caret issue
         Alfresco.util.undoCaretFix(formElement);
         this.widgets.dialog.hide();
      },

      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
       _msg: function ConfigBlog__msg(messageId)
       {
          return Alfresco.util.message.call(this, messageId, this.name, Array.prototype.slice.call(arguments).slice(1));
       }
   };
})();

/* Dummy instance to load optional YUI components early */
new Alfresco.module.ConfigBlog(null);