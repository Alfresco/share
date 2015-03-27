/**
 * Site Members component GET method
 */
function main()
{
   model.isManager = false;
   
   // Check the role of the user - only SiteManagers are allowed to invite people/view invites
   var obj = null;
   var json = remote.call("/api/sites/" + page.url.templateArgs.site + "/memberships/" + encodeURIComponent(user.name));
   if (json.status == 200)
   {
      obj = JSON.parse(json);
   }
   if (obj)
   {
      model.isManager = (obj.role == "SiteManager");
   }

   model.links = [];
   model.activePage = page.url.templateArgs.pageid.toLowerCase();

   // Add Site Members link
   model.links.push(
   {
      id: "site-members-link",
      href: "site-members",
      cssClass: (model.activePage == "site-members" || model.activePage == "invite") ? "theme-color-4" : null,
      label: msg.get("link.site-members")
   });

   // Add Site Groups link
   model.links.push(
   {
      id: "site-groups-link",
      href: "site-groups",
      cssClass: (model.activePage == "site-groups" || model.activePage == "add-groups") ? "theme-color-4" : null,
      label: msg.get("link.site-groups")
   });

   // Add Pending Invites link (if user is manager)
   if (model.isManager)
   {
      model.links.push(
      {
         id: "pending-invites-link",
         href: "pending-invites",
         cssClass: (model.activePage == "pending-invites") ? "theme-color-4" : null,
         label: msg.get("link.pending-invites")
      });
   }
}

main();