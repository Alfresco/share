function main()
{
   var url = "/api/invitations?inviteeUserName=" + encodeURIComponent(user.name) + "&resultsLimit=200",
   result = remote.connect("alfresco").get(url),
   inviteData = [];

   if (result.status == status.STATUS_OK)
   {
      var json = JSON.parse(result.response);
      inviteData = json.data;
   }
   
   model.inviteData = inviteData;
   
   
   // Widget instantiation metadata...
   var searchConfig = config.scoped['Search']['search'],
       defaultMinSearchTermLength = searchConfig.getChildValue('min-search-term-length'),
       defaultMaxSearchResults = searchConfig.getChildValue('max-search-results');

   var inviteData = [];
   for (var i = 0; i < model.inviteData.length; i++)
   {
      var invite = {};
      invite.id = model.inviteData[i].inviteId;
      invite.siteId = model.inviteData[i].resourceName;
      invite.type = model.inviteData[i].invitationType;
      inviteData.push(invite);
   }

   
   var siteFinder = {
      id : "SiteFinder", 
      name : "Alfresco.SiteFinder",
      options : {
         currentUser : user.name,
         minSearchTermLength : parseInt((args.minSearchTermLength != null) ? args.minSearchTermLength : defaultMinSearchTermLength),
         maxSearchResults : parseInt((args.maxSearchResults != null) ? args.maxSearchResults : defaultMaxSearchResults),
         setFocus : (args.setFocus == "true"),
         inviteData: inviteData
      }
   };
   model.widgets = [siteFinder];
}

main();

