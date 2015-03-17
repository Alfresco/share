<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

/**
 * User Profile Component - Following list GET method
 */

function main()
{
   var userId = page.url.templateArgs["userid"];
   if (userId == null)
   {
      userId = user.name;
   }
   
   model.activeUserProfile = (userId == null || userId == user.name);
   
   var result = remote.call("/api/subscriptions/" + encodeURIComponent(userId) + "/following");
   model.numPeople = 0;
   if (result.status == 200)
   {
      var people = JSON.parse(result);
      model.data = people;
      model.numPeople = people.people.length;
   }
   
   var result = remote.call("/api/subscriptions/" + encodeURIComponent(userId) + "/private");
   model.privatelist = false;
   if (result.status == 200)
   {
      model.privatelist = JSON.parse(result)['private'];
   }

   // Widget instantiation metadata...
   var following = {
      id: "Following",
      name: "Alfresco.Following",
      options:
      {
         isPrivate: model.privatelist
      }
   };
   model.widgets = [following];
}

main();