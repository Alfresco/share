/**
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
 * RuleConfigAction.
 *
 * @namespace Alfresco
 * @class Alfresco.RuleConfigAction
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
   
   Alfresco.RuleConfigAction = function(htmlId)
   {
      Alfresco.RuleConfigAction.superclass.constructor.call(this, htmlId);

      // Re-register with our own name
      this.name = "Alfresco.RuleConfigAction";
      Alfresco.util.ComponentManager.reregister(this);

      // Instance variables
      this.customisations = YAHOO.lang.merge(this.customisations, Alfresco.RuleConfigAction.superclass.customisations);
      this.renderers = YAHOO.lang.merge(this.renderers, Alfresco.RuleConfigAction.superclass.renderers);
      return this;
   };

   YAHOO.extend(Alfresco.RuleConfigAction, Alfresco.RuleConfig,
   {

      /**
       * CUSTOMISATIONS
       */

      customisations:
      {
         // The "SetPropertyValue" key maps directly to the action name returned all the way from the 
         // repository. It does NOT map directly to the menu config from the rule-config-action.get.config.xml
         // as that only indicates how to lay out actions that are returned from the repository. This customisations
         // object will be checked within the "rule-config.js" script to determine if any actions require
         // custom renderers.
         SetPropertyValue:
         {
            edit: function(configDef, ruleConfig, configEl)
            {
               // Hide parameters since we are using a custom ui
               this._hideParameters(configDef.parameterDefinitions);

               // Add in a custom UI renderer for selecting the property. Some key things to note here:
               // 1) In the splice operation the original "value" parameter is removed - this is done so
               //    because the "arca:set-property-value" renderer will create a new parameter with the
               //    name "value" which will be a different control depending upon the data type (e.g.
               //    a Calendar widget for dates and an input box for other values, etc. It is important
               //    that the default input field is not just hidden but NEVER rendered so that it doesn't
               //    provide erroneous data in the form post.
               // 2) The "_destinationParam" of the new property does NOT map to anything that the 
               //    set value action executer will actually use - the renderer will copy across changes
               //    to the hidden text field.
               configDef.parameterDefinitions.splice(1,1,
               {
                  type: "arca:set-property-value",
                  _buttonLabel: this.msg("button.select-folder"),
                  _destinationParam: "_property"
               });
               return configDef;
            }
         },
         SpecialiseType:
         {
            edit: function(configDef, ruleConfig, configEl)
            {
               // Limit the available types to the ones specified in share-config.xml
               this._getParamDef(configDef, "type-name")._constraintFilter = "changeable";
               return configDef;
            }
         },

         AddFeatures:
         {
            edit: function(configDef, ruleConfig, configEl)
            {
               // Limit the available types to the ones specified in share-config.xml
               var ad = this._getParamDef(configDef, "aspect-name");
               ad._constraintFilter = "addable";
               ad.displayLabel = null;
               return configDef;
            }
         },

         RemoveFeatures:
         {
            edit: function(configDef, ruleConfig, configEl)
            {
               // Limit the available types to the ones specified in share-config.xml
               var ad = this._getParamDef(configDef, "aspect-name");
               ad._constraintFilter = "removeable";
               ad.displayLabel = null;
               return configDef;
            }
         },

         Script:
         {
            edit: function(configDef, ruleConfig, configEl)
            {
               this._getParamDef(configDef, "script-ref").displayLabel = null;
               return configDef;
            }
         },

         Select:
         {
            edit: function(configDef, ruleConfig, configEl)
            {
               configDef.parameterDefinitions = [
                  {
                     name: "mandatory",
                     type: "d:text",
                     isMandatory: true,
                     isMultiValued: false,
                     displayLabel: this.msg("label.selectAnAction"),
                     _type: "hidden"
                  }
               ];
               return configDef;
            }
         },

         Mail:
         {
            edit: function(configDef, ruleConfig, configEl)
            {
               this._hideParameters(configDef.parameterDefinitions);
               configDef.parameterDefinitions.push({
                  type: "arca:email-dialog-button",
                  _buttonLabel: this.msg("button.message")
               });
               return configDef;
            }
         },

         CheckIn:
         {
            text: function(configDef, ruleConfig, configEl)
            {
               // Display minor/major instead of Yes/No
               ruleConfig.parameterValues = ruleConfig.parameterValues || {};
               if (ruleConfig.parameterValues["minorChange"])
               {
                  ruleConfig.parameterValues["minorChange"] = this.msg("label.checkin.minor");
               }
               else
               {
                  ruleConfig.parameterValues["minorChange"] = this.msg("label.checkin.major");
               }
               this._getParamDef(configDef, "minorChange").type = "d:text";
               return configDef;
            },
            edit: function(configDef, ruleConfig, configEl)
            {
               this._hideParameters(configDef.parameterDefinitions);
               configDef.parameterDefinitions.push({
                  type: "arca:checkin-dialog-button",
                  _buttonLabel: this.msg("button.options")
               });
               return configDef;
            }
         },

         Checkout:
         {
            text: function(configDef, ruleConfig, configEl)
            {
               // Display as path
               this._getParamDef(configDef, "destination-folder")._type = "path";
               return configDef;
            },
            edit: function(configDef, ruleConfig, configEl)
            {
               // Note: Destination folder isn't mandatory, the same folder as the "current file" will be used  
               this._hideParameters(configDef.parameterDefinitions);
               this._setParameter(ruleConfig, "assoc-type", "cm:contains");
               this._setParameter(ruleConfig, "assoc-name", "cm:checkout");
               configDef.parameterDefinitions.push({
                  type: "arca:destination-dialog-button",
                  displayLabel: this.msg("label.workingCopyLocation"),
                  _buttonLabel: this.msg("button.select-folder"),
                  _destinationParam: "destination-folder"
               });
               return configDef;
            }
         },

         Copy:
         {
            text: function(configDef, ruleConfig, configEl)
            {
               // Display as path
               this._getParamDef(configDef, "destination-folder")._type = "path";

               // Use special text if we are making a deep copy
               if (ruleConfig.parameterValues && ruleConfig.parameterValues["deep-copy"])
               {
                  configDef._customMessageKey = "customise.copy.deep.text";
               }

               return configDef;
            },
            edit: function(configDef, ruleConfig, configEl)
            {
               // Hide parameters since we are using a custom ui
               this._hideParameters(configDef.parameterDefinitions);

               // Make parameter renderer create a "Destination" button that displays an destination folder browser
               configDef.parameterDefinitions.splice(0,0,
               {
                  type: "arca:destination-dialog-button",
                  displayLabel: this.msg("label.to"),
                  _buttonLabel: this.msg("button.select-folder"),
                  _destinationParam: "destination-folder"
               });

               // Make deep-copy visible and make the display label be placed as a unit
               var deepCopy = this._getParamDef(configDef, "deep-copy");
               deepCopy._type = null;
               deepCopy._displayLabelToRight = true;
               deepCopy._hideColon = true;
               return configDef;
            }
         },

         Move:
         {
            text: function(configDef, ruleConfig, configEl)
            {
               // Display as path
               this._getParamDef(configDef, "destination-folder")._type = "path";

               return configDef;
            },
            edit: function(configDef, ruleConfig, configEl)
            {
               // Hide parameters since we are using a custom ui
               this._hideParameters(configDef.parameterDefinitions);

               // Make parameter renderer create a "Destination" button that displays an destination folder browser
               configDef.parameterDefinitions.splice(0,0,
               {
                  type: "arca:destination-dialog-button",
                  displayLabel: this.msg("label.to"),
                  _buttonLabel: this.msg("button.select-folder"),
                  _destinationParam: "destination-folder"
               });

               return configDef;
            }
         },

         SimpleWorkflow:
         {
            text: function(configDef, ruleConfig, configEl)
            {
               // Display as path and moves/copies instead of Yes/No
               this._getParamDef(configDef, "approve-folder")._type = "path";
               this._getParamDef(configDef, "approve-move").type = "d:text";
               if (ruleConfig.parameterValues["approve-move"] == true)
               {
                  ruleConfig.parameterValues["approve-move"] = this.msg("label.workflow.moves");
               }
               else
               {
                  ruleConfig.parameterValues["approve-move"] = this.msg("label.workflow.copies");
               }
               if (ruleConfig.parameterValues && ruleConfig.parameterValues["reject-step"])
               {
                  // Change the custom message key to use and display 
                  configDef._customMessageKey = "customise.simple-workflow.full.text";
                  this._getParamDef(configDef, "reject-folder")._type = "path";
                  this._getParamDef(configDef, "reject-move").type = "d:text";
                  if (ruleConfig.parameterValues["reject-move"] == true)
                  {
                     ruleConfig.parameterValues["reject-move"] = this.msg("label.workflow.moves");
                  }
                  else
                  {
                     ruleConfig.parameterValues["reject-move"] = this.msg("label.workflow.copies");
                  }
               }
               return configDef;
            },
            edit: function(configDef, ruleConfig, configEl)
            {
               // Hide all parameters since we are using a cusotm ui
               this._hideParameters(configDef.parameterDefinitions);

               // Make approve parameters mandatory (not changed in repo for legacy reasons)
               this._getParamDef(configDef, "approve-step").isMandatory = true;
               this._getParamDef(configDef, "approve-folder").isMandatory = true;
               this._getParamDef(configDef, "approve-move").isMandatory = true;

               // Make reject parameters mandatory (will only be used when reject is enabled and inputs are enabled)
               this._getParamDef(configDef, "reject-step").isMandatory = true;
               this._getParamDef(configDef, "reject-folder").isMandatory = true;
               this._getParamDef(configDef, "reject-move").isMandatory = true;

               // Make parameter renderer create an "Approve" button that displays an approve simple workflow dialog
               configDef.parameterDefinitions.push(
               {
                  type: "arca:simple-workflow-dialog-button",
                  _buttonLabel: this.msg("button.approve"),
                  _mode: Alfresco.module.RulesWorkflowAction.VIEW_MODE_APPROVAL_STEP
               });

               // Make parameter renderer create a "Reject" button that displays a reject simple workflow dialog
               configDef.parameterDefinitions.push(
               {
                  type: "arca:simple-workflow-dialog-button",
                  _buttonLabel: this.msg("button.reject"),
                  _mode: Alfresco.module.RulesWorkflowAction.VIEW_MODE_REJECTION_STEP
               });
               return configDef;
            }
         },

         LinkCategory:
         {
            text: function(configDef, ruleConfig, configEl)
            {
               // Resolve category nodeRef
               this._getParamDef(configDef, "category-value")._type = "category";
               return configDef;
            },
            edit: function(configDef, ruleConfig, configEl)
            {
               this._hideParameters(configDef.parameterDefinitions);
               configDef.parameterDefinitions.push(
               {
                  type: "arca:category-picker"
               });
               return configDef;
            }
         },

         Transform:
         {
            text: function(configDef, ruleConfig, configEl)
            {
               // Display as path
               this._getParamDef(configDef, "destination-folder")._type = "path";
               return configDef;
            },
            edit: function(configDef, ruleConfig, configEl)
            {
               // Hide all parameters but mime-type adn set mandatory values
               this._hideParameters(configDef.parameterDefinitions);
               this._getParamDef(configDef, "mime-type")._type = null;
               this._setParameter(ruleConfig, "assoc-type", "cm:contains");
               this._setParameter(ruleConfig, "assoc-name", "cm:copy");

               // Make parameter renderer create a "Destination" button that displays an destination folder browser
               configDef.parameterDefinitions.push({
                  type: "arca:destination-dialog-button",
                  displayLabel: this.msg("label.to"),
                  _buttonLabel: this.msg("button.select-folder"),
                  _destinationParam: "destination-folder"
               });
               return configDef;
            }
         },

         Import:
         {
            text: function(configDef, ruleConfig, configEl)
            {
               // Display as path
               this._getParamDef(configDef, "destination")._type = "path";
               return configDef;
            },
            edit: function(configDef, ruleConfig, configEl)
            {
               // Hide all parameters
               this._hideParameters(configDef.parameterDefinitions);

               // Make parameter renderer create a "Destination" button that displays an destination folder browser
               configDef.parameterDefinitions.push({
                  type: "arca:destination-dialog-button",
                  displayLabel: this.msg("label.to"),
                  _buttonLabel: this.msg("button.select-folder"),
                  _destinationParam: "destination"
               });
               return configDef;
            }
         }


      },

      /**
       * RENDERERS
       */

      renderers:
      {
         "arca:set-property-value":
         {
            manual: { edit: true },
            currentCtx: {},
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               this.renderers["arca:set-property-value"].currentCtx =
               {
                  configDef: configDef,
                  ruleConfig: ruleConfig,
                  paramDef: paramDef
               };
               
               // Create a new picker for selecting properties, but update the update the options to
               // override the default tabs to constrain the values loaded to a simple set that can
               // be assigned from a simple HTML field...
               var propPicker = new Alfresco.module.PropertyPicker(this.id + "-selectSetPropertyDialog").setOptions({
                  tabs: [
                     {
                        id: "properties",
                        treeNodes: [
                           {
                              id: "all",
                              listItems:
                              {
                                 url: "{url.proxy}api/properties?type=d:text&type=d:mltext&type=d:int&type=d:long&type=d:float&type=d:double&type=d:date&type=d:boolean&type=d:qname",
                                 dataModifier: function (listItemObjs, descriptorObj)
                                 {
                                    return this._addTransientProperties(listItemObjs);
                                 },
                                 id: "{item.name}",
                                 type: "{item.dataType}",
                                 label: "{item.title}",
                                 title: "{item.name}"
                              }
                           },
                           {
                              id: "aspects",
                              treeNodes:
                              {
                                 url: "{url.proxy}api/classes?cf=aspect",
                                 id: "{node.name}",
                                 title: "{node.name}",
                                 label: "{node.title}",
                                 listItems:
                                 {
                                    url: "{url.proxy}api/classes/{node.name}/properties?type=d:text&type=d:mltext&type=d:int&type=d:long&type=d:float&type=d:double&type=d:date&type=d:boolean&type=d:qname",
                                    dataModifier: function (listItemObjs, descriptorObj)
                                    {
                                       return this._addTransientProperties(listItemObjs);
                                    },
                                    id: "{item.name}",
                                    type: "{item.dataType}",
                                    label: "{item.title}",
                                    title: "{item.name}"
                                 }
                              }
                           },
                           {
                              id: "types",
                              treeNodes:
                              {
                                 url: "{url.proxy}api/classes?cf=type",
                                 id: "{node.name}",
                                 title: "{node.name}",
                                 label: "{node.title}",
                                 listItems:
                                 {
                                    url: "{url.proxy}api/classes/{node.name}/properties?type=d:text&type=d:mltext&type=d:int&type=d:long&type=d:float&type=d:double&type=d:date&type=d:boolean&type=d:qname",
                                    dataModifier: function (listItemObjs, descriptorObj)
                                    {
                                       return this._addTransientProperties(listItemObjs);
                                    },
                                    id: "{item.name}",
                                    type: "{item.dataType}",
                                    label: "{item.title}",
                                    title: "{item.name}"
                                 }
                              }
                           }
                        ],
                        listItems: []
                     }
                  ]
               });
               // Set an event group on the picker to ensure that each picker only responds to it's own selections...
               propPicker.eventGroup = propPicker;
               
               // Set the context on the picker object as it is not safe to rely on the context assigned to the renderer
               // as it will always contain the data for the last rendered action configuration and not the target
               // action. The dataItemSelected listener will use this context to accurately update the appropriate hidden
               // values. This is necessary because the _setHiddenParameter function relies on finding a parameter element
               // within an element identified by a value in the configDef...
               propPicker.currentCtx = {
                  configDef: configDef,
                  ruleConfig: ruleConfig,
                  paramDef: paramDef
               };
               
               // This connects an event handler to the property picker so that when a new property
               // is picked the parameters are re-rendered. This is particularly important when 
               // the property data-type changes as a different control may need to be rendered...
               YAHOO.Bubbling.on("dataItemSelected", function (layer, args) {
                  if ($hasEventInterest(propPicker, args))
                  {
                     // Clear the old elements (but not the picker button)...
                     while (containerEl.children.length > 1) {
                        containerEl.removeChild(containerEl.lastChild);
                     }
                     
                     // Get the property that was selected from the picker and get the renderer
                     // for it's data type...
                     var property = args[1].selectedItem.item;
                     var renderer = this.renderers[property.dataType];
                     
                     // Create a label for the selected property...
                     var selectedPropLabelEl = document.createElement("label");
                     selectedPropLabelEl.innerHTML = this.msg("label.setProperty.property");
                     containerEl.appendChild(selectedPropLabelEl);
                     
                     // Create an element to display the selected property...
                     var selectedPropEl = document.createElement("span");
                     selectedPropEl.innerHTML = property.name;
                     containerEl.appendChild(selectedPropEl);
                     
                     var selectedPropValueLabelEl = document.createElement("label");
                     selectedPropValueLabelEl.innerHTML = this.msg("label.setProperty.value");
                     containerEl.appendChild(selectedPropValueLabelEl);
                     
                     // Update the hidden control that will provide the property value
                     // when the form is submitted...
                     var ctx = this.renderers["arca:set-property-value"].currentCtx;
                     this._setHiddenParameter(propPicker.currentCtx.configDef, propPicker.currentCtx.ruleConfig, "property", property.name);
                     this._updateSubmitElements(propPicker.currentCtx.configDef);
                     
                     // Create a new renderer for the value (using the appropriate renderer for the
                     // property type)... note that the "name" of the parameter is set to value as this
                     // control will be used in the form submit to set the value on the action...
                     if (renderer && 
                         typeof this.renderers[property.dataType].edit === "function")
                     {
                        var _configDef = {};
                        var _paramDef = {
                           displayLabel: this.msg("label.setProperty.value"),
                           isMandatory: true,
                           isMultiValued: false,
                           name: "value",
                           type: property.dataType
                        };
                        this.renderers[property.dataType].edit.call(this, containerEl, _configDef, _paramDef, null, null);
                     }
                     
                     // Create a field to store the type...
                     // It is important that the property type is stored to ensure that when the folder rule is
                     // edited it will be possible to select the appropriate renderer for the property. Ad-hoc
                     // properties have been enabled on the action executer to ensure that this additional information
                     // can be persisted. Without this data it would not be possible to render the appropriate 
                     // form control without making an XHR request back to the server...
                     this._createInputText(containerEl, {}, {
                        _type: "hidden",
                        name: "prop_type",
                        isMandatory: false,
                        isMultiValued: false,
                        displayLabel: "prop_type"
                     }, null, property.dataType);
                     this._setParameter(propPicker.currentCtx.ruleConfig, "prop_type", property.dataType);
                     this._updateSubmitElements(propPicker.currentCtx.configDef);
                  }
               }, this);
               
               // Create a new button to allow the user to select a property...
               var button = this._createButton(containerEl, configDef, paramDef, ruleConfig, function RCA_setPropertyValueButton_onClick(type, obj) {
                  propPicker.showDialog();
               });
               
               // Display the currently set property details...
               // It's important that this information is setup up correctly when previously configured actions
               // are displayed to ensure that if no changes are made that the original data is reset correctly.
               // If the form elements are not set up then the original data will be lost. If the user makes
               // a change to the property to set then the form elements will be replaced...
               if (ruleConfig.parameterValues && ruleConfig.parameterValues.property != null)
               {
                  var selectedPropLabelEl = document.createElement("label");
                  selectedPropLabelEl.innerHTML = this.msg("label.setProperty.property");
                  containerEl.appendChild(selectedPropLabelEl);
                  
                  var selectedPropEl = document.createElement("span");
                  selectedPropEl.innerHTML = ruleConfig.parameterValues.property;
                  containerEl.appendChild(selectedPropEl);
                  
                  var selectedPropValueLabelEl = document.createElement("label");
                  selectedPropValueLabelEl.innerHTML = this.msg("label.setProperty.value");
                  containerEl.appendChild(selectedPropValueLabelEl);
                  
                  var currRenderer = this.renderers[ruleConfig.parameterValues.prop_type];
                  if (currRenderer && 
                      typeof this.renderers[ruleConfig.parameterValues.prop_type].edit === "function")
                  {
                     var _configDef = {};
                     var _paramDef = {
                        displayLabel: "Value",
                        isMandatory: true,
                        isMultiValued: false,
                        name: "value",
                        type: ruleConfig.parameterValues.prop_type
                    };
                    this.renderers[ruleConfig.parameterValues.prop_type].edit.call(this, containerEl, _configDef, _paramDef, null, ruleConfig.parameterValues.value);
                  }
                  
                  this._createInputText(containerEl, {}, {
                     _type: "hidden",
                     name: "prop_type",
                     isMandatory: false,
                     isMultiValued: false,
                     displayLabel: "prop_type"
                  }, null, ruleConfig.parameterValues.prop_type);
                  
                  // Ensure the properties are set...
                  this._setParameter(ruleConfig, "property", ruleConfig.parameterValues.property);
                  this._setParameter(ruleConfig, "value", ruleConfig.parameterValues.value);
                  this._setParameter(ruleConfig, "prop_type", ruleConfig.parameterValues.prop_type);
                  this._updateSubmitElements(configDef);
               }
            }
         },
         "arca:email-dialog-button":
         {
            manual: { edit: true },
            currentCtx: {},
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               this._createButton(containerEl, configDef, paramDef, ruleConfig, function RCA_emailFormButton_onClick(type, obj)
               {
                  this.renderers["arca:email-dialog-button"].currentCtx =
                  {
                     configDef: obj.configDef,
                     ruleConfig: obj.ruleConfig
                  };
                  if (!this.widgets.emailForm)
                  {
                     this.widgets.emailForm = new Alfresco.module.EmailForm(this.id + "-emailForm");
                     YAHOO.Bubbling.on("emailFormCompleted", function (layer, args)
                     {
                        if ($hasEventInterest(this.widgets.emailForm, args))
                        {
                           var values = args[1].options;
                           if (values !== null)
                           {
                              var ctx = this.renderers["arca:email-dialog-button"].currentCtx;
                              this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, "to_many", values.recipients);
                              this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, "subject", values.subject);
                              this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, "text", values.message ? values.message : "");
                              this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, "template", values.template ? values.template : "");
                              this._updateSubmitElements(ctx.configDef);
                           }
                        }
                     }, this);
                  }
                  var params = this._getParameters(obj.configDef);
                  this.widgets.emailForm.showDialog(
                  {
                     recipients: params.to_many,
                     subject: params.subject,
                     message: params.text,
                     template: params.template
                  });
               });
            }
         },

         "arca:checkin-dialog-button":
         {
            manual: { edit: true },
            currentCtx: {},
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               this._createButton(containerEl, configDef, paramDef, ruleConfig, function RCA_checkinFormButton_onClick(type, obj)
               {
                  this.renderers["arca:checkin-dialog-button"].currentCtx =
                  {
                     configDef: obj.configDef,
                     ruleConfig: obj.ruleConfig
                  };
                  if (!this.widgets.checkInForm)
                  {
                     this.widgets.checkInForm = new Alfresco.module.RulesCheckinAction(this.id + "-checkinForm");
                     YAHOO.Bubbling.on("checkinConfigCompleted", function (layer, args)
                     {
                        if ($hasEventInterest(this.widgets.checkInForm, args))
                        {
                           var values = args[1].options;
                           if (values !== null)
                           {
                              var ctx = this.renderers["arca:checkin-dialog-button"].currentCtx;
                              this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, "minorChange", values.version == "minor" ? "true" : "false");
                              this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, "description", values.comments);
                              this._updateSubmitElements(ctx.configDef);
                           }
                        }
                     }, this);
                  }
                  var params = this._getParameters(obj.configDef);
                  this.widgets.checkInForm.showDialog(
                  {
                     version: params.minorChange ? "minor" : "major",
                     comments: params.description
                  });
               });
            }
         },

         "arca:destination-dialog-button":
         {
            manual: { edit: true },
            currentCtx: {},
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               this._createLabel(paramDef.displayLabel, containerEl); 
               var nodeRef = ruleConfig.parameterValues ? ruleConfig.parameterValues[paramDef._destinationParam] : null;
               this._createPathSpan(containerEl, configDef, paramDef, this.id + "-" + configDef._id + "-destinationLabel", nodeRef);
               this._createButton(containerEl, configDef, paramDef, ruleConfig, function RCA_destinationDialogButton_onClick(type, obj)
               {
                  this.renderers["arca:destination-dialog-button"].currentCtx =
                  {
                     configDef: obj.configDef,
                     ruleConfig: obj.ruleConfig,
                     paramDef: obj.paramDef
                  };
                  if (!this.widgets.destinationDialog)
                  {
                     this.widgets.destinationDialog = new Alfresco.module.DoclibGlobalFolder(this.id + "-destinationDialog");
                     this.widgets.destinationDialog.setOptions(
                     {
                        title: this.msg("dialog.destination.title")
                     });

                     YAHOO.Bubbling.on("folderSelected", function (layer, args)
                     {
                        if ($hasEventInterest(this.widgets.destinationDialog, args))
                        {
                           var selectedFolder = args[1].selectedFolder;
                           if (selectedFolder !== null)
                           {
                              var ctx = this.renderers["arca:destination-dialog-button"].currentCtx;
                              this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, ctx.paramDef._destinationParam, selectedFolder.nodeRef);
                              Alfresco.util.ComponentManager.get(this.id + "-" + ctx.configDef._id + "-destinationLabel").displayByPath(selectedFolder.path, selectedFolder.siteId, selectedFolder.siteTitle);
                              this._updateSubmitElements(ctx.configDef);
                           }
                        }
                     }, this);
                  }
                  var pathNodeRef = this._getParameters(obj.configDef)["destination-folder"],
                     allowedViewModes =
                     [
                        Alfresco.module.DoclibGlobalFolder.VIEW_MODE_SITE
                     ];

                  if (this.options.repositoryBrowsing === true)
                  {
                     allowedViewModes.push(Alfresco.module.DoclibGlobalFolder.VIEW_MODE_REPOSITORY, 
                                           Alfresco.module.DoclibGlobalFolder.VIEW_MODE_USERHOME, 
                                           Alfresco.module.DoclibGlobalFolder.VIEW_MODE_SHARED);
                  }
                  this.widgets.destinationDialog.setOptions(
                  {
                     allowedViewModes: allowedViewModes,
                     rootNode: this.options.rootNode,
                     pathNodeRef: pathNodeRef ? new Alfresco.util.NodeRef(pathNodeRef) : null
                  });
                  this.widgets.destinationDialog.showDialog();
               });
            }
         },

         "arca:simple-workflow-dialog-button":
         {
            manual: { edit: true },
            currentCtx: {},
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               var enableCheckboxEl,
                  RWA = Alfresco.module.RulesWorkflowAction,
                  prefix = paramDef._mode == RWA.VIEW_MODE_APPROVAL_STEP ? "approve" : "reject";
               if (paramDef._mode == RWA.VIEW_MODE_REJECTION_STEP)
               {
                  /**
                   * Add a checkbox that enables/disables the reject button and hidden input parameters
                   * listener will be attached later so the button object can be passed to callback
                   */
                  enableCheckboxEl = document.createElement("input");
                  enableCheckboxEl.setAttribute("type", "checkbox");
                  containerEl.appendChild(enableCheckboxEl);
               }

               // Create button that displays the simple workflow dialog
               var button = this._createButton(containerEl, configDef, paramDef, ruleConfig, function RCA_destinationDialogButton_onClick(type, obj)
               {
                  this.renderers["arca:simple-workflow-dialog-button"].currentCtx =
                  {
                     configDef: obj.configDef,
                     ruleConfig: obj.ruleConfig,
                     paramDef: obj.paramDef
                  };
                  if (!this.widgets.simpleWorkflowDialog)
                  {
                     this.widgets.simpleWorkflowDialog = new Alfresco.module.RulesWorkflowAction(this.id + "-simpleWorkflowDialog");
                     YAHOO.Bubbling.on("workflowOptionsSelected", function (layer, args)
                     {
                        if ($hasEventInterest(this.widgets.simpleWorkflowDialog, args))
                        {
                           var values = args[1].options;
                           if (values !== null)
                           {
                              var ctx = this.renderers["arca:simple-workflow-dialog-button"].currentCtx,
                                 RWA = Alfresco.module.RulesWorkflowAction,
                                 prefix = values.viewMode == RWA.VIEW_MODE_APPROVAL_STEP ? "approve" : "reject";
                              this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, prefix + "-step", values.label);
                              this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, prefix + "-move", values.action == "move");
                              this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, prefix + "-folder", values.nodeRef);
                              this._updateSubmitElements(ctx.configDef);
                           }
                        }
                     }, this);
                  }
                  var viewMode = obj.paramDef._mode;
                  this.widgets.simpleWorkflowDialog.setOptions(
                  {
                     viewMode: viewMode,
                     siteId: this.options.siteId,
                     rootNode: this.options.rootNode,
                     repositoryBrowsing: this.options.repositoryBrowsing
                  });
                  var params = this._getParameters(obj.configDef),
                     RWA = Alfresco.module.RulesWorkflowAction,
                     prefix = viewMode == RWA.VIEW_MODE_APPROVAL_STEP ? "approve" : "reject";
                  this.widgets.simpleWorkflowDialog.showDialog(
                  {
                     label: params[prefix + "-step"],
                     action: (params[prefix + "-move"] ? "move" : "copy"),
                     nodeRef: params[prefix + "-folder"] ? new Alfresco.util.NodeRef(params[prefix + "-folder"]) : null
                  });
               });

               if (enableCheckboxEl)
               {
                  // Toggle enable/disable on the button and the belonging hidden fields
                  Event.addListener(enableCheckboxEl, "click", function(p_oEvent, p_oObj)
                  {
                     var disabled = !p_oObj.enableCheckboxEl.checked;
                     var hiddenFields = [];
                     hiddenFields.push(Selector.query("input[param=" + p_oObj.prefix + "-step]", p_oObj.configDef._id)[0]);
                     hiddenFields.push(Selector.query("input[param=" + p_oObj.prefix + "-folder]", p_oObj.configDef._id)[0]);
                     hiddenFields.push(Selector.query("input[param=" + p_oObj.prefix + "-move]", p_oObj.configDef._id)[0]);
                     this._toggleDisableOnElements(hiddenFields, disabled);
                     if (disabled)
                     {
                        Selector.query("input[param=" + p_oObj.prefix + "-step]", p_oObj.configDef._id)[0].value = "";
                        Selector.query("input[param=" + p_oObj.prefix + "-folder]", p_oObj.configDef._id)[0].value = "";
                        Selector.query("input[param=" + p_oObj.prefix + "-move]", p_oObj.configDef._id)[0].value = "";
                     }
                     p_oObj.button.set("disabled", disabled);
                     this._updateSubmitElements(p_oObj.configDef);
                  },
                  {
                     enableCheckboxEl: enableCheckboxEl,
                     button: button,
                     configDef: configDef,                     
                     prefix: prefix
                  }, this);

                  // Enable
                  enableCheckboxEl.click();
                  if (!ruleConfig.parameterValues || !ruleConfig.parameterValues[prefix + "-folder"])
                  {
                     // Disable
                     enableCheckboxEl.click();
                  }
               }

            }
         },
         
         "arca:category-picker":
         {
            manual: { edit: true },
            currentCtx: {},
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               this.renderers["arca:category-picker"].currentCtx =
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
                  value: (ruleConfig.parameterValues ? ruleConfig.parameterValues["category-value"] : null),
                  controlParams:
                  {
                     multipleSelectMode: false
                  },
                  fnValueChanged:
                  {
                     fn: function(obj)
                     {
                        var ctx = this.renderers["arca:category-picker"].currentCtx;
                        this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, "category-aspect", "cm:generalclassifiable");
                        this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, "category-value", obj.selectedItems[0]);
                        this._updateSubmitElements(ctx.configDef);
                     },
                     scope: this
                  }
               });
               picker.render();
            }
         }
      }
   });

})();
