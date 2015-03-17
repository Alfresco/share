function main()
{
   var scopedRoot = config.scoped["DocumentLibrary"]["aspects"];

   return (
   {
      visible: getConfigAspects(scopedRoot, "visible"),
      addable: getConfigAspects(scopedRoot, "addable"),
      removeable: getConfigAspects(scopedRoot, "removeable")
   });
}

function getConfigAspects(scopedRoot, childName)
{
   var aspects = [],
       aspectName,
       configs,
       aspectConfig;

   try
   {
      configs = scopedRoot.getChildren(childName);
      if (configs)
      {
         for (var i = 0; i < configs.size(); i++)
         {
            aspectConfig = configs.get(i).childrenMap["aspect"];
            if (aspectConfig)
            {
               for (var j = 0, aspectName; j < aspectConfig.size(); j++)
               {
                  // Get aspect qname from each config item
                  aspectName = aspectConfig.get(j).attributes["name"];
                  if (aspectName)
                  {
                     aspects.push(aspectName.toString());
                  }
               }
            }
         }
      }
   }
   catch (e)
   {
   }

   return aspects;
}

model.aspects = main();