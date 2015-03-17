<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/workflow/workflow.lib.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/workflow/filter/filter.lib.js">

function main()
{
   model.hiddenTaskTypes = getHiddenTaskTypes();
   model.filterParameters = getFilterParameters();
   model.maxItems = getMaxItems();
   
   // Widget instantiation metadata...
   var taskList = {
      id : "TaskList", 
      name : "Alfresco.component.TaskList",
      options : {
         filterParameters : model.filterParameters,
         hiddenTaskTypes : model.hiddenTaskTypes,
         maxItems : parseInt((model.maxItems != null) ? model.maxItems : "50")
      }
   };
   model.widgets = [taskList];
}

main();
