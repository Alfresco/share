/**
 * User Profile Component Update method
 * 
 * @method POST
 */
 
function main()
{
   var names = json.names();
   if (names.length() == 1)
   {
      // found special case avatar noderef set
      // this value is already persisted to the repo via the uploader
      // so we just need to mirror the value in the user object properties
      var field = names.get(0);
      if (field.indexOf("-photoref") != -1)
      {
         var ref = json.get(field);
         if (ref != null && ref.length() != 0)
         {
            user.properties["avatar"] = ref;
         }
      }
   }
   else
   {
      for (var i=0; i<names.length(); i++)
      {
         var field = names.get(i);
         
         // look and set simple text input values
         var index = field.indexOf("-input-");
         if (index != -1)
         {
            user.properties[field.substring(index + 7)] = json.get(field);
         }
         // apply person description content field
         else if (field.indexOf("-text-biography") != -1)
         {
            user.properties["persondescription"] = json.get(field);
         }
      }
      user.save();
   }
   model.success = true;
}

main();