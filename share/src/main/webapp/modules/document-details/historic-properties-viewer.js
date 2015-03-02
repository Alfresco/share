/**
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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
 * HistoricPropertiesViewer component.
 *
 * Popups a YUI panel and lets the user view the properties (metadata) from previous versions of a document
 *
 * @namespace Alfresco.module
 * @class Alfresco.module.HistoricPropertiesViewer
 */

(function()
{

   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
         Event = YAHOO.util.Event,
         KeyListener = YAHOO.util.KeyListener;

   /**
    * HistoricPropertiesViewer constructor.
    *
    * HistoricPropertiesViewer is considered a singleton so constructor should be treated as private,
    * please use Alfresco.module.getHistoricPropertiesViewerInstance() instead.
    *
    * @param {string} htmlId The HTML id of the parent element
    * @return {Alfresco.module.HistoricPropertiesViewer} The new HistoricPropertiesViewer instance
    * @constructor
    * @private
    */
   Alfresco.module.HistoricPropertiesViewer = function(containerId)
   {
      this.name = "Alfresco.module.HistoricPropertiesViewer";
      this.id = containerId;

      var instance = Alfresco.util.ComponentManager.get(this.id);
      if (instance !== null)
      {
         throw new Error("An instance of Alfresco.module.HistoricPropertiesViewer already exists.");
      }

      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      // Load YUI Components
      Alfresco.util.YUILoaderHelper.require(["button", "container"], this.onComponentsLoaded, this);

      return this;

   };

   Alfresco.module.HistoricPropertiesViewer.prototype =
   {

      /**
       * The default config for the gui state for the historic properties dialog.
       * The user can override these properties in the show() method.
       *
       * @property defaultShowConfig
       * @type object
       */
      defaultShowConfig:
      {
         nodeRef: null,
         filename: null,
         version: null
      },

      /**
       * The merged result of the defaultShowConfig and the config passed in
       * to the show method.
       *
       * @property showConfig
       * @type object
       */
      showConfig: {},

      /**
       *  A local cache of the document version response
       *  this is retrieved during set up & updated on each show
       *
       *  @property versions
       *  @type array
       */
      versions: [],

      /**
       * A reference to the earliest version.
       * Set when creating the dropdown menu
       *
       * @property earliestVersion
       * @type object
       */
      earliestVersion: {},

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
      onComponentsLoaded: function HPV_onComponentsLoaded()
      {
         // Shortcut for dummy instance
         if (this.id === null)
         {
            return;
         }
      },

      /**
       * Show can be called multiple times and will display the dialog
       * in different ways depending on the config parameter.
       *
       * @method show
       * @param config {object} describes how the dialog should be displayed
       * The config object is in the form of:
       * {
       *    nodeRef: {string},  // the nodeRef
       *    version: {string}   // the version to show properties of
       * }
       */
      show: function HPV_show(config)
      {
         // Merge the supplied config with default config and check mandatory properties
         this.showConfig = YAHOO.lang.merge(this.defaultShowConfig, config);
         if (this.showConfig.nodeRef === undefined ||
             this.showConfig.filename === undefined ||
             this.showConfig.currentNodeRef === undefined)
         {
             throw new Error("A nodeRef, filename and version must be provided");
         }

         // Read versions from cache
         var documentVersions = Alfresco.util.ComponentManager.findFirst("Alfresco.DocumentVersions");
         if (documentVersions) {
            this.versions = documentVersions.versionCache;
         }

         // Check if the dialog has been showed before
         if (this.widgets.panel)
         {
            // It'll need updating, probably.
            this.update(this.showConfig.nodeRef);

            // The displaying.
            this._showPanel();
         }
         else
         {
            // If it hasn't load the gui (template) from the server
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "modules/document-details/historic-properties-viewer?nodeRef=" + this.showConfig.currentNodeRef + "&htmlid=" + this.id,
               successCallback:
               {
                  fn: this.onTemplateLoaded,
                  scope: this
               },
               failureMessage: "Could not load html template for properties viewer",
               execScripts: true
            });

            // Register the ESC key to close the dialog
            this.widgets.escapeListener = new KeyListener(document,
            {
               keys: KeyListener.KEY.ESCAPE
            },
            {
               fn: this.onCancelButtonClick,
               scope: this,
               correctScope: true
            });

         }

      },

      /**
       * Called when the dialog html template has been returned from the server.
       * Creates the YIU gui objects such as the panel.
       *
       * @method onTemplateLoaded
       * @param response {object} a Alfresco.util.Ajax.request response object
       */
      onTemplateLoaded: function HPV_onTemplateLoaded(response)
      {

         // Inject the template from the XHR request into a new DIV element
         var containerDiv = document.createElement("div");
         containerDiv.innerHTML = response.serverResponse.responseText;

         var dialogDiv = YAHOO.util.Dom.getFirstChild(containerDiv);

         // Create the panel from the HTML returned in the server reponse
         this.widgets.panel = Alfresco.util.createYUIPanel(dialogDiv);

         // Create menu button:
         this.createMenu(dialogDiv);

         // Save a reference to the HTMLElement displaying texts so we can alter the text later
         this.widgets.headerText = Dom.get(this.id + "-header-span");
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel-button", this.onCancelButtonClick);
         this.widgets.formContainer = Dom.get(this.id + "-properties-form");

         // Set up Nav events:
         navEls = Dom.getElementsByClassName("historic-properties-nav", "a", this.id + "-dialog");
         Event.addListener(navEls[0], "click", this.onNavButtonClick, this, true);
         Event.addListener(navEls[1], "click", this.onNavButtonClick, this, true);
         this.updateNavState();

         // Load Form content
         this.loadProperties();

         // Show panel
         this._showPanel();

      },

      /**
       *
       * Fired when the option in the dropdown version menu is changed
       *
       * @method onVersionMenuChange
       *
       */
      onVersionMenuChange: function HPV_onVersionMenuChange(sType, aArgs, p_obj)
      {
         var domEvent = aArgs[0],
            eventTarget = aArgs[1],
            newNodeRef = eventTarget.value;

         // Update the display:
         this.update(newNodeRef);
      },

      /**
       *
       * This function updates the display, menu and config with a new NodeRef.
       *
       * @method update
       *
       */
      update: function HPV_update(newNodeRef)
      {
         if (newNodeRef) {

            // Update Config Node Ref.
            this.showConfig.nodeRef = newNodeRef;

            // Update the properties display
            this.loadProperties();

            // Update the Menu
            this.setMenuTitle();

            // Update the Navigation
            this.updateNavState();
         }
      },

      /**
       *
       * Determines if the Next and Previous buttons should be enabled.
       * Buttons are disabled by added a disabled class to them
       *
       * @method updateNavState
       *
       */
      updateNavState: function HPV_updateNavState()
      {
         var navEls = Dom.getElementsByClassName("historic-properties-nav", "a", this.id + "-dialog");

         // Start from a known state, default = enabled.
         Dom.removeClass(navEls, "disabled");

         if (this.showConfig.nodeRef === this.earliestVersion.nodeRef)
         {
            // At earliest, so disable the previous button
            Dom.addClass(navEls[0], "disabled");
         }
         else if (this.showConfig.nodeRef === this.showConfig.latestVersion.nodeRef)
         {
            // at latest, so disable the next button.
            Dom.addClass(navEls[1], "disabled");
         }
      },

      /**
       *
       * Instantiates a YUI Menu Button & Writes the Menu HTML for it.
       *
       * @method createMenu
       * @param {HTMLElement} The containing element for the Version History Dialogue
       *
       */

      createMenu: function HPV_createMenu(dialogDiv)
      {
         var menuContainer = Dom.get(this.id + "-versionNav-menu"),
            navContainer = Dom.getElementsByClassName("nav", "div", dialogDiv)[0],
            currentVersionHeader = document.createElement("h6"),
            previousVersionHeader = document.createElement("h6"),
            currentTitle = Alfresco.util.message("historicProperties.menu.title", this.name,
               {
                  "0": this.showConfig.latestVersion.label
               }),
            i, menuHTML = [], menuTitle;


         // Write HTML for menu & add current version and option groups to menu:
         menuHTML.push(
         {
            value: this.showConfig.latestVersion.nodeRef,
            text: currentTitle
         });

         // Add an option element for each of the previous versions
         for (i in this.versions) {
            var version = this.versions[i],
               title = Alfresco.util.message("historicProperties.menu.title", this.name,
                  {
                     "0": version.label
                  });

            // Check if this version is the earliest available
            if (parseInt(i, 10) === this.versions.length - 1)
            {
               this.earliestVersion = version;
            }

            menuHTML.push(
            {
               value: version.nodeRef,
               text: title
            });
            if (version.nodeRef === this.showConfig.nodeRef) {
               menuTitle = title;
            }
         }

         for (var i = 0; i < menuHTML.length; i++)
         {
            var option = document.createElement("option");
            option.text = menuHTML[i].text;
            option.value = menuHTML[i].value;
            menuContainer.add(option);
         }

         // Instantiate the Menu
         this.widgets.versionMenu = new Alfresco.util.createYUIButton(this, "versionNav-button", this.onVersionMenuChange, {
            type: "menu",
            menu: menuContainer,
            lazyloadmenu: false
         });

         // Set the menu title:
         this.setMenuTitle(menuTitle);

         var firstUL = Dom.getElementsByClassName("first-of-type", "ul", navContainer)[0],
         firstLI = Dom.getElementsByClassName("first-of-type", "li", firstUL)[0];

         // Inject item headers
         currentVersionHeader.innerHTML = Alfresco.util.message("historicProperties.menu.current", this.name);
         previousVersionHeader.innerHTML = Alfresco.util.message("historicProperties.menu.previous", this.name);

         Dom.insertBefore(currentVersionHeader, firstLI);
         Dom.insertAfter(previousVersionHeader, firstLI);
      },

      /**
       *
       * @method getVersion
       * @param {string} - either "previous", "next", or a version label
       * @return {string} - nodeRef to the specified version
       */
      getVersionNodeRef: function HPV_getVersionNodeRef(returnLabel)
      {
         var visibleNodeRef = this.showConfig.nodeRef,
            i, returnNodeRef, visibleIndex, returnIndex;

         // Latest version isn't in the versions array, so default visibleIndex to -1 to allow the maths below to work
         visibleIndex = -1;

         // find the index of the version we're showing at the moment:
         for (i in this.versions) {
              if (this.versions[i].nodeRef === visibleNodeRef) {
                 visibleIndex = i;
              }
              // While we're looping through, check to see if we were passed in a label.
              if (this.versions[i].label === returnLabel) {
                 returnIndex = i;
              }
         }

         if (returnLabel === this.showConfig.latestVersion.label) {
            return this.showConfig.latestVersion.nodeRef;
         }
         // NOTE: the versions array has the most recent item first, so to navigate backwards in time, we need to increase the index.
         else if (returnLabel === "next") {
            returnIndex = parseInt(visibleIndex, 10) - 1;
         }
         else if (returnLabel === "previous") {
            returnIndex = parseInt(visibleIndex, 10) + 1
         }

         // Treat current version specially: -1 = current version
         if (returnIndex === -1)
         {
            return this.showConfig.latestVersion.nodeRef;
         }

         returnVersion = this.versions[returnIndex]
         if (typeof(returnVersion) !== "undefined") {
            returnNodeRef = returnVersion.nodeRef;
            return returnNodeRef;
         }
      },

      /**
       * Fired when the user clicks the cancel button.
       * Closes the panel.
       *
       * @method onCancelButtonClick
       * @param event {object} a Button "click" event
       */
      onCancelButtonClick: function HPV_onCancelButtonClick()
      {
         // Hide the panel
         this.widgets.panel.hide();

         // Disable the Esc key listener
         this.widgets.escapeListener.disable();

      },

      /**
       *
       * Triggered by the user clicking on the Next or Previous navigation buttons.
       *
       * @method onNavButtonClick
       *
       */
      onNavButtonClick: function HPV_onNavButtonClick(event, p_obj)
      {
         var target = Event.getTarget(event),
            dir = target.rel,
            newNodeRef = this.getVersionNodeRef(dir);

         if (!Dom.hasClass(target, "disabled"))
         {
            this.update(newNodeRef);
         }
         //prevent the default action.
         Event.preventDefault(event);
      },

      /**
       *
       * Trigger an AJAX request to load the properties via the forms service
       *
       * @method loadProperties
       *
       */
      loadProperties: function HPV_loadProperties(){
         Alfresco.util.Ajax.request(
               {
                  url: Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind=node&itemId=" + this.showConfig.nodeRef + "&mode=view&htmlid=" + this.id,
                  successCallback:
                  {
                     fn: this.onPropertiesLoaded,
                     scope: this
                  },
                  failureMessage: "Could not version properties",
                  execScripts: true
               });
      },

      /**
       *
       * Updates the title on the dropdown box with the current version number.
       *
       * @method setMenuTitle
       */
      setMenuTitle: function HPV_setMenuTitle(title){
         var label, i;

         // If the title hasn't been passed, we'll need to find it from the currentNodeRef.
         if (!title) {
            if (this.showConfig.nodeRef === this.showConfig.latestVersion.nodeRef) {
               label = this.showConfig.latestVersion.label;

               title = Alfresco.util.message("historicProperties.menu.title.latest", this.name,
                     {
                        "0": label
                     });

            }
            else
            {
               for (i in this.versions) {
                  if (this.versions[i].nodeRef === this.showConfig.nodeRef) {
                     label = this.versions[i].label;
                  }
               }

               title = Alfresco.util.message("historicProperties.menu.title", this.name,
                     {
                        "0": label
                     });

            }
         }

         // Set the title.
         this.widgets.versionMenu.set("label", title);

      },

      /**
       *
       * Fired when loadProperties successfully returns
       * Loads the results of the AJAX call into the HTML element we grabbed a reference to earlier
       *
       * @method onPropertiesLoaded
       *
       */
      onPropertiesLoaded: function HPV_onPropertiesLoaded(response){
         this.widgets.formContainer.innerHTML = response.serverResponse.responseText;
         var dateFields = Dom.getElementsByClassName("viewmode-value-date", "span", this.widgets.formContainer);
		 
         for (var i = 0; i < dateFields.length; i++)
         {
            var showTime = Dom.getAttribute(dateFields[i], "data-show-time"),
                fieldValue = Dom.getAttribute(dateFields[i], "data-date-iso8601"),
                ignoreTime = (showTime == 'false'),
                dateFormat = ignoreTime ? Alfresco.util.message("date-format.defaultDateOnly") : Alfresco.util.message("date-format.default"),
                theDate = Alfresco.util.fromISO8601(fieldValue, ignoreTime);
            
            dateFields[i].innerHTML = Alfresco.util.formatDate(theDate, dateFormat);
         }
         
      },

      /**
       * Adjust the gui according to the config passed into the show method.
       *
       * @method _applyConfig
       * @private
       */
      _applyConfig: function HPV__applyConfig()
      {

         // Set the panel section
         var header = Alfresco.util.message("historicProperties.dialogue.header", this.name,
         {
            "0": "<strong>" + this.showConfig.filename + "</strong>"
         });
         this.widgets.headerText["innerHTML"] = header;

         this.widgets.cancelButton.set("disabled", false);
      },

      /**
       * Prepares the gui and shows the panel.
       *
       * @method _showPanel
       * @private
       */
      _showPanel: function HPV__showPanel()
      {

         // Apply the config before it is showed
         this._applyConfig();

         // Enable the Esc key listener
         this.widgets.escapeListener.enable();

         // Show the panel
         this.widgets.panel.show();
      }
   };
})();

Alfresco.module.getHistoricPropertiesViewerInstance = function()
{
   var instanceId = "alfresco-historicPropertiesViewer-instance";
   return Alfresco.util.ComponentManager.get(instanceId) || new Alfresco.module.HistoricPropertiesViewer(instanceId);
}
