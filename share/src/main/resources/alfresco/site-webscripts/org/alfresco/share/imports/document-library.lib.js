<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-header.lib.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-footer.lib.js">


/* *********************************************************************************
 *                                                                                 *
 * GET ALL USER PREFERENCES                                                        *
 *                                                                                 *
 ***********************************************************************************/
function getUserPreferences() {
   var userPreferences = {};
   var prefs = JSON.parse(preferences.value);
   return prefs
}

/* *********************************************************************************
 *                                                                                 *
 * DEFAULTS                                                                        *
 *                                                                                 *
 ***********************************************************************************/
var userPreferences = getUserPreferences();
var docLibPreferences = eval('try{(userPreferences.org.alfresco.share.documentList)}catch(e){}');
if (typeof docLibPreferences != "object")
{
   docLibPreferences = {};
}
var viewRendererName =  (docLibPreferences.viewRendererName ? docLibPreferences.viewRendererName : "detailed");
var sortField = (docLibPreferences.sortField ? docLibPreferences.sortField : "cm:name");
var sortAscending = ((docLibPreferences.sortAscending != null) ? docLibPreferences.sortAscending : true);
var showFolders = ((docLibPreferences.showFolders != null) ? docLibPreferences.showFolders : true);
var hideBreadcrumbTrail = ((docLibPreferences.hideNavBar != null) ? docLibPreferences.hideNavBar : true); // Using "hideNavBar" for breadcrumb trail as it previously existed
var showSidebar = ((docLibPreferences.showSidebar != null) ? docLibPreferences.showSidebar : true); // "showSidebar" is a new 4.2E preference

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


/* *********************************************************************************
 *                                                                                 *
 * SORT FILE OPTIONS                                                               *
 *                                                                                 *
 ***********************************************************************************/


/* *********************************************************************************
 *                                                                                 *
 * SELECTED ITEMS ACTION OPTIONS                                                   *
 *                                                                                 *
 ***********************************************************************************/
// Actions
var getMultiActionImage = function(attr) {
   var imageUrl = url.context + "/res/components/documentlibrary/actions/";
   if (attr["icon"])
   {
      imageUrl += attr["icon"];
   }
   else if (attr["id"])
   {
      imageUrl += attr["id"];
   }
   else
   {
      imageUrl += generic;
   }
   imageUrl += "-16.png";
   return imageUrl;
};

var multiSelectConfig = config.scoped["DocumentLibrary"]["multi-select"],
    multiSelectActions = multiSelectConfig.getChildren("action"),
    actionSet = [];

var multiSelectAction;
for (var i = 0; i < multiSelectActions.size(); i++)
{
   multiSelectAction = multiSelectActions.get(i);
   attr = multiSelectAction.attributes;

   if(!attr["syncMode"] || attr["syncMode"].toString() == syncMode.value)
   {
      var getActionItemImage = function(attr) {
         if (attr["icon"])
         {
            return url.context + "/res/components/documentlibrary/actions/" + attr["icon"] + "-16.png";
         }
         else
         {
            return url.context + "/res/components/documentlibrary/actions/" + attr["type"] + "-16.png";
         }
      };
      
      // Multi-Select Actions
      // Note that we're using an AlfDocumentActionMenuItem widget here...
      // This particular widget extends the AlfFilteringMenuItem (which in turn
      // extends AlfMenuItem) to subscribe to publications on the "ALF_FILTER_SELECTED_FILE_ACTIONS"
      // topic (although that could be overridden by setting a 'filterTopic' attribute
      // in the widget config). The ActionService on the page will publish on this topic
      // each time the selected documents changes and the payload will contain all of the
      // permissions and aspect data for the selected documents. The AlfDocumentActionMenuItem
      // widget will compare that data against it's configuration and show/hide itself appropriately.
      var action = {
         name: "alfresco/documentlibrary/AlfDocumentActionMenuItem",
         config: {
            id: attr["id"] ? attr["id"].toString() : "",
            label: attr["label"] ? attr["label"].toString() : "",
            iconImage: getMultiActionImage(attr),
            type: attr["type"] ? attr["type"].toString() : "",
            permission: attr["permission"] ? attr["permission"].toString() : "",
            asset: attr["asset"] ? attr["asset"].toString() : "",
            href: attr["href"] ? attr["href"].toString() : "",
            hasAspect: attr["hasAspect"] ? attr["hasAspect"].toString() : "",
            notAspect: attr["notAspect"] ? attr["notAspect"].toString() : "",
            publishTopic: "ALF_MULTIPLE_DOCUMENT_ACTION_REQUEST",
            publishPayload: {
               action: attr["id"] ? attr["id"].toString() : ""
            }
         }
      };
      actionSet.push(action);
   }
}

