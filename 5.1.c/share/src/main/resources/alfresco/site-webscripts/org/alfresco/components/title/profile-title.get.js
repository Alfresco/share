/**
 * User Profile - Title Component GET method
 */

function main()
{
   var profileId = page.url.templateArgs["userid"];
   if (profileId && profileId != user.name)
   {
      // load user details for the profile from the repo
      var userObj = user.getUser(profileId);
      if (userObj != null)
      {
         model.profile = userObj;
      }
      else
      {
         // fallback if unable to get user details
         model.profile = user;
      }
   }
   else
   {
      // no profile specified or selection is the current user
      model.profile = user;
   }
   
   // Save the user object to avoid multiple remote calls further down the page as the user
   // retrieval goes via user.getUser() so is not indirectly cached like other remote calls.
   context.setValue("userprofile", model.profile);
}

main();