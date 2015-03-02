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
 * RulesHeader template.
 *
 * @namespace Alfresco
 * @class Alfresco.RulesHeader
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Selector = YAHOO.util.Selector,
      Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $siteURL = Alfresco.util.siteURL;

   /**
    * RulesHeader constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RulesHeader} The new RulesHeader instance
    * @constructor
    */
   Alfresco.RulesHeader = function RulesHeader_constructor(htmlId)
   {
      Alfresco.RulesHeader.superclass.constructor.call(this, "Alfresco.RulesHeader", htmlId, ["button"]);

      /* Decoupled event listeners */
      YAHOO.Bubbling.on("folderDetailsAvailable", this.onFolderDetailsAvailable, this);
      YAHOO.Bubbling.on("folderRulesetDetailsAvailable", this.onFolderRulesetDetailsAvailable, this);
      YAHOO.Bubbling.on("inheritChange", this.onInheritChange, this, this.options.inheritRules);

      return this;
   };

   YAHOO.extend(Alfresco.RulesHeader, Alfresco.component.Base,
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
          * The nodeRef of the folder being viewed
          *
          * @property nodeRef
          * @type Alfresco.util.NodeRef
          */
         nodeRef: null,

         /**
          * Current siteId.
          *
          * @property siteId
          * @type string
          */
         siteId: "",

         /**
          * Are rules inherited from parent?
          *
          * @property inheritRules
          * @type Boolean
          */
         inheritRules: true
      },

      /**
       * Flag set after component is instantiated.
       *
       * @property isReady
       * @type {boolean}
       */
      isReady: false,

      /**
       * The folders name
       *
       * @property folderDetails
       * @type {string}
       */
      folderDetails: null,

      /**
       * The inherit and folder rules for the folder
       *
       * @property ruleset
       * @type {object}
       */
      ruleset: null,

      /**
       * Keeps track if this component is running rules or not
       *
       * @property isRunning
       * @type Boolean
       */
      isRunning: false,

      /**
       * Fired by YUI when parent element is available for scripting.
       * Template initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function RulesHeader_onReady()
      {
         // Save references to dom objects
         this.widgets.actionsEl = Dom.get(this.id + "-actions");
         this.widgets.titleEl = Dom.get(this.id + "-title");

         // Create buttons
         this.widgets.newRuleButton = Alfresco.util.createYUIButton(this, "newRule-button", this.onNewRuleButtonClick);
         this.widgets.copyRuleFromButton = Alfresco.util.createYUIButton(this, "copyRuleFrom-button", this.onCopyRuleFromButtonClick);
         this.widgets.runRulesMenu = Alfresco.util.createYUIButton(this, "runRules-menu", this.onRunRulesMenuSelect,
         {
            type: "menu",
            menu: "runRules-options"
         });

         this.widgets.inheritRulesToggleButton = Alfresco.util.createYUIButton(this, "inheritButton", this.onInheritToggleClick);

         // Display folder name & appropriate actions if info has been given
         this.isReady = true;
         this._displayDetails();

         // TODO:
         // set state correctly for button initially
         // other UI candy

      },

      /**
       * Event handler called when the "folderDetailsAvailable" event is received
       *
       * @method onFolderDetailsAvailable
       * @param layer
       * @param args
       */
      onFolderDetailsAvailable: function RulesHeader_onFolderDetailsAvailable(layer, args)
      {
         this.folderDetails = args[1].folderDetails;
         this._displayDetails();
      },

      /**
       * Event handler called when the "folderRulesDetailsAvailable" event is received
       *
       * @method onFolderRulesetDetailsAvailable
       * @param layer
       * @param args
       */
      onFolderRulesetDetailsAvailable: function RulesHeader_onFolderRulesetDetailsAvailable(layer, args)
      {
         this.ruleset = args[1].folderRulesetDetails;
         this._displayDetails();
      },

      /**
       * Called when user clicks on the create rule button.
       * Takes the user to the new rule page.
       *
       * @method onNewRuleButtonClick
       * @param type
       * @param args
       */
      onNewRuleButtonClick: function RulesHeader_onNewRuleButtonClick(type, args)
      {
         window.location.href = $siteURL("rule-edit?nodeRef={nodeRef}",
         {
            nodeRef: Alfresco.util.NodeRef(this.options.nodeRef).toString()
         });
      },

      /**
       * Called when user clicks on the copy rule from button.
       * Displays a rule folder dialog.
       *
       * @method onCopyRuleFromButtonClick
       * @param type
       * @param args
       */
      onCopyRuleFromButtonClick: function RulesHeader_onCopyRuleFromButtonClick(type, args)
      {
         if (!this.modules.rulesPicker)
         {
            this.modules.rulesPicker = new Alfresco.module.RulesPicker(this.id + "-rulesPicker");
         }

         this.modules.rulesPicker.setOptions(
         {
            mode: Alfresco.module.RulesPicker.MODE_COPY_FROM,
            siteId: this.options.siteId,
            files:
            {
               displayName: this.folderDetails,
               nodeRef: Alfresco.util.NodeRef(this.options.nodeRef).toString()
            }
         }).showDialog();
      },

      /**
       * Called when an option in the Run Rules menu has been called.
       *
       * @method onRunRulesMenuSelect
       * @param sType {string} Event type, e.g. "click"
       * @param aArgs {array} Arguments array, [0] = DomEvent, [1] = EventTarget
       * @param p_obj {object} Object passed back from subscribe method
       */
      onRunRulesMenuSelect: function RulesHeader_onRunRulesMenuSelect(sType, aArgs, p_obj)
      {
         // Display a wait feedback message if the people hasn't been found yet
         this.widgets.runRulesMenu.set("disabled", true);
         YAHOO.lang.later(2000, this, function()
         {
            if (this.isRunning)
            {
               if (!this.widgets.feedbackMessage)
               {
                  this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.runningRules"),
                     spanClass: "wait",
                     displayTime: 0
                  });
               }
               else if (!this.widgets.feedbackMessage.cfg.getProperty("visible"))
               {
                  this.widgets.feedbackMessage.show();
               }
            }
         }, []);

         var runMode = aArgs[1].value;

         // Run rules for folder (and sub folders)
         if (!this.isRunning)
         {
            this.isRunning = true;

            // Start/stop inherit rules from parent folder
            Alfresco.util.Ajax.jsonPost(
            {
               url: Alfresco.constants.PROXY_URI_RELATIVE + "api/actionQueue",
               dataObj:
               {
                  "actionedUponNode": Alfresco.util.NodeRef(this.options.nodeRef).toString(),
                  "actionDefinitionName": "execute-all-rules",
                  "parameterValues":
                  {
                     "execute-inherited-rules": this.options.inheritRules,
                     "run-all-rules-on-children": (runMode == "run-recursive")
                  }
               },
               successCallback:
               {
                  fn: function(response)
                  {
                     this._enableRunRulesButton();
                     var data = response.json;
                     if (data)
                     {
                        Alfresco.util.PopupManager.displayMessage(
                        {
                           text: this.msg("message.runRules-success")
                        });
                     }
                  },
                  scope: this
               },
               failureCallback:
               {
                  fn: function(response)
                  {
                     this._enableRunRulesButton();
                     Alfresco.util.PopupManager.displayPrompt(
                     {
                        title: this.msg("message.failure"),
                        text: this.msg("message.runRules-failure")
                     });                     
                  },
                  scope: this
               }
            });            
         }

         Event.preventDefault(aArgs[0]);
      },

      onInheritToggleClick: function RulesHeader_onInheritToggleClick()
      {
         var me = this;
         // Start/stop inherit rules from parent folder
         Alfresco.util.Ajax.jsonPost(
            {
               url: Alfresco.constants.PROXY_URI_RELATIVE + "/api/node/" + Alfresco.util.NodeRef(this.options.nodeRef).uri + "/ruleset/inheritrules/toggle",
               successCallback:
               {
                  fn: function(response)
                  {
                     var data = response.json.data;
                     if (data)
                     {
                        me.options.inheritRules = !!((data.inheritRules === "true"));
                        var status = (me.options.inheritRules)? "on": "off";
                        Alfresco.util.PopupManager.displayMessage(
                           {
                              text: this.msg("message.inheritToggle-success-" + status)
                           });
                        YAHOO.Bubbling.fire("inheritChange");
                     }
                  },
                  scope: this
               },
               failureCallback:
               {
                  fn: function(response)
                  {
                     Alfresco.util.PopupManager.displayPrompt(
                        {
                           title: this.msg("message.failure"),
                           text: this.msg("message.inheritToggle-failure")
                        });
                  },
                  scope: this
               }
            });
      },

      onInheritChange: function()
      {
         var elId = this.id + "-inheritButtonContainer",
            onClass = "inherit-on",
            offClass = "inherit-off",
            label = "button.inherit",
            el = Dom.get(this.id + "-inheritButton-button");

         if (this.options.inheritRules)
         {
            Dom.removeClass(elId, offClass);
            Dom.addClass(elId, onClass);
            el.innerHTML = this.msg(label + ".on")
         } else
         {
            Dom.removeClass(elId, onClass);
            Dom.addClass(elId, offClass);
            el.innerHTML = this.msg(label + ".off")
         }
      },

      /**
       * Starts rendering when details has been loaded
       *
       * @method _displayDetails
       */
      _displayDetails: function RulesHeader__displayDetails()
      {
         if (this.isReady && this.ruleset && this.folderDetails)
         {
            // Display actions container
            if (this.ruleset.rules || this.ruleset.linkedToRuleSet)
            {
               Dom.removeClass(this.widgets.actionsEl, "hidden");
            }
            // More actions always appropriate to display
            Dom.removeClass(Dom.get(this.id + "-actions-more"), "hidden");

            // Display file name
            this.widgets.titleEl.innerHTML = $html(this.folderDetails.fileName);
         }
      },

      /**
       * Enable search button, hide the pending wait message and set the panel as not searching.
       *
       * @method _enableRunRulesButton
       * @private
       */
      _enableRunRulesButton: function RulesHeader__enableRunRulesButton()
      {
         // Enable search button and close the wait feedback message if present
         if (this.widgets.feedbackMessage && this.widgets.feedbackMessage.cfg.getProperty("visible"))
         {
            this.widgets.feedbackMessage.hide();
         }
         this.widgets.runRulesMenu.set("disabled", false);
         this.isRunning = false;
      }
   });
})();
