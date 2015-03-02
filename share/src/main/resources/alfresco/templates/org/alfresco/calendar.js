<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/calendar/enabledViews.js">

/**
 * Calendar template controller script
 *
 * Sets the filteredView context param so that all components can access it.
 */

function getFilteredView()
{
   var view = escape(page.url.args["view"]);
   // Check that view is enabled. If not return default.
   if (typeof(model.enabledViews) != "undefined" && !model.enabledViews[view]) 
   {
      return model.defaultView
   }
   return view;
};

context.setValue("filteredView", getFilteredView());