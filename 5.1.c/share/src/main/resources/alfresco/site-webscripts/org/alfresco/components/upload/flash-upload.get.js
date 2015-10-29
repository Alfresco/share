/**
 * Custom content types
 */
function getContentTypes()
{
   // TODO: Data webscript call to return list of available types
   var contentTypes = [
   {
      id: "cm:content",
      value: "cm_content"
   }];

   return contentTypes;
}

model.contentTypes = getContentTypes();

function main()
{
   // Widget instantiation metadata...
   var flashUpload = {
      id : "FlashUpload", 
      name : "Alfresco.FlashUpload"
   };
   model.widgets = [flashUpload];
}

main();