/* *********************************************************************************
 *                                                                                 *
 * CREATE CONTENT OPTIONS                                                          *
 *                                                                                 *
 ***********************************************************************************/

var createContent = [];

// Create content config items
// var createContentConfig = config.scoped["DocumentLibrary"]["create-content"];
// if (createContentConfig !== null)
// {
//    var contentConfigs = createContentConfig.getChildren("content");
//    if (contentConfigs)
//    {
//       var attr, content, contentConfig, paramConfigs, paramConfig, permissionsConfigs, permissionConfigs, permissionConfig;
//       for (var i = 0; i < contentConfigs.size(); i++)
//       {
//          contentConfig = contentConfigs.get(i);
//          attr = contentConfig.attributes;

//          var getCreateContentImage = function(attr) {
//             var imageUrl = url.context + "/res/components/images/filetypes/";
//             if (attr["icon"])
//             {
//                imageUrl += attr["icon"];
//             }
//             else if (attr["id"])
//             {
//                imageUrl += attr["id"];
//             }
//             else
//             {
//                imageUrl += generic;
//             }
//             imageUrl += "-file-16.png";
//             return imageUrl;
//          };
         
//          var content = {
//             name: "alfresco/documentlibrary/AlfCreateContentMenuItem",
//             config: {
//                iconImage: getCreateContentImage(attr),
//                label: attr["label"] ? attr["label"].toString() : attr["id"] ? "create-content." + attr["id"].toString() : null,
//                index: parseInt(attr["index"] || "0"),
//                permission: "CreateChildren",
//                publishTopic: "ALF_CREATE_CONTENT",
//                publishPayload: {
//                   action: attr["id"] ? attr["id"].toString() : "",
//                   type: attr["type"] ? attr["type"].toString() : null,
//                   params: {},
//                }
//             }
//          };

//          // Read params
//          paramConfigs = contentConfig.getChildren("param");
//          for (var pi = 0; pi < paramConfigs.size(); pi++)
//          {
//             paramConfig = paramConfigs.get(pi);
//             if (paramConfig.attributes["name"])
//             {
//                content.config.publishPayload.params[paramConfig.attributes["name"]] = (paramConfig.value || "").toString();
//             }
//          }

//          // Read permissions
//          permissionsConfigs = contentConfig.getChildren("permissions");
//          if (permissionsConfigs.size() > 0)
//          {
//             var allow, deny, value, match;
//             permissionConfigs = permissionsConfigs.get(0).getChildren("permission");
//             for (var pi = 0; pi < permissionConfigs.size(); pi++)
//             {
//                permissionConfig = permissionConfigs.get(pi);
//                allow = permissionConfig.attributes["allow"];
//                deny = permissionConfig.attributes["deny"];
//                value = (permissionConfig.value || "").toString();
//                if (value.length() > 0)
//                {
//                   match = true;
//                   if (allow != null)
//                   {
//                      match = (allow == "true");
//                   }
//                   else if (deny != null)
//                   {
//                      match = (deny == "false");
//                   }
//                   content.config.permission += (content.config.permission.length == 0 ? "" : ",") + (value + ":" + match);
//                }
//             }
//          }

