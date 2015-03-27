<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

if (!json.isNull("wikipage"))
{
   var wikipage = String(json.get("wikipage"));
   model.pagecontent = getPageText(wikipage);
   model.title = wikipage.replace(/_/g, " ");
}
else
{
   model.pagecontent = "<h3>" + msg.get("message.nopage") + "</h3>";
   model.title = "";
}

function getPageText(wikipage)
{
   var c = sitedata.getComponent(url.templateArgs.componentId);
   c.properties["wikipage"] = wikipage;
   c.save();

   var siteId = String(json.get("siteId"));
   var uri = "/slingshot/wiki/page/" + siteId + "/" + encodeURIComponent(wikipage) + "?format=mediawiki";

   var connector = remote.connect("alfresco");
   var result = connector.get(uri);
   if (result.status == status.STATUS_OK)
   {
      /**
       * Always strip unsafe tags here.
       * The config to option this is currently webscript-local elsewhere, so this is the safest option
       * until the config can be moved to share-config scope in a future version.
       */
      return stringUtils.stripUnsafeHTML(result.response);
   }
   else
   {
      return "";
   }
}