<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/rules/config/rule-config.lib.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/config.lib.js">

function main()
{
   var c = new XML(config.script);
   processScriptConfig(c);
   processGlobalConfig();

   // Load rule config definitions, or in this case "ActionConditionDefinition:s"
   var actionConditionDefinitions = loadRuleConfigDefinitions(c);
   /**
    * Remove the "compare-property-value" action condition definition from the list
    * and put it as a dedicated variable since that condition is a special case
    * and will be dynamically created on the page based on which property that is selected.
    */
   var i = 0;
   while(i < actionConditionDefinitions.length)
   {
      if (actionConditionDefinitions[i].name == "compare-property-value")
      {
         model.comparePropertyValueDefinition = jsonUtils.toJSONString(actionConditionDefinitions[i]);
         actionConditionDefinitions.splice(i, 1);
      }
      else if (actionConditionDefinitions[i].name == "compare-mime-type")
      {
         model.compareMimeTypeDefinition = jsonUtils.toJSONString(actionConditionDefinitions[i]);
         actionConditionDefinitions.splice(i, 1);
      }
      else
      {
         i++;
      }
   }   
   model.ruleConfigDefinitions = jsonUtils.toJSONString(actionConditionDefinitions);

   // Load constraints for rule types
   var conditionConstraints = loadRuleConstraints(c);
   model.constraints = jsonUtils.toJSONString(conditionConstraints);

   // Load filter for aspects, types and property evalurators that shall be visible
   var constraintsFilter = {};
   constraintsFilter["ac-aspects"] = getDocumentLibraryAspects();
   constraintsFilter["ac-types"] = getDocumentLibraryTypes();

   // Save property-evaluator config as json
   var propertyEvaluatorMap = {},
      propertyEvaluatorNodes = c.elements("property-evaluators"),
      propertyEvaluatorNode = propertyEvaluatorNodes && propertyEvaluatorNodes.length() > 0 ? propertyEvaluatorNodes[0] : null,
      evaluatorNode,
      propertyNode,
      propertyTypes;
   if (propertyEvaluatorNode)
   {
      for each (propertyNode in propertyEvaluatorNode.elements("property"))
      {
         evaluatorNames = [];
         for each (evaluatorNode in propertyNode.elements("evaluator"))
         {
            evaluatorNames.push(evaluatorNode.@name.toString());
         }
         propertyEvaluatorMap[propertyNode.@type.toString()] = evaluatorNames;
      }
      constraintsFilter["ac-compare-operations"] = propertyEvaluatorMap;
   }
   model.constraintsFilter = jsonUtils.toJSONString(constraintsFilter);

   // Load user preferences for which proeprties to show in menu as default
   var prefs = jsonUtils.toObject(preferences.value),
      ruleProperties = {};
   // Get all default properties
   if (c.defaults)
   {
      for each (propertyNode in c.defaults.elements("property"))
      {
         ruleProperties[propertyNode.text()] = "show";
      }
   }

   // Complete with user preferences
   if (prefs && prefs.org && prefs.org.alfresco && prefs.org.alfresco.share && prefs.org.alfresco.share.rule && prefs.org.alfresco.share.rule.properties)
   {
      var userProperties = prefs.org.alfresco.share.rule.properties;
      for (propertyName in userProperties)
      {
         // Will set value to "show" or "hide"
         ruleProperties[propertyName] = userProperties[propertyName];
      }
   }

   // Get info such as type and display name about the properties to display
   var propertiesParam = [],
      transientPropertyInstructions = {},
      instructions,
      propertyNameTokens,
      basePropertyName;
   for (propertyName in ruleProperties)
   {
      if (ruleProperties[propertyName] == "show")
      {
         propertyNameTokens = propertyName.split(":");
         basePropertyName = propertyNameTokens[0] + ":" + propertyNameTokens[1];
         instructions = transientPropertyInstructions[basePropertyName];
         if (!instructions)
         {
            instructions = [];
            transientPropertyInstructions[basePropertyName] = instructions;
         }
         propertiesParam.push(basePropertyName);
         instructions.push(propertyNameTokens.length > 2 ? propertyName : basePropertyName);
      }
   }

   var allProperties = [],
      instruction;
   if (propertiesParam.length > 0)
   {
      var connector = remote.connect("alfresco");
      var result = connector.get("/api/properties?name=" + propertiesParam.join("&name="));
      if (result.status == 200)
      {
         var properties = JSON.parse(result);
         for (var i = 0, il = properties.length; i < il; i++)
         {
            property = properties[i];
            instructions = transientPropertyInstructions[property.name];
            for (var j = 0, jl = instructions ? instructions.length : 0; j < jl; j++)
            {
               instruction = instructions[j];
               if (instruction == property.name)
               {
                  // It was a normal property, just add it
                  allProperties.push(
                  {
                     name: property.name,
                     dataType: property.dataType,
                     title: property.title ? property.title : property.name 
                  });
               }
               else
               {
                  // It was a transient property, modify the id to represent the transient property
                  allProperties.push(
                  {
                     name: instruction,
                     dataType: property.dataType,
                     title: null // will be set inside client js file instead
                  });
               }
            }
         }
      }
   }
   model.properties = jsonUtils.toJSONString(allProperties);
}

main();
