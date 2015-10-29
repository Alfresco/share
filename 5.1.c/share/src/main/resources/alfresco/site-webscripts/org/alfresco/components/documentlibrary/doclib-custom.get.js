var this_DocLibCustom = this;

var DocLibCustom =
{
   /**
    * Get custom CSS and JavaScript dependencies.
    * TODO: This is temporary code to be removed when the config reader has been implemented
    *
    * @method getDependencies
    * @return {Object} Object literal containing css and js dependencies
    */
   getDependencies: function getDependencies(configFamily)
   {
      /**
       * Get config for a given dependency type
       */
      var fnGetConfig = function fnGetConfig(scopedRoot, dependencyType)
      {
         var dependencies = [], src, configs, dependencyConfig;

         try
         {
            configs = scopedRoot.getChildren(dependencyType);
            if (configs)
            {
               for (var i = 0; i < configs.size(); i++)
               {
                  dependencyConfig = configs.get(i);
                  if (dependencyConfig)
                  {
                     // Get src attribute from each config item
                     src = dependencyConfig.attributes["src"];
                     if (src)
                     {
                        dependencies.push(src.toString());
                     }
                  }
               }
            }
         }
         catch (e)
         {
         }

         return dependencies;
      }

      var scopedRoot = config.scoped[configFamily]["dependencies"];

      return (
      {
         css: fnGetConfig(scopedRoot, "css"),
         js: fnGetConfig(scopedRoot, "js")
      });
   }
};

model.dependencies = DocLibCustom.getDependencies("DocLibCustom");