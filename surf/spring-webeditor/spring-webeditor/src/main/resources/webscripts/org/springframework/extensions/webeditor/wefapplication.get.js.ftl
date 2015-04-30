(function()
{
   YAHOO.namespace("${appName}");

   YAHOO.${appName} = function ${appName?replace(".", "_")}()
   {
      // initialise each plugin after WEF has rendered
      YAHOO.Bubbling.on(
         'WEF-Ribbon'+WEF.SEPARATOR+'afterRender',
         function(e, args)
         {
            var plugins = [],
               plugin,
               pluginInstance,
               config,
               debug = (WEF.get('debugMode')===true && (window['console'] && window['console'].log) );

            <#list plugins as plugin>
            if (debug)
            {
               console.log('Retrieving plugin: ${plugin.name?html}');
            }
            plugin = WEF.PluginRegistry.getPlugin("${plugin.name?html}");
            
            // retrieve or create plugin config
            config = YAHOO.org.springframework.extensions.webeditor.ConfigRegistry.getConfig("${plugin.name?html}");
            if (config == null)
            {
               config = 
               {
                  id: "${plugin.name?html}",
                  name: "${plugin.name?html}"
               }
            }
            //create instance of plugin
            
            pluginInstance = new plugin(config);
            //store instance
            plugins.push(WEF.PluginRegistry.registerInstance(config.id, pluginInstance));
            if (debug)
            {
               console.log('Registered plugin instance (${plugin.name?html}) with id of ' + config.id + ' using config:');
               console.log(config);
            }
            
            // initialize the plugin
            pluginInstance.init();

            </#list>
            // render plugins
            for (var i=0, len=plugins.length; i < len; i++)
            {
               plugins[i].render();
            }
         }
      );
   };
})();

WEF.register("${appName}", YAHOO.${appName}, {version: "1.0.0", build: "1"}, YAHOO);
