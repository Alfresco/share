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
 * RuleConfigCondition.
 *
 * @namespace Alfresco
 * @class Alfresco.RuleConfigCondition
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
       $hasEventInterest = Alfresco.util.hasEventInterest;
   
   Alfresco.RuleConfigCondition = function(htmlId)
   {
      Alfresco.RuleConfigCondition.superclass.constructor.call(this, htmlId);

      // Re-register with our own name
      this.name = "Alfresco.RuleConfigCondition";
      Alfresco.util.ComponentManager.reregister(this);

      // Instance variables
      this.options = YAHOO.lang.merge(this.options, Alfresco.RuleConfigCondition.superclass.options);
      this.customisations = YAHOO.lang.merge(this.customisations, Alfresco.RuleConfigCondition.superclass.customisations);
      this.renderers = YAHOO.lang.merge(this.renderers, Alfresco.RuleConfigCondition.superclass.renderers);
      this.previousConfigNameSelections = {};

      // Decoupled event listeners
      YAHOO.Bubbling.on("rulePropertySettingsChanged", this.onRulePropertySettingsChanged, this);
      return this;
   };

   YAHOO.extend(Alfresco.RuleConfigCondition, Alfresco.RuleConfig,
   {

      previousConfigNameSelections: {},

      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * The properties to display in the menu
          *
          * @property properties
          * @type {array}
          * @default []
          */
         properties: [],

         /**
          * The transient properties's types to add to "special" properties.
          *
          * @property transientPropertyTypes
          * @type object
          * @default {}
          */
         transientPropertyTypes: {
            "d:content":
            {
               "SIZE" : "d:long",
               "ENCODING" : "d:any",
               "MIME_TYPE" : "d:any"
            }
         },

         /**
          * The config definition that will be used by/specialized by each property
          *
          * @property comparePropertyValueDefinition
          * @type {object}
          * @mandatory
          */
         comparePropertyValueDefinition: {},

         /**
          * The config definition that will be used by/specialized by the mime type property
          *
          * @property compareMimeTypeDefinition
          * @type {object}
          * @mandatory
          */
         compareMimeTypeDefinition: {}
      },


      /**
       * Overriden so we can apply the transient properties
       *
       * @method onReady
       * @override
       */
      onReady: function RuleConfigCondition_onReady()
      {
         if (this.options.properties.length > 0)
         {
            this._handleTransientProperties(this.options.properties);
         }
         return Alfresco.RuleConfigCondition.superclass.onReady.call(this);
      },

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.BlogPostEdit} returns 'this' for method chaining
       * @override
       */
      setOptions: function RuleConfigCondition_setOptions(obj)
      {
         this._handleTransientProperties(this.options.properties);
         return Alfresco.RuleConfigCondition.superclass.setOptions.call(this, obj);
      },


      /**
       * Displays ruleConfig rows as described in ruleConfigs.
       * See parent class for more information.
       *
       * We override since we need to load all property info before we display the rule configs
       *
       * @method displayRuleConfigs
       * @param ruleConfigs {array} An array of rule configurations
       * @override
       */
      displayRuleConfigs: function RuleConfigCondition_displayRulConfigs(ruleConfigs)
      {
         // Find out which properties to load
         var ruleConfig,
            propertyName,
            contentPropertyName,
            properties = this.options.properties,
            propertiesToLoad = [],
            propertyInstructions = {},
            foundProperty,
            foundContentProperty;
         for (var i = 0, il = (ruleConfigs ? ruleConfigs.length : 0); i < il; i++)
         {
            ruleConfig = ruleConfigs[i];
            if (this._isComparePropertyDefinition(ruleConfig[this.options.ruleConfigDefinitionKey]) &&
               ruleConfig.parameterValues)
            {
               // Get the property name and content proeprty name
               propertyName = ruleConfig.parameterValues["property"];
               if (propertyName)
               {
                  if (ruleConfig.parameterValues["content-property"])
                  {
                     contentPropertyName = propertyName + ":" + ruleConfig.parameterValues["content-property"];
                  }
                  else
                  {
                     contentPropertyName = null;
                  }

                  // See if we already have the property in this.options.properties
                  foundProperty = false;
                  foundContentProperty = false;
                  for (var j = 0, jl = properties.length; j < jl; j++)
                  {
                     if (properties[j].name == propertyName)
                     {
                        foundProperty = true;
                     }
                     if (properties[j].name == contentPropertyName)
                     {
                        foundContentProperty = true;
                     }
                  }
                  if (foundContentProperty || (!contentPropertyName && foundProperty))
                  {
                     // ...yes we had it, don't load it again
                  }
                  else
                  {
                     // ... we did NOT have the property, make sure we load it
                     propertiesToLoad.push(propertyName);
                     var instructions = propertyInstructions[propertyName];
                     if (!instructions)
                     {
                        instructions = [];
                        propertyInstructions[propertyName] = instructions;
                     }

                     /**
                      * We can only load "Normal" proeprties, therefore save instructions so we can create a
                      * content proeprty based on the normal property after load
                      */
                     instructions.push(contentPropertyName ? contentPropertyName : propertyName);
                  }
               }
            }
         }
         if (propertiesToLoad.length == 0)
         {
            // No properties shall be loaded, call super class directly
            Alfresco.RuleConfigCondition.superclass.displayRuleConfigs.call(this, ruleConfigs);
         }
         else
         {
            // Load properties and call super class afterwards
            Alfresco.util.Ajax.jsonGet(
            {
               url: Alfresco.constants.PROXY_URI_RELATIVE + "api/properties?name=" + propertiesToLoad.join("&name="),
               successCallback:
               {
                  fn: function(response, propertyInstructions)
                  {
                     var newProperties = response.json,
                        newProperty,
                        instructions,
                        p;
                     for (var pi = 0, pil = newProperties.length; pi < pil; pi++)
                     {
                        newProperty = newProperties[pi];
                        instructions = propertyInstructions[newProperty.name];
                        for (var ii = 0, iil = instructions.length; ii < iil; ii++)
                        {
                           /**
                            * Add the new property and marked it as hidden so it just turns up in menus where
                            * its the actual value when the config row is added
                            */
                           p = Alfresco.util.deepCopy(newProperty);
                           p._hidden = true;
                           if (instructions[ii] != p.name)
                           {
                              // This is a content property make sure it is treated as one
                              p.name = instructions[ii];
                              p = this._handleTransientProperty(p);
                           }
                           this.options.properties.push(p);
                        }
                     }

                     // Re create the menu template
                     this.widgets.selectTemplateEl = this._createSelectMenu();

                     // Finally call super class since we now have the property info we need
                     Alfresco.RuleConfigCondition.superclass.displayRuleConfigs.call(this, ruleConfigs);
                  },
                  obj: propertyInstructions,
                  scope: this
               },
               failureMessage: this.msg("message.getPropertiesFailure")
            });
         }

      },

      /**
       * Called when the user changed what properties to display in the "Show more..." dialog (aka rules picker) 
       *
       * @method onReady
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onRulePropertySettingsChanged: function RuleConfigCondition_onRulePropertySettingsChanged(layer, args)
      {
         // Refresh all menues to hide/remove the property
         var values = args[1];
         if (values)
         {
            var newProperty = this._handleTransientProperty(values.property.item);
            var show = values.state == Alfresco.module.RulesPropertyPicker.PROPERTY_SHOW;
            for (var i = 0, il = this.options.properties.length, property; i < il; i++)
            {
               property = this.options.properties[i];
               if (property.name == newProperty.name)
               {
                  // The propery was found; add or remove it after loop
                  property._hidden = !show;
                  break;
               }
            }
            if (i == il && show)
            {
               // Add the property since it wasn't added before
               this.options.properties.push(newProperty);
            }
            this.widgets.selectTemplateEl = this._createSelectMenu();

            // Look through all menus to and add or remove it if it isn't selected
            var configEls = Selector.query('li.config', this.id + "-body"),
                  configEl,
                  selectEl;
            for (var ci = 0, cil = configEls.length; ci < cil; ci++)
            {
               configEl = configEls[ci];
               selectEl = Selector.query('select', configEl)[0];
               for (var oi = 0, oil = selectEl.options.length, option; oi < oil; oi++)
               {
                  option = selectEl.options[oi];
                  if (this._isComparePropertyDefinition(option.value) && option.getAttribute("rel") == "property_" + newProperty.name)
                  {
                     if (!option.selected)
                     {
                        if (!show)
                        {
                           // Remove the element since it wasn't selected
                           option.parentNode.removeChild(option);
                           this._selectPreviousConfigName(selectEl);
                        }
                     }
                     break;
                  }
               }
               if (show && oi == oil)
               {
                  // The property is currently not in the menu, add it by replacing the old menu with a new one
                  var selectedOption = selectEl.options[selectEl.selectedIndex],
                     selectedConfigName = selectedOption.value,
                     selectedRelPropertyName = selectedOption.getAttribute("rel");
                  var newSelectEl = this.widgets.selectTemplateEl.cloneNode(true);

                  // Since we have created a new select menu, the history of previous selections must taken from the old menu
                  this.previousConfigNameSelections[Alfresco.util.generateDomId(newSelectEl)] = this.previousConfigNameSelections[selectEl.getAttribute("id")];

                  // Remove the old select menu and add the new one
                  selectEl.parentNode.removeChild(selectEl);
                  Event.addListener(newSelectEl, "change", this.onConfigNameSelectChange, configEl, this);
                  Selector.query('div.name', configEl)[0].appendChild(newSelectEl);

                  // Select the previous choice
                  this._selectConfigName(newSelectEl, selectedConfigName, selectedRelPropertyName);
               }
            }
         }
      },

      /**
       * Called to get the config definitions that matches a menu item.
       *
       * @method _getConfigItems
       * @param itemType
       * @param itemPatternObject
       * @param displayHiddenPropertyName {object} Custom parameter
       * @return {array} Menu item objects (as described below) representing a config (or item)
       *                 matching all attributes in itemPatternObject.
       * {
       *    id: string,
       *    label: string,
       *    descriptor: object
       * }
       * @override
       */
      _getConfigItems: function RuleConfigCondition__getConfigItems(itemType, itemPatternObject, displayHiddenPropertyName)
      {
         if (itemType == "property")
         {
            var results = [];
            for (var ci = 0, cil = this.options.properties.length, property; ci < cil; ci++)
            {
               property = this.options.properties[ci];
               if (!property._hidden || displayHiddenPropertyName)
               {
                  if (Alfresco.util.objectMatchesPattern(property, itemPatternObject))
                  {
                     // Create a menu item with a "faked" config definition for the property
                     results.push(this._createPropertyConfigDef(property));
                  }
               }
            }
            return results;
         }
         return Alfresco.RuleConfigCondition.superclass._getConfigItems.call(this, itemType, itemPatternObject);
      },

      /**
       * Called to get the constraint options for a constraint.
       *
       * @method _getConstraintValues
       * @param p_oParamDef
       * @param p_oRuleConfig
       * @return {array} rule constraint values
       * @override
       */
      _getConstraintValues: function RuleConfigCondition__getConstraintValues(p_oParamDef, p_oRuleConfig)
      {
         if (this._isComparePropertyDefinition(p_oRuleConfig[this.options.ruleConfigDefinitionKey]))
         {
            var propertyName = p_oRuleConfig.parameterValues.property,
               contentProperty = p_oRuleConfig.parameterValues["content-property"];
            if (contentProperty)
            {
               propertyName += ":" + contentProperty;
            }
            var propertyType;
            for (var i = 0, il = this.options.properties.length; i < il; i++)
            {
               if (this.options.properties[i].name == propertyName)
               {
                  propertyType = this.options.properties[i].dataType;
                  break;
               }
            }
            p_oParamDef._constraintFilter = propertyType;
         }
         return Alfresco.RuleConfigCondition.superclass._getConstraintValues.call(this, p_oParamDef, p_oRuleConfig);
      },

      /**
       * @method _createPropertyConfigDef
       * @param property {object}
       * @return {object} A configDef based on info from property
       */
      _createPropertyConfigDef: function RuleConfigCondition__createPropertyConfigDef(property)
      {
         var descriptor,
            propertyNameTokens = property.name.split(":");
         if (propertyNameTokens.length == 3)
         {
            // This is a transient property, modify the type
            if (propertyNameTokens[2] == "MIME_TYPE")
            {
               // use the specific mime type comparator
               descriptor = Alfresco.util.deepCopy(this.options.compareMimeTypeDefinition);
               this._getParamDef(descriptor, "property")._type = "hidden";
            }
         }
         if (!descriptor)
         {
            // Use the standard property comparator config definition
            descriptor = Alfresco.util.deepCopy(this.options.comparePropertyValueDefinition);
            this._getParamDef(descriptor, "property")._type = "hidden";
            this._getParamDef(descriptor, "content-property")._type = "hidden";
            this._getParamDef(descriptor, "operation").displayLabel = "";
            this._getParamDef(descriptor, "operation").isMandatory = true;
            this._getParamDef(descriptor, "value").displayLabel = "";
            this._getParamDef(descriptor, "value").type = property.dataType;
            descriptor.parameterDefinitions.reverse();
         }
         if (propertyNameTokens.length == 3 && propertyNameTokens[2] == "SIZE")
         {
            this._getParamDef(descriptor, "value")._unit = this.msg("label.sizeUnit");
         }         

         var propertyConfigDef = {
            id: descriptor.name,
            type: "property_" + property.name,
            label: property.title ? property.title : property.name,
            descriptor: descriptor
         };
         return propertyConfigDef;
      },


      /**
       * Creates a config row with the parameters and the parameter values.
       * Will make sure to create a new selectEl if a proeprty is about to be displayed and
       * that property is marked as hidden.
       *
       * @method _createConfigUI
       * @param p_oRuleConfig {object} Rule config descriptor object
       * @param p_oSelectEl {HTMLSelectElement} (Optional) Will clone the selectTemaplteEl if not provided
       * @param p_eRelativeConfigEl {object} (Optional) will be placed in the end of omitted
       * @return {HTMLElement} The config row
       * @override
       */
      _createConfigUI: function RuleConfig__createConfigUI(p_oRuleConfig, p_oSelectEl, p_eRelativeConfigEl)
      {
         var configDefinitionName = p_oRuleConfig[this.options.ruleConfigDefinitionKey];
         if (this._isComparePropertyDefinition(configDefinitionName) && !p_oSelectEl)
         {
            var isHidden = false,
               properties = this.options.properties,
               property,
               propertyName = p_oRuleConfig.parameterValues["property"],
               contentPropertyName = p_oRuleConfig.parameterValues["content-property"];
            propertyName += contentPropertyName ? ":" + contentPropertyName : "";
            for (var i = 0, il = properties.length; i < il; i++)
            {
               property = properties[i];
               if (property.name == propertyName)
               {
                  isHidden = property._hidden;
                  break;
               }
            }
            if (isHidden)
            {
               p_oSelectEl = this._createSelectMenu(property);
            }
            else
            {
               // Do nothing super class will clone selectTemplateEl if attribute isn't defined
            }
         }
         return Alfresco.RuleConfigCondition.superclass._createConfigUI.call(this, p_oRuleConfig, p_oSelectEl, p_eRelativeConfigEl);
      },            

      /**
       * @method _createRuleConfig
       * @param propertyName {string}
       * @return {object} A ruleConfig based on info from property
       */
      _createRuleConfig: function RuleConfigCondition__createRuleConfig(propertyName)
      {
         var propertyNameTokens = propertyName.split(":"),
            basePropertyName = propertyNameTokens[0] + ":" + propertyNameTokens[1],
            contentProperty = propertyNameTokens.length == 3 ? propertyNameTokens[2] : null;
         var ruleConfig = {
            parameterValues:
            {
               "property": basePropertyName
            }
         };
         ruleConfig[this.options.ruleConfigDefinitionKey] = this.options.comparePropertyValueDefinition.name;
         if (contentProperty)
         {
            ruleConfig.parameterValues["content-property"] = contentProperty;
            if (contentProperty == "MIME_TYPE")
            {
               ruleConfig[this.options.ruleConfigDefinitionKey] = this.options.compareMimeTypeDefinition.name;
            }
         }
         return ruleConfig;
      },

      /**
       * @method onConfigNameSelectChange
       * @param p_oEvent {object} The change event
       * @param p_eConfigEl {HTMLElement} Contains the rule configEl objects
       * @override
       */
      onConfigNameSelectChange: function RuleConfigCondition_onConfigNameSelectChange(p_oEvent, p_eConfigEl)
      {
         // Get or create the list of the 3 last config name selections
         var selectEl = Event.getTarget(p_oEvent),
            optionEl = selectEl.options[selectEl.selectedIndex],
            selectElDomId = selectEl.getAttribute("id");
         if (!selectElDomId)
         {
            selectElDomId = Alfresco.util.generateDomId(selectEl);
         }
         var previousSelections = this.previousConfigNameSelections[selectElDomId];
         if (!previousSelections)
         {
            previousSelections = [{}, {}];
            this.previousConfigNameSelections[selectElDomId] = previousSelections;
         }

         // Move the previous value to the end of the list and store the new selection
         if (previousSelections[0].value != optionEl.value || previousSelections[0].rel != optionEl.getAttribute("rel"))
         {
            previousSelections[1] = previousSelections[0];
            previousSelections[0] = {
               rel: optionEl.getAttribute("rel"),
               value: optionEl.value
            };
         }

         if (this._isComparePropertyDefinition(optionEl.value))
         {
            // Don't call super class since we want to invoke _createConfigParameterUI our selves
            var propertyName = optionEl.getAttribute("rel").substring("property".length + 1),
               ruleConfig = this._createRuleConfig(propertyName);
            this._createConfigParameterUI(ruleConfig, p_eConfigEl);
         }
         else
         {
            // Let super class handle default case
            Alfresco.RuleConfigCondition.superclass.onConfigNameSelectChange.call(this, p_oEvent, p_eConfigEl);
         }
      },

      /**
       * Since all properties share "compare-property-value" as the config type
       * we must make sure the specific property is set in the drop down when the initial value.
       * We do this by looking at the "rel" attribute of the option element were we have combined the
       * normal menu item type (property) with the specific property name.
       *
       *
       * @method _createConfigNameUI
       * @param p_oRuleConfig {object} Rule config descriptor object
       * @param p_oSelectEl {HTMLSelectElement} The select menu to clone and display
       * @param p_eRelativeConfigEl {object}
       * @override
       */
      _createConfigNameUI: function RuleConfigCondition__createConfigNameUI(p_oRuleConfig, p_oSelectEl, p_eRelativeConfigEl)
      {
         // Super class will handle item & conditions....
         var configEl = Alfresco.RuleConfigCondition.superclass._createConfigNameUI.call(this, p_oRuleConfig, p_oSelectEl, p_eRelativeConfigEl);

         // ... but if it is a property we need to re-select the config type to select the specific property
         var configDefinitionName = p_oRuleConfig[this.options.ruleConfigDefinitionKey];
         if (this._isComparePropertyDefinition(configDefinitionName))
         {
            // Find select element and the property parameter
            var propertyName = p_oRuleConfig.parameterValues.property;
            if (configDefinitionName == this.options.compareMimeTypeDefinition.name)
            {
               propertyName += ":" + "MIME_TYPE";
            }
            else if (p_oRuleConfig.parameterValues["content-property"])
            {
               propertyName += ":" + p_oRuleConfig.parameterValues["content-property"];
            }
            this._selectConfigName(p_oSelectEl, configDefinitionName, "property_" + propertyName);
         }

         // Return configEl like super class
         return configEl;
      },

      /**
       * @method _selectConfigName
       * @param p_oSelectEl {HTMLSelectElement} The select element
       * @param p_sConfigDefName {string} The
       * @param p_sRelPropertyName {string} The string "property_" concatenated with the property name/id taken from the data dictionary
       * @private
       */
      _selectConfigName: function RuleConfigCondition__selectConfigName(p_oSelectEl, p_sConfigDefName, p_sRelPropertyName)
      {
         for (var adi = 0, adil = p_oSelectEl.options.length; adi < adil; adi++)
         {
            if (p_oSelectEl.options[adi].value == p_sConfigDefName &&
               (!p_sRelPropertyName || p_oSelectEl.options[adi].getAttribute("rel") == p_sRelPropertyName))
            {
               p_oSelectEl.selectedIndex = adi;
               if (this.options.mode == Alfresco.RuleConfig.MODE_TEXT)
               {
                  // Update the label since we are in text mode
                  var nameEl = p_oSelectEl.parentNode.getElementsByTagName("span")[0];
                  nameEl.innerHTML = $html(p_oSelectEl.options[p_oSelectEl.selectedIndex].text);
               }
               break;
            }
         }
         // Nothing was selected, select the first option instead
         if (adi == adil)
         {
            p_oSelectEl.selectedIndex = 0;
         }
      },

      /**
       * @method _selectPreviousConfigName
       * @param selectEl {HTMLSelectElement} The select element
       * @private
       */
      _selectPreviousConfigName: function RuleConfigCondition__selectPreviousConfigName(selectEl)
      {
         var previousSelections = this.previousConfigNameSelections[selectEl.getAttribute("id")],
               previousSelectedConfigDef = previousSelections && previousSelections.length > 1 ? previousSelections[1] : null;
         if (previousSelectedConfigDef && previousSelectedConfigDef.value)
         {
            this._selectConfigName(selectEl, previousSelectedConfigDef.value, previousSelectedConfigDef.rel);
         }
         else
         {
            // There are no previous selections, select first element
            selectEl.selectedIndex = 0;
         }
      },

      /**
       * @method getConfigCustomisation
       * @param itemType
       * @param configDef
       * @return {object} A RuleConfig parameter renderer
       * @protected
       * @override
       */
      _getConfigCustomisation: function RuleConfig__getConfigCustomisation(itemType, configDef)
      {
         if (this._isComparePropertyDefinition(configDef.name))
         {
            itemType = "property";
         }

         // Super class will handle item & conditions....
         return Alfresco.RuleConfigCondition.superclass._getConfigCustomisation.call(this, itemType, configDef);
      },

      /**
       * @method _isComparePropertyDefinition
       * @param configDefinitionName {string} The config definition name to compare
       * @private
       */
      _isComparePropertyDefinition: function RuleConfigCondition__isComparePropertyDefinition(configDefinitionName)
      {
         return (configDefinitionName == this.options.comparePropertyValueDefinition.name ||
                  configDefinitionName == this.options.compareMimeTypeDefinition.name);
      },

      /**
       * @method _handleTransientProperties
       * @param properties {array} The config definition name to compare
       */
      _handleTransientProperties: function RuleConfigCondition__handleTransientProperties(properties)
      {         
         if (YAHOO.lang.isArray(properties))
         {
            for (var i = 0, il = properties.length; i < il; i++)
            {
               properties[i] = this._handleTransientProperty(properties[i]);
            }
         }
      },

      /**
       * @method _handleTransientProperty
       * @param property {string} The property to modify if its a transient property
       * @protected
       */
      _handleTransientProperty: function RuleConfigCondition__handleTransientProperty(property)
      {
         var propertyNameTokens = property.name.split(":");
         if (!property._transientHandled && propertyNameTokens.length == 3)
         {
            if (!property.title)
            {
               // No proper title find the label from constraints list
               var constraints = [];
               if (property.dataType == "d:content")
               {
                  constraints = this.options.constraints["ac-content-properties"];
               }
               for (var i = 0, il = constraints.length, constraint; i < il; i++)
               {
                  constraint = constraints[i];
                  if (constraint.value == propertyNameTokens[2])
                  {
                     property.title = this.msg("label.transientProperty", constraint.displayLabel, propertyNameTokens[0] + ":" + propertyNameTokens[1]); 
                  }
               }
            }
            if (this.options.transientPropertyTypes[property.dataType])
            {
               property.dataType = this.options.transientPropertyTypes[property.dataType][propertyNameTokens[2]];
            }
            property._transientHandled = true;
         }
         return property;
      },

      /**
       * CUSTOMISATIONS
       */

      customisations:
      {

         /**
          * Has aspect
          */
         HasAspect:
         {
            edit: function(configDef, ruleConfig, configEl)
            {
               // Limit the available types to the ones specified in share-config.xml
               var ad = this._getParamDef(configDef, "aspect");
               ad._constraintFilter = "visible";
               ad.displayLabel = null;
               return configDef;
            }
         },

         /**
          * Is sub type
          */
         IsSubType:
         {
            edit: function(configDef, ruleConfig, configEl)
            {
               // Limit the available types to the ones specified in share-config.xml
               var td = this._getParamDef(configDef, "type");
               td._constraintFilter = "visible";
               td.displayLabel = this.msg("label.is");
               td._hideColon = true;
               return configDef;
            }
         },

         /**
          * Category picker
          */
         InCategory:
         {
            text: function(configDef, ruleConfig, configEl)
            {
               this._getParamDef(configDef, "category-value")._type = "category";
               return configDef;
            },
            edit: function(configDef, ruleConfig, configEl)
            {
               // Hide parameters and set mandatory value
               this._hideParameters(configDef.parameterDefinitions);
               this._setParameter(ruleConfig, "category-aspect", "cm:generalclassifiable");

               // Make ui display a category picker
               configDef.parameterDefinitions.push(
               {
                  type: "arcc:category-picker"
               });
               return configDef;
            }
         },

         /**
          * Tag picker
          */
         HasTag:
         {
            edit: function(configDef, ruleConfig, configEl)
            {
               // Hide parameters
               this._hideParameters(configDef.parameterDefinitions);

               // Make ui display a tag picker
               configDef.parameterDefinitions.push(
               {
                  type: "arcc:tag-picker"
               });
               return configDef;
            }
         },

         /**
          * Compare property value
          */
         ComparePropertyValue:
         {
            text: function(configDef, ruleConfig, configEl)
            {
               var pd = this._getParamDef(configDef, "value");
               if (Alfresco.util.arrayContains(["d:any", "d:text", "d:mltext"], pd.type))
               {
                  configDef._customMessageKey = "customise.compare-property-value.text.hyphen";
               }
               return configDef;
            }
         },

         /**
          * Compare mime type
          */
         CompareMimeType:
         {
            edit: function(configDef, ruleConfig, configEl)
            {
               var vd = this._getParamDef(configDef, "value");
               vd.displayLabel = this.msg("label.is");
               vd._hideColon = true;
               return configDef;
            }
         },

         /**
          * Show more shall not render any parameter ui by itself so keep the paramDefinitions empty.
          * Instead display a dialog and if:
          *
          * a) A property was selected add that property to the menu with "compare-property-value"
          *    as rule config definition name and make sure it gets selected.
          *
          * b) Cancel was clicked, make sure to reselect the previous selected option in the menu.
          */
         ShowMore:
         {
            manual: { edit: true },
            currentCtx: {},
            edit: function(configDefinition, p_oRuleConfig, configEl, paramsEl)
            {
               this.customisations.ShowMore.currentCtx =
               {
                  configEl: configEl
               };

               if (!this.widgets.showMoreDialog)
               {
                  this.widgets.showMoreDialog = new Alfresco.module.RulesPropertyPicker(this.id + "-showMoreDialog");

                  YAHOO.Bubbling.on("dataItemSelected", function (layer, args)
                  {
                     if ($hasEventInterest(this.widgets.showMoreDialog, args))
                     {
                        // Add property to this menu
                        var properties = this.options.properties,
                           property = this._handleTransientProperty(args[1].selectedItem.item);
                        property._hidden = true;

                        for (var i = 0, il = properties.length; i < il; i++)
                        {
                           if (properties[i].name == property.name)
                           {
                              break;
                           }
                        }
                        if (i == il)
                        {
                           // Its a new property, add it
                           properties.push(property);
                        }

                        /**
                         * Create a new select drop down, based on the current/temporary properties so it becomes
                         * specific for this config row
                         */
                        var configEl = this.customisations.ShowMore.currentCtx.configEl,
                           newSelectEl = this._createSelectMenu(property.name),
                           selectEl = Selector.query('select', configEl)[0];

                        // Since we have created a new select menu, the history of previous selections must taken from the old menu
                        this.previousConfigNameSelections[Alfresco.util.generateDomId(newSelectEl)] = this.previousConfigNameSelections[selectEl.getAttribute("id")];

                        // Create a ruleConfig and replace the current configEl and with a new one based on ruleConfig
                        var ruleConfig = this._createRuleConfig(property.name);
                        var newConfigEl = this._createConfigNameUI(ruleConfig, newSelectEl, configEl);
                        this.customisations.ShowMore.currentCtx.configEl = newConfigEl;
                        configEl.parentNode.removeChild(configEl);
                        this._createConfigParameterUI(ruleConfig, newConfigEl);
                     }
                  }, this);
                  YAHOO.Bubbling.on("dataItemSelectionCancelled", function (layer, args)
                  {
                     if ($hasEventInterest(this.widgets.showMoreDialog, args))
                     {
                        // Reselect the previous choice in the menu
                        var configEl = this.customisations.ShowMore.currentCtx.configEl;
                        this._selectPreviousConfigName(Selector.query('select', configEl)[0]);
                     }
                  }, this);
               }
               this.widgets.showMoreDialog.showDialog();
               return null;
            }
         }

      },

      /**
       * RENDERERS
       */

      renderers:
      {
         /**
          * Category Picker
          */
         "arcc:category-picker":
         {
            currentCtx: {},
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               this.renderers["arcc:category-picker"].currentCtx =
               {
                  configDef: configDef,
                  ruleConfig: ruleConfig,
                  paramDef: paramDef
               };
               var picker = new Alfresco.module.ControlWrapper(Alfresco.util.generateDomId());
               picker.setOptions(
               {
                  type: "category",
                  container: containerEl,
                  value: ruleConfig.parameterValues["category-value"],
                  controlParams:
                  {
                     multipleSelectMode: false
                  },
                  fnValueChanged:
                  {
                     fn: function(obj)
                     {
                        var ctx = this.renderers["arcc:category-picker"].currentCtx;
                        this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, "category-value", obj.selectedItems[0]);
                        this._updateSubmitElements(ctx.configDef);
                     },
                     scope: this
                  }
               });
               picker.render();
            }
         },

         /**
          * Tag Picker
          */
         "arcc:tag-picker":
         {
            currentCtx: {},
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               this.renderers["arcc:tag-picker"].currentCtx =
               {
                  configDef: configDef,
                  ruleConfig: ruleConfig,
                  paramDef: paramDef
               };

               var onNodeRefTagLoaded = function (containerEl, nodeRefTag)
               {
                  var picker = new Alfresco.module.ControlWrapper(Alfresco.util.generateDomId());
                  var options =
                  {
                     type: "tag",
                     container: containerEl,
                     controlParams:
                     {
                        multipleSelectMode: false
                     },
                     fnValueChanged:
                     {
                        fn: function(obj)
                        {
                           var ctx = this.renderers["arcc:tag-picker"].currentCtx,
                              tagNodeRef = obj.selectedItems[0],
                              tag = obj.selectedItemsMetaData[tagNodeRef].name;
                           this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, "tag", tag);
                           this._updateSubmitElements(ctx.configDef);
                        },
                        scope: this
                     }
                  };
                  if (nodeRefTag)
                  {
                     options.value = nodeRefTag;
                  }
                  picker.setOptions(options);
                  picker.render();
               };

               var tag = ruleConfig.parameterValues ? ruleConfig.parameterValues["tag"] : null
               if (!tag)
               {
                  onNodeRefTagLoaded.call(this, containerEl, null);
               }
               else
               {
                  Alfresco.util.Ajax.jsonPost(
                  {
                     url: Alfresco.constants.PROXY_URI_RELATIVE + "api/tag/workspace/SpacesStore",
                     dataObj:
                     {
                        name: ruleConfig.parameterValues["tag"]
                     },
                     successCallback:
                     {
                        fn: function (p_oResponse, p_oObj)
                        {
                           p_oObj.onNodeRefTagLoaded.call(this, p_oObj.containerEl, p_oResponse.json.nodeRef)
                        },
                        scope: this,
                        obj:
                        {
                           containerEl: containerEl,
                           onNodeRefTagLoaded: onNodeRefTagLoaded
                        }
                     },
                     failureMessage: this.msg("message.getTagFailure")
                  });
               }
            }
         }
      }
   });
})();
