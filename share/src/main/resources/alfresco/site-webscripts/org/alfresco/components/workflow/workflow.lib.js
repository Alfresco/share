function getHiddenTaskTypes()
{
   var hiddenTaskTypes = [],
      hiddenTasks = config.scoped["Workflow"]["hidden-tasks"].childrenMap["task"];
   if (hiddenTasks)
   {
      for (var hi = 0, hil = hiddenTasks.size(); hi < hil; hi++)
      {
         hiddenTaskTypes.push(hiddenTasks.get(hi).attributes["type"]);
      }
   }
   return hiddenTaskTypes;
}

function getHiddenWorkflowNames()
{
   var hiddenWorkflowNames = [],
      hiddenWorkflows = config.scoped["Workflow"]["hidden-workflows"].childrenMap["workflow"];
   if (hiddenWorkflows)
   {
      for (var hi = 0, hil = hiddenWorkflows.size(); hi < hil; hi++)
      {
         hiddenWorkflowNames.push(hiddenWorkflows.get(hi).attributes["name"]);
      }
   }
   return hiddenWorkflowNames;
}

function sortByTitle(workflow1, workflow2)
{
   var title1 = (workflow1.title || workflow1.name).toUpperCase(),
      title2 = (workflow2.title || workflow2.name).toUpperCase();
   return (title1 > title2) ? 1 : (title1 < title2) ? -1 : 0;
}

function getWorkflowDefinitions()
{
   var hiddenWorkflowNames = getHiddenWorkflowNames(),
      connector = remote.connect("alfresco"),
      result = connector.get("/api/workflow-definitions?exclude=" + hiddenWorkflowNames.join(","));
   if (result.status == 200)
   {
      var workflows = JSON.parse(result).data;
      workflows.sort(sortByTitle);
      return workflows;
   }
   return [];
}

function getMaxItems()
{
   var myConfig = new XML(config.script),
      maxItems = myConfig["max-items"];
   if (maxItems)
   {
      maxItems = myConfig["max-items"].toString();
   }
   return maxItems && maxItems.length > 0 ? maxItems : null;
}

function getSiteUrl(relativeURL, siteId)
{
   var site_url = relativeURL;

   if (!siteId)
   {
      siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : ((args.site != null) ? args.site : "");
   }
   
   if (siteId.length > 0)
   {
      site_url = "site/" + siteId + "/" + site_url;
   }

   if (site_url.indexOf("/") == 0)
   {
      site_url = site_url.substring(1);
   }
   if (site_url.indexOf("page/") != 0)
   {
      site_url = "page/" + site_url;
   }
   site_url = url.context + "/" + site_url;

   return site_url;
}