//          if (!content.config.publishPayload.type)
//          {
//             /**
//              * Support simple/old configs like below by making them of type "pagelink" pointing to the create-content page.
//              * <content id="xml" mimetype="text/xml" label="create-content.xml" itemid="cm:content" permission="Write" formid=""/>
//              */
//             var permission = attr["permission"] ? attr["permission"].toString() : null,
//                 mimetype = attr["mimetype"] ? attr["mimetype"].toString() : null,
//                 itemid = attr["itemid"] ? attr["itemid"].toString() : null,
//                 formid = attr["formid"] ? attr["formid"].toString() : null,
//                 _url = "create-content?destination={node.nodeRef}";
//             if (permission)
//             {
//                content.config.permission += (content.config.permission.length == 0 ? "" : ",") + permission;
//             }
//             if (itemid)
//             {
//                _url += "&itemId=" + itemid;
//             }
//             if (formid)
//             {
//                _url += "&formId=" + formid;
//             }
//             if (mimetype)
//             {
//                _url += "&mimeType=" + mimetype;
//             }

//             content.config.publishPayload.type = "pagelink";
//             content.config.publishPayload.params.page = _url;
//          }

//          createContent.push(content);
//       }
//    }
// }


function generateCreateContentMenuItem(menuItemLabel, dialogTitle, iconClass, modelType, mimeType, contentWidgetName, contentWidgetConfig, additionalWidgets) {
   var menuItem = {
      name: "alfresco/documentlibrary/AlfCreateContentMenuItem",
      config: {
         label: menuItemLabel,
         iconClass: iconClass,
         publishTopic: "ALF_CREATE_FORM_DIALOG_REQUEST",
         publishPayloadType: "PROCESS",
         publishPayloadModifiers: ["processCurrentItemTokens"],
         publishPayload: {
            dialogTitle: dialogTitle,
            dialogConfirmationButtonTitle: "Create",
            dialogCancellationButtonTitle: "Cancel",
            formSubmissionTopic: "ALF_CREATE_CONTENT_REQUEST",
            formSubmissionPayloadMixin: {
               type: modelType,
               prop_mimetype: (mimeType != null ? mimeType : "")
            },
            fixedWidth: true,
            widgets: [
               {
                  name: "alfresco/forms/controls/DojoValidationTextBox",
                  config: {
                     label: msg.get("create.content.name.label"),
                     name: "prop_cm_name",
                     value: "",
                     requirementConfig: {
                        initialValue: true
                     }
                  }
               },
               {
                  name: "alfresco/forms/controls/DojoValidationTextBox",
                  config: {
                     label: msg.get("create.content.title.label"),
                     name: "prop_cm_title",
                     value: ""
                  }
               },
               {
                  name: "alfresco/forms/controls/DojoTextarea",
                  config: {
                     label: msg.get("create.content.description.label"),
                     name: "prop_cm_description",
                     value: ""
                  }
               }
            ]
         }
      }
   };
   // If a content widget name has been specified then define the additional widget
   // and add in any additionally supplied configuration for it
   if (contentWidgetName != null)
   {
      var contentWidget = {
         name: contentWidgetName,
         config: {
            label: msg.get("create.content.content.label"),
            name: "prop_cm_content",
            value: ""
         }
      };
      if (contentWidgetConfig != null)
      {
         for (var key in contentWidgetConfig)
         {
            contentWidget.config[key] = contentWidgetConfig[key];
         }
      }
      menuItem.config.publishPayload.widgets.push(contentWidget);
   }

   // Add in any additional widgets requested...
   menuItem.config.publishPayload.widgets.concat(additionalWidgets != null ? additionalWidgets : []);
   return menuItem;
}

