/**
 * Collaboration Site Title component GET method
 */
function main()
{
   // Call the repository for the site profile
   var json = remote.call("/api/sites/" + page.url.templateArgs.site);
   
   var profile =
   {
      title: "",
      shortName: "",
      visibility: "PRIVATE"
   };
   
   if (json.status == 200)
   {
      // Create javascript objects from the repo response
      var obj = JSON.parse(json);
      if (obj)
      {
         profile = obj;
      }
   }
   
   // Call the repository to see if the user is site manager or not
   var userIsSiteManager = false,
       userIsMember = user.isAdmin,
       userIsDirectMember = false,
       exists = (json.status == 404) ? false : true;

   json = remote.call("/api/sites/" + page.url.templateArgs.site + "/memberships/" + encodeURIComponent(user.name));
   if (json.status == 200)
   {
      var obj = JSON.parse(json);
      if (obj)
      {
         userIsMember = true;
         userIsDirectMember = !(obj.isMemberOfGroup);
         userIsSiteManager = (obj.role == "SiteManager");
      }
   }

   var activePage = page.url.templateArgs.pageid || "";
   var siteTitle = (profile.title != "") ? profile.title : profile.shortName;
   var siteDashboardUrl = page.url.context + "/page/site/" + page.url.templateArgs.site;

   var links = [];

   if (userIsSiteManager)
   {
      links.push(
      {
         id: "inviteUser-link",
         href: "invite",
         cssClass: ("invite" == activePage) ? "active-page" : null,
         label: "link.invite"
      });
   }

   // Join links
   if (!userIsMember)
   {
      if (profile.visibility == "PUBLIC")
      {
         links.push(
         {
            id: "join-link",
            label: "link.join"
         });
      }
      else if (profile.visibility != "PRIVATE")
      {
         links.push(
         {
            id: "requestJoin-link",
            label: "link.request-join"
         });
      }
   }

   if (user.isAdmin && !userIsSiteManager)
   {
      links.push(
      {
         id: "become-manager-link",
         label: "link.become-manager"
      });
   }

   // Customise Site link
   if (userIsSiteManager && (page.url.uri == siteDashboardUrl || "customise-site-dashboard" == activePage))
   {
      links.push(
      {
         id: "customiseDashboard-link",
         href: "customise-site-dashboard",
         cssClass: "customise-site-dashboard" == activePage ? "active-page" : null,
         label: "link.customiseDashboard"
      });
   }

   var moreMenu = null;

   if (userIsSiteManager)
   {
      moreMenu = {
         label: "link.more",
         options: [
            { value: "editSite", label: "link.editSite" },
            { value: "customiseSite", label: "link.customiseSite" }
         ]
      };
      if (userIsDirectMember)
      {
         moreMenu.options.push({ value: "leaveSite", label: "link.leave" });
      }
   }
   else if (userIsMember)
   {
      moreMenu = {
         label: "link.actions",
         options: [
            { value: "leaveSite", label: "link.leave" }
         ]
      };
   }

   // Prepare the model
   model.activePage = activePage;
   model.siteTitle = siteTitle;
   model.siteDashboardUrl = siteDashboardUrl;
   model.siteExists = exists;
   model.profile = profile;
   model.userIsSiteManager = userIsSiteManager;
   model.userIsMember = userIsMember;
   model.links = links;
   model.moreMenu = moreMenu;
   model.userIsDirectMember = userIsDirectMember;
   
   // Widget instantiation metadata...
   var collaborationTitle = {
      id : "CollaborationTitle", 
      name : "Alfresco.CollaborationTitle",
      options : {
         site : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         siteTitle : model.siteTitle,
         user : (user.name != null) ? user.name : "",
         userIsMember: userIsMember,
         currentSiteVisibility: profile.visibility,
         siteExists: exists
      }
   };
   model.widgets = [collaborationTitle];
}

main();