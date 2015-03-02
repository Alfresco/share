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
 * RuleConfig template.
 *
 * @namespace Alfresco
 * @class Alfresco.RuleConfig
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
       $hasEventInterest = Alfresco.util.hasEventInterest,
       $combine = Alfresco.util.combinePaths;

   /**
    * RuleConfig constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RuleConfig} The new RuleConfig instance
    * @constructor
    */
   Alfresco.RuleConfig = function RuleConfig_constructor(htmlId)
   {
      Alfresco.RuleConfig.superclass.constructor.call(this, "Alfresco.RuleConfig", htmlId, ["button"]);

      // Instance variables
      this._configDefs = {};
      this._datePickerConfigDefMap = {};

      // Decoupled event listeners
      YAHOO.Bubbling.on("mandatoryControlValueUpdated", this.onDatePickerMandatoryControlValueUpdated, this);
      return this;
   };


   /**
   * Alias to self
   */
   var RC = Alfresco.RuleConfig;

   /**
   * View Mode Constants
   */
   YAHOO.lang.augmentObject(RC,
   {
      /**
       * Set options.mode to this value to get a non editable text representation of the config
       *
       * @property MODE_TEXT
       * @type {string}
       * @public
       */
      MODE_TEXT: "text",

      /**
       * Set options.mode to this value to get an editable form input representation of the config
       *
       * @property MODE_EDIT
       * @type {string}
       * @public
       */
      MODE_EDIT: "edit"
   });

   YAHOO.extend(Alfresco.RuleConfig, Alfresco.component.Base,
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
          * Current siteId.
          *
          * @property siteId
          * @type string
          */
         siteId: "",

         /**
          * Repository's rootNode
          *
          * @property rootNode
          * @type Alfresco.util.NodeRef
          */
         rootNode: null,

         /**
          * Set to:
          * - MODE_TEXT for non editable text representation
          * - MODE_EDIT for editable form inputs
          *
          * @property mode
          * @type string
          * @default RC.MODE_EDIT
          */
         mode: RC.MODE_EDIT,

         /**
          * The type of configs that are manipulated
          *
          * @property ruleConfigType
          * @type string
          */
         ruleConfigType: null,

         /**
          * The id-name to use when accepting ruleConfig-objects in displayRuleConfigs and
          * when returning ruleConfig-objects in getRuleConfigs.
          *
          * @property ruleConfigDefinitionKey
          * @type string
          * @default "name"
          */         
         ruleConfigDefinitionKey: "name",
         
         /**
          * Describes how the menu items shall be ordered and grouped.
          * Note that this is NOT the rule configs that will appear in the menu, it is a representation of how the
          * select element shall group its options where each group contains of pattern objects that will match a
          * rule config (or item) and therefore place the rule config in that specific group.
          *
          * @property menuMap
          * @type array
          */
         menuMap: [],

         /**
          * The rule configs that shall be selectable in the menu (types, conditions or actions).
          * These will be placed inside the select menu and be organised/ordered as described in the menuMap.
          *
          * @property ruleConfigDefinitions
          * @type array
          */
         ruleConfigDefinitions: [],

         /**
          * Customisations that may modify the default ui rendering of a rule config.
          * Contains objects with the rule config name as the key and the name of a member variable in this.customisations as the value.
          *
          * @property customisationsMap
          * @type array
          */
         customisationsMap: [],

         /**
          * The rule constraints that shall be selectable in drop downs for some parameters.
          * Contains the constraint name as attribute keys and an array of constraint values as the attribute value.
          *
          * @property constraints
          * @type object
          */
         constraints: {},

         /**
          * If a filter is provided for a contraint name only the values given inside this filter will be used in the
          * constraint lists. I.e. a filter is used to make sure only date-valid contstraints are used against a date value.
          *
          * @property constraintsFilter
          * @type object
          */
         constraintsFilter: {},

         /**
          * The form created by the outside component that uses this config handler.
          * This component will hook in to the forms validation process to enable/disable its buttons.
          *
          * @property form
          * @type object
          * @default null
          */
         form: null
      },

      /**
       * Used when a menu is built to save the config for each menu item so it can be passed into the renderer if selected.
       *
       * @property _configDefs
       * @type {object}
       * @private
       */
      _configDefs: null,

      /**
       * Each date picker that is created will have its associated configDef mapped here so the bubbling event handler
       * will know if the date picker belongs to this component and can provide the configDef to the
       * _updateSubmitElements method.
       *
       * @property _datePickerConfigDefMap
       * @type {object}
       */
      _datePickerConfigDefMap: null,

      /**
       * Fired by YUI when parent element is available for scripting.
       * Template initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function RuleConfig_onReady()
      {
         // Enabling checkbox
         var enableCheckboxEl = Dom.get(this.id + "-" + this.options.ruleConfigType + "-checkbox");
         if (enableCheckboxEl)
         {
            Event.addListener(enableCheckboxEl, "click", function(p_oEvent, p_oEnableCheckboxEl)
            {
               var configsEl = Dom.get(this.id + "-configs");
               if (p_oEnableCheckboxEl.checked)
               {
                  Dom.removeClass(configsEl, "hidden");
                  this._toggleDisableOnElements(Selector.query("[param]", configsEl), false);
                  this._toggleDisableOnElements(Selector.query("select.config-name", configsEl), false);
               }
               else
               {
                  Dom.addClass(configsEl, "hidden");
                  this._toggleDisableOnElements(Selector.query("[param]", configsEl), true);
                  this._toggleDisableOnElements(Selector.query("select.config-name", configsEl), true);
               }
               this._updateSubmitElements();
            }, enableCheckboxEl, this);
         }

         // Relation menus
         var relationButtonEl = Dom.get(this.id + "-" + this.options.ruleConfigType + "-menubutton");
         if (relationButtonEl)
         {
            this.widgets.relationButton = new YAHOO.widget.Button(relationButtonEl,
            {
               type: "menu",
               menu: this.id + "-" + this.options.ruleConfigType + "-menubuttonselect",
               menualignment: ["tr", "br"]
            });
         }

         // Save reference to config template
         this.widgets.configTemplateEl = Dom.get(this.id + "-configTemplate");

         // Create select menu template that will be used for each config
         this.widgets.selectTemplateEl = this._createSelectMenu();

         // Tell other components that this component is ready
         YAHOO.Bubbling.fire("ruleConfigReady",
         {
            eventGroup: this
         });
      },

      /**
       * Returns a list of all the current ruleConfigs as an array of ruleConfig objects.
       *
       * I.e
       * [
       *    {
       *       "<the value of this.options.ruleConfigDefinitionKey>": "<the selected option value in the select menu>",
       *       "parameterValues":
       *       {
       *          "paramName1": "paramValue1",
       *          "paramName2": "paramValue2"
       *       }
       *    }
       * ]
       *
       * @method getRuleConfigs
       * @return {array} An array of ruleConfig objects
       */
      getRuleConfigs: function RuleConfig_getRuleConfigs()
      {
         // Empty result
         var configs = [];

         if (!Dom.hasClass(this.id + "-configs", "hidden"))
         {
            // Add configs
            var configEls = Selector.query('li.config', this.id + "-body"),
                  configEl,
                  configDef,
                  config;
            for (var ci = 0, cil = configEls.length; ci < cil; ci++)
            {
               configEl = configEls[ci];

               // Find config & name
               configDef = this._getSelectedConfigDef(configEl);
               if (configDef)
               {
                  config = {};
                  if (configEl.getAttribute("paramid"))
                  {
                     config.id = configEl.getAttribute("paramid");
                  }
                  config[this.options.ruleConfigDefinitionKey] = configDef.name;
                  config.parameterValues = this._getParameters(configDef);
                  configs.push(config);
               }
            }
         }
         return configs;
      },

      /**
       * Displays ruleConfig rows as described in ruleConfigs.
       * Expects the following format of the ruleConfig array:
       *
       * [
       *    {
       *       "<match the value of this.options.ruleConfigDefinitionKey>": "<the ruleConfig name to select in the select menu>",
       *       "parameterValues":
       *       {
       *          "paramName1": "paramValue1",
       *          "paramName2": "paramValue2"
       *       }
       *    }
       * ]
       *
       * Note! This method shall be called after the "ruleConfigReady" event has been fired.
       * Note! Before this method is called the config body will be empty.
       * Note! Even if ruleConfigs contains no elements 1 row will always be created.
       *
       * @method displayRuleConfigs
       * @param ruleConfigs {array} An array of rule configurations
       */
      displayRuleConfigs: function RuleConfig_displayRulConfigs(ruleConfigs)
      {
         Dom.removeClass(this.id + "-body", RC.MODE_EDIT);
         Dom.removeClass(this.id + "-body", RC.MODE_TEXT);
         Dom.addClass(this.id + "-body", this.options.mode);

         Dom.get(this.id + "-configs").innerHTML = "";
         var checkboxEl = Dom.get(this.id + "-" + this.options.ruleConfigType + "-checkbox");
         if (checkboxEl && this.options.mode == RC.MODE_EDIT)
         {
            Dom.removeClass(checkboxEl, "hidden");
            checkboxEl.checked = ruleConfigs.length > 0;
         }

         if (!ruleConfigs || ruleConfigs.length == 0)
         {
            if (this.options.mode == RC.MODE_TEXT)
            {
               // Hide this component we are in text mode and ther's nothing to display
               Dom.addClass(this.id + "-body", "hidden");
            }
            else
            {
               ruleConfigs.push({});
               Dom.addClass(this.id + "-configs", "hidden");
            }
         }
         else
         {
            // There are rule configs to display make sure we are not hiding this component
            Dom.removeClass(this.id + "-body", "hidden");
            Dom.removeClass(this.id + "-configs", "hidden");
         }

         var ruleConfig,
            configEl;
         for (var i = 0, il = ruleConfigs.length; i < il; i++)
         {
            ruleConfig = ruleConfigs[i];
            this._createConfigUI(ruleConfig, null, null);
         }
         this._refreshRemoveButtonState();
      },

      
      /**
       * EVENT HANDLERS
       */

      /**
       * Called from the "+" link to create another value for multi valued parameter
       *
       * @method onAddMoreParameterIconClick
       * @param p_oEvent {object} The click event
       * @param p_oParameterCtx {object} References to paramDef, configDef, ruleConfig
       */
      onAddExtraParameterIconClick: function RuleConfig_onAddMoreParameterIconClick(p_oEvent, p_oParameterCtx)
      {
         this._addExtraParameter(p_oParameterCtx.paramDef, p_oParameterCtx.addButton, p_oParameterCtx.configDef, p_oParameterCtx.ruleConfig, p_oParameterCtx.paramRenderer, value);
      },

      /**
       * Called from the "-" link to remove a value for multi valued parameter
       *
       * @method onDeleteExtraParameterIconClick
       * @param p_oEvent {object} The click event
       * @param p_oExtraparamEl {HTMLElement}
       */
      onDeleteExtraParameterIconClick: function RuleConfig_onDeleteExtraParameterIconClick(p_oEvent, p_oExtraparamEl)
      {
         p_oExtraparamEl.parentNode.removeChild(p_oExtraparamEl);
      },

      /**
       * Called when the user selects an option int the ruleConfig select menu
       *
       * @method onConfigNameSelectChange
       * @param p_oEvent {object} The change event
       * @param configEl {HTMLElement} Contains the ruleConfig and configEl objects
       */
      onConfigNameSelectChange: function RuleConfig_onConfigNameSelectChange(p_oEvent, configEl)
      {
         this._createConfigParameterUI({}, configEl);
      },

      /**
       * Called when the user clicks on an "+"/add rule config button
       *
       * @method onAddConfigButtonClick
       * @param p_oEvent {object} The click event
       * @param p_eConfig {HTMLDivElement} the config element the button belongs to
       */
      onAddConfigButtonClick: function RuleConfig_onAddConfigButtonClick(p_oEvent, p_eConfig)
      {
         this._createConfigUI({}, null, p_eConfig);
         this._refreshRemoveButtonState();
      },

      /**
       * Called when the user clicks on an "-"/remove rule config button
       *
       * @method onRemoveConfigButtonClick
       * @param p_oEvent {object} The click event
       * @param p_eConfig {HTMLDivElement} the config element the button belongs to
       */
      onRemoveConfigButtonClick: function RuleConfig_onRemoveConfigButtonClick(p_oEvent, p_eConfig)
      {
         p_eConfig.parentNode.removeChild(p_eConfig);
         this._refreshRemoveButtonState();
         this._updateSubmitElements();
      },

      /**
       * Called when a date has been selected from a date picker.
       * Will cause the forms validation to run.
       *
       * @method onDatePickerMandatoryControlValueUpdated
       * @param layer
       * @param args
       */
      onDatePickerMandatoryControlValueUpdated: function RuleConfig_onDatePickerMandatoryControlValueUpdated(layer, args)
      {
         var configDef = this._datePickerConfigDefMap[args[1].id];
         if (configDef)
         {
            this._updateSubmitElements(configDef);
         }
      },

      
      /**
       * PRIVATE OR PROTECTED METHODS
       */


      /**
       * Called after the "+" link has been pressed to create another value for multi valued parameter
       *
       * @method addExtraParameter
       */
      _addExtraParameter: function RuleConfig_addExtraParameter(paramDef, addButton, configDef, ruleConfig, paramRenderer, value)
      {
         // Create a container for the extra parameter
         var extraParamEl = document.createElement("span");

         // Add new parameter container to the left of the add button
         addButton.parentNode.insertBefore(extraParamEl, addButton);

         if (this.options.mode == RC.MODE_TEXT && paramDef._type != "hidden")
         {
            // Add comma before parameter
            addButton.parentNode.insertBefore(document.createTextNode(", "), extraParamEl);
         }

         // Add another parameter ui control
         var fn = paramRenderer[this.options.mode],
            el = fn.call(this, extraParamEl, configDef, paramDef, ruleConfig, value);
         Dom.addClass(el, "param");

         if (this.options.mode == RC.MODE_EDIT && paramDef._type != "hidden")
         {
            // Add a delete button for the new parameter control
            var deleteButton = document.createElement("span");
            deleteButton.setAttribute("title", this.msg("button.deleteExtraParameter", paramDef.displayLabel ? paramDef.displayLabel: paramDef.name));
            Dom.addClass(deleteButton, "delete-extra-parameter-button");
            deleteButton.innerHTML = "-";
            Dom.setStyle(deleteButton, "width", "10px");
            Dom.setStyle(deleteButton, "height", "10px");
            Event.addListener(deleteButton, "click", this.onDeleteExtraParameterIconClick, extraParamEl, this);
            extraParamEl.appendChild(deleteButton);
         }
         this._updateSubmitElements(configDef);
      },
      
      /**
       * Will set the disabled attribute to the value of "disabled" for the elements in p_aEls
       *
       * @method _toggleDisableOnElements
       * @param p_aEls {array} An array of HTMLElements
       * @param p_bDisable {boolean} True if elements shall be disabled
       * @private
       */
      _toggleDisableOnElements: function RuleConfig__toggleDisableOnElements(p_aEls, p_bDisable)
      {
         for (var i = 0, il = p_aEls.length; i < il; i ++)
         {
            if (p_bDisable)
            {
               p_aEls[i].setAttribute("disabled", true);
            }
            else
            {
               p_aEls[i].removeAttribute("disabled");
            }
         }
      },

      /**
       * Called when all data that is needed for the menu has been loaded.
       *
       * Will walk through the this.options.menuMap descriptor twice to create the menu, menu groups and items by
       * inserting the configDefs from this.options.ruleConfigDefinitions into a select-menu and its opt-groups.
       *
       * 1. First pass will ask for objects using only pattern objects WITHOUT a wildcard attribute.
       * 2. Seconds pass will ask for objects using only pattern objects WITH at least one wild card attribute.
       *
       * @method _createSelectMenu
       * @param obj {object} (Optional) Custom parameter to send in for use for overriding classes,
       *                                will be a parameter to _getConfigItems.
       * @return {HTMLSelectElement} The created menu
       * @private
       */
      _createSelectMenu: function RuleConfig__createSelectMenu(obj)
      {
         // Used to see if a menu item already has been added
         var alreadyAdded = {};

         // Create menu items & groups from the menu options (make sure to make a copy so we can alter it)
         var menuMapOpt = Alfresco.util.deepCopy(this.options.menuMap),
            groupOpt,
            itemOpt,
            itemPatternOpt,
            hasWildcard,
            menuItems,
            menuItem,
            menuItemKey;

         // Make 2 passes though the options, first collect all exact matches then the ones matching wildcards
         for (var pass = 1; pass <=2; pass++)
         {
            for (var gi = 0, gil = menuMapOpt.length; gi < gil; gi++)
            {
               groupOpt = menuMapOpt[gi];
               for (var gii = 0, giil = groupOpt.length; gii < giil; gii++)
               {
                  itemOpt = groupOpt[gii];
                  for (var itemTypeOpt in itemOpt)
                  {
                     if (itemOpt.hasOwnProperty(itemTypeOpt))
                     {
                        itemPatternOpt = itemOpt[itemTypeOpt];
                        hasWildcard = false;
                        for (var itemPatternAttributeOpt in itemPatternOpt)
                        {
                           if (itemTypeOpt != "_menuItems" &&
                               itemPatternOpt.hasOwnProperty(itemPatternAttributeOpt) &&
                               itemPatternOpt[itemPatternAttributeOpt].indexOf("*") > -1)
                           {
                              hasWildcard = true;
                              break;
                           }
                        }

                        if ((pass == 1 && !hasWildcard) || (pass == 2 && hasWildcard))
                        {
                           // Add internal variable for storing the real menu items
                           if (!itemOpt["_menuItems"])
                           {
                              itemOpt["_menuItems"] = [];
                           }
                           menuItems = this._getConfigItems(itemTypeOpt, itemPatternOpt, obj);
                           for (var mii = 0, mil = menuItems.length; mii < mil; mii++)
                           {
                              menuItem = menuItems[mii];
                              menuItemKey = menuItem.type + "_" + menuItem.id;
                              if (!alreadyAdded[menuItemKey])
                              {
                                 // Add item if it hasn't already been used
                                 itemOpt["_menuItems"].push(menuItem);
                                 alreadyAdded[menuItemKey] = true;
                              }
                              if (!this._configDefs[menuItemKey])
                              {
                                 // Save descriptor so we can look it up when menu changes
                                 this._configDefs[menuItemKey] = menuItem.descriptor;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         // Create a select element that later will be cloned so it can be used individually use in the configs
         var selectEl = document.createElement("select"),
            optGroupEl,
            optionEl;
         selectEl.setAttribute("name", "-");
         Dom.addClass(selectEl, "config-name");
         for (gi = 0, gil = menuMapOpt.length; gi < gil; gi++)
         {
            groupOpt = menuMapOpt[gi];
            optGroupEl = null;
            for (gii = 0, giil = groupOpt.length; gii < giil; gii++)
            {
               itemOpt = groupOpt[gii];
               menuItems = itemOpt["_menuItems"];
               for (mii = 0, mil = menuItems.length; mii < mil; mii++)
               {
                  if (!optGroupEl)
                  {
                     // The optGroup shall only be created if there was at least 1 option
                     optGroupEl = document.createElement("optgroup");
                     selectEl.appendChild(optGroupEl);
                  }
                  menuItem = menuItems[mii];
                  optionEl = document.createElement("option");
                  optionEl.setAttribute("value", menuItem.id);
                  optionEl.appendChild(document.createTextNode(menuItem.label));
                  optionEl.setAttribute("rel", menuItem.type);
                  optGroupEl.appendChild(optionEl);
               }
            }
         }
         return selectEl;
      },

      /**
       * Called from _createSelectMenu to get the configDef for a menu item.
       *
       * @method _getConfigItems
       * @param itemType
       * @param itemPatternObject
       * @param obj {object} The custom parameter obj that was passed in to _createSelectMenu,
       *                     suitable for overriding classes.
       * @return {array} Menu item objects (as described below) representing a configDef (or item)
       *                 matching all attributes in itemPatternObject.
       * @protected
       * {
       *    id: string,
       *    label: string,
       *    descriptor: object
       * }
       */
      _getConfigItems: function RuleConfig__getConfigItems(itemType, itemPatternObject, obj)
      {
         var results = [],
            ruleConfigDef;
         if (itemType == this.options.ruleConfigType)
         {
            for (var ci = 0, cil = this.options.ruleConfigDefinitions.length; ci < cil; ci++)
            {
               ruleConfigDef = this.options.ruleConfigDefinitions[ci];
               if (Alfresco.util.objectMatchesPattern(ruleConfigDef, itemPatternObject))
               {
                  results.push(
                  {
                     id: ruleConfigDef.name,
                     type: this.options.ruleConfigType,
                     label: ruleConfigDef.displayLabel || ruleConfigDef.name,
                     descriptor: ruleConfigDef
                  });
               }
            }
         }
         else if (itemType == "item")
         {
            results.push(
            {
               id: itemPatternObject.id,
               type: "item",
               label: this.msg("menu.item." + itemPatternObject.id),
               descriptor: itemPatternObject
            });
         }
         return results;
      },

      /**
       * Called to get the constraint options for a constraint.
       *
       * @method _getConstraintValues
       * @param p_oParamDef {object}
       * @param p_oRuleConfig {object}
       * @return {array} rule constraint values
       */
      _getConstraintValues: function RuleConfig__getConstraintValues(p_oParamDef, p_oRuleConfig)
      {
         var values = this.options.constraints[p_oParamDef.constraint],
            filter;
         if (p_oParamDef._constraintFilter)
         {
            filter = Alfresco.util.findValueByDotNotation(this.options.constraintsFilter[p_oParamDef.constraint], p_oParamDef._constraintFilter);
         }
         if (!values)
         {
            values = [];
         }
         else if (filter)
         {
            var filteredValues = [];
            for (var i = 0, il = values.length; i <il; i++)
            {
               if (Alfresco.util.arrayContains(filter, values[i].value))
               {
                  filteredValues.push(values[i]);
               }
            }
            values = filteredValues;
         }
         return values;
      },

      /**
       * Creates a config row with the parameters and the parameter values
       *
       * @method _createConfigUI
       * @param p_oRuleConfig {object} Rule config descriptor object
       * @param p_oSelectEl {HTMLSelectElement} (Optional) Will clone the selectTemaplteEl if not provided
       * @param p_eRelativeConfigEl {object} (Optional) will be placed in the end of omitted
       * @return {HTMLElement} The config row
       * @protected
       */
      _createConfigUI: function RuleConfig__createConfigUI(p_oRuleConfig, p_oSelectEl, p_eRelativeConfigEl)
      {
         var configEl = this._createConfigNameUI(p_oRuleConfig, p_oSelectEl ? p_oSelectEl : this.widgets.selectTemplateEl.cloneNode(true), p_eRelativeConfigEl);
         this._createConfigParameterUI(p_oRuleConfig, configEl);
         return configEl;
      },
      
      /**
       * Creates a config name select
       *
       * @method _createConfigNameUI
       * @param p_oRuleConfig {object} Rule config descriptor object
       * @param p_oSelectEl {HTMLSelectElement} The select menu to use
       * @param p_eRelativeConfigEl {object}
       * @protected
       */
      _createConfigNameUI: function RuleConfig__createConfigNameUI(p_oRuleConfig, p_oSelectEl, p_eRelativeConfigEl)
      {
         // Add config element
         var configEl = this.widgets.configTemplateEl.cloneNode(true);
         Alfresco.util.generateDomId(configEl);
         if (p_eRelativeConfigEl)
         {
            p_eRelativeConfigEl.parentNode.insertBefore(configEl, p_eRelativeConfigEl.nextSibling);
         }
         else
         {
            Dom.get(this.id + "-configs").appendChild(configEl);
         }

         // Add config name/type drop down
         if (!p_oSelectEl.getAttribute("id"))
         {
            Alfresco.util.generateDomId(p_oSelectEl);
         }
         Event.addListener(p_oSelectEl, "change", this.onConfigNameSelectChange, configEl, this);
         var configNameContainerEl = Selector.query('div.name', configEl)[0];
         configNameContainerEl.appendChild(p_oSelectEl);

         // Set values
         if (p_oRuleConfig.id)
         {
            configEl.setAttribute("paramid", p_oRuleConfig.id);
         }
         Alfresco.util.setSelectedIndex(p_oSelectEl, p_oRuleConfig[this.options.ruleConfigDefinitionKey]);

         if (this.options.mode == RC.MODE_EDIT)
         {
            // Create add button
            var addButtonEl = Selector.query('div.actions .add-config', configEl, true);
            var addButton = new YAHOO.widget.Button(addButtonEl,
            {
               type: "push"
            });
            addButton.on("click", this.onAddConfigButtonClick, configEl, this);
            addButton.addClass("add-config");
                                                                                     
            // Create remove button
            var removeButtonEl = Selector.query('div.actions .remove-config', configEl, true);
            var removeButton = new YAHOO.widget.Button(removeButtonEl,
            {
               type: "push",
               disabled: true
            });
            removeButton.on("click", this.onRemoveConfigButtonClick, configEl, this);
            removeButton.addClass("remove-config");
         }
         else if (this.options.mode == RC.MODE_TEXT)
         {
            // Hide actions and config name select
            Dom.addClass(Selector.query('div.actions', configEl, true), "hidden");
            var nameEl = document.createElement("span");
            Dom.addClass(p_oSelectEl, "hidden");
            nameEl.appendChild(document.createTextNode(p_oSelectEl.options[p_oSelectEl.selectedIndex].text));
            configNameContainerEl.appendChild(nameEl);

            // See if we have custom display message for the row
            if (this._getCustomisedMessage(this._getSelectedConfigDef(configEl), p_oRuleConfig))
            {
               // Hide the name column and also the parameters
               Dom.addClass(Selector.query('div.name', configEl, true), "hidden");
               Dom.addClass(Selector.query('div.parameters', configEl, true), "hidden");
            }
         }

         // Return element
         return configEl;         
      },

      /**
       *
       *
       * @method _createConfigParameterUI
       * @param p_oRuleConfig {object} Rule config descriptor object
       * @param configEl {HTMLLIElement} Rule config descriptor object
       */
      _createConfigParameterUI: function RuleConfig__createConfigParameterUI(p_oRuleConfig, configEl)
      {
         // Remove old ui
         configEl.removeAttribute("id");
         var paramsEl = Selector.query('div.parameters', configEl)[0];

         var configDef = this._getSelectedConfigDef(configEl);
         if (configDef)
         {
            /**
             * Create a copy of the configDef and give it a unique id that also will
             * be applied to the configEl as well to assist ui management
             */
            configDef = Alfresco.util.deepCopy(configDef); 
            configDef._id = Alfresco.util.generateDomId(configEl);

            // Find the correct customisation renderer
            var selectEl = Selector.query('select', configEl)[0],
               optionEl = selectEl.options[selectEl.selectedIndex],
               configCustomisation = this._getConfigCustomisation(optionEl.getAttribute("rel"), configDef),
               customisationFn = configCustomisation ? configCustomisation[this.options.mode] : null;
            if (!customisationFn || !(configCustomisation.manual && configCustomisation.manual[this.options.mode]))
            {
               // If Customisation for this mode is manual it wants to handle cleanup by itself (or leave the old parameters)
               paramsEl.innerHTML = "";
            }
            if (customisationFn)
            {
               // There was a customisation handler configured
               configDef = customisationFn.call(this, configDef, p_oRuleConfig, configEl, paramsEl);
            }

            // Render new parameter ui if any
            if (configDef && configDef.parameterDefinitions)
            {
               var paramDef,
                  paramRenderer,
                  value;
               for (var i = 0, il = configDef.parameterDefinitions.length; i < il; i++)
               {
                  paramDef = configDef.parameterDefinitions[i];
                  if (paramDef._type == "hidden" && this.options.mode == RC.MODE_TEXT)
                  {
                     continue;
                  }
                  paramRenderer = this._getParamRenderer(paramDef.type);
                  var fn = paramRenderer[this.options.mode];
                  value = p_oRuleConfig.parameterValues ? p_oRuleConfig.parameterValues[paramDef.name] : null;
                  if (!paramRenderer || !fn)
                  {
                     // There is no renderer for the parameter type in this mode
                     var errorSpan = document.createElement("span");
                     Dom.addClass(errorSpan, "error");
                     errorSpan.innerHTML = this.msg("label.noRendererForType", paramDef.type);
                     paramsEl.appendChild(errorSpan);
                     continue;
                  }
                  // Create element for parameter
                  var paramEl = document.createElement("span");
                  Dom.addClass(paramEl, "menutype_" + optionEl.getAttribute("rel"));
                  Dom.addClass(paramEl, "menuname_" + optionEl.value);
                  Dom.addClass(paramEl, "paramtype_" + paramDef.type.replace(":", "_"));
                  Dom.addClass(paramEl, "paramname_" + paramDef.name);
                  paramsEl.appendChild(paramEl);

                  if (paramRenderer.manual && paramRenderer.manual[this.options.mode])
                  {
                     // Renderer wants to implement the "contraint":s- and "multiValued"-support for the parameter
                     fn.call(this, paramEl, configDef, paramDef, p_oRuleConfig, value);
                  }
                  else
                  {
                     var controlEl;
                     if (paramDef.constraint && this.options.mode == RC.MODE_EDIT)
                     {
                        /**
                         * Implement support for the "constraint" by using a select element
                         * that will be multi-valued depending on the paramDef.
                         */
                        var constraintOptions = this._getConstraintValues(paramDef, p_oRuleConfig);
                        controlEl = this._createSelect(paramEl, configDef, paramDef, constraintOptions, value);
                     }
                     else
                     {
                        // Save first values controlEl so we can add the label to the left of it below
                        controlEl = fn.call(this, paramEl, configDef, paramDef, p_oRuleConfig, YAHOO.lang.isArray(value) && value.length > 0 ? value[0] : value);

                        /**
                         * Create an add button element (so we can add additional value elements to the left of it)
                         * but activate it only if we are in edit mode
                         */
                        var addButton = document.createElement("span");
                        paramEl.appendChild(addButton);

                        // If it was a multi value we add the remaining values
                        var vil = YAHOO.lang.isArray(value) ? value.length : -1;
                        for (var vi = 1; vi < vil; vi++)
                        {
                           this._addExtraParameter(paramDef, addButton, configDef, p_oRuleConfig, paramRenderer, value[vi]);
                        }

                        // Style and activate add button if we are in edit mode
                        if (paramDef.isMultiValued && this.options.mode == RC.MODE_EDIT && paramDef._type != "hidden")
                        {
                           addButton.setAttribute("title", this.msg("button.addExtraParameter", paramDef.displayLabel ? paramDef.displayLabel: paramDef.name));
                           Dom.addClass(addButton, "add-extra-parameter-button");
                           addButton.innerHTML = "+";
                           Dom.setStyle(addButton, "width", "10px");
                           Dom.setStyle(addButton, "height", "10px");
                           Event.addListener(addButton, "click", this.onAddExtraParameterIconClick,
                           {
                              paramRenderer: paramRenderer,
                              paramDef: paramDef,
                              configDef: configDef,
                              ruleConfig: p_oRuleConfig,
                              addButton: addButton
                           }, this);
                        }
                        Dom.addClass(controlEl, "param");                                             
                     }
                     if (paramDef._type != "hidden")
                     {
                        if (paramDef.displayLabel)
                        {
                           // Display a label left to the parameter if displayLabel is present
                           this._createLabel(paramDef.displayLabel + (paramDef._hideColon ? "" : ":"), controlEl, paramDef._displayLabelToRight);
                        }
                        if (paramDef._unit) {
                           // Display a text span to the right of the parameter if unit is present
                           var unitEl = document.createElement("span");
                           Dom.addClass(unitEl, "unit");
                           unitEl.appendChild(document.createTextNode(paramDef._unit));
                           controlEl.parentNode.appendChild(unitEl);
                        }
                     }
                  }
               }
            }

            if (this.options.mode == RC.MODE_TEXT)
            {
               // Override the default text rendering of the config
               var message = configDef._customMessageKey ? this.msg(configDef._customMessageKey) : undefined;
               if (message == configDef._customMessageKey)
               {
                  message = this._getCustomisedMessage(configDef, p_oRuleConfig);
               }
               if (message)
               {
                  this._renderByCustomisedMessage(configEl, message);
               }
            }
         }

         // Make sure form is re-validated
         this._updateSubmitElements(configDef);
      },

      /**
       * Gets a custom message to display rather than the default text layout
       *
       * @method _getCustomisedMessage
       * @param configDef {object} The configDef
       * @return {string} The message or null if it wasn't found
       * @protected
       */
      _getCustomisedMessage: function RuleConfig__getCustomisedMessage(configDef)
      {
         var messageKey = "customise." + configDef.name + "." + this.options.mode,
            message = this.msg(messageKey);
         return message != messageKey ? message : null;
      },

      /**
       * Creates a message key to use when looking for a custom display message for the config def.
       * Will create a new parameters div element where it will render the result from the customised message.
       * It will parse message and will:
       * - add regular text in message in the new div as span elements
       * - for each {param.xxx} it finds it check the param name (xxx) and use that name to find the element inside
       *   the old parameters div and insert that span into the new.
       *
       * @method _renderByCustomisedMessage
       * @param configEl {HTMLElement} The config row
       * @return {string} The customised message
       * @protected
       */
      _renderByCustomisedMessage: function RuleConfig__renderByCustomisedMessage(configEl, customisedMessage)
      {
         var newParametersEl = document.createElement("div"),
            startIndex,
            endIndex,
            token,
            param,
            paramValueEl,
            paramName;
         Dom.addClass(newParametersEl, "parameters");
         configEl.insertBefore(newParametersEl, Selector.query("div.parameters", configEl, true));
         if (customisedMessage)
         {
            // Fix for ALF-8776 to simulate the usual Alfresco.util.message behaviour,
            // where msg keys that gets input parameters/tokens must use double apostrophes.
            customisedMessage = customisedMessage.replace(/''/g, "'");
         }
         while (customisedMessage)
         {
            startIndex = customisedMessage.indexOf("{");
            endIndex = customisedMessage.indexOf("}");
            if (startIndex > -1 && endIndex > -1)
            {
               token = customisedMessage.substring(0, customisedMessage.indexOf("{"));
               param = customisedMessage.substring(customisedMessage.indexOf("{") + 1, customisedMessage.indexOf("}"));
               customisedMessage = customisedMessage.substring(customisedMessage.indexOf("}") + 1);
            }
            else
            {
               token = customisedMessage;
               param = null;
               customisedMessage = null;
            }
            if (token)
            {
               newParametersEl.appendChild(document.createTextNode(token));
            }
            if (param)
            {
               if (param.indexOf("param.") == 0)
               {
                  paramName = param.substring(param.indexOf(".") + 1);
                  paramValueEl = Selector.query("div.parameters span.paramname_" + paramName + " span", configEl, true);
                  if (paramValueEl)
                  {
                     var paramWrapperEl = document.createElement("span");
                     paramWrapperEl.setAttribute("class", paramValueEl.parentNode.getAttribute("class"));
                     Dom.removeClass(paramValueEl, "param");
                     Dom.addClass(paramValueEl, "custom-param");
                     paramWrapperEl.appendChild(paramValueEl);
                     newParametersEl.appendChild(paramWrapperEl);
                  }
               }
               else if (param == ("name"))
               {
                  newParametersEl.appendChild(Selector.query("div.name span", configEl, true));
               }
            }
         }
      },

      /**
       * Looks up the configDef depending on the selection in the select menu
       * and makes a deepCopy of the object and sets the internal "_id" from
       * the configEl.
       *
       * @method _getSelectedConfigDef
       * @param configEl
       */
      _getSelectedConfigDef: function RuleConfig__getSelectedConfigDef(configEl)
      {
         // Find the correct config definition by looking in the config type menu
         var selectEl = Selector.query('select', configEl)[0];
         if (selectEl.selectedIndex > -1)
         {
            var optionEl = selectEl.options[selectEl.selectedIndex];
            var configDef = this._configDefs[optionEl.getAttribute("rel") + "_" + optionEl.value];
            if (configDef)
            {
               configDef._id = configEl.getAttribute("id");
               return configDef;
            }
         }
         return null;
      },

      /**
       * Method that locates the config customisation
       *
       * @method getConfigCustomisation
       * @param itemType
       * @param configDef
       * @return {object} A RuleConfig parameter renderer
       * @protected
       */
      _getConfigCustomisation: function RuleConfig__getConfigCustomisation(itemType, configDef)
      {
         var customisationOpt,
            customisationOptValues,
            customisationOptPattern,
            hasWildcard;

         // Make 2 passes though the options, first collect all exact matches then the ones matching wildcards
         for (var pass = 1; pass <= 2; pass++)
         {
            for (var gi = 0, gil = this.options.customisationsMap.length; gi < gil; gi++)
            {
               customisationOpt = this.options.customisationsMap[gi];
               if (customisationOpt.hasOwnProperty(itemType))
               {
                  customisationOptValues = customisationOpt[itemType];
                  if (YAHOO.lang.isArray(customisationOptValues))
                  {
                     customisationOptPattern = customisationOptValues[0];
                     hasWildcard = this._hasWildcard(customisationOptValues);
                     if ((pass == 1 && !hasWildcard) || (pass == 2 && hasWildcard))
                     {
                        if (Alfresco.util.objectMatchesPattern(configDef, customisationOptPattern))
                        {
                           return this.customisations[customisationOptValues[1]];
                        }
                     }
                  }
               }
            }
         }
         return null;
      },

      /**
       * Locates the rule parameter renderer
       *
       * @method _getParamRenderer
       * @param paramDefType
       * @return {object} A RuleCOnfig parameter renderer
       */
      _getParamRenderer: function RuleConfig__getParamRenderer(paramDefType)
      {
         return this.renderers[paramDefType];
      },
      
      /**
       * Makes sure that the first config row's '-'/(minus)-button is disabled if there is only one row present
       *
       * @method _refreshRemoveButtonState
       */
      _refreshRemoveButtonState: function RuleConfig__refreshRemoveButtonState()
      {
         var buttons = Selector.query("li div.actions .remove-config", this.id + "-configs");
         for (var i = 0, il = buttons.length, buttonId; i < il; i++)
         {
            buttonId = buttons[i].getAttribute("id");
            if (buttonId)
            {
               YAHOO.widget.Button.getButton(buttonId).set("disabled", i == 0 && buttons.length == 1);
            }
         }
      },

      /**
       * Returns true if the object contains an attribute value equal to "*"
       *
       * @method _hasWildcard
       * @param obj {object} The click event
       * @return {boolean} true if obj contains an attribute with "*" as the value
       */
      _hasWildcard: function RuleConfig__hasWildcard(obj)
      {
         for (var attr in obj)
         {
            if (obj.hasOwnProperty(attr) && obj[attr] == "*")
            {
               return true;
            }
         }
         return false;
      },

      /**
       * CUSTOMISATIONS
       */

      customisations: {

      },

      /**
       * PARAMETER RENDERERS
       */

      renderers:
      {
         "d:any":
         {
            text: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               return this._createValueSpan(containerEl, configDef, paramDef, ruleConfig, value);
            },
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               return this._createInputText(containerEl, configDef, paramDef, [], value);
            }
         },

         "d:text":
         {
            text: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               return this._createValueSpan(containerEl, configDef, paramDef, ruleConfig, value);
            },
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               return this._createInputText(containerEl, configDef, paramDef, [], value);
            }
         },

         "d:mltext":
         {
            text: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               return this._createValueSpan(containerEl, configDef, paramDef, ruleConfig, value);
            },
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               return this._createInputText(containerEl, configDef, paramDef, [], value);
            }
         },

         "d:content":
         {
            text: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               return this._createValueSpan(containerEl, configDef, paramDef, ruleConfig, value);
            },
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               return this._createInputText(containerEl, configDef, paramDef, [], value);
            }
         },

         "d:int":
         {
            text: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               return this._createValueSpan(containerEl, configDef, paramDef, ruleConfig, value);
            },
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               // todo limit validator to int's limit
               return this._createInputText(containerEl, configDef, paramDef, [Alfresco.forms.validation.number], value);
            }
         },

         "d:long":
         {
            text: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               return this._createValueSpan(containerEl, configDef, paramDef, ruleConfig, value);
            },
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               // todo limit validator to long's limit
               return this._createInputText(containerEl, configDef, paramDef, [Alfresco.forms.validation.number], value);
            }
         },

         "d:float":
         {
            text: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               return this._createValueSpan(containerEl, configDef, paramDef, ruleConfig, value);
            },
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               // todo add float validator
               return this._createInputText(containerEl, configDef, paramDef, [], value);
            }
         },

         "d:double":
         {
            text: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               return this._createValueSpan(containerEl, configDef, paramDef, ruleConfig, value);
            },
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               // todo add double validator
               return this._createInputText(containerEl, configDef, paramDef, [], value);
            }
         },

         "d:date":
         {
            text: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               return this._createDateSpan(containerEl, configDef, paramDef, ruleConfig, value);
            },
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               return this._createDatePicker(containerEl, configDef, paramDef, [], value, false);
            }
         },

         "d:datetime":
         {
            text: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               return this._createDateSpan(containerEl, configDef, paramDef, ruleConfig, value);
            },
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               return this._createDatePicker(containerEl, configDef, paramDef, [], value, true);
            }
         },

         "d:boolean":
         {
            text: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               return this._createValueSpan(containerEl, configDef, paramDef, ruleConfig, value ? this.msg("label.yes") : this.msg("label.no"));
            },
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               return this._createCheckbox(containerEl, configDef, paramDef, null, value);
            }
         },

         "d:qname":
         {
            text: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               return this._createValueSpan(containerEl, configDef, paramDef, ruleConfig, value);
            },
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               return this._createInputText(containerEl, configDef, paramDef, [], value);
            }
         },

         "d:noderef":
         {
            text: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               return this._createValueSpan(containerEl, configDef, paramDef, ruleConfig, value);
            },
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               return this._createInputText(containerEl, configDef, paramDef, [Alfresco.forms.validation.nodeRef], value);
            }
         },

         "d:childassocref":
         {
            text: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               return this._createValueSpan(containerEl, configDef, paramDef, ruleConfig, value);
            },
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               return this._createInputText(containerEl, configDef, paramDef, [Alfresco.forms.validation.nodeRef], value);
            }
         },

         "d:path":
         {
            text: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               return this._createValueSpan(containerEl, configDef, paramDef, ruleConfig, value);
            },
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               return this._createInputText(containerEl, configDef, paramDef, [], value);
            }
         },

         "d:category":
         {
            manual: { edit: true },
            text: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               return this._createValueSpan(containerEl, configDef, paramDef, ruleConfig, value);
            },
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               // todo display category picker
            }
         },

         "d:locale":
         {
            text: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               return this._createValueSpan(containerEl, configDef, paramDef, ruleConfig, value);
            },
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               return this._createInputText(containerEl, configDef, paramDef, [], value);
            }
         },

         "d:version":
         {
            text: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               return this._createValueSpan(containerEl, configDef, paramDef, ruleConfig, value);
            },
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               return this._createInputText(containerEl, configDef, paramDef, [], value);
            }
         }

      },

      
      /**
       * RENDERER HELPERS
       */

      _createInputText: function (containerEl, configDef, paramDef, validators, value)
      {
         if (paramDef._type == "hidden")
         {
            return this._createInputOfType(containerEl, configDef,paramDef, validators, value, "hidden");
         }
         else
         {
            return this._createInputOfType(containerEl, configDef,paramDef, validators, value, "text");
         }
      },

      _createInputOfType: function (containerEl, configDef, paramDef, validators, value, type)
      {
         var el = document.createElement("input");
         el.setAttribute("type", type);
         el.setAttribute("name", "-");
         el.setAttribute("title", paramDef.displayLabel ? paramDef.displayLabel : paramDef.name);
         el.setAttribute("param", paramDef.name);
         el.setAttribute("value", (value != undefined && value != null) ? value : "");
         containerEl.appendChild(el);
         if (paramDef.isMandatory)
         {
            this._addValidation(el, Alfresco.forms.validation.mandatory, configDef);
         }
         for (var i = 0, il = validators ? validators.length : 0; i < il; i++)
         {
            this._addValidation(el, validators[i], configDef);
         }
         return el;
      },

      _createLabel: function (text, forEl, alignToRight)
      {
         if (text && forEl)
         {
            var id = forEl.getAttribute("id") ? forEl.getAttribute("id") : Alfresco.util.generateDomId(forEl),
                  labelEl = document.createElement("label");
            labelEl.setAttribute("for", id);
            labelEl.appendChild(document.createTextNode(text));
            if (alignToRight)
            {
               forEl.parentNode.appendChild(labelEl);
            }
            else
            {
               forEl.parentNode.insertBefore(labelEl, forEl);
            }
            return labelEl;
         }
      },
      
      _createSelect: function (containerEl, configDef, paramDef, constraintOptions, value)
      {
         if (paramDef._type == "hidden")
         {
            return this._createInputOfType(containerEl, configDef, paramDef, [], value, "hidden");
         }
         else
         {
            var selectEl = document.createElement("select");
            YAHOO.util.Dom.addClass(selectEl, "suppress-validation");
            selectEl.setAttribute("name", "-");
            selectEl.setAttribute("title", paramDef.displayLabel ? paramDef.displayLabel : paramDef.name);
            selectEl.setAttribute("param", paramDef.name);
            if (paramDef.isMultiValued)
            {
               selectEl.setAttribute("multiple", "true");
               selectEl.setAttribute("size", "3");
            }
            if (containerEl)
            {
               containerEl.appendChild(selectEl);
            }
            if (!paramDef.isMandatory || !constraintOptions || constraintOptions.length == 0)
            {
               /**
                * Create an empty options to select none-value OR
                * since if there are no options in the select an error will be thrown
                */
               selectEl.appendChild(document.createElement("option"));
            }
            if (paramDef.isMandatory)
            {
               this._addValidation(selectEl, Alfresco.forms.validation.mandatory, configDef, "change");
            }
            if (constraintOptions)
            {
               var constraintOption,
                     optionEl;
               for (var i = 0, l = constraintOptions.length; i < l; i++)
               {
                  constraintOption = constraintOptions[i];
                  optionEl = document.createElement("option");
                  optionEl.setAttribute("value", constraintOption.value);
                  optionEl.appendChild(document.createTextNode(constraintOption.displayLabel ? constraintOption.displayLabel : constraintOption.value));
                  if (constraintOption.value == value)
                  {
                     optionEl.setAttribute("selected", "true");
                  }
                  selectEl.appendChild(optionEl);
               }
            }
            return selectEl;
         }
      },

      _createCheckbox: function (containerEl, configDef, paramDef, constraintOptions, value)
      {
         if (paramDef._type == "hidden")
         {
            return this._createInputOfType(containerEl, configDef, paramDef, [], value, "hidden");
         }
         else
         {
            var checkBoxEl = document.createElement("input");
            checkBoxEl.type = "checkbox";
            checkBoxEl.value = "";
            checkBoxEl.setAttribute("name", "-");
            checkBoxEl.setAttribute("title", paramDef.displayLabel ? paramDef.displayLabel : paramDef.name);
            checkBoxEl.setAttribute("param", paramDef.name);
            if (containerEl)
            {
               containerEl.appendChild(checkBoxEl);
            }
            checkBoxEl.checked = YAHOO.lang.isBoolean(value) ? value : false;
            return checkBoxEl;
         }
      },

      _createDatePicker: function (containerEl, configDef, paramDef, constraintOptions, value, showTime)
      {
         if (paramDef._type == "hidden")
         {
            // todo Add in custom validator that checks value against date pattern
            return this._createInputOfType(containerEl, configDef, paramDef, [], "hidden");
         }
         else
         {
            var valueEl = this._createInputOfType(containerEl, configDef, paramDef, [], value, "hidden"),
                  valueId = valueEl.getAttribute("id") ? valueEl.getAttribute("id") : Alfresco.util.generateDomId(valueEl);
            containerEl.appendChild(valueEl);

            var datePickerParentEl = document.createElement("span");
            Dom.setStyle(datePickerParentEl, "position", "relative");
            var datePickerEl = document.createElement("div"),
                  datePickerId = Alfresco.util.generateDomId(datePickerEl);
            Dom.addClass(datePickerEl, "datepicker");

            var datePickerIconEl = document.createElement("a");
            Alfresco.util.setDomId(datePickerIconEl, datePickerId + "-icon");
            var datePickerImgEl = document.createElement("img");
            datePickerImgEl.setAttribute("src", Alfresco.constants.URL_RESCONTEXT + "components/form/images/calendar.png");
            Dom.addClass(datePickerImgEl, "datepicker-icon");
            datePickerIconEl.appendChild(datePickerImgEl);

            var displayDateEl = document.createElement("input");
            displayDateEl.setAttribute("name", "-");
            displayDateEl.setAttribute("title", paramDef.displayLabel ? paramDef.displayLabel : paramDef.name);
            displayDateEl.setAttribute("type", "text");
            Dom.addClass(displayDateEl, "datepicker-date");
            Alfresco.util.setDomId(displayDateEl, datePickerId + "-date");
            containerEl.appendChild(displayDateEl);

            if (showTime)
            {
               var displayTimeEl = document.createElement("input");
               displayTimeEl.setAttribute("name", "-");
               displayTimeEl.setAttribute("type", "text");
               Dom.addClass(displayTimeEl, "datepicker-time");
               Alfresco.util.setDomId(displayTimeEl, datePickerId + "-time");            
               containerEl.appendChild(displayTimeEl);
            }

            containerEl.appendChild(datePickerIconEl);
            containerEl.appendChild(datePickerParentEl);
            datePickerParentEl.appendChild(datePickerEl);
            var options = {
               showTime: showTime,
               submitTime: showTime,
               mandatory: paramDef.isMandatory,
               currentValue: (value && value != "") ? value : Alfresco.util.toISO8601(new Date()) 
            };
            var datePicker = new Alfresco.DatePicker(datePickerId, valueId).setOptions(options).setMessages(
            {
               "form.control.date-picker.choose": "",
               "form.control.date-picker.entry.date.format": this.msg("form.control.date-picker.entry.date.format"),
               "form.control.date-picker.display.date.format": this.msg("form.control.date-picker.display.date.format"),
               "form.control.date-picker.entry.time.format": this.msg("form.control.date-picker.entry.time.format"),
               "form.control.date-picker.display.time.format": this.msg("form.control.date-picker.display.time.format")
            });
            this._datePickerConfigDefMap[datePicker.id] = configDef;
            return valueEl;
         }
      },
      
      _createButton: function (containerEl, configDef, paramDef, ruleConfig, onClickHandler)
      {
         var buttonEl = document.createElement("button");
         Alfresco.util.generateDomId(buttonEl);
         containerEl.appendChild(buttonEl);
         var button = new YAHOO.widget.Button(buttonEl,
         {
            type: "button",
            label: paramDef._buttonLabel
         });
         button.on("click", onClickHandler, {
            configDef: configDef,
            ruleConfig: ruleConfig,
            paramDef: paramDef,
            containerEl: containerEl
         }, this);
         return button;
      },

      /**
       * Displays the value as text
       *
       * @method _createValueSpan
       * @param containerEl {HTMLElement} Element within which the new span tag will be created
       * @param configDef {object} Object describing the configuration
       * @param paramDef {object} Object describing the parameter
       * @param ruleConfig {object} Object describing the rule config
       * @param value {string} The value to display
       * @param msgKey {string} (Optional) if a i18n message shall be used to enhance the display
       */
      _createValueSpan: function RC__createValueSpan(containerEl, configDef, paramDef, ruleConfig, value, msgKey)
      {
         var valueEl = document.createElement("span");
         if (value && paramDef._type != "hidden")
         {
            if (paramDef.constraint)
            {
               var constraintValues = this._getConstraintValues(paramDef, ruleConfig);
               for (var i = 0, il = constraintValues.length; i < il; i++)
               {
                  if (constraintValues[i].value == value)
                  {
                     value = constraintValues[i].displayLabel ? constraintValues[i].displayLabel : value;
                     break;
                  }
               }
            }
            if (paramDef.type = "d:noderef" && paramDef._type == "path")
            {
               return this._createPathSpan(containerEl, configDef, paramDef, this.id + "-" + configDef._id + "-" + paramDef.name, value);
            }
            else if (paramDef.type = "d:noderef" && paramDef._type == "category")
            {
               return this._createCategorySpan(containerEl, configDef, paramDef, this.id + "-" + configDef._id + "-" + paramDef.name, value);
            }
            if (msgKey)
            {
               var tmp = this.msg(msgKey, value);
               value = tmp != msgKey ? tmp : value;
            }
            valueEl.appendChild(document.createTextNode(value));
         }
         containerEl.appendChild(valueEl);
         return valueEl;
      },

      /**
       * Format the date and display it using _createValueSpan
       *
       * @method _createDateSpan
       * @param containerEl {HTMLElement} Element within which the new span tag will be created
       * @param configDef {object} Object describing the configuration
       * @param paramDef {object} Object describing the parameter
       * @param ruleConfig {object} Object describing the rule config
       * @param value {string} The value to display
       * @param msgKey {string} (Optional) if a i18n message shall be used to enhance the display
       */
      _createDateSpan: function RC__createDateSpan(containerEl, configDef, paramDef, ruleConfig, value, msgKey)
      {
         value = Alfresco.util.formatDate(Alfresco.util.fromISO8601(value));
         return this._createValueSpan(containerEl, configDef, paramDef, ruleConfig, value, msgKey);
      },

      /**
       * Populate a category from a nodeRef.
       *
       * @method _createCategorySpan
       * @param containerEl {HTMLElement} Element within which the new span tag will be created
       * @param id {string} Dom ID to be given to span tag
       * @param nodeRef {string} NodeRef of folder
       */
      _createCategorySpan: function (containerEl, configDef, paramDef, id, nodeRef)
      {
         var url = nodeRef ? Alfresco.constants.PROXY_URI + "api/forms/picker/items" : null;
         return this._createResolvableValueSpan(containerEl, id, Alfresco.util.Ajax.POST, url,
         {
            items: [nodeRef]
         }, function (json)
         {
            var item = json.data.items[0];
            return $html(item.name);
         });
      },

      /**
       * Populate a folder path from a nodeRef.
       *
       * @method _createPathSpan
       * @param containerEl {HTMLElement} Element within which the new span tag will be created
       * @param id {string} Dom ID to be given to span tag
       * @param nodeRef {string} NodeRef of folder
       */
      _createPathSpan: function (containerEl, configDef, paramDef, id, nodeRef)
      {
         var pathEl = document.createElement("span");
         Alfresco.util.setDomId(pathEl, id);
         containerEl.appendChild(pathEl);
         new Alfresco.Location(pathEl).setOptions(
         {
            siteId: this.options.siteId,
            rootNode: this.options.rootNode
         }).displayByNodeRef(nodeRef);
         return pathEl;
      },

      /**
       * Populate a folder path from a nodeRef.
       *
       * @method _createResolvableValueSpan
       * @param containerEl {HTMLElement} Element within which the new span tag will be created
       * @param id {string} Dom ID to be given to span tag
       * @param url {string} The url to cal to get the display label
       */
      _createResolvableValueSpan: function (containerEl, id, method, url, dataObj, displayValueHandler)
      {
         var pathEl = document.createElement("span");         
         Alfresco.util.setDomId(pathEl, id);
         if (url)
         {
            pathEl.innerHTML = this.msg("message.loading");

            // Find the path for the value
            var config =
            {
               method: method,
               url: url,
               successCallback:
               {
                  fn: function (response, obj)
                  {
                     obj.pathEl.innerHTML = obj.displayValueHandler.call(this, response.json);
                  },
                  obj:
                  {
                     pathEl: pathEl,
                     displayValueHandler: displayValueHandler
                  },
                  scope: this
               },
               failureCallback:
               {
                  fn: function (response, obj)
                  {
                     obj.pathEl.innerHTML = this.msg("message.failure");
                  },
                  obj:
                  {
                     pathEl: pathEl,
                     displayValueHandler: displayValueHandler
                  },
                  scope: this
               }
            };
            if (dataObj)
            {
               config.dataObj = dataObj;
            }
            Alfresco.util.Ajax.jsonRequest(config);
         }
         else
         {
            pathEl.innerHTML = this.msg("label.none");
         }
         containerEl.appendChild(pathEl);
         return pathEl;
      },

      /**
       *
       * @param el
       * @param validator
       * @param configDef
       * @param event
       * @param isMandatoryValidator (Optional) Set to tru if another validator then Alfresco.forms.validation.mandatory is used
       * @private
       */
      _addValidation: function (el, validator, configDef, event, isMandatoryValidator)
      {
         var mandatory = validator == Alfresco.forms.validation.mandatory || isMandatoryValidator;
         if (el && validator && this.options.form)
         {
            var id = el.getAttribute("id") ? el.getAttribute("id") : Alfresco.util.generateDomId(el),
               validationArgs = {
                  configDef: configDef,
                  me: this,
                  handler: validator
               };

            // Add validator to forms runtime
            this.options.form.addValidation(id, function (field, args, event, form, silent, message)
            {
               var valid = args.handler(field, args, event, form, silent, message);
               if (!valid)
               {
                  YAHOO.util.Dom.addClass(args.configDef._id, "invalid");
               }
               else if (valid && YAHOO.util.Dom.hasClass(args.configDef._id, "invalid"))
               {
                  // Make sure all other fields are valid as well before we display it as valid
                  if (args.me._validateConfigDef(args.configDef))
                  {
                     YAHOO.util.Dom.removeClass(args.configDef._id, "invalid");
                  }
               }
               return valid;
            }, validationArgs, event ? event : "keyup", null, { validationType: mandatory ? "mandatory" :  "invalid" });

            /**
             * ...but also group together validator with other validators for the same config
             * so we can run all of them to decide if the config as a whole is valid or not.
             */
            if (!configDef._validations)
            {
               configDef._validations = [];
            }
            configDef._validations.push(
            {
               fieldId: id,
               args: validationArgs,
               handler: validator
            });
         }
      },

      /**
       * Will update the forms submit elements
       *
       * @mehtod _updateSubmitElements
       * @param configDef {object} (Optional) Will validate this configDef before updating submit elements
       * @protected
       */
      _updateSubmitElements: function (configDef)
      {
         if (this.options.form)
         {
            if (configDef)
            {
               if (this._validateConfigDef(configDef))
               {
                  Dom.removeClass(configDef._id, "invalid");
               }
               else
               {
                  Dom.addClass(configDef._id, "invalid");
               }
            }
            this.options.form.validate();
         }
      },

      _validateConfigDef: function (configDef)
      {
         if (YAHOO.lang.isArray(configDef._validations))
         {
            for (var i = 0, il = configDef._validations.length, validation; i < il; i++)
            {
               validation = configDef._validations[i];
               var el = Dom.get(validation.fieldId);
               if (!el.disabled && !validation.handler(el, validation.args, "keyup", this, true, null))
               {
                  return false;
               }
            }
         }
         return true;
      },

      _getParameters: function (configDef)
      {
         var paramEls = Selector.query("[param]", configDef._id),
            params = {},
            paramEl,
            paramName,
            paramValue,
            paramDef,
            previousValue;
         for (var i = 0, il = paramEls.length; i < il; i++)
         {
            paramEl = paramEls[i];
            paramName = paramEl.getAttribute("param");
            paramDef = this._getParamDef(configDef, paramName);
            paramValue = this._getValue(paramEl, paramDef);
            if (paramDef)
            {
               if (paramDef.isMultiValued && paramValue && !YAHOO.lang.isArray(paramValue))
               {
                  paramValue = [paramValue];
               }
               // Convert string to proper type
               if (YAHOO.lang.isArray(paramValue))
               {
                  for (var j = 0, jl = paramValue.length; j < jl; j++)
                  {
                     paramValue[j] = this._convertType(paramValue[j], paramDef.type);
                  }
               }
               else
               {
                  paramValue = this._convertType(paramValue, paramDef.type);
               }
               previousValue = params[paramName];
               if (YAHOO.lang.isArray(previousValue))
               {
                  if (YAHOO.lang.isArray(paramValue))
                  {
                     paramValue = previousValue.concat(paramValue);
                  }
                  else if (paramValue != null)
                  {
                     paramValue = previousValue.push(paramValue);
                  }
               }
               if (paramName && paramValue != undefined && paramValue != null)
               {
                  params[paramName] = paramValue;
               }
            }
         }
         return params;
      },

      _convertType: function(paramValue, type)
      {
         if (paramValue)
         {
            if (Alfresco.util.arrayContains(["d:int", "d:long"], type))
            {
               return parseInt(paramValue);
            }
            else if (Alfresco.util.arrayContains(["d:float", "d:double"], type))
            {
               return parseFloat(paramValue);
            }
            else if (Alfresco.util.arrayContains(["d:boolean"], type))
            {
               if (paramValue.toLowerCase() == "true")
               {
                  return true;
               }
               else if (paramValue.toLowerCase() == "false")
               {
                  return false;
               }
            }
         }
         return paramValue;
      },

      _getValue: function(el)
      {
         var tagName = el.tagName.toLowerCase();
         if (tagName == "select")
         {
            if (el.getAttribute("multiple") != "true")
            {
               return el.options[el.selectedIndex].value;
            }
            else
            {
               var values = [];
               for (var i = 0, il = el.options.length; i < il; i++)
               {
                  if (el.options[i].selected)
                  {
                     values.push(el.options[i].value);
                  }
               }
               return values;
            }
         }
         else if (tagName == "input" && (el.type == "checkbox" || el.type == "radio"))
         {
            if (!el.value || el.value.length == 0)
            {
               return el.checked ? "true" : "false";
            }
         }
         return el.value && el.value.length > 0 ? el.value : null;
      },

      _getParamDef: function (configDef, paramName)
      {
         if (configDef.parameterDefinitions)
         {
            for (var i = 0, il = configDef.parameterDefinitions.length, paramDefinition; i < il; i++)
            {
               paramDefinition = configDef.parameterDefinitions[i];
               if (paramDefinition.name == paramName)
               {
                  return paramDefinition;
               }
            }
         }
         
         if (configDef.adHocPropertiesAllowed == true)
         {
            return {};
         }
         
         return null;
      },

      _setHiddenParameter: function (configDef, ruleConfig, paramName, paramValue)
      {
         var paramEls = Selector.query("[param=" + paramName + "]", configDef._id),
            paramDef = this._getParamDef(configDef, paramName);
         if (paramDef.isMultiValued && YAHOO.lang.isArray(paramValue))
         {
            // Remove previous hidden input elements that won't be needed ...
            for (var phei = paramEls.length, nhel = paramValue.length, phe; phei > nhel; phei--)
            {
               // Remove the previous hidden element from array and Dom
               phe = paramEls.pop();
               phe.parentNode.removeChild(phe);
            }
            // Set values (and create new input elements if needed)
            for (var pvi = 0, pvil = paramValue.length, paramEl, peil = paramEls.length; pvi < pvil; pvi++)
            {
               if (pvi >= peil)
               {
                  paramEl = this._getParamRenderer(paramDef.type)[this.options.mode].call(this, paramEls[0].parentNode, configDef, paramDef, ruleConfig, paramValue[pvi]);
                  Dom.addClass(paramEl, "param");
               }
               else
               {
                  paramEls[pvi].value = paramValue[pvi];
               }
            }
         }
         else
         {
            paramEls[0].value = paramValue;
         }
      },

      _hideParameters: function (parameterDefinitions)
      {
         if (parameterDefinitions)
         {
            for (var i = 0, il = parameterDefinitions.length, paramDef; i < il; i++)
            {
               paramDef = parameterDefinitions[i];
               paramDef._type = "hidden";
            }
         }
      },

      _setParameter: function (ruleConfig, parameterName, value)
      {
         if (!ruleConfig.parameterValues)
         {
            ruleConfig.parameterValues = {};
         }
         ruleConfig.parameterValues[parameterName] = value;         
      }

   });
})();