// Add in the create content options...
var folder = generateCreateContentMenuItem(msg.get("create.folder.label"), msg.get("create.folder.title"), "alf-showfolders-icon", "cm:folder", null);
var plainText = generateCreateContentMenuItem(msg.get("create.text-document.label"), msg.get("create.text-document.title"), "alf-textdoc-icon", "cm:content", "text/plain", "alfresco/forms/controls/DojoTextarea");
var html = generateCreateContentMenuItem(msg.get("create.html-document.label"), msg.get("create.html-document.title"), "alf-htmldoc-icon", "cm:content", "text/html", "alfresco/forms/controls/TinyMCE");
var xml = generateCreateContentMenuItem(msg.get("create.xml-document.label"), msg.get("create.xml-document.title"), "alf-xmldoc-icon", "cm:content", "text/xml", "alfresco/forms/controls/CodeMirrorEditor", { editMode: "xml"});
createContent.splice(0, 0, folder, plainText, html, xml);

// Create content by template
var createContentByTemplateConfig = config.scoped["DocumentLibrary"]["create-content-by-template"];
createContentByTemplateEnabled = createContentByTemplateConfig !== null ? createContentByTemplateConfig.value.toString() == "true" : false;
if (createContentByTemplateEnabled)
{
   createContent.push({
      name: "alfresco/documentlibrary/AlfCreateTemplateContentMenu"
   });
}


/**
 * Helper function to retrieve configuration values.
 * 
 * @method getConfigValue
 * @param {string} configFamily
 * @param {string} configName
 * @param {string} defaultValue
 */
function getConfigValue(configFamily, configName, defaultValue)
{
   var value = defaultValue,
       theConfig = config.scoped[configFamily][configName];
   if (theConfig !== null)
   {
      value = theConfig.value;
   }
   return value;
}

/**
 * Replication URL Mapping
 */
function getReplicationUrlMappingJSON()
{
   var mapping = {};
   try
   {
      var urlConfig, 
          repositoryId,
          configs = config.scoped["Replication"]["share-urls"].getChildren("share-url");

      if (configs)
      {
         for (var i = 0; i < configs.size(); i++)
         {
            // Get repositoryId and Share URL from each config entry
            urlConfig = configs.get(i);
            repositoryId = urlConfig.attributes["repositoryId"];
            if (repositoryId)
            {
               mapping[repositoryId] = urlConfig.value.toString();
            }
         }
      }
   }
   catch (e)
   {
   }
   return jsonUtils.toJSONString(mapping);
}

var syncMode = syncMode.getValue(),
    useTitle = getConfigValue("DocumentLibrary", "use-title", null);

var userIsSiteManager = false,
    siteData = getSiteData();
if (siteData != null)
{
   userIsSiteManager = siteData.userIsSiteManager;
}

/* *********************************************************************************
 *                                                                                 *
 * REPOSITORY URL                                                                  *
 *                                                                                 *
 ***********************************************************************************/
getRepositoryUrl: function getRepositoryUrl()
{
   // Repository Url
   var repositoryUrl = null,
      repositoryConfig = config.scoped["DocumentLibrary"]["repository-url"];

   if (repositoryConfig !== null)
   {
      repositoryUrl = repositoryConfig.value;
   }
   return repositoryUrl;
}

/* *********************************************************************************
 *                                                                                 *
 * PAGE CONSTRUCTION                                                               *
 *                                                                                 *
 ***********************************************************************************/

/**
 * Returns a JSON array of the configuration for all the services required by the document library
 */
function getDocumentLibraryServices() {
   var services = getHeaderServices();
   services = services.concat([
      "alfresco/dialogs/AlfDialogService",
      "alfresco/services/ActionService",
      "alfresco/services/ContentService",
      "alfresco/services/CrudService",
      "alfresco/services/DocumentService",
      "alfresco/services/LightboxService",
      "alfresco/services/QuickShareService",
      "alfresco/services/RatingsService",
      "alfresco/services/SearchService",
      "alfresco/services/TagService"
   ]);
   return services;
}

