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
 * RuleEdit component.
 *
 * @namespace Alfresco
 * @class Alfresco.RuleEdit
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
   var $siteURL = Alfresco.util.siteURL;

   /**
    * RuleEdit constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RuleEdit} The new RuleEdit instance
    * @constructor
    */
   Alfresco.RuleEdit = function RuleEdit_constructor(htmlId)
   {
      Alfresco.RuleEdit.superclass.constructor.call(this, "Alfresco.RuleEdit", htmlId, []);

      return this;
   };

   /**
    * Extend from Alfresco.component.Base
    */
   YAHOO.extend(Alfresco.RuleEdit, Alfresco.component.Base);

   /**
    * Augment prototype with Actions module
    */
   YAHOO.lang.augmentProto(Alfresco.RuleEdit, Alfresco.RuleConfigUtil);

   /**
    * Augment prototype with main class implementation, ensuring overwrite is enabled
    */   
   YAHOO.lang.augmentObject(Alfresco.RuleEdit.prototype,
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
          * nodeRef of folder who's rules are being viewed
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
          * The rule constraints that shall be selectable in drop downs for some parameters.
          * Contains the constraint name as attribute keys and an array of constraint values as the attribute value.
          *
          * @property constraints
          * @type object
          */
         constraints: {},

         /**
          * Full info about the rule being edited
          *
          * @property rule
          * @type object
          */
         rule: null,

         /**
          * An object describing an emtpy rule
          *
          * @property emptyRule
          * @type object
          */
         ruleTemplate:
         {
            title: "",
            description: "",
            ruleType: ["inbound"],
            applyToChildren: false,
            executeAsynchronously: false,
            disabled: false,
            action:
            {
               actionDefinitionName: "composite-action",
               actions: [
                  {
                     actionDefinitionName: "",
                     parameterValues: {}
                  }
               ],
               conditions: [
                  {
                     conditionDefinitionName: "",
                     parameterValues: {}
                  }
               ],
               compensatingAction:
               {
                  actionDefinitionName: "script",
                  parameterValues:
                  {
                     "script-ref": ""
                  }
               }
            }
         }
      },

      /**
       * Returns the base ruleTemplate that was passed-in using the options.
       * Suitable for overriding if the ruleTemplate default values need to be changed.
       *
       * @method getRuleTemplate
       */
      getRuleTemplate: function()
      {
         return this.options.ruleTemplate;
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Template initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function RuleEdit_onReady()
      {
         /**
          * Load rule config components.
          * onRuleConfigsLoaded will be called when they have been inserted into the Dom and are visually ok.
          * onRuleConfigsReady will be called when they have loaded their dependencies and are ready to display rules.
          */
         this.loadRuleConfigs();

         // Make sure the compensating script select only is available if rule is run in background
         var asynchronousCheckboxEl = Dom.get(this.id + "-executeAsynchronously");
         Event.addListener(asynchronousCheckboxEl, "click", function(p_oEvent, p_oAsynchronousCheckboxEl)
         {
            this._toggleScriptRef(!p_oAsynchronousCheckboxEl.checked);
         }, asynchronousCheckboxEl, this);

         // Create & Edit menues & buttons
         this.widgets.createButton = Alfresco.util.createYUIButton(this, "create-button", function ()
         {
            this.createAnotherRule = false;
         },
         {
            type: "submit"
         }, this.id + "-create-button");
         this.widgets.createAnotherButton = Alfresco.util.createYUIButton(this, "createAnother-button", function ()
         {
            this.createAnotherRule = true;
         },
         {
            type: "submit"
         }, this.id + "-createAnother-button");
         this.widgets.saveButton = Alfresco.util.createYUIButton(this, "save-button", function ()
         {
            this.createAnotherRule = false;
         },
         {
            type: "submit"
         }, this.id + "-save-button");
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel-button", this.onCancelButtonClick);

         // Setup Form definition
         var form = new Alfresco.forms.Form(this.id + "-rule-form");
         this.widgets.form = form;
         this.widgets.formEl = Dom.get(this.id + "-rule-form");
         form.setSubmitElements([this.widgets.createButton, this.widgets.createAnotherButton, this.widgets.saveButton]);
         form.setSubmitAsJSON(true);
         form.doBeforeFormSubmit =
         {
            fn: function()
            {
               var ruleId = Dom.get(this.id + "-id").value,
                  url = Alfresco.constants.PROXY_URI + "api/node/" + Alfresco.util.NodeRef(this.options.nodeRef).uri + "/ruleset/rules",
                  successCallback,
                  waitMessage;
               
               if (ruleId.length > 0)
               {
                  waitMessage = this.msg("message.updating");
                  this.widgets.formEl.attributes.action.nodeValue = url + "/" + ruleId;
                  this.widgets.form.setAjaxSubmitMethod(Alfresco.util.Ajax.PUT);
                  successCallback =
                  {
                     fn: this.onRuleUpdated,
                     scope: this
                  };
               }
               else
               {
                  waitMessage = this.msg("message.creating");
                  this.widgets.formEl.attributes.action.nodeValue = url;
                  this.widgets.form.setAjaxSubmitMethod(Alfresco.util.Ajax.POST);
                  successCallback =
                  {
                     fn: this.onRuleCreated,
                     scope: this
                  };
               }
               this.widgets.form.setAJAXSubmit(true,
               {
                  successCallback: successCallback,
                  failureCallback:
                  {
                     fn: this.onPersistRuleFailed,
                     scope: this
                  }
               });
               this._toggleButtons(true);
               this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
               {
                  text: waitMessage,
                  spanClass: "wait",
                  displayTime: 0
               });
            },
            obj: null,
            scope: this
         };
         form.doBeforeAjaxRequest =
         {
            fn: function(p_oConfig)
            {
               // Adjust the obj to fit the webscripts
               var rule = p_oConfig.dataObj;
               rule.disabled = Dom.get(this.id + "-disabled").checked;
               rule.applyToChildren = Dom.get(this.id + "-applyToChildren").checked;
               rule.executeAsynchronously = Dom.get(this.id + "-executeAsynchronously").checked;

               rule.ruleType = [];
               var ruleConfigTypes = this.ruleConfigs[this.id + "-ruleConfigType"].getRuleConfigs();
               for (var i = 0, il = ruleConfigTypes.length; i < il; i++)
               {
                  rule.ruleType.push(ruleConfigTypes[i].name);
               }
               rule.action.conditions = this.ruleConfigs[this.id + "-ruleConfigIfCondition"].getRuleConfigs();
               var unless = this.ruleConfigs[this.id + "-ruleConfigUnlessCondition"].getRuleConfigs();
               for (i = 0, il = unless.length; i < il; i++)
               {
                  unless[i].invertCondition = true;
                  rule.action.conditions.push(unless[i]);
               }
               if (rule.action.conditions.length == 0)
               {
                  rule.action.conditions.push(
                  {
                     conditionDefinitionName: "no-condition"
                  });
               }
               rule.action.actions = this.ruleConfigs[this.id + "-ruleConfigAction"].getRuleConfigs();
               if (!rule.action.compensatingAction.parameterValues || 
                   rule.action.compensatingAction.parameterValues["script-ref"].length == 0)
               {
                  // Remove attribute so it doesn't get sent to the server
                  delete rule.action.compensatingAction;
               }
               else if (!rule.action.compensatingAction.id)
               {
                  delete rule.action.compensatingAction.id;
               }
               return true;
            },
            obj: null,
            scope: this
         };

         // Form field validation
         form.addValidation(this.id + "-title", Alfresco.forms.validation.mandatory, null, "keyup");

         // Note: The form will be initialized when the ruleConfig components are ready.
      },

      /**
       * Called then the rule config components have been loaded and inserted to the Dom and are looking visually ok.
       * However they are not ready to use yet since they will need to load their own dependencies.
       *
       * @method onRuleConfigsLoaded
       * @override
       */
      onRuleConfigsLoaded: function RuleEdit_onRuleConfigsLoaded()
      {
         // Remove config loading message and display configs
         Dom.addClass(this.id + "-configsMessage", "hidden");
         Dom.removeClass(this.id + "-configsContainer", "hidden");
      },

      /**
       * Called then the rule config components have been loaded, inserted and are ready to dispaly rule configs
       *
       * @method onRuleConfigsReady
       * @override
       */
      onRuleConfigsReady: function RuleEdit_onRuleConfigsReady()
      {
         // All config components are ready, display rule info
         if (this.options.rule)
         {
            this.displayRule(this.options.rule);
         }
         else
         {
            this.displayRule(this.getRuleTemplate());
         }

         // Finally initialise the form
         this.widgets.form.init();
      },

      /**
       * Display the rule
       *
       * @method displayRule
       * @param rule {object} An object describing the rule
       */
      displayRule: function RuleEdit_displayRule(rule)
      {
         // Set id and hide/show create/edit buttons
         Dom.get(this.id + "-id").value = rule.id ? rule.id : "";
         if (rule.id)
         {
            Dom.removeClass(this.id + "-body", "create-mode");
            Dom.addClass(this.id + "-body", "edit-mode");
         }
         else
         {
            Dom.addClass(this.id + "-body", "create-mode");
            Dom.removeClass(this.id + "-body", "edit-mode");
         }

         // Text fields
         var titleEl = Dom.get(this.id + "-title");
         titleEl.value = rule.title;
         titleEl.focus();
         Dom.get(this.id + "-description").value = rule.description;

         // Display all rule configs (when, if, unless & action)
         this.displayRuleConfigs(rule, Alfresco.RuleConfig.MODE_EDIT, this.widgets.form);

         // Checkboxes
         Dom.get(this.id + "-disabled").checked = rule.disabled;
         Dom.get(this.id + "-applyToChildren").checked = rule.applyToChildren;
         Dom.get(this.id + "-executeAsynchronously").checked = rule.executeAsynchronously;

         // Compensating script
         var scriptRef = Alfresco.util.findValueByDotNotation(rule, "action.compensatingAction.parameterValues.script-ref", null);
         if (scriptRef)
         {
            Alfresco.util.setSelectedIndex(Dom.get(this.id + "-scriptRef"), scriptRef);
         }
         var compensatingActionId = Alfresco.util.findValueByDotNotation(rule, "action.compensatingAction.id", null);
         Dom.get(this.id + "-compensatingActionId").value = compensatingActionId ? compensatingActionId : "";
         this._toggleScriptRef(!rule.executeAsynchronously);
      },

      /**
       * Called when user clicks the cancel button.
       * Takes the user to the folder rules page.
       *
       * @method onCancelButtonClick
       * @param type
       * @param args
       */
      onCancelButtonClick: function RuleEdit_onCancelButtonClick(type, args)
      {
         this._toggleButtons(false);
         this._navigateToFoldersPage();
      },

      /**
       * @method onRuleCreated
       * @param response
       */
      onRuleCreated: function RE_onRuleCreated(response)
      {
         this.widgets.feedbackMessage.hide();
         if (this.createAnotherRule)
         {            
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.createAnotherRule") 
            });
            this.displayRule(this.getRuleTemplate());
            this.widgets.cancelButton.set("disabled", false);
         }
         else
         {
            this._navigateToFoldersPage();
         }         
      },

      /**
       * @method onRuleUpdate
       * @param response
       */
      onRuleUpdated: function RE_onRuleUpdate(response)
      {
         this.widgets.feedbackMessage.hide();
         this._navigateToFoldersPage();
      },

      /**
       * @method onPersistRuleFailed
       * @param response
       */
      onPersistRuleFailed: function RE_onPersistRuleFailed(response)
      {
         this._toggleButtons(false);
         this.widgets.feedbackMessage.hide();
         var title = this.msg("message.failure"),
            text = (response.json && response.json.message) ? response.json.message : this.msg("message.persist-failure");
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: title,
            text: text
         });
      },

      /**
       * Navigate to the main folder rules page
       *
       * @method _navigateToFoldersPage
       * @private
       */
      _navigateToFoldersPage: function RE__navigateToFoldersPage()
      {
         window.location.href = $siteURL("folder-rules?nodeRef={nodeRef}",
         {
            nodeRef: Alfresco.util.NodeRef(this.options.nodeRef).toString()
         });
      },

      /**
       * Toggles disable on buttons
       *
       * @method _toggleButtons
       * @param disable
       */
      _toggleButtons: function RE__toggleButtons(disable)
      {
         this.widgets.cancelButton.set("disabled", disable);
      },

      /**
       * Toggles disable on script loation select
       *
       * @method _toggleButtons
       * @param disable
       */
      _toggleScriptRef: function RE__toggleScriptRef(disable)
      {
         var scriptRefSelect = Dom.get(this.id + "-scriptRef");
         if (disable)
         {
            scriptRefSelect.setAttribute("disabled", true);
         }
         else
         {
            scriptRefSelect.removeAttribute("disabled");
         }
      }
   }, true);
})();
