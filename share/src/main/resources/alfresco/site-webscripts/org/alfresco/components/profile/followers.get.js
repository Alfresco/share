<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

/**
 * User Profile Component - Followers list GET method
 */

function main()
{
   var userId = page.url.templateArgs["userid"];
   if (userId == null)
   {
      userId = user.name;
   }
   
   var result = remote.call("/api/subscriptions/" + encodeURIComponent(userId) + "/followers");
   model.numPeople = 0;
   if (result.status == 200)
   {
      var people = JSON.parse(result);
      var peopleCount = people.people.length;
      // convert status update times to relative time messages
      for (var i=0,person; i<peopleCount; i++)
      {
         person = people.people[i];
      }
      model.data = people;
      model.numPeople = peopleCount;
   }
}
main();