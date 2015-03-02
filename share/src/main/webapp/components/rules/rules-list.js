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
 * RulesList component.
 *
 * @namespace Alfresco
 * @class Alfresco.RulesList
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Selector = YAHOO.util.Selector;

   /**
    * Alfresco Slingshot aliases
    */
    var $html = Alfresco.util.encodeHTML,
       $hasEventInterest = Alfresco.util.hasEventInterest,
       $siteURL = Alfresco.util.siteURL;

   /**
    * RulesList constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RulesList} The new RulesList instance
    * @constructor
    */
   Alfresco.RulesList = function RulesList_constructor(htmlId)
   {
      Alfresco.RulesList.superclass.constructor.call(this, "Alfresco.RulesList", htmlId, []);

      // Instance variables
      this.isReady = false;
      this.folderDetails = null;
      this.rules = null;

      // Decoupled event listeners
      YAHOO.Bubbling.on("ruleSelected", this.onRuleSelected, this);
      YAHOO.Bubbling.on("folderDetailsAvailable", this.onFolderDetailsAvailable, this);     
      YAHOO.Bubbling.on("folderRulesetDetailsAvailable", this.onFolderRulesetDetailsAvailable, this);

      return this;
   };

   YAHOO.extend(Alfresco.RulesList, Alfresco.component.Base,
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
          * nodeRef of folder being viewed
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
          * The filter of the rule list.
          * Allowed values are: "all", "folder" or "inherited"
          *
          * @property filter
          * @type string
          */
         filter: "all",

         /**
          * Editable, set to true if the rules order shall be editable by using
          * drag n drop and tab focus and navigation keys.
          *
          * Note! Will only work when folter is set to folder.
          *
          * @property editable
          * @type string
          */
         editable: false,

         /**
          * The filter of the rule list.
          * Allowed values are: "all", "folder" or "inherited"
          *
          * @property selectDefault
          * @type boolean
          */
         selectDefault: false
      },

      /**
       * Flag set after component is instantiated.
       *
       * @property isReady
       * @type boolean
       */
      isReady: false,

      /**
       * Folder from page load.
       *
       * @property folderDetails
       * @type {object}
       */
      folderDetails: null,

      /**
       * Rules on page load.
       *
       * @property rules
       * @type {array}
       */
      rules: null,

      /**
       * Fired by YUI when parent element is available for scripting.
       * Template initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function RulesList_onReady()
      {
         // Get the html elements from the Dom
         this.widgets.rulesListText = Dom.get(this.id + "-rulesListText");
         this.widgets.rulesListBarText = Dom.get(this.id + "-rulesListBarText");
         this.widgets.rulesListContainerEl = Dom.get(this.id + "-rulesListContainer");
         this.widgets.ruleTemplateEl = Selector.query("li", this.id + "-ruleTemplate", true);
         this.widgets.buttonsContainerEl = Dom.get(this.id + "-buttonsContainer");
         this.widgets.saveButton = Alfresco.util.createYUIButton(this, "save-button", this.onSaveButtonClick,
         {
            disabled: true
         });
         this.widgets.resetButton = Alfresco.util.createYUIButton(this, "reset-button", this.onResetButtonClick,
         {
            disabled: true
         });

         Dom.addClass(this.widgets.rulesListContainerEl, this.options.filter);
         
         // Render rules if the info have been given (from an external event)
         this.isReady = true;
         this._displayDetails();
      },

      /**
       * Event handler called when the "ruleSelected" event is received
       *
       * @method onRuleSelected
       * @param layer
       * @param args
       */
      onRuleSelected: function RulesList_onRuleSelected(layer, args)
      {
         var id = args[1].ruleDetails.id;
         if (!Selector.query('input[name=id][value=' + id + ']', this.widgets.rulesListContainerEl, true))
         {
            Alfresco.util.setSelectedClass(this.widgets.rulesListContainerEl);
         }
      },

      /**
       * Event handler called when the "folderDetailsAvailable" event is received
       *
       * @method onFolderDetailsAvailable
       * @param layer
       * @param args
       */
      onFolderDetailsAvailable: function RulesList_onFolderDetailsAvailable(layer, args)
      {
         // Defer if event received before we're ready
         this.folderDetails = args[1].folderDetails;
         this._displayDetails();
      },

      /**
       * Event handler called when the "folderFulesDetailsAvailable" event is received
       *
       * @method onFolderRulesetDetailsAvailable
       * @param layer
       * @param args
       */
      onFolderRulesetDetailsAvailable: function RulesList_onFolderRulesetDetailsAvailable(layer, args)
      {
         this.ruleset = args[1].folderRulesetDetails;
         this._displayDetails();
      },

      /**
       * Called when the rules order has been changed
       * @param action
       * @param draggable
       * @param container
       */
      onDragAndDropAction: function(action, draggable, container)
      {
         if (action == Alfresco.util.DragAndDrop.ACTION_MOVED)
         {
            // Enable button so new order can be stored or restored
            this.widgets.saveButton.set("disabled", false);
            this.widgets.resetButton.set("disabled", false);

            // Change the no label
            var noEls = Selector.query("li .no", this.widgets.rulesListContainerEl);
            for (var i = 0, il = noEls.length; i < il; i++)
            {
               noEls[i].innerHTML = (i + 1) + "";
            }
         }
      },

      /**
       * Called when user clicks the reset button to restore the reoredering.
       *
       * @method onResetButtonClick
       * @param type
       * @param args
       */
      onResetButtonClick: function RulesList_onResetButtonClick(type, args)
      {
         document.location.reload();
         this.widgets.resetButton.set("disabled", true);
         this.widgets.saveButton.set("disabled", true);
      },

      /**
       * Called when user clicks the save button to persist the reoredering.
       *
       * @method onSaveButtonClick
       * @param type
       * @param args
       */
      onSaveButtonClick: function RulesList_onSaveButtonClick(type, args)
      {
         // Disable save button
         this.widgets.saveButton.set("disabled", true);
         this.widgets.resetButton.set("disabled", true);

         // Collect rule ids from hidden input fields
         var rules = [],
            ruleInputs = Selector.query("li input[type=hidden][name=id]", this.widgets.rulesListContainerEl);

         for (var i = 0, il = ruleInputs.length; i < il; i++)
         {
            rules.push(Alfresco.util.NodeRef(this.options.nodeRef).storeType + "://" + Alfresco.util.NodeRef(this.options.nodeRef).storeId + "/" + ruleInputs[i].value);
         }

         // Start/stop inherit rules from parent folder
         Alfresco.util.Ajax.jsonPost(
         {
            url: Alfresco.constants.PROXY_URI_RELATIVE + "api/actionQueue",
            dataObj:
            {
               actionedUponNode : Alfresco.util.NodeRef(this.options.nodeRef).toString(),
               actionDefinitionName: "reorder-rules",
               parameterValues:
               {
                  rules: rules
               }
            },
            successCallback:
            {
               fn: function(response)
               {
                  if (response.json)
                  {
                     // Successfully persisted reorder of folders 
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: this.msg("message.persistRuleorder-success")
                     });

                     this.widgets.saveButton.set("disabled", true);
                  }
               },
               scope: this
            },
            failureCallback:
            {
               fn: function(response)
               {
                  // Display error message and reload
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     title: Alfresco.util.message("message.failure", this.name),
                     text: this.msg("message.persistRuleorder-failure")
                  });
                  this.widgets.resetButton.set("disabled", false);
               },
               scope: this
            }
         });
      },

      /**
       * Renders the details after they have been loaded
       *
       * @method _displayDetails
       * @private
       */
      _displayDetails: function RulesList__displayDetails()
      {
         // Display loaded details
         if (this.isReady && this.ruleset && this.folderDetails)
         {
            this._renderRules();
            this._renderText();
         }
      },

      /**
       * Renders the text above the rules
       *
       * @method _renderText
       * @private
       */
      _renderText: function RulesList__renderText()
      {
         // Set info/message bar
         if (this.options.filter == "inherited")
         {
            this.widgets.rulesListText.innerHTML = this.msg("label.inheritedRules");
            this.widgets.rulesListBarText.innerHTML = this.msg("info.inheritedRulesRunOrder");
         }
         else if (this.options.filter == "folder")
         {
            this.widgets.rulesListText.innerHTML = $html(this.msg("label.folderRules", this.folderDetails.fileName));
            this.widgets.rulesListBarText.innerHTML = this.msg("info.folderRulesRunOrder");
         }
         else if (this.options.filter == "all")
         {
            this.widgets.rulesListText.innerHTML = $html(this.msg("label.allRules", this.folderDetails.fileName));
            if (this.ruleset.linkedFromRuleSets && this.ruleset.linkedFromRuleSets.length > 0)
            {
               this.widgets.rulesListBarText.innerHTML = this.msg("info.folderLinkedFromRuleSets", this.ruleset.linkedFromRuleSets.length);
            }
         }
      },

      /**
       * Renders the rules
       *
       * @method _renderRules
       * @private
       */
      _renderRules: function RulesList__renderRules()
      {
         var rule,
            ruleEl,
            counter = 0,
            ruleset = this.ruleset;

         // Remove all rules
         while(this.widgets.rulesListContainerEl.hasChildNodes())
         {
            this.widgets.rulesListContainerEl.removeChild(this.widgets.rulesListContainerEl.firstChild);
         }

         // Render rules
         var inherited,
            folderRulesStartIndex = ruleset.inheritedRules ? ruleset.inheritedRules.length : 0,
            rules = (ruleset.inheritedRules ? ruleset.inheritedRules : []).concat(ruleset.rules ? ruleset.rules : []);

         for (var i = 0, ii = rules.length; i < ii; i++)
         {
            inherited = (i < folderRulesStartIndex);
            rule = rules[i];
            rule.index = i;
            if ((this.options.filter == "inherited" && inherited) ||
                (this.options.filter == "folder" && !inherited) ||
                this.options.filter == "all")
            {
               counter++;
               ruleEl = this._createRule(rule, inherited, counter);
               ruleEl = this.widgets.rulesListContainerEl.appendChild(ruleEl);
            }

            // Select the first rule as default
            if (counter == 1 && this.options.selectDefault)
            {
               this.onRuleClick(null,
               {
                  rule: rule,
                  ruleEl: ruleEl
               });
            }
         }
         
         // Display message that no rules exist
         if (counter == 0)
         {
            var noRulesDiv = document.createElement("li");
            Dom.addClass(noRulesDiv, "message");
            noRulesDiv.innerHTML = this.msg("message.noRules");
            this.widgets.rulesListContainerEl.appendChild(noRulesDiv);
         }
         else if (this.options.filter == "folder" && this.options.editable)
         {
            // Add drag n drop support
            this.widgets.dnd = new Alfresco.util.DragAndDrop(
            {
               draggables: [
                  {
                     container: this.widgets.rulesListContainerEl,
                     groups: [Alfresco.util.DragAndDrop.GROUP_MOVE],
                     callback: {
                        fn: this.onDragAndDropAction,
                        scope: this
                     },
                     cssClass: "rules-list-item"
                  }
               ],
               targets: [
                  {
                     container: this.widgets.rulesListContainerEl,
                     group: Alfresco.util.DragAndDrop.GROUP_MOVE
                  }
               ]
            });
            Dom.removeClass(this.widgets.buttonsContainerEl, "hidden");
         }
      },
      

      /**
       * Create a rule in the list
       *
       * @method _createRule
       * @param rule {object} The rule info object
       * @param inherited {boolean}
       * @param counter {int}
       * @private
       */
      _createRule: function RulesList__createRule(rule, inherited, counter)
      {
         // Clone template
         var ruleEl = this.widgets.ruleTemplateEl.cloneNode(true);
         Alfresco.util.generateDomId(ruleEl);

         // Rule Id for later submit of reordering
         Dom.getElementsByClassName("id", "input", ruleEl)[0].value = rule.id;

         // Display rest of values
         Dom.getElementsByClassName("no", "span", ruleEl)[0].innerHTML = counter;
         Dom.getElementsByClassName("title", "a", ruleEl)[0].innerHTML = $html(rule.title);
         Dom.getElementsByClassName("description", "span", ruleEl)[0].innerHTML = $html(rule.description);

         var activeIconEl = Dom.getElementsByClassName("active-icon", "span", ruleEl)[0]
         if (rule.disabled)
         {
            activeIconEl.setAttribute("title", this.msg("label.inactive"));
            Dom.addClass(activeIconEl, "disabled");
         }
         else
         {
            activeIconEl.setAttribute("title", this.msg("label.active"));
         }
         if (inherited)
         {
            Dom.getElementsByClassName("inherited", "span", ruleEl)[0].innerHTML = this.msg("label.inheritedShort");
            Dom.getElementsByClassName("inherited-from", "span", ruleEl)[0].innerHTML = this.msg("label.inheritedFrom");

            if (rule.owningNode)
            {
               var a = Dom.getElementsByClassName("inherited-folder", "a", ruleEl)[0],
                  url = $siteURL("folder-rules?nodeRef={nodeRef}",
                  {
                     nodeRef: rule.owningNode.nodeRef
                  });

               a.href = url;
               a.innerHTML = $html(rule.owningNode.name);
               Event.addListener(a, "click", function (e, u)
               {                  
                  window.location.href = u;
               }, url, this);
            }
         }

         // Add listener to clicks on the rule
         Event.addListener(ruleEl, "click", this.onRuleClick,
         {
            rule: rule,
            ruleEl: ruleEl
         }, this);

         return ruleEl;
      },


      /**
       * Called when user clicks on a rule
       *
       * @method onRuleClick
       * @param e click event object
       * @param obj callback object containg rule info & HTMLElements
       */
      onRuleClick: function RulesList_onRuleClick(e, obj)
      {
         Alfresco.util.setSelectedClass(obj.ruleEl.parentNode, obj.ruleEl);

         // Fire event to inform any listening components that the data is ready
         YAHOO.Bubbling.fire("ruleSelected",
         {
            folderDetails: this.folderDetails,
            ruleDetails: obj.rule
         });

         // Stop event if method was called from a user click
         if (e)
         {
            Event.stopEvent(e);
         }
      }

   });
})();
