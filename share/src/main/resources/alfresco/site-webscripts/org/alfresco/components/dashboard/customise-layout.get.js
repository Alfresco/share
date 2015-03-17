/**
 * Customise Site Layout component GET method
 */

function getNoOfColumns(template)
{
   var noOfColumns = 0;
   while (template.properties["gridColumn" + (noOfColumns + 1)] !== null)
   {
      noOfColumns++;
   }
   return noOfColumns;
}

function main()
{
   // Get current template
   var dashboardId;
   if (args.dashboardType == "user")
   {
      dashboardId = "user/" + user.name + "/dashboard";
   }
   else if (args.dashboardType == "site")
   {
      dashboardId = "site/" + page.url.templateArgs.site + "/dashboard";
   }
   
   // Hardcoded templates until proper service exists
   var layouts = {
      "dashboard-1-column" : {
         templateId: "dashboard-1-column",
         noOfColumns: 1, 
         description: msg.get("msg.template-1-column"), 
         icon: url.context + "/res/components/dashboard/images/dashboard-1-column.png"
      },
      "dashboard-2-columns-wide-right" : {
         templateId: "dashboard-2-columns-wide-right", 
         noOfColumns: 2, 
         description: msg.get("msg.template-2-columns-wide-right"), 
         icon: url.context + "/res/components/dashboard/images/dashboard-2-columns-wide-right.png"
       },
      "dashboard-2-columns-wide-left" : {
         templateId: "dashboard-2-columns-wide-left",  
         noOfColumns: 2, 
         description: msg.get("msg.template-2-columns-wide-left"), 
         icon: url.context + "/res/components/dashboard/images/dashboard-2-columns-wide-left.png"},
      "dashboard-3-columns" : {
         templateId: "dashboard-3-columns",
         noOfColumns: 3, 
         description: msg.get("msg.template-3-columns"),
         icon: url.context + "/res/components/dashboard/images/dashboard-3-columns.png"},
      "dashboard-4-columns" : {
         templateId: "dashboard-4-columns",
         noOfColumns: 4, 
         description: msg.get("msg.template-4-columns"), 
         icon: url.context + "/res/components/dashboard/images/dashboard-4-columns.png"}
   };
   
   var currentTemplate = sitedata.findTemplate(dashboardId),
       currentNoOfColumns = getNoOfColumns(currentTemplate),
       currentTemplateDescription = "NONE";
   
   for (var key in layouts)
   {
      if (layouts[key].templateId == currentTemplate.id)
      {
         currentTemplateDescription = layouts[key].description;
         break;
      }
   }
   
   var currentLayout =
   {
      templateId: currentTemplate.id,
      noOfColumns: currentNoOfColumns,
      description: currentTemplateDescription,
      icon : url.context + "/res/components/dashboard/images/" + currentTemplate.id + ".png"
   };
   
   // Prepare model for template
   model.currentLayout = currentLayout;
   model.layouts = layouts;
   
   var customizeDashlets = {
      id : "CustomiseLayout",
      name : "Alfresco.CustomiseLayout",
      options : {
         currentLayout : currentLayout,
         layouts : layouts
      }
   };
   model.widgets = [customizeDashlets];
}

main();