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
 * Rule config util
 *
 * @namespace Alfresco
 * @class Alfresco.RuleConfigUtil
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $combine = Alfresco.util.combinePaths;

   /**
    * Alfresco.RuleConfigUtil implementation
    */
   Alfresco.RuleConfigUtil = {};
   Alfresco.RuleConfigUtil.prototype =
   {

      /**
       * Flag to help us add listener for "ruleConfigReady" only once
       *
       * @property ruleConfigs
       * @type boolean
       * @default false
       */
      listeningForReadyRuleConfigs: false,

      /**
       * This is where the rule configs will be stored: type (event), condition (if & unless) & action (action).
       * Will first contain the componentId as key and a false boolean value to indicate that a component is being
       * loaded but hasn't fired its "ruleConfigReady" event. When the config component has fired the event
       * onRuleConfigReady method will replace the boolean value with the component instance to
       * indicate the config component is ready to be used.
       *
       * @property ruleConfigs
       * @type object
       */
      ruleConfigs: {},

      /**
       * Load rule config components and insert them inside this component
       *
       * @method loadRuleConfigs
       * @private
       */
      loadRuleConfigs: function RuleEditUtil_loadRuleConfigs()
      {
         // Make sure we have a hash for storing the rule configs
         if (!this.modules.ruleConfigs)
         {
            this.modules.ruleConfigs = {};
         }

         if (!this.listeningForReadyRuleConfigs)
         {
            // Listen for "ruleConfigReady" events
            YAHOO.Bubbling.on("ruleConfigReady", this.onRuleConfigReady, this);
         }

         this._loadRuleConfigs(
         [
            { component: "components/rules/config/type", name: "ruleConfigType", dataObj: {} },
            { component: "components/rules/config/condition", name: "ruleConfigIfCondition", dataObj: { mode: "if" } },
            { component: "components/rules/config/condition", name: "ruleConfigUnlessCondition", dataObj: { mode: "unless" } },
            { component: "components/rules/config/action", name: "ruleConfigAction", dataObj: {} }
         ]);
      },

      /**
       * Load rule config components and insert them inside this component
       *
       * @method _loadRuleConfigs
       * @param {array} ruleConfigs array with objects describing from where to load the components and where to insert them
       * @private
       */
      _loadRuleConfigs: function RuleEditUtil__loadRuleConfigs(ruleConfigs)
      {
         if (ruleConfigs && ruleConfigs.length > 0)
         {
            var ruleConfig = ruleConfigs[0],
               ruleConfigComponentId = this.id + "-" + ruleConfig.name,
               dataObj = YAHOO.lang.merge(
               {
                  htmlid: ruleConfigComponentId,
                  site: this.options.siteId
               }, ruleConfig.dataObj);
            
            this.ruleConfigs[ruleConfigComponentId] = false;
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + ruleConfig.component,
               dataObj: dataObj,
               successCallback:
               {
                  fn: function (response)
                  {
                     // Insert config components html to this component
                     Dom.get(this.id + "-" + ruleConfig.name).innerHTML = response.serverResponse.responseText;

                     // Get the rest of the configs
                     ruleConfigs.splice(0, 1);
                     this._loadRuleConfigs(ruleConfigs);
                  },
                  scope: this
               },
               execScripts: true
            });
         }
         else
         {
            this.onRuleConfigsLoaded();
         }
      },


      /**
       * Called when a rule config component has sent event telling its ready.
       * This method will display the configs when all 4 of them are ready.
       *
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters
       */
      onRuleConfigReady: function RuleEditUtil_onRuleConfigReady(layer, args)
      {
         // Check the event is directed towards this instance
         var configComponent = args[1].eventGroup,
             configComponentId = configComponent.id;
         if (YAHOO.lang.isBoolean(this.ruleConfigs[configComponentId]))
         {
            // The config component belongs to this component and is ready
            // Save reference to config component instance
            this.ruleConfigs[configComponentId] = configComponent;
         }

         // Check if all config components are ready, if so display the rules rule config sections
         if (!this.ruleConfigs[this.id + "-ruleConfigType"] ||
            !this.ruleConfigs[this.id + "-ruleConfigIfCondition"] ||
            !this.ruleConfigs[this.id + "-ruleConfigUnlessCondition"] ||
            !this.ruleConfigs[this.id + "-ruleConfigAction"])
         {
            // Not all config components are ready
            return;
         }

         // Let superclass
         this.onRuleConfigsReady();
      },

      /**
       * Called then the rule config components have been loaded, inserted has loaded all of their
       * dependecies, and thereofre is ready to use.
       * In other words ready for getting their displayRuleConfigs() invoked.
       *
       * @method onRuleConfigsReady
       */
      onRuleConfigsReady: function RuleEditUtil_onRuleConfigsReady()
      {
         /**
          * Override this method to take appropriate actions when the config has been loaded,
          * inserted to the Dom and has loaded all of it's own dependencies and is ready to
          * get it's displayRuleConfig method invoked.
          */
      },

      /**
       * Called then the rule config components have been loaded and inserted.
       *
       * @method onRuleConfigsLoaded
       */
      onRuleConfigsLoaded: function RuleEditUtil_onRuleConfigsLoaded()
      {
         /**
          * Override this method to take appropriate actions when the config has been loaded,
          * inserted to the Dom and is looking visually ok.
          */
      },

      /**
       * Takes a rule and displays the info inside the rule config components in editable mode
       * if editMode is true.
       *
       * @method displayRuleConfigs
       * @param rule {object} The object describing the rule with the info to display as rule configs
       * @param mode {string}
       * @return {array} the number of configs for each of the for config types.
       */
      displayRuleConfigs: function RuleEditUtil_displayRuleConfigs(rule, mode, form)
      {
         var ruleConfig = null,
            result = [];

         // Transform types into a config object for event section
         var typeConfigs = [];
         for (var i = 0, il = rule.ruleType.length; i < il; i++)
         {
            typeConfigs.push(
            {
               name: rule.ruleType[i]
            });
         }

         // Initialise type config
         ruleConfig = this.ruleConfigs[this.id + "-ruleConfigType"];
         ruleConfig.setOptions(
         {
            siteId: this.options.siteId,
            form: form,
            mode: mode
         });
         ruleConfig.displayRuleConfigs(typeConfigs);
         result.push(typeConfigs.length);

         // Add all conditions to if OR unless config sections
         var ifConditionConfigs = [],
               unlessConditionConfigs = [],
               config;
         for (i = 0, il = rule.action.conditions ? rule.action.conditions.length : 0; i < il; i++)
         {
            config = rule.action.conditions[i];
            if (config.invertCondition)
            {
               unlessConditionConfigs.push(config);
            }
            else
            {
               ifConditionConfigs.push(config);
            }
         }

         // Initialise if condition config
         ruleConfig = this.ruleConfigs[this.id + "-ruleConfigIfCondition"];
         ruleConfig.setOptions(
         {
            siteId: this.options.siteId,
            form: form,
            mode: mode,
            ruleConfigDefinitionKey: "conditionDefinitionName"
         });
         ruleConfig.displayRuleConfigs(ifConditionConfigs);
         result.push(ifConditionConfigs.length);

         // Initialise unless condition config
         ruleConfig = this.ruleConfigs[this.id + "-ruleConfigUnlessCondition"];
         ruleConfig.setOptions(
         {
            siteId: this.options.siteId,
            form: form,
            mode: mode,
            ruleConfigDefinitionKey: "conditionDefinitionName"
         });
         ruleConfig.displayRuleConfigs(unlessConditionConfigs);
         result.push(unlessConditionConfigs.length);

         // Add actions to action section and initilise action config
         ruleConfig = this.ruleConfigs[this.id + "-ruleConfigAction"];
         ruleConfig.setOptions(
         {
            siteId: this.options.siteId,
            form: form,
            mode: mode,
            ruleConfigDefinitionKey: "actionDefinitionName"
         });
         ruleConfig.displayRuleConfigs(rule.action.actions);
         result.push(rule.action.actions.length);

         return result;
      }

   };
})();