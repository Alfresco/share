<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

function main()
{
   // check that we got all required information
   var check = ["inviteId", "inviteeUserName", "siteShortName", "inviteTicket"];
   for (var i = 0, j = check.length; i < j; i++)
   {
      if (page.url.args[check[i]] == undefined)
      {
         // redirect to error page
         status.code = 400;
         status.message = "One or more parameters are missing";
         return;
      }
   }
   
   // fetch the user information from the url
   var inviteId = page.url.args.inviteId,
      inviteTicket = page.url.args.inviteTicket,
      inviteeUserName = decodeURIComponent(page.url.args.inviteeUserName);
   
   // fetch the invite information, which gives us all information required to display the page
   var theUrl, connector, json, data;
   
   theUrl = "/api/invite/" + inviteId + "/" + inviteTicket;
   // for MT share
   if (inviteeUserName != undefined)
   {
      inviteeUserName = encodeURIComponent(inviteeUserName);
      theUrl = theUrl + '?inviteeUserName=' + inviteeUserName;
   }
   
   connector = remote.connect("alfresco-noauth");
   json = connector.call(theUrl);
   if (json.status == 200)
   {
      // Create javascript objects from the repo response
      data = JSON.parse(json);
      model.invite = data.invite;
   }
   else
   {
      // Inform the user that there is no invite object available
      model.error = true;
   }
   
   // Widget instantiation metadata...
   if (!model.error)
   {
      var rejectInvite = {
         id : "RejectInvite", 
         name : "Alfresco.RejectInvite",
         options : {
            inviteId : (page.url.args.inviteId != null) ? page.url.args.inviteId : "",
            inviteTicket : (page.url.args.inviteTicket != null) ? page.url.args.inviteTicket : "",
            inviteeUserName : (page.url.args.inviteeUserName != null) ? page.url.args.inviteeUserName : ""
         }
      };
      model.widgets = [rejectInvite];
   }
}

main();

