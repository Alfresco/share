<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/rules/config/rule-config.lib.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/config.lib.js">

function main()
{
   var c = new XML(config.script);
   processScriptConfig(c);
   processGlobalConfig();

   // Load rule config definitions, or in this case "ActionDefinition:s"
   var actionDefinitions = loadRuleConfigDefinitions(c);
   model.ruleConfigDefinitions = jsonUtils.toJSONString(actionDefinitions);

   // Load constraints for rule types
   var actionConstraints = loadRuleConstraints(c);
   model.constraints = jsonUtils.toJSONString(actionConstraints);

   // Load aspects and types that shall be visible
   var constraintsFilter = {};
   constraintsFilter["ac-aspects"] = getDocumentLibraryAspects();
   constraintsFilter["ac-types"] = getDocumentLibraryTypes();
   model.constraintsFilter = jsonUtils.toJSONString(constraintsFilter);
}

main();