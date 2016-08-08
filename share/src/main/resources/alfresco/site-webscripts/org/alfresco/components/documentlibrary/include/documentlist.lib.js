var this_DocumentList = this;

var DocumentList =
{
   PREFERENCES_ROOT: "org.alfresco.share.documentList",

   /* Sort the actions by preference order */
   sortByOrder: function sortByOrder(a, b)
   {
      return (a.order - b.order);
   },

   /* Get user preferences */
   getPreferences: function getPreferences()
   {
      var userprefs = {};

      var prefs = jsonUtils.toObject(preferences.value);
      // Populate the preferences object literal for easy look-up later
      userprefs = eval('try{(prefs.' + DocumentList.PREFERENCES_ROOT + ')}catch(e){}');
      if (typeof userprefs != "object")
      {
         userprefs = {};
      }

      return userprefs;
   },

   /* Get configuration value */
   getConfigValue: function getConfigValue(configFamily, configName, defaultValue)
   {
      var value = defaultValue,
         theConfig = config.scoped[configFamily][configName];

      if (theConfig !== null)
      {
         value = theConfig.value;
      }

      return value;
   },

   /* Replication URL Mapping */
   getReplicationUrlMappingJSON: function getReplicationUrlMappingJSON()
   {
      var mapping = {};

      try
      {
         var urlConfig, repositoryId,
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

      return mapping;
   },

   /* Sort Options */
   getSortOptions: function getSortOptions()
   {
      // New Content
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
                     value: valueTokens[0],
                     direction: valueTokens[1] || null,
                     label: sortLabel
                  });
               }
            }
         }
      }

      return sortOptions;
   },

   isUserSiteManager: function isUserSiteManager()
   {
      // Call the repository to see if the user is site manager or not
      var userIsSiteManager = false,
         obj = null,
         site = args.site;

      if (!site)
      {
         site = this_DocumentList.hasOwnProperty('page') ? page.url.templateArgs.site : null;
      }
      if (site)
      {
         json = remote.call("/api/sites/" + site + "/memberships/" + encodeURIComponent(user.name));
         if (json.status == 200)
         {
            obj = JSON.parse(json);
         }
         if (obj)
         {
            userIsSiteManager = obj.role == "SiteManager";
         }
      }
      else
      {
         userIsSiteManager = false;
      }
      return userIsSiteManager;
   },
   
   /**
    * This function inspects the view renderer configuration (typically found in the "share-documentlibrary-config.xml" file)
    * and retrieves the view renderer details. It constructs and returns an object containing the correctly ordered views
    * (as defined by their configured index attribute) and the JavaScript and CSS resources that they depend on in order to
    * be processed. This information is then used by the documentlist_v2.lib.ftl file to request the dependencies and add
    * the views to the "Options" menu. Only views with an "id" attribute are added.
    * 
    * @returns
    */
   getViewData: function getViewJsDeps()
   {
      var data = {};
      data.views = [];
      data.deps = {};
      data.deps.js = [];
      data.deps.css = [];
      
      // Iterate over the view-renderer configuration...
      var docListViewConfig = config.scoped["DocumentLibraryViews"]["view-renderers"];
      var commonComponentStyleConfig = config.scoped["CommonComponentStyle"]["component-style"];
      var suppressComponentConfig = config.scoped["SuppressComponent"]["component-config"];
      data.commonComponentStyle = commonComponentStyleConfig != null ? commonComponentStyleConfig.value : null;
      data.suppressComponent = suppressComponentConfig != null ? suppressComponentConfig.value : null;
      var viewRendererList = docListViewConfig.getChildren("view-renderer");
      for (var i=0; i<viewRendererList.size(); i++)
      {
         // Get the attributes of the current view...
         var viewRenderer = viewRendererList.get(i);
         var id = viewRenderer.getAttribute("id"),
             iconClass = viewRenderer.getAttribute("iconClass"),
             label = viewRenderer.getAttribute("label"),
             indexStr = viewRenderer.getAttribute("index"),
             widget = viewRenderer.getAttribute("widget");
         
         if (id != null)
         {
            // Only process the view if an "id" attribute has been set...
            var view = {
               id: id,
               iconClass: (iconClass != null) ? iconClass : id,
               label: msg.get(label),
               index: parseInt(indexStr),
               widget: widget
            };
            data.views.push(view);
            
            // Obtain the JS and CSS dependencies for the view...
            var dependenciesConfig = viewRenderer.getChild("dependencies");
            if (dependenciesConfig)
            {
               var jsDependencies = dependenciesConfig.getChildren("js");
               for (var j=0; j<jsDependencies.size(); j++)
               {
                  var src = jsDependencies.get(j).getAttribute("src");
                  if (src != null && src.trim() != "")
                  {
                     data.deps.js.push(src.trim());
                  }
               }
               var cssDependencies = dependenciesConfig.getChildren("css");
               for (var k=0; k<cssDependencies.size(); k++)
               {
                  var src = cssDependencies.get(k).getAttribute("src");
                  if (src != null && src.trim() != "")
                  {
                     data.deps.css.push(src.trim());
                  }
               }
            }
            
            // See if there is a JSON config to pass to the view...
            var jsonConfig = viewRenderer.getChildValue("json-config");
            if (jsonConfig != null) 
            {
               view.jsonConfig = jsonConfig;
            }
         }
      }
      
      // Sort the views based on their index (if provided). If no index is provided for both views
      // compared then their current order is retained. If one view has no index then it is placed
      // after the view with an index and if both views have an index then the lower index is placed
      // first.
      data.views.sort(function(a, b) {
         if (!isNaN(a.index) && isNaN(b.index))
         {
            return -1;
         }
         else if (isNaN(a.index) && !isNaN(b.index))
         {
            return 1;
         }
         else if (isNaN(a.index) && isNaN(b.index))
         {
            return 0;
         }
         else
         {
            return a.index - b.index;
         }
      });
      
      return data;
   }
   
};

