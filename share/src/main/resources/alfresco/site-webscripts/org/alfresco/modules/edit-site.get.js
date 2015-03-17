/**
 * Edit Site Details component GET method
 */

function main()
{
   // Call the repo for the sites profile
   var profile,
       json = remote.call("/api/sites/" + args.shortName);
   if (json.status == 200)
   {
      // Create javascript object from the repo response
      var obj = JSON.parse(json);
      if (obj)
      {
         profile = obj;
      }
   }
   else
   {
      status.setCode(json.status, json.status.message);
   }
   
   // Prepare the model
   model.profile = profile;
}

main();