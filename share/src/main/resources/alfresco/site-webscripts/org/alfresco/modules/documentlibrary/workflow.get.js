function main()
{
   var scopedRoot = config.scoped["DocumentLibrary"]["workflows"];

   return getConfigWorkflow(scopedRoot);
}

function getConfigWorkflow(scopedRoot)
{
   var workflows = [],
       workflowDefinition,
       configs;

   try
   {
      configs = scopedRoot.childrenMap["workflow"];
      if (configs)
      {
         for (var i = 0; i < configs.size(); i++)
         {
            // Get workflow definition name from each config item
            workflowDefinition = configs.get(i).attributes["definition"];
            if (workflowDefinition)
            {
               workflows.push(workflowDefinition.toString());
            }
         }
      }
   }
   catch (e)
   {
   }

   return workflows;
}

model.workflows = main();