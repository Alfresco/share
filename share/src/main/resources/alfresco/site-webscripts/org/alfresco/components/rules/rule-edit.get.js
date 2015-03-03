function main()
{
   var ruleNodeRef = page.url.args.nodeRef,
       ruleId = page.url.args.ruleId,
       connector = remote.connect("alfresco"),
       rule = null,
       result;

   // Load rule to edit of given in url
   if (ruleNodeRef && ruleId)
   {
      result = connector.get("/api/node/" + ruleNodeRef.replace("://", "/") + "/ruleset/rules/" + ruleId);
      if (result.status == 200)
      {
         rule = JSON.parse(result);
         model.ruleTitle = rule.title;
      }
   }

   // Load constraints
   result = connector.get("/api/actionConstraints");

   if (result.status == 200)
   {
      var constraintsArr = JSON.parse(result).data;
      var constraintsObj = {};
      for (var i = 0, il = constraintsArr.length, constraint; i < il; i++)
      {
         constraint = constraintsArr[i];
         constraintsObj[constraint.name] = constraint.values;
         if (constraint.name == "ac-scripts")
         {
            model.scripts = constraint.values;
         }
      }
      model.constraints = constraintsObj;
   }

   // Repository Library root node
   var rootNode = "alfresco://company/home",
      repoConfig = config.scoped["RepositoryLibrary"]["root-node"];
   if (repoConfig !== null)
   {
      rootNode = repoConfig.value;
   }

   model.rootNode = rootNode;
   
   // Widget instantiation metadata...
   var ruleEdit = {
      id: "RuleEdit", 
      name: "Alfresco.RuleEdit",
      options: {
         nodeRef: (page.url.args.nodeRef != null) ? page.url.args.nodeRef : "",
         siteId: (page.url.templateArgs.site != null) ? page.url.templateArgs.site : ""
      }
   };
   
   if (rule)
   {
      ruleEdit.options.rule = rule;
   }
   if (model.constraints)
   {
      ruleEdit.options.constraints = model.constraints;
   }

   model.widgets = [ruleEdit];
}

main();