function getFilters() {
   var filters = {
      id: "DOCLIB_FILTERS",
      name: "alfresco/documentlibrary/AlfDocumentFilters",
      config: {
         label: "filter.label.documents",
         additionalCssClasses: "no-borders",
         widgets: [
            {
               name: "alfresco/documentlibrary/AlfDocumentFilter",
               config: {
                  label: "link.all",
                  filter: "all",
                  description: "link.all.description"
               }
            },
            {
               name: "alfresco/documentlibrary/AlfDocumentFilter",
               config: {
                  label: "link.editingMe",
                  filter: "editingMe",
                  description: "link.editingMe.description"
               }
            },
            {
               name: "alfresco/documentlibrary/AlfDocumentFilter",
               config: {
                  label: "link.editingOthers",
                  filter: "editingOthers",
                  description: "link.editingOthers.description"
               }
            },
            {
               name: "alfresco/documentlibrary/AlfDocumentFilter",
               config: {
                  label: "link.recentlyModified",
                  filter: "recentlyModified",
                  description: "link.recentlyModified.description"
               }
            },
            {
               name: "alfresco/documentlibrary/AlfDocumentFilter",
               config: {
                  label: "link.recentlyAdded",
                  filter: "recentlyAdded",
                  description: "link.recentlyAdded.description"
               }
            },
            {
               name: "alfresco/documentlibrary/AlfDocumentFilter",
               config: {
                  label: "link.favourites",
                  filter: "favourites",
                  description: "link.favourites.description"
               }
            }
         ]
      }
   };

   // Add the additional cloud synchronization related filters...
   if (syncMode != "OFF")
   {
      filters.config.widgets.push({
         name: "alfresco/documentlibrary/AlfDocumentFilter",
         config: {
            label: "link.synced",
            filter: "synced",
            description: "link.synced.description"
         }
      });
   }
   if (syncMode == "ON_PREMISE")
   {
      filters.config.widgets.push({
         name: "alfresco/documentlibrary/AlfDocumentFilter",
         config: {
            label: "link.syncedErrors",
            filter: "syncedErrors",
            description: "link.syncedErrors.description"
         }
      });
   }
   return filters;
}

function getPathTree(siteId, containerId, rootNode, rootLabel) {
   var tree = {
      name: "alfresco/layout/Twister",
      config: {
         label: "twister.library.label",
         additionalCssClasses: "no-borders",
         widgets: [
            {
               name: "alfresco/navigation/PathTree",
               config: {
                  siteId: siteId,
                  containerId: containerId,
                  rootNode: rootNode,
                  rootLabel: rootLabel
               }
            }
         ]
      }
   };
   return tree;
}

function getTags(siteId, containerId, rootNode) {
   var tags = {
      id: "DOCLIB_TAGS",
      name: "alfresco/documentlibrary/AlfTagFilters",
      config: {
         label: "filter.label.tags",
         additionalCssClasses: "no-borders",
         siteId: siteId,
         containerId: containerId,
         rootNode: rootNode
      }
   };
   return tags;
}

function getCategories() {
   var categories = {
      name: "alfresco/layout/Twister",
      config: {
         label: "twister.categories.label",
         additionalCssClasses: "no-borders",
         widgets: [
            {
               name: "alfresco/navigation/CategoryTree"
            }
         ]
      }
   };
   return categories;
}

function getCreateContentMenu() {
   var menu = {
      id: "DOCLIB_CREATE_CONTENT_MENU",
      name: "alfresco/documentlibrary/AlfCreateContentMenuBarPopup",
      config: {
         widgets: [
            {
               id: "DOCLIB_CREATE_CONTENT_MENU_GROUP1",
               name: "alfresco/menus/AlfMenuGroup",
               config: {
                  widgets: createContent
               }
            }
         ]
      }
   };
   return menu;
}

