<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/calendar/helper.js">

function main()
{
   // Check whether the current user is a member of the site first and then if they are
   // the role of the user - until there is a method of doing this check on the web tier 
   // we have to make a call back to the repo to get this information.
   
   var role = null;
   var obj = null;
   var json = remote.call("/api/sites/" + page.url.templateArgs.site + "/memberships/" + encodeURIComponent(user.name));
   if (json.status == 200)
   {
      obj = JSON.parse(json);
   }
   if (obj)
   {
      role = obj.role;
   }
   
   // set role appropriately
   if (role !== null)
   {
      model.role = role;
   }
   else
   {
      model.role = "Consumer"; // default to safe option
   }
   model.viewType = CalendarScriptHelper.getView(); // returns current view after checking for enabled views
   model.viewToolbarViews = CalendarScriptHelper.listViews(); // returns a array of enabled views
   model.viewToolbarViewCount = CalendarScriptHelper.countViews() > 1; // if only one view, hide view switching buttons
   model.viewToolbarNav = (model.enabledViews.day || model.enabledViews.week || model.enabledViews.month) // see if the Today, Previous and Next navigation items are relevant
   
   var enabledViewList = [];
   if (model.enabledViews.day)
   {
      model.day = '<input id="' + args.htmlid + '-day" type="radio" name="navigation" value="' + msg.get("button.day") + '" />';
      enabledViewList.push("\"day\"");
   }
   if (model.enabledViews.week)
   {
      model.week ='<input id="' + args.htmlid + '-week" type="radio" name="navigation" value="' + msg.get("button.week") + '" />';
      enabledViewList.push("\"week\"");
   }   
   if (model.enabledViews.month)
   {
      model.month ='<input id="' + args.htmlid + '-month" type="radio" name="navigation" value="' + msg.get("button.month") + '" />';
      enabledViewList.push("\"month\"");
   }
   if (model.enabledViews.agenda)
   {
      model.agenda ='<input id="' + args.htmlid + '-agenda" type="radio" name="navigation" value="' + msg.get("button.agenda") + '" />';
      enabledViewList.push("\"agenda\"");
   }
   
   var calendarToolbar = {
      id : "CalendarToolbar",
      name : "Alfresco.CalendarToolbar",
      assignTo : "calendarToolbar",
      initArgs : ["\"" + args.htmlid + "\"", enabledViewList, "\"" + model.defaultView + "\""]
   };
   model.widgets = [calendarToolbar];
}

main();
