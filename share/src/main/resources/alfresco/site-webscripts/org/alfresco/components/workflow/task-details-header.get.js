function main()
{
   // Widget instantiation metadata...
   var taskDetailsHeader = {
      id : "TaskDetailsHeader", 
      name : "Alfresco.component.TaskDetailsHeader",
      options : {
         referrer : page.url.args.referrer,
         nodeRef : page.url.args.nodeRef
      }
   };
   model.widgets = [taskDetailsHeader];
}

main();