function getSelectedItemActions() {
   var actionsMenu = {
      id: "DOCLIB_SELECTED_ITEMS_MENU",
      name: "alfresco/documentlibrary/AlfSelectedItemsMenuBarPopup",
      config: {
         label: msg.get("selected-items.label"),
         widgets: [
            {
               id: "DOCLIB_SELECTED_ITEMS_MENU_GROUP1",
               name: "alfresco/menus/AlfMenuGroup",
               config: {
                  widgets: actionSet
               }
            }
         ]
      }
   };
   return actionsMenu;
}

function getSortOptions() {
   var sortOptions = [],
       sortingConfig = config.scoped["DocumentLibrary"]["sorting"];

   if (sortingConfig !== null)
   {
      var configs = sortingConfig.getChildren(),
         configItem,
         sortLabel,
         sortValue,
         valueTokens;

      if (configs)
      {
         for (var i = 0; i < configs.size(); i++)
         {
            configItem = configs.get(i);
            // Get label and value from each config item
            sortLabel = String(configItem.attributes["label"]);
            sortValue = String(configItem.value);
            if (sortLabel && sortValue)
            {
               valueTokens = sortValue.split("|");
               sortOptions.push(
               {
                  name: "alfresco/menus/AlfCheckableMenuItem",
                  config: {
                     label: msg.get(sortLabel),
                     value: valueTokens[0],
                     group: "DOCUMENT_LIBRARY_SORT_FIELD",
                     publishTopic: "ALF_DOCLIST_SORT_FIELD_SELECTION",
                     checked: (sortField == valueTokens[0]),
                     publishPayload: {
                        label: msg.get(sortLabel),
                        direction: valueTokens[1] || null
                     }
                  }
               });
            }
         }
      }
   };
   return sortOptions;
}

/* *********************************************************************************
 *                                                                                 *
 * DOCUMENT LIST CONFIG MENU CONSTRUCTION                                          *
 *                                                                                 *
 ***********************************************************************************/
function getDocumentListConfigMenu(showFolders, hideBreadcrumbTrail, showSidebar) {
   return {
      id: "DOCLIB_CONFIG_MENU",
      name: "alfresco/menus/AlfMenuBarPopup",
      config: {
         iconClass: "alf-configure-icon",
         widgets: [
            {
               id: "DOCLIB_CONFIG_MENU_VIEW_SELECT_GROUP",
               name: "alfresco/documentlibrary/AlfViewSelectionGroup"
            },
            // The actions to toggle full-screen and full-window have been left in place
            // for the time being, however the function is not working correctly due to
            // issues with absolutely positioned popups used for menus that are not shown.
            // Theses issues need to be resolved before this can be released.
            {
               id: "DOCLIB_CONFIG_MENU_VIEW_MODE_GROUP",
               name: "alfresco/menus/AlfMenuGroup",
               config: {
                  label: msg.get("doclib.viewModes.label"),
                  widgets: [
                     {
                        id: "DOCLIB_FULL_WINDOW_OPTION",
                        name: "alfresco/menus/AlfCheckableMenuItem",
                        config: {
                           label: msg.get("doclib.fullwindow.label"),
                           iconClass: "alf-fullscreen-icon",
                           checked: false,
                           publishTopic: "ALF_FULL_WINDOW"
                        }
                     },
                     {
                        id: "DOCLIB_FULL_SCREEN_OPTION",
                        name: "alfresco/menus/AlfCheckableMenuItem",
                        config: {
                           label: msg.get("doclib.fullscreen.label"),
                           iconClass: "alf-fullscreen-icon",
                           checked: false,
                           publishTopic: "ALF_FULL_SCREEN"
                        }
                     }
                  ]
               }
            },
            {
               id: "DOCLIB_CONFIG_MENU_OPTIONS_GROUP",
               name: "alfresco/menus/AlfMenuGroup",
               config: {
                  label: msg.get("doclib.options.label"),
                  widgets: [
                     {
                        id: "DOCLIB_SHOW_FOLDERS_OPTION",
                        name: "alfresco/menus/AlfCheckableMenuItem",
                        config: {
                           label: msg.get("show-folders.label"),
                           iconClass: "alf-showfolders-icon",
                           checked: showFolders,
                           publishTopic: "ALF_DOCLIST_SHOW_FOLDERS"
                        }
                     },
                     {
                        id: "DOCLIB_SHOW_PATH_OPTION",
                        name: "alfresco/menus/AlfCheckableMenuItem",
                        config: {
                           label: msg.get("show-path.label"),
                           checked: !hideBreadcrumbTrail,
                           iconClass: "alf-showpath-icon",
                           publishTopic: "ALF_DOCLIST_SHOW_PATH"
                        }
                     },
                     {
                        id: "DOCLIB_SHOW_SIDEBAR_OPTION",
                        name: "alfresco/menus/AlfCheckableMenuItem",
                        config: {
                           label: msg.get("show-sidebar.label"),
                           iconClass: "alf-showsidebar-icon",
                           checked: showSidebar,
                           publishTopic: "ALF_DOCLIST_SHOW_SIDEBAR"
                        }
                     }
                  ]
               }
            }
         ]
      }
   };
}

