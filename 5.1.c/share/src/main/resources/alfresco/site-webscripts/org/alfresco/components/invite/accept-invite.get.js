<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

function main()
{
   // make sure we don't redirect by default
   model.doRedirect = false;
    
   // fetch the user information from the url
   var inviteId = page.url.args.inviteId,
      inviteTicket = page.url.args.inviteTicket,
      inviteeUserName = decodeURIComponent(page.url.args.inviteeUserName);
   
   if ((inviteId == undefined) || (inviteTicket == undefined))
   {
      model.error = "Parameters missing!";
      return;
   }
             
   // do invite request and redirect if it succeedes, show error otherwise
   var theUrl, connector, result, json, data;

   theUrl = '/api/invite/' + inviteId + '/' + inviteTicket + '/accept';
   // for MT share
   if (inviteeUserName != undefined)
   {
      inviteeUserName = encodeURIComponent(inviteeUserName);
      theUrl = theUrl + '?inviteeUserName=' + inviteeUserName;
   }
   
   connector = remote.connect("alfresco-noauth");
   result = connector.put(theUrl, "{}", "application/json");
   if (result.status != status.STATUS_OK)
   {
      model.doRedirect = false;
      model.error = result.status.message; // result.response;
   }
   else
   {
      // redirect to the site dashboard
      model.doRedirect = true;
      data = JSON.parse(result.response);
      model.siteShortName = data.siteShortName;
   }
}

main();