<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/workflow/workflow.lib.js">

function main()
{
   // Widget instantiation metadata...
   var workflowDetailsActions = {
      id : "WorkflowDetailsActions", 
      name : "Alfresco.component.WorkflowDetailsActions",
      options : {
         submitUrl : getSiteUrl("my-tasks")
      }
   };
   model.widgets = [workflowDetailsActions];
}

main();