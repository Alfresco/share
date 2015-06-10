function main()
{
   var siteId, theUrl, json, data;

   siteId = page.url.templateArgs.site;

   // get the roles available for the given site
   theUrl = "/api/sites/" + siteId + "/roles";
   json = remote.call(theUrl);
   data = JSON.parse(json);

   // add all roles except "None"
   model.siteRoles = [];
   var rolesTooltipData = { items: [] };
   for (var i = 0, j = data.siteRoles.length; i < j; i++)
   {
      if (data.siteRoles[i] != "None")
      {
         model.siteRoles.push(data.siteRoles[i]);
         rolesTooltipData.items.push({
            roleName: msg.get("role." + data.siteRoles[i]),
            roleDescription: msg.get("role." + data.siteRoles[i] + ".description")
         });
      }
   }

   // Widget instantiation metadata...
   var invitationList = {
      id : "InvitationList", 
      name : "Alfresco.InvitationList",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         roles : model.siteRoles
      }
   };
   model.widgets = [invitationList];
   
   
   // Aikau Widget Configuration
   model.jsonModel = {
      rootNodeId: args.htmlid + "-role-info",
      services: [
         "alfresco/services/LoggingService"
      ],
      widgets: [
         {
            name: "alfresco/misc/AlfTooltip",
            config: {
               widgets: [
                  {
                     name: "alfresco/html/Label",
                     config: {
                        label: " ",
                        additionalCssClasses: "alf-info-icon"
                     }
                  }
               ],
               widgetsForTooltip: [
                  {
                     name: "alfresco/html/Label",
                     config: {
                        label: msg.get("invitationlist.role-tooltip.header")
                     }
                  },
                  {
                     name: "alfresco/lists/views/AlfListView",
                     config: {
                        widgets: [
                            {
                               name: "alfresco/lists/views/layouts/Row",
                               config: {
                                  widgets: [
                                     {
                                        name: "alfresco/lists/views/layouts/Cell",
                                        config: {
                                           widgets: [
                                              {
                                                 name: "alfresco/renderers/Property",
                                                 config: {
                                                    propertyToRender: "roleName",
                                                    renderOnNewLine: false,
                                                    renderedValueClass: "alf-role-tooltip-role-name"
                                                 }
                                              },
                                              {
                                                 name: "alfresco/renderers/Property",
                                                 config: {
                                                    propertyToRender: "roleDescription",
                                                    renderOnNewLine: false
                                                 }
                                              }
                                           ]
                                        }
                                     }
                                  ]
                               }
                            }
                        ],
                        currentData: rolesTooltipData
                     }
                  },
                  {
                     name: "alfresco/html/Label",
                     config: {
                        label: msg.get("invitationlist.role-tooltip.docs-url-label")
                     }
                  }
               ],
               additionalCssClasses: "alf-roles-tooltip",
               triggeringEvent: "click",
               tooltipStyle: "width: 350px;"
            }
         }
      ]
   };
}

main();

