/**
 * User Profile Component - User Notifications GET method
 */

function main()
{
   // Call the repo to retrieve user properties
   var emailFeedDisabled = false;
   var result = remote.call("/api/people/" + encodeURIComponent(user.id));
   if (result.status == 200)
   {
      var person = JSON.parse(result);
      // we are interested in the "cm:emailFeedDisabled" property
      emailFeedDisabled = person.emailFeedDisabled;
   }
   model.emailFeedDisabled = emailFeedDisabled;
   
   // Widget instantiation metadata...
   var userNotification = {
      id : "UserNotifications", 
      name : "Alfresco.UserNotifications"
   };
   model.widgets = [userNotification];
}

main();

