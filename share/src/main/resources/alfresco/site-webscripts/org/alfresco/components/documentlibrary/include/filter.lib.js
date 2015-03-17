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
      // add support for evaluators on the filter. They should either be missing or eval to true
      if (xmlFilter.@evaluator.toString() === "" || runEvaluator(xmlFilter.@evaluator.toString()))
      {
         filters.push(
         {
            id: xmlFilter.@id.toString(),
            data: xmlFilter.@data.toString(),
            label: xmlFilter.@label.toString()
         });
      }
   }
   
   model.filters = filters;

   var docListFilter = {
      id : "BaseFilter",
      name : "Alfresco.component.BaseFilter",
      initArgs: [ "\"Alfresco.DocListFilter\"", "\"" + args.htmlid + "\""],
      assignTo : "filter",
      useMessages : false
   };
   model.widgets = [docListFilter];
}

function runEvaluator(evaluator)
{
   return eval(evaluator);
}

main();