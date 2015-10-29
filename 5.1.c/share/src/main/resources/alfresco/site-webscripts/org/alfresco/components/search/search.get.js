/**
 * Search component GET method
 */

function main()
{
   // fetch the request params required by the search component template
   var siteId = (page.url.templateArgs["site"] != null) ? page.url.templateArgs["site"] : "";
   var siteTitle = null;
   if (siteId.length != 0)
   {
      // Call the repository for the site profile
      var json = remote.call("/api/sites/" + siteId);
      if (json.status == 200)
      {
         // Create javascript objects from the repo response
         var obj = JSON.parse(json);
         if (obj)
         {
            siteTitle = (obj.title.length != 0) ? obj.title : obj.shortName;
         }
      }
   }
   
   // get the search sorting fields from the config
   var sortables = config.scoped["Search"]["sorting"].childrenMap["sort"];
   var sortFields = [];
   for (var i = 0, sort, label; i < sortables.size(); i++)
   {
      sort = sortables.get(i);
      
      // resolve label text
      label = sort.attributes["label"];
      if (label == null)
      {
         label = sort.attributes["labelId"];
         if (label != null)
         {
            label = msg.get(label);
         }
      }
      
      // create the model object to represent the sort field definition
      sortFields.push(
      {
         type: (sort.value !== null ? sort.value : ""),
         label: label ? label : sort.value
      });
   }
   
   // Prepare the model
   var repoconfig = config.scoped['Search']['search'].getChildValue('repository-search');
   model.siteId = siteId;
   model.siteTitle = (siteTitle != null ? siteTitle : "");
   model.sortFields = sortFields;
   model.searchTerm = (page.url.args["t"] != null) ? page.url.args["t"] : "";
   model.searchTag = (page.url.args["tag"] != null) ? page.url.args["tag"] : "";
   model.searchSort = (page.url.args["s"] != null) ? page.url.args["s"] : (sortFields.length !== 0 ? sortFields[0].type : "");
   // config override can force repository search on/off
   model.searchRepo = ((page.url.args["r"] == "true") || repoconfig == "always") && repoconfig != "none";
   model.searchAllSites = (page.url.args["a"] == "true" || siteId.length == 0);
   
   // Advanced search forms based json query
   model.searchQuery = (page.url.args["q"] != null) ? page.url.args["q"] : "";
   
   // Widget instantiation metadata...
   var searchConfig = config.scoped['Search']['search'],
       defaultMinSearchTermLength = searchConfig.getChildValue('min-search-term-length'),
       defaultMaxSearchResults = searchConfig.getChildValue('max-search-results');

   var search = {
      id : "Search", 
      name : "Alfresco.Search",
      options : {
         siteId : model.siteId,
         siteTitle : model.siteTitle,
         initialSearchTerm : model.searchTerm,
         initialSearchTag : model.searchTag,
         initialSearchAllSites : model.searchAllSites,
         initialSearchRepository : model.searchRepo,
         initialSort : model.searchSort,
         searchQuery : model.searchQuery,
         searchRootNode : config.scoped['RepositoryLibrary']['root-node'].value,
         minSearchTermLength : parseInt((args.minSearchTermLength != null) ? args.minSearchTermLength : defaultMinSearchTermLength),
         maxSearchResults : parseInt((args.maxSearchResults != null) ? args.maxSearchResults : defaultMaxSearchResults)
      }
   };
   model.widgets = [search];
}

main();
