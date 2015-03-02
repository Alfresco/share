function main()
{
   var dataLoader = {
      id: "DataLoader", 
      name: "Alfresco.DataLoader",
      options: {
         url: (args.url != null) ? args.url : ""
      }
   };
   if (args.eventData != null)
   {
      dataLoader.options.eventData = args.eventData;
   }
   if (args.useProxy != null)
   {
      dataLoader.options.useProxy = (args.useProxy == "true");
   }
   if (args.failureMessageKey != null)
   {
      dataLoader.options.failureMessageKey = args.failureMessageKey;
   }
   if (args.eventName != null)
   {
      dataLoader.options.eventName = args.eventName;
   }
   model.widgets = [dataLoader];
}

main();
