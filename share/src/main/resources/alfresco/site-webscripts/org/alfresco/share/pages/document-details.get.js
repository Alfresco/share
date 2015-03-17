<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/include/documentlist.lib.js">

/* *********************************************************************************
 *                                                                                 *
 * QUICK SHARE LINK                                                                *
 *                                                                                 *
 ***********************************************************************************/

var quickShareLink = "",
    quickShareConfig = config.scoped["Social"]["quickshare"];
if (quickShareConfig)
{
   var configValue = quickShareConfig.getChildValue("url");
   if (configValue != null)
   {
      quickShareLink = configValue.replace("{context}", url.context);
   }
}

/* *********************************************************************************
 *                                                                                 *
 * SOCIAL LINKS                                                                    *
 *                                                                                 *
 ***********************************************************************************/


var socialLinks = [],
    socialLinksConfig = config.scoped["Social"]["linkshare"];
if (socialLinksConfig !== null)
{
   var configs = socialLinksConfig.getChildren(),
       configItem,
       sortLabel,
       sortValue,
       valueTokens;

   if (configs)
   {
      for (var i = 0; i < configs.size(); i++)
      {
         configItem = configs.get(i);
         sortLabel = String(configItem.attributes["label"]);
         sortValue = String(configItem.value);
         socialLinks[i] = {
            id: configItem.attributes["id"],
            type: configItem.attributes["type"],
            index: configItem.attributes["index"],
            params: []
         };
         var params = configItem.getChildren();
         if (params)
         {
            for (var j = 0; j < params.size(); j++)
            {
               var paramConfig = params.get(j);
               var param = {};
               param[paramConfig.attributes["name"]] = paramConfig.value
               socialLinks[i].params[j] = param;
            }
         }
      }
   }
}

