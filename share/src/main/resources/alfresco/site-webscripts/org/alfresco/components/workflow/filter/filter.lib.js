/**
 * Parses the config file and returns an object model of the filters
 */
function getFilters()
{
   var myConfig = new XML(config.script),
      filterParameters = [];
   for each(var xmlFilter in myConfig..filter)
   {
      filterParameters.push({
         id: xmlFilter.@id.toString(),
         data: xmlFilter.@data.toString(),
         label: xmlFilter.@label.toString()
      });
   }
   return filterParameters;
}

/**
 * Parses the config file and returns an object model of the filter parameters
 */
function getFilterParameters()
{
   var myConfig = new XML(config.script),
      filters = [];
   for each(var xmlFilter in myConfig..filter)
   {
      filters.push({
         id: xmlFilter.@id.toString(),
         data: xmlFilter.@data.toString(),
         parameters: xmlFilter.@parameters.toString()
      });
   }
   return filters;
}
