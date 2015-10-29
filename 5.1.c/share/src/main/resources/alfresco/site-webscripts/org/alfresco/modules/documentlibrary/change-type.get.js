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
                        
                        // if we have a type label defined then use it and append typename for clarity (e.g. resolving duplicates)
                        if (typeLabel)
                        {
                           typeLabel = typeLabel.toString() + " ("+typeName+")";
                        }
                        else
                        {
                           var labelMsg = "type." + typeName.replace(":","_");
                           typeLabel = msg.get(labelMsg);
                           if (labelMsg == typeLabel) typeLabel = typeName;
                        }
                        
                        types.push({
                           name: typeName,
                           label: typeLabel
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