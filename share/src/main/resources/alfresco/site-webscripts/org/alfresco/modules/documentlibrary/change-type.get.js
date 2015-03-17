function main()
{
   var scopedRoot = config.scoped["DocumentLibrary"]["types"];

   return (
   {
      selectable: getConfigTypes(scopedRoot, args.currentType || "")
   });
}

function getConfigTypes(scopedRoot, currentType)
{
   var types = [],
       configs, typeConfig, typeName, subTypeConfigs;

   try
   {
      configs = scopedRoot.getChildren("type");
      if (configs)
      {
         for (var i = 0; i < configs.size(); i++)
         {
            // Get type qname from each config item
            typeConfig = configs.get(i);
            typeName = typeConfig.attributes["name"];
            if (typeName == currentType)
            {
               subTypeConfigs = typeConfig.childrenMap["subtype"];
               if (subTypeConfigs)
               {
                  for (var j = 0; j < subTypeConfigs.size(); j++)
                  {
                     typeName = subTypeConfigs.get(j).attributes["name"];
                     if (typeName)
                     {
                        types.push(typeName.toString());
                     }
                  }
               }
            }
         }
         
         return types;
      }
   }
   catch (e)
   {
   }

   return types;
}

model.types = main();