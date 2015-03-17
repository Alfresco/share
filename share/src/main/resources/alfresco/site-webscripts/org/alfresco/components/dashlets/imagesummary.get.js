/**
 * Image Summary component GET method
 */

function main()
{
   // We are in the context of a site
   var siteId = page.url.templateArgs.site;
   
   // Call the repository to see if the user is site manager or not
   var userIsSiteManager = false,
       json = remote.call("/api/sites/" + siteId + "/memberships/" + encodeURIComponent(user.name));
   if (json.status == 200)
   {
      var obj = JSON.parse(json);
      if (obj)
      {
         userIsSiteManager = (obj.role == "SiteManager");
      }
   }
   
   // Call the repository for the site profile
   var siteTitle = siteId;
   json = remote.call("/api/sites/" + siteId);
   if (json.status == 200)
   {
      var obj = JSON.parse(json);
      if (obj)
      {
         siteTitle = obj.title;
      }
   }
   
   // Component definition
   var imageSummary = {
      id: "ImageSummary",
      name: "Alfresco.dashlet.ImageSummary",
      assignTo : "imageSummary",                   // Need to reference the generated JS object
      options: {
         componentId : instance.object.id,         // Reference to allow saving of component properties
         siteId: (siteId !== null) ? siteId : "",
         siteTitle: siteTitle,
         siteFolderPath: args.siteFolderPath       // Folder path from component properties
      }
   };
   
   // Dashlet title bar component actions and resizer
   var actions = [];
   if (userIsSiteManager)
   {
      actions.push(
      {
         cssClass: "edit",
         eventOnClick: { _alfValue : "imageFolderDashletEvent" + args.htmlid.replace(/-/g, "_"), _alfType: "REFERENCE"}, 
         tooltip: msg.get("dashlet.edit.tooltip")
      });
   }
   actions.push({
      cssClass: "help",
      bubbleOnClick:
      {
         message: msg.get("dashlet.help")
      },
      tooltip: msg.get("dashlet.help.tooltip")
   });
   
   var dashletResizer = {
      id: "DashletResizer",
      name: "Alfresco.widget.DashletResizer",
      initArgs: ['"' + args.htmlid + '"', '"' + instance.object.id + '"'],
      useMessages: false
   };
   
   var dashletTitleBarActions = {
      id: "DashletTitleBarActions",
      name: "Alfresco.widget.DashletTitleBarActions",
      useMessages: false,
      options: {
         actions: actions
      }
   };
   model.widgets = [imageSummary, dashletResizer, dashletTitleBarActions];
}

main();