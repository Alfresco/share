<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-header.lib.js">

function main()
{
   var siteData = getSiteData();
   var userIsSiteManager = false;
   if (siteData != null)
   {
      userIsSiteManager = siteData.userIsSiteManager;
   }
   var messageKey = "message.siteRedirect.noPages." + (userIsSiteManager ? "manager" : "user");
   model.siteNoPagesMessage = msg.get(messageKey);
}

main();
