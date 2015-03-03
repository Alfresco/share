function main()
{
   var workflowConfig =  new XML(config.script),
      stepConfig = workflowConfig ? workflowConfig.step : null,
      actionConfigs = stepConfig ? stepConfig.action : [],
      actions = [];

   for each(var actionConfig in actionConfigs)
   {
      actions.push(
      {
         value: actionConfig + "",
         label: msg.get("workflow.step.action." + actionConfig)
      });
   }
   model.actions = actions;
}

main();