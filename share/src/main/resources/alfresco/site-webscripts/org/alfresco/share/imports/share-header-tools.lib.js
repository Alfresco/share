// This function provides a consistent way in which specific links to admin console pages can be provided
// for users that are non-admins (e.g. the site administrator)...
function addNonAdminAdministrativeMenuItem(jsonModel, menuItemConfig) {
   // See if we've already added an non-admin admin console menu popup or menu bar item...
   var nonAdminMenuItem = widgetUtils.findObject(jsonModel, "id", "HEADER_NON_ADMIN_ADMIN_CONSOLE");
   if (nonAdminMenuItem)
   {
      if (nonAdminMenuItem.name === "alfresco/menus/AlfMenuBarItem")
      {
         // Only one item has been found so far, we're going to need to convert this into a popup menu item and move
         // the existing configuration to become a menu item in the popup...
         var previousConfig = nonAdminMenuItem.config;
         nonAdminMenuItem.name = "alfresco/header/AlfMenuBarPopup";
         nonAdminMenuItem.config = {
            label: msg.get("header.menu.admin.label"),
            widgets: [
               {
                  id: "HEADER_NON_ADMIN_ADMIN_CONSOLE_GROUP",
                  name: "alfresco/menus/AlfMenuGroup",
                  config: {
                     widgets: [
                        {
                           name: "alfresco/header/AlfMenuItem",
                           config: previousConfig
                        },
                        {
                           name: "alfresco/header/AlfMenuItem",
                           config: menuItemConfig
                        }
                     ]
                  }
               }
            ]
         };
      }
      else if (nonAdminMenuItem.name === "alfresco/header/AlfMenuBarPopup")
      {
         // A drop-down menu already exists, we just need to add the menu item to it...
         var nonAdminMenuGroup = widgetUtils.findObject(jsonModel, "id", "HEADER_NON_ADMIN_ADMIN_CONSOLE_GROUP");
         if (nonAdminMenuGroup && nonAdminMenuGroup.config && nonAdminMenuGroup.config.widgets)
         {
            nonAdminMenuGroup.config.widgets.push({
               name: "alfresco/header/AlfMenuItem",
               config: menuItemConfig
            });
         }
      }
   }
   else
   {
      // No non-admin admin menu items have been found, this will be the first...
      // Find the application menu bar...
      var appMenu = widgetUtils.findObject(jsonModel, "id", "HEADER_APP_MENU_BAR");
      if (appMenu && appMenu.config && appMenu.config.widgets)
      {
         // Now that we have a reference to the application menu bar we can add a menu item to it...
         appMenu.config.widgets.push({
            id: "HEADER_NON_ADMIN_ADMIN_CONSOLE",
            name: "alfresco/menus/AlfMenuBarItem",
            config: menuItemConfig
         });
      }
   }
}
