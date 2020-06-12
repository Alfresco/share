function main()
{
   // Widget instantiation metadata...
   var usersConfig = config.scoped['Users']['users'],
       minPasswordLength = usersConfig.getChildValue('password-min-length');
   
   var changePassword = {
      id : "ChangePassword", 
      name : "Alfresco.ChangePassword",
      options : {
         minPasswordLength : parseInt(minPasswordLength)
      }
   };
   changePassword.options.aimsEnabled = typeof aimsEnabled === "boolean" ? aimsEnabled : false;
   model.widgets = [changePassword];
}

main();

