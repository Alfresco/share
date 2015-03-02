function main()
{
   // Widget instantiation metadata...
   var workflowForm = {
      id : "WorkflowForm", 
      name : "Alfresco.component.WorkflowForm",
      options : {
         referrer : page.url.args.referrer,
         nodeRef : page.url.args.nodeRef
      }
   };
   model.widgets = [workflowForm];
}

main();

