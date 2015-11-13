const PREFERENCES_ROOT = "org.alfresco.share.documentList";

function sortByIndex(obj1, obj2)
{
   return (obj1.index > obj2.index) ? 1 : (obj1.index < obj2.index) ? -1 : 0;
}

function getPreferences()
{
   var doclistPrefs = {};
   
   // Populate the preferences object literal for easy look-up later
   var prefs = jsonUtils.toObject(preferences.value);
   doclistPrefs = eval('try{(prefs.' + PREFERENCES_ROOT + ')}catch(e){}');
   if (typeof doclistPrefs != "object")
   {
      doclistPrefs = {};
   }
   
   toolbar.preferences = doclistPrefs;
}

function getActionSet(myConfig)
{
   // Actions
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
         // Multi-Select Actions
         action = {
            id: attr["id"] ? attr["id"].toString() : "",
            type: attr["type"] ? attr["type"].toString() : "",
            permission: attr["permission"] ? attr["permission"].toString() : "",
            asset: attr["asset"] ? attr["asset"].toString() : "",
            href: attr["href"] ? attr["href"].toString() : "",
            label: attr["label"] ? attr["label"].toString() : "",
            hasAspect: attr["hasAspect"] ? attr["hasAspect"].toString() : "",
            notAspect: attr["notAspect"] ? attr["notAspect"].toString() : ""
         };

         actionSet.push(action)
      }
   }

   model.actionSet = actionSet;
}

function getCreateContent()
{
   var createContent = [];

   // Create content config items
   var createContentConfig = config.scoped["DocumentLibrary"]["create-content"];
   if (createContentConfig !== null)
   {
      var contentConfigs = createContentConfig.getChildren("content");
      if (contentConfigs)
      {
         var attr, content, contentConfig, paramConfigs, paramConfig, permissionsConfigs, permissionConfigs, permissionConfig;
         for (var i = 0; i < contentConfigs.size(); i++)
         {
            contentConfig = contentConfigs.get(i);
            attr = contentConfig.attributes;

            // Create content menu items
            content = {
               id: attr["id"] ? attr["id"].toString() : null,
               type: attr["type"] ? attr["type"].toString() : null,
               icon: attr["icon"] ? attr["icon"].toString() : attr["id"] ? attr["id"].toString() : "generic",
               label: attr["label"] ? attr["label"].toString() : attr["id"] ? "create-content." + attr["id"].toString() : null,
               index: parseInt(attr["index"] || "0"),
               permission: "",
               params: {}
            };

            // Read params
            paramConfigs = contentConfig.getChildren("param");
            for (var pi = 0; pi < paramConfigs.size(); pi++)
            {
               paramConfig = paramConfigs.get(pi);
               if (paramConfig.attributes["name"])
               {
                  content.params[paramConfig.attributes["name"]] = (paramConfig.value || "").toString();
               }
            }

            // Read permissions
            permissionsConfigs = contentConfig.getChildren("permissions");
            if (permissionsConfigs.size() > 0)
            {
               var allow, deny, value, match;
               permissionConfigs = permissionsConfigs.get(0).getChildren("permission");
               for (var pi = 0; pi < permissionConfigs.size(); pi++)
               {
                  permissionConfig = permissionConfigs.get(pi);
                  allow = permissionConfig.attributes["allow"];
                  deny = permissionConfig.attributes["deny"];
                  value = (permissionConfig.value || "").toString();
                  if (value.length() > 0)
                  {
                     match = true;
                     if (allow != null)
                     {
                        match = (allow == "true");
                     }
                     else if (deny != null)
                     {
                        match = (deny == "false");
                     }
                     content.permission += (content.permission.length == 0 ? "" : ",") + (value + ":" + match);
                  }
               }
            }

            if (!content.type)
            {
               /**
                * Support simple/old configs like below by making them of type "pagelink" pointing to the create-content page.
                * <content id="xml" mimetype="text/xml" label="create-content.xml" itemid="cm:content" permission="Write" formid=""/>
                */
               var permission = attr["permission"] ? attr["permission"].toString() : null,
                  mimetype = attr["mimetype"] ? attr["mimetype"].toString() : null,
                  itemid = attr["itemid"] ? attr["itemid"].toString() : null,
                  formid = attr["formid"] ? attr["formid"].toString() : null,
                  url = "create-content?destination={nodeRef}";
               if (permission)
               {
                  content.permission += (content.permission.length == 0 ? "" : ",") + permission;
               }
               if (itemid)
               {
                  url += "&itemId=" + itemid;
               }
               if (formid)
               {
                  url += "&formId=" + formid;
               }
               if (mimetype)
               {
                  url += "&mimeType=" + mimetype;
               }

               content.type = "pagelink";
               content.params.page = url;
            }

            createContent.push(content);
         }
      }
   }

   // Create content by template
   var createContentByTemplateConfig = config.scoped["DocumentLibrary"]["create-content-by-template"];
   createContentByTemplateEnabled = createContentByTemplateConfig !== null ? createContentByTemplateConfig.value.toString() == "true" : false;

   model.createContent = createContent.sort(sortByIndex);
   model.createContentByTemplateEnabled = createContentByTemplateEnabled;
}

/* Repository Browser root */
function getRepositoryBrowserRoot()
{
   // Repository Library root node
   var rootNode = "alfresco://company/home",
      repoConfig = config.scoped["RepositoryLibrary"]["root-node"];

   if (repoConfig !== null)
   {
      rootNode = repoConfig.value;
   }

   toolbar.rootNode = rootNode;
}

var toolbar = {};

/**
 * Main entrypoint for component webscript logic
 *
 * @method main
 */
function main()
{
   var myConfig = new XML(config.script);
   
   getPreferences();
   getActionSet(myConfig);
   getCreateContent(myConfig);
   getRepositoryBrowserRoot();

   toolbar.syncMode = syncMode.value;
}

main();