function getNodeMetadata(proxy, api, nodeRef)
{
   var result = remote.connect(proxy).get("/" + api + "/node/" + nodeRef.replace(/:\//g, "") + "/metadata"),
      node;
   if (result.status == 200)
   {
      var nodeMetadata = JSON.parse(result);
      node = {};
      node.name = nodeMetadata.name || nodeMetadata.title;
      node.mimeType = nodeMetadata.mimetype;
      node.size = nodeMetadata.size || "0";
      node.thumbnailModifications = nodeMetadata.lastThumbnailModificationData;
      node.thumbnails = nodeMetadata.thumbnailDefinitions;
   }
   return node;
}

// Check to see that Node information has been included in the URL...
// Otherwise there will be nothing to display...
if (page.url.templateArgs.store_type != null && 
    page.url.templateArgs.store_id != null &&
    page.url.templateArgs.id != null)
{
   var nodeRef = page.url.templateArgs.store_type + "://" + page.url.templateArgs.store_id + "/" + page.url.templateArgs.id,
       node = null;

   var nodeMetadata = getNodeMetadata("alfresco", "api", nodeRef);
   var jsonNode = AlfrescoUtil.getNodeDetails(nodeRef, null, {
         actions: true
   });
   if (nodeMetadata)
   {
      // Get the actions for the node...
      var actions = [];
      for (var i=0; i<jsonNode.item.actions.length; i++)
      {
         var actionConfig = jsonNode.item.actions[i];
         var action = {
            name: "alfresco/menus/AlfMenuBarItem",
            config: {
               label: actionConfig.label,
               iconImage: "/share/res/components/documentlibrary/actions/" + actionConfig.icon + "-16.png",
               type: actionConfig.type,
               publishTopic: "ALF_SINGLE_DOCUMENT_ACTION_REQUEST",
               publishPayload: {
                  document: jsonNode.item,
                  action: actionConfig
               }
            }
         };
         actions.push(action);
      }

      // Get the properties of the node....
      var properties = [];
      for (var key in jsonNode.item.node.properties)
      {
         var value = jsonNode.item.node.properties[key];
         var property = {
            name: "alfresco/renderers/Property",
            config: {
               propertyToRender: "node.properties." + key,
               currentItem: jsonNode.item,
               label: key,
               renderOnNewLine: true
            }
         }
         properties.push(property);
      }


      model.jsonModel = {
         services: [
            {
               name: "alfresco/services/ContentService"
            },
            // {
            //    name: "alfresco/services/ActionService",
            //    config: {
            //       customAggregatedJsResource: customAggregatedJsResource,
            //       customAggregatedCssResource: customAggregatedCssResource,
            //       siteId: siteId,
            //       containerId: containerId, 
            //       rootNode: rootNode,
            //       repositoryUrl: getRepositoryUrl(),
            //       replicationUrlMapping: getReplicationUrlMappingJSON()
            //    }
            // },
            // {
            //    name: "alfresco/services/TagService",
            //    config: {
            //       siteId: siteId,
            //       containerId: containerId, 
            //       rootNode: rootNode
            //    }
            // },
            {
               name: "alfresco/services/RatingsService"
            },
            {
               name: "alfresco/services/QuickShareService",
               config: {
                  quickShareLink: quickShareLink,
                  socialLinks: socialLinks
               }
            },
            {
               name: "alfresco/services/TagService"
            }
         ],
         widgets: [
            {
               id: "SET_PAGE_TITLE",
               name: "alfresco/header/SetTitle",
               config: {
                  title: "Document Details"
               }
            },
            {
               id: "SHARE_VERTICAL_LAYOUT",
               name: "alfresco/layout/VerticalWidgets",
               config: 
               {
                  widgets: [
                     {
                        name: "alfresco/documentlibrary/AlfBreadcrumbTrail",
                        config: {
                           rootLabel: "Documents",
                           hide: false,
                           _currentNode: jsonNode.item,
                           currentPath: jsonNode.item.location.path
                        }
                     },
                     {
                        name: "alfresco/layout/LeftAndRight",
                        config: {
                           style: {
                              marginTop: "10px",
                              marginBottom: "10px"
                           },
                           widgets: [
                              {
                                 name: "alfresco/renderers/FileType",
                                 align: "left",
                                 config: {
                                    currentItem: jsonNode.item
                                 }
                              },
                              {
                                 name: "alfresco/layout/VerticalWidgets",
                                 config: {
                                    align: "left",
                                    style: {
                                       marginLeft: "20px"
                                    },
                                    widgets: [
                                       {
                                          name: "alfresco/layout/LeftAndRight",
                                          config: {
                                             style: {
                                                marginBottom: "5px"
                                             },
                                             widgets: [
                                                {
                                                   name: "alfresco/renderers/InlineEditProperty",
                                                   align: "left",
                                                   config: {
                                                      propertyToRender: "node.properties.cm:name",
                                                      postParam: "prop_cm_name",
                                                      currentItem: jsonNode.item,
                                                      renderSize: "large"
                                                   }
                                                },
                                                {
                                                   name: "alfresco/renderers/Separator",
                                                   align: "left"
                                                },
                                                {
                                                   name: "alfresco/renderers/Version",
                                                   align: "left",
                                                   config: {
                                                      currentItem: jsonNode.item
                                                   }
                                                }
                                             ]
                                          }
                                       },
                                       {
                                          name: "alfresco/layout/LeftAndRight",
                                          config: {
                                             widgets: [
                                                {
                                                   name: "alfresco/renderers/Date",
                                                   align: "left",
                                                   config: {
                                                      currentItem: jsonNode.item
                                                   }
                                                },
                                                {
                                                   name: "alfresco/renderers/Separator",
                                                   align: "left"
                                                },
                                                {
                                                   name: "alfresco/renderers/Favourite",
                                                   align: "left",
                                                   config: {
                                                      currentItem: jsonNode.item
                                                   }
                                                },
                                                {
                                                   name: "alfresco/renderers/Separator",
                                                   align: "left"
                                                },
                                                {
                                                   name: "alfresco/renderers/Like",
                                                   align: "left",
                                                   config: {
                                                      currentItem: jsonNode.item
                                                   }
                                                },
                                                {
                                                   name: "alfresco/renderers/Separator",
                                                   align: "left"
                                                },
                                                {
                                                   name: "alfresco/renderers/Comments",
                                                   align: "left",
                                                   config: {
                                                      currentItem: jsonNode.item
                                                   }
                                                },
                                                {
                                                   name: "alfresco/renderers/Separator",
                                                   align: "left"
                                                },
                                                {
                                                   name: "alfresco/renderers/QuickShare",
                                                   align: "left",
                                                   config: {
                                                      currentItem: jsonNode.item
                                                   }
                                                }
                                             ]
                                          }
                                       }
                                    ]
                                 }
                              },
                              {
                                 name: "alfresco/buttons/AlfButton",
                                 align: "right",
                                 config: {
                                    label: "Download",
                                    currentItem: jsonNode.item
                                 }
                              }
                           ]
                        }
                     },
                     {
                        name: "alfresco/layout/HorizontalWidgets",
                        config: {
                           widgetMarginLeft: 5,
                           widgetMarginRight: 5,
                           widgets: [
                              {
                                 name: "alfresco/preview/AlfDocumentPreview",
                                 config: {
                                    nodeRef: nodeRef,
                                    thumbnailModification : nodeMetadata.thumbnailModifications,
                                    name : nodeMetadata.name,
                                    mimeType : nodeMetadata.mimeType,
                                    size: nodeMetadata.size,
                                    thumbnails : nodeMetadata.thumbnails,
                                    api: "api",
                                    proxy: "alfresco"
                                 }
                              },
                              {
                                 name: "alfresco/layout/VerticalWidgets",
                                 widthPx: 300,
                                 config: {
                                    widgets: [
                                       {
                                          name: "alfresco/layout/ClassicWindow",
                                          config: {
                                             title: "Document Actions",
                                             widgets: [
                                                {
                                                   name: "alfresco/menus/AlfVerticalMenuBar",
                                                   config: {
                                                      widgets: actions
                                                   }
                                                }
                                             ]
                                          }
                                       },
                                       {
                                          name: "alfresco/layout/ClassicWindow",
                                          config: {
                                             title: "Tags",
                                             widgets: [
                                                {
                                                   name: "alfresco/renderers/Tags",
                                                   config: {
                                                      currentItem: jsonNode.item,
                                                      propertyToRender: "node.properties.cm:taggable",
                                                      postParam: "prop_cm_taggable",
                                                      warnIfNotAvailable: true,
                                                      warnIfNoteAvailableMessage: "no.tags.message",
                                                      renderFilter: [
                                                         {
                                                            property: "workingCopy.isWorkingCopy",
                                                            values: [false],
                                                            renderOnAbsentProperty: true
                                                         }
                                                      ]
                                                   }
                                                }
                                             ]
                                          }
                                       },
                                       {
                                          name: "alfresco/layout/ClassicWindow",
                                          config: {
                                             title: "Properties",
                                             widgets: properties
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
   }
   else
   {
      model.jsonModel = {
         services: [],
         widgets: [
            {
               id: "SET_PAGE_TITLE",
               name: "alfresco/header/SetTitle",
               config: {
                  title: "Invalid Node Reference"
               }
            }
         ]
      };
   }
}
else
{
   // No nodeRef...
   model.jsonModel = {
      services: [],
      widgets: [
         {
            id: "SET_PAGE_TITLE",
            name: "alfresco/header/SetTitle",
            config: {
               title: "No NodeRef Supplied"
            }
         }
      ]
   };
}