/**
 * Main entrypoint for common doclib functionality
 *
 * @method doclibCommon
 */
function doclibCommon()
{
   var preferences = DocumentList.getPreferences();
   model.preferences = preferences;
   if (model.preferences.simpleView != null && model.preferences.simpleView === true || model.preferences.simpleView === false)
   {
      model.preferences.viewRendererName = (model.preferences.simpleView ? "simple" : "detailed");
   }
   model.viewRendererNames = ["simple", "detailed", "gallery", "filmstrip"];
   model.repositoryUrl = DocumentList.getConfigValue("DocumentLibrary", "repository-url", null);
   model.replicationUrlMapping = DocumentList.getReplicationUrlMappingJSON();
   model.rootNode = DocumentList.getConfigValue("RepositoryLibrary", "root-node", "alfresco://company/home");
   model.sortOptions = DocumentList.getSortOptions();
   model.useTitle = DocumentList.getConfigValue("DocumentLibrary", "use-title", null);
   model.userIsSiteManager = DocumentList.isUserSiteManager();
   model.metadataTemplates = {};
   model.syncMode = syncMode.getValue();
   
   // Get the dependencies defined for each view configured...
   var viewData = DocumentList.getViewData();
   model.viewRendererNames = [];
   for (var i=0; i<viewData.views.length; i++)
   {
      model.viewRendererNames.push(viewData.views[i].id);
   }
   
   model.viewRenderers = viewData.views;
   model.viewJsDeps = viewData.deps.js;
   model.viewCssDeps = viewData.deps.css;
   model.commonComponentStyle = viewData.commonComponentStyle;
   model.suppressComponent = viewData.suppressComponent;
   model.filmstripImageLazyLoading = DocumentList.getConfigValue("DocumentLibrary", "filmstripImageLazyLoading");
}
