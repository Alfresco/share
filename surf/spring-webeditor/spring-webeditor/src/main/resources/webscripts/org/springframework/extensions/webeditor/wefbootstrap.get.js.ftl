<#include "wef-boot.js" />

<#if url.args?contains("debug=true")><#assign debug="true"><#else><#assign debug="false"></#if>

WEF.init(
{
   /**
    * Context path of application
    * 
    * @type String
    */
   contextPath: "${url.context}",
   
   /**
    * Debug mode
    * 
    * @type Boolean
    */
   debugMode: ${debug},

   /**
    * constants
    * @type Object 
    */
   constants: {},

   /**
    * Object literal of applications to render.
    * 
    * @type Object 
    */ 
   applications: {},
   
   /**
    * Configuration for loader
    *  
    */
   loaderConfig: 
   {
      /**
       * Server port of awe app
       *
       * @type String
       */
      serverPort: window.location.protocol + "//" + window.location.host,
      
      /**
       * Context path of awe app
       *
       * @type String
       */
      urlContext: window.location.protocol + "//" + window.location.host + "${url.context}" + "/res",
      
      /**
       * Use sandbox to load files
       *
       * @type Boolean
       */
      useSandboxLoader: false,
      
      /**
       * Path to yuiloader. This is loaded via script tags so can be absolute or relative
       * 
       * @type String 
       */
      yuiloaderPath: "/yui/yuiloader/yuiloader-min.js",
      
      /**
       * Base path to yui files. Use empty string to use YDN 
       * 
       * @type string
       */
      yuibase: "${url.context}/res/yui/",
      
      /**
       * A filter to apply to loader.
       * 3rd party plugins need to be available in -[filter].js versions too
       * Defaults to min if set to null. Use "" to load files as specified in 
       * their resource path. For yui files "" loads the -debug versions.
       *
       * @type String 
       */
      filter: "min",
      
      /**
       * Flag for yui loader to determine whether to load extra optional resources as well
       *
       * @type Boolean 
       */
      loadOptional: true,
      
      /**
       * Skin overrides for YUI
       * 
       * @type Object 
       */
      skin: 
      {
         base: "/assets/skins/",
         defaultSkin: "sam" 
      },
      
      /**
       * moduleInfo overrides for YUI loader. Useful if YUI loader module names differ across releases 
       * eg selector-beta (using loader from yui v2.6) vs selector (yui v2.7+)
       * 
       * @type Object
       */
       moduleInfoOverrides:
       {
          'selector': function WEF_selectorModuleInfoOverride(module, yuiVersion)
          {
            if (yuiVersion < 2.7)
            {
               // override path; strip '-beta'
               module.path = module.path.replace('-beta', '');
            }
            return module;
          },
          'cookie': function WEF_selectorModuleInfoOverride(module, yuiVersion)
          {
            if (yuiVersion < 2.6)
            {
               // override path; strip '-beta'
               module.path = module.path.replace('-beta', '');
            }
            return module;
          }
       },
       
       /**
        * Handlers to run before a specified module is set to load.
        * This is run by WEF Loader before specified module is set to load
        * 
        * @param module {Object} Object literal describing module 
        * @return {Object} Module object *Must be retured*
        */
       modulePreLoadHandlers:
       {
          'com.moxiecode.tinymce': function WEF_tinyMCE_moduleOnPreLoad(module, loaderConfig)
          {
             //set the path to tinymce so tinymce can pick it up
             window.tinyMCEPreInit = {
               base: module.fullpath.substring(0,module.fullpath.lastIndexOf('/')),
               suffix: ".min",
               query: ""
             };                
             return module;
          }
       },
       
       /**
        * Handlers to run after specified module is loaded
        * This is run by WEF Loader after specified module is loaded
        * 
        * @param module {Object} Object literal describing module 
        * @return {Object} Module object *Must be retured*
        *  
        */
       moduleLoadHandlers:
       {
       }
   }
});