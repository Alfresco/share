/**
 * User Notifications Update method
 * 
 * @method POST
 */
 
function main()
{
   // make remote call to update user notification setting on person object
   var emailFeedDisabled = true;
   if (json.has("user-notifications-email"))
   {
      emailFeedDisabled = !(json.get("user-notifications-email") == "on");
   }
   var conn = remote.connect("alfresco");
   var result = conn.post(
      "/slingshot/profile/userprofile",
      jsonUtils.toJSONString(
         {
            "username": user.id,
            "properties":
            {
               "cm:emailFeedDisabled": emailFeedDisabled
            }
         }),
      "application/json");
   if (result.status == 200)
   {
      model.success = true;
   }
   else
   {
      model.success = false;
      status.code = result.status;
   }
}

main();