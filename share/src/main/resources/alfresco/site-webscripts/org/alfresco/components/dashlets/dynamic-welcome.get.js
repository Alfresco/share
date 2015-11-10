function main()
{
   // Define a set of functions to return common column settings...
   function getTutorialColumn()
   {
      var docsEdition = context.properties["docsEdition"].getValue();
      var tutorial = msg.get("share-tutorial.docs-url", [docsEdition]);
      return (
      {
         title: "welcome.user.tutorial.title",
         description: "welcome.user.tutorial.description",
         imageUrl: "/res/components/images/help-tutorial-bw-64.png",
         actionMsg: "welcome.user.tutorial.link",
         actionHref: tutorial,
         actionId: null,
         actionTarget: "_blank"
      });
   }

   function getSiteColumn()
   {
      return (
      {
         title: "welcome.user.sites.title",
         description: "welcome.user.sites.description",
         imageUrl: "/res/components/images/help-site-bw-64.png",
         actionMsg: "welcome.user.sites.link",
         actionHref: "#",
         actionId: "-createSite-button",
         actionTarget: null
      });
   }

   function getProfileColumn()
   {
      return (
      {
         title: "welcome.user.profile.title",
         description: "welcome.user.profile.description",
         imageUrl: "/res/components/images/help-avatar-bw-64.png",
         actionMsg: "welcome.user.profile.link",
         actionHref: page.url.context + "/page/user/profile#edit",
         actionId: null,
         actionTarget: null
      });
   }

   function getSiteInfoColumn(siteTitle, actionMsg, actionHref)
   {
      return (
      {
         title: "welcome.site.dashboard.title",
         description: "welcome.site.dashboard.description",
         descriptionArgs: [siteTitle],
         imageUrl: "/res/components/images/help-dashboard-bw-64.png",
         actionMsg: actionMsg,
         actionHref: actionHref,
         actionId: null,
         actionTarget: null
      });
   }

   function getUploadInfoColumn()
   {
      return (
      {
         title: "welcome.site.upload.title",
         description: "welcome.site.upload.description",
         imageUrl: "/res/components/images/help-share-bw-64.png",
         actionMsg: "welcome.site.upload.link",
         actionHref: "documentlibrary#upload",
         actionId: "-upload-button",
         actionTarget: null
      });
   }

   function getInviteColumn()
   {
      return (
      {
         title: "welcome.site.invite.title",
         description: "welcome.site.invite.description",
         imageUrl: "/res/components/images/help-people-bw-64.png",
         actionMsg: "welcome.site.invite.link",
         actionHref: "invite",
         actionId: "-invite-button",
         actionTarget: null
      });
   }

   function getModeratedSiteColumn()
   {
      return (
      {
         title: "welcome.site.moderated.title",
         description: "welcome.site.moderated.description",
         imageUrl: "/res/components/images/help-site-bw-64.png",
         actionMsg: "welcome.site.moderated.link",
         actionHref: "#",
         actionId: "-requestJoin-button",
         actionTarget: null
      });
   }

   function getSiteMembersColumn()
   {
      return (
      {
         title: "welcome.site.members.title",
         description: "welcome.site.members.description",
         imageUrl: "/res/components/images/help-people-bw-64.png",
         actionMsg: "welcome.site.members.link",
      actionHref: "site-members",
         actionId: null,
         actionTarget: null
      });
   }

   function getBrowseSiteColumn()
   {
      return (
      {
         title: "welcome.site.browse.title",
         description: "welcome.site.browse.description",
         imageUrl: "/res/components/images/help-site-bw-64.png",
         actionMsg: "welcome.site.browse.link",
         actionHref: "documentlibrary",
         actionId: null,
         actionTarget: null
      });
   }

   function getCloudSignUpColumn()
   {
      return (
      {
         title: "welcome.cloud.sign-up.title",
         description: "welcome.cloud.sign-up.description",
         imageUrl: "/res/components/images/help-cloud-bw-64.png",
         actionMsg: "welcome.cloud.sign-up.link",
         actionHref: "http://www.alfresco.com/cloud?utm_source=AlfEnt4&utm_medium=anchor&utm_campaign=claimnetwork",
         actionId: null,
         actionTarget: "_blank"
      });
   }

   model.showDashlet = true;
   model.userIsMember = false;
   model.userIsSiteManager = false;
   model.userIsSiteConsumer = false;

   // This WebScript will render welcome dashlets for both user and site dashboards, however
   // since each is handled differently we need to determine which type is being rendered.
   // This should be defined in a component property called "dashboardType" which should be
   // either "user" or "site"...
   var dashboardId, dashboardUrl, columns = [];
   if (args.dashboardType == "user")
   {
      dashboardId = "user/" + user.name + "/dashboard";
      dashboardUrl = "user/" + encodeURIComponent(user.name) + "/dashboard";

      model.siteURL = ""; // Not needed for user

      model.title="welcome.user";
      model.description="welcome.user.description";

      columns[0] = getTutorialColumn();
      columns[1] = getSiteColumn();
      columns[2] = getProfileColumn();
   }
   else if (args.dashboardType == "site")
   {
      // Each user has their dashboard configuration in the sitedata, and there
      // is only one configuration for each site dashboard. Whether or not it is
      // displayed is determined by user preferences. Before going any further we
      // need to establish whether the welcome dashlet should even be displayed.
      var hideDashlet = false,
          profile;

      try
      {
         // Call the repository for the site profile
         var json = remote.call("/api/sites/" + page.url.templateArgs.site);
         profile =
         {
            title: "",
            shortName: "",
            visibility: "PUBLIC"
         };

         if (json.status == 200)
         {
            // Create javascript objects from the repo response
            var obj = JSON.parse(json);
            if (obj)
            {
               profile = obj;
               model.siteNodeRef = obj.node;
            }
         }

         // Request the current user's preferences to determine whether or not
         // the dashlet should be displayed...
         var prefs = jsonUtils.toObject(preferences.value);
         // Populate the preferences object literal for easy look-up later
         var dashletprefs = eval('try{(prefs.org.alfresco.share.siteWelcome)}catch(e){}');
         if (typeof dashletprefs != "object")
         {
            dashletprefs = {};
         }
         else
         {
            // replace the forward slash "/" and dot "." characters with dash "-"
            hideDashlet = dashletprefs[profile.node.substring(1).replace(/\/|\./g, "-")] != null;
         }
      }
      catch (e)
      {
      }
   
      if (hideDashlet)
      {
         // If the user has opted not to see the welcome dashlet for this site dashboard then
         // hide the component...
         model.showDashlet = false;
      }
      else
      {
         // If there are no site welcome dashlet preferences configured for the current user
         // or the user prefers to see the welcome dashlet then continue with the rendering...
         dashboardId = "site/" + page.url.templateArgs.site + "/dashboard";
         dashboardUrl = dashboardId;
         model.siteURL = page.url.templateArgs.site;
   
         var siteTitle = (profile.title != "") ? profile.title : profile.shortName;
   
         model.site = siteTitle;
         model.title="welcome.site";
         model.description="welcome.site.description";
   
         // Call the repository to see if the user is site manager or not
         var userIsSiteManager = false,
            userIsMember = false,
            userIsSiteConsumer = true,
            obj = null;
   
         json = remote.call("/api/sites/" + page.url.templateArgs.site + "/memberships/" + encodeURIComponent(user.name));
         if (json.status == 200)
         {
            obj = JSON.parse(json);
         }
         if (obj)
         {
            userIsMember = true;
            userIsSiteManager = obj.role == "SiteManager";
            userIsSiteConsumer = obj.role == "SiteConsumer";
            model.userIsMember = userIsMember;
            model.userIsSiteManager = userIsSiteManager;
            model.userIsSiteConsumer = userIsSiteConsumer;
         }
   
         // Configure the columns in the dashlet based on the users ownership and access rights...
         if (userIsSiteManager)
         {
            // Configure the dashlet for a site manager...
            columns[0] = getSiteInfoColumn(siteTitle, "welcome.site.dashboard.link", "customise-site-dashboard");
            columns[1] = getInviteColumn();
            columns[2] = getUploadInfoColumn();
         }
         else if (userIsMember)
         {
            columns[0] = getBrowseSiteColumn();
            columns[1] = getSiteMembersColumn();
               
            if (userIsSiteConsumer)
            {
               // Configure the 3rd column for a user with read access...
               columns[2] = getProfileColumn();
            }
            else
            {
               // Configure the 3rd column for a user with write access...
               columns[2] = getUploadInfoColumn();
            }
         }  
         else if (profile.visibility == "MODERATED")
         {
            // Configure the dashlet for a moderated site
            columns[0] = getModeratedSiteColumn();
            columns[1] = getSiteMembersColumn();
            columns[2] = null;
         }
         else
         {
            model.showDashlet = false;
         }
      }
   }

   columns.push(getCloudSignUpColumn());

   model.columns = columns;
   model.dashboardUrl = dashboardUrl;
   model.dashboardId = dashboardId;
   model.dashboardType = args.dashboardType;
   var docsEdition = context.properties["docsEdition"];

   // Widget instantiation metadata...
   var dynamicWelcome = {
      id : "DynamicWelcome",
      name : "Alfresco.dashlet.DynamicWelcome",
      initArgs : ["\"" + args.htmlid + "\"",
                  "\"" + model.dashboardUrl + "\"",
                  "\"" + model.dashboardType + "\"",
                  "\"" + (model.siteNodeRef == null ? "" : model.siteNodeRef) + "\"",
                  "\"" + (model.site == null ? "" : encodeURIComponent(jsonUtils.encodeJSONString(model.site))) + "\"",
                  "\"" + docsEdition.getValue() + "\""]
   };
   model.widgets = [dynamicWelcome];
}

main();
