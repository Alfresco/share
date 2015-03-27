/**
 * Main entrypoint for component webscript logic
 *
 * @method main
 */
function main()
{
   // Actions
   var myConfig = new XML(config.script),
      filters = [];
   
   for each(var xmlFilter in myConfig..filter)
   {
      filters.push(
      {
         id: xmlFilter.@id.toString(),
         label: xmlFilter.@label.toString()
      });
   }
   
   model.filters = filters;
   
   // Widget instantiation metadata...
   var filter = {
      id : "BaseFilter", 
      name : "Alfresco.component.BaseFilter",
      initArgs : ["'Alfresco.TopicListFilter'","\"" + args.htmlid + "\""],
      useMessages : false,
      useOptions : false
   };
   model.widgets = [filter];
}

main();

