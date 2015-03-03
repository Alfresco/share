function main()
{
   // My Files root node
   model.rootNode = user.properties['userHome'];
   
   // Widget instantiation metadata...
   var filters = config.scoped['DocumentLibrary']['filters'],
       maxTagCount = filters.getChildValue('maximum-tag-count');
   
   if (maxTagCount == null)
   {
      maxTagCount = "100";
   }
   
   var tagFilter = {
      id : "TagFilter", 
      name : "Alfresco.TagFilter",
      assignTo : "tagFilter",
      options : {
         rootNode : model.rootNode,
         numTags : parseInt(maxTagCount)
      }
   };
   model.widgets = [tagFilter];
}

main();