/* *********************************************************************************
 *                                                                                 *
 * DOCUMENT LIST CONSTRUCTION                                                      *
 *                                                                                 *
 ***********************************************************************************/
function getDocumentLibraryList(siteId, containerId, rootNode) {
   return {
      id: "DOCLIB_DOCUMENT_LIST",
      name: "alfresco/documentlibrary/AlfDocumentList",
      config: {
         useHash: true,
         hashVarsForUpdate: [
            "path",
            "filter",
            "tag",
            "category"
         ],
         siteId: siteId,
         containerId: containerId,
         rootNode: rootNode,
         usePagination: true,
         showFolders: showFolders,
         sortAscending: sortAscending,
         sortField: sortField,
         view: viewRendererName,
         widgets: [
            {
               name: "alfresco/documentlibrary/views/AlfSimpleView"
            },
            {
               name: "alfresco/documentlibrary/views/AlfDetailedView"
            },
            {
               name: "alfresco/documentlibrary/views/AlfGalleryView"
            },
            {
               name: "alfresco/documentlibrary/views/AlfTableView"
            },
            {
               name: "alfresco/documentlibrary/views/AlfFilmStripView"
            }
         ]
      }
   };
}

/**
 * Builds the JSON model for rendering a DocumentLibrary. 
 * 
 * @param {string} siteId The id of the site to render the document library for (if applicable)
 * @param {string} containerId The id of the container to render (if applicable - sites only)
 * @param {string} rootNode The node that is the root of the DocumentLibrary to render
 * @returns {object} An object containing the JSON model for a DocumentLibrary
 */
