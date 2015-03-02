<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/rules/config/rule-config.lib.js">

function main()
{
   var c = new XML(config.script);
   processScriptConfig(c);
   processGlobalConfig();

   // Load rule config definitions, or in this case "RuleType:s"
   var ruleTypes = loadRuleConfigDefinitions(c);
   model.ruleConfigDefinitions = jsonUtils.toJSONString(ruleTypes);   
}

main();