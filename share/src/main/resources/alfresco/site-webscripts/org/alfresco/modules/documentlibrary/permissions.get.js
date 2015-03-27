function getIsSitePublic()
{
   var isPublic = false;
   var json = remote.call("/api/sites/" + args.site);
   if (json.status == status.STATUS_OK)
   {
      var obj = JSON.parse(json);
      if (obj)
      {
         isPublic = obj.isPublic;
      }
   }
   return isPublic;
}

function getRoles()
{
   var roles = {};
   var json = remote.call("/api/sites/" + args.site + "/roles");
   if (json.status == status.STATUS_OK)
   {
      var obj = JSON.parse(json);
      if (obj)
      {
         roles = obj;
      }
   }
   return roles;
}

function main()
{
   var roles = getRoles(),
       isSitePublic = getIsSitePublic(),
       groupNames = [],
       permGroups = [],
       permissionGroups = roles.permissionGroups;
   for (group in permissionGroups)
   {
      // strip group name down to group identifier
      var permissionGroup = permissionGroups[group],
          groupName = permissionGroup.substring(permissionGroup.lastIndexOf("_") + 1);
      
      // it is not allowed for a user to increase access for "All Other Users" on a non-public site.
      // so don't show this option in the UI
      if (groupName === "EVERYONE" && !isSitePublic)
      {
         permissionGroups.splice(group, 1);
         continue;
      }
      
      // ignore the SiteManager group as we do not allow it to be modified
      if (groupName !== "SiteManager")
      {
         groupNames.push(groupName);
         permGroups.push(permissionGroup);
      }
   }
   
   var roleNames = [],
       siteRoles = roles.siteRoles;
   for (role in siteRoles)
   {
      var roleName = siteRoles[role];
      
      // ignore the SiteManager role as we do not allow it to be applied
      if (roleName !== "SiteManager")
      {
         roleNames.push(roleName);
      }
   }
   
   model.siteRoles = roleNames;
   model.isSitePublic = isSitePublic.toString();
   model.permissionGroups = permGroups;
   model.groupNames = groupNames;
}
main();