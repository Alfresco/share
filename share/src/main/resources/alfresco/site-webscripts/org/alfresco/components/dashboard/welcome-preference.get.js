/**
 * Welcome panel preference component GET method
 */


function main()
{
   // Get current template
   var dashboardId = "user/" + user.name + "/dashboard";
   
   // TODO SHA-1070: Read User Preferences
   var welcomePanelEnabled = true;
   
   model.welcomePanelEnabled = welcomePanelEnabled;
   
   var welcomePreference = {
      id : "WelcomePreference",
      name : "Alfresco.WelcomePreference",
      options: {
         welcomePanelEnabled: welcomePanelEnabled
      }
   };
   model.widgets = [welcomePreference];
}

main();
