/**
 * User Profile Component - Cloud Authentication Details
 */

function main()
{
   model.syncEnabled = (syncMode.getValue() != "OFF");
   if (model.syncEnabled)
   {
      model.cloudConnected = false;
      model.email = "";
      model.lastLoginSuccessful = false;

      var connectionResult = remote.call("/cloud/person/credentials");

      // Check and parse the response
      if (connectionResult.status == 200)
      {
         var connectionSettings = JSON.parse(connectionResult);
         if (connectionSettings.known)
         {
            model.cloudConnected = true;
            model.email= connectionSettings.username;
            model.lastLoginSuccessful = connectionSettings.lastLoginSuccessful;
         }
      }

      // If we're not connected, fetch the User's email address to auto fill into the form field.
      if (model.email === "")
      {
         var personResult = remote.call("/api/people/" + encodeURIComponent(user.id));
         if (personResult.status == 200)
         {
            var person = JSON.parse(personResult);
            model.email = person.email;
         }
      }
      
      var cloudAuth = {
         id : "UserCloudAuth", 
         name : "Alfresco.UserCloudAuth"
      };
      model.widgets = [cloudAuth];
   }
}

main();
