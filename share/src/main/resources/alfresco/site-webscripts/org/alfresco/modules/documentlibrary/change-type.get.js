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
       configs, typeConfig, typeName, typeLabel, subTypeConfigs;

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
                     typeLabel = subTypeConfigs.get(j).attributes["label"];
                     typeName = subTypeConfigs.get(j).attributes["name"];
                     if (typeName)
                     {
                        typeName = typeName.toString();
                        types.push({
                           name: typeName,
                           label: typeLabel ? typeLabel.toString() : msg.get("type." + typeName.replace(":","_"))
                        });
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