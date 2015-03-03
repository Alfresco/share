<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/upload/flash-upload.get.js">

function main2()
{
   // Widget instantiation metadata...
   var htmlUpload = {
      id : "HtmlUpload", 
      name : "Alfresco.HtmlUpload",
      assignTo : "htmlUpload"
   };
   model.widgets = [htmlUpload];

   //Get the file-upload settings...
   var _maximumFileSizeLimit;
   var docLibConfig = config.scoped["DocumentLibrary"];
   if (docLibConfig != null)
   {
      var fileUpload = docLibConfig["file-upload"];
      if (fileUpload != null)
      {
         _maximumFileSizeLimit = fileUpload["maximum-file-size-limit"];
      }
   }
   model.fileUploadSizeLimit = (_maximumFileSizeLimit != null) ? _maximumFileSizeLimit : "0";
}

main2();

