<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/workflow/workflow.lib.js">

function main()
{
   // Widget instantiation metadata...
   var taskDetailsActions = {
      id : "TaskDetailsActions", 
      name : "Alfresco.component.TaskDetailsActions",
      options : {
         defaultUrl : getSiteUrl("my-workflows"),
         referrer : page.url.args.referrer,
         nodeRef : page.url.args.nodeRef
      }
   };
   model.widgets = [taskDetailsActions];
}

main();
