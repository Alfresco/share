/**
 * User Profile Template script
 * 
 * This logic is used to ensure the profile page link points to a valid existing user - if not, the standard error page is shown.
 */
function main()
{
   var profileId = page.url.templateArgs["userid"];
   if (profileId != null)
   {
      // load user details for the profile from the repo
      if (user.getUser(profileId) == null)
      {
         // If the user does not exist then we are going to intentionally throw an error that will force Surf
         // to render the standard error page.
         throw new Error("A user attempted to access a profile page that no longer exists or is invalid.");
      }
   }
}

main();