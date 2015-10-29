/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Core WEF Object
 * @class WEF 
 * @module WEF
 */
WEF = function WEF()
{
   var config = {}, loader = (function()
   {
      var loadingModules = [], 
          unloadedModules = [], 
          loadedModules = [],
          ignoreModules = [], 
          registry = {}, 
          repositories = 
          {
             lib: '/lib',
             plugin: '/plugin',
             core: ''
          },
          bootloaderConfig = {}, 
          YUILoader = null;

      /**
       * Initialises WEF
       *
       * @param config {Object} Config to initialize with
       *
       */
      var init = function WEF_initialise(config, callback)
      {
         bootloaderConfig = config;
      };

      /**
       * boot
       *
       * @param config {Object} Config to initialize with
       *
       */
      var boot = function WEF_initialise(callback)
      {
         var loadYUILoader = function loadYUILoader(yuiloaderPath, callback)
         {
            loadYUILoader._counter = (loadYUILoader._counter === undefined) ? 0 : ++loadYUILoader._counter;
            var node = document.createElement('script');
            node.id = 'wef-yuiloader' + loadYUILoader._counter;
            node.src = bootloaderConfig.urlContext + yuiloaderPath;
            if (!+"\v1") // true only in IE
            {
               var id = node.id;
               node.onreadystatechange = function()
               {
                  var rs = this.readyState;
                  if ("loaded" === rs || "complete" === rs) 
                  {
                     node.onreadystatechange = null;
                     callback();
                  }
               };
            }
            else
            {
               node.onload = function()
               {
                  callback();
               };
            }

            document.getElementsByTagName('head')[0].appendChild(node);
         };

         try 
         {
            var y = YAHOO;
         } 
         catch (e) 
         {
            //if no YAHOO on page assume YUILoader isn't too so let's load it up.
            return loadYUILoader(bootloaderConfig.yuiloaderPath, callback);
         }

         return callback();
      };

      /**
       * @private
       * Internal method for converting a simple configuration to a fully fledged YUI module config
       *
       * @param config {Object} A simple config which looks like this:
       *
       * {
       *    name: <module_id> (required),
       *    type: <type_id> (optional - "js" or "css", defaults to "js"),
       *    repo: <repo_id> (optional - "plugin", "lib", "root" or a custom repo, defaults to "plugin"),
       *    path: <path> (optional - if supplied, overrides repo)
       *    requires: <array of ids> (optional - additional requirements)
       *    varName: <variable name> (optional - if not supplied, will be auto-generated)
       * }
       *
       * @return {Object} YUI Loader compatible module
       */
      var convertConfigToYUIModuleConfig = function WEF_convertConfigToYUIModuleConfig(config)
      {
         var yuiConfig = 
         {
            name: config.name,
            type: (config.type && config.type === 'css') ? 'css' : 'js',
            requires: config.requires || null,
            userAgent: config.userAgent || null
         };

         if (config.varName)
         {
            yuiConfig.varName = config.varName;
         }
         else 
         {
            var n = config.name, x = config.name.indexOf('.');
            if (x > -1) 
            {
               n = config.name.substring(0, x);
            }
            
            // use internal function counter
            convertConfigToYUIModuleConfig._counter = (convertConfigToYUIModuleConfig._counter === undefined) ? 0 : ++convertConfigToYUIModuleConfig._counter;
            yuiConfig.varName = "WEF_Loader_Variable_" + n + convertConfigToYUIModuleConfig._counter;
         }

         var repoRootPath = (config.repo) ? repositories[config.repo] : repositories.core;

         if (config.fullpath)
         {
            config.path = config.fullpath;
         }

         if (config.path) 
         {
            // absolute path
            if (config.path.match(/^http:/) || config.path.match(/^https:/)) 
            {
               yuiConfig.fullpath = config.path;
            }
            // relative path so make it absolute
            else 
            {
               if (!config.path.match(/^\//)) 
               {
                  config.path = "/" + config.path;
               }

               yuiConfig.fullpath = bootloaderConfig.serverPort + repoRootPath + config.path;
            }
         }
         else 
         {
            var modulePath = config.name.replace(/\./g, "/");
            if (!modulePath.match(/^\//)) 
            {
               modulePath = '/' + modulePath;
            }

            // append filename - this is assumed using last part of path.
            yuiConfig.fullpath = bootloaderConfig.serverPort + repoRootPath + modulePath + '/' + modulePath.substring(modulePath.lastIndexOf('/') + 1) + '.' + yuiConfig.type;
         }

         //load version of resource as specified by loader config filter eg minified.
         var m;
         bootloaderConfig.filter = (bootloaderConfig.filter===null) ? 'min' : bootloaderConfig.filter;
         if (bootloaderConfig.filter!==null && (m = yuiConfig.fullpath.match(/.js$/)))
         {
            //only apply if filter not already specified in path eg -min
            var testFilter = new RegExp('(-'+bootloaderConfig.filter+'-)|(_'+bootloaderConfig.filter+'_)|('+bootloaderConfig.filter+'.)')
            if (!testFilter.test(yuiConfig.fullpath))
            {
               yuiConfig.fullpath = yuiConfig.fullpath.replace(m[0], '-' + bootloaderConfig.filter + m[0]);                  
            }
         }

         return yuiConfig;
      };

      /**
       * Determines if specified module is valid for the current useragent.
       * 
       * @param {Object} module
       * @private
       * @return {Boolean} True if valid, false if not.
       */
      var _filterByUA = function WEF__filterByUA(module)
      {
         if (!module)
         {
            return false;
         }

         var moduleUA = module.userAgent, useragent = YAHOO.env.ua;
         if (!YAHOO.lang.isNull(moduleUA)) 
         {
            if (!useragent[moduleUA.toLowerCase()]) 
            {
               return false;
            }
         }
         return true;
      };

      /**
       * Adds a module to be loaded. If module declares dependencies then also sets those modules to be loaded too
       *
       * @param config {Object} Module to setup for loading
       * @param isYUILoaderCompatible {Boolean} if config object is YUI Loader compatible then no conversion is performed.
       *
       */
      var require = function WEF_require(config, isYUILoaderCompatible)
      {
         // convert to object if string (id only)
         if (typeof(config) == 'string') 
         {
            config = registry[config];
         }
         if (YAHOO.lang.isObject(config) && config.requires) 
         {
            for (var i = 0, len = config.requires.length; i < len; i++) 
            {
               var modName = config.requires[i];
               if (_filterByUA(registry[modName]))
               {
                  unloadedModules.push(registry[modName]);
               }
               else if (modName.indexOf('.')>-1)
               {
                  ignoreModules.push(modName);
               }
               // non yui files
               if (modName.indexOf('.') !== -1 && registry[modName]) 
               {
                  // add any dependencies
                  if (registry[modName].requires) 
                  {
                     for (var j = 0, jlen = registry[modName].requires.length; j < jlen; j++) 
                     {
                        var depModName = registry[modName].requires[j];
                        this.require(registry[depModName], true);
                     }
                  }
               }
            }
         }

         var moduleConfig = (isYUILoaderCompatible) ? config : convertConfigToYUIModuleConfig(config);
         if (moduleConfig)
         {
            unloadedModules.push(moduleConfig);
         }

         return this;
      };

      /**
       * Starts the WEF loader
       *
       * @param successCallback {Object} Object literal describing callback and scope for onSuccess of loading process
       * @param failureCallback {Object} Object literal describing callback and scope for onFailure of loading process
       */
      var load = function WEF_load(successCallback, failureCallback)
      {
         var loaderConfig = {}, 
            useragent = YAHOO.env.ua,
            yuiVersion;

         if (!YAHOO.util.YUILoader) 
         {
            throw new Error('YUI Loader unavailable; Unable to load assets.');
         }

         if (!YUILoader) 
         {
            YUILoader = new YAHOO.util.YUILoader();
            yuiVersion = parseFloat(YAHOO.VERSION.split('.')[0] += '.' + YAHOO.VERSION.split('.').slice(1).join(''))
            //apply any overrides for moduleInfo
            for (var modName in bootloaderConfig.moduleInfoOverrides)
            {
               
               if (YUILoader.moduleInfo[modName])
               {
                  YUILoader.moduleInfo[modName] = bootloaderConfig.moduleInfoOverrides[modName](YUILoader.moduleInfo[modName], yuiVersion ) 
               }
            }
         }

         for (var i = 0, len = unloadedModules.length; i < len; i++) 
         {
            var module  = unloadedModules[i];
            YUILoader.addModule(module);
            loadingModules.push(module);
         }

         unloadedModules = [];

         var requires = [];
         for (var i = 0, len = loadingModules.length; i < len; i++) 
         {
            var module = loadingModules[i];
            if (YAHOO.lang.isObject(module) && requires.join(',').indexOf(module.name)===-1) 
            {               
               //if a preload handler exists for module, run it and reassign
               if (bootloaderConfig.modulePreLoadHandlers && bootloaderConfig.modulePreLoadHandlers[module.name])
               {
                  module = bootloaderConfig.modulePreLoadHandlers[module.name](module, bootloaderConfig);
               }
               requires.push(module.name);               
            }
         }

         loaderConfig.onFailure = function(o)
         {
            var yuiloader_fail = function WEF_YUILoader_failure(obj)
            {
               if (failureCallback) 
               {
                  failureCallback.fn.call(failureCallback.scope || window, obj.msg, obj);
               }
            };

            return function(msg, obj)
            {
               yuiloader_fail.call(o, msg, obj);
            };
         }(this);

         loaderConfig.onSuccess = function(o)
         {
            var yuiloader_success = function WEF_YUILoader_success(msg, obj)
            {
               for (var i = 0, len = loadingModules.length; i < len; i++)
               {
                  loadedModules.push(loadingModules[i]);
               }

               loadingModules = [];
               if (unloadedModules.length > 0) 
               {
                  load(successCallback, failureCallback);
               }
               else 
               {
                  if (successCallback) 
                  {
                     successCallback.fn.call(successCallback.scope || window, msg, obj);
                  }
               }
               if (bootloaderConfig.debugMode===true && (window['console'] && window['console'].log) )
               {
                  var stats = o.report();
                  console.log('WEF Loader stats (loaded/unloaded): ' + stats.loaded.length + '/' + stats.unloaded.length);
               }  
            };

            return function(msg, obj)
            {
               yuiloader_success.call(o, msg, obj);
            };
         }(this);

         if (bootloaderConfig.debugMode===true)
         {
            loaderConfig.onProgress = function onProgress(obj)
            {
            if (bootloaderConfig.debugMode===true && (window['console'] && window['console'].log) )
               {
                  if (!obj.hasOwnProperty('xhrResponse'))
                  {
                     console.log('Loaded ' + obj.name);
                  }
                  //if a load handler exists for module, run it and reassign
                  if (bootloaderConfig.moduleLoadHandlers && bootloaderConfig.moduleLoadHandlers[obj.name])
                  {
                     obj = bootloaderConfig.moduleLoadHandlers[obj.name](obj);
                  }
               }
            };
         }

         if (bootloaderConfig.useSandboxLoader)
         {
            loaderConfig.require = requires;
            loaderConfig.base = bootloaderConfig.yuibase;
            loaderConfig.filter = (bootloaderConfig.filter!==null) ? (bootloaderConfig.filter==="") ? 'debug' : bootloaderConfig.filter : 'min';
            loaderConfig.loadOptional = bootloaderConfig.loadOptional || true;
            loaderConfig.skin = bootloaderConfig.skin || null;
            loaderConfig.ignore = ignoreModules;
            YUILoader.sandbox(loaderConfig);
         }
         else
         {
            YUILoader.base = bootloaderConfig.yuibase;
            YUILoader.require(requires);
            YUILoader.onSuccess = loaderConfig.onSuccess;
            YUILoader.onFailure = loaderConfig.onFailure;
            if (bootloaderConfig.debugMode)
            {
               YUILoader.onProgress = loaderConfig.onProgress;
            }
            YUILoader.filter = (bootloaderConfig.filter!==null) ? (bootloaderConfig.filter==="") ? 'debug' : bootloaderConfig.filter : 'min';
            YUILoader.loadOptional = bootloaderConfig.loadOptional || true;
            YUILoader.skin = bootloaderConfig.skin || null;
            YUILoader.ignore = ignoreModules;
            YUILoader.insert();
         }
      };

      /**
       * Adds a module so YUILoader knows where to find it. If required, coverts descriptor to YUILoader compatible format
       *
       * @param o {Object|Array} An object or array of object descriptor describing modules to add
       * @param isYUILoaderCompatible {Boolean} Flag denoting whether descriptor is using YUILoader format. Defaults to false.
       *
       */
      var addResource = function addResource(o, isYUILoaderCompatible)
      {
         var isYUILoaderCompatible = isYUILoaderCompatible || false;
         // is an array
         if (Object.prototype.toString.apply(o) === ['[object Array]']) 
         {
            for (var i = 0, len = o.length; i < len; i++) 
            {
               addResource(o[i]);
            }
         }
         else 
         {
            if (!isYUILoaderCompatible) 
            {
               o = convertConfigToYUIModuleConfig(o);
            }
            registry[o.name] = o;
         }
      };

      var report = function WEF_Loader_report()
      {
         var loaderReport = 
         {
            loaded: loadedModules,
            unloaded: unloadedModules
         };

         return loaderReport;
      };

      return {
         init: init,
         require: require,
         load: load,
         addResource: addResource,
         boot: boot,
         report: report
      };
   })();

   return {
      /**
       * Initialises the WEF instance.
       * 
       * @param {Object} wefConfig
       */
      init: function WEF_Init(wefConfig)
      {
         config = wefConfig;
         config.loaderConfig.debugMode = wefConfig.debugMode || false;
         loader.init(config.loaderConfig);
      },

      /**
       * Container for instances of used components i.e Ribbon
       * 
       * @property module
       * @type Object
       */
      module: {},

      /**
       * Run the specified app, initialising the boot process first. 
       * Loads any required modules, sets up Ribbon and starts the webeditor
       * 
       * @param {Function} callback
       */
      run: function WEF_Run(appName)
      {
         loader.boot(function (o)
         {
            return function WEF_Boot_Callback() 
            {
               o.require(appName);
               o.load(
               {
                  fn:function WEF_load_success(obj)
                  {
                     var y = (obj.reference) ? obj.reference : YAHOO
                     app = y.env.getVersion(appName).mainClass;

                     if (app)
                     {
                        y.org.springframework.extensions.webeditor.module.Ribbon = new y.org.springframework.extensions.webeditor.ui.Ribbon(
                        {
                           id : 'wef-ribbon',
                           name : 'WEF-Ribbon',
                           element: 'wef-ribbon'
                        });

                        y.org.springframework.extensions.webeditor.module.Ribbon.init(y.org.springframework.extensions.webeditor.ConfigRegistry.getConfig("org.springframework.extensions.webeditor.ribbon"));

                        // show ribbon after it is rendered.
                        y.Bubbling.on('WEF-Ribbon--afterRender', function(e, args)
                        {
                           y.org.springframework.extensions.webeditor.module.Ribbon.show();
                        });

                        // run app
                        app();
                        y.Bubbling.fire('WEF'+y.org.springframework.extensions.webeditor.SEPARATOR+'afterInit');
                     }

                     o.render(y);
                  }
               },
               {
                  fn:function WEF_load_failure(msg, obj)
                  {
                     if (o.get('debugMode') === true && (window['console'] && window['console'].log) )
                     {
                        console.log(msg);
                        console.log(obj);
                     }
                  }
               });
            };
         }(this));
      },
      
      /**
       * Renders root elements for WEF elements and adds class names to &lt;body&gt;
       * 
       * @method render
       * 
       * @param y {Object} Reference to YAHOO object (YUI)
       */
      render: function WEF_render(y)
      {
         var Dom = y.util.Dom, 
             Bubbling = y.Bubbling;

         // init core WEF divs
         var body = document.getElementsByTagName('body')[0];
         if (!Dom.get('wef'))
         {
            var el = document.createElement('div');
                el.id = "wef";
                el.className = 'wef';
            
            body.insertBefore(el, body.firstChild);
         }

         if (!Dom.hasClass(body, 'wef-root-body'))
         {
            Dom.addClass(body, 'wef-root-body');
         }

         if (body.className.indexOf('yui-skin') == -1)
         {
            Dom.addClass(body, 'yui-skin-sam');
         }

         // add ua classnames to body
         if (y.env.ua.ie)
         {
            Dom.addClass('wef', 'wef-ie');
            Dom.addClass('wef', 'wef-ie-' + y.env.ua.ie);
         }

         if (document.compatMode==='BackCompat')
         {
            Dom.addClass(body, 'wef-quirks');
         }

         Bubbling.fire('WEF'+y.org.springframework.extensions.webeditor.SEPARATOR+'afterRender');
      },

      /**
       * Retrieve a config property 
       * 
       * @param {String} name
       * 
       * @return {Object|String|Number} value of config property
       */
      get: function WEF_get(name)
      {
         var value = config[name];
         
         if (typeof(value) == "undefined")
         {
            value = null;
         }
         
         return value;
      },

      /**
       * Set a config property
       * 
       * @param {String} name
       * @param {Object} value
       */
      set: function WEF_set(name, value)
      {
         config[name] = value;
      },

      /**
       * Registers a resource to WEF for the loader to use.
       * @param config {Object} Configuration object that describes the resource
       */
      addResource: function WEF_addResource(config)
      {
         if (config.type == "plugin")
         {
            this.PluginRegistry.registerPlugin(config.name);
         }

         loader.addResource(config);
      },
      
      /**
       * Specify a dependency that is required and should be loaded
       * @param o {Object} config of resource that is required
       */
      require: function WEF_require(o)
      {
         loader.require(o);
      },

      /**
       * Start the load process.
       * 
       * @param successCallback {Object} Callback object that is used when loading has succeeded
       * @param failureCallback {Object} Callback object that is used when loading has failed
       *  
       */
      load: function WEF_load(successCallback, failureCallback)
      {
         loader.load(successCallback, failureCallback);
      },
      
      /**
       * Registers a resource as loaded and available for use.
       * 
       * @param name {String} Name of resource
       * @param o {Object} Reference to object
       * @param metadata {Object} Metadata object describing version of resource
       */
      register: function WEF_register(name, o, metadata, y)
      {
         //store YAHOO reference in plugin registry
         if (y && !this.PluginRegistry.getInstance('YAHOO'))
         {
            y = this.PluginRegistry.registerInstance('YAHOO', y);
         }

         y.register(name, o, metadata);
      },
      
      /**
       * Returns an object describing the modules that were loaded and failed to load
       * 
       * @return {Object} Object with loaded and unloaded properties listing modules that did load and failed to load respectively
       */
      report: function WEF_report()
      {
         var report = loader.report(),
             loaded = report.loaded,
             index = '',
             loadedModules = [];

         // strip out any duplicates    
         for (var i = 0; i < loaded.length; i++)
         {
            if (loaded[i])
            {
               var name = loaded[i].name;
               if (index.indexOf(name + ',') == -1)
               {
                  index+=name+',';
                  loadedModules.push(loaded[i]);
               }
            }
         }

         report.loaded = loadedModules;
         return report;
      }
   };
}();

/**
 * Registry object to store configuration for components.
 * 
 * @class WEF.ConfigRegistry
 * @static
 * 
 */
WEF.ConfigRegistry = (function WEF_Config_Registry()
{
   /**
    * Container of registered configs
    * @property config
    * @type Object
    */
   var configs = {},
       name = 'WEF_Config_Registry';

   /**
    * Registers config object against specified name
    * 
    * @param configName {String} name of object that the config is for
    * @param config {Object} Object literal describing configuration
    * 
    */
   var registerConfig = function WEF_Config_Registry_registerConfig(configName, config)
   {
      configs[configName] = config;
   };

   /**
    * Retrieves config for specified object
    * 
    * @param configName {String} Name of object to specify config for
    * @return config {Object}
    * 
    */
   var getConfig = function WEF_Config_Registry_getConfig(configName)
   {
      return configs[configName] || null;
   };

   return {
      registerConfig : registerConfig,
      getConfig : getConfig
   }; 
})();

/**
 * Registry object to store plugins.
 * 
 * @class WEF.PluginRegistry
 * @static
 * 
 */
WEF.PluginRegistry = (function WEF_Plugin_Registry()
{
   /**
    * Container of registered plugins
    * @property config
    * @type Object
    */
   var plugins = {}, 
       name = 'WEF_Plugin_Registry'
       pluginInstances = {};

   /**
    * Registers plugin object against specified name
    * 
    * @param pluginName {String} name of object that the plugin is for
    * @param plugin {Object} Object literal describing configuration
    * 
    */
   var registerPlugin = function WEF_Plugin_Registry_registerPlugin(pluginName)
   {
      plugins[pluginName] = pluginName;
   };

   /**
    * Retrieves plugin for specified plugin name
    * 
    * @param configName {String} Name of plugin
    * 
    * @return config {Object}
    * 
    */
   var getPlugin = function WEF_Plugin_Registry_getPlugin(pluginName)
   {
      var plugin = null,
         y = this.getInstance('YAHOO');

      if (plugins[pluginName] != null)
      {
         var versionObj = y.env.getVersion(pluginName); 
         if (versionObj != null)
         {
            plugin = versionObj.mainClass;
         }
      }

      return plugin;
   };
   
   /**
    * Retreives instance of specified plugin
    * 
    * @param pluginId {String} Id of plugin
    * 
    * @return {WEF.Plugin} Instance of plugin or null if plugin with specified id is not registered
    */
   var getInstance = function WEF_Plugin_Registry_getInstance(pluginId)
   {
      return pluginInstances[pluginId] || null;
   };

   /**
    * Register a plugin instance
    * 
    * @param pluginId {String} Id of plugin
    * @param instance {Object} plugin instance
    */
   var registerInstance = function WEF_Plugin_Registry_registerInstance(pluginId, instance)
   {
      pluginInstances[pluginId] = instance;
      return pluginInstances[pluginId];
   }
 
   return {
      registerPlugin : registerPlugin,
      getPlugin: getPlugin,
      registerInstance : registerInstance,
      getInstance: getInstance
   }; 
})();