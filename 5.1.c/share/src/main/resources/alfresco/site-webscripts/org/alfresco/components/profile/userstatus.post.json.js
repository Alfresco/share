/**
 * User Status Update method
 * 
 * @method POST
 */
 
function main()
{
   // make remote call to update user status
   var conn = remote.connect("alfresco");
   var result = conn.post(
      "/slingshot/profile/userstatus",
      jsonUtils.toJSONString( {status: json.get("status")} ),
      "application/json");
   if (result.status == 200 && result.response != "{}")
   {
      // update local cached user with status and date updated 
      user.properties["userStatus"] = json.get("status");
      var userStatusTime = JSON.parse(result.response).userStatusTime.iso8601;
      user.properties["userStatusTime"] = userStatusTime;
      model.userStatusTime = userStatusTime;
   }
   else
   {
      status.code = result.status;
   }
}

main();