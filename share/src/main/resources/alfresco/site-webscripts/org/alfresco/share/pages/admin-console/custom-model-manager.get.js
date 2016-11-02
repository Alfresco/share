<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/pages/admin-console/imports/cmm-misc.lib.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/pages/admin-console/imports/cmm-models.lib.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/pages/admin-console/imports/cmm-types-propertygroups.lib.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/pages/admin-console/imports/cmm-properties.lib.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/pages/admin-console/imports/cmm-editor.lib.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/pages/admin-console/imports/cmm-editor-modelling-service.lib.js">

/*
 * Custom Model Manager
 * 
 * This file sets up the models to define the Custom Model Manager pages.
 * 
 * @author Kevin Roast
 * @author Richard Smith
 */

var typesBlackList = {}, aspectsBlackList = {};
var types = config.scoped["CMM"]["types"].getChildren("ns");
if (types)
{
   for (var i = 0; i<types.size(); i++)
   {
      typesBlackList[types.get(i).value.toString()] = true;
   }
}
var aspects = config.scoped["CMM"]["aspects"].getChildren("ns");
if (aspects)
{
   for (var i = 0; i<aspects.size(); i++)
   {
      aspectsBlackList[aspects.get(i).value.toString()] = true;
   }
}

/**
 *
 * @returns {object} The group information for the current user
 */
function getUserGroupData() {
   var userData = {};
   var groups = user.properties["alfUserGroups"];
   if (groups != null)
   {
      groups = groups.split(",");
      var processedGroups = {};
      for (var i=0; i<groups.length; i++)
      {
         processedGroups[groups[i]] = true;
      }
      userData.groups = processedGroups;
   }
   userData.isAdmin = user.capabilities["isAdmin"];
   if (userData.isAdmin == null)
   {
      userData.isAdmin = false;
   }

   return userData;
}

model.jsonModel = {
   services: [
      {
         name: "cmm/services/CMMService",
         config: {
            typesBlackList: typesBlackList,
            aspectsBlackList: aspectsBlackList
         }
      },
      {
         name: "cmm/services/TitlePaneService",
         config: {
            clearAllTopic: [
               "CMM_EDITOR_CLEAR",
               "ALF_DND_DROPPED_ITEM_DELETED"
            ]
         }
      },
      "cmm/CMMDisplayController",
      /*
      NOTE! Making use of the global Aikau Services from share-header.lib.js
      "alfresco/services/DialogService"
      "alfresco/services/NotificationService"
      "alfresco/services/NavigationService"
      */
      modellingService.service
   ],
   widgets: [
      pageTitle,
      {
         id: "CMM",
         name: "alfresco/layout/VerticalWidgets",
         config: {
            currentItem: {
               user: getUserGroupData()
            },
            baseClass: "side-margins",
            widgets: [
               spacer.twelve,
               {
                  name: "alfresco/layout/HorizontalWidgets",
                  config: {
                     renderFilterMethod: "ANY",
                     renderFilter: [
                        {
                           property: "user.groups.GROUP_ALFRESCO_ADMINISTRATORS",
                           values: [true]
                        },
                        {
                           property: "user.groups.GROUP_ALFRESCO_MODEL_ADMINISTRATORS",
                           values: [true]
                        },
                        {
                           property: "user.isAdmin",
                           values: [true]
                        }
                     ],
                     widgets: [
                        {
                           id: "CMM_PANE_CONTAINER",
                           name: "alfresco/layout/AlfStackContainer",
                           config: {
                              paneSelectionHashVar: "view",
                              widgets: [
                                 {
                                    title: "models",
                                    name: "alfresco/layout/VerticalWidgets",
                                    selected: true,
                                    delayProcessing: false,
                                    config: {
                                       widgets: [
                                          introLabel1,
                                          spacer.eight,
                                          introLabel2,
                                          spacer.twenty,
                                          heading.createModelMenu,
                                          models.createMenu,
                                          spacer.twenty,
                                          heading.existingModels,
                                          models.list
                                       ]
                                    }
                                 },
                                 {
                                    title: "types_property_groups",
                                    name: "alfresco/layout/VerticalWidgets",
                                    delayProcessing: false,
                                    config: {
                                       widgets: [
                                          heading.currentModel,
                                          spacer.eight,
                                          heading.createTypesPropertyGroupsMenu,
                                          typePropertyGroups.createMenu,
                                          spacer.twenty,
                                          heading.types,
                                          typePropertyGroups.typesList,
                                          spacer.twelve,
                                          heading.propertyGroups,
                                          typePropertyGroups.propertyGroupsList
                                       ]
                                    }
                                 },
                                 {
                                    title: "properties",
                                    name: "alfresco/layout/VerticalWidgets",
                                    delayProcessing: false,
                                    config: {
                                       widgets: [
                                          heading.currentTypePropertyGroup,
                                          spacer.eight,
                                          heading.createPropertyMenu,
                                          properties.createMenu,
                                          spacer.twenty,
                                          heading.properties,
                                          properties.list
                                       ]
                                    }
                                 },
                                 {
                                    title: "editor",
                                    name: "alfresco/layout/VerticalWidgets",
                                    delayProcessing: false,
                                    config: {
                                       widgets: [
                                          heading.formEditorCurrentTypePropertyGroup,
                                          spacer.eight,
                                          editor.backMenu,
                                          spacer.eight,
                                          editorIntroLabel1,
                                          spacer.eight,
                                          editor.console
                                       ]
                                    }
                                 }
                              ]
                           }
                        }
                     ]
                  }
               }
            ]
         }
      }
   ]
};