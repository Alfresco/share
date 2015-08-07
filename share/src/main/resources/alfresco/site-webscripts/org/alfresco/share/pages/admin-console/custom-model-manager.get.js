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

model.jsonModel = {
   groupMemberships: user.properties["alfUserGroups"],
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
            baseClass: "side-margins",
            renderFilterMethod: "ANY",
            renderFilter: [
               {
                  target: "groupMemberships",
                  property: "GROUP_ALFRESCO_ADMINISTRATORS",
                  values: [true]
               },
               {
                  target: "groupMemberships",
                  property: "GROUP_ALFRESCO_MODEL_ADMINISTRATORS",
                  values: [true]
               }
            ],
            widgets: [
               spacer.twelve,
               {
                  name: "alfresco/layout/HorizontalWidgets",
                  config: {
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