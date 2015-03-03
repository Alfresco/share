function getDocumentLibraryTypes()
{
   var types,
      type,
      subTypes,
      subType,
      typesConstraintFilter =
      {
         visible: [],
         changeable: []
      };
   types = config.scoped["DocumentLibrary"]["types"].childrenMap["type"];
   if (types)
   {
      for (var i = 0, il = types.size(); i < il; i++)
      {
         // Get aspect qname from each config item
         type = types.get(i).attributes["name"];
         if (type)
         {
            typesConstraintFilter.visible.push(type.toString());
            subTypes = types.get(i).childrenMap["subtype"];
            if (subTypes)
            {
               for (var j = 0, jl = subTypes.size(); j < jl; j++)
               {
                  subType = subTypes.get(j).attributes["name"].toString();
                  typesConstraintFilter.visible.push(subType);
                  typesConstraintFilter.changeable.push(subType);
               }
            }
         }
      }
   }
   return typesConstraintFilter;
}

function getDocumentLibraryAspects()
{
   var scopedRoot = config.scoped["DocumentLibrary"]["aspects"];

   var aspectsConstraintFilter =
   {
      visible: getConfigAspects(scopedRoot, "visible"),
      addable: getConfigAspects(scopedRoot, "addable"),
      removeable: getConfigAspects(scopedRoot, "removeable")
   };
   if (aspectsConstraintFilter.addable.length == 0)
   {
      aspectsConstraintFilter.addable = aspectsConstraintFilter.visible;
   }
   if (aspectsConstraintFilter.removeable.length == 0)
   {
      aspectsConstraintFilter.removeable = aspectsConstraintFilter.visible;
   }
   return aspectsConstraintFilter;
}

function getConfigAspects(scopedRoot, childName)
{
   var aspects = [],
      name,
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
               for (var j = 0, name; j < aspectConfig.size(); j++)
               {
                  // Get class qname from each config item
                  name = aspectConfig.get(j).attributes["name"];
                  if (name)
                  {
                     aspects.push(name.toString());
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
