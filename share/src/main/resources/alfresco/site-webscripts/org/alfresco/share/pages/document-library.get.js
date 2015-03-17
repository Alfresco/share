<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/document-library.lib.js">

var siteData = getSiteData();
if (siteData != null)
{
   // Get the initial header widgets...
   var widgets = getHeaderModel(siteData.profile.title);

   // Get the DocLib specific services and widgets...
   var docLibServices = getDocumentLibraryServices();
   var docLibWidgets = getDocumentLibraryModel(siteData.profile.shortName, "documentlibrary", null, "documentlibrary.root.label");

   // Add the DocLib services and widgets...
   widgets.push(docLibWidgets);

   // Push services and widgets into the getFooterModel to return with a sticky footer wrapper
   model.jsonModel = getFooterModel(docLibServices, widgets);
   model.jsonModel.groupMemberships = user.properties["alfUserGroups"];
}
else
{
   // Output a warning if there is no site data? Or just render repository?
}
