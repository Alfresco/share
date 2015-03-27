function processScriptConfig(xmlConfig)
{
   // Set the type of the config
   model.ruleConfigType = xmlConfig.@type.toString();

   // Set javascript class component that will handle the ui
   model.component = xmlConfig.component.toString();

   // Create a json representation of the menu that will be used to merge in configs in the ui
   var menuMap = [],
      menuNode = xmlConfig.menu ? xmlConfig.menu : new XML(),
      groupNode,
      itemNode,
      attribute,
      group,
      item,
      itemPattern;
   if (menuNode)
   {
      for each (groupNode in menuNode.elements("group"))
      {
         group = [];
         for each (itemNode in groupNode.elements())
         {
            item = {};
            itemPattern = {};
            for each (attribute in itemNode.attributes())
            {
               itemPattern[attribute.name()] = attribute.text();
            }
            item[itemNode.name()] = itemPattern;
            group.push(item);
         }
         menuMap.push(group);
      }
   }
   model.menuMap = jsonUtils.toJSONString(menuMap);

   // Create a json representation of the customisations that will be used to modify the configs into a custom ui
   var customisationsMap = [],
      customisationsNode = xmlConfig.customise ? xmlConfig.customise : new XML(),
      customisationNode,
      customisation,
      customisationPattern;
   if (customisationsNode)
   {
      for each (customisationNode in customisationsNode.elements())
      {
         customisation = {};
         customisationPattern = {};
         for each (attribute in customisationNode.attributes())
         {
            customisationPattern[attribute.name()] = attribute.text();
         }
         customisation[customisationNode.name()] = [customisationPattern, customisationNode.text()];
         customisationsMap.push(customisation);
      }
   }
   model.customisationsMap = jsonUtils.toJSONString(customisationsMap);
}

function processGlobalConfig()
{
   // Repository Library root node
   var rootNode = "alfresco://company/home",
      repoConfig = config.scoped["RepositoryLibrary"]["root-node"];
   if (repoConfig !== null)
   {
      rootNode = repoConfig.value;
   }

   model.rootNode = rootNode;
}

function loadRuleConfigDefinitions(xmlConfig)
{
   // Set the type of the config
   var connector = remote.connect("alfresco"),
      configDefinitionsNode = xmlConfig['config-definitions'],
      type = xmlConfig.@type.toString();
   if (configDefinitionsNode.@webscript)
   {
      var result = connector.get(configDefinitionsNode.@webscript.toString());
      if (result.status == 200)
      {
         var ruleConfigDefinitions = JSON.parse(result).data;

         // Remove configuration definitions that NOT shall show up in the ui
         if (configDefinitionsNode.remove)
         {
            var filteredRuleConfigDefinitions = [], remove;
            for each (configDefinition in ruleConfigDefinitions)
            {
               remove = false;
               for each (removeConfigDefinitionNode in configDefinitionsNode.remove.elements(type))
               {
                  if (removeConfigDefinitionNode.@name == configDefinition.name)
                  {
                     remove = true;
                  }
               }
               if (!remove)
               {
                  filteredRuleConfigDefinitions.push(configDefinition);
               }
             }
            return filteredRuleConfigDefinitions;
         }
         else
         {
            return ruleConfigDefinitions;
         }
      }
   }
   return [];
}

function sortConstraintsByTitle(constraint1, constraint2)
{
   return (constraint1.displayLabel > constraint2.displayLabel) ? 1 : (constraint1.displayLabel < constraint2.displayLabel) ? -1 : 0;
}

function loadRuleConstraints(xmlConfig)
{
   // Set the type of the config
   var connector = remote.connect("alfresco"),
      constraintsNode = xmlConfig.constraints;
   if (constraintsNode.@webscript)
   {
      var result = connector.get(constraintsNode.@webscript.toString());
      if (result.status == 200)
      {
         var constraintsArr = JSON.parse(result).data,
            constraintsObj = {},
            values;
         for (var i = 0, il = constraintsArr.length, constraint; i < il; i++)
         {
            constraint = constraintsArr[i];
            values = constraint.values;
            values.sort(sortConstraintsByTitle);
            constraintsObj[constraint.name] = values;
         }
         return constraintsObj;
      }
   }
   return {};
}