function getDocumentLibraryModel(siteId, containerId, rootNode, rootLabel) {
   
   var docLibModel = {
      id: "DOCLIB_SIDEBAR",
      name: "alfresco/layout/AlfSideBarContainer",
      config: {
         showSidebar: showSidebar,
         customResizeTopics: ["ALF_DOCLIST_READY","ALF_RESIZE_SIDEBAR"],
         footerHeight: 50,
         widgets: [
            {
               id: "DOCLIB_SIDEBAR_BAR",
               align: "sidebar",
               name: "alfresco/layout/VerticalWidgets",
               config: {
                  widgets: [
                     getFilters(),
                     getPathTree(siteId, containerId, rootNode, rootLabel),
                     getTags(siteId, containerId, rootNode),
                     getCategories()
                  ]
               }
            },
            {
               id: "DOCLIB_SIDEBAR_MAIN",
               name: "alfresco/layout/FullScreenWidgets",
               config: 
               {
                  widgets: 
                  [
                     {
                        id: "DOCLIB_TOOLBAR",
                        name: "alfresco/documentlibrary/AlfToolbar",
                        config: {
                           id: "DOCLIB_TOOLBAR",
                           widgets: [
                              {
                                 id: "DOCLIB_TOOLBAR_LEFT_MENU",
                                 name: "alfresco/menus/AlfMenuBar",
                                 align: "left",
                                 config: {
                                    widgets: [
                                       {
                                          id: "DOCLIB_SELECT_ITEMS_MENU",
                                          name: "alfresco/documentlibrary/AlfSelectDocumentListItems"
                                       },
                                       getCreateContentMenu(),
                                       {
                                          id: "DOCLIB_UPLOAD_BUTTON",
                                          name: "alfresco/documentlibrary/AlfCreateContentMenuBarItem",
                                          config: {
                                             label: msg.get("upload.label"),
                                             iconClass: "alf-upload-icon",
                                             publishTopic: "ALF_SHOW_UPLOADER"
                                          }
                                       },
                                       {
                                          id: "DOCLIB_SYNC_TO_CLOUD_BUTTON",
                                          name: "alfresco/documentlibrary/AlfCloudSyncFilteredMenuBarItem",
                                          config: {
                                             label: msg.get("actions.document.cloud-sync"),
                                             publishTopic: "ALF_SYNC_CURRENT_LOCATION"
                                          }
                                       },
                                       {
                                          id: "DOCLIB_UNSYNC_FROM_CLOUD_BUTTON",
                                          name: "alfresco/documentlibrary/AlfCloudSyncFilteredMenuBarItem",
                                          config: {
                                             label: msg.get("actions.document.cloud-unsync"),
                                             invertFilter: true,
                                             publishTopic: "ALF_UNSYNC_CURRENT_LOCATION"
                                          }
                                       },
                                       getSelectedItemActions()
                                    ]
                                 }
                              },
                              {
                                 id: "DOCLIB_PAGINATION_MENU",
                                 name: "alfresco/documentlibrary/AlfDocumentListPaginator",
                                 align: "left"
                              },
                              {
                                 id: "DOCLIB_TOOLBAR_RIGHT_MENU",
                                 name: "alfresco/menus/AlfMenuBar",
                                 align: "right",
                                 config: {
                                    widgets: [
                                       {
                                          id: "DOCLIB_SORT_ORDER_TOGGLE",
                                          name: "alfresco/menus/AlfMenuBarToggle",
                                          config: {
                                             checked: sortAscending,
                                             onConfig: {
                                                iconClass: "alf-sort-ascending-icon",
                                                publishTopic: "ALF_DOCLIST_SORT",
                                                publishPayload: {
                                                   direction: "ascending"
                                                }
                                             },
                                             offConfig: {
                                                iconClass: "alf-sort-descending-icon",
                                                publishTopic: "ALF_DOCLIST_SORT",
                                                publishPayload: {
                                                   direction: "descending"
                                                }
                                             }
                                          }
                                       },
                                       {
                                          id: "DOCLIB_SORT_FIELD_SELECT",
                                          name: "alfresco/menus/AlfMenuBarSelect",
                                          config: {
                                             selectionTopic: "ALF_DOCLIST_SORT_FIELD_SELECTION",
                                             widgets: [
                                                {
                                                   id: "DOCLIB_SORT_FIELD_SELECT_GROUP",
                                                   name: "alfresco/menus/AlfMenuGroup",
                                                   config: {
                                                      widgets: getSortOptions()
                                                   }
                                                }
                                             ]
                                          }
                                       },
                                       getDocumentListConfigMenu(showFolders, hideBreadcrumbTrail, showSidebar)
                                    ]
                                 }
                              }
                           ]
                        }
                     },
                     {
                        id: "DOCLIB_BREADCRUMB_TRAIL",
                        name: "alfresco/documentlibrary/AlfBreadcrumbTrail",
                        config: {
                           hide: hideBreadcrumbTrail,
                           rootLabel: rootLabel
                        }
                     },
                     getDocumentLibraryList(siteId, containerId, rootNode),
                     {
                        name: "alfresco/upload/AlfUpload"
                     }
                  ]
               }
            }
         ]
      }
   };
   return docLibModel;
}