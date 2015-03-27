function sortByTitle(link1, link2)
{
   return (link1.title > link2.title) ? 1 : (link1.title < link2.title) ? -1 : 0;
}

function main()
{
   var site, container, theUrl, connector, result, links;
   
   site = page.url.templateArgs.site;
   container = 'links';
   theUrl = '/api/links/site/' + site + '/' + container + '?page=1&pageSize=512';
   connector = remote.connect("alfresco");
   result = connector.get(theUrl);
   if (result.status == 200)
   {
      links = JSON.parse(result.response).items;
      links.sort(sortByTitle);
      model.links = links;
      model.numLinks = links.length;
   }
   
   model.userIsNotSiteConsumer = false;
   var obj = null;
   var json = remote.call("/api/sites/" + page.url.templateArgs.site + "/memberships/" + encodeURIComponent(user.name));
   if (json.status == 200)
   {
      obj = JSON.parse(json);
   }
   if (obj)
   {
      model.userIsNotSiteConsumer = obj.role != "SiteConsumer";
   }
   
   // Widget instantiation metadata...
   var dashletResizer = {
      id : "DashletResizer", 
      name : "Alfresco.widget.DashletResizer",
      initArgs : ["\"" + args.htmlid + "\"","\"" + instance.object.id + "\""],
      useMessages: false
   };
   var dashletTitleBarActions = {
      id : "DashletTitleBarActions", 
      name : "Alfresco.widget.DashletTitleBarActions",
      useMessages : false,
      options : {
         actions: [
            {
               cssClass: "help",
               bubbleOnClick:
               {
                  message: msg.get("dashlet.help")
               },
               tooltip: msg.get("dashlet.help.tooltip")
            }
         ]
      }
   };
   model.widgets = [dashletResizer, dashletTitleBarActions];
}

main();