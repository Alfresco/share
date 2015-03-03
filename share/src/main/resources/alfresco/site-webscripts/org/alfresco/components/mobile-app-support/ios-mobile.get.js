<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{
   AlfrescoUtil.param("nodeRef");
   var nodeDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site),
      repositoryUrl = null;

   if (nodeDetails && nodeDetails.metadata)
   {
      repositoryUrl = nodeDetails.metadata.serverURL;

      if (repositoryUrl != null)
      {
         if (repositoryUrl.indexOf("localhost") >= 0)
         {
            repositoryUrl = repositoryUrl.replace(/localhost/, Packages.org.springframework.extensions.surf.ServletUtil.request.serverName);
         }

         var appURL = "alfresco://doc-preview";

         // repositoryURL points to the host of the repository, e.g. http://alfresco.com:8080
         appURL += "?repositoryUrl=" + encodeURIComponent(repositoryUrl);
         
         // objectId - the nodeRef of the document
         appURL += "&objectId=" + encodeURIComponent(model.nodeRef);

         // the current user's username
         appURL += "&user=" + encodeURIComponent(user.name);

         // the current tenantID, if any
         var tenantID = context.attributes["org.alfresco.cloud.tenant.name"];
         if (tenantID != null)
         {
            appURL += "&tenant=" + encodeURIComponent(tenantID);
         }

         model.appURL = appURL;
      }
   }
}

main();
