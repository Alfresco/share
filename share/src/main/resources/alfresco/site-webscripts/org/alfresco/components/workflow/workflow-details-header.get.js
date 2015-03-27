function main()
{
   // Widget instantiation metadata...
   var workflowDetailsHeader = {
      id : "WorkflowDetailsHeader", 
      name : "Alfresco.component.WorkflowDetailsHeader",
      options : {
         taskId : page.url.args.taskId
      }
   };
   model.widgets = [workflowDetailsHeader];
}

main();

