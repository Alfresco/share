var pageDefinition;
if (page.url.templateArgs.pagename != null)
{
   // If a page name has been supplied then retrieve it's details...
   // By passing the name only a single result should be returned...
   var json = remote.call("/remote-share/pages/name/" + page.url.templateArgs.pagename);
       pageDetails = null;
   try
   {
      if (json.status == 200)
      {
         pageDetails = JSON.parse(json.response);
      }
      else
      {
         model.jsonModelError = "remote.page.error.remotefailure";
      }
      if (pageDetails &&
          pageDetails.items != null &&
          pageDetails.items.length == 1 &&
          pageDetails.items[0].content != null &&
          pageDetails.items[0].content != "")
      {
         pageDefinition = pageDetails.items[0].content;
      }
      else
      {
         model.jsonModelError = "remote.page.error.invalidData";
         model.jsonModelErrorArgs = pageDetails;
      }

      model.jsonModel = JSON.parse(pageDefinition);
      model.jsonModel.groupMemberships = user.properties["alfUserGroups"];
   }
   catch(e)
   {
      model.jsonModelError = "remote.page.load.error";
      model.jsonModelErrorArgs = page.url.templateArgs.pagename;
   }
}
else
{
   // No page name supplied...
   model.jsonModelError = "remote.page.error.nopage"
}

