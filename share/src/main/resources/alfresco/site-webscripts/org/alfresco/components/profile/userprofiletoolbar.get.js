/**
 * User Profile - Toolbar Component GET method
 */
function main()
{
   var userId = page.url.templateArgs["userid"];
   if (userId == null)
   {
      userId = user.name;
   }
   model.activeUserProfile = (userId == user.name);
   model.activePage = (page.url.templateArgs.pageid || "");

   model.following = -1;
   model.followers = -1;

   var following = remote.call("/api/subscriptions/" + encodeURIComponent(userId) + "/following/count");
   if (following.status == 200)
   {
      model.following = JSON.parse(following).count;

      if (model.activeUserProfile)
      {
         var followers = remote.call("/api/subscriptions/" + encodeURIComponent(userId) + "/followers/count");
         if(followers.status == 200)
         {
            model.followers = JSON.parse(followers).count;
         }
      }
   }

   if (model.activeUserProfile)
   {
      model.syncEnabled = (syncMode.getValue() != "OFF");
   }

   model.links = [];

   // Add Profile link
   addLink("profile-link", "profile", "link.info");
   // Add User Sites link
   addLink("user-sites-link", "user-sites", "link.sites");
   // Add User Content link
   addLink("user-content-link", "user-content", "link.content");

   if (model.activeUserProfile)
   {
      if (model.following != -1)
      {
         // Add Following link
         addLink("following-link", "following", "link.following", [model.following]);
      }

      if (model.followers != -1)
      {
         // Add Followers link
         addLink("followers-link", "followers", "link.followers", [model.followers]);
      }

      if (user.capabilities.isMutable)
      {
         // Add Change Password link
         addLink("change-password-link", "change-password", "link.changepassword");
      }

      // Add Notifications links
      addLink("user-notifications-link", "user-notifications", "link.notifications");

      if (model.syncEnabled)
      {
         // Add Cloud Sync
         addLink("user-cloud-auth-link", "user-cloud-auth", "link.cloud-auth");
      }
      
      // Add Trashcan link
      addLink("user-trashcan-link", "user-trashcan", "link.trashcan");
   }
   else
   {
      if (model.following != -1)
      {
         // Add Following link
         addLink("otherfollowing-link", "following", "link.otherfollowing", [model.following]);
      }
   }
}

function addLink(id, href, msgId, msgArgs)
{
   model.links.push(
   {
      id: id,
      href: href,
      cssClass: (model.activePage == href) ? "theme-color-4" : null,
      label: msg.get(msgId, msgArgs ? msgArgs : null)
   });
}

main();