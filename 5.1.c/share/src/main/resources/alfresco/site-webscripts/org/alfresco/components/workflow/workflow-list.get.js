<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/workflow/workflow.lib.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/workflow/filter/filter.lib.js">

function main()
{
   model.workflowDefinitions = getWorkflowDefinitions();
   model.hiddenWorkflowNames = getHiddenWorkflowNames();
   model.filterParameters = getFilterParameters();
   model.maxItems = getMaxItems();
   
   
   //Widget instantiation metadata...
   var workflowList = {
      id : "WorkflowList", 
      name : "Alfresco.component.WorkflowList",
      options : {
         filterParameters : model.filterParameters,
         hiddenWorkflowNames : model.hiddenWorkflowNames,
         workflowDefinitions : model.workflowDefinitions,
         maxItems : parseInt((model.maxItems != null) ? model.maxItems : "50")
      }
   };
   model.widgets = [workflowList];
}

main();

