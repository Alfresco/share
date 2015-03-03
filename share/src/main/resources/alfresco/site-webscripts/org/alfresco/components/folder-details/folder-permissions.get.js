<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function setPermissions(folderDetails)
{
   var rawPerms = folderDetails.item.node.permissions.roles,
      permParts,
      group,
      permission;

   model.roles = rawPerms != null ? rawPerms : [];
   model.readPermission = false;

   if (rawPerms && rawPerms.length > 0)
   {
      model.readPermission = true;
      model.managers = "None";
      model.collaborators = "None";
      model.contributors = "None";
      model.consumers = "None";
      model.everyone = "None";

      for (i = 0, ii = rawPerms.length; i < ii; i++)
      {
         permParts = rawPerms[i].split(";");
         group = permParts[1];
         permission = permParts[2];
         if (group.search("_SiteManager$") !== -1)
         {
            model.managers = permission;
         }
         else if (group.search("_SiteCollaborator$") !== -1)
         {
            model.collaborators = permission;
         }
         else if (group.search("_SiteContributor$") !== -1)
         {
            model.contributors = permission;
         }
         else if (group.search("_SiteConsumer$") !== -1)
         {
            model.consumers = permission;
         }
         // there can be multiple permissions per group - but this is not handled well in the UI
         // here we ensure only Site ACLs for GROUP_EVERYONE are displayed in the UI
         else if (group === "GROUP_EVERYONE" && permission.indexOf("Site") === 0)
         {
            model.everyone = permission;
         }
      }
   }
}

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);

   var folderDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);
   if (folderDetails)
   {
      setPermissions(folderDetails);
      model.allowPermissionsUpdate = folderDetails.item.node.permissions.user["ChangePermissions"] || false;
      model.displayName = folderDetails.item.displayName;
   }
   
   // Widget instantiation metadata...
   var folderPermissions = {
      id : "FolderPermissions", 
      name : "Alfresco.FolderPermissions",
      options : {
         nodeRef : model.nodeRef,
         siteId : model.site,
         displayName : model.displayName,
         roles : model.roles
      }
   };
   model.widgets = [folderPermissions];
}

main();

