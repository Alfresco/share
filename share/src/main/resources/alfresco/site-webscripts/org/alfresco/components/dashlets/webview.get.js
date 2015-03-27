function main()
{
   var uri = args.webviewURI,
      webviewTitle = '',
      isDefault = false;

   if (!uri)
   {
      // Use the default
      var conf = new XML(config.script);
      uri = conf.uri[0].toString();
      isDefault = true;
   }

   if (args.webviewTitle)
   {
      webviewTitle = args.webviewTitle;
   }

   var height = args.height;
   if (!height)
   {
      height = "";
   }

   var re = /^(http|https):\/\//;

   if (!isDefault && !re.test(uri))
   {
      uri = "http://" + uri;
   }

   model.webviewTitle = webviewTitle;
   model.uri = uri;
   model.height = height;
   model.isDefault = isDefault;

   var userIsSiteManager = true;
   if (page.url.templateArgs.site)
   {
      // We are in the context of a site, so call the repository to see if the user is site manager or not
      userIsSiteManager = false;
      var json = remote.call("/api/sites/" + page.url.templateArgs.site + "/memberships/" + encodeURIComponent(user.name));

      if (json.status == 200)
      {
         var obj = JSON.parse(json);
         if (obj)
         {
            userIsSiteManager = (obj.role == "SiteManager");
         }
      }
   }
   model.userIsSiteManager = userIsSiteManager;
   
   // Widget instantiation metadata...
   var webView = {
      id : "WebView", 
      name : "Alfresco.dashlet.WebView",
      assignTo : "webView",
      options : {
         componentId : instance.object.id,
         webviewURI : model.uri,
         webviewTitle : model.webviewTitle,
         webviewHeight : model.height,
         isDefault : model.isDefault
      }
   };

   var dashletResizer = {
      id : "DashletResizer", 
      name : "Alfresco.widget.DashletResizer",
      initArgs : ["\"" + args.htmlid + "\"", "\"" + instance.object.id + "\""],
      useMessages: false
   };

   var actions = [];
   if (model.userIsSiteManager)
   {
      actions.push(
      {
         cssClass: "edit",
         eventOnClick: {
            _alfValue : "editWebViewDashletEvent" + args.htmlid.replace(/-/g, "_"),
            _alfType: "REFERENCE"
         }, 
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
   
   var dashletTitleBarActions = {
      id : "DashletTitleBarActions", 
      name : "Alfresco.widget.DashletTitleBarActions",
      useMessages : false,
      options : {
         actions: actions
      }
   };
   model.widgets = [webView, dashletResizer, dashletTitleBarActions];
}

main();