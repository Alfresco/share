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
 * RevertWikiVersion component.
 *
 * Popups a YUI panel and lets the user choose version and comment for the revert.
 *
 * @namespace Alfresco.module
 * @class Alfresco.module.RevertWikiVersion
 */
(function()
{

   /**
    * RevertWikiVersion constructor.
    *
    * RevertWikiVersion is considered a singleton so constructor should be treated as private,
    * please use Alfresco.module.getRevertWikiVersionInstance() instead.
    *
    * @param {string} htmlId The HTML id of the parent element
    * @return {Alfresco.module.RevertWikiVersion} The new RevertWikiVersion instance
    * @constructor
    * @private
    */
   Alfresco.module.RevertWikiVersion = function(containerId)
   {
      this.name = "Alfresco.module.RevertWikiVersion";
      this.id = containerId;

      var instance = Alfresco.util.ComponentManager.get(this.id);
      if (instance !== null)
      {
         throw new Error("An instance of Alfresco.module.RevertWikiVersion already exists.");
      }

      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      // Load YUI Components
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datatable", "datasource"], this.onComponentsLoaded, this);

      return this;
   }

   Alfresco.module.RevertWikiVersion.prototype =
   {

      /**
       * The default config for the gui state for the revert dialog.
       * The user can override these properties in the show() method.
       *
       * @property defaultShowConfig
       * @type object
       */
      defaultShowConfig:
      {
         siteId: null,
         pageTitle: null,
         version: null,
         versionId: null,
         onRevertWikiVersionComplete: null
      },

      /**
       * The merged result of the defaultShowConfig and the config passed in
       * to the show method.
       *
       * @property defaultShowConfig
       * @type object
       */
      showConfig: {},

      /**
       * Object container for storing YUI widget and HTMLElement instances.
       *
       * @property widgets
       * @type object
       */
      widgets: {},

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function RV_onComponentsLoaded()
      {
         // Shortcut for dummy instance
         if (this.id === null)
         {
            return;
         }
      },

      /**
       * Show can be called multiple times and will display the revert dialog
       * in different ways depending on the config parameter.
       *
       * @method show
       * @param config {object} describes how the revert dialog should be displayed
       * The config object is in the form of:
       * {
       *    nodeRef: {string},  // the nodeRef to revert
       *    version: {string}   // the version to revert nodeRef to
       * }
       */
      show: function RV_show(config)
      {
         // Merge the supplied config with default config and check mandatory properties
         this.showConfig = YAHOO.lang.merge(this.defaultShowConfig, config);
         if (false &&
             this.showConfig.nodeRef === undefined &&
             this.showConfig.version === undefined)
         {
             throw new Error("A nodeRef, filename and version must be provided");
         }
         // Check if the revert dialog has been showed before
         if (this.widgets.panel)
         {
            this._showPanel();
         }
         else
         {
            // If it hasn't load the gui (template) from the server
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "modules/wiki/revert-wiki-version?htmlid=" + this.id,
               successCallback:
               {
                  fn: this.onTemplateLoaded,
                  scope: this
               },
               failureMessage: Alfresco.util.message("message.templateFailure", this.name),
               execScripts: true
            });
         }
      },


      /**
       * Called when the revret dialog html template has been returned from the server.
       * Creates the YIU gui objects such as the panel.
       *
       * @method onTemplateLoaded
       * @param response {object} a Alfresco.util.Ajax.request response object
       */
      onTemplateLoaded: function RV_onTemplateLoaded(response)
      {
         var Dom = YAHOO.util.Dom;

         // Inject the template from the XHR request into a new DIV element
         var containerDiv = document.createElement("div");
         containerDiv.innerHTML = response.serverResponse.responseText;

         // Create the panel from the HTML returned in the server reponse
         var dialogDiv = YAHOO.util.Dom.getFirstChild(containerDiv);
         this.widgets.panel = Alfresco.util.createYUIPanel(dialogDiv);

         // Save a reference to the HTMLElement displaying texts so we can alter the texts later
         this.widgets.promptText = Dom.get(this.id + "-prompt-span");

         // Create and save a reference to the buttons so we can alter them later
         this.widgets.okButton = Alfresco.util.createYUIButton(this, "ok-button", this.onOkButtonClick);
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel-button", this.onCancelButtonClick);         

         // Show panel
         this._showPanel();
      },

      /**
       * Called when the user clicks on the ok button
       *
       * @method onOkButtonClick
       */
      onOkButtonClick: function RevertWikiVersion_onOkButtonClick()
      {
         this.widgets.okButton.set("disabled", true);
         this.widgets.cancelButton.set("disabled", true);

         // Its ok to load the version using the proxy as long as its content isn't inserted into the Dom.
         var actionUrl = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "slingshot/wiki/version/{site}/{title}/{version}",
         {
            site: this.showConfig.siteId,
            title: encodeURIComponent(this.showConfig.pageTitle),
            version: this.showConfig.versionId
         });

		   Alfresco.util.Ajax.request(
			{
				method: Alfresco.util.Ajax.GET,
		      url: actionUrl,
				successCallback:
				{
					fn: this.onVersionContent,
					scope: this
				},
		      failureMessage: "Could not retrieve version content"
		   });

         this.widgets.panel.hide();
         this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: Alfresco.util.message("message.reverting", this.name,
            {
               "0": this.showConfig.pageTitle,
               "1": this.showConfig.version
            }),
            spanClass: "wait",
            displayTime: 0
         });
      },

      /**
       * Called when the content for the version has been loaded.
       * Makes a
       *
       * @method onVersionContent
       */
      onVersionContent: function RevertWikiVersion_onVersionContent(event)
      {
	      // Make a PUT request
         var actionUrl = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "slingshot/wiki/page/{site}/{title}",
         {
            site: this.showConfig.siteId,
            title: encodeURIComponent(this.showConfig.pageTitle)
         });

		   var content = event.serverResponse.responseText;
   		var obj =
   		{
   		   pagecontent: content,
   		   page: "wiki-page",
   		   forceSave: true
   	   };

		   Alfresco.util.Ajax.request(
			{
				method: Alfresco.util.Ajax.PUT,
		      url: actionUrl,
		      dataObj: obj,
		      requestContentType: Alfresco.util.Ajax.JSON,
				successCallback:
				{
					fn: this.onRevertSuccess,
					scope: this
				},
		      failureCallback:
		      {
					fn: this.onRevertFailure,
					scope: this
		      }
		   });
      },

      /**
       * Called when a node has been successfully reverted
       *
       * @method onRevertSuccess
       */
      onRevertSuccess: function RV_onRevertSuccess(response)
      {
         // Hide the current message display
         this.widgets.feedbackMessage.destroy();

         // Tell the document list to refresh itself if present
         YAHOO.Bubbling.fire("wikiVersionReverted",
         {
            siteId: this.showConfig.siteId,
            pageTitle: this.showConfig.pageTitle,
            version: this.showConfig.version,
            versionId: this.showConfig.versionId
         });

         var objComplete =
         {
            successful: [
            {
               siteId: this.showConfig.siteId,
               pageTitle: this.showConfig.pageTitle,
               version: this.showConfig.version,
               versionId: this.showConfig.versionId
            }]
         };

         var callback = this.showConfig.onRevertWikiVersionComplete;
         if (callback && typeof callback.fn == "function")
         {
            // Call the onRevertWikiVersionComplete callback in the correct scope
            callback.fn.call((typeof callback.scope == "object" ? callback.scope : this), objComplete, callback.obj);
         }
      },

      /**
       * Called when a node failed to be reverted
       * Informs the user.
       *
       * @method onRevertFailure
       */
      onRevertFailure: function RV_onRevertFailure(response)
      {
         // Hide the current message display
         this.widgets.feedbackMessage.destroy();

         // Make sure the ok button is enabled for next time
         this.widgets.okButton.set("disabled", false);

         // Inform user that revert was successful
         Alfresco.util.PopupManager.displayMessage(
         {
            text: Alfresco.util.message("message.failure", this.name,
            {
               "0": this.showConfig.pageTitle
            })
         });

      },

      /**
       * Fired when the user clicks the cancel button.
       * Closes the panel.
       *
       * @method onCancelButtonClick
       * @param event {object} a Button "click" event
       */
      onCancelButtonClick: function RV_onCancelButtonClick()
      {
         // Hide the panel
         this.widgets.panel.hide();

         // and make sure ok is enabed next time its showed
         this.widgets.okButton.set("disabled", false);

      },

      /**
       * Adjust the gui according to the config passed into the show method.
       *
       * @method _applyConfig
       * @private
       */
      _applyConfig: function RV__applyConfig()
      {
         var Dom = YAHOO.util.Dom;

         // Set the panel section
         var prompt = Alfresco.util.message("label.prompt", this.name,
         {
            "0": this.showConfig.pageTitle,
            "1": this.showConfig.version
         });
         this.widgets.promptText["innerHTML"] = prompt;

         this.widgets.cancelButton.set("disabled", false);
      },

      /**
       * Prepares the gui and shows the panel.
       *
       * @method _showPanel
       * @private
       */
      _showPanel: function RV__showPanel()
      {
         // Apply the config before it is showed
         this._applyConfig();

         // Show the revert panel
         this.widgets.panel.show();
      }

   };

})();

Alfresco.module.getRevertWikiVersionInstance = function()
{
   var instanceId = "alfresco-revertWikiVersion-instance";
   return Alfresco.util.ComponentManager.get(instanceId) || new Alfresco.module.RevertWikiVersion(instanceId);
